package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Aviation & Maritime — IATA ETN, IMO number, GDS PNR code. Mirrors aviation.py. */
public final class AviationGen {
    private AviationGen() {}

    private static final int[] AIRLINE_CODES = {
        1, 4, 5, 6, 8, 9, 11, 14, 16, 20, 21, 23, 25, 26, 29, 30,
        31, 34, 36, 37, 39, 41, 42, 43, 45, 47, 48, 49, 52, 53, 55,
        57, 63, 65, 66, 67, 70, 74, 76, 80, 82, 83, 85, 86, 87, 88,
        90, 91, 95, 98, 100, 101, 105, 106, 107, 108, 109, 110, 112,
        114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125,
        127, 128, 129, 130, 131, 132, 133, 135, 139, 141, 142, 146,
        147, 148, 150, 153, 155, 157, 158, 160, 161, 162, 163, 164,
        165, 167, 168, 169, 170, 172, 174, 176, 178, 179, 180, 181,
        183, 185, 186, 187, 188, 189, 190, 191, 195, 196, 197, 198,
        200, 201, 202, 203, 205, 206, 207, 210, 212, 213, 214, 217,
        220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 232, 235,
        239, 240, 243, 245, 247, 248, 250, 251, 257, 260, 262, 263
    };

    // PNR character set: A-Z + 2-9, excludes 0, 1, I, O (GDS visual clarity rule)
    private static final String PNR_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int[] IMO_WEIGHTS = {7, 6, 5, 4, 3, 2};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "iata_ticket"  -> iataTicket(rng);
            case "imo_number"   -> imoNumber(rng);
            case "pnr_code"     -> pnr(rng);
            default -> "ERROR: Unknown aviation type '" + type + "'";
        };
    }

    private static String iataTicket(ThreadLocalRandom rng) {
        int airline = AIRLINE_CODES[rng.nextInt(AIRLINE_CODES.length)];
        long serial = rng.nextLong(1, 1_000_000_000L);
        int check = (int) (serial % 7);
        return String.format("%03d%09d%d", airline, serial, check);
    }

    private static String imoNumber(ThreadLocalRandom rng) {
        int[] d = new int[6];
        d[0] = rng.nextInt(1, 10);
        for (int i = 1; i < 6; i++) d[i] = rng.nextInt(0, 10);
        int total = 0;
        for (int i = 0; i < 6; i++) total += d[i] * IMO_WEIGHTS[i];
        int check = total % 10;
        StringBuilder sb = new StringBuilder("IMO ");
        for (int v : d) sb.append(v);
        sb.append(check);
        return sb.toString();
    }

    private static String pnr(ThreadLocalRandom rng) {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) sb.append(PNR_CHARS.charAt(rng.nextInt(PNR_CHARS.length())));
        return sb.toString();
    }
}
