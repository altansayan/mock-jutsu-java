package io.github.altansayan.mockjutsu.enums;

/**
 * Cryptocurrency selector for {@code crypto_address}, {@code tx_hash}, and {@code block_hash}
 * type generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.CryptoCurrency.*;
 *
 * // BTC: Base58Check P2PKH address
 * String btcAddr = MockJutsu.generate(CRYPTO_ADDRESS, US, BTC);
 *
 * // ETH: 0x-prefixed hex address
 * String ethAddr = MockJutsu.generate(CRYPTO_ADDRESS, US, ETH);
 *
 * // Transaction hashes
 * String btcTx = MockJutsu.generate(TX_HASH, US, BTC);
 * String ethTx = MockJutsu.generate(TX_HASH, US, ETH);
 *
 * // Fluent builder
 * String addr = MockJutsu.cryptoAddress().currency(ETH).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum CryptoCurrency {

    /**
     * Bitcoin — generates Base58Check P2PKH address starting with {@code 1} or {@code 3}.
     * Tx/block hashes are 64 hex characters (256-bit SHA-256).
     */
    BTC("btc"),

    /**
     * Ethereum — generates {@code 0x}-prefixed 40-hex-character address (EIP-55 checksum).
     * Tx/block hashes are {@code 0x}-prefixed 64 hex characters (Keccak-256).
     */
    ETH("eth");

    private final String key;

    CryptoCurrency(String key) { this.key = key; }

    /**
     * Returns the currency key forwarded to the generator ({@code "btc"} or {@code "eth"}).
     *
     * @return the lowercase currency key
     */
    public String key() { return key; }

    /**
     * Parses a currency ticker string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #BTC} for {@code null} or unrecognised values.
     *
     * @param currency a currency ticker (e.g. {@code "btc"}, {@code "ETH"})
     * @return the matching constant, or {@link #BTC} as the default
     */
    public static CryptoCurrency of(String currency) {
        if (currency == null || currency.isBlank()) return BTC;
        for (CryptoCurrency c : values()) {
            if (c.key.equalsIgnoreCase(currency.trim())) return c;
        }
        return BTC;
    }
}
