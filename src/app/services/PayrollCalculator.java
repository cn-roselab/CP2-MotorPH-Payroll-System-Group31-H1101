package app.services;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.models.AttendanceRecord;
import app.models.Employee;

/**
 * Contains all payroll business rules for MotorPH.
 *
 * These rules mirror the reviewer-approved logic from the original
 * PayrollSystemExcel implementation:
 *   - Login grace period: on or before 8:10 AM is treated as 8:00 AM.
 *   - Logout is capped at 5:00 PM (17:00); no overtime is credited.
 *   - Hours worked = adjusted logout - adjusted login - 1 hour lunch (min 0).
 *   - Statutory deductions are computed on the MONTHLY gross
 *     (cutoff 1 + cutoff 2) and applied on the second cutoff.
 */
public class PayrollCalculator {

    private static final LocalTime GRACE_LIMIT = LocalTime.of(8, 10);
    private static final LocalTime WORK_START = LocalTime.of(8, 0);
    private static final LocalTime WORK_END = LocalTime.of(17, 0);

    // Last calendar day per month (index 1-12).
    private static final int[] LAST_DAYS =
            {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final String[] MONTH_NAMES = {
            "", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    // ===================== HOURS =====================

    /** Total hours worked for one employee within a day range of a month. */
    public static double computeHours(List<AttendanceRecord> attendance, String empNo,
                                      int month, int startDay, int endDay) {
        double total = 0;

        for (AttendanceRecord r : attendance) {
            if (!r.getEmpNo().equals(empNo)) continue;
            if (r.getDate().getMonthValue() != month) continue;

            int day = r.getDate().getDayOfMonth();
            if (day < startDay || day > endDay) continue;

            LocalTime in = r.getTimeIn();
            LocalTime out = r.getTimeOut();

            // Grace period: at or before 8:10 AM counts as 8:00 AM.
            if (!in.isAfter(GRACE_LIMIT)) {
                in = WORK_START;
            }

            // Cap logout at 5:00 PM.
            if (out.isAfter(WORK_END)) {
                out = WORK_END;
            }

            double hours = ChronoUnit.MINUTES.between(in, out) / 60.0 - 1.0; // 1 hr lunch
            if (hours < 0) hours = 0;

            total += hours;
        }

        return total;
    }

    public static double computeGross(double hours, double hourlyRate) {
        return hours * hourlyRate;
    }

    /**
     * Change-request named wrapper: gross pay for one period.
     * Delegates to {@link #computeGross(double, double)}.
     */
    public static double computeGrossPay(double hours, double hourlyRate) {
        return computeGross(hours, hourlyRate);
    }

    // ===================== DEDUCTIONS =====================

    /** SSS employee share: 4.5% of the Monthly Salary Credit (4,000-30,000). */
    public static double computeSSS(double monthlyGross) {
        double msc;
        if (monthlyGross < 4250) {
            msc = 4000;
        } else if (monthlyGross >= 29750) {
            msc = 30000;
        } else {
            msc = Math.round(monthlyGross / 500.0) * 500;
        }
        return msc * 0.045;
    }

    /** PhilHealth employee share: half of a 5% premium (floor 500, cap 5,000). */
    public static double computePhilHealth(double monthlyGross) {
        double premium = monthlyGross * 0.05;
        if (premium < 500) premium = 500;
        if (premium > 5000) premium = 5000;
        return premium / 2.0;
    }

    /** Pag-IBIG employee share: 1% (<=1,500) or 2% (>1,500), capped at 100. */
    public static double computePagIbig(double monthlyGross) {
        double contribution;
        if (monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else {
            contribution = monthlyGross * 0.02;
        }
        return Math.min(contribution, 100.0);
    }

    /** Withholding tax (TRAIN law) on taxable income. */
    public static double computeTax(double taxableIncome) {
        if (taxableIncome <= 20833) {
            return 0;
        } else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.15;
        } else if (taxableIncome <= 66667) {
            return 1875.00 + (taxableIncome - 33333) * 0.20;
        } else if (taxableIncome <= 166667) {
            return 8541.80 + (taxableIncome - 66667) * 0.25;
        } else if (taxableIncome <= 666667) {
            return 33541.80 + (taxableIncome - 166667) * 0.30;
        } else {
            return 183541.80 + (taxableIncome - 666667) * 0.35;
        }
    }

    /**
     * Change-request named wrapper: total statutory deductions on a monthly
     * gross (SSS + PhilHealth + Pag-IBIG + withholding tax). Reuses the
     * individual modular methods so the logic stays in one place.
     */
    public static double computeDeductions(double monthlyGross) {
        double sss = computeSSS(monthlyGross);
        double philHealth = computePhilHealth(monthlyGross);
        double pagIbig = computePagIbig(monthlyGross);
        double tax = computeTax(monthlyGross - sss - philHealth - pagIbig);
        return sss + philHealth + pagIbig + tax;
    }

    /**
     * Change-request named wrapper: net pay = gross pay - total deductions.
     */
    public static double computeNetPay(double grossPay, double deductions) {
        return grossPay - deductions;
    }

    // ===================== MONTHLY BREAKDOWN =====================

    /**
     * Computes a complete two-cutoff payslip for one employee in one month,
     * with all deductions applied on the second cutoff.
     */
    public static MonthlyPayslip computeMonth(List<AttendanceRecord> attendance,
                                              Employee employee, int month) {
        int lastDay = LAST_DAYS[month];
        double rate = employee.getHourlyRate();

        double hours1 = computeHours(attendance, employee.getEmpNo(), month, 1, 15);
        double gross1 = computeGross(hours1, rate);

        double hours2 = computeHours(attendance, employee.getEmpNo(), month, 16, lastDay);
        double gross2 = computeGross(hours2, rate);

        double monthlyGross = gross1 + gross2;

        double sss = computeSSS(monthlyGross);
        double philHealth = computePhilHealth(monthlyGross);
        double pagIbig = computePagIbig(monthlyGross);
        double taxable = monthlyGross - sss - philHealth - pagIbig;
        double tax = computeTax(taxable);
        double totalDeductions = sss + philHealth + pagIbig + tax;

        MonthlyPayslip p = new MonthlyPayslip();
        p.month = month;
        p.lastDay = lastDay;
        p.hours1 = hours1;
        p.gross1 = gross1;
        p.net1 = gross1; // first cutoff has no deductions
        p.hours2 = hours2;
        p.gross2 = gross2;
        p.sss = sss;
        p.philHealth = philHealth;
        p.pagIbig = pagIbig;
        p.tax = tax;
        p.totalDeductions = totalDeductions;
        p.net2 = gross2 - totalDeductions;
        return p;
    }

    /** Months (6-12) that have at least one attendance entry for an employee. */
    public static List<Integer> getMonthsWithData(List<AttendanceRecord> attendance, String empNo) {
        List<Integer> months = new ArrayList<>();
        for (AttendanceRecord r : attendance) {
            if (!r.getEmpNo().equals(empNo)) continue;
            int m = r.getDate().getMonthValue();
            if (m >= 6 && m <= 12 && !months.contains(m)) {
                months.add(m);
            }
        }
        Collections.sort(months);
        return months;
    }

    public static String getMonthName(int month) {
        return MONTH_NAMES[month];
    }

    // ===================== PAYROLL SUMMARY (FEATURE 5) =====================

    /**
     * Aggregates payroll across all employees and every month that has
     * attendance data. Produces total employees, total gross pay, total
     * deductions, and average net pay.
     */
    public static PayrollSummary generatePayrollSummary(List<Employee> employees,
                                                List<AttendanceRecord> attendance) {
        PayrollSummary summary = new PayrollSummary();
        summary.totalEmployees = employees.size();

        double totalGross = 0;
        double totalDeductions = 0;
        double totalNet = 0;

        for (Employee e : employees) {
            List<Integer> months = getMonthsWithData(attendance, e.getEmpNo());
            for (int month : months) {
                MonthlyPayslip p = computeMonth(attendance, e, month);
                totalGross += p.gross1 + p.gross2;
                totalDeductions += p.totalDeductions;
                totalNet += p.net1 + p.net2;
            }
        }

        summary.totalGrossPay = totalGross;
        summary.totalDeductions = totalDeductions;
        summary.averageNetPay =
                summary.totalEmployees == 0 ? 0 : totalNet / summary.totalEmployees;
        return summary;
    }

    /** Holder for the aggregated payroll summary (Feature 5). */
    public static class PayrollSummary {
        public int totalEmployees;
        public double totalGrossPay;
        public double totalDeductions;
        public double averageNetPay;
    }

    /** Simple holder for a single month's computed payslip. */
    public static class MonthlyPayslip {
        public int month;
        public int lastDay;
        public double hours1;
        public double gross1;
        public double net1;
        public double hours2;
        public double gross2;
        public double sss;
        public double philHealth;
        public double pagIbig;
        public double tax;
        public double totalDeductions;
        public double net2;
    }
}
