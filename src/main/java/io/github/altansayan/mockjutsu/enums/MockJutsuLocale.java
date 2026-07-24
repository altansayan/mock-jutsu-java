package io.github.altansayan.mockjutsu.enums;

/** Supported locales — mirrors locale keys used in Python generators. */
public enum MockJutsuLocale {
    TR, US, DE, GB, FR, ES, IT, NL, PT, PL,
    RU, UA, IN, CN, JP, KR, BR, AU, CA, MX,
    SE, DK, NO, FI, AR, CL, CO, ZA, SG, MY,
    TH, EG, IL, RO, HR, BG, LT, EE, NZ, PK;

    /** Returns the locale code string (e.g. "TR", "US"). */
    public String code() {
        return this.name();
    }

    /** Parses a locale string to enum, case-insensitive. Falls back to US if unknown. */
    public static MockJutsuLocale of(String locale) {
        if (locale == null || locale.isBlank()) return US;
        try {
            return valueOf(locale.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return US;
        }
    }
}
