package com.snake.game;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodTest {

    @Test
    void respawnPlacesFoodInBounds() {
        Food food = new Food(new Random(42));
        Snake snake = new Snake(new Point(5, 5));

        for (int i = 0; i < 100; i++) {
            food.respawn(20, 20, snake);
            Point pos = food.position();
            assertNotNull(pos);
            assertTrue(pos.x() >= 0 && pos.x() < 20, "x out of bounds: " + pos.x());
            assertTrue(pos.y() >= 0 && pos.y() < 20, "y out of bounds: " + pos.y());
        }
    }

    @Test
    void respawnDoesNotLandOnSnake() {
        Food food = new Food(new Random(42));
        Snake snake = new Snake(new Point(5, 5));

        for (int i = 0; i < 100; i++) {
            food.respawn(20, 20, snake);
            assertFalse(snake.occupies(food.position()),
                "Food landed on snake at " + food.position());
        }
    }

    @Test
    void respawnWorksOnSmallGrid() {
        // 4x1 grid, snake occupies 3 cells — only one cell left
        Snake snake = new Snake(new Point(0, 0));
        // snake body: (2,0), (1,0), (0,0) — head at (2,0)
        Food food = new Food(new Random(42));
        food.respawn(4, 1, snake);
        // Only valid position is (3,0)
        assertTrue(food.position().equals(new Point(3, 0)),
            "Expected (3,0) but got " + food.position());
    }
}
