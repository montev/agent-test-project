package com.snake.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DirectionTest {

    @Test
    void oppositeOfUpIsDown() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
    }

    @Test
    void oppositeOfDownIsUp() {
        assertEquals(Direction.UP, Direction.DOWN.opposite());
    }

    @Test
    void oppositeOfLeftIsRight() {
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
    }

    @Test
    void oppositeOfRightIsLeft() {
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }

    @Test
    void deltasAreCorrect() {
        assertEquals(0, Direction.UP.dx());
        assertEquals(-1, Direction.UP.dy());
        assertEquals(0, Direction.DOWN.dx());
        assertEquals(1, Direction.DOWN.dy());
        assertEquals(-1, Direction.LEFT.dx());
        assertEquals(0, Direction.LEFT.dy());
        assertEquals(1, Direction.RIGHT.dx());
        assertEquals(0, Direction.RIGHT.dy());
    }
}
