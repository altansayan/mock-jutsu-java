package io.github.altansayan.mockjutsu.enums;

/** Card network identifiers — mirrors Python CARD_NETWORKS keys. */
public enum Network {
    VISA("visa"),
    MC("mc"),
    AMEX("amex"),
    TROY("troy"),
    MIR("mir"),
    JCB("jcb"),
    DISCOVER("discover"),
    UNIONPAY("unionpay"),
    MAESTRO("maestro");

    private final String key;

    Network(String key) { this.key = key; }

    /** Returns the string key used internally by generators (e.g. "visa", "mc"). */
    public String key() { return key; }

    /** Parses a network string to enum, case-insensitive. Falls back to VISA if unknown. */
    public static Network of(String network) {
        if (network == null || network.isBlank()) return VISA;
        String lower = network.trim().toLowerCase();
        for (Network n : values()) {
            if (n.key.equals(lower) || n.name().equalsIgnoreCase(lower)) return n;
        }
        return VISA;
    }
}
