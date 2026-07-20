package app.views;

import app.services.CsvManager;
import app.models.AttendanceRecord;
import app.models.Employee;
import app.util.Dialogs;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Payroll staff dashboard.
 *
 * A single window with two tabs:
 *   1. Employee Information  - view / add / update / delete records (no payroll).
 *   2. Salary Computation    - compute and view payslips per employee or for all,
 *                              by month and cutoff (results persist to the CSV),
 *                              plus the company-wide payroll summary.
 *
 * The frame owns the shared data (employee list, lookup map, attendance) so all
 * tabs stay in sync, and exposes helpers to persist and refresh.
 */
public class PayrollDashboardFrame extends JFrame {

    private List<Employee> employees;
    private List<AttendanceRecord> attendance;
    private final Map<String, Employee> employeeMap = new HashMap<>();

    private EmployeeInfoPanel employeeInfoPanel;
    private SalaryComputationPanel salaryComputationPanel;

    public PayrollDashboardFrame() {
        setTitle("MotorPH - Payroll Staff Dashboard");
        setSize(1300, 880);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Open maximized by default; the 1300x880 size is the restore-down size.
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        loadData();

        JLabel header = new JLabel("MotorPH Employee Application", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(33, 97, 140));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(22, 10, 22, 10));

        employeeInfoPanel = new EmployeeInfoPanel(this);
        salaryComputationPanel = new SalaryComputationPanel(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employees", employeeInfoPanel);
        tabs.addTab("Payroll", salaryComputationPanel);

        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");
        UiStyle.outlineButton(logoutButton, UiStyle.PRIMARY);
        UiStyle.outlineButton(exitButton, UiStyle.DANGER);
        UiStyle.sameWidth(logoutButton, exitButton);
        logoutButton.addActionListener(e -> logout());
        exitButton.addActionListener(e -> System.exit(0));

        JPanel south = new JPanel();
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 219, 223)),
                BorderFactory.createEmptyBorder(12, 0, 14, 0)));
        south.add(logoutButton);
        south.add(exitButton);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    // ===================== DATA =====================

    private void loadData() {
        try {
            employees = CsvManager.loadEmployees();
            attendance = CsvManager.loadAttendance();
            rebuildIndex();
        } catch (IOException ex) {
            Dialogs.error(this, "Could not read CSV data.\n" + ex.getMessage(), "File Error");
        }
    }

    /** Rebuilds the employee-number lookup map from the current list. */
    public void rebuildIndex() {
        employeeMap.clear();
        if (employees == null) return;
        for (Employee e : employees) {
            employeeMap.put(e.getEmpNo(), e);
        }
    }

    /** Saves the employee list to the CSV file. */
    public void saveChangesToFile() {
        try {
            CsvManager.saveEmployees(employees);
        } catch (IOException ex) {
            Dialogs.error(this,
                    "Could not save to " + CsvManager.EMPLOYEE_FILE + ".\n" + ex.getMessage(),
                    "Save Error");
        }
    }

    /**
     * Adds a new employee to the in-memory collection, saves the change to the
     * CSV file, and refreshes every tab. (Data layer for the Add action.)
     */
    public void addRecord(Employee employee) {
        employees.add(employee);
        saveChangesToFile();
        dataChanged();
    }

    /**
     * Persists edits made to an existing employee (found by ID and modified in
     * the form) to the CSV file, then refreshes every tab. (updateRecord)
     */
    public void updateRecord(Employee employee) {
        saveChangesToFile();
        dataChanged();
    }

    /**
     * Removes an employee from the in-memory collection, saves the change to
     * the CSV file, and refreshes every tab. (deleteRecord)
     */
    public void deleteRecord(Employee employee) {
        employees.remove(employee);
        saveChangesToFile();
        dataChanged();
    }

    /** Refreshes every tab after the underlying data changes. */
    public void dataChanged() {
        rebuildIndex();
        if (employeeInfoPanel != null) employeeInfoPanel.refresh();
        if (salaryComputationPanel != null) salaryComputationPanel.refresh();
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public Employee findByEmpNo(String id) {
        return employeeMap.get(id);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
