package ru.rest.dataBaseCreate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Вспомогательный класс для настройки и заполнения базы данных MySQL для Java-приложения.
 */
public class MySQLSetup {
    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Создаем базу данных "Task2RestApi"
            createDatabase(connection);

            // Обновляем URL для подключения к новой базе данных
            String dbUrl = "jdbc:mysql://localhost:3306/Task2RestApi";
            try (Connection dbConnection = DriverManager.getConnection(dbUrl, username, password)) {
                // Создаем таблицу "users"
                createUsersTable(dbConnection);

                // Создаем таблицу "orders"
                createOrdersTable(dbConnection);

                // Вставляем примерных пользователей
                insertUsers(dbConnection);

                // Вставляем примерные заказы
                insertOrders(dbConnection);

                System.out.println("Настройка и заполнение базы данных выполнено успешно.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает базу данных "Task2RestApi".
     *
     * @param connection подключение к серверу базы данных
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void createDatabase(Connection connection) throws SQLException {
        String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS Task2RestApi";
        executeUpdate(connection, createDatabaseQuery);
    }

    /**
     * Создает таблицу "users" в базе данных.
     *
     * @param connection подключение к базе данных
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void createUsersTable(Connection connection) throws SQLException {
        String createUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "email VARCHAR(50) NOT NULL)";
        executeUpdate(connection, createUsersTableQuery);
    }

    /**
     * Создает таблицу "orders" в базе данных.
     *
     * @param connection подключение к базе данных
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void createOrdersTable(Connection connection) throws SQLException {
        String createOrdersTableQuery = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "product VARCHAR(50) NOT NULL," +
                "user_id INT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id))";
        executeUpdate(connection, createOrdersTableQuery);
    }

/**
 * Вставляет примерные данные пользователей в таблицу "users".
 *
 * @param connection подключение к базе данных
 * @throws SQLException если возникает ошибка при выполнении SQL-запроса
 */

private static void insertUsers(Connection connection) throws SQLException {
    insertUser(connection, "John Doe", "john.doe@example.com");
    insertUser(connection, "Jane Smith", "jane.smith@example.com");
    insertUser(connection, "Bob Johnson", "bob.johnson@example.com");
    insertUser(connection, "Sarah Lee", "sarah.lee@example.com");
    insertUser(connection, "Tom Williams", "tom.williams@example.com");
}

    /**
     * Вставляет примерные данные заказов в таблицу "orders".
     *
     * @param connection подключение к базе данных
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void insertOrders(Connection connection) throws SQLException {
        insertOrder(connection, "Product A", 1);
        insertOrder(connection, "Product B", 1);
        insertOrder(connection, "Product C", 2);
        insertOrder(connection, "Product D", 2);
        insertOrder(connection, "Product E", 3);
        insertOrder(connection, "Product F", 3);
        insertOrder(connection, "Product G", 4);
        insertOrder(connection, "Product H", 4);
        insertOrder(connection, "Product I", 5);
        insertOrder(connection, "Product J", 5);
    }

    /**
     * Выполняет обновление базы данных (INSERT, UPDATE, DELETE).
     *
     * @param connection подключение к базе данных
     * @param query SQL-запрос для выполнения
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void executeUpdate(Connection connection, String query) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

    /**
     * Вставляет пользователя в таблицу "users".
     *
     * @param connection подключение к базе данных
     * @param name имя пользователя
     * @param email электронная почта пользователя
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void insertUser(Connection connection, String name, String email) throws SQLException {
        String insertUserQuery = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertUserQuery)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.executeUpdate();
        }
    }

    /**
     * Вставляет заказ в таблицу "orders".
     *
     * @param connection подключение к базе данных
     * @param product наименование продукта
     * @param userId идентификатор пользователя, связанного с заказом
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private static void insertOrder(Connection connection, String product, int userId) throws SQLException {
        String insertOrderQuery = "INSERT INTO orders (product, user_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertOrderQuery)) {
            statement.setString(1, product);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
}
