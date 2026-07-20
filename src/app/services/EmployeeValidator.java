package app.services;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Employee field validation rules for MotorPH.
 *
 * These are the domain-specific rules (formats derived from the CSV data) kept
 * out of the view so the form only handles presentation while the rules live in
 * one reusable, testable place.
 */
public final class EmployeeValidator {

    // Formats taken from the MotorPH CSV data.
    public static final String EMP_NO_PATTERN = "\\d{5}";
    public static final String PHONE_PATTERN = "\\d{3}-\\d{3}-\\d{3}";
    public static final String SSS_PATTERN = "\\d{2}-\\d{7}-\\d{1}";
    public static final String PHILHEALTH_PATTERN = "\\d{12}";
    public static final String TIN_PATTERN = "\\d{3}-\\d{3}-\\d{3}-\\d{3}";
    public static final String PAGIBIG_PATTERN = "\\d{12}";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    private EmployeeValidator() {
    }

    /** Employee number must be exactly 5 digits. */
    public static boolean isValidEmployeeNumber(String value) {
        return value != null && value.matches(EMP_NO_PATTERN);
    }

    /** Names may contain only letters, spaces, and . ' - and need a letter. */
    public static boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-z .'-]+") && name.matches(".*[A-Za-z].*");
    }

    /** Birthday must be a real M/d/yyyy date that is not in the future. */
    public static boolean isValidBirthday(String raw) {
        try {
            LocalDate date = LocalDate.parse(raw, DATE_FORMAT);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeException ex) {
            return false;
        }
    }

    /**
     * Validates an optional value against a pattern. Blank passes (optional);
     * a non-blank value must match the pattern exactly.
     */
    public static boolean isValidOptionalPattern(String value, String pattern) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return value.trim().matches(pattern);
    }

    /** A non-negative amount (blank is treated as valid/zero by the caller). */
    public static boolean isNonNegativeAmount(String value) {
        try {
            return Double.parseDouble(value.trim()) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
