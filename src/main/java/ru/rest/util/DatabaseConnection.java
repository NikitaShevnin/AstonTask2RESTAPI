package ru.rest.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/database.properties"));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driverClassName"));

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void main(String[] args) {
        // Получаем DataSource
        DataSource dataSource = DatabaseConnection.getDataSource();

        try (Connection connection = dataSource.getConnection()) {
            // Проверяем подключение к базе данных
            if (connection != null) {
                System.out.println("Успешное подключение к базе данных!");

                // Выполняем SQL-запросы или другие операции с базой данных
                // ...

            } else {
                System.out.println("Не удалось подключиться к базе данных.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

