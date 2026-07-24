package io.github.altansayan.mockjutsu.generators;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/** Security — CEF log, X.509 fields, pcap hex dump, password/hash, CVE ID. Mirrors security.py. */
public final class SecurityGen {
    private SecurityGen() {}
    private static final SecureRandom SEC = new SecureRandom();

    private static final String PWD_SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String BCRYPT_ALPHABET = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    private static final String[][] CEF_DEVICES = {
        {"Cisco", "ASA", "9.16"},
        {"Palo Alto Networks", "PAN-OS", "10.2"},
        {"Fortinet", "FortiGate", "7.4"},
        {"Check Point", "Firewall-1", "R81.20"},
        {"CrowdStrike", "Falcon", "6.58"},
        {"IBM", "QRadar SIEM", "7.5"},
        {"Splunk", "Enterprise Security", "7.3"},
        {"Darktrace", "Enterprise Immune System", "5.2"}
    };

    private static final Object[][] CEF_EVENTS = {
        {"100001", "Outbound Connection Allowed", 3, "proto=TCP act=allowed"},
        {"100002", "Inbound Connection Blocked", 5, "proto=TCP act=blocked"},
        {"200001", "Authentication Success", 2, "outcome=success"},
        {"200002", "Authentication Failure", 7, "outcome=failure"},
        {"300001", "Malware Detected", 9, "fname=payload.exe cs1=Trojan.GenericKD"},
        {"400001", "DDoS Attack Detected", 10, "cnt=50000 proto=UDP"},
        {"500001", "Data Exfiltration Attempt", 10, "bytesOut=10485760 proto=FTP"},
        {"600001", "Port Scan Detected", 6, "proto=TCP cnt=1024"},
        {"700001", "SQL Injection Attempt", 8, "request=/api/login cs1=blind"},
        {"800001", "Brute Force Attack", 7, "attempt=100 outcome=failure"},
        {"900001", "Privilege Escalation", 8, "suser=guest duser=root"},
        {"110001", "Lateral Movement", 7, "proto=SMB cs1=PsExec"}
    };

    private static final String[] COMMON_NAMES = {
        "api.example.com", "secure.corp.net", "auth.internal.io",
        "app.enterprise.org", "gateway.prod.local", "portal.company.com",
        "vpn.office.net", "sso.enterprise.io"
    };
    private static final String[] CA_ISSUERS = {
        "CN=DigiCert TLS RSA SHA256 2020 CA1, O=DigiCert Inc, C=US",
        "CN=Let's Encrypt Authority X3, O=Let's Encrypt, C=US",
        "CN=Sectigo RSA Domain Validation Secure Server CA, O=Sectigo Limited, C=GB",
        "CN=GlobalSign RSA OV SSL CA 2018, O=GlobalSign nv-sa, C=BE",
        "CN=Amazon RSA 2048 M02, O=Amazon, C=US",
        "CN=Microsoft Azure TLS Issuing CA 01, O=Microsoft Corporation, C=US"
    };
    private static final String[] ORGANIZATIONS = {
        "Acme Corp", "TechCorp Inc", "SecureBank Ltd", "GlobalFinance AG",
        "Enterprise Solutions LLC", "DataSystems GmbH", "InfoSec Corp"
    };
    private static final String[] ALGORITHMS = {"sha256WithRSAEncryption", "ecdsa-with-SHA256"};
    private static final int[] KEY_SIZES = {2048, 4096};
    private static final String[] COUNTRIES = {"US", "GB", "DE", "TR", "NL", "FR", "JP", "SG"};
    private static final int[] COMMON_PORTS = {22, 80, 443, 8080, 8443, 3306, 5432, 6379, 3389, 5900};

    private static final byte[][] PCAP_PAYLOADS = {
        "GET / HTTP/1.1\r\nHost: api.example.com\r\nUser-Agent: Mozilla/5.0\r\n\r\n".getBytes(StandardCharsets.US_ASCII),
        "POST /api/v1/auth HTTP/1.1\r\nContent-Type: application/json\r\nContent-Length: 42\r\n\r\n".getBytes(StandardCharsets.US_ASCII),
        {0x16, 0x03, 0x01, 0x00, (byte) 0xf1, 0x01, 0x00, 0x00, (byte) 0xed, 0x03, 0x03},
        "SSH-2.0-OpenSSH_8.9p1 Ubuntu-3ubuntu0.6\r\n".getBytes(StandardCharsets.US_ASCII),
        {0x00, 0x00, 0x00, 0x05, 0x01, 0x00, 0x00, 0x00, 0x00}
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "cef_log"      -> cefLog(rng);
            case "x509_cert"    -> x509Cert(rng);
            case "pcap_hex"     -> pcapHex(rng);
            case "password"     -> password(rng);
            case "password_hash" -> passwordHash(rng);
            case "cve_id"       -> cveId(rng);
            default -> "ERROR: Unknown security type '" + type + "'";
        };
    }

    // ── Password ──────────────────────────────────────────────────────────────

    private static String password(ThreadLocalRandom rng) {
        int length = rng.nextInt(12, 21);
        java.util.List<Character> chars = new java.util.ArrayList<>();
        chars.add(UPPER.charAt(rng.nextInt(UPPER.length())));
        chars.add(LOWER.charAt(rng.nextInt(LOWER.length())));
        chars.add(DIGITS.charAt(rng.nextInt(DIGITS.length())));
        chars.add(PWD_SPECIAL.charAt(rng.nextInt(PWD_SPECIAL.length())));
        String pool = UPPER + LOWER + DIGITS + PWD_SPECIAL;
        for (int i = 0; i < length - 4; i++) chars.add(pool.charAt(rng.nextInt(pool.length())));
        java.util.Collections.shuffle(chars, new java.util.Random(rng.nextLong()));
        StringBuilder sb = new StringBuilder(chars.size());
        for (char c : chars) sb.append(c);
        return sb.toString();
    }

    // ── Password Hash (bcrypt format) ────────────────────────────────────────

    private static String passwordHash(ThreadLocalRandom rng) {
        int cost = rng.nextInt(10, 15);
        StringBuilder body = new StringBuilder(53);
        for (int i = 0; i < 53; i++) body.append(BCRYPT_ALPHABET.charAt(rng.nextInt(BCRYPT_ALPHABET.length())));
        return String.format("$2b$%02d$%s", cost, body);
    }

    // ── CVE ID ────────────────────────────────────────────────────────────────

    private static String cveId(ThreadLocalRandom rng) {
        int year = rng.nextInt(2000, 2026);
        int num = rng.nextInt(1000, 100000);
        return "CVE-" + year + "-" + num;
    }

    // ── CEF Log ───────────────────────────────────────────────────────────────

    private static int[] rip(ThreadLocalRandom rng) {
        return new int[]{rng.nextInt(1, 255), rng.nextInt(0, 255), rng.nextInt(0, 255), rng.nextInt(1, 255)};
    }

    private static String ipToString(int[] ip) {
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }

    private static String cefLog(ThreadLocalRandom rng) {
        String[] device = CEF_DEVICES[rng.nextInt(CEF_DEVICES.length)];
        Object[] event = CEF_EVENTS[rng.nextInt(CEF_EVENTS.length)];
        String vendor = device[0], product = device[1], version = device[2];
        String sigId = (String) event[0], name = (String) event[1];
        int severity = (int) event[2];
        String bExt = (String) event[3];

        String srcIp = ipToString(rip(rng));
        String dstIp = ipToString(rip(rng));
        int srcPort = rng.nextInt(1024, 65536);
        int dstPort = COMMON_PORTS[rng.nextInt(COMMON_PORTS.length)];
        String user = "user" + rng.nextInt(100, 10000);
        String ext = "src=" + srcIp + " dst=" + dstIp + " spt=" + srcPort + " dpt=" + dstPort +
            " suser=" + user + " " + bExt;

        return "CEF:0|" + vendor + "|" + product + "|" + version + "|" + sigId + "|" + name + "|" + severity + "|" + ext;
    }

    // ── X.509 Certificate fields ─────────────────────────────────────────────

    private static String x509Cert(ThreadLocalRandom rng) {
        String cn = COMMON_NAMES[rng.nextInt(COMMON_NAMES.length)];
        String org = ORGANIZATIONS[rng.nextInt(ORGANIZATIONS.length)];
        String country = COUNTRIES[rng.nextInt(COUNTRIES.length)];
        String subject = "CN=" + cn + ", O=" + org + ", OU=IT Security, C=" + country;
        String issuer = CA_ISSUERS[rng.nextInt(CA_ISSUERS.length)];

        byte[] serialBytes = new byte[16];
        SEC.nextBytes(serialBytes);
        String serial = bytesToHexLower(serialBytes);

        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        java.time.ZonedDateTime notBefore = now.minusDays(rng.nextInt(30, 366));
        int[] validityDays = {365, 397, 730};
        java.time.ZonedDateTime notAfter = notBefore.plusDays(validityDays[rng.nextInt(validityDays.length)]);

        byte[] fpBytes = new byte[32];
        SEC.nextBytes(fpBytes);
        StringBuilder fingerprint = new StringBuilder();
        for (int i = 0; i < fpBytes.length; i++) {
            if (i > 0) fingerprint.append(':');
            fingerprint.append(String.format("%02X", fpBytes[i]));
        }

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return "{\"version\":3,\"serial\":\"" + serial + "\",\"algorithm\":\"" + ALGORITHMS[rng.nextInt(ALGORITHMS.length)] +
            "\",\"key_size\":" + KEY_SIZES[rng.nextInt(KEY_SIZES.length)] + ",\"subject\":\"" + subject + "\"," +
            "\"issuer\":\"" + issuer + "\"," +
            "\"not_before\":\"" + notBefore.format(fmt) + "\",\"not_after\":\"" + notAfter.format(fmt) + "\"," +
            "\"san\":[\"" + cn + "\",\"www." + cn + "\"]," +
            "\"fingerprint\":\"" + fingerprint + "\"}";
    }

    private static String bytesToHexLower(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte v : b) sb.append(String.format("%02x", v));
        return sb.toString();
    }

    // ── PCAP hex dump (libpcap format) ───────────────────────────────────────

    private static String pcapHex(ThreadLocalRandom rng) {
        byte[] globalHeader = {
            (byte) 0xd4, (byte) 0xc3, (byte) 0xb2, (byte) 0xa1, // magic (LE)
            0x02, 0x00, // version major = 2
            0x04, 0x00, // version minor = 4
            0x00, 0x00, 0x00, 0x00, // thiszone
            0x00, 0x00, 0x00, 0x00, // sigfigs
            (byte) 0xff, (byte) 0xff, 0x00, 0x00, // snaplen = 65535
            0x01, 0x00, 0x00, 0x00  // network = LINKTYPE_ETHERNET
        };

        byte[] dstMac = randomBytes(rng, 6);
        byte[] srcMac = randomBytes(rng, 6);
        byte[] etherType = {0x08, 0x00};

        int[] srcIp = rip(rng);
        int[] dstIp = rip(rng);
        int srcPort = rng.nextInt(1024, 65536);
        int dstPort = COMMON_PORTS[rng.nextInt(COMMON_PORTS.length)];
        byte[] payload = PCAP_PAYLOADS[rng.nextInt(PCAP_PAYLOADS.length)];

        byte[] tcpHeader = concat(
            be2(srcPort), be2(dstPort),
            be4(rng.nextLong(0, 4294967296L)), // seq
            be4(rng.nextLong(0, 4294967296L)), // ack
            new byte[]{0x50, 0x18}, // offset=5, PSH+ACK
            new byte[]{(byte) 0xff, (byte) 0xff}, // window
            new byte[]{0x00, 0x00}, // checksum (mock)
            new byte[]{0x00, 0x00}  // urgent
        );

        int totalLen = 20 + tcpHeader.length + payload.length;
        byte[] ipHeader = concat(
            new byte[]{0x45, 0x00},
            be2(totalLen),
            be2(rng.nextInt(65536)), // ID
            new byte[]{0x40, 0x00}, // Don't Fragment
            new byte[]{0x40, 0x06, 0x00, 0x00}, // TTL=64, proto=TCP, chksum=0
            ipBytes(srcIp), ipBytes(dstIp)
        );

        byte[] frame = concat(dstMac, srcMac, etherType, ipHeader, tcpHeader, payload);
        int frameLen = frame.length;

        long ts = System.currentTimeMillis() / 1000;
        byte[] pktHeader = concat(
            le4(ts),
            new byte[]{0x00, 0x00, 0x00, 0x00},
            le4(frameLen),
            le4(frameLen)
        );

        byte[] raw = concat(globalHeader, pktHeader, frame);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < raw.length; i += 16) {
            if (i > 0) out.append('\n');
            int end = Math.min(i + 16, raw.length);
            for (int j = i; j < end; j++) {
                if (j > i) out.append(' ');
                out.append(String.format("%02x", raw[j]));
            }
        }
        return out.toString();
    }

    private static byte[] ipBytes(int[] ip) {
        return new byte[]{(byte) ip[0], (byte) ip[1], (byte) ip[2], (byte) ip[3]};
    }

    private static byte[] be2(int v) {
        return new byte[]{(byte) (v >> 8), (byte) v};
    }

    private static byte[] be4(long v) {
        return new byte[]{(byte) (v >> 24), (byte) (v >> 16), (byte) (v >> 8), (byte) v};
    }

    private static byte[] le4(long v) {
        return new byte[]{(byte) v, (byte) (v >> 8), (byte) (v >> 16), (byte) (v >> 24)};
    }

    private static byte[] randomBytes(ThreadLocalRandom rng, int n) {
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) b[i] = (byte) rng.nextInt(256);
        return b;
    }

    private static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] p : parts) len += p.length;
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] p : parts) { System.arraycopy(p, 0, out, pos, p.length); pos += p.length; }
        return out;
    }
}
