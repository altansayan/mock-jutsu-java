package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/**
 * IoT — RFID, NFC, IR, MQTT, LoRa. Mirrors iot.py.
 *
 * NOTE ON OUTPUT FORMAT: iot.py's dict-returning functions (rfid_tag, ndef_uri,
 * ndef_text, apdu, nfc_tag, ir_nec, ir_rc5, ir_raw) are printed by the Python CLI
 * via str(dict) — Python repr syntax: single-quoted strings, True/False, None,
 * unquoted numbers, dict insertion order. Only mqtt_payload calls json.dumps()
 * itself and is therefore real compact JSON (double quotes, true/false/lowercase).
 */
public final class IoTGen {
    private IoTGen() {}

    // ISO/IEC 7816-6 IC manufacturer codes (public registry)
    private static final int[] NFC_MFR_BYTES = {0x04, 0x02, 0x05, 0x07, 0xE0, 0x15, 0x16, 0x68};

    // (standard, frequency_mhz literal, memory_bytes)
    private static final String[][] RFID_PROFILES = {
        {"ISO 14443-A", "13.56", "144"},
        {"ISO 14443-A", "13.56", "504"},
        {"ISO 14443-A", "13.56", "888"},
        {"ISO 14443-A", "13.56", "1024"},
        {"ISO 14443-A", "13.56", "4096"},
        {"ISO 14443-B", "13.56", "256"},
        {"ISO 15693",   "13.56", "256"},
        {"ISO 15693",   "13.56", "512"},
        {"ISO 18000-6C", "915",  "96"},
        {"ISO 18000-6C", "868",  "96"},
        {"EM4100",       "0.125","40"},
        {"HID Prox",     "0.125","40"},
        {"HID iCLASS",   "13.56","2048"},
    };

    // GS1 EPC SGTIN-96 partition table: partition -> {company_prefix_bits, item_ref_bits}
    private static final int[][] EPC_PARTITION = {
        {40, 4}, {37, 7}, {34, 10}, {30, 14}, {27, 17}, {24, 20}, {20, 24}
    };

    // (atqa, sak, tag_type, capacity_bytes)
    private static final String[][] NFC_TAG_PROFILES = {
        {"00:44", "00", "NTAG213", "144"},
        {"00:44", "00", "NTAG215", "504"},
        {"00:44", "00", "NTAG216", "888"},
        {"00:04", "08", "MIFARE Classic 1K", "1024"},
        {"00:02", "18", "MIFARE Classic 4K", "4096"},
        {"03:44", "20", "MIFARE DESFire EV1", "8192"},
        {"00:44", "20", "MIFARE Plus SL3", "4096"},
        {"00:04", "28", "MIFARE Plus SL1", "1024"},
        {"03:04", "60", "MIFARE DESFire", "4096"},
    };

    // NFC Forum URI RTD v1.0 prefix codes -> prefix strings
    private static final int[] URI_PREFIX_CODES = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x08, 0x09, 0x0D};
    private static final java.util.Map<Integer, String> URI_PREFIX_MAP = new java.util.HashMap<>();
    static {
        URI_PREFIX_MAP.put(0x01, "http://www.");
        URI_PREFIX_MAP.put(0x02, "https://www.");
        URI_PREFIX_MAP.put(0x03, "http://");
        URI_PREFIX_MAP.put(0x04, "https://");
        URI_PREFIX_MAP.put(0x05, "tel:");
        URI_PREFIX_MAP.put(0x06, "mailto:");
        URI_PREFIX_MAP.put(0x08, "ftp://ftp.");
        URI_PREFIX_MAP.put(0x09, "ftps://");
        URI_PREFIX_MAP.put(0x0D, "urn:epc:");
    }

    private static final String[] URI_HOSTS = {
        "example.com", "api.example.org", "shop.test.io",
        "auth.demo.net", "pay.service.co", "device.iot.local",
        "hub.connect.app", "data.edge.tech", "link.tag.cloud",
        "nfc.reader.dev"
    };

    // EMVCo AIDs + ISO 7816-5 registered AIDs
    private static final String[][] EMVCO_AIDS = {
        {"A0000000031010", "Visa Credit/Debit"},
        {"A0000000032010", "Visa Electron"},
        {"A0000000033010", "Visa V Pay"},
        {"A0000000041010", "Mastercard Credit/Debit"},
        {"A0000000043060", "Maestro"},
        {"A0000000044010", "Mastercard Cirrus"},
        {"A0000000025010", "American Express"},
        {"A0000000651010", "JCB"},
        {"A0000003241010", "Discover"},
        {"A000000333010101", "UnionPay Credit"},
        {"315041592E5359532E4444463031", "EMV PPSE"},
    };

    private static final String[][] APDU_GENERAL = {
        {"00", "B0", "00", "00", "READ BINARY"},
        {"00", "CA", "9F", "17", "GET DATA — PIN try counter"},
        {"00", "CA", "9F", "36", "GET DATA — ATC"},
        {"00", "CA", "5F", "50", "GET DATA — issuer URL"},
        {"00", "84", "00", "00", "GET CHALLENGE"},
        {"00", "20", "00", "82", "VERIFY PIN — format 2"},
        {"80", "AE", "80", "00", "GENERATE AC — ARQC"},
    };

    // Philips RC-5 system addresses (public standard)
    private static final int[] RC5_SYSTEM_KEYS = {0, 1, 2, 3, 5, 6, 12, 16, 17, 18, 20, 21, 26, 29};
    private static final java.util.Map<Integer, String> RC5_SYSTEMS = new java.util.HashMap<>();
    static {
        RC5_SYSTEMS.put(0, "TV");
        RC5_SYSTEMS.put(1, "TV2 / Monitor");
        RC5_SYSTEMS.put(2, "Teletext");
        RC5_SYSTEMS.put(3, "Video");
        RC5_SYSTEMS.put(5, "VCR");
        RC5_SYSTEMS.put(6, "VCR2");
        RC5_SYSTEMS.put(12, "Laser Disc");
        RC5_SYSTEMS.put(16, "Audio Pre-amp");
        RC5_SYSTEMS.put(17, "Tuner / Radio");
        RC5_SYSTEMS.put(18, "Cassette / Tape");
        RC5_SYSTEMS.put(20, "CD Player");
        RC5_SYSTEMS.put(21, "Phono");
        RC5_SYSTEMS.put(26, "Satellite Receiver");
        RC5_SYSTEMS.put(29, "Lighting");
    }

    private static final int NEC_CARRIER_HZ = 38_000;
    private static final int PRONTO_FREQ_WORD = (int) Math.round(4_145_146.0 / NEC_CARRIER_HZ);
    private static final int RC5_CARRIER_HZ = 36_000;

    private static final String[] MQTT_SENSOR_TYPES = {
        "temperature", "humidity", "pressure", "motion", "light", "co2", "voltage"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "rfid_uid"            -> rfidUid(rng);
            case "epc"                 -> epc(rng);
            case "rfid_tag"            -> rfidTag(rng);
            case "nfc_uid"             -> nfcUid(rng);
            case "nfc_atqa"            -> pick(rng, NFC_TAG_PROFILES)[0];
            case "nfc_sak"             -> pick(rng, NFC_TAG_PROFILES)[1];
            case "ndef_uri"            -> ndefUri(rng);
            case "ndef_text"           -> ndefText(rng, locale);
            case "apdu"                -> apdu(rng);
            case "nfc_tag"             -> nfcTag(rng);
            case "ir_nec"              -> irNec(rng);
            case "ir_rc5"              -> irRc5(rng);
            case "ir_pronto"           -> irPronto(rng);
            case "ir_raw"              -> irRaw(rng);
            case "mqtt_payload"        -> mqttPayload(rng);
            case "lora_packet"         -> loraPacket(rng);
            default -> "ERROR: Unknown IoT type '" + type + "'";
        };
    }

    // ── RFID UID — 65% 7-byte (mfr + 6 random), 35% 4-byte random ────────────

    private static String rfidUid(ThreadLocalRandom rng) {
        byte[] raw;
        if (rng.nextDouble() < 0.65) {
            int mfr = NFC_MFR_BYTES[rng.nextInt(NFC_MFR_BYTES.length)];
            raw = new byte[7];
            raw[0] = (byte) mfr;
            for (int i = 1; i < 7; i++) raw[i] = (byte) rng.nextInt(256);
        } else {
            raw = new byte[4];
            for (int i = 0; i < 4; i++) raw[i] = (byte) rng.nextInt(256);
        }
        return hexColon(raw);
    }

    private static String hexColon(byte[] raw) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02X", raw[i] & 0xFF));
        }
        return sb.toString();
    }

    // ── NFC UID — always 7-byte (mfr + 6 random) ─────────────────────────────

    private static String nfcUid(ThreadLocalRandom rng) {
        int mfr = NFC_MFR_BYTES[rng.nextInt(NFC_MFR_BYTES.length)];
        byte[] raw = new byte[7];
        raw[0] = (byte) mfr;
        for (int i = 1; i < 7; i++) raw[i] = (byte) rng.nextInt(256);
        return hexColon(raw);
    }

    // ── EPC SGTIN-96 (ISO 18000-6C / EPCglobal Gen2), 24 uppercase hex chars ─

    private static String epc(ThreadLocalRandom rng) {
        int header = 0x30;
        int filt = rng.nextInt(8);
        int partition = rng.nextInt(7);
        int cpBits = EPC_PARTITION[partition][0];
        int irBits = EPC_PARTITION[partition][1];

        long companyPrefix = randomBits(rng, cpBits);
        long itemRef        = randomBits(rng, irBits);
        long serial          = randomBits(rng, 38);

        java.math.BigInteger value = java.math.BigInteger.valueOf(header);
        value = value.shiftLeft(3).or(java.math.BigInteger.valueOf(filt));
        value = value.shiftLeft(3).or(java.math.BigInteger.valueOf(partition));
        value = value.shiftLeft(cpBits).or(java.math.BigInteger.valueOf(companyPrefix));
        value = value.shiftLeft(irBits).or(java.math.BigInteger.valueOf(itemRef));
        value = value.shiftLeft(38).or(java.math.BigInteger.valueOf(serial));

        String hex = value.toString(16).toUpperCase(java.util.Locale.ROOT);
        while (hex.length() < 24) hex = "0" + hex;
        return hex;
    }

    private static long randomBits(ThreadLocalRandom rng, int bits) {
        if (bits <= 0) return 0L;
        long mask = (bits >= 64) ? -1L : ((1L << bits) - 1);
        return rng.nextLong() & mask;
    }

    // ── RFID Tag — Python-repr dict {uid, standard, frequency_mhz, memory_bytes[, epc]} ──

    private static String rfidTag(ThreadLocalRandom rng) {
        String[] profile = pick(rng, RFID_PROFILES);
        String std = profile[0], freq = profile[1], mem = profile[2];
        StringBuilder sb = new StringBuilder("{");
        sb.append("'uid': ").append(pyStr(rfidUid(rng))).append(", ");
        sb.append("'standard': ").append(pyStr(std)).append(", ");
        sb.append("'frequency_mhz': ").append(freq).append(", ");
        sb.append("'memory_bytes': ").append(mem);
        if (std.contains("18000")) {
            sb.append(", 'epc': ").append(pyStr(epc(rng)));
        }
        sb.append("}");
        return sb.toString();
    }

    // ── NDEF URI ──────────────────────────────────────────────────────────────

    private static String ndefUri(ThreadLocalRandom rng) {
        int prefixCode = URI_PREFIX_CODES[rng.nextInt(URI_PREFIX_CODES.length)];
        String prefixStr = URI_PREFIX_MAP.get(prefixCode);
        String host = URI_HOSTS[rng.nextInt(URI_HOSTS.length)];
        String path = "/" + randomHexLower(rng, 8);
        String uriSuffix = host + path;
        byte[] uriBytes = uriSuffix.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] payload = new byte[1 + uriBytes.length];
        payload[0] = (byte) prefixCode;
        System.arraycopy(uriBytes, 0, payload, 1, uriBytes.length);
        byte[] raw = new byte[4 + payload.length];
        raw[0] = (byte) 0xD1; raw[1] = 0x01; raw[2] = (byte) payload.length; raw[3] = 0x55;
        System.arraycopy(payload, 0, raw, 4, payload.length);

        String decoded = prefixStr + uriSuffix;
        return ndefUriRepr(bytesToHexUpper(raw), decoded, prefixCode);
    }

    private static String ndefUriRepr(String rawHex, String decoded, int prefixCode) {
        return "{'raw_hex': " + pyStr(rawHex) + ", 'decoded': " + pyStr(decoded) +
            ", 'tnf': 1, 'type': 'U', 'prefix_code': " + pyStr(String.format("0x%02X", prefixCode)) + "}";
    }

    // ── NDEF Text ─────────────────────────────────────────────────────────────

    private static final java.util.Map<String, String[]> NDEF_TEXT_POOL = new java.util.HashMap<>();
    static {
        NDEF_TEXT_POOL.put("tr", new String[]{"Merhaba Dünya", "Test etiket verisi", "NFC bağlantı noktası",
            "Ödeme terminali", "Akıllı etiket içeriği"});
        NDEF_TEXT_POOL.put("en", new String[]{"Hello World", "NFC tag payload", "Smart label data",
            "Payment terminal", "Device handshake token"});
        NDEF_TEXT_POOL.put("de", new String[]{"Hallo Welt", "NFC-Etikett Daten", "Smarte Kennzeichnung",
            "Zahlungsterminal", "Geräteverknüpfung"});
        NDEF_TEXT_POOL.put("fr", new String[]{"Bonjour le monde", "Données étiquette NFC", "Terminal de paiement",
            "Dispositif intelligent", "Jeton d'accès NFC"});
        NDEF_TEXT_POOL.put("ru", new String[]{"Привет мир", "Данные метки NFC", "Платёжный терминал",
            "Смарт-этикетка", "Токен устройства"});
    }

    private static String ndefText(ThreadLocalRandom rng, String locale) {
        String lang = switch (locale.toUpperCase(java.util.Locale.ROOT)) {
            case "TR" -> "tr"; case "US", "UK" -> "en"; case "DE" -> "de"; case "FR" -> "fr"; case "RU" -> "ru";
            default -> "en";
        };
        String[] pool = NDEF_TEXT_POOL.getOrDefault(lang, NDEF_TEXT_POOL.get("en"));
        String text = pool[rng.nextInt(pool.length)];
        byte[] langB = lang.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        byte[] textB = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int status = langB.length & 0x3F;
        byte[] payload = new byte[1 + langB.length + textB.length];
        payload[0] = (byte) status;
        System.arraycopy(langB, 0, payload, 1, langB.length);
        System.arraycopy(textB, 0, payload, 1 + langB.length, textB.length);
        byte[] raw = new byte[4 + payload.length];
        raw[0] = (byte) 0xD1; raw[1] = 0x01; raw[2] = (byte) payload.length; raw[3] = 0x54;
        System.arraycopy(payload, 0, raw, 4, payload.length);

        return "{'raw_hex': " + pyStr(bytesToHexUpper(raw)) + ", 'decoded': " + pyStr(text) +
            ", 'lang': " + pyStr(lang) + ", 'tnf': 1, 'type': 'T', 'encoding': 'UTF-8'}";
    }

    // ── APDU ──────────────────────────────────────────────────────────────────

    private static String apdu(ThreadLocalRandom rng) {
        if (rng.nextDouble() < 0.6) {
            String[] aid = pick(rng, EMVCO_AIDS);
            String aidHex = aid[0], aidName = aid[1];
            int lc = aidHex.length() / 2;
            StringBuilder hexStr = new StringBuilder("00 A4 04 00 ").append(String.format("%02X", lc));
            for (int i = 0; i < aidHex.length(); i += 2) hexStr.append(' ').append(aidHex, i, i + 2);
            hexStr.append(" 00");
            return "{'cla': '00', 'ins': 'A4', 'p1': '04', 'p2': '00', 'lc': " + pyStr(String.format("%02X", lc)) +
                ", 'data': " + pyStr(aidHex) + ", 'le': '00', 'hex': " + pyStr(hexStr.toString()) +
                ", 'description': " + pyStr("SELECT AID — " + aidName) + "}";
        }
        String[] g = pick(rng, APDU_GENERAL);
        String cla = g[0], ins = g[1], p1 = g[2], p2 = g[3], desc = g[4];
        String le = String.format("%02X", rng.nextInt(256));
        String hex = cla + " " + ins + " " + p1 + " " + p2 + " " + le;
        return "{'cla': " + pyStr(cla) + ", 'ins': " + pyStr(ins) + ", 'p1': " + pyStr(p1) + ", 'p2': " + pyStr(p2) +
            ", 'le': " + pyStr(le) + ", 'data': None, 'hex': " + pyStr(hex) + ", 'description': " + pyStr(desc) + "}";
    }

    // ── NFC Tag ───────────────────────────────────────────────────────────────

    private static String nfcTag(ThreadLocalRandom rng) {
        String[] profile = pick(rng, NFC_TAG_PROFILES);
        String atqa = profile[0], sak = profile[1], tagType = profile[2], capacity = profile[3];

        int prefixCode = URI_PREFIX_CODES[rng.nextInt(URI_PREFIX_CODES.length)];
        String prefixStr = URI_PREFIX_MAP.get(prefixCode);
        String host = URI_HOSTS[rng.nextInt(URI_HOSTS.length)];
        String path = "/" + randomHexLower(rng, 8);
        String uriSuffix = host + path;
        byte[] uriBytes = uriSuffix.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] payload = new byte[1 + uriBytes.length];
        payload[0] = (byte) prefixCode;
        System.arraycopy(uriBytes, 0, payload, 1, uriBytes.length);
        byte[] raw = new byte[4 + payload.length];
        raw[0] = (byte) 0xD1; raw[1] = 0x01; raw[2] = (byte) payload.length; raw[3] = 0x55;
        System.arraycopy(payload, 0, raw, 4, payload.length);
        String ndefRawHex = bytesToHexUpper(raw);
        String ndefDecoded = prefixStr + uriSuffix;

        return "{'uid': " + pyStr(nfcUid(rng)) + ", 'atqa': " + pyStr(atqa) + ", 'sak': " + pyStr(sak) +
            ", 'type': " + pyStr(tagType) + ", 'capacity_bytes': " + capacity +
            ", 'ndef_message': " + pyStr(ndefRawHex) + ", 'ndef_decoded': " + pyStr(ndefDecoded) + "}";
    }

    // ── IR NEC ────────────────────────────────────────────────────────────────

    private static String irNec(ThreadLocalRandom rng) {
        int address = rng.nextInt(256);
        int command = rng.nextInt(256);
        int invAddr = (~address) & 0xFF;
        int invCmd  = (~command) & 0xFF;
        String hex = String.format("%02X%02X%02X%02X", address, invAddr, command, invCmd);
        return String.format(java.util.Locale.ROOT,
            "{'address': '0x%02X', 'command': '0x%02X', 'inv_address': '0x%02X', 'inv_command': '0x%02X', " +
            "'hex': '%s', 'checksum_valid': True, 'carrier_hz': %d, 'protocol': 'NEC'}",
            address, command, invAddr, invCmd, hex, NEC_CARRIER_HZ);
    }

    // ── IR RC-5 ───────────────────────────────────────────────────────────────

    private static String irRc5(ThreadLocalRandom rng) {
        int system = RC5_SYSTEM_KEYS[rng.nextInt(RC5_SYSTEM_KEYS.length)];
        int command = rng.nextInt(128);
        int toggle  = rng.nextInt(2);
        String frameBits = rc5FrameBits(system, command, toggle);
        return "{'system': " + system + ", 'system_name': " + pyStr(RC5_SYSTEMS.get(system)) +
            ", 'command': " + command + ", 'toggle': " + toggle +
            ", 'frame_bits': " + pyStr(frameBits) + ", 'carrier_hz': " + RC5_CARRIER_HZ + ", 'protocol': 'RC-5'}";
    }

    private static String rc5FrameBits(int system, int command, int toggle) {
        int field = 1 - ((command >> 6) & 1);
        int frame = (1 << 13) | (field << 12) | (toggle << 11) | (system << 6) | (command & 0x3F);
        String bin = Integer.toBinaryString(frame);
        while (bin.length() < 14) bin = "0" + bin;
        return bin;
    }

    // ── NEC bit order — LSB-first per byte: address, ~address, command, ~command ──

    private static int[] necBitsLsb(int address, int command) {
        int invA = (~address) & 0xFF;
        int invC = (~command) & 0xFF;
        int[] bytes = {address, invA, command, invC};
        int[] bits = new int[32];
        int idx = 0;
        for (int b : bytes) {
            for (int bit = 0; bit < 8; bit++) bits[idx++] = (b >> bit) & 1;
        }
        return bits;
    }

    private static int usToCycles(double us, int carrierHz) {
        return (int) Math.round(us * carrierHz / 1_000_000.0);
    }

    // ── IR Pronto Hex (CCF) — NEC frame ──────────────────────────────────────

    private static String irPronto(ThreadLocalRandom rng) {
        int address = rng.nextInt(256);
        int command = rng.nextInt(256);
        int[] bits = necBitsLsb(address, command);

        java.util.List<int[]> pairs = new java.util.ArrayList<>();
        pairs.add(new int[]{usToCycles(9000, NEC_CARRIER_HZ), usToCycles(4500, NEC_CARRIER_HZ)});
        for (int bit : bits) {
            pairs.add(new int[]{usToCycles(562.5, NEC_CARRIER_HZ), usToCycles(bit == 1 ? 1687.5 : 562.5, NEC_CARRIER_HZ)});
        }
        pairs.add(new int[]{usToCycles(562.5, NEC_CARRIER_HZ), usToCycles(40000, NEC_CARRIER_HZ)});

        java.util.List<Integer> words = new java.util.ArrayList<>();
        words.add(0x0000);
        words.add(PRONTO_FREQ_WORD);
        words.add(pairs.size());
        words.add(0x0000);
        for (int[] p : pairs) { words.add(p[0]); words.add(p[1]); }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            if (i > 0) sb.append(' ');
            sb.append(String.format("%04X", words.get(i)));
        }
        return sb.toString();
    }

    // ── IR Raw — microsecond pulse/space timings, LSB-first per byte ────────

    private static String irRaw(ThreadLocalRandom rng) {
        int address = rng.nextInt(256);
        int command = rng.nextInt(256);
        int[] bits = necBitsLsb(address, command);

        java.util.List<Integer> pulses = new java.util.ArrayList<>();
        pulses.add(9024);
        pulses.add(4512);
        for (int bit : bits) {
            pulses.add(562);
            pulses.add(bit == 1 ? 1686 : 562);
        }
        pulses.add(562);

        StringBuilder psb = new StringBuilder("[");
        for (int i = 0; i < pulses.size(); i++) {
            if (i > 0) psb.append(", ");
            psb.append(pulses.get(i));
        }
        psb.append("]");

        return String.format(java.util.Locale.ROOT,
            "{'carrier_hz': %d, 'address': '0x%02X', 'command': '0x%02X', 'pulses': %s, 'pulse_count': %d}",
            NEC_CARRIER_HZ, address, command, psb, pulses.size());
    }

    // ── MQTT Payload — real compact JSON (json.dumps in Python source) ──────

    private static String mqttPayload(ThreadLocalRandom rng) {
        String sensorType = MQTT_SENSOR_TYPES[rng.nextInt(MQTT_SENSOR_TYPES.length)];
        String readings;
        switch (sensorType) {
            case "temperature" -> readings = String.format(java.util.Locale.US,
                "{\"celsius\":%.2f,\"fahrenheit\":%.2f}",
                rng.nextDouble(-40.0, 85.0), rng.nextDouble(-40.0, 85.0) * 9 / 5 + 32);
            case "humidity" -> readings = String.format(java.util.Locale.US,
                "{\"percent\":%.2f}", rng.nextDouble(0.0, 100.0));
            case "pressure" -> readings = String.format(java.util.Locale.US,
                "{\"hpa\":%.2f}", rng.nextDouble(900.0, 1100.0));
            case "motion" -> readings = String.format(
                "{\"detected\":%s,\"count\":%d}", rng.nextBoolean() ? "true" : "false", rng.nextInt(0, 51));
            case "light" -> readings = String.format(java.util.Locale.US,
                "{\"lux\":%.1f}", rng.nextDouble(0.0, 100000.0));
            case "co2" -> readings = String.format("{\"ppm\":%d}", rng.nextInt(400, 5001));
            default -> readings = String.format(java.util.Locale.US, "{\"volts\":%.3f}", rng.nextDouble(0.0, 5.0));
        }
        int offsetSec = rng.nextInt(7 * 24 * 3600);
        String ts = java.time.Instant.now().minusSeconds(offsetSec)
            .atZone(java.time.ZoneOffset.UTC)
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        String deviceId = randomHexLower(rng, 8) + "-" + randomHexLower(rng, 4) + "-" + randomHexLower(rng, 4);
        int rssi = rng.nextInt(-120, -29);
        double snr = Math.round(rng.nextDouble(-20.0, 10.0) * 10.0) / 10.0;
        int batteryPct = rng.nextInt(0, 101);

        return String.format(java.util.Locale.US,
            "{\"device_id\":\"%s\",\"timestamp\":\"%s\",\"sensor_type\":\"%s\",\"readings\":%s,\"rssi\":%d,\"snr\":%.1f,\"battery_pct\":%d}",
            deviceId, ts, sensorType, readings, rssi, snr, batteryPct);
    }

    // ── LoRaWAN Packet — space-separated lowercase hex ───────────────────────

    private static String loraPacket(ThreadLocalRandom rng) {
        int mhdr = 0x40;
        byte[] devAddr = randomBytes(rng, 4);
        int fctrl = 0x00;
        byte[] fcnt = randomBytes(rng, 2);
        int fport = rng.nextInt(1, 11);
        int payloadLen = rng.nextInt(4, 13);
        byte[] payload = randomBytes(rng, payloadLen);
        byte[] mic = randomBytes(rng, 4);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02x", mhdr));
        for (byte b : devAddr) sb.append(String.format(" %02x", b & 0xFF));
        sb.append(String.format(" %02x", fctrl));
        for (byte b : fcnt) sb.append(String.format(" %02x", b & 0xFF));
        sb.append(String.format(" %02x", fport));
        for (byte b : payload) sb.append(String.format(" %02x", b & 0xFF));
        for (byte b : mic) sb.append(String.format(" %02x", b & 0xFF));
        return sb.toString();
    }

    private static byte[] randomBytes(ThreadLocalRandom rng, int n) {
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) b[i] = (byte) rng.nextInt(256);
        return b;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }

    private static String pyStr(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    private static String bytesToHexUpper(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02X", x & 0xFF));
        return sb.toString();
    }

    private static String randomHexLower(ThreadLocalRandom rng, int chars) {
        String hex = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(chars);
        for (int i = 0; i < chars; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }
}
