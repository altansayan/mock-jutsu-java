package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Hardware — Track1/2, chip_data (locale-aware), pin_block ISO 9564. Mirrors hardware.py. */
public final class HardwareGen {
    private HardwareGen() {}

    // ISO 4217 BCD currency codes per locale (tag 5F2A in chip TLV)
    private static final java.util.Map<String,String> CURRENCY_TLV = java.util.Map.of(
        "TR", "0949", "DE", "0978", "FR", "0978", "UK", "0826", "US", "0840", "RU", "0643"
    );

    // Track1 surname pool (MOCKJ prefix, uppercase, no spaces per ISO 7813)
    private static final String[] TRACK1_SURNAMES = {
        "MOCKJDOE","MOCKJROE","MOCKJKIM","MOCKJLEE","MOCKJRAY"
    };
    private static final String[] TRACK1_GIVEN = {
        "MOCKJJOHN","MOCKJJANE","MOCKJBOB","MOCKJANN","MOCKJMAX"
    };

    // Service codes: position 1 = 1 (international) or 2 (national only)
    //                position 2 = 0 (normal)
    //                position 3 = 1 (no restriction) or 6 (PIN required)
    private static final String[] SERVICE_CODES = {"101","106","201","206"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "track1_data"    -> track1(rng, locale);
            case "track2_data"    -> track2(rng, locale);
            case "chip_data"      -> chipData(rng, locale);
            case "pin_block"      -> pinBlockFmt0(rng);
            case "pin_block_fmt3" -> pinBlockFmt3(rng);
            default -> "ERROR: Unknown hardware type '" + type + "'";
        };
    }

    // ── Track 1 — ISO/IEC 7813 ───────────────────────────────────────────────
    // Format: %B{PAN}^{SURNAME}/{GIVEN}^{YYMM}{SC}{000}?
    // SC: 3 digits matching service_codes pool

    private static String track1(ThreadLocalRandom rng, String locale) {
        String pan  = FinancialGen.cardnum(rng, locale);
        String name = pick(rng, TRACK1_SURNAMES) + "/" + pick(rng, TRACK1_GIVEN);
        int yy = (java.time.LocalDate.now().getYear() % 100) + rng.nextInt(1, 6);
        int mm = rng.nextInt(1, 13);
        String sc = pick(rng, SERVICE_CODES);
        // discretionary = 3 random digits
        String disc = String.format("%03d", rng.nextInt(1000));
        return String.format("%%B%s^%s^%02d%02d%s%s?", pan, name, yy, mm, sc, disc);
    }

    // ── Track 2 — ISO/IEC 7813 ───────────────────────────────────────────────
    // Format: ;{PAN}={YYMM}{SC}{disc}?
    // disc: 3-5 random digits

    private static String track2(ThreadLocalRandom rng, String locale) {
        String pan = FinancialGen.cardnum(rng, locale);
        int yy = (java.time.LocalDate.now().getYear() % 100) + rng.nextInt(1, 11);
        int mm = rng.nextInt(1, 13);
        String sc = pick(rng, SERVICE_CODES);
        int discLen = 3 + rng.nextInt(3); // 3-5
        StringBuilder disc = new StringBuilder();
        for (int i = 0; i < discLen; i++) disc.append(rng.nextInt(10));
        return String.format(";%s=%02d%02d%s%s?", pan, yy, mm, sc, disc);
    }

    // ── EMV Chip data TLV — 6 tags matching hardware.py ──────────────────────
    // Tags: 9F02 (amount), 9F03 (other amount), 9505 (TVR), 5F2A (currency), 9A (date), 9C (tx type)

    private static String chipData(ThreadLocalRandom rng, String locale) {
        String ccy = CURRENCY_TLV.getOrDefault(locale, "0949");

        // 9F02: amount authorised 6 bytes BCD (12 digits, padded)
        String tag9F02 = String.format("9F0206%012d", rng.nextLong(0, 100000000000L));

        // 9F03: other amount 6 bytes BCD
        String tag9F03 = String.format("9F0306%012d", rng.nextLong(0, 100000000000L));

        // 9505: Terminal Verification Results 5 bytes
        String tag9505 = String.format("9505%010X", rng.nextLong(0, 10000000000L));

        // 5F2A: currency code 2 bytes
        String tag5F2A = "5F2A02" + ccy;

        // 9A: transaction date YYMMDD 3 bytes
        java.time.LocalDate today = java.time.LocalDate.now();
        String tag9A = String.format("9A03%02d%02d%02d",
            today.getYear() % 100, today.getMonthValue(), today.getDayOfMonth());

        // 9C: transaction type 1 byte (fixed: 00 = purchase)
        String tag9C = "9C0100";

        return tag9F02 + tag9F03 + tag9505 + tag5F2A + tag9A + tag9C;
    }

    // ── PIN Block Format 0 — ISO 9564-1 ──────────────────────────────────────

    static String pinBlockFmt0(ThreadLocalRandom rng) {
        int pinLen = rng.nextInt(4, 7);
        int[] nibbles = new int[16];
        nibbles[0] = 0;
        nibbles[1] = pinLen;
        for (int i = 0; i < pinLen; i++) nibbles[2 + i] = rng.nextInt(0, 10);
        for (int i = 2 + pinLen; i < 16; i++) nibbles[i] = 0xF;
        StringBuilder sb = new StringBuilder(16);
        for (int n : nibbles) sb.append(String.format("%X", n));
        return sb.toString();
    }

    // ── PIN Block Format 3 — ISO 9564-1 ──────────────────────────────────────

    static String pinBlockFmt3(ThreadLocalRandom rng) {
        int pinLen = rng.nextInt(4, 7);
        int[] nibbles = new int[16];
        nibbles[0] = 3;
        nibbles[1] = pinLen;
        for (int i = 0; i < pinLen; i++) nibbles[2 + i] = rng.nextInt(0, 10);
        for (int i = 2 + pinLen; i < 16; i++) nibbles[i] = rng.nextInt(0, 10);
        StringBuilder sb = new StringBuilder(16);
        for (int n : nibbles) sb.append(String.format("%X", n));
        return sb.toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) { return arr[rng.nextInt(arr.length)]; }
}
