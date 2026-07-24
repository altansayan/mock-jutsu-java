package io.github.altansayan.mockjutsu.generators;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Compliance / AML / KYC data generator. Mirrors compliance.py. */
public final class ComplianceGen {

    private ComplianceGen() {}

    private static final String[] PEP_STATUSES      = {"Not PEP", "PEP", "RCA", "Former PEP", "Unknown"};
    private static final String[] AML_RATINGS        = {"Low","Medium","High","Critical"};
    private static final String[] CDD_LEVELS         = {"Standard","Enhanced","Simplified"};
    private static final String[] KYC_DOC_TYPES      = {
        "Passport", "National ID", "Driver's License", "Residence Permit",
        "Tax ID", "Utility Bill", "Bank Statement", "Birth Certificate"
    };
    private static final String[] ONBOARDING_METHODS = {"eKYC", "Video KYC", "In-Branch", "Document Upload", "Biometric", "Agent"};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "policy_number"         -> policyNumber(rng);
            case "claim_number"          -> claimNumber(rng);
            case "pep_status"            -> pick(rng, PEP_STATUSES);
            case "aml_risk_rating"       -> pick(rng, AML_RATINGS);
            case "cdd_level"             -> pick(rng, CDD_LEVELS);
            case "sar_number"            -> sarNumber(rng);
            case "ubo_ownership_percentage" -> uboOwnershipPct(rng);
            case "kyc_document_type"     -> pick(rng, KYC_DOC_TYPES);
            case "consent_id"            -> consentId(rng);
            case "tpp_id"                -> tppId(rng);
            case "onboarding_method"     -> pick(rng, ONBOARDING_METHODS);
            case "sanctions_hit"         -> rng.nextDouble() < 0.05 ? "True" : "False";
            default                      -> "ERROR: Unknown compliance type '" + type + "'";
        };
    }

    private static String policyNumber(ThreadLocalRandom rng) {
        String date8 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "POL-" + date8 + "-" + rng.nextInt(10000, 100000);
    }

    private static String claimNumber(ThreadLocalRandom rng) {
        String date8 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "CLM-" + date8 + "-" + rng.nextInt(10000, 100000);
    }

    private static String sarNumber(ThreadLocalRandom rng) {
        String date8 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "SAR-" + date8 + "-" + rng.nextInt(1000, 1000000);
    }

    private static String uboOwnershipPct(ThreadLocalRandom rng) {
        int tier = rng.nextInt(10);
        double v;
        if (tier <= 3) v = 0.01 + rng.nextDouble() * 24.98;
        else if (tier <= 7) v = 25.0 + rng.nextDouble() * 25.99;
        else if (tier <= 8) v = 51.0 + rng.nextDouble() * 48.99;
        else v = 100.00;
        v = Math.round(v * 100.0) / 100.0;
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private static String consentId(ThreadLocalRandom rng) {
        if (rng.nextDouble() < 0.8) {
            return UUID.randomUUID().toString();
        }
        String suffix = UUID.randomUUID().toString().replace("-", "")
            .toUpperCase(java.util.Locale.ROOT).substring(0, 12);
        return "CONSENT-" + suffix;
    }

    private static String tppId(ThreadLocalRandom rng) {
        String prefix = rng.nextBoolean() ? "PSP" : "TPP";
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return prefix + "-" + sb;
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
