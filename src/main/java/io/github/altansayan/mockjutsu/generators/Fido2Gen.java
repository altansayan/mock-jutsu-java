package io.github.altansayan.mockjutsu.generators;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/** FIDO2/WebAuthn — credential and assertion. Mirrors fido2.py (minimal CBOR encoder). */
public final class Fido2Gen {
    private Fido2Gen() {}
    private static final SecureRandom SEC = new SecureRandom();

    private static final String[] RP_IDS = {
        "example.com", "login.acme.io", "auth.mockjutsu.dev", "accounts.test.org", "secure.app.localhost"
    };
    private static final java.util.Map<String, String> ORIGINS = java.util.Map.of(
        "example.com", "https://example.com",
        "login.acme.io", "https://login.acme.io",
        "auth.mockjutsu.dev", "https://auth.mockjutsu.dev",
        "accounts.test.org", "https://accounts.test.org",
        "secure.app.localhost", "http://localhost:3000"
    );

    public static String generate(String type, String locale) {
        return switch (type) {
            case "webauthn_credential" -> webauthnCredential();
            case "fido2_assertion"     -> fido2Assertion();
            default -> "ERROR: Unknown FIDO2 type '" + type + "'";
        };
    }

    private static String webauthnCredential() {
        try {
            String rpId = RP_IDS[SEC.nextInt(RP_IDS.length)];
            String origin = ORIGINS.get(rpId);
            byte[] credId = randomBytes(32);
            byte[] challenge = randomBytes(32);
            int counter = SEC.nextInt(1000);
            byte[] x = randomBytes(32);
            byte[] y = randomBytes(32);

            String clientDataJson = "{\"type\":\"webauthn.create\",\"challenge\":\"" + b64url(challenge) +
                "\",\"origin\":\"" + origin + "\",\"crossOrigin\":false}";
            byte[] cdjBytes = clientDataJson.getBytes(StandardCharsets.UTF_8);

            byte[] cose = coseKey(x, y);
            byte[] authData = authDataRegistration(rpId, counter, credId, cose);
            byte[] attObj = concat(
                cborMapHeader(3),
                cborText("fmt"), cborText("none"),
                cborText("attStmt"), cborMapHeader(0),
                cborText("authData"), cborBytes(authData)
            );

            return "{\"id\":\"" + b64url(credId) + "\",\"rawId\":\"" + b64url(credId) + "\",\"type\":\"public-key\"," +
                "\"response\":{\"clientDataJSON\":\"" + b64url(cdjBytes) + "\",\"attestationObject\":\"" + b64url(attObj) + "\"}," +
                "\"clientExtensionResults\":{}}";
        } catch (Exception e) {
            return "ERROR: webauthn_credential generation failed: " + e.getMessage();
        }
    }

    private static String fido2Assertion() {
        try {
            String rpId = RP_IDS[SEC.nextInt(RP_IDS.length)];
            String origin = ORIGINS.get(rpId);
            byte[] credId = randomBytes(32);
            byte[] challenge = randomBytes(32);
            int counter = SEC.nextInt(99999) + 1;
            byte[] userId = randomBytes(16);

            String clientDataJson = "{\"type\":\"webauthn.get\",\"challenge\":\"" + b64url(challenge) +
                "\",\"origin\":\"" + origin + "\",\"crossOrigin\":false}";
            byte[] cdjBytes = clientDataJson.getBytes(StandardCharsets.UTF_8);

            byte[] rpHash = EcCrypto.sha256(rpId.getBytes(StandardCharsets.UTF_8));
            byte[] flags = {0x05};
            byte[] authData = concat(rpHash, flags, be4(counter));

            byte[] sig = derSignature();

            return "{\"id\":\"" + b64url(credId) + "\",\"rawId\":\"" + b64url(credId) + "\",\"type\":\"public-key\"," +
                "\"response\":{\"clientDataJSON\":\"" + b64url(cdjBytes) + "\",\"authenticatorData\":\"" + b64url(authData) +
                "\",\"signature\":\"" + b64url(sig) + "\",\"userHandle\":\"" + b64url(userId) + "\"}," +
                "\"clientExtensionResults\":{}}";
        } catch (Exception e) {
            return "ERROR: fido2_assertion generation failed: " + e.getMessage();
        }
    }

    // ── CBOR (RFC 7049) — minimal encoder for the WebAuthn subset ────────────

    private static byte[] cborUint(int major, long value) {
        int mt = major << 5;
        if (value <= 23) return new byte[]{(byte) (mt | value)};
        if (value <= 0xFF) return concat(new byte[]{(byte) (mt | 24)}, new byte[]{(byte) value});
        if (value <= 0xFFFF) return concat(new byte[]{(byte) (mt | 25)}, be2((int) value));
        return concat(new byte[]{(byte) (mt | 26)}, be4((int) value));
    }

    private static byte[] cborInt(long v) {
        return v >= 0 ? cborUint(0, v) : cborUint(1, -v - 1);
    }

    private static byte[] cborBytes(byte[] b) {
        return concat(cborUint(2, b.length), b);
    }

    private static byte[] cborText(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        return concat(cborUint(3, b.length), b);
    }

    private static byte[] cborMapHeader(int n) {
        return cborUint(5, n);
    }

    // EC P-256 public key in COSE_Key (CBOR) format — RFC 8152.
    private static byte[] coseKey(byte[] x, byte[] y) {
        return concat(
            cborMapHeader(5),
            cborInt(1), cborInt(2),
            cborInt(3), cborInt(-7),
            cborInt(-1), cborInt(1),
            cborInt(-2), cborBytes(x),
            cborInt(-3), cborBytes(y)
        );
    }

    private static byte[] authDataRegistration(String rpId, int counter, byte[] credId, byte[] cose) throws Exception {
        byte[] rpHash = EcCrypto.sha256(rpId.getBytes(StandardCharsets.UTF_8));
        byte[] flags = {0x45};
        byte[] counterB = be4(counter);
        byte[] aaguid = new byte[16];
        byte[] credLen = be2(credId.length);
        return concat(rpHash, flags, counterB, aaguid, credLen, credId, cose);
    }

    private static byte[] derSignature() {
        byte[] r = randomBytes(32);
        byte[] s = randomBytes(32);
        r[0] &= 0x7F;
        s[0] &= 0x7F;
        byte[] body = concat(new byte[]{0x02, (byte) r.length}, r, new byte[]{0x02, (byte) s.length}, s);
        return concat(new byte[]{0x30, (byte) body.length}, body);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static byte[] randomBytes(int n) {
        byte[] b = new byte[n];
        SEC.nextBytes(b);
        return b;
    }

    private static byte[] be2(int v) {
        return new byte[]{(byte) ((v >> 8) & 0xFF), (byte) (v & 0xFF)};
    }

    private static byte[] be4(int v) {
        return new byte[]{(byte) ((v >> 24) & 0xFF), (byte) ((v >> 16) & 0xFF), (byte) ((v >> 8) & 0xFF), (byte) (v & 0xFF)};
    }

    private static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] p : parts) len += p.length;
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] p : parts) { System.arraycopy(p, 0, out, pos, p.length); pos += p.length; }
        return out;
    }

    private static String b64url(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
