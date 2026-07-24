package io.github.altansayan.mockjutsu.enums;

/**
 * Gender selector for name and identity generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.Gender.*;
 *
 * String maleName   = MockJutsu.fullname().locale("TR").gender(MALE).generate();
 * String femaleName = MockJutsu.fullname().locale("TR").gender(FEMALE).generate();
 * String anyName    = MockJutsu.fullname().locale("TR").gender(RANDOM).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum Gender {

    /** Male — generators use male name pools and male-inflected forms. */
    MALE("M"),

    /** Female — generators use female name pools and female-inflected forms. */
    FEMALE("F"),

    /** No preference — generator picks randomly. */
    RANDOM("");

    private final String key;

    Gender(String key) { this.key = key; }

    /**
     * Returns the qualifier key forwarded to generators ({@code "M"}, {@code "F"}, or empty).
     *
     * @return the single-character key, or empty string for {@link #RANDOM}
     */
    public String key() { return key; }

    /**
     * Parses a gender string to the corresponding enum constant, case-insensitive.
     * Accepts {@code "M"}, {@code "male"}, {@code "erkek"} for male;
     * {@code "F"}, {@code "female"}, {@code "kadın"} for female.
     * Falls back to {@link #RANDOM} for {@code null}, blank, or unrecognised values.
     *
     * @param gender a gender string
     * @return the matching enum constant, or {@link #RANDOM} as the default
     */
    public static Gender of(String gender) {
        if (gender == null || gender.isBlank()) return RANDOM;
        return switch (gender.trim().toLowerCase()) {
            case "m", "male", "erkek"          -> MALE;
            case "f", "female", "kadin", "kadın" -> FEMALE;
            default                              -> RANDOM;
        };
    }
}
