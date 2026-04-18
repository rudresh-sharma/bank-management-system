package com.bms.config;

public final class DatabaseConfig {
    private static final String DEFAULT_SERVER_URL = "jdbc:mysql://localhost:3306";
    private static final String DEFAULT_DATABASE_NAME = "banking_management_system";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "Rudresh@2005";

    private DatabaseConfig() {
    }

    public static String getServerUrl() {
        return System.getenv().getOrDefault("BMS_DB_SERVER_URL", DEFAULT_SERVER_URL);
    }

    public static String getDatabaseName() {
        return System.getenv().getOrDefault("BMS_DB_NAME", DEFAULT_DATABASE_NAME);
    }

    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault(
                "BMS_DB_URL",
                getServerUrl() + "/" + getDatabaseName() + "?createDatabaseIfNotExist=true&serverTimezone=UTC"
        );
    }

    public static String getUsername() {
        return System.getenv().getOrDefault("BMS_DB_USERNAME", DEFAULT_USERNAME);
    }

    public static String getPassword() {
        return System.getenv().getOrDefault("BMS_DB_PASSWORD", DEFAULT_PASSWORD);
    }
}
