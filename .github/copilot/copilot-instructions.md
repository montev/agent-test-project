# Copilot Instructions — Snake Game (Java)

## Project Overview

This is a classic Snake game written in Java 21 using Swing for rendering. It follows a clean MVC-inspired separation: the game model (Snake, Food, Point, Direction) is decoupled from the view/controller (GameBoard, SnakeGame).

## Code Conventions

- **Java 21** — use modern Java features (records, sealed classes, switch expressions, text blocks) where appropriate.
- **Package**: `com.snake.game` — all classes live in a single flat package.
- **Naming**: standard Java conventions — PascalCase for classes, camelCase for methods/fields, UPPER_SNAKE for constants.
- **No external dependencies** beyond JUnit 5 for tests. The game uses only `java.awt` and `javax.swing`.
- **Immutable value objects** — `Point` and `Direction` should be immutable. Prefer records for simple data carriers.
- **Thread safety** — the game loop runs on the Swing Timer (EDT). All state mutations happen on the EDT; no manual threading.

## Architecture

| Layer | Classes | Responsibility |
|-------|---------|---------------|
| Entry | `SnakeGame` | Creates `JFrame`, instantiates `GameBoard`, shows window |
| View / Controller | `GameBoard` | `JPanel` subclass — handles rendering, keyboard input, game timer, game state transitions |
| Model | `Snake` | Maintains ordered list of body `Point`s, exposes `move()`, `grow()`, `collidesSelf()` |
| Model | `Food` | Picks a random grid cell not occupied by the snake |
| Model | `Direction` | Enum: `UP`, `DOWN`, `LEFT`, `RIGHT` with delta-x / delta-y |
| Model | `Point` | Immutable grid coordinate (row, col) |

## Key Design Decisions

- **Grid-based**: the playing field is a logical grid (e.g., 20×20). Each cell is rendered as a square of `CELL_SIZE` pixels.
- **Game loop**: driven by `javax.swing.Timer` firing at a fixed interval (~150 ms). Each tick: move snake → check collisions → check food → repaint.
- **Input buffering**: queue direction changes so rapid key presses between ticks don't cause the snake to reverse into itself.
- **Score**: +10 points per food item eaten. Displayed on-screen.

## Testing Guidelines

- Model classes (`Snake`, `Food`, `Point`, `Direction`) should have thorough unit tests.
- Tests should **not** depend on Swing or rendering — test the model in isolation.
- Use JUnit 5 (`org.junit.jupiter`).

## Style Preferences

- Keep methods short (≤ 20 lines preferred).
- Prefer early returns over deep nesting.
- Only add comments where the logic is non-obvious; let clear naming do the work.
- No wildcard imports.

## Self-Maintenance

When the user asks to **commit**, review all changes made during the session and update this instructions file (`copilot-instructions.md`) to reflect any relevant modifications — for example, updated Java versions, new dependencies, changed architecture, renamed classes, or revised conventions. Do **not** update these instructions on every change; only synchronize them at commit time.
