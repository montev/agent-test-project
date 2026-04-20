package com.snake.game;

public class SnakeGame {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        boolean testMode = false;
        for (String arg : args) {
            if ("--test".equals(arg)) {
                testMode = true;
            } else {
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException ignored) {
                    // use default
                }
            }
        }
        GameServer server = new GameServer(port);
        if (testMode) {
            server.setTestMode(true);
        }
        server.start();
    }
}
