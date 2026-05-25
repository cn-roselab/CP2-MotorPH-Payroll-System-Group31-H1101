# 🏍️ MotorPH Payroll System

A Java Swing desktop application for managing employee payroll at MotorPH. Built as part of an object-oriented programming course, this project transitions a console-based employee management system into a fully interactive Graphical User Interface (GUI).

---

## 📋 Change Request Reference

| Field | Details |
|---|---|
| **Change Reference No.** | MPHCR01 |
| **Change Title** | Feature 1 – Graphical User Interface (GUI) |
| **Requested By** | MotorPH |
| **Status** | Approved |

---

## ✨ Features

- 🔍 **Search Employee** — Look up an employee by their Employee Number
- 🧮 **Compute Payroll** — Calculate gross pay, deductions, and net pay per cutoff period
- 🗓️ **Pay Coverage Selection** — Choose month (June–December) and cutoff (1st or 2nd)
- 📄 **Payslip Display** — View a formatted payslip with complete deduction breakdown
- 🧹 **Clear Fields** — Reset all inputs and results in one click
- ⚠️ **Input Validation** — User-friendly error dialogs for missing or invalid inputs

---

## 💸 Deductions Computed (2nd Cutoff)

| Deduction | Description |
|---|---|
| SSS | Social Security System contribution |
| PhilHealth | Philippine Health Insurance |
| Pag-IBIG | Home Development Mutual Fund |
| Income Tax | Withholding tax based on gross pay |

> Deductions are applied only on the **2nd cutoff** (16th–31st) of each month.

---

## 🖥️ GUI Components

| Component | Purpose |
|---|---|
| `JTextField` | Employee Number input |
| `JTextField` (read-only) | Auto-filled Employee Name after search |
| `JComboBox` | Month selection (June–December) |
| `JComboBox` | Cutoff selection (Cutoff 1 / Cutoff 2) |
| `JButton` | Search, Compute, and Clear actions |
| `JTextArea` + `JScrollPane` | Payslip result display |
| `JOptionPane` | Success and error message dialogs |

---

## 🏗️ Project Structure

```
MotorPHPayrollSystem/
│
├── MotorPHGUI.java            # GUI layer — Swing interface & event handling
├── MotorPHPayroll.java        # Core logic — employee data, hours, gross pay
├── MotorPHApplyDeductions.java # Deduction calculations (SSS, PhilHealth, etc.)
└── README.md
```

---

## ⚙️ How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any IDE (IntelliJ IDEA, Eclipse, NetBeans) or command line

### Using the Command Line

```bash
# 1. Clone the repository
git clone https://github.com/your-username/motorph-payroll-system.git
cd motorph-payroll-system

# 2. Compile all Java files
javac *.java

# 3. Run the application
java MotorPHGUI
```

### Using an IDE
1. Open the project folder in your IDE
2. Set `MotorPHGUI.java` as the main class
3. Run the project

---

## 🚀 Usage

1. **Enter** an Employee Number in the input field
2. Click **Search Employee** to verify and load the employee's name
3. Select the **Pay Coverage month** and **Cutoff period**
4. Click **Compute Payroll** to generate the payslip
5. View the payslip in the results area below
6. Click **Clear** to reset all fields

---

## 🛡️ Exception Handling

The application validates all user inputs and handles errors gracefully:

- Empty Employee Number field → error dialog prompt
- Employee not found in records → error dialog with message
- Invalid or missing selections → prevented by dropdown defaults
- All exceptions caught and displayed via `JOptionPane` without crashing the app

---

## 📚 Concepts Applied

- **Object-Oriented Programming** — Separate classes for GUI, payroll logic, and deductions
- **Java Swing** — `JFrame`, `JPanel`, `GridLayout`, `BorderLayout`, `JScrollPane`
- **Event Handling** — `ActionListener` interface implemented on all interactive buttons
- **Exception Handling** — `try-catch` blocks and input validation throughout
- **Separation of Concerns** — GUI layer is independent from business logic classes

---

## 👥 Authors

| Name | Role |
|---|---|
| Rosemarie Labiste | Developer |
| Jed Beltran | Developer |
