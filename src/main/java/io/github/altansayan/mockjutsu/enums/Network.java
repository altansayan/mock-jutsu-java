package io.github.altansayan.mockjutsu.enums;

/**
 * Payment card network identifiers for {@code cardnum} type generation.
 *
 * <p><b>All 9 supported networks:</b>
 * <ul>
 *   <li>{@link #VISA}     — 16-digit, starts with {@code 4}</li>
 *   <li>{@link #MC}       — 16-digit, starts with {@code 51-55} or {@code 2221-2720}</li>
 *   <li>{@link #AMEX}     — 15-digit, starts with {@code 34} or {@code 37}</li>
 *   <li>{@link #TROY}     — 16-digit, starts with {@code 9792} (Turkey)</li>
 *   <li>{@link #MIR}      — 16-digit, starts with {@code 2200-2204} (Russia)</li>
 *   <li>{@link #JCB}      — 16-digit, starts with {@code 3528-3589} (Japan)</li>
 *   <li>{@link #DISCOVER} — 16-digit, starts with {@code 6011} or {@code 65}</li>
 *   <li>{@link #UNIONPAY} — 16-digit, starts with {@code 62} (China)</li>
 *   <li>{@link #MAESTRO}  — 13-19 digit, starts with {@code 6304}</li>
 * </ul>
 *
 * <p><b>Tip (VS Code):</b> Press {@code Ctrl+Space} at the network parameter position
 * to see all values via autocomplete, or press {@code F12} on {@code VISA} / {@code MC}
 * to jump to this enum and browse all options.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.Network.*;
 *
 * String visa = MockJutsu.generate(CARDNUM, TR, VISA);   // 4...
 * String mc   = MockJutsu.generate(CARDNUM, DE, MC);     // 5...
 * String amex = MockJutsu.generate(CARDNUM, US, AMEX);   // 3... (15-digit)
 * String troy = MockJutsu.generate(CARDNUM, TR, TROY);   // 9792...
 *
 * // Builder API
 * String card = MockJutsu.cardnum().locale(TR).network(VISA).generate();
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
