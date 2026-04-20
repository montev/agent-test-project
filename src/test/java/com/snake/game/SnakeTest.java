package com.snake.game;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeTest {

    @Test
    void initialSnakeHasThreeSegments() {
        Snake snake = new Snake(new Point(5, 5));
        assertEquals(3, snake.length());
    }

    @Test
    void headIsLeadingSegment() {
        Snake snake = new Snake(new Point(5, 5));
        // Snake constructed at (5,5) grows RIGHT twice, so head is at (7,5)
        assertEquals(new Point(7, 5), snake.head());
    }

    @Test
    void moveRightAdvancesHead() {
        Snake snake = new Snake(new Point(5, 5));
        snake.move(Direction.RIGHT);
        assertEquals(new Point(8, 5), snake.head());
        assertEquals(3, snake.length());
    }

    @Test
    void moveUpAdvancesHead() {
        Snake snake = new Snake(new Point(5, 5));
        snake.move(Direction.UP);
        assertEquals(new Point(7, 4), snake.head());
    }

    @Test
    void moveDownAdvancesHead() {
        Snake snake = new Snake(new Point(5, 5));
        snake.move(Direction.DOWN);
        assertEquals(new Point(7, 6), snake.head());
    }

    @Test
    void moveLeftAdvancesHead() {
        Snake snake = new Snake(new Point(5, 5));
        snake.move(Direction.LEFT);
        assertEquals(new Point(6, 5), snake.head());
    }

    @Test
    void growIncreasesLengthOnNextMove() {
        Snake snake = new Snake(new Point(5, 5));
        snake.grow();
        snake.move(Direction.RIGHT);
        assertEquals(4, snake.length());
    }

    @Test
    void growOnlyLastsOneMove() {
        Snake snake = new Snake(new Point(5, 5));
        snake.grow();
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        assertEquals(4, snake.length());
    }

    @Test
    void collidesSelfReturnsFalseForStraightSnake() {
        Snake snake = new Snake(new Point(5, 5));
        assertFalse(snake.collidesSelf());
    }

    @Test
    void collidesSelfDetectsOverlap() {
        // Build a snake that loops back on itself
        Snake snake = new Snake(List.of(
            new Point(3, 3), // head — same as tail
            new Point(3, 2),
            new Point(4, 2),
            new Point(4, 3),
            new Point(3, 3)  // tail — overlaps head
        ));
        assertTrue(snake.collidesSelf());
    }

    @Test
    void occupiesReturnsTrueForBodyCell() {
        Snake snake = new Snake(new Point(5, 5));
        assertTrue(snake.occupies(new Point(7, 5))); // head
        assertTrue(snake.occupies(new Point(6, 5))); // body
        assertTrue(snake.occupies(new Point(5, 5))); // tail
    }

    @Test
    void occupiesReturnsFalseForEmptyCell() {
        Snake snake = new Snake(new Point(5, 5));
        assertFalse(snake.occupies(new Point(0, 0)));
    }

    @Test
    void bodyReturnsImmutableCopy() {
        Snake snake = new Snake(new Point(5, 5));
        List<Point> body = snake.body();
        assertEquals(3, body.size());
        // Verify it's a copy — modifications shouldn't affect the snake
        try {
            body.add(new Point(0, 0));
        } catch (UnsupportedOperationException ignored) {
            // expected for unmodifiable list
        }
        assertEquals(3, snake.length());
    }
}
