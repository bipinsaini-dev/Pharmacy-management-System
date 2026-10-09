package com.pharmacare.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates JDBC connections. Settings come from the environment variables
 * DB_URL, DB_USERNAME, DB_PASSWORD, or fall back to /db.properties on the classpath.
 */
public final class DBConnection {
    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);
    private static final String url;
    private static final String user;
    private static final String pass;

    static {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            log.error("Could not read db.properties", e);
        }
        url = pick("DB_URL", p, "db.url", "jdbc:mysql://localhost:3306/pharmacy_management");
        user = pick("DB_USERNAME", p, "db.username", "root");
        pass = pick("DB_PASSWORD", p, "db.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error("MySQL JDBC driver not found on classpath", e);
        }
    }

    private DBConnection() { }

    private static String pick(String env, Properties p, String key, String def) {
        String v = System.getenv(env);
        if (v != null && !v.isBlank()) return v;
        v = p.getProperty(key);
        return (v != null) ? v.trim() : def;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
