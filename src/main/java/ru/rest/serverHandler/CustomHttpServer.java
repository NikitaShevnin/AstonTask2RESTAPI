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

/**
 * Класс {@code CustomHttpServer} отвечает за создание и конфигурирование HTTP-сервера,
 * обслуживающего запросы к RESTful API приложению.
 *
 * @author [Ваше Имя]
 * @version 1.0
 */
public class CustomHttpServer {
    private final HttpServer server;

    /**
     * Создает новый экземпляр {@code CustomHttpServer} на указанном порту.
     *
     * @param port порт, на котором будет запущен HTTP-сервер
     * @throws IOException если возникнут ошибки при создании HTTP-сервера
     */
    public CustomHttpServer(int port) throws IOException {
        server = createHttpServer(new InetSocketAddress(port), 0);
        configureRoutes();
        server.setExecutor(createThreadPoolExecutor());
    }

    /**
     * Запускает HTTP-сервер.
     */
    public void start() {
        server.start();
    }

    /**
     * Настраивает маршруты для обработки запросов к ресурсам пользователей и заказов.
     */
    private void configureRoutes() {
        // Маршруты для пользователей
        server.createContext("/users", this::handleUserRequests);

        // Маршруты для заказов
        server.createContext("/orders", this::handleOrderRequests);
    }

    /**
     * Обрабатывает запросы, связанные с пользователями.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
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

    /**
     * Обрабатывает запросы, связанные с заказами.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @throws IOException если возникнут ошибки при обработке HTTP-запроса
     */
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

    /**
     * Создает новый экземпляр {@link HttpServer} на указанном адресе и порту.
     *
     * @param inetSocketAddress адрес и порт для запуска HTTP-сервера
     * @param backlog размер очереди для входящих подключений
     * @return новый экземпляр {@link HttpServer}
     * @throws IOException если возникнут ошибки при создании HTTP-сервера
     */
    private static HttpServer createHttpServer(InetSocketAddress inetSocketAddress, int backlog) throws IOException {
        return HttpServer.create(inetSocketAddress, backlog);
    }

    /**
     * Создает экземпляр {@link ThreadPoolExecutor} для обработки запросов к HTTP-серверу.
     *
     * @return новый экземпляр {@link ThreadPoolExecutor}
     */
    private static ThreadPoolExecutor createThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
