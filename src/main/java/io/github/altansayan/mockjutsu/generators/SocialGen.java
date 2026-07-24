package io.github.altansayan.mockjutsu.generators;

import java.util.concurrent.ThreadLocalRandom;

public final class SocialGen {
    private SocialGen() {}

    private static final String[] ADJECTIVES = {
        "cool","super","pro","mega","ultra","fast","smart","lazy","happy","dark",
        "swift","silent","bright","bold","sharp","wild","calm","quick","lone","epic"
    };
    private static final String[] NOUNS = {
        "cat","dog","dev","coder","ninja","guru","wizard","hacker","pixel","byte",
        "fox","wolf","hawk","lion","bear","tiger","eagle","shark","viper","storm"
    };
    private static final String[] TOPICS = {
        "tech","ai","dev","code","startup","fintech","java","mock","test","data",
        "cloud","devops","python","security","mobile","backend","frontend","api","ux","infra"
    };
    private static final String[] BIO_TEMPLATES = {
        "Building the future one line of code at a time.",
        "Developer, maker, and coffee enthusiast.",
        "Turning ideas into products since day one.",
        "Passionate about technology and design.",
        "Open source advocate. Always learning.",
        "Entrepreneur | Engineer | Dreamer.",
        "Making the web a better place.",
        "Data nerd. Problem solver. Cat person.",
        "Exploring the intersection of AI and creativity.",
        "Full-stack developer by day, gamer by night.",
        "Startup founder. Failed fast, learned faster.",
        "Minimalist. Futurist. Software craftsman.",
        "I ship products people actually use.",
        "Code. Coffee. Repeat.",
        "Engineering manager who still loves to code."
    };

    public static String generate(String type, String locale) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        return switch (type) {
            case "username"       -> username(rng);
            case "handle"         -> "@" + username(rng);
            case "hashtag"        -> hashtag(rng);
            case "bio"            -> bio(rng);
            case "follower_count" -> String.valueOf(followerCount(rng));
            default -> "ERROR: Unknown social type '" + type + "'";
        };
    }

    private static String username(ThreadLocalRandom rng) {
        String result;
        int strategy = rng.nextInt(3);
        if (strategy == 0) {
            // adj + noun
            result = pick(rng, ADJECTIVES) + pick(rng, NOUNS);
        } else if (strategy == 1) {
            // noun + 1000-9999
            result = pick(rng, NOUNS) + (1000 + rng.nextInt(9000));
        } else {
            // adj + noun + 10-99
            result = pick(rng, ADJECTIVES) + pick(rng, NOUNS) + (10 + rng.nextInt(90));
        }
        // clamp to 4-15 chars
        if (result.length() < 4) result = result + "user";
        if (result.length() > 15) result = result.substring(0, 15);
        return result;
    }

    private static String hashtag(ThreadLocalRandom rng) {
        String topic = pick(rng, TOPICS);
        if (rng.nextBoolean()) {
            return "#" + topic;
        } else {
            return "#" + topic + (1000 + rng.nextInt(9000));
        }
    }

    private static String bio(ThreadLocalRandom rng) {
        return BIO_TEMPLATES[rng.nextInt(BIO_TEMPLATES.length)];
    }

    private static int followerCount(ThreadLocalRandom rng) {
        int tier = rng.nextInt(100);
        if (tier < 40) return rng.nextInt(1, 500);
        if (tier < 65) return rng.nextInt(4500) + 500;
        if (tier < 80) return rng.nextInt(45000) + 5000;
        if (tier < 92) return rng.nextInt(450000) + 50000;
        if (tier < 98) return rng.nextInt(4500000) + 500000;
        return rng.nextInt(45000000) + 5000000;
    }

    private static <T> T pick(ThreadLocalRandom rng, T[] arr) { return arr[rng.nextInt(arr.length)]; }
}
