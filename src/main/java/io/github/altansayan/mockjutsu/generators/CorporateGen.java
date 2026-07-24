package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

/** Corporate — company names, job titles. Mirrors corporate.py. */
public final class CorporateGen {
    private CorporateGen() {}

    private static final String[] ADJ_TR  = {"Anadolu","Ege","Boğaz","İstanbul","Mavi","Altın","Yıldız","Güneş","Deniz","Türk"};
    private static final String[] NOUN_TR = {"Teknoloji","Yazılım","Finans","Enerji","Lojistik","Sanayi","Ticaret","Danışmanlık","İnşaat","Holding"};
    private static final String[] SFX_TR  = {"A.Ş.","Ltd. Şti.","Holding A.Ş.","Grup A.Ş.","A.Ş."};

    private static final String[] ADJ_US  = {"National","Pacific","Liberty","American","First","Premier","Global","United","Pioneer","Apex"};
    private static final String[] NOUN_US = {"Technologies","Solutions","Financial","Energy","Logistics","Industries","Commerce","Consulting","Holdings","Systems"};
    private static final String[] SFX_US  = {"LLC","Inc.","Corp.","& Associates LLC","Group Inc."};

    private static final String[] ADJ_UK  = {"Royal","British","Crown","Imperial","Northern","Southern","Central","Allied","United","National"};
    private static final String[] NOUN_UK = {"Technologies","Solutions","Finance","Energy","Logistics","Industries","Commerce","Consulting","Holdings","Systems"};
    private static final String[] SFX_UK  = {"Ltd.","PLC","& Co. Ltd.","LLP","Group Ltd."};

    private static final String[] ADJ_DE  = {"Deutsche","Berliner","Hamburger","Bayrische","Rheinische","Nord","Süd","West","Ost","Zentral"};
    private static final String[] NOUN_DE = {"Technologien","Lösungen","Finanzen","Energie","Logistik","Industrien","Handel","Beratung","Systeme","Holding"};
    private static final String[] SFX_DE  = {"GmbH","AG","KG","GmbH & Co. KG"};

    private static final String[] ADJ_FR  = {"Française","Parisienne","Nationale","Générale","Centrale","Atlantique","Méditerranée","Loire","Normandie","Alsace"};
    private static final String[] NOUN_FR = {"Technologies","Solutions","Finance","Énergie","Logistique","Industries","Commerce","Conseil","Systèmes","Groupe"};
    private static final String[] SFX_FR  = {"SARL","SA","SAS","SASU"};

    private static final String[] ADJ_RU  = {"Российский","Московский","Сибирский","Уральский","Северный","Южный","Восточный","Западный","Центральный","Национальный"};
    private static final String[] NOUN_RU = {"Технологии","Решения","Финанс","Энергия","Логистика","Индустрия","Торговля","Консалтинг","Системы","Холдинг"};
    private static final String[] SFX_RU  = {"ООО","АО","ПАО"};

    private static final String[] JOBS_TR = {
        "Yazılım Mühendisi","Kıdemli Yazılım Mühendisi","Ürün Müdürü","Proje Yöneticisi",
        "Veri Bilimcisi","DevOps Mühendisi","Mali Müşavir","Satış Müdürü",
        "İnsan Kaynakları Uzmanı","Finans Analisti","Operasyon Müdürü","Pazarlama Uzmanı",
        "Sistem Analisti","QA Mühendisi","İş Geliştirme Müdürü"
    };
    private static final String[] JOBS_US = {
        "Software Engineer","Senior Software Engineer","Product Manager","Project Manager",
        "Data Scientist","DevOps Engineer","Financial Advisor","Sales Manager",
        "HR Business Partner","Financial Analyst","Operations Manager","Marketing Specialist",
        "Systems Analyst","QA Engineer","Business Development Manager"
    };
    private static final String[] JOBS_UK = {
        "Software Engineer","Senior Developer","Product Manager","Programme Manager",
        "Data Analyst","Infrastructure Engineer","Financial Consultant","Account Manager",
        "HR Advisor","Finance Manager","Operations Director","Marketing Manager",
        "Business Analyst","Test Engineer","Commercial Director"
    };
    private static final String[] JOBS_DE = {
        "Softwareentwickler","Senior Entwickler","Produktmanager","Projektleiter",
        "Datenwissenschaftler","DevOps Ingenieur","Finanzberater","Vertriebsleiter",
        "Personalreferent","Controller","Betriebsleiter","Marketingspezialist",
        "Systemanalytiker","QA Ingenieur","Geschäftsentwickler"
    };
    private static final String[] JOBS_FR = {
        "Ingénieur Logiciel","Ingénieur Senior","Chef de Produit","Chef de Projet",
        "Data Scientist","Ingénieur DevOps","Conseiller Financier","Directeur Commercial",
        "Responsable RH","Analyste Financier","Directeur Opérations","Responsable Marketing",
        "Analyste Systèmes","Ingénieur QA","Développeur Business"
    };
    private static final String[] JOBS_RU = {
        "Разработчик ПО","Старший разработчик","Менеджер продукта","Руководитель проекта",
        "Аналитик данных","DevOps инженер","Финансовый консультант","Руководитель отдела продаж",
        "HR бизнес-партнёр","Финансовый аналитик","Операционный директор","Маркетолог",
        "Системный аналитик","Инженер QA","Директор по развитию бизнеса"
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "company_name"                  -> companyName(rng, locale);
            case "job_title","jobtitle","occupation" -> jobTitle(rng, locale);
            default -> "ERROR: Unknown corporate type '" + type + "'";
        };
    }

    private static String companyName(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "US" -> pick(rng, ADJ_US) + " " + pick(rng, NOUN_US) + " " + pick(rng, SFX_US);
            case "UK" -> pick(rng, ADJ_UK) + " " + pick(rng, NOUN_UK) + " " + pick(rng, SFX_UK);
            case "DE" -> pick(rng, ADJ_DE) + " " + pick(rng, NOUN_DE) + " " + pick(rng, SFX_DE);
            case "FR" -> pick(rng, ADJ_FR) + " " + pick(rng, NOUN_FR) + " " + pick(rng, SFX_FR);
            case "RU" -> pick(rng, SFX_RU) + " «" + pick(rng, ADJ_RU) + " " + pick(rng, NOUN_RU) + "»";
            default   -> pick(rng, ADJ_TR) + " " + pick(rng, NOUN_TR) + " " + pick(rng, SFX_TR);
        };
    }

    private static String jobTitle(ThreadLocalRandom rng, String locale) {
        return switch (locale) {
            case "US" -> pick(rng, JOBS_US);
            case "UK" -> pick(rng, JOBS_UK);
            case "DE" -> pick(rng, JOBS_DE);
            case "FR" -> pick(rng, JOBS_FR);
            case "RU" -> pick(rng, JOBS_RU);
            default   -> pick(rng, JOBS_TR);
        };
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) { return arr[rng.nextInt(arr.length)]; }
}
