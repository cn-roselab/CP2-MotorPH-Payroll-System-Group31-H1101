package app.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * A slim, modern scrollbar: no arrow buttons, a light track, and a rounded
 * thumb.
 */
public class RoundedScrollBarUI extends BasicScrollBarUI {

    private static final Color TRACK = new Color(238, 240, 242);
    private static final Color THUMB = new Color(184, 190, 196);

    public static ComponentUI createUI(JComponent c) {
        return new RoundedScrollBarUI();
    }

    @Override
    protected void configureScrollBarColors() {
        this.trackColor = TRACK;
        this.thumbColor = THUMB;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private JButton zeroButton() {
        JButton button = new JButton();
        Dimension zero = new Dimension(0, 0);
        button.setPreferredSize(zero);
        button.setMinimumSize(zero);
        button.setMaximumSize(zero);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(TRACK);
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(THUMB);
        int pad = 3;
        g2.fillRoundRect(r.x + pad, r.y + pad, r.width - 2 * pad, r.height - 2 * pad, 8, 8);
        g2.dispose();
    }
}
