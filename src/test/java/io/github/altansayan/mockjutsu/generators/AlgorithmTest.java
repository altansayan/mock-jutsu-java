package io.github.altansayan.mockjutsu.generators;

import io.github.altansayan.mockjutsu.MockJutsu;
import io.github.altansayan.mockjutsu.enums.MockJutsuLocale;
import io.github.altansayan.mockjutsu.enums.Network;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/** Layer-1 unit tests — individual generator algorithms + MockJutsu API. */
class AlgorithmTest {

    // ── TCKN ─────────────────────────────────────────────────────────────────

    @RepeatedTest(50)
    void tcknIsValid() {
        String tckn = IdentityGen.tckn(ThreadLocalRandom.current());
        assertEquals(11, tckn.length(), "TCKN must be 11 digits");
        assertTrue(tckn.matches("\\d{11}"), "TCKN must be all digits");
        assertNotEquals('0', tckn.charAt(0), "TCKN first digit must not be 0");

        int[] d = new int[11];
        for (int i = 0; i < 11; i++) d[i] = tckn.charAt(i) - '0';
        int odd  = d[0]+d[2]+d[4]+d[6]+d[8];
        int even = d[1]+d[3]+d[5]+d[7];
        assertEquals(d[9], ((7 * odd) - even + 100) % 10, "TCKN d9 invalid");
        int sum = 0;
        for (int i = 0; i < 10; i++) sum += d[i];
        assertEquals(d[10], sum % 10, "TCKN d10 invalid");
    }

    // ── SSN ──────────────────────────────────────────────────────────────────

    @RepeatedTest(20)
    void ssnAreaNotSpecial() {
        String ssn = IdentityGen.ssn(ThreadLocalRandom.current());
        int area = Integer.parseInt(ssn.substring(0, 3));
        assertNotEquals(666, area, "SSN area 666 is reserved");
        assertTrue(area >= 1 && area <= 899, "SSN area out of range: " + area);
    }

    // ── NIN ──────────────────────────────────────────────────────────────────

    @RepeatedTest(20)
    void ninFormatValid() {
        String nin = IdentityGen.nin(ThreadLocalRandom.current());
        assertEquals(13, nin.length(), "NIN must be 13 chars incl. spaces");
        assertTrue(nin.matches("[A-Z]{2} \\d{2} \\d{2} \\d{2} [A-D]"), "NIN format invalid: " + nin);
        char p1 = nin.charAt(0), p2 = nin.charAt(1);
        assertFalse("DFIQUV".indexOf(p1) >= 0, "NIN prefix char 1 invalid: " + p1);
        assertFalse("DFIOQUV".indexOf(p2) >= 0, "NIN prefix char 2 invalid: " + p2);
        String prefix = "" + p1 + p2;
        assertFalse(java.util.Set.of("BG","GB","NK","KN","NT","TN","ZZ").contains(prefix), "NIN forbidden prefix: " + prefix);
    }

    // ── Card number ───────────────────────────────────────────────────────────

    @RepeatedTest(50)
    void cardnumLuhnValid() {
        String card = FinancialGen.cardnum(ThreadLocalRandom.current(), "TR");
        assertTrue(isLuhnValid(card), "Card " + card + " failed Luhn");
    }

    @Test
    void cardnumVisaStartsWith4() {
        var rng = ThreadLocalRandom.current();
        for (int i = 0; i < 50; i++) {
            String card = FinancialGen.cardnum(rng, "TR", "visa");
            assertTrue(card.startsWith("4"), "Visa card must start with 4: " + card);
            assertEquals(16, card.length());
            assertTrue(isLuhnValid(card));
        }
    }

    @Test
    void cardnumMcStartsWith5() {
        var rng = ThreadLocalRandom.current();
        for (int i = 0; i < 50; i++) {
            String card = FinancialGen.cardnum(rng, "TR", "mc");
            assertTrue(card.startsWith("5"), "Mastercard must start with 5: " + card);
            assertEquals(16, card.length());
            assertTrue(isLuhnValid(card));
        }
    }

    @Test
    void cardnumAmexStartsWith3() {
        var rng = ThreadLocalRandom.current();
        for (int i = 0; i < 50; i++) {
            String card = FinancialGen.cardnum(rng, "TR", "amex");
            assertTrue(card.startsWith("3"));
            assertEquals(15, card.length());
            assertTrue(isLuhnValid(card));
        }
    }

    @Test
    void cardnumTroyStartsWith9792() {
        var rng = ThreadLocalRandom.current();
        for (int i = 0; i < 50; i++) {
            String card = FinancialGen.cardnum(rng, "TR", "troy");
            assertTrue(card.startsWith("9792"));
            assertEquals(16, card.length());
            assertTrue(isLuhnValid(card));
        }
    }

    // ── IBAN ─────────────────────────────────────────────────────────────────

    @RepeatedTest(30)
    void trIbanMod97Valid() {
        String iban = FinancialGen.iban(ThreadLocalRandom.current(), "TR");
        assertEquals("TR", iban.substring(0, 2));
        assertEquals(26, iban.length());
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        assertEquals(1, mod97(toNumeric(rearranged)), "TR IBAN MOD-97 failed: " + iban);
    }

    @RepeatedTest(20)
    void deIbanMod97Valid() {
        String iban = FinancialGen.iban(ThreadLocalRandom.current(), "DE");
        assertEquals("DE", iban.substring(0, 2));
        assertEquals(22, iban.length());
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        assertEquals(1, mod97(toNumeric(rearranged)), "DE IBAN MOD-97 failed: " + iban);
    }

    // ── IMEI ─────────────────────────────────────────────────────────────────

    @RepeatedTest(50)
    void imeiLuhnValid() {
        String imei = TelecomGen.imei(ThreadLocalRandom.current());
        assertEquals(15, imei.length());
        assertTrue(isLuhnValid(imei), "IMEI " + imei + " failed Luhn");
    }

    // ── NHS ───────────────────────────────────────────────────────────────────

    @RepeatedTest(30)
    void nhsMod11Valid() {
        String nhs = HealthGen.nhsNumber(ThreadLocalRandom.current());
        String digits = nhs.replace(" ", "");
        assertEquals(10, digits.length());
        int[] d = new int[10];
        for (int i = 0; i < 10; i++) d[i] = digits.charAt(i) - '0';
        int[] w = {10,9,8,7,6,5,4,3,2};
        int total = 0;
        for (int i = 0; i < 9; i++) total += d[i] * w[i];
        int remainder = total % 11;
        int expected  = (remainder == 0) ? 0 : 11 - remainder;
        assertEquals(expected, d[9], "NHS check digit invalid for: " + nhs);
    }

    // ── EAN-13 ───────────────────────────────────────────────────────────────

    @RepeatedTest(20)
    void ean13ChecksumValid() {
        String ean = BarcodeGen.ean13(ThreadLocalRandom.current(), "TR");
        assertEquals(13, ean.length());
        int[] d = new int[13];
        for (int i = 0; i < 13; i++) d[i] = ean.charAt(i) - '0';
        int odd = 0, even = 0;
        for (int i = 0; i < 12; i++) {
            if (i % 2 == 0) odd += d[i]; else even += d[i];
        }
        int expected = (10 - ((odd + 3 * even) % 10)) % 10;
        assertEquals(expected, d[12], "EAN-13 check digit invalid: " + ean);
    }

    // ── NMEA ─────────────────────────────────────────────────────────────────

    @RepeatedTest(20)
    void nmeaGpggaChecksumCorrect() {
        String json = NmeaGen.generate("nmea_gpgga", "TR");
        int s = json.indexOf("\"sentence\":\"") + "\"sentence\":\"".length();
        int e = json.indexOf("\",\"type\":");
        String sentence = json.substring(s, e);
        int star = sentence.lastIndexOf('*');
        String body = sentence.substring(1, star);
        String cs   = sentence.substring(star + 1);
        int expected = 0;
        for (char c : body.toCharArray()) expected ^= c;
        assertEquals(String.format("%02X", expected), cs, "NMEA checksum mismatch");
    }

    // ── MRZ ──────────────────────────────────────────────────────────────────

    @Test
    void mrzCheckDigitAlgorithm() {
        int cd = MrzGen.mrzCheck("490154203237518");
        assertTrue(cd >= 0 && cd <= 9);
    }

    @RepeatedTest(20)
    void mrzTd3Line2Is44Chars() {
        String json = MrzGen.generate("mrz_td3", "TR");
        int s = json.indexOf("\"lines\":\"") + "\"lines\":\"".length();
        int e = json.indexOf("\",\"surname\"");
        String[] parts = json.substring(s, e).split(" \\| ");
        assertEquals(44, parts[0].length());
        assertEquals(44, parts[1].length());
    }

    // ── PIN Block ─────────────────────────────────────────────────────────────

    @RepeatedTest(30)
    void pinBlockFmt0Structure() {
        String pb = HardwareGen.pinBlockFmt0(ThreadLocalRandom.current());
        assertEquals(16, pb.length());
        assertEquals('0', pb.charAt(0));
        int pinLen = pb.charAt(1) - '0';
        assertTrue(pinLen >= 4 && pinLen <= 6);
        for (int i = 2 + pinLen; i < 16; i++) assertEquals('F', pb.charAt(i));
    }

    @RepeatedTest(30)
    void pinBlockFmt3Structure() {
        String pb = HardwareGen.pinBlockFmt3(ThreadLocalRandom.current());
        assertEquals(16, pb.length());
        assertEquals('3', pb.charAt(0));
    }

    // ── EMV ───────────────────────────────────────────────────────────────────

    @RepeatedTest(20)
    void emvArqcIs16HexUppercase() {
        String arqc = CardPhysicsGen.emvArqc();
        assertEquals(16, arqc.length());
        assertTrue(arqc.matches("[0-9A-F]{16}"));
    }

    @Test
    void iso8583BitmapsCorrect() {
        String authReq = CardPhysicsGen.iso8583AuthReq(ThreadLocalRandom.current(), "TR");
        assertTrue(authReq.contains("723C448008C08000"), "Auth req bitmap mismatch");
        String authResp = CardPhysicsGen.iso8583AuthResp(ThreadLocalRandom.current(), "TR");
        assertTrue(authResp.contains("7238000006C00000"), "Auth resp bitmap mismatch");
    }

    // ── ISIN — ISO 6166 Luhn checksum ────────────────────────────────────────

    @RepeatedTest(30)
    void isinChecksumValid() {
        String isin = FinancialMarketsGen.isin(ThreadLocalRandom.current(), "US");
        assertEquals(12, isin.length(), "ISIN must be 12 chars");
        assertTrue(isin.substring(0, 2).matches("[A-Z]{2}"), "ISIN must start with 2 letters");
        assertTrue(Character.isDigit(isin.charAt(11)), "ISIN last char must be digit");

        // Expand letters to numbers, then Luhn validate full 12-char ISIN
        StringBuilder numeric = new StringBuilder();
        for (char c : isin.toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else numeric.append(c);
        }
        assertTrue(isLuhnValid(numeric.toString()), "ISIN Luhn check failed: " + isin);
    }

    // ── CUSIP — CUSIP check digit ─────────────────────────────────────────────

    @RepeatedTest(30)
    void cusipChecksumValid() {
        String cusip = FinancialMarketsGen.cusip(ThreadLocalRandom.current());
        assertEquals(9, cusip.length(), "CUSIP must be 9 chars");
        String payload = cusip.substring(0, 8);
        int expected = FinancialMarketsGen.cusipCheck(payload);
        assertEquals(expected, cusip.charAt(8) - '0', "CUSIP check digit invalid: " + cusip);
    }

    // ── SEDOL — weighted sum check ────────────────────────────────────────────

    @RepeatedTest(30)
    void sedolChecksumValid() {
        String sedol = FinancialMarketsGen.sedol(ThreadLocalRandom.current());
        assertEquals(7, sedol.length(), "SEDOL must be 7 chars");
        String payload = sedol.substring(0, 6);
        int expected = FinancialMarketsGen.sedolCheck(payload);
        assertEquals(expected, sedol.charAt(6) - '0', "SEDOL check digit invalid: " + sedol);
    }

    // ── LEI — ISO 17442 MOD-97 ───────────────────────────────────────────────

    @RepeatedTest(30)
    void leiMod97Valid() {
        String lei = FinancialMarketsGen.lei(ThreadLocalRandom.current());
        assertEquals(20, lei.length(), "LEI must be 20 chars");

        // Convert all chars to numeric (A=10…Z=35), then mod97 must equal 1
        StringBuilder numeric = new StringBuilder();
        for (char c : lei.toCharArray()) {
            if (Character.isLetter(c)) numeric.append(c - 'A' + 10);
            else numeric.append(c);
        }
        int mod = 0;
        for (int i = 0; i < numeric.length(); i++) {
            mod = (mod * 10 + (numeric.charAt(i) - '0')) % 97;
        }
        assertEquals(1, mod, "LEI MOD-97 check failed: " + lei);
    }

    // ── BTC address — Base58Check format ─────────────────────────────────────

    @RepeatedTest(20)
    void btcAddressFormatValid() {
        String btc = CryptoGen.btcAddress(ThreadLocalRandom.current());
        // P2PKH: starts with '1', length 25–34, Base58 charset only
        assertTrue(btc.startsWith("1"), "BTC P2PKH address must start with '1': " + btc);
        assertTrue(btc.length() >= 25 && btc.length() <= 34, "BTC address length invalid: " + btc.length());
        assertTrue(btc.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+"),
            "BTC address contains invalid Base58 chars: " + btc);
    }

    // ── ETH address — EIP-55 mixed-case checksum ─────────────────────────────

    @RepeatedTest(20)
    void ethAddressEip55Valid() {
        String eth = CryptoGen.ethAddress();
        assertTrue(eth.startsWith("0x"), "ETH address must start with 0x: " + eth);
        assertEquals(42, eth.length(), "ETH address must be 42 chars: " + eth);
        // Strip 0x, must be valid hex
        assertTrue(eth.substring(2).matches("[0-9a-fA-F]{40}"), "ETH address not valid hex: " + eth);
        // Mixed case = checksum address (not all lower, not all upper for non-trivial addresses)
        String hex = eth.substring(2);
        boolean hasUpper = hex.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = hex.chars().anyMatch(Character::isLowerCase);
        // A valid EIP-55 address that has at least one letter should have mixed case
        long letterCount = hex.chars().filter(Character::isLetter).count();
        if (letterCount >= 2) {
            assertTrue(hasUpper && hasLower, "EIP-55 address should be mixed case: " + eth);
        }
    }

    // ── MockJutsu API ─────────────────────────────────────────────────────────

    @Test
    void stringApiGeneratesNonEmpty() {
        assertFalse(MockJutsu.generate("tckn", "TR").isBlank());
        assertFalse(MockJutsu.generate("iban", "DE").isBlank());
        assertFalse(MockJutsu.generate("cardnum", "TR", "visa").isBlank());
        assertFalse(MockJutsu.generate("email", "US").isBlank());
    }

    @Test
    void fluentApiCardnum() {
        String card = MockJutsu.cardnum().locale(MockJutsuLocale.TR).network(Network.VISA).generate();
        assertTrue(card.startsWith("4"), "Visa via fluent API must start with 4");
        assertTrue(isLuhnValid(card));
    }

    @Test
    void fluentApiIban() {
        String iban = MockJutsu.iban().locale("TR").generate();
        assertEquals("TR", iban.substring(0, 2));
    }

    @Test
    void bulkGeneratesCorrectCount() {
        List<String> cards = MockJutsu.cardnum().locale("TR").network("mc").bulk(10);
        assertEquals(10, cards.size());
        cards.forEach(c -> assertTrue(isLuhnValid(c), "Bulk card Luhn invalid: " + c));
    }

    @Test
    void maskerCardnum() {
        String masked = MockJutsu.mask("cardnum", "4532015112830366");
        assertTrue(masked.contains("*"), "Cardnum should be masked");
        assertTrue(masked.startsWith("4532"), "Cardnum should show first 6 digits (BIN)");
    }

    @Test
    void maskerCardnumBin8() {
        String masked = MockJutsu.mask("cardnum_bin8", "4532015112830366");
        assertTrue(masked.startsWith("4532 0151"), "Cardnum BIN8 should show first 8 digits");
    }

    @Test
    void maskerEmail() {
        String masked = MockJutsu.mask("email", "john.doe@example.com");
        assertTrue(masked.contains("***@example.com"));
    }

    @Test
    void unknownTypeReturnsError() {
        String result = MockJutsu.generate("nonexistent_type_xyz", "TR");
        assertTrue(result.startsWith("ERROR:"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean isLuhnValid(String number) {
        int sum = 0;
        boolean alt = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';
            if (d < 0 || d > 9) return false;
            if (alt) { d *= 2; if (d > 9) d -= 9; }
            sum += d;
            alt = !alt;
        }
        return sum % 10 == 0;
    }

    private static String toNumeric(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c - 'A' + 10);
            else sb.append(c);
        }
        return sb.toString();
    }

    private static int mod97(String numStr) {
        int mod = 0;
        for (int i = 0; i < numStr.length(); i++) mod = (mod * 10 + (numStr.charAt(i) - '0')) % 97;
        return mod;
    }
}
