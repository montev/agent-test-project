package com.snake.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PointTest {

    @Test
    void translateMovesInDirection() {
        Point p = new Point(5, 5);
        assertEquals(new Point(5, 4), p.translate(Direction.UP));
        assertEquals(new Point(5, 6), p.translate(Direction.DOWN));
        assertEquals(new Point(4, 5), p.translate(Direction.LEFT));
        assertEquals(new Point(6, 5), p.translate(Direction.RIGHT));
    }

    @Test
    void translateDoesNotMutateOriginal() {
        Point p = new Point(3, 3);
        Point moved = p.translate(Direction.RIGHT);
        assertEquals(new Point(3, 3), p);
        assertNotEquals(p, moved);
    }

    @Test
    void equalityByValue() {
        assertEquals(new Point(1, 2), new Point(1, 2));
        assertNotEquals(new Point(1, 2), new Point(2, 1));
    }
}
