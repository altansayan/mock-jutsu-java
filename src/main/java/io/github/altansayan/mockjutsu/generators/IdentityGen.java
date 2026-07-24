package io.github.altansayan.mockjutsu.generators;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/** Identity generator — mirrors identity.py algorithms. */
public final class IdentityGen {

    private IdentityGen() {}

    // ── Name pools ───────────────────────────────────────────────────────────

    private static final String[] NATIONALITIES = {
        "Turkish","American","British","German","French","Russian",
        "Chinese","Indian","Brazilian","Japanese","Korean","Italian",
        "Spanish","Dutch","Polish","Swedish","Norwegian","Danish",
        "Finnish","Australian","Canadian","Mexican","Argentine",
        "South African","Egyptian","Nigerian","Saudi","Emirati",
        "Iranian","Pakistani","Ukrainian","Belgian","Swiss","Austrian",
        "Portuguese","Greek","Hungarian","Czech","Romanian","Bulgarian"
    };

    // ── NIN helpers ──────────────────────────────────────────────────────────

    private static final String NIN_FORBIDDEN_FIRST = "DFIQUV";
    private static final String NIN_FORBIDDEN_SECOND = "DFIOQUV";
    private static final String[] NIN_FORBIDDEN_PAIRS = {
        "BG","GB","KN","NK","NT","TN","ZZ"
    };
    private static final char[] NIN_ALLOWED_FIRST = ninAllowed(NIN_FORBIDDEN_FIRST);
    private static final char[] NIN_ALLOWED_SECOND = ninAllowed(NIN_FORBIDDEN_SECOND);

    private static char[] ninAllowed(String forbidden) {
        StringBuilder sb = new StringBuilder();
        for (char c = 'A'; c <= 'Z'; c++) if (forbidden.indexOf(c) < 0) sb.append(c);
        return sb.toString().toCharArray();
    }

    // ── Public API ───────────────────────────────────────────────────────────

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "tckn"         -> qualifier.isEmpty() ? tckn(rng) : qualifier + tckn(rng);
            case "tckn_masked"  -> tcknMasked(rng);
            case "ykn"          -> ykn(rng);
            case "taxid","vkn"  -> vkn(rng);
            case "nationalid"   -> nationalid(rng, locale);
            case "ssn"          -> ssn(rng);
            case "ssn_masked"   -> ssnMasked(rng);
            case "nin"          -> nin(rng);
            case "inn"          -> inn(rng);
            case "inn_individual" -> innIndividual(rng);
            case "snils"        -> snils(rng);
            case "sgk"          -> sgk(rng);
            case "mersis"       -> mersis(rng);
            case "ein"          -> ein(rng);
            case "utr"          -> utr(rng);
            case "crn"          -> crn(rng);
            case "paye"         -> paye(rng);
            case "ust_id","ustid" -> ustId(rng);
            case "hrb"          -> hrb(rng);
            case "rvn"          -> rvn(rng);
            case "siren"        -> siren(rng);
            case "siret"        -> siret(rng);
            case "tva"          -> tva(rng);
            case "ogrn"         -> ogrn(rng);
            case "kpp"          -> kpp(rng);
            case "employer_id"  -> employerId(rng, locale);
            case "insurance_id" -> insuranceId(rng, locale);
            case "firstname"    -> firstname(rng, locale, qualifier);
            case "lastname"     -> lastname(rng, locale, qualifier);
            case "fullname"     -> fullnameQ(rng, locale, qualifier);
            case "patronymic"   -> patronymic(rng, locale, qualifier);
            case "passport"     -> passport(rng, locale);
            case "license"      -> license(rng, locale);
            case "age"          -> ageRange(rng, qualifier);
            case "gender"       -> rng.nextBoolean() ? "Male" : "Female";
            case "birthdate"    -> birthdate(rng);
            case "nationality"  -> NATIONALITIES[rng.nextInt(NATIONALITIES.length)];
            case "vat_number"   -> vatNumber(rng, locale);
            default             -> "ERROR: Unknown identity type '" + type + "'";
        };
    }

    // ── TCKN — Turkish Citizen ID (11 digits) ────────────────────────────────

    private static final int[] TCKN_FIRST_DIGITS = {2, 4, 5, 6, 7, 8};

    static String tckn(ThreadLocalRandom rng) {
        int[] d = new int[11];
        d[0] = TCKN_FIRST_DIGITS[rng.nextInt(TCKN_FIRST_DIGITS.length)];
        for (int i = 1; i < 9; i++) d[i] = rng.nextInt(0, 10);

        int odd  = d[0] + d[2] + d[4] + d[6] + d[8];
        int even = d[1] + d[3] + d[5] + d[7];
        d[9] = ((7 * odd) - even + 100) % 10;
        d[10] = 0;
        for (int i = 0; i < 10; i++) d[10] = (d[10] + d[i]) % 10;

        StringBuilder sb = new StringBuilder(11);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    // ── TCKN masked — KVKK: first 2 + last 2 visible, middle 7 masked ─────────

    private static String tcknMasked(ThreadLocalRandom rng) {
        int d1 = rng.nextInt(1, 10);
        int d2 = rng.nextInt(0, 10);
        int d10 = rng.nextInt(0, 10);
        int d11 = rng.nextInt(0, 10);
        return String.format("%d%d*******%d%d", d1, d2, d10, d11);
    }

    // ── YKN — Foreign Resident ID (11 digits, starts with 99, Luhn check) ────

    private static String ykn(ThreadLocalRandom rng) {
        StringBuilder base = new StringBuilder("99");
        for (int i = 0; i < 8; i++) base.append(rng.nextInt(0, 10));
        return base.toString() + luhnCheckDigit(base.toString());
    }

    // ── VKN — Turkish Tax Number (10 digits) ─────────────────────────────────

    static String vkn(ThreadLocalRandom rng) {
        int[] d = new int[10];
        d[0] = rng.nextInt(1, 10);
        for (int i = 1; i < 9; i++) d[i] = rng.nextInt(0, 10);
        // VKN check digit algorithm (Turkish Tax ID):
        // for i=0..8: v = (d[i] + (9-i)) % 10
        //             if v != 0: c = (v * 2^(9-i)) % 9; if c == 0: c = 9
        //             else: c = 0
        // check = (10 - sum(c) % 10) % 10
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int v = (d[i] + (9 - i)) % 10;
            int c;
            if (v != 0) {
                int pow = 1;
                for (int p = 0; p < (9 - i); p++) pow *= 2;
                c = (v * pow) % 9;
                if (c == 0) c = 9;
            } else {
                c = 0;
            }
            sum += c;
        }
        d[9] = (10 - (sum % 10)) % 10;
        StringBuilder sb = new StringBuilder(10);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    // ── SSN — US Social Security Number ──────────────────────────────────────

    static String ssn(ThreadLocalRandom rng) {
        int area;
        do { area = rng.nextInt(1, 900); } while (area == 666);
        int group  = rng.nextInt(1, 100);
        int serial = rng.nextInt(1, 10000);
        return String.format("%03d-%02d-%04d", area, group, serial);
    }

    private static String ssnMasked(ThreadLocalRandom rng) {
        int serial = rng.nextInt(0, 10000);
        return String.format("***-**-%04d", serial);
    }

    // ── NIN — UK National Insurance Number ───────────────────────────────────

    static String nin(ThreadLocalRandom rng) {
        char p1, p2;
        String pair;
        do {
            p1 = NIN_ALLOWED_FIRST[rng.nextInt(NIN_ALLOWED_FIRST.length)];
            p2 = NIN_ALLOWED_SECOND[rng.nextInt(NIN_ALLOWED_SECOND.length)];
            pair = "" + p1 + p2;
        } while (isForbiddenNinPair(pair));

        String digits = String.format("%d%d %d%d %d%d",
            rng.nextInt(0, 10), rng.nextInt(0, 10), rng.nextInt(0, 10),
            rng.nextInt(0, 10), rng.nextInt(0, 10), rng.nextInt(0, 10));
        char suffix = "ABCD".charAt(rng.nextInt(4));
        return pair + " " + digits + " " + suffix;
    }

    private static boolean isForbiddenNinPair(String pair) {
        for (String fp : NIN_FORBIDDEN_PAIRS) {
            if (fp.equals(pair)) return true;
        }
        return false;
    }

    // ── Russian INN (organisation) — 10 digits ───────────────────────────────

    private static String inn(ThreadLocalRandom rng) {
        int[] d = new int[10];
        for (int i = 0; i < 9; i++) d[i] = rng.nextInt(0, 10);
        int[] w = {2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += d[i] * w[i];
        d[9] = sum % 11 % 10;
        StringBuilder sb = new StringBuilder(10);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    // ── Russian INN individual — 12 digits ───────────────────────────────────

    private static String innIndividual(ThreadLocalRandom rng) {
        int[] d = new int[12];
        for (int i = 0; i < 10; i++) d[i] = rng.nextInt(0, 10);
        int[] w1 = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int[] w2 = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
        int s1 = 0;
        for (int i = 0; i < 10; i++) s1 += d[i] * w1[i];
        d[10] = s1 % 11 % 10;
        int s2 = 0;
        for (int i = 0; i < 11; i++) s2 += d[i] * w2[i];
        d[11] = s2 % 11 % 10;
        StringBuilder sb = new StringBuilder(12);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    // ── Russian SNILS — 11 digits with check ─────────────────────────────────

    private static String snils(ThreadLocalRandom rng) {
        int[] d = new int[9];
        StringBuilder numSb = new StringBuilder();
        for (int i = 0; i < 9; i++) { d[i] = rng.nextInt(0, 10); numSb.append(d[i]); }
        long num = Long.parseLong(numSb.toString());
        int check;
        if (num <= 1001) {
            check = 0;
        } else {
            int total = 0;
            for (int i = 0; i < 9; i++) total += d[i] * (9 - i);
            check = (total == 100 || total == 101) ? 0 : (total % 101 % 100);
        }
        return String.format("%03d-%03d-%03d %02d",
            d[0]*100 + d[1]*10 + d[2],
            d[3]*100 + d[4]*10 + d[5],
            d[6]*100 + d[7]*10 + d[8],
            check);
    }

    // ── Turkish SGK — Social Security (il-seq-unit.sub-sube) ─────────────────

    private static String sgk(ThreadLocalRandom rng) {
        int il   = rng.nextInt(1, 82);          // province 01-81
        int seq  = rng.nextInt(1, 10_000_000);  // sequence 1-9999999
        int unit = rng.nextInt(1, 10);           // unit 1-9
        int sub  = rng.nextInt(1, 100);          // sub 01-99
        int sube = rng.nextInt(1, 100);          // sube 01-99
        return String.format("%02d-%07d-%d.%02d-%02d", il, seq, unit, sub, sube);
    }

    // ── Turkish MERSIS — company registration: VKN(10) + "0" + 5digits ──────

    private static String mersis(ThreadLocalRandom rng) {
        String vknStr = vkn(rng);
        String suffix = String.format("%05d", rng.nextInt(0, 100_000));
        return vknStr + "0" + suffix;  // 10 + 1 + 5 = 16 chars
    }

    // ── US EIN — Employer Identification Number ───────────────────────────────

    private static final int[] VALID_EIN_PREFIXES = {
        10,11,12,13,14,15,16,20,21,22,23,24,25,26,27,
        30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,
        50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,
        71,72,73,74,75,76,77,80,81,82,83,84,85,86,87,88,
        90,91,92,93,94,95,98,99
    };

    static String ein(ThreadLocalRandom rng) {
        int prefix = VALID_EIN_PREFIXES[rng.nextInt(VALID_EIN_PREFIXES.length)];
        return String.format("%02d-%07d", prefix, rng.nextInt(1000000, 10000000));
    }

    // ── UK UTR — Unique Taxpayer Reference (10 digits) ───────────────────────

    private static final int[] UTR_WEIGHTS = {6,7,8,9,10,5,4,3,2};
    private static final String UTR_CHECK_MAP = "21987654321";

    private static String utr(ThreadLocalRandom rng) {
        int[] d = new int[9];
        int sum = 0;
        for (int i = 0; i < 9; i++) { d[i] = rng.nextInt(0, 10); sum += d[i] * UTR_WEIGHTS[i]; }
        char check = UTR_CHECK_MAP.charAt(sum % 11);
        StringBuilder sb = new StringBuilder(10);
        sb.append(check);
        for (int x : d) sb.append(x);
        return sb.toString();
    }

    // ── UK CRN — Company Registration Number ─────────────────────────────────

    private static String crn(ThreadLocalRandom rng) {
        int r = rng.nextInt(10); // weighted 8:1:1 -> EW if 0-7, SC if 8, NI if 9
        if (r < 8) {
            return String.valueOf(rng.nextInt(10000000, 100000000));
        }
        String prefix = r == 8 ? "SC" : "NI";
        return prefix + rng.nextInt(100000, 1000000);
    }

    // ── UK PAYE — Employer Pay Reference ─────────────────────────────────────

    private static final String PAYE_ALNUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String paye(ThreadLocalRandom rng) {
        int office = rng.nextInt(100, 1000);
        StringBuilder ref = new StringBuilder(6);
        for (int i = 0; i < 6; i++) ref.append(PAYE_ALNUM.charAt(rng.nextInt(PAYE_ALNUM.length())));
        return office + "/" + ref;
    }

    // ── German USt-IdNr ───────────────────────────────────────────────────────

    private static String ustId(ThreadLocalRandom rng) {
        while (true) {
            int[] base = new int[8];
            base[0] = rng.nextInt(1, 10);
            for (int i = 1; i < 8; i++) base[i] = rng.nextInt(0, 10);
            int product = 10;
            for (int d : base) {
                int s = (product + d) % 10;
                if (s == 0) s = 10;
                product = (s * 2) % 11;
            }
            int check = (11 - product) % 10;
            if (check == 10) continue;
            StringBuilder sb = new StringBuilder("DE");
            for (int d : base) sb.append(d);
            sb.append(check);
            return sb.toString();
        }
    }

    // ── German HRB ───────────────────────────────────────────────────────────

    private static final String[] HRB_COURTS = {
        "Aachen","Altenburg","Amberg","Ansbach","Arnsberg","Arnstadt",
        "Aschaffenburg","Augsburg","Aurich","Bad Hersfeld","Bad Homburg v.d.H.",
        "Bad Kreuznach","Bad Oeynhausen","Bad Salzungen","Bamberg","Bayreuth",
        "Berlin (Charlottenburg)","Bielefeld","Bochum","Bonn","Braunschweig",
        "Bremen","Chemnitz","Coburg","Cottbus","Darmstadt",
        "Deggendorf","Dortmund","Dresden","Duisburg","Erfurt","Essen",
        "Frankfurt am Main","Frankfurt (Oder)","Friedberg","Fulda",
        "Gelsenkirchen","Gera","Gotha","Hagen",
        "Hamburg","Hamm","Hanau","Hannover",
        "Hildburghausen","Ingolstadt","Iserlohn","Jena","Kaiserslautern",
        "Kassel","Kempten (Allgau)","Kiel","Kleve","Landshut","Leipzig",
        "Limburg","Mainz","Mannheim",
        "Marburg","Meiningen","Memmingen","Nordhausen",
        "Offenbach am Main","Oldenburg (Oldenburg)","Paderborn",
        "Passau","Potsdam","Regensburg","Rostock","Rudolstadt",
        "Schwerin","Siegen","Stadthagen","Stendal","Stralsund","Stuttgart",
        "Suhl","Tostedt","Traunstein","Ulm","Weiden i. d. OPf.","Weimar",
        "Wiesbaden","Wittlich","Wuppertal","Wurzburg"
    };

    private static String hrb(ThreadLocalRandom rng) {
        String court = HRB_COURTS[rng.nextInt(HRB_COURTS.length)];
        String registry = rng.nextBoolean() ? "HRB" : "HRA";
        int number = rng.nextInt(1, 1000000);
        return court + " " + registry + " " + number;
    }

    // ── German RVN ────────────────────────────────────────────────────────────

    private static final int[] RVN_AREA_CODES = {
        10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,
        30,31,32,33,38,39,40,48,50,52,58,60,61,62,65,70,71,72,78,80,
        81,88,89,90,91,92,93,94
    };

    private static String rvn(ThreadLocalRandom rng) {
        int area = RVN_AREA_CODES[rng.nextInt(RVN_AREA_CODES.length)];
        int day = rng.nextInt(1, 29);
        int month = rng.nextInt(1, 13);
        int year = rng.nextInt(40, 100);
        char letter = (char) ('A' + rng.nextInt(26));
        int serial = rng.nextInt(1, 1000);

        String base = String.format("%02d%02d%02d%02d%c%03d", area, day, month, year, letter, serial);

        StringBuilder expanded = new StringBuilder();
        for (char c : base.toCharArray()) {
            if (Character.isDigit(c)) expanded.append(c);
            else expanded.append(String.format("%02d", c - 'A' + 1));
        }
        String digits = expanded.toString();

        int total = 0;
        for (int i = 0; i < digits.length(); i++) {
            int d = digits.charAt(i) - '0';
            int n = d * (i % 2 == 0 ? 2 : 1);
            total += n / 10 + n % 10;
        }
        int check = total % 10;
        return String.format("%02d %02d%02d%02d %c %03d%d", area, day, month, year, letter, serial, check);
    }

    // ── French SIREN (9 digits, Luhn) ────────────────────────────────────────

    private static String siren(ThreadLocalRandom rng) {
        StringBuilder sb = new StringBuilder(8);
        sb.append(rng.nextInt(1, 10));
        for (int i = 1; i < 8; i++) sb.append(rng.nextInt(0, 10));
        String partial = sb.toString();
        return partial + luhnCheckDigit(partial);
    }

    // ── French SIRET (SIREN + 5 digits) ──────────────────────────────────────

    private static String siret(ThreadLocalRandom rng) {
        String sir = siren(rng);
        StringBuilder sb = new StringBuilder(sir);
        for (int i = 0; i < 4; i++) sb.append(rng.nextInt(0, 10));
        String partial = sb.toString();
        return partial + luhnCheckDigit(partial);
    }

    // ── French TVA ───────────────────────────────────────────────────────────

    private static String tva(ThreadLocalRandom rng) {
        String siren = siren(rng);
        int key = (12 + 3 * (Integer.parseInt(siren) % 97)) % 97;
        return String.format("FR%02d%s", key, siren);
    }

    // ── Russian OGRN (13 digits) ──────────────────────────────────────────────

    private static String ogrn(ThreadLocalRandom rng) {
        int year = rng.nextInt(2, 25);
        int region = rng.nextInt(1, 80);
        int seq = rng.nextInt(1, 10000000);
        String base = String.format("1%02d%02d%07d", year, region, seq);
        long val = Long.parseLong(base);
        int check = (int) ((val % 11) % 10);
        return base + check;
    }

    // ── Russian KPP (9 chars) ─────────────────────────────────────────────────

    private static String kpp(ThreadLocalRandom rng) {
        int region = rng.nextInt(1, 93);
        int ifns = rng.nextInt(1, 100);
        int reason = rng.nextInt(1, 51);
        int seq = rng.nextInt(1, 1000);
        return String.format("%02d%02d%02d%03d", region, ifns, reason, seq);
    }

    // ── Employer ID & Insurance ID ────────────────────────────────────────────

    private static String employerId(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR"       -> mersis(rng);
            case "US"       -> ein(rng);
            case "UK"       -> crn(rng);
            case "DE"       -> hrb(rng);
            case "FR"       -> siret(rng);
            case "RU"       -> ogrn(rng);
            default         -> mersis(rng);
        };
    }

    private static String insuranceId(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR"       -> individualInsuranceTr(rng);
            case "US"       -> ssn(rng);
            case "UK"       -> nhsNumber(rng);
            case "DE"       -> rvn(rng);
            case "RU"       -> snils(rng);
            case "FR"       -> inseeNumber(rng);
            default         -> sgk(rng);
        };
    }

    private static String individualInsuranceTr(ThreadLocalRandom rng) {
        int[] d = new int[9];
        d[0] = rng.nextInt(1, 10);
        for (int i = 1; i < 9; i++) d[i] = rng.nextInt(0, 10);
        int d9 = ((7 * (d[0]+d[2]+d[4]+d[6]+d[8])) - (d[1]+d[3]+d[5]+d[7])) % 10;
        if (d9 < 0) d9 += 10;
        int sum = 0;
        for (int x : d) sum += x;
        int d10 = (sum + d9) % 10;
        StringBuilder sb = new StringBuilder();
        for (int x : d) sb.append(x);
        return sb.append(d9).append(d10).toString();
    }

    private static String nhsNumber(ThreadLocalRandom rng) {
        while (true) {
            int[] base = new int[9];
            for (int i = 0; i < 9; i++) base[i] = rng.nextInt(0, 10);
            int[] weights = {10,9,8,7,6,5,4,3,2};
            int total = 0;
            for (int i = 0; i < 9; i++) total += base[i] * weights[i];
            int remainder = total % 11;
            if (remainder == 1) continue;
            int check = remainder == 0 ? 0 : 11 - remainder;
            if (check == 10) continue;
            StringBuilder digits = new StringBuilder();
            for (int x : base) digits.append(x);
            digits.append(check);
            String s = digits.toString();
            return s.substring(0,3) + " " + s.substring(3,6) + " " + s.substring(6);
        }
    }

    private static String inseeNumber(ThreadLocalRandom rng) {
        int gender  = rng.nextInt(1, 3);
        int yy      = rng.nextInt(40, 100);
        int month   = rng.nextInt(1, 13);
        int dep     = rng.nextInt(1, 96);
        int commune = rng.nextInt(1, 1000);
        int order   = rng.nextInt(1, 1000);
        String base = String.format("%d%02d%02d%02d%03d%03d", gender, yy, month, dep, commune, order);
        int key = 97 - (int) (Long.parseLong(base) % 97);
        return String.format("%s%02d", base, key);
    }

    // ── Names ────────────────────────────────────────────────────────────────

    private static boolean isFemale(ThreadLocalRandom rng, String genderKwarg) {
        String g = genderKwarg.toLowerCase();
        if (g.equals("female") || g.equals("kadın") || g.equals("f")) return true;
        if (g.equals("male") || g.equals("erkek") || g.equals("m")) return false;
        return rng.nextBoolean();
    }

    private static String[] namePool(String locale, String key) {
        return switch (locale + "_" + key) {
            case "TR_male" -> NameData.TR_MALE; case "TR_female" -> NameData.TR_FEMALE; case "TR_last" -> NameData.TR_LAST;
            case "US_male" -> NameData.US_MALE; case "US_female" -> NameData.US_FEMALE; case "US_last" -> NameData.US_LAST;
            case "UK_male" -> NameData.UK_MALE; case "UK_female" -> NameData.UK_FEMALE; case "UK_last" -> NameData.UK_LAST;
            case "DE_male" -> NameData.DE_MALE; case "DE_female" -> NameData.DE_FEMALE; case "DE_last" -> NameData.DE_LAST;
            case "FR_male" -> NameData.FR_MALE; case "FR_female" -> NameData.FR_FEMALE; case "FR_last" -> NameData.FR_LAST;
            case "RU_male" -> NameData.RU_MALE; case "RU_female" -> NameData.RU_FEMALE; case "RU_last" -> NameData.RU_LAST;
            case "RU_last_f" -> NameData.RU_LAST_F; case "RU_pat_m" -> NameData.RU_PAT_M; case "RU_pat_f" -> NameData.RU_PAT_F;
            default -> NameData.TR_MALE;
        };
    }

    private static String localeKey(String locale) {
        return switch (locale) { case "TR","US","UK","DE","FR","RU" -> locale; default -> "TR"; };
    }

    static String firstname(ThreadLocalRandom rng, String locale, String gender) {
        String l = localeKey(locale);
        boolean female = isFemale(rng, gender);
        return pick(rng, namePool(l, female ? "female" : "male"));
    }

    static String lastname(ThreadLocalRandom rng, String locale, String gender) {
        String l = localeKey(locale);
        boolean female = isFemale(rng, gender);
        String key = ("RU".equals(l) && female) ? "last_f" : "last";
        return pick(rng, namePool(l, key));
    }

    static String fullname(ThreadLocalRandom rng, String locale) {
        return fullnameQ(rng, locale, "");
    }

    static String fullnameQ(ThreadLocalRandom rng, String locale, String qualifier) {
        String l = localeKey(locale);
        boolean female = isFemale(rng, qualifier);
        String fn = pick(rng, namePool(l, female ? "female" : "male"));
        String ln = pick(rng, namePool(l, ("RU".equals(l) && female) ? "last_f" : "last"));
        if ("RU".equals(l)) {
            String pat = pick(rng, namePool(l, female ? "pat_f" : "pat_m"));
            return fn + " " + pat + " " + ln;
        }
        return fn + " " + ln;
    }

    private static String ageRange(ThreadLocalRandom rng, String qualifier) {
        int min = 18, max = 80;
        if (!qualifier.isEmpty() && qualifier.contains("-")) {
            String[] parts = qualifier.split("-", 2);
            try { min = Integer.parseInt(parts[0].trim()); } catch (NumberFormatException ignored) {}
            if (parts.length > 1) try { max = Integer.parseInt(parts[1].trim()); } catch (NumberFormatException ignored) {}
        }
        if (min >= max) max = min + 1;
        return String.valueOf(rng.nextInt(min, max + 1));
    }

    static String patronymic(ThreadLocalRandom rng, String locale, String gender) {
        if (!"RU".equals(locale)) return "";
        boolean female = isFemale(rng, gender);
        return pick(rng, namePool("RU", female ? "pat_f" : "pat_m"));
    }

    // ── Passport ──────────────────────────────────────────────────────────────

    private static final String TR_PASSPORT_LETTERS = "ABCDEFGHJKLMNPRSTUVYZ";
    private static final String DE_PASSPORT_LETTERS = "CFGHJKLMNPRTVWXYZ";

    private static String randDigitsIndep(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(rng.nextInt(0, 10));
        return sb.toString();
    }

    private static String passport(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> TR_PASSPORT_LETTERS.charAt(rng.nextInt(TR_PASSPORT_LETTERS.length())) + randDigitsIndep(rng, 8);
            case "US" -> ((char) ('A' + rng.nextInt(26))) + randDigitsIndep(rng, 8);
            case "UK" -> randDigitsIndep(rng, 9);
            case "DE" -> {
                char letter = DE_PASSPORT_LETTERS.charAt(rng.nextInt(DE_PASSPORT_LETTERS.length()));
                String alnum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) sb.append(alnum.charAt(rng.nextInt(alnum.length())));
                yield letter + sb.toString();
            }
            case "FR" -> {
                String d1 = String.format("%02d", rng.nextInt(1, 100));
                String letters = randomAlpha(rng, 2);
                String digits = String.format("%05d", rng.nextInt(1, 100000));
                yield d1 + letters + digits;
            }
            case "RU" -> String.format("%02d%07d", rng.nextInt(10, 100), rng.nextInt(1000000, 10000000));
            default   -> TR_PASSPORT_LETTERS.charAt(rng.nextInt(TR_PASSPORT_LETTERS.length())) + randDigitsIndep(rng, 8);
        };
    }

    // ── Driver's license ──────────────────────────────────────────────────────

    private static final String[] DE_LICENSE_STATES = {"B","BY","BW","HH","HB","HE","MV","NI","NW","RP","SL","SN","ST","SH","TH"};
    private static final String RU_LICENSE_SERIES = "ABCEHKMOPTXY";
    private static final String ALPHA26 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUM36 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String license(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> randomAlpha(rng, 2) + randDigitsIndep(rng, 6);
            case "US" -> ((char) ('A' + rng.nextInt(26))) + randDigitsIndep(rng, 7);
            case "UK" -> {
                StringBuilder surname = new StringBuilder();
                for (int i = 0; i < 5; i++) surname.append(ALPHA26.charAt(rng.nextInt(26)));
                int day = rng.nextInt(1, 29);
                int month = rng.nextInt(1, 13);
                int year = rng.nextInt(40, 100);
                boolean female = rng.nextBoolean();
                String genderD = female ? "5" : "0";
                int mEnc = female ? month + 50 : month;
                char initial = ALPHA26.charAt(rng.nextInt(26));
                int serial = rng.nextInt(1, 1000);
                char check = ALPHANUM36.charAt(rng.nextInt(ALPHANUM36.length()));
                yield String.format("%s%02d%02d%02d%s%c%03d%c", surname, day, mEnc, year, genderD, initial, serial, check);
            }
            case "DE" -> {
                String state = DE_LICENSE_STATES[rng.nextInt(DE_LICENSE_STATES.length)];
                char letter = ALPHA26.charAt(rng.nextInt(26));
                int len = rng.nextInt(4, 8);
                yield state + letter + randDigitsIndep(rng, len);
            }
            case "FR" -> randDigitsIndep(rng, 12);
            case "RU" -> {
                char s1 = RU_LICENSE_SERIES.charAt(rng.nextInt(RU_LICENSE_SERIES.length()));
                char s2 = RU_LICENSE_SERIES.charAt(rng.nextInt(RU_LICENSE_SERIES.length()));
                yield "" + s1 + s2 + randDigitsIndep(rng, 6);
            }
            default   -> randomAlpha(rng, 2) + randDigitsIndep(rng, 6);
        };
    }

    // ── Birthdate ─────────────────────────────────────────────────────────────

    private static String birthdate(ThreadLocalRandom rng) {
        int year  = LocalDate.now().getYear() - rng.nextInt(18, 81);
        int month = rng.nextInt(1, 13);
        int day   = rng.nextInt(1, 29);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // ── NationalID — routes by locale ────────────────────────────────────────

    private static String nationalid(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> tckn(rng);
            case "US" -> ssn(rng);
            case "UK" -> nin(rng);
            case "RU" -> rng.nextInt(1000, 10000) + " " + rng.nextInt(100000, 1000000);
            case "DE" -> IntlIdsGen.generate("de_idnr", locale);
            case "FR" -> inseeNumber(rng);
            default   -> tckn(rng);
        };
    }

    // ── VAT number ───────────────────────────────────────────────────────────

    private static String vatNumber(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> "TR" + vkn(rng);
            case "DE" -> ustId(rng);
            case "FR" -> tva(rng);
            case "UK" -> gbVat(rng);
            case "US" -> String.format("US%02d-%07d", rng.nextInt(10, 100), rng.nextInt(1000000, 10000000));
            case "RU" -> "RU" + inn(rng);
            default   -> "TR" + vkn(rng);
        };
    }

    private static final int[] GB_VAT_WEIGHTS = {8,7,6,5,4,3,2};

    private static String gbVat(ThreadLocalRandom rng) {
        int[] d = new int[7];
        d[0] = rng.nextInt(1, 10);
        for (int i = 1; i < 7; i++) d[i] = rng.nextInt(0, 10);
        int partial = 0;
        for (int i = 0; i < 7; i++) partial += GB_VAT_WEIGHTS[i] * d[i];
        int target = ((-partial) % 97 + 97) % 97;
        int d7 = target / 10, d8 = target % 10;
        StringBuilder sb = new StringBuilder("GB");
        for (int x : d) sb.append(x);
        return sb.append(d7).append(d8).toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }

    private static String randomAlpha(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append((char)('A' + rng.nextInt(26)));
        return sb.toString();
    }

    static int luhnCheckDigit(String partial) {
        int sum = 0;
        boolean alt = true;
        for (int i = partial.length() - 1; i >= 0; i--) {
            int d = partial.charAt(i) - '0';
            if (alt) { d *= 2; if (d > 9) d -= 9; }
            sum += d;
            alt = !alt;
        }
        return (10 - (sum % 10)) % 10;
    }
}
