package app.views;

import app.util.Dialogs;
import app.util.UiStyle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Login window for the MotorPH Employee Application.
 *
 * Valid usernames: "employee" or "payroll_staff".
 * Password: "12345".
 * On success, routes to the matching portal. On failure, shows an
 * error message (the program stays on the login screen).
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginFrame() {
        setTitle("MotorPH Employee Application - Login");
        setSize(560, 440);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)));

        // Branded banner header (doubles as the window drag handle).
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(33, 97, 140));
        banner.setPreferredSize(new Dimension(0, 140));

        JLabel close = new JLabel("\u00D7");
        close.setForeground(new Color(214, 227, 236));
        close.setFont(new Font("Arial", Font.BOLD, 22));
        close.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 14));
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(close, BorderLayout.EAST);

        JPanel bannerText = new JPanel();
        bannerText.setOpaque(false);
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("MotorPH");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Arial", Font.BOLD, 36));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Employee Application");
        sub.setForeground(new Color(214, 227, 236));
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        bannerText.add(brand);
        bannerText.add(Box.createVerticalStrut(6));
        bannerText.add(sub);
        JPanel bannerCenter = new JPanel(new GridBagLayout());
        bannerCenter.setOpaque(false);
        bannerCenter.add(bannerText);

        banner.add(topRow, BorderLayout.NORTH);
        banner.add(bannerCenter, BorderLayout.CENTER);

        // Let the user drag the undecorated window by the banner.
        MouseAdapter drag = new MouseAdapter() {
            private Point start;

            @Override
            public void mousePressed(MouseEvent e) {
                start = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - start.x, loc.y + e.getY() - start.y);
            }
        };
        banner.addMouseListener(drag);
        banner.addMouseMotionListener(drag);
        bannerCenter.addMouseListener(drag);
        bannerCenter.addMouseMotionListener(drag);

        // Login card.
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel signIn = new JLabel("Sign in to continue");
        signIn.setFont(new Font("Arial", Font.PLAIN, 14));
        signIn.setForeground(new Color(130, 134, 138));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 8, 14, 8);
        card.add(signIn, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        usernameField.setPreferredSize(new Dimension(230, 32));
        passwordField.setPreferredSize(new Dimension(230, 32));

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        card.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        card.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(110, 34));
        UiStyle.primaryButton(loginButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(18, 8, 8, 8);
        card.add(loginButton, gbc);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(card);

        add(banner, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // Event-driven: login on button click or Enter key.
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        boolean validUser = username.equals("employee") || username.equals("payroll_staff");
        boolean validPass = password.equals("12345");

        if (!validUser || !validPass) {
            Dialogs.error(this, "Incorrect username and/or password.", "Login Failed");
            return;
        }

        // Route to the correct portal, then close the login window.
        if (username.equals("employee")) {
            new EmployeePortalFrame().setVisible(true);
        } else {
            new PayrollDashboardFrame().setVisible(true);
        }
        dispose();
    }
}
