package io.github.altansayan.mockjutsu;

import io.github.altansayan.mockjutsu.enums.MockJutsuLocale;
import io.github.altansayan.mockjutsu.enums.Network;
import io.github.altansayan.mockjutsu.masker.Masker;

import java.util.ArrayList;
import java.util.List;

/**
 * MockJutsu — zero-dependency Java library for realistic mock data generation.
 *
 * <p>Two APIs are available:
 * <ul>
 *   <li><b>String API</b> — dynamic/scriptable: {@code MockJutsu.generate("cardnum", "TR", "visa")}</li>
 *   <li><b>Fluent Builder API</b> — type-safe with IDE autocomplete: {@code MockJutsu.cardnum().locale(TR).network(VISA).generate()}</li>
 * </ul>
 *
 * <pre>{@code
 * // String API
 * String card  = MockJutsu.generate("cardnum", "TR");
 * String iban  = MockJutsu.generate("iban", "DE");
 * String tckn  = MockJutsu.generate("tckn", "TR");
 *
 * // Fluent Builder API
 * String card  = MockJutsu.cardnum().locale(TR).network(VISA).generate();
 * String iban  = MockJutsu.iban().locale(DE).generate();
 * List<String> cards = MockJutsu.cardnum().locale(TR).network(MC).bulk(10);
 *
 * // Masker
 * String masked = MockJutsu.mask("cardnum", "4532015112830366");
 * // → "453201****** 0366"
 * }</pre>
 */
public final class MockJutsu {

    private MockJutsu() {}

    // ── String API ────────────────────────────────────────────────────────────

    /** Generate a single value. Locale defaults to "US" when null. */
    public static String generate(String type, String locale) {
        return Registry.generate(type, locale == null ? "US" : locale);
    }

    /** Generate with an optional qualifier (e.g. network for cardnum, gender for fullname). */
    public static String generate(String type, String locale, String qualifier) {
        return Registry.generate(type, locale == null ? "US" : locale, qualifier == null ? "" : qualifier);
    }

    /** Generate using enum locale. */
    public static String generate(String type, MockJutsuLocale locale) {
        return Registry.generate(type, locale == null ? "US" : locale.code());
    }

    /** Generate a list of values. */
    public static List<String> bulk(String type, String locale, int count) {
        List<String> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) results.add(generate(type, locale));
        return results;
    }

    // ── Masker ────────────────────────────────────────────────────────────────

    /**
     * Returns a regulation-compliant masked version of the given value.
     * Supports PCI DSS, KVKK, GDPR, HIPAA, and 160+ type-specific rules.
     */
    public static String mask(String type, String value) {
        return Masker.mask(type, value);
    }

    // ── Fluent Builder API ────────────────────────────────────────────────────

    /** Fluent builder for card number generation. */
    public static CardnumBuilder cardnum() { return new CardnumBuilder(); }

    /** Fluent builder for IBAN generation. */
    public static IbanBuilder iban() { return new IbanBuilder(); }

    /** Fluent builder for TCKN (Turkish national ID) generation. */
    public static SimpleBuilder tckn() { return new SimpleBuilder("tckn"); }

    /** Fluent builder for SSN (US Social Security Number) generation. */
    public static SimpleBuilder ssn() { return new SimpleBuilder("ssn"); }

    /** Fluent builder for full name generation. */
    public static FullnameBuilder fullname() { return new FullnameBuilder(); }

    /** Fluent builder for email generation. */
    public static SimpleBuilder email() { return new SimpleBuilder("email"); }

    /** Fluent builder for phone number generation. */
    public static SimpleBuilder phone() { return new SimpleBuilder("phone"); }

    /** Fluent builder for UUID generation. */
    public static SimpleBuilder uuid() { return new SimpleBuilder("uuid"); }

    /** Fluent builder for JWT token generation. */
    public static SimpleBuilder jwt() { return new SimpleBuilder("jwt"); }

    /** Fluent builder for IMEI generation. */
    public static SimpleBuilder imei() { return new SimpleBuilder("imei"); }

    /** Fluent builder for SWIFT/BIC code generation. */
    public static SimpleBuilder swift() { return new SimpleBuilder("swift"); }

    /** Fluent builder for EAN-13 barcode generation. */
    public static SimpleBuilder ean13() { return new SimpleBuilder("ean13"); }

    // ── Builder classes ───────────────────────────────────────────────────────

    /** Base builder with locale and bulk support. */
    public abstract static class BaseBuilder<T extends BaseBuilder<T>> {
        protected String locale = "US";
        protected abstract String typeName();

        @SuppressWarnings("unchecked")
        public T locale(String locale) { this.locale = locale == null ? "US" : locale.toUpperCase(); return (T) this; }

        @SuppressWarnings("unchecked")
        public T locale(MockJutsuLocale locale) { this.locale = locale == null ? "US" : locale.code(); return (T) this; }

        public String generate() { return Registry.generate(typeName(), locale, qualifier()); }

        public List<String> bulk(int count) {
            List<String> out = new ArrayList<>(count);
            for (int i = 0; i < count; i++) out.add(generate());
            return out;
        }

        protected String qualifier() { return ""; }
    }

    /** Generic builder for types that only need locale. */
    public static final class SimpleBuilder extends BaseBuilder<SimpleBuilder> {
        private final String type;
        SimpleBuilder(String type) { this.type = type; }
        @Override protected String typeName() { return type; }
    }

    /** Builder for card number — adds network() method. */
    public static final class CardnumBuilder extends BaseBuilder<CardnumBuilder> {
        private String network = "";
        private boolean bin8 = false;

        @Override protected String typeName() { return "cardnum"; }

        public CardnumBuilder network(Network network) { this.network = network == null ? "" : network.key(); return this; }
        public CardnumBuilder network(String network)  { this.network = network == null ? "" : network; return this; }

        /** Use 8-digit BIN masking (ISO/IEC 7812:2017). */
        public CardnumBuilder bin8() { this.bin8 = true; return this; }

        @Override protected String qualifier() { return network; }

        @Override public String generate() {
            String card = Registry.generate(typeName(), locale, network);
            return bin8 ? Masker.mask("cardnum_bin8", card) : card;
        }
    }

    /** Builder for IBAN — adds country() as alias for locale(). */
    public static final class IbanBuilder extends BaseBuilder<IbanBuilder> {
        @Override protected String typeName() { return "iban"; }

        /** Alias for locale() — more natural for IBAN usage. */
        public IbanBuilder country(String countryCode) { return locale(countryCode); }
        public IbanBuilder country(MockJutsuLocale locale) { return locale(locale); }
    }

    /** Builder for full name — adds gender() method. */
    public static final class FullnameBuilder extends BaseBuilder<FullnameBuilder> {
        private String gender = "";
        @Override protected String typeName() { return "fullname"; }
        public FullnameBuilder gender(String gender) { this.gender = gender == null ? "" : gender; return this; }
        @Override protected String qualifier() { return gender; }
    }
}
