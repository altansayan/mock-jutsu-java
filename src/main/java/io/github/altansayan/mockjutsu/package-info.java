/**
 * MockJutsu Core — zero-dependency Java library for generating realistic synthetic test data.
 *
 * <h2>Entry points</h2>
 * <ul>
 *   <li>{@link io.github.altansayan.mockjutsu.MockJutsu} — String API ({@code generate}, {@code bulk}, {@code mask})
 *       and fluent builder factories ({@code cardnum()}, {@code iban()}, {@code tckn()}, etc.)
 *   <li>{@link io.github.altansayan.mockjutsu.masker.Masker} — regulation-compliant masking (PCI DSS, KVKK, GDPR, NHS, HIPAA)
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 *   <li>{@link io.github.altansayan.mockjutsu.enums.MockJutsuLocale} — 40 supported locale codes
 *   <li>{@link io.github.altansayan.mockjutsu.enums.Network} — 9 payment card networks
 * </ul>
 *
 * <h2>Quick example</h2>
 * <pre>{@code
 * import io.github.altansayan.mockjutsu.MockJutsu;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 * import static io.github.altansayan.mockjutsu.enums.Network.*;
 *
 * // String API
 * String tckn = MockJutsu.generate("tckn", "TR");
 * String iban  = MockJutsu.generate("iban", "DE");
 *
 * // Fluent builder
 * String card = MockJutsu.cardnum().locale(TR).network(VISA).generate();
 *
 * // Masking
 * String masked = MockJutsu.masker("cardnum", "4532015112830366"); // → "4532 01****** 0366"
 * }</pre>
 *
 * <p>Supports 390+ data types across identity, financial, telecom, health, IoT, cryptography, and more.
 * All generated values are entirely synthetic and must not be submitted to real production systems.
 *
 * @since 1.0.0
 * @see <a href="https://github.com/altansayan/mock-jutsu-java">GitHub Repository</a>
 */
package io.github.altansayan.mockjutsu;
