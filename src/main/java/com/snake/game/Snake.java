package com.snake.game;

import java.util.LinkedList;
import java.util.List;

public class Snake {

    private final LinkedList<Point> body;
    private boolean growing;

    public Snake(Point start) {
        body = new LinkedList<>();
        body.addFirst(start);
        body.addFirst(start.translate(Direction.RIGHT));
        body.addFirst(start.translate(Direction.RIGHT).translate(Direction.RIGHT));
        growing = false;
    }

    Snake(List<Point> segments) {
        body = new LinkedList<>(segments);
        growing = false;
    }

    public void move(Direction dir) {
        Point newHead = body.getFirst().translate(dir);
        body.addFirst(newHead);
        if (growing) {
            growing = false;
        } else {
            body.removeLast();
        }
    }

    public void grow() {
        growing = true;
    }

    public Point head() {
        return body.getFirst();
    }

    public int length() {
        return body.size();
    }

    public boolean collidesSelf() {
        Point h = head();
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(h)) {
                return true;
            }
        }
        return false;
    }

    public boolean occupies(Point p) {
        return body.contains(p);
    }

    public List<Point> body() {
        return List.copyOf(body);
    }
}
