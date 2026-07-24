package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** ANSI X12 EDI 850 / UN-EDIFACT ORDERS D96A generator. Mirrors edi.py. */
public final class EdiGen {
    private EdiGen() {}

    private static final String[] COMPANIES = {
        "ACME CORP", "GLOBAL TRADE INC", "APEX SOLUTIONS", "METRO SUPPLY CO",
        "PINNACLE GOODS", "SUMMIT TRADING", "HORIZON GROUP", "NEXUS LOGISTICS"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "edi_850"        -> edi850(rng);
            case "edifact_orders" -> edifactOrders(rng);
            default -> "ERROR: Unknown EDI type '" + type + "'";
        };
    }

    private static String randAlphaNum(ThreadLocalRandom rng, int n) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    private static String randNumeric(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('0' + rng.nextInt(10)));
        return sb.toString();
    }

    private static String padRight(String s, int len) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append(' ');
        return sb.toString();
    }

    // ── X12 EDI 850 Purchase Order ────────────────────────────────────────────

    private static String edi850(ThreadLocalRandom rng) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        String ctrlNum  = randNumeric(rng, 9);
        String grpNum   = randNumeric(rng, 4);
        String transNum = randNumeric(rng, 4);
        String poNum    = "PO" + randNumeric(rng, 6);

        String date6 = today.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String date8 = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time4 = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmm"));

        String senderId   = padRight(randAlphaNum(rng, 10), 15);
        String receiverId = padRight(randAlphaNum(rng, 10), 15);

        String buyerName  = COMPANIES[rng.nextInt(COMPANIES.length)];
        String sellerName = COMPANIES[rng.nextInt(COMPANIES.length)];
        String buyerId  = randAlphaNum(rng, 6);
        String sellerId = randAlphaNum(rng, 6);

        int nLines = rng.nextInt(1, 4);
        java.util.List<String> po1Segs = new java.util.ArrayList<>();
        for (int i = 1; i <= nLines; i++) {
            int qty = rng.nextInt(1, 101);
            double price = Math.round(rng.nextDouble(5.0, 500.0) * 100.0) / 100.0;
            String part = randAlphaNum(rng, 8);
            po1Segs.add(String.format(java.util.Locale.US, "PO1*%d*%d*EA*%.2f**VP*%s", i, qty, price, part));
        }

        java.util.List<String> inner = new java.util.ArrayList<>();
        inner.add("ST*850*" + transNum);
        inner.add("BEG*00*SA*" + poNum + "**" + date8);
        inner.add("N1*BY*" + buyerName + "*92*" + buyerId);
        inner.add("N1*SE*" + sellerName + "*92*" + sellerId);
        inner.addAll(po1Segs);
        inner.add("CTT*" + nLines);

        int segCount = inner.size() + 1;
        inner.add("SE*" + segCount + "*" + transNum);

        String isa = "ISA*00*          *00*          " +
            "*ZZ*" + senderId + "*ZZ*" + receiverId +
            "*" + date6 + "*" + time4 + "*^*00501*" + ctrlNum + "*0*P*:";

        java.util.List<String> segments = new java.util.ArrayList<>();
        segments.add(isa);
        segments.add("GS*PO*" + senderId.strip() + "*" + receiverId.strip() + "*" + date8 + "*" + time4 + "*" + grpNum + "*X*004010");
        segments.addAll(inner);
        segments.add("GE*1*" + grpNum);
        segments.add("IEA*1*" + ctrlNum);

        return String.join("~\n", segments) + "~";
    }

    // ── EDIFACT ORDERS D96A ───────────────────────────────────────────────────

    private static String edifactOrders(ThreadLocalRandom rng) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        String ctrlRef = randNumeric(rng, 9);
        String msgRef  = randAlphaNum(rng, 8);
        String poNum   = "ORD" + randNumeric(rng, 6);

        String date8 = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time4 = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmm"));

        String senderId   = randAlphaNum(rng, 10);
        String receiverId = randAlphaNum(rng, 10);

        String buyerName  = COMPANIES[rng.nextInt(COMPANIES.length)];
        String sellerName = COMPANIES[rng.nextInt(COMPANIES.length)];
        String buyerId  = randAlphaNum(rng, 6);
        String sellerId = randAlphaNum(rng, 6);

        int nLines = rng.nextInt(1, 4);
        java.util.List<String> lineSegs = new java.util.ArrayList<>();
        for (int i = 1; i <= nLines; i++) {
            int qty = rng.nextInt(1, 101);
            double price = Math.round(rng.nextDouble(5.0, 500.0) * 100.0) / 100.0;
            String part = randAlphaNum(rng, 8);
            lineSegs.add("LIN+" + i + "++" + part + ":SA");
            lineSegs.add("QTY+21:" + qty);
            lineSegs.add(String.format(java.util.Locale.US, "PRI+AAA:%.2f", price));
        }

        java.util.List<String> inner = new java.util.ArrayList<>();
        inner.add("UNH+" + msgRef + "+ORDERS:D:96A:UN");
        inner.add("BGM+220+" + poNum + "+9");
        inner.add("DTM+137:" + date8 + ":102");
        inner.add("NAD+BY+" + buyerId + "::92++" + buyerName);
        inner.add("NAD+SE+" + sellerId + "::92++" + sellerName);
        inner.addAll(lineSegs);
        inner.add("UNS+S");
        inner.add("CNT+2:" + nLines);

        int segCount = inner.size() + 1;
        inner.add("UNT+" + segCount + "+" + msgRef);

        java.util.List<String> segments = new java.util.ArrayList<>();
        segments.add("UNB+UNOC:3+" + senderId + ":ZZZ+" + receiverId + ":ZZZ+" + date8 + ":" + time4 + "+" + ctrlRef);
        segments.addAll(inner);
        segments.add("UNZ+1+" + ctrlRef);

        return String.join("'\n", segments) + "'";
    }
}
