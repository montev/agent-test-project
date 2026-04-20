# Snake Game — Implementation Plan

## Problem Statement

Implement the classic Snake arcade game in Java using Swing. The player controls a snake on a bounded grid, collecting food to score points and grow longer, while avoiding collisions with walls and the snake's own body. This is modeled after the iconic Nokia Snake (1998) — a single-player game with simple rules but addictive gameplay.

## Game Mechanics (from research)

Based on the original Snake game and genre conventions:

- **Playing field**: a rectangular grid bounded by walls on all four sides.
- **Snake**: starts as a short line (3 segments) near the centre, moving in an initial direction (e.g., RIGHT).
- **Movement**: the snake moves continuously at a fixed tick rate. The head advances one cell per tick, and the tail follows. The player can change direction using arrow keys — but cannot reverse (e.g., going LEFT while currently going RIGHT).
- **Food**: a single food item appears at a random unoccupied cell. When the snake's head enters the food cell, the snake grows by one segment (the tail doesn't move that tick), the score increases, and a new food spawns.
- **Collision — walls**: if the head moves outside the grid bounds, game over.
- **Collision — self**: if the head moves into any of its own body segments, game over.
- **Scoring**: +10 points per food eaten. Score displayed during play.
- **Game over**: display final score and allow restart.
- **Speed**: constant tick interval (~150ms). Optionally increase speed as score grows.

## Approach

Build model-first (testable logic with no Swing dependency), then layer on the view and input handling. All code lives in one package: `com.snake.game`.

## Todos

### 1. `Direction` enum
Create `Direction.java` with `UP`, `DOWN`, `LEFT`, `RIGHT`. Each constant stores dx/dy deltas. Add a helper `opposite()` method so the game can prevent 180° reversals.

### 2. `Point` record
Create `Point.java` as a Java record with `int x, int y`. Add a `translate(Direction)` method that returns a new Point shifted by the direction's delta.

### 3. `Snake` model
Create `Snake.java`:
- Internally holds a `LinkedList<Point>` for body segments (head = first element).
- `move(Direction dir)` — adds a new head in the given direction, removes the tail.
- `grow()` — on the next move, skip removing the tail (the snake extends by one).
- `head()` — returns the head point.
- `collidesSelf()` — returns true if the head occupies the same cell as any other segment.
- `occupies(Point p)` — returns true if any segment is at that point (used for food placement).

### 4. `Food` model
Create `Food.java`:
- Holds the current food `Point`.
- `respawn(int gridWidth, int gridHeight, Snake snake)` — picks a random cell not occupied by the snake.

### 5. `GameBoard` — core game panel
Create `GameBoard.java` extending `JPanel`:
- **Constants**: `GRID_WIDTH`, `GRID_HEIGHT` (e.g., 20×20), `CELL_SIZE` (e.g., 25px), `TICK_MS` (e.g., 150).
- **State**: `Snake`, `Food`, current `Direction`, `score`, `gameOver` flag, `paused` flag.
- **Game loop**: `javax.swing.Timer` that fires `actionPerformed` every `TICK_MS`. Each tick:
  1. Apply buffered direction change.
  2. Move the snake.
  3. Check wall collision (head out of bounds → game over).
  4. Check self collision → game over.
  5. Check food collision → grow snake, increment score, respawn food.
  6. Call `repaint()`.
- **Rendering** (`paintComponent`):
  - Fill background.
  - Draw grid lines (optional).
  - Draw snake segments as filled rectangles (head in a distinct colour).
  - Draw food as a filled circle or rectangle.
  - Draw score text.
  - If game over, draw overlay with "GAME OVER" and final score.
  - If paused, draw "PAUSED" overlay.
- **Input** (`KeyListener` or key bindings):
  - Arrow keys → buffer direction change (reject reversal).
  - P → toggle pause.
  - Enter → restart when game over.

### 6. `SnakeGame` — entry point
Create `SnakeGame.java` with `main()`:
- Create a `JFrame` with title "Snake".
- Add a `GameBoard` panel.
- Pack, centre, set visible.
- `setDefaultCloseOperation(EXIT_ON_CLOSE)`.

### 7. Unit tests
- **`SnakeTest`**: movement in all directions, growth, self-collision detection, `occupies` check.
- **`FoodTest`**: respawn doesn't land on snake, respawn stays in bounds.
- **`GameBoardTest`** (optional): test game-over detection, score increment logic via exposed methods (keep tests Swing-free if possible).

### 8. Polish & verification
- Run `mvn clean test` — all tests green.
- Run `mvn clean package` — jar builds successfully.
- Manual smoke test — launch the jar, play a round, verify food spawning, growth, collision, score, game over, restart.

## Implementation Order & Dependencies

```
Direction ──┐
             ├──► Snake ──┐
Point   ────┘             ├──► GameBoard ──► SnakeGame
             Food ────────┘
```

1. `Direction` + `Point` (no dependencies — can be done in parallel)
2. `Snake` (depends on Direction, Point)
3. `Food` (depends on Point, Snake)
4. `GameBoard` (depends on all model classes)
5. `SnakeGame` (depends on GameBoard)
6. Tests (written alongside or after each model class)
7. Polish & verification

## Open Questions / Future Enhancements

- **Speed ramp**: increase tick speed every N points for added challenge.
- **High-score persistence**: save best score to a file.
- **Wrap-around mode**: snake exits one wall and enters from the opposite side (alternate game mode).
- **Sound effects**: play a beep on food eaten / game over.

These are out of scope for the initial implementation.
