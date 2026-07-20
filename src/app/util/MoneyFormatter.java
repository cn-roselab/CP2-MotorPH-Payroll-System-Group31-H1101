package app.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Generic currency-formatting helper.
 *
 * Centralizes the Philippine peso formatting that the views previously each
 * created on their own, so money is displayed consistently everywhere.
 */
public final class MoneyFormatter {

    private static final NumberFormat PHP =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    private MoneyFormatter() {
    }

    /** Formats an amount as Philippine pesos (e.g. "PHP 1,125.00"). */
    public static String format(double amount) {
        return PHP.format(amount);
    }

    /**
     * Returns the shared peso {@link NumberFormat} instance, so views can format
     * money through one central definition instead of each creating their own.
     */
    public static NumberFormat currency() {
        return PHP;
    }
}
