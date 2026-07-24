package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** MRZ — TD3 (passport) and TD1 (ID card) with correct check digits. Mirrors mrz.py. */
public final class MrzGen {
    private MrzGen() {}

    private static final int[] MRZ_VALUES = new int[128];
    static {
        for (char c = '0'; c <= '9'; c++) MRZ_VALUES[c] = c - '0';
        for (char c = 'A'; c <= 'Z'; c++) MRZ_VALUES[c] = c - 'A' + 10;
        MRZ_VALUES['<'] = 0;
    }
    private static final int[] WEIGHTS = {7, 3, 1};

    private static final String[] MRZ_SURNAMES = {
        "SMITH","JOHNSON","WILLIAMS","BROWN","JONES","GARCIA","MILLER",
        "DAVIS","WILSON","TAYLOR","YILMAZ","DEMIR","KAYA","SAHIN","CELIK",
        "MUELLER","SCHMIDT","FISCHER","WEBER","MARTIN","BERNARD","THOMAS",
        "IVANOV","SMIRNOV","KUZNETSOV","TANAKA","YAMAMOTO","NAKAMURA",
        "WANG","LI","ZHANG","ROSSI","FERRARI","ESPOSITO"
    };
    private static final String[] MRZ_GIVEN_NAMES = {
        "JOHN","JAMES","ROBERT","MICHAEL","WILLIAM","DAVID","RICHARD",
        "JOSEPH","THOMAS","CHARLES","MARY","PATRICIA","JENNIFER","LINDA",
        "BARBARA","ELIZABETH","SUSAN","JESSICA","SARAH","KAREN",
        "AHMET","MEHMET","MUSTAFA","ALI","AYSE","FATMA","ZEYNEP",
        "HANS","PETRA","ANNA","IVAN","OLGA","YUKI","HIROSHI"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "mrz_td3" -> td3(rng, locale);
            case "mrz_td1" -> td1(rng, locale);
            default -> "ERROR: Unknown MRZ type '" + type + "'";
        };
    }

    // ── TD3 — 2 lines × 44 chars (passport) ──────────────────────────────────

    static String td3(ThreadLocalRandom rng, String locale) {
        String cc     = localeToCC(locale);
        String fn     = IdentityGen.firstname(rng, locale, "").toUpperCase().replaceAll("[^A-Z]","");
        String ln     = IdentityGen.lastname(rng, locale, "").toUpperCase().replaceAll("[^A-Z]","");
        String number = randomDocNumber(rng);
        String dob    = randomDate(rng, true);
        String expiry = randomDate(rng, false);
        char   sex    = rng.nextBoolean() ? 'M' : 'F';

        // Line 1: P<CCLN<<FN (padded to 44) — ICAO 9303: no separator between
        // the 3-letter country code and the surname, which begins immediately after.
        String line1raw = "P<" + cc + ln + "<<" + fn;
        String line1    = pad(line1raw, 44);

        // Line 2: ICAO Doc 9303 TD3 — 44 chars exactly
        // number(9)+check(1)+cc(3)+dob(6)+check(1)+sex(1)+expiry(6)+check(1)+optional(14)+optionalChk(1)+composite(1)
        String numChk     = String.valueOf(mrzCheck(number));
        String dobChk     = String.valueOf(mrzCheck(dob));
        String expiryChk  = String.valueOf(mrzCheck(expiry));
        String personal   = pad("<", 14);
        String personalChk = String.valueOf(mrzCheck(personal));  // ICAO 9303: all-'<' → 0
        String composite  = number + numChk + dob + dobChk + expiry + expiryChk + personal + personalChk;
        String compChk    = String.valueOf(mrzCheck(composite));
        String line2      = number + numChk + cc + dob + dobChk + sex + expiry + expiryChk + personal + personalChk + compChk;

        String lines = line1 + " | " + line2;
        return mrzJson("TD3", lines, fn, ln, cc, number, dob, String.valueOf(sex), expiry);
    }

    // ── TD1 — 3 lines × 30 chars (ID card) ───────────────────────────────────

    static String td1(ThreadLocalRandom rng, String locale) {
        String surname = MRZ_SURNAMES[rng.nextInt(MRZ_SURNAMES.length)];
        String given = MRZ_GIVEN_NAMES[rng.nextInt(MRZ_GIVEN_NAMES.length)];
        String country = localeToCC(locale);
        String nationality = country;

        char[] docTypes = {'I', 'A', 'C'};
        char docTypeChar = docTypes[rng.nextInt(docTypes.length)];

        String docNo = randomDocNumber(rng);
        String cd1 = String.valueOf(mrzCheck(docNo));
        String opt1 = "<".repeat(15);

        String line1 = docTypeChar + "<" + country + docNo + cd1 + opt1; // 1+1+3+9+1+15 = 30

        String dob = randomDate(rng, true);
        String cdDob = String.valueOf(mrzCheck(dob));
        char[] sexes = {'M', 'F', '<'};
        char sex = sexes[rng.nextInt(sexes.length)];
        String expiry = randomDate(rng, false);
        String cdExp = String.valueOf(mrzCheck(expiry));
        String opt2 = "<".repeat(11);

        // ICAO 9303-5: composite = line1[5:30] + dob+cd + expiry+cd + opt2 (sex, nationality excluded)
        String compositeInput = line1.substring(5, 30) + dob + cdDob + expiry + cdExp + opt2;
        String cdComp = String.valueOf(mrzCheck(compositeInput));

        String line2 = dob + cdDob + sex + expiry + cdExp + nationality + opt2 + cdComp; // 6+1+1+6+1+3+11+1 = 30

        String line3 = pad(surname + "<<" + given, 30);

        String lines = line1 + " | " + line2 + " | " + line3;
        String natDisplay = nationality.replaceAll("<+$", "");
        return mrzJson("TD1", lines, given, surname, natDisplay, docNo, dob, String.valueOf(sex), expiry);
    }

    private static String mrzJson(String type, String lines, String fn, String ln,
                                   String nat, String docNum, String dob, String sex, String expiry) {
        return "{\"mrz_type\":\"" + type + "\",\"lines\":\"" + lines + "\"," +
               "\"surname\":\"" + ln + "\",\"given_names\":\"" + fn + "\"," +
               "\"nationality\":\"" + nat + "\",\"doc_number\":\"" + docNum + "\"," +
               "\"dob\":\"" + dob + "\",\"sex\":\"" + sex + "\",\"expiry\":\"" + expiry + "\"}";
    }

    // ── MRZ check digit ───────────────────────────────────────────────────────

    static int mrzCheck(String field) {
        int sum = 0;
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            int v = (c >= 0 && c < MRZ_VALUES.length) ? MRZ_VALUES[c] : 0;
            sum += v * WEIGHTS[i % 3];
        }
        return sum % 10;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // YYMMDD: past (DOB) is either 1940-1999 or 2000-2009 (50/50); future (expiry) is 2026-2035.
    private static String randomDate(ThreadLocalRandom rng, boolean past) {
        String yy;
        if (past) {
            yy = rng.nextBoolean() ? String.valueOf(rng.nextInt(40, 100)) : "0" + rng.nextInt(10);
        } else {
            yy = String.valueOf(rng.nextInt(26, 36));
        }
        String mm = String.format("%02d", rng.nextInt(1, 13));
        String dd = String.format("%02d", rng.nextInt(1, 29));
        return yy + mm + dd;
    }

    private static String pad(String s, int len) {
        if (s.length() >= len) return s.substring(0, len);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append('<');
        return sb.toString();
    }

    private static String localeToCC(String locale) {
        return switch (locale) {
            case "TR" -> "TUR"; case "US" -> "USA"; case "DE" -> "D<<";
            case "FR" -> "FRA"; case "UK" -> "GBR"; case "RU" -> "RUS";
            default   -> "TUR";
        };
    }

    // 2 random uppercase letters + 7 random digits (ICAO doc number convention used by mrz.py)
    private static String randomDocNumber(ThreadLocalRandom rng) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 2; i++) sb.append(letters.charAt(rng.nextInt(letters.length())));
        for (int i = 0; i < 7; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }
}
