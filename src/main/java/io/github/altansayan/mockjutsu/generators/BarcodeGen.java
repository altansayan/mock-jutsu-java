package io.github.altansayan.mockjutsu.generators;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/** Barcode — EAN-13, EAN-8, UPC-A, ISBN-13, ISBN-10, GS1-128. Mirrors barcode.py. */
public final class BarcodeGen {

    private BarcodeGen() {}

    // GS1 Country/Region Prefixes — public list at gs1.org/standards/id-keys/prefix
    private static final java.util.Map<String, int[]> GS1_PREFIXES = java.util.Map.of(
        "TR", new int[]{868, 869},
        "US", new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13},
        "UK", new int[]{500,501,502,503,504,505,509},
        "DE", new int[]{400,401,410,420,430,440},
        "FR", new int[]{300,310,320,330,340,350,360,370},
        "RU", new int[]{460,461,462,463,464,465,469}
    );
    private static final int[] GS1_PREFIXES_DEFAULT = {0, 300, 400, 500, 868, 460};

    private static final java.util.Map<String, int[]> EAN8_PREFIXES = java.util.Map.of(
        "TR", new int[]{868, 869},
        "US", new int[]{0,1,2,3},
        "UK", new int[]{500,501,502},
        "DE", new int[]{400,401,402},
        "FR", new int[]{300,301,302},
        "RU", new int[]{460,461,462}
    );

    private static final int[] UPCA_SYSTEMS = {0,0,0,0,1,3,6,7,8};
    private static final int[] ISBN10_GROUPS = {0,1,2,3,4,5,7};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "ean13"   -> ean13(rng, locale);
            case "ean8"    -> ean8(rng, locale);
            case "upca"    -> upcA(rng);
            case "isbn13"  -> isbn13(rng);
            case "isbn10"  -> isbn10(rng);
            case "gs1_128" -> gs1128(rng);
            default        -> "ERROR: Unknown barcode type '" + type + "'";
        };
    }

    // EAN-13: 3-digit GS1 prefix + 9 random digits + MOD-10 check
    static String ean13(ThreadLocalRandom rng, String locale) {
        int[] prefixes = GS1_PREFIXES.getOrDefault(locale.toUpperCase(java.util.Locale.ROOT), GS1_PREFIXES_DEFAULT);
        int prefix = prefixes[rng.nextInt(prefixes.length)];
        String prefixStr = String.format("%03d", prefix);
        int[] d = new int[13];
        for (int i = 0; i < 3; i++) d[i] = prefixStr.charAt(i) - '0';
        for (int i = 3; i < 12; i++) d[i] = rng.nextInt(0, 10);
        d[12] = ean13Check(d, 12);
        return digitsToString(d);
    }

    // EAN-8: 3-digit GS1 prefix + 4 random digits + MOD-10 check
    static String ean8(ThreadLocalRandom rng, String locale) {
        int[] prefixes = EAN8_PREFIXES.getOrDefault(locale.toUpperCase(java.util.Locale.ROOT), GS1_PREFIXES_DEFAULT);
        int prefix = prefixes[rng.nextInt(prefixes.length)];
        String prefixStr = String.format("%03d", prefix);
        int[] d = new int[8];
        for (int i = 0; i < 3; i++) d[i] = prefixStr.charAt(i) - '0';
        for (int i = 3; i < 7; i++) d[i] = rng.nextInt(0, 10);
        d[7] = ean13Check(d, 7);
        return digitsToString(d);
    }

    // UPC-A — weighted system digit + 10 random digits + MOD-10 check
    static String upcA(ThreadLocalRandom rng) {
        int[] d = new int[12];
        d[0] = UPCA_SYSTEMS[rng.nextInt(UPCA_SYSTEMS.length)];
        for (int i = 1; i < 11; i++) d[i] = rng.nextInt(0, 10);
        d[11] = ean13Check(d, 11);
        return digitsToString(d);
    }

    // ISBN-13 = EAN-13 with prefix 978 or 979
    private static String isbn13(ThreadLocalRandom rng) {
        int[] d = new int[13];
        int[] prefix = rng.nextBoolean() ? new int[]{9,7,8} : new int[]{9,7,9};
        System.arraycopy(prefix, 0, d, 0, 3);
        for (int i = 3; i < 12; i++) d[i] = rng.nextInt(0, 10);
        d[12] = ean13Check(d, 12);
        return digitsToString(d);
    }

    // ISBN-10: 9 digits + check (mod 11, X=10)
    private static String isbn10(ThreadLocalRandom rng) {
        int[] d = new int[9];
        d[0] = ISBN10_GROUPS[rng.nextInt(ISBN10_GROUPS.length)];
        for (int i = 1; i < 9; i++) d[i] = rng.nextInt(0, 10);
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += d[i] * (10 - i);
        int check = (11 - (sum % 11)) % 11;
        StringBuilder sb = new StringBuilder();
        for (int v : d) sb.append(v);
        sb.append(check == 10 ? "X" : String.valueOf(check));
        return sb.toString();
    }

    // GS1-128: AI(01) GTIN-14, AI(17) expiry YYMMDD, AI(10) lot — GS1 v24.0 §5.4
    private static final String GS1_LOT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static String gs1128(ThreadLocalRandom rng) {
        // GTIN-14: indicator(1) + company(7) + item_ref(5) + check(1)
        int indicator = rng.nextInt(9);
        int[] payload = new int[13];
        payload[0] = indicator;
        for (int i = 1; i < 13; i++) payload[i] = rng.nextInt(0, 10);
        StringBuilder gtin14 = new StringBuilder(14);
        for (int v : payload) gtin14.append(v);
        gtin14.append(ean13Check(payload, 13));

        // AI(17): expiry YYMMDD — future date within 730 days
        LocalDate expiry = LocalDate.now().plusDays(rng.nextInt(730));
        String expiryStr = String.format("%02d%02d%02d",
            expiry.getYear() % 100, expiry.getMonthValue(), expiry.getDayOfMonth());

        // AI(10): 6-char alphanumeric lot
        StringBuilder lot = new StringBuilder(6);
        for (int i = 0; i < 6; i++) lot.append(GS1_LOT_CHARS.charAt(rng.nextInt(GS1_LOT_CHARS.length())));

        return "(01)" + gtin14 + "(17)" + expiryStr + "(10)" + lot;
    }

    // ── EAN check digit algorithm ─────────────────────────────────────────────

    private static int ean13Check(int[] d, int len) {
        // GS1 MOD-10: weight alternates 3,1 starting from the digit closest to the
        // check digit (rightmost), regardless of payload length parity.
        int total = 0;
        for (int i = 0; i < len; i++) {
            int posFromRight = len - 1 - i;
            int weight = (posFromRight % 2 == 0) ? 3 : 1;
            total += d[i] * weight;
        }
        return (10 - (total % 10)) % 10;
    }

    private static String digitsToString(int[] d) {
        StringBuilder sb = new StringBuilder(d.length);
        for (int v : d) sb.append(v);
        return sb.toString();
    }
}
