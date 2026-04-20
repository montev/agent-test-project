package com.snake.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class GameBoard extends JPanel {

    static final int GRID_WIDTH = 20;
    static final int GRID_HEIGHT = 20;
    private static final int CELL_SIZE = 25;
    private static final int TICK_MS = 150;

    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color GRID_COLOR = new Color(45, 45, 45);
    private static final Color SNAKE_HEAD_COLOR = new Color(0, 200, 80);
    private static final Color SNAKE_BODY_COLOR = new Color(0, 160, 60);
    private static final Color FOOD_COLOR = new Color(220, 50, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 160);

    private Snake snake;
    private Food food;
    private Direction direction;
    private Direction bufferedDirection;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private final Timer timer;

    public GameBoard() {
        setPreferredSize(new Dimension(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE));
        setBackground(BG_COLOR);
        setFocusable(true);
        setupKeyBindings();

        timer = new Timer(TICK_MS, e -> tick());
        initGame();
    }

    private void initGame() {
        Point start = new Point(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2);
        snake = new Snake(start);
        food = new Food(new Random());
        food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
        direction = Direction.RIGHT;
        bufferedDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        paused = false;
        timer.start();
    }

    private void tick() {
        if (gameOver || paused) {
            return;
        }

        direction = bufferedDirection;
        snake.move(direction);

        Point head = snake.head();
        if (head.x() < 0 || head.x() >= GRID_WIDTH || head.y() < 0 || head.y() >= GRID_HEIGHT) {
            gameOver = true;
            timer.stop();
            repaint();
            return;
        }

        if (snake.collidesSelf()) {
            gameOver = true;
            timer.stop();
            repaint();
            return;
        }

        if (head.equals(food.position())) {
            snake.grow();
            score += 10;
            food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
        }

        repaint();
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        bindDirection(im, am, KeyEvent.VK_UP, Direction.UP);
        bindDirection(im, am, KeyEvent.VK_DOWN, Direction.DOWN);
        bindDirection(im, am, KeyEvent.VK_LEFT, Direction.LEFT);
        bindDirection(im, am, KeyEvent.VK_RIGHT, Direction.RIGHT);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
        am.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) return;
                paused = !paused;
                if (paused) {
                    timer.stop();
                } else {
                    timer.start();
                }
                repaint();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "restart");
        am.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    initGame();
                    repaint();
                }
            }
        });
    }

    private void bindDirection(InputMap im, ActionMap am, int keyCode, Direction dir) {
        String name = dir.name();
        im.put(KeyStroke.getKeyStroke(keyCode, 0), name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && !paused && dir != direction.opposite()) {
                    bufferedDirection = dir;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawSnake(g2);
        drawFood(g2);
        drawScore(g2);

        if (gameOver) {
            drawOverlay(g2, "GAME OVER", "Score: " + score + "  —  Press ENTER to restart");
        } else if (paused) {
            drawOverlay(g2, "PAUSED", "Press P to resume");
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        for (int x = 0; x <= GRID_WIDTH; x++) {
            g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        }
        for (int y = 0; y <= GRID_HEIGHT; y++) {
            g.drawLine(0, y * CELL_SIZE, GRID_WIDTH * CELL_SIZE, y * CELL_SIZE);
        }
    }

    private void drawSnake(Graphics2D g) {
        var body = snake.body();
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            g.setColor(i == 0 ? SNAKE_HEAD_COLOR : SNAKE_BODY_COLOR);
            g.fillRoundRect(
                p.x() * CELL_SIZE + 1, p.y() * CELL_SIZE + 1,
                CELL_SIZE - 2, CELL_SIZE - 2,
                6, 6
            );
        }
    }

    private void drawFood(Graphics2D g) {
        Point p = food.position();
        g.setColor(FOOD_COLOR);
        g.fillOval(
            p.x() * CELL_SIZE + 2, p.y() * CELL_SIZE + 2,
            CELL_SIZE - 4, CELL_SIZE - 4
        );
    }

    private void drawScore(Graphics2D g) {
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Score: " + score, 8, 18);
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        int w = getWidth();
        int h = getHeight();

        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, w, h);

        g.setColor(TEXT_COLOR);
        Font titleFont = new Font("SansSerif", Font.BOLD, 36);
        g.setFont(titleFont);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (w - fm.stringWidth(title)) / 2, h / 2 - 10);

        Font subFont = new Font("SansSerif", Font.PLAIN, 16);
        g.setFont(subFont);
        fm = g.getFontMetrics();
        g.drawString(subtitle, (w - fm.stringWidth(subtitle)) / 2, h / 2 + 20);
    }
}
