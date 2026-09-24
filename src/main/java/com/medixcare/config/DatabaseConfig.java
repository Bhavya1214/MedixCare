package com.medixcare.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place for database connection settings, shared by the GUI (JavaFX)
 * and CLI applications.
 *
 * Resolution order for every setting, highest priority first:
 *   1. src/main/resources/config.properties (copy config.properties.example and edit it)
 *   2. Environment variable
 *   3. Built-in default (matches each app's original hardcoded value)
 *
 * Nothing sensitive is hardcoded or committed: config.properties is
 * git-ignored, so credentials never end up in version control.
 */
public final class DatabaseConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read config.properties, falling back to environment " +
                    "variables / defaults: " + e.getMessage());
        }
    }

    private DatabaseConfig() {
    }

    private static String resolve(String propertyKey, String envVar, String defaultValue) {
        String fromProps = PROPS.getProperty(propertyKey);
        if (fromProps != null && !fromProps.isBlank()) {
            return fromProps;
        }
        String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return defaultValue;
    }

    private static String user() {
        return resolve("db.user", "MEDIXCARE_DB_USER", "root");
    }

    private static String password() {
        return resolve("db.password", "MEDIXCARE_DB_PASSWORD", "");
    }

    /** Connection for the JavaFX GUI app (originally jdbc:mysql://localhost:3306/HMS). */
    public static Connection getGuiConnection() throws SQLException {
        String url = resolve("gui.db.url", "MEDIXCARE_GUI_DB_URL", "jdbc:mysql://localhost:3306/HMS");
        return DriverManager.getConnection(url, user(), password());
    }

    /** Connection for the console/CLI app (originally jdbc:mysql://localhost:3306/MedixCare). */
    public static Connection getCliConnection() throws SQLException {
        String url = resolve("cli.db.url", "MEDIXCARE_CLI_DB_URL", "jdbc:mysql://localhost:3306/MedixCare");
        return DriverManager.getConnection(url, user(), password());
    }
}
