package io.github.altansayan.mockjutsu;

import io.github.altansayan.mockjutsu.enums.Carrier;
import io.github.altansayan.mockjutsu.enums.ColorFormat;
import io.github.altansayan.mockjutsu.enums.CryptoCurrency;
import io.github.altansayan.mockjutsu.enums.DataType;
import io.github.altansayan.mockjutsu.enums.Gender;
import io.github.altansayan.mockjutsu.enums.HashAlgorithm;
import io.github.altansayan.mockjutsu.enums.MockJutsuLocale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Faz 4 parity test — every known type must generate a non-empty, non-error value.
 * Mirrors the type lists in Registry.java; keeps Java coverage in sync with Python.
 */
class TypeCoverageTest {

    // ── All 390+ types, locale TR (covers most) ───────────────────────────────

    static Stream<String> allTypes() {
        return Stream.of(
            // Identity
            "tckn","ykn","taxid","vkn","nationalid","ssn","nin","inn","inn_individual",
            "snils","sgk","mersis","ein","utr","crn","paye","ust_id","hrb","rvn",
            "siren","siret","tva","ogrn","kpp","employer_id","insurance_id",
            "firstname","lastname","fullname","patronymic","passport","license",
            "age","gender","birthdate","nationality","vat_number",
            // Financial
            "cardnum","cardnetwork","cardtype","cardstatus","cvv3","cvv4",
            "issuer","expiry","expirymonth","expiryyear","pin","balance",
            "iban","cardcategory","credit_score",
            "sepa_qr","emv_qr_p2p","emv_qr_atm","emv_qr_pos","3ds_cavv","3ds_eci",
            // Communication / Contact
            "phone","phone_country","phone_area","phone_local",
            "address_city","address_street","address_full","postalcode","plate","email",
            // Meta
            "uuid","requestid","correlationid","sessionid","idempotencykey",
            "deviceid","ipv4","ipv6","browser_name","browser_version","browser_engine",
            "useragent","timestamp","timestamp_iso","clientversion","bearertoken",
            "signature","apppassword","jwt","hash","mac_address","domain","url","color",
            "api_key","totp_code","webhook_signature","transaction_id","public_ip","private_ip",
            "slug","http_method","http_status_code","port_number","hostname","tld","uri_path",
            // Banking
            "swift","bic","sort_code","routing_number","wire_routing_number","bik_code",
            "transaction","bank_name","sepa_ref","creditor_ref",
            "account_type","transaction_type","transaction_description",
            "ifsc_code","bsb_code","check_number","micr_line",
            "payment_reference","account_number",
            "micr_line_masked","transaction_description_masked",
            "check_number_masked","payment_reference_masked",
            // Corporate
            "company_name","job_title","occupation",
            // Health
            "blood_type","nhs_number","icd10","height","weight","npi","bmi",
            "hl7_message","fhir_patient","dicom_uid",
            // Commerce
            "currency","tax_rate","invoice_number","vin","vehicle",
            // IoT
            "rfid_uid","epc","rfid_tag","nfc_uid","nfc_atqa","nfc_sak",
            "ndef_uri","ndef_text","apdu","nfc_tag",
            "ir_nec","ir_rc5","ir_pronto","ir_raw","mqtt_payload","lora_packet",
            // Barcode
            "ean13","ean8","upca","isbn13","isbn10","gs1_128",
            // Telecom
            "imei","imei2","iccid","imsi","msisdn",
            // Securities
            "isin","cusip","sedol","lei","fix_message","psd2_consent",
            "figi","nsin","stock_ticker","forex_pair","forex_rate","ric","mic",
            "stock_exchange","option_contract","bond_yield","coupon_rate","settlement_date",
            "portfolio_id",
            // Crypto
            "btc_address","eth_address","crypto_address","tx_hash","block_hash","mnemonic",
            "nft_token_id","gas_price","gas_limit","defi_protocol_name","blockchain_network",
            "wallet_label","defi_position_type","cryptocurrency_name","liquidity_pool_id",
            // E-commerce
            "product_name","sku","order_id","tracking_number","category","rating","dhl_tracking",
            // Location
            "latitude","longitude","timezone","country_code","coordinates",
            // Social
            "username","hashtag","bio","handle","follower_count",
            // Hardware (PCI)
            "track1_data","track2_data","chip_data","pin_block","pin_block_fmt3",
            // Card physics
            "emv_arqc","emv_atc","emv_iad",
            "iso8583_auth_request","iso8583_auth_response","iso8583_reversal",
            "atm_session","pos_receipt",
            // Security
            "cef_log","x509_cert","pcap_hex","password","password_hash","cve_id",
            // Aviation
            "iata_ticket","imo_number","pnr_code",
            // FIDO2
            "webauthn_credential","fido2_assertion",
            // Wallets
            "eth_wallet","btc_wallet","sol_wallet",
            // AI vector
            "ai_embedding","ai_vector","ai_sparse_vector",
            // OIDC
            "oidc_token_set","jwks","oidc_token",
            // Bank statements
            "mt940","camt053",
            // EDI
            "edi_850","edifact_orders",
            // Event sourcing
            "event_stream","cdc_event",
            // Telemetry
            "fdr_record","drone_telemetry",
            // Crypto fuzz
            "jwt_attack","asn1_fuzz",
            // MRZ
            "mrz_td3","mrz_td1",
            // OHLCV
            "ohlcv_candles","market_tick",
            // NMEA
            "nmea_gpgga","nmea_gprmc",
            // Prometheus
            "prometheus_metrics","openmetrics_snapshot",
            // GameDev
            "quaternion","navmesh_path",
            // UBL
            "ubl_invoice","xmldsig",
            // Automotive
            "can_frame","obd2_response",
            // TLE
            "tle_satellite",
            // Payments
            "swift_mt103","pain001","nacha_ach","sepa_mandate","fedwire",
            // Compliance
            "policy_number","claim_number","pep_status","aml_risk_rating","cdd_level",
            "sar_number","ubo_ownership_percentage","kyc_document_type","consent_id",
            "tpp_id","onboarding_method","sanctions_hit",
            // Compliance masked variants
            "sar_number_masked","policy_number_masked","claim_number_masked",
            "ubo_ownership_percentage_masked","consent_id_masked",
            // Financial ext
            "credit_score_model","credit_score_tier","credit_limit","credit_utilization",
            "credit_card_issuer_name","apr","loan_type","mortgage_rate","mortgage_term",
            "premium_amount","deductible","coverage_limit","claim_status",
            // Financial ext masked variants
            "credit_limit_masked","mortgage_rate_masked","premium_amount_masked",
            // DateTime
            "past_date","future_date","date_this_year","date_this_month","time_only",
            "past_datetime","future_datetime",
            // Reverse regex (needs qualifier but should not error)
            // International IDs
            "br_cpf","br_cnpj",
            "in_pan","in_aadhaar","in_gstin","in_epic",
            "cn_ric",
            "mx_curp","mx_rfc",
            "it_codicefiscale",
            "es_dni","es_nie","es_ccc",
            "de_idnr","de_stnr",
            "pk_cnic",
            "jp_cn","jp_in",
            "kr_rrn","kr_brn",
            "nl_bsn",
            "pl_pesel",
            "se_personnummer",
            "dk_cpr",
            "fi_hetu",
            "no_fodselsnummer",
            "au_abn","au_tfn","au_acn",
            "my_nric",
            "th_pin","th_tin",
            "sg_uen",
            "za_idnr",
            "ca_bn",
            "nz_ird",
            "ar_cuit","ar_dni",
            "cl_rut",
            "co_nit",
            "il_idnr",
            "ro_cnp","ro_cui",
            "hr_oib",
            "bg_egn",
            "lt_asmens",
            "ee_ik",
            "pt_cc",
            "eg_tn"
        );
    }

    // Types that return empty string for TR locale by design (locale-specific data)
    private static final java.util.Set<String> LOCALE_SPECIFIC = java.util.Set.of(
        "patronymic",    // Russian naming — only non-empty for RU/UA
        "snils",         // Russian social security — only for RU
        "ogrn", "kpp"   // Russian company IDs
    );

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTypes")
    void generateDoesNotReturnError(String type) {
        String locale = LOCALE_SPECIFIC.contains(type) ? "RU" : "TR";
        String result = MockJutsu.generate(type, locale);
        assertNotNull(result, type + " returned null");
        assertFalse(result.startsWith("ERROR:"), type + " returned: " + result);
        if (!LOCALE_SPECIFIC.contains(type)) {
            assertFalse(result.isBlank(), type + " returned blank");
        }
    }

    @Test
    void cardnumAllNetworks() {
        for (String net : List.of("visa","mc","amex","troy","mir","jcb","discover","unionpay","maestro")) {
            String card = MockJutsu.generate("cardnum", "TR", net);
            assertFalse(card.startsWith("ERROR:"), "cardnum " + net + " error: " + card);
            assertFalse(card.isBlank(), "cardnum " + net + " is blank");
        }
    }

    @Test
    void fullnameGenders() {
        String male   = MockJutsu.generate("fullname", "TR", "M");
        String female = MockJutsu.generate("fullname", "TR", "F");
        assertFalse(male.isBlank());
        assertFalse(female.isBlank());
    }

    @Test
    void maskedVariantsNotError() {
        for (String type : List.of("tckn_masked","ssn_masked","account_number_masked","portfolio_id_masked")) {
            String result = MockJutsu.generate(type, "TR");
            assertFalse(result.startsWith("ERROR:"), type + " error: " + result);
        }
    }

    // ── DataType enum: 390 constants, all backed by a working key ─────────────

    @Test
    void dataTypeEnumHas390Constants() {
        assertEquals(390, DataType.values().length,
            "DataType enum must have exactly 390 constants (one per supported type)");
    }

    @Test
    void dataTypeEnumAllConstantsWork() {
        for (DataType dt : DataType.values()) {
            String locale = LOCALE_SPECIFIC.contains(dt.key()) ? "RU" : "TR";
            String result = MockJutsu.generate(dt, MockJutsuLocale.of(locale));
            assertFalse(result.startsWith("ERROR:"),
                "DataType." + dt.name() + " (\"" + dt.key() + "\") returned: " + result);
        }
    }

    @Test
    void dataTypeEnumOverloads() {
        // generate(DataType, String)
        String r1 = MockJutsu.generate(DataType.TCKN, "TR");
        assertFalse(r1.startsWith("ERROR:"));

        // generate(DataType, MockJutsuLocale)
        String r2 = MockJutsu.generate(DataType.IBAN, MockJutsuLocale.DE);
        assertFalse(r2.startsWith("ERROR:"));

        // generate(DataType, MockJutsuLocale, qualifier)
        String r3 = MockJutsu.generate(DataType.CARDNUM, MockJutsuLocale.TR, "visa");
        assertFalse(r3.startsWith("ERROR:"));

        // bulk(DataType, MockJutsuLocale, count)
        List<String> bulk = MockJutsu.bulk(DataType.IBAN, MockJutsuLocale.TR, 5);
        assertEquals(5, bulk.size());
        bulk.forEach(v -> assertFalse(v.startsWith("ERROR:")));

        // mask(DataType, String)
        String masked = MockJutsu.mask(DataType.CARDNUM, "4532015112830366");
        assertFalse(masked.startsWith("ERROR:"));
        assertTrue(masked.contains("*"));
    }

    // ── Gender enum ───────────────────────────────────────────────────────────

    @Test
    void genderEnumOverload() {
        String male   = MockJutsu.fullname().locale("TR").gender(Gender.MALE).generate();
        String female = MockJutsu.fullname().locale("TR").gender(Gender.FEMALE).generate();
        String random = MockJutsu.fullname().locale("TR").gender(Gender.RANDOM).generate();
        assertFalse(male.isBlank(),   "MALE fullname is blank");
        assertFalse(female.isBlank(), "FEMALE fullname is blank");
        assertFalse(random.isBlank(), "RANDOM fullname is blank");
    }

    @Test
    void genderEnumOfParsing() {
        assertEquals(Gender.MALE,   Gender.of("M"));
        assertEquals(Gender.MALE,   Gender.of("male"));
        assertEquals(Gender.MALE,   Gender.of("erkek"));
        assertEquals(Gender.FEMALE, Gender.of("F"));
        assertEquals(Gender.FEMALE, Gender.of("female"));
        assertEquals(Gender.RANDOM, Gender.of(null));
        assertEquals(Gender.RANDOM, Gender.of(""));
        assertEquals(Gender.RANDOM, Gender.of("unknown"));
    }

    // ── generate(DataType, MockJutsuLocale, Network) ──────────────────────────

    @Test
    void networkEnumOverloadInGenerateMethod() {
        // generate(CARDNUM, TR, VISA) — the main request from the user
        String visa = MockJutsu.generate(DataType.CARDNUM, MockJutsuLocale.TR, io.github.altansayan.mockjutsu.enums.Network.VISA);
        assertFalse(visa.startsWith("ERROR:"), "VISA card error: " + visa);
        assertFalse(visa.isBlank());

        String amex = MockJutsu.generate(DataType.CARDNUM, MockJutsuLocale.US, io.github.altansayan.mockjutsu.enums.Network.AMEX);
        assertFalse(amex.startsWith("ERROR:"), "AMEX card error: " + amex);
        // AMEX is 15-digit, starts with 3
        assertTrue(amex.replaceAll("\\s","").startsWith("3"), "AMEX should start with 3: " + amex);
    }

    @Test
    void genderEnumOverloadInGenerateMethod() {
        String male   = MockJutsu.generate(DataType.FULLNAME, MockJutsuLocale.TR, Gender.MALE);
        String female = MockJutsu.generate(DataType.FULLNAME, MockJutsuLocale.TR, Gender.FEMALE);
        assertFalse(male.isBlank());
        assertFalse(female.isBlank());
    }

    // ── AgeBuilder ────────────────────────────────────────────────────────────

    @Test
    void ageBuilderDefault() {
        String age = MockJutsu.age().generate();
        int v = Integer.parseInt(age);
        assertTrue(v >= 18 && v <= 80, "Default age out of range: " + v);
    }

    @Test
    void ageBuilderRange() {
        for (int i = 0; i < 20; i++) {
            String age = MockJutsu.age().min(18).max(35).generate();
            int v = Integer.parseInt(age);
            assertTrue(v >= 18 && v <= 35, "Age out of 18-35 range: " + v);
        }
    }

    @Test
    void ageBuilderBulk() {
        List<String> ages = MockJutsu.age().min(20).max(40).bulk(50);
        assertEquals(50, ages.size());
        for (String age : ages) {
            int v = Integer.parseInt(age);
            assertTrue(v >= 20 && v <= 40, "Age out of 20-40 range: " + v);
        }
    }

    // ── MnemonicBuilder ───────────────────────────────────────────────────────

    @Test
    void mnemonicBuilderDefaultIs12Words() {
        String phrase = MockJutsu.mnemonic().generate();
        assertEquals(12, phrase.split("\\s+").length, "Default mnemonic should be 12 words: " + phrase);
    }

    @Test
    void mnemonicBuilderWords24() {
        String phrase = MockJutsu.mnemonic().words(24).generate();
        assertEquals(24, phrase.split("\\s+").length, "24-word mnemonic word count: " + phrase);
    }

    // ── HashAlgorithm enum overload ───────────────────────────────────────────

    @Test
    void hashAlgorithmEnumOverload() {
        for (HashAlgorithm algo : HashAlgorithm.values()) {
            String h = MockJutsu.generate(DataType.HASH, MockJutsuLocale.US, algo);
            assertFalse(h.startsWith("ERROR:"), "HASH " + algo + " error: " + h);
            assertFalse(h.isBlank(), "HASH " + algo + " is blank");
        }
    }

    @Test
    void hashBuilderAlgorithm() {
        String sha256 = MockJutsu.hash().algorithm(HashAlgorithm.SHA256).generate();
        assertEquals(64, sha256.length(), "SHA-256 should be 64 hex chars: " + sha256);

        String md5 = MockJutsu.hash().algorithm(HashAlgorithm.MD5).generate();
        assertEquals(32, md5.length(), "MD5 should be 32 hex chars: " + md5);

        String crc32 = MockJutsu.hash().algorithm(HashAlgorithm.CRC32).generate();
        assertEquals(8, crc32.length(), "CRC32 should be 8 hex chars: " + crc32);
    }

    // ── ColorFormat enum overload ─────────────────────────────────────────────

    @Test
    void colorFormatEnumOverload() {
        String hex  = MockJutsu.generate(DataType.COLOR, MockJutsuLocale.US, ColorFormat.HEX);
        String rgb  = MockJutsu.generate(DataType.COLOR, MockJutsuLocale.US, ColorFormat.RGB);
        String hsl  = MockJutsu.generate(DataType.COLOR, MockJutsuLocale.US, ColorFormat.HSL);
        String name = MockJutsu.generate(DataType.COLOR, MockJutsuLocale.US, ColorFormat.NAME);

        assertTrue(hex.startsWith("#"), "HEX color should start with #: " + hex);
        assertTrue(rgb.startsWith("rgb("), "RGB color format: " + rgb);
        assertTrue(hsl.startsWith("hsl("), "HSL color format: " + hsl);
        assertFalse(name.isBlank(), "NAME color is blank");
    }

    @Test
    void colorBuilderFormat() {
        String hsl = MockJutsu.color().format(ColorFormat.HSL).generate();
        assertTrue(hsl.startsWith("hsl("), "ColorBuilder HSL: " + hsl);
    }

    // ── Carrier enum overload ─────────────────────────────────────────────────

    @Test
    void carrierEnumOverload() {
        for (Carrier c : Carrier.values()) {
            String t = MockJutsu.generate(DataType.TRACKING_NUMBER, MockJutsuLocale.US, c);
            assertFalse(t.startsWith("ERROR:"), "tracking_number " + c + " error: " + t);
            assertFalse(t.isBlank(), "tracking_number " + c + " is blank");
        }
    }

    @Test
    void trackingNumberBuilderCarrier() {
        String fedex = MockJutsu.trackingNumber().carrier(Carrier.FEDEX).generate();
        assertFalse(fedex.startsWith("ERROR:"), "FEDEX tracking: " + fedex);
        assertFalse(fedex.isBlank());

        String ups = MockJutsu.trackingNumber().carrier(Carrier.UPS).generate();
        assertTrue(ups.startsWith("1Z"), "UPS tracking should start with 1Z: " + ups);
    }

    // ── CryptoCurrency enum overload ──────────────────────────────────────────

    @Test
    void cryptoCurrencyEnumOverload() {
        String btcAddr = MockJutsu.generate(DataType.CRYPTO_ADDRESS, MockJutsuLocale.US, CryptoCurrency.BTC);
        String ethAddr = MockJutsu.generate(DataType.CRYPTO_ADDRESS, MockJutsuLocale.US, CryptoCurrency.ETH);

        // BTC P2PKH starts with 1, P2SH with 3
        assertTrue(btcAddr.startsWith("1") || btcAddr.startsWith("3"),
            "BTC address format: " + btcAddr);
        assertTrue(ethAddr.startsWith("0x"), "ETH address should start with 0x: " + ethAddr);

        String btcTx = MockJutsu.generate(DataType.TX_HASH, MockJutsuLocale.US, CryptoCurrency.BTC);
        String ethTx = MockJutsu.generate(DataType.TX_HASH, MockJutsuLocale.US, CryptoCurrency.ETH);
        assertFalse(btcTx.isBlank());
        assertTrue(ethTx.startsWith("0x"), "ETH tx hash format: " + ethTx);
    }

    @Test
    void cryptoAddressBuilderCurrency() {
        String eth = MockJutsu.cryptoAddress().currency(CryptoCurrency.ETH).generate();
        assertTrue(eth.startsWith("0x"), "CryptoAddressBuilder ETH: " + eth);
    }

    // ── BalanceBuilder ────────────────────────────────────────────────────────

    @Test
    void balanceBuilderDefault() {
        String b = MockJutsu.balance().generate();
        assertFalse(b.startsWith("ERROR:"), "balance default: " + b);
        assertFalse(b.isBlank());
    }

    @Test
    void balanceBuilderRange() {
        String b = MockJutsu.balance().min(100).max(500).generate();
        assertFalse(b.startsWith("ERROR:"), "balance range: " + b);
    }

    // ── DateRangeBuilder ──────────────────────────────────────────────────────

    @Test
    void dateRangeBuilderDefault() {
        String d = MockJutsu.dateRange().generate();
        assertFalse(d.startsWith("ERROR:"), "dateRange default: " + d);
    }

    @Test
    void dateRangeBuilderWithRange() {
        String d = MockJutsu.dateRange().from("2020-01-01").to("2025-12-31").generate();
        assertFalse(d.startsWith("ERROR:"), "dateRange with range: " + d);
        assertFalse(d.isBlank());
    }

    // ── AiVectorBuilder ───────────────────────────────────────────────────────

    @Test
    void aiEmbeddingBuilderDefault() {
        String e = MockJutsu.aiEmbedding().generate();
        assertFalse(e.startsWith("ERROR:"), "aiEmbedding default: " + e);
        assertTrue(e.startsWith("["), "ai_embedding should be JSON array: " + e.substring(0, Math.min(20,e.length())));
    }

    @Test
    void aiVectorBuilderDims() {
        String v = MockJutsu.aiVector().dims(128).generate();
        assertFalse(v.startsWith("ERROR:"), "aiVector 128 dims: " + v);
        // 128 floats in JSON array = 128 commas-separated values
        long commas = v.chars().filter(c -> c == ',').count();
        assertEquals(127, commas, "ai_vector dims=128 should have 127 commas");
    }

    // ── AiSparseVectorBuilder ─────────────────────────────────────────────────

    @Test
    void aiSparseVectorBuilderDefault() {
        String s = MockJutsu.aiSparseVector().generate();
        assertFalse(s.startsWith("ERROR:"), "aiSparseVector default: " + s);
        assertTrue(s.startsWith("{"), "ai_sparse_vector should be JSON object: " + s.substring(0, Math.min(20,s.length())));
    }

    @Test
    void aiSparseVectorBuilderCustom() {
        String s = MockJutsu.aiSparseVector().dims(1000).nnz(32).generate();
        assertFalse(s.startsWith("ERROR:"), "aiSparseVector custom: " + s);
    }

    // ── PatternBuilder ────────────────────────────────────────────────────────

    @Test
    void patternBuilderReverseRegex() {
        String s = MockJutsu.reverseRegex().pattern("[A-Z]{3}-\\d{4}").generate();
        assertFalse(s.startsWith("ERROR:"), "reverseRegex pattern: " + s);
        assertFalse(s.isBlank());
    }

    // ── SignatureBuilder ──────────────────────────────────────────────────────

    @Test
    void signatureBuilderDefault() {
        String sig = MockJutsu.signature().generate();
        assertFalse(sig.startsWith("ERROR:"), "signature default: " + sig);
        assertFalse(sig.isBlank());
    }

    @Test
    void signatureBuilderWithSecretAndPayload() {
        String sig = MockJutsu.signature().secret("my-key").payload("{\"amount\":100}").generate();
        assertFalse(sig.startsWith("ERROR:"), "signature with secret+payload: " + sig);
        assertFalse(sig.isBlank());
    }

    // ── StrictPaymentBuilder ──────────────────────────────────────────────────

    @Test
    void strictPaymentBuilderSwiftMt103() {
        String mt103 = MockJutsu.swiftMt103().locale(MockJutsuLocale.TR).generate();
        assertFalse(mt103.startsWith("ERROR:"), "swiftMt103 default: " + mt103);
        assertFalse(mt103.isBlank());
    }

    @Test
    void strictPaymentBuilderSwiftMt103Strict() {
        String mt103 = MockJutsu.swiftMt103().locale(MockJutsuLocale.TR).strict().generate();
        assertFalse(mt103.startsWith("ERROR:"), "swiftMt103 strict: " + mt103);
    }

    @Test
    void strictPaymentBuilderPain001() {
        String pain = MockJutsu.pain001().strict(true).generate();
        assertFalse(pain.startsWith("ERROR:"), "pain001 strict: " + pain);
        assertTrue(pain.contains("<?xml"), "pain001 should be XML: " + pain.substring(0, Math.min(50, pain.length())));
    }

    @Test
    void strictPaymentBuilderSepaMandate() {
        String m = MockJutsu.sepaMandate().strict().generate();
        assertFalse(m.startsWith("ERROR:"), "sepaMandate strict: " + m);
    }

    // ── ForexRateBuilder ──────────────────────────────────────────────────────

    @Test
    void forexRateBuilderDefault() {
        String r = MockJutsu.forexRate().generate();
        assertFalse(r.startsWith("ERROR:"), "forexRate default: " + r);
        assertFalse(r.isBlank());
    }

    @Test
    void forexRateBuilderPair() {
        String r = MockJutsu.forexRate().pair("EUR/USD").generate();
        assertFalse(r.startsWith("ERROR:"), "forexRate EUR/USD: " + r);
        assertTrue(r.contains("EUR") || r.contains("."), "forexRate should contain pair info or decimal: " + r);
    }

    // ── Psd2ConsentBuilder ────────────────────────────────────────────────────

    @Test
    void psd2ConsentBuilderDefault() {
        String c = MockJutsu.psd2Consent().generate();
        assertFalse(c.startsWith("ERROR:"), "psd2Consent default: " + c);
        assertFalse(c.isBlank());
    }

    @Test
    void psd2ConsentBuilderAmount() {
        // psd2_consent output is a signed JWT — amount is embedded in Base64 payload
        String c = MockJutsu.psd2Consent().amount(9999.99).locale("DE").generate();
        assertFalse(c.startsWith("ERROR:"), "psd2Consent with amount: " + c);
        assertFalse(c.isBlank(), "psd2Consent with amount is blank");
        // JWT has 3 parts separated by '.'
        assertEquals(3, c.split("\\.").length, "psd2Consent should be a JWT (3 parts): " + c.substring(0, Math.min(30, c.length())));
    }

    // ── icd10 description qualifier ───────────────────────────────────────────

    @Test
    void icd10WithDescriptionQualifier() {
        String withDesc = MockJutsu.generate("icd10", "TR", "desc");
        assertFalse(withDesc.startsWith("ERROR:"), "icd10 desc error: " + withDesc);
        assertTrue(withDesc.startsWith("{\"code\":"), "icd10 desc should be JSON: " + withDesc);
        assertTrue(withDesc.contains("\"description\":"), "icd10 desc should have description field: " + withDesc);
    }

    @Test
    void icd10WithoutDescriptionReturnsCodeOnly() {
        String codeOnly = MockJutsu.generate("icd10", "TR");
        assertFalse(codeOnly.startsWith("ERROR:"), "icd10 code only: " + codeOnly);
        assertFalse(codeOnly.contains("{"), "icd10 without desc should not be JSON: " + codeOnly);
    }

    // ── enum of() parsing ─────────────────────────────────────────────────────

    @Test
    void hashAlgorithmOfParsing() {
        assertEquals(HashAlgorithm.SHA256,  HashAlgorithm.of("sha256"));
        assertEquals(HashAlgorithm.SHA256,  HashAlgorithm.of("SHA256"));
        assertEquals(HashAlgorithm.MD5,     HashAlgorithm.of("md5"));
        assertEquals(HashAlgorithm.SHA3_256, HashAlgorithm.of("sha3-256"));
        assertEquals(HashAlgorithm.CRC32,   HashAlgorithm.of("crc32"));
        assertEquals(HashAlgorithm.SHA256,  HashAlgorithm.of(null));
        assertEquals(HashAlgorithm.SHA256,  HashAlgorithm.of("unknown"));
    }

    @Test
    void colorFormatOfParsing() {
        assertEquals(ColorFormat.HEX,  ColorFormat.of("hex"));
        assertEquals(ColorFormat.RGB,  ColorFormat.of("RGB"));
        assertEquals(ColorFormat.HSL,  ColorFormat.of("hsl"));
        assertEquals(ColorFormat.NAME, ColorFormat.of("name"));
        assertEquals(ColorFormat.HEX,  ColorFormat.of(null));
    }

    @Test
    void carrierOfParsing() {
        assertEquals(Carrier.USPS,  Carrier.of("usps"));
        assertEquals(Carrier.UPS,   Carrier.of("UPS"));
        assertEquals(Carrier.FEDEX, Carrier.of("fedex"));
        assertEquals(Carrier.USPS,  Carrier.of(null));
    }

    @Test
    void cryptoCurrencyOfParsing() {
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.of("btc"));
        assertEquals(CryptoCurrency.ETH, CryptoCurrency.of("ETH"));
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.of(null));
    }
}
