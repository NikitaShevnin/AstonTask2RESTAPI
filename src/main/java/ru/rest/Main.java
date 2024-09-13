package ru.rest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import ru.rest.DAO.OrderDAO;
import ru.rest.DAO.UserDAO;
import ru.rest.util.DatabaseConnector;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Основной класс приложения, который инициализирует DAO и запускает встроенный HTTP сервер.
 */
public class Main {

    /** Объект для работы с данными пользователей. */
    private static UserDAO userDAO;

    /** Объект для работы с данными заказов. */
    private static OrderDAO orderDAO;

    /**
     * Главный метод приложения, который настраивает сервер и инициализирует DAO.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Получение источника данных из DatabaseConnector
        DataSource dataSource = DatabaseConnector.getDataSource();

        // Инициализация DAO
        userDAO = new UserDAO(dataSource);
        orderDAO = new OrderDAO(dataSource);

        // Запуск сервера
        startServer(8080);
    }

    /**
     * Метод для запуска HTTP сервера на заданном порту.
     *
     * @param port порт для запуска сервера
     */
    private static void startServer(int port) {
        try {
            // Создаем HTTP сервер
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/users", new UserHttpHandler());
            server.createContext("/orders", new OrderHttpHandler());
            server.setExecutor(null); // создает встроенный поток обработки
            server.start();
            System.out.println("Сервер запущен на порту " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработчик HTTP запросов для пользователей.
     */
    static class UserHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Обработка запроса на пользователей";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     * Обработчик HTTP запросов для заказов.
     */
    static class OrderHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Обработка запроса на заказы";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
