# MockJutsu Core — Java

[![Build](https://github.com/altansayan/mock-jutsu-java/actions/workflows/build.yml/badge.svg)](https://github.com/altansayan/mock-jutsu-java/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.altansayan/mockjutsu-core?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.altansayan/mockjutsu-core)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

**Zero-dependency** Java library for generating realistic mock data — 390+ types across identity, financial, telecom, health, IoT, cryptography, and more.

Java port of [Mock Jutsu](https://github.com/altansayan/mock-jutsu-api) (Python/PyPI).

---

## Installation

### Maven

```xml
<dependency>
  <groupId>io.github.altansayan</groupId>
  <artifactId>mockjutsu-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.altansayan:mockjutsu-core:1.0.0'
```

---

## Imports

Add these to the top of your Java file. Only include the qualifier enums you actually use.

```java
// ── Core (required) ───────────────────────────────────────────────────────
import static io.github.altansayan.mockjutsu.MockJutsu.*;            // generate(), bulk(), builders + all 390 DataType constants
import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*; // TR, US, DE, GB, FR, RU, CA, AU, JP, KR, CN, BR, IN ...

// ── Qualifier enums (add as needed) ──────────────────────────────────────
import static io.github.altansayan.mockjutsu.enums.Network.*;        // VISA, MC, AMEX, TROY, DISCOVER, UNIONPAY, JCB, MAESTRO
import static io.github.altansayan.mockjutsu.enums.Gender.*;         // MALE, FEMALE
import static io.github.altansayan.mockjutsu.enums.HashAlgorithm.*;  // MD5, SHA1, SHA256, SHA512, BLAKE2B
import static io.github.altansayan.mockjutsu.enums.ColorFormat.*;    // HEX, RGB, HSL, NAMED
import static io.github.altansayan.mockjutsu.enums.Carrier.*;        // TURKCELL, VODAFONE, TURK_TELEKOM, ATT, VERIZON, T_MOBILE, O2, DEUTSCHE_TELEKOM
import static io.github.altansayan.mockjutsu.enums.CryptoCurrency.*; // BTC, ETH, SOL, BNB, ADA, XRP, DOT, AVAX, MATIC, LINK
import static io.github.altansayan.mockjutsu.enums.AccountType.*;    // CHECKING, SAVINGS, CURRENT, BUSINESS_CHECKING, MONEY_MARKET, CD, INVESTMENT
```

> **Tip:** Your IDE (IntelliJ / VS Code) will suggest missing static imports automatically as you type.

---

## Quick Start

### String API — dynamic / scriptable

```java
import io.github.altansayan.mockjutsu.MockJutsu;
import java.util.List;

// Identity
String tckn  = MockJutsu.generate("tckn",  "TR");   // 11-digit Turkish national ID
String ssn   = MockJutsu.generate("ssn",   "US");   // XXX-XX-XXXX
String iban  = MockJutsu.generate("iban",  "DE");   // DE + 20 digits, MOD-97 valid
String email = MockJutsu.generate("email", "TR");

// Card numbers (with network qualifier)
String visa  = MockJutsu.generate("cardnum", "TR", "visa");
String amex  = MockJutsu.generate("cardnum", "TR", "amex");
String troy  = MockJutsu.generate("cardnum", "TR", "troy");

// Bulk
List<String> cards = MockJutsu.bulk("cardnum", "TR", 100);
```

### Enum API — fully qualified, no static imports needed

```java
import io.github.altansayan.mockjutsu.MockJutsu;
import io.github.altansayan.mockjutsu.enums.DataType;
import io.github.altansayan.mockjutsu.enums.MockJutsuLocale;
import io.github.altansayan.mockjutsu.enums.Network;
import io.github.altansayan.mockjutsu.enums.Gender;

String card  = MockJutsu.generate(DataType.CARDNUM,  MockJutsuLocale.TR, Network.VISA);
String name  = MockJutsu.generate(DataType.FULLNAME, MockJutsuLocale.TR, Gender.MALE);
String iban  = MockJutsu.generate(DataType.IBAN,     MockJutsuLocale.DE);
String tckn  = MockJutsu.generate(DataType.TCKN,     MockJutsuLocale.TR);
```

### Fluent Builder API — type-safe, IDE autocomplete

```java
import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
import static io.github.altansayan.mockjutsu.enums.Network.*;

// Card number
String card = MockJutsu.cardnum()
    .locale(TR)
    .network(VISA)
    .generate();

// IBAN
String iban = MockJutsu.iban()
    .country(DE)
    .generate();

// Bulk
List<String> ibanList = MockJutsu.iban()
    .locale(TR)
    .bulk(50);

// Full name with gender
String name = MockJutsu.fullname()
    .locale(TR)
    .gender("M")
    .generate();
```

### Masker — regulation-compliant

```java
// PCI DSS (6-digit BIN visible + last 4)
MockJutsu.masker(DataType.CARDNUM,       "4532015112830366");          // → "4532 01****** 0366"

// ISO/IEC 7812:2017 — 8-digit BIN masking
MockJutsu.masker(DataType.CARDNUM_BIN8,  "4532015112830366");          // → "4532 0151 **** 0366"

// KVKK / GDPR
MockJutsu.masker(DataType.TCKN,         "12345678901");                // → "12*******01"
MockJutsu.masker(DataType.IBAN,         "TR330006100519786457841326"); // → "TR33 **** **** **** **** 1326"
MockJutsu.masker(DataType.EMAIL,        "john@example.com");           // → "jo***@example.com"
MockJutsu.masker(DataType.PHONE,        "+905321234567");              // → "+90 *** *** ** 67"
MockJutsu.masker(DataType.ADDRESS_FULL, "Bağdat Caddesi No:45 Kadıköy"); // → "B*** C*** N*** K***"
```

---

## Supported Types (390+)

| Category | Types |
|----------|-------|
| **Identity** | `tckn`, `ssn`, `nin`, `vkn`, `passport`, `license`, `firstname`, `lastname`, `fullname`, `birthdate`, `age`, `gender` + 30 more |
| **Financial** | `cardnum`, `iban`, `cvv3`, `cvv4`, `expiry`, `pin`, `balance`, `credit_score`, `sepa_qr`, `emv_qr_p2p` + 10 more |
| **Banking** | `swift`, `bic`, `sort_code`, `routing_number`, `bank_name`, `iban`, `micr_line`, `account_number` + 10 more |
| **Telecom** | `imei`, `imei2`, `iccid`, `imsi`, `msisdn` |
| **Health** | `nhs_number`, `icd10`, `npi`, `blood_type`, `hl7_message`, `fhir_patient`, `dicom_uid` |
| **Security** | `password`, `password_hash`, `cef_log`, `x509_cert`, `pcap_hex`, `cve_id` |
| **Crypto** | `btc_address`, `eth_address`, `mnemonic`, `tx_hash`, `block_hash`, `btc_wallet`, `eth_wallet`, `sol_wallet` |
| **Markets** | `isin`, `cusip`, `sedol`, `lei`, `fix_message`, `ohlcv_candles`, `market_tick`, `forex_pair` |
| **Identity (Intl)** | `br_cpf`, `br_cnpj`, `in_pan`, `in_aadhaar`, `cn_ric`, `mx_curp`, `kr_rrn`, `it_codicefiscale` + 30 more |
| **IoT** | `rfid_uid`, `epc`, `nfc_uid`, `mqtt_payload`, `lora_packet`, `apdu` |
| **Location** | `latitude`, `longitude`, `timezone`, `country_code`, `coordinates` |
| **Commerce** | `ean13`, `ean8`, `upca`, `isbn13`, `vin`, `invoice_number`, `sku`, `order_id` |
| **Meta** | `uuid`, `jwt`, `bearertoken`, `ipv4`, `ipv6`, `useragent`, `mac_address`, `api_key` |
| **Payments** | `swift_mt103`, `pain001`, `nacha_ach`, `sepa_mandate`, `fedwire` |
| **Compliance** | `pep_status`, `aml_risk_rating`, `kyc_document_type`, `consent_id`, `sanctions_hit` |
| **Automotive** | `can_frame`, `obd2_response`, `vin` |
| **Aviation** | `iata_ticket`, `pnr_code`, `imo_number`, `mrz_td3`, `mrz_td1` |
| **FIDO2/OIDC** | `webauthn_credential`, `fido2_assertion`, `oidc_token`, `jwks` |
| **Other** | `prometheus_metrics`, `nmea_gpgga`, `tle_satellite`, `ohlcv_candles`, `quaternion`, `ubl_invoice` |

---

## Supported Locales

`TR` `US` `DE` `GB` `FR` `ES` `IT` `NL` `PT` `PL` `RU` `UA` `IN` `CN` `JP` `KR` `BR` `AU` `CA` `MX` `SE` `DK` `NO` `FI` `AR` `CL` `CO` `ZA` `SG` `MY` `TH` `EG` `IL` `RO` `HR` `BG` `LT` `EE` `NZ` `PK`

---

## Algorithm Guarantees

| Algorithm | Standard | Status |
|-----------|----------|--------|
| Luhn | ISO/IEC 7812 | ✅ Verified |
| IBAN MOD-97 | ISO 13616 | ✅ Verified |
| TCKN | Turkish MERNİS | ✅ Verified |
| NHS Modulo-11 | UK NHS | ✅ Verified |
| EAN-13 checksum | GS1 | ✅ Verified |
| NMEA checksum | NMEA 0183 | ✅ Verified |
| PIN Block Format 0/3 | ISO 9564 | ✅ Verified |
| EMV ARQC/IAD | EMV 4.3 | ✅ Verified |
| ISO 8583 bitmap | ISO 8583 | ✅ Verified |
| ISIN Luhn | ISO 6166 | ✅ Verified |
| CUSIP check digit | CUSIP | ✅ Verified |
| SEDOL check digit | LSE | ✅ Verified |
| LEI MOD-97 | ISO 17442 | ✅ Verified |
| BTC Base58Check | BIP-16 | ✅ Verified |
| ETH EIP-55 | EIP-55 | ✅ Verified |
| MRZ check digit | ICAO 9303 | ✅ Verified |

---

## Requirements

- Java 17+
- Zero external dependencies (main library)
- JUnit Jupiter 5.10+ (test only)

---

## Related

- **Python / CLI / API**: [mock-jutsu-api](https://github.com/altansayan/mock-jutsu-api) — `pip install mockjutsu`
- **JMeter Plugin**: [mock-jutsu-jmeter](https://github.com/altansayan/mock-jutsu-jmeter)
- **PyPI**: [mockjutsu](https://pypi.org/project/mockjutsu/)

---

## ⚖️ Legal Disclaimer

Generated data is **entirely synthetic** and for development/testing environments only.

- Do not submit to real financial, government, or telecom production systems.
- Generated IBANs, card numbers, and national IDs are mathematically valid but **do not belong to real entities**.

---

## 💝 Support Mock Jutsu

<div align="center">

Mock Jutsu is **free and open-source**. If it saved you hours of test data setup, consider buying me a coffee ☕

*"Every cup of coffee = one more data type."* 🥷

| Network | Address |
|---------|---------|
| Ξ Ethereum (ETH) | `0x8D2fF0a795E3a19D41758Cb9b4451C39D528BbAF` |

*This section will be updated with our sponsors.*

---

If mock-jutsu-java saved you from debugging a "valid-looking but broken" test ID, please leave a ⭐!

Released under the [MIT License](LICENSE) • Copyright © 2026 [Altan Sezer Ayan — A.S.A](https://github.com/altansayan)

</div>
