package ru.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.entity.Order;

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

/**
 * Класс {@code OrderController} отвечает за обработку запросов, связанных с заказами.
 *
 * @author [Ваше Имя]
 * @version 1.0
 */
public class OrderController {
    public static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    /**
     * Обрабатывает запрос на получение списка всех заказов.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void getOrders(HttpExchange exchange) throws IOException {
        List<Order> orders = getAllOrders();
        sendJsonResponse(exchange, orders);
    }

    /**
     * Обрабатывает запрос на получение заказа по его идентификатору.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void getOrderById(HttpExchange exchange) throws IOException {
        int orderId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        Order order = getOrderById(orderId);
        if (order != null) {
            sendJsonResponse(exchange, order);
        } else {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Order not found".getBytes());
            }
        }
    }

    /**
     * Обрабатывает запрос на создание нового заказа.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void createOrder(HttpExchange exchange) throws IOException {
        Order newOrder = readOrderFromRequest(exchange);
        if (newOrder != null) {
            int orderId = insertOrderIntoDatabase(newOrder);
            newOrder.setId(orderId);
            sendJsonResponse(exchange, newOrder);
        } else {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Invalid order data".getBytes());
            }
        }
    }

    /**
     * Обрабатывает запрос на обновление заказа.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void updateOrder(HttpExchange exchange) throws IOException {
        int orderId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        Order updatedOrder = readOrderFromRequest(exchange);
        if (updatedOrder != null) {
            updatedOrder.setId(orderId);
            if (updateOrderInDatabase(updatedOrder)) {
                sendJsonResponse(exchange, updatedOrder);
            } else {
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("Order not found".getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Invalid order data".getBytes());
            }
        }
    }

    /**
     * Обрабатывает запрос на удаление заказа.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void deleteOrder(HttpExchange exchange) throws IOException {
        int orderId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        if (deleteOrderFromDatabase(orderId)) {
            exchange.sendResponseHeaders(204, 0);
        } else {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("Order not found".getBytes());
            }
        }
    }

    /**
     * Обрабатывает запрос на получение списка заказов по идентификатору пользователя.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
    public static void getOrdersByUserId(HttpExchange exchange) throws IOException {
        int userId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        List<Order> orders = getOrdersByUserId(userId);
        sendJsonResponse(exchange, orders);
    }

    /**
     * Получает все заказы из базы данных.
     *
     * @return список всех заказов. Если произошла ошибка при доступе к базе данных, возвращается null.
     */
    private static List<Order> getAllOrders() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                ));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Получает заказ по его идентификатору.
     *
     * @param orderId идентификатор заказа, который нужно получить.
     * @return объект заказа с указанным идентификатором или null, если заказ не найден или произошла ошибка.
     */
    private static Order getOrderById(int orderId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Читает заказ из тела HTTP-запроса.
     *
     * @param exchange объект HttpExchange, содержащий данные запроса.
     * @return объект заказа, созданный из данных запроса.
     * @throws IOException если произошла ошибка при чтении данных из запроса.
     */
    private static Order readOrderFromRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return objectMapper.readValue(is, Order.class);
        }
    }

    /**
     * Вставляет новый заказ в базу данных.
     *
     * @param order объект заказа, который нужно вставить.
     * @return идентификатор вставленного заказа или -1 в случае ошибки.
     */
    private static int insertOrderIntoDatabase(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO orders (product, user_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, order.getProduct());
            statement.setInt(2, order.getUserId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Обновляет существующий заказ в базе данных.
     *
     * @param order объект заказа с обновленными данными.
     * @return true, если заказ был успешно обновлен; иначе false.
     */
    private static boolean updateOrderInDatabase(Order order) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE orders SET product = ?, user_id = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, order.getProduct());
            statement.setInt(2, order.getUserId());
            statement.setInt(3, order.getId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет заказ из базы данных.
     *
     * @param orderId идентификатор заказа, который нужно удалить.
     * @return true, если заказ был успешно удален; иначе false.
     */
    private static boolean deleteOrderFromDatabase(int orderId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM orders WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает список заказов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя, для которого необходимо получить заказы.
     * @return список заказов для указанного пользователя. Если произошла ошибка при доступе к базе данных, возвращается null.
     */
    private static List<Order> getOrdersByUserId(int userId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, product, user_id FROM orders WHERE user_id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("id"),
                        resultSet.getString("product"),
                        resultSet.getInt("user_id")
                ));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Отправляет JSON-ответ на HTTP-запрос.
     *
     * @param exchange объект HttpExchange, представляющий HTTP-запрос и ответ.
     * @param object объект, который необходимо сериализовать в JSON и отправить в ответ.
     * @throws IOException если произошла ошибка при записи ответа.
     */
    private static void sendJsonResponse(HttpExchange exchange, Object object) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = objectMapper.writeValueAsBytes(object);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
