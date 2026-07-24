package io.github.altansayan.mockjutsu;

import io.github.altansayan.mockjutsu.enums.Carrier;
import io.github.altansayan.mockjutsu.enums.ColorFormat;
import io.github.altansayan.mockjutsu.enums.CryptoCurrency;
import io.github.altansayan.mockjutsu.enums.DataType;
import io.github.altansayan.mockjutsu.enums.Gender;
import io.github.altansayan.mockjutsu.enums.HashAlgorithm;
import io.github.altansayan.mockjutsu.enums.MockJutsuLocale;
import io.github.altansayan.mockjutsu.enums.Network;
import io.github.altansayan.mockjutsu.masker.Masker;

import java.util.ArrayList;
import java.util.List;

/**
 * MockJutsu — zero-dependency Java library for realistic mock data generation.
 *
 * <p>Supports 390+ data types across identity, financial, telecom, health, IoT,
 * cryptography, compliance, and more. Two APIs are available:
 *
 * <ul>
 *   <li><b>String API</b> — dynamic/scriptable, suitable for templates and scripts.</li>
 *   <li><b>Fluent Builder API</b> — type-safe with full IDE autocomplete.</li>
 * </ul>
 *
 * <h2>String API</h2>
 * <pre>{@code
 * String tckn  = MockJutsu.generate("tckn",  "TR");
 * String iban  = MockJutsu.generate("iban",  "DE");
 * String card  = MockJutsu.generate("cardnum", "TR", "visa");
 * List<String> bulk = MockJutsu.bulk("cardnum", "TR", 100);
 * }</pre>
 *
 * <h2>Fluent Builder API</h2>
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.Network.*;
 *
 * String card  = MockJutsu.cardnum().locale(TR).network(VISA).generate();
 * String iban  = MockJutsu.iban().country(DE).generate();
 * List<String> cards = MockJutsu.cardnum().locale(TR).network(MC).bulk(10);
 * }</pre>
 *
 * <h2>Masker</h2>
 * <pre>{@code
 * MockJutsu.mask("cardnum",   "4532015112830366"); // → "4532 01****** 0366"  (PCI DSS)
 * MockJutsu.mask("tckn",     "12345678901");       // → "12*******01"         (KVKK)
 * MockJutsu.mask("email",    "john@example.com");  // → "jo***@example.com"   (GDPR)
 * MockJutsu.mask("iban",     "TR330006100519786"); // → "TR33 **** ... 6"     (SEPA/PSD2)
 * }</pre>
 *
 * @author Altan Sezer Ayan
 * @since 1.0.0
 * @see <a href="https://github.com/altansayan/mock-jutsu-java">GitHub Repository</a>
 */
public final class MockJutsu {

    private MockJutsu() {}

    // ── String API ────────────────────────────────────────────────────────────

    /**
     * Generates a single mock value for the given type and locale.
     *
     * <p>Returns {@code "ERROR: Unknown DataType 'xyz'"} if the type is not recognised.
     *
     * @param type   the data type key (e.g. {@code "tckn"}, {@code "cardnum"}, {@code "iban"})
     * @param locale the locale code (e.g. {@code "TR"}, {@code "US"}, {@code "DE"});
     *               defaults to {@code "US"} when {@code null}
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(String type, String locale) {
        return Registry.generate(type, locale == null ? "US" : locale);
    }

    /**
     * Generates a single mock value with an optional qualifier.
     *
     * <p>The qualifier meaning depends on the type:
     * <ul>
     *   <li>{@code "cardnum"} — network key: {@code "visa"}, {@code "mc"}, {@code "amex"}, {@code "troy"} …</li>
     *   <li>{@code "fullname"} — gender: {@code "M"} or {@code "F"}</li>
     *   <li>{@code "mnemonic"} — word count: {@code "12"}, {@code "24"} …</li>
     * </ul>
     *
     * @param type      the data type key
     * @param locale    the locale code; defaults to {@code "US"} when {@code null}
     * @param qualifier optional qualifier; ignored when empty or {@code null}
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(String type, String locale, String qualifier) {
        return Registry.generate(type, locale == null ? "US" : locale, qualifier == null ? "" : qualifier);
    }

    /**
     * Generates a single mock value using a typed locale enum.
     *
     * @param type   the data type key
     * @param locale the locale enum constant; defaults to {@code US} when {@code null}
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(String type, MockJutsuLocale locale) {
        return Registry.generate(type, locale == null ? "US" : locale.code());
    }

    /**
     * Generates a list of {@code count} mock values for the given type and locale.
     *
     * @param type   the data type key
     * @param locale the locale code; defaults to {@code "US"} when {@code null}
     * @param count  the number of values to generate; must be &gt;= 0
     * @return a mutable list of generated strings, never {@code null}
     * @since 1.0.0
     */
    public static List<String> bulk(String type, String locale, int count) {
        List<String> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) results.add(generate(type, locale));
        return results;
    }

    // ── DataType enum overloads ───────────────────────────────────────────────

    /**
     * Generates a single mock value using a typed {@link DataType} enum constant.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     *
     * String tckn = MockJutsu.generate(TCKN, TR);
     * String iban = MockJutsu.generate(IBAN, DE);
     * }</pre>
     *
     * @param type   the data type enum constant
     * @param locale the locale code string; defaults to {@code "US"} when {@code null}
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(DataType type, String locale) {
        return Registry.generate(type.key(), locale == null ? "US" : locale);
    }

    /**
     * Generates a single mock value using typed {@link DataType} and {@link MockJutsuLocale} enums.
     *
     * <pre>{@code
     * String tckn = MockJutsu.generate(DataType.TCKN, MockJutsuLocale.TR);
     * }</pre>
     *
     * @param type   the data type enum constant
     * @param locale the locale enum constant; defaults to {@code US} when {@code null}
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(DataType type, MockJutsuLocale locale) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code());
    }

    /**
     * Generates a single mock value using a typed {@link DataType} enum with a string qualifier.
     *
     * <p>Each qualifier-accepting type uses a different format:
     * <ul>
     *   <li>{@link DataType#CARDNUM} — network key: use {@link #generate(DataType, MockJutsuLocale, Network)} instead</li>
     *   <li>{@link DataType#FULLNAME} — gender: use {@link #generate(DataType, MockJutsuLocale, Gender)} instead</li>
     *   <li>{@link DataType#AGE} — range: use {@link #age()} builder instead</li>
     *   <li>{@link DataType#BALANCE} — range: {@code "100|5000"} (min|max)</li>
     *   <li>{@link DataType#MNEMONIC} — word count: {@code "12"}, {@code "15"}, {@code "18"}, {@code "21"}, {@code "24"}; use {@link #mnemonic()} builder instead</li>
     *   <li>{@link DataType#DATE_BETWEEN} — date range: {@code "2020-01-01|2025-12-31"}</li>
     * </ul>
     *
     * @param type      the data type enum constant
     * @param locale    the locale enum constant; defaults to {@code US} when {@code null}
     * @param qualifier optional qualifier string; see per-type documentation above
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(DataType type, MockJutsuLocale locale, String qualifier) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(), qualifier == null ? "" : qualifier);
    }

    /**
     * Generates a card number using a typed {@link Network} enum — fully type-safe, no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.Network.*;
     *
     * String visa = MockJutsu.generate(CARDNUM, TR, VISA);
     * String amex = MockJutsu.generate(CARDNUM, US, AMEX);
     * String troy = MockJutsu.generate(CARDNUM, TR, TROY);
     * }</pre>
     *
     * <p><b>Available locales</b> ({@link MockJutsuLocale}):
     * {@code TR} · {@code DE} · {@code US} · {@code GB} · {@code FR} · {@code RU}
     * · {@code JP} · {@code CN} · {@code IN} · {@code BR} · {@code ES} · {@code IT}
     * · {@code MX} · {@code AU} · {@code CA} · {@code PL} · {@code NL} · …
     *
     * <p><b>Available networks</b> ({@link Network}):
     * {@code VISA} · {@code MC} · {@code AMEX} · {@code TROY} · {@code MIR}
     * · {@code JCB} · {@code DISCOVER} · {@code UNIONPAY} · {@code MAESTRO}
     *
     * <p><b>Tip (VS Code):</b> Press {@code Ctrl+Space} after the comma to see
     * all available enum values via autocomplete. Press {@code F12} on {@code TR}
     * or {@code VISA} to jump to the full enum definition.
     *
     * @param type    the data type enum constant; only {@link DataType#CARDNUM} uses the network
     * @param locale  the locale — see {@link MockJutsuLocale} for all supported countries
     * @param network the card network — see {@link Network} for all supported schemes;
     *                {@code null} uses the locale default
     * @return a non-null Luhn-valid card number string
     * @since 1.0.0
     * @see Network
     * @see MockJutsuLocale
     * @see CardnumBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, Network network) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                network == null ? "" : network.key());
    }

    /**
     * Generates a full name using a typed {@link Gender} enum — fully type-safe, no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.Gender.*;
     *
     * String male   = MockJutsu.generate(FULLNAME, TR, MALE);
     * String female = MockJutsu.generate(FULLNAME, DE, FEMALE);
     * }</pre>
     *
     * @param type   the data type enum constant; only {@link DataType#FULLNAME}, {@link DataType#FIRSTNAME},
     *               {@link DataType#LASTNAME} use the gender qualifier
     * @param locale the locale enum constant
     * @param gender the gender; {@link Gender#RANDOM} picks randomly
     * @return a non-null generated name string
     * @since 1.0.0
     * @see FullnameBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, Gender gender) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                gender == null ? "" : gender.key());
    }

    /**
     * Generates a hash using a typed {@link HashAlgorithm} enum — no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.HashAlgorithm.*;
     *
     * String h256 = MockJutsu.generate(HASH, US, SHA256);
     * String md5  = MockJutsu.generate(HASH, US, MD5);
     * }</pre>
     *
     * @param type      the data type enum constant; only {@link DataType#HASH} uses the algorithm
     * @param locale    the locale enum constant
     * @param algorithm the hash algorithm; {@code null} defaults to {@link HashAlgorithm#SHA256}
     * @return a non-null hex-encoded hash string
     * @since 1.0.0
     * @see HashBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, HashAlgorithm algorithm) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                algorithm == null ? "" : algorithm.key());
    }

    /**
     * Generates a color value using a typed {@link ColorFormat} enum — no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.ColorFormat.*;
     *
     * String hex = MockJutsu.generate(COLOR, US, HEX);
     * String rgb = MockJutsu.generate(COLOR, US, RGB);
     * }</pre>
     *
     * @param type   the data type enum constant; only {@link DataType#COLOR} uses the format
     * @param locale the locale enum constant
     * @param format the color format; {@code null} defaults to {@link ColorFormat#HEX}
     * @return a non-null CSS color string
     * @since 1.0.0
     * @see ColorBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, ColorFormat format) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                format == null ? "" : format.key());
    }

    /**
     * Generates a tracking number using a typed {@link Carrier} enum — no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.Carrier.*;
     *
     * String usps  = MockJutsu.generate(TRACKING_NUMBER, US, USPS);
     * String fedex = MockJutsu.generate(TRACKING_NUMBER, US, FEDEX);
     * }</pre>
     *
     * @param type    the data type enum constant; only {@link DataType#TRACKING_NUMBER} uses the carrier
     * @param locale  the locale enum constant
     * @param carrier the shipping carrier; {@code null} defaults to {@link Carrier#USPS}
     * @return a non-null tracking number string in carrier-specific format
     * @since 1.0.0
     * @see TrackingNumberBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, Carrier carrier) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                carrier == null ? "" : carrier.key());
    }

    /**
     * Generates a crypto address/hash using a typed {@link CryptoCurrency} enum — no magic strings.
     *
     * <pre>{@code
     * import static io.github.altansayan.mockjutsu.enums.DataType.*;
     * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
     * import static io.github.altansayan.mockjutsu.enums.CryptoCurrency.*;
     *
     * String btcAddr = MockJutsu.generate(CRYPTO_ADDRESS, US, BTC);
     * String ethAddr = MockJutsu.generate(CRYPTO_ADDRESS, US, ETH);
     * String ethTx   = MockJutsu.generate(TX_HASH, US, ETH);
     * }</pre>
     *
     * @param type     the data type enum constant; {@link DataType#CRYPTO_ADDRESS},
     *                 {@link DataType#TX_HASH}, or {@link DataType#BLOCK_HASH}
     * @param locale   the locale enum constant
     * @param currency the cryptocurrency; {@code null} defaults to {@link CryptoCurrency#BTC}
     * @return a non-null address or hash string in the currency-specific format
     * @since 1.0.0
     * @see CryptoAddressBuilder
     */
    public static String generate(DataType type, MockJutsuLocale locale, CryptoCurrency currency) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(),
                currency == null ? "" : currency.key());
    }

    /**
     * Generates a list of mock values using a typed {@link DataType} enum constant.
     *
     * <pre>{@code
     * List<String> ibans = MockJutsu.bulk(DataType.IBAN, MockJutsuLocale.TR, 50);
     * }</pre>
     *
     * @param type   the data type enum constant
     * @param locale the locale enum constant; defaults to {@code US} when {@code null}
     * @param count  the number of values to generate
     * @return a mutable list of generated strings, never {@code null}
     * @since 1.0.0
     */
    public static List<String> bulk(DataType type, MockJutsuLocale locale, int count) {
        List<String> results = new ArrayList<>(count);
        String loc = locale == null ? "US" : locale.code();
        for (int i = 0; i < count; i++) results.add(Registry.generate(type.key(), loc));
        return results;
    }

    /**
     * Returns a regulation-compliant masked value using a typed {@link DataType} enum constant.
     *
     * <pre>{@code
     * String masked = MockJutsu.mask(DataType.CARDNUM, "4532015112830366");
     * }</pre>
     *
     * @param type  the data type enum constant
     * @param value the raw value to mask
     * @return the masked value, or the original value if no masking rule exists
     * @since 1.0.0
     */
    public static String mask(DataType type, String value) {
        return Masker.mask(type.key(), value);
    }

    // ── Masker ────────────────────────────────────────────────────────────────

    /**
     * Returns a regulation-compliant masked version of the given value.
     *
     * <p>Masking rules by regulation:
     * <ul>
     *   <li><b>PCI DSS v4.0 §3.4.1</b> — {@code cardnum}: first 6 + last 4 visible</li>
     *   <li><b>ISO/IEC 7812:2017</b> — {@code cardnum_bin8}: first 8 + last 4 visible</li>
     *   <li><b>KVKK / GDPR Art.5</b> — {@code tckn}, {@code email}, {@code phone}, {@code fullname}</li>
     *   <li><b>SEPA / PSD2</b> — {@code iban}: country + check + last 4 visible</li>
     *   <li><b>HIPAA</b> — {@code npi}, {@code icd10}, {@code nhs_number}</li>
     *   <li><b>E.164 / 3GPP</b> — {@code phone}, {@code msisdn}, {@code imei}</li>
     * </ul>
     *
     * <p>Types with no masking rule are returned unchanged.
     *
     * @param type  the data type key (e.g. {@code "cardnum"}, {@code "iban"}, {@code "email"})
     * @param value the raw value to mask; {@code null} returns {@code null}
     * @return the masked value, or the original value if no masking rule exists for the type
     * @since 1.0.0
     */
    public static String mask(String type, String value) {
        return Masker.mask(type, value);
    }

    // ── Fluent Builder entry points ───────────────────────────────────────────

    /**
     * Returns a fluent builder for card number generation.
     *
     * <pre>{@code
     * String card = MockJutsu.cardnum()
     *     .locale(MockJutsuLocale.TR)
     *     .network(Network.VISA)
     *     .generate();
     * }</pre>
     *
     * @return a new {@link CardnumBuilder}
     * @since 1.0.0
     */
    public static CardnumBuilder cardnum() { return new CardnumBuilder(); }

    /**
     * Returns a fluent builder for IBAN generation.
     *
     * <pre>{@code
     * String iban = MockJutsu.iban().country("DE").generate();
     * }</pre>
     *
     * @return a new {@link IbanBuilder}
     * @since 1.0.0
     */
    public static IbanBuilder iban() { return new IbanBuilder(); }

    /**
     * Returns a fluent builder for Turkish National ID (TCKN) generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "tckn"}
     * @since 1.0.0
     */
    public static SimpleBuilder tckn() { return new SimpleBuilder("tckn"); }

    /**
     * Returns a fluent builder for US Social Security Number (SSN) generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "ssn"}
     * @since 1.0.0
     */
    public static SimpleBuilder ssn() { return new SimpleBuilder("ssn"); }

    /**
     * Returns a fluent builder for full name generation.
     *
     * <pre>{@code
     * String name = MockJutsu.fullname().locale("TR").gender("F").generate();
     * }</pre>
     *
     * @return a new {@link FullnameBuilder}
     * @since 1.0.0
     */
    public static FullnameBuilder fullname() { return new FullnameBuilder(); }

    /**
     * Returns a fluent builder for email address generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "email"}
     * @since 1.0.0
     */
    public static SimpleBuilder email() { return new SimpleBuilder("email"); }

    /**
     * Returns a fluent builder for phone number generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "phone"}
     * @since 1.0.0
     */
    public static SimpleBuilder phone() { return new SimpleBuilder("phone"); }

    /**
     * Returns a fluent builder for UUID generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "uuid"}
     * @since 1.0.0
     */
    public static SimpleBuilder uuid() { return new SimpleBuilder("uuid"); }

    /**
     * Returns a fluent builder for JWT token generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "jwt"}
     * @since 1.0.0
     */
    public static SimpleBuilder jwt() { return new SimpleBuilder("jwt"); }

    /**
     * Returns a fluent builder for IMEI (International Mobile Equipment Identity) generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "imei"}
     * @since 1.0.0
     */
    public static SimpleBuilder imei() { return new SimpleBuilder("imei"); }

    /**
     * Returns a fluent builder for SWIFT/BIC code generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "swift"}
     * @since 1.0.0
     */
    public static SimpleBuilder swift() { return new SimpleBuilder("swift"); }

    /**
     * Returns a fluent builder for EAN-13 barcode generation.
     *
     * @return a new {@link SimpleBuilder} for type {@code "ean13"}
     * @since 1.0.0
     */
    public static SimpleBuilder ean13() { return new SimpleBuilder("ean13"); }

    /**
     * Returns a fluent builder for age generation with optional min/max range.
     *
     * <pre>{@code
     * // Random age 18–80 (default)
     * String age = MockJutsu.age().locale("TR").generate();
     *
     * // Constrained range
     * String adult = MockJutsu.age().min(18).max(35).generate();
     * String senior = MockJutsu.age().min(65).max(90).generate();
     *
     * // Bulk
     * List<String> ages = MockJutsu.age().min(20).max(40).bulk(100);
     * }</pre>
     *
     * @return a new {@link AgeBuilder}
     * @since 1.0.0
     */
    public static AgeBuilder age() { return new AgeBuilder(); }

    /**
     * Returns a fluent builder for BIP-39 mnemonic phrase generation.
     *
     * <pre>{@code
     * // Default 12 words
     * String phrase = MockJutsu.mnemonic().generate();
     *
     * // 24-word seed phrase
     * String phrase24 = MockJutsu.mnemonic().words(24).generate();
     * }</pre>
     *
     * @return a new {@link MnemonicBuilder}
     * @since 1.0.0
     */
    public static MnemonicBuilder mnemonic() { return new MnemonicBuilder(); }

    /**
     * Returns a fluent builder for account balance generation with optional min/max range.
     *
     * <pre>{@code
     * // Random balance (default range)
     * String balance = MockJutsu.balance().locale("TR").generate();
     *
     * // Constrained range
     * String small = MockJutsu.balance().min(100).max(5000).generate();
     * }</pre>
     *
     * @return a new {@link BalanceBuilder}
     * @since 1.0.0
     */
    public static BalanceBuilder balance() { return new BalanceBuilder(); }

    /**
     * Returns a fluent builder for date-between generation with optional date range.
     *
     * <pre>{@code
     * String date = MockJutsu.dateRange()
     *     .from("2020-01-01")
     *     .to("2025-12-31")
     *     .generate();
     * }</pre>
     *
     * @return a new {@link DateRangeBuilder}
     * @since 1.0.0
     */
    public static DateRangeBuilder dateRange() { return new DateRangeBuilder(); }

    /**
     * Returns a fluent builder for AI embedding vector generation with optional dimension count.
     *
     * <pre>{@code
     * String emb = MockJutsu.aiEmbedding().dims(768).generate();
     * }</pre>
     *
     * @return a new {@link AiVectorBuilder} for {@code "ai_embedding"}
     * @since 1.0.0
     */
    public static AiVectorBuilder aiEmbedding() { return new AiVectorBuilder("ai_embedding"); }

    /**
     * Returns a fluent builder for dense AI vector generation with optional dimension count.
     *
     * <pre>{@code
     * String vec = MockJutsu.aiVector().dims(1536).generate();
     * }</pre>
     *
     * @return a new {@link AiVectorBuilder} for {@code "ai_vector"}
     * @since 1.0.0
     */
    public static AiVectorBuilder aiVector() { return new AiVectorBuilder("ai_vector"); }

    /**
     * Returns a fluent builder for sparse AI vector generation with optional dims and nnz count.
     *
     * <pre>{@code
     * String sparse = MockJutsu.aiSparseVector().dims(10000).nnz(64).generate();
     * }</pre>
     *
     * @return a new {@link AiSparseVectorBuilder}
     * @since 1.0.0
     */
    public static AiSparseVectorBuilder aiSparseVector() { return new AiSparseVectorBuilder(); }

    /**
     * Returns a fluent builder for reverse-regex string generation.
     *
     * <pre>{@code
     * // Generate a string matching [A-Z]{3}-\d{4}
     * String s = MockJutsu.reverseRegex().pattern("[A-Z]{3}-\\d{4}").generate();
     * }</pre>
     *
     * @return a new {@link PatternBuilder}
     * @since 1.0.0
     */
    public static PatternBuilder reverseRegex() { return new PatternBuilder(); }

    /**
     * Returns a fluent builder for HMAC signature generation with optional secret and payload.
     *
     * <pre>{@code
     * String sig = MockJutsu.signature()
     *     .secret("my-secret-key")
     *     .payload("{\"event\":\"payment\"}")
     *     .generate();
     * }</pre>
     *
     * @return a new {@link SignatureBuilder}
     * @since 1.0.0
     */
    public static SignatureBuilder signature() { return new SignatureBuilder(); }

    /**
     * Returns a fluent builder for SWIFT MT103 payment message generation.
     * Use {@link StrictPaymentBuilder#strict()} to enable ISO 9362 strict BIC mode.
     *
     * <pre>{@code
     * String mt103 = MockJutsu.swiftMt103().locale(TR).generate();
     * String mt103strict = MockJutsu.swiftMt103().locale(TR).strict().generate();
     * }</pre>
     *
     * @return a new {@link StrictPaymentBuilder} for {@code "swift_mt103"}
     * @since 1.0.0
     */
    public static StrictPaymentBuilder swiftMt103() { return new StrictPaymentBuilder("swift_mt103"); }

    /**
     * Returns a fluent builder for ISO 20022 pain.001 payment message generation.
     * Use {@link StrictPaymentBuilder#strict()} to enable ISO 9362 strict BIC mode.
     *
     * <pre>{@code
     * String pain = MockJutsu.pain001().strict().generate();
     * }</pre>
     *
     * @return a new {@link StrictPaymentBuilder} for {@code "pain001"}
     * @since 1.0.0
     */
    public static StrictPaymentBuilder pain001() { return new StrictPaymentBuilder("pain001"); }

    /**
     * Returns a fluent builder for SEPA Direct Debit mandate generation.
     * Use {@link StrictPaymentBuilder#strict()} to enable ISO 9362 strict BIC mode.
     *
     * <pre>{@code
     * String mandate = MockJutsu.sepaMandate().strict().generate();
     * }</pre>
     *
     * @return a new {@link StrictPaymentBuilder} for {@code "sepa_mandate"}
     * @since 1.0.0
     */
    public static StrictPaymentBuilder sepaMandate() { return new StrictPaymentBuilder("sepa_mandate"); }

    /**
     * Returns a fluent builder for forex exchange rate generation with optional pair.
     *
     * <pre>{@code
     * String rate = MockJutsu.forexRate().pair("EUR/USD").generate();
     * String any  = MockJutsu.forexRate().generate(); // random pair
     * }</pre>
     *
     * @return a new {@link ForexRateBuilder}
     * @since 1.0.0
     */
    public static ForexRateBuilder forexRate() { return new ForexRateBuilder(); }

    /**
     * Returns a fluent builder for PSD2 consent object generation with optional amount.
     *
     * <pre>{@code
     * String consent = MockJutsu.psd2Consent().amount(1234.56).locale("DE").generate();
     * }</pre>
     *
     * @return a new {@link Psd2ConsentBuilder}
     * @since 1.0.0
     */
    public static Psd2ConsentBuilder psd2Consent() { return new Psd2ConsentBuilder(); }

    /**
     * Returns a fluent builder for hash generation with a typed algorithm selector.
     *
     * <pre>{@code
     * String h = MockJutsu.hash().algorithm(HashAlgorithm.SHA256).generate();
     * }</pre>
     *
     * @return a new {@link HashBuilder}
     * @since 1.0.0
     */
    public static HashBuilder hash() { return new HashBuilder(); }

    /**
     * Returns a fluent builder for color generation with a typed format selector.
     *
     * <pre>{@code
     * String c = MockJutsu.color().format(ColorFormat.HSL).generate();
     * }</pre>
     *
     * @return a new {@link ColorBuilder}
     * @since 1.0.0
     */
    public static ColorBuilder color() { return new ColorBuilder(); }

    /**
     * Returns a fluent builder for tracking number generation with a typed carrier selector.
     *
     * <pre>{@code
     * String t = MockJutsu.trackingNumber().carrier(Carrier.FEDEX).generate();
     * }</pre>
     *
     * @return a new {@link TrackingNumberBuilder}
     * @since 1.0.0
     */
    public static TrackingNumberBuilder trackingNumber() { return new TrackingNumberBuilder(); }

    /**
     * Returns a fluent builder for crypto address/hash generation with a typed currency selector.
     *
     * <pre>{@code
     * String addr = MockJutsu.cryptoAddress().currency(CryptoCurrency.ETH).generate();
     * }</pre>
     *
     * @return a new {@link CryptoAddressBuilder}
     * @since 1.0.0
     */
    public static CryptoAddressBuilder cryptoAddress() { return new CryptoAddressBuilder(); }

    // ── Builder classes ───────────────────────────────────────────────────────

    /**
     * Base class for all fluent builders — provides {@link #locale(String)},
     * {@link #generate()}, and {@link #bulk(int)}.
     *
     * @param <T> the concrete builder type (for fluent chaining)
     * @since 1.0.0
     */
    public abstract static class BaseBuilder<T extends BaseBuilder<T>> {
        /** Current locale; defaults to {@code "US"}. */
        protected String locale = "US";

        /**
         * Returns the data type key dispatched to {@link Registry}.
         *
         * @return type key string
         */
        protected abstract String typeName();

        /**
         * Sets the locale for generation.
         *
         * @param locale locale code (e.g. {@code "TR"}, {@code "DE"}); defaults to {@code "US"} when {@code null}
         * @return this builder
         */
        @SuppressWarnings("unchecked")
        public T locale(String locale) {
            this.locale = locale == null ? "US" : locale.toUpperCase();
            return (T) this;
        }

        /**
         * Sets the locale using a typed enum constant.
         *
         * @param locale the locale enum; defaults to {@code US} when {@code null}
         * @return this builder
         */
        @SuppressWarnings("unchecked")
        public T locale(MockJutsuLocale locale) {
            this.locale = locale == null ? "US" : locale.code();
            return (T) this;
        }

        /**
         * Generates a single value.
         *
         * @return a non-null generated string
         */
        public String generate() { return Registry.generate(typeName(), locale, qualifier()); }

        /**
         * Generates a list of {@code count} values.
         *
         * @param count number of values to generate; must be &gt;= 0
         * @return a mutable list of generated strings
         */
        public List<String> bulk(int count) {
            List<String> out = new ArrayList<>(count);
            for (int i = 0; i < count; i++) out.add(generate());
            return out;
        }

        /**
         * Returns the optional qualifier forwarded to the generator (e.g. network, gender).
         * Subclasses override this to supply type-specific qualifiers.
         *
         * @return qualifier string, empty by default
         */
        protected String qualifier() { return ""; }
    }

    /**
     * Generic builder for data types that only require a locale.
     * Used by {@link MockJutsu#tckn()}, {@link MockJutsu#ssn()}, {@link MockJutsu#email()} etc.
     *
     * @since 1.0.0
     */
    public static final class SimpleBuilder extends BaseBuilder<SimpleBuilder> {
        private final String type;

        SimpleBuilder(String type) { this.type = type; }

        @Override
        protected String typeName() { return type; }
    }

    /**
     * Fluent builder for card number ({@code cardnum}) generation.
     * Supports network selection and 8-digit BIN masking.
     *
     * <pre>{@code
     * String card = MockJutsu.cardnum()
     *     .locale("TR")
     *     .network(Network.AMEX)
     *     .generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class CardnumBuilder extends BaseBuilder<CardnumBuilder> {
        private String network = "";
        private boolean bin8 = false;

        @Override
        protected String typeName() { return "cardnum"; }

        /**
         * Sets the card network using a typed enum.
         *
         * @param network the card network; {@code null} uses the locale default
         * @return this builder
         */
        public CardnumBuilder network(Network network) {
            this.network = network == null ? "" : network.key();
            return this;
        }

        /**
         * Sets the card network using a string key.
         *
         * @param network network key: {@code "visa"}, {@code "mc"}, {@code "amex"},
         *                {@code "troy"}, {@code "mir"}, {@code "jcb"}, {@code "discover"},
         *                {@code "unionpay"}, {@code "maestro"}
         * @return this builder
         */
        public CardnumBuilder network(String network) {
            this.network = network == null ? "" : network;
            return this;
        }

        /**
         * Applies 8-digit BIN masking per ISO/IEC 7812:2017 to the generated card number.
         * First 8 digits + last 4 digits are visible; middle digits are masked with {@code *}.
         *
         * @return this builder
         */
        public CardnumBuilder bin8() { this.bin8 = true; return this; }

        @Override
        protected String qualifier() { return network; }

        @Override
        public String generate() {
            String card = Registry.generate(typeName(), locale, network);
            return bin8 ? Masker.mask("cardnum_bin8", card) : card;
        }
    }

    /**
     * Fluent builder for IBAN generation.
     * Provides a {@link #country(String)} alias for {@link #locale(String)} for natural readability.
     *
     * <pre>{@code
     * String iban = MockJutsu.iban().country("DE").generate();
     * List<String> ibans = MockJutsu.iban().country(MockJutsuLocale.TR).bulk(50);
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class IbanBuilder extends BaseBuilder<IbanBuilder> {
        @Override
        protected String typeName() { return "iban"; }

        /**
         * Sets the country (locale) for IBAN generation.
         * Alias for {@link #locale(String)} with a more intuitive name for IBAN usage.
         *
         * @param countryCode ISO 3166-1 alpha-2 country code (e.g. {@code "TR"}, {@code "DE"})
         * @return this builder
         */
        public IbanBuilder country(String countryCode) { return locale(countryCode); }

        /**
         * Sets the country using a typed locale enum.
         *
         * @param locale the locale enum constant
         * @return this builder
         */
        public IbanBuilder country(MockJutsuLocale locale) { return locale(locale); }
    }

    /**
     * Fluent builder for full name generation with optional gender selection.
     *
     * <pre>{@code
     * String male   = MockJutsu.fullname().locale("TR").gender("M").generate();
     * String female = MockJutsu.fullname().locale("TR").gender("F").generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class FullnameBuilder extends BaseBuilder<FullnameBuilder> {
        private String gender = "";

        @Override
        protected String typeName() { return "fullname"; }

        /**
         * Filters the generated name by gender.
         *
         * @param gender {@code "M"} for male, {@code "F"} for female, or empty for random
         * @return this builder
         */
        public FullnameBuilder gender(String gender) {
            this.gender = gender == null ? "" : gender;
            return this;
        }

        /**
         * Filters the generated name by gender using a typed {@link Gender} enum.
         *
         * <pre>{@code
         * import static io.github.altansayan.mockjutsu.enums.Gender.*;
         *
         * String maleName = MockJutsu.fullname().locale("TR").gender(MALE).generate();
         * }</pre>
         *
         * @param gender the gender enum constant; {@link Gender#RANDOM} picks randomly
         * @return this builder
         * @since 1.0.0
         */
        public FullnameBuilder gender(Gender gender) {
            this.gender = gender == null ? "" : gender.key();
            return this;
        }

        @Override
        protected String qualifier() { return gender; }
    }

    /**
     * Fluent builder for age generation with optional numeric range.
     *
     * <p>The qualifier format forwarded to the generator is {@code "min-max"}
     * (e.g. {@code "18-35"}), matching the CLI {@code --min}/{@code --max} parameters.
     *
     * @since 1.0.0
     */
    public static final class AgeBuilder extends BaseBuilder<AgeBuilder> {
        private int min = 0;
        private int max = 0;

        /**
         * Sets the minimum age (inclusive). Default is 18.
         *
         * @param min minimum age value
         * @return this builder
         */
        public AgeBuilder min(int min) { this.min = min; return this; }

        /**
         * Sets the maximum age (inclusive). Default is 80.
         *
         * @param max maximum age value
         * @return this builder
         */
        public AgeBuilder max(int max) { this.max = max; return this; }

        @Override
        protected String typeName() { return "age"; }

        @Override
        protected String qualifier() {
            if (min > 0 || max > 0) {
                int lo = min > 0 ? min : 18;
                int hi = max > 0 ? max : 80;
                return lo + "-" + hi;
            }
            return "";
        }
    }

    /**
     * Fluent builder for BIP-39 mnemonic phrase generation.
     *
     * <p>Valid word counts: 12, 15, 18, 21, 24. Any other value defaults to 12.
     *
     * @since 1.0.0
     */
    public static final class MnemonicBuilder extends BaseBuilder<MnemonicBuilder> {
        private int words = 12;

        /**
         * Sets the mnemonic word count. Valid values: 12, 15, 18, 21, 24.
         *
         * @param words the number of BIP-39 words; invalid values default to 12
         * @return this builder
         */
        public MnemonicBuilder words(int words) { this.words = words; return this; }

        @Override
        protected String typeName() { return "mnemonic"; }

        @Override
        protected String qualifier() { return String.valueOf(words); }
    }

    /**
     * Fluent builder for account balance generation with optional numeric range.
     *
     * <p>The qualifier format forwarded to the generator is {@code "min|max"}
     * (e.g. {@code "100|5000"}).
     *
     * @since 1.0.0
     */
    public static final class BalanceBuilder extends BaseBuilder<BalanceBuilder> {
        private double min = 0;
        private double max = 0;

        /** Sets the minimum balance (inclusive). */
        public BalanceBuilder min(double min) { this.min = min; return this; }

        /** Sets the maximum balance (inclusive). */
        public BalanceBuilder max(double max) { this.max = max; return this; }

        @Override
        protected String typeName() { return "balance"; }

        @Override
        protected String qualifier() {
            if (min != 0 || max != 0) {
                double lo = min != 0 ? min : 100.0;
                double hi = max != 0 ? max : 9999.99;
                return String.format(java.util.Locale.US, "%.2f|%.2f", lo, hi);
            }
            return "";
        }
    }

    /**
     * Fluent builder for {@code date_between} generation with optional date range.
     *
     * <p>The qualifier format is {@code "YYYY-MM-DD|YYYY-MM-DD"} (start|end).
     *
     * @since 1.0.0
     */
    public static final class DateRangeBuilder extends BaseBuilder<DateRangeBuilder> {
        private String from = "";
        private String to   = "";

        /** Sets the start date in {@code YYYY-MM-DD} format. */
        public DateRangeBuilder from(String from) { this.from = from == null ? "" : from; return this; }

        /** Sets the end date in {@code YYYY-MM-DD} format. */
        public DateRangeBuilder to(String to) { this.to = to == null ? "" : to; return this; }

        @Override
        protected String typeName() { return "date_between"; }

        @Override
        protected String qualifier() {
            if (!from.isBlank() && !to.isBlank()) return from + "|" + to;
            return "";
        }
    }

    /**
     * Fluent builder for dense AI vector ({@code ai_embedding} / {@code ai_vector}) generation
     * with optional dimension count.
     *
     * @since 1.0.0
     */
    public static final class AiVectorBuilder extends BaseBuilder<AiVectorBuilder> {
        private final String type;
        private int dims = 0;

        AiVectorBuilder(String type) { this.type = type; }

        /** Sets the vector dimension count. Default is 1536. */
        public AiVectorBuilder dims(int dims) { this.dims = dims; return this; }

        @Override
        protected String typeName() { return type; }

        @Override
        protected String qualifier() { return dims > 0 ? String.valueOf(dims) : ""; }
    }

    /**
     * Fluent builder for sparse AI vector ({@code ai_sparse_vector}) generation
     * with optional dims and non-zero count.
     *
     * <p>The qualifier format is {@code "dims|nnz"} (e.g. {@code "10000|128"}).
     *
     * @since 1.0.0
     */
    public static final class AiSparseVectorBuilder extends BaseBuilder<AiSparseVectorBuilder> {
        private int dims = 0;
        private int nnz  = 0;

        /** Sets the total vector dimension count. Default is 10000. */
        public AiSparseVectorBuilder dims(int dims) { this.dims = dims; return this; }

        /** Sets the number of non-zero elements. Default is 128. */
        public AiSparseVectorBuilder nnz(int nnz) { this.nnz = nnz; return this; }

        @Override
        protected String typeName() { return "ai_sparse_vector"; }

        @Override
        protected String qualifier() {
            if (dims > 0 || nnz > 0) {
                int d = dims > 0 ? dims : 10000;
                int n = nnz  > 0 ? nnz  : 128;
                return d + "|" + n;
            }
            return "";
        }
    }

    /**
     * Fluent builder for {@code reverse_regex} string generation.
     *
     * <pre>{@code
     * String s = MockJutsu.reverseRegex().pattern("[A-Z]{3}-\\d{4}").generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class PatternBuilder extends BaseBuilder<PatternBuilder> {
        private String pattern = "";

        /** Sets the regex pattern to generate a matching string from. */
        public PatternBuilder pattern(String pattern) {
            this.pattern = pattern == null ? "" : pattern;
            return this;
        }

        @Override
        protected String typeName() { return "reverse_regex"; }

        @Override
        protected String qualifier() { return pattern; }
    }

    /**
     * Fluent builder for HMAC {@code signature} generation with optional secret and payload.
     *
     * <p>The qualifier format is {@code "secret|payload"}.
     *
     * @since 1.0.0
     */
    public static final class SignatureBuilder extends BaseBuilder<SignatureBuilder> {
        private String secret  = "";
        private String payload = "";

        /** Sets the HMAC secret key. */
        public SignatureBuilder secret(String secret) {
            this.secret = secret == null ? "" : secret;
            return this;
        }

        /** Sets the payload to sign. */
        public SignatureBuilder payload(String payload) {
            this.payload = payload == null ? "" : payload;
            return this;
        }

        @Override
        protected String typeName() { return "signature"; }

        @Override
        protected String qualifier() {
            if (!secret.isBlank() || !payload.isBlank()) return secret + "|" + payload;
            return "";
        }
    }

    /**
     * Fluent builder for strict-mode payment message generation
     * ({@code swift_mt103}, {@code pain001}, {@code sepa_mandate}).
     *
     * <p>In strict mode, BICs are drawn from the ISO 9362 compliant pool
     * (position 8 = {@code "0"}, no branch qualifiers).
     *
     * <pre>{@code
     * String mt103 = MockJutsu.swiftMt103().locale(TR).strict().generate();
     * String pain  = MockJutsu.pain001().strict().generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class StrictPaymentBuilder extends BaseBuilder<StrictPaymentBuilder> {
        private final String type;
        private boolean strict = false;

        StrictPaymentBuilder(String type) { this.type = type; }

        /** Enables ISO 9362 strict BIC mode (position 8 = {@code "0"}). */
        public StrictPaymentBuilder strict() { this.strict = true; return this; }

        /** Explicitly sets strict mode on or off. */
        public StrictPaymentBuilder strict(boolean strict) { this.strict = strict; return this; }

        @Override
        protected String typeName() { return type; }

        @Override
        protected String qualifier() { return strict ? "strict" : ""; }
    }

    /**
     * Fluent builder for {@code forex_rate} generation with optional currency pair.
     *
     * <pre>{@code
     * String rate = MockJutsu.forexRate().pair("EUR/USD").generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class ForexRateBuilder extends BaseBuilder<ForexRateBuilder> {
        private String pair = "";

        /**
         * Sets the currency pair in {@code "BASE/QUOTE"} format (e.g. {@code "EUR/USD"}).
         *
         * @param pair the currency pair string
         * @return this builder
         */
        public ForexRateBuilder pair(String pair) { this.pair = pair == null ? "" : pair; return this; }

        @Override
        protected String typeName() { return "forex_rate"; }

        @Override
        protected String qualifier() { return pair; }
    }

    /**
     * Fluent builder for {@code psd2_consent} object generation with optional fixed amount.
     *
     * <pre>{@code
     * String consent = MockJutsu.psd2Consent().amount(1234.56).locale("DE").generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class Psd2ConsentBuilder extends BaseBuilder<Psd2ConsentBuilder> {
        private double amount = 0;

        /**
         * Sets a fixed instructed amount for the consent object.
         *
         * @param amount the instructed amount in the locale currency
         * @return this builder
         */
        public Psd2ConsentBuilder amount(double amount) { this.amount = amount; return this; }

        @Override
        protected String typeName() { return "psd2_consent"; }

        @Override
        protected String qualifier() {
            return amount > 0 ? String.format(java.util.Locale.US, "%.2f", amount) : "";
        }
    }

    /**
     * Fluent builder for {@code hash} generation with a typed algorithm selector.
     *
     * <pre>{@code
     * String h = MockJutsu.hash().algorithm(HashAlgorithm.SHA3_256).generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class HashBuilder extends BaseBuilder<HashBuilder> {
        private HashAlgorithm algorithm = HashAlgorithm.SHA256;

        /** Sets the hash algorithm. Default is {@link HashAlgorithm#SHA256}. */
        public HashBuilder algorithm(HashAlgorithm algorithm) {
            this.algorithm = algorithm == null ? HashAlgorithm.SHA256 : algorithm;
            return this;
        }

        @Override
        protected String typeName() { return "hash"; }

        @Override
        protected String qualifier() { return algorithm.key(); }
    }

    /**
     * Fluent builder for {@code color} generation with a typed format selector.
     *
     * <pre>{@code
     * String c = MockJutsu.color().format(ColorFormat.HSL).generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class ColorBuilder extends BaseBuilder<ColorBuilder> {
        private ColorFormat format = ColorFormat.HEX;

        /** Sets the color format. Default is {@link ColorFormat#HEX}. */
        public ColorBuilder format(ColorFormat format) {
            this.format = format == null ? ColorFormat.HEX : format;
            return this;
        }

        @Override
        protected String typeName() { return "color"; }

        @Override
        protected String qualifier() { return format.key(); }
    }

    /**
     * Fluent builder for {@code tracking_number} generation with a typed carrier selector.
     *
     * <pre>{@code
     * String t = MockJutsu.trackingNumber().carrier(Carrier.FEDEX).generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class TrackingNumberBuilder extends BaseBuilder<TrackingNumberBuilder> {
        private Carrier carrier = Carrier.USPS;

        /** Sets the shipping carrier. Default is {@link Carrier#USPS}. */
        public TrackingNumberBuilder carrier(Carrier carrier) {
            this.carrier = carrier == null ? Carrier.USPS : carrier;
            return this;
        }

        @Override
        protected String typeName() { return "tracking_number"; }

        @Override
        protected String qualifier() { return carrier.key(); }
    }

    /**
     * Fluent builder for {@code crypto_address} generation with a typed currency selector.
     *
     * <pre>{@code
     * String addr = MockJutsu.cryptoAddress().currency(CryptoCurrency.ETH).generate();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class CryptoAddressBuilder extends BaseBuilder<CryptoAddressBuilder> {
        private CryptoCurrency currency = CryptoCurrency.BTC;

        /** Sets the cryptocurrency. Default is {@link CryptoCurrency#BTC}. */
        public CryptoAddressBuilder currency(CryptoCurrency currency) {
            this.currency = currency == null ? CryptoCurrency.BTC : currency;
            return this;
        }

        @Override
        protected String typeName() { return "crypto_address"; }

        @Override
        protected String qualifier() { return currency.key(); }
    }
}
