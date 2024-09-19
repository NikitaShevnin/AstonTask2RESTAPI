package ru.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class JsonResponseOrderUtil {
    public static void sendJsonResponse(HttpExchange exchange, Object object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // Установка заголовка Content-Type
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        // Преобразование объекта в JSON
        byte[] responseBytes = objectMapper.writeValueAsBytes(object);

        // Отправка HTTP-ответа с кодом 200 и длиной ответа
        exchange.sendResponseHeaders(200, responseBytes.length);

        // Отправка тела ответа
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
