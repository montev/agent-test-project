# 🐍 Snake Game

A classic Snake game with a **Java backend** and **TypeScript/Canvas web frontend**.

## About

This is a faithful implementation of the classic Snake arcade game, inspired by the iconic 1998 Nokia mobile game. The player controls a snake on a grid-based playing field, collecting food to grow longer while avoiding collisions with the walls and the snake's own body.

The game logic runs on a Java HTTP server, while the UI is rendered in the browser using HTML5 Canvas and TypeScript.

## How to Play

- **Arrow keys** — steer the snake (Up, Down, Left, Right)
- **P** — pause / resume
- **Enter** — restart after game over

### Rules

1. The snake moves continuously in the current direction
2. Eating food scores points and makes the snake one segment longer
3. The game ends if the snake hits a wall or its own body
4. The goal is to achieve the highest score possible

## Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+ and npm

## Build & Run

```bash
# Build frontend
cd frontend && npm install && npm run build && cd ..

# Build backend
mvn clean package

# Run (starts web server on port 8080)
java -jar target/snake-game-1.0-SNAPSHOT.jar
```

Then open **http://localhost:8080** in your browser.

To use a custom port: `java -jar target/snake-game-1.0-SNAPSHOT.jar 3000`

## Run Tests

```bash
mvn test
```

## Architecture

```
┌──────────────────────┐        HTTP         ┌──────────────────────┐
│   TypeScript / Canvas│◄────────────────────►│    Java Backend      │
│   (frontend/)        │  GET /api/state      │                      │
│                      │  POST /api/action    │  GameEngine (logic)  │
│  Polls game state    │                      │  GameServer (HTTP)   │
│  Sends key inputs    │                      │  Snake, Food, Point  │
│  Renders on Canvas   │                      │  Direction           │
└──────────────────────┘                      └──────────────────────┘
```

## Project Structure

```
src/main/java/com/snake/game/
├── SnakeGame.java        # Entry point — starts HTTP server
├── GameServer.java       # HTTP server — serves frontend + REST API
├── GameEngine.java       # Game loop and state management
├── GameBoard.java        # Legacy Swing panel (retained for reference)
├── Snake.java            # Snake model — body segments and movement
├── Direction.java        # Enum for movement directions
├── Point.java            # Grid coordinate value object
└── Food.java             # Food placement logic

frontend/
├── index.html            # Game page
├── src/main.ts           # TypeScript — canvas rendering + input
├── package.json
└── tsconfig.json

src/test/java/com/snake/game/
├── SnakeTest.java        # Snake movement and growth tests
├── DirectionTest.java    # Direction and opposite tests
├── PointTest.java        # Point translation tests
└── FoodTest.java         # Food spawning tests
```

## API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/state` | GET | Returns current game state as JSON |
| `/api/action` | POST | Sends a player action (`UP`, `DOWN`, `LEFT`, `RIGHT`, `PAUSE`, `RESTART`) |
| `/` | GET | Serves the web frontend |

## License

MIT
