# MotorPH Employee Application — Test Cases

Systematic test cases covering Features 1–5, GUI/navigation, and exception
handling. Run each on a machine with a JDK, then record the **Actual Result**
and mark **Status** as Pass or Fail.

**How to launch:** (from the project's root folder)
```bash
javac -d out $(find src -name "*.java")
java -cp out app.MotorPHApp
```

**Login:** `employee` / `12345` or `payroll_staff` / `12345`

---

## Reference deduction example (for verifying Feature 3)
Deductions use the approved rules on the **monthly gross**. For a monthly gross
of **₱25,000**:

| Metric | Formula | Expected |
|--------|---------|----------|
| SSS | 25,000 × 4.5% (MSC 25,000) | ₱1,125.00 |
| PhilHealth | 25,000 × 5% ÷ 2 | ₱625.00 |
| Pag-IBIG | min(25,000 × 2%, 100) | ₱100.00 |
| Taxable | 25,000 − 1,125 − 625 − 100 | ₱23,150.00 |
| Tax | (23,150 − 20,833) × 15% | ₱347.55 |

Use this to cross-check the deductions shown for any cutoff whose gross you read
from the payslip output.

---

## Test Cases

### TC-01 — GUI and Navigation (Feature 1)
| Field | Detail |
|-------|--------|
| **Objective** | All tabs, labels, buttons, and dialogs render and respond. |
| **Preconditions** | App launched. |
| **Steps** | 1. Launch app. 2. Log in as `payroll_staff`. 3. Observe the two tabs and the Logout/Exit buttons. |
| **Test Data** | `payroll_staff` / `12345` |
| **Expected Result** | Login window appears; after login the Payroll Staff Dashboard opens with two tabs — **Employees** and **Payroll** (the panels inside are titled *Employee Information* and *Salary Computation*) — plus Logout and Exit. Each tab is selectable and its controls are visible and clickable. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-02 — Employee Record Management (Feature 2)
| Field | Detail |
|-------|--------|
| **Objective** | Employee details display and the record list loads correctly. |
| **Preconditions** | App launched. |
| **Steps** | 1. Log in as `employee`. 2. Enter `10001`. 3. Click **View Details**. |
| **Test Data** | Employee number `10001` |
| **Expected Result** | Displays Employee Number `10001`, Name `Manuel III Garcia`, and Birthday `10/11/1983`. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-03 — Salary Computation (Feature 3)
| Field | Detail |
|-------|--------|
| **Objective** | Gross pay, deductions, and net pay compute per the approved rules. |
| **Preconditions** | Logged in as `payroll_staff`. |
| **Steps** | 1. Open the **Payroll** tab. 2. Set Scope = `One Employee`, enter employee `10001`, Pay Coverage `June`, `Cutoff 1`; click **Compute**. 3. Change to `Cutoff 2` and click **Compute**. 4. Verify deductions against the monthly gross using the reference example method. |
| **Test Data** | Employee `10001`, June, Cutoff 1 then Cutoff 2 |
| **Expected Result** | Cutoff 1 shows hours, gross (hours × hourly rate), net = gross (no deductions). Cutoff 2 shows Withholding Tax, SSS, Pag-IBIG, PhilHealth computed on the monthly gross; Net = cutoff-2 gross − total deductions. Numbers match manual calculation. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-04 — Update and Delete Records (Feature 4)
| Field | Detail |
|-------|--------|
| **Objective** | Add, update, and delete persist to the CSV and survive a reload. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Employees** tab. |
| **Steps** | 1. Click **Add**; fill the form (number e.g. `19999`, name, birthday, salary, rate) and Save. 2. Confirm it appears in the table. 3. Select it, click **Update**, change the name, Save. 4. Select it, click **Delete**, confirm. 5. Close and relaunch the app. |
| **Test Data** | New employee `19999` |
| **Expected Result** | Added row appears and is written to `MotorPH_Employees.csv`; update changes the name in the table and file; delete removes it after confirmation. After relaunch, the table reflects the saved state (added/updated present until deleted; deleted gone). |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-05 — Payroll Summary (Feature 5)
| Field | Detail |
|-------|--------|
| **Objective** | Summary metrics compute and display, and export produces a valid CSV. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Payroll** tab. |
| **Steps** | 1. In the **Company-wide** row, click **Generate Payroll Summary** and read the pop-up dialog. 2. Click **Export Payroll Summary**. 3. Open `MotorPH_PayrollSummary.csv`. |
| **Test Data** | Full loaded data set |
| **Expected Result** | Dialog shows Total Employees, Total Gross Pay, Total Deductions, Average Net Pay (2 decimals). Total Employees equals the row count; Average Net Pay = total net pay ÷ number of employees. Export shows "Payroll summary exported successfully." and creates `MotorPH_PayrollSummary.csv` with header `Metric,Value` and four labeled rows matching the dialog values. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-06 — Exception Handling and Feedback
| Field | Detail |
|-------|--------|
| **Objective** | Invalid/missing input is handled gracefully without crashing. |
| **Preconditions** | App launched. |
| **Steps** | 1. On login, enter wrong credentials. 2. As `employee`, click View Details with an empty field. 3. As `employee`, search a non-existent number (e.g. `99999`). 4. As `payroll_staff`, click **Add** and enter letters for basic salary. |
| **Test Data** | `wrong`/`wrong`; empty; `99999`; salary `abc` |
| **Expected Result** | Each shows a clear error dialog ("Incorrect username and/or password.", "Please enter your employee number.", "Employee number does not exist.", "Please enter a valid number.") and the app keeps running — no crash. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-07 — Input Validation on Add/Update (Feature 4)
| Field | Detail |
|-------|--------|
| **Objective** | The Add/Update form validates required fields first, then formats. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Employees** tab. |
| **Steps** | 1. Click **Add**, then click **Save** immediately with everything blank. 2. Fill the required fields (marked `*`) but enter Employee Number `1234`, then `1000a`, then `100011`, saving each time. 3. Enter First Name `John123` and Save. 4. Enter Birthday `13/45/2020` then `hello` and Save. 5. Enter valid values and Save. 6. Select an employee, click **Update**, confirm the form is pre-filled (employee number read-only) and the same validation applies. |
| **Test Data** | blank; Emp # `1234`, `1000a`, `100011`; name `John123`; birthday `13/45/2020`, `hello` |
| **Expected Result** | Saving blank flags all required fields with a **red border** and shows one "Please fill in all required fields" warning (no format error yet). After required fields are filled, format checks run: employee number must be **exactly 5 digits** and unique; names reject digits/symbols; birthday must be a real non-future `M/D/YYYY` date; numeric fields reject non-numbers/negatives. Optional numeric fields left blank default to 0. Valid data saves and persists to CSV. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-11 — Search Employee (Feature 4)
| Field | Detail |
|-------|--------|
| **Objective** | Search finds an employee by number and selects the row. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Employees** tab. |
| **Steps** | 1. In the Search Employee field, type `10005` and click **Search**. 2. Type `99999` and click **Search**. 3. Clear the field and click **Search**. |
| **Test Data** | `10005`, `99999`, empty |
| **Expected Result** | `10005` selects that employee's row and shows the details (Name `Eduard Hernandez`). `99999` shows "Employee number does not exist." Empty shows "Please enter an employee number to search." |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-08 — Salary Computation & Payslip (Features 1 & 3)
| Field | Detail |
|-------|--------|
| **Objective** | Scope drives the view; Compute saves; View Payslip shows a document. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Payroll** tab. |
| **Steps** | 1. Confirm a default welcome screen shows on entry. 2. Scope = `One Employee`, enter `10001`, Pay Coverage `June`, `Cutoff 1`; click **Compute**. 3. Change to `Cutoff 2`; click **Compute**. 4. Click **View Payslip**. 5. Set Scope = `All Employees` and confirm the employee-number field and View Payslip are disabled. 6. Click **Compute**. |
| **Test Data** | Employee `10001`, June, Cutoff 1 then Cutoff 2; All |
| **Expected Result** | One-Employee Compute shows the labeled **form view** (Number `10001`, Name `Manuel III Garcia`, Pay Period, Rate, Hours, Gross, deductions, Net) and confirms it saved. Cutoff 1 shows deductions `PHP 0.00`, Net = Gross; Cutoff 2 shows Withholding Tax, SSS, Pag-IBIG, PhilHealth, Net = Gross − total deductions. View Payslip shows a two-column document payslip for `10001`. Selecting All disables the number field and View Payslip; All Compute fills a **table** of every employee and reports success. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-09 — Logout and Exit (Feature 1)
| Field | Detail |
|-------|--------|
| **Objective** | Logout returns to login without quitting; Exit closes the app. |
| **Preconditions** | Logged in as either role. |
| **Steps** | 1. Click **Logout**. 2. Confirm the login window reappears and the portal closes. 3. Log in again. 4. Click **Exit**. |
| **Test Data** | Any valid login |
| **Expected Result** | Logout closes the current portal and shows a fresh login window (able to log in as a different role). Exit terminates the application. No errors or leftover windows. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-10 — Computed Salary Persistence (Feature 3)
| Field | Detail |
|-------|--------|
| **Objective** | Computed salary values are written to the CSV and persist after reopening. |
| **Preconditions** | Logged in as `payroll_staff`, on the **Payroll** tab. |
| **Steps** | 1. Enter employee `10001`. 2. Set Pay Coverage = `June`, `Cutoff 2`. 3. Click **Compute**. 4. Close the app and relaunch; log in again. 5. Open `MotorPH_Employees.csv` in a text editor. |
| **Test Data** | Employee `10001`, June, Cutoff 2 |
| **Expected Result** | The CSV row for `10001` ends with the Computed Pay Period (`June - Cutoff 2`), Computed Hours, Computed Gross, Computed Deductions, and Computed Net values. The persisted values remain after relaunch. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

### TC-13 — Cross-Tab Data Sync (Features 2 & 4)
| Field | Detail |
|-------|--------|
| **Objective** | Record changes on one tab are reflected on the other tab without restarting. |
| **Preconditions** | Logged in as `payroll_staff`. |
| **Steps** | 1. On the **Employees** tab, click **Add** and create employee `19999` with valid details. 2. Switch to the **Payroll** tab and click **Generate Payroll Summary** (Company-wide); note the Total Employees count includes `19999`. 3. Enter `19999` and click **Compute**. 4. Return to **Employees**, select `19999`, click **Delete**, and confirm. 5. Click **Generate Payroll Summary** again. |
| **Test Data** | New employee `19999` |
| **Expected Result** | After adding on Tab 1, the Total Employees count on Tab 2 increases and `19999` can be computed — no restart needed. After deleting on Tab 1, the regenerated summary count decreases. Both tabs reflect the same shared data. |
| **Actual Result** | _(fill in)_ |
| **Status** | _(Pass / Fail)_ |

---

## Summary Table

| Test Case | Feature | Status |
|-----------|---------|--------|
| TC-01 | 1 — GUI & Navigation | _(Pass / Fail)_ |
| TC-02 | 2 — Employee Record Management | _(Pass / Fail)_ |
| TC-03 | 3 — Salary Computation | _(Pass / Fail)_ |
| TC-04 | 4 — Update & Delete | _(Pass / Fail)_ |
| TC-05 | 5 — Payroll Summary | _(Pass / Fail)_ |
| TC-06 | Exception Handling | _(Pass / Fail)_ |
| TC-07 | Input Validation (Add/Update) | _(Pass / Fail)_ |
| TC-08 | Salary Computation by Pay Coverage (1 & 3) | _(Pass / Fail)_ || TC-09 | Logout & Exit (1) | _(Pass / Fail)_ |
| TC-10 | Computed Salary Persistence (3) | _(Pass / Fail)_ |
| TC-11 | Search Employee (4) | _(Pass / Fail)_ |
| TC-13 | Cross-Tab Data Sync (2 & 4) | _(Pass / Fail)_ |
