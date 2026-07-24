package io.github.altansayan.mockjutsu.enums;

/**
 * All 390 supported data types as compile-time constants.
 *
 * <p>Enables IDE autocomplete and eliminates stringly-typed {@code generate()} calls:
 * <pre>{@code
 * import static io.github.altansayan.mockjutsu.enums.DataType.*;
 * import static io.github.altansayan.mockjutsu.enums.MockJutsuLocale.*;
 *
 * // Type-safe String API
 * String tckn = MockJutsu.generate(TCKN, TR);
 * String iban = MockJutsu.generate(IBAN, DE);
 * String visa = MockJutsu.generate(CARDNUM, TR, "visa");
 *
 * // Bulk
 * List<String> ibans = MockJutsu.bulk(IBAN, TR, 50);
 * }</pre>
 *
 * <p>Types starting with a digit use the {@code THREE_} prefix
 * (e.g. {@link #THREE_DS_CAVV} for {@code "3ds_cavv"}).
 *
 * @since 1.0.0
 */
public enum DataType {

    // ── 3DS ──────────────────────────────────────────────────────────────────
    THREE_DS_CAVV("3ds_cavv"),
    THREE_DS_ECI("3ds_eci"),

    // ── A ─────────────────────────────────────────────────────────────────────
    ACCOUNT_NUMBER("account_number"),
    ACCOUNT_NUMBER_MASKED("account_number_masked"),
    ACCOUNT_TYPE("account_type"),
    ADDRESS_CITY("address_city"),
    ADDRESS_FULL("address_full"),
    ADDRESS_STREET("address_street"),
    AGE("age"),
    AI_EMBEDDING("ai_embedding"),
    AI_SPARSE_VECTOR("ai_sparse_vector"),
    AI_VECTOR("ai_vector"),
    AML_RISK_RATING("aml_risk_rating"),
    APDU("apdu"),
    API_KEY("api_key"),
    APPPASSWORD("apppassword"),
    APR("apr"),
    AR_CUIT("ar_cuit"),
    AR_DNI("ar_dni"),
    ASN1_FUZZ("asn1_fuzz"),
    ATM_SESSION("atm_session"),
    AU_ABN("au_abn"),
    AU_ACN("au_acn"),
    AU_TFN("au_tfn"),

    // ── B ─────────────────────────────────────────────────────────────────────
    BALANCE("balance"),
    BANK_NAME("bank_name"),
    BEARERTOKEN("bearertoken"),
    BG_EGN("bg_egn"),
    BIC("bic"),
    BIK_CODE("bik_code"),
    BIO("bio"),
    BIRTHDATE("birthdate"),
    BLOCK_HASH("block_hash"),
    BLOCKCHAIN_NETWORK("blockchain_network"),
    BLOOD_TYPE("blood_type"),
    BLOODTYPE("bloodtype"),
    BMI("bmi"),
    BOND_YIELD("bond_yield"),
    BR_CNPJ("br_cnpj"),
    BR_CPF("br_cpf"),
    BROWSER_ENGINE("browser_engine"),
    BROWSER_NAME("browser_name"),
    BROWSER_VERSION("browser_version"),
    BSB_CODE("bsb_code"),
    BTC_ADDRESS("btc_address"),
    BTC_WALLET("btc_wallet"),

    // ── C ─────────────────────────────────────────────────────────────────────
    CA_BN("ca_bn"),
    CAMT053("camt053"),
    CAN_FRAME("can_frame"),
    CARDCATEGORY("cardcategory"),
    CARDNETWORK("cardnetwork"),
    CARDNUM("cardnum"),
    CARDOWNER("cardowner"),
    CARDSTATUS("cardstatus"),
    CARDTYPE("cardtype"),
    CATEGORY("category"),
    CDC_EVENT("cdc_event"),
    CDD_LEVEL("cdd_level"),
    CEF_LOG("cef_log"),
    CHECK_NUMBER("check_number"),
    CHECK_NUMBER_MASKED("check_number_masked"),
    CHIP_DATA("chip_data"),
    CL_RUT("cl_rut"),
    CLAIM_NUMBER("claim_number"),
    CLAIM_NUMBER_MASKED("claim_number_masked"),
    CLAIM_STATUS("claim_status"),
    CLIENTVERSION("clientversion"),
    CN_RIC("cn_ric"),
    CO_NIT("co_nit"),
    COLOR("color"),
    COMPANY_NAME("company_name"),
    CONSENT_ID("consent_id"),
    CONSENT_ID_MASKED("consent_id_masked"),
    COORDINATES("coordinates"),
    CORRELATIONID("correlationid"),
    COUNTRY_CODE("country_code"),
    COUPON_RATE("coupon_rate"),
    COVERAGE_LIMIT("coverage_limit"),
    CREDIT_CARD_ISSUER_NAME("credit_card_issuer_name"),
    CREDIT_LIMIT("credit_limit"),
    CREDIT_LIMIT_MASKED("credit_limit_masked"),
    CREDIT_SCORE("credit_score"),
    CREDIT_SCORE_MODEL("credit_score_model"),
    CREDIT_SCORE_TIER("credit_score_tier"),
    CREDIT_UTILIZATION("credit_utilization"),
    CREDITOR_REF("creditor_ref"),
    CRN("crn"),
    CRYPTO_ADDRESS("crypto_address"),
    CRYPTOCURRENCY_NAME("cryptocurrency_name"),
    CURRENCY("currency"),
    CUSIP("cusip"),
    CVE_ID("cve_id"),
    CVV3("cvv3"),
    CVV4("cvv4"),

    // ── D ─────────────────────────────────────────────────────────────────────
    DATE_BETWEEN("date_between"),
    DATE_THIS_MONTH("date_this_month"),
    DATE_THIS_YEAR("date_this_year"),
    DE_IDNR("de_idnr"),
    DE_STNR("de_stnr"),
    DEDUCTIBLE("deductible"),
    DEFI_POSITION_TYPE("defi_position_type"),
    DEFI_PROTOCOL_NAME("defi_protocol_name"),
    DEVICEID("deviceid"),
    DHL_TRACKING("dhl_tracking"),
    DICOM_UID("dicom_uid"),
    DK_CPR("dk_cpr"),
    DOMAIN("domain"),
    DRONE_TELEMETRY("drone_telemetry"),

    // ── E ─────────────────────────────────────────────────────────────────────
    EAN13("ean13"),
    EAN8("ean8"),
    EDI_850("edi_850"),
    EDIFACT_ORDERS("edifact_orders"),
    EE_IK("ee_ik"),
    EG_TN("eg_tn"),
    EIN("ein"),
    EMAIL("email"),
    EMPLOYER_ID("employer_id"),
    EMV_ARQC("emv_arqc"),
    EMV_ATC("emv_atc"),
    EMV_IAD("emv_iad"),
    EMV_QR_ATM("emv_qr_atm"),
    EMV_QR_P2P("emv_qr_p2p"),
    EMV_QR_POS("emv_qr_pos"),
    EPC("epc"),
    ES_CCC("es_ccc"),
    ES_DNI("es_dni"),
    ES_NIE("es_nie"),
    ETH_ADDRESS("eth_address"),
    ETH_WALLET("eth_wallet"),
    EVENT_STREAM("event_stream"),
    EXPIRY("expiry"),
    EXPIRYMONTH("expirymonth"),
    EXPIRYYEAR("expiryyear"),

    // ── F ─────────────────────────────────────────────────────────────────────
    FDR_RECORD("fdr_record"),
    FEDWIRE("fedwire"),
    FHIR_PATIENT("fhir_patient"),
    FI_HETU("fi_hetu"),
    FIDO2_ASSERTION("fido2_assertion"),
    FIGI("figi"),
    FIRSTNAME("firstname"),
    FIX_MESSAGE("fix_message"),
    FOLLOWER_COUNT("follower_count"),
    FOREX_PAIR("forex_pair"),
    FOREX_RATE("forex_rate"),
    FULLNAME("fullname"),
    FUTURE_DATE("future_date"),
    FUTURE_DATETIME("future_datetime"),

    // ── G ─────────────────────────────────────────────────────────────────────
    GAS_LIMIT("gas_limit"),
    GAS_PRICE("gas_price"),
    GENDER("gender"),
    GS1_128("gs1_128"),

    // ── H ─────────────────────────────────────────────────────────────────────
    HANDLE("handle"),
    HASH("hash"),
    HASHTAG("hashtag"),
    HEIGHT("height"),
    HL7_MESSAGE("hl7_message"),
    HOSTNAME("hostname"),
    HR_OIB("hr_oib"),
    HRB("hrb"),
    HTTP_METHOD("http_method"),
    HTTP_STATUS_CODE("http_status_code"),

    // ── I ─────────────────────────────────────────────────────────────────────
    IATA_TICKET("iata_ticket"),
    IBAN("iban"),
    ICCID("iccid"),
    ICD10("icd10"),
    IDEMPOTENCYKEY("idempotencykey"),
    IFSC_CODE("ifsc_code"),
    IL_IDNR("il_idnr"),
    IMEI("imei"),
    IMEI2("imei2"),
    IMO_NUMBER("imo_number"),
    IMSI("imsi"),
    IN_AADHAAR("in_aadhaar"),
    IN_EPIC("in_epic"),
    IN_GSTIN("in_gstin"),
    IN_PAN("in_pan"),
    INN("inn"),
    INN_INDIVIDUAL("inn_individual"),
    INSURANCE_ID("insurance_id"),
    INVOICE_NUMBER("invoice_number"),
    INVOICENUMBER("invoicenumber"),
    IPV4("ipv4"),
    IPV6("ipv6"),
    IR_NEC("ir_nec"),
    IR_PRONTO("ir_pronto"),
    IR_RAW("ir_raw"),
    IR_RC5("ir_rc5"),
    ISBN10("isbn10"),
    ISBN13("isbn13"),
    ISIN("isin"),
    ISO8583_AUTH_REQUEST("iso8583_auth_request"),
    ISO8583_AUTH_RESPONSE("iso8583_auth_response"),
    ISO8583_REVERSAL("iso8583_reversal"),
    ISSUER("issuer"),
    IT_CODICEFISCALE("it_codicefiscale"),

    // ── J ─────────────────────────────────────────────────────────────────────
    JOB_TITLE("job_title"),
    JOBTITLE("jobtitle"),
    JP_CN("jp_cn"),
    JP_IN("jp_in"),
    JWKS("jwks"),
    JWT("jwt"),
    JWT_ATTACK("jwt_attack"),

    // ── K ─────────────────────────────────────────────────────────────────────
    KPP("kpp"),
    KR_BRN("kr_brn"),
    KR_RRN("kr_rrn"),
    KYC_DOCUMENT_TYPE("kyc_document_type"),

    // ── L ─────────────────────────────────────────────────────────────────────
    LASTNAME("lastname"),
    LATITUDE("latitude"),
    LEI("lei"),
    LICENSE("license"),
    LIQUIDITY_POOL_ID("liquidity_pool_id"),
    LIQUIDITY_POOL_ID_MASKED("liquidity_pool_id_masked"),
    LOAN_TYPE("loan_type"),
    LONGITUDE("longitude"),
    LORA_PACKET("lora_packet"),
    LT_ASMENS("lt_asmens"),

    // ── M ─────────────────────────────────────────────────────────────────────
    MAC_ADDRESS("mac_address"),
    MARKET_TICK("market_tick"),
    MERSIS("mersis"),
    MIC("mic"),
    MICR_LINE("micr_line"),
    MICR_LINE_MASKED("micr_line_masked"),
    MNEMONIC("mnemonic"),
    MORTGAGE_RATE("mortgage_rate"),
    MORTGAGE_RATE_MASKED("mortgage_rate_masked"),
    MORTGAGE_TERM("mortgage_term"),
    MQTT_PAYLOAD("mqtt_payload"),
    MRZ_TD1("mrz_td1"),
    MRZ_TD3("mrz_td3"),
    MSISDN("msisdn"),
    MT940("mt940"),
    MX_CURP("mx_curp"),
    MX_RFC("mx_rfc"),
    MY_NRIC("my_nric"),

    // ── N ─────────────────────────────────────────────────────────────────────
    NACHA_ACH("nacha_ach"),
    NATIONALID("nationalid"),
    NATIONALITY("nationality"),
    NAVMESH_PATH("navmesh_path"),
    NDEF_TEXT("ndef_text"),
    NDEF_URI("ndef_uri"),
    NFC_ATQA("nfc_atqa"),
    NFC_SAK("nfc_sak"),
    NFC_TAG("nfc_tag"),
    NFC_UID("nfc_uid"),
    NFT_TOKEN_ID("nft_token_id"),
    NHS_NUMBER("nhs_number"),
    NHSNUMBER("nhsnumber"),
    NIN("nin"),
    NL_BSN("nl_bsn"),
    NMEA_GPGGA("nmea_gpgga"),
    NMEA_GPRMC("nmea_gprmc"),
    NO_FODSELSNUMMER("no_fodselsnummer"),
    NPI("npi"),
    NSIN("nsin"),
    NZ_IRD("nz_ird"),

    // ── O ─────────────────────────────────────────────────────────────────────
    OBD2_RESPONSE("obd2_response"),
    OCCUPATION("occupation"),
    OGRN("ogrn"),
    OHLCV_CANDLES("ohlcv_candles"),
    OIDC_TOKEN("oidc_token"),
    OIDC_TOKEN_SET("oidc_token_set"),
    ONBOARDING_METHOD("onboarding_method"),
    OPENMETRICS_SNAPSHOT("openmetrics_snapshot"),
    OPTION_CONTRACT("option_contract"),
    ORDER_ID("order_id"),

    // ── P ─────────────────────────────────────────────────────────────────────
    PAIN001("pain001"),
    PASSPORT("passport"),
    PASSWORD("password"),
    PASSWORD_HASH("password_hash"),
    PAST_DATE("past_date"),
    PAST_DATETIME("past_datetime"),
    PATRONYMIC("patronymic"),
    PAYE("paye"),
    PAYMENT_REFERENCE("payment_reference"),
    PAYMENT_REFERENCE_MASKED("payment_reference_masked"),
    PCAP_HEX("pcap_hex"),
    PEP_STATUS("pep_status"),
    PHONE("phone"),
    PHONE_AREA("phone_area"),
    PHONE_COUNTRY("phone_country"),
    PHONE_LOCAL("phone_local"),
    PIN("pin"),
    PIN_BLOCK("pin_block"),
    PIN_BLOCK_FMT3("pin_block_fmt3"),
    PK_CNIC("pk_cnic"),
    PL_PESEL("pl_pesel"),
    PLATE("plate"),
    PNR_CODE("pnr_code"),
    POLICY_NUMBER("policy_number"),
    POLICY_NUMBER_MASKED("policy_number_masked"),
    PORT_NUMBER("port_number"),
    PORTFOLIO_ID("portfolio_id"),
    PORTFOLIO_ID_MASKED("portfolio_id_masked"),
    POS_RECEIPT("pos_receipt"),
    POSTALCODE("postalcode"),
    PREMIUM_AMOUNT("premium_amount"),
    PREMIUM_AMOUNT_MASKED("premium_amount_masked"),
    PRIVATE_IP("private_ip"),
    PRODUCT_NAME("product_name"),
    PROMETHEUS_METRICS("prometheus_metrics"),
    PSD2_CONSENT("psd2_consent"),
    PT_CC("pt_cc"),
    PUBLIC_IP("public_ip"),

    // ── Q ─────────────────────────────────────────────────────────────────────
    QUATERNION("quaternion"),

    // ── R ─────────────────────────────────────────────────────────────────────
    RATING("rating"),
    REQUESTID("requestid"),
    REVERSE_REGEX("reverse_regex"),
    RFID_TAG("rfid_tag"),
    RFID_UID("rfid_uid"),
    RIC("ric"),
    RO_CNP("ro_cnp"),
    RO_CUI("ro_cui"),
    ROUTING_NUMBER("routing_number"),
    RVN("rvn"),

    // ── S ─────────────────────────────────────────────────────────────────────
    SANCTIONS_HIT("sanctions_hit"),
    SAR_NUMBER("sar_number"),
    SAR_NUMBER_MASKED("sar_number_masked"),
    SE_PERSONNUMMER("se_personnummer"),
    SEDOL("sedol"),
    SEPA_MANDATE("sepa_mandate"),
    SEPA_QR("sepa_qr"),
    SEPA_REF("sepa_ref"),
    SESSIONID("sessionid"),
    SETTLEMENT_DATE("settlement_date"),
    SG_UEN("sg_uen"),
    SGK("sgk"),
    SIGNATURE("signature"),
    SIREN("siren"),
    SIRET("siret"),
    SKU("sku"),
    SLUG("slug"),
    SNILS("snils"),
    SOL_WALLET("sol_wallet"),
    SORT_CODE("sort_code"),
    SSN("ssn"),
    SSN_MASKED("ssn_masked"),
    STOCK_EXCHANGE("stock_exchange"),
    STOCK_TICKER("stock_ticker"),
    SWIFT("swift"),
    SWIFT_MT103("swift_mt103"),

    // ── T ─────────────────────────────────────────────────────────────────────
    TAX_RATE("tax_rate"),
    TAXID("taxid"),
    TAXRATE("taxrate"),
    TCKN("tckn"),
    TCKN_MASKED("tckn_masked"),
    TH_PIN("th_pin"),
    TH_TIN("th_tin"),
    TIME_ONLY("time_only"),
    TIMESTAMP("timestamp"),
    TIMESTAMP_ISO("timestamp_iso"),
    TIMEZONE("timezone"),
    TLD("tld"),
    TLE_SATELLITE("tle_satellite"),
    TOTP_CODE("totp_code"),
    TPP_ID("tpp_id"),
    TRACK1_DATA("track1_data"),
    TRACK2_DATA("track2_data"),
    TRACKING_NUMBER("tracking_number"),
    TRANSACTION("transaction"),
    TRANSACTION_DESCRIPTION("transaction_description"),
    TRANSACTION_DESCRIPTION_MASKED("transaction_description_masked"),
    TRANSACTION_ID("transaction_id"),
    TRANSACTION_TYPE("transaction_type"),
    TVA("tva"),
    TX_HASH("tx_hash"),

    // ── U ─────────────────────────────────────────────────────────────────────
    UBL_INVOICE("ubl_invoice"),
    UBO_OWNERSHIP_PERCENTAGE("ubo_ownership_percentage"),
    UBO_OWNERSHIP_PERCENTAGE_MASKED("ubo_ownership_percentage_masked"),
    UPCA("upca"),
    URI_PATH("uri_path"),
    URL("url"),
    USERAGENT("useragent"),
    USERNAME("username"),
    UST_ID("ust_id"),
    USTID("ustid"),
    UTR("utr"),
    UUID("uuid"),

    // ── V ─────────────────────────────────────────────────────────────────────
    VAT_NUMBER("vat_number"),
    VEHICLE("vehicle"),
    VIN("vin"),
    VKN("vkn"),

    // ── W ─────────────────────────────────────────────────────────────────────
    WALLET_LABEL("wallet_label"),
    WEBAUTHN_CREDENTIAL("webauthn_credential"),
    WEBHOOK_SIGNATURE("webhook_signature"),
    WEIGHT("weight"),
    WIRE_ROUTING_NUMBER("wire_routing_number"),

    // ── X ─────────────────────────────────────────────────────────────────────
    X509_CERT("x509_cert"),
    XMLDSIG("xmldsig"),

    // ── Y ─────────────────────────────────────────────────────────────────────
    YKN("ykn"),

    // ── Z ─────────────────────────────────────────────────────────────────────
    ZA_IDNR("za_idnr");

    private final String key;

    DataType(String key) { this.key = key; }

    /**
     * Returns the string key used by {@link io.github.altansayan.mockjutsu.MockJutsu#generate(String, String)}.
     *
     * @return the lowercase type key, e.g. {@code "tckn"}, {@code "3ds_cavv"}
     */
    public String key() { return key; }

    /**
     * Parses a type key string to the corresponding enum constant, case-insensitive.
     * Falls back to {@code null} if the key is unrecognised.
     *
     * @param key a type key string such as {@code "tckn"}, {@code "iban"}, {@code "3ds_cavv"}
     * @return the matching enum constant, or {@code null} if not found
     */
    public static DataType of(String key) {
        if (key == null || key.isBlank()) return null;
        String lower = key.trim().toLowerCase();
        for (DataType dt : values()) {
            if (dt.key.equals(lower)) return dt;
        }
        return null;
    }
}
