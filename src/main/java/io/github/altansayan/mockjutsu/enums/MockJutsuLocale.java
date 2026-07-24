package io.github.altansayan.mockjutsu.enums;

/**
 * Supported locale codes for mock data generation.
 *
 * <p>Each constant maps to an ISO 3166-1 alpha-2 country code. Generators use the
 * locale to produce culturally appropriate values (names, addresses, ID formats, etc.).
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 *
 * MockJutsu.generate("tckn", TR);
 * MockJutsu.iban().country(DE).generate();
 * MockJutsu.fullname().locale(JP).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum MockJutsuLocale {

    /** Turkey */           TR,
    /** United States */    US,
    /** Germany */          DE,
    /** United Kingdom */   GB,
    /** France */           FR,
    /** Spain */            ES,
    /** Italy */            IT,
    /** Netherlands */      NL,
    /** Portugal */         PT,
    /** Poland */           PL,
    /** Russia */           RU,
    /** Ukraine */          UA,
    /** India */            IN,
    /** China */            CN,
    /** Japan */            JP,
    /** South Korea */      KR,
    /** Brazil */           BR,
    /** Australia */        AU,
    /** Canada */           CA,
    /** Mexico */           MX,
    /** Sweden */           SE,
    /** Denmark */          DK,
    /** Norway */           NO,
    /** Finland */          FI,
    /** Argentina */        AR,
    /** Chile */            CL,
    /** Colombia */         CO,
    /** South Africa */     ZA,
    /** Singapore */        SG,
    /** Malaysia */         MY,
    /** Thailand */         TH,
    /** Egypt */            EG,
    /** Israel */           IL,
    /** Romania */          RO,
    /** Croatia */          HR,
    /** Bulgaria */         BG,
    /** Lithuania */        LT,
    /** Estonia */          EE,
    /** New Zealand */      NZ,
    /** Pakistan */         PK;

    /**
     * Returns the locale code string as used by generators (e.g. {@code "TR"}, {@code "US"}).
     *
     * @return the two-letter locale code
     */
    public String code() {
        return this.name();
    }

    /**
     * Parses a locale string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #US} if the locale is {@code null}, blank, or unrecognised.
     *
     * @param locale a locale string such as {@code "TR"}, {@code "de"}, {@code "GB"}
     * @return the matching enum constant, or {@link #US} as the default
     */
    public static MockJutsuLocale of(String locale) {
        if (locale == null || locale.isBlank()) return US;
        try {
            return valueOf(locale.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return US;
        }
    }
}
