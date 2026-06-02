package com.gamevault.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                log.warn("application.properties introuvable – valeurs par défaut utilisées");
            }
        } catch (IOException e) {
            log.error("Impossible de charger application.properties", e);
        }
    }

    private AppConfig() {}

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
