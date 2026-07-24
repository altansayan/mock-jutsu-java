package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Automotive OBD-II and CAN Bus generator. Mirrors automotive.py. */
public final class AutomotiveGen {
    private AutomotiveGen() {}

    private static final int CAN_CRC_POLY = 0xC599;
    private static final int OBD2_ECU_ID = 0x7E8;
    private static final String OBD2_ECU_ID_STR = "7E8";
    private static final int OBD2_MODE_01 = 0x41;

    private static final String[] COMMON_DTCS = {
        "P0300","P0301","P0302","P0303","P0304",
        "P0171","P0174","P0128","P0420","P0430",
        "P0401","P0442","P0455","P0325","P0340",
        "C0001","C0031","C0034","C0040","C0110",
        "B1234","B1341","B2100",
        "U0100","U0101","U0121","U0155"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "can_frame"     -> canFrame(rng);
            case "obd2_response" -> obd2Response(rng);
            default -> "ERROR: Unknown automotive type '" + type + "'";
        };
    }

    // ── CRC-15 (ISO 11898-1, polynomial 0xC599) ──────────────────────────────

    private static int canCrc15(int canId, int dlc, int[] data, boolean extended) {
        java.util.List<Integer> bits = new java.util.ArrayList<>();
        bits.add(0);
        if (!extended) {
            for (int i = 10; i >= 0; i--) bits.add((canId >> i) & 1);
            bits.add(0); bits.add(0); bits.add(0);
        } else {
            for (int i = 28; i >= 0; i--) bits.add((canId >> i) & 1);
            bits.add(1); bits.add(1); bits.add(0); bits.add(0); bits.add(0);
        }
        for (int i = 3; i >= 0; i--) bits.add((dlc >> i) & 1);
        for (int b : data) {
            for (int i = 7; i >= 0; i--) bits.add((b >> i) & 1);
        }
        int crc = 0;
        for (int bit : bits) {
            if (((crc >> 14) ^ bit) == 1) {
                crc = ((crc << 1) ^ CAN_CRC_POLY) & 0x7FFF;
            } else {
                crc = (crc << 1) & 0x7FFF;
            }
        }
        return crc;
    }

    private static String hexJoin(int[] data) {
        StringBuilder sb = new StringBuilder();
        for (int b : data) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    private static String jsonIntArray(int[] data) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(data[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    // ── CAN Frame ─────────────────────────────────────────────────────────────

    private static String canFrame(ThreadLocalRandom rng) {
        boolean extended = rng.nextDouble() < 0.3;
        int canId = extended ? rng.nextInt(0, 536870912) : rng.nextInt(0, 2048);
        int dlc = rng.nextInt(0, 9);
        int[] data = new int[dlc];
        for (int i = 0; i < dlc; i++) data[i] = rng.nextInt(256);
        int crc15 = canCrc15(canId, dlc, data, extended);
        String dataHex = hexJoin(data);
        String idStr = extended ? String.format("%08X", canId) : String.format("%03X", canId);

        return String.format(java.util.Locale.US,
            "{\"frame_type\":\"%s\",\"can_id\":\"0x%s\",\"can_id_int\":%d,\"dlc\":%d," +
            "\"data\":%s,\"data_hex\":\"%s\",\"crc15\":%d,\"crc15_hex\":\"%04X\",\"socketcan\":\"%s#%s\"}",
            extended ? "extended" : "standard", idStr, canId, dlc, jsonIntArray(data), dataHex,
            crc15, crc15, idStr, dataHex);
    }

    // ── OBD-II ────────────────────────────────────────────────────────────────

    private static String obd2PidEntry(int pidByte, String name, String unit, String valueJson, int[] rawBytes) {
        int[] canData = new int[8];
        canData[0] = 1 + 1 + rawBytes.length;
        canData[1] = OBD2_MODE_01;
        canData[2] = pidByte;
        for (int i = 0; i < rawBytes.length; i++) canData[3 + i] = rawBytes[i] & 0xFF;

        int crc15 = canCrc15(OBD2_ECU_ID, 8, canData, false);
        String dataHex = hexJoin(canData);
        String rawHex = hexJoin(rawBytes);

        return String.format(java.util.Locale.US,
            "{\"pid\":\"%02X\",\"name\":\"%s\",\"value\":%s,\"unit\":\"%s\",\"raw_hex\":\"%s\"," +
            "\"can_id_int\":%d,\"can_dlc\":8,\"can_data\":%s,\"crc15\":%d,\"socketcan\":\"%s#%s\"}",
            pidByte, name, valueJson, unit, rawHex, OBD2_ECU_ID, jsonIntArray(canData), crc15,
            OBD2_ECU_ID_STR, dataHex);
    }

    private static String obd2Response(ThreadLocalRandom rng) {
        double rpm     = Math.round(rng.nextDouble(600.0, 7000.0) * 100.0) / 100.0;
        int speed      = rng.nextInt(0, 251);
        int coolant    = rng.nextInt(-10, 111);
        double throttle    = Math.round(rng.nextDouble(0.0, 100.0) * 10.0) / 10.0;
        double engineLoad  = Math.round(rng.nextDouble(10.0, 90.0) * 10.0) / 10.0;
        double fuelLevel   = Math.round(rng.nextDouble(5.0, 95.0) * 10.0) / 10.0;

        int rpmRaw = (int) (rpm * 4);
        int rpmA = (rpmRaw >> 8) & 0xFF;
        int rpmB = rpmRaw & 0xFF;
        int thrRaw = ((int) (throttle * 255 / 100)) & 0xFF;
        int loadRaw = ((int) (engineLoad * 255 / 100)) & 0xFF;
        int fuelRaw = ((int) (fuelLevel * 255 / 100)) & 0xFF;

        String[] pids = {
            obd2PidEntry(0x0C, "Engine RPM", "rpm", fmt2(rpm), new int[]{rpmA, rpmB}),
            obd2PidEntry(0x0D, "Vehicle Speed", "km/h", String.valueOf(speed), new int[]{speed}),
            obd2PidEntry(0x05, "Coolant Temp", "°C", String.valueOf(coolant), new int[]{coolant + 40}),
            obd2PidEntry(0x11, "Throttle Pos", "%", fmt1(throttle), new int[]{thrRaw}),
            obd2PidEntry(0x04, "Engine Load", "%", fmt1(engineLoad), new int[]{loadRaw}),
            obd2PidEntry(0x2F, "Fuel Level", "%", fmt1(fuelLevel), new int[]{fuelRaw}),
        };
        StringBuilder pidsArr = new StringBuilder("[");
        for (int i = 0; i < pids.length; i++) {
            if (i > 0) pidsArr.append(',');
            pidsArr.append(pids[i]);
        }
        pidsArr.append(']');

        java.util.List<String> dtcs = new java.util.ArrayList<>();
        if (rng.nextDouble() < 0.3) {
            int n = rng.nextInt(1, 4);
            java.util.List<String> pool = new java.util.ArrayList<>(java.util.Arrays.asList(COMMON_DTCS));
            java.util.Collections.shuffle(pool, new java.util.Random(rng.nextLong()));
            for (int i = 0; i < Math.min(n, pool.size()); i++) dtcs.add(pool.get(i));
        }
        StringBuilder dtcsArr = new StringBuilder("[");
        for (int i = 0; i < dtcs.size(); i++) {
            if (i > 0) dtcsArr.append(',');
            dtcsArr.append('"').append(dtcs.get(i)).append('"');
        }
        dtcsArr.append(']');

        return String.format(java.util.Locale.US,
            "{\"ecu_id\":\"%s\",\"mode\":\"01\",\"pids\":%s,\"dtcs\":%s,\"dtc_count\":%d," +
            "\"rpm\":%s,\"speed_kmh\":%d,\"coolant_temp_c\":%d," +
            "\"throttle_pct\":%s,\"engine_load_pct\":%s,\"fuel_level_pct\":%s}",
            OBD2_ECU_ID_STR, pidsArr, dtcsArr, dtcs.size(),
            fmt2(rpm), speed, coolant, fmt1(throttle), fmt1(engineLoad), fmt1(fuelLevel));
    }

    private static String fmt2(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private static String fmt1(double v) {
        return String.format(java.util.Locale.US, "%.1f", v);
    }
}
