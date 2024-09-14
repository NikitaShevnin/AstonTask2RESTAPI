package ru.rest;

import com.sun.net.httpserver.HttpExchange;
import ru.rest.controller.OrderController;
import ru.rest.controller.UserController;
import ru.rest.repository.OrderRepositoryImpl;
import ru.rest.repository.UserRepository;
import ru.rest.repository.UserRepositoryImpl;
import ru.rest.service.OrderService;
import ru.rest.service.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class AstonTask2RestapiApplication {
    public static void main(String[] args) {
        // Создание необходимых компонентов
        OrderRepository orderRepository = new OrderRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        OrderService orderService = new OrderService(orderRepository);
        UserService userService = new UserService(userRepository);
        OrderController orderController = new OrderController(orderRepository, orderService);
        UserController userController = new UserController(userRepository, userService);

        // Настройка HttpServer
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/users", exchange -> {
                List<Object> users = userController.getUsersFromDatabase(exchange);
                sendResponse(exchange, users.toString());
            });
            server.createContext("/orders", exchange -> {
                List<Object> orders = orderController.getAllOrders(exchange);
                sendResponse(exchange, orders.toString());
            });
            server.setExecutor(null); // создает встроенный поток обработки
            server.start();
            System.out.println("Сервер запущен на порту 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}
