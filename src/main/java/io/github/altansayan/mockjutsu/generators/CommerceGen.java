package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Commerce — Currency, Tax Rate, Invoice, VIN, Vehicle. Mirrors commerce.py. */
public final class CommerceGen {
    private CommerceGen() {}

    private static final java.util.Map<String, String> CURRENCIES = java.util.Map.of(
        "TR", "TRY", "US", "USD", "UK", "GBP", "DE", "EUR", "FR", "EUR", "RU", "RUB"
    );

    // locale -> standard tax rate string ("null" sentinel means US-style random)
    private static final java.util.Map<String, String> TAX_RATES = java.util.Map.of(
        "TR", "20", "UK", "20", "DE", "19", "FR", "20", "RU", "20"
    );

    private static final java.util.Map<String, String> INVOICE_PREFIXES = java.util.Map.of(
        "TR", "INV", "US", "INV", "UK", "INV", "DE", "RE", "FR", "FACT", "RU", "СФ"
    );

    // VIN transliteration table (ISO 3779) — I, O, Q not used
    private static final java.util.Map<Character, Integer> VIN_TRANS = new java.util.HashMap<>();
    static {
        int[] vals = {1,2,3,4,5,6,7,8,1,2,3,4,5,7,9,2,3,4,5,6,7,8};
        char[] chs = {'A','B','C','D','E','F','G','H','J','K','L','M','N','P','R','S','T','U','V','W','X','Y'};
        // NOTE: Z is handled separately below to keep this array aligned 1:1 with letters.
        for (int i = 0; i < chs.length; i++) VIN_TRANS.put(chs[i], vals[i]);
        VIN_TRANS.put('Z', 9);
    }
    private static final int[] VIN_WEIGHTS = {8,7,6,5,4,3,2,10,0,9,8,7,6,5,4,3,2};
    private static final String VIN_CHARS = "0123456789ABCDEFGHJKLMNPRSTUVWXYZ";
    private static final String MODEL_YEAR_CHARS = "ABCDEFGHJKLMNPRSTVWXY123456789";

    private static final java.util.Map<String, String[]> WMI_CODES = java.util.Map.of(
        "TR", new String[]{"NM0","NM1","NMT"},
        "US", new String[]{"1HG","1G1","4T1","1FA","1GC","5YJ"},
        "UK", new String[]{"SAL","SAJ","SAR","SCA"},
        "DE", new String[]{"WBA","WDB","WVW","WAU","WP0"},
        "FR", new String[]{"VF1","VF3","VF7","VFA"},
        "RU", new String[]{"XTA","XUF","X9F","XWB"}
    );

    private static final class VehicleData {
        String[] makes;
        java.util.Map<String, String[]> models;
        String[] colors;
        String[] fuel;
    }
    private static final java.util.Map<String, VehicleData> VEHICLES = new java.util.HashMap<>();
    static {
        VehicleData tr = new VehicleData();
        tr.makes = new String[]{"Renault","Fiat","Ford","Volkswagen","Toyota","Hyundai","Honda","Peugeot"};
        tr.models = new java.util.HashMap<>();
        tr.models.put("Renault", new String[]{"Clio","Megane","Symbol","Kadjar","Captur"});
        tr.models.put("Fiat", new String[]{"Egea","Doblò","Fiorino"});
        tr.models.put("Ford", new String[]{"Focus","Fiesta","Kuga","Puma"});
        tr.models.put("Volkswagen", new String[]{"Passat","Golf","Polo","Tiguan"});
        tr.models.put("Toyota", new String[]{"Corolla","Yaris","RAV4","C-HR"});
        tr.models.put("Hyundai", new String[]{"i20","Tucson","i10","Kona"});
        tr.models.put("Honda", new String[]{"Civic","CR-V","Jazz"});
        tr.models.put("Peugeot", new String[]{"208","2008","308","3008"});
        tr.colors = new String[]{"Beyaz","Siyah","Gri","Gümüş","Kırmızı","Mavi","Lacivert","Bej"};
        tr.fuel = new String[]{"Benzin","Dizel","Hibrit","Elektrik","LPG"};
        VEHICLES.put("TR", tr);

        VehicleData us = new VehicleData();
        us.makes = new String[]{"Ford","Chevrolet","Toyota","Honda","Tesla","GMC","Ram","Dodge"};
        us.models = new java.util.HashMap<>();
        us.models.put("Ford", new String[]{"F-150","Explorer","Mustang","Escape"});
        us.models.put("Chevrolet", new String[]{"Silverado","Equinox","Malibu","Tahoe"});
        us.models.put("Toyota", new String[]{"Camry","RAV4","Corolla","Highlander"});
        us.models.put("Honda", new String[]{"Accord","Civic","CR-V","Pilot"});
        us.models.put("Tesla", new String[]{"Model 3","Model Y","Model S","Model X"});
        us.models.put("GMC", new String[]{"Sierra","Terrain","Acadia"});
        us.models.put("Ram", new String[]{"1500","2500","ProMaster"});
        us.models.put("Dodge", new String[]{"Charger","Challenger","Durango"});
        us.colors = new String[]{"White","Black","Silver","Gray","Red","Blue","Navy","Pearl"};
        us.fuel = new String[]{"Gasoline","Diesel","Hybrid","Electric","Flex Fuel"};
        VEHICLES.put("US", us);

        VehicleData uk = new VehicleData();
        uk.makes = new String[]{"Vauxhall","Ford","Volkswagen","BMW","Audi","Land Rover","Jaguar","Mini"};
        uk.models = new java.util.HashMap<>();
        uk.models.put("Vauxhall", new String[]{"Corsa","Astra","Mokka","Insignia"});
        uk.models.put("Ford", new String[]{"Fiesta","Focus","Kuga","Puma"});
        uk.models.put("BMW", new String[]{"3 Series","5 Series","X3","X5"});
        uk.models.put("Audi", new String[]{"A3","A4","Q3","Q5"});
        uk.models.put("Land Rover", new String[]{"Defender","Discovery","Range Rover","Freelander"});
        uk.models.put("Volkswagen", new String[]{"Polo","Golf","Tiguan","Passat"});
        uk.models.put("Jaguar", new String[]{"XE","XF","E-Pace","F-Pace"});
        uk.models.put("Mini", new String[]{"Hatch","Countryman","Clubman"});
        uk.colors = new String[]{"White","Black","Silver","Grey","Red","Blue","Green","Bronze"};
        uk.fuel = new String[]{"Petrol","Diesel","Hybrid","Electric","Mild Hybrid"};
        VEHICLES.put("UK", uk);

        VehicleData de = new VehicleData();
        de.makes = new String[]{"Volkswagen","BMW","Mercedes-Benz","Audi","Opel","Ford","Porsche","Skoda"};
        de.models = new java.util.HashMap<>();
        de.models.put("Volkswagen", new String[]{"Golf","Polo","Passat","Tiguan","T-Roc"});
        de.models.put("BMW", new String[]{"3er","5er","X3","1er","X5"});
        de.models.put("Mercedes-Benz", new String[]{"C-Klasse","E-Klasse","GLC","A-Klasse","GLE"});
        de.models.put("Audi", new String[]{"A4","A3","Q5","Q3","A6"});
        de.models.put("Opel", new String[]{"Corsa","Astra","Mokka","Insignia"});
        de.models.put("Ford", new String[]{"Focus","Fiesta","Kuga","Puma"});
        de.models.put("Porsche", new String[]{"911","Cayenne","Macan","Panamera"});
        de.models.put("Skoda", new String[]{"Octavia","Fabia","Kodiaq","Karoq"});
        de.colors = new String[]{"Weiß","Schwarz","Silber","Grau","Rot","Blau","Beige","Grün"};
        de.fuel = new String[]{"Benzin","Diesel","Hybrid","Elektro","Erdgas"};
        VEHICLES.put("DE", de);

        VehicleData fr = new VehicleData();
        fr.makes = new String[]{"Renault","Peugeot","Citroën","Volkswagen","Toyota","Ford","BMW","Dacia"};
        fr.models = new java.util.HashMap<>();
        fr.models.put("Renault", new String[]{"Clio","Mégane","Kadjar","Captur","Zoé"});
        fr.models.put("Peugeot", new String[]{"208","2008","308","3008","5008"});
        fr.models.put("Citroën", new String[]{"C3","C4","C5 Aircross","Berlingo"});
        fr.models.put("Volkswagen", new String[]{"Golf","Polo","Tiguan","Passat"});
        fr.models.put("Toyota", new String[]{"Yaris","Corolla","RAV4","C-HR"});
        fr.models.put("BMW", new String[]{"Série 3","Série 5","X3","X1"});
        fr.models.put("Dacia", new String[]{"Sandero","Duster","Logan","Spring"});
        fr.models.put("Ford", new String[]{"Fiesta","Focus","Puma","Kuga"});
        fr.colors = new String[]{"Blanc","Noir","Gris","Argent","Rouge","Bleu","Marine","Beige"};
        fr.fuel = new String[]{"Essence","Diesel","Hybride","Électrique","GPL"};
        VEHICLES.put("FR", fr);

        VehicleData ru = new VehicleData();
        ru.makes = new String[]{"АвтоВАЗ","Toyota","Hyundai","Kia","Volkswagen","УАЗ","GAZ","Skoda"};
        ru.models = new java.util.HashMap<>();
        ru.models.put("АвтоВАЗ", new String[]{"Гранта","Веста","XRAY","Нива Тревел"});
        ru.models.put("Toyota", new String[]{"Camry","RAV4","Land Cruiser","Corolla"});
        ru.models.put("Hyundai", new String[]{"Solaris","Creta","Tucson","Sonata"});
        ru.models.put("Kia", new String[]{"Rio","Sportage","Ceed","K5"});
        ru.models.put("Volkswagen", new String[]{"Polo","Tiguan","Passat","Golf"});
        ru.models.put("УАЗ", new String[]{"Патриот","Hunter","Буханка"});
        ru.models.put("GAZ", new String[]{"Газель Next","Газель Бизнес"});
        ru.models.put("Skoda", new String[]{"Octavia","Rapid","Kodiaq","Karoq"});
        ru.colors = new String[]{"Белый","Чёрный","Серебристый","Серый","Красный","Синий","Золотой","Зелёный"};
        ru.fuel = new String[]{"Бензин","Дизель","Гибрид","Электро","Газ"};
        VEHICLES.put("RU", ru);
    }

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        String loc = locale.toUpperCase(java.util.Locale.ROOT);
        return switch (type) {
            case "currency"                       -> currency(loc);
            case "tax_rate","taxrate"             -> taxRate(rng, loc);
            case "invoice_number","invoicenumber" -> invoiceNumber(rng, loc);
            case "vin"                            -> vin(rng, loc);
            case "vehicle"                        -> vehicle(rng, loc);
            default -> "ERROR: Unknown commerce type '" + type + "'";
        };
    }

    private static String currency(String loc) {
        return CURRENCIES.getOrDefault(loc, CURRENCIES.get("TR"));
    }

    private static String taxRate(ThreadLocalRandom rng, String loc) {
        if ("US".equals(loc)) {
            double v = Math.round(rng.nextDouble(0, 10.25) * 100.0) / 100.0;
            return String.format(java.util.Locale.US, "%.2f", v);
        }
        return TAX_RATES.getOrDefault(loc, TAX_RATES.get("TR"));
    }

    // invoice_number — locale-specific format (unchanged; not a reported mismatch)
    private static String invoiceNumber(ThreadLocalRandom rng, String locale) {
        int year  = java.time.LocalDate.now().getYear();
        int month = java.time.LocalDate.now().getMonthValue();
        int day   = java.time.LocalDate.now().getDayOfMonth();
        int n     = 1 + rng.nextInt(999999);
        return switch (locale) {
            case "TR" -> String.format("INV-%d-%06d", year, n);
            case "US" -> String.format("INV-%d%02d%02d-%04d", year, month, day, rng.nextInt(10000));
            case "UK" -> String.format("INV/%d/%06d", year, n);
            case "DE" -> String.format("RE-%d/%05d", year, rng.nextInt(100000));
            case "FR" -> String.format("FACT-%d-%06d", year, n);
            case "RU" -> String.format("СФ-%d-%06d", year, n);
            default   -> String.format("INV-%d-%06d", year, n);
        };
    }

    // VIN — ISO 3779, WMI per locale, real check digit algorithm
    private static String vin(ThreadLocalRandom rng, String loc) {
        String[] wmiPool = WMI_CODES.getOrDefault(loc, WMI_CODES.get("TR"));
        String wmi = wmiPool[rng.nextInt(wmiPool.length)];

        StringBuilder vds = new StringBuilder(5);
        for (int i = 0; i < 5; i++) vds.append(VIN_CHARS.charAt(rng.nextInt(VIN_CHARS.length())));
        char modelYear = MODEL_YEAR_CHARS.charAt(rng.nextInt(MODEL_YEAR_CHARS.length()));
        char plant = VIN_CHARS.charAt(rng.nextInt(VIN_CHARS.length()));
        int seq = rng.nextInt(900000) + 100000;

        char[] partial = (wmi + vds + "0" + modelYear + plant + seq).toCharArray();
        int total = 0;
        for (int i = 0; i < 17; i++) {
            char c = partial[i];
            int val = Character.isDigit(c) ? (c - '0') : VIN_TRANS.getOrDefault(c, 0);
            total += val * VIN_WEIGHTS[i];
        }
        int check = total % 11;
        partial[8] = check == 10 ? 'X' : (char) ('0' + check);
        return new String(partial);
    }

    // vehicle — Python-repr dict (CLI prints str(dict) for non-string return types)
    private static String vehicle(ThreadLocalRandom rng, String loc) {
        VehicleData data = VEHICLES.getOrDefault(loc, VEHICLES.get("TR"));
        String make = data.makes[rng.nextInt(data.makes.length)];
        String[] modelPool = data.models.getOrDefault(make, new String[]{make});
        String model = modelPool[rng.nextInt(modelPool.length)];
        int year = rng.nextInt(27) + 2000;
        String vinVal = vin(rng, loc);
        String color = data.colors[rng.nextInt(data.colors.length)];
        String fuel = data.fuel[rng.nextInt(data.fuel.length)];

        return "{'make': " + pyStr(make) + ", 'model': " + pyStr(model) + ", 'year': " + year +
            ", 'vin': " + pyStr(vinVal) + ", 'color': " + pyStr(color) + ", 'fuel': " + pyStr(fuel) + "}";
    }

    private static String pyStr(String s) {
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }
}
