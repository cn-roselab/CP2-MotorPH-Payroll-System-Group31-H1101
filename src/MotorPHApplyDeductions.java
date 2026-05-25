import java.util.Scanner;

public class MotorPHApplyDeductions {

    // ===================== SSS (BASED ON MATRIX) =====================

    public static double computeSSSDeduction(double grossSalary) {

        if (grossSalary < 3250) return 135;
        else if (grossSalary < 3750) return 157.5;
        else if (grossSalary < 4250) return 180;
        else if (grossSalary < 4750) return 202.5;
        else if (grossSalary < 5250) return 225;
        else if (grossSalary < 5750) return 247.5;
        else if (grossSalary < 6250) return 270;
        else if (grossSalary < 6750) return 292.5;
        else if (grossSalary < 7250) return 315;
        else if (grossSalary < 7750) return 337.5;
        else if (grossSalary < 8250) return 360;
        else if (grossSalary < 8750) return 382.5;
        else if (grossSalary < 9250) return 405;
        else if (grossSalary < 9750) return 427.5;
        else if (grossSalary < 10250) return 450;
        else if (grossSalary < 10750) return 472.5;
        else if (grossSalary < 11250) return 495;
        else if (grossSalary < 11750) return 517.5;
        else if (grossSalary < 12250) return 540;
        else if (grossSalary < 12750) return 562.5;
        else if (grossSalary < 13250) return 585;
        else if (grossSalary < 13750) return 607.5;
        else if (grossSalary < 14250) return 630;
        else if (grossSalary < 14750) return 652.5;
        else if (grossSalary < 15250) return 675;
        else if (grossSalary < 15750) return 697.5;
        else if (grossSalary < 16250) return 720;
        else if (grossSalary < 16750) return 742.5;
        else if (grossSalary < 17250) return 765;
        else if (grossSalary < 17750) return 787.5;
        else if (grossSalary < 18250) return 810;
        else if (grossSalary < 18750) return 832.5;
        else if (grossSalary < 19250) return 855;
        else if (grossSalary < 19750) return 877.5;
        else if (grossSalary < 20250) return 900;
        else if (grossSalary < 20750) return 922.5;
        else if (grossSalary < 21250) return 945;
        else if (grossSalary < 21750) return 967.5;
        else if (grossSalary < 22250) return 990;
        else if (grossSalary < 22750) return 1012.5;
        else if (grossSalary < 23250) return 1035;
        else if (grossSalary < 23750) return 1057.5;
        else if (grossSalary < 24250) return 1080;
        else if (grossSalary < 24750) return 1102.5;
        else return 1125;
    }

    // ===================== PHILHEALTH =====================

    public static double computePhilHealthDeduction(double grossSalary) {

        double contribution;

        if (grossSalary <= 10000) {
            contribution = 300;
        } else if (grossSalary >= 60000) {
            contribution = 1800;
        } else {
            contribution = grossSalary * 0.03;
        }

        return contribution / 2; // employee share
    }

    // ===================== PAG-IBIG =====================

    public static double computePagIbigDeduction(double grossSalary) {

        if (grossSalary <= 1500) {
            return grossSalary * 0.01;
        } else {
            return grossSalary * 0.02;
        }
    }

    // ===================== INCOME TAX =====================

    public static double computeIncomeTaxDeduction(double grossSalary) {

        if (grossSalary <= 20833) return 0;
        else if (grossSalary <= 33333)
            return (grossSalary - 20833) * 0.20;
        else if (grossSalary <= 66667)
            return (grossSalary - 33333) * 0.25 + 2500;
        else
            return (grossSalary - 66667) * 0.30 + 10833;
    }

    // ===================== NET PAY =====================

    public static double computeNetPay(double grossSalary) {

        double sss = computeSSSDeduction(grossSalary);
        double philhealth = computePhilHealthDeduction(grossSalary);
        double pagibig = computePagIbigDeduction(grossSalary);
        double tax = computeIncomeTaxDeduction(grossSalary);

        double totalDeductions = sss + philhealth + pagibig + tax;

        return grossSalary - totalDeductions;
    }

    // ===================== OPTIONAL TEST =====================

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Gross Salary: ");
        double gross = sc.nextDouble();

        double sss = computeSSSDeduction(gross);
        double philhealth = computePhilHealthDeduction(gross);
        double pagibig = computePagIbigDeduction(gross);
        double tax = computeIncomeTaxDeduction(gross);
        double net = computeNetPay(gross);

        System.out.println("\n===== DEDUCTION BREAKDOWN =====");
        System.out.printf("Gross Salary: %.2f\n", gross);
        System.out.printf("SSS: %.2f\n", sss);
        System.out.printf("PhilHealth: %.2f\n", philhealth);
        System.out.printf("Pag-IBIG: %.2f\n", pagibig);
        System.out.printf("Tax: %.2f\n", tax);
        System.out.printf("Net Pay: %.2f\n", net);

        sc.close();
    }
}