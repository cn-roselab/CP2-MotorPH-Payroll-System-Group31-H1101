package app.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * A combo box that matches the rounded text-field look: a white rounded body
 * with a thin border and a small caret (triangle) on the right instead of the
 * default chunky arrow button.
 */
public class RoundedComboBoxUI extends BasicComboBoxUI {

    private static final int ARC = 14;
    private static final Color LINE = new Color(170, 175, 180);

    public static ComponentUI createUI(JComponent c) {
        return new RoundedComboBoxUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        comboBox.setOpaque(true);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(new UiStyle.RoundedBorder(ARC, LINE, new Insets(6, 9, 6, 9)));
        comboBox.setRenderer(new StyledRenderer());
    }

    /**
     * Don't paint any rectangle behind the selected value; the combo's own
     * white background shows through, so there's no gray box inside the field.
     * (Only the background is skipped, not the value text, so the selection
     * still displays correctly.)
     */
    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Intentionally empty.
    }

    @Override
    protected JButton createArrowButton() {
        JButton caret = new JButton() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(110, 115, 120));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int[] xs = {cx - 4, cx + 4, cx};
                int[] ys = {cy - 2, cy - 2, cy + 3};
                g2.fillPolygon(xs, ys, 3);
                g2.dispose();
            }
        };
        caret.setContentAreaFilled(false);
        caret.setBorderPainted(false);
        caret.setFocusable(false);
        caret.setOpaque(false);
        caret.setPreferredSize(new Dimension(22, 22));
        return caret;
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            @Override
            protected void configurePopup() {
                super.configurePopup();
                setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)));
            }

            @Override
            protected void configureList() {
                super.configureList();
                list.setBackground(Color.WHITE);
                list.setSelectionBackground(new Color(41, 128, 185));
                list.setSelectionForeground(Color.WHITE);
                list.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            }
        };
        return popup;
    }

    /**
     * Renders the selected value transparently (no inner rectangle) and gives
     * the pop-up list items padding and a themed selection colour.
     */
    private static class StyledRenderer extends DefaultListCellRenderer {
        private static final Color SELECTED = new Color(41, 128, 185);
        private static final Color TEXT = new Color(52, 58, 64);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            boolean selected = index != -1 && isSelected;
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, selected, cellHasFocus);
            if (index == -1) {
                label.setBackground(Color.WHITE);
                label.setForeground(TEXT);
                label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
            } else {
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setBackground(selected ? SELECTED : Color.WHITE);
                label.setForeground(selected ? Color.WHITE : TEXT);
            }
            return label;
        }
    }
}
