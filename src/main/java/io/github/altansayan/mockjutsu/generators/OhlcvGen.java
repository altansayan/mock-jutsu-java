package io.github.altansayan.mockjutsu.generators;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** OHLCV candles and market tick generator — Geometric Brownian Motion. Mirrors ohlcv.py. */
public final class OhlcvGen {
    private OhlcvGen() {}

    private static final String[] SYMBOLS = {
        "AAPL","MSFT","GOOGL","AMZN","NVDA","TSLA","META","NFLX",
        "BABA","BRK","JPM","GS","BAC","C","WMT","COST","XOM",
        "CVX","JNJ","PFE","AMD","INTC","ORCL","SAP","ASML",
        "THYAO","EREGL","AKBNK","GARAN","SISE",
        "BTC-USD","ETH-USD","SOL-USD","XRP-USD"
    };
    private static final String[] INTERVALS = {"1m","5m","15m","1h","4h","1d"};
    private static final java.util.Map<String, Integer> INTERVAL_SECS = java.util.Map.of(
        "1m", 60, "5m", 300, "15m", 900, "1h", 3600, "4h", 14400, "1d", 86400
    );
    private static final double BASE_DAILY_VOL = 0.25;

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "ohlcv_candles" -> ohlcvCandles(rng);
            case "market_tick"   -> marketTick(rng);
            default -> "ERROR: Unknown OHLCV type '" + type + "'";
        };
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double gbmStep(ThreadLocalRandom rng, double price, double mu, double sigma, double dtFrac) {
        double z = rng.nextGaussian();
        double logReturn = (mu - 0.5 * sigma * sigma) * dtFrac + sigma * Math.sqrt(dtFrac) * z;
        return round2(Math.max(0.01, price * Math.exp(logReturn)));
    }

    private static String ohlcvCandles(ThreadLocalRandom rng) {
        String symbol = SYMBOLS[rng.nextInt(SYMBOLS.length)];
        String interval = INTERVALS[rng.nextInt(INTERVALS.length)];
        int n = rng.nextInt(10, 31);

        int intervalSecs = INTERVAL_SECS.get(interval);
        double dtFrac = intervalSecs / 86400.0;
        double sigma = BASE_DAILY_VOL * Math.sqrt(dtFrac);
        double mu = rng.nextDouble(-0.0005, 0.0005);

        double startPrice = symbol.contains("-USD")
            ? round2(rng.nextDouble(100.0, 70000.0))
            : round2(rng.nextDouble(5.0, 2000.0));

        ZonedDateTime startDt = ZonedDateTime.now(ZoneOffset.UTC).minusSeconds((long) intervalSecs * n);
        startDt = startDt.withSecond(0).withNano(0);

        int baseVol = rng.nextInt(10000, 5000001);
        double lnBaseVol = Math.log(baseVol);

        double prevClose = startPrice;
        StringBuilder candlesSb = new StringBuilder("[");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        for (int i = 0; i < n; i++) {
            double o = prevClose;
            double c = gbmStep(rng, o, mu, sigma, dtFrac);

            double intraRange = rng.nextDouble(0.0, 0.03);
            double bodyHi = Math.max(o, c);
            double bodyLo = Math.min(o, c);
            double h = round2(bodyHi * (1.0 + rng.nextDouble(0.0, intraRange)));
            double l = round2(bodyLo * (1.0 - rng.nextDouble(0.0, intraRange)));
            l = Math.max(0.01, l);

            long v = Math.max(1L, (long) Math.exp(lnBaseVol + 0.5 * rng.nextGaussian()));

            String ts = startDt.plusSeconds((long) intervalSecs * i).format(fmt);

            if (i > 0) candlesSb.append(",");
            candlesSb.append(String.format(java.util.Locale.US,
                "{\"t\":\"%s\",\"o\":%s,\"h\":%s,\"l\":%s,\"c\":%s,\"v\":%d}",
                ts, fmtNum(o), fmtNum(h), fmtNum(l), fmtNum(c), v));
            prevClose = c;
        }
        candlesSb.append("]");

        return "{\"symbol\":\"" + symbol + "\",\"interval\":\"" + interval + "\",\"candles\":" + candlesSb + "}";
    }

    private static final String[] SIDES = {"buy","sell"};

    private static String marketTick(ThreadLocalRandom rng) {
        String symbol = SYMBOLS[rng.nextInt(SYMBOLS.length)];
        double midPrice = round2(rng.nextDouble(5.0, 2000.0));

        double spread = round2(rng.nextDouble(0.01, 0.10));
        double half = round2(spread / 2);
        double bid = round2(Math.max(0.01, midPrice - half));
        double ask = round2(bid + spread);

        double price = round2(rng.nextDouble(bid, Math.nextUp(ask)));
        price = round2(Math.max(bid, Math.min(ask, price)));
        if (price <= bid) price = round2(Math.min(ask, bid + 0.01));

        double midpoint = round2((bid + ask) / 2);
        String side = price >= midpoint ? "buy" : "sell";

        int[] multipliers = {1, 10, 100};
        long size = (long) rng.nextInt(1, 10001) * multipliers[rng.nextInt(3)];
        long seq = rng.nextLong(1, 1_000_000_000L);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String ts = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.")) +
            String.format("%03d", now.getNano() / 1_000_000) + "Z";

        return String.format(java.util.Locale.US,
            "{\"symbol\":\"%s\",\"timestamp\":\"%s\",\"price\":%s,\"size\":%d," +
            "\"bid\":%s,\"ask\":%s,\"side\":\"%s\",\"seq\":%d}",
            symbol, ts, fmtNum(price), size, fmtNum(bid), fmtNum(ask), side, seq);
    }

    private static String fmtNum(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }
}
