package ru.rest.view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticResourceServer {

    private static final String STATIC_RESOURCE_DIR = "src/main/resources/static";

    public static void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(null, 0);
        server.createContext("/", new IndexPageHandler());
        server.createContext("/static", new StaticResourceHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }

    static class IndexPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File indexFile = new File(STATIC_RESOURCE_DIR + "/index.html");
            if (indexFile.exists() && indexFile.isFile()) {
                sendFile(exchange, indexFile);
            } else {
                send404(exchange);
            }
        }

        private void sendFile(HttpExchange exchange, File file) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", Files.probeContentType(file.toPath()));
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(os);
            }
        }

        private void send404(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("404 Not Found".getBytes());
            }
        }
    }

    static class StaticResourceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.startsWith("/static/")) {
                String filePath = STATIC_RESOURCE_DIR + requestPath.substring("/static".length());
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    sendFile(exchange, file);
                } else {
                    send404(exchange);
                }
            } else {
                send404(exchange);
            }
        }

        private void sendFile(HttpExchange exchange, File file) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", Files.probeContentType(file.toPath()));
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(os);
            }
        }

        private void send404(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(404, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("404 Not Found".getBytes());
            }
        }
    }
}
