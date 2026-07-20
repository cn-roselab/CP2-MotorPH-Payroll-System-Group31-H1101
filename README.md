# MotorPH Employee Application

A Java Swing desktop application for managing MotorPH employee records and
processing semi-monthly payroll. Employee and attendance data are stored in
CSV files, which the application reads on startup and writes back whenever
records are added, updated, or deleted.

**Quick start:** from the `motorph-app` folder, run `./run.sh` (Linux/macOS) or
`run.bat` (Windows) to build and launch the app.

---

## Table of Contents
- [Requirements](#requirements)
- [How to Run](#how-to-run)
- [Login Credentials](#login-credentials)
- [Features](#features)
- [Payroll Rules](#payroll-rules)
- [Project Structure](#project-structure)
- [Data Files](#data-files)
- [Notes](#notes)

---

## Requirements
- **Java Development Kit (JDK) 8 or higher** (`javac` and `java` on your PATH).
- No external libraries — the app uses only the standard Java SE APIs
  (`javax.swing`, `java.time`, `java.io`).

---

## How to Run
Run all commands **from inside the `motorph-app` folder**, because the app
reads and writes the CSV files by relative name.

The easiest way is the provided build-and-run script:

```bash
./run.sh          # Linux/macOS
```

```powershell
.\run.bat         # Windows PowerShell (the .\ prefix is required)
```

```bat
run.bat           :: Windows Command Prompt
```

Or compile and run manually:

```bash
cd motorph-app
javac -d out $(find src -name "*.java")
java -cp out app.MotorPHApp
```

On Windows PowerShell, compile with:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out app.MotorPHApp
```

The login window opens first. After a successful login you are routed to the
portal that matches your username. The CSV files must stay in the folder you
run from (they are opened by relative name).

---

## Login Credentials
| Username        | Password | Opens               |
|-----------------|----------|---------------------|
| `employee`      | `12345`  | Employee Portal     |
| `payroll_staff` | `12345`  | Payroll Staff Portal|

Incorrect credentials show an error message and keep you on the login screen.

---

## Features

The payroll staff dashboard is organized into two tabs — **Employees** and
**Payroll**. The panels inside are titled *Employee Information* and *Salary
Computation* (the latter also hosts the company-wide Payroll Summary).

### Feature 1 — Graphical User Interface
- Swing-based windows with labeled fields, buttons, and consistent layout.
- Tabbed dashboard for payroll staff; event-driven actions with immediate feedback.
- Confirmation and error dialogs for every user action.
- **Logout** (return to login) and **Exit** are available in both portals.

### Feature 2 — Employee Record Management
- **Employee Portal:** enter an employee number to view Employee Number,
  Employee Name, and Birthday. Unknown numbers show
  "Employee number does not exist."
- **Employee Information tab:** all records in a table (Emp #, Name, Birthday,
  Position, Basic Salary, Hourly Rate), a search box, and a details panel. No
  computation happens on this tab.

### Feature 3 — Salary Computation
- **Salary Computation tab:** choose a **Scope** (One Employee / All Employees),
  enter an employee number, and pick a **Pay Coverage** (Month + Cutoff 1 / Cutoff 2
  / **Whole Month**). Uses the approved MotorPH rules
  (see [Payroll Rules](#payroll-rules)). A default welcome screen is shown on
  entry and via the **Clear** button.
- **Compute** — calculates and **saves** the values (pay period, hours, gross,
  deductions, net) to the CSV. For **One Employee** it shows a labeled **form
  view**; for **All Employees** it shows a **results table** for everyone.
- **View Payslip** — shows a **document-style payslip** modeled on a standard
  two-column payslip: company header, Employee Information / Other Information,
  Earnings / Gov't Deductions (indented line items with aligned sub totals), and
  bold Gross Pay / Net Pay. For **All Employees** the payslips are **stacked** in
  the scrollable view.

### Feature 4 — Update and Delete Records
- **Search Employee** — look up an employee by number on the Employee
  Information tab; the matching row is selected.
- **Add** — opens a single form with **all** employee fields (number, name,
  birthday, address, phone, SSS, PhilHealth, TIN, Pag-IBIG, status, position,
  supervisor, salary, allowances, rates). Validation runs in two layers on Save:
  required fields (marked with a red `*`) are checked first and any that are
  blank get a red border with a single "Please fill in all required fields"
  warning; then format checks run:
  - Employee number: exactly 5 digits and unique.
  - Names: letters, spaces, and `. ' -` only.
  - Birthday: a real, non-future `MM/DD/YYYY` date.
  - Phone / SSS / PhilHealth / TIN / Pag-IBIG: optional, but if provided must
    match the CSV data format — Phone `966-860-270`, SSS `44-4506057-3`,
    TIN `442-605-657-000`, PhilHealth and Pag-IBIG 12 digits.
  - **Status**: a dropdown limited to `Regular` or `Probationary`.
  - Money fields: optional non-negative numbers (blank counts as 0); Basic Salary
    and Hourly Rate are required.
- **Update** — opens the same form pre-filled with the selected employee's
  current details; the employee number is read-only. Any field can be edited.
- **Delete** — remove the selected employee after a confirmation prompt.
- Records are held in an `ArrayList` (for the table) and indexed by a
  `HashMap` keyed on employee number for fast lookup.
- Every change is **saved back to the CSV file** immediately.

### Feature 5 — Payroll Summary
- On the **Salary Computation tab**, a separate **Company-wide** row hosts the
  payroll summary actions (independent of the Scope / Employee Number / Pay
  Coverage selectors above it).
- **Generate Payroll Summary** computes **total employees, total gross pay, total
  deductions, and average net pay** (average net = total net ÷ number of
  employees) and shows them in a styled pop-up dialog.
- **Export Payroll Summary** writes the summary to `MotorPH_PayrollSummary.csv`
  in a labeled `Metric,Value` format.

---

## Payroll Rules
These mirror the reviewer-approved computation logic.

**Hours worked (per day)**
- Grace period: a login at or before **8:10 AM** is treated as **8:00 AM**.
- Logout is capped at **5:00 PM (17:00)** — no overtime is credited.
- **Hours = adjusted logout − adjusted login − 1 hour lunch**, never below 0.

**Cutoffs**
- Cutoff 1: days **1–15**.
- Cutoff 2: day **16 – end of month**.

**Deductions** (computed on the **monthly gross** = cutoff 1 + cutoff 2, and
applied on the second cutoff):
- **SSS** — 4.5% of the Monthly Salary Credit (MSC 4,000–30,000).
- **PhilHealth** — 5% premium ÷ 2 (premium floor 500, cap 5,000).
- **Pag-IBIG** — 1% (≤1,500) or 2% (>1,500), capped at 100.
- **Withholding Tax** — TRAIN law brackets on
  (monthly gross − SSS − PhilHealth − Pag-IBIG).

---

## Project Structure
```
motorph-app/
├── src/
│   └── app/                            # Root package
│       ├── MotorPHApp.java             # Application entry point (launches login)
│       ├── models/                     # Data models (MVC "models")
│       │   ├── Employee.java
│       │   └── AttendanceRecord.java
│       ├── services/                   # Data access + business logic (MVC "services")
│       │   ├── CsvManager.java         # CSV read/write + parsing
│       │   ├── PayrollCalculator.java  # Payroll business rules + summary
│       │   └── EmployeeValidator.java  # Employee field validation rules
│       ├── util/                       # Generic, reusable helpers
│       │   ├── FieldFilters.java       # Input filters, masks, placeholders
│       │   ├── MoneyFormatter.java     # Shared peso currency formatting
│       │   ├── Dialogs.java            # Styled success/error/confirm dialogs
│       │   ├── UiStyle.java            # Rounded-field theme + button helpers
│       │   ├── RoundedButtonUI.java    # Rounded button look-and-feel
│       │   ├── RoundedComboBoxUI.java  # Rounded combo box + styled popup
│       │   └── RoundedScrollBarUI.java # Slim modern scrollbar
│       └── views/                      # Swing user interface (MVC "views")
│           ├── LoginFrame.java             # Login window + role routing
│           ├── EmployeePortalFrame.java    # Employee self-service portal (details only)
│           ├── PayrollDashboardFrame.java  # Payroll staff dashboard (2 tabs, shared data)
│           ├── EmployeeInfoPanel.java      # Tab 1 - view / add / update / delete records
│           ├── SalaryComputationPanel.java # Tab 2 - compute payslips + company payroll summary
│           └── EmployeeFormDialog.java     # Shared add/update form (all fields)
├── docs/
│   ├── TEST_CASES.md                   # Manual test cases (TC-01 …)
│   └── SELF_ASSESSMENT.md              # Feature 5 self-assessment answers
├── run.sh                              # Build & run (Linux/macOS)
├── run.bat                             # Build & run (Windows)
├── README.md
├── MotorPH_Employees.csv               # Employee data (read/write at run root)
└── MotorPH_Attendance.csv              # Attendance data
```

**Design:** the code follows an MVC-style layered structure under the `app`
package — `app.models` (`Employee`, `AttendanceRecord`), `app.services`
(`CsvManager` for data access, `PayrollCalculator` for business logic,
`EmployeeValidator` for field rules), `app.util` (generic helpers like
`FieldFilters` and `MoneyFormatter`), and `app.views` (all Swing UI). The payroll
staff experience is a tabbed dashboard (`PayrollDashboardFrame`)
hosting two panels — Employee Information and Salary Computation (which also
hosts the company-wide Payroll Summary) — that share one employee list, lookup
map, and attendance set. The CSV
data files stay at the folder you
run from, since they are opened by relative name.

---

## Data Files
The application depends on two CSV files in the working directory:

| File | Contents |
|------|----------|
| `MotorPH_Employees.csv` | One row per employee. Original 19 columns plus 5 computed columns (Computed Pay Period, Computed Hours, Computed Gross, Computed Deductions, Computed Net). |
| `MotorPH_Attendance.csv` | Daily attendance: Employee #, Date, Log In, Log Out. |

Because records are saved back to `MotorPH_Employees.csv`, keep a backup copy if
you want to preserve the original data set. Older files with only 19 columns are
still read correctly; the computed columns default to 0 until a salary is
computed.

The Salary Computation tab can also generate `MotorPH_PayrollSummary.csv`
(`Metric,Value` format) when you export the payroll summary.

---

## Notes
- Run `java -cp out app.MotorPHApp` from within `motorph-app/` so the CSV files
  are found.
- The application writes to the CSV files located in this folder.
- No third-party dependencies are required.
