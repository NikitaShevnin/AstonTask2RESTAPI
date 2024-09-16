package ru.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public static void getUsers(HttpExchange exchange) throws IOException {
        List<User> users = getUsersFromDatabase();
        sendJsonResponse(exchange, users);
    }

    public static void getUserById (HttpExchange exchange) throws IOException {
        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        User user = getUserById(userId);
        if (user != null) {
            sendJsonResponse(exchange, user);
        } else {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("User not found".getBytes());
            }
        }
    }

    public static void createUser(HttpExchange exchange) throws IOException {
        User newUser = readUserFromRequest(exchange);
        if (newUser != null) {
            int userId = insertUserIntoDatabase(newUser);
            newUser.setId(userId);
            sendJsonResponse(exchange, newUser);
        } else {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Invalid user data".getBytes());
            }
        }
    }

    public static void updateUser(HttpExchange exchange) throws IOException {
        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        User updatedUser = readUserFromRequest(exchange);
        if (updatedUser != null) {
            updatedUser.setId(userId);
            if (updateUserInDatabase(updatedUser)) {
                sendJsonResponse(exchange, updatedUser);
            } else {
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("User not found".getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Invalid user data".getBytes());
            }
        }
    }

    public static void deleteUser(HttpExchange exchange) throws IOException {
        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (deleteUserFromDatabase(userId)) {
            exchange.sendResponseHeaders(204, 0);
        } else {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("User not found".getBytes());
            }
        }
    }

    private static List<User> getUsersFromDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, name, email FROM users";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                ));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static User getUserById(int userId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, name, email FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static User readUserFromRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return objectMapper.readValue(is, User.class);
        }
    }

    private static int insertUserIntoDatabase(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static boolean updateUserInDatabase(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean deleteUserFromDatabase(int userId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, Object object) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = objectMapper.writeValueAsBytes(object);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
