package io.github.altansayan.mockjutsu.generators;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Real elliptic-curve cryptography helpers — mirrors crypto.py / wallet.py.
 * Uses java.math.BigInteger affine-coordinate arithmetic (simpler than the
 * Jacobian/extended-coordinate optimizations in the Python port, but produces
 * identical mathematical results).
 */
final class EcCrypto {
    private EcCrypto() {}

    // ── secp256k1 curve parameters ───────────────────────────────────────────
    static final BigInteger SECP_P = new BigInteger(
        "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    static final BigInteger SECP_N = new BigInteger(
        "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    static final BigInteger SECP_GX = new BigInteger(
        "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16);
    static final BigInteger SECP_GY = new BigInteger(
        "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16);

    record Point(BigInteger x, BigInteger y) {}

    private static Point secpDouble(Point p, BigInteger mod) {
        BigInteger lam = BigInteger.valueOf(3).multiply(p.x).multiply(p.x)
            .multiply(BigInteger.TWO.multiply(p.y).modInverse(mod)).mod(mod);
        BigInteger x3 = lam.multiply(lam).subtract(p.x.multiply(BigInteger.TWO)).mod(mod);
        BigInteger y3 = lam.multiply(p.x.subtract(x3)).subtract(p.y).mod(mod);
        return new Point(x3, y3);
    }

    private static Point secpAdd(Point p, Point q, BigInteger mod) {
        if (p == null) return q;
        if (q == null) return p;
        if (p.x.equals(q.x)) {
            if (!p.y.equals(q.y)) return null; // point at infinity
            return secpDouble(p, mod);
        }
        BigInteger lam = q.y.subtract(p.y).multiply(q.x.subtract(p.x).modInverse(mod)).mod(mod);
        BigInteger x3 = lam.multiply(lam).subtract(p.x).subtract(q.x).mod(mod);
        BigInteger y3 = lam.multiply(p.x.subtract(x3)).subtract(p.y).mod(mod);
        return new Point(x3, y3);
    }

    /** Scalar-multiply the secp256k1 base point by k. Returns affine (x, y). */
    static Point secpMultiplyBase(BigInteger k) {
        Point result = null;
        Point base = new Point(SECP_GX, SECP_GY);
        Point addend = base;
        while (k.signum() > 0) {
            if (k.testBit(0)) result = secpAdd(result, addend, SECP_P);
            addend = secpDouble(addend, SECP_P);
            k = k.shiftRight(1);
        }
        return result;
    }

    // ── P-256 (secp256r1 / prime256v1) curve parameters ──────────────────────
    static final BigInteger P256_P = new BigInteger(
        "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16);
    static final BigInteger P256_A = new BigInteger(
        "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16);
    static final BigInteger P256_GX = new BigInteger(
        "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16);
    static final BigInteger P256_GY = new BigInteger(
        "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16);
    static final BigInteger P256_N = new BigInteger(
        "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16);

    private static Point p256Double(Point p) {
        BigInteger mod = P256_P;
        BigInteger lam = BigInteger.valueOf(3).multiply(p.x).multiply(p.x).add(P256_A)
            .multiply(BigInteger.TWO.multiply(p.y).modInverse(mod)).mod(mod);
        BigInteger x3 = lam.multiply(lam).subtract(p.x.multiply(BigInteger.TWO)).mod(mod);
        BigInteger y3 = lam.multiply(p.x.subtract(x3)).subtract(p.y).mod(mod);
        return new Point(x3, y3);
    }

    private static Point p256Add(Point p, Point q) {
        BigInteger mod = P256_P;
        if (p == null) return q;
        if (q == null) return p;
        if (p.x.equals(q.x)) {
            if (!p.y.equals(q.y)) return null; // point at infinity
            return p256Double(p);
        }
        BigInteger lam = q.y.subtract(p.y).multiply(q.x.subtract(p.x).modInverse(mod)).mod(mod);
        BigInteger x3 = lam.multiply(lam).subtract(p.x).subtract(q.x).mod(mod);
        BigInteger y3 = lam.multiply(p.x.subtract(x3)).subtract(p.y).mod(mod);
        return new Point(x3, y3);
    }

    /** Scalar-multiply the P-256 base point by k. Returns affine (x, y). */
    static Point p256MultiplyBase(BigInteger k) {
        Point result = null;
        Point base = new Point(P256_GX, P256_GY);
        Point addend = base;
        while (k.signum() > 0) {
            if (k.testBit(0)) result = p256Add(result, addend);
            addend = p256Double(addend);
            k = k.shiftRight(1);
        }
        return result;
    }

    /** ECDSA sign (ES256 = ECDSA + SHA-256 + P-256). Returns raw r‖s (64 bytes). */
    static byte[] ecdsaSignP256(BigInteger privkey, byte[] msgHash, java.security.SecureRandom secRandom) {
        BigInteger h = new BigInteger(1, msgHash);
        BigInteger nMinus1 = P256_N.subtract(BigInteger.ONE);
        while (true) {
            BigInteger k;
            do {
                k = new BigInteger(P256_N.bitLength(), secRandom).mod(nMinus1).add(BigInteger.ONE);
            } while (k.signum() <= 0 || k.compareTo(P256_N) >= 0);
            Point r1 = p256MultiplyBase(k);
            BigInteger r = r1.x.mod(P256_N);
            if (r.signum() == 0) continue;
            BigInteger kInv = k.modInverse(P256_N);
            BigInteger s = kInv.multiply(h.add(r.multiply(privkey))).mod(P256_N);
            if (s.signum() == 0) continue;
            byte[] sig = new byte[64];
            System.arraycopy(toBigEndian32(r), 0, sig, 0, 32);
            System.arraycopy(toBigEndian32(s), 0, sig, 32, 32);
            return sig;
        }
    }

    private static byte[] toBigEndian32(BigInteger v) {
        byte[] raw = v.toByteArray();
        byte[] out = new byte[32];
        if (raw.length >= 32) {
            System.arraycopy(raw, raw.length - 32, out, 0, 32);
        } else {
            System.arraycopy(raw, 0, out, 32 - raw.length, raw.length);
        }
        return out;
    }

    // ── Ed25519 curve parameters ──────────────────────────────────────────────
    static final BigInteger ED_P = BigInteger.TWO.pow(255).subtract(BigInteger.valueOf(19));
    static final BigInteger ED_D;
    static final Point ED_BASE;
    static {
        BigInteger inv121666 = BigInteger.valueOf(121666).modPow(ED_P.subtract(BigInteger.TWO), ED_P);
        ED_D = BigInteger.valueOf(-121665).multiply(inv121666).mod(ED_P);
        ED_BASE = edBasePoint();
    }

    private static Point edBasePoint() {
        BigInteger four = BigInteger.valueOf(4);
        BigInteger five = BigInteger.valueOf(5);
        BigInteger by = four.multiply(five.modPow(ED_P.subtract(BigInteger.TWO), ED_P)).mod(ED_P);
        BigInteger y2 = by.multiply(by).mod(ED_P);
        BigInteger num = y2.subtract(BigInteger.ONE).mod(ED_P);
        BigInteger den = ED_D.multiply(y2).add(BigInteger.ONE).mod(ED_P);
        BigInteger x2 = num.multiply(den.modPow(ED_P.subtract(BigInteger.TWO), ED_P)).mod(ED_P);
        BigInteger exp = ED_P.add(BigInteger.valueOf(3)).divide(BigInteger.valueOf(8));
        BigInteger bx = x2.modPow(exp, ED_P);
        if (!bx.multiply(bx).subtract(x2).mod(ED_P).equals(BigInteger.ZERO)) {
            BigInteger two = BigInteger.TWO;
            BigInteger sqrtM1 = two.modPow(ED_P.subtract(BigInteger.ONE).divide(BigInteger.valueOf(4)), ED_P);
            bx = bx.multiply(sqrtM1).mod(ED_P);
        }
        if (bx.testBit(0)) bx = ED_P.subtract(bx);
        return new Point(bx, by);
    }

    // Unified affine addition formula for twisted Edwards curve (a = -1).
    private static Point edAdd(Point p, Point q) {
        BigInteger x1 = p.x, y1 = p.y, x2 = q.x, y2 = q.y;
        BigInteger dxy = ED_D.multiply(x1).mod(ED_P).multiply(x2).mod(ED_P).multiply(y1).mod(ED_P).multiply(y2).mod(ED_P);
        BigInteger xNum = x1.multiply(y2).add(x2.multiply(y1)).mod(ED_P);
        BigInteger xDen = BigInteger.ONE.add(dxy).mod(ED_P);
        BigInteger yNum = y1.multiply(y2).add(x1.multiply(x2)).mod(ED_P);
        BigInteger yDen = BigInteger.ONE.subtract(dxy).mod(ED_P);
        BigInteger x3 = xNum.multiply(xDen.modInverse(ED_P)).mod(ED_P);
        BigInteger y3 = yNum.multiply(yDen.modInverse(ED_P)).mod(ED_P);
        return new Point(x3, y3);
    }

    /** Scalar-multiply an Ed25519 point by k using double-and-add (affine). */
    static Point edMultiply(BigInteger k, Point p) {
        Point result = new Point(BigInteger.ZERO, BigInteger.ONE); // identity
        Point addend = p;
        while (k.signum() > 0) {
            if (k.testBit(0)) result = edAdd(result, addend);
            addend = edAdd(addend, addend);
            k = k.shiftRight(1);
        }
        return result;
    }

    /** Compress an Ed25519 point to 32 bytes (little-endian y, sign bit of x in MSB). */
    static byte[] edEncode(Point p) {
        byte[] yBytes = toLittleEndian32(p.y);
        if (p.x.testBit(0)) yBytes[31] |= (byte) 0x80;
        return yBytes;
    }

    private static byte[] toLittleEndian32(BigInteger v) {
        byte[] out = new byte[32];
        BigInteger tmp = v;
        for (int i = 0; i < 32; i++) {
            out[i] = tmp.and(BigInteger.valueOf(0xFF)).byteValue();
            tmp = tmp.shiftRight(8);
        }
        return out;
    }

    /** Derive an Ed25519 public key from a 32-byte private seed (RFC 8032). */
    static byte[] ed25519PublicKey(byte[] seed) throws NoSuchAlgorithmException {
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] h = sha512.digest(seed);
        byte[] s = new byte[32];
        System.arraycopy(h, 0, s, 0, 32);
        s[0] &= 248;
        s[31] &= 127;
        s[31] |= 64;
        BigInteger scalar = littleEndianToBigInteger(s);
        Point pub = edMultiply(scalar, ED_BASE);
        return edEncode(pub);
    }

    private static BigInteger littleEndianToBigInteger(byte[] b) {
        BigInteger v = BigInteger.ZERO;
        for (int i = b.length - 1; i >= 0; i--) {
            v = v.shiftLeft(8).or(BigInteger.valueOf(b[i] & 0xFF));
        }
        return v;
    }

    // ── Base58 / Base58Check ──────────────────────────────────────────────────
    private static final String B58 = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    static String base58Encode(byte[] payload) {
        BigInteger n = new BigInteger(1, payload);
        StringBuilder sb = new StringBuilder();
        BigInteger fiftyEight = BigInteger.valueOf(58);
        while (n.signum() > 0) {
            BigInteger[] qr = n.divideAndRemainder(fiftyEight);
            sb.append(B58.charAt(qr[1].intValue()));
            n = qr[0];
        }
        for (byte b : payload) {
            if (b == 0) sb.append('1');
            else break;
        }
        return sb.reverse().toString();
    }

    static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    static String base58Check(byte[] payload) throws NoSuchAlgorithmException {
        byte[] checksum = sha256(sha256(payload));
        byte[] full = new byte[payload.length + 4];
        System.arraycopy(payload, 0, full, 0, payload.length);
        System.arraycopy(checksum, 0, full, payload.length, 4);
        return base58Encode(full);
    }

    static byte[] hash160(byte[] data) throws Exception {
        byte[] sha = sha256(data);
        return ripemd160(sha);
    }

    // ── RIPEMD-160 (pure Java — no JDK/BouncyCastle provider available) ─────
    private static final int[] RMD_ZL = {
        0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
        7,4,13,1,10,6,15,3,12,0,9,5,2,14,11,8,
        3,10,14,4,9,15,8,1,2,7,0,6,13,11,5,12,
        1,9,11,10,0,8,12,4,13,3,7,15,14,5,6,2,
        4,0,5,9,7,12,2,10,14,1,3,8,11,6,15,13,
    };
    private static final int[] RMD_ZR = {
        5,14,7,0,9,2,11,4,13,6,15,8,1,10,3,12,
        6,11,3,7,0,13,5,10,14,15,8,12,4,9,1,2,
        15,5,1,3,7,14,6,9,11,8,12,2,10,0,4,13,
        8,6,4,1,3,11,15,0,5,12,2,13,9,7,10,14,
        12,15,10,4,1,5,8,7,6,2,13,14,0,3,9,11,
    };
    private static final int[] RMD_SL = {
        11,14,15,12,5,8,7,9,11,13,14,15,6,7,9,8,
        7,6,8,13,11,9,7,15,7,12,15,9,11,7,13,12,
        11,13,6,7,14,9,13,15,14,8,13,6,5,12,7,5,
        11,12,14,15,14,15,9,8,9,14,5,6,8,6,5,12,
        9,15,5,11,6,8,13,12,5,12,13,14,11,8,5,6,
    };
    private static final int[] RMD_SR = {
        8,9,9,11,13,15,15,5,7,7,8,11,14,14,12,6,
        9,13,15,7,12,8,9,11,7,7,12,7,6,15,13,11,
        9,7,15,11,8,6,6,14,12,13,5,14,13,13,7,5,
        15,5,8,11,14,14,6,14,6,9,12,9,12,5,15,8,
        8,5,12,9,12,5,14,6,8,13,6,5,15,13,11,11,
    };
    private static final int[] RMD_KL = {0x00000000, 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xA953FD4E};
    private static final int[] RMD_KR = {0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x7A6D76E9, 0x00000000};

    private static int rmdF(int j, int x, int y, int z) {
        if (j < 16) return x ^ y ^ z;
        if (j < 32) return (x & y) | (~x & z);
        if (j < 48) return (x | ~y) ^ z;
        if (j < 64) return (x & z) | (y & ~z);
        return x ^ (y | ~z);
    }

    private static int rmdRotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    static byte[] ripemd160(byte[] message) {
        long bitLen = (long) message.length * 8;
        int padLen = (int) (((55 - message.length) % 64 + 64) % 64) + 1;
        byte[] padded = new byte[message.length + padLen + 8];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) (bitLen >>> (8 * i));
        }

        int h0 = 0x67452301, h1 = 0xEFCDAB89, h2 = 0x98BADCFE, h3 = 0x10325476, h4 = 0xC3D2E1F0;

        for (int off = 0; off < padded.length; off += 64) {
            int[] x = new int[16];
            for (int i = 0; i < 16; i++) {
                x[i] = (padded[off + i * 4] & 0xFF) | ((padded[off + i * 4 + 1] & 0xFF) << 8)
                     | ((padded[off + i * 4 + 2] & 0xFF) << 16) | ((padded[off + i * 4 + 3] & 0xFF) << 24);
            }
            int al = h0, bl = h1, cl = h2, dl = h3, el = h4;
            int ar = h0, br = h1, cr = h2, dr = h3, er = h4;

            for (int j = 0; j < 80; j++) {
                int t = rmdRotl(al + rmdF(j, bl, cl, dl) + x[RMD_ZL[j]] + RMD_KL[j / 16], RMD_SL[j]) + el;
                al = el; el = dl; dl = rmdRotl(cl, 10); cl = bl; bl = t;

                t = rmdRotl(ar + rmdF(79 - j, br, cr, dr) + x[RMD_ZR[j]] + RMD_KR[j / 16], RMD_SR[j]) + er;
                ar = er; er = dr; dr = rmdRotl(cr, 10); cr = br; br = t;
            }

            int t = h1 + cl + dr;
            h1 = h2 + dl + er;
            h2 = h3 + el + ar;
            h3 = h4 + al + br;
            h4 = h0 + bl + cr;
            h0 = t;
        }

        byte[] out = new byte[20];
        int[] hs = {h0, h1, h2, h3, h4};
        for (int i = 0; i < 5; i++) {
            for (int b = 0; b < 4; b++) out[i * 4 + b] = (byte) (hs[i] >>> (8 * b));
        }
        return out;
    }

    // ── Keccak-256 (Ethereum variant, padding 0x01) ──────────────────────────
    private static final long[] RC = {
        0x0000000000000001L, 0x0000000000008082L, 0x800000000000808AL,
        0x8000000080008000L, 0x000000000000808BL, 0x0000000080000001L,
        0x8000000080008081L, 0x8000000000008009L, 0x000000000000008AL,
        0x0000000000000088L, 0x0000000080008009L, 0x000000008000000AL,
        0x000000008000808BL, 0x800000000000008BL, 0x8000000000008089L,
        0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
        0x000000000000800AL, 0x800000008000000AL, 0x8000000080008081L,
        0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L,
    };
    private static final int[][] ROT = {
        { 0, 36,  3, 41, 18},
        { 1, 44, 10, 45,  2},
        {62,  6, 43, 15, 61},
        {28, 55, 25, 21, 56},
        {27, 20, 39,  8, 14},
    };

    private static long rotl64(long x, int n) {
        return (x << n) | (x >>> (64 - n));
    }

    private static void keccakF(long[] s) {
        for (long rc : RC) {
            long[] c = new long[5];
            for (int x = 0; x < 5; x++) c[x] = s[x] ^ s[x + 5] ^ s[x + 10] ^ s[x + 15] ^ s[x + 20];
            long[] d = new long[5];
            for (int x = 0; x < 5; x++) d[x] = c[(x + 4) % 5] ^ rotl64(c[(x + 1) % 5], 1);
            for (int i = 0; i < 25; i++) s[i] ^= d[i % 5];
            long[] b = new long[25];
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    b[y + 5 * ((2 * x + 3 * y) % 5)] = rotl64(s[x + 5 * y], ROT[x][y]);
                }
            }
            for (int i = 0; i < 25; i++) {
                s[i] = b[i] ^ ((~b[(i % 5 + 1) % 5 + (i / 5) * 5]) & b[(i % 5 + 2) % 5 + (i / 5) * 5]);
            }
            s[0] ^= rc;
        }
    }

    static byte[] keccak256(byte[] data) {
        int rate = 136;
        int msgLen = data.length + 1;
        int padded = ((msgLen + rate - 1) / rate) * rate;
        byte[] msg = new byte[padded];
        System.arraycopy(data, 0, msg, 0, data.length);
        msg[data.length] = 0x01;
        msg[padded - 1] |= (byte) 0x80;

        long[] state = new long[25];
        for (int off = 0; off < msg.length; off += rate) {
            for (int i = 0; i < rate / 8; i++) {
                long word = 0;
                for (int b = 0; b < 8; b++) {
                    word |= (long) (msg[off + i * 8 + b] & 0xFF) << (8 * b);
                }
                state[i] ^= word;
            }
            keccakF(state);
        }
        byte[] out = new byte[32];
        for (int i = 0; i < 4; i++) {
            for (int b = 0; b < 8; b++) {
                out[i * 8 + b] = (byte) (state[i] >>> (8 * b));
            }
        }
        return out;
    }

    /** EIP-55 mixed-case checksum address from a 20-byte address. */
    static String eip55(byte[] addr20) {
        StringBuilder hexLower = new StringBuilder();
        for (byte b : addr20) hexLower.append(String.format("%02x", b));
        byte[] hashBytes = keccak256(hexLower.toString().getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        StringBuilder hashHex = new StringBuilder();
        for (byte b : hashBytes) hashHex.append(String.format("%02x", b));
        StringBuilder out = new StringBuilder("0x");
        String lower = hexLower.toString();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            int nibble = Character.digit(hashHex.charAt(i), 16);
            out.append(nibble >= 8 ? Character.toUpperCase(c) : c);
        }
        return out.toString();
    }

    static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte v : b) sb.append(String.format("%02x", v));
        return sb.toString();
    }
}
