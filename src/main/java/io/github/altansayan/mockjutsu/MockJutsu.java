package io.github.altansayan.mockjutsu;

import io.github.altansayan.mockjutsu.enums.AccountType;
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
 * MockJutsu.masker(DataType.CARDNUM,       "4532015112830366");          // → "4532 01****** 0366"  (PCI DSS)
 * MockJutsu.masker(DataType.TCKN,         "12345678901");                // → "12*******01"         (KVKK)
 * MockJutsu.masker(DataType.EMAIL,        "john@example.com");           // → "jo***@example.com"   (GDPR)
 * MockJutsu.masker(DataType.IBAN,         "TR330006100519786");          // → "TR33 **** ... 6"     (SEPA/PSD2)
 * MockJutsu.masker(DataType.ADDRESS_FULL, "Bağdat Caddesi No:45");       // → "B*** C*** N***"      (GDPR)
 * }</pre>
 *
 * @author Altan Sezer Ayan
 * @since 1.0.0
 * @see <a href="https://github.com/altansayan/mock-jutsu-java">GitHub Repository</a>
 */
public final class MockJutsu {

    private MockJutsu() {}
    // ── DataType re-exports ── MockJutsu.CARDNUM gibi kullanım için ─────────────────────────
    public static final DataType THREE_DS_CAVV = DataType.THREE_DS_CAVV;
    public static final DataType THREE_DS_ECI = DataType.THREE_DS_ECI;
    public static final DataType ACCOUNT_NUMBER = DataType.ACCOUNT_NUMBER;
    public static final DataType ACCOUNT_NUMBER_MASKED = DataType.ACCOUNT_NUMBER_MASKED;
    public static final DataType ACCOUNT_TYPE = DataType.ACCOUNT_TYPE;
    public static final DataType ADDRESS_CITY = DataType.ADDRESS_CITY;
    public static final DataType ADDRESS_FULL = DataType.ADDRESS_FULL;
    public static final DataType ADDRESS_STREET = DataType.ADDRESS_STREET;
    public static final DataType AGE = DataType.AGE;
    public static final DataType AI_EMBEDDING = DataType.AI_EMBEDDING;
    public static final DataType AI_SPARSE_VECTOR = DataType.AI_SPARSE_VECTOR;
    public static final DataType AI_VECTOR = DataType.AI_VECTOR;
    public static final DataType AML_RISK_RATING = DataType.AML_RISK_RATING;
    public static final DataType APDU = DataType.APDU;
    public static final DataType API_KEY = DataType.API_KEY;
    public static final DataType APPPASSWORD = DataType.APPPASSWORD;
    public static final DataType APR = DataType.APR;
    public static final DataType AR_CUIT = DataType.AR_CUIT;
    public static final DataType AR_DNI = DataType.AR_DNI;
    public static final DataType ASN1_FUZZ = DataType.ASN1_FUZZ;
    public static final DataType ATM_SESSION = DataType.ATM_SESSION;
    public static final DataType AU_ABN = DataType.AU_ABN;
    public static final DataType AU_ACN = DataType.AU_ACN;
    public static final DataType AU_TFN = DataType.AU_TFN;
    public static final DataType BALANCE = DataType.BALANCE;
    public static final DataType BANK_NAME = DataType.BANK_NAME;
    public static final DataType BEARERTOKEN = DataType.BEARERTOKEN;
    public static final DataType BG_EGN = DataType.BG_EGN;
    public static final DataType BIC = DataType.BIC;
    public static final DataType BIK_CODE = DataType.BIK_CODE;
    public static final DataType BIO = DataType.BIO;
    public static final DataType BIRTHDATE = DataType.BIRTHDATE;
    public static final DataType BLOCK_HASH = DataType.BLOCK_HASH;
    public static final DataType BLOCKCHAIN_NETWORK = DataType.BLOCKCHAIN_NETWORK;
    public static final DataType BLOOD_TYPE = DataType.BLOOD_TYPE;
    public static final DataType BLOODTYPE = DataType.BLOODTYPE;
    public static final DataType BMI = DataType.BMI;
    public static final DataType BOND_YIELD = DataType.BOND_YIELD;
    public static final DataType BR_CNPJ = DataType.BR_CNPJ;
    public static final DataType BR_CPF = DataType.BR_CPF;
    public static final DataType BROWSER_ENGINE = DataType.BROWSER_ENGINE;
    public static final DataType BROWSER_NAME = DataType.BROWSER_NAME;
    public static final DataType BROWSER_VERSION = DataType.BROWSER_VERSION;
    public static final DataType BSB_CODE = DataType.BSB_CODE;
    public static final DataType BTC_ADDRESS = DataType.BTC_ADDRESS;
    public static final DataType BTC_WALLET = DataType.BTC_WALLET;
    public static final DataType CA_BN = DataType.CA_BN;
    public static final DataType CAMT053 = DataType.CAMT053;
    public static final DataType CAN_FRAME = DataType.CAN_FRAME;
    public static final DataType CARDCATEGORY = DataType.CARDCATEGORY;
    public static final DataType CARDNETWORK = DataType.CARDNETWORK;
    public static final DataType CARDNUM = DataType.CARDNUM;
    public static final DataType CARDOWNER = DataType.CARDOWNER;
    public static final DataType CARDSTATUS = DataType.CARDSTATUS;
    public static final DataType CARDTYPE = DataType.CARDTYPE;
    public static final DataType CATEGORY = DataType.CATEGORY;
    public static final DataType CDC_EVENT = DataType.CDC_EVENT;
    public static final DataType CDD_LEVEL = DataType.CDD_LEVEL;
    public static final DataType CEF_LOG = DataType.CEF_LOG;
    public static final DataType CHECK_NUMBER = DataType.CHECK_NUMBER;
    public static final DataType CHECK_NUMBER_MASKED = DataType.CHECK_NUMBER_MASKED;
    public static final DataType CHIP_DATA = DataType.CHIP_DATA;
    public static final DataType CL_RUT = DataType.CL_RUT;
    public static final DataType CLAIM_NUMBER = DataType.CLAIM_NUMBER;
    public static final DataType CLAIM_NUMBER_MASKED = DataType.CLAIM_NUMBER_MASKED;
    public static final DataType CLAIM_STATUS = DataType.CLAIM_STATUS;
    public static final DataType CLIENTVERSION = DataType.CLIENTVERSION;
    public static final DataType CN_RIC = DataType.CN_RIC;
    public static final DataType CO_NIT = DataType.CO_NIT;
    public static final DataType COLOR = DataType.COLOR;
    public static final DataType COMPANY_NAME = DataType.COMPANY_NAME;
    public static final DataType CONSENT_ID = DataType.CONSENT_ID;
    public static final DataType CONSENT_ID_MASKED = DataType.CONSENT_ID_MASKED;
    public static final DataType COORDINATES = DataType.COORDINATES;
    public static final DataType CORRELATIONID = DataType.CORRELATIONID;
    public static final DataType COUNTRY_CODE = DataType.COUNTRY_CODE;
    public static final DataType COUPON_RATE = DataType.COUPON_RATE;
    public static final DataType COVERAGE_LIMIT = DataType.COVERAGE_LIMIT;
    public static final DataType CREDIT_CARD_ISSUER_NAME = DataType.CREDIT_CARD_ISSUER_NAME;
    public static final DataType CREDIT_LIMIT = DataType.CREDIT_LIMIT;
    public static final DataType CREDIT_LIMIT_MASKED = DataType.CREDIT_LIMIT_MASKED;
    public static final DataType CREDIT_SCORE = DataType.CREDIT_SCORE;
    public static final DataType CREDIT_SCORE_MODEL = DataType.CREDIT_SCORE_MODEL;
    public static final DataType CREDIT_SCORE_TIER = DataType.CREDIT_SCORE_TIER;
    public static final DataType CREDIT_UTILIZATION = DataType.CREDIT_UTILIZATION;
    public static final DataType CREDITOR_REF = DataType.CREDITOR_REF;
    public static final DataType CRN = DataType.CRN;
    public static final DataType CRYPTO_ADDRESS = DataType.CRYPTO_ADDRESS;
    public static final DataType CRYPTOCURRENCY_NAME = DataType.CRYPTOCURRENCY_NAME;
    public static final DataType CURRENCY = DataType.CURRENCY;
    public static final DataType CUSIP = DataType.CUSIP;
    public static final DataType CVE_ID = DataType.CVE_ID;
    public static final DataType CVV3 = DataType.CVV3;
    public static final DataType CVV4 = DataType.CVV4;
    public static final DataType DATE_BETWEEN = DataType.DATE_BETWEEN;
    public static final DataType DATE_THIS_MONTH = DataType.DATE_THIS_MONTH;
    public static final DataType DATE_THIS_YEAR = DataType.DATE_THIS_YEAR;
    public static final DataType DE_IDNR = DataType.DE_IDNR;
    public static final DataType DE_STNR = DataType.DE_STNR;
    public static final DataType DEDUCTIBLE = DataType.DEDUCTIBLE;
    public static final DataType DEFI_POSITION_TYPE = DataType.DEFI_POSITION_TYPE;
    public static final DataType DEFI_PROTOCOL_NAME = DataType.DEFI_PROTOCOL_NAME;
    public static final DataType DEVICEID = DataType.DEVICEID;
    public static final DataType DHL_TRACKING = DataType.DHL_TRACKING;
    public static final DataType DICOM_UID = DataType.DICOM_UID;
    public static final DataType DK_CPR = DataType.DK_CPR;
    public static final DataType DOMAIN = DataType.DOMAIN;
    public static final DataType DRONE_TELEMETRY = DataType.DRONE_TELEMETRY;
    public static final DataType EAN13 = DataType.EAN13;
    public static final DataType EAN8 = DataType.EAN8;
    public static final DataType EDI_850 = DataType.EDI_850;
    public static final DataType EDIFACT_ORDERS = DataType.EDIFACT_ORDERS;
    public static final DataType EE_IK = DataType.EE_IK;
    public static final DataType EG_TN = DataType.EG_TN;
    public static final DataType EIN = DataType.EIN;
    public static final DataType EMAIL = DataType.EMAIL;
    public static final DataType EMPLOYER_ID = DataType.EMPLOYER_ID;
    public static final DataType EMV_ARQC = DataType.EMV_ARQC;
    public static final DataType EMV_ATC = DataType.EMV_ATC;
    public static final DataType EMV_IAD = DataType.EMV_IAD;
    public static final DataType EMV_QR_ATM = DataType.EMV_QR_ATM;
    public static final DataType EMV_QR_P2P = DataType.EMV_QR_P2P;
    public static final DataType EMV_QR_POS = DataType.EMV_QR_POS;
    public static final DataType EPC = DataType.EPC;
    public static final DataType ES_CCC = DataType.ES_CCC;
    public static final DataType ES_DNI = DataType.ES_DNI;
    public static final DataType ES_NIE = DataType.ES_NIE;
    public static final DataType ETH_ADDRESS = DataType.ETH_ADDRESS;
    public static final DataType ETH_WALLET = DataType.ETH_WALLET;
    public static final DataType EVENT_STREAM = DataType.EVENT_STREAM;
    public static final DataType EXPIRY = DataType.EXPIRY;
    public static final DataType EXPIRYMONTH = DataType.EXPIRYMONTH;
    public static final DataType EXPIRYYEAR = DataType.EXPIRYYEAR;
    public static final DataType FDR_RECORD = DataType.FDR_RECORD;
    public static final DataType FEDWIRE = DataType.FEDWIRE;
    public static final DataType FHIR_PATIENT = DataType.FHIR_PATIENT;
    public static final DataType FI_HETU = DataType.FI_HETU;
    public static final DataType FIDO2_ASSERTION = DataType.FIDO2_ASSERTION;
    public static final DataType FIGI = DataType.FIGI;
    public static final DataType FIRSTNAME = DataType.FIRSTNAME;
    public static final DataType FIX_MESSAGE = DataType.FIX_MESSAGE;
    public static final DataType FOLLOWER_COUNT = DataType.FOLLOWER_COUNT;
    public static final DataType FOREX_PAIR = DataType.FOREX_PAIR;
    public static final DataType FOREX_RATE = DataType.FOREX_RATE;
    public static final DataType FULLNAME = DataType.FULLNAME;
    public static final DataType FUTURE_DATE = DataType.FUTURE_DATE;
    public static final DataType FUTURE_DATETIME = DataType.FUTURE_DATETIME;
    public static final DataType GAS_LIMIT = DataType.GAS_LIMIT;
    public static final DataType GAS_PRICE = DataType.GAS_PRICE;
    public static final DataType GENDER = DataType.GENDER;
    public static final DataType GS1_128 = DataType.GS1_128;
    public static final DataType HANDLE = DataType.HANDLE;
    public static final DataType HASH = DataType.HASH;
    public static final DataType HASHTAG = DataType.HASHTAG;
    public static final DataType HEIGHT = DataType.HEIGHT;
    public static final DataType HL7_MESSAGE = DataType.HL7_MESSAGE;
    public static final DataType HOSTNAME = DataType.HOSTNAME;
    public static final DataType HR_OIB = DataType.HR_OIB;
    public static final DataType HRB = DataType.HRB;
    public static final DataType HTTP_METHOD = DataType.HTTP_METHOD;
    public static final DataType HTTP_STATUS_CODE = DataType.HTTP_STATUS_CODE;
    public static final DataType IATA_TICKET = DataType.IATA_TICKET;
    public static final DataType IBAN = DataType.IBAN;
    public static final DataType ICCID = DataType.ICCID;
    public static final DataType ICD10 = DataType.ICD10;
    public static final DataType IDEMPOTENCYKEY = DataType.IDEMPOTENCYKEY;
    public static final DataType IFSC_CODE = DataType.IFSC_CODE;
    public static final DataType IL_IDNR = DataType.IL_IDNR;
    public static final DataType IMEI = DataType.IMEI;
    public static final DataType IMEI2 = DataType.IMEI2;
    public static final DataType IMO_NUMBER = DataType.IMO_NUMBER;
    public static final DataType IMSI = DataType.IMSI;
    public static final DataType IN_AADHAAR = DataType.IN_AADHAAR;
    public static final DataType IN_EPIC = DataType.IN_EPIC;
    public static final DataType IN_GSTIN = DataType.IN_GSTIN;
    public static final DataType IN_PAN = DataType.IN_PAN;
    public static final DataType INN = DataType.INN;
    public static final DataType INN_INDIVIDUAL = DataType.INN_INDIVIDUAL;
    public static final DataType INSURANCE_ID = DataType.INSURANCE_ID;
    public static final DataType INVOICE_NUMBER = DataType.INVOICE_NUMBER;
    public static final DataType INVOICENUMBER = DataType.INVOICENUMBER;
    public static final DataType IPV4 = DataType.IPV4;
    public static final DataType IPV6 = DataType.IPV6;
    public static final DataType IR_NEC = DataType.IR_NEC;
    public static final DataType IR_PRONTO = DataType.IR_PRONTO;
    public static final DataType IR_RAW = DataType.IR_RAW;
    public static final DataType IR_RC5 = DataType.IR_RC5;
    public static final DataType ISBN10 = DataType.ISBN10;
    public static final DataType ISBN13 = DataType.ISBN13;
    public static final DataType ISIN = DataType.ISIN;
    public static final DataType ISO8583_AUTH_REQUEST = DataType.ISO8583_AUTH_REQUEST;
    public static final DataType ISO8583_AUTH_RESPONSE = DataType.ISO8583_AUTH_RESPONSE;
    public static final DataType ISO8583_REVERSAL = DataType.ISO8583_REVERSAL;
    public static final DataType ISSUER = DataType.ISSUER;
    public static final DataType IT_CODICEFISCALE = DataType.IT_CODICEFISCALE;
    public static final DataType JOB_TITLE = DataType.JOB_TITLE;
    public static final DataType JOBTITLE = DataType.JOBTITLE;
    public static final DataType JP_CN = DataType.JP_CN;
    public static final DataType JP_IN = DataType.JP_IN;
    public static final DataType JWKS = DataType.JWKS;
    public static final DataType JWT = DataType.JWT;
    public static final DataType JWT_ATTACK = DataType.JWT_ATTACK;
    public static final DataType KPP = DataType.KPP;
    public static final DataType KR_BRN = DataType.KR_BRN;
    public static final DataType KR_RRN = DataType.KR_RRN;
    public static final DataType KYC_DOCUMENT_TYPE = DataType.KYC_DOCUMENT_TYPE;
    public static final DataType LASTNAME = DataType.LASTNAME;
    public static final DataType LATITUDE = DataType.LATITUDE;
    public static final DataType LEI = DataType.LEI;
    public static final DataType LICENSE = DataType.LICENSE;
    public static final DataType LIQUIDITY_POOL_ID = DataType.LIQUIDITY_POOL_ID;
    public static final DataType LIQUIDITY_POOL_ID_MASKED = DataType.LIQUIDITY_POOL_ID_MASKED;
    public static final DataType LOAN_TYPE = DataType.LOAN_TYPE;
    public static final DataType LONGITUDE = DataType.LONGITUDE;
    public static final DataType LORA_PACKET = DataType.LORA_PACKET;
    public static final DataType LT_ASMENS = DataType.LT_ASMENS;
    public static final DataType MAC_ADDRESS = DataType.MAC_ADDRESS;
    public static final DataType MARKET_TICK = DataType.MARKET_TICK;
    public static final DataType MERSIS = DataType.MERSIS;
    public static final DataType MIC = DataType.MIC;
    public static final DataType MICR_LINE = DataType.MICR_LINE;
    public static final DataType MICR_LINE_MASKED = DataType.MICR_LINE_MASKED;
    public static final DataType MNEMONIC = DataType.MNEMONIC;
    public static final DataType MORTGAGE_RATE = DataType.MORTGAGE_RATE;
    public static final DataType MORTGAGE_RATE_MASKED = DataType.MORTGAGE_RATE_MASKED;
    public static final DataType MORTGAGE_TERM = DataType.MORTGAGE_TERM;
    public static final DataType MQTT_PAYLOAD = DataType.MQTT_PAYLOAD;
    public static final DataType MRZ_TD1 = DataType.MRZ_TD1;
    public static final DataType MRZ_TD3 = DataType.MRZ_TD3;
    public static final DataType MSISDN = DataType.MSISDN;
    public static final DataType MT940 = DataType.MT940;
    public static final DataType MX_CURP = DataType.MX_CURP;
    public static final DataType MX_RFC = DataType.MX_RFC;
    public static final DataType MY_NRIC = DataType.MY_NRIC;
    public static final DataType NACHA_ACH = DataType.NACHA_ACH;
    public static final DataType NATIONALID = DataType.NATIONALID;
    public static final DataType NATIONALITY = DataType.NATIONALITY;
    public static final DataType NAVMESH_PATH = DataType.NAVMESH_PATH;
    public static final DataType NDEF_TEXT = DataType.NDEF_TEXT;
    public static final DataType NDEF_URI = DataType.NDEF_URI;
    public static final DataType NFC_ATQA = DataType.NFC_ATQA;
    public static final DataType NFC_SAK = DataType.NFC_SAK;
    public static final DataType NFC_TAG = DataType.NFC_TAG;
    public static final DataType NFC_UID = DataType.NFC_UID;
    public static final DataType NFT_TOKEN_ID = DataType.NFT_TOKEN_ID;
    public static final DataType NHS_NUMBER = DataType.NHS_NUMBER;
    public static final DataType NHSNUMBER = DataType.NHSNUMBER;
    public static final DataType NIN = DataType.NIN;
    public static final DataType NL_BSN = DataType.NL_BSN;
    public static final DataType NMEA_GPGGA = DataType.NMEA_GPGGA;
    public static final DataType NMEA_GPRMC = DataType.NMEA_GPRMC;
    public static final DataType NO_FODSELSNUMMER = DataType.NO_FODSELSNUMMER;
    public static final DataType NPI = DataType.NPI;
    public static final DataType NSIN = DataType.NSIN;
    public static final DataType NZ_IRD = DataType.NZ_IRD;
    public static final DataType OBD2_RESPONSE = DataType.OBD2_RESPONSE;
    public static final DataType OCCUPATION = DataType.OCCUPATION;
    public static final DataType OGRN = DataType.OGRN;
    public static final DataType OHLCV_CANDLES = DataType.OHLCV_CANDLES;
    public static final DataType OIDC_TOKEN = DataType.OIDC_TOKEN;
    public static final DataType OIDC_TOKEN_SET = DataType.OIDC_TOKEN_SET;
    public static final DataType ONBOARDING_METHOD = DataType.ONBOARDING_METHOD;
    public static final DataType OPENMETRICS_SNAPSHOT = DataType.OPENMETRICS_SNAPSHOT;
    public static final DataType OPTION_CONTRACT = DataType.OPTION_CONTRACT;
    public static final DataType ORDER_ID = DataType.ORDER_ID;
    public static final DataType PAIN001 = DataType.PAIN001;
    public static final DataType PASSPORT = DataType.PASSPORT;
    public static final DataType PASSWORD = DataType.PASSWORD;
    public static final DataType PASSWORD_HASH = DataType.PASSWORD_HASH;
    public static final DataType PAST_DATE = DataType.PAST_DATE;
    public static final DataType PAST_DATETIME = DataType.PAST_DATETIME;
    public static final DataType PATRONYMIC = DataType.PATRONYMIC;
    public static final DataType PAYE = DataType.PAYE;
    public static final DataType PAYMENT_REFERENCE = DataType.PAYMENT_REFERENCE;
    public static final DataType PAYMENT_REFERENCE_MASKED = DataType.PAYMENT_REFERENCE_MASKED;
    public static final DataType PCAP_HEX = DataType.PCAP_HEX;
    public static final DataType PEP_STATUS = DataType.PEP_STATUS;
    public static final DataType PHONE = DataType.PHONE;
    public static final DataType PHONE_AREA = DataType.PHONE_AREA;
    public static final DataType PHONE_COUNTRY = DataType.PHONE_COUNTRY;
    public static final DataType PHONE_LOCAL = DataType.PHONE_LOCAL;
    public static final DataType PIN = DataType.PIN;
    public static final DataType PIN_BLOCK = DataType.PIN_BLOCK;
    public static final DataType PIN_BLOCK_FMT3 = DataType.PIN_BLOCK_FMT3;
    public static final DataType PK_CNIC = DataType.PK_CNIC;
    public static final DataType PL_PESEL = DataType.PL_PESEL;
    public static final DataType PLATE = DataType.PLATE;
    public static final DataType PNR_CODE = DataType.PNR_CODE;
    public static final DataType POLICY_NUMBER = DataType.POLICY_NUMBER;
    public static final DataType POLICY_NUMBER_MASKED = DataType.POLICY_NUMBER_MASKED;
    public static final DataType PORT_NUMBER = DataType.PORT_NUMBER;
    public static final DataType PORTFOLIO_ID = DataType.PORTFOLIO_ID;
    public static final DataType PORTFOLIO_ID_MASKED = DataType.PORTFOLIO_ID_MASKED;
    public static final DataType POS_RECEIPT = DataType.POS_RECEIPT;
    public static final DataType POSTALCODE = DataType.POSTALCODE;
    public static final DataType PREMIUM_AMOUNT = DataType.PREMIUM_AMOUNT;
    public static final DataType PREMIUM_AMOUNT_MASKED = DataType.PREMIUM_AMOUNT_MASKED;
    public static final DataType PRIVATE_IP = DataType.PRIVATE_IP;
    public static final DataType PRODUCT_NAME = DataType.PRODUCT_NAME;
    public static final DataType PROMETHEUS_METRICS = DataType.PROMETHEUS_METRICS;
    public static final DataType PSD2_CONSENT = DataType.PSD2_CONSENT;
    public static final DataType PT_CC = DataType.PT_CC;
    public static final DataType PUBLIC_IP = DataType.PUBLIC_IP;
    public static final DataType QUATERNION = DataType.QUATERNION;
    public static final DataType RATING = DataType.RATING;
    public static final DataType REQUESTID = DataType.REQUESTID;
    public static final DataType REVERSE_REGEX = DataType.REVERSE_REGEX;
    public static final DataType RFID_TAG = DataType.RFID_TAG;
    public static final DataType RFID_UID = DataType.RFID_UID;
    public static final DataType RIC = DataType.RIC;
    public static final DataType RO_CNP = DataType.RO_CNP;
    public static final DataType RO_CUI = DataType.RO_CUI;
    public static final DataType ROUTING_NUMBER = DataType.ROUTING_NUMBER;
    public static final DataType RVN = DataType.RVN;
    public static final DataType SANCTIONS_HIT = DataType.SANCTIONS_HIT;
    public static final DataType SAR_NUMBER = DataType.SAR_NUMBER;
    public static final DataType SAR_NUMBER_MASKED = DataType.SAR_NUMBER_MASKED;
    public static final DataType SE_PERSONNUMMER = DataType.SE_PERSONNUMMER;
    public static final DataType SEDOL = DataType.SEDOL;
    public static final DataType SEPA_MANDATE = DataType.SEPA_MANDATE;
    public static final DataType SEPA_QR = DataType.SEPA_QR;
    public static final DataType SEPA_REF = DataType.SEPA_REF;
    public static final DataType SESSIONID = DataType.SESSIONID;
    public static final DataType SETTLEMENT_DATE = DataType.SETTLEMENT_DATE;
    public static final DataType SG_UEN = DataType.SG_UEN;
    public static final DataType SGK = DataType.SGK;
    public static final DataType SIGNATURE = DataType.SIGNATURE;
    public static final DataType SIREN = DataType.SIREN;
    public static final DataType SIRET = DataType.SIRET;
    public static final DataType SKU = DataType.SKU;
    public static final DataType SLUG = DataType.SLUG;
    public static final DataType SNILS = DataType.SNILS;
    public static final DataType SOL_WALLET = DataType.SOL_WALLET;
    public static final DataType SORT_CODE = DataType.SORT_CODE;
    public static final DataType SSN = DataType.SSN;
    public static final DataType SSN_MASKED = DataType.SSN_MASKED;
    public static final DataType STOCK_EXCHANGE = DataType.STOCK_EXCHANGE;
    public static final DataType STOCK_TICKER = DataType.STOCK_TICKER;
    public static final DataType SWIFT = DataType.SWIFT;
    public static final DataType SWIFT_MT103 = DataType.SWIFT_MT103;
    public static final DataType TAX_RATE = DataType.TAX_RATE;
    public static final DataType TAXID = DataType.TAXID;
    public static final DataType TAXRATE = DataType.TAXRATE;
    public static final DataType TCKN = DataType.TCKN;
    public static final DataType TCKN_MASKED = DataType.TCKN_MASKED;
    public static final DataType TH_PIN = DataType.TH_PIN;
    public static final DataType TH_TIN = DataType.TH_TIN;
    public static final DataType TIME_ONLY = DataType.TIME_ONLY;
    public static final DataType TIMESTAMP = DataType.TIMESTAMP;
    public static final DataType TIMESTAMP_ISO = DataType.TIMESTAMP_ISO;
    public static final DataType TIMEZONE = DataType.TIMEZONE;
    public static final DataType TLD = DataType.TLD;
    public static final DataType TLE_SATELLITE = DataType.TLE_SATELLITE;
    public static final DataType TOTP_CODE = DataType.TOTP_CODE;
    public static final DataType TPP_ID = DataType.TPP_ID;
    public static final DataType TRACK1_DATA = DataType.TRACK1_DATA;
    public static final DataType TRACK2_DATA = DataType.TRACK2_DATA;
    public static final DataType TRACKING_NUMBER = DataType.TRACKING_NUMBER;
    public static final DataType TRANSACTION = DataType.TRANSACTION;
    public static final DataType TRANSACTION_DESCRIPTION = DataType.TRANSACTION_DESCRIPTION;
    public static final DataType TRANSACTION_DESCRIPTION_MASKED = DataType.TRANSACTION_DESCRIPTION_MASKED;
    public static final DataType TRANSACTION_ID = DataType.TRANSACTION_ID;
    public static final DataType TRANSACTION_TYPE = DataType.TRANSACTION_TYPE;
    public static final DataType TVA = DataType.TVA;
    public static final DataType TX_HASH = DataType.TX_HASH;
    public static final DataType UBL_INVOICE = DataType.UBL_INVOICE;
    public static final DataType UBO_OWNERSHIP_PERCENTAGE = DataType.UBO_OWNERSHIP_PERCENTAGE;
    public static final DataType UBO_OWNERSHIP_PERCENTAGE_MASKED = DataType.UBO_OWNERSHIP_PERCENTAGE_MASKED;
    public static final DataType UPCA = DataType.UPCA;
    public static final DataType URI_PATH = DataType.URI_PATH;
    public static final DataType URL = DataType.URL;
    public static final DataType USERAGENT = DataType.USERAGENT;
    public static final DataType USERNAME = DataType.USERNAME;
    public static final DataType UST_ID = DataType.UST_ID;
    public static final DataType USTID = DataType.USTID;
    public static final DataType UTR = DataType.UTR;
    public static final DataType UUID = DataType.UUID;
    public static final DataType VAT_NUMBER = DataType.VAT_NUMBER;
    public static final DataType VEHICLE = DataType.VEHICLE;
    public static final DataType VIN = DataType.VIN;
    public static final DataType VKN = DataType.VKN;
    public static final DataType WALLET_LABEL = DataType.WALLET_LABEL;
    public static final DataType WEBAUTHN_CREDENTIAL = DataType.WEBAUTHN_CREDENTIAL;
    public static final DataType WEBHOOK_SIGNATURE = DataType.WEBHOOK_SIGNATURE;
    public static final DataType WEIGHT = DataType.WEIGHT;
    public static final DataType WIRE_ROUTING_NUMBER = DataType.WIRE_ROUTING_NUMBER;
    public static final DataType X509_CERT = DataType.X509_CERT;
    public static final DataType XMLDSIG = DataType.XMLDSIG;
    public static final DataType YKN = DataType.YKN;
    public static final DataType ZA_IDNR = DataType.ZA_IDNR;
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
     * String masked = MockJutsu.masker(DataType.CARDNUM, "4532015112830366");
     * }</pre>
     *
     * @param type  the data type enum constant
     * @param value the raw value to mask
     * @return the masked value, or the original value if no masking rule exists
     * @since 1.0.0
     */
    public static String masker(DataType type, String value) {
        return Masker.mask(type.key(), value);
    }

    // ── Generate + Mask ──────────────────────────────────────────────────────

    /**
     * Generates a mock value and immediately masks it — equivalent to CLI {@code --mask} flag.
     *
     * <p>Combines {@link #generate(DataType, MockJutsuLocale)} and {@link #mask(String, String)}
     * in a single call. The generated value is algorithmically valid before masking.
     *
     * <p>Example:
     * <pre>{@code
     * MockJutsu.generateMasked(DataType.TCKN,    TR);  // → "47*******83"   (KVKK)
     * MockJutsu.generateMasked(DataType.CARDNUM, TR);  // → "4532 01****** 0366"  (PCI DSS)
     * MockJutsu.generateMasked(DataType.IBAN,    DE);  // → "DE89 **** **** **** 1326"  (SEPA)
     * MockJutsu.generateMasked(DataType.EMAIL,   TR);  // → "jo***@example.com"  (GDPR)
     * }</pre>
     *
     * @param type   the data type enum constant
     * @param locale the locale enum constant; defaults to {@code US} when {@code null}
     * @return a masked version of a freshly generated value
     * @since 1.0.0
     */
    public static String generateMasked(DataType type, MockJutsuLocale locale) {
        String value = Registry.generate(type.key(), locale == null ? "US" : locale.code());
        return Masker.mask(type.key(), value);
    }

    /**
     * Generates a mock value and immediately masks it using a string type key.
     *
     * @param type   the type key string (e.g. {@code "tckn"}, {@code "cardnum"})
     * @param locale the locale string (e.g. {@code "TR"}, {@code "US"})
     * @return a masked version of a freshly generated value
     * @since 1.0.0
     */
    public static String generateMasked(String type, String locale) {
        String value = Registry.generate(type, locale == null ? "US" : locale);
        return Masker.mask(type, value);
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
    public static String masker(String type, String value) {
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
    // ── Generic builders — tüm tipler için fluent API ────────────────────────────────────────
    public static AccountTypeBuilder accountType() { return new AccountTypeBuilder(); }

    /**
     * Generates an account type with a specific {@link AccountType}.
     * <p>Available: {@link AccountType#CHECKING}, {@link AccountType#SAVINGS},
     * {@link AccountType#CURRENT}, {@link AccountType#BUSINESS_CHECKING},
     * {@link AccountType#MONEY_MARKET}, {@link AccountType#CD}, {@link AccountType#INVESTMENT}.
     */
    public static String generate(DataType type, MockJutsuLocale locale, AccountType accountType) {
        return Registry.generate(type.key(), locale.code(), accountType.value());
    }


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

    /**
     * Fluent builder for account type generation.
     *
     * <pre>{@code
     * String t = MockJutsu.accountType().locale(CA).type(AccountType.CHECKING).generate();
     * String t = MockJutsu.accountType().locale(TR).generate(); // random
     * }</pre>
     */
    public static final class AccountTypeBuilder extends BaseBuilder<AccountTypeBuilder> {
        private AccountType type = null;

        /**
         * Sets the account type. If not set, a random type is returned.
         * <p>Available: {@link AccountType#CHECKING}, {@link AccountType#SAVINGS},
         * {@link AccountType#CURRENT}, {@link AccountType#BUSINESS_CHECKING},
         * {@link AccountType#MONEY_MARKET}, {@link AccountType#CD}, {@link AccountType#INVESTMENT}.
         */
        public AccountTypeBuilder type(AccountType type) { this.type = type; return this; }

        @Override protected String typeName() { return "account_type"; }

        @Override protected String qualifier() { return type != null ? type.value() : ""; }
    }
}
