package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** SWIFT MT940 / ISO 20022 CAMT.053 bank statement generator. Mirrors bank_statement.py. */
public final class BankStatementGen {
    private BankStatementGen() {}

    private static final java.util.Map<String, String> CURRENCIES = java.util.Map.of(
        "TR","TRY","DE","EUR","FR","EUR","UK","GBP","US","USD","RU","RUB"
    );

    private static final String[] TX_DESCRIPTIONS = {
        "TRANSFER FROM CUSTOMER","INCOMING WIRE TRANSFER","SALARY PAYMENT",
        "VENDOR PAYMENT","ATM WITHDRAWAL","CARD PAYMENT","DIRECT DEBIT",
        "STANDING ORDER","TAX PAYMENT","UTILITY BILL","INSURANCE PREMIUM",
        "LOAN REPAYMENT","RENT PAYMENT","INVOICE SETTLEMENT"
    };

    private static final String[] MT940_TX_CODES = {"NTRN","NTRF","NCHK","NDDP","NFEX"};

    private static final String NS = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.02";

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "mt940"   -> generateMt940(rng, locale);
            case "camt053" -> generateCamt053(rng, locale);
            default -> "ERROR: Unknown bank statement type '" + type + "'";
        };
    }

    // ── IBAN helper (own algorithm — NOT FinancialGen.iban) ──────────────────

    private static String mockIban(ThreadLocalRandom rng, String locale) {
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        if ("US".equals(loc)) return randomDigits(rng, 12);
        String country;
        String bban;
        switch (loc) {
            case "TR" -> { country = "TR"; bban = randomDigits(rng, 5) + randomDigits(rng, 1) + randomDigits(rng, 16); }
            case "DE" -> { country = "DE"; bban = randomDigits(rng, 8) + randomDigits(rng, 10); }
            case "FR" -> { country = "FR"; bban = randomDigits(rng, 5) + randomDigits(rng, 5) + randomDigits(rng, 11) + randomDigits(rng, 2); }
            case "UK" -> { country = "GB"; bban = randomAlpha(rng, 4) + randomDigits(rng, 6) + randomDigits(rng, 8); }
            default   -> { country = "RU"; bban = randomDigits(rng, 3) + randomDigits(rng, 15); }
        }
        String check = FinancialGen.ibanCheckDigits(country, bban);
        return country + check + bban;
    }

    private static String randomDigits(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('0' + rng.nextInt(10)));
        return sb.toString();
    }

    private static String randomAlpha(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('A' + rng.nextInt(26)));
        return sb.toString();
    }

    private static String randRef(ThreadLocalRandom rng, int n) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    // ── Transaction builder ───────────────────────────────────────────────────

    private static final class Txn {
        java.time.LocalDate date;
        boolean credit;
        double amount;
        String code;
        String ref;
        String desc;
    }

    private static final class TxnResult {
        java.util.List<Txn> txns;
        double closing;
    }

    private static TxnResult buildTransactions(ThreadLocalRandom rng, int n, double opening) {
        double balance = opening;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate base  = today.minusDays(rng.nextInt(2, 8));
        java.time.LocalDate date  = base;
        java.util.List<Txn> txns = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i > 0 && rng.nextDouble() < 0.35) {
                java.time.LocalDate candidate = date.plusDays(1);
                java.time.LocalDate cap = today.minusDays(1);
                date = candidate.isBefore(cap) ? candidate : cap;
            }
            double amount = Math.round(rng.nextDouble(10.0, 5000.0) * 100.0) / 100.0;
            boolean isCredit = rng.nextDouble() > 0.4;
            if (!isCredit && balance - amount < 100) isCredit = true;
            balance += isCredit ? amount : -amount;
            Txn t = new Txn();
            t.date = date;
            t.credit = isCredit;
            t.amount = amount;
            t.code = MT940_TX_CODES[rng.nextInt(MT940_TX_CODES.length)];
            t.ref = randRef(rng, 8);
            t.desc = TX_DESCRIPTIONS[rng.nextInt(TX_DESCRIPTIONS.length)];
            txns.add(t);
        }
        TxnResult result = new TxnResult();
        result.txns = txns;
        result.closing = Math.round(balance * 100.0) / 100.0;
        return result;
    }

    // ── MT940 ─────────────────────────────────────────────────────────────────

    private static String mt940Date(java.time.LocalDate d) {
        return d.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
    }

    private static String mt940Amount(double v) {
        long whole = (long) v;
        long cents = Math.round((v - whole) * 100);
        return whole + "," + String.format("%02d", cents);
    }

    private static String generateMt940(ThreadLocalRandom rng, String locale) {
        String loc      = locale.toUpperCase(java.util.Locale.ROOT);
        String currency = CURRENCIES.getOrDefault(loc, "EUR");
        String iban      = mockIban(rng, loc);
        double opening   = Math.round(rng.nextDouble(1000.0, 50000.0) * 100.0) / 100.0;
        TxnResult res    = buildTransactions(rng, rng.nextInt(2, 6), opening);

        java.time.LocalDate startDate = res.txns.get(0).date;
        java.time.LocalDate endDate   = res.txns.get(res.txns.size() - 1).date;
        int stmtNum   = rng.nextInt(1, 100);
        String refNum = randRef(rng, 10);

        StringBuilder sb = new StringBuilder();
        sb.append(":20:").append(refNum).append('\n');
        sb.append(":25:").append(iban).append('/').append(currency).append('\n');
        sb.append(":28C:").append(String.format("%05d", stmtNum)).append("/001\n");
        sb.append(":60F:C").append(mt940Date(startDate)).append(currency).append(mt940Amount(opening)).append('\n');
        for (Txn t : res.txns) {
            String ind  = t.credit ? "C" : "D";
            String d    = mt940Date(t.date);
            String mmdd = t.date.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));
            sb.append(":61:").append(d).append(mmdd).append(ind).append(mt940Amount(t.amount))
              .append(t.code).append(t.ref).append("//").append(t.ref, 0, 8).append('\n');
            sb.append(":86:").append(t.desc).append('\n');
        }
        sb.append(":62F:C").append(mt940Date(endDate)).append(currency).append(mt940Amount(res.closing));
        return sb.toString();
    }

    // ── CAMT.053 ──────────────────────────────────────────────────────────────

    private static String generateCamt053(ThreadLocalRandom rng, String locale) {
        String loc      = locale.toUpperCase(java.util.Locale.ROOT);
        String currency = CURRENCIES.getOrDefault(loc, "EUR");
        String iban      = mockIban(rng, loc);
        double opening   = Math.round(rng.nextDouble(1000.0, 50000.0) * 100.0) / 100.0;
        TxnResult res    = buildTransactions(rng, rng.nextInt(2, 5), opening);

        java.time.LocalDate today     = java.time.LocalDate.now();
        java.time.LocalDate startDate = res.txns.get(0).date;
        java.time.LocalDate endDate   = res.txns.get(res.txns.size() - 1).date;
        String nowDt  = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String msgId  = "MOCK" + today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + rng.nextInt(1000, 10000);
        String stmtId = "STMT" + rng.nextInt(10000, 100000);

        StringBuilder entries = new StringBuilder();
        for (Txn t : res.txns) {
            String cdtDbt = t.credit ? "CRDT" : "DBIT";
            String end2end = randRef(rng, 8);
            String dStr = t.date.toString();
            entries.append("      <Ntry>\n")
                .append("        <Amt Ccy=\"").append(currency).append("\">").append(String.format(java.util.Locale.US, "%.2f", t.amount)).append("</Amt>\n")
                .append("        <CdtDbtInd>").append(cdtDbt).append("</CdtDbtInd>\n")
                .append("        <Sts><Cd>BOOK</Cd></Sts>\n")
                .append("        <BookgDt><Dt>").append(dStr).append("</Dt></BookgDt>\n")
                .append("        <ValDt><Dt>").append(dStr).append("</Dt></ValDt>\n")
                .append("        <BkTxCd><Domn><Cd>PMNT</Cd><Fmly><Cd>ICDT</Cd><SubFmlyCd>AUTT</SubFmlyCd></Fmly></Domn></BkTxCd>\n")
                .append("        <NtryDtls><TxDtls><Refs><EndToEndId>").append(end2end).append("</EndToEndId></Refs>")
                .append("<RmtInf><Ustrd>").append(t.desc).append("</Ustrd></RmtInf></TxDtls></NtryDtls>\n")
                .append("      </Ntry>\n");
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Document xmlns=\"" + NS + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <BkToCstmrStmt>\n" +
            "    <GrpHdr>\n" +
            "      <MsgId>" + msgId + "</MsgId>\n" +
            "      <CreDtTm>" + nowDt + "</CreDtTm>\n" +
            "      <MsgPgntn><PgNb>1</PgNb><LastPgInd>true</LastPgInd></MsgPgntn>\n" +
            "    </GrpHdr>\n" +
            "    <Stmt>\n" +
            "      <Id>" + stmtId + "</Id>\n" +
            "      <CreDtTm>" + nowDt + "</CreDtTm>\n" +
            "      <FrToDt>\n" +
            "        <FrDtTm>" + startDate + "T00:00:00</FrDtTm>\n" +
            "        <ToDtTm>" + endDate + "T23:59:59</ToDtTm>\n" +
            "      </FrToDt>\n" +
            "      <Acct>\n" +
            "        <Id><IBAN>" + iban + "</IBAN></Id>\n" +
            "        <Ccy>" + currency + "</Ccy>\n" +
            "      </Acct>\n" +
            "      <Bal>\n" +
            "        <Tp><CdOrPrtry><Cd>OPBD</Cd></CdOrPrtry></Tp>\n" +
            "        <Amt Ccy=\"" + currency + "\">" + String.format(java.util.Locale.US, "%.2f", opening) + "</Amt>\n" +
            "        <CdtDbtInd>CRDT</CdtDbtInd>\n" +
            "        <Dt><Dt>" + startDate + "</Dt></Dt>\n" +
            "      </Bal>\n" +
            entries +
            "      <Bal>\n" +
            "        <Tp><CdOrPrtry><Cd>CLBD</Cd></CdOrPrtry></Tp>\n" +
            "        <Amt Ccy=\"" + currency + "\">" + String.format(java.util.Locale.US, "%.2f", res.closing) + "</Amt>\n" +
            "        <CdtDbtInd>CRDT</CdtDbtInd>\n" +
            "        <Dt><Dt>" + endDate + "</Dt></Dt>\n" +
            "      </Bal>\n" +
            "    </Stmt>\n" +
            "  </BkToCstmrStmt>\n" +
            "</Document>";
    }
}
