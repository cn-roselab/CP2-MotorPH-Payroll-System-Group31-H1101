package app.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Small, dependency-free UI theme helpers for a modern rounded look.
 *
 * {@link #install()} registers rounded borders for text fields and combo boxes
 * app-wide (so every field is consistent with no per-field code), while the
 * button helpers round individual buttons on demand.
 */
public final class UiStyle {

    public static final Color PRIMARY = new Color(41, 128, 185);
    public static final Color SUCCESS = new Color(34, 153, 84);
    public static final Color DANGER = new Color(192, 57, 43);
    public static final Color SECONDARY = new Color(108, 117, 125);
    public static final Color LOGOUT = new Color(52, 73, 94);
    public static final Color LINE = new Color(170, 175, 180);
    private static final int ARC = 14;

    private UiStyle() {
    }

    /** Registers app-wide rounded borders for text fields. */
    public static void install() {
        Border fieldBorder = new RoundedBorder(ARC, LINE, new Insets(5, 9, 5, 9));
        UIManager.put("TextField.border", fieldBorder);
        UIManager.put("PasswordField.border", fieldBorder);
        UIManager.put("FormattedTextField.border", fieldBorder);
        UIManager.put("ComboBoxUI", RoundedComboBoxUI.class.getName());
        UIManager.put("ScrollBarUI", RoundedScrollBarUI.class.getName());
    }

    /** Gives a button rounded corners using its current background colour. */
    public static void roundButton(AbstractButton button) {
        button.setUI(new RoundedButtonUI());
    }

    /** Blue primary call-to-action button (filled; inverts to white on hover). */
    public static void primaryButton(AbstractButton button) {
        styleButton(button, PRIMARY);
    }

    /** Green success/confirm button (Save / OK); inverts on hover. */
    public static void successButton(AbstractButton button) {
        styleButton(button, SUCCESS);
    }

    /** Red danger button (Exit); inverts on hover. */
    public static void dangerButton(AbstractButton button) {
        styleButton(button, DANGER);
    }

    /** Gray secondary button (Close / Clear / Cancel); inverts on hover. */
    public static void secondaryButton(AbstractButton button) {
        styleButton(button, SECONDARY);
    }

    /** Button filled with a specific colour; inverts to white on hover. */
    public static void coloredButton(AbstractButton button, Color color) {
        styleButton(button, color);
    }

    /** Outline-first button: white/outlined by default, fills with colour on hover. */
    public static void outlineButton(AbstractButton button, Color color) {
        button.setBackground(Color.WHITE);
        button.setForeground(color);
        button.setUI(new RoundedButtonUI(color, true));
    }

    private static void styleButton(AbstractButton button, Color accent) {
        button.setBackground(accent);
        button.setForeground(Color.WHITE);
        button.setUI(new RoundedButtonUI(accent));
    }

    /**
     * Gives a group of buttons the same size (the largest preferred size), so
     * they look uniform even when their labels differ in length. Call this
     * after styling the buttons.
     */
    public static void sameWidth(AbstractButton... buttons) {
        int w = 0;
        int h = 0;
        for (AbstractButton b : buttons) {
            Dimension d = b.getPreferredSize();
            w = Math.max(w, d.width);
            h = Math.max(h, d.height);
        }
        for (AbstractButton b : buttons) {
            b.setPreferredSize(new Dimension(w, h));
        }
    }

    /** Standalone rounded border (e.g. for cards or the search box). */
    public static Border roundedLine(Color color, Insets padding) {
        return new RoundedBorder(ARC, color, padding);
    }

    /**
     * A rounded rectangle border. It masks the component's square corners with
     * the parent's background colour, so an opaque field/panel appears rounded.
     */
    public static class RoundedBorder implements Border {
        private final int arc;
        private final Color color;
        private final Insets insets;

        public RoundedBorder(int arc, Color color, Insets insets) {
            this.arc = arc;
            this.color = color;
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color mask = (c.getParent() != null) ? c.getParent().getBackground() : c.getBackground();
            RoundRectangle2D round = new RoundRectangle2D.Float(x, y, w - 1f, h - 1f, arc, arc);
            Area corners = new Area(new Rectangle(x, y, w, h));
            corners.subtract(new Area(round));

            g2.setColor(mask);
            g2.fill(corners);
            g2.setColor(color);
            g2.draw(round);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
