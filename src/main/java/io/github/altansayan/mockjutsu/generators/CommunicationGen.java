package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Communication — phone, email, address, plate. Mirrors communication.py. */
public final class CommunicationGen {

    private CommunicationGen() {}

    private static final java.util.Map<String, String[]> CITIES = java.util.Map.of(
        "TR", new String[]{"İstanbul","Ankara","İzmir","Bursa","Antalya","Adana","Konya","Gaziantep"},
        "US", new String[]{"New York","Los Angeles","Chicago","Houston","Phoenix","Philadelphia","San Antonio","Dallas"},
        "UK", new String[]{"London","Birmingham","Manchester","Leeds","Glasgow","Liverpool","Bristol","Sheffield"},
        "DE", new String[]{"Berlin","Hamburg","München","Köln","Frankfurt","Stuttgart","Düsseldorf","Leipzig"},
        "FR", new String[]{"Paris","Marseille","Lyon","Toulouse","Nice","Nantes","Strasbourg","Montpellier"},
        "RU", new String[]{"Москва","Санкт-Петербург","Новосибирск","Екатеринбург","Казань","Нижний Новгород"}
    );

    private static final java.util.Map<String, String[]> STREETS = java.util.Map.of(
        "TR", new String[]{"Atatürk Caddesi","İstiklal Caddesi","Bağdat Caddesi","Şair Nefi Sokak","Nispetiye Caddesi","Cumhuriyet Caddesi","Halaskargazi Caddesi"},
        "US", new String[]{"Main Street","Oak Avenue","Maple Drive","Broadway","5th Avenue","Park Lane","Elm Street","Cedar Road"},
        "UK", new String[]{"High Street","Church Road","Victoria Road","Green Lane","Station Road","King Street","Queen Street","Mill Road"},
        "DE", new String[]{"Hauptstraße","Bahnhofstraße","Kirchenstraße","Schillerstraße","Goethestraße","Friedrichstraße","Wilhelmstraße"},
        "FR", new String[]{"Rue de la Paix","Avenue des Champs-Élysées","Boulevard Saint-Germain","Rue Victor Hugo","Rue de Rivoli","Avenue Montaigne"},
        "RU", new String[]{"Улица Ленина","Проспект Мира","Улица Пушкина","Невский проспект","Тверская улица","Арбат","Садовая улица"}
    );

    private static final class PhoneData {
        final String prefix;
        final String[] carriers;
        final int localLen;
        PhoneData(String prefix, String[] carriers, int localLen) {
            this.prefix = prefix; this.carriers = carriers; this.localLen = localLen;
        }
    }
    private static final java.util.Map<String, PhoneData> PHONE_DATA = new java.util.HashMap<>();
    static {
        PHONE_DATA.put("TR", new PhoneData("+90", new String[]{"532","533","542","544","505","506"}, 7));
        PHONE_DATA.put("US", new PhoneData("+1",  new String[]{"555","212","310","415","718","312"}, 7));
        PHONE_DATA.put("UK", new PhoneData("+44", new String[]{"7911","7800","7712","7490"}, 6));
        PHONE_DATA.put("DE", new PhoneData("+49", new String[]{"151","160","170","171","176"}, 7));
        PHONE_DATA.put("FR", new PhoneData("+33", new String[]{"6","7"}, 8));
        PHONE_DATA.put("RU", new PhoneData("+7",  new String[]{"916","999","903","917","926"}, 7));
    }

    private static final java.util.Map<String, String[]> EMAIL_DOMAINS = java.util.Map.of(
        "TR", new String[]{"testposta.com.tr","mock-mail.net.tr","ornek-eposta.tr","deneme-posta.org"},
        "US", new String[]{"testmail.us","mockmail.net","samplemail.org","devtest-mail.io"},
        "UK", new String[]{"testmail.co.uk","mockpost.org.uk","samplemail.uk","devmail.co.uk"},
        "DE", new String[]{"testmail.de","mustermail.de","beispiel-post.de","probemail.org"},
        "FR", new String[]{"testmail.fr","courrielfaux.fr","exemple-mail.fr","fakepost.org"},
        "RU", new String[]{"testmail.ru","testovaya-pochta.ru","primer-mail.ru","fakepost.org"}
    );
    private static final String[] EMAIL_PREFIXES = {"user","test","mock","dev","demo","sandbox","ninja"};

    private static final String[] TR_PLATE_REGIONS;
    static {
        TR_PLATE_REGIONS = new String[81];
        for (int i = 1; i <= 81; i++) TR_PLATE_REGIONS[i - 1] = String.format("%02d", i);
    }
    private static final String[] RU_PLATE_REGIONS = {"77","78","99","197","199"};
    private static final String[] DE_PLATE_REGIONS = {"B","M","H","HH","S","K"};

    private static final String UK_PLATE_LETTERS = "ABCEGHJKLMNOPRSTWXYZ";
    private static final String UK_PLATE_RAND     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String RU_PLATE_CHARS    = "ABEKMHOPCTYX";
    private static final String US_PLATE_LETTERS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String FR_PLATE_LETTERS  = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DE_PLATE_LETTERS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String TR_PLATE_LETTERS  = "ABCDEFGHJKLMNPRSTUVYZ";

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "phone"         -> phone(rng, locale);
            case "phone_country" -> phoneAtomic(rng, locale, "country");
            case "phone_area"    -> phoneAtomic(rng, locale, "area");
            case "phone_local"   -> phoneAtomic(rng, locale, "local");
            case "address_city"  -> city(rng, locale);
            case "address_street"-> street(rng, locale);
            case "address_district" -> city(rng, locale);
            case "address_neighborhood" -> city(rng, locale) + " " + rng.nextInt(1, 21) + ". Mahalle";
            case "address_full"  -> addressFull(rng, locale);
            case "postalcode"    -> postalCode(rng, locale);
            case "plate"         -> plate(rng, locale);
            case "email"         -> email(rng, locale);
            default              -> "ERROR: Unknown comm type '" + type + "'";
        };
    }

    // ── Phone ─────────────────────────────────────────────────────────────────

    private static PhoneData phoneData(String locale) {
        return PHONE_DATA.getOrDefault(locale, PHONE_DATA.get("TR"));
    }

    private static String randomDigits(ThreadLocalRandom rng, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(rng.nextInt(10));
        return sb.toString();
    }

    static String phone(ThreadLocalRandom rng, String locale) {
        PhoneData d = phoneData(locale);
        String carrier = pick(rng, d.carriers);
        String local = randomDigits(rng, d.localLen);
        return d.prefix + carrier + local;
    }

    private static String phoneAtomic(ThreadLocalRandom rng, String locale, String which) {
        PhoneData d = phoneData(locale);
        return switch (which) {
            case "country" -> d.prefix;
            case "area"    -> pick(rng, d.carriers);
            default        -> randomDigits(rng, d.localLen);
        };
    }

    // ── Address ───────────────────────────────────────────────────────────────

    static String city(ThreadLocalRandom rng, String locale) {
        String[] pool = CITIES.getOrDefault(locale, CITIES.get("TR"));
        return pick(rng, pool);
    }

    private static String street(ThreadLocalRandom rng, String locale) {
        String[] pool = STREETS.getOrDefault(locale, STREETS.get("TR"));
        return pick(rng, pool);
    }

    private static String addressFull(ThreadLocalRandom rng, String locale) {
        String c = city(rng, locale);
        String s = street(rng, locale);
        int no = rng.nextInt(1, 201);
        return c + ", " + s + " No:" + no;
    }

    // ── Postal code ───────────────────────────────────────────────────────────

    static String postalCode(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> {
                String[] prefixes = {"06","16","34","35","41","42"};
                yield pick(rng, prefixes) + (rng.nextInt(900) + 100);
            }
            case "UK" -> {
                String[] areas = {"SW","EC","WC","SE","E","N","NW","W","EC"};
                String letters = randomFrom(rng, "ABCDEFGHJKLMNPRSTUVWXY", 2);
                yield pick(rng, areas) + (rng.nextInt(9) + 1) + " " + (rng.nextInt(9) + 1) + letters;
            }
            case "FR" -> String.format("%05d", rng.nextInt(97000) + 1000);
            case "RU" -> String.valueOf(rng.nextInt(900000) + 100000);
            default   -> String.valueOf(rng.nextInt(90000) + 10000);
        };
    }

    // ── License Plate ─────────────────────────────────────────────────────────

    private static String plate(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "TR" -> {
                String city = pick(rng, TR_PLATE_REGIONS);
                String letters = randomFrom(rng, TR_PLATE_LETTERS, rng.nextInt(3) + 1);
                yield city + " " + letters + " " + (rng.nextInt(9990) + 10);
            }
            case "UK" -> {
                String region = randomFrom(rng, UK_PLATE_LETTERS, 1) + randomFrom(rng, UK_PLATE_LETTERS, 1);
                String[] ages = {"23","73","24","74"};
                String age = pick(rng, ages);
                String rand = randomFrom(rng, UK_PLATE_RAND, 3);
                yield region + age + " " + rand;
            }
            case "DE" -> {
                String city = pick(rng, DE_PLATE_REGIONS);
                String letters = randomFrom(rng, DE_PLATE_LETTERS, rng.nextInt(2) + 1);
                yield city + "-" + letters + " " + (rng.nextInt(9999) + 1);
            }
            case "FR" -> {
                String l1 = randomFrom(rng, FR_PLATE_LETTERS, 2);
                String l2 = randomFrom(rng, FR_PLATE_LETTERS, 2);
                yield l1 + "-" + (rng.nextInt(900) + 100) + "-" + l2;
            }
            case "RU" -> {
                String region = pick(rng, RU_PLATE_REGIONS);
                String c1 = randomFrom(rng, RU_PLATE_CHARS, 1);
                String c2 = randomFrom(rng, RU_PLATE_CHARS, 2);
                yield c1 + (rng.nextInt(900) + 100) + c2 + " " + region;
            }
            case "US" -> String.valueOf(rng.nextInt(9) + 1) + randomFrom(rng, US_PLATE_LETTERS, 3) + (rng.nextInt(900) + 100);
            default   -> "PLATE-123";
        };
    }

    private static String randomFrom(ThreadLocalRandom rng, String chars, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }

    // ── Email ─────────────────────────────────────────────────────────────────

    static String email(ThreadLocalRandom rng, String locale) {
        String[] domains = EMAIL_DOMAINS.getOrDefault(locale, EMAIL_DOMAINS.get("TR"));
        String prefix = pick(rng, EMAIL_PREFIXES);
        int num = rng.nextInt(9900) + 100;
        return prefix + num + "@" + pick(rng, domains);
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }
}
