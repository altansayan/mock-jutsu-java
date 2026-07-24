package io.github.altansayan.mockjutsu.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Full EC-derived wallets — mirrors wallet.py exactly.
 * ETH (secp256k1 → Keccak-256 → EIP-55), BTC (secp256k1 → HASH160 → Base58Check P2PKH + WIF),
 * Solana (Ed25519 → base58 address).
 */
public final class WalletGen {
    private WalletGen() {}
    private static final SecureRandom SEC = new SecureRandom();

    public static String generate(String type, String locale) {
        return switch (type) {
            case "eth_wallet" -> ethWallet();
            case "btc_wallet" -> btcWallet();
            case "sol_wallet" -> solWallet();
            default -> "ERROR: Unknown wallet type '" + type + "'";
        };
    }

    private static String ethWallet() {
        try {
            byte[] privKey = new byte[32];
            SEC.nextBytes(privKey);
            BigInteger k = new BigInteger(1, privKey).mod(EcCrypto.SECP_N);
            if (k.equals(BigInteger.ZERO)) k = BigInteger.ONE;
            EcCrypto.Point p = EcCrypto.secpMultiplyBase(k);
            byte[] pubkeyBytes = concat(to32(p.x()), to32(p.y()));
            byte[] kecc = EcCrypto.keccak256(pubkeyBytes);
            byte[] last20 = Arrays.copyOfRange(kecc, 12, 32);
            String address = EcCrypto.eip55(last20);
            return "{\"private_key\":\"" + bytesToHex(privKey) +
                   "\",\"public_key\":\"04" + bytesToHex(pubkeyBytes) +
                   "\",\"address\":\"" + address + "\"}";
        } catch (Exception e) {
            return "ERROR: eth_wallet generation failed: " + e.getMessage();
        }
    }

    private static String btcWallet() {
        try {
            byte[] privKey = new byte[32];
            SEC.nextBytes(privKey);
            BigInteger k = new BigInteger(1, privKey).mod(EcCrypto.SECP_N);
            if (k.equals(BigInteger.ZERO)) k = BigInteger.ONE;
            EcCrypto.Point p = EcCrypto.secpMultiplyBase(k);
            byte[] xb = to32(p.x());
            byte[] compressed = concat(new byte[]{(byte) (p.y().testBit(0) ? 0x03 : 0x02)}, xb);
            byte[] h160 = EcCrypto.hash160(compressed);
            String address = EcCrypto.base58Check(concat(new byte[]{0x00}, h160));
            String wif = EcCrypto.base58Check(concat(concat(new byte[]{(byte) 0x80}, privKey), new byte[]{0x01}));
            return "{\"private_key\":\"" + bytesToHex(privKey) +
                   "\",\"wif\":\"" + wif +
                   "\",\"public_key\":\"" + bytesToHex(compressed) +
                   "\",\"address\":\"" + address + "\"}";
        } catch (Exception e) {
            return "ERROR: btc_wallet generation failed: " + e.getMessage();
        }
    }

    private static String solWallet() {
        try {
            byte[] privKey = new byte[32];
            SEC.nextBytes(privKey);
            byte[] pubKey = EcCrypto.ed25519PublicKey(privKey);
            String address = EcCrypto.base58Encode(pubKey);
            String keypair = EcCrypto.base58Encode(concat(privKey, pubKey));
            return "{\"private_key\":\"" + bytesToHex(privKey) +
                   "\",\"public_key\":\"" + bytesToHex(pubKey) +
                   "\",\"address\":\"" + address +
                   "\",\"keypair\":\"" + keypair + "\"}";
        } catch (Exception e) {
            return "ERROR: sol_wallet generation failed: " + e.getMessage();
        }
    }

    private static byte[] to32(BigInteger v) {
        byte[] b = v.toByteArray();
        byte[] out = new byte[32];
        if (b.length >= 32) {
            System.arraycopy(b, b.length - 32, out, 0, 32);
        } else {
            System.arraycopy(b, 0, out, 32 - b.length, b.length);
        }
        return out;
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte v : b) sb.append(String.format("%02x", v));
        return sb.toString();
    }
}
