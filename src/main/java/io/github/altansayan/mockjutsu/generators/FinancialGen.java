package io.github.altansayan.mockjutsu.generators;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/** Financial generator — mirrors financial.py algorithms (Luhn, MOD-97, IBAN). */
public final class FinancialGen {

    private FinancialGen() {}

    private static final SecureRandom SEC = new SecureRandom();

    // ── Card networks & BINs ──────────────────────────────────────────────────

    // network key -> {prefixes (each a digit-string alternative), length}
    private static final String[] NETWORK_KEYS = {"visa","mc","amex","troy","jcb","discover","unionpay","mir","maestro"};
    private static final String[][] NETWORK_PREFIXES = {
        {"4"},
        {"51","52","53","54","55"},
        {"34","37"},
        {"9792"},
        {"352","358"},
        {"6011","65"},
        {"62"},
        {"2200","2201","2202"},
        {"6304","6759"}
    };
    private static final int[] NETWORK_LENGTHS = {16,16,15,16,16,16,16,16,16};

    private static final String[] CARD_TYPES   = {"Credit","Debit"};
    private static final String[] CARD_STATUS  = {"Active","Blocked","Expired"};
    private static final String[] CARD_CATS    = {"Classic","Gold","Platinum","Business"};

    // ── Issuers ────────────────────────────────────────────────────────────────

    private static final String[] ISSUERS_TR = {"Türkbank A.Ş.", "AnadoluFinans", "BosphorusBank", "GüvenFinans", "KırmızıBanka", "Boğaz Finans"};
    private static final String[] ISSUERS_US = {"First National Bank", "Pacific Trust", "American Commerce", "Liberty Bank", "Freedom Financial"};
    private static final String[] ISSUERS_UK = {"Royal Borough Bank", "Crown Finance Trust", "London Clearing Bank", "Imperial Trust", "Commonwealth Bank"};
    private static final String[] ISSUERS_DE = {"Volksbank Nord", "Hamburger Sparkasse", "Berliner Bank", "Rhine Finance", "Saxon Trust"};
    private static final String[] ISSUERS_FR = {"Crédit Parisien", "Banque Nationale", "Société de Crédit", "Paris Finance", "Loire Bank"};
    private static final String[] ISSUERS_RU = {"Народный Банк", "Столичный Банк", "Восточный Кредит", "Русфинанс", "МоскваБанк"};

    // ── Public API ────────────────────────────────────────────────────────────

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "cardnum"      -> cardnum(rng, locale, qualifier);
            case "cardnetwork"  -> cardnetwork(rng, locale);
            case "cardtype"     -> pick(rng, CARD_TYPES);
            case "cardstatus"   -> pick(rng, CARD_STATUS);
            case "cardcategory" -> pick(rng, CARD_CATS);
            case "cvv3"         -> String.valueOf(rng.nextInt(100, 1000));
            case "cvv4"         -> String.valueOf(rng.nextInt(1000, 10000));
            case "issuer"       -> issuer(rng, locale);
            case "expiry"       -> expiry(rng);
            case "expirymonth"  -> String.format("%02d", rng.nextInt(1, 13));
            case "expiryyear"   -> String.valueOf(rng.nextInt(0, 6) + (java.time.LocalDate.now().getYear() % 100));
            case "pin"          -> String.valueOf(rng.nextInt(1000, 10000));
            case "balance"      -> balance(rng, locale, qualifier);
            case "iban"         -> iban(rng, locale);
            case "credit_score" -> creditScore(rng, locale);
            case "sepa_qr"      -> sepaQr(rng, locale);
            case "emv_qr_p2p"   -> emvQrP2p(rng, locale);
            case "emv_qr_atm"   -> emvQrAtm(rng, locale);
            case "emv_qr_pos"   -> emvQrPos(rng, locale);
            case "3ds_cavv"     -> cavv();
            case "3ds_eci"      -> eci(rng, qualifier);
            default             -> "ERROR: Unknown financial type '" + type + "'";
        };
    }

    // ── Card number (Luhn-valid) ──────────────────────────────────────────────

    static String cardnum(ThreadLocalRandom rng, String locale) {
        return cardnum(rng, locale, "");
    }

    static String cardnum(ThreadLocalRandom rng, String locale, String network) {
        String key = network.isEmpty() ? "visa" : network.toLowerCase();
        int idx = -1;
        for (int i = 0; i < NETWORK_KEYS.length; i++) if (NETWORK_KEYS[i].equals(key)) { idx = i; break; }
        if (idx < 0) idx = 0; // unknown network -> visa, matching Python's dict.get(..., CARD_NETWORKS["visa"])
        String[] prefixOptions = NETWORK_PREFIXES[idx];
        String prefix = prefixOptions[rng.nextInt(prefixOptions.length)];
        int length = NETWORK_LENGTHS[idx];
        StringBuilder sb = new StringBuilder(prefix);
        while (sb.length() < length - 1) sb.append(rng.nextInt(0, 10));
        sb.append(IdentityGen.luhnCheckDigit(sb.toString()));
        return sb.toString();
    }

    private static String cardnetwork(ThreadLocalRandom rng, String locale) {
        return NETWORK_KEYS[rng.nextInt(NETWORK_KEYS.length)].toUpperCase(java.util.Locale.ROOT);
    }

    // ── IBAN — ISO 13616 with MOD-97 check digits ────────────────────────────

    private static final String IBAN_ALNUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    static String iban(ThreadLocalRandom rng, String locale) {
        String l = locale.toUpperCase(java.util.Locale.ROOT);
        return switch (l) {
            case "TR" -> ibanGeneric(rng, "TR", 22); // pure random digits, no bank sub-structure
            case "UK" -> ibanGeneric(rng, "GB", "alpha4digit14");
            case "DE" -> ibanGeneric(rng, "DE", 18);
            case "FR" -> ibanGeneric(rng, "FR", "digit10alnum11digit2");
            case "US" -> "RT:" + BankingGen.routingNumber(rng) + " ACC:" + randomDigits(rng, 12);
            case "RU" -> "BIK:04" + randomDigits(rng, 7) + " ACC:40817" + randomDigits(rng, 15);
            default   -> ibanGeneric(rng, "TR", 22);
        };
    }

    private static String ibanGeneric(ThreadLocalRandom rng, String prefix, int digitLen) {
        String bban = randomDigits(rng, digitLen);
        String check = ibanCheckDigits(prefix, bban);
        return prefix + check + bban;
    }

    private static String ibanGeneric(ThreadLocalRandom rng, String prefix, String shape) {
        String bban;
        if ("alpha4digit14".equals(shape)) {
            bban = randomAlpha(rng, 4) + randomDigits(rng, 14);
        } else { // digit10alnum11digit2 (FR)
            bban = randomDigits(rng, 10) + randomAlnum(rng, 11) + randomDigits(rng, 2);
        }
        String check = ibanCheckDigits(prefix, bban);
        return prefix + check + bban;
    }

    private static String randomAlpha(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append((char) ('A' + rng.nextInt(26)));
        return sb.toString();
    }

    private static String randomAlnum(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(IBAN_ALNUM.charAt(rng.nextInt(IBAN_ALNUM.length())));
        return sb.toString();
    }

    static String ibanCheckDigits(String country, String bban) {
        // Rearrange: BBAN + country + "00", then convert to numeric, compute 98 - mod97
        String rearranged = bban + country + "00";
        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else numeric.append(c);
        }
        // Big number MOD 97 via chunked computation
        String numStr = numeric.toString();
        int mod = 0;
        for (int i = 0; i < numStr.length(); i++) {
            mod = (mod * 10 + (numStr.charAt(i) - '0')) % 97;
        }
        int check = 98 - mod;
        return String.format("%02d", check);
    }

    // ── Expiry ────────────────────────────────────────────────────────────────

    private static String expiry(ThreadLocalRandom rng) {
        int month = rng.nextInt(1, 13);
        int year  = (java.time.LocalDate.now().getYear() % 100) + rng.nextInt(0, 6);
        return String.format("%02d/%02d", month, year);
    }

    // ── Issuer ────────────────────────────────────────────────────────────────

    static String issuer(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> pick(rng, ISSUERS_TR);
            case "US" -> pick(rng, ISSUERS_US);
            case "DE" -> pick(rng, ISSUERS_DE);
            case "FR" -> pick(rng, ISSUERS_FR);
            case "UK" -> pick(rng, ISSUERS_UK);
            case "RU" -> pick(rng, ISSUERS_RU);
            default   -> pick(rng, ISSUERS_TR);
        };
    }

    // ── Balance ───────────────────────────────────────────────────────────────

    private static String balance(ThreadLocalRandom rng, String locale, String qualifier) {
        double min = 10.0, max = 50000.0;
        if (!qualifier.isEmpty() && qualifier.contains("|")) {
            String[] parts = qualifier.split("\\|", 2);
            try { min = Double.parseDouble(parts[0]); } catch (NumberFormatException ignored) {}
            if (parts.length > 1) try { max = Double.parseDouble(parts[1]); } catch (NumberFormatException ignored) {}
        }
        if (min >= max) max = min + 1000.0;
        return String.format(java.util.Locale.US, "%.2f", min + rng.nextDouble(max - min));
    }

    // ── Credit score ──────────────────────────────────────────────────────────

    private static String creditScore(ThreadLocalRandom rng, String locale) {
        return String.valueOf(rng.nextInt(300, 851));
    }

    // ── SEPA QR ───────────────────────────────────────────────────────────────

    private static String sepaFallbackLocale(String locale) {
        return switch (locale) { case "DE","FR","UK" -> locale; default -> "DE"; };
    }

    private static String[] namePool(String locale, String key) {
        return switch (locale + "_" + key) {
            case "TR_male" -> NameData.TR_MALE; case "TR_female" -> NameData.TR_FEMALE; case "TR_last" -> NameData.TR_LAST;
            case "US_male" -> NameData.US_MALE; case "US_female" -> NameData.US_FEMALE; case "US_last" -> NameData.US_LAST;
            case "UK_male" -> NameData.UK_MALE; case "UK_female" -> NameData.UK_FEMALE; case "UK_last" -> NameData.UK_LAST;
            case "DE_male" -> NameData.DE_MALE; case "DE_female" -> NameData.DE_FEMALE; case "DE_last" -> NameData.DE_LAST;
            case "FR_male" -> NameData.FR_MALE; case "FR_female" -> NameData.FR_FEMALE; case "FR_last" -> NameData.FR_LAST;
            case "RU_male" -> NameData.RU_MALE; case "RU_female" -> NameData.RU_FEMALE; case "RU_last" -> NameData.RU_LAST;
            default -> NameData.DE_MALE;
        };
    }

    private static String randomFullName(ThreadLocalRandom rng, String locale) {
        String first = pick(rng, namePool(locale, rng.nextBoolean() ? "male" : "female"));
        String last = pick(rng, namePool(locale, "last"));
        return first + " " + last;
    }

    private static String sepaQr(ThreadLocalRandom rng, String locale) {
        String sepaLoc = sepaFallbackLocale(locale);
        String name = randomFullName(rng, sepaLoc);
        String ibanStr = iban(rng, sepaLoc);
        String bicCc = switch (sepaLoc) { case "DE" -> "DE"; case "FR" -> "FR"; case "UK" -> "GB"; default -> "DE"; };
        String bic = randomAlpha(rng, 4) + bicCc + "2X";
        String amount = rng.nextInt(10, 1000) + "." + (rng.nextBoolean() ? "00" : "50");
        String reference = "INV-" + java.time.LocalDate.now().getYear() + "-" + rng.nextInt(1000, 10000);
        return String.format("BCD\n002\n1\nSCT\n%s\n%s\n%s\nEUR%s\n\n%s\n\n", bic, name, ibanStr, amount, reference);
    }

    // ── EMV QR ────────────────────────────────────────────────────────────────

    private static final java.util.Map<String,String> EMV_CURRENCY = java.util.Map.of(
        "TR","949", "DE","978", "FR","978", "US","840", "UK","826", "RU","643");
    private static final java.util.Map<String,String> EMV_COUNTRY = java.util.Map.of(
        "TR","TR", "DE","DE", "FR","FR", "US","US", "UK","GB", "RU","RU");
    private static final java.util.Map<String,String> EMV_CITY = java.util.Map.of(
        "TR","ISTANBUL", "DE","BERLIN", "FR","PARIS", "US","NEW YORK", "UK","LONDON", "RU","MOSCOW");

    private static String[] emvLocaleData(String locale) {
        String loc = EMV_CURRENCY.containsKey(locale) ? locale : "TR";
        return new String[]{loc, EMV_CURRENCY.get(loc), EMV_COUNTRY.get(loc)};
    }

    private static String crc16Emvco(String data) {
        int crc = 0xFFFF;
        for (int i = 0; i < data.length(); i++) {
            crc ^= (data.charAt(i) & 0xFF) << 8;
            for (int b = 0; b < 8; b++) {
                crc = ((crc & 0x8000) != 0) ? ((crc << 1) ^ 0x1021) : (crc << 1);
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }

    private static String tlv(String tag, String value) {
        return tag + String.format("%02d", value.length()) + value;
    }

    private static String emvQrP2p(ThreadLocalRandom rng, String locale) {
        String[] ld = emvLocaleData(locale);
        String loc = ld[0], currency = ld[1], countryCode = ld[2];
        String name = randomFullName(rng, "TR".equals(loc) ? "TR" : loc);
        String ibanStr = iban(rng, loc);
        String amount = rng.nextInt(10, 5000) + "." + (rng.nextBoolean() ? "00" : "50");

        String p00 = "000201";
        String p01 = "010211";
        String p53 = "5303" + currency;
        String p54 = tlv("54", amount);
        String p58 = "5802" + countryCode;
        String p59 = tlv("59", name);
        String merchInfo = tlv("01", ibanStr);
        String p26 = tlv("26", merchInfo);
        String payload = p00 + p01 + p26 + p53 + p54 + p58 + p59 + "6304";
        return payload + crc16Emvco(payload);
    }

    private static String emvQrAtm(ThreadLocalRandom rng, String locale) {
        String[] ld = emvLocaleData(locale);
        String loc = ld[0], currency = ld[1], countryCode = ld[2];
        String p00 = "000201";
        String p01 = "010212";
        String p53 = "5303" + currency;
        String p58 = "5802" + countryCode;
        String atmName = "ATM " + loc + "-" + rng.nextInt(1000, 10000);
        String p59 = tlv("59", atmName);
        String tid = "T" + rng.nextInt(1000000, 10000000);
        String token = "TOK" + rng.nextInt(10000000, 100000000);
        String tag6207 = tlv("07", tid);
        String tag6208 = tlv("08", token);
        String tag62Val = tag6207 + tag6208;
        String p62 = tlv("62", tag62Val);
        String payload = p00 + p01 + p53 + p58 + p59 + p62 + "6304";
        return payload + crc16Emvco(payload);
    }

    private static String emvQrPos(ThreadLocalRandom rng, String locale) {
        String[] ld = emvLocaleData(locale);
        String loc = ld[0], currency = ld[1], countryCode = ld[2];
        String p00 = "000201";
        String p01 = "010211";
        String mcc = String.valueOf(rng.nextInt(5000, 6000));
        String p52 = "5204" + mcc;
        String p53 = "5303" + currency;
        String[] centOpts = {"00","25","50","75"};
        String amount = rng.nextInt(10, 1000) + "." + centOpts[rng.nextInt(4)];
        String p54 = tlv("54", amount);
        String p58 = "5802" + countryCode;
        String lastName = pick(rng, namePool(loc, "last"));
        String suffix = "TR".equals(loc) ? "A.S." : "DE".equals(loc) ? "GmbH" : "US".equals(loc) ? "LLC" : "LTD";
        String merchName = lastName.toUpperCase(java.util.Locale.ROOT) + " " + suffix;
        String p59 = tlv("59", merchName);
        String city = EMV_CITY.getOrDefault(loc, "CAPITAL");
        String p60 = tlv("60", city);
        String merchId = String.valueOf(rng.nextLong(1000000000L, 10000000000L));
        String tag2601 = tlv("01", merchId);
        String p26 = tlv("26", tag2601);
        String payload = p00 + p01 + p26 + p52 + p53 + p54 + p58 + p59 + p60 + "6304";
        return payload + crc16Emvco(payload);
    }

    // ── 3DS ───────────────────────────────────────────────────────────────────

    private static String cavv() {
        byte[] bytes = new byte[20];
        SEC.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes).substring(0, 28);
    }

    private static String eci(ThreadLocalRandom rng, String network) {
        return switch (network.toLowerCase()) {
            case "mc","mastercard" -> pick(rng, new String[]{"00","01","02"});
            default -> pick(rng, new String[]{"05","06","07"}); // visa/amex/jcb/default
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static String randomDigits(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(rng.nextInt(0, 10));
        return sb.toString();
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
