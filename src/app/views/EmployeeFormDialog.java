package app.views;

import app.models.Employee;
import app.services.EmployeeValidator;
import app.util.Dialogs;
import app.util.FieldFilters;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A single modal form for adding or updating an employee. It shows every
 * editable employee field pre-filled with the current values, validates all
 * inputs on Save, and writes the values back into the given {@link Employee}.
 *
 * Use {@link #isSaved()} after the dialog closes to know whether the user saved.
 */
public class EmployeeFormDialog extends JDialog {

    private final Employee employee;
    private final boolean addMode;
    private final List<Employee> existingEmployees;
    private boolean saved = false;

    private final JTextField empNoField = new JTextField(20);
    private final JTextField lastNameField = new JTextField(20);
    private final JTextField firstNameField = new JTextField(20);
    private final JTextField birthdayField = new JTextField(20);
    private final JTextField addressField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField sssField = new JTextField(20);
    private final JTextField philhealthField = new JTextField(20);
    private final JTextField tinField = new JTextField(20);
    private final JTextField pagibigField = new JTextField(20);
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField positionField = new JTextField(20);
    private final JTextField supervisorField = new JTextField(20);
    private final JTextField basicSalaryField = new JTextField(20);
    private final JTextField riceSubsidyField = new JTextField(20);
    private final JTextField phoneAllowanceField = new JTextField(20);
    private final JTextField clothingAllowanceField = new JTextField(20);
    private final JTextField grossSemiMonthlyField = new JTextField(20);
    private final JTextField hourlyRateField = new JTextField(20);

    // Save starts disabled and enables once the user edits something.
    private JButton saveButton;

    // Captured so red validation borders can be reverted to the original look.
    private final Border defaultFieldBorder = new JTextField().getBorder();

    public EmployeeFormDialog(Frame owner, Employee employee, boolean addMode, List<Employee> existingEmployees) {
        super(owner, addMode ? "Add Employee" : "Update Employee", true);
        this.employee = employee;
        this.addMode = addMode;
        this.existingEmployees = existingEmployees;

        // No OS title bar/icon: the dialog has its own styled header instead.
        setUndecorated(true);

        buildUi();
        populateFields();
        installInputFilters();
        attachAutoClear();

        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)));

        // Employee number is only editable when adding a new record.
        empNoField.setEditable(addMode);

        setSize(560, 700);
        setLocationRelativeTo(owner);
    }

    /** Blocks invalid characters at the keystroke level on number-only fields. */
    private void installInputFilters() {
        FieldFilters.mask(empNoField, "#####");
        FieldFilters.nameChars(lastNameField);
        FieldFilters.nameChars(firstNameField);
        FieldFilters.positionChars(positionField);
        FieldFilters.supervisorChars(supervisorField);
        // Auto-format the structured fields so the user only types digits and the
        // separators (slashes / dashes) are inserted for them as they go.
        FieldFilters.mask(birthdayField, "##/##/####");
        FieldFilters.mask(phoneField, "###-###-###");
        FieldFilters.mask(sssField, "##-#######-#");
        FieldFilters.mask(philhealthField, "############");
        FieldFilters.mask(tinField, "###-###-###-###");
        FieldFilters.mask(pagibigField, "############");
        FieldFilters.decimal(basicSalaryField);
        FieldFilters.decimal(riceSubsidyField);
        FieldFilters.decimal(phoneAllowanceField);
        FieldFilters.decimal(clothingAllowanceField);
        FieldFilters.decimal(grossSemiMonthlyField);
        FieldFilters.decimal(hourlyRateField);

        // Gray hints showing the expected format before the user types.
        FieldFilters.placeholder(empNoField, "(ex. 10001)");
        FieldFilters.placeholder(birthdayField, "(ex. 06/19/1988)");
        FieldFilters.placeholder(phoneField, "(ex. 966-860-270)");
        FieldFilters.placeholder(sssField, "(ex. 44-4506057-3)");
        FieldFilters.placeholder(philhealthField, "(ex. 123456789012)");
        FieldFilters.placeholder(tinField, "(ex. 442-605-657-000)");
        FieldFilters.placeholder(pagibigField, "(ex. 123456789012)");
    }

    /** Clears a field's red invalid border as soon as the user edits it. */
    private void attachAutoClear() {
        JTextField[] all = {
                empNoField, lastNameField, firstNameField, birthdayField, addressField,
                phoneField, sssField, philhealthField, tinField, pagibigField,
                positionField, supervisorField, basicSalaryField, riceSubsidyField,
                phoneAllowanceField, clothingAllowanceField, grossSemiMonthlyField, hourlyRateField
        };
        for (JTextField f : all) {
            f.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { reset(); }
                @Override public void removeUpdate(DocumentEvent e) { reset(); }
                @Override public void changedUpdate(DocumentEvent e) { reset(); }

                private void reset() {
                    f.setBorder(FieldFilters.withPlaceholder(defaultFieldBorder));
                    markDirty();
                }
            });
        }
        // Changing the status dropdown also counts as an edit.
        statusCombo.addActionListener(e -> markDirty());
    }

    /** Enables Save once the user has changed something in the form. */
    private void markDirty() {
        if (saveButton != null) {
            saveButton.setEnabled(true);
        }
    }

    private void buildUi() {
        JLabel heading = new JLabel(addMode ? "Add New Employee" : "Update Employee", SwingConstants.CENTER);
        heading.setOpaque(true);
        heading.setBackground(new Color(52, 152, 219));
        heading.setForeground(Color.WHITE);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JLabel legend = new JLabel(
                "<html>Fields marked with <font color='red'>*</font> are mandatory</html>",
                SwingConstants.CENTER);
        legend.setForeground(new Color(108, 117, 125));
        legend.setFont(legend.getFont().deriveFont(Font.PLAIN, legend.getFont().getSize2D() - 1f));
        legend.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(heading, BorderLayout.NORTH);
        headerPanel.add(legend, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addSectionHeader(form, gbc, row++, "Personal Information");
        addRow(form, gbc, row++, req("Employee Number:"), empNoField);
        addRow(form, gbc, row++, req("Last Name:"), lastNameField);
        addRow(form, gbc, row++, req("First Name:"), firstNameField);
        addRow(form, gbc, row++, req("Birthday:"), birthdayField);
        addRow(form, gbc, row++, "Address:", addressField);
        addRow(form, gbc, row++, "Phone Number:", phoneField);

        addSectionHeader(form, gbc, row++, "Government IDs");
        addRow(form, gbc, row++, "SSS #:", sssField);
        addRow(form, gbc, row++, "PhilHealth #:", philhealthField);
        addRow(form, gbc, row++, "TIN #:", tinField);
        addRow(form, gbc, row++, "Pag-IBIG #:", pagibigField);

        addSectionHeader(form, gbc, row++, "Employment");
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo.setPreferredSize(new Dimension(
                positionField.getPreferredSize().width, statusCombo.getPreferredSize().height));
        form.add(statusCombo, gbc);
        row++;
        addRow(form, gbc, row++, "Position:", positionField);
        addRow(form, gbc, row++, "Immediate Supervisor:", supervisorField);

        addSectionHeader(form, gbc, row++, "Compensation");
        addRow(form, gbc, row++, req("Basic Salary:"), basicSalaryField);
        addRow(form, gbc, row++, "Rice Subsidy:", riceSubsidyField);
        addRow(form, gbc, row++, "Phone Allowance:", phoneAllowanceField);
        addRow(form, gbc, row++, "Clothing Allowance:", clothingAllowanceField);
        addRow(form, gbc, row++, "Gross Semi-monthly Rate:", grossSemiMonthlyField);
        addRow(form, gbc, row++, req("Hourly Rate:"), hourlyRateField);

        saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());
        UiStyle.successButton(saveButton);
        UiStyle.secondaryButton(cancelButton);
        UiStyle.sameWidth(saveButton, cancelButton);
        // Nothing to save until the user actually edits a field.
        saveButton.setEnabled(false);

        JPanel buttons = new JPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(12, 0, 14, 0));
        buttons.add(saveButton);
        buttons.add(cancelButton);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(232, 235, 238)));

        add(headerPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);
    }

    /** A bold, colored section title spanning both columns with an underline. */
    private void addSectionHeader(JPanel form, GridBagConstraints gbc, int row, String title) {
        JLabel header = new JLabel(title);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12.5f));
        header.setForeground(new Color(33, 97, 140));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(214, 219, 223)),
                BorderFactory.createEmptyBorder(0, 0, 3, 0)));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(row == 0 ? 2 : 14, 8, 6, 8);
        form.add(header, gbc);

        // Reset for the normal label/field rows.
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 8, 4, 8);
    }

    /** Builds a label with a red asterisk marking a required field. */
    private String req(String label) {
        return "<html>" + label + " <font color='red'>*</font></html>";
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        form.add(field, gbc);
    }

    private void populateFields() {
        empNoField.setText(employee.getEmpNo());
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        birthdayField.setText(normalizeBirthday(employee.getBirthday()));
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhone());
        sssField.setText(employee.getSss());
        philhealthField.setText(employee.getPhilhealth());
        tinField.setText(employee.getTin());
        pagibigField.setText(employee.getPagibig());
        String status = employee.getStatus();
        if (status != null && (status.equalsIgnoreCase("Probationary"))) {
            statusCombo.setSelectedItem("Probationary");
        } else {
            statusCombo.setSelectedItem("Regular");
        }
        positionField.setText(employee.getPosition());
        supervisorField.setText(employee.getSupervisor());
        // In add mode the numeric fields start blank so the required-field check
        // can detect them; in update mode they show the current values.
        basicSalaryField.setText(addMode ? "" : numText(employee.getBasicSalary()));
        riceSubsidyField.setText(addMode ? "" : numText(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(addMode ? "" : numText(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(addMode ? "" : numText(employee.getClothingAllowance()));
        grossSemiMonthlyField.setText(addMode ? "" : numText(employee.getGrossSemiMonthly()));
        hourlyRateField.setText(addMode ? "" : numText(employee.getHourlyRate()));

        JTextField[] all = {
                empNoField, lastNameField, firstNameField, birthdayField, addressField,
                phoneField, sssField, philhealthField, tinField, pagibigField,
                positionField, supervisorField, basicSalaryField, riceSubsidyField,
                phoneAllowanceField, clothingAllowanceField, grossSemiMonthlyField, hourlyRateField
        };
        for (JTextField f : all) {
            f.setCaretPosition(0);
        }
    }

    private void onSave() {
        // Layer 1: required fields must be filled. Empty ones are flagged red
        // and a single warning is shown before any format checks run.
        clearFieldMarks();
        List<JTextField> missing = new ArrayList<>();
        if (addMode) requireField(empNoField, missing);
        requireField(lastNameField, missing);
        requireField(firstNameField, missing);
        requireField(birthdayField, missing);
        requireField(basicSalaryField, missing);
        requireField(hourlyRateField, missing);

        if (!missing.isEmpty()) {
            for (JTextField f : missing) {
                f.setBorder(FieldFilters.withPlaceholder(
                        UiStyle.roundedLine(Color.RED, new Insets(5, 9, 5, 9))));
            }
            missing.get(0).requestFocus();
            error("Please fill in all required fields.");
            return;
        }

        // Layer 2: format validation.
        // --- Employee number (add mode only) ---
        String empNo = empNoField.getText().trim();
        if (addMode) {
            if (!EmployeeValidator.isValidEmployeeNumber(empNo)) {
                markInvalid(empNoField);
                error("Employee number must be exactly 5 digits.");
                return;
            }
            if (existingEmployees != null) {
                for (Employee other : existingEmployees) {
                    if (other.getEmpNo().equals(empNo)) {
                        markInvalid(empNoField);
                        error("An employee with number " + empNo + " already exists.");
                        return;
                    }
                }
            }
        }

        // --- Names ---
        String lastName = lastNameField.getText().trim();
        if (!isValidName(lastName)) {
            markInvalid(lastNameField);
            error("Last name may only contain letters, spaces, and . ' - characters.");
            return;
        }
        String firstName = firstNameField.getText().trim();
        if (!isValidName(firstName)) {
            markInvalid(firstNameField);
            error("First name may only contain letters, spaces, and . ' - characters.");
            return;
        }

        // --- Birthday ---
        String birthday = birthdayField.getText().trim();
        if (!isValidBirthday(birthday)) {
            markInvalid(birthdayField);
            error("Please enter a valid, non-future birthday in MM/DD/YYYY format.");
            return;
        }

        // --- ID/number fields (optional; must match the CSV data formats) ---
        if (!isValidPattern(phoneField, EmployeeValidator.PHONE_PATTERN,
                "Phone Number must follow the format ### - ### - ###.")) return;
        if (!isValidPattern(sssField, EmployeeValidator.SSS_PATTERN,
                "SSS # must follow the format ## - ####### - #.")) return;
        if (!isValidPattern(philhealthField, EmployeeValidator.PHILHEALTH_PATTERN,
                "PhilHealth # must be 12 digits.")) return;
        if (!isValidPattern(tinField, EmployeeValidator.TIN_PATTERN,
                "TIN # must follow the format ### - ### - ### - ###.")) return;
        if (!isValidPattern(pagibigField, EmployeeValidator.PAGIBIG_PATTERN,
                "Pag-IBIG # must be 12 digits.")) return;

        // --- Numeric fields (required: basic salary, hourly rate) ---
        Double basicSalary = parseNonNegative(basicSalaryField, "Basic Salary");
        if (basicSalary == null) return;
        Double hourlyRate = parseNonNegative(hourlyRateField, "Hourly Rate");
        if (hourlyRate == null) return;

        // --- Numeric fields (optional: blank counts as 0) ---
        Double riceSubsidy = parseOptional(riceSubsidyField, "Rice Subsidy");
        if (riceSubsidy == null) return;
        Double phoneAllowance = parseOptional(phoneAllowanceField, "Phone Allowance");
        if (phoneAllowance == null) return;
        Double clothingAllowance = parseOptional(clothingAllowanceField, "Clothing Allowance");
        if (clothingAllowance == null) return;
        Double grossSemiMonthly = parseOptional(grossSemiMonthlyField, "Gross Semi-monthly Rate");
        if (grossSemiMonthly == null) return;

        // --- Commit to the employee object ---
        if (addMode) {
            employee.setEmpNo(empNo);
        }
        employee.setLastName(lastName);
        employee.setFirstName(firstName);
        employee.setBirthday(birthday);
        employee.setAddress(addressField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setSss(sssField.getText().trim());
        employee.setPhilhealth(philhealthField.getText().trim());
        employee.setTin(tinField.getText().trim());
        employee.setPagibig(pagibigField.getText().trim());
        employee.setStatus((String) statusCombo.getSelectedItem());
        employee.setPosition(positionField.getText().trim());
        employee.setSupervisor(supervisorField.getText().trim());
        employee.setBasicSalary(basicSalary);
        employee.setRiceSubsidy(riceSubsidy);
        employee.setPhoneAllowance(phoneAllowance);
        employee.setClothingAllowance(clothingAllowance);
        employee.setGrossSemiMonthly(grossSemiMonthly);
        employee.setHourlyRate(hourlyRate);

        saved = true;
        dispose();
    }

    // ===================== FIELD MARKING =====================

    /** Adds a field to the missing list if it is blank. */
    private void requireField(JTextField field, List<JTextField> missing) {
        if (field.getText().trim().isEmpty()) {
            missing.add(field);
        }
    }

    /** Restores the default border on every input field. */
    private void clearFieldMarks() {
        JTextField[] all = {
                empNoField, lastNameField, firstNameField, birthdayField, addressField,
                phoneField, sssField, philhealthField, tinField, pagibigField,
                positionField, supervisorField, basicSalaryField, riceSubsidyField,
                phoneAllowanceField, clothingAllowanceField, grossSemiMonthlyField, hourlyRateField
        };
        for (JTextField f : all) {
            f.setBorder(FieldFilters.withPlaceholder(defaultFieldBorder));
        }
    }

    /** Flags a single field with a red border. */
    private void markInvalid(JTextField field) {
        field.setBorder(FieldFilters.withPlaceholder(
                UiStyle.roundedLine(Color.RED, new Insets(5, 9, 5, 9))));
        field.requestFocus();
    }

    // ===================== VALIDATION HELPERS =====================

    private boolean isValidName(String name) {
        return EmployeeValidator.isValidName(name);
    }

    /**
     * Validates an optional ID/number field against the exact format used in
     * the CSV data. Blank is allowed; if provided it must match the pattern.
     */
    private boolean isValidPattern(JTextField field, String pattern, String message) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            return true; // optional
        }
        if (!value.matches(pattern)) {
            markInvalid(field);
            error(message);
            return false;
        }
        return true;
    }

    private boolean isValidBirthday(String raw) {
        return EmployeeValidator.isValidBirthday(raw);
    }

    /** Pads an existing birthday to MM/DD/YYYY so it lines up with the field mask. */
    private String normalizeBirthday(String raw) {
        if (raw == null) {
            return "";
        }
        String[] parts = raw.trim().split("/");
        if (parts.length == 3) {
            try {
                int month = Integer.parseInt(parts[0].trim());
                int day = Integer.parseInt(parts[1].trim());
                int year = Integer.parseInt(parts[2].trim());
                return String.format("%02d/%02d/%04d", month, day, year);
            } catch (NumberFormatException ignored) {
                // Fall through and return the raw value unchanged.
            }
        }
        return raw;
    }

    private Double parseNonNegative(JTextField field, String fieldName) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value < 0) {
                markInvalid(field);
                error(fieldName + " cannot be negative.");
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            markInvalid(field);
            error("Please enter a valid number for " + fieldName + ".");
            return null;
        }
    }

    /** Parses an optional numeric field; blank counts as 0. */
    private Double parseOptional(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            return 0.0;
        }
        return parseNonNegative(field, fieldName);
    }

    private String numText(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    private void error(String message) {
        Dialogs.error(this, message, "Validation Error");
    }

    public boolean isSaved() {
        return saved;
    }
}
