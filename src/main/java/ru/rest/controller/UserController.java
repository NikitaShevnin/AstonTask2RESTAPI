package ru.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.entity.User;
import ru.rest.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static ru.rest.service.UserService.*;
import static ru.rest.util.JsonResponseUserUtil.sendJsonResponse;

/**
 * Класс {@code UserController} отвечает за обработку запросов, связанных с пользователями.
 */
public class UserController {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Обрабатывает запрос на получение списка всех пользователей.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void getUsers(HttpExchange exchange) throws IOException {
        List<User> users = getUsersFromDatabase();
        sendJsonResponse(exchange, users);
    }

    /**
     * Обрабатывает запрос на получение пользователя по его идентификатору.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void getUserById (HttpExchange exchange) throws IOException {
        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        User user = UserService.getUserById(userId);
        if (user != null) {
            sendJsonResponse(exchange, user);
        } else {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("User not found".getBytes());
            }
        }
    }

    /**
     * Обрабатывает запрос на создание нового пользователя.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
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

    /**
     * Обрабатывает запрос на обновление пользователя.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
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

    /**
     * Обрабатывает запрос на удаление пользователя.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
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

    /**
     * Читает пользователя из входящего HTTP-запроса.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @return прочитанный пользователь или {@code null}, если данные запроса некорректны
     * @throws IOException если возникнут ошибки при чтении запроса
     */
    static User readUserFromRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return objectMapper.readValue(is, User.class);
        }
    }
}
