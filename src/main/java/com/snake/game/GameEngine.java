package com.snake.game;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameEngine {

    static final int GRID_WIDTH = 20;
    static final int GRID_HEIGHT = 20;
    private static final int TICK_MS = 150;

    private Snake snake;
    private Food food;
    private Direction direction;
    private Direction bufferedDirection;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private final ScheduledExecutorService scheduler;

    public GameEngine() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "game-loop");
            t.setDaemon(true);
            return t;
        });
        initGame();
    }

    private synchronized void initGame() {
        Point start = new Point(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2);
        snake = new Snake(start);
        food = new Food(new Random());
        food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
        direction = Direction.RIGHT;
        bufferedDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        paused = false;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::tick, TICK_MS, TICK_MS, TimeUnit.MILLISECONDS);
    }

    private synchronized void tick() {
        if (gameOver || paused) {
            return;
        }

        direction = bufferedDirection;
        snake.move(direction);

        Point head = snake.head();
        if (head.x() < 0 || head.x() >= GRID_WIDTH || head.y() < 0 || head.y() >= GRID_HEIGHT) {
            gameOver = true;
            return;
        }

        if (snake.collidesSelf()) {
            gameOver = true;
            return;
        }

        if (head.equals(food.position())) {
            snake.grow();
            score += 10;
            food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
        }
    }

    public synchronized void changeDirection(Direction dir) {
        if (!gameOver && !paused && dir != direction.opposite()) {
            bufferedDirection = dir;
        }
    }

    public synchronized void togglePause() {
        if (!gameOver) {
            paused = !paused;
        }
    }

    public synchronized void restart() {
        if (gameOver) {
            initGame();
        }
    }

    public synchronized String getStateJson() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');

        sb.append("\"snake\":[");
        List<Point> body = snake.body();
        for (int i = 0; i < body.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"x\":").append(body.get(i).x())
              .append(",\"y\":").append(body.get(i).y()).append('}');
        }
        sb.append("],");

        Point f = food.position();
        sb.append("\"food\":{\"x\":").append(f.x())
          .append(",\"y\":").append(f.y()).append("},");

        sb.append("\"score\":").append(score).append(',');
        sb.append("\"gameOver\":").append(gameOver).append(',');
        sb.append("\"paused\":").append(paused).append(',');
        sb.append("\"gridWidth\":").append(GRID_WIDTH).append(',');
        sb.append("\"gridHeight\":").append(GRID_HEIGHT).append(',');
        sb.append("\"direction\":\"").append(direction.name()).append('"');

        sb.append('}');
        return sb.toString();
    }
}
