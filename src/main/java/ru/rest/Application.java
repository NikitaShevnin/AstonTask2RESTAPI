package ru.rest;

import ru.rest.serverHandler.CustomHttpServer;

/**
 * Класс {@code Application} является основной точкой входа в RESTful API приложение.
 * Он отвечает за создание и запуск экземпляра {@link CustomHttpServer}.
 */
public class Application {

    /**
     * Основной метод, который служит точкой входа в приложение.
     *
     * @param args командные аргументы (не используются)
     */
    public static void main(String[] args) {
        try {
            // Создаем новый экземпляр CustomHttpServer на порту 8080
            CustomHttpServer httpServer = new CustomHttpServer(8085);

            // Запускаем HTTP-сервер
            httpServer.start();
            System.out.println("Сервер запущен. Слушает порт 8085.");
        } catch (Exception e) {
            // Обрабатываем любые исключения, которые могут возникнуть во время запуска сервера
            e.printStackTrace();
        }
    }
}
