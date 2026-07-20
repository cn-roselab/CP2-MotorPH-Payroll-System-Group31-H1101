package app.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import app.models.AttendanceRecord;
import app.models.Employee;

/**
 * Handles all CSV read/write operations
 * Handles all CSV read/write operations for the MotorPH application.
 *
 * Employee records are parsed with a quote-aware parser so that fields
 * containing commas (addresses, "90,000") are read correctly, and are
 * written back with the same escaping so the file stays valid.
 */
public class CsvManager {

    public static final String EMPLOYEE_FILE = "MotorPH_Employees.csv";
    public static final String ATTENDANCE_FILE = "MotorPH_Attendance.csv";

    private static final String EMPLOYEE_HEADER =
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,"
            + "Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,"
            + "Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,"
            + "Gross Semi-monthly Rate,Hourly Rate,"
            + "Computed Pay Period,Computed Hours,Computed Gross,Computed Deductions,Computed Net";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("H:mm");

    // ===================== EMPLOYEES =====================

    /** Reads all employee records from the CSV file. */
    public static List<Employee> loadEmployees() throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(EMPLOYEE_FILE), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] c = parseCsvLine(line);
                if (c.length < 19) continue; // guard against malformed rows

                Employee e = new Employee(
                        c[0].trim(),
                        c[1].trim(),
                        c[2].trim(),
                        c[3].trim(),
                        c[4].trim(),
                        c[5].trim(),
                        c[6].trim(),
                        c[7].trim(),
                        c[8].trim(),
                        c[9].trim(),
                        c[10].trim(),
                        c[11].trim(),
                        c[12].trim(),
                        parseMoney(c[13]),
                        parseMoney(c[14]),
                        parseMoney(c[15]),
                        parseMoney(c[16]),
                        parseMoney(c[17]),
                        parseMoney(c[18])
                );

                // Computed columns are optional and support two layouts:
                //  - New (24 cols): Pay Period, Hours, Gross, Deductions, Net
                //  - Old (22 cols): Gross, Deductions, Net
                if (c.length >= 24) {
                    e.setComputedPayPeriod(c[19].trim());
                    e.setComputedHours(parseMoney(c[20]));
                    e.setComputedGross(parseMoney(c[21]));
                    e.setComputedDeductions(parseMoney(c[22]));
                    e.setComputedNet(parseMoney(c[23]));
                } else if (c.length >= 22) {
                    e.setComputedGross(parseMoney(c[19]));
                    e.setComputedDeductions(parseMoney(c[20]));
                    e.setComputedNet(parseMoney(c[21]));
                }

                employees.add(e);
            }
        }

        return employees;
    }

    /** Writes all employee records back to the CSV file. */
    public static void saveEmployees(List<Employee> employees) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(EMPLOYEE_FILE), StandardCharsets.UTF_8))) {
            bw.write(EMPLOYEE_HEADER);
            bw.newLine();

            for (Employee e : employees) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCsv(e.getEmpNo())).append(",");
                sb.append(escapeCsv(e.getLastName())).append(",");
                sb.append(escapeCsv(e.getFirstName())).append(",");
                sb.append(escapeCsv(e.getBirthday())).append(",");
                sb.append(escapeCsv(e.getAddress())).append(",");
                sb.append(escapeCsv(e.getPhone())).append(",");
                sb.append(escapeCsv(e.getSss())).append(",");
                sb.append(escapeCsv(e.getPhilhealth())).append(",");
                sb.append(escapeCsv(e.getTin())).append(",");
                sb.append(escapeCsv(e.getPagibig())).append(",");
                sb.append(escapeCsv(e.getStatus())).append(",");
                sb.append(escapeCsv(e.getPosition())).append(",");
                sb.append(escapeCsv(e.getSupervisor())).append(",");
                sb.append(fmtNum(e.getBasicSalary())).append(",");
                sb.append(fmtNum(e.getRiceSubsidy())).append(",");
                sb.append(fmtNum(e.getPhoneAllowance())).append(",");
                sb.append(fmtNum(e.getClothingAllowance())).append(",");
                sb.append(fmtNum(e.getGrossSemiMonthly())).append(",");
                sb.append(fmtNum(e.getHourlyRate())).append(",");
                sb.append(escapeCsv(e.getComputedPayPeriod())).append(",");
                sb.append(fmtNum(e.getComputedHours())).append(",");
                sb.append(fmtNum(e.getComputedGross())).append(",");
                sb.append(fmtNum(e.getComputedDeductions())).append(",");
                sb.append(fmtNum(e.getComputedNet()));

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }

    // ===================== GENERIC CSV WRITE =====================

    /**
     * Writes the given lines to a UTF-8 CSV file (one line per list entry).
     * Shared by the payroll report/summary exports so all CSV
     * file writing goes through this data-access layer.
     */
    public static void writeCsv(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // ===================== ATTENDANCE =====================

    /** Reads all attendance entries from the CSV file. */
    public static List<AttendanceRecord> loadAttendance() throws IOException {
        List<AttendanceRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ATTENDANCE_FILE), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Attendance fields never contain commas, so stripping any
                // stray quote characters keeps parsing simple and robust.
                String clean = line.replace("\"", "");
                String[] c = clean.split(",");
                if (c.length < 6) continue;

                try {
                    String empNo = c[0].trim();
                    LocalDate date = LocalDate.parse(c[3].trim(), DATE_FORMAT);
                    LocalTime in = LocalTime.parse(c[4].trim(), TIME_FORMAT);
                    LocalTime out = LocalTime.parse(c[5].trim(), TIME_FORMAT);
                    records.add(new AttendanceRecord(empNo, date, in, out));
                } catch (Exception ex) {
                    // Skip any row with an unparseable date/time.
                }
            }
        }

        return records;
    }

    // ===================== HELPERS =====================

    /** Splits a CSV line while respecting double-quoted fields. */
    public static String[] parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); // escaped quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                out.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        out.add(sb.toString());

        return out.toArray(new String[0]);
    }

    /** Wraps a field in quotes only when it contains a comma or quote. */
    public static String escapeCsv(String value) {
        if (value == null) value = "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /** Parses a money/number field, tolerating quotes, commas, and blanks. */
    public static double parseMoney(String s) {
        if (s == null) return 0;
        s = s.replace("\"", "").replace(",", "").trim();
        if (s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Formats a number without a trailing ".0" for whole values. */
    public static String fmtNum(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }
}
