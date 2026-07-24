package io.github.altansayan.mockjutsu.generators;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** OIDC / JWT cryptographic signature kit — real ES256 (P-256) + HS256. Mirrors oidc.py. */
public final class OidcGen {
    private OidcGen() {}
    private static final SecureRandom SEC = new SecureRandom();

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        try {
            return switch (type) {
                case "oidc_token_set" -> tokenSet(rng);
                case "jwks"           -> jwks();
                case "oidc_token"     -> oidcToken(rng);
                default -> "ERROR: Unknown OIDC type '" + type + "'";
            };
        } catch (Exception e) {
            return "ERROR: OIDC generation failed: " + e.getMessage();
        }
    }

    private static String oidcClaimsJson(ThreadLocalRandom rng) {
        long now = System.currentTimeMillis() / 1000;
        return "{\"iss\":\"https://mock-issuer.example.com\",\"sub\":\"user-" + UUID.randomUUID() +
            "\",\"aud\":\"mock-client\",\"exp\":" + (now + 3600) + ",\"iat\":" + now +
            ",\"jti\":\"" + UUID.randomUUID() + "\",\"email\":\"user" + rng.nextInt(1, 10000) +
            "@example.com\",\"name\":\"Mock User " + rng.nextInt(1, 1000) + "\"}";
    }

    private static String tokenSet(ThreadLocalRandom rng) throws Exception {
        BigInteger nMinus1 = EcCrypto.P256_N.subtract(BigInteger.ONE);
        BigInteger privkey = new BigInteger(EcCrypto.P256_N.bitLength(), SEC).mod(nMinus1).add(BigInteger.ONE);
        EcCrypto.Point pub = EcCrypto.p256MultiplyBase(privkey);
        String kid = UUID.randomUUID().toString().substring(0, 8);

        String xB64 = b64uBigInt(pub.x());
        String yB64 = b64uBigInt(pub.y());
        String jwk = "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"" + xB64 + "\",\"y\":\"" + yB64 +
            "\",\"kid\":\"" + kid + "\",\"use\":\"sig\",\"alg\":\"ES256\"}";

        String claims = oidcClaimsJson(rng);
        String header = b64u(("{\"alg\":\"ES256\",\"typ\":\"JWT\",\"kid\":\"" + kid + "\"}").getBytes(StandardCharsets.UTF_8));
        String payload = b64u(claims.getBytes(StandardCharsets.UTF_8));
        byte[] signing = (header + "." + payload).getBytes(StandardCharsets.UTF_8);
        byte[] msgHash = EcCrypto.sha256(signing);
        byte[] sig = EcCrypto.ecdsaSignP256(privkey, msgHash, SEC);
        String token = header + "." + payload + "." + b64u(sig);

        return "{\"token\":\"" + token + "\",\"jwks\":{\"keys\":[" + jwk + "]},\"kid\":\"" + kid +
            "\",\"claims\":" + claims + "}";
    }

    private static String jwks() throws Exception {
        BigInteger nMinus1 = EcCrypto.P256_N.subtract(BigInteger.ONE);
        BigInteger privkey = new BigInteger(EcCrypto.P256_N.bitLength(), SEC).mod(nMinus1).add(BigInteger.ONE);
        EcCrypto.Point pub = EcCrypto.p256MultiplyBase(privkey);
        String kid = UUID.randomUUID().toString().substring(0, 8);
        String xB64 = b64uBigInt(pub.x());
        String yB64 = b64uBigInt(pub.y());
        return "{\"keys\":[{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"" + xB64 + "\",\"y\":\"" + yB64 +
            "\",\"kid\":\"" + kid + "\",\"use\":\"sig\",\"alg\":\"ES256\"}]}";
    }

    private static String oidcToken(ThreadLocalRandom rng) throws Exception {
        byte[] secret = new byte[32];
        SEC.nextBytes(secret);
        String claims = oidcClaimsJson(rng);
        String header = b64u("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = b64u(claims.getBytes(StandardCharsets.UTF_8));
        byte[] signing = (header + "." + payload).getBytes(StandardCharsets.UTF_8);
        byte[] sig = CryptoFuzzGen.hmacSha256(secret, signing);
        return header + "." + payload + "." + b64u(sig);
    }

    private static String b64u(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private static String b64uBigInt(BigInteger v) {
        byte[] raw = v.toByteArray();
        byte[] out = new byte[32];
        if (raw.length >= 32) {
            System.arraycopy(raw, raw.length - 32, out, 0, 32);
        } else {
            System.arraycopy(raw, 0, out, 32 - raw.length, raw.length);
        }
        return b64u(out);
    }
}
