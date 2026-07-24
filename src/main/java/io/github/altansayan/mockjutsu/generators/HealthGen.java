package io.github.altansayan.mockjutsu.generators;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** Health — NHS Modulo 11, ICD-10, NPI Luhn, HL7, FHIR, DICOM. Mirrors health.py. */
public final class HealthGen {

    private HealthGen() {}

    private static final String[] BLOOD_TYPES = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};

    private static final String[][] ICD10_POOL = {
        {"J18.9",   "Pneumonia, unspecified"},
        {"E11.9",   "Type 2 diabetes mellitus without complications"},
        {"I10",     "Essential hypertension"},
        {"E78.5",   "Hyperlipidemia, unspecified"},
        {"M54.5",   "Low back pain"},
        {"J06.9",   "Acute upper respiratory infection, unspecified"},
        {"F32.1",   "Major depressive disorder, moderate"},
        {"K21.0",   "GERD with oesophagitis"},
        {"N18.3",   "Chronic kidney disease, stage 3"},
        {"I21.0",   "Acute myocardial infarction, anterior wall"},
        {"C34.1",   "Malignant neoplasm, upper lobe bronchus"},
        {"Z00.00",  "Encounter for general adult medical examination"},
        {"J45.909", "Unspecified asthma, uncomplicated"},
        {"G43.909", "Migraine, unspecified, not intractable"},
        {"F41.1",   "Generalized anxiety disorder"},
        {"K29.70",  "Gastritis, unspecified, without bleeding"},
        {"M17.11",  "Primary osteoarthritis, right knee"},
        {"E03.9",   "Hypothyroidism, unspecified"},
        {"I25.10",  "Atherosclerotic heart disease, unspecified vessel"},
        {"J20.9",   "Acute bronchitis, unspecified"}
    };

    private static final String[] HL7_APPS = {"EMR_SYS","LIS","RADIOLOGY","PHARMACY","BILLING"};
    private static final String[] HL7_FACILITIES = {"CITYHOSP","METRO_MEDICAL","ST_JOHNS","CENTRAL_CLINIC"};
    private static final String[] HL7_UNITS = {"2EAST","3WEST","ICU","ED","ONCOLOGY","SURGERY"};
    private static final String[] HL7_LAST_NAMES = {
        "SMITH","JONES","WILLIAMS","BROWN","TAYLOR","ANDERSON","THOMAS","JACKSON","WHITE","HARRIS"
    };
    private static final String[] HL7_FIRST_NAMES = {
        "JAMES","MARY","ROBERT","LINDA","MICHAEL","PATRICIA","WILLIAM","BARBARA","DAVID","SUSAN"
    };
    private static final String[] HL7_PHYSICIANS = {
        "001^SMITH^JAMES^A^^MD","002^JOHNSON^MARY^B^^DO","003^WILLIAMS^ROBERT^C^^MD",
        "004^BROWN^LINDA^D^^NP","005^DAVIS^MICHAEL^E^^MD","006^MILLER^SUSAN^F^^DO"
    };

    private static final java.util.Map<String, String[]> FHIR_FAMILIES = java.util.Map.of(
        "TR", new String[]{"Yılmaz","Kaya","Demir","Şahin","Çelik","Aydın","Arslan"},
        "US", new String[]{"Smith","Johnson","Williams","Brown","Jones","Garcia","Miller"},
        "UK", new String[]{"Smith","Jones","Taylor","Brown","Davies","Evans","Wilson"},
        "DE", new String[]{"Müller","Schmidt","Schneider","Fischer","Weber","Meyer"},
        "FR", new String[]{"Martin","Bernard","Dubois","Thomas","Robert","Richard"},
        "RU", new String[]{"Иванов","Смирнов","Кузнецов","Попов","Васильев","Новиков"}
    );
    private static final java.util.Map<String, String[]> FHIR_GIVENS_M = java.util.Map.of(
        "TR", new String[]{"Ahmet","Mehmet","Mustafa","Ali","Hüseyin","Hasan"},
        "US", new String[]{"James","John","Robert","Michael","William","David"},
        "UK", new String[]{"Oliver","George","Harry","Jack","Charlie","Thomas"},
        "DE", new String[]{"Lukas","Jonas","Leon","Finn","Elias","Noah"},
        "FR", new String[]{"Lucas","Hugo","Gabriel","Arthur","Louis","Raphaël"},
        "RU", new String[]{"Александр","Дмитрий","Максим","Сергей","Андрей","Алексей"}
    );
    private static final java.util.Map<String, String[]> FHIR_GIVENS_F = java.util.Map.of(
        "TR", new String[]{"Fatma","Ayşe","Emine","Hatice","Zeynep","Elif"},
        "US", new String[]{"Mary","Patricia","Jennifer","Linda","Barbara","Susan"},
        "UK", new String[]{"Olivia","Amelia","Isla","Ava","Mia","Isabella"},
        "DE", new String[]{"Emma","Mia","Hannah","Lena","Lea","Anna"},
        "FR", new String[]{"Emma","Jade","Louise","Alice","Chloé","Inès"},
        "RU", new String[]{"Анастасия","Мария","Анна","Виктория","Екатерина","Наталья"}
    );
    private static final java.util.Map<String, String[]> FHIR_CITIES = java.util.Map.of(
        "TR", new String[]{"İstanbul","Ankara","İzmir","Bursa","Antalya"},
        "US", new String[]{"New York","Los Angeles","Chicago","Houston","Phoenix"},
        "UK", new String[]{"London","Birmingham","Manchester","Leeds","Glasgow"},
        "DE", new String[]{"Berlin","Hamburg","München","Köln","Frankfurt"},
        "FR", new String[]{"Paris","Lyon","Marseille","Toulouse","Nice"},
        "RU", new String[]{"Москва","Санкт-Петербург","Новосибирск","Екатеринбург","Казань"}
    );
    private static final java.util.Map<String, String> FHIR_COUNTRY_CODE = java.util.Map.of(
        "TR","TR","US","US","UK","GB","DE","DE","FR","FR","RU","RU"
    );
    private static final java.util.Map<String, String> E164_CC = java.util.Map.of(
        "TR","90","US","1","UK","44","DE","49","FR","33","RU","7"
    );

    public static String generate(String type, String locale) {
        return generate(type, locale, "");
    }

    public static String generate(String type, String locale, String qualifier) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "blood_type","bloodtype" -> pick(rng, BLOOD_TYPES);
            case "nhs_number","nhsnumber" -> nhsNumber(rng);
            case "icd10"                  -> icd10(rng, qualifier);
            case "height"                 -> height(rng, locale);
            case "weight"                 -> weight(rng, locale);
            case "bmi"                    -> bmi(rng);
            case "npi"                    -> npi(rng);
            case "hl7_message"            -> hl7Message(rng);
            case "fhir_patient"           -> fhirPatient(rng, locale);
            case "dicom_uid"              -> dicomUid(rng);
            default                       -> "ERROR: Unknown health type '" + type + "'";
        };
    }

    private static String icd10(ThreadLocalRandom rng, String qualifier) {
        String[] entry = ICD10_POOL[rng.nextInt(ICD10_POOL.length)];
        boolean withDesc = "desc".equalsIgnoreCase(qualifier) || "true".equalsIgnoreCase(qualifier)
                        || "description".equalsIgnoreCase(qualifier);
        if (withDesc)
            return "{\"code\":\"" + entry[0] + "\",\"description\":\"" + entry[1] + "\"}";
        return entry[0];
    }

    // ── NHS Number — Modulo 11 checksum ──────────────────────────────────────

    static String nhsNumber(ThreadLocalRandom rng) {
        int[] d = new int[10];
        while (true) {
            for (int i = 0; i < 9; i++) d[i] = rng.nextInt(0, 10);
            int[] weights = {10, 9, 8, 7, 6, 5, 4, 3, 2};
            int total = 0;
            for (int i = 0; i < 9; i++) total += d[i] * weights[i];
            int rem = total % 11;
            if (rem == 1) continue;
            int check = rem == 0 ? 0 : 11 - rem;
            if (check == 10) continue;
            d[9] = check;
            break;
        }
        return String.format("%d%d%d %d%d%d %d%d%d%d",
            d[0],d[1],d[2], d[3],d[4],d[5], d[6],d[7],d[8],d[9]);
    }

    // ── Height / Weight (locale-aware units) ─────────────────────────────────

    private static String height(ThreadLocalRandom rng, String locale) {
        String l = locale.toUpperCase(java.util.Locale.ROOT);
        int cm = rng.nextInt(41) + 155;
        if (l.equals("US")) {
            int totalInches = (int) Math.round(cm / 2.54);
            int feet = totalInches / 12;
            int inches = totalInches % 12;
            return feet + "'" + inches + "\"";
        }
        if (l.equals("UK")) {
            int totalInches = (int) Math.round(cm / 2.54);
            int feet = totalInches / 12;
            int inches = totalInches % 12;
            return feet + "'" + inches + "\" (" + cm + " cm)";
        }
        return cm + " cm";
    }

    private static String weight(ThreadLocalRandom rng, String locale) {
        String l = locale.toUpperCase(java.util.Locale.ROOT);
        int kg = rng.nextInt(61) + 50;
        if (l.equals("US")) {
            int lbs = (int) Math.round(kg * 2.20462);
            return lbs + " lbs";
        }
        if (l.equals("UK")) {
            int totalLbs = (int) Math.round(kg * 2.20462);
            int stones = totalLbs / 14;
            int lbs = totalLbs % 14;
            return stones + "st " + lbs + "lb (" + kg + " kg)";
        }
        return kg + " kg";
    }

    private static String bmi(ThreadLocalRandom rng) {
        double raw = 18.5 + rng.nextInt(166) / 10.0;
        double v = Math.round(raw * 10.0) / 10.0;
        return String.format(java.util.Locale.US, "%.1f", v);
    }

    // ── NPI — US National Provider Identifier (10 digits, Luhn w/ 80840 prefix) ──

    static String npi(ThreadLocalRandom rng) {
        int[] d = new int[9];
        for (int i = 0; i < 9; i++) d[i] = rng.nextInt(0, 10);
        StringBuilder sb = new StringBuilder("80840");
        for (int v : d) sb.append(v);
        int check = IdentityGen.luhnCheckDigit(sb.toString());
        StringBuilder out = new StringBuilder();
        for (int v : d) out.append(v);
        out.append(check);
        return out.toString();
    }

    // ── HL7 v2.5 ADT^A01 message ──────────────────────────────────────────────

    private static String hl7Message(ThreadLocalRandom rng) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String dob = rng.nextInt(1940, 2001) + String.format("%02d", rng.nextInt(1, 13)) + String.format("%02d", rng.nextInt(1, 29));

        String sendingApp = pick(rng, HL7_APPS);
        String sendingFac = pick(rng, HL7_FACILITIES);
        String recvApp = pick(rng, HL7_APPS);
        String recvFac = pick(rng, HL7_FACILITIES);
        String ctrlId = "MSG" + randomHexUpper(rng, 8);
        String last = pick(rng, HL7_LAST_NAMES);
        String first = pick(rng, HL7_FIRST_NAMES);
        String gender = rng.nextBoolean() ? "M" : "F";
        String mrn = "MRN" + rng.nextInt(100000, 1000000);
        String unit = pick(rng, HL7_UNITS);
        int room = rng.nextInt(100, 500);
        String bed = new String[]{"A","B","C"}[rng.nextInt(3)];
        String physician = pick(rng, HL7_PHYSICIANS);

        String msh = String.join("|", "MSH", "^~\\&", sendingApp, sendingFac, recvApp, recvFac,
            ts, "", "ADT^A01^ADT_A01", ctrlId, "P", "2.5");
        String evn = String.join("|", "EVN", "A01", ts);
        String pid = String.join("|", "PID", "1", "", mrn + "^^^" + sendingFac + "^MR", "",
            last + "^" + first + "^", "", dob, gender, "", "", "", "", "S");
        String pv1 = String.join("|", "PV1", "1", "I", unit + "^" + room + "^" + bed, "E",
            "", "", physician, "", "", "MED", "", "", "A");
        return String.join("\r", msh, evn, pid, pv1) + "\r";
    }

    // ── FHIR R4 Patient resource ──────────────────────────────────────────────

    private static String fhirPatient(ThreadLocalRandom rng, String locale) {
        String upper = locale.toUpperCase(java.util.Locale.ROOT);
        String loc = FHIR_FAMILIES.containsKey(upper) ? upper : "US";

        String[] genders = {"male","female","other","unknown"};
        String gender = genders[rng.nextInt(4)];
        String[] families = FHIR_FAMILIES.get(loc);
        String[] givens = gender.equals("female") ? FHIR_GIVENS_F.get(loc) : FHIR_GIVENS_M.get(loc);
        String family = pick(rng, families);
        String given = pick(rng, givens);
        String[] cities = FHIR_CITIES.get(loc);
        String country = FHIR_COUNTRY_CODE.getOrDefault(loc, loc);
        String city = pick(rng, cities);

        int birthYr = rng.nextInt(1940, 2006);
        int birthMo = rng.nextInt(1, 13);
        int birthDay = rng.nextInt(1, 29);
        String birthDt = birthYr + "-" + String.format("%02d", birthMo) + "-" + String.format("%02d", birthDay);
        String nowIso = java.time.Instant.now().atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"));
        String patientId = java.util.UUID.randomUUID().toString();

        String identifierValue = rng.nextInt(100, 1000) + "-" + rng.nextInt(10, 100) + "-" + rng.nextInt(1000, 10000);
        String phone = "+" + E164_CC.getOrDefault(loc, "90") + "-" + rng.nextInt(100, 1000) + "-" + rng.nextInt(1000000, 10000000);

        return "{\"resourceType\":\"Patient\",\"id\":\"" + patientId + "\"," +
            "\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"" + nowIso + "\"," +
            "\"profile\":[\"http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient\"]}," +
            "\"active\":true," +
            "\"identifier\":[{\"use\":\"official\",\"system\":\"urn:oid:2.16.840.1.113883.4.1\",\"value\":\"" + identifierValue + "\"}]," +
            "\"name\":[{\"use\":\"official\",\"family\":\"" + family + "\",\"given\":[\"" + given + "\"]}]," +
            "\"telecom\":[{\"system\":\"phone\",\"value\":\"" + phone + "\",\"use\":\"home\"}]," +
            "\"gender\":\"" + gender + "\"," +
            "\"birthDate\":\"" + birthDt + "\"," +
            "\"address\":[{\"use\":\"home\",\"type\":\"physical\",\"city\":\"" + city + "\",\"country\":\"" + country + "\"}]}";
    }

    // ── DICOM UID — root 2.25, 128-bit random decimal ────────────────────────

    private static String dicomUid(ThreadLocalRandom rng) {
        byte[] raw = new byte[16];
        for (int i = 0; i < 16; i++) raw[i] = (byte) rng.nextInt(256);
        java.math.BigInteger uid = new java.math.BigInteger(1, raw);
        return "2.25." + uid;
    }

    private static String randomHexUpper(ThreadLocalRandom rng, int chars) {
        String hex = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(chars);
        for (int i = 0; i < chars; i++) sb.append(hex.charAt(rng.nextInt(16)));
        return sb.toString();
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
