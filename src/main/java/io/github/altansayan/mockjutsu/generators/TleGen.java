package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** TLE — Two-Line Element set (satellite orbital data). Mirrors tle.py. */
public final class TleGen {
    private TleGen() {}

    private static final char[] CLASSIFICATION = {'U', 'U', 'U', 'U', 'C', 'S'};
    private static final String[] SAT_PREFIXES = {
        "MOCKSAT", "TESTBIRD", "SIMSTAR", "JUTSU", "DEMOSAT",
        "TESTBED", "MOCKSTAR", "SIMBIRD", "DEMOSTAR", "MOCKBIRD"
    };
    private static final int[] LAUNCH_YEARS = buildLaunchYears();
    private static int[] buildLaunchYears() {
        int[] arr = new int[30 + 27];
        int idx = 0;
        for (int y = 70; y <= 99; y++) arr[idx++] = y;
        for (int y = 0; y <= 26; y++) arr[idx++] = y;
        return arr;
    }

    public static String generate(String type, String locale) {
        if (!"tle_satellite".equals(type)) return "ERROR: Unknown TLE type '" + type + "'";
        return tleSatellite(ThreadLocalRandom.current());
    }

    private static String pickOrbitType(ThreadLocalRandom rng) {
        int r = rng.nextInt(100);
        if (r < 40) return "LEO";
        if (r < 60) return "MEO";
        if (r < 80) return "GEO";
        if (r < 95) return "SSO";
        return "HEO";
    }

    private static double[] orbitMeanMotion(String type) {
        return switch (type) {
            case "LEO" -> new double[]{11.25, 16.0};
            case "MEO" -> new double[]{2.0, 11.25};
            case "GEO" -> new double[]{0.99, 1.01};
            case "SSO" -> new double[]{14.0, 15.0};
            default    -> new double[]{2.0, 4.0}; // HEO
        };
    }

    private static double[] orbitInclination(String type) {
        return switch (type) {
            case "LEO" -> new double[]{0.0, 97.0};
            case "MEO" -> new double[]{20.0, 65.0};
            case "GEO" -> new double[]{0.0, 0.1};
            case "SSO" -> new double[]{96.0, 98.0};
            default    -> new double[]{50.0, 65.0}; // HEO
        };
    }

    private static double[] orbitEccentricity(String type) {
        return switch (type) {
            case "LEO" -> new double[]{0.0, 0.001};
            case "MEO" -> new double[]{0.0, 0.01};
            case "GEO" -> new double[]{0.0, 0.0001};
            case "SSO" -> new double[]{0.0, 0.001};
            default    -> new double[]{0.5, 0.85}; // HEO
        };
    }

    private static String tleSatellite(ThreadLocalRandom rng) {
        String orbitType = pickOrbitType(rng);
        double[] mmRange = orbitMeanMotion(orbitType);
        double[] inclRange = orbitInclination(orbitType);
        double[] eccRange = orbitEccentricity(orbitType);

        int noradId = rng.nextInt(1000, 100000);
        char classification = CLASSIFICATION[rng.nextInt(CLASSIFICATION.length)];

        int launchYr = LAUNCH_YEARS[rng.nextInt(LAUNCH_YEARS.length)];
        int launchNum = rng.nextInt(1, 1000);
        char launchPiece = (char) ('A' + rng.nextInt(26));
        String intlStr = String.format("%02d%03d%-3s", launchYr, launchNum, String.valueOf(launchPiece));

        int epochYr = rng.nextInt(14, 27);
        double epochDay = round8(rng.nextDouble(1.0, 365.9999));
        String epochStr = String.format(java.util.Locale.US, "%02d%012.8f", epochYr, epochDay);

        double ndot = round8(rng.nextDouble(0.0, 0.00009999));
        String ndotStr = fmt1stDeriv(ndot);
        String nddotStr = " 00000-0";

        double bstar = round8(rng.nextDouble(1e-6, 9.9e-4));
        String bstarStr = fmtExp(bstar);

        int elemSet = rng.nextInt(1, 10000);

        double incl = round4(rng.nextDouble(inclRange[0], inclRange[1]));
        double raan = round4(rng.nextDouble(0.0, 360.0));
        double ecc = round7(rng.nextDouble(eccRange[0], eccRange[1]));
        double argp = round4(rng.nextDouble(0.0, 360.0));
        double manom = round4(rng.nextDouble(0.0, 360.0));
        double mm = round8(rng.nextDouble(mmRange[0], mmRange[1]));
        int revNum = rng.nextInt(0, 100000);

        String line1Body = String.format(java.util.Locale.US, "1 %05d%c %s %s %s %s %s 0 %4d",
            noradId, classification, intlStr, epochStr, ndotStr, nddotStr, bstarStr, elemSet);
        String line1 = line1Body + tleChecksum(line1Body);

        long eccRaw = Math.round(ecc * 1e7);
        String line2Body = String.format(java.util.Locale.US, "2 %05d %8.4f %8.4f %07d %8.4f %8.4f %11.8f%05d",
            noradId, incl, raan, eccRaw, argp, manom, mm, revNum);
        String line2 = line2Body + tleChecksum(line2Body);

        String satName = SAT_PREFIXES[rng.nextInt(SAT_PREFIXES.length)] + "-" + rng.nextInt(1, 1000);

        return String.format(java.util.Locale.US,
            "{\"name\": \"%s\", \"line1\": \"%s\", \"line2\": \"%s\", \"norad_id\": %d, " +
            "\"classification\": \"%c\", \"epoch_year\": %d, \"epoch_day\": %.8f, " +
            "\"inclination\": %.4f, \"raan\": %.4f, \"eccentricity\": %.7f, " +
            "\"arg_of_perigee\": %.4f, \"mean_anomaly\": %.4f, \"mean_motion\": %.8f, " +
            "\"rev_number\": %d, \"orbit_type\": \"%s\"}",
            satName, line1, line2, noradId, classification, epochYr, epochDay,
            incl, raan, ecc, argp, manom, mm, revNum, orbitType);
    }

    // 10-char first time derivative of mean motion: ±.NNNNNNNN
    private static String fmt1stDeriv(double v) {
        char sign = v < 0 ? '-' : ' ';
        double absV = Math.min(Math.abs(v), 0.99999999);
        String full = String.format(java.util.Locale.US, "%.8f", absV); // "0.00001717"
        String frac = full.substring(2);
        return sign + "." + frac;
    }

    // 8-char TLE exponential notation: ±NNNNN±N
    private static String fmtExp(double value) {
        if (value == 0.0) return " 00000-0";
        boolean neg = value < 0;
        double v = Math.abs(value);
        int exp = (int) Math.floor(Math.log10(v)) + 1;
        exp = Math.max(-9, Math.min(9, exp));
        long mantissa = Math.round(v / Math.pow(10.0, exp - 5));
        mantissa = Math.max(10000, Math.min(99999, mantissa));
        char expSign = exp >= 0 ? '+' : '-';
        return (neg ? "-" : " ") + String.format("%05d", mantissa) + expSign + Math.abs(exp);
    }

    static int tleChecksum(String line68) {
        int sum = 0;
        for (int i = 0; i < line68.length() && i < 68; i++) {
            char c = line68.charAt(i);
            if (Character.isDigit(c)) sum += c - '0';
            else if (c == '-') sum += 1;
        }
        return sum % 10;
    }

    private static double round4(double v) { return Math.round(v * 1e4) / 1e4; }
    private static double round7(double v) { return Math.round(v * 1e7) / 1e7; }
    private static double round8(double v) { return Math.round(v * 1e8) / 1e8; }
}
