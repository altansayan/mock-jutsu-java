package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Causal Event-Sourcing & CDC generator. Mirrors event_sourcing.py. */
public final class EventSourcingGen {
    private EventSourcingGen() {}

    // ── Markov Chain — user journey ──────────────────────────────────────────

    private static final java.util.Map<String, Object[][]> TRANSITIONS = new java.util.LinkedHashMap<>();
    static {
        TRANSITIONS.put("login", new Object[][]{{"page_view", 0.80}, {"logout", 0.20}});
        TRANSITIONS.put("page_view", new Object[][]{{"page_view", 0.30}, {"search", 0.30}, {"add_to_cart", 0.20}, {"checkout", 0.10}, {"logout", 0.10}});
        TRANSITIONS.put("search", new Object[][]{{"page_view", 0.40}, {"add_to_cart", 0.30}, {"search", 0.20}, {"logout", 0.10}});
        TRANSITIONS.put("add_to_cart", new Object[][]{{"page_view", 0.30}, {"add_to_cart", 0.20}, {"checkout", 0.40}, {"logout", 0.10}});
        TRANSITIONS.put("checkout", new Object[][]{{"payment", 0.70}, {"add_to_cart", 0.10}, {"logout", 0.20}});
        TRANSITIONS.put("payment", new Object[][]{{"logout", 0.90}, {"page_view", 0.10}});
        TRANSITIONS.put("logout", new Object[][]{});
    }

    private static final String[] PAGES = {"/", "/products", "/category/electronics", "/category/clothing", "/about", "/deals"};
    private static final String[] SEARCH_TERMS = {"laptop", "shoes", "headphones", "keyboard", "monitor", "jacket", "phone", "tablet"};
    private static final String[] PAY_METHODS_EVENT = {"credit_card", "paypal", "bank_transfer", "crypto"};

    // ── CDC (Debezium-style) ──────────────────────────────────────────────────

    private static final java.util.Map<String, java.util.LinkedHashMap<String, String>> CDC_TABLES = new java.util.LinkedHashMap<>();
    static {
        java.util.LinkedHashMap<String, String> orders = new java.util.LinkedHashMap<>();
        orders.put("order_id", "uuid"); orders.put("user_id", "uuid"); orders.put("total", "decimal");
        orders.put("status", "order_status"); orders.put("created_at", "ts");
        CDC_TABLES.put("orders", orders);

        java.util.LinkedHashMap<String, String> users = new java.util.LinkedHashMap<>();
        users.put("user_id", "uuid"); users.put("email", "email"); users.put("full_name", "name"); users.put("created_at", "ts");
        CDC_TABLES.put("users", users);

        java.util.LinkedHashMap<String, String> products = new java.util.LinkedHashMap<>();
        products.put("product_id", "uuid"); products.put("name", "product_name"); products.put("price", "decimal"); products.put("stock", "pos_int");
        CDC_TABLES.put("products", products);

        java.util.LinkedHashMap<String, String> payments = new java.util.LinkedHashMap<>();
        payments.put("payment_id", "uuid"); payments.put("order_id", "uuid"); payments.put("amount", "decimal");
        payments.put("method", "pay_method"); payments.put("status", "pay_status");
        CDC_TABLES.put("payments", payments);
    }

    private static final String[] ORDER_STATUSES = {"pending", "processing", "shipped", "delivered", "cancelled"};
    private static final String[] PAY_STATUSES = {"pending", "authorized", "captured", "failed", "refunded"};
    private static final String[] PAY_METHODS = {"credit_card", "debit_card", "paypal", "bank_transfer", "crypto"};
    private static final String[] PRODUCT_NAMES = {"Laptop Pro 15\"", "Wireless Headset", "USB-C Hub 7-in-1", "Mechanical Keyboard", "Monitor 27\" 4K"};
    private static final String[] FULL_NAMES = {"Alice Smith", "Bob Johnson", "Carol Lee", "David Park", "Emma Wilson"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "event_stream" -> eventStream(rng);
            case "cdc_event"    -> cdcEvent(rng);
            default -> "ERROR: Unknown event sourcing type '" + type + "'";
        };
    }

    // ── Event Stream ──────────────────────────────────────────────────────────

    private static String eventStream(ThreadLocalRandom rng) {
        int maxSteps = 15;
        String correlationId = java.util.UUID.randomUUID().toString();
        String sessionId = java.util.UUID.randomUUID().toString();
        String userId = java.util.UUID.randomUUID().toString();

        java.time.ZonedDateTime ts = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
            .minusDays(rng.nextInt(7))
            .minusHours(rng.nextInt(24))
            .minusMinutes(rng.nextInt(60));

        java.util.List<String> events = new java.util.ArrayList<>();
        java.util.List<String> eventTypes = new java.util.ArrayList<>();
        String state = "login";

        for (int i = 0; i < maxSteps; i++) {
            String eventId = java.util.UUID.randomUUID().toString();
            String timestamp = formatTs(ts, rng);
            events.add(buildEvent(eventId, userId, correlationId, sessionId, timestamp, state, payload(rng, state)));
            eventTypes.add(state);
            ts = ts.plusSeconds(rng.nextInt(5, 121));

            if (state.equals("logout")) break;
            String nxt = nextState(rng, state);
            if (nxt == null) break;
            state = nxt;
        }

        if (!eventTypes.get(eventTypes.size() - 1).equals("logout")) {
            String eventId = java.util.UUID.randomUUID().toString();
            String timestamp = formatTs(ts, rng);
            events.add(buildEvent(eventId, userId, correlationId, sessionId, timestamp, "logout", payload(rng, "logout")));
        }

        return "[" + String.join(", ", events) + "]";
    }

    private static String formatTs(java.time.ZonedDateTime ts, ThreadLocalRandom rng) {
        String base = ts.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return base + "." + String.format("%03d", rng.nextInt(1000)) + "Z";
    }

    private static String nextState(ThreadLocalRandom rng, String current) {
        Object[][] transitions = TRANSITIONS.getOrDefault(current, new Object[0][]);
        if (transitions.length == 0) return null;
        double r = rng.nextDouble();
        double cumul = 0.0;
        for (Object[] t : transitions) {
            cumul += (double) t[1];
            if (r < cumul) return (String) t[0];
        }
        return (String) transitions[transitions.length - 1][0];
    }

    private static String buildEvent(String eventId, String userId, String correlationId, String sessionId,
                                       String timestamp, String eventType, String payloadJson) {
        return "{\"event_id\": \"" + eventId + "\", \"aggregate_id\": \"" + userId + "\", \"aggregate_type\": \"User\", " +
            "\"correlation_id\": \"" + correlationId + "\", \"session_id\": \"" + sessionId + "\", \"user_id\": \"" + userId + "\", " +
            "\"timestamp\": \"" + timestamp + "\", \"event_type\": \"" + eventType + "\", \"payload\": " + payloadJson + "}";
    }

    private static String payload(ThreadLocalRandom rng, String eventType) {
        return switch (eventType) {
            case "login" -> {
                String[] methods = {"password", "oauth", "sso"};
                yield "{\"method\": \"" + methods[rng.nextInt(methods.length)] + "\", \"ip\": \"" + randIp(rng) + "\"}";
            }
            case "page_view" -> {
                String[] refs = {"direct", "google", "email", "social"};
                yield "{\"url\": \"" + PAGES[rng.nextInt(PAGES.length)] + "\", \"referrer\": \"" + refs[rng.nextInt(refs.length)] + "\"}";
            }
            case "search" -> "{\"query\": \"" + SEARCH_TERMS[rng.nextInt(SEARCH_TERMS.length)] + "\", \"results\": " + rng.nextInt(0, 251) + "}";
            case "add_to_cart" -> {
                String sku = "SKU-" + rng.nextInt(10000, 100000);
                int qty = rng.nextInt(1, 6);
                double price = round2(rng.nextDouble(9.99, 499.99));
                yield "{\"sku\": \"" + sku + "\", \"quantity\": " + qty + ", \"price\": " + price + "}";
            }
            case "checkout" -> {
                int items = rng.nextInt(1, 9);
                double total = round2(rng.nextDouble(19.99, 1999.99));
                yield "{\"items\": " + items + ", \"total\": " + total + ", \"currency\": \"USD\"}";
            }
            case "payment" -> {
                double amount = round2(rng.nextDouble(19.99, 1999.99));
                yield "{\"method\": \"" + PAY_METHODS_EVENT[rng.nextInt(PAY_METHODS_EVENT.length)] + "\", \"status\": \"success\", \"amount\": " + amount + "}";
            }
            case "logout" -> {
                String[] reasons = {"user_initiated", "session_timeout", "inactivity"};
                yield "{\"reason\": \"" + reasons[rng.nextInt(reasons.length)] + "\"}";
            }
            default -> "{}";
        };
    }

    private static String randIp(ThreadLocalRandom rng) {
        return rng.nextInt(1, 255) + "." + rng.nextInt(0, 256) + "." + rng.nextInt(0, 256) + "." + rng.nextInt(1, 255);
    }

    // ── CDC Event ─────────────────────────────────────────────────────────────

    private static String cdcEvent(ThreadLocalRandom rng) {
        String[] tables = CDC_TABLES.keySet().toArray(new String[0]);
        String table = tables[rng.nextInt(tables.length)];
        java.util.LinkedHashMap<String, String> schema = CDC_TABLES.get(table);

        int r = rng.nextInt(100);
        String op = r < 30 ? "c" : (r < 80 ? "u" : "d");

        long tsMs = System.currentTimeMillis() + rng.nextLong(-86_400_000L, 1L);

        java.util.LinkedHashMap<String, Object> beforeRow = genRow(rng, schema);
        java.util.LinkedHashMap<String, Object> afterRow = genRow(rng, schema);
        String pkField = schema.keySet().iterator().next();
        afterRow.put(pkField, beforeRow.get(pkField));

        String beforeJson, afterJson;
        if (op.equals("c")) {
            beforeJson = "null";
            afterJson = rowToJson(afterRow);
        } else if (op.equals("d")) {
            beforeJson = rowToJson(beforeRow);
            afterJson = "null";
        } else {
            beforeJson = rowToJson(beforeRow);
            afterJson = rowToJson(afterRow);
        }

        return "{\"op\": \"" + op + "\", \"ts_ms\": " + tsMs + ", \"source\": {\"version\": \"2.3.0\", " +
            "\"connector\": \"postgresql\", \"db\": \"mockdb\", \"table\": \"" + table + "\"}, " +
            "\"before\": " + beforeJson + ", \"after\": " + afterJson + "}";
    }

    private static java.util.LinkedHashMap<String, Object> genRow(ThreadLocalRandom rng, java.util.LinkedHashMap<String, String> schema) {
        java.util.LinkedHashMap<String, Object> row = new java.util.LinkedHashMap<>();
        for (var e : schema.entrySet()) row.put(e.getKey(), genField(rng, e.getValue()));
        return row;
    }

    private static Object genField(ThreadLocalRandom rng, String ftype) {
        return switch (ftype) {
            case "uuid" -> java.util.UUID.randomUUID().toString();
            case "decimal" -> round2(rng.nextDouble(1.0, 9999.99));
            case "pos_int" -> rng.nextInt(0, 1001);
            case "email" -> "user" + rng.nextInt(1000, 10000) + "@example.com";
            case "name" -> FULL_NAMES[rng.nextInt(FULL_NAMES.length)];
            case "ts" -> java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            case "order_status" -> ORDER_STATUSES[rng.nextInt(ORDER_STATUSES.length)];
            case "pay_status" -> PAY_STATUSES[rng.nextInt(PAY_STATUSES.length)];
            case "pay_method" -> PAY_METHODS[rng.nextInt(PAY_METHODS.length)];
            case "product_name" -> PRODUCT_NAMES[rng.nextInt(PRODUCT_NAMES.length)];
            default -> null;
        };
    }

    private static String rowToJson(java.util.LinkedHashMap<String, Object> row) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : row.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append("\"").append(e.getKey()).append("\": ").append(jsonValue(e.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String jsonValue(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s.replace("\"", "\\\"") + "\"";
        return v.toString();
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
