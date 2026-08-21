package com.demo.demo_integradora.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnector {

    private static HikariDataSource dataSource;

    static {
        try {
            ClassLoader classLoader = SQLConnector.class.getClassLoader();
            URL walletUrl = classLoader.getResource("Wallet/");

            if (walletUrl == null) {
                throw new RuntimeException("No se encontró la Wallet");
            }

            String walletPath = new File(walletUrl.toURI()).getAbsolutePath();
            walletPath = walletPath.replace("\\", "/");

            String dbUser = System.getenv("DB_USER");
            String dbPass = System.getenv("DB_PASS");
            String dbName = System.getenv("DB_NAME");

            if (dbUser == null || dbPass == null || dbName == null) {
                System.err.println("Advertencia: Faltan variables de entorno de la BD. Buscando en credentials.properties...");
                Properties creds = new Properties();
                try (InputStream is = classLoader.getResourceAsStream("credentials.properties")) {
                    if (is == null) {
                        throw new RuntimeException("No se encontró el archivo credentials.properties ni las variables de entorno de la base de datos.");
                    }
                    creds.load(is);

                    // Si ya se habían leído del entorno, conservamos ese valor; si no, del archivo
                    if (dbUser == null) dbUser = creds.getProperty("db.user");
                    if (dbPass == null) dbPass = creds.getProperty("db.pass");
                    if (dbName == null) dbName = creds.getProperty("db.name");
                }
            }

            // Validar que finalmente tengamos el nombre de la BD
            if (dbName == null) {
                throw new RuntimeException("El nombre de la base de datos (db.name / DB_NAME) no está configurado.");
            }
            System.out.println("DEBUG -> dbUser cargado: [" + dbUser + "]");
            System.out.println("DEBUG -> dbName cargado: [" + dbName + "]");
            System.out.println("DEBUG -> dbPass longitud: " + (dbPass != null ? dbPass.length() : "null"));

            // Configuración de Hikari (incluye conf de conexion y pool)
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("oracle.jdbc.OracleDriver");

            // Concatenamos la variable dbName dinámicamente aquí:
            config.setJdbcUrl("jdbc:oracle:thin:@" + dbName + "?TNS_ADMIN=" + walletPath);

            // Asignar los valores dinámicos y seguros
            config.setUsername(dbUser);
            config.setPassword(dbPass);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            System.out.println("DEBUG -> JDBC URL: " + config.getJdbcUrl());
            dataSource = new HikariDataSource(config);
            System.out.println("¡Pool de conexiones a la base de datos inicializado con éxito!");

        } catch (Exception e) {
            System.err.println("Error crítico al inicializar la base de datos");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection() {
        if(dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}