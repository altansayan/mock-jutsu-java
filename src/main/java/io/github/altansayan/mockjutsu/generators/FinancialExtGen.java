package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Extended financial attributes — credit, insurance, loan. Mirrors financial.py extended section. */
public final class FinancialExtGen {

    private FinancialExtGen() {}

    private static final String[] CREDIT_MODELS  = {"FICO","VantageScore","TransUnion","Equifax","Experian"};
    private static final String[] CREDIT_TIERS   = {"Exceptional","Very Good","Good","Fair","Poor"};
    private static final String[] LOAN_TYPES     = {"Personal","Mortgage","Auto","Student","Business","Home Equity","Payday"};
    private static final String[] CLAIM_STATUSES = {"Submitted","Under Review","Approved","Denied","Paid","Closed","Appealed"};
    private static final int[] MORTGAGE_TERMS    = {10, 15, 20, 25, 30};
    private static final int[] DEDUCTIBLE_STEPS  = {100, 250, 500, 750, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000};
    private static final int[] COVERAGE_TIERS    = {10000, 25000, 50000, 100000, 250000, 500000, 1000000, 2000000, 5000000};

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "credit_score_model"    -> pick(rng, CREDIT_MODELS);
            case "credit_score_tier"     -> pick(rng, CREDIT_TIERS);
            case "credit_limit"          -> creditLimit(rng);
            case "credit_utilization"    -> String.format(java.util.Locale.US, "%.2f", rng.nextInt(10001) / 100.0);
            case "credit_card_issuer_name" -> FinancialGen.issuer(rng, locale);
            case "apr"                   -> String.format(java.util.Locale.US, "%.2f", 3.99 + rng.nextInt(2601) / 100.0);
            case "loan_type"             -> pick(rng, LOAN_TYPES);
            case "mortgage_rate"         -> String.format(java.util.Locale.US, "%.2f", 1.50 + rng.nextInt(1051) / 100.0);
            case "mortgage_term"         -> String.valueOf(MORTGAGE_TERMS[rng.nextInt(MORTGAGE_TERMS.length)]);
            case "premium_amount"        -> String.format(java.util.Locale.US, "%.2f", 25 + rng.nextInt(247601) / 100.0);
            case "deductible"            -> String.format(java.util.Locale.US, "%.2f", (double) DEDUCTIBLE_STEPS[rng.nextInt(DEDUCTIBLE_STEPS.length)]);
            case "coverage_limit"        -> String.format(java.util.Locale.US, "%.2f", (double) COVERAGE_TIERS[rng.nextInt(COVERAGE_TIERS.length)]);
            case "claim_status"          -> pick(rng, CLAIM_STATUSES);
            default -> "ERROR: Unknown financial_ext type '" + type + "'";
        };
    }

    private static String creditLimit(ThreadLocalRandom rng) {
        int tier = rng.nextInt(10);
        int v;
        if (tier <= 3) v = 500 + rng.nextInt(4501);
        else if (tier <= 8) v = 5000 + rng.nextInt(25001);
        else v = 30000 + rng.nextInt(70001);
        return String.format(java.util.Locale.US, "%.2f", (double) v);
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
