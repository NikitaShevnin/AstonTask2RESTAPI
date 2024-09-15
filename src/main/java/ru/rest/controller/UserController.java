package ru.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс UserController отвечает за обработку HTTP-запросов, связанных с пользователями.
 * Контроллер на прямую работает с базой данных без использования сервисов.
 */
public class UserController {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    /**
     * Получает информацию о конкретном пользователе по его ID, указанном в URL-пути.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     * @throws IOException если произошла ошибка ввода-вывода при записи ответа
     */
    public static void getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getPathInfo().substring(1));
        List<User> users = getUsersFromDatabase();
        User user = null;
        for (User u : users) {
            if (u.getId() == userId) {
                user = u;
                break;
            }
        }
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), user);
    }

    /**
     * Получает список всех пользователей из базы данных.
     *
     * @return список пользователей
     */
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


    /**
     * Получает список всех пользователей и отправляет их в виде JSON-ответа.
     *
     * @param exchange объект HttpExchange
     * @throws IOException если произошла ошибка ввода-вывода при записи ответа
     */
    public static void getUsers(HttpExchange exchange) throws IOException {
        List<User> users = getUsersFromDatabase();
        sendJsonResponse(exchange, users);
    }

    /**
     * Получает информацию о конкретном пользователе по его ID и отправляет ее в виде JSON-ответа.
     *
     * @param exchange объект HttpExchange
     * @throws IOException если произошла ошибка ввода-вывода при записи ответа
     */
    public static void getUser(HttpExchange exchange) throws IOException {

        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        List<User> users = getUsersFromDatabase();
        User user = null;
        for (User u : users) {
            if (u.getId() == userId) {
                user = u;
                break;
            }
        }
        sendJsonResponse(exchange, user);
    }

    /**
     * Отправляет объект в формате JSON в качестве ответа на HTTP-запрос.
     *
     * @param exchange объект HttpExchange
     * @param data      объект, который необходимо отправить в виде JSON-ответа
     * @throws IOException если произошла ошибка ввода-вывода при записи ответа
     */
    private static void sendJsonResponse(HttpExchange exchange, Object data) throws IOException {
        byte[] response = objectMapper.writeValueAsBytes(data);
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
