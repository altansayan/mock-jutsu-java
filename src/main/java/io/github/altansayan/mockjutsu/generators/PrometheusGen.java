package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Prometheus / OpenMetrics exposition format generator. Mirrors prometheus.py. */
public final class PrometheusGen {
    private PrometheusGen() {}

    private static final String[] HTTP_METHODS = {"GET","POST","PUT","DELETE","PATCH"};
    private static final String[] HTTP_PATHS = {
        "/", "/api/health", "/api/v1/users", "/api/v1/orders",
        "/api/v1/products", "/api/v1/data", "/api/v1/auth/token", "/metrics"
    };
    private static final String[] HTTP_STATUS_OK = {"200","201","204"};
    private static final String[] HTTP_STATUS_ERR = {"400","401","403","404","500","502"};
    private static final String[] HTTP_STATUS_POOL = buildStatusPool();
    private static String[] buildStatusPool() {
        java.util.List<String> l = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) l.addAll(java.util.Arrays.asList(HTTP_STATUS_OK));
        l.addAll(java.util.Arrays.asList(HTTP_STATUS_ERR));
        return l.toArray(new String[0]);
    }

    private static final String[] HIST_LE = {
        "0.005","0.01","0.025","0.05","0.1","0.25","0.5","1","2.5","5","10"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "prometheus_metrics"   -> prometheusMetrics(rng);
            case "openmetrics_snapshot" -> openMetrics(rng);
            default -> "ERROR: Unknown prometheus type '" + type + "'";
        };
    }

    // ── Number formatting (Prometheus-compatible) ────────────────────────────

    private static String fmt(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15) {
            return String.valueOf((long) v);
        }
        if (Math.abs(v) < 1e-3 || Math.abs(v) >= 1e10) {
            return String.format(java.util.Locale.US, "%.6e", v);
        }
        String s = String.format(java.util.Locale.US, "%.4f", v);
        int end = s.length();
        while (end > 0 && s.charAt(end - 1) == '0') end--;
        if (end > 0 && s.charAt(end - 1) == '.') end--;
        return s.substring(0, end);
    }

    // ── Monotonic histogram buckets ──────────────────────────────────────────

    private static java.util.List<String[]> monoBuckets(ThreadLocalRandom rng, long totalCount) {
        double[] fracs = new double[HIST_LE.length];
        for (int i = 0; i < fracs.length; i++) fracs[i] = rng.nextDouble(0.05, 1.0);
        java.util.Arrays.sort(fracs);
        double top = fracs[fracs.length - 1];
        long[] counts = new long[fracs.length];
        for (int i = 0; i < fracs.length; i++) counts[i] = (long) (totalCount * (fracs[i] / top));
        for (int i = 1; i < counts.length; i++) counts[i] = Math.max(counts[i], counts[i - 1]);
        counts[counts.length - 1] = Math.min(counts[counts.length - 1], totalCount);

        java.util.List<String[]> result = new java.util.ArrayList<>();
        for (int i = 0; i < HIST_LE.length; i++) result.add(new String[]{HIST_LE[i], String.valueOf(counts[i])});
        result.add(new String[]{"+Inf", String.valueOf(totalCount)});
        return result;
    }

    // ── Metric family builders ────────────────────────────────────────────────

    private static java.util.List<String> buildProcessMetrics(ThreadLocalRandom rng) {
        double cpu = round2(rng.nextDouble(0.5, 100000.0));
        long rss = rng.nextLong(10L * 1024 * 1024, 4L * 1024 * 1024 * 1024);
        long vms = rss + rng.nextLong(50L * 1024 * 1024, 512L * 1024 * 1024);
        int fds = rng.nextInt(5, 513);
        int ageS = rng.nextInt(3600, 86400 * 30 + 1);
        double startT = round3(System.currentTimeMillis() / 1000.0 - ageS);

        java.util.List<String> lines = new java.util.ArrayList<>();
        Object[][] specs = {
            {"process_cpu_seconds_total", "counter", cpu, "Total user and system CPU time spent in seconds."},
            {"process_resident_memory_bytes", "gauge", (double) rss, "Resident memory size in bytes."},
            {"process_virtual_memory_bytes", "gauge", (double) vms, "Virtual memory size in bytes."},
            {"process_open_fds", "gauge", (double) fds, "Number of open file descriptors."},
            {"process_start_time_seconds", "gauge", startT, "Start time of the process since unix epoch in seconds."},
        };
        for (Object[] s : specs) {
            String name = (String) s[0], typ = (String) s[1], help = (String) s[3];
            double val = (double) s[2];
            lines.add("# HELP " + name + " " + help);
            lines.add("# TYPE " + name + " " + typ);
            lines.add(name + " " + fmt(val));
        }
        return lines;
    }

    private static java.util.List<String> buildHttpCounter(ThreadLocalRandom rng) {
        String name = "http_requests_total";
        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add("# HELP " + name + " Total number of HTTP requests received.");
        lines.add("# TYPE " + name + " counter");

        String[] methods = sampleWithoutReplacement(rng, HTTP_METHODS, rng.nextInt(2, 5));
        String[] paths = sampleWithoutReplacement(rng, HTTP_PATHS, rng.nextInt(2, 5));
        for (String method : methods) {
            for (String path : paths) {
                String status = HTTP_STATUS_POOL[rng.nextInt(HTTP_STATUS_POOL.length)];
                int count = rng.nextInt(1, 100001);
                lines.add(name + "{method=\"" + method + "\",path=\"" + path + "\",status=\"" + status + "\"} " + count);
            }
        }
        return lines;
    }

    private static java.util.List<String> buildHttpHistogram(ThreadLocalRandom rng) {
        String name = "http_request_duration_seconds";
        long totalCount = rng.nextLong(200, 50001);
        double avgDur = rng.nextDouble(0.01, 1.0);
        double totalSum = round3(totalCount * avgDur * rng.nextDouble(0.8, 1.2));

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add("# HELP " + name + " HTTP request latency in seconds.");
        lines.add("# TYPE " + name + " histogram");
        for (String[] bucket : monoBuckets(rng, totalCount)) {
            lines.add(name + "_bucket{le=\"" + bucket[0] + "\"} " + bucket[1]);
        }
        lines.add(name + "_sum " + fmt(totalSum));
        lines.add(name + "_count " + totalCount);
        return lines;
    }

    private static java.util.List<String> buildGoMetrics(ThreadLocalRandom rng) {
        int goroutines = rng.nextInt(5, 201);
        long alloc = rng.nextLong(1L * 1024 * 1024, 500L * 1024 * 1024);
        int gcCount = rng.nextInt(1, 2001);
        double gcSum = round6(gcCount * rng.nextDouble(1e-5, 5e-4));

        java.util.List<String> lines = new java.util.ArrayList<>();
        Object[][] specs = {
            {"go_goroutines", "gauge", (double) goroutines, "Number of goroutines that currently exist."},
            {"go_memstats_alloc_bytes", "gauge", (double) alloc, "Number of bytes allocated and still in use."},
        };
        for (Object[] s : specs) {
            String name = (String) s[0], typ = (String) s[1], help = (String) s[3];
            double val = (double) s[2];
            lines.add("# HELP " + name + " " + help);
            lines.add("# TYPE " + name + " " + typ);
            lines.add(name + " " + fmt(val));
        }

        String name = "go_gc_duration_seconds";
        String[] quants = {"0","0.25","0.5","0.75","1"};
        double[] qVals = new double[5];
        for (int i = 0; i < 5; i++) qVals[i] = rng.nextDouble(1e-6, 1e-3);
        java.util.Arrays.sort(qVals);
        lines.add("# HELP " + name + " A summary of GC invocation durations.");
        lines.add("# TYPE " + name + " summary");
        for (int i = 0; i < 5; i++) {
            lines.add(name + "{quantile=\"" + quants[i] + "\"} " + fmt(qVals[i]));
        }
        lines.add(name + "_sum " + fmt(gcSum));
        lines.add(name + "_count " + gcCount);
        return lines;
    }

    // ── Assembly ──────────────────────────────────────────────────────────────

    private static String assemble(java.util.List<java.util.List<String>> blocks, String terminator) {
        java.util.List<String> sections = new java.util.ArrayList<>();
        for (java.util.List<String> block : blocks) {
            if (!block.isEmpty()) sections.add(String.join("\n", block));
        }
        String body = String.join("\n\n", sections) + "\n";
        if (terminator != null) body += terminator + "\n";
        return body;
    }

    private static String prometheusMetrics(ThreadLocalRandom rng) {
        java.util.List<java.util.List<String>> blocks = new java.util.ArrayList<>();
        blocks.add(buildProcessMetrics(rng));
        blocks.add(buildHttpCounter(rng));
        blocks.add(buildHttpHistogram(rng));
        if (rng.nextDouble() < 0.7) blocks.add(buildGoMetrics(rng));

        String exposition = assemble(blocks, null);
        return buildResultJson(exposition, "prometheus");
    }

    private static String openMetrics(ThreadLocalRandom rng) {
        java.util.List<java.util.List<String>> blocks = new java.util.ArrayList<>();
        blocks.add(buildProcessMetrics(rng));
        blocks.add(buildHttpCounter(rng));
        blocks.add(buildHttpHistogram(rng));
        if (rng.nextDouble() < 0.7) blocks.add(buildGoMetrics(rng));

        String exposition = assemble(blocks, "# EOF");
        return buildResultJson(exposition, "openmetrics");
    }

    private static String buildResultJson(String exposition, String format) {
        java.util.List<String> families = new java.util.ArrayList<>();
        int samples = 0;
        for (String line : exposition.split("\n", -1)) {
            if (line.startsWith("# TYPE ")) {
                String[] parts = line.split("\\s+");
                if (parts.length > 2) families.add(parts[2]);
            } else if (!line.isEmpty() && !line.startsWith("#")) {
                samples++;
            }
        }
        StringBuilder famArr = new StringBuilder("[");
        for (int i = 0; i < families.size(); i++) {
            if (i > 0) famArr.append(',');
            famArr.append('"').append(families.get(i)).append('"');
        }
        famArr.append(']');

        return "{\"exposition\":\"" + jsonEscape(exposition) + "\",\"format\":\"" + format + "\"," +
            "\"metric_families\":" + famArr + ",\"total_samples\":" + samples + "}";
    }

    private static String[] sampleWithoutReplacement(ThreadLocalRandom rng, String[] pool, int k) {
        java.util.List<String> list = new java.util.ArrayList<>(java.util.Arrays.asList(pool));
        java.util.Collections.shuffle(list, new java.util.Random(rng.nextLong()));
        return list.subList(0, Math.min(k, list.size())).toArray(new String[0]);
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private static double round3(double v) { return Math.round(v * 1000.0) / 1000.0; }
    private static double round6(double v) { return Math.round(v * 1000000.0) / 1000000.0; }

    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
