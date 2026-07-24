package io.github.altansayan.mockjutsu.generators;

import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/** UBL 2.1 E-Invoice and W3C XMLDSig generator. Mirrors ubl.py. */
public final class UblGen {
    private UblGen() {}

    private static final String[] INVOICE_TYPES = {"SATIS", "IADE", "TEVKIFAT", "IHTIYAT", "ISTISNA"};
    private static final String[] CURRENCIES = {"TRY", "EUR", "USD", "GBP"};
    private static final int[] TAX_RATES = {0, 8, 18, 20};
    private static final String[] PRODUCT_NAMES = {
        "Yazilim Lisansi", "Danismanlik Hizmeti", "Donanim Ekipmani",
        "Egitim Hizmeti", "Teknik Destek", "Proje Yonetimi",
        "Network Altyapisi", "Bulut Depolama", "API Entegrasyonu",
        "Sistem Kurulumu"
    };
    private static final String[] UNIT_CODES = {"C62", "HUR", "MTR", "KGM", "LTR", "SET", "MON"};

    private static final String C14N_ALGO   = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    private static final String SIG_ALGO    = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String DIGEST_ALGO = "http://www.w3.org/2001/04/xmlenc#sha256";
    private static final String ENV_ALGO    = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "ubl_invoice" -> ublInvoice(rng);
            case "xmldsig"     -> xmldsig(rng);
            default -> "ERROR: Unknown UBL type '" + type + "'";
        };
    }

    private static final class Line {
        int id;
        int qty;
        String unitCode;
        double unitPrice;
        double lineTotal;
        String name;
    }

    private static String ublInvoice(ThreadLocalRandom rng) {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        String invoiceId = "INV-" + now.getYear() + "-" + String.format("%05d", rng.nextInt(1, 100000));
        String invoiceUuid = java.util.UUID.randomUUID().toString().toUpperCase(java.util.Locale.ROOT);
        String issueDate = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String issueTime = now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

        String invoiceType = INVOICE_TYPES[rng.nextInt(INVOICE_TYPES.length)];
        String currency    = CURRENCIES[rng.nextInt(CURRENCIES.length)];
        int taxRate        = TAX_RATES[rng.nextInt(TAX_RATES.length)];

        int nLines = rng.nextInt(1, 6);
        java.util.List<Line> lines = new java.util.ArrayList<>();
        double netAmount = 0.0;
        for (int i = 1; i <= nLines; i++) {
            Line l = new Line();
            l.id = i;
            l.qty = rng.nextInt(1, 101);
            l.unitPrice = Math.round(rng.nextDouble(10.0, 5000.0) * 100.0) / 100.0;
            l.lineTotal = Math.round(l.qty * l.unitPrice * 100.0) / 100.0;
            l.unitCode = UNIT_CODES[rng.nextInt(UNIT_CODES.length)];
            l.name = PRODUCT_NAMES[rng.nextInt(PRODUCT_NAMES.length)];
            lines.add(l);
            netAmount += l.lineTotal;
        }
        netAmount = Math.round(netAmount * 100.0) / 100.0;
        double taxAmount = Math.round(netAmount * taxRate / 100.0 * 100.0) / 100.0;
        double grossAmount = Math.round((netAmount + taxAmount) * 100.0) / 100.0;

        String supplierVkn = IdentityGen.vkn(rng);
        String customerTckn = IdentityGen.tckn(rng);

        StringBuilder lineBlocks = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            Line l = lines.get(i);
            if (i > 0) lineBlocks.append('\n');
            lineBlocks.append("  <cac:InvoiceLine>\n")
                .append("    <cbc:ID>").append(l.id).append("</cbc:ID>\n")
                .append("    <cbc:InvoicedQuantity unitCode=\"").append(l.unitCode).append("\">").append(l.qty).append("</cbc:InvoicedQuantity>\n")
                .append("    <cbc:LineExtensionAmount currencyID=\"").append(currency).append("\">").append(fmt2(l.lineTotal)).append("</cbc:LineExtensionAmount>\n")
                .append("    <cac:Item><cbc:Name>").append(l.name).append("</cbc:Name></cac:Item>\n")
                .append("    <cac:Price><cbc:PriceAmount currencyID=\"").append(currency).append("\">").append(fmt2(l.unitPrice)).append("</cbc:PriceAmount></cac:Price>\n")
                .append("  </cac:InvoiceLine>");
        }

        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n" +
            "  xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n" +
            "  xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">\n" +
            "  <cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n" +
            "  <cbc:CustomizationID>TR1.2</cbc:CustomizationID>\n" +
            "  <cbc:ID>" + invoiceId + "</cbc:ID>\n" +
            "  <cbc:CopyIndicator>false</cbc:CopyIndicator>\n" +
            "  <cbc:UUID>" + invoiceUuid + "</cbc:UUID>\n" +
            "  <cbc:IssueDate>" + issueDate + "</cbc:IssueDate>\n" +
            "  <cbc:IssueTime>" + issueTime + "</cbc:IssueTime>\n" +
            "  <cbc:InvoiceTypeCode>" + invoiceType + "</cbc:InvoiceTypeCode>\n" +
            "  <cbc:DocumentCurrencyCode>" + currency + "</cbc:DocumentCurrencyCode>\n" +
            "  <cbc:LineCountNumeric>" + nLines + "</cbc:LineCountNumeric>\n" +
            "  <cac:AccountingSupplierParty>\n" +
            "    <cac:Party>\n" +
            "      <cac:PartyTaxScheme>\n" +
            "        <cbc:CompanyID>" + supplierVkn + "</cbc:CompanyID>\n" +
            "        <cac:TaxScheme><cbc:ID>VAT</cbc:ID></cac:TaxScheme>\n" +
            "      </cac:PartyTaxScheme>\n" +
            "    </cac:Party>\n" +
            "  </cac:AccountingSupplierParty>\n" +
            "  <cac:AccountingCustomerParty>\n" +
            "    <cac:Party>\n" +
            "      <cac:PartyIdentification>\n" +
            "        <cbc:ID schemeID=\"TCKN\">" + customerTckn + "</cbc:ID>\n" +
            "      </cac:PartyIdentification>\n" +
            "    </cac:Party>\n" +
            "  </cac:AccountingCustomerParty>\n" +
            "  <cac:TaxTotal>\n" +
            "    <cbc:TaxAmount currencyID=\"" + currency + "\">" + fmt2(taxAmount) + "</cbc:TaxAmount>\n" +
            "    <cac:TaxSubtotal>\n" +
            "      <cbc:TaxableAmount currencyID=\"" + currency + "\">" + fmt2(netAmount) + "</cbc:TaxableAmount>\n" +
            "      <cbc:TaxAmount currencyID=\"" + currency + "\">" + fmt2(taxAmount) + "</cbc:TaxAmount>\n" +
            "      <cac:TaxCategory>\n" +
            "        <cbc:Percent>" + taxRate + "</cbc:Percent>\n" +
            "        <cac:TaxScheme><cbc:ID>KDV</cbc:ID></cac:TaxScheme>\n" +
            "      </cac:TaxCategory>\n" +
            "    </cac:TaxSubtotal>\n" +
            "  </cac:TaxTotal>\n" +
            "  <cac:LegalMonetaryTotal>\n" +
            "    <cbc:LineExtensionAmount currencyID=\"" + currency + "\">" + fmt2(netAmount) + "</cbc:LineExtensionAmount>\n" +
            "    <cbc:TaxExclusiveAmount currencyID=\"" + currency + "\">" + fmt2(netAmount) + "</cbc:TaxExclusiveAmount>\n" +
            "    <cbc:TaxInclusiveAmount currencyID=\"" + currency + "\">" + fmt2(grossAmount) + "</cbc:TaxInclusiveAmount>\n" +
            "    <cbc:PayableAmount currencyID=\"" + currency + "\">" + fmt2(grossAmount) + "</cbc:PayableAmount>\n" +
            "  </cac:LegalMonetaryTotal>\n" +
            lineBlocks + "\n" +
            "</Invoice>";

        StringBuilder json = new StringBuilder("{");
        json.append("\"xml\":\"").append(jsonEscape(xml)).append("\",");
        json.append("\"invoice_id\":\"").append(invoiceId).append("\",");
        json.append("\"uuid\":\"").append(invoiceUuid).append("\",");
        json.append("\"issue_date\":\"").append(issueDate).append("\",");
        json.append("\"currency\":\"").append(currency).append("\",");
        json.append("\"invoice_type\":\"").append(invoiceType).append("\",");
        json.append("\"tax_rate\":").append(taxRate).append(",");
        json.append("\"net_amount\":").append(fmt2(netAmount)).append(",");
        json.append("\"tax_amount\":").append(fmt2(taxAmount)).append(",");
        json.append("\"gross_amount\":").append(fmt2(grossAmount)).append(",");
        json.append("\"line_count\":").append(nLines).append(",");
        json.append("\"ubl_version\":\"2.1\"");
        json.append("}");
        return json.toString();
    }

    private static String fmt2(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    // ── W3C XMLDSig ───────────────────────────────────────────────────────────

    private static String xmldsig(ThreadLocalRandom rng) {
        String sigId = "Signature-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(java.util.Locale.ROOT);
        String refId = "Reference-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(java.util.Locale.ROOT);

        byte[] digestBytes = randomBytes(rng, 32);
        byte[] sigBytes = randomBytes(rng, 256);
        String digestValue = Base64.getEncoder().encodeToString(digestBytes);
        String signatureValue = Base64.getEncoder().encodeToString(sigBytes);
        String x509Cert = Base64.getEncoder().encodeToString(mockX509Der(rng));

        String xml =
            "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"" + sigId + "\">\n" +
            "  <ds:SignedInfo>\n" +
            "    <ds:CanonicalizationMethod Algorithm=\"" + C14N_ALGO + "\"/>\n" +
            "    <ds:SignatureMethod Algorithm=\"" + SIG_ALGO + "\"/>\n" +
            "    <ds:Reference Id=\"" + refId + "\" URI=\"\">\n" +
            "      <ds:Transforms>\n" +
            "        <ds:Transform Algorithm=\"" + ENV_ALGO + "\"/>\n" +
            "      </ds:Transforms>\n" +
            "      <ds:DigestMethod Algorithm=\"" + DIGEST_ALGO + "\"/>\n" +
            "      <ds:DigestValue>" + digestValue + "</ds:DigestValue>\n" +
            "    </ds:Reference>\n" +
            "  </ds:SignedInfo>\n" +
            "  <ds:SignatureValue Id=\"SignatureValue\">" + signatureValue + "</ds:SignatureValue>\n" +
            "  <ds:KeyInfo>\n" +
            "    <ds:X509Data>\n" +
            "      <ds:X509Certificate>" + x509Cert + "</ds:X509Certificate>\n" +
            "    </ds:X509Data>\n" +
            "  </ds:KeyInfo>\n" +
            "</ds:Signature>";

        StringBuilder json = new StringBuilder("{");
        json.append("\"xml\":\"").append(jsonEscape(xml)).append("\",");
        json.append("\"signature_id\":\"").append(sigId).append("\",");
        json.append("\"reference_id\":\"").append(refId).append("\",");
        json.append("\"algorithm\":\"").append(SIG_ALGO).append("\",");
        json.append("\"digest_method\":\"").append(DIGEST_ALGO).append("\",");
        json.append("\"c14n_method\":\"").append(C14N_ALGO).append("\",");
        json.append("\"digest_value\":\"").append(digestValue).append("\",");
        json.append("\"signature_value\":\"").append(signatureValue).append("\"");
        json.append("}");
        return json.toString();
    }

    // ── Minimal structurally-plausible X.509 DER mock (not cryptographically valid) ──

    private static byte[] mockX509Der(ThreadLocalRandom rng) {
        byte[] oidSha256Rsa = {0x06, 0x09, 0x2a, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xf7, 0x0d, 0x01, 0x01, 0x0b, 0x05, 0x00};
        byte[] serial = randomBytes(rng, 16);
        byte[] padTbs = randomBytes(rng, 800);
        byte[] padSig = randomBytes(rng, 256);

        java.io.ByteArrayOutputStream tbs = new java.io.ByteArrayOutputStream();
        tbs.writeBytes(new byte[]{(byte) 0xa0, 0x03, 0x02, 0x01, 0x02});
        tbs.writeBytes(new byte[]{0x02, (byte) serial.length});
        tbs.writeBytes(serial);
        tbs.writeBytes(oidSha256Rsa);
        tbs.writeBytes(padTbs);
        byte[] tbsBytes = tbs.toByteArray();

        java.io.ByteArrayOutputStream tbsSeq = new java.io.ByteArrayOutputStream();
        tbsSeq.writeBytes(new byte[]{0x30, (byte) 0x82});
        tbsSeq.writeBytes(len2Be(tbsBytes.length));
        tbsSeq.writeBytes(tbsBytes);
        byte[] tbsSeqBytes = tbsSeq.toByteArray();

        java.io.ByteArrayOutputStream cert = new java.io.ByteArrayOutputStream();
        cert.writeBytes(tbsSeqBytes);
        cert.writeBytes(oidSha256Rsa);
        cert.writeBytes(new byte[]{0x03, (byte) 0x82});
        cert.writeBytes(len2Be(padSig.length));
        cert.writeBytes(padSig);
        byte[] certBytes = cert.toByteArray();

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        out.writeBytes(new byte[]{0x30, (byte) 0x82});
        out.writeBytes(len2Be(certBytes.length));
        out.writeBytes(certBytes);
        return out.toByteArray();
    }

    private static byte[] len2Be(int len) {
        return new byte[]{(byte) ((len >> 8) & 0xFF), (byte) (len & 0xFF)};
    }

    private static byte[] randomBytes(ThreadLocalRandom rng, int n) {
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) b[i] = (byte) rng.nextInt(256);
        return b;
    }

    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
