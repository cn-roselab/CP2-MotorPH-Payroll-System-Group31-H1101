package app.views;

import app.services.CsvManager;
import app.models.Employee;
import app.util.Dialogs;
import app.util.FieldFilters;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Employee portal.
 *
 * Per the specification, the employee enters their employee number and,
 * if it exists, the app displays Employee Number, Employee Name, and
 * Birthday. Otherwise it shows "Employee number does not exist."
 */
public class EmployeePortalFrame extends JFrame {

    private final JTextField empNoField = new JTextField(15);
    private final JLabel numberValue = new JLabel("-");
    private final JLabel nameValue = new JLabel("-");
    private final JLabel birthdayValue = new JLabel("-");

    private List<Employee> employees;

    public EmployeePortalFrame() {
        setTitle("MotorPH - Employee Portal");
        setSize(640, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        loadData();

        FieldFilters.mask(empNoField, "#####");
        FieldFilters.placeholder(empNoField, "(ex. 10001)");

        // Branded banner header.
        JPanel banner = new JPanel(new GridBagLayout());
        banner.setBackground(new Color(33, 97, 140));
        banner.setPreferredSize(new Dimension(0, 110));
        JPanel bannerText = new JPanel();
        bannerText.setOpaque(false);
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("MotorPH");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Arial", Font.BOLD, 30));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Employee Portal");
        sub.setForeground(new Color(214, 227, 236));
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        bannerText.add(brand);
        bannerText.add(Box.createVerticalStrut(4));
        bannerText.add(sub);
        banner.add(bannerText);

        // Search row.
        JButton viewButton = new JButton("View Details");
        empNoField.setPreferredSize(new Dimension(200, 34));
        UiStyle.primaryButton(viewButton);
        viewButton.setPreferredSize(new Dimension(130, 34));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        searchRow.add(new JLabel("Enter Employee Number:"));
        searchRow.add(empNoField);
        searchRow.add(viewButton);

        // Profile card.
        nameValue.setFont(new Font("Arial", Font.BOLD, 20));
        nameValue.setForeground(new Color(33, 37, 41));
        numberValue.setForeground(new Color(52, 58, 64));
        birthdayValue.setForeground(new Color(52, 58, 64));

        JLabel cardTitle = new JLabel("  Employee Details");
        cardTitle.setOpaque(true);
        cardTitle.setBackground(new Color(52, 152, 219));
        cardTitle.setForeground(Color.WHITE);
        cardTitle.setFont(new Font("Arial", Font.BOLD, 13));
        cardTitle.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 22, 18, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        int gy = 0;
        addDetail(grid, gbc, gy++, "\uD83D\uDC64  Employee Name:", nameValue);
        addDetail(grid, gbc, gy++, "\uD83C\uDD94  Employee Number:", numberValue);
        addDetail(grid, gbc, gy++, "\uD83C\uDF82  Birthday:", birthdayValue);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(214, 219, 223)));
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);

        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setBorder(BorderFactory.createEmptyBorder(18, 44, 12, 44));
        cardWrap.add(card, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(18, 10, 8, 10));
        center.add(searchRow, BorderLayout.NORTH);
        center.add(cardWrap, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");
        UiStyle.outlineButton(logoutButton, UiStyle.PRIMARY);
        UiStyle.outlineButton(exitButton, UiStyle.DANGER);
        UiStyle.sameWidth(logoutButton, exitButton);
        JPanel buttonBar = new JPanel();
        buttonBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 219, 223)),
                BorderFactory.createEmptyBorder(12, 0, 14, 0)));
        buttonBar.add(logoutButton);
        buttonBar.add(exitButton);

        add(banner, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buttonBar, BorderLayout.SOUTH);

        viewButton.addActionListener(e -> viewDetails());
        empNoField.addActionListener(e -> viewDetails());
        logoutButton.addActionListener(e -> logout());
        exitButton.addActionListener(e -> System.exit(0));
    }

    /** One caption/value row inside the employee details card. */
    private void addDetail(JPanel panel, GridBagConstraints gbc, int row, String caption, JLabel value) {
        JLabel cap = new JLabel(caption);
        cap.setForeground(new Color(130, 134, 138));
        gbc.gridx = 0; gbc.gridy = row; gbc.insets = new Insets(6, 0, 6, 22);
        panel.add(cap, gbc);
        gbc.gridx = 1; gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(value, gbc);
    }

    /** Returns to the login screen. */
    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void loadData() {
        try {
            employees = CsvManager.loadEmployees();
        } catch (IOException ex) {
            Dialogs.error(this,
                    "Could not read " + CsvManager.EMPLOYEE_FILE + ".\n" + ex.getMessage(),
                    "File Error");
        }
    }

    private void viewDetails() {
        String id = empNoField.getText().trim();

        if (id.isEmpty()) {
            Dialogs.error(this, "Please enter your employee number.", "Input Error");
            return;
        }

        Employee found = null;
        if (employees != null) {
            for (Employee e : employees) {
                if (e.getEmpNo().equals(id)) {
                    found = e;
                    break;
                }
            }
        }

        if (found == null) {
            Dialogs.error(this, "Employee number does not exist.", "Not Found");
            numberValue.setText("-");
            nameValue.setText("-");
            birthdayValue.setText("-");
            return;
        }

        numberValue.setText(found.getEmpNo());
        nameValue.setText(found.getFullName());
        birthdayValue.setText(found.getBirthday());
    }
}
