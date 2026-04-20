package com.snake.game;

public record Point(int x, int y) {

    public Point translate(Direction dir) {
        return new Point(x + dir.dx(), y + dir.dy());
    }
}
