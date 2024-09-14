package ru.rest;

import com.sun.net.httpserver.HttpServer;
import ru.rest.DAO.OrderDAO;
import ru.rest.DAO.UserDAO;
import ru.rest.util.DatabaseConnector;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Основной класс приложения, который инициализирует DAO и запускает встроенный HTTP сервер.
 * <p>
 * Этот класс отвечает за настройку источников данных, создание экземпляров DAO для работы
 * с пользователями и заказами, а также за запуск HTTP сервера, обрабатывающего запросы
 * к ресурсам пользователей и заказов. Сервлеты для работы с запросами реализованы
 * в виде внутренних классов.
 * </p>
 */
public class Main {

    /**
     * Объект для работы с данными пользователей.
     */
    private static UserDAO userDAO;

    /**
     * Объект для работы с данными заказов.
     */
    private static OrderDAO orderDAO;

    /**
     * Главный метод приложения, который настраивает сервер и инициализирует DAO.
     * <p>
     * Метод получает источник данных с помощью {@link DatabaseConnector}, инициализирует
     * соответствующие DAO, после чего запускает встроенный HTTP сервер на порту 8080.
     * Аргументы командной строки не используются.
     * </p>
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Получение источника данных из DatabaseConnector
        DataSource dataSource = DatabaseConnector.getDataSource();

        // Инициализация DAO
        userDAO = new UserDAO(dataSource);
        orderDAO = new OrderDAO(dataSource);

        // Запуск сервера для обработки запросов пользователей и заказов
        try {
            startServer(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для запуска HTTP серверов на заданном порту.
     * <p>
     * Этот метод создает и запускает два HTTP сервера:
     * 1. Сервер для обслуживания статических ресурсов (HTML, CSS, JavaScript).
     * 2. Сервер для обработки запросов к пользователям и заказам.
     * </p>
     *
     * @param port порт для запуска серверов
     * @throws IOException если возникли проблемы с запуском серверов
     */
    private static void startServer(int port) throws IOException {
        // Создаем HTTP сервер
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", new UserHttpHandler());
        server.createContext("/orders", new OrderHttpHandler());
        server.setExecutor(null); // создает встроенный поток обработки
        server.start();
        System.out.println("Сервер для обработки запросов запущен на порту " + port);
    }

    /**
     * Обработчик HTTP запросов для пользователей.
     * <p>
     * Этот класс реализует интерфейс {@link com.sun.net.httpserver.HttpHandler} и обрабатывает запросы, связанные
     * с пользователями. В данном простом реализации он отправляет фиксированный ответ
     * "Обработка запроса на пользователей".
     * </p>
     */
    static class UserHttpHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
            String response = "Обработка запроса на пользователей";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }

    /**

     * Обработчик HTTP запросов для заказов.
     * <p>
     * Этот класс реализует интерфейс {@link com.sun.net.httpserver.HttpHandler} и обрабатывает запросы, связанные
     * с заказами. В этом простом варианте он отправляет фиксированный ответ
     * "Обработка запроса на заказы".
     * </p>
     */
    static class OrderHttpHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
            String response = "Обработка запроса на заказы";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}
