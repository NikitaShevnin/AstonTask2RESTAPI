package ru.rest.serverHandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.rest.controller.OrderController;
import ru.rest.controller.UserController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomHttpServer {
    private final HttpServer server;

    public CustomHttpServer(int port) throws IOException {
        server = createHttpServer(new InetSocketAddress(port), 0);
        configureRoutes();
        server.setExecutor(createThreadPoolExecutor());
    }

    public void start() {
        server.start();
    }

    private void configureRoutes() {
        // Маршруты для пользователей
        server.createContext("/users", this::handleUserRequests);

        // Маршруты для заказов
        server.createContext("/orders", this::handleOrderRequests);
    }

    private void handleUserRequests(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getPath().equals("/users")) {
                UserController.getUsers(exchange);
            } else if (exchange.getRequestURI().getPath().matches("/users/\\d+")) {
                int userId = Integer.parseInt(exchange.getRequestURI().getPath().substring("/users/".length()));
                UserController.getUserById(exchange);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        }
        exchange.close();
    }

    private void handleOrderRequests(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getPath().equals("/orders")) {
                OrderController.getOrders(exchange);
            } else if (exchange.getRequestURI().getPath().matches("/orders/\\d+")) {
                int orderId = Integer.parseInt(exchange.getRequestURI().getPath().substring("/orders/".length()));
                OrderController.getOrderById(exchange);
            } else if (exchange.getRequestURI().getPath().matches("/orders/\\d+/users")) {
                int userId = Integer.parseInt(exchange.getRequestURI().getPath().substring("/orders/".length(), exchange.getRequestURI().getPath().lastIndexOf("/")));
                OrderController.getOrdersByUserId(exchange);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } else if ("POST".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getPath().equals("/orders")) {
                OrderController.createOrder(exchange);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } else if ("PUT".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getPath().matches("/orders/\\d+")) {
                int orderId = Integer.parseInt(exchange.getRequestURI().getPath().substring("/orders/".length()));
                OrderController.updateOrder(exchange);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            if (exchange.getRequestURI().getPath().matches("/orders/\\d+")) {

                int orderId = Integer.parseInt(exchange.getRequestURI().getPath().substring("/orders/".length()));
                OrderController.deleteOrder(exchange);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        }
        exchange.close();
    }

    private static HttpServer createHttpServer(InetSocketAddress inetSocketAddress, int backlog) throws IOException {
        return HttpServer.create(inetSocketAddress, backlog);
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
