package io.github.altansayan.mockjutsu.generators;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Meta / infrastructure types — mirrors meta.py. */
public final class MetaGen {

    private MetaGen() {}

    private static final SecureRandom SEC = new SecureRandom();

    private static final String[] BROWSER_NAMES    = {"Chrome","Firefox","Safari","Edge","Opera"};
    private static final String[] BROWSER_ENGINES  = {"Blink","Gecko","WebKit","Blink","Blink"};
    private static final int[][] BROWSER_MAJOR_RANGE = {{120,126},{120,127},{16,18},{120,126},{105,110}};
    private static final String[] TLDS             = {".com",".net",".org",".io",".co",".dev",".app",".ai",".tech",".cloud"};

    private static final java.util.Map<String, String[]> DOMAIN_TLDS = java.util.Map.of(
        "TR", new String[]{".com.tr",".net.tr",".org.tr",".com"},
        "US", new String[]{".com",".net",".org",".io",".co"},
        "UK", new String[]{".co.uk",".org.uk",".me.uk",".com"},
        "DE", new String[]{".de",".com",".net"},
        "FR", new String[]{".fr",".com",".net"},
        "RU", new String[]{".ru",".com"}
    );

    private static final String[] URL_PATHS = {
        "/api/v1/users","/api/v2/transactions","/api/v1/accounts",
        "/products/list","/orders/pending","/invoices/2024",
        "/auth/login","/auth/refresh","/dashboard/overview",
        "/settings/profile","/reports/monthly","/webhook/events"
    };

    private static final String[][] COLOR_NAMES_HEX = {
        {"Crimson","#DC143C"}, {"Dodger Blue","#1E90FF"}, {"Emerald","#50C878"},
        {"Goldenrod","#DAA520"}, {"Orchid","#DA70D6"}, {"Tomato","#FF6347"},
        {"Steel Blue","#4682B4"}, {"Coral","#FF7F50"}, {"Medium Purple","#9370DB"},
        {"Sea Green","#2E8B57"}, {"Sienna","#A0522D"}, {"Slate Gray","#708090"}
    };

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "uuid"              -> UUID.randomUUID().toString();
            case "requestid"         -> UUID.randomUUID().toString();
            case "correlationid"     -> UUID.randomUUID().toString();
            case "sessionid"         -> UUID.randomUUID().toString();
            case "idempotencykey"    -> UUID.randomUUID().toString();
            case "deviceid"          -> UUID.randomUUID().toString().toUpperCase();
            case "ipv4"              -> ipv4(rng);
            case "ipv6"              -> ipv6(rng);
            case "public_ip"         -> ipv4Public(rng);
            case "private_ip"        -> ipv4Private(rng);
            case "browser_name"      -> pick(rng, BROWSER_NAMES);
            case "browser_version"   -> browserVersion(rng);
            case "browser_engine"    -> pick(rng, BROWSER_ENGINES);
            case "useragent"         -> userAgent(rng);
            case "timestamp"         -> String.valueOf(System.currentTimeMillis() / 1000);
            case "timestamp_iso"     -> timestampIso();
            case "clientversion"     -> String.format("%d.%d.%d", rng.nextInt(1,5), rng.nextInt(0,10), rng.nextInt(0,10));
            case "bearertoken"       -> "Bearer " + jwt(rng);
            case "signature"         -> signature(qualifier);
            case "apppassword"       -> appPassword(rng);
            case "jwt"               -> jwt(rng);
            case "hash"              -> hash(qualifier);
            case "mac_address"       -> macAddress(rng);
            case "domain"            -> domain(rng, locale);
            case "url"               -> url(rng, locale);
            case "color"             -> color(rng, qualifier);
            case "api_key"           -> apiKey(rng);
            case "totp_code"         -> String.format("%06d", rng.nextInt(0, 1000000));
            case "webhook_signature" -> "sha256=" + hash("");
            case "transaction_id"    -> "TXN" + randomHex(rng, 16).toUpperCase();
            case "slug"              -> slug(rng);
            case "http_method"       -> pick(rng, new String[]{"GET","POST","PUT","PATCH","DELETE","HEAD","OPTIONS"});
            case "http_status_code"  -> String.valueOf(pick(rng, HTTP_STATUS_CODES));
            case "port_number"       -> portNumber(rng);
            case "hostname"          -> hostname(rng);
            case "tld"               -> pick(rng, TLDS);
            case "uri_path"          -> pick(rng, URI_PATHS);
            default                  -> "ERROR: Unknown meta type '" + type + "'";
        };
    }

    private static final int[] HTTP_STATUS_CODES = {
        200,201,204, 301,302,304, 400,401,403,404,405,409,410,422,429, 500,502,503,504
    };
    private static final int[] COMMON_PORTS = {
        80,443,8080,8443,3000,3306,5432,6379,9200,27017,
        22,25,53,110,143,993,995,8000,8888,9000
    };
    private static final String[] URI_PATHS = {
        "/api/v1/users","/api/v1/accounts","/api/v2/payments",
        "/api/v1/orders","/api/v1/products","/api/v1/invoices",
        "/api/v2/reports","/api/v1/customers","/api/v1/transactions",
        "/api/v1/settings","/api/v1/sessions","/api/v1/webhooks",
        "/admin/users","/admin/reports","/admin/audit-logs",
        "/public/assets","/static/files","/data/exports",
        "/api/v1/search","/api/v1/notifications","/api/v1/analytics"
    };

    private static String portNumber(ThreadLocalRandom rng) {
        if (rng.nextDouble() < 0.4) return String.valueOf(pick(rng, COMMON_PORTS));
        return String.valueOf(rng.nextInt(1024, 65536));
    }

    private static String timestampIso() {
        return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
    }

    // ── IPv4 ──────────────────────────────────────────────────────────────────

    private static final java.util.Set<Integer> RESERVED_FIRST_OCTETS = java.util.Set.of(
        0,10,127,169,172,192,198,203,
        224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,
        240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255
    );

    private static boolean isPublicIpv4(int a, int b) {
        if (RESERVED_FIRST_OCTETS.contains(a)) return false;
        if (a == 172 && b >= 16 && b <= 31) return false;
        if (a == 192 && b == 168) return false;
        return true;
    }

    static String ipv4(ThreadLocalRandom rng) {
        return ipv4Public(rng);
    }

    private static String ipv4Public(ThreadLocalRandom rng) {
        int a, b, c, d;
        do {
            a = rng.nextInt(0, 256);
            b = rng.nextInt(0, 256);
        } while (!isPublicIpv4(a, b));
        c = rng.nextInt(0, 256);
        d = rng.nextInt(0, 256);
        return a + "." + b + "." + c + "." + d;
    }

    private static String ipv4Private(ThreadLocalRandom rng) {
        return switch (rng.nextInt(3)) {
            case 0  -> "10." + rng.nextInt(0,256) + "." + rng.nextInt(0,256) + "." + rng.nextInt(0,256);
            case 1  -> "172." + rng.nextInt(16,32) + "." + rng.nextInt(0,256) + "." + rng.nextInt(0,256);
            default -> "192.168." + rng.nextInt(0,256) + "." + rng.nextInt(0,256);
        };
    }

    // ── IPv6 ──────────────────────────────────────────────────────────────────

    static String ipv6(ThreadLocalRandom rng) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%04x", rng.nextInt(0, 65536)));
        }
        return sb.toString();
    }

    // ── Browser ───────────────────────────────────────────────────────────────

    private static String browserVersion(ThreadLocalRandom rng) {
        int idx = rng.nextInt(BROWSER_NAMES.length);
        int lo = BROWSER_MAJOR_RANGE[idx][0], hi = BROWSER_MAJOR_RANGE[idx][1];
        int major = rng.nextInt(lo, hi + 1);
        return String.format("%d.0.%d.%d", major, rng.nextInt(1000, 10000), rng.nextInt(10, 100));
    }

    private static final String[] UA_PLATFORMS = {
        "Windows NT 10.0", "Windows NT 11.0",
        "Macintosh; Intel Mac OS X 10_15_7",
        "X11; Linux x86_64",
        "iPhone; CPU iPhone OS 17_5 like Mac OS X"
    };
    private static final String[] UA_ARCHS = {"Win64; x64", "WOW64", "ARM64", "x86_64"};

    private static String userAgent(ThreadLocalRandom rng) {
        String chromeV = String.format("%d.0.%d.%d", rng.nextInt(120, 127), rng.nextInt(1000, 10000), rng.nextInt(10, 100));
        String safariV = String.format("%d.%d", rng.nextInt(530, 601), rng.nextInt(1, 41));
        String plat = UA_PLATFORMS[rng.nextInt(UA_PLATFORMS.length)];
        String arch = (plat.contains("Windows") || plat.contains("Linux")) ? UA_ARCHS[rng.nextInt(UA_ARCHS.length)] : "";
        String platPart = plat + (arch.isEmpty() ? "" : "; " + arch);
        return String.format("Mozilla/5.0 (%s) AppleWebKit/%s (KHTML, like Gecko) Chrome/%s Safari/%s",
            platPart, safariV, chromeV, safariV);
    }

    // ── JWT ───────────────────────────────────────────────────────────────────

    static String jwt(ThreadLocalRandom rng) {
        String header  = b64url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long iat = System.currentTimeMillis() / 1000;
        long exp = iat + 3600; // 1 hour
        String sub = UUID.randomUUID().toString();
        String payload = b64url(String.format(
            "{\"sub\":\"%s\",\"iss\":\"https://auth.mockjutsu.dev\",\"aud\":\"mockjutsu-api\",\"iat\":%d,\"exp\":%d}",
            sub, iat, exp));
        byte[] sig = new byte[32];
        SEC.nextBytes(sig);
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        return header + "." + payload + "." + signature;
    }

    private static String b64url(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes());
    }

    // ── Hash ──────────────────────────────────────────────────────────────────

    private static String hash(String algorithm) {
        String algo = algorithm.toLowerCase();
        // CRC variants — fixed output widths, no random bytes needed
        if ("crc32".equals(algo)) {
            byte[] dummy = new byte[8]; SEC.nextBytes(dummy);
            java.util.zip.CRC32 crc = new java.util.zip.CRC32(); crc.update(dummy);
            return String.format("%08x", crc.getValue());
        }
        if ("adler32".equals(algo)) {
            byte[] dummy = new byte[8]; SEC.nextBytes(dummy);
            java.util.zip.Adler32 a = new java.util.zip.Adler32(); a.update(dummy);
            return String.format("%08x", a.getValue());
        }
        if ("crc16".equals(algo)) {
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            return String.format("%04x", rng.nextInt(0x10000));
        }
        // SHA/MD variants — map to byte count
        int bytes = switch (algo) {
            case "md5"                        -> 16;
            case "sha1"                       -> 20;
            case "sha224","sha3-224"          -> 28;
            case "sha256","sha3-256"          -> 32;
            case "sha384","sha3-384"          -> 48;
            case "sha512","sha3-512"          -> 64;
            default                           -> 32; // sha256
        };
        byte[] b = new byte[bytes];
        SEC.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }

    private static String color(ThreadLocalRandom rng, String format) {
        String[] entry = COLOR_NAMES_HEX[rng.nextInt(COLOR_NAMES_HEX.length)];
        String name = entry[0], hex = entry[1];
        int r = Integer.parseInt(hex.substring(1,3), 16);
        int g = Integer.parseInt(hex.substring(3,5), 16);
        int b = Integer.parseInt(hex.substring(5,7), 16);
        return switch (format.toLowerCase()) {
            case "rgb"  -> String.format("rgb(%d, %d, %d)", r, g, b);
            case "hsl"  -> rgbToHsl(r, g, b);
            case "name" -> name;
            default     -> hex; // hex (default)
        };
    }

    private static String rgbToHsl(int r, int g, int b) {
        double rf = r / 255.0, gf = g / 255.0, bf = b / 255.0;
        double max = Math.max(rf, Math.max(gf, bf)), min = Math.min(rf, Math.min(gf, bf));
        double l = (max + min) / 2.0;
        double h, s;
        if (max == min) { h = 0; s = 0; }
        else {
            double d = max - min;
            s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
            if (max == rf) h = (gf - bf) / d + (gf < bf ? 6 : 0);
            else if (max == gf) h = (bf - rf) / d + 2;
            else h = (rf - gf) / d + 4;
            h /= 6.0;
        }
        return String.format("hsl(%d, %d%%, %d%%)", (int)(h*360), (int)(s*100), (int)(l*100));
    }

    // ── Signature ─────────────────────────────────────────────────────────────

    private static String signature(String qualifier) {
        String secret = "ninja";
        String payload = "mock";
        if (!qualifier.isEmpty()) {
            String[] parts = qualifier.split("\\|", 2);
            if (!parts[0].isEmpty()) secret = parts[0];
            if (parts.length > 1 && !parts[1].isEmpty()) payload = parts[1];
        }
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(sig);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ── App Password — 6-digit PIN, no consecutive repeats, no sequential run of 3+ ──

    private static String appPassword(ThreadLocalRandom rng) {
        int[] d;
        do {
            d = new int[6];
            for (int i = 0; i < 6; i++) d[i] = rng.nextInt(10);
        } while (hasConsecutiveRepeat(d) || hasSequentialRun(d));
        StringBuilder sb = new StringBuilder(6);
        for (int v : d) sb.append(v);
        return sb.toString();
    }

    private static boolean hasConsecutiveRepeat(int[] d) {
        for (int i = 0; i < d.length - 1; i++) if (d[i] == d[i + 1]) return true;
        return false;
    }

    private static boolean hasSequentialRun(int[] d) {
        for (int i = 0; i < d.length - 2; i++) {
            if (d[i + 1] - d[i] == 1 && d[i + 2] - d[i + 1] == 1) return true;
            if (d[i] - d[i + 1] == 1 && d[i + 1] - d[i + 2] == 1) return true;
        }
        return false;
    }

    // ── MAC address (IEEE OUI prefix + 3 random bytes) ───────────────────────

    private static final String[] OUI_PREFIXES = {
        "A4:C3:F0","3C:22:FB","B8:27:EB","DC:2C:6E","00:50:56","08:00:27",
        "D8:BB:2C","28:6F:7F","F0:18:98","00:1C:42","00:23:AE","AC:BC:32",
        "F4:5C:89","70:F0:96","CC:46:D6","00:0C:29","44:38:39","2C:F0:5D",
        "B0:BE:83","00:25:90","3C:D9:2B","78:E3:B5","00:1A:11","54:EE:75",
        "00:17:88","18:B4:30","70:85:C2","00:27:22","44:D9:E7","A8:40:41"
    };

    private static String macAddress(ThreadLocalRandom rng) {
        return OUI_PREFIXES[rng.nextInt(OUI_PREFIXES.length)] + ":" +
               String.format("%02X:%02X:%02X",
                   rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
    }

    // ── Domain & URL ─────────────────────────────────────────────────────────

    private static final String[] DOMAIN_WORDS = {"api","data","test","mock","demo","dev","sample","sandbox","lab","platform"};

    private static String[] tldsForLocale(String locale) {
        String[] tlds = DOMAIN_TLDS.get(locale);
        return tlds != null ? tlds : DOMAIN_TLDS.get("TR");
    }

    private static String domain(ThreadLocalRandom rng, String locale) {
        String tld = pick(rng, tldsForLocale(locale));
        return pick(rng, DOMAIN_WORDS) + "-" + rng.nextInt(10, 100) + tld;
    }

    private static String url(ThreadLocalRandom rng, String locale) {
        String tld = pick(rng, tldsForLocale(locale));
        String host = "mockapi-" + rng.nextInt(100, 1000) + tld;
        String path = pick(rng, URL_PATHS);
        return "https://" + host + path;
    }

    // ── API key ───────────────────────────────────────────────────────────────

    private static final String API_KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static String apiKey(ThreadLocalRandom rng) {
        StringBuilder sb = new StringBuilder(48);
        for (int i = 0; i < 48; i++) sb.append(API_KEY_CHARS.charAt(rng.nextInt(API_KEY_CHARS.length())));
        return "mjk-" + sb;
    }

    // ── Slug ──────────────────────────────────────────────────────────────────

    private static final String[] SLUG_WORDS = {
        "api","user","account","payment","order","product","invoice",
        "customer","transaction","report","dashboard","settings","profile",
        "upload","download","search","filter","export","import","webhook",
        "session","auth","token","refresh","verify","confirm","reset",
        "admin","public","private","internal","external"
    };

    private static String slug(ThreadLocalRandom rng) {
        int n = rng.nextInt(2, 4);
        java.util.List<String> pool = new java.util.ArrayList<>(java.util.Arrays.asList(SLUG_WORDS));
        java.util.Collections.shuffle(pool, new java.util.Random(rng.nextLong()));
        java.util.List<String> parts = new java.util.ArrayList<>(pool.subList(0, n));
        if (rng.nextDouble() < 0.3) parts.add(String.valueOf(rng.nextInt(2020, 2027)));
        return String.join("-", parts);
    }

    // ── Hostname ──────────────────────────────────────────────────────────────

    private static final String[] HOSTNAME_WORDS = {
        "api","data","auth","gateway","proxy","cache","cdn","static",
        "media","stream","metrics","monitor","log","trace","event",
        "broker","queue","worker","scheduler","webhook","notify"
    };

    private static String hostname(ThreadLocalRandom rng) {
        String prefix = pick(rng, HOSTNAME_WORDS);
        String suffix = rng.nextDouble() < 0.5 ? String.format("-%02d", rng.nextInt(1, 100)) : "";
        return prefix + suffix;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static String randomHex(ThreadLocalRandom rng, int chars) {
        StringBuilder sb = new StringBuilder(chars);
        for (int i = 0; i < chars; i++) sb.append("0123456789abcdef".charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }

    private static int pick(ThreadLocalRandom rng, int[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
