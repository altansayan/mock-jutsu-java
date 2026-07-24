package io.github.altansayan.mockjutsu.generators;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** FDR / Drone Telemetry — physics-constrained bounded random walk time series. Mirrors telemetry.py. */
public final class TelemetryGen {
    private TelemetryGen() {}

    private static final String[] AIRCRAFT_TYPES = {"B737", "B777", "A320", "A350", "B787", "A380"};
    private static final String[] DRONE_MODELS = {"DJI-MAVIC", "DJI-PHANTOM", "DJI-MINI", "PARROT-ANAFI", "AUTEL-EVO"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "fdr_record"      -> fdrRecord(rng);
            case "drone_telemetry" -> droneTelemetry(rng);
            default -> "ERROR: Unknown telemetry type '" + type + "'";
        };
    }

    // ── FDR Record ────────────────────────────────────────────────────────────

    private static String fdrRecord(ThreadLocalRandom rng) {
        int nSamples = rng.nextInt(10, 31);
        int intervalMs = 100;

        double pitch = round2(rng.nextDouble(-5.0, 5.0));
        double roll = round2(rng.nextDouble(-5.0, 5.0));
        double yaw = round2(pymod(rng.nextDouble(0.0, 360.0), 360.0));
        double altitudeFt = round2(rng.nextDouble(28000.0, 40000.0));
        double speedKts = round2(rng.nextDouble(380.0, 480.0));
        double vspeedFpm = round2(rng.nextDouble(-200.0, 200.0));
        double gForce = round2(rng.nextDouble(0.95, 1.05));

        StringBuilder samples = new StringBuilder("[");
        for (int i = 0; i < nSamples; i++) {
            if (i > 0) samples.append(", ");
            samples.append(String.format(java.util.Locale.US,
                "{\"t\": %d, \"pitch\": %.2f, \"roll\": %.2f, \"yaw\": %.2f, \"altitude_ft\": %.2f, " +
                "\"speed_kts\": %.2f, \"vspeed_fpm\": %.2f, \"g_force\": %.2f}",
                i * intervalMs, pitch, roll, yaw, altitudeFt, speedKts, vspeedFpm, gForce));

            pitch = walkClamp(rng, pitch, -30.0, 30.0, 0.5);
            roll = walkClamp(rng, roll, -45.0, 45.0, 1.0);
            yaw = walkWrap(rng, yaw, 1.0, 360.0);
            altitudeFt = walkClamp(rng, altitudeFt, 0.0, 45000.0, 100.0);
            speedKts = walkClamp(rng, speedKts, 150.0, 600.0, 5.0);
            vspeedFpm = walkClamp(rng, vspeedFpm, -3000.0, 3000.0, 200.0);
            gForce = walkClamp(rng, gForce, 0.5, 3.0, 0.1);
        }
        samples.append("]");

        return "{\"flight_id\": \"" + UUID.randomUUID() + "\", \"aircraft\": \"" + AIRCRAFT_TYPES[rng.nextInt(AIRCRAFT_TYPES.length)] +
            "\", \"recording_start\": \"" + nowIso() + "\", \"interval_ms\": " + intervalMs +
            ", \"samples\": " + samples + "}";
    }

    // ── Drone Telemetry ──────────────────────────────────────────────────────

    private static String droneTelemetry(ThreadLocalRandom rng) {
        int nSamples = rng.nextInt(10, 26);
        int intervalMs = 50;

        double lat = round6(rng.nextDouble(-70.0, 70.0));
        double lon = round6(rng.nextDouble(-170.0, 170.0));
        double altM = round2(rng.nextDouble(50.0, 200.0));
        double pitch = round2(rng.nextDouble(-5.0, 5.0));
        double roll = round2(rng.nextDouble(-5.0, 5.0));
        double yaw = round2(pymod(rng.nextDouble(0.0, 360.0), 360.0));
        double speedMs = round2(rng.nextDouble(2.0, 10.0));
        double batteryPct = round1(rng.nextDouble(80.0, 100.0));
        int rssi = rng.nextInt(-75, -54);

        StringBuilder samples = new StringBuilder("[");
        for (int i = 0; i < nSamples; i++) {
            if (i > 0) samples.append(", ");
            samples.append(String.format(java.util.Locale.US,
                "{\"t\": %d, \"lat\": %.6f, \"lon\": %.6f, \"alt_m\": %.2f, \"pitch\": %.2f, \"roll\": %.2f, " +
                "\"yaw\": %.2f, \"speed_ms\": %.2f, \"battery_pct\": %.1f, \"rssi\": %d}",
                i * intervalMs, lat, lon, altM, pitch, roll, yaw, speedMs, batteryPct, rssi));

            lat = round6(lat + rng.nextDouble(-0.00005, 0.00005));
            lon = round6(lon + rng.nextDouble(-0.00005, 0.00005));
            altM = walkClamp(rng, altM, 0.0, 400.0, 2.0);
            pitch = walkClamp(rng, pitch, -30.0, 30.0, 1.0);
            roll = walkClamp(rng, roll, -30.0, 30.0, 1.0);
            yaw = walkWrap(rng, yaw, 2.0, 360.0);
            speedMs = walkClamp(rng, speedMs, 0.0, 20.0, 0.5);
            batteryPct = round1(Math.max(0.0, batteryPct - rng.nextDouble(0.0, 0.3)));
            rssi = (int) clamp(rssi + rng.nextInt(-2, 3), -100, -30);
        }
        samples.append("]");

        String droneId = DRONE_MODELS[rng.nextInt(DRONE_MODELS.length)] + "-" + rng.nextInt(1000, 10000);
        return "{\"drone_id\": \"" + droneId + "\", \"mission_id\": \"" + UUID.randomUUID() +
            "\", \"recording_start\": \"" + nowIso() + "\", \"interval_ms\": " + intervalMs +
            ", \"samples\": " + samples + "}";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double walkClamp(ThreadLocalRandom rng, double val, double lo, double hi, double maxStep) {
        return round2(clamp(val + rng.nextDouble(-maxStep, maxStep), lo, hi));
    }

    private static double walkWrap(ThreadLocalRandom rng, double val, double maxStep, double mod) {
        return round2(pymod(val + rng.nextDouble(-maxStep, maxStep), mod));
    }

    private static double pymod(double v, double m) {
        double r = v % m;
        return r < 0 ? r + m : r;
    }

    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private static double round6(double v) { return Math.round(v * 1_000_000.0) / 1_000_000.0; }

    private static String nowIso() {
        java.time.ZonedDateTime z = java.time.Instant.now().atZone(java.time.ZoneOffset.UTC);
        String base = z.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        int millis = z.getNano() / 1_000_000;
        return base + "." + String.format("%03d", millis) + "Z";
    }
}
