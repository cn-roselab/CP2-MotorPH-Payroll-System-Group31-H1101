import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;

public class MotorPHGUI extends JFrame implements ActionListener {

    // ===================== COMPONENTS =====================

    JLabel titleLabel;

    JLabel employeeNumberLabel;
    JLabel employeeNameLabel;
    JLabel payCoverageLabel;
    JLabel cutoffLabel;

    JTextField employeeNumberField;
    JTextField employeeNameField;

    JComboBox<String> monthComboBox;
    JComboBox<String> cutoffComboBox;

   private JButton searchButton;
   private JButton computeButton;
   
   private JButton addButton;
   private JButton updateButton;
   private JButton deleteButton;
   
   private JButton clearButton;

    JTextArea resultArea;

    // ===================== CONSTRUCTOR =====================

    public MotorPHGUI() {

        // FRAME SETTINGS
        setTitle("MOTORPH EMPLOYEE MANAGEMENT SYSTEM");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MAIN PANEL
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 2, 10, 10));

        // ===================== TITLE =====================

        titleLabel = new JLabel(
                "MOTORPH EMPLOYEE MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        // ===================== LABELS =====================

        employeeNumberLabel =
                new JLabel("Employee Number:");

        employeeNameLabel =
                new JLabel("Employee Name:");

        payCoverageLabel =
                new JLabel("Pay Coverage:");

        cutoffLabel =
                new JLabel("Cutoff:");

        // ===================== TEXT FIELDS =====================

        employeeNumberField =
                new JTextField();

        employeeNameField =
                new JTextField();

        employeeNameField.setEditable(false);

        // ===================== COMBO BOX =====================

        String[] months = {
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        };

        monthComboBox =
                new JComboBox<>(months);

        String[] cutoffOptions = {
                "Cutoff 1",
                "Cutoff 2"
        };

        cutoffComboBox =
                new JComboBox<>(cutoffOptions);

        // ===================== BUTTONS =====================

        searchButton =
                new JButton("Search Employee");

        computeButton =
                new JButton("Compute Payroll");

        addButton =
                new JButton("Add Employee");
        
        updateButton = 
                new JButton("Update Employee");
                
        deleteButton =
                new JButton("Delete Employee");

        clearButton =
                new JButton("Clear");

        searchButton.addActionListener(this);
        computeButton.addActionListener(this);
        
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        clearButton.addActionListener(this);

        // ===================== RESULT AREA =====================

        resultArea =
                new JTextArea(25, 70);

        resultArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(resultArea);

        // ===================== ADD COMPONENTS =====================

        panel.add(employeeNumberLabel);
        panel.add(employeeNumberField);

        panel.add(employeeNameLabel);
        panel.add(employeeNameField);

        panel.add(payCoverageLabel);
        panel.add(monthComboBox);

        panel.add(cutoffLabel);
        panel.add(cutoffComboBox);

        panel.add(searchButton);
        panel.add(computeButton);
        
        panel.add(addButton);
        panel.add(updateButton);
        
        panel.add(deleteButton);
        panel.add(clearButton);

        // MAIN LAYOUT
        setLayout(new BorderLayout());

        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // LOAD EMPLOYEES
        MotorPHPayroll.loadEmployees();

        System.out.println(
                "Employees loaded: " +
                MotorPHPayroll.empId.size()
        );

        setVisible(true);
    }

    // ===================== BUTTON EVENTS =====================

    @Override
    public void actionPerformed(ActionEvent e) {

        // SEARCH EMPLOYEE
        if (e.getSource() == searchButton) {

            String employeeId =
                    employeeNumberField.getText().trim();

            // VALIDATION
            if (employeeId.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter employee number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            int index =
                    MotorPHPayroll.findEmployee(employeeId);

            if (index == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee not found.",
                        "Search Error",
                        JOptionPane.ERROR_MESSAGE
                );

                employeeNameField.setText("");

            } else {

                employeeNameField.setText(
                        MotorPHPayroll.fullName.get(index)
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Employee found successfully!"
                );
            }
        }

        // COMPUTE PAYROLL
        if (e.getSource() == computeButton) {

            String employeeId =
                    employeeNumberField.getText().trim();

            if (employeeId.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter employee number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            int index =
                    MotorPHPayroll.findEmployee(employeeId);

            if (index == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // MONTH SELECTION
            int month =
                    monthComboBox.getSelectedIndex() + 6;

            // CUTOFF SELECTION
            int startDay;
            int endDay;

            if (cutoffComboBox.getSelectedIndex() == 0) {

                startDay = 1;
                endDay = 15;

            } else {

                startDay = 16;
                endDay = 31;
            }

            // COMPUTE HOURS
            double hours =
                    MotorPHPayroll.computeHours(
                            employeeId,
                            month,
                            startDay,
                            endDay
                    );

            // COMPUTE GROSS
            double gross =
                    MotorPHPayroll.computeGross(
                            hours,
                            MotorPHPayroll.hourlyRate.get(index)
                    );

            double sss = 0;
            double philhealth = 0;
            double pagibig = 0;
            double tax = 0;

            double deductions = 0;
            double net = gross;

            // SECOND CUTOFF ONLY
            if (startDay == 16) {

                sss =
                        MotorPHApplyDeductions
                                .computeSSSDeduction(gross);

                philhealth =
                        MotorPHApplyDeductions
                                .computePhilHealthDeduction(gross);

                pagibig =
                        MotorPHApplyDeductions
                                .computePagIbigDeduction(gross);

                tax =
                        MotorPHApplyDeductions
                                .computeIncomeTaxDeduction(gross);

                deductions =
                        sss + philhealth + pagibig + tax;

                net =
                        MotorPHApplyDeductions
                                .computeNetPay(gross);
            }

            // CURRENCY FORMAT
            NumberFormat php =
                    NumberFormat.getCurrencyInstance(
                            new Locale("en", "PH")
                    );

            // DISPLAY RESULTS
            resultArea.setText("");

            resultArea.append(
                    "=========== MOTORPH PAYSLIP ===========\n\n"
            );

            resultArea.append(
                    "Employee #: "
                            + employeeId + "\n"
            );

            resultArea.append(
                    "Employee Name: "
                            + employeeNameField.getText()
                            + "\n"
            );

            resultArea.append(
                    "Pay Coverage: "
                            + monthComboBox.getSelectedItem()
                            + "\n"
            );

            resultArea.append(
                    "Cutoff: "
                            + cutoffComboBox.getSelectedItem()
                            + "\n\n"
            );

            resultArea.append(
                    "Hours Worked: "
                            + String.format("%.2f", hours)
                            + "\n"
            );

            resultArea.append(
                    "Gross Pay: "
                            + php.format(gross)
                            + "\n"
            );

            resultArea.append(
        "\n=========== DEDUCTIONS ===========\n\n"
);

resultArea.append(
        "SSS: "
                + php.format(sss)
                + "\n"
);

resultArea.append(
        "PhilHealth: "
                + php.format(philhealth)
                + "\n"
);

resultArea.append(
        "Pag-IBIG: "
                + php.format(pagibig)
                + "\n"
);

resultArea.append(
        "Tax: "
                + php.format(tax)
                + "\n\n"
);

resultArea.append(
        "Total Deductions: "
                + php.format(deductions)
                + "\n\n"
);

resultArea.append(
        "Net Pay: "
                + php.format(net)
                + "\n"
);

            JOptionPane.showMessageDialog(
                    this,
                    "Payroll computed successfully!"
            );
        }

       // ADD EMPLOYEE
        if (e.getSource() == addButton) {
                
                String newId =
                        JOptionPane.showInputDialog(
                                this,
                                "Enter Employee Number:"
                        );

                if (newId == null ||
                        newId.trim().isEmpty()) {

                        return;
                }

        String newName =
                JOptionPane.showInputDialog(
                        this,
                        "Enter Employee Name:"
                );

        if (newName == null ||
                newName.trim().isEmpty()) {

                return;
        }

        // ADD TO LISTS
        MotorPHPayroll.empId.add(
                newId.trim()
        );

        MotorPHPayroll.fullName.add(
                newName.trim()
        );

         MotorPHPayroll.birthday.add(
                "N/A"
        );

        MotorPHPayroll.hourlyRate.add(
                0.0
        );

        employeeNumberField.setText(
                newId.trim()
        );

        employeeNameField.setText(
                newName.trim()
        );

        JOptionPane.showMessageDialog(
                this,
                "Employee added successfully!"
        );
}

        // UPDATE EMPLOYEE
        if (e.getSource() == updateButton) {
        
        String employeeId =
            employeeNumberField.getText().trim();

        if (employeeId.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter employee number."
                );

                return;
        }

        int index =
                MotorPHPayroll.findEmployee(employeeId);

        if (index == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee not found."
                );

                return;
        }

        String newName =
                JOptionPane.showInputDialog(
                        this,
                        "Enter new employee name:"
                );

        if (newName == null ||
                        newName.trim().isEmpty()) {

                return;
        }

        MotorPHPayroll.fullName.set(
                index,
                newName.trim()
        );
        
        employeeNameField.setText(
                newName.trim()
        );

        JOptionPane.showMessageDialog(
                this,
                "Employee updated successfully!"
        );
}

        // DELETE EMPLOYEE
        if (e.getSource() == deleteButton) {

        String employeeId =
                employeeNumberField.getText().trim();

        if (employeeId.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter employee number."
        );

        return;
}

        int index =
                MotorPHPayroll.findEmployee(employeeId);

        if (index == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee not found."
                );

                return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this employee?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm == JOptionPane.YES_OPTION) {

                MotorPHPayroll.empId.remove(index);
                MotorPHPayroll.fullName.remove(index);
                MotorPHPayroll.birthday.remove(index);
                MotorPHPayroll.hourlyRate.remove(index);

                employeeNumberField.setText("");
                employeeNameField.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Employee deleted successfully!"
                );
        }
}

        // CLEAR BUTTON
        if (e.getSource() == clearButton) {

            employeeNumberField.setText("");
            employeeNameField.setText("");
            resultArea.setText("");

            monthComboBox.setSelectedIndex(0);
            cutoffComboBox.setSelectedIndex(0);

            JOptionPane.showMessageDialog(
                    this,
                    "Fields cleared successfully!"
            );
        }
    }

    // ===================== MAIN =====================

    public static void main(String[] args) {

        new MotorPHGUI();
    }
}
