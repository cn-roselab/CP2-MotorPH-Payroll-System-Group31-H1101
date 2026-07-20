package app.models;

/**
 * Model class representing a single MotorPH employee record.
 * Holds every column from MotorPH_Employees.csv so records can be
 * read, edited, and written back without losing data.
 */
public class Employee {

    private String empNo;
    private String lastName;
    private String firstName;
    private String birthday;
    private String address;
    private String phone;
    private String sss;
    private String philhealth;
    private String tin;
    private String pagibig;
    private String status;
    private String position;
    private String supervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthly;
    private double hourlyRate;

    // Computed salary values persisted back to the CSV (Feature 3).
    private String computedPayPeriod = "";
    private double computedHours;
    private double computedGross;
    private double computedDeductions;
    private double computedNet;

    public Employee() {
        // Defaults for a brand-new record added through the GUI.
        this.empNo = "";
        this.lastName = "";
        this.firstName = "";
        this.birthday = "";
        this.address = "";
        this.phone = "";
        this.sss = "";
        this.philhealth = "";
        this.tin = "";
        this.pagibig = "";
        this.status = "Regular";
        this.position = "";
        this.supervisor = "";
    }

    public Employee(String empNo, String lastName, String firstName, String birthday,
                    String address, String phone, String sss, String philhealth,
                    String tin, String pagibig, String status, String position,
                    String supervisor, double basicSalary, double riceSubsidy,
                    double phoneAllowance, double clothingAllowance,
                    double grossSemiMonthly, double hourlyRate) {
        this.empNo = empNo;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phone = phone;
        this.sss = sss;
        this.philhealth = philhealth;
        this.tin = tin;
        this.pagibig = pagibig;
        this.status = status;
        this.position = position;
        this.supervisor = supervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthly = grossSemiMonthly;
        this.hourlyRate = hourlyRate;
    }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    // ===================== GETTERS / SETTERS =====================

    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSss() { return sss; }
    public void setSss(String sss) { this.sss = sss; }

    public String getPhilhealth() { return philhealth; }
    public void setPhilhealth(String philhealth) { this.philhealth = philhealth; }

    public String getTin() { return tin; }
    public void setTin(String tin) { this.tin = tin; }

    public String getPagibig() { return pagibig; }
    public void setPagibig(String pagibig) { this.pagibig = pagibig; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getRiceSubsidy() { return riceSubsidy; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }

    public double getClothingAllowance() { return clothingAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }

    public double getGrossSemiMonthly() { return grossSemiMonthly; }
    public void setGrossSemiMonthly(double grossSemiMonthly) { this.grossSemiMonthly = grossSemiMonthly; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getComputedGross() { return computedGross; }
    public void setComputedGross(double computedGross) { this.computedGross = computedGross; }

    public String getComputedPayPeriod() { return computedPayPeriod; }
    public void setComputedPayPeriod(String computedPayPeriod) { this.computedPayPeriod = computedPayPeriod; }

    public double getComputedHours() { return computedHours; }
    public void setComputedHours(double computedHours) { this.computedHours = computedHours; }

    public double getComputedDeductions() { return computedDeductions; }
    public void setComputedDeductions(double computedDeductions) { this.computedDeductions = computedDeductions; }

    public double getComputedNet() { return computedNet; }
    public void setComputedNet(double computedNet) { this.computedNet = computedNet; }
}
