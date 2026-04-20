package com.snake.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class GameServer {

    private final GameEngine engine;
    private final HttpServer server;
    private final Path frontendRoot;

    public GameServer(int port) throws IOException {
        engine = new GameEngine();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));
        frontendRoot = Path.of("frontend").toAbsolutePath().normalize();

        server.createContext("/api/state", this::handleState);
        server.createContext("/api/action", this::handleAction);
        server.createContext("/", this::handleStatic);
    }

    public void start() {
        engine.start();
        server.start();
        System.out.println("Snake game running at http://localhost:"
                + server.getAddress().getPort());
    }

    private void handleState(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        byte[] response = engine.getStateJson().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private void handleAction(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            setCorsHeaders(exchange);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes());
        String action = extractJsonString(body, "action");

        if (action != null) {
            switch (action) {
                case "UP"      -> engine.changeDirection(Direction.UP);
                case "DOWN"    -> engine.changeDirection(Direction.DOWN);
                case "LEFT"    -> engine.changeDirection(Direction.LEFT);
                case "RIGHT"   -> engine.changeDirection(Direction.RIGHT);
                case "PAUSE"   -> engine.togglePause();
                case "RESTART" -> engine.restart();
                default -> { /* ignore unknown actions */ }
            }
        }

        byte[] response = "{\"ok\":true}".getBytes();
        setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        if ("/".equals(uriPath)) {
            uriPath = "/index.html";
        }

        Path filePath = frontendRoot.resolve(uriPath.substring(1)).normalize();
        if (!filePath.startsWith(frontendRoot) || !Files.isRegularFile(filePath)) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }

        byte[] content = Files.readAllBytes(filePath);
        exchange.getResponseHeaders().set("Content-Type", contentType(uriPath));
        exchange.sendResponseHeaders(200, content.length);
        exchange.getResponseBody().write(content);
        exchange.close();
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String contentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        return "application/octet-stream";
    }

    private static String extractJsonString(String json, String key) {
        String needle = "\"" + key + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) return null;
        idx = json.indexOf(':', idx + needle.length());
        if (idx < 0) return null;
        idx = json.indexOf('"', idx + 1);
        if (idx < 0) return null;
        int end = json.indexOf('"', idx + 1);
        if (end < 0) return null;
        return json.substring(idx + 1, end);
    }
}
