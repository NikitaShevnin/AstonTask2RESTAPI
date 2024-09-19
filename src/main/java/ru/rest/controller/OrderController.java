package ru.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.entity.Order;
import ru.rest.service.OrderService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import static ru.rest.service.OrderService.*;
import static ru.rest.util.JsonResponseOrderUtil.sendJsonResponse;

import ru.rest.util.JsonResponseOrderUtil;

public class OrderController {

    public static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

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
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            JsonResponseOrderUtil.sendJsonResponse(exchange, order); // Исправленный вызов
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
            JsonResponseOrderUtil.sendJsonResponse(exchange, newOrder); // Исправленный вызов
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
                JsonResponseOrderUtil.sendJsonResponse(exchange, updatedOrder); // Исправленный вызов
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
        List<Order> orders = orderService.getOrdersByUserId(userId);
        JsonResponseOrderUtil.sendJsonResponse(exchange, orders);
    }

    /**
     * Читает заказ из тела HTTP-запроса.
     *
     * @param exchange объект HttpExchange, содержащий данные запроса.
     * @return объект заказа, созданный из данных запроса.
     * @throws IOException если произошла ошибка при чтении данных из запроса.
     */
    static Order readOrderFromRequest(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return objectMapper.readValue(is, Order.class);
        }
    }
}
