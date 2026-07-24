package io.github.altansayan.mockjutsu.enums;

/**
 * Payment card network identifiers for card number generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.Network.*;
 *
 * String visa = MockJutsu.cardnum().locale("TR").network(VISA).generate();
 * String amex = MockJutsu.cardnum().locale("US").network(AMEX).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum Network {

    /** Visa — 16-digit, starts with 4. */
    VISA("visa"),

    /** Mastercard — 16-digit, starts with 5. */
    MC("mc"),

    /** American Express — 15-digit, starts with 3. */
    AMEX("amex"),

    /** Troy (Turkey) — 16-digit, starts with 9792. */
    TROY("troy"),

    /** Mir (Russia) — 16-digit, starts with 2. */
    MIR("mir"),

    /** JCB (Japan) — 16-digit, starts with 35. */
    JCB("jcb"),

    /** Discover — 16-digit, starts with 6011 or 65. */
    DISCOVER("discover"),

    /** UnionPay (China) — 16-digit, starts with 62. */
    UNIONPAY("unionpay"),

    /** Maestro (debit) — variable length, starts with 6. */
    MAESTRO("maestro");

    private final String key;

    Network(String key) { this.key = key; }

    /**
     * Returns the string key used internally by generators (e.g. {@code "visa"}, {@code "mc"}).
     *
     * @return the lowercase network key
     */
    public String key() { return key; }

    /**
     * Parses a network string to the corresponding enum constant, case-insensitive.
     * Accepts both the enum name ({@code "VISA"}) and the key ({@code "visa"}).
     * Falls back to {@link #VISA} if the value is {@code null}, blank, or unrecognised.
     *
     * @param network a network string such as {@code "visa"}, {@code "MC"}, {@code "amex"}
     * @return the matching enum constant, or {@link #VISA} as the default
     */
    public static Network of(String network) {
        if (network == null || network.isBlank()) return VISA;
        String lower = network.trim().toLowerCase();
        for (Network n : values()) {
            if (n.key.equals(lower) || n.name().equalsIgnoreCase(lower)) return n;
        }
        return VISA;
    }
}
