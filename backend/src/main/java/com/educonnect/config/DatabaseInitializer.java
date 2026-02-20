package com.educonnect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates the SQL Server database if it does not exist, before the main DataSource connects.
 * Only runs when using SQL Server (URL contains "sqlserver") and when
 * app.datasource.auto-create-database is true (default: true).
 * Skipped for H2 (e.g. dev profile) and when the property is false.
 */
@Component
@Order(Integer.MIN_VALUE) // run as early as possible
@ConditionalOnProperty(name = "app.datasource.auto-create-database", havingValue = "true", matchIfMissing = true)
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final Pattern DATABASE_NAME = Pattern.compile("(?i)databaseName=([^;]+)");

    private final Environment environment;

    public DatabaseInitializer(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void createDatabaseIfNotExists() {
        String url = environment.getProperty("spring.datasource.url", "");
        if (!url.contains("sqlserver")) {
            return; // H2, PostgreSQL, etc. – no auto-create for them here
        }

        Matcher m = DATABASE_NAME.matcher(url);
        if (!m.find()) {
            log.debug("No databaseName in URL, skipping auto-create");
            return;
        }
        String databaseName = m.group(1).trim();

        // Connect to server without database (defaults to 'master')
        String urlToMaster = url.replaceAll("(?i)databaseName=[^;]+;?", "").replaceAll(";+$", "");
        if (!urlToMaster.contains(";")) {
            urlToMaster += ";";
        }

        try {
            Class.forName(environment.getProperty("spring.datasource.driver-class-name",
                    "com.microsoft.sqlserver.jdbc.SQLServerDriver"));
        } catch (ClassNotFoundException e) {
            log.warn("SQL Server driver not found, skipping database auto-create: {}", e.getMessage());
            return;
        }

        boolean useIntegratedAuth = url.contains("integratedSecurity=true");
        try {
            Connection conn = useIntegratedAuth
                    ? DriverManager.getConnection(urlToMaster)
                    : DriverManager.getConnection(urlToMaster,
                            environment.getProperty("spring.datasource.username", "sa"),
                            environment.getProperty("spring.datasource.password", ""));
            try (conn; Statement stmt = conn.createStatement()) {
                String sql = "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'" + databaseName + "')\n"
                        + "  CREATE DATABASE [" + databaseName + "]";
                stmt.execute(sql);
                log.info("Database '{}' ensured (created if missing).", databaseName);
            }
        } catch (Exception e) {
            log.warn("Could not auto-create database '{}'. Ensure it exists or create it manually: {}",
                    databaseName, e.getMessage());
        }
    }
}
