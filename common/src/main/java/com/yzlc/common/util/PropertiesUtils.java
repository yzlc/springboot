package com.yzlc.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {
    private static final String PROPERTIES_FILE_NAME = "application.properties";
    private static Properties properties = new Properties();

    static {
        try (InputStreamReader inputStream = new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME), "UTF-8")) {
            properties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties from " + PROPERTIES_FILE_NAME, ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
