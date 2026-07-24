package io.github.altansayan.mockjutsu.enums;

/**
 * Bank account type qualifier for {@link DataType#ACCOUNT_TYPE} generation.
 *
 * <p><b>Available account types:</b>
 * <ul>
 *   <li>{@link #CHECKING}          — Standard checking account</li>
 *   <li>{@link #SAVINGS}           — Savings account</li>
 *   <li>{@link #CURRENT}           — Current account (UK/EU)</li>
 *   <li>{@link #BUSINESS_CHECKING} — Business checking account</li>
 *   <li>{@link #MONEY_MARKET}      — Money market account</li>
 *   <li>{@link #CD}                — Certificate of Deposit</li>
 *   <li>{@link #INVESTMENT}        — Investment account</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.AccountType.*;
 *
 * String t = MockJutsu.accountType().locale(CA).type(CHECKING).generate();
 * String t = MockJutsu.generate(ACCOUNT_TYPE, CA, CHECKING);
 * }</pre>
 *
 * @since 1.0.0
 */
public enum AccountType {

    /** Standard checking account. */
    CHECKING("Checking"),

    /** Savings account. */
    SAVINGS("Savings"),

    /** Current account (common in UK/EU). */
    CURRENT("Current"),

    /** Business checking account. */
    BUSINESS_CHECKING("Business Checking"),

    /** Money market account. */
    MONEY_MARKET("Money Market"),

    /** Certificate of Deposit. */
    CD("CD"),

    /** Investment account. */
    INVESTMENT("Investment");

    private final String value;

    AccountType(String value) { this.value = value; }

    /** Returns the exact string value generated (e.g. {@code "Checking"}). */
    public String value() { return value; }

    /**
     * Parses a string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #CHECKING} if unrecognised.
     */
    public static AccountType of(String s) {
        if (s == null || s.isBlank()) return CHECKING;
        for (AccountType t : values()) {
            if (t.value.equalsIgnoreCase(s.trim()) || t.name().equalsIgnoreCase(s.trim())) return t;
        }
        return CHECKING;
    }
}
