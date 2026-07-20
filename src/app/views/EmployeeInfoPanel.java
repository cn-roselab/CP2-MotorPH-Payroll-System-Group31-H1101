package app.views;

import app.models.Employee;
import app.util.Dialogs;
import app.util.FieldFilters;
import app.util.MoneyFormatter;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Tab 1 - Employee Information.
 *
 * Pure record management: view, search, add, update, and delete employees.
 * No payroll computation happens here.
 */
public class EmployeeInfoPanel extends JPanel {

    private final PayrollDashboardFrame dashboard;
    private final NumberFormat php = MoneyFormatter.currency();

    private final JTextField searchField = new JTextField(16);
    private final JLabel nameValue = new JLabel("-");
    private final JLabel numberValue = new JLabel("-");
    private final JLabel birthdayValue = new JLabel("-");
    private final JLabel positionValue = new JLabel("-");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Emp #", "Name", "Birthday", "Position", "Basic Salary", "Hourly Rate"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public EmployeeInfoPanel(PayrollDashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        FieldFilters.mask(searchField, "#####");

        add(buildTopPanel(), BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> showSelectedDetails());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Employee Records"));
        add(tableScroll, BorderLayout.CENTER);

        add(buildButtonBar(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildTopPanel() {
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.add(new JLabel("Search Employee (number):"));

        JComboBox<String> sizer = new JComboBox<>();
        int h = sizer.getPreferredSize().height;
        Color line = new Color(170, 175, 180);

        searchField.setBorder(null);
        searchField.setOpaque(false);

        JButton searchButton = new JButton("\uD83D\uDD0D"); // magnifying glass
        searchButton.setFocusable(false);
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setToolTipText("Search");
        searchButton.setContentAreaFilled(false);
        searchButton.setFont(searchButton.getFont().deriveFont(14f));
        searchButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, line));
        searchButton.setPreferredSize(new Dimension(h + 4, h));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel fieldWrap = new JPanel(new BorderLayout());
        fieldWrap.setOpaque(false);
        fieldWrap.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        fieldWrap.add(searchField, BorderLayout.CENTER);

        JPanel searchBox = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(line);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        searchBox.setOpaque(false);
        searchBox.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
        searchBox.setPreferredSize(new Dimension(205, h + 4));
        searchBox.add(fieldWrap, BorderLayout.CENTER);
        searchBox.add(searchButton, BorderLayout.EAST);

        search.add(searchBox);

        searchButton.addActionListener(e -> searchEmployee());
        searchField.addActionListener(e -> searchEmployee());

        // Styled "card" for the selected employee's details.
        nameValue.setFont(nameValue.getFont().deriveFont(Font.BOLD, 18f));
        nameValue.setForeground(new Color(33, 37, 41));
        numberValue.setForeground(new Color(52, 58, 64));
        birthdayValue.setForeground(new Color(52, 58, 64));
        positionValue.setForeground(new Color(52, 58, 64));

        JLabel cardTitle = new JLabel("  Employee Information");
        cardTitle.setOpaque(true);
        cardTitle.setBackground(new Color(52, 152, 219));
        cardTitle.setForeground(Color.WHITE);
        cardTitle.setFont(cardTitle.getFont().deriveFont(Font.BOLD, 13f));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        int gy = 0;
        addDetail(grid, gbc, gy++, "\uD83D\uDC64  Employee Name:", nameValue);
        addDetail(grid, gbc, gy++, "\uD83C\uDD94  Employee Number:", numberValue);
        addDetail(grid, gbc, gy++, "\uD83C\uDF82  Birthday:", birthdayValue);
        addDetail(grid, gbc, gy++, "\uD83D\uDCBC  Position:", positionValue);

        JPanel details = new JPanel(new BorderLayout());
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createLineBorder(new Color(214, 219, 223)));
        details.add(cardTitle, BorderLayout.NORTH);
        details.add(grid, BorderLayout.CENTER);

        JPanel detailsWrap = new JPanel(new BorderLayout());
        detailsWrap.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
        detailsWrap.add(details, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.add(search, BorderLayout.NORTH);
        top.add(detailsWrap, BorderLayout.CENTER);
        return top;
    }

    /** One caption/value row inside the employee details card. */
    private void addDetail(JPanel panel, GridBagConstraints gbc, int row, String caption, JLabel value) {
        JLabel cap = new JLabel(caption);
        cap.setForeground(new Color(130, 134, 138));
        gbc.gridx = 0; gbc.gridy = row; gbc.insets = new Insets(5, 0, 5, 20);
        panel.add(cap, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(value, gbc);
    }

    private JPanel buildButtonBar() {
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        UiStyle.primaryButton(addButton);
        UiStyle.primaryButton(updateButton);
        UiStyle.primaryButton(deleteButton);
        UiStyle.sameWidth(addButton, updateButton, deleteButton);

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(addButton);
        bar.add(updateButton);
        bar.add(deleteButton);
        return bar;
    }

    // ===================== ACTIONS =====================

    private void searchEmployee() {
        String id = searchField.getText().trim();
        if (id.isEmpty()) {
            error("Please enter an employee number to search.");
            return;
        }
        List<Employee> employees = dashboard.getEmployees();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmpNo().equals(id)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
        }
        error("Employee number does not exist.");
    }

    private void addEmployee() {
        Employee draft = new Employee();
        EmployeeFormDialog dialog =
                new EmployeeFormDialog(dashboard, draft, true, dashboard.getEmployees());
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            dashboard.addRecord(draft);
            info("Employee added successfully.");
        }
    }

    private void updateEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            error("Please select an employee from the table to update.");
            return;
        }
        Employee e = dashboard.getEmployees().get(row);
        EmployeeFormDialog dialog =
                new EmployeeFormDialog(dashboard, e, false, dashboard.getEmployees());
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            dashboard.updateRecord(e);
            info("Employee updated successfully.");
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            error("Please select an employee from the table to delete.");
            return;
        }
        Employee e = dashboard.getEmployees().get(row);
        boolean confirmed = Dialogs.confirm(this,
                "Delete employee " + e.getEmpNo() + " - " + e.getFullName() + "?",
                "Confirm Delete");
        if (confirmed) {
            dashboard.deleteRecord(e);
            // The deleted employee should no longer linger in the details card.
            nameValue.setText("-");
            numberValue.setText("-");
            birthdayValue.setText("-");
            positionValue.setText("-");
            searchField.setText("");
            table.clearSelection();
            info("Employee deleted successfully.");
        }
    }

    private void showSelectedDetails() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Employee e = dashboard.getEmployees().get(row);
        nameValue.setText(e.getFullName());
        numberValue.setText(e.getEmpNo());
        birthdayValue.setText(e.getBirthday());
        positionValue.setText(e.getPosition());
        // Keep the search field in sync with the selected row.
        searchField.setText(e.getEmpNo());
    }

    // ===================== REFRESH =====================

    /** Rebuilds the table from the shared employee list. */
    public void refresh() {
        tableModel.setRowCount(0);
        List<Employee> employees = dashboard.getEmployees();
        if (employees == null) return;
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                    e.getEmpNo(),
                    e.getFullName(),
                    e.getBirthday(),
                    e.getPosition(),
                    php.format(e.getBasicSalary()),
                    php.format(e.getHourlyRate())
            });
        }
    }

    private void info(String message) {
        Dialogs.info(this, message);
    }

    private void error(String message) {
        Dialogs.error(this, message);
    }
}
