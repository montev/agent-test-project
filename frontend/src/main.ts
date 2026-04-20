interface Point {
  x: number;
  y: number;
}

interface GameState {
  snake: Point[];
  food: Point;
  score: number;
  gameOver: boolean;
  paused: boolean;
  gridWidth: number;
  gridHeight: number;
  direction: string;
}

const CELL_SIZE = 25;
const POLL_MS = 50;

const BG        = '#1E1E1E';
const GRID      = '#2D2D2D';
const HEAD      = '#00C850';
const BODY      = '#00A03C';
const FOOD      = '#DC3232';
const TEXT      = '#FFFFFF';
const OVERLAY   = 'rgba(0, 0, 0, 0.63)';

const canvas  = document.getElementById('game') as HTMLCanvasElement;
const ctx     = canvas.getContext('2d')!;
const scoreEl = document.getElementById('score')!;

let sized = false;

async function fetchState(): Promise<GameState | null> {
  try {
    const res = await fetch('/api/state');
    return await res.json();
  } catch {
    return null;
  }
}

async function sendAction(action: string): Promise<void> {
  try {
    await fetch('/api/action', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action }),
    });
  } catch { /* server not ready */ }
}

function render(state: GameState): void {
  if (!sized) {
    canvas.width  = state.gridWidth  * CELL_SIZE;
    canvas.height = state.gridHeight * CELL_SIZE;
    sized = true;
  }

  const w = canvas.width;
  const h = canvas.height;

  // background
  ctx.fillStyle = BG;
  ctx.fillRect(0, 0, w, h);

  // grid lines
  ctx.strokeStyle = GRID;
  ctx.lineWidth = 1;
  for (let x = 0; x <= state.gridWidth; x++) {
    ctx.beginPath();
    ctx.moveTo(x * CELL_SIZE + 0.5, 0);
    ctx.lineTo(x * CELL_SIZE + 0.5, h);
    ctx.stroke();
  }
  for (let y = 0; y <= state.gridHeight; y++) {
    ctx.beginPath();
    ctx.moveTo(0, y * CELL_SIZE + 0.5);
    ctx.lineTo(w, y * CELL_SIZE + 0.5);
    ctx.stroke();
  }

  // snake
  state.snake.forEach((p, i) => {
    ctx.fillStyle = i === 0 ? HEAD : BODY;
    roundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2, 6);
  });

  // food
  ctx.fillStyle = FOOD;
  ctx.beginPath();
  ctx.arc(
    state.food.x * CELL_SIZE + CELL_SIZE / 2,
    state.food.y * CELL_SIZE + CELL_SIZE / 2,
    (CELL_SIZE - 4) / 2,
    0, Math.PI * 2
  );
  ctx.fill();

  // score
  scoreEl.textContent = `Score: ${state.score}`;

  // overlays
  if (state.gameOver) {
    drawOverlay('GAME OVER', `Score: ${state.score}  —  Press ENTER to restart`);
  } else if (state.paused) {
    drawOverlay('PAUSED', 'Press P to resume');
  }
}

function roundRect(x: number, y: number, w: number, h: number, r: number): void {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.lineTo(x + w - r, y);
  ctx.quadraticCurveTo(x + w, y, x + w, y + r);
  ctx.lineTo(x + w, y + h - r);
  ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
  ctx.lineTo(x + r, y + h);
  ctx.quadraticCurveTo(x, y + h, x, y + h - r);
  ctx.lineTo(x, y + r);
  ctx.quadraticCurveTo(x, y, x + r, y);
  ctx.closePath();
  ctx.fill();
}

function drawOverlay(title: string, subtitle: string): void {
  const w = canvas.width;
  const h = canvas.height;

  ctx.fillStyle = OVERLAY;
  ctx.fillRect(0, 0, w, h);

  ctx.fillStyle = TEXT;
  ctx.textAlign = 'center';

  ctx.font = 'bold 36px "Segoe UI", Arial, sans-serif';
  ctx.fillText(title, w / 2, h / 2 - 10);

  ctx.font = '16px "Segoe UI", Arial, sans-serif';
  ctx.fillText(subtitle, w / 2, h / 2 + 24);

  ctx.textAlign = 'start';
}

// keyboard input
const KEY_MAP: Record<string, string> = {
  ArrowUp:    'UP',
  ArrowDown:  'DOWN',
  ArrowLeft:  'LEFT',
  ArrowRight: 'RIGHT',
  p: 'PAUSE',
  P: 'PAUSE',
  Enter: 'RESTART',
};

document.addEventListener('keydown', (e) => {
  const action = KEY_MAP[e.key];
  if (action) {
    e.preventDefault();
    sendAction(action);
  }
});

// poll loop
async function loop(): Promise<void> {
  const state = await fetchState();
  if (state) {
    render(state);
  }
  setTimeout(loop, POLL_MS);
}

loop();
