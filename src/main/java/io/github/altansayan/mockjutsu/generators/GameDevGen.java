package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Quaternion and NavMesh path generator. Mirrors gamedev.py. */
public final class GameDevGen {
    private GameDevGen() {}

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "quaternion"   -> quaternion(rng);
            case "navmesh_path" -> navmeshPath(rng);
            default -> "ERROR: Unknown gamedev type '" + type + "'";
        };
    }

    // ── Quaternion — L2-normalized unit quaternion + Euler angles (ZYX) ───────

    private static String quaternion(ThreadLocalRandom rng) {
        double[] raw = randomQuaternion(rng);
        double x = round(raw[0], 1e8);
        double y = round(raw[1], 1e8);
        double z = round(raw[2], 1e8);
        double w = round(raw[3], 1e8);
        double magnitude = round(Math.sqrt(x*x + y*y + z*z + w*w), 1e10);

        double sinr = 2.0 * (w*x + y*z);
        double cosr = 1.0 - 2.0 * (x*x + y*y);
        double roll = Math.toDegrees(Math.atan2(sinr, cosr));

        double sinp = 2.0 * (w*y - z*x);
        sinp = Math.max(-1.0, Math.min(1.0, sinp));
        double pitch = Math.toDegrees(Math.asin(sinp));

        double siny = 2.0 * (w*z + x*y);
        double cosy = 1.0 - 2.0 * (y*y + z*z);
        double yaw = Math.toDegrees(Math.atan2(siny, cosy));

        pitch = round(pitch, 1e4);
        yaw   = round(yaw, 1e4);
        roll  = round(roll, 1e4);

        return String.format(java.util.Locale.US,
            "{\"x\":%.8f,\"y\":%.8f,\"z\":%.8f,\"w\":%.8f,\"magnitude\":%.10f," +
            "\"euler_degrees\":{\"pitch\":%.4f,\"yaw\":%.4f,\"roll\":%.4f}}",
            x, y, z, w, magnitude, pitch, yaw, roll);
    }

    private static double[] randomQuaternion(ThreadLocalRandom rng) {
        // Gaussian sampling for uniform rotation distribution (Shoemake 1992)
        double x, y, z, w, norm;
        do {
            x = rng.nextGaussian();
            y = rng.nextGaussian();
            z = rng.nextGaussian();
            w = rng.nextGaussian();
            norm = Math.sqrt(x*x + y*y + z*z + w*w);
        } while (norm == 0);
        return new double[]{x/norm, y/norm, z/norm, w/norm};
    }

    // ── NavMesh Path — heading-based random walk, 3-15 waypoints ─────────────

    private static String navmeshPath(ThreadLocalRandom rng) {
        int n = rng.nextInt(3, 16);
        double cx = round(rng.nextDouble(-200.0, 200.0), 1e3);
        double cy = round(rng.nextDouble(-2.0, 2.0), 1e3);
        double cz = round(rng.nextDouble(-200.0, 200.0), 1e3);

        java.util.List<double[]> waypoints = new java.util.ArrayList<>();
        waypoints.add(new double[]{cx, cy, cz});
        double angle = rng.nextDouble(0.0, 2.0 * Math.PI);

        for (int i = 0; i < n - 1; i++) {
            double step = rng.nextDouble(5.0, 25.0);
            angle += rng.nextDouble(-Math.PI / 3.0, Math.PI / 3.0);
            cx = round(cx + step * Math.cos(angle), 1e3);
            cy = round(Math.max(-5.0, Math.min(5.0, cy + rng.nextDouble(-0.5, 0.5))), 1e3);
            cz = round(cz + step * Math.sin(angle), 1e3);
            waypoints.add(new double[]{cx, cy, cz});
        }

        double total = 0.0;
        for (int i = 1; i < waypoints.size(); i++) {
            double[] a = waypoints.get(i - 1);
            double[] b = waypoints.get(i);
            double dx = b[0]-a[0], dy = b[1]-a[1], dz = b[2]-a[2];
            total += Math.sqrt(dx*dx + dy*dy + dz*dz);
        }
        total = round(total, 1e3);

        StringBuilder wsb = new StringBuilder("[");
        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) wsb.append(",");
            double[] p = waypoints.get(i);
            wsb.append(String.format(java.util.Locale.US, "{\"x\":%.3f,\"y\":%.3f,\"z\":%.3f}", p[0], p[1], p[2]));
        }
        wsb.append("]");

        double[] start = waypoints.get(0);
        double[] end = waypoints.get(waypoints.size() - 1);

        return String.format(java.util.Locale.US,
            "{\"start\":{\"x\":%.3f,\"y\":%.3f,\"z\":%.3f}," +
            "\"end\":{\"x\":%.3f,\"y\":%.3f,\"z\":%.3f}," +
            "\"waypoints\":%s," +
            "\"total_distance\":%.3f,\"waypoint_count\":%d}",
            start[0], start[1], start[2], end[0], end[1], end[2], wsb, total, waypoints.size());
    }

    private static double round(double v, double scale) {
        return Math.round(v * scale) / scale;
    }
}
