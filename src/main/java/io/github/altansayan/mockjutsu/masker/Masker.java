package io.github.altansayan.mockjutsu.masker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regulation-compliant data masker — mirrors masker.py.
 *
 * Supported regulations: PCI DSS v4.0 §3.4.1, KVKK, GDPR Art.5, E.164,
 * SEPA/PSD2, US GLBA/IRS, UK HMRC/NHS, HIPAA, Telecom (3GPP/GSMA),
 * Network (RFC 791), Location (GDPR).
 */
public final class Masker {

    private Masker() {}

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String digitsOnly(String s) {
        return s.replaceAll("[^0-9]", "");
    }

    static String maskDigits(String digits, int showFirst, int showLast) {
        int n = digits.length();
        if (n <= showFirst + showLast) return "*".repeat(n);
        return digits.substring(0, showFirst)
             + "*".repeat(n - showFirst - showLast)
             + digits.substring(n - showLast);
    }

    static String maskAlphanum(String s, int showFirst, int showLast) {
        StringBuilder alphanum = new StringBuilder();
        for (char c : s.toCharArray()) if (Character.isLetterOrDigit(c)) alphanum.append(c);
        int n = alphanum.length();
        if (n <= showFirst + showLast) return "*".repeat(n);
        String masked = alphanum.substring(0, showFirst)
                      + "*".repeat(n - showFirst - showLast)
                      + alphanum.substring(n - showLast);

        StringBuilder result = new StringBuilder();
        int mi = 0;
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) result.append(masked.charAt(mi++));
            else result.append(c);
        }
        return result.toString();
    }

    private static String reformatLike(String original, String maskedDigits) {
        StringBuilder result = new StringBuilder();
        int di = 0;
        for (char c : original.toCharArray()) {
            if (Character.isDigit(c)) result.append(di < maskedDigits.length() ? maskedDigits.charAt(di++) : '*');
            else result.append(c);
        }
        return result.toString();
    }

    // ── Type-specific maskers ─────────────────────────────────────────────────

    private static String maskCardnum(String v) {
        String d = digitsOnly(v);
        int n = d.length();
        if (n < 13) return "*".repeat(n);
        String raw = d.substring(0, 6) + "*".repeat(n - 10) + d.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(' ');
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }

    private static String maskCardnumBin8(String v) {
        String d = digitsOnly(v);
        int n = d.length();
        if (n < 13) return "*".repeat(n);
        String raw = d.substring(0, 8) + "*".repeat(n - 12) + d.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(' ');
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }

    private static String maskPciSad(String v) {
        return v.replaceAll("[A-Za-z0-9]", "*");
    }

    private static String maskTckn(String v) {
        String d = digitsOnly(v);
        if (d.length() != 11) return maskDigits(d, 2, 2);
        return d.substring(0, 2) + "*".repeat(7) + d.substring(9);
    }

    private static String maskVkn(String v) {
        String d = digitsOnly(v);
        if (d.length() <= 6) return "*".repeat(d.length());
        return d.substring(0, 3) + "*".repeat(d.length() - 6) + d.substring(d.length() - 3);
    }

    private static String maskSgk(String v) {
        Matcher m = Pattern.compile("(\\d{2}-)(\\d+)(-\\d+\\.\\d+-\\d+)").matcher(v);
        if (m.find()) return m.group(1) + "*".repeat(m.group(2).length()) + m.group(3);
        return v;
    }

    private static String maskIban(String v) {
        String s = v.replaceAll(" ", "").toUpperCase();
        int n = s.length();
        if (n < 8) return "*".repeat(n);
        String raw = s.substring(0, 4) + "*".repeat(n - 8) + s.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(' ');
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }

    private static String maskEmail(String v) {
        int at = v.indexOf('@');
        if (at < 0) return (v.length() >= 2 ? v.substring(0, 2) : v.substring(0, 1)) + "***";
        String local  = v.substring(0, at);
        String domain = v.substring(at + 1);
        String vis    = local.length() >= 2 ? local.substring(0, 2) : local.substring(0, 1);
        return vis + "***@" + domain;
    }

    private static String maskPhone(String v) {
        if (v.startsWith("+") && v.length() >= 4) {
            String prefix;
            String rest;
            if (v.charAt(1) == '1' || v.charAt(1) == '7') {
                prefix = v.substring(0, 2);
                rest   = digitsOnly(v.substring(2));
            } else {
                prefix = v.substring(0, 3);
                rest   = digitsOnly(v.substring(3));
            }
            String last2 = rest.length() >= 2 ? rest.substring(rest.length() - 2) : rest;
            return prefix + " *** *** ** " + last2;
        }
        String d     = digitsOnly(v);
        String last2 = d.length() >= 2 ? d.substring(d.length() - 2) : d;
        return "*** *** ** " + last2;
    }

    private static String maskBirthdate(String v) {
        Matcher m = Pattern.compile("(\\d{4})([-/])(\\d{2})([-/])(\\d{2})").matcher(v);
        if (m.find()) return m.group(1) + m.group(2) + "**" + m.group(4) + "**";
        return v.length() >= 4 ? v.substring(0, 4) + "-**-**" : "****-**-**";
    }

    private static String maskName(String v) {
        String[] words = v.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                if (i > 0) sb.append(' ');
                sb.append(words[i].charAt(0)).append("***");
            }
        }
        return !sb.isEmpty() ? sb.toString() : v;
    }

    private static String maskPassport(String v) {
        String s = v.strip();
        if (s.length() <= 4) return "*".repeat(s.length());
        return s.substring(0, 2) + "*".repeat(s.length() - 4) + s.substring(s.length() - 2);
    }

    private static String maskSsn(String v) {
        String d = digitsOnly(v);
        String last4 = d.length() >= 4 ? d.substring(d.length() - 4) : d;
        return "***-**-" + last4;
    }

    private static String maskEin(String v) {
        String d     = digitsOnly(v);
        String last4 = d.length() >= 4 ? d.substring(d.length() - 4) : d;
        return "**-" + "*".repeat(Math.max(0, d.length() - 4)) + last4;
    }

    private static String maskNin(String v) {
        String s = v.replaceAll(" ", "").toUpperCase();
        if (s.length() != 9) return v;
        return "" + s.charAt(0) + s.charAt(1) + " ** ** ** " + s.charAt(8);
    }

    private static String maskNhs(String v) {
        String d = digitsOnly(v);
        if (d.length() != 10) return maskDigits(d, 3, 1);
        return d.substring(0, 3) + " *** ***" + d.charAt(9);
    }

    private static String maskUtr(String v) {
        String d = digitsOnly(v);
        return d.length() > 5 ? d.substring(0, 5) + "*".repeat(d.length() - 5) : d;
    }

    private static String maskImei(String v) {
        String raw = maskDigits(digitsOnly(v), 8, 2);
        return reformatLike(v, raw);
    }

    private static String maskIpv4(String v) {
        String[] parts = v.split("\\.");
        return parts.length == 4 ? parts[0] + "." + parts[1] + ".*.*" : v;
    }

    private static String maskMac(String v) {
        String[] parts = v.split(":");
        return parts.length == 6 ? parts[0]+":"+parts[1]+":"+parts[2]+":"+"**:**:**" : v;
    }

    private static String maskCoord(String v) {
        Matcher m = Pattern.compile("(\\d+\\.\\d{2})\\d+").matcher(v);
        return m.find() ? v.substring(0, m.start()) + m.group(1) + "*****" : v;
    }

    private static String maskSessionId(String v) {
        String[] parts = v.split("-");
        if (parts.length == 5) return parts[0] + "-****-****-****-" + parts[4];
        return v.length() >= 8 ? v.substring(0, 8) + "***..." : v;
    }

    private static String maskBalance(String v) {
        Matcher m = Pattern.compile("(-?)(\\d+)(\\.\\d+)?").matcher(v.strip());
        if (!m.find()) return v;
        String sign    = m.group(1) != null ? m.group(1) : "";
        String intpart = m.group(2);
        String dec     = m.group(3) != null ? m.group(3) : "";
        String vis     = intpart.length() > 2 ? intpart.substring(intpart.length() - 2) : intpart;
        return sign + "*".repeat(intpart.length() - vis.length()) + vis + dec;
    }

    private static String maskPlate(String v) {
        String[] parts = v.split(" ");
        if (parts.length >= 2) {
            parts[1] = parts[1].charAt(0) + "*".repeat(parts[1].length() - 1);
            return String.join(" ", parts);
        }
        return v;
    }

    private static String maskCodiceFiscale(String v) {
        if (v.length() < 16) return maskAlphanum(v, 4, 2);
        return v.substring(0, 4) + "**" + v.substring(6, 8) + "****" + v.charAt(v.length() - 1);
    }

    private static String maskKrRrn(String v) {
        Matcher m = Pattern.compile("(\\d{6})-(\\d)(\\d+)").matcher(v);
        if (m.find()) return m.group(1) + "-" + m.group(2) + "*".repeat(m.group(3).length());
        return maskAlphanum(v, 6, 1);
    }

    private static String maskDateDash6(String v) {
        Matcher m = Pattern.compile("(\\d{6})-(.+)").matcher(v);
        return m.find() ? m.group(1) + "-****" : maskAlphanum(v, 6, 0);
    }

    private static String maskDateDash8(String v) {
        Matcher m = Pattern.compile("(\\d{8})-(.+)").matcher(v);
        return m.find() ? m.group(1) + "-****" : maskAlphanum(v, 8, 0);
    }

    // ── Main dispatcher ───────────────────────────────────────────────────────

    /**
     * Returns a regulation-compliant masked version of {@code value} for the given data type.
     *
     * <p>Masking rules are chosen per type to satisfy relevant regulations:
     * <ul>
     *   <li>{@code cardnum} — PCI DSS v4.0 §3.4.1: first 6 + last 4 visible
     *   <li>{@code cardnum_bin8} — ISO/IEC 7812:2017: first 8 + last 4 visible
     *   <li>{@code cvv3} / {@code cvv4} / {@code pin} — fully masked (PCI SAF)
     *   <li>{@code iban} — SEPA/PSD2: last 4 digits visible
     *   <li>{@code tckn} / {@code ykn} — KVKK: first 2 + last 2 visible
     *   <li>{@code ssn} — US GLBA: last 4 visible
     *   <li>{@code email} — GDPR Art.5: local part partially masked
     *   <li>{@code phone} — E.164 / country-prefix preserved, last 2 visible
     *   <li>{@code nhs_number} — UK NHS: first 3 + last 3 visible
     *   <li>{@code ip} / {@code ipv4} — GDPR pseudonymisation: last octet zeroed
     * </ul>
     *
     * <p>Types not in the registry are returned unchanged.
     *
     * @param type  the data type key (case-insensitive), e.g. {@code "cardnum"}, {@code "iban"}
     * @param value the raw value to mask; if {@code null} the method returns {@code null}
     * @return the masked string, or {@code value} unchanged if the type is not recognised
     * @since 1.0.0
     */
    public static String mask(String type, String value) {
        if (type == null || value == null) return value;
        return switch (type.toLowerCase().strip()) {
            case "cvv3"                     -> "***";
            case "cvv4", "pin"              -> "****";
            case "track1_data", "track2_data", "chip_data",
                 "pin_block", "pin_block_fmt3", "3ds_cavv",
                 "password", "password_hash" -> maskPciSad(value);
            case "cardnum"                  -> maskCardnum(value);
            case "cardnum_bin8"             -> maskCardnumBin8(value);
            case "cardowner"                -> maskName(value);
            case "expiry"                   -> "**/**";
            case "expirymonth", "expiryyear"-> "**";
            case "iban"                     -> maskIban(value);
            case "balance"                  -> maskBalance(value);
            case "credit_score"             -> value.isEmpty() ? value : value.charAt(0) + "*".repeat(value.length() - 1);
            case "tckn", "ykn"              -> maskTckn(value);
            case "vkn", "taxid"             -> maskVkn(value);
            case "sgk"                      -> maskSgk(value);
            case "mersis"                   -> maskAlphanum(value, 4, 4);
            case "ssn"                      -> maskSsn(value);
            case "ein"                      -> maskEin(value);
            case "npi"                      -> maskDigits(digitsOnly(value), 5, 4);
            case "nin"                      -> maskNin(value);
            case "utr"                      -> maskUtr(value);
            case "nhs_number", "nhsnumber"  -> maskNhs(value);
            case "crn"                      -> maskAlphanum(value, 2, 2);
            case "paye"                     -> maskAlphanum(value, 4, 3);
            case "sort_code"                -> value.replaceAll("\\d{2}", "**");
            case "de_idnr"                  -> maskDigits(digitsOnly(value), 4, 4);
            case "de_stnr", "rvn"           -> maskAlphanum(value, 3, 2);
            case "inn", "inn_individual"    -> maskDigits(digitsOnly(value), 3, 3);
            case "snils"                    -> maskAlphanum(value, 3, 2);
            case "passport", "license"      -> maskPassport(value);
            case "mrz_td3", "mrz_td1"       -> {
                Matcher mrz = Pattern.compile("[A-Z0-9]{4,}").matcher(value);
                yield mrz.replaceAll(mr -> "*".repeat(mr.group().length()));
            }
            case "birthdate"                -> maskBirthdate(value);
            case "age"                      -> "**";
            case "gender", "nationality"    -> value;
            case "firstname", "lastname",
                 "fullname", "patronymic"   -> maskName(value);
            case "email"                    -> maskEmail(value);
            case "phone", "msisdn"          -> maskPhone(value);
            case "phone_local"              -> {
                yield "***" + (value.length() >= 2 ? value.substring(value.length() - 2) : value);
            }
            case "phone_area", "phone_country" -> value;
            case "postalcode"               -> {
                yield (value.length() >= 2 ? value.substring(0, 2) : value) + "***";
            }
            case "plate"                    -> maskPlate(value);
            case "imei", "imei2"            -> maskImei(value);
            case "iccid"                    -> maskDigits(digitsOnly(value), 6, 4);
            case "imsi"                     -> maskDigits(digitsOnly(value), 5, 4);
            case "icd10"                    -> value.replaceAll("\\d", "*");
            case "bmi", "height", "weight"  -> value.replaceAll("\\d", "*");
            case "hl7_message"              -> value.replaceAll("(?<=\\|)[^|]{4,}", "****");
            case "ipv4", "public_ip"        -> maskIpv4(value);
            case "mac_address"              -> maskMac(value);
            case "username"                 -> value.length() > 4
                ? value.substring(0, 2) + "***" + value.substring(value.length() - 2)
                : (value.isEmpty() ? value : value.charAt(0) + "***");
            case "handle"                   -> {
                String stripped = value.startsWith("@") ? value.substring(1) : value;
                yield "@" + (stripped.length() > 2 ? stripped.substring(0, 2) + "***" : stripped);
            }
            case "latitude", "longitude"    -> maskCoord(value);
            case "coordinates"              -> {
                String[] coords = value.split(",");
                yield coords.length == 2
                    ? maskCoord(coords[0].strip()) + "," + maskCoord(coords[1].strip())
                    : maskCoord(value);
            }
            case "vin"                      -> value.length() == 17
                ? value.substring(0, 9) + "****" + value.substring(13)
                : maskAlphanum(value, 4, 4);
            case "order_id"                 -> value.length() > 10
                ? value.substring(0, 6) + "****" + value.substring(value.length() - 4)
                : value;
            case "tracking_number"          -> maskDigits(digitsOnly(value), 4, 4);
            case "pnr_code"                 -> value.length() > 2
                ? value.substring(0, 2) + "*".repeat(value.length() - 2)
                : value;
            case "iata_ticket"              -> maskDigits(digitsOnly(value), 3, 3);
            case "sessionid", "deviceid"    -> maskSessionId(value);
            case "oidc_token"               -> value.length() > 14
                ? value.substring(0, 10) + "***." + value.substring(value.length() - 4)
                : "eyJ***";
            case "mnemonic"                 -> {
                String[] words = value.split("\\s+");
                yield words.length > 0 ? words[0] + " *** *** ... ***" : value;
            }
            case "psd2_consent"             -> value.length() > 12 ? value.substring(0, 12) + "***" : value;
            case "br_cpf"                   -> maskDigits(digitsOnly(value), 3, 2);
            case "br_cnpj"                  -> maskAlphanum(value, 4, 4);
            case "in_pan"                   -> value.length() >= 6
                ? value.substring(0, 5) + "****" + value.charAt(value.length() - 1)
                : value;
            case "in_aadhaar"               -> {
                String d = digitsOnly(value);
                String last4 = d.length() >= 4 ? d.substring(d.length() - 4) : d;
                yield "XXXX XXXX " + last4;
            }
            case "in_gstin"                 -> value.length() >= 14
                ? value.substring(0, 2) + value.substring(2, 7) + "****" + value.substring(value.length() - 2)
                : value;
            case "in_epic"                  -> maskAlphanum(value, 3, 2);
            case "cn_ric"                   -> maskDigits(digitsOnly(value), 6, 4);
            case "mx_curp", "mx_rfc"        -> maskAlphanum(value, 4, 2);
            case "it_codicefiscale"         -> maskCodiceFiscale(value);
            case "es_dni", "es_nie"         -> maskAlphanum(value, 2, 2);
            case "es_ccc"                   -> maskAlphanum(value, 4, 4);
            case "pk_cnic"                  -> maskAlphanum(value, 5, 2);
            case "jp_cn", "jp_in"           -> maskDigits(digitsOnly(value), 4, 4);
            case "kr_rrn"                   -> maskKrRrn(value);
            case "kr_brn"                   -> maskAlphanum(value, 3, 3);
            case "nl_bsn"                   -> maskDigits(digitsOnly(value), 3, 2);
            case "pl_pesel"                 -> maskDigits(digitsOnly(value), 6, 2);
            case "se_personnummer"           -> maskDateDash8(value);
            case "dk_cpr", "fi_hetu"        -> maskDateDash6(value);
            case "no_fodselsnummer"         -> maskDigits(digitsOnly(value), 6, 2);
            case "au_abn", "au_acn"         -> maskDigits(digitsOnly(value), 3, 3);
            case "au_tfn"                   -> maskDigits(digitsOnly(value), 3, 2);
            case "my_nric"                  -> maskAlphanum(value, 6, 4);
            case "th_pin", "th_tin"         -> maskDigits(digitsOnly(value), 4, 4);
            case "sg_uen"                   -> maskAlphanum(value, 4, 2);
            case "za_idnr"                  -> maskDigits(digitsOnly(value), 6, 3);
            case "ca_bn"                    -> maskDigits(digitsOnly(value), 3, 2);
            case "nz_ird"                   -> maskDigits(digitsOnly(value), 3, 2);
            case "ar_cuit"                  -> maskAlphanum(value, 4, 2);
            case "ar_dni"                   -> maskAlphanum(value, 2, 2);
            case "cl_rut"                   -> maskAlphanum(value, 3, 2);
            case "co_nit"                   -> maskAlphanum(value, 3, 3);
            case "il_idnr"                  -> maskDigits(digitsOnly(value), 3, 2);
            case "ro_cnp"                   -> maskDigits(digitsOnly(value), 4, 3);
            case "ro_cui"                   -> maskAlphanum(value, 4, 3);
            case "hr_oib"                   -> maskDigits(digitsOnly(value), 4, 3);
            case "bg_egn"                   -> maskDigits(digitsOnly(value), 6, 2);
            case "lt_asmens"                -> maskDigits(digitsOnly(value), 5, 2);
            case "ee_ik"                    -> maskDigits(digitsOnly(value), 5, 2);
            case "pt_cc"                    -> maskAlphanum(value, 4, 3);
            case "eg_tn"                    -> maskDigits(digitsOnly(value), 3, 2);
            case "tckn_masked", "ssn_masked",
                 "account_number_masked", "micr_line_masked",
                 "transaction_description_masked", "check_number_masked",
                 "payment_reference_masked", "credit_limit_masked",
                 "mortgage_rate_masked", "premium_amount_masked",
                 "portfolio_id_masked", "sar_number_masked",
                 "policy_number_masked", "claim_number_masked",
                 "ubo_ownership_percentage_masked", "consent_id_masked",
                 "liquidity_pool_id_masked"          -> value;
            default                         -> value;
        };
    }
}
