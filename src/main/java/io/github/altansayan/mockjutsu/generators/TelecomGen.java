package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Telecom — IMEI (Luhn), ICCID, IMSI, MSISDN, IMEI2. Mirrors telecom.py. */
public final class TelecomGen {

    private TelecomGen() {}

    // RBI (Reporting Body Identifier) codes — 3GPP TS 23.003 v17.5.0 Annex B
    private static final String[] RBI_CODES = {
        "00","01","10","13","20","30","35","40","44","53","54","55","86","91","98","99"
    };

    // MCC/MNC pairs — ITU-T E.212
    private static final java.util.Map<String, String[][]> MCC_MNC = java.util.Map.of(
        "TR", new String[][]{{"286","01"},{"286","02"},{"286","03"},{"286","04"}},
        "US", new String[][]{{"310","010"},{"310","260"},{"311","480"},{"310","030"}},
        "UK", new String[][]{{"234","10"},{"234","20"},{"234","30"},{"234","50"}},
        "DE", new String[][]{{"262","01"},{"262","02"},{"262","03"},{"262","07"}},
        "FR", new String[][]{{"208","01"},{"208","10"},{"208","20"},{"208","25"}},
        "RU", new String[][]{{"250","01"},{"250","02"},{"250","20"},{"250","99"}}
    );

    // ICCID issuer codes — ITU-T E.118 §3
    private static final java.util.Map<String, String> ICCID_CC = java.util.Map.of(
        "TR","90","US","1","UK","44","DE","49","FR","33","RU","7"
    );
    private static final java.util.Map<String, String[]> ICCID_ISSUERS = java.util.Map.of(
        "TR", new String[]{"0534","0542","0552"},
        "US", new String[]{"1234","4567","7890"},
        "UK", new String[]{"7900","7800","7700"},
        "DE", new String[]{"1511","1521","1601"},
        "FR", new String[]{"0600","0601","0602"},
        "RU", new String[]{"9101","9211","9261"}
    );

    // MSISDN / E.164 locale configuration: {country_code, prefixes[], trailing_digits}
    private static final java.util.Map<String, String> MSISDN_CC = java.util.Map.of(
        "TR","+90","US","+1","UK","+44","DE","+49","FR","+33","RU","+7"
    );
    private static final java.util.Map<String, String[]> MSISDN_PREFIXES = new java.util.HashMap<>() {{
        put("TR", new String[]{
            "505","506","507","508","509",
            "530","531","532","533","534","535","537","538","539",
            "540","541","542","543","544","545","546","547","548","549",
            "551","552","553","554","555","556","559","562"});
        put("US", new String[]{
            "201","202","203","212","213","310","312","404","407",
            "408","415","425","469","512","617","646","650","702",
            "714","718","773","818","917","949"});
        put("UK", new String[]{"7300","7400","7500","7600","7800","7900"});
        put("DE", new String[]{
            "160","162","163","170","171","172","173","174","175","176","177","178","179"});
        put("FR", new String[]{
            "601","602","603","610","611","612","613","614","615",
            "616","617","618","619","620","621","622","623","624","625"});
        put("RU", new String[]{
            "901","902","903","904","905","906","907","908","909",
            "910","911","912","913","914","915","916","917","918","919",
            "920","921","922","923","924","925","926","927","928","929"});
    }};
    private static final java.util.Map<String, Integer> MSISDN_RAND_LEN = java.util.Map.of(
        "TR", 7, "US", 7, "UK", 6, "DE", 7, "FR", 6, "RU", 7
    );

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "imei"   -> imei(rng);
            case "imei2"  -> imei2(rng);
            case "iccid"  -> iccid(rng, locale);
            case "imsi"   -> imsi(rng, locale);
            case "msisdn" -> msisdn(rng, locale);
            default       -> "ERROR: Unknown telecom type '" + type + "'";
        };
    }

    // ── IMEI — TAC(8) + SNR(6) + Luhn check(1) = 15 digits ───────────────────

    static String imei(ThreadLocalRandom rng) {
        String rbi = RBI_CODES[rng.nextInt(RBI_CODES.length)];
        String model = digits(rng, 6);
        String snr = digits(rng, 6);
        String payload = rbi + model + snr;
        int check = IdentityGen.luhnCheckDigit(payload);
        return payload + check;
    }

    private static String imei2(ThreadLocalRandom rng) {
        String raw = imei(rng);
        return raw.substring(0, 2) + "-" + raw.substring(2, 8) + "-" + raw.substring(8, 14) + "-" + raw.charAt(14);
    }

    // ── ICCID — 89 + CC + issuer(4) + serial + Luhn check = 19 digits ────────

    static String iccid(ThreadLocalRandom rng, String locale) {
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        String cc = ICCID_CC.getOrDefault(loc, ICCID_CC.get("TR"));
        String[] issuers = ICCID_ISSUERS.getOrDefault(loc, ICCID_ISSUERS.get("TR"));
        String issuer = issuers[rng.nextInt(issuers.length)];
        String prefix = "89" + cc + issuer;
        int serialLen = 18 - prefix.length();
        String serial = digits(rng, serialLen);
        String payload = prefix + serial;
        int check = IdentityGen.luhnCheckDigit(payload);
        return payload + check;
    }

    // ── IMSI — MCC(3) + MNC(2-3) + MSIN(variable) = 15 digits ────────────────

    static String imsi(ThreadLocalRandom rng, String locale) {
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        String[][] pairs = MCC_MNC.getOrDefault(loc, MCC_MNC.get("TR"));
        String[] pair = pairs[rng.nextInt(pairs.length)];
        String mcc = pair[0], mnc = pair[1];
        int msinLen = 15 - mcc.length() - mnc.length();
        return mcc + mnc + digits(rng, msinLen);
    }

    // ── MSISDN — E.164 format ─────────────────────────────────────────────────

    private static String msisdn(ThreadLocalRandom rng, String locale) {
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        String cc = MSISDN_CC.getOrDefault(loc, MSISDN_CC.get("TR"));
        String[] prefixes = MSISDN_PREFIXES.getOrDefault(loc, MSISDN_PREFIXES.get("TR"));
        int randLen = MSISDN_RAND_LEN.getOrDefault(loc, 7);
        String prefix = prefixes[rng.nextInt(prefixes.length)];
        String digits;
        if ("US".equals(loc)) {
            String exchange = rng.nextInt(2, 10) + digits(rng, 2);
            String line = digits(rng, 4);
            digits = exchange + line;
        } else {
            digits = digits(rng, randLen);
        }
        return cc + prefix + digits;
    }

    private static String digits(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }
}
