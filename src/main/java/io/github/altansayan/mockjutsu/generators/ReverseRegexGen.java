package io.github.altansayan.mockjutsu.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ReverseRegex — parses a regex pattern into an AST and generates a random
 * string that matches it. Mirrors reverse_regex.py's sre_parse-based engine
 * with a hand-rolled recursive-descent parser (Java has no public regex AST).
 *
 * Supported constructs: literals, '.', character classes '[...]' with ranges
 * and negation, shorthand classes \d \D \w \W \s \S, quantifiers * + ? {n} {n,} {n,m},
 * groups '(...)' / '(?:...)', alternation 'a|b', anchors ^ $ (no-op).
 */
public final class ReverseRegexGen {
    private ReverseRegexGen() {}

    private static final int MAX_UNBOUNDED = 8;

    private static final char[] PRINTABLE = chars(33, 126);          // visible ASCII, no space
    private static final char[] PRINTABLE_SPACE = chars(32, 126);    // includes space
    private static final char[] DIGITS = chars('0', '9');
    private static final char[] UPPER = chars('A', 'Z');
    private static final char[] LOWER = chars('a', 'z');
    private static final char[] WORD = concat(concat(UPPER, LOWER), concat(DIGITS, new char[]{'_'}));
    private static final char[] SPACE = {' ', '\t'};
    private static final char[] NON_DIGIT = exclude(PRINTABLE_SPACE, DIGITS);
    private static final char[] NON_WORD = exclude(PRINTABLE_SPACE, WORD);
    private static final char[] NON_SPACE = PRINTABLE;

    private static final String[] PRESETS = {
        "\\d{3}-\\d{4}",
        "[A-Z]{2}\\d{6}",
        "[A-Fa-f0-9]{8}",
        "\\d{2}/\\d{2}/\\d{4}",
        "[A-Z][a-z]{4,8}",
        "\\w{8}",
        "[A-Z0-9]{4}-[A-Z0-9]{4}",
        "v\\d+\\.\\d+\\.\\d+",
        "[A-Z]{3}\\d{4}",
        "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}",
    };

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        String pattern = (qualifier == null || qualifier.isEmpty())
            ? PRESETS[rng.nextInt(PRESETS.length)]
            : qualifier;
        try {
            Node ast = new Parser(pattern).parseAlternation();
            return ast.generate(rng);
        } catch (Exception e) {
            return "ERROR: Invalid pattern '" + pattern + "'";
        }
    }

    // ── AST ───────────────────────────────────────────────────────────────────

    private interface Node {
        String generate(ThreadLocalRandom rng);
    }

    private record Literal(char c) implements Node {
        public String generate(ThreadLocalRandom rng) { return String.valueOf(c); }
    }

    private record AnyChar() implements Node {
        public String generate(ThreadLocalRandom rng) { return String.valueOf(pick(rng, PRINTABLE)); }
    }

    private record Anchor() implements Node {
        public String generate(ThreadLocalRandom rng) { return ""; }
    }

    private record Sequence(List<Node> items) implements Node {
        public String generate(ThreadLocalRandom rng) {
            StringBuilder sb = new StringBuilder();
            for (Node n : items) sb.append(n.generate(rng));
            return sb.toString();
        }
    }

    private record Alternation(List<Node> branches) implements Node {
        public String generate(ThreadLocalRandom rng) {
            return branches.get(rng.nextInt(branches.size())).generate(rng);
        }
    }

    private record Repeat(int lo, int hi, Node child) implements Node {
        public String generate(ThreadLocalRandom rng) {
            int effHi = (hi < 0) ? lo + MAX_UNBOUNDED : Math.min(hi, lo + MAX_UNBOUNDED);
            int count = (lo >= effHi) ? lo : rng.nextInt(lo, effHi + 1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) sb.append(child.generate(rng));
            return sb.toString();
        }
    }

    private record CharClass(boolean negate, char[] pool) implements Node {
        public String generate(ThreadLocalRandom rng) {
            char[] effective = negate ? exclude(PRINTABLE, pool) : pool;
            return effective.length == 0 ? "?" : String.valueOf(pick(rng, effective));
        }
    }

    // ── Parser (recursive descent) ────────────────────────────────────────────

    private static final class Parser {
        private final String s;
        private int pos;

        Parser(String pattern) { this.s = pattern; this.pos = 0; }

        private boolean has() { return pos < s.length(); }
        private char peek() { return s.charAt(pos); }
        private char next() { return s.charAt(pos++); }

        Node parseAlternation() {
            List<Node> branches = new ArrayList<>();
            branches.add(parseSequence());
            while (has() && peek() == '|') {
                next();
                branches.add(parseSequence());
            }
            return branches.size() == 1 ? branches.get(0) : new Alternation(branches);
        }

        private Node parseSequence() {
            List<Node> items = new ArrayList<>();
            while (has() && peek() != '|' && peek() != ')') {
                items.add(parseQuantified());
            }
            return new Sequence(items);
        }

        private Node parseQuantified() {
            Node atom = parseAtom();
            if (!has()) return atom;
            char c = peek();
            int lo, hi;
            if (c == '*') { next(); lo = 0; hi = -1; }
            else if (c == '+') { next(); lo = 1; hi = -1; }
            else if (c == '?') { next(); lo = 0; hi = 1; }
            else if (c == '{') {
                int save = pos;
                Integer[] range = tryParseBraceRange();
                if (range == null) { pos = save; return atom; }
                lo = range[0]; hi = range[1];
            } else {
                return atom;
            }
            // consume non-greedy marker if present (semantics ignored — count still random)
            if (has() && peek() == '?') next();
            return new Repeat(lo, hi, atom);
        }

        private Integer[] tryParseBraceRange() {
            if (peek() != '{') return null;
            int start = pos;
            next();
            StringBuilder loSb = new StringBuilder();
            while (has() && Character.isDigit(peek())) loSb.append(next());
            int lo, hi;
            if (has() && peek() == ',') {
                next();
                StringBuilder hiSb = new StringBuilder();
                while (has() && Character.isDigit(peek())) hiSb.append(next());
                if (!has() || peek() != '}') { pos = start; return null; }
                next();
                if (loSb.isEmpty()) return null;
                lo = Integer.parseInt(loSb.toString());
                hi = hiSb.isEmpty() ? -1 : Integer.parseInt(hiSb.toString());
            } else {
                if (!has() || peek() != '}' || loSb.isEmpty()) { pos = start; return null; }
                next();
                lo = hi = Integer.parseInt(loSb.toString());
            }
            return new Integer[]{lo, hi};
        }

        private Node parseAtom() {
            char c = next();
            if (c == '^' || c == '$') return new Anchor();
            if (c == '.') return new AnyChar();
            if (c == '(') {
                if (has() && peek() == '?') {
                    next(); // consume '?'
                    if (has() && (peek() == ':')) next(); // non-capturing ':'
                    // named groups / lookaround not supported — best-effort: skip modifier char
                }
                Node inner = parseAlternation();
                if (has() && peek() == ')') next();
                return inner;
            }
            if (c == '[') return parseCharClass();
            if (c == '\\') return parseEscape();
            return new Literal(c);
        }

        private Node parseEscape() {
            if (!has()) return new Literal('\\');
            char e = next();
            return switch (e) {
                case 'd' -> new CharClass(false, DIGITS);
                case 'D' -> new CharClass(false, NON_DIGIT);
                case 'w' -> new CharClass(false, WORD);
                case 'W' -> new CharClass(false, NON_WORD);
                case 's' -> new CharClass(false, SPACE);
                case 'S' -> new CharClass(false, NON_SPACE);
                case 'n' -> new Literal('\n');
                case 't' -> new Literal('\t');
                case 'r' -> new Literal('\r');
                default -> new Literal(e);
            };
        }

        private Node parseCharClass() {
            boolean negate = false;
            if (has() && peek() == '^') { negate = true; next(); }
            List<Character> pool = new ArrayList<>();
            boolean first = true;
            while (has() && (peek() != ']' || first)) {
                first = false;
                char c = next();
                if (c == '\\' && has()) {
                    char e = next();
                    char[] catPool = switch (e) {
                        case 'd' -> DIGITS; case 'D' -> NON_DIGIT;
                        case 'w' -> WORD; case 'W' -> NON_WORD;
                        case 's' -> SPACE; case 'S' -> NON_SPACE;
                        case 'n' -> new char[]{'\n'}; case 't' -> new char[]{'\t'};
                        default -> new char[]{e};
                    };
                    for (char pc : catPool) pool.add(pc);
                    continue;
                }
                if (has() && peek() == '-' && pos + 1 < s.length() && s.charAt(pos + 1) != ']') {
                    next(); // consume '-'
                    char end = next();
                    for (char r = c; r <= end && (33 <= r && r <= 126); r++) pool.add(r);
                } else {
                    pool.add(c);
                }
            }
            if (has() && peek() == ']') next();
            char[] arr = new char[pool.size()];
            for (int i = 0; i < arr.length; i++) arr[i] = pool.get(i);
            return new CharClass(negate, arr);
        }
    }

    // ── Char-pool helpers ──────────────────────────────────────────────────────

    private static char[] chars(int lo, int hi) {
        char[] out = new char[hi - lo + 1];
        for (int i = 0; i < out.length; i++) out[i] = (char) (lo + i);
        return out;
    }

    private static char[] concat(char[] a, char[] b) {
        char[] out = new char[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    private static char[] exclude(char[] pool, char[] excluded) {
        StringBuilder sb = new StringBuilder();
        outer:
        for (char c : pool) {
            for (char e : excluded) if (c == e) continue outer;
            sb.append(c);
        }
        char[] out = new char[sb.length()];
        sb.getChars(0, sb.length(), out, 0);
        return out;
    }

    private static char pick(ThreadLocalRandom rng, char[] pool) {
        return pool[rng.nextInt(pool.length)];
    }
}
