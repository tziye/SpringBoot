package com.common.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyUtil {

    static ObjectMapper objectMapper = new ObjectMapper();
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Random random = new Random();

    public static void log(Object object) {
        log.info("验证：{}", toString(object));
    }

    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Map toMap(Object object) {
        return parse(toString(object), Map.class);
    }

    public static <T> T parse(String str, Class<T> target) {
        try {
            return objectMapper.readValue(str, target);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parse(Map map, Class<T> target) {
        return parse(toString(map), target);
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return (List) objectMapper.readValue(json, type);
        } catch (IOException var4) {
            throw new IllegalArgumentException(var4);
        }
    }

    public static Date toDate(String str) {
        try {
            return DATE_FORMAT.parse(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String formatDate(Date date) {
        try {
            return DATE_FORMAT.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Date toTime(String str) {
        try {
            return TIME_FORMAT.parse(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String formatTime(Date date) {
        try {
            return TIME_FORMAT.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static int random(int n) {
        return random.nextInt(n);
    }

    public static boolean random() {
        return random.nextBoolean();
    }

    public static String randomUpLetter(int n) {
        return String.valueOf((char) (65 + random(n)));
    }

    public static String randomLowLetter(int n) {
        return String.valueOf((char) (97 + random(n)));
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleepMilli(long seconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleepRandom(int n) {
        try {
            TimeUnit.SECONDS.sleep(random(n));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
