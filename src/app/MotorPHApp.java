package app;

import app.util.UiStyle;
import app.views.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point for the MotorPH Employee Application.
 * Launches the login window on the Swing event dispatch thread.
 */
public class MotorPHApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiStyle.install();
            new LoginFrame().setVisible(true);
        });
    }
}
