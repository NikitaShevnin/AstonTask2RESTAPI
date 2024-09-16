package ru.rest;

import ru.rest.serverHandler.CustomHttpServer;

public class Application {
    public static void main(String[] args) {
        try {
            // Создаем HTTP-сервер
            CustomHttpServer httpServer = new CustomHttpServer(8080);

            // Запускаем HTTP-сервер
            httpServer.start();
            System.out.println("Сервер запущен. Слушает порт 8080.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
