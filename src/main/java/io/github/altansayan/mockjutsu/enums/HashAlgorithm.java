package io.github.altansayan.mockjutsu.enums;

/**
 * Hash algorithm selector for {@code hash} type generation.
 *
 * <p>Usage:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.HashAlgorithm.*;
 *
 * String h256 = MockJutsu.generate(HASH, TR, SHA256);
 * String h512 = MockJutsu.generate(HASH, US, SHA512);
 * String crc  = MockJutsu.generate(HASH, US, CRC32);
 *
 * // Fluent builder
 * String md5  = MockJutsu.hash().algorithm(MD5).generate();
 * }</pre>
 *
 * @since 1.0.0
 */
public enum HashAlgorithm {

    /** MD5 — 32 hex characters (128-bit digest). */
    MD5("md5"),

    /** SHA-1 — 40 hex characters (160-bit digest). */
    SHA1("sha1"),

    /** SHA-224 — 56 hex characters (224-bit digest). */
    SHA224("sha224"),

    /** SHA-256 — 64 hex characters (256-bit digest). Default algorithm. */
    SHA256("sha256"),

    /** SHA-384 — 96 hex characters (384-bit digest). */
    SHA384("sha384"),

    /** SHA-512 — 128 hex characters (512-bit digest). */
    SHA512("sha512"),

    /** SHA3-224 — 56 hex characters (224-bit Keccak digest). */
    SHA3_224("sha3-224"),

    /** SHA3-256 — 64 hex characters (256-bit Keccak digest). */
    SHA3_256("sha3-256"),

    /** SHA3-384 — 96 hex characters (384-bit Keccak digest). */
    SHA3_384("sha3-384"),

    /** SHA3-512 — 128 hex characters (512-bit Keccak digest). */
    SHA3_512("sha3-512"),

    /** CRC-32 — 8 hex characters (32-bit cyclic redundancy check). */
    CRC32("crc32"),

    /** Adler-32 — 8 hex characters (32-bit Adler checksum). */
    ADLER32("adler32"),

    /** CRC-16 — 4 hex characters (16-bit cyclic redundancy check). */
    CRC16("crc16");

    private final String key;

    HashAlgorithm(String key) { this.key = key; }

    /**
     * Returns the algorithm key forwarded to the generator (e.g. {@code "sha256"}, {@code "md5"}).
     *
     * @return the lowercase algorithm key
     */
    public String key() { return key; }

    /**
     * Parses an algorithm name string to the corresponding enum constant, case-insensitive.
     * Falls back to {@link #SHA256} for {@code null} or unrecognised values.
     *
     * @param algorithm an algorithm name (e.g. {@code "sha256"}, {@code "SHA-256"}, {@code "md5"})
     * @return the matching constant, or {@link #SHA256} as the default
     */
    public static HashAlgorithm of(String algorithm) {
        if (algorithm == null || algorithm.isBlank()) return SHA256;
        String normalized = algorithm.trim().toLowerCase().replace("-", "");
        for (HashAlgorithm h : values()) {
            if (h.key.replace("-", "").equals(normalized) || h.name().toLowerCase().equals(normalized))
                return h;
        }
        return SHA256;
    }
}
