package io.github.altansayan.mockjutsu.generators;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/** CardPhysics — ISO 8583 bitmap, EMV ARQC/ATC/IAD, ATM session, POS receipt. Mirrors cardphysics.py. */
public final class CardPhysicsGen {
    private CardPhysicsGen() {}

    private static final SecureRandom SEC = new SecureRandom();

    // Pre-computed ISO 8583 bitmaps (verified bit positions)
    private static final String BITMAP_AUTH_REQ  = makeBitmap(2,3,4,7,11,12,13,14,18,22,25,37,41,42,49);
    private static final String BITMAP_AUTH_RESP = makeBitmap(2,3,4,7,11,12,13,38,39,41,42);
    private static final String BITMAP_REVERSAL  = makeBitmap(2,3,4,7,11,12,13,37,41,42,49,56);

    private static String makeBitmap(int... des) {
        long bits = 0L;
        for (int de : des) bits |= (1L << (64 - de));
        return String.format("%016X", bits);
    }

    // ISO 4217: DE049 numeric n3 + alphabetic name, keyed by locale
    private static final java.util.Map<String, String[]> CURRENCY = java.util.Map.of(
        "TR", new String[]{"949","TRY"},
        "DE", new String[]{"978","EUR"},
        "FR", new String[]{"978","EUR"},
        "UK", new String[]{"826","GBP"},
        "US", new String[]{"840","USD"},
        "RU", new String[]{"643","RUB"}
    );

    private static final String[] AID_POOL = {
        "A0000000031010", "A0000000041010", "A0000000651010", "A0000000251010"
    };
    private static final String[] MCC_POOL = {"5411","5999","4111","5912","5691","7011","5812","6011"};
    private static final String[] ENTRY_MODES = {"051","071","002"};
    private static final String[] RESPONSE_CODES = {"00","05","12","14","51","54","57","91"};
    private static final int RECEIPT_WIDTH = 40;

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "emv_arqc"               -> emvArqc();
            case "emv_atc"                -> emvAtc(rng);
            case "emv_iad"                -> emvIad(rng);
            case "iso8583_auth_request"   -> iso8583AuthReq(rng, locale);
            case "iso8583_auth_response"  -> iso8583AuthResp(rng, locale);
            case "iso8583_reversal"       -> iso8583Reversal(rng, locale);
            case "atm_session"            -> atmSession(rng, locale);
            case "pos_receipt"            -> posReceipt(rng, locale);
            default -> "ERROR: Unknown cardphysics type '" + type + "'";
        };
    }

    // ── EMV ARQC (tag 9F26) — 8 bytes = 16 hex uppercase ────────────────────

    static String emvArqc() {
        byte[] b = new byte[8];
        SEC.nextBytes(b);
        StringBuilder sb = new StringBuilder(16);
        for (byte v : b) sb.append(String.format("%02X", v));
        return sb.toString();
    }

    // ── EMV ATC (tag 9F36) — 2 bytes = 4 hex ────────────────────────────────

    static String emvAtc(ThreadLocalRandom rng) {
        return String.format("%04X", rng.nextInt(1, 10000));
    }

    // ── EMV IAD (tag 9F10) — starts with 0A, 11 bytes = 22 hex ──────────────

    static String emvIad(ThreadLocalRandom rng) {
        int dki = rng.nextInt(1, 4);
        int[] cvnPool = {0x10, 0x1A, 0x22};
        int cvn = cvnPool[rng.nextInt(cvnPool.length)];
        int cvr = rng.nextInt(0, 65536);
        long add = rng.nextLong(0, 4294967296L);
        int pad = rng.nextInt(0, 65536);
        return String.format("0A%02X%02X%04X%08X%04X", dki, cvn, cvr, add, pad);
    }

    // ── ISO 8583 Auth Request (MTI 0100) ─────────────────────────────────────

    static String iso8583AuthReq(ThreadLocalRandom rng, String locale) {
        String pan = FinancialGen.cardnum(rng, locale);
        int currYy = java.time.LocalDate.now().getYear() % 100;
        String exp = String.format("%02d%02d", currYy + rng.nextInt(1, 6), rng.nextInt(1, 13));
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String trace = String.format("%06d", rng.nextInt(1, 1000000));
        String rrn = "MOCKJ" + hexRng(rng, 7);
        String tid = "MOCKJT" + rng.nextInt(10, 100);
        String mid = "MOCKJM" + rng.nextInt(100000000, 1000000000);
        String ccy = CURRENCY.getOrDefault(locale, CURRENCY.get("TR"))[0];
        int amount = rng.nextInt(100, 10000000);

        String mmddHHmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmmss"));
        String hhmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        String mmdd = now.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));

        return String.join("\n",
            "MTI:0100",
            "BITMAP:" + BITMAP_AUTH_REQ,
            "DE002:" + pan,
            "DE003:000000",
            "DE004:" + String.format("%012d", amount),
            "DE007:" + mmddHHmmss,
            "DE011:" + trace,
            "DE012:" + hhmmss,
            "DE013:" + mmdd,
            "DE014:" + exp,
            "DE018:" + MCC_POOL[rng.nextInt(MCC_POOL.length)],
            "DE022:" + ENTRY_MODES[rng.nextInt(ENTRY_MODES.length)],
            "DE025:00",
            "DE037:" + rrn,
            "DE041:" + tid,
            "DE042:" + mid,
            "DE049:" + ccy
        );
    }

    // ── ISO 8583 Auth Response (MTI 0110) ────────────────────────────────────

    static String iso8583AuthResp(ThreadLocalRandom rng, String locale) {
        String pan = FinancialGen.cardnum(rng, locale);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String trace = String.format("%06d", rng.nextInt(1, 1000000));
        String authCode = "MOCKJ" + rng.nextInt(1, 10);
        String respCode = RESPONSE_CODES[rng.nextInt(RESPONSE_CODES.length)];
        String tid = "MOCKJT" + rng.nextInt(10, 100);
        String mid = "MOCKJM" + rng.nextInt(100000000, 1000000000);
        int amount = rng.nextInt(100, 10000000);

        String mmddHHmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmmss"));
        String hhmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        String mmdd = now.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));

        return String.join("\n",
            "MTI:0110",
            "BITMAP:" + BITMAP_AUTH_RESP,
            "DE002:" + pan,
            "DE003:000000",
            "DE004:" + String.format("%012d", amount),
            "DE007:" + mmddHHmmss,
            "DE011:" + trace,
            "DE012:" + hhmmss,
            "DE013:" + mmdd,
            "DE038:" + authCode,
            "DE039:" + respCode,
            "DE041:" + tid,
            "DE042:" + mid
        );
    }

    // ── ISO 8583 Reversal (MTI 0400) ─────────────────────────────────────────

    static String iso8583Reversal(ThreadLocalRandom rng, String locale) {
        String pan = FinancialGen.cardnum(rng, locale);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String trace = String.format("%06d", rng.nextInt(1, 1000000));
        String rrn = "MOCKJ" + hexRng(rng, 7);
        String tid = "MOCKJT" + rng.nextInt(10, 100);
        String mid = "MOCKJM" + rng.nextInt(100000000, 1000000000);
        String ccy = CURRENCY.getOrDefault(locale, CURRENCY.get("TR"))[0];
        int amount = rng.nextInt(100, 10000000);
        String origTrace = String.format("%06d", rng.nextInt(1, 1000000));
        String acqId = "MOCKJ" + String.format("%06d", rng.nextInt(100000, 1000000));

        String mmddHHmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmmss"));
        String hhmmss = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        String mmdd = now.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));
        String de056 = "0100" + origTrace + mmddHHmmss + acqId;

        return String.join("\n",
            "MTI:0400",
            "BITMAP:" + BITMAP_REVERSAL,
            "DE002:" + pan,
            "DE003:000000",
            "DE004:" + String.format("%012d", amount),
            "DE007:" + mmddHHmmss,
            "DE011:" + trace,
            "DE012:" + hhmmss,
            "DE013:" + mmdd,
            "DE037:" + rrn,
            "DE041:" + tid,
            "DE042:" + mid,
            "DE049:" + ccy,
            "DE056:" + de056
        );
    }

    // ── ATM Session JSON ──────────────────────────────────────────────────────

    static String atmSession(ThreadLocalRandom rng, String locale) {
        String[] ccy = CURRENCY.getOrDefault(locale, CURRENCY.get("TR"));
        String currName = ccy[1];
        String pan = FinancialGen.cardnum(rng, locale);
        String maskedPan = pan.substring(0, 4) + " **** **** " + pan.substring(pan.length() - 4);
        int currYy = java.time.LocalDate.now().getYear() % 100;
        int expYy = currYy + rng.nextInt(1, 6);
        int expMm = rng.nextInt(1, 13);
        double amount = rng.nextInt(2000, 1000001) / 100.0;
        String trace = String.format("%06d", rng.nextInt(1, 1000000));
        String sessionId = "MOCKJ-ATM-" + randomHexUpper(rng, 8);
        String tid = "MOCKJT" + rng.nextInt(10, 100);
        String authCode = "MOCKJ" + rng.nextInt(1, 10);
        String[] txTypes = {"CASH_WITHDRAWAL", "BALANCE_INQUIRY", "MINI_STATEMENT"};
        String ts = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return "{\"session_id\": \"" + sessionId + "\", \"terminal_id\": \"" + tid + "\"," +
            " \"terminal_location\": \"MOCKJ Bank Branch " + rng.nextInt(1, 100) + "\"," +
            " \"card_scheme\": \"VISA\", \"masked_pan\": \"" + maskedPan + "\"," +
            " \"expiry\": \"" + String.format("%02d/%02d", expMm, expYy) + "\"," +
            " \"transaction_type\": \"" + txTypes[rng.nextInt(txTypes.length)] + "\"," +
            " \"amount\": \"" + String.format(java.util.Locale.US, "%.2f", amount) + "\"," +
            " \"currency\": \"" + currName + "\", \"response_code\": \"00\"," +
            " \"response_message\": \"APPROVED\", \"auth_code\": \"" + authCode + "\"," +
            " \"atc\": \"" + emvAtc(rng) + "\", \"arqc\": \"" + emvArqc() + "\"," +
            " \"stan\": \"" + trace + "\", \"timestamp\": \"" + ts + "\"}";
    }

    // ── POS Receipt (40-char width text) ─────────────────────────────────────

    static String posReceipt(ThreadLocalRandom rng, String locale) {
        String currName = CURRENCY.getOrDefault(locale, CURRENCY.get("TR"))[1];
        String pan = FinancialGen.cardnum(rng, locale);
        String maskedPan = "**** **** **** " + pan.substring(pan.length() - 4);
        int currYy = java.time.LocalDate.now().getYear() % 100;
        int expYy = currYy + rng.nextInt(1, 6);
        int expMm = rng.nextInt(1, 13);
        double amount = rng.nextInt(100, 100000) / 100.0;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String tid = "MOCKJT" + rng.nextInt(10, 100);
        String mid = "MOCKJM" + rng.nextInt(100000000, 1000000000);
        String authCode = "MOCKJ" + rng.nextInt(1, 10);
        String aid = AID_POOL[rng.nextInt(AID_POOL.length)];
        String entryCode = ENTRY_MODES[rng.nextInt(ENTRY_MODES.length)];
        String entry = switch (entryCode) {
            case "051" -> "CHIP/CONTACT";
            case "071" -> "CHIP/CONTACTLESS";
            default -> "MAGNETIC";
        };

        int w = RECEIPT_WIDTH;
        String sep = "=".repeat(w);
        String dsh = "-".repeat(w);

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add(sep);
        lines.add(centerText("MOCKJ MERCHANT SERVICES", w));
        lines.add(sep);
        lines.add("Date: " + now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
            "  Time: " + now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        lines.add("MID : " + mid);
        lines.add("TID : " + tid);
        lines.add(dsh);
        lines.add("SALE");
        lines.add("Card  : " + maskedPan);
        lines.add(String.format("Expiry: %02d/%02d", expYy, expMm));
        lines.add("Mode  : " + entry);
        lines.add("AID   : " + aid);
        lines.add(dsh);
        String amtStr = String.format(java.util.Locale.US, "%s %10.2f", currName, amount);
        lines.add("Amount: " + amtStr);
        lines.add(dsh);
        lines.add("Auth  : " + authCode);
        lines.add("Result: APPROVED");
        lines.add(dsh);
        lines.add(centerText("*** TEST TRANSACTION ***", w));
        lines.add(centerText("*** MOCKJ TEST DATA ***", w));
        lines.add(sep);

        return String.join("\n", lines);
    }

    private static String centerText(String s, int width) {
        int pad = width - s.length();
        if (pad <= 0) return s;
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String hexRng(ThreadLocalRandom rng, int chars) {
        StringBuilder sb = new StringBuilder(chars);
        String hex = "0123456789ABCDEF";
        for (int i = 0; i < chars; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static String randomHexUpper(ThreadLocalRandom rng, int chars) {
        return hexRng(rng, chars);
    }
}
