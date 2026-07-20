# Feature 5 (Payroll Summary) — Self-Assessment Answers

Answers reflect the actual implementation in
[PayrollCalculator.java](../src/app/services/PayrollCalculator.java) and
[SalaryComputationPanel.java](../src/app/views/SalaryComputationPanel.java).

---

## A. Payroll Summary Logic

**1. How does your program compute the total number of employees?**
It uses the size of the in-memory employee list. After the CSV is loaded into a
`List<Employee>`, `generatePayrollSummary()` sets
`totalEmployees = employees.size()`. Because add and delete operations modify
that same list, the count always reflects the current records.

**2. How does it calculate:**

- **a. Total gross pay** — an accumulator (`totalGross`) starts at 0. For every
  employee, for each month that has attendance data, the program computes that
  month's payslip and adds both cutoffs' gross (`gross1 + gross2`) to the
  accumulator.
- **b. Total deductions** — a second accumulator (`totalDeductions`) adds each
  month's `totalDeductions` (SSS + PhilHealth + Pag-IBIG + tax, computed on the
  monthly gross).
- **c. Average net pay** — a third accumulator (`totalNet`) sums every month's
  net pay (`net1 + net2`). The average is then `totalNet / totalEmployees`
  (guarded so division never happens when there are zero employees), matching
  the change request's definition: total net pay divided by number of employees.

**3. Explain how your program loops through employee records.**
`generatePayrollSummary()` uses an outer `for-each` loop over the
`List<Employee>`. For each employee it retrieves the months that have attendance
data and runs an inner loop over those months, computing a payslip per month and
adding the results to the accumulators. When both loops finish, the totals and
average are packaged into a `PayrollSummary` object and returned.

---

## B. Reuse of Existing Modules

**1. Which methods from the Salary Computation feature did you reuse?**
The summary reuses the Feature 3 logic in `PayrollCalculator`:
- `computeMonth()` — which itself calls `computeHours()`, `computeGross()`,
  `computeSSS()`, `computePhilHealth()`, `computePagIbig()`, and `computeTax()`.
- `getMonthsWithData()` — to know which months to include per employee.

No payroll math is re-written inside the summary feature; it calls the same
methods the payslip screens use.

It also reuses the existing **file-handling** layer: employee and attendance
data are loaded through `CsvManager.loadEmployees()` / `loadAttendance()`, and
both the report and summary exports write through the shared
`CsvManager.writeCsv()` method, so all CSV I/O lives in one data-access class.

**2. Why is reusing existing logic important for program consistency?**
Because the summary totals are then guaranteed to match the individual
payslips. If the rules ever change, they change in one place and every feature
stays in sync. It also avoids duplicated, drift-prone code and reduces the risk
of a bug appearing in one copy of the logic but not the other.

---

## C. Data Accuracy and Validation

**1. How does your program ensure the summary stays accurate when:**

- **a. Employees are updated** — updates modify the `Employee` objects in the
  shared list and are saved to CSV. The summary always recomputes from that live
  list when the button is pressed, so it reflects the latest values.
- **b. Records are deleted** — delete removes the employee from the same list
  (and rewrites the CSV). The next summary run sees the smaller list, so both
  the totals and the employee count update automatically.
- **c. Salary values change** — because gross, deductions, and net are computed
  on demand from the current hourly rate and attendance (not stored as stale
  totals), any change flows straight into the next summary calculation.

The common thread: the summary is **recomputed from the current in-memory list
every time**, never cached.

---

## D. Summary Presentation

**1. How are the payroll summary results displayed to the user?**
On the **Payroll** tab, the **Generate Payroll Summary** button shows the formatted
results in a styled modal pop-up dialog. A separate
**Export Payroll Summary** button writes the same values to
`MotorPH_PayrollSummary.csv` in a labeled `Metric,Value` format.

**2. How did you ensure the summary is clear and readable?**
- Every value is labeled (Total Employees, Total Gross Pay, Total Deductions,
  Average Net Pay).
- Money values use Philippine peso currency formatting with two decimals.
- The report is framed with a heading and aligned labels so values line up.
- The exported CSV uses an explicit `Metric,Value` header for clarity.

---

## E. Structural Improvement

**1. Identify one improvement that would make the feature more robust.**
Add **department- or position-level summaries** — group employees by position
and report per-group totals in addition to the company-wide figure. This would
make the summary more useful for analysis. Other worthwhile options: exporting
additional metrics (e.g., per-deduction totals) or adding a graphical summary
(bar chart) of gross vs. deductions vs. net.
