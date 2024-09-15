package ru.rest;

import ru.rest.controller.OrderController;
import ru.rest.controller.UserController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AstonTask2RestapiApplication {
    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/users", exchange -> {
                if (exchange.getRequestMethod().equals("GET")) {
                    UserController.getUsers(exchange);
                } else if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().startsWith("/users/")) {
                    UserController.getUser(exchange);
                }
            });
            server.createContext("/orders", exchange -> {
                if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("userId")) {
                    OrderController.getOrders(exchange);
                }
            });
            server.setExecutor(null);
            server.start();
            System.out.println("Сервер запущен на порту 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
