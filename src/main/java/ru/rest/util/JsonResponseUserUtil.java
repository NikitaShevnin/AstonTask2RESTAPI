package ru.rest.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import static ru.rest.controller.UserController.objectMapper;

public class JsonResponseUserUtil {

    /**
     * Отправляет JSON-ответ клиенту.
     *
     * @param exchange объект, представляющий HTTP-обмен
     * @param object объект, который необходимо сериализовать и отправить как ответ
     * @throws IOException если возникнут ошибки при отправке ответа
     */
    public static void sendJsonResponse(HttpExchange exchange, Object object) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = objectMapper.writeValueAsBytes(object);
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
