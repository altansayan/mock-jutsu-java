package io.github.altansayan.mockjutsu.enums;

/**
 * Supported locale codes for mock data generation.
 *
 * <p>Each constant maps to an ISO 3166-1 alpha-2 country code. Generators use the
 * locale to produce culturally appropriate values (names, addresses, ID formats, etc.).
 *
 * <p><b>All supported locales (38 total):</b>
 * <table border="1" cellpadding="4">
 *   <tr><th>Constant</th><th>Country</th><th>Constant</th><th>Country</th></tr>
 *   <tr><td>{@link #TR}</td><td>Turkey 🇹🇷</td>      <td>{@link #US}</td><td>United States 🇺🇸</td></tr>
 *   <tr><td>{@link #DE}</td><td>Germany 🇩🇪</td>      <td>{@link #GB}</td><td>United Kingdom 🇬🇧</td></tr>
 *   <tr><td>{@link #FR}</td><td>France 🇫🇷</td>       <td>{@link #ES}</td><td>Spain 🇪🇸</td></tr>
 *   <tr><td>{@link #IT}</td><td>Italy 🇮🇹</td>        <td>{@link #NL}</td><td>Netherlands 🇳🇱</td></tr>
 *   <tr><td>{@link #RU}</td><td>Russia 🇷🇺</td>       <td>{@link #IN}</td><td>India 🇮🇳</td></tr>
 *   <tr><td>{@link #CN}</td><td>China 🇨🇳</td>        <td>{@link #JP}</td><td>Japan 🇯🇵</td></tr>
 *   <tr><td>{@link #KR}</td><td>South Korea 🇰🇷</td>  <td>{@link #BR}</td><td>Brazil 🇧🇷</td></tr>
 *   <tr><td>{@link #AU}</td><td>Australia 🇦🇺</td>    <td>{@link #CA}</td><td>Canada 🇨🇦</td></tr>
 *   <tr><td>{@link #MX}</td><td>Mexico 🇲🇽</td>       <td>{@link #SE}</td><td>Sweden 🇸🇪</td></tr>
 *   <tr><td>{@link #PL}</td><td>Poland 🇵🇱</td>       <td>{@link #ZA}</td><td>South Africa 🇿🇦</td></tr>
 *   <tr><td colspan="4">… and more: DK, NO, FI, AR, CL, CO, SG, MY, TH, EG, IL, RO, HR, BG, LT, EE, NZ, PK, PT, UA</td></tr>
 * </table>
 *
 * <p><b>Tip (VS Code):</b> Press {@code Ctrl+Space} at the locale parameter position
 * to see all values via autocomplete, or press {@code F12} on any locale constant
 * to jump to this enum and browse all 38 options.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 *
 * MockJutsu.generate("tckn",   TR);   // Türk kimlik numarası
 * MockJutsu.generate("iban",   DE);   // Alman IBAN
 * MockJutsu.generate("phone",  JP);   // Japon telefon numarası
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
