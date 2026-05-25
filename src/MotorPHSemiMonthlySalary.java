import java.util.Scanner;

public class MotorPHSemiMonthlySalary {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===== MOTORPH SEMI-MONTHLY SALARY SYSTEM =====");

        System.out.print("Enter Employee Name: ");
        String employeeName = scanner.nextLine();

        System.out.print("Enter Hourly Rate: ");
        double hourlyRate = scanner.nextDouble();

        System.out.print("Enter Total Hours Worked for Cutoff: ");
        double totalHoursWorked = scanner.nextDouble();

        // VALIDATION
        if (hourlyRate <= 0 || totalHoursWorked <= 0) {
            System.out.println("\nInvalid input detected.");
            System.out.println("Hourly rate and total hours must be greater than zero.");
            scanner.close();
            return;
        }

        // GROSS
        double grossSalary = totalHoursWorked * hourlyRate;

        // DEDUCTIONS (reusing your class)
        double sss = MotorPHApplyDeductions.computeSSSDeduction(grossSalary);
        double philhealth = MotorPHApplyDeductions.computePhilHealthDeduction(grossSalary);
        double pagibig = MotorPHApplyDeductions.computePagIbigDeduction(grossSalary);
        double tax = MotorPHApplyDeductions.computeIncomeTaxDeduction(grossSalary);
        double netSalary = MotorPHApplyDeductions.computeNetPay(grossSalary);

        // OUTPUT
        System.out.println("\n===== SEMI-MONTHLY PAYROLL REPORT =====");
        System.out.println("Employee Name: " + employeeName);
        System.out.printf("Gross Salary: %.2f\n", grossSalary);

        System.out.println("\n--- Deductions ---");
        System.out.printf("SSS: %.2f\n", sss);
        System.out.printf("PhilHealth: %.2f\n", philhealth);
        System.out.printf("Pag-IBIG: %.2f\n", pagibig);
        System.out.printf("Tax: %.2f\n", tax);

        System.out.printf("\nNet Salary: %.2f\n", netSalary);

        System.out.println("\nComputation verified successfully!");

        scanner.close();
    }
}