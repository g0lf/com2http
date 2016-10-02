package ru.connector.com2http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by g0lf on 02.10.2016.
 */
public class Settings {

    private final static Logger log = LoggerFactory.getLogger(Settings.class);

    private Properties properties = new Properties();

    public static final String COM_NAME = "com.name";
    public static final String COM_BAUDRATE = "com.baudrate";
    public static final String COM_DATABIT = "com.databit";
    public static final String COM_STOPBIT = "com.stopbit";
    public static final String COM_PARITY = "com.parity";

    public static final String HTTP_URL = "http.url";


    public Settings(String path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (Exception e) {
            log.error("can't read settings file");
            throw e;
        }
    }

    public int getIntParam(String paramName, int defaultValue) {
        String prop = properties.getProperty(paramName);
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (NumberFormatException nfe) {
                log.error("Property with name {} is not integer. Use default value {}", prop, defaultValue);
                return defaultValue;
            }
        }
        log.info("Property {} was not setted. Use default value {}", prop, defaultValue);
        return defaultValue;
    }

    public String getParam(String paramName, String defaultValue) {
        return properties.getProperty(paramName, defaultValue);
    }
}
