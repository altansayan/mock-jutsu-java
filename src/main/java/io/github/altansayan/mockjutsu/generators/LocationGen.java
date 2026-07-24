package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

public final class LocationGen {
    private LocationGen() {}

    private static final String[] TZ_TR = {"Europe/Istanbul"};
    private static final String[] TZ_US = {"America/New_York","America/Chicago","America/Denver","America/Los_Angeles","America/Phoenix","America/Anchorage"};
    private static final String[] TZ_UK = {"Europe/London"};
    private static final String[] TZ_DE = {"Europe/Berlin"};
    private static final String[] TZ_FR = {"Europe/Paris"};
    private static final String[] TZ_RU = {"Europe/Moscow","Asia/Yekaterinburg","Asia/Novosibirsk","Asia/Krasnoyarsk","Asia/Irkutsk","Asia/Vladivostok"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "latitude"     -> latitude(rng, locale);
            case "longitude"    -> longitude(rng, locale);
            case "timezone"     -> timezone(rng, locale);
            case "country_code" -> countryCode(locale);
            case "coordinates"  -> latitude(rng, locale) + "," + longitude(rng, locale);
            default -> "ERROR: Unknown location type '" + type + "'";
        };
    }

    static String latitude(ThreadLocalRandom rng, String locale) {
        double min = switch (locale) {
            case "TR" -> 36.0; case "US" -> 25.0; case "UK" -> 50.0;
            case "DE" -> 47.0; case "FR" -> 42.0; case "RU" -> 41.0;
            default   -> -90.0;
        };
        double max = switch (locale) {
            case "TR" -> 42.0; case "US" -> 49.0; case "UK" -> 59.0;
            case "DE" -> 55.0; case "FR" -> 51.0; case "RU" -> 82.0;
            default   -> 90.0;
        };
        return String.format(java.util.Locale.US, "%.6f", min + rng.nextDouble(max - min));
    }

    static String longitude(ThreadLocalRandom rng, String locale) {
        double min = switch (locale) {
            case "TR" ->   26.0; case "US" -> -125.0; case "UK" ->  -8.0;
            case "DE" ->    6.0; case "FR" ->   -5.0; case "RU" ->  27.0;
            default   -> -180.0;
        };
        double max = switch (locale) {
            case "TR" ->  45.0; case "US" -> -66.0; case "UK" ->   2.0;
            case "DE" ->  15.0; case "FR" ->   8.0; case "RU" -> 170.0;
            default   -> 180.0;
        };
        return String.format(java.util.Locale.US, "%.6f", min + rng.nextDouble(max - min));
    }

    static String timezone(ThreadLocalRandom rng, String locale) {
        String[] zones = switch (locale) {
            case "TR" -> TZ_TR; case "US" -> TZ_US; case "UK" -> TZ_UK;
            case "DE" -> TZ_DE; case "FR" -> TZ_FR; case "RU" -> TZ_RU;
            default   -> TZ_TR;
        };
        return zones[rng.nextInt(zones.length)];
    }

    static String countryCode(String locale) {
        return switch (locale) {
            case "TR" -> "TR"; case "US" -> "US"; case "UK" -> "GB";
            case "DE" -> "DE"; case "FR" -> "FR"; case "RU" -> "RU";
            default   -> "TR";
        };
    }
}
