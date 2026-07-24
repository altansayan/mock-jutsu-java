package io.github.altansayan.mockjutsu.generators;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** Banking — SWIFT/BIC, sort code, routing, BIK. Mirrors banking.py. */
public final class BankingGen {

    private BankingGen() {}

    // Real published BIC/SWIFT codes (mirrors banking.py BIC_CODES exactly).
    private static final String[] BIC_TR = {"TCZBTR2A","ISBKTRIS","AKBKTRIS","HLBKTRIS","TVBATR2A","DENITRIS","TEBUTRIS","GRNBTRIS"};
    private static final String[] BIC_US = {"CHASUS33","BOFAUS3N","CITIUS33","WFBIUS6S","USBKUS44","PNCCUS33","SVBKUS6S"};
    private static final String[] BIC_UK = {"BUKBGB22","HBUKGB4B","LOYDGB2L","NWBKGB2L","ABBYGB2L","RBOSGB2L","HLFXGB21"};
    private static final String[] BIC_DE = {"DEUTDEDB","COBADEFF","HYVEDEMM","GENODEFF","BELADEBE","INGDDEFF","DRESDEFF"};
    private static final String[] BIC_FR = {"BNPAFRPP","SOGEFRPP","AGRIFRPP","CRLYFRPP","CMCIFRPP","CCFRFRPP","BNPAFRPPPAC"};
    private static final String[] BIC_RU = {"SABRRUMM","VTBRRUMM","ALFARUMM","RZBSRUMM","GAZPRUMM","TICSRUMM","RAIFRU8T"};

    private static String[] bicCodesFor(String locale) {
        return switch (locale) {
            case "US" -> BIC_US; case "UK" -> BIC_UK; case "DE" -> BIC_DE;
            case "FR" -> BIC_FR; case "RU" -> BIC_RU; default -> BIC_TR;
        };
    }

    // Real published sort code pool (Pay.UK Vocalink directory).
    private static final String[] SORT_CODE_POOL = {
        "20-00-00","20-00-55","20-47-00",
        "40-00-00","40-14-26","40-02-50",
        "30-00-00","30-12-34","30-80-00",
        "60-00-01","60-70-80","60-14-73",
        "09-01-26","09-01-27","09-01-28",
        "16-00-00","16-22-33","16-44-55"
    };

    // Real published Russian BIK codes (Central Bank of Russia directory).
    private static final String[] BIK_POOL = {
        "044525225","044525187","044525593","044525700",
        "044525823","044525999","044030653","044585326"
    };

    private static final String[] BANK_NAMES_TR = {"MOCKJ Finans A.Ş.", "AnadoluFinans A.Ş.", "BosphorusBank A.Ş.", "GüvenFinans A.Ş.", "MaviBank A.Ş."};
    private static final String[] BANK_NAMES_US = {"MOCKJ Federal Bank", "Pacific Trust Bank", "Liberty National Bank", "Freedom Financial", "Pioneer Bank"};
    private static final String[] BANK_NAMES_UK = {"MOCKJ Crown Bank", "Royal Borough Bank", "Crown Finance Trust", "London Clearing Bank", "Imperial Trust"};
    private static final String[] BANK_NAMES_DE = {"MOCKJ Deutsche Finans", "Volksbank Nord GmbH", "Rheinische Sparkasse", "Berliner Finanzbank", "Saxon Trust AG"};
    private static final String[] BANK_NAMES_FR = {"MOCKJ Paris Banque", "Crédit Parisien SARL", "Banque Nationale Libre", "Loire Finance SA", "Paris Finance SA"};
    private static final String[] BANK_NAMES_RU = {"MOCKJ Народный Банк", "Столичный Банк АО", "Восточный Кредит ООО", "Русфинанс АО", "МоскваБанк ПАО"};

    private static final String[] TX_DESC_TR = {
        "Fatura ödemesi","Market alışverişi","FAST transferi","EFT havalesi",
        "Kredi kartı ödemesi","Kira ödemesi","Sigorta primi","Maaş ödemesi"
    };
    private static final String[] TX_DESC_US = {
        "Utility payment","Online purchase","Wire transfer","ACH payment",
        "Rent payment","Credit card payment","Payroll deposit","Insurance premium"
    };
    private static final String[] TX_DESC_UK = {
        "Utility bill payment","Online shopping","Faster Payment","Standing order",
        "Direct debit","Salary credit","Mortgage payment","Council tax"
    };
    private static final String[] TX_DESC_DE = {
        "Rechnung bezahlen","Online-Einkauf","SEPA-Überweisung","Dauerauftrag",
        "Lastschrift","Gehalt","Miete","Versicherungsprämie"
    };
    private static final String[] TX_DESC_FR = {
        "Paiement facture","Achat en ligne","Virement SEPA","Prélèvement automatique",
        "Loyer","Salaire","Assurance","Remboursement"
    };
    private static final String[] TX_DESC_RU = {
        "Оплата услуг ЖКХ","Покупка в интернете","Перевод СБП","Оплата кредита",
        "Аренда квартиры","Зарплата","Страховой взнос","Возврат средств"
    };

    private static final String[] TX_CHAN_TR = {"FAST","EFT","Havale","SWIFT","Kart"};
    private static final String[] TX_CHAN_US = {"ACH","Wire","Zelle","SWIFT","Check"};
    private static final String[] TX_CHAN_UK = {"Faster Payments","BACS","CHAPS","SWIFT"};
    private static final String[] TX_CHAN_DE = {"SEPA Credit Transfer","SEPA Direct Debit","SWIFT","Lastschrift"};
    private static final String[] TX_CHAN_FR = {"Virement SEPA","Prélèvement SEPA","SWIFT","TIP"};
    private static final String[] TX_CHAN_RU = {"СБП","Межбанк","SWIFT","Карточный перевод"};

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String[] ACCOUNT_TYPES  = {"Checking","Savings","Current","Business Checking","Money Market","CD","Investment"};
    private static final String[] TX_TYPES       = {"CREDIT","DEBIT","TRANSFER","REFUND","REVERSAL","CHARGEBACK","FEE","INTEREST"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "swift","bic"           -> bic(rng, locale, false);
            case "sort_code"             -> sortCode(rng);
            case "routing_number"        -> routingNumber(rng);
            case "wire_routing_number"   -> routingNumber(rng);
            case "bik_code"              -> bikCode(rng);
            case "transaction"           -> transaction(rng, locale);
            case "bank_name"             -> bankName(rng, locale);
            case "sepa_ref"              -> sepaRef(rng);
            case "creditor_ref"          -> creditorRef(rng);
            case "account_type"          -> pick(rng, ACCOUNT_TYPES);
            case "transaction_type"      -> pick(rng, TX_TYPES);
            case "transaction_description" -> transactionDescription(rng, locale);
            case "ifsc_code"             -> ifscCode(rng);
            case "bsb_code"              -> bsbCode(rng);
            case "check_number"          -> String.format("%04d", rng.nextInt(1, 10000));
            case "micr_line"             -> micrLine(rng);
            case "payment_reference"     -> paymentReference(rng);
            case "account_number"        -> accountNumber(rng);
            case "account_number_masked"             -> "****" + randomDigits(rng, 4);
            // PCI-DSS §3.3 / GLBA masked variants
            case "micr_line_masked"                  -> micrLineMasked(rng);
            case "transaction_description_masked"    -> transactionDescriptionMasked(rng, locale);
            case "check_number_masked"               -> checkNumberMasked(rng);
            case "payment_reference_masked"          -> paymentReferenceMasked();
            default                                  -> "ERROR: Unknown banking type '" + type + "'";
        };
    }

    // ── BIC / SWIFT (ISO 9362) ────────────────────────────────────────────────

    static String bic(ThreadLocalRandom rng, String locale, boolean strict) {
        return pick(rng, bicCodesFor(locale));
    }

    // ── UK Sort Code — real published pool ───────────────────────────────────

    private static String sortCode(ThreadLocalRandom rng) {
        return pick(rng, SORT_CODE_POOL);
    }

    // ── US ABA Routing Number (9 digits with check digit) ────────────────────

    private static final String[] ABA_DISTRICTS = {
        "01","02","03","04","05","06","07","08","09","10","11","12",
        "21","22","23","24","25","26","27","28","29","30","31","32",
        "61","62","63","64","65","66","67","68","69","70","71","72","80"
    };

    static String routingNumber(ThreadLocalRandom rng) {
        // ABA check digit: (3*(d0+d3+d6) + 7*(d1+d4+d7) + (d2+d5+d8)) % 10 == 0
        String district = ABA_DISTRICTS[rng.nextInt(ABA_DISTRICTS.length)];
        int[] d = new int[9];
        d[0] = district.charAt(0) - '0';
        d[1] = district.charAt(1) - '0';
        for (int i = 2; i < 8; i++) d[i] = rng.nextInt(0, 10);
        int partial = 3*(d[0]+d[3]+d[6]) + 7*(d[1]+d[4]+d[7]) + (d[2]+d[5]);
        d[8] = (10 - (partial % 10)) % 10;
        StringBuilder sb = new StringBuilder(9);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    // ── Russian BIK — real published pool ────────────────────────────────────

    private static String bikCode(ThreadLocalRandom rng) {
        return pick(rng, BIK_POOL);
    }

    // ── Transaction — JSON dict matching banking.py output ───────────────────

    private static String transaction(ThreadLocalRandom rng, String locale) {
        String ccy = switch (locale) { case "DE","FR" -> "EUR"; case "UK" -> "GBP"; case "US" -> "USD"; case "RU" -> "RUB"; default -> "TRY"; };

        // Three-tier amount distribution: micro / normal / large
        int tier = rng.nextInt(10);
        double amount;
        if (tier == 0) {
            amount = Math.round((0.01 + rng.nextInt(100) / 100.0) * 100) / 100.0;
        } else if (tier <= 8) {
            amount = Math.round((5.0 + rng.nextDouble() * 9994.99) * 100) / 100.0;
        } else {
            amount = 100000.0 + rng.nextInt(900000);
        }

        // Timestamp: within last 7 days, UTC
        OffsetDateTime ts = OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(rng.nextLong(0, 7L * 24 * 3600));
        String timestamp = ts.format(TS_FMT);

        // Reference: MOCKJ-TRN{YYYYMMDD}-{10000-99999}
        String ref = "MOCKJ-TRN" + LocalDate.now().format(DATE_FMT) + "-" + (rng.nextInt(90000) + 10000);

        // IBAN (non-US) or routing (US)
        String senderIban, receiverIban;
        if ("US".equals(locale)) {
            senderIban   = "RT:" + routingNumber(rng);
            receiverIban = "RT:" + routingNumber(rng);
        } else {
            senderIban   = simpleIban(rng, locale);
            receiverIban = simpleIban(rng, locale);
        }

        // Description and channel pools
        String[] descs = switch (locale) {
            case "US" -> TX_DESC_US; case "UK" -> TX_DESC_UK; case "DE" -> TX_DESC_DE;
            case "FR" -> TX_DESC_FR; case "RU" -> TX_DESC_RU; default   -> TX_DESC_TR;
        };
        String[] chans = switch (locale) {
            case "US" -> TX_CHAN_US; case "UK" -> TX_CHAN_UK; case "DE" -> TX_CHAN_DE;
            case "FR" -> TX_CHAN_FR; case "RU" -> TX_CHAN_RU; default   -> TX_CHAN_TR;
        };

        // Status: 80% COMPLETED, 15% PENDING, 5% FAILED
        int r = rng.nextInt(20);
        String status = r < 16 ? "COMPLETED" : r < 19 ? "PENDING" : "FAILED";

        return String.format(java.util.Locale.US,
            "{\"ref\":\"%s\",\"sender_iban\":\"%s\",\"receiver_iban\":\"%s\",\"amount\":%.2f," +
            "\"currency\":\"%s\",\"description\":\"%s\",\"channel\":\"%s\",\"timestamp\":\"%s\",\"status\":\"%s\"}",
            ref, senderIban, receiverIban, amount, ccy,
            pick(rng, descs).replace("\"", "\\\""),
            pick(rng, chans), timestamp, status
        );
    }

    // ── Simple IBAN — pure random digit body, no bank sub-structure ──────────
    // (mirrors banking.py's own _generate_iban, distinct from financial.py's
    // structured generate_bank_account used by the top-level "iban" type)

    private static String simpleIban(ThreadLocalRandom rng, String locale) {
        String prefix; int len;
        switch (locale) {
            case "UK" -> { prefix = "GB"; len = 22; }
            case "DE" -> { prefix = "DE"; len = 22; }
            case "FR" -> { prefix = "FR"; len = 27; }
            default   -> { prefix = "TR"; len = 26; }
        }
        int bodyLen = len - prefix.length() - 2;
        String body = randomDigits(rng, bodyLen);
        String check = FinancialGen.ibanCheckDigits(prefix, body);
        return prefix + check + body;
    }

    // ── Bank name ─────────────────────────────────────────────────────────────

    private static String bankName(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> pick(rng, BANK_NAMES_TR);
            case "DE" -> pick(rng, BANK_NAMES_DE);
            case "FR" -> pick(rng, BANK_NAMES_FR);
            case "UK" -> pick(rng, BANK_NAMES_UK);
            case "US" -> pick(rng, BANK_NAMES_US);
            case "RU" -> pick(rng, BANK_NAMES_RU);
            default   -> pick(rng, BANK_NAMES_TR);
        };
    }

    // ── SEPA Reference (ISO 11649 creditor reference) ────────────────────────

    private static String sepaRef(ThreadLocalRandom rng) {
        int len = rng.nextInt(10, 20);
        return "MOCKJ-E2E-" + randomAlphaNum(rng, len);
    }

    // ── Creditor Reference (ISO 11649) ────────────────────────────────────────

    private static String creditorRef(ThreadLocalRandom rng) {
        int length = rng.nextInt(5, 15); // 5-14
        String body = "MOCKJ" + randomAlphaNum(rng, length);
        String check = FinancialGen.ibanCheckDigits("RF", body);
        return "RF" + check + body;
    }

    // ── IFSC Code (India) — real published bank codes ────────────────────────

    private static final String[] IFSC_BANK_CODES = {
        "SBIN","HDFC","ICIC","AXIS","KKBK","UTIB","PUNB",
        "CNRB","BARB","UBIN","IOBA","CBIN","BKID","VIJB"
    };

    private static String ifscCode(ThreadLocalRandom rng) {
        return pick(rng, IFSC_BANK_CODES) + "0" + randomAlphaNum(rng, 6);
    }

    // ── BSB Code (Australia) — real bank prefixes ────────────────────────────

    private static final String[] BSB_BANK_CODES = {"01","03","06","08","73","76"};

    private static String bsbCode(ThreadLocalRandom rng) {
        String bankPrefix = pick(rng, BSB_BANK_CODES);
        int branch = rng.nextInt(0, 1000);
        return bankPrefix + rng.nextInt(0, 10) + "-" + String.format("%03d", branch);
    }

    // ── MICR Line ─────────────────────────────────────────────────────────────

    private static String micrLine(ThreadLocalRandom rng) {
        String rt  = routingNumber(rng);
        int acctLen = rng.nextInt(8, 13);
        String acct = randomDigits(rng, acctLen);
        String chk  = String.format("%04d", rng.nextInt(1, 10000));
        return "|" + rt + "| |" + acct + "| " + chk;
    }

    // ── Payment Reference ─────────────────────────────────────────────────────

    private static String paymentReference(ThreadLocalRandom rng) {
        String datePart = java.time.LocalDate.now().format(DATE_FMT);
        int seq = rng.nextInt(10000, 100000);
        return "PAYREF-" + datePart + "-" + seq;
    }

    // ── Account Number ────────────────────────────────────────────────────────

    private static String accountNumber(ThreadLocalRandom rng) {
        int len = rng.nextInt(8, 13); // 8-12 digits, leading zero allowed
        return randomDigits(rng, len);
    }

    private static String randomDigits(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(rng.nextInt(0, 10));
        return sb.toString();
    }

    // ── Transaction Description ───────────────────────────────────────────────

    private static String transactionDescription(ThreadLocalRandom rng, String locale) {
        String[] descs = switch (locale) {
            case "US" -> TX_DESC_US; case "UK" -> TX_DESC_UK; case "DE" -> TX_DESC_DE;
            case "FR" -> TX_DESC_FR; case "RU" -> TX_DESC_RU; default   -> TX_DESC_TR;
        };
        return pick(rng, descs);
    }

    // ── Masked Variants ───────────────────────────────────────────────────────

    private static String micrLineMasked(ThreadLocalRandom rng) {
        // PCI-DSS §3.3: routing number is public (ABA directory), account segment masked
        String rt  = routingNumber(rng);
        String chk = String.format("%04d", rng.nextInt(1, 10000));
        return "|" + rt + "| |****| " + chk;
    }

    private static String transactionDescriptionMasked(ThreadLocalRandom rng, String locale) {
        // GDPR Art. 5(1)(c) data minimisation: truncate free-text to 10 chars
        String desc = transactionDescription(rng, locale);
        if (desc.length() > 10) return desc.substring(0, 10) + "***";
        return desc + "***";
    }

    private static String checkNumberMasked(ThreadLocalRandom rng) {
        // Mask check sequence — last 2 digits visible
        int n = rng.nextInt(1, 10000);
        String last2 = String.format("%04d", n).substring(2);
        return "**" + last2;
    }

    private static String paymentReferenceMasked() {
        // GLBA best practice: date segment not sensitive, sequence masked
        String datePart = java.time.LocalDate.now().format(DATE_FMT);
        return "PAYREF-" + datePart + "-*****";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String randomAlphaNum(ThreadLocalRandom rng, int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
