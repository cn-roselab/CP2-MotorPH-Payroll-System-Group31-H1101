package app.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A flat, rounded button.
 *
 * By default the button is filled with its accent colour and white text; on
 * hover/press it "switches colours" to a white body with the accent as the
 * border and text (and back again). Disabled buttons render gray.
 */
public class RoundedButtonUI extends BasicButtonUI {

    private static final int ARC = 14;
    private static final Color DISABLED_FILL = new Color(228, 230, 233);
    private static final Color DISABLED_TEXT = new Color(168, 172, 176);

    private final Color accent;
    private final boolean outlineFirst;

    public RoundedButtonUI() {
        this(new Color(41, 128, 185), false);
    }

    public RoundedButtonUI(Color accent) {
        this(accent, false);
    }

    /**
     * @param outlineFirst when true the button starts white/outlined and fills
     *                     on hover (the opposite of the default filled look).
     */
    public RoundedButtonUI(Color accent, boolean outlineFirst) {
        this.accent = accent;
        this.outlineFirst = outlineFirst;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton b = (AbstractButton) c;
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel m = b.getModel();
        boolean active = b.isEnabled() && (m.isPressed() || m.isRollover());

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!b.isEnabled()) {
            g2.setColor(DISABLED_FILL);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ARC, ARC);
            g2.setColor(new Color(203, 206, 210));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
        } else if (filled(active)) {
            g2.setColor(m.isPressed() ? shade(accent, -28) : accent);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ARC, ARC);
        } else {
            g2.setColor(m.isPressed() ? new Color(236, 239, 242) : Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), ARC, ARC);
            g2.setColor(accent);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
        }
        g2.dispose();

        paint(g, c);
    }

    /** True when the body should be filled with the accent (vs. white/outline). */
    private boolean filled(boolean active) {
        return outlineFirst == active;
    }

    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(b.getFont());

        ButtonModel m = b.getModel();
        boolean active = b.isEnabled() && (m.isPressed() || m.isRollover());
        Color textColor;
        if (!b.isEnabled()) {
            textColor = DISABLED_TEXT;
        } else if (filled(active)) {
            textColor = Color.WHITE;
        } else {
            textColor = accent;
        }
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        int ty = (b.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textRect.x, ty);
        g2.dispose();
    }

    private static Color shade(Color c, int delta) {
        return new Color(
                Math.max(0, Math.min(255, c.getRed() + delta)),
                Math.max(0, Math.min(255, c.getGreen() + delta)),
                Math.max(0, Math.min(255, c.getBlue() + delta)));
    }
}
