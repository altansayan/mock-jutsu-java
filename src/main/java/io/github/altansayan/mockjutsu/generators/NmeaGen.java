package io.github.altansayan.mockjutsu.generators;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** NMEA — GPGGA, GPRMC with correct NMEA checksum. Mirrors nmea.py. */
public final class NmeaGen {
    private NmeaGen() {}

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "nmea_gpgga" -> gpgga(rng);
            case "nmea_gprmc" -> gprmc(rng);
            default -> "ERROR: Unknown NMEA type '" + type + "'";
        };
    }

    // ── GPGGA ─────────────────────────────────────────────────────────────────

    private static String gpgga(ThreadLocalRandom rng) {
        String timeStr = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HHmmss")) + ".00";

        double latDeg = rng.nextDouble(0.0, 90.0);
        char latDir = rng.nextBoolean() ? 'N' : 'S';
        double lonDeg = rng.nextDouble(0.0, 180.0);
        char lonDir = rng.nextBoolean() ? 'E' : 'W';
        String latStr = latNmea(latDeg);
        String lonStr = lonNmea(lonDeg);

        int fixQuality = rng.nextBoolean() ? 1 : 2;
        int numSats = rng.nextInt(4, 13);
        double hdop = round1(rng.nextDouble(0.5, 5.0));
        double altitude = round1(rng.nextDouble(-50.0, 8849.0));
        double geoid = round1(rng.nextDouble(-100.0, 100.0));

        String body = String.format(java.util.Locale.US,
            "GPGGA,%s,%s,%c,%s,%c,%d,%02d,%s,%s,M,%s,M,,",
            timeStr, latStr, latDir, lonStr, lonDir, fixQuality, numSats,
            fmtNum(hdop), fmtNum(altitude), fmtNum(geoid));
        String checksum = nmeaChecksum(body);
        String sentence = "$" + body + "*" + checksum;

        return String.format(java.util.Locale.US,
            "{\"sentence\":\"%s\",\"type\":\"GPGGA\",\"time\":\"%s\",\"lat\":\"%s\",\"lat_dir\":\"%c\"," +
            "\"lon\":\"%s\",\"lon_dir\":\"%c\",\"fix_quality\":%d,\"num_satellites\":%d," +
            "\"hdop\":%s,\"altitude\":%s,\"geoid_height\":%s,\"checksum\":\"%s\"}",
            sentence, timeStr, latStr, latDir, lonStr, lonDir, fixQuality, numSats,
            fmtNum(hdop), fmtNum(altitude), fmtNum(geoid), checksum);
    }

    // ── GPRMC ─────────────────────────────────────────────────────────────────

    private static String gprmc(ThreadLocalRandom rng) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String timeStr = now.format(DateTimeFormatter.ofPattern("HHmmss")) + ".00";
        String dateStr = now.format(DateTimeFormatter.ofPattern("ddMMyy"));

        double latDeg = rng.nextDouble(0.0, 90.0);
        char latDir = rng.nextBoolean() ? 'N' : 'S';
        double lonDeg = rng.nextDouble(0.0, 180.0);
        char lonDir = rng.nextBoolean() ? 'E' : 'W';
        String latStr = latNmea(latDeg);
        String lonStr = lonNmea(lonDeg);

        double speed = round1(rng.nextDouble(0.0, 100.0));
        double course = round1(rng.nextDouble(0.0, 359.9));

        String body = String.format(java.util.Locale.US,
            "GPRMC,%s,A,%s,%c,%s,%c,%s,%s,%s,,",
            timeStr, latStr, latDir, lonStr, lonDir, fmtNum(speed), fmtNum(course), dateStr);
        String checksum = nmeaChecksum(body);
        String sentence = "$" + body + "*" + checksum;

        return String.format(java.util.Locale.US,
            "{\"sentence\":\"%s\",\"type\":\"GPRMC\",\"time\":\"%s\",\"status\":\"A\"," +
            "\"lat\":\"%s\",\"lat_dir\":\"%c\",\"lon\":\"%s\",\"lon_dir\":\"%c\"," +
            "\"speed_knots\":%s,\"course\":%s,\"date\":\"%s\",\"checksum\":\"%s\"}",
            sentence, timeStr, latStr, latDir, lonStr, lonDir, fmtNum(speed), fmtNum(course), dateStr, checksum);
    }

    // ── NMEA checksum: XOR of all bytes between $ and * ──────────────────────

    static String nmeaChecksum(String sentence) {
        int cs = 0;
        for (char c : sentence.toCharArray()) cs ^= c;
        return String.format("%02X", cs);
    }

    private static String latNmea(double deg) {
        int d = (int) deg;
        double m = (deg - d) * 60.0;
        return String.format(java.util.Locale.US, "%02d%07.4f", d, m);
    }

    private static String lonNmea(double deg) {
        int d = (int) deg;
        double m = (deg - d) * 60.0;
        return String.format(java.util.Locale.US, "%03d%07.4f", d, m);
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static String fmtNum(double v) {
        return String.format(java.util.Locale.US, "%.1f", v);
    }
}
