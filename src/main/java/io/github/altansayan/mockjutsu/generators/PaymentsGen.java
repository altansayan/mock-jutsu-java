package io.github.altansayan.mockjutsu.generators;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** Payments — SWIFT MT103, PAIN.001, NACHA ACH, SEPA Mandate, Fedwire. Mirrors payments.py. */
public final class PaymentsGen {
    private PaymentsGen() {}

    // ── Shared: MOCKJ fictional BIC codes ────────────────────────────────────

    private static final String[] MOCKJ_BICS = {
        "MOCKDE01","MOCKDE02","MOCKDE03","MOCKGB01","MOCKGB02","MOCKFR01","MOCKFR02",
        "MOCKNL01","MOCKAT01","MOCKES01","MOCKIT01","MOCKBE01"
    };
    private static final String[] MOCKJ_BICS_STRICT = {
        "MOCKDE00","MOCKGB00","MOCKFR00","MOCKNL00","MOCKAT00","MOCKES00","MOCKIT00","MOCKBE00"
    };

    private static String[] bicPool(boolean strict) {
        return strict ? MOCKJ_BICS_STRICT : MOCKJ_BICS;
    }

    private static final java.util.Map<String, String> SWIFT_CCY = java.util.Map.of(
        "TR","TRY","US","USD","UK","GBP","DE","EUR","FR","EUR","RU","RUB"
    );
    private static final String[] SWIFT_23B = {"CRED","CRTS","SPAY","SPRI","SSTD"};
    private static final String[] SWIFT_71A = {"OUR","SHA","BEN"};
    private static final String SWIFT_REF_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int[] ABA_PREFIXES = buildAbaPrefixes();
    private static int[] buildAbaPrefixes() {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 12; i++) list.add(i);
        for (int i = 21; i <= 32; i++) list.add(i);
        for (int i = 61; i <= 72; i++) list.add(i);
        list.add(80);
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
    }

    private static final java.util.Map<String, Integer> IBAN_BBAN_LEN = new java.util.LinkedHashMap<>();
    static {
        IBAN_BBAN_LEN.put("DE", 18); IBAN_BBAN_LEN.put("GB", 18);
        IBAN_BBAN_LEN.put("FR", 23); IBAN_BBAN_LEN.put("NL", 14);
        IBAN_BBAN_LEN.put("BE", 12); IBAN_BBAN_LEN.put("AT", 16);
        IBAN_BBAN_LEN.put("ES", 20); IBAN_BBAN_LEN.put("IT", 23);
    }
    private static final String[] PAIN001_COUNTRIES = IBAN_BBAN_LEN.keySet().toArray(new String[0]);

    private static final String[] ACH_SEC_CODES = {"PPD","CCD","CTX","WEB","TEL"};
    private static final String[] SEPA_COUNTRIES = {"DE","FR","NL","BE","AT","ES"};
    private static final String[] SEPA_SEQUENCES = {"FRST","RCUR","FNAL","OOFF"};
    private static final String[] FEDWIRE_TYPE_CODES = {"10","15","16"};
    private static final String[] FEDWIRE_BFC = {"CTR","BTR","DEP"};

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        boolean strict = "strict".equalsIgnoreCase(qualifier == null ? "" : qualifier.trim());
        return switch (type) {
            case "swift_mt103"  -> mt103(rng, locale, strict);
            case "pain001"      -> pain001(rng, strict);
            case "nacha_ach"    -> nachaAch(rng);
            case "sepa_mandate" -> sepaMandate(rng, strict);
            case "fedwire"      -> fedwire(rng);
            default -> "ERROR: Unknown payments type '" + type + "'";
        };
    }

    private static String swiftRef(ThreadLocalRandom rng) {
        int k = rng.nextInt(8, 17);
        StringBuilder sb = new StringBuilder(k);
        for (int i = 0; i < k; i++) sb.append(SWIFT_REF_CHARS.charAt(rng.nextInt(SWIFT_REF_CHARS.length())));
        return sb.toString();
    }

    private static String randomHexUpper(ThreadLocalRandom rng, int n) {
        String hex = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(n * 2);
        for (int i = 0; i < n * 2; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static String padRight(String s, int len) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < len) sb.append(' ');
        return sb.toString();
    }

    private static String truncate(String s, int len) {
        return s.length() > len ? s.substring(0, len) : s;
    }

    // ── SWIFT MT103 ───────────────────────────────────────────────────────────

    private static String mt103(ThreadLocalRandom rng, String locale, boolean strict) {
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        String ccy = SWIFT_CCY.getOrDefault(loc, "USD");
        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyMMdd"));
        String amount = rng.nextInt(100, 10000000) + "," + String.format("%02d", rng.nextInt(100));
        String ref = truncate("MOCKJ-" + swiftRef(rng), 16);

        String[] pool = bicPool(strict);
        String senderBic = pool[rng.nextInt(pool.length)];
        String receiverBic;
        do { receiverBic = pool[rng.nextInt(pool.length)]; } while (receiverBic.equals(senderBic));

        String b1 = "1:F01" + senderBic + "AXXX0000000000";
        String b2 = "2:I103" + receiverBic + "XXXN";

        String orderingAcc = "/ACC" + rng.nextInt(10000000, 100000000);
        String beneficiaryAcc = "/ACC" + rng.nextInt(10000000, 100000000);

        String[] tags = {
            ":20:" + ref,
            ":23B:" + SWIFT_23B[rng.nextInt(SWIFT_23B.length)],
            ":32A:" + date + ccy + amount,
            ":50K:" + orderingAcc,
            "MOCKJ CORP " + rng.nextInt(100, 1000),
            ":59:" + beneficiaryAcc,
            "MOCKJ BENE " + rng.nextInt(100, 1000),
            ":71A:" + SWIFT_71A[rng.nextInt(SWIFT_71A.length)]
        };
        String b4 = "4:\n" + String.join("\n", tags) + "\n-";
        String b5 = "5:{CHK:" + randomHexUpper(rng, 6) + "}";

        return "{" + b1 + "}{" + b2 + "}{" + b4 + "}{" + b5 + "}";
    }

    // ── ISO 20022 Pain.001 ────────────────────────────────────────────────────

    private static String randomIban(ThreadLocalRandom rng, String country) {
        int bbanLen = IBAN_BBAN_LEN.getOrDefault(country, 18);
        StringBuilder bban = new StringBuilder(bbanLen);
        for (int i = 0; i < bbanLen; i++) bban.append(rng.nextInt(10));
        String check = FinancialGen.ibanCheckDigits(country, bban.toString());
        return country + check + bban;
    }

    private static String pain001(ThreadLocalRandom rng, boolean strict) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String msgId = "MOCKJ-PAIN-" + randomHexUpper(rng, 4);
        String endToEndId = "MOCKJ-E2E-" + randomHexUpper(rng, 4);
        String creationDt = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String reqExecutionDt = now.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String debtorCountry = PAIN001_COUNTRIES[rng.nextInt(PAIN001_COUNTRIES.length)];
        String creditorCountry = PAIN001_COUNTRIES[rng.nextInt(PAIN001_COUNTRIES.length)];
        String debtorIban = randomIban(rng, debtorCountry);
        String creditorIban = randomIban(rng, creditorCountry);
        String[] pool = bicPool(strict);
        String debtorBic = pool[rng.nextInt(pool.length)];
        String creditorBic;
        do { creditorBic = pool[rng.nextInt(pool.length)]; } while (creditorBic.equals(debtorBic));
        String amount = rng.nextInt(1, 100000) + "." + String.format("%02d", rng.nextInt(100));

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09\">\n" +
            "  <CstmrCdtTrfInitn>\n" +
            "    <GrpHdr>\n" +
            "      <MsgId>" + msgId + "</MsgId>\n" +
            "      <CreDtTm>" + creationDt + "</CreDtTm>\n" +
            "      <NbOfTxs>1</NbOfTxs>\n" +
            "      <CtrlSum>" + amount + "</CtrlSum>\n" +
            "      <InitgPty>\n" +
            "        <Nm>MOCKJ CORP " + rng.nextInt(10, 100) + "</Nm>\n" +
            "      </InitgPty>\n" +
            "    </GrpHdr>\n" +
            "    <PmtInf>\n" +
            "      <PmtInfId>MOCKJ-PMT-" + randomHexUpper(rng, 4) + "</PmtInfId>\n" +
            "      <PmtMtd>TRF</PmtMtd>\n" +
            "      <PmtTpInf>\n" +
            "        <SvcLvl><Cd>SEPA</Cd></SvcLvl>\n" +
            "      </PmtTpInf>\n" +
            "      <ReqdExctnDt><Dt>" + reqExecutionDt + "</Dt></ReqdExctnDt>\n" +
            "      <Dbtr>\n" +
            "        <Nm>MOCKJ DEBTOR " + rng.nextInt(100, 1000) + "</Nm>\n" +
            "      </Dbtr>\n" +
            "      <DbtrAcct>\n" +
            "        <Id><IBAN>" + debtorIban + "</IBAN></Id>\n" +
            "      </DbtrAcct>\n" +
            "      <DbtrAgt>\n" +
            "        <FinInstnId><BICFI>" + debtorBic + "</BICFI></FinInstnId>\n" +
            "      </DbtrAgt>\n" +
            "      <ChrgBr>SLEV</ChrgBr>\n" +
            "      <CdtTrfTxInf>\n" +
            "        <PmtId>\n" +
            "          <EndToEndId>" + endToEndId + "</EndToEndId>\n" +
            "        </PmtId>\n" +
            "        <Amt>\n" +
            "          <InstdAmt Ccy=\"EUR\">" + amount + "</InstdAmt>\n" +
            "        </Amt>\n" +
            "        <CdtrAgt>\n" +
            "          <FinInstnId><BICFI>" + creditorBic + "</BICFI></FinInstnId>\n" +
            "        </CdtrAgt>\n" +
            "        <Cdtr><Nm>MOCKJ BENE " + rng.nextInt(100, 1000) + "</Nm></Cdtr>\n" +
            "        <CdtrAcct>\n" +
            "          <Id><IBAN>" + creditorIban + "</IBAN></Id>\n" +
            "        </CdtrAcct>\n" +
            "      </CdtTrfTxInf>\n" +
            "    </PmtInf>\n" +
            "  </CstmrCdtTrfInitn>\n" +
            "</Document>";
    }

    // ── NACHA ACH ─────────────────────────────────────────────────────────────

    private static String achRouting(ThreadLocalRandom rng) {
        int prefix = ABA_PREFIXES[rng.nextInt(ABA_PREFIXES.length)];
        String routing8 = String.format("%02d%06d", prefix, rng.nextInt(1000000));
        int[] weights = {3, 7, 1, 3, 7, 1, 3, 7};
        int total = 0;
        for (int i = 0; i < 8; i++) total += (routing8.charAt(i) - '0') * weights[i];
        int check = (10 - (total % 10)) % 10;
        return routing8 + check;
    }

    private static String nachaAch(ThreadLocalRandom rng) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String fileDate = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String fileTime = now.format(DateTimeFormatter.ofPattern("HHmm"));

        String odfiRouting = achRouting(rng);
        String rdfiRouting = achRouting(rng);
        String secCode = ACH_SEC_CODES[rng.nextInt(ACH_SEC_CODES.length)];
        String companyName = "MOCKJ CORP " + rng.nextInt(10, 100);
        String companyId = "1" + rng.nextInt(100000000, 1000000000);
        String batchNum = String.format("%07d", rng.nextInt(1, 10000000));
        int amountCents = rng.nextInt(100, 10000000);
        String amountStr = String.format("%010d", amountCents);
        String indivName = "MOCKJ USER " + rng.nextInt(100, 1000);
        String indivId = truncate("MOCKJ-" + rng.nextInt(100000, 1000000), 15);
        String traceNum = odfiRouting.substring(0, 8) + String.format("%08d", rng.nextInt(1, 100000000));
        String accountNum = padRight(String.valueOf(rng.nextLong(10000000000L, 100000000000L)), 17);

        String entry = "6" + "22" + rdfiRouting + accountNum + amountStr +
            padRight(indivId, 15) + padRight(truncate(indivName, 22), 22) + " " + "1" + traceNum;

        String addendaInfo = truncate(padRight("MOCKJ PAYMENT REF " + randomHexUpper(rng, 4), 80), 80);
        String addenda = "7" + "05" + addendaInfo + "0001" + traceNum.substring(traceNum.length() - 7);

        long rdfiFirst8 = Long.parseLong(rdfiRouting.substring(0, 8));
        String hashTotal = String.format("%010d", rdfiFirst8 % 10000000000L);

        String dest = padRight(" " + odfiRouting, 10);
        String origin = padRight(truncate(companyId, 10), 10);
        String fileHeader = "1" + "01" + dest + origin + fileDate + fileTime + "A" + "094" + "10" + "1" +
            padRight("MOCKJ FEDERAL BANK", 23) + padRight(truncate(companyName, 23), 23) + " ".repeat(8);

        String batchHeader = "5" + "200" + padRight(truncate(companyName, 16), 16) + " ".repeat(20) +
            padRight(truncate(companyId, 10), 10) + secCode + "PAYMENT   " + " ".repeat(6) +
            fileDate + " ".repeat(3) + "1" + odfiRouting.substring(0, 8) + batchNum;

        String amountCents12 = String.format("%012d", amountCents);
        String batchControl = "8" + "200" + "000002" + hashTotal + amountCents12 + amountCents12 +
            padRight(truncate(companyId, 10), 10) + " ".repeat(19) + " ".repeat(6) +
            odfiRouting.substring(0, 8) + batchNum;

        String fileControl = "9" + "000001" + "000001" + "00000002" + hashTotal +
            amountCents12 + amountCents12 + " ".repeat(39);

        java.util.List<String> records = new java.util.ArrayList<>(java.util.List.of(
            fileHeader, batchHeader, entry, addenda, batchControl, fileControl));
        while (records.size() % 10 != 0) records.add("9".repeat(94));

        return String.join("\n", records);
    }

    // ── SEPA Direct Debit Mandate ─────────────────────────────────────────────

    private static String sepaCreditorId(ThreadLocalRandom rng, String country) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder bizCode = new StringBuilder(3);
        for (int i = 0; i < 3; i++) bizCode.append(letters.charAt(rng.nextInt(26)));
        String alnum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int natLen = rng.nextInt(6, 12);
        StringBuilder natId = new StringBuilder(natLen);
        for (int i = 0; i < natLen; i++) natId.append(alnum.charAt(rng.nextInt(alnum.length())));
        String body = bizCode.toString() + natId;
        String check = FinancialGen.ibanCheckDigits(country, body);
        return country + check + body;
    }

    private static String sepaMandate(ThreadLocalRandom rng, boolean strict) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String msgId = "MOCKJ-SDD-" + randomHexUpper(rng, 4);
        String mandateRef = "UMR-MOCKJ-" + randomHexUpper(rng, 4);
        String creationDt = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String country = SEPA_COUNTRIES[rng.nextInt(SEPA_COUNTRIES.length)];
        String debtorIban = randomIban(rng, country);
        String[] pool = bicPool(strict);
        String debtorBic = pool[rng.nextInt(pool.length)];
        String creditorId = sepaCreditorId(rng, country);
        String amount = rng.nextInt(10, 5001) + "." + String.format("%02d", rng.nextInt(100));

        String reqColltnDt = now.plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String todayDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.08\">\n" +
            "  <CstmrDrctDbtInitn>\n" +
            "    <GrpHdr>\n" +
            "      <MsgId>" + msgId + "</MsgId>\n" +
            "      <CreDtTm>" + creationDt + "</CreDtTm>\n" +
            "      <NbOfTxs>1</NbOfTxs>\n" +
            "      <InitgPty>\n" +
            "        <Nm>MOCKJ CORP " + rng.nextInt(10, 100) + "</Nm>\n" +
            "      </InitgPty>\n" +
            "    </GrpHdr>\n" +
            "    <PmtInf>\n" +
            "      <PmtInfId>MOCKJ-SDD-PMT-" + randomHexUpper(rng, 4) + "</PmtInfId>\n" +
            "      <PmtMtd>DD</PmtMtd>\n" +
            "      <NbOfTxs>1</NbOfTxs>\n" +
            "      <PmtTpInf>\n" +
            "        <SvcLvl><Cd>SEPA</Cd></SvcLvl>\n" +
            "        <LclInstrm><Cd>CORE</Cd></LclInstrm>\n" +
            "        <SeqTp>" + SEPA_SEQUENCES[rng.nextInt(SEPA_SEQUENCES.length)] + "</SeqTp>\n" +
            "      </PmtTpInf>\n" +
            "      <ReqdColltnDt>" + reqColltnDt + "</ReqdColltnDt>\n" +
            "      <Cdtr>\n" +
            "        <Nm>MOCKJ CREDITOR " + rng.nextInt(100, 1000) + "</Nm>\n" +
            "      </Cdtr>\n" +
            "      <CdtrAcct>\n" +
            "        <Id><IBAN>" + randomIban(rng, country) + "</IBAN></Id>\n" +
            "      </CdtrAcct>\n" +
            "      <CdtrAgt>\n" +
            "        <FinInstnId><BICFI>" + pool[rng.nextInt(pool.length)] + "</BICFI></FinInstnId>\n" +
            "      </CdtrAgt>\n" +
            "      <CdtrSchmeId>\n" +
            "        <Id><PrvtId><Othr><Id>" + creditorId + "</Id><SchmeNm><Prtry>SEPA</Prtry></SchmeNm></Othr></PrvtId></Id>\n" +
            "      </CdtrSchmeId>\n" +
            "      <DrctDbtTxInf>\n" +
            "        <PmtId>\n" +
            "          <EndToEndId>MOCKJ-E2E-" + randomHexUpper(rng, 4) + "</EndToEndId>\n" +
            "        </PmtId>\n" +
            "        <InstdAmt Ccy=\"EUR\">" + amount + "</InstdAmt>\n" +
            "        <DrctDbtTx>\n" +
            "          <MndtRltdInf>\n" +
            "            <MndtId>" + mandateRef + "</MndtId>\n" +
            "            <DtOfSgntr>" + todayDate + "</DtOfSgntr>\n" +
            "          </MndtRltdInf>\n" +
            "        </DrctDbtTx>\n" +
            "        <DbtrAgt>\n" +
            "          <FinInstnId><BICFI>" + debtorBic + "</BICFI></FinInstnId>\n" +
            "        </DbtrAgt>\n" +
            "        <Dbtr>\n" +
            "          <Nm>MOCKJ DEBTOR " + rng.nextInt(100, 1000) + "</Nm>\n" +
            "        </Dbtr>\n" +
            "        <DbtrAcct>\n" +
            "          <Id><IBAN>" + debtorIban + "</IBAN></Id>\n" +
            "        </DbtrAcct>\n" +
            "      </DrctDbtTxInf>\n" +
            "    </PmtInf>\n" +
            "  </CstmrDrctDbtInitn>\n" +
            "</Document>";
    }

    // ── Fedwire Funds Transfer ────────────────────────────────────────────────

    private static String fedwire(ThreadLocalRandom rng) {
        String date = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String senderRef = truncate("MOCKJ-" + swiftRef(rng), 16);
        String frbLineId = String.format("%08d", rng.nextInt(10000000, 100000000));
        String sequence = String.format("%06d", rng.nextInt(100000, 1000000));
        String imad = date + frbLineId + sequence;
        int amountCents = rng.nextInt(1, 100000000);
        String amountStr = String.format("%012d", amountCents);
        String senderRouting = achRouting(rng);
        String receiverRouting = achRouting(rng);
        String typeCode = FEDWIRE_TYPE_CODES[rng.nextInt(FEDWIRE_TYPE_CODES.length)];

        String[] lines = {
            "{1500}" + senderRef,
            "{1510}" + typeCode + "00",
            "{1520}" + imad,
            "{2000}" + amountStr,
            "{3100}" + senderRouting + "MOCKJSNDR",
            "{3400}" + receiverRouting + "MOCKJRCVR",
            "{3600}" + FEDWIRE_BFC[rng.nextInt(FEDWIRE_BFC.length)],
            "{4200}MOCKJ BENE " + rng.nextInt(100, 1000)
        };
        return String.join("\n", lines);
    }
}
