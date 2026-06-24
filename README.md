# 🏍️ MotorPH Employee Management System

A Java Swing desktop application for managing employee records and payroll at MotorPH. Built as part of an Object-Oriented Programming course, this project enhances the original payroll system by integrating Employee Record Management, Salary Computation, and Data Update functionalities into a single user-friendly Graphical User Interface (GUI).

---

## 📋 Change Request Reference

| Field                    | Details                                                                |
| ------------------------ | ---------------------------------------------------------------------- |
| **Change Reference No.** | MPHCR01                                                                |
| **Change Title**         | Milestone 2 - Employee Management, Salary Computation, and Data Update |
| **Requested By**         | MotorPH                                                                |
| **Status**               | Approved                                                               |

---

## 🚀 Milestone 2 Enhancements

This milestone expands the original MotorPH Payroll System by implementing:

* Employee Record Management
* Salary Computation
* Employee Record Update
* Employee Record Deletion
* Improved GUI Integration
* Enhanced User Input Validation and Error Handling

All features are integrated into a single Java Swing application.

---

## ✨ Features

### Feature 2 - Employee Record Management

* Search Employee by Employee Number
* Add New Employee Records
* View Employee Information
* Employee Record Validation

### Feature 3 - Salary Computation

* Compute Hours Worked
* Compute Gross Pay
* Compute SSS Contribution
* Compute PhilHealth Contribution
* Compute Pag-IBIG Contribution
* Compute Income Tax
* Compute Net Pay
* Generate Employee Payslip

### Feature 4 - Update and Delete Records

* Update Existing Employee Records
* Delete Employee Records
* Clear Form Inputs
* Confirmation Dialog Before Deletion

---

## 💸 Deductions Computed (2nd Cutoff)

| Deduction  | Description                         |
| ---------- | ----------------------------------- |
| SSS        | Social Security System contribution |
| PhilHealth | Philippine Health Insurance         |
| Pag-IBIG   | Home Development Mutual Fund        |
| Income Tax | Withholding tax based on gross pay  |

> Deductions are applied only during the **2nd cutoff period (16th–31st)** of each month.

---

## 🖥️ GUI Components

| Component                   | Purpose                             |
| --------------------------- | ----------------------------------- |
| `JTextField`                | Employee Number input               |
| `JTextField` (read-only)    | Employee Name display               |
| `JComboBox`                 | Month selection                     |
| `JComboBox`                 | Cutoff selection                    |
| `JButton`                   | Search Employee                     |
| `JButton`                   | Compute Payroll                     |
| `JButton`                   | Add Employee                        |
| `JButton`                   | Update Employee                     |
| `JButton`                   | Delete Employee                     |
| `JButton`                   | Clear Form                          |
| `JTextArea` + `JScrollPane` | Payslip result display              |
| `JOptionPane`               | Success, warning, and error dialogs |

---

## 🏗️ Project Structure

```text
CP2-MotorPH-Payroll-System-Group31-H1101/
│
├── src/
│   ├── MotorPHGUI.java
│   ├── MotorPHPayroll.java
│   ├── MotorPHApplyDeductions.java
│   └── MotorPHSemiMonthlySalary.java
│
├── MotorPHemployees.csv
├── MotorPHemployeeattendance.csv
└── README.md
```

---

## ⚙️ How to Run

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* Any IDE (IntelliJ IDEA, Eclipse, NetBeans, VS Code)

### Using an IDE

1. Open the project folder
2. Open the `src` folder
3. Set `MotorPHGUI.java` as the main class
4. Run the application

### Using the Command Line

```bash
javac src/*.java
java MotorPHGUI
```

---

## 🚀 Usage

### Employee Search

1. Enter an Employee Number
2. Click **Search Employee**
3. Employee information will be displayed

### Payroll Computation

1. Enter an Employee Number
2. Select Pay Coverage Month
3. Select Cutoff Period
4. Click **Compute Payroll**
5. View payroll details and deductions

### Add Employee

1. Click **Add Employee**
2. Enter Employee Number
3. Enter Employee Name
4. Save the record

### Update Employee

1. Search for an existing employee
2. Click **Update Employee**
3. Enter the updated employee name
4. Save changes

### Delete Employee

1. Search for an existing employee
2. Click **Delete Employee**
3. Confirm deletion
4. Record is removed from the active list

---

## 🛡️ Exception Handling

The application validates all user inputs and handles errors gracefully:

* Empty Employee Number field
* Employee not found in records
* Invalid employee data
* Missing required inputs
* Confirmation before deleting records
* Error dialogs displayed using `JOptionPane`
* Application continues running without crashing

---

## 📚 Concepts Applied

* **Object-Oriented Programming (OOP)** — Separation of GUI, payroll, and deduction logic
* **Java Swing** — GUI development using Swing components
* **Event Handling** — Button actions implemented through `ActionListener`
* **Exception Handling** — Input validation and error management
* **File Processing** — Employee and attendance records stored in CSV files
* **Separation of Concerns** — Business logic separated from presentation layer

---

## 👥 Authors

| Name              | Role      |
| ----------------- | --------- |
| Rosemarie Labiste | Developer |
| Jed Beltran       | Developer |
