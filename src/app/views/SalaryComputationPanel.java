package app.views;

import app.services.CsvManager;
import app.services.PayrollCalculator;
import app.models.Employee;
import app.util.Dialogs;
import app.util.FieldFilters;
import app.util.MoneyFormatter;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Tab 2 - Salary Computation.
 *
 * Controls: a compute row (Scope + employee number + pay coverage) plus a
 * company-wide row for the payroll summary.
 * Result views share a CardLayout:
 *   - "default"  a welcome screen shown on entry / after Clear.
 *   - "form"     labeled form of one employee's computed values (Compute, One).
 *   - "payslip"  document-style payslip(s) (View Payslip; stacks when All).
 *   - "table"    computed values for all employees (Compute, All).
 * Computing persists the values to the CSV.
 */
public class SalaryComputationPanel extends JPanel {

    private final PayrollDashboardFrame dashboard;
    private final NumberFormat php = MoneyFormatter.currency();

    private final JComboBox<String> scopeCombo = new JComboBox<>(new String[]{
            "One Employee", "All Employees"});
    private final JTextField empNoField = new JTextField(10);
    private final JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "June", "July", "August", "September", "October", "November", "December"});
    private final JComboBox<String> cutoffCombo = new JComboBox<>(new String[]{
            "Cutoff 1", "Cutoff 2", "Whole Month"});
    private final JButton payslipButton = new JButton("View Payslip");

    // Result views (CardLayout).
    private final CardLayout cards = new CardLayout();
    private final JPanel resultArea = new JPanel(cards);

    // Form-view fields (read-only).
    private final JTextField fNumber = roField();
    private final JTextField fName = roField();
    private final JTextField fBirthday = roField();
    private final JTextField fPeriod = roField();
    private final JTextField fRate = roField();
    private final JTextField fHours = roField();
    private final JTextField fGross = roField();
    private final JTextField fTax = roField();
    private final JTextField fSss = roField();
    private final JTextField fPagibig = roField();
    private final JTextField fPhilhealth = roField();
    private final JTextField fDeductions = roField();
    private final JTextField fNet = roField();

    // All-employees table view.
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Employee Number", "Employee Name", "Pay Period", "Rate", "Hours Worked",
                    "Gross Pay", "Deductions", "Net Pay"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    // Payslip document view.
    private final JEditorPane payslipPane = new JEditorPane();

    public SalaryComputationPanel(PayrollDashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        FieldFilters.mask(empNoField, "#####");

        add(buildControls(), BorderLayout.NORTH);

        resultArea.add(buildDefaultView(), "default");
        resultArea.add(buildFormView(), "form");
        resultArea.add(buildTableView(), "table");
        resultArea.add(buildPayslipView(), "payslip");
        add(resultArea, BorderLayout.CENTER);

        scopeCombo.addActionListener(e -> updateScopeControls());
        updateScopeControls();
        cards.show(resultArea, "default");
    }

    private JPanel buildControls() {
        JButton computeButton = new JButton("Compute");
        JButton clearButton = new JButton("Clear");
        computeButton.addActionListener(e -> compute());
        payslipButton.addActionListener(e -> viewPayslip());
        clearButton.addActionListener(e -> clearToDefault());
        UiStyle.primaryButton(computeButton);
        UiStyle.primaryButton(payslipButton);
        UiStyle.secondaryButton(clearButton);
        UiStyle.sameWidth(computeButton, payslipButton, clearButton);

        JButton summaryButton = new JButton("Generate Payroll Summary");
        JButton exportSummaryButton = new JButton("Export Payroll Summary");
        summaryButton.addActionListener(e -> showSummary());
        exportSummaryButton.addActionListener(e -> exportSummary());
        UiStyle.primaryButton(summaryButton);
        UiStyle.primaryButton(exportSummaryButton);
        UiStyle.sameWidth(summaryButton, exportSummaryButton);

        int h = monthCombo.getPreferredSize().height + 2;
        int w = summaryButton.getPreferredSize().width;
        Dimension ctrlSize = new Dimension(w, h);
        scopeCombo.setPreferredSize(ctrlSize);
        monthCombo.setPreferredSize(ctrlSize);
        cutoffCombo.setPreferredSize(ctrlSize);
        empNoField.setPreferredSize(ctrlSize);
        empNoField.setMinimumSize(ctrlSize);
        FieldFilters.placeholder(empNoField, "Enter employee number");

        // A payslip only applies to a single cutoff, not the Whole Month view.
        cutoffCombo.addActionListener(e -> updatePayslipButton());
        updatePayslipButton();

        // ----- Left column: per-employee / all-employees computation -----
        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder("Salary Computation"));
        GridBagConstraints lg = new GridBagConstraints();
        lg.insets = new Insets(12, 16, 12, 16);
        lg.anchor = GridBagConstraints.WEST;

        int r = 0;
        lg.fill = GridBagConstraints.NONE;
        lg.gridx = 0; lg.gridy = r; left.add(new JLabel("Scope:"), lg);
        lg.fill = GridBagConstraints.BOTH;
        lg.gridx = 1; left.add(scopeCombo, lg);
        lg.gridx = 2; left.add(empNoField, lg);
        r++;
        lg.fill = GridBagConstraints.NONE;
        lg.gridx = 0; lg.gridy = r; left.add(new JLabel("Pay Coverage:"), lg);
        lg.fill = GridBagConstraints.BOTH;
        lg.gridx = 1; left.add(monthCombo, lg);
        lg.gridx = 2; left.add(cutoffCombo, lg);
        r++;

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBtns.setOpaque(false);
        leftBtns.add(computeButton);
        leftBtns.add(payslipButton);
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(clearButton);
        JPanel actionButtons = new JPanel(new BorderLayout());
        actionButtons.setOpaque(false);
        actionButtons.add(leftBtns, BorderLayout.WEST);
        actionButtons.add(rightBtns, BorderLayout.EAST);
        lg.gridx = 0; lg.gridy = r; lg.gridwidth = 3;
        lg.fill = GridBagConstraints.HORIZONTAL;
        lg.insets = new Insets(14, 8, 12, 8);
        left.add(actionButtons, lg);
        lg.gridwidth = 1;
        lg.fill = GridBagConstraints.NONE;
        lg.insets = new Insets(12, 16, 12, 16);

        // ----- Right column: company-wide payroll summary -----
        JPanel right = new JPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder("Company Payroll Summary"));
        GridBagConstraints rg = new GridBagConstraints();
        rg.insets = new Insets(12, 14, 12, 14);
        rg.gridx = 0;
        rg.anchor = GridBagConstraints.WEST;
        rg.gridy = 0; right.add(summaryButton, rg);
        rg.gridy = 1; right.add(exportSummaryButton, rg);
        JLabel note = new JLabel("* Totals across all employees.");
        note.setFont(note.getFont().deriveFont(Font.ITALIC, note.getFont().getSize2D() - 1f));
        note.setForeground(new Color(150, 153, 158));
        rg.gridy = 2; right.add(note, rg);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 4));
        GridBagConstraints cg = new GridBagConstraints();
        cg.fill = GridBagConstraints.BOTH;
        cg.gridy = 0;
        cg.gridx = 0; cg.weightx = 0; controls.add(left, cg);
        cg.gridx = 1; cg.weightx = 0; cg.insets = new Insets(0, 28, 0, 0); controls.add(right, cg);
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        cg.gridx = 2; cg.weightx = 1.0; cg.insets = new Insets(0, 0, 0, 0);
        controls.add(filler, cg);
        return controls;
    }

    private void updatePayslipButton() {
        boolean wholeMonth = cutoffCombo.getSelectedIndex() == 2;
        payslipButton.setEnabled(!wholeMonth);
    }

    private JPanel buildDefaultView() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel msg = new JLabel("<html><div style='text-align:center;'>"
                + "<h2 style='color:#21618C;'>Salary Computation</h2>"
                + "<p>Choose a <b>Scope</b> and <b>Pay Coverage</b>, then:</p>"
                + "<p><b>Compute</b> &mdash; calculate and save salaries "
                + "(one employee shows a form, all employees show a table).</p>"
                + "<p><b>View Payslip</b> &mdash; display a formatted payslip "
                + "(Cutoff 1 or Cutoff 2 only).</p>"
                + "</div></html>", SwingConstants.CENTER);
        panel.add(msg, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFormView() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Computation (One Employee)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int r = 0;
        addFormRow(form, gbc, r++, "Employee Number:", fNumber);
        addFormRow(form, gbc, r++, "Employee Name:", fName);
        addFormRow(form, gbc, r++, "Birthday:", fBirthday);
        addFormRow(form, gbc, r++, "Pay Period:", fPeriod);
        addFormRow(form, gbc, r++, "Rate:", fRate);
        addFormRow(form, gbc, r++, "Hours Worked:", fHours);
        addFormRow(form, gbc, r++, "Gross Pay:", fGross);
        addFormRow(form, gbc, r++, "Withholding Tax:", fTax);
        addFormRow(form, gbc, r++, "SSS Contribution:", fSss);
        addFormRow(form, gbc, r++, "Pag-IBIG Contribution:", fPagibig);
        addFormRow(form, gbc, r++, "PhilHealth Contribution:", fPhilhealth);
        addFormRow(form, gbc, r++, "Deductions:", fDeductions);
        addFormRow(form, gbc, r++, "Net Pay:", fNet);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(form, BorderLayout.NORTH);
        return wrap;
    }

    private JScrollPane buildTableView() {
        // Clicking a row fills the employee number field, so the user can then
        // switch to "One Employee" and view that employee's payslip directly.
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            // Only populate in "One Employee" mode (the field is disabled in All).
            if (scopeCombo.getSelectedIndex() != 0) return;
            int row = table.getSelectedRow();
            if (row != -1) {
                Object empNo = tableModel.getValueAt(row, 0);
                if (empNo != null) {
                    empNoField.setText(empNo.toString());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Computation (All Employees)"));
        return scroll;
    }

    private JScrollPane buildPayslipView() {
        payslipPane.setContentType("text/html");
        payslipPane.setEditable(false);
        JScrollPane scroll = new JScrollPane(payslipPane);
        scroll.setBorder(BorderFactory.createTitledBorder("Payslip"));
        return scroll;
    }

    private void updateScopeControls() {
        boolean one = scopeCombo.getSelectedIndex() == 0;
        // Switching to "All Employees" clears the (now unused) single number.
        if (!one) {
            empNoField.setText("");
        }
        empNoField.setEnabled(one);
        empNoField.setBackground(one ? Color.WHITE : new Color(233, 236, 239));
    }

    // ===================== ACTIONS =====================

    private void compute() {
        int month = monthCombo.getSelectedIndex() + 6;
        boolean all = scopeCombo.getSelectedIndex() == 1;

        if (all) {
            List<Employee> employees = dashboard.getEmployees();
            if (employees == null || employees.isEmpty()) {
                error("There are no employees to compute.");
                return;
            }
            tableModel.setRowCount(0);
            for (Employee e : employees) {
                Breakdown b = applyComputation(e, month);
                tableModel.addRow(new Object[]{
                        e.getEmpNo(), e.getFullName(), periodLabel(),
                        php.format(e.getHourlyRate()), String.format("%.2f", b.hours),
                        php.format(b.gross), php.format(b.deductions), php.format(b.net)
                });
            }
            dashboard.saveChangesToFile();
            dashboard.dataChanged();
            cards.show(resultArea, "table");
            info("All employees computed and saved for " + periodLabel() + ".");
            return;
        }

        Employee e = requireEmployee();
        if (e == null) return;
        applyComputation(e, month);
        dashboard.saveChangesToFile();
        dashboard.dataChanged();
        fillForm(e, month);
        cards.show(resultArea, "form");
        info("Salary computed and saved for " + e.getEmpNo() + ".");
    }

    private void viewPayslip() {
        int month = monthCombo.getSelectedIndex() + 6;
        boolean all = scopeCombo.getSelectedIndex() == 1;

        if (all) {
            List<Employee> employees = dashboard.getEmployees();
            if (employees == null || employees.isEmpty()) {
                error("There are no employees to show.");
                return;
            }
            StringBuilder sb = new StringBuilder("<html><body>");
            for (Employee e : employees) {
                sb.append(payslipBodyHtml(e, month));
                sb.append("<div style='border-top:2px dashed #888; margin:10px 0;'></div>");
            }
            sb.append("</body></html>");
            payslipPane.setText(sb.toString());
            payslipPane.setCaretPosition(0);
            cards.show(resultArea, "payslip");
            return;
        }

        Employee e = requireEmployee();
        if (e == null) return;
        payslipPane.setText("<html><body>" + payslipBodyHtml(e, month) + "</body></html>");
        payslipPane.setCaretPosition(0);
        cards.show(resultArea, "payslip");
    }

    private void clearToDefault() {
        empNoField.setText("");
        scopeCombo.setSelectedIndex(0);
        monthCombo.setSelectedIndex(0);
        cutoffCombo.setSelectedIndex(0);
        updateScopeControls();
        cards.show(resultArea, "default");
    }

    private Employee requireEmployee() {
        String id = empNoField.getText().trim();
        if (id.isEmpty()) {
            error("Please enter an employee number.");
            return null;
        }
        Employee e = dashboard.findByEmpNo(id);
        if (e == null) {
            error("Employee number does not exist.");
            return null;
        }
        return e;
    }

    // ===================== COMPUTATION =====================

    /** Values for the currently selected pay period. */
    private static class Breakdown {
        double hours, gross, tax, sss, pagibig, philhealth, deductions, net;
    }

    private Breakdown breakdown(Employee e, int month) {
        int sel = cutoffCombo.getSelectedIndex(); // 0=Cutoff1, 1=Cutoff2, 2=Whole Month
        PayrollCalculator.MonthlyPayslip p =
                PayrollCalculator.computeMonth(dashboard.getAttendance(), e, month);

        Breakdown b = new Breakdown();
        if (sel == 0) {
            b.hours = p.hours1;
            b.gross = p.gross1;
            b.net = p.net1;
            // no deductions on the first cutoff
        } else if (sel == 1) {
            b.hours = p.hours2;
            b.gross = p.gross2;
            b.tax = p.tax; b.sss = p.sss; b.pagibig = p.pagIbig; b.philhealth = p.philHealth;
            b.deductions = p.totalDeductions;
            b.net = p.net2;
        } else {
            b.hours = p.hours1 + p.hours2;
            b.gross = p.gross1 + p.gross2;
            b.tax = p.tax; b.sss = p.sss; b.pagibig = p.pagIbig; b.philhealth = p.philHealth;
            b.deductions = p.totalDeductions;
            b.net = p.net1 + p.net2;
        }
        return b;
    }

    /** Computes, stores the values on the employee, and returns them. */
    private Breakdown applyComputation(Employee e, int month) {
        Breakdown b = breakdown(e, month);
        e.setComputedPayPeriod(periodLabel());
        e.setComputedHours(b.hours);
        e.setComputedGross(b.gross);
        e.setComputedDeductions(b.deductions);
        e.setComputedNet(b.net);
        return b;
    }

    private void fillForm(Employee e, int month) {
        Breakdown b = breakdown(e, month);
        fNumber.setText(e.getEmpNo());
        fName.setText(e.getFullName());
        fBirthday.setText(e.getBirthday());
        fPeriod.setText(periodLabel());
        fRate.setText(php.format(e.getHourlyRate()));
        fHours.setText(String.format("%.2f", b.hours));
        fGross.setText(php.format(b.gross));
        fTax.setText(php.format(b.tax));
        fSss.setText(php.format(b.sss));
        fPagibig.setText(php.format(b.pagibig));
        fPhilhealth.setText(php.format(b.philhealth));
        fDeductions.setText(php.format(b.deductions));
        fNet.setText(php.format(b.net));
    }

    /**
     * Builds a document-style payslip modeled on a standard two-column payslip:
     * Employee Information / Other Information, Earnings / Gov't Deductions
     * (with indented line items and aligned sub totals), and Gross / Net pay.
     */
    private String payslipBodyHtml(Employee e, int month) {
        Breakdown b = breakdown(e, month);

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:sans-serif; padding:16px 56px; font-size:12px;'>");

        sb.append("<div style='background:#d4e6f1; text-align:center; padding:6px;'>"
                + "<span style='font-size:24px; font-weight:bold;'>MotorPH</span></div>");
        sb.append("<div style='text-align:center; font-size:14px; font-weight:bold; margin:8px 0;'>Payslip</div>");

        sb.append("<table style='margin-bottom:8px;'>");
        sb.append("<tr><td><b>Pay Period</b></td><td>:&nbsp;&nbsp;").append(escape(periodLabel())).append("</td></tr>");
        sb.append("<tr><td><b>Hours Worked</b></td><td>:&nbsp;&nbsp;").append(String.format("%.2f", b.hours)).append("</td></tr>");
        sb.append("</table>");

        // Employee Information | Other Information
        sb.append("<table width='100%' cellpadding='4'><tr valign='top'>");
        sb.append("<td width='50%' style='border:1px solid #000;'>");
        sb.append("<b>Employee Information</b>");
        sb.append("<table width='100%'>");
        sb.append(kv("Employee Code", e.getEmpNo()));
        sb.append(kv("Employee Name", e.getFullName()));
        sb.append(kv("Birthday", e.getBirthday()));
        sb.append(kv("TIN", e.getTin()));
        sb.append(kv("SSS No.", e.getSss()));
        sb.append("</table></td>");

        sb.append("<td width='50%' style='border:1px solid #000;'>");
        sb.append("<b>Other Information</b>");
        sb.append("<table width='100%'>");
        sb.append(kv("Designation", e.getPosition()));
        sb.append(kv("Supervisor", e.getSupervisor()));
        sb.append(kv("Employment Type", e.getStatus()));
        sb.append(kv("Payroll Frequency", "Semi-monthly"));
        sb.append(kv("Monthly Rate", php.format(e.getBasicSalary())));
        sb.append("</table></td>");
        sb.append("</tr></table>");

        // Earnings | Gov't Deductions (3-column tables: indent, label, amount)
        sb.append("<table width='100%' cellpadding='4'><tr valign='top'>");

        sb.append("<td width='50%'>");
        sb.append("<table width='100%'>");
        sb.append("<tr><td colspan='2'><b>Earnings:</b></td><td align='right'><b>This Period</b></td></tr>");
        sb.append(indentMoney("REG BASIC", b.gross));
        sb.append("<tr><td width='18'></td><td></td><td align='right'><hr noshade size='1'></td></tr>");
        sb.append("<tr><td width='18'></td><td>Sub Total:</td>"
                + "<td align='right'>" + php.format(b.gross) + "</td></tr>");
        sb.append("</table></td>");

        sb.append("<td width='50%'>");
        sb.append("<table width='100%'>");
        sb.append("<tr><td colspan='2'><b>Gov't Deductions:</b></td><td align='right'><b>This Period</b></td></tr>");
        sb.append(indentMoney("HDMF (Pag-IBIG)", b.pagibig));
        sb.append(indentMoney("PHIC (PhilHealth)", b.philhealth));
        sb.append(indentMoney("SSS", b.sss));
        sb.append(indentMoney("WTAX", b.tax));
        sb.append("<tr><td width='18'></td><td></td><td align='right'><hr noshade size='1'></td></tr>");
        sb.append("<tr><td width='18'></td><td>Total Deductions:</td>"
                + "<td align='right'>" + php.format(b.deductions) + "</td></tr>");
        sb.append("</table></td>");

        sb.append("</tr></table>");

        // Gross / Net (both bold, underlined amounts, mirrored section labels)
        sb.append("<table width='100%' cellpadding='4' style='margin-top:8px;'><tr valign='top'>");
        sb.append("<td width='50%'><table width='100%'><tr>"
                + "<td><b>GROSS PAY:</b></td>"
                + "<td align='right'><b><u>" + php.format(b.gross) + "</u></b></td></tr></table></td>");
        sb.append("<td width='50%'><table width='100%'><tr>"
                + "<td><b>NET PAY:</b></td>"
                + "<td align='right'><b style='color:#1E8449;'><u>" + php.format(b.net) + "</u></b></td></tr></table></td>");
        sb.append("</tr></table>");

        sb.append("</div>");
        return sb.toString();
    }

    private String kv(String label, String value) {
        return "<tr><td style='color:#333;'>" + label + "</td><td>:&nbsp;&nbsp;" + escape(value) + "</td></tr>";
    }

    private String indentMoney(String label, double value) {
        return "<tr><td width='18'></td><td>" + label + "</td>"
                + "<td align='right'>" + php.format(value) + "</td></tr>";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String periodLabel() {
        return monthCombo.getSelectedItem() + " - " + cutoffCombo.getSelectedItem();
    }

    // ===================== HELPERS =====================

    private static JTextField roField() {
        JTextField f = new JTextField(22);
        f.setEditable(false);
        f.setBackground(Color.WHITE);
        return f;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(Font.PLAIN));
        form.add(l, gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    /** Called when shared data changes; no cached state to rebuild here. */
    public void refresh() {
        // Intentionally empty: reads live data on each computation.
    }

    // ===================== PAYROLL SUMMARY (FEATURE 5) =====================

    private void showSummary() {
        List<Employee> employees = dashboard.getEmployees();
        if (employees == null || employees.isEmpty()) {
            error("There are no employees to summarize.");
            return;
        }
        PayrollCalculator.PayrollSummary s =
                PayrollCalculator.generatePayrollSummary(employees, dashboard.getAttendance());

        // Render the summary as a styled report card inside the pop-up dialog.
        String today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String html =
                "<html><body style='width:420px; font-family:sans-serif;'>"
                + "<div style='background:#21618C; color:white; padding:16px 20px;'>"
                + "<div style='font-size:22px; font-weight:bold;'>MotorPH</div>"
                + "<div style='font-size:13px;'>Company Payroll Summary</div>"
                + "<div style='font-size:11px; color:#CFE0EC;'>(As of " + today + ")</div>"
                + "</div>"
                + "<div style='padding:18px 20px;'>"
                + "<table width='100%' cellspacing='0' cellpadding='9' style='font-size:14px;'>"
                + summaryRow("Total Employees", String.valueOf(s.totalEmployees), false)
                + summaryRow("Total Gross Pay", php.format(s.totalGrossPay), true)
                + summaryRow("Total Deductions", php.format(s.totalDeductions), false)
                + "</table>"
                + "<div style='border-top:2px solid #21618C; margin-top:12px; padding-top:10px;'>"
                + "<table width='100%' cellpadding='9' style='font-size:17px; font-weight:bold;'>"
                + "<tr><td>Average Net Pay</td>"
                + "<td align='right' style='color:#1E8449;'>" + php.format(s.averageNetPay) + "</td></tr>"
                + "</table></div>"
                + "<div style='font-style:italic; font-size:11px; color:#9AA0A6; padding-top:10px;'>"
                + "* Computed from all employees with attendance data.</div>"
                + "</div></body></html>";

        JLabel card = new JLabel(html);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xBD, 0xC3, 0xC7)));
        Dialogs.card(this, "Payroll Summary", card);
    }

    /** One striped label/value row for the summary card. */
    private String summaryRow(String label, String value, boolean shaded) {
        String bg = shaded ? " style='background:#F4F6F7;'" : "";
        return "<tr" + bg + "><td style='color:#555;'>" + label + "</td>"
                + "<td align='right'>" + value + "</td></tr>";
    }

    private void exportSummary() {
        List<Employee> employees = dashboard.getEmployees();
        if (employees == null || employees.isEmpty()) {
            error("There are no employees to summarize.");
            return;
        }
        PayrollCalculator.PayrollSummary s =
                PayrollCalculator.generatePayrollSummary(employees, dashboard.getAttendance());
        String fileName = "MotorPH_PayrollSummary.csv";
        List<String> lines = new ArrayList<>();
        lines.add("Metric,Value");
        lines.add("Total Employees," + s.totalEmployees);
        lines.add("Total Gross Pay," + String.format("%.2f", s.totalGrossPay));
        lines.add("Total Deductions," + String.format("%.2f", s.totalDeductions));
        lines.add("Average Net Pay," + String.format("%.2f", s.averageNetPay));

        try {
            CsvManager.writeCsv(fileName, lines);
            info("Payroll summary exported successfully.");
        } catch (IOException ex) {
            error("Could not export summary.\n" + ex.getMessage());
        }
    }

    private void info(String message) {
        Dialogs.info(this, message);
    }

    private void error(String message) {
        Dialogs.error(this, message);
    }
}
