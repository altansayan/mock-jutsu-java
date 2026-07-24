package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** International ID generator — mirrors intl_ids.py algorithms. */
public final class IntlIdsGen {

    private IntlIdsGen() {}

    private static ThreadLocalRandom rng() { return ThreadLocalRandom.current(); }

    // ── Verhoeff tables (Aadhaar) ─────────────────────────────────────────────
    private static final int[][] V_D = {
        {0,1,2,3,4,5,6,7,8,9},{1,2,3,4,0,6,7,8,9,5},{2,3,4,0,1,7,8,9,5,6},
        {3,4,0,1,2,8,9,5,6,7},{4,0,1,2,3,9,5,6,7,8},{5,9,8,7,6,0,4,3,2,1},
        {6,5,9,8,7,1,0,4,3,2},{7,6,5,9,8,2,1,0,4,3},{8,7,6,5,9,3,2,1,0,4},
        {9,8,7,6,5,4,3,2,1,0}
    };
    private static final int[][] V_P = {
        {0,1,2,3,4,5,6,7,8,9},{1,5,7,6,2,8,3,0,9,4},{5,8,0,3,7,9,6,1,4,2},
        {8,9,1,6,0,4,3,5,2,7},{9,4,5,3,1,2,6,8,7,0},{4,2,8,6,5,7,3,9,0,1},
        {2,7,9,3,8,0,6,4,1,5},{7,0,4,6,9,1,3,2,5,8}
    };
    private static final int[] V_INV = {0,4,3,2,1,5,6,7,8,9};

    private static int verhoeffCheck(int[] baseDigits) {
        int[] full = new int[baseDigits.length + 1];
        System.arraycopy(baseDigits, 0, full, 0, baseDigits.length);
        for (int check = 0; check <= 9; check++) {
            full[baseDigits.length] = check;
            int c = 0;
            for (int i = full.length - 1; i >= 0; i--) {
                c = V_D[c][V_P[(full.length - 1 - i) % 8][full[i]]];
            }
            if (c == 0) return check;
        }
        return 0;
    }

    // ── Luhn ──────────────────────────────────────────────────────────────────
    private static int luhnCheck(String partial) {
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

    // ── ISO 7064 MOD 11,10 ────────────────────────────────────────────────────
    private static int mod1110Check(String partial) {
        int p = 10;
        for (char c : partial.toCharArray()) {
            int s = (p + (c - '0')) % 10;
            if (s == 0) s = 10;
            p = (2 * s) % 11;
        }
        return (11 - p) % 10;
    }

    // ── Luhn mod-36 (GSTIN) ───────────────────────────────────────────────────
    private static final String GSTIN_ALPHA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static char luhn36Check(String partial) {
        int n = GSTIN_ALPHA.length();
        for (int ci = 0; ci < n; ci++) {
            char cand = GSTIN_ALPHA.charAt(ci);
            String full = partial + cand;
            int sum = 0;
            boolean dbl = false;
            for (int i = full.length() - 1; i >= 0; i--) {
                int v = GSTIN_ALPHA.indexOf(full.charAt(i));
                if (dbl) { v *= 2; if (v >= n) v = v / n + v % n; }
                sum += v;
                dbl = !dbl;
            }
            if (sum % n == 0) return cand;
        }
        return '0';
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static String randDigits(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(rng().nextInt(10));
        return sb.toString();
    }
    private static String randDigitsRange(int lo, int hi) {
        return String.valueOf(rng().nextInt(lo, hi + 1));
    }
    private static char randAlpha() { return (char)('A' + rng().nextInt(26)); }
    private static String randAlphas(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(randAlpha());
        return sb.toString();
    }
    private static char randAlphaNum() {
        int r = rng().nextInt(36);
        return r < 26 ? (char)('A' + r) : (char)('0' + r - 26);
    }
    private static String padLeft(int v, int width) {
        return String.format("%0" + width + "d", v);
    }

    // ── DNI/NIE letter table ──────────────────────────────────────────────────
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    // ── BR CPF ────────────────────────────────────────────────────────────────
    private static String genBrCpf() {
        int[] d = new int[9];
        for (int i = 0; i < 9; i++) d[i] = rng().nextInt(10);
        int s1 = 0; for (int i = 0; i < 9; i++) s1 += d[i] * (10 - i);
        int c1 = 11 - s1 % 11; if (c1 >= 10) c1 = 0;
        int s2 = 0; for (int i = 0; i < 9; i++) s2 += d[i] * (11 - i); s2 += c1 * 2;
        int c2 = 11 - s2 % 11; if (c2 >= 10) c2 = 0;
        StringBuilder b = new StringBuilder();
        for (int x : d) b.append(x);
        return b.append(c1).append(c2).toString();
    }

    // ── BR CNPJ ───────────────────────────────────────────────────────────────
    private static String genBrCnpj() {
        int[] d = new int[12];
        for (int i = 0; i < 8; i++) d[i] = rng().nextInt(10);
        d[8] = 0; d[9] = 0; d[10] = 0; d[11] = 1;
        int[] w1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int s1 = 0; for (int i = 0; i < 12; i++) s1 += d[i] * w1[i];
        int c1 = 11 - s1 % 11; if (c1 >= 10) c1 = 0;
        int[] w2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        int s2 = 0; for (int i = 0; i < 12; i++) s2 += d[i] * w2[i]; s2 += c1 * w2[12];
        int c2 = 11 - s2 % 11; if (c2 >= 10) c2 = 0;
        StringBuilder b = new StringBuilder();
        for (int x : d) b.append(x);
        return b.append(c1).append(c2).toString();
    }

    // ── IN PAN ────────────────────────────────────────────────────────────────
    private static final String IN_PAN_P4_SET = "PCFHBLJGTA";
    private static String genInPan() {
        char p4 = IN_PAN_P4_SET.charAt(rng().nextInt(IN_PAN_P4_SET.length()));
        return randAlphas(3) + p4 + randAlpha() + randDigits(4) + randAlpha();
    }

    // ── IN Aadhaar ────────────────────────────────────────────────────────────
    private static String genInAadhaar() {
        while (true) {
            int first = rng().nextInt(2, 10);
            int[] base = new int[11];
            base[0] = first;
            for (int i = 1; i < 11; i++) base[i] = rng().nextInt(10);
            // palindrome check
            int[] all = new int[12];
            System.arraycopy(base, 0, all, 0, 11);
            all[11] = verhoeffCheck(base);
            boolean pal = true;
            for (int i = 0; i < 6; i++) { if (all[i] != all[11-i]) { pal = false; break; } }
            if (!pal) {
                StringBuilder b = new StringBuilder();
                for (int x : all) b.append(x);
                String n = b.toString();
                return n.substring(0, 4) + " " + n.substring(4, 8) + " " + n.substring(8);
            }
        }
    }

    // ── IN GSTIN ──────────────────────────────────────────────────────────────
    private static final String[] GSTIN_STATES = {
        "01","02","03","04","05","06","07","08","09","10",
        "11","12","13","14","15","16","17","18","19","20",
        "21","22","23","24","25","26","27","28","29","30",
        "31","32","33","34","35","36","37"
    };
    private static String genInGstin() {
        String state = GSTIN_STATES[rng().nextInt(GSTIN_STATES.length)];
        String pan = genInPan();
        String entity = String.valueOf(rng().nextInt(1, 10));
        String z = "Z";
        String partial = state + pan + entity + z;
        char check = luhn36Check(partial);
        return partial + check;
    }

    // ── IN EPIC ───────────────────────────────────────────────────────────────
    private static String genInEpic() {
        String digits = randDigits(6);
        int check = luhnCheck(digits);
        return randAlphas(3) + digits + check;
    }

    // ── CN RIC ────────────────────────────────────────────────────────────────
    private static final String[] CN_AREAS = {
        "110000","120000","130000","210000","220000","230000",
        "310000","320000","330000","340000","350000","360000",
        "370000","410000","420000","430000","440000",
        "510000","520000","530000","610000","620000","630000"
    };
    private static final int[] CN_RIC_WEIGHTS = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
    private static char cnRicCheck(String partial17) {
        int s = 0;
        for (int i = 0; i < 17; i++) s += (partial17.charAt(i) - '0') * CN_RIC_WEIGHTS[i];
        return "10X98765432".charAt(s % 11);
    }
    private static String genCnRic() {
        String area = CN_AREAS[rng().nextInt(CN_AREAS.length)];
        int year = rng().nextInt(1960, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        String seq = padLeft(rng().nextInt(1, 1000), 3);
        String partial = area + String.format("%04d%02d%02d", year, month, day) + seq;
        return partial + cnRicCheck(partial);
    }

    // ── MX CURP ───────────────────────────────────────────────────────────────
    private static final String[] MX_STATES = {
        "AS","BC","BS","CC","CL","CM","CS","CH","DF","DG",
        "GT","GR","HG","JC","MC","MN","MS","NT","NL","OC",
        "PL","QT","QR","SP","SL","SR","TC","TL","TS","VZ",
        "YN","ZS","NE"
    };
    private static final String CURP_ALPH = "0123456789ABCDEFGHIJKLMN&OPQRSTUVWXYZ";
    private static final String[] CURP_CONSONANTS_POOL = {"B","C","D","F","G","H","J","K","L","M","N","P","Q","R","S","T","V","W","X","Y","Z"};
    private static final String[] CURP_VOWELS = {"A","E","I","O","U"};
    private static final String[] CURP_BLOCKED = {"BACA","BAKA","BUEI","BUEY","CACA","CACO","CAGA","CAGO","CAKA","CAKO","COGE","COGI","COJA","COJE","COJI","COJO","COLA","CULO","FALO","FETO","GETA","GUEI","GUEY","JETA","JOTO","KACA","KACO","KAGA","KAGO","KAKA","KAKO","KOGE","KOGI","KOJA","KOJE","KOJI","KOJO","KOLA","KULO","LELO","LOCA","LOCO","LOKA","LOKO","MAME","MAMO","MEAR","MEAS","MEON","MIAR","MION","MOCO","MOKO","MULA","MULO","NACA","NACO","PEDA","PEDO","PENE","PIPI","PITO","POPO","PUTA","PUTO","QULO","RATA","ROBA","ROBE","ROBO","RUIN","SENO","TETA","VACA","VAGA","VAGO","VAKA","VUEI","VUEY","WUEI","WUEY"};
    private static final String CURP_ALNUM36 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String genMxCurp() {
        while (true) {
            String v1 = CURP_VOWELS[rng().nextInt(CURP_VOWELS.length)];
            String l1 = String.valueOf(randAlpha());
            String l2 = String.valueOf(randAlpha());
            String l3 = String.valueOf(randAlpha());
            String block = v1 + l1 + l2 + l3;
            boolean blocked = false;
            for (String b : CURP_BLOCKED) if (b.equals(block)) { blocked = true; break; }
            if (blocked) continue;
            int year = rng().nextInt(1950, 2006);
            int month = rng().nextInt(1, 13);
            int day = rng().nextInt(1, 29);
            String gender = rng().nextBoolean() ? "H" : "M";
            String state = MX_STATES[rng().nextInt(MX_STATES.length)];
            String c1 = CURP_CONSONANTS_POOL[rng().nextInt(CURP_CONSONANTS_POOL.length)];
            String c2 = CURP_CONSONANTS_POOL[rng().nextInt(CURP_CONSONANTS_POOL.length)];
            String c3 = CURP_CONSONANTS_POOL[rng().nextInt(CURP_CONSONANTS_POOL.length)];
            String alnum = String.valueOf(CURP_ALNUM36.charAt(rng().nextInt(CURP_ALNUM36.length())));
            String partial = block + String.format("%02d%02d%02d", year % 100, month, day)
                           + gender + state + c1 + c2 + c3 + alnum;
            // compute check digit
            int s = 0;
            for (int i = 0; i < 17; i++) {
                s += CURP_ALPH.indexOf(partial.charAt(i)) * (18 - i);
            }
            int check = (10 - s % 10) % 10;
            return partial + check;
        }
    }

    // ── MX RFC ────────────────────────────────────────────────────────────────
    private static String genMxRfc() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        boolean isIndividual = rng().nextDouble() > 0.4;
        String letters = isIndividual ? randAlphas(4) : randAlphas(3);
        String homoclave = String.valueOf(CURP_ALNUM36.charAt(rng().nextInt(CURP_ALNUM36.length())))
                          + String.valueOf(CURP_ALNUM36.charAt(rng().nextInt(CURP_ALNUM36.length())));
        String hd = String.valueOf(rng().nextInt(10));
        return letters + String.format("%02d%02d%02d", year % 100, month, day) + homoclave + hd;
    }

    // ── IT Codice Fiscale ─────────────────────────────────────────────────────
    private static final int[] CF_ODD = {
        1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23
    };
    private static final int[] CF_EVEN_DIGIT = {0,1,2,3,4,5,6,7,8,9};
    private static final int[] CF_EVEN_ALPHA = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
    private static String genItCf() {
        String surname = randAlphas(3);
        String name = randAlphas(3);
        int year = rng().nextInt(1950, 2006);
        String[] months = {"A","B","C","D","E","H","L","M","P","R","S","T"};
        String month = months[rng().nextInt(12)];
        int dayBase = rng().nextInt(1, 29);
        boolean female = rng().nextBoolean();
        int day = female ? dayBase + 40 : dayBase;
        String mun = randAlphas(1) + randDigits(3);
        String partial = (surname + name + String.format("%02d", year % 100) + month
                        + String.format("%02d", day) + mun).toUpperCase();
        int sum = 0;
        for (int i = 0; i < 15; i++) {
            char c = partial.charAt(i);
            int v;
            if ((i + 1) % 2 == 0) {
                // even position (1-based)
                v = Character.isDigit(c) ? CF_EVEN_DIGIT[c - '0'] : CF_EVEN_ALPHA[c - 'A'];
            } else {
                // odd position
                v = Character.isDigit(c) ? CF_ODD[c - '0'] : CF_ODD[c - 'A'];
            }
            sum += v;
        }
        char check = (char)('A' + sum % 26);
        return partial + check;
    }

    // ── ES DNI ────────────────────────────────────────────────────────────────
    private static String genEsDni() {
        int num = rng().nextInt(10000000, 100000000);
        return String.format("%08d", num) + DNI_LETTERS.charAt(num % 23);
    }

    // ── ES NIE ────────────────────────────────────────────────────────────────
    private static String genEsNie() {
        char prefix = "XYZ".charAt(rng().nextInt(3));
        int prefixVal = "XYZ".indexOf(prefix);
        int num = rng().nextInt(1000000, 10000000);
        int forMod = Integer.parseInt(prefixVal + String.format("%07d", num));
        return prefix + String.format("%07d", num) + DNI_LETTERS.charAt(forMod % 23);
    }

    // ── ES CCC ────────────────────────────────────────────────────────────────
    private static String genEsCcc() {
        String bank = String.valueOf(rng().nextInt(1000, 10000));
        String branch = String.valueOf(rng().nextInt(1000, 10000));
        String account = randDigits(10);
        int c1 = esCccCheckDigit("00" + bank + branch);
        int c2 = esCccCheckDigit(account);
        return bank + "-" + branch + "-" + c1 + c2 + "-" + account;
    }

    // Spanish CCC check digit: sum(digit * 2^position) % 11, mapped 0/1 as-is, else 11-check.
    private static int esCccCheckDigit(String s) {
        int sum = 0;
        int pow = 1;
        for (int i = 0; i < s.length(); i++) {
            sum += (s.charAt(i) - '0') * pow;
            pow = (pow * 2) % 11;
        }
        int check = sum % 11;
        return check < 2 ? check : 11 - check;
    }

    // ── DE IdNr ───────────────────────────────────────────────────────────────
    private static String genDeIdnr() {
        // Exactly one digit value must repeat (2 or 3 times) among the first
        // 10 digits; every other digit value used must appear exactly once.
        int repeatDigit = rng().nextInt(0, 10);
        int repeatCount = rng().nextInt(2, 4); // 2 or 3
        java.util.List<Integer> pool = new java.util.ArrayList<>();
        for (int i = 0; i < repeatCount; i++) pool.add(repeatDigit);
        java.util.List<Integer> others = new java.util.ArrayList<>();
        for (int d = 0; d <= 9; d++) if (d != repeatDigit) others.add(d);
        java.util.Collections.shuffle(others, new java.util.Random(rng().nextLong()));
        int need = 10 - repeatCount;
        for (int i = 0; i < need; i++) pool.add(others.get(i));
        java.util.Collections.shuffle(pool, new java.util.Random(rng().nextLong()));
        // First digit must not be 0
        if (pool.get(0) == 0) {
            for (int i = 1; i < pool.size(); i++) {
                if (pool.get(i) != 0) { java.util.Collections.swap(pool, 0, i); break; }
            }
        }
        StringBuilder b = new StringBuilder();
        for (int d : pool) b.append(d);
        String partial = b.toString();
        int check = mod1110Check(partial);
        return partial + check;
    }

    // ── DE StNr ───────────────────────────────────────────────────────────────
    private static final String[] DE_STATE_PREFIXES = {
        "10","11","20","21","22","23","24","25","26","27",
        "28","29","30","31","32","33","40","41","42","43",
        "44","45","46","47","48","50","51","52","53","54",
        "55","56","57","58","60","61","62","63","64","65",
        "66","70","71","80","81","82","83","84","85","86",
        "87","88","89","90","91","92","93","94","95"
    };
    private static String genDeStnr() {
        String state = DE_STATE_PREFIXES[rng().nextInt(DE_STATE_PREFIXES.length)];
        String dist = String.valueOf(rng().nextInt(100, 1000));
        String seq = String.valueOf(rng().nextInt(10000, 100000));
        String check = String.valueOf(rng().nextInt(0, 10));
        return state + "/" + dist + "/" + seq + " " + check;
    }

    // ── PK CNIC ───────────────────────────────────────────────────────────────
    private static final int[] PK_CNIC_GENDER_DIGITS = {1, 3, 5, 7, 9};
    private static String genPkCnic() {
        String province = String.valueOf(rng().nextInt(10000, 50000));
        String serial = String.valueOf(rng().nextInt(1000000, 10000000));
        int gender = PK_CNIC_GENDER_DIGITS[rng().nextInt(PK_CNIC_GENDER_DIGITS.length)];
        return province + "-" + serial + "-" + gender;
    }

    // ── JP CN ─────────────────────────────────────────────────────────────────
    private static String genJpCn() {
        int[] weights = {1,2,1,2,1,2,1,2,1,2,1,2};
        int[] body = new int[12];
        for (int i = 0; i < 12; i++) body[i] = rng().nextInt(10);
        int s = 0;
        for (int i = 0; i < 12; i++) s = (s + weights[i] * body[11 - i]) % 9;
        int check = 9 - s;
        StringBuilder b = new StringBuilder();
        b.append(check);
        for (int x : body) b.append(x);
        return b.toString();
    }

    // ── JP IN ─────────────────────────────────────────────────────────────────
    private static String genJpIn() {
        int[] w = {6,5,4,3,2,7,6,5,4,3,2};
        StringBuilder partial = new StringBuilder();
        for (int i = 0; i < 11; i++) partial.append(rng().nextInt(10));
        int s = 0;
        for (int i = 0; i < 11; i++) s += (partial.charAt(i) - '0') * w[i];
        int r = s % 11;
        int check = r < 2 ? 0 : 11 - r;
        return partial + String.valueOf(check);
    }

    // ── KR RRN ────────────────────────────────────────────────────────────────
    private static String genKrRrn() {
        int[] weights = {2,3,4,5,6,7,8,9,2,3,4,5};
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int yy = year % 100;
        int gender = year < 2000 ? rng().nextInt(1, 3) : rng().nextInt(3, 5);
        int pb = rng().nextInt(0, 97);
        int xx = rng().nextInt(0, 100);
        int cc = rng().nextInt(0, 10);
        String base = String.format("%02d%02d%02d%d%02d%02d%d", yy, month, day, gender, pb, xx, cc);
        int s = 0;
        for (int i = 0; i < 12; i++) s += (base.charAt(i) - '0') * weights[i];
        int check = (11 - s % 11) % 10;
        return String.format("%02d%02d%02d-%d%02d%02d%d%d", yy, month, day, gender, pb, xx, cc, check);
    }

    // ── KR BRN ────────────────────────────────────────────────────────────────
    private static String genKrBrn() {
        // 10 digits: tax office(3, 101-999) - mid(2, 10-99) - tail(5, 10000-99999)
        int office = rng().nextInt(101, 1000);
        int mid = rng().nextInt(10, 100);
        int tail = rng().nextInt(10000, 100000);
        return String.format("%03d-%02d-%05d", office, mid, tail);
    }

    // ── NL BSN ────────────────────────────────────────────────────────────────
    private static String genNlBsn() {
        while (true) {
            int[] d = new int[8];
            for (int i = 0; i < 8; i++) d[i] = rng().nextInt(10);
            if (d[0] == 0) continue;
            int s = 0;
            for (int i = 0; i < 8; i++) s += d[i] * (9 - i);
            int d8 = s % 11;
            if (d8 > 9) continue;
            StringBuilder b = new StringBuilder();
            for (int x : d) b.append(x);
            return b.append(d8).toString();
        }
    }

    // ── PL PESEL ─────────────────────────────────────────────────────────────
    private static String genPlPesel() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int encMonth = month + (year >= 2000 ? 20 : 0);
        String partial = String.format("%02d%02d%02d", year % 100, encMonth, day) + randDigits(4);
        int[] w = {1,3,7,9,1,3,7,9,1,3};
        int s = 0;
        for (int i = 0; i < 10; i++) s += (partial.charAt(i) - '0') * w[i];
        int check = (10 - s % 10) % 10;
        return partial + check;
    }

    // ── SE Personnummer ───────────────────────────────────────────────────────
    private static String genSePersonnummer() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        String partial = String.format("%02d%02d%02d", year % 100, month, day) + randDigits(3);
        int check = luhnCheck(partial);
        return String.format("%04d%02d%02d-%s%d", year, month, day, partial.substring(6), check);
    }

    // ── DK CPR ────────────────────────────────────────────────────────────────
    private static String genDkCpr() {
        int year = rng().nextInt(1950, 2001);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int seq = rng().nextInt(0, 4000);
        return String.format("%02d%02d%02d-%04d", day, month, year % 100, seq);
    }

    // ── FI Hetu ───────────────────────────────────────────────────────────────
    private static final String HETU_CHARS = "0123456789ABCDEFHJKLMNPRSTUVWXY";
    private static String genFiHetu() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int seq = rng().nextInt(2, 900);
        char sep = year < 2000 ? '-' : 'A';
        String partial = String.format("%02d%02d%02d", day, month, year % 100) + String.format("%03d", seq);
        int r = Integer.parseInt(partial) % 31;
        return String.format("%02d%02d%02d%c%03d%c", day, month, year % 100, sep, seq, HETU_CHARS.charAt(r));
    }

    // ── NO Fødselsnummer ─────────────────────────────────────────────────────
    private static String genNoFodselsnummer() {
        while (true) {
            int year = rng().nextInt(1950, 2006);
            int month = rng().nextInt(1, 13);
            int day = rng().nextInt(1, 29);
            int ind = rng().nextInt(0, 500);
            String partial = String.format("%02d%02d%02d%03d", day, month, year % 100, ind);
            int[] w1 = {3,7,6,1,8,9,4,5,2};
            int s1 = 0;
            for (int i = 0; i < 9; i++) s1 += (partial.charAt(i) - '0') * w1[i];
            int c1r = s1 % 11;
            if (c1r == 1) continue;
            int c1 = c1r == 0 ? 0 : 11 - c1r;
            int[] w2 = {5,4,3,2,7,6,5,4,3,2};
            int s2 = 0;
            for (int i = 0; i < 9; i++) s2 += (partial.charAt(i) - '0') * w2[i];
            s2 += c1 * w2[9];
            int c2r = s2 % 11;
            if (c2r == 1) continue;
            int c2 = c2r == 0 ? 0 : 11 - c2r;
            return partial + c1 + c2;
        }
    }

    // ── AU ABN ────────────────────────────────────────────────────────────────
    private static String genAuAbn() {
        int[] w = {3,5,7,9,11,13,15,17,19};
        String body = randDigits(9);
        int s = 0;
        for (int i = 0; i < 9; i++) s += -w[i] * (body.charAt(i) - '0');
        // compute first 2 check digits: 11 + (s-1) % 89
        int check2val = 11 + ((s - 1) % 89 + 89) % 89;
        return String.format("%02d", check2val) + body;
    }

    // ── AU TFN ────────────────────────────────────────────────────────────────
    private static String genAuTfn() {
        while (true) {
            StringBuilder b = new StringBuilder();
            b.append(rng().nextInt(1, 10));
            for (int i = 1; i < 8; i++) b.append(rng().nextInt(10));
            String s = b.toString();
            int[] w = {1,4,3,7,5,8,6,9,10};
            int sum = 0;
            for (int i = 0; i < 8; i++) sum += (s.charAt(i) - '0') * w[i];
            int check = sum % 11;
            if (check == 10) continue;
            return s + check;
        }
    }

    // ── AU ACN ────────────────────────────────────────────────────────────────
    private static String genAuAcn() {
        while (true) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 8; i++) b.append(rng().nextInt(10));
            String partial = b.toString();
            if (partial.charAt(0) == '0') continue;
            int[] w = {8,7,6,5,4,3,2,1};
            int s = 0;
            for (int i = 0; i < 8; i++) s += (partial.charAt(i) - '0') * w[i];
            int check = (10 - s % 10) % 10;
            return partial + check;
        }
    }

    // ── MY NRIC ───────────────────────────────────────────────────────────────
    private static final String[] MY_BP_CODES = {
        "01","02","03","04","05","06","07","08","09","10",
        "11","12","13","14","15","16","21","22","23","24",
        "25","26","27","28","29","30","31","32","33","34",
        "35","36","37","38","39","40","41","42","43","44",
        "45","46","47","48","49","50","51","52","53","54",
        "55","56","57","58","59","60","61","62","63","64",
        "65","66","67","68","71","72","74","75","76","77",
        "78","79","82","83","84","85","86","87","88","89",
        "90","91","92","93","98","99"
    };
    private static String genMyNric() {
        int year = rng().nextInt(1960, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        String bp = MY_BP_CODES[rng().nextInt(MY_BP_CODES.length)];
        int seq = rng().nextInt(1, 10000);
        return String.format("%02d%02d%02d-%s-%04d", year % 100, month, day, bp, seq);
    }

    // ── TH PIN ────────────────────────────────────────────────────────────────
    private static String genThPin() {
        StringBuilder b = new StringBuilder();
        b.append(rng().nextInt(1, 9));
        for (int i = 0; i < 11; i++) b.append(rng().nextInt(10));
        String partial = b.toString();
        int s = 0;
        for (int i = 0; i < 12; i++) s += (partial.charAt(i) - '0') * (13 - i);
        int check = (11 - s % 11) % 10;
        return partial + check;
    }

    // ── TH TIN ────────────────────────────────────────────────────────────────
    // A Thai personal TIN is the same 13-digit PIN with the same check digit.
    private static String genThTin() {
        return genThPin();
    }

    // ── SG UEN ────────────────────────────────────────────────────────────────
    // Business (ROB) format: 8 digits + check letter.
    private static final String SG_UEN_ALPHABET = "XMKECAWLJDB";
    private static final int[] SG_UEN_WEIGHTS = {10, 4, 9, 3, 8, 2, 7, 1};

    private static String genSgUen() {
        String body = randDigits(8);
        int sum = 0;
        for (int i = 0; i < 8; i++) sum += (body.charAt(i) - '0') * SG_UEN_WEIGHTS[i];
        char check = SG_UEN_ALPHABET.charAt(sum % 11);
        return body + check;
    }

    // ── ZA IDNr ───────────────────────────────────────────────────────────────
    private static String genZaIdnr() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int gender = rng().nextInt(0, 10000);
        int citizen = 0;
        // 12 digits before check: date(6) + gender/sequence(4) + citizenship(1) + race digit(1, always 8)
        String partial = String.format("%02d%02d%02d%04d%d8", year % 100, month, day, gender, citizen);
        int check = luhnCheck(partial);
        return partial + check;
    }

    // ── CA BN ─────────────────────────────────────────────────────────────────
    private static String genCaBn() {
        String partial = rng().nextInt(1, 10) + randDigits(7);
        return partial + luhnCheck(partial);
    }

    // ── NZ IRD ────────────────────────────────────────────────────────────────
    private static String genNzIrd() {
        while (true) {
            int base = rng().nextInt(1000001, 15000000);
            String s = String.format("%08d", base);
            int[] w1 = {3,2,7,6,5,4,3,2};
            int sum1 = 0;
            for (int i = 0; i < 8; i++) sum1 += (s.charAt(i) - '0') * w1[i];
            int r1 = sum1 % 11;
            int check;
            if (r1 == 0) {
                check = 0;
            } else {
                check = 11 - r1;
                if (check == 10) {
                    int[] w2 = {7,4,3,2,5,2,7,6};
                    int sum2 = 0;
                    for (int i = 0; i < 8; i++) sum2 += (s.charAt(i) - '0') * w2[i];
                    int r2 = sum2 % 11;
                    check = r2 == 0 ? 0 : 11 - r2;
                    if (check == 10) continue;
                }
            }
            return s + check;
        }
    }

    // ── AR CUIT ───────────────────────────────────────────────────────────────
    private static String genArCuit() {
        int[] prefixes = {20,23,24,27,30,33,34};
        int prefix = prefixes[rng().nextInt(prefixes.length)];
        String body = String.format("%08d", rng().nextInt(1000000, 100000000));
        String partial = String.format("%02d%s", prefix, body);
        int[] w = {5,4,3,2,7,6,5,4,3,2};
        int s = 0;
        for (int i = 0; i < 10; i++) s += (partial.charAt(i) - '0') * w[i];
        int r = 11 - s % 11;
        int check = r == 11 ? 0 : r == 10 ? 9 : r;
        return partial.substring(0,2) + "-" + body + "-" + check;
    }

    // ── AR DNI ────────────────────────────────────────────────────────────────
    private static String genArDni() {
        return String.valueOf(rng().nextInt(1000000, 100000000));
    }

    // ── CL RUT ────────────────────────────────────────────────────────────────
    private static String genClRut() {
        int num = rng().nextInt(1000000, 30000000);
        int[] digits = String.valueOf(num).chars().map(c -> c - '0').toArray();
        int[] w = {2,3,4,5,6,7};
        int s = 0;
        for (int i = 0; i < digits.length; i++) s += digits[digits.length - 1 - i] * w[i % 6];
        int r = 11 - s % 11;
        String check = r == 11 ? "0" : r == 10 ? "K" : String.valueOf(r);
        return String.format("%,d", num).replace(",", ".") + "-" + check;
    }

    // ── CO NIT ────────────────────────────────────────────────────────────────
    private static final String CO_NIT_TABLE = "01987654321";
    private static String genCoNit() {
        String body = randDigits(9);
        int[] w = {3,7,13,17,19,23,29,37,41,43,47,53,59,67,71};
        // Weights apply to the number read right-to-left (rightmost digit = weight[0]).
        int s = 0;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(body.length() - 1 - i);
            s += (c - '0') * w[i];
        }
        char check = CO_NIT_TABLE.charAt(s % 11);
        return body + "-" + check;
    }

    // ── IL IDNr ───────────────────────────────────────────────────────────────
    private static String genIlIdnr() {
        String partial = randDigits(8);
        int check = luhnCheck(partial);
        return partial + check;
    }

    // ── RO CNP ────────────────────────────────────────────────────────────────
    private static String genRoCnp() {
        int s = rng().nextBoolean() ? 1 : 2;
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int county = rng().nextInt(1, 47);
        int seq = rng().nextInt(1, 1000);
        String partial = String.format("%d%02d%02d%02d%02d%03d", s, year % 100, month, day, county, seq);
        int[] w = {2,7,9,1,4,6,3,5,8,2,7,9};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (partial.charAt(i) - '0') * w[i];
        int check = sum % 11;
        if (check == 10) check = 1;
        return partial + check;
    }

    // ── RO CUI ────────────────────────────────────────────────────────────────
    private static String genRoCui() {
        int body = rng().nextInt(1000000, 100000000);
        String s = String.valueOf(body);
        String padded = String.format("%09d", body);
        int[] w = {7,5,3,2,1,7,5,3,2};
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (padded.charAt(i) - '0') * w[i];
        int check = sum * 10 % 11 % 10;
        return "RO" + s + check;
    }

    // ── HR OIB ────────────────────────────────────────────────────────────────
    private static String genHrOib() {
        // OIB = 11 digits: 10 random digits + 1 ISO 7064 MOD 11,10 check digit.
        String partial = randDigits(10);
        int check = mod1110Check(partial);
        return partial + check;
    }

    // ── BG EGN ────────────────────────────────────────────────────────────────
    private static String genBgEgn() {
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        int encMonth = year >= 2000 ? month + 20 : month;
        String partial = String.format("%02d%02d%02d", year % 100, encMonth, day) + randDigits(3);
        int[] w = {2,4,8,5,10,9,7,3,6};
        int s = 0;
        for (int i = 0; i < 9; i++) s += (partial.charAt(i) - '0') * w[i];
        int check = s % 11 % 10;
        return partial + check;
    }

    // ── LT Asmens ─────────────────────────────────────────────────────────────
    private static String genLtAsmens() {
        int gender = rng().nextBoolean() ? 3 : 4;
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        String partial = gender + String.format("%02d%02d%02d", year % 100, month, day) + randDigits(3);
        int[] w1 = {1,2,3,4,5,6,7,8,9,1};
        int s1 = 0;
        for (int i = 0; i < 10; i++) s1 += (partial.charAt(i) - '0') * w1[i];
        int c = s1 % 11;
        if (c == 10) {
            int[] w2 = {3,4,5,6,7,8,9,1,2,3};
            int s2 = 0;
            for (int i = 0; i < 10; i++) s2 += (partial.charAt(i) - '0') * w2[i];
            c = s2 % 11 % 10;
        }
        return partial + c;
    }

    // ── EE IK ─────────────────────────────────────────────────────────────────
    private static String genEeIk() {
        int gender = rng().nextBoolean() ? 3 : 4;
        int year = rng().nextInt(1950, 2006);
        int month = rng().nextInt(1, 13);
        int day = rng().nextInt(1, 29);
        String partial = gender + String.format("%02d%02d%02d", year % 100, month, day) + randDigits(3);
        int[] w1 = {1,2,3,4,5,6,7,8,9,1};
        int s1 = 0;
        for (int i = 0; i < 10; i++) s1 += (partial.charAt(i) - '0') * w1[i];
        int c = s1 % 11;
        if (c == 10) {
            int[] w2 = {3,4,5,6,7,8,9,1,2,3};
            int s2 = 0;
            for (int i = 0; i < 10; i++) s2 += (partial.charAt(i) - '0') * w2[i];
            c = s2 % 11 % 10;
        }
        return partial + c;
    }

    // ── PT CC ─────────────────────────────────────────────────────────────────
    private static final String PT_CC_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int ptCcCutoff(int x) { return x > 9 ? x - 9 : x; }

    private static char ptCcCheckDigit(String s) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(s.length() - 1 - i); // process reversed
            int idx = PT_CC_ALPHABET.indexOf(c);
            sum += (i % 2 == 0) ? ptCcCutoff(idx * 2) : idx;
        }
        return Character.forDigit((10 - sum % 10) % 10, 10);
    }

    private static String genPtCc() {
        String num = String.valueOf(rng().nextInt(10000000, 100000000));
        char c1 = randAlpha();
        char c2 = randAlpha();
        String body = num + "0" + c1 + c2; // 11 chars before the check digit
        char check = ptCcCheckDigit(body);
        return num + " 0 " + c1 + c2 + check;
    }

    // ── EG TN ─────────────────────────────────────────────────────────────────
    private static String genEgTn() {
        return randDigits(9);
    }

    // ── Dispatch ──────────────────────────────────────────────────────────────
    public static String generate(String type, String locale) {
        switch (type) {
            case "br_cpf":           return genBrCpf();
            case "br_cnpj":          return genBrCnpj();
            case "in_pan":           return genInPan();
            case "in_aadhaar":       return genInAadhaar();
            case "in_gstin":         return genInGstin();
            case "in_epic":          return genInEpic();
            case "cn_ric":           return genCnRic();
            case "mx_curp":          return genMxCurp();
            case "mx_rfc":           return genMxRfc();
            case "it_codicefiscale": return genItCf();
            case "es_dni":           return genEsDni();
            case "es_nie":           return genEsNie();
            case "es_ccc":           return genEsCcc();
            case "de_idnr":          return genDeIdnr();
            case "de_stnr":          return genDeStnr();
            case "pk_cnic":          return genPkCnic();
            case "jp_cn":            return genJpCn();
            case "jp_in":            return genJpIn();
            case "kr_rrn":           return genKrRrn();
            case "kr_brn":           return genKrBrn();
            case "nl_bsn":           return genNlBsn();
            case "pl_pesel":         return genPlPesel();
            case "se_personnummer":  return genSePersonnummer();
            case "dk_cpr":           return genDkCpr();
            case "fi_hetu":          return genFiHetu();
            case "no_fodselsnummer": return genNoFodselsnummer();
            case "au_abn":           return genAuAbn();
            case "au_tfn":           return genAuTfn();
            case "au_acn":           return genAuAcn();
            case "my_nric":          return genMyNric();
            case "th_pin":           return genThPin();
            case "th_tin":           return genThTin();
            case "sg_uen":           return genSgUen();
            case "za_idnr":          return genZaIdnr();
            case "ca_bn":            return genCaBn();
            case "nz_ird":           return genNzIrd();
            case "ar_cuit":          return genArCuit();
            case "ar_dni":           return genArDni();
            case "cl_rut":           return genClRut();
            case "co_nit":           return genCoNit();
            case "il_idnr":          return genIlIdnr();
            case "ro_cnp":           return genRoCnp();
            case "ro_cui":           return genRoCui();
            case "hr_oib":           return genHrOib();
            case "bg_egn":           return genBgEgn();
            case "lt_asmens":        return genLtAsmens();
            case "ee_ik":            return genEeIk();
            case "pt_cc":            return genPtCc();
            case "eg_tn":            return genEgTn();
            default:                 return "ERROR: Unknown IntlIds type '" + type + "'";
        }
    }
}
