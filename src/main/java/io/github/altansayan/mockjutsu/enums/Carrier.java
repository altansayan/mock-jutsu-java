package io.github.altansayan.mockjutsu.enums;

/**
 * Shipping carrier selector for {@code tracking_number} type generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.Carrier.*;
 *
 * String usps  = MockJutsu.generate(TRACKING_NUMBER, US, USPS);
 * String ups   = MockJutsu.generate(TRACKING_NUMBER, US, UPS);
 * String fedex = MockJutsu.generate(TRACKING_NUMBER, US, FEDEX);
 *
 * // Fluent builder
 * String track = MockJutsu.trackingNumber().carrier(UPS).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum Carrier {

    /**
     * United States Postal Service — 22-digit IMpb format with Luhn check digit.
     * Conforms to USPS Publication 97, Appendix F.
     */
    USPS("usps"),

    /**
     * United Parcel Service — 18-character format starting with {@code "1Z"}.
     */
    UPS("ups"),

    /**
     * FedEx — 12-digit or 15-digit tracking number.
     */
    FEDEX("fedex");

    private final String key;

    Carrier(String key) { this.key = key; }

    /**
     * Returns the carrier key forwarded to the generator (e.g. {@code "usps"}, {@code "fedex"}).
     *
     * @return the lowercase carrier key
     */
    public String key() { return key; }

    /**
     * Parses a carrier name string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #USPS} for {@code null} or unrecognised values.
     *
     * @param carrier a carrier name (e.g. {@code "usps"}, {@code "UPS"}, {@code "fedex"})
     * @return the matching constant, or {@link #USPS} as the default
     */
    public static Carrier of(String carrier) {
        if (carrier == null || carrier.isBlank()) return USPS;
        for (Carrier c : values()) {
            if (c.key.equalsIgnoreCase(carrier.trim())) return c;
        }
        return USPS;
    }
}
