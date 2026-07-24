package io.github.altansayan.mockjutsu.enums;

/**
 * Color format selector for {@code color} type generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.ColorFormat.*;
 *
 * String hex  = MockJutsu.generate(COLOR, US, HEX);   // "#A3C4F7"
 * String rgb  = MockJutsu.generate(COLOR, US, RGB);   // "rgb(163, 196, 247)"
 * String hsl  = MockJutsu.generate(COLOR, US, HSL);   // "hsl(219, 85%, 80%)"
 * String name = MockJutsu.generate(COLOR, US, NAME);  // "coral"
 *
 * // Fluent builder
 * String color = MockJutsu.color().format(HSL).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum ColorFormat {

    /** Hexadecimal CSS color — {@code #RRGGBB} format. Default. */
    HEX("hex"),

    /** RGB CSS color — {@code rgb(R, G, B)} format. */
    RGB("rgb"),

    /** HSL CSS color — {@code hsl(H, S%, L%)} format. */
    HSL("hsl"),

    /** CSS color name — one of the 140 standard named colors. */
    NAME("name");

    private final String key;

    ColorFormat(String key) { this.key = key; }

    /**
     * Returns the format key forwarded to the generator (e.g. {@code "hex"}, {@code "rgb"}).
     *
     * @return the lowercase format key
     */
    public String key() { return key; }

    /**
     * Parses a format name string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #HEX} for {@code null} or unrecognised values.
     *
     * @param format a format name (e.g. {@code "hex"}, {@code "RGB"}, {@code "name"})
     * @return the matching constant, or {@link #HEX} as the default
     */
    public static ColorFormat of(String format) {
        if (format == null || format.isBlank()) return HEX;
        for (ColorFormat f : values()) {
            if (f.key.equalsIgnoreCase(format.trim())) return f;
        }
        return HEX;
    }
}
