package com.snake.game;

import java.util.Random;

public class Food {

    private Point position;
    private final Random random;

    public Food(Random random) {
        this.random = random;
    }

    public void respawn(int gridWidth, int gridHeight, Snake snake) {
        Point candidate;
        do {
            candidate = new Point(random.nextInt(gridWidth), random.nextInt(gridHeight));
        } while (snake.occupies(candidate));
        this.position = candidate;
    }

    public Point position() {
        return position;
    }
}
