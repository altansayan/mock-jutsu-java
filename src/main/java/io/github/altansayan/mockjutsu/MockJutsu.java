package io.github.altansayan.mockjutsu;

import io.github.altansayan.mockjutsu.enums.DataType;
import io.github.altansayan.mockjutsu.enums.Gender;
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
     * Generates a single mock value using a typed {@link DataType} enum constant with a qualifier.
     *
     * <pre>{@code
     * String visa = MockJutsu.generate(DataType.CARDNUM, MockJutsuLocale.TR, "visa");
     * }</pre>
     *
     * @param type      the data type enum constant
     * @param locale    the locale enum constant; defaults to {@code US} when {@code null}
     * @param qualifier optional qualifier (e.g. network key, gender); empty string if unused
     * @return a non-null generated value string
     * @since 1.0.0
     */
    public static String generate(DataType type, MockJutsuLocale locale, String qualifier) {
        return Registry.generate(type.key(), locale == null ? "US" : locale.code(), qualifier == null ? "" : qualifier);
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
}
