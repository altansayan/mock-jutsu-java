package io.github.altansayan.mockjutsu.generators;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/** Crypto — BTC/ETH addresses, tx hash, mnemonic. Mirrors crypto.py. */
public final class CryptoGen {
    private CryptoGen() {}

    private static final SecureRandom SEC = new SecureRandom();

    private static final String[] DEFI_PROTOCOLS  = {
        "Uniswap","Aave","Compound","Curve Finance","MakerDAO","Lido",
        "Convex Finance","Balancer","dYdX","GMX","Synthetix","Yearn Finance",
        "1inch","Sushiswap","Pancakeswap","Velodrome","Camelot","Stargate",
        "Pendle","Morpho","Euler Finance","Radiant Capital","Beefy Finance"
    };
    private static final String[] BLOCKCHAIN_NETS = {
        "Ethereum","Polygon","Arbitrum","Optimism","Base","Avalanche",
        "BNB Chain","Solana","Bitcoin","Fantom","Cronos","zkSync Era"
    };
    private static final String[] WALLET_LABELS   = {
        "Hot Wallet","Cold Storage","Trading Wallet","DeFi Wallet",
        "Hardware Wallet","Custodial Wallet","Multi-sig Vault","Treasury",
        "Yield Farm","Staking Wallet","Airdrop Wallet","Development Wallet"
    };
    private static final String[] DEFI_POSITIONS  = {
        "Liquidity Provider","Lending","Borrowing","Staking",
        "Yield Farming","Perpetual","Options","Vaulted"
    };
    private static final String[] CRYPTO_NAMES    = {
        "Bitcoin","Ethereum","Tether","BNB","Solana","USDC","XRP",
        "Cardano","Avalanche","Polkadot","Dogecoin","Shiba Inu","Polygon",
        "Chainlink","Litecoin","Cosmos","Uniswap","Aave","Arbitrum","Optimism"
    };
    private static final int[] GAS_LIMITS = {21000, 45000, 65000, 100000, 150000, 200000, 300000, 500000, 1_000_000};

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        String q = qualifier.toLowerCase();
        return switch (type) {
            case "btc_address"          -> btcAddress(rng);
            case "eth_address"          -> ethAddress();
            case "crypto_address"       -> "eth".equals(q) ? ethAddress() : btcAddress(rng);
            case "tx_hash"              -> "eth".equals(q) ? "0x" + randomSecHex(32) : randomSecHex(32);
            case "block_hash"           -> "eth".equals(q) ? "0x" + randomSecHex(32) : randomSecHex(32);
            case "mnemonic"             -> {
                int words = 12;
                if (!qualifier.isEmpty()) {
                    try {
                        int n = Integer.parseInt(qualifier);
                        if (n == 12 || n == 15 || n == 18 || n == 21 || n == 24) words = n;
                    } catch (NumberFormatException ignored) {}
                }
                yield mnemonic(rng, words);
            }
            case "nft_token_id"         -> rng.nextDouble() < 0.6
                ? String.valueOf(rng.nextInt(0, 10000))
                : String.valueOf(rng.nextLong(10000L, 1_000_000_000_000_000_000L));
            case "gas_price"            -> {
                int tier = rng.nextInt(10);
                yield String.valueOf(tier <= 5 ? rng.nextInt(1, 31) : tier <= 8 ? rng.nextInt(30, 201) : rng.nextInt(200, 5001));
            }
            case "gas_limit"            -> String.valueOf(GAS_LIMITS[rng.nextInt(GAS_LIMITS.length)]);
            case "defi_protocol_name"   -> DEFI_PROTOCOLS[rng.nextInt(DEFI_PROTOCOLS.length)];
            case "blockchain_network"   -> BLOCKCHAIN_NETS[rng.nextInt(BLOCKCHAIN_NETS.length)];
            case "wallet_label"         -> WALLET_LABELS[rng.nextInt(WALLET_LABELS.length)];
            case "defi_position_type"   -> DEFI_POSITIONS[rng.nextInt(DEFI_POSITIONS.length)];
            case "cryptocurrency_name"  -> CRYPTO_NAMES[rng.nextInt(CRYPTO_NAMES.length)];
            case "liquidity_pool_id"    -> ethAddress();
            case "liquidity_pool_id_masked" -> {
                String hexLower = randomSecHex(20);
                yield "0x" + hexLower.substring(0, 4) + "..." + hexLower.substring(hexLower.length() - 4);
            }
            default -> "ERROR: Unknown crypto type '" + type + "'";
        };
    }

    // ── BTC P2PKH address — version(0x00) + 20-byte hash + SHA256d checksum → Base58Check ──

    static String btcAddress(ThreadLocalRandom rng) {
        byte[] pubkeyHash = new byte[20];
        SEC.nextBytes(pubkeyHash);
        try {
            byte[] versioned = new byte[21];
            versioned[0] = 0x00;
            System.arraycopy(pubkeyHash, 0, versioned, 1, 20);
            return EcCrypto.base58Check(versioned);
        } catch (Exception e) {
            throw new IllegalStateException("btc_address generation failed", e);
        }
    }

    // ── ETH address — 0x + 40 hex, real EIP-55 mixed-case Keccak-256 checksum ──

    static String ethAddress() {
        byte[] raw = new byte[20];
        SEC.nextBytes(raw);
        return EcCrypto.eip55(raw);
    }

    // ── BIP-39 mnemonic — entropy → SHA-256 checksum → 11-bit wordlist mapping ──

    private static String mnemonic(ThreadLocalRandom rng, int wordCount) {
        try {
            int entBits = switch (wordCount) {
                case 15 -> 160; case 18 -> 192; case 21 -> 224; case 24 -> 256; default -> 128;
            };
            byte[] entropy = new byte[entBits / 8];
            SEC.nextBytes(entropy);
            byte[] hash = EcCrypto.sha256(entropy);
            int csLen = entBits / 32;

            StringBuilder bits = new StringBuilder();
            for (byte b : entropy) appendByteBits(bits, b);
            StringBuilder hashBits = new StringBuilder();
            for (byte b : hash) appendByteBits(hashBits, b);
            bits.append(hashBits, 0, csLen);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bits.length(); i += 11) {
                int idx = Integer.parseInt(bits.substring(i, i + 11), 2);
                if (i > 0) sb.append(' ');
                sb.append(Bip39Wordlist.WORDS[idx]);
            }
            return sb.toString();
        } catch (Exception e) {
            return "ERROR: mnemonic generation failed: " + e.getMessage();
        }
    }

    private static void appendByteBits(StringBuilder sb, byte b) {
        for (int i = 7; i >= 0; i--) sb.append((b >> i) & 1);
    }

    private static String randomSecHex(int bytes) {
        byte[] b = new byte[bytes];
        SEC.nextBytes(b);
        StringBuilder sb = new StringBuilder(bytes * 2);
        for (byte v : b) sb.append(String.format("%02x", v));
        return sb.toString();
    }
}
