package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Capital markets — ISIN, CUSIP, SEDOL, LEI, FIX, PSD2. Mirrors financial_markets.py. */
public final class FinancialMarketsGen {

    private FinancialMarketsGen() {}

    private static final char[]   ALPHA_NUM        = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final String[] STOCK_TICKERS = {
        "AAPL","MSFT","GOOGL","AMZN","TSLA","NVDA","META","JPM","V","MA",
        "UNH","JNJ","PG","HD","MRK","ABBV","CVX","PEP","KO","COST",
        "WMT","BAC","TMO","AVGO","NEE","QCOM","TXN","CMCSA","LIN","DHR",
        "ACN","VZ","ADBE","PM","RTX","HON","INTC","T","ORCL","AMD",
        "CRM","SBUX","IBM","GS","BLK","AXP","NFLX","SPGI","CAT","MU",
        "XOM","BKNG","DE","ZTS","CI","CB","SO","GE","MMM","MDT"
    };
    private static final String FIGI_NSIN_CHARS = "0123456789BCDFGHJKLMNPQRSTVWXYZ";
    private static final String[] FIGI_PREFIXES = {"BB","BL","BM","BN","BP","BQ","BR","BS","BT","BV"};
    private static final String[] FOREX_MAJORS = {"EUR","USD","GBP","JPY","AUD","CAD","CHF","NZD","SEK","NOK","DKK","SGD","HKD","MXN","ZAR","TRY","RUB","CNY","INR","BRL"};

    private static final java.util.Map<String, double[]> FOREX_RATES = new java.util.HashMap<>();
    static {
        FOREX_RATES.put("EUR/USD", new double[]{1.0200, 1.1200});
        FOREX_RATES.put("GBP/USD", new double[]{1.2000, 1.3200});
        FOREX_RATES.put("USD/JPY", new double[]{130.0, 155.0});
        FOREX_RATES.put("USD/CHF", new double[]{0.8500, 0.9600});
        FOREX_RATES.put("AUD/USD", new double[]{0.6200, 0.7200});
        FOREX_RATES.put("USD/CAD", new double[]{1.2500, 1.4000});
        FOREX_RATES.put("EUR/GBP", new double[]{0.8400, 0.9200});
        FOREX_RATES.put("USD/TRY", new double[]{28.0, 35.0});
        FOREX_RATES.put("USD/RUB", new double[]{75.0, 95.0});
        FOREX_RATES.put("EUR/JPY", new double[]{140.0, 165.0});
    }

    private static final java.util.Map<String, String[]> RIC_EXCHANGES = java.util.Map.of(
        "TR", new String[]{"IS","ISTE"}, "US", new String[]{"O","N","A","OQ"},
        "UK", new String[]{"L","LN"}, "DE", new String[]{"DE","F","XETR"},
        "FR", new String[]{"PA","P"}, "RU", new String[]{"MM","RTS"}
    );
    private static final java.util.Map<String, String[]> MIC_CODES_BY_LOCALE = java.util.Map.of(
        "TR", new String[]{"XIST","XETK"}, "US", new String[]{"XNYS","XNAS","XASE","ARCX","BATS"},
        "UK", new String[]{"XLON","XAIM"}, "DE", new String[]{"XETR","XFRA","XBER"},
        "FR", new String[]{"XPAR","XEUR"}, "RU", new String[]{"MISX","RTSX"}
    );
    private static final java.util.Map<String, String[]> STOCK_EXCHANGES_BY_LOCALE = java.util.Map.of(
        "TR", new String[]{"Borsa İstanbul"},
        "US", new String[]{"NYSE","NASDAQ","NYSE American","CBOE","IEX"},
        "UK", new String[]{"London Stock Exchange","AIM"},
        "DE", new String[]{"Xetra","Frankfurt Stock Exchange","Berlin Stock Exchange"},
        "FR", new String[]{"Euronext Paris","Euronext Growth Paris"},
        "RU", new String[]{"Moscow Exchange","RTS Stock Exchange"}
    );

    private static final java.util.Map<String, String> ISIN_COUNTRY = java.util.Map.of(
        "TR","TR","US","US","UK","GB","DE","DE","FR","FR","RU","RU"
    );
    private static final String ISIN_NSIN_ALPHA = "BCDFGHJKLMNPQRSTVWXYZ";

    private static final String CUSIP_ISSUER_CHARS = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // no I, O

    private static final String SEDOL_CHARS = "0123456789BCDFGHJKLMNPQRSTVWXYZ";
    private static final String SEDOL_CONSONANTS = "BCDFGHJKLMNPQRSTVWXYZ";

    private static final String[] LOU_PREFIXES = {
        "5299000","2138000","7LTWFH","3H2OSJ","XKZZ2J",
        "HWUPKR","UWJKHY","6SHGI4","9695005","EVKOSJ",
        "BF90RS","F3716T","XTIQ1S","YZ83GR","QEKMOT"
    };

    private static final String[] FIX_SENDER_IDS = {
        "ALGOCLIENT","TRADEWORKS","HEDGE_FUND","MM_CORP","PROP_DESK",
        "QUANT_ALPHA","ARB_CAPITAL","HFT_ENGINE","STAT_ARB","MKTMKR01"
    };
    private static final String[] FIX_TARGET_IDS = {
        "EXCHANGE","BROKER_NYC","PRIME_BRKR","DARK_POOL","ECN_VENUE",
        "NYSE_ARCA","NASDAQ_OMX","BATS_EXCH","IEX_VENUE","CBOE_EXCH"
    };
    private static final String[] FIX_SYMBOLS = {
        "AAPL","GOOGL","MSFT","AMZN","META","TSLA","NVDA","JPM",
        "GS","BAC","IBM","ORCL","INTC","AMD","NFLX","SPY","QQQ",
        "IWM","DIA","VXX","BRKB","V","MA","UNH","WMT"
    };

    private static final java.util.Map<String, String> PSD2_CURRENCY = java.util.Map.of(
        "TR","TRY","UK","GBP","US","USD","DE","EUR","FR","EUR","RU","RUB"
    );
    private static final java.util.Map<String, String> PSD2_SCHEME = java.util.Map.of(
        "TR","TR.IBAN","UK","UK.OBIE.SortCodeAccountNumber","US","US.RoutingNumberAccountNumber",
        "DE","IBAN","FR","IBAN","RU","RU.IBAN"
    );
    private static final String[] PSD2_ISSUERS = {
        "C=GB, ST=England, O=Acme Bank, OU=PISP, CN=PISP/openbanking.org.uk",
        "C=GB, ST=England, O=Fintech Ltd, OU=AISP, CN=AISP/openbanking.org.uk",
        "C=DE, ST=Bayern, O=TechBank GmbH, OU=PISP, CN=PISP/openbanking.org.uk",
        "C=TR, ST=Istanbul, O=FinKurumu AS, OU=PISP, CN=PISP/openbanking.org.uk"
    };
    private static final String[] PSD2_CREDITOR_NAMES = {
        "Acme Inc","TechCorp Ltd","Global Trade GmbH","FinServ SA",
        "Nordic Pay AB","EastBank LLC","Metro Supplies Co","AlphaFunds Ltd"
    };
    private static final String[] PSD2_UNSTRUCTURED = {
        "Internal ops code 5120101","Supplier payment Q2","Invoice settlement",
        "Contract payment ref 7821","Monthly retainer fee","Project milestone 3"
    };

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "isin"              -> isin(rng, locale);
            case "cusip"             -> cusip(rng);
            case "sedol"             -> sedol(rng);
            case "lei"               -> lei(rng);
            case "fix_message"       -> fixMessage(rng);
            case "psd2_consent"      -> psd2Consent(rng, locale, qualifier);
            case "figi"              -> figi(rng);
            case "nsin"              -> nsin(rng, locale);
            case "stock_ticker"      -> pick(rng, STOCK_TICKERS);
            case "forex_pair"        -> forexPair(rng);
            case "forex_rate"        -> forexRate(rng, qualifier);
            case "ric"               -> ric(rng, locale);
            case "mic"               -> pick(rng, MIC_CODES_BY_LOCALE.getOrDefault(locale, MIC_CODES_BY_LOCALE.get("US")));
            case "stock_exchange"    -> pick(rng, STOCK_EXCHANGES_BY_LOCALE.getOrDefault(locale, STOCK_EXCHANGES_BY_LOCALE.get("US")));
            case "option_contract"   -> optionContract(rng);
            case "bond_yield"        -> String.format(java.util.Locale.US, "%.2f", 0.01 + rng.nextInt(1500) / 100.0);
            case "coupon_rate"       -> String.format(java.util.Locale.US, "%.2f", rng.nextInt(1201) / 100.0);
            case "settlement_date"   -> settlementDate(rng);
            case "portfolio_id"      -> portfolioId(rng);
            case "portfolio_id_masked" -> portfolioIdMasked(rng);
            default                  -> "ERROR: Unknown markets type '" + type + "'";
        };
    }

    // ── ISIN — ISO 6166: CC + 9-char NSIN + Luhn check digit ──────────────

    static String isin(ThreadLocalRandom rng, String locale) {
        String cc = ISIN_COUNTRY.getOrDefault(locale, "US");
        StringBuilder nsin = new StringBuilder(9);
        for (int i = 0; i < 9; i++) {
            if (i == 0 && rng.nextInt(4) == 0) {
                nsin.append(ISIN_NSIN_ALPHA.charAt(rng.nextInt(ISIN_NSIN_ALPHA.length())));
            } else {
                nsin.append((char) ('0' + rng.nextInt(10)));
            }
        }
        String payload = cc + nsin;

        StringBuilder numeric = new StringBuilder();
        for (char c : payload.toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else numeric.append(c);
        }
        int check = IdentityGen.luhnCheckDigit(numeric.toString());
        return payload + check;
    }

    // ── CUSIP — 9 chars (6 issuer, 2 issue, 1 check) ─────────────────────────

    static String cusip(ThreadLocalRandom rng) {
        StringBuilder issuer = new StringBuilder(6);
        for (int i = 0; i < 6; i++) issuer.append(CUSIP_ISSUER_CHARS.charAt(rng.nextInt(CUSIP_ISSUER_CHARS.length())));
        String issue = randomAlphaNum(rng, 2);
        String payload = issuer + issue;
        int check = cusipCheck(payload);
        return payload + check;
    }

    static int cusipCheck(String payload) {
        int sum = 0;
        for (int i = 0; i < payload.length(); i++) {
            char c = payload.charAt(i);
            int v = Character.isDigit(c) ? c - '0' : c - 'A' + 10;
            if (i % 2 == 1) v *= 2;
            sum += v / 10 + v % 10;
        }
        return (10 - sum % 10) % 10;
    }

    // ── SEDOL — 7 chars: 6 alphanumeric + 1 check digit ─────────────────────

    static String sedol(ThreadLocalRandom rng) {
        String payload;
        if (rng.nextBoolean()) {
            // New-style: first char consonant (B-Z, no vowels), rest mixed digit/consonant
            StringBuilder sb = new StringBuilder(6);
            sb.append(SEDOL_CONSONANTS.charAt(rng.nextInt(SEDOL_CONSONANTS.length())));
            for (int i = 0; i < 5; i++) sb.append(SEDOL_CHARS.charAt(rng.nextInt(SEDOL_CHARS.length())));
            payload = sb.toString();
        } else {
            // Old-style: all digits
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) sb.append((char) ('0' + rng.nextInt(10)));
            payload = sb.toString();
        }
        int check = sedolCheck(payload);
        return payload + check;
    }

    static int sedolCheck(String payload) {
        int[] weights = {1, 3, 1, 7, 3, 9};
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            char c = payload.charAt(i);
            int v = Character.isDigit(c) ? c - '0' : c - 'A' + 10;
            sum += v * weights[i];
        }
        return (10 - sum % 10) % 10;
    }

    // ── LEI — ISO 17442: 20 chars, MOD-97 check ──────────────────────────────

    static String lei(ThreadLocalRandom rng) {
        String lou = pick(rng, LOU_PREFIXES).substring(0, 4);
        String entity = randomAlphaNum(rng, 14);
        String prefix18 = lou + entity;
        String check = leiCheckDigits(prefix18);
        return prefix18 + check;
    }

    // ISO 17442 MOD 97-10: unlike IBAN, the 18-char prefix is NOT rearranged —
    // just append "00" and take the numeric value mod 97 as-is.
    private static String leiCheckDigits(String prefix18) {
        StringBuilder numeric = new StringBuilder();
        for (char c : (prefix18 + "00").toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else numeric.append(c);
        }
        int mod = 0;
        String numStr = numeric.toString();
        for (int i = 0; i < numStr.length(); i++) {
            mod = (mod * 10 + (numStr.charAt(i) - '0')) % 97;
        }
        return String.format("%02d", 98 - mod);
    }

    // ── FIX 4.4 New Order Single (MsgType=D) ─────────────────────────────────

    private static String fixMessage(ThreadLocalRandom rng) {
        char SOH = 1;
        String sender  = pick(rng, FIX_SENDER_IDS);
        String target  = pick(rng, FIX_TARGET_IDS);
        int    seqNum  = rng.nextInt(1, 10_000_000);
        String clOrdId = randomHex(rng, 16);
        String symbol  = pick(rng, FIX_SYMBOLS);
        String side    = rng.nextBoolean() ? "1" : "2";
        int    qty     = rng.nextInt(1, 1001) * 100;
        String[] ordTypes = {"1","2","3"};
        String ordType = pick(rng, ordTypes);

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
            .ofPattern("yyyyMMdd-HH:mm:ss.SSS").withZone(java.time.ZoneOffset.UTC);
        String ts = fmt.format(java.time.Instant.now());

        StringBuilder body = new StringBuilder();
        body.append("35=D").append(SOH);
        body.append("49=").append(sender).append(SOH);
        body.append("56=").append(target).append(SOH);
        body.append("34=").append(seqNum).append(SOH);
        body.append("52=").append(ts).append(SOH);
        body.append("11=").append(clOrdId).append(SOH);
        body.append("55=").append(symbol).append(SOH);
        body.append("54=").append(side).append(SOH);
        body.append("38=").append(qty).append(SOH);
        body.append("40=").append(ordType).append(SOH);
        body.append("60=").append(ts).append(SOH);
        if (ordType.equals("2") || ordType.equals("3")) {
            double price = rng.nextDouble(1.0, 9999.99);
            body.append("44=").append(String.format(java.util.Locale.US, "%.2f", price)).append(SOH);
        }
        String bodyStr = body.toString();
        int bodyLen = bodyStr.length();

        String header = "8=FIX.4.4" + SOH + "9=" + bodyLen + SOH;
        String beforeChecksum = header + bodyStr;
        int checksum = 0;
        for (int i = 0; i < beforeChecksum.length(); i++) checksum += beforeChecksum.charAt(i);
        checksum %= 256;

        return beforeChecksum + "10=" + String.format("%03d", checksum) + SOH;
    }

    // ── PSD2 / Open Banking JWS ───────────────────────────────────────────────

    private static String psd2Consent(ThreadLocalRandom rng, String locale, String qualifier) {
        String loc = PSD2_CURRENCY.containsKey(locale) ? locale : "UK";
        String currency = PSD2_CURRENCY.get(loc);
        String scheme    = PSD2_SCHEME.get(loc);

        String kid    = randomHex(rng, 16);
        String issuer = pick(rng, PSD2_ISSUERS);
        long   iat    = System.currentTimeMillis() / 1000;

        String headerJson = "{\"alg\":\"PS256\",\"kid\":\"" + kid + "\",\"b64\":false,\"crit\":[\"b64\","
            + "\"http://openbanking.org.uk/iat\",\"http://openbanking.org.uk/iss\",\"http://openbanking.org.uk/tan\"],"
            + "\"http://openbanking.org.uk/iat\":" + iat + ","
            + "\"http://openbanking.org.uk/iss\":\"" + issuer + "\","
            + "\"http://openbanking.org.uk/tan\":\"openbanking.org.uk\"}";
        String headerB64 = base64UrlNoPad(headerJson);

        String consentId = "MOCKJ-aac-" + randomHexLower(rng, 12);
        String instrId   = randomHex(rng, 8);
        String e2eId     = "E2E-" + randomHex(rng, 10);

        Double parsedAmount = null;
        if (!qualifier.isEmpty()) {
            try { parsedAmount = Double.parseDouble(qualifier); } catch (NumberFormatException ignored) {}
        }
        double amountVal = parsedAmount != null ? parsedAmount : rng.nextDouble(10.0, 9999.99);
        String creditorName = pick(rng, PSD2_CREDITOR_NAMES);
        String creditorIdV  = creditorId(rng, loc);
        String ref           = "REF-" + randomHex(rng, 8);
        String unstructured  = pick(rng, PSD2_UNSTRUCTURED);

        String payloadJson = "{\"Data\":{\"ConsentId\":\"" + consentId + "\",\"Initiation\":{"
            + "\"InstructionIdentification\":\"" + instrId + "\","
            + "\"EndToEndIdentification\":\"" + e2eId + "\","
            + "\"InstructedAmount\":{\"Amount\":\"" + String.format(java.util.Locale.US, "%.2f", amountVal) + "\",\"Currency\":\"" + currency + "\"},"
            + "\"CreditorAccount\":{\"SchemeName\":\"" + scheme + "\",\"Identification\":\"" + creditorIdV + "\",\"Name\":\"" + creditorName + "\"},"
            + "\"RemittanceInformation\":{\"Reference\":\"" + ref + "\",\"Unstructured\":\"" + unstructured + "\"}"
            + "}},\"Risk\":{}}";
        String payloadB64 = base64UrlNoPad(payloadJson);

        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        byte[] sig;
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256"));
            sig = mac.doFinal((headerB64 + "." + payloadB64).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            sig = new byte[32];
        }
        String sigB64 = base64UrlNoPad(sig);
        return headerB64 + "." + payloadB64 + "." + sigB64;
    }

    private static String creditorId(ThreadLocalRandom rng, String locale) {
        if ("UK".equals(locale)) {
            String sort = String.format("%02d-%02d-%02d",
                rng.nextInt(10, 100), rng.nextInt(10, 100), rng.nextInt(10, 100));
            String acct = String.valueOf(rng.nextInt(10000000, 100000000));
            return sort + acct;
        }
        if ("US".equals(locale)) {
            String routing = String.valueOf(rng.nextInt(100000000, 1000000000));
            String acct    = String.valueOf(rng.nextLong(100000000L, 10000000000L));
            return routing + acct;
        }
        String prefix = switch (locale) {
            case "TR" -> "TR"; case "DE" -> "DE"; case "FR" -> "FR"; case "RU" -> "RU";
            default   -> "GB";
        };
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(rng.nextInt(10, 100));
        for (int i = 0; i < 16; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }

    // ── FIGI — 2-letter registrar prefix + 'G' + 8-char body + CUSIP-style check ──

    static String figi(ThreadLocalRandom rng) {
        String prefix = FIGI_PREFIXES[rng.nextInt(FIGI_PREFIXES.length)];
        StringBuilder body = new StringBuilder(prefix).append('G');
        for (int i = 0; i < 8; i++) body.append(FIGI_NSIN_CHARS.charAt(rng.nextInt(FIGI_NSIN_CHARS.length())));
        String s = body.toString();
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            char c = s.charAt(i);
            int v = Character.isDigit(c) ? c - '0' : c - 'A' + 10;
            if (i % 2 == 1) v *= 2;
            sum += v / 10 + v % 10;
        }
        int check = (10 - (sum % 10)) % 10;
        return s + check;
    }

    // ── NSIN (locale-appropriate National Securities Identifying Number) ────

    static String nsin(ThreadLocalRandom rng, String locale) {
        if ("US".equals(locale)) {
            String chars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
            String payload = sb.toString();
            return payload + cusipCheck(payload);
        }
        if ("UK".equals(locale)) {
            String sedolChars = "0123456789BCDFGHJKLMNPQRSTVWXYZ";
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) sb.append(sedolChars.charAt(rng.nextInt(sedolChars.length())));
            String payload = sb.toString();
            return payload + sedolCheck(payload);
        }
        return randomAlphaNum(rng, 9);
    }

    // ── Forex pair / rate ─────────────────────────────────────────────────────

    private static String forexPair(ThreadLocalRandom rng) {
        String base = FOREX_MAJORS[rng.nextInt(FOREX_MAJORS.length)];
        String quote;
        do { quote = FOREX_MAJORS[rng.nextInt(FOREX_MAJORS.length)]; } while (quote.equals(base));
        return base + "/" + quote;
    }

    private static String forexRate(ThreadLocalRandom rng, String pairStr) {
        if (pairStr.contains("/")) {
            String[] parts = pairStr.toUpperCase(java.util.Locale.ROOT).split("/", -1);
            if (parts.length == 2) {
                double[] range = FOREX_RATES.get(parts[0] + "/" + parts[1]);
                if (range != null) {
                    double v = range[0] + (range[1] - range[0]) * rng.nextDouble();
                    return String.format(java.util.Locale.US, "%.4f", v);
                }
            }
        }
        double v = 0.5 + rng.nextDouble() * 149.5;
        return String.format(java.util.Locale.US, "%.4f", v);
    }

    // ── RIC (Reuters Instrument Code) ────────────────────────────────────────

    private static String ric(ThreadLocalRandom rng, String locale) {
        String ticker = STOCK_TICKERS[rng.nextInt(STOCK_TICKERS.length)];
        String[] exch = RIC_EXCHANGES.getOrDefault(locale, RIC_EXCHANGES.get("US"));
        String sfx = exch[rng.nextInt(exch.length)];
        return ticker + "." + sfx;
    }

    // ── Option Contract — OCC symbol: TICKER + YYMMDD + C/P + 8-digit strike ──

    private static String optionContract(ThreadLocalRandom rng) {
        String ticker = pick(rng, STOCK_TICKERS);
        java.time.LocalDate expiry = java.time.LocalDate.now().plusDays(rng.nextInt(7, 366));
        String expiryStr = expiry.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        char cp = rng.nextBoolean() ? 'C' : 'P';
        int strike = rng.nextInt(500, 50001) * 100;
        return String.format("%s%s%c%08d", ticker, expiryStr, cp, strike);
    }

    // ── Settlement Date — T+1/T+2/T+3 weighted, weekends skipped ────────────

    private static String settlementDate(ThreadLocalRandom rng) {
        int r = rng.nextInt(100);
        int settleT = r < 20 ? 1 : (r < 80 ? 2 : 3);
        java.time.LocalDate target = java.time.LocalDate.now();
        int bdays = 0;
        while (bdays < settleT) {
            target = target.plusDays(1);
            if (target.getDayOfWeek().getValue() < 6) bdays++;
        }
        return target.toString();
    }

    // ── Portfolio ID ──────────────────────────────────────────────────────────

    private static String portfolioId(ThreadLocalRandom rng) {
        String prefix = rng.nextBoolean() ? "PRTF" : "PORT";
        return prefix + "-" + randomAlphaNum(rng, 8);
    }

    private static String portfolioIdMasked(ThreadLocalRandom rng) {
        String prefix = rng.nextBoolean() ? "PRTF" : "PORT";
        String last4 = randomAlphaNum(rng, 4);
        return prefix + "-****" + last4;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }

    private static String randomAlphaNum(ThreadLocalRandom rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHA_NUM[rng.nextInt(ALPHA_NUM.length)]);
        return sb.toString();
    }

    private static String randomHex(ThreadLocalRandom rng, int len) {
        String hex = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static String randomHexLower(ThreadLocalRandom rng, int len) {
        String hex = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static String base64UrlNoPad(String s) {
        return base64UrlNoPad(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String base64UrlNoPad(byte[] b) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
