package app.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Reusable input filters that block invalid characters as the user types.
 *
 * Each installer attaches a {@link DocumentFilter} that only permits an edit
 * when the resulting field text still matches an allowed pattern, so letters
 * (and other disallowed characters) simply never appear in number-only fields.
 */
public final class FieldFilters {

    private FieldFilters() {
    }

    /** Digits only (e.g. employee number, PhilHealth #, Pag-IBIG #). */
    public static void digitsOnly(JTextField field) {
        apply(field, "[0-9]*");
    }

    /** Letters plus the punctuation used in names (space, period, apostrophe, hyphen). */
    public static void nameChars(JTextField field) {
        apply(field, "[A-Za-z .'\\-]*");
    }

    /** Letters, spaces, and job-title punctuation (& - / . '). */
    public static void positionChars(JTextField field) {
        apply(field, "[A-Za-z .&/'\\-]*");
    }

    /** Supervisor names: letters, spaces, and , . ' - / (for "Last, First" and "N/A"). */
    public static void supervisorChars(JTextField field) {
        apply(field, "[A-Za-z .,'/\\-]*");
    }

    /** Digits and dashes only (e.g. phone, SSS, TIN). */
    public static void digitsAndDashes(JTextField field) {
        apply(field, "[0-9-]*");
    }

    /** Non-negative decimal amount: digits with an optional single decimal point. */
    public static void decimal(JTextField field) {
        apply(field, "[0-9]*\\.?[0-9]*");
    }

    private static void apply(JTextField field, String regex) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new RegexFilter(regex));
    }

    /**
     * Auto-formats the field to a fixed digit pattern as the user types.
     *
     * <p>The {@code pattern} uses {@code '#'} for each digit slot and any other
     * character as a literal separator (e.g. {@code "##/##/####"} for a date or
     * {@code "###-###-###"} for a phone number). The user only types digits;
     * separators are inserted automatically, so after finishing a group the
     * next keystroke lands past the separator.</p>
     */
    public static void mask(JTextField field, String pattern) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new MaskFilter(pattern));
    }

    /**
     * Shows a light-gray hint inside the field while it is empty and unfocused,
     * without ever putting the hint text into the document.
     */
    public static void placeholder(JTextField field, String hint) {
        field.putClientProperty("placeholder", hint);
        field.setBorder(withPlaceholder(field.getBorder()));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });
    }

    /**
     * Wraps a border so it also paints the field's placeholder hint. Fields
     * without a {@code "placeholder"} client property are left visually
     * unchanged, so this is safe to use when resetting or flagging borders.
     */
    public static Border withPlaceholder(Border base) {
        return new PlaceholderBorder(base);
    }

    /** Allows an edit only if the resulting text matches the regex. */
    private static class RegexFilter extends DocumentFilter {
        private final String regex;

        RegexFilter(String regex) {
            this.regex = regex;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String result = current.substring(0, offset) + text + current.substring(offset);
            if (result.matches(regex)) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String result = current.substring(0, offset) + text + current.substring(offset + length);
            if (result.matches(regex)) {
                super.replace(fb, offset, length, text, attr);
            }
        }
    }

    /**
     * Rebuilds the field on every edit so it always matches a digit pattern,
     * auto-inserting the literal separators from the pattern.
     */
    private static class MaskFilter extends DocumentFilter {
        private final String pattern;
        private final int maxDigits;

        MaskFilter(String pattern) {
            this.pattern = pattern;
            int slots = 0;
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == '#') {
                    slots++;
                }
            }
            this.maxDigits = slots;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, text, attr);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String removed = current.substring(offset, offset + length);
            int start = offset;
            // Deleting only separators would be undone by the reformat, so also
            // drop the digit just before it (so backspace after "11/" gives "1").
            if (!containsDigit(removed) && start > 0) {
                start = offset - 1;
            }
            String kept = current.substring(0, start) + current.substring(offset + length);
            reformat(fb, kept);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String combined = current.substring(0, offset)
                    + (text == null ? "" : text)
                    + current.substring(offset + length);
            reformat(fb, combined);
        }

        private void reformat(FilterBypass fb, String raw) throws BadLocationException {
            StringBuilder digits = new StringBuilder();
            for (int i = 0; i < raw.length() && digits.length() < maxDigits; i++) {
                char c = raw.charAt(i);
                if (Character.isDigit(c)) {
                    digits.append(c);
                }
            }
            fb.replace(0, fb.getDocument().getLength(), format(digits.toString()), null);
        }

        private String format(String digits) {
            StringBuilder out = new StringBuilder();
            int di = 0;
            for (int i = 0; i < pattern.length(); i++) {
                char p = pattern.charAt(i);
                if (p == '#') {
                    if (di < digits.length()) {
                        out.append(digits.charAt(di++));
                    } else {
                        break;
                    }
                } else {
                    // Trailing separator after a completed group, so the next
                    // digit the user types lands after it automatically.
                    out.append(p);
                }
            }
            return out.toString();
        }

        private static boolean containsDigit(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (Character.isDigit(s.charAt(i))) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Paints a gray hint over an empty, unfocused text field. */
    private static class PlaceholderBorder implements Border {
        private final Border delegate;

        PlaceholderBorder(Border delegate) {
            this.delegate = delegate;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (delegate != null) {
                delegate.paintBorder(c, g, x, y, w, h);
            }
            JTextField field = (JTextField) c;
            Object hint = field.getClientProperty("placeholder");
            if (hint != null && field.getText().isEmpty() && !field.isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(175, 178, 182));
                g2.setFont(field.getFont().deriveFont(
                        Font.ITALIC, field.getFont().getSize2D() - 1.5f));
                Insets in = getBorderInsets(c);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + in.left + 1;
                int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint.toString(), tx, ty);
                g2.dispose();
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return delegate != null ? delegate.getBorderInsets(c) : new Insets(2, 2, 2, 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return delegate != null && delegate.isBorderOpaque();
        }
    }
}
