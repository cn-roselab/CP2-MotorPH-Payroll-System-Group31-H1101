package app.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Centralized, consistently-styled message dialogs.
 *
 * These are custom undecorated modal dialogs (coloured header with an icon,
 * rounded buttons) so every pop-up matches the app's look instead of the plain
 * default {@link javax.swing.JOptionPane} windows.
 */
public final class Dialogs {

    private static final Color HEADER_INFO = new Color(33, 97, 140);
    private static final Color HEADER_SUCCESS = new Color(30, 132, 73);
    private static final Color HEADER_ERROR = new Color(192, 57, 43);
    private static final Color HEADER_WARNING = new Color(241, 196, 15);

    private Dialogs() {
    }

    /** Success / informational message (green header). */
    public static void info(Component parent, String message) {
        show(parent, "Success", textBody(message), HEADER_SUCCESS,
                UIManager.getIcon("OptionPane.informationIcon"), false, "OK");
    }

    /** Error message with the default "Error" title. */
    public static void error(Component parent, String message) {
        error(parent, message, "Error");
    }

    /** Error message with a specific title (red header). */
    public static void error(Component parent, String message, String title) {
        show(parent, title, textBody(message), HEADER_ERROR,
                UIManager.getIcon("OptionPane.errorIcon"), false, "OK");
    }

    /** Yes/No confirmation; returns true only when the user chooses Yes. */
    public static boolean confirm(Component parent, String message, String title) {
        return show(parent, title, textBody(message), HEADER_WARNING,
                UIManager.getIcon("OptionPane.warningIcon"), false, "Yes", "No") == 0;
    }

    /** Shows a rich component (e.g. a styled report card) with no extra header. */
    public static void card(Component parent, String title, JComponent content) {
        show(parent, title, content, null, null, true, "Close");
    }

    private static JLabel textBody(String message) {
        String safe = message == null ? "" : message
                .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
        return new JLabel("<html><div style='width:280px; font-family:sans-serif;"
                + " font-size:12px; color:#343A40;'>" + safe + "</div></html>");
    }

    /**
     * Builds and shows the modal dialog. Returns the index of the button the
     * user clicked (0 = first / default), or -1 if closed otherwise.
     */
    private static int show(Component parent, String title, JComponent body, Color header,
                            Icon icon, boolean secondaryButtons, String... buttons) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)));

        if (header != null) {
            JLabel head = new JLabel(title == null ? "" : title.toUpperCase(), icon, SwingConstants.LEFT);
            head.setOpaque(true);
            head.setBackground(header);
            head.setForeground(Color.WHITE);
            head.setIconTextGap(12);
            head.setVerticalAlignment(SwingConstants.CENTER);
            head.setVerticalTextPosition(SwingConstants.CENTER);
            head.setFont(head.getFont().deriveFont(Font.BOLD, 18f));
            head.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 20));
            root.add(head, BorderLayout.NORTH);
        }

        JPanel bodyWrap = new JPanel(new BorderLayout());
        bodyWrap.setBackground(Color.WHITE);
        bodyWrap.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));
        bodyWrap.add(body, BorderLayout.CENTER);
        root.add(bodyWrap, BorderLayout.CENTER);

        final int[] result = {-1};
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 16));
        JButton[] made = new JButton[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final int idx = i;
            JButton b = new JButton(buttons[i]);
            if (secondaryButtons) {
                UiStyle.secondaryButton(b);
            } else if (i == 0) {
                UiStyle.successButton(b);
            } else {
                UiStyle.secondaryButton(b);
            }
            b.addActionListener(e -> {
                result[0] = idx;
                dialog.dispose();
            });
            footer.add(b);
            made[i] = b;
            if (i == 0) {
                dialog.getRootPane().setDefaultButton(b);
            }
        }
        UiStyle.sameWidth(made);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        return result[0];
    }
}
