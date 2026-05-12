import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

class Node {
    int x, y;
    Node parent;
    public Node(int x, int y, Node parent) { this.x = x; this.y = y; this.parent = parent; }
}

public class AIShooterGame extends JFrame {
    public AIShooterGame() {
        setTitle("AI Shooter - 8-Dir BFS & Pause System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIShooterGame::new);
    }
}

class GamePanel extends JPanel {
    private final int TILE_SIZE = 40, GRID_SIZE = 15;
    private final int WIDTH = TILE_SIZE * GRID_SIZE, HEIGHT = TILE_SIZE * GRID_SIZE;

    private int score = 0, hp = 20, tick = 0; 
    private int shootCooldown = 0;
    private boolean gameOver = false, gameWin = false, isPaused = false;
    
    private Point player = new Point(7, 13);
    private List<Point> enemies = new CopyOnWriteArrayList<>();
    private List<Point> bullets = new CopyOnWriteArrayList<>();
    private List<Point> obstacles = new CopyOnWriteArrayList<>();
    
    private boolean up, down, left, right, shooting;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH + 200, HEIGHT));
        setBackground(new Color(10, 10, 15));
        initLevel();
        setupKeyBindings();

        new Thread(() -> {
            while (true) {
                try {
                    // 只有在未暫停且未結束時才更新邏輯
                    if (!isPaused && !gameOver && !gameWin) update();
                    repaint();
                    Thread.sleep(30);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }).start();
    }

    private void initLevel() {
        obstacles.clear(); enemies.clear(); bullets.clear();
        Random r = new Random();
        for (int i = 0; i < 5; i++) enemies.add(new Point(r.nextInt(GRID_SIZE), 0));
        for (int i = 0; i < 10; i++) obstacles.add(new Point(r.nextInt(GRID_SIZE), r.nextInt(6) + 2));
    }

    private void update() {
        tick++;
        if (shootCooldown > 0) shootCooldown--;

        // 玩家移動 (偵測斜向操作)
        if (tick % 3 == 0) {
            int nx = player.x, ny = player.y;
            if (up) ny--; if (down) ny++; if (left) nx--; if (right) nx++;
            if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE) {
                player.setLocation(nx, ny);
            }
        }

        // 射擊
        if (shooting && shootCooldown <= 0) {
            bullets.add(new Point(player.x, player.y));
            shootCooldown = 6;
        }

        // 物體移動
        bullets.forEach(b -> { b.y--; if (b.y < 0) bullets.remove(b); });
        if (tick % 25 == 0) {
            for (Point p : obstacles) {
                p.y++;
                if (p.y >= GRID_SIZE) { p.y = 0; p.x = new Random().nextInt(GRID_SIZE); }
            }
        }

        // 8 方向斜線 BFS AI
        int enemySpeed = (score < 1000) ? 18 : 12;
        if (tick % enemySpeed == 0) {
            for (Point en : enemies) {
                List<Node> path = findDiagonalPath(en, player);
                if (path != null && path.size() > 1) {
                    Node nextStep = path.get(1); 
                    en.setLocation(nextStep.x, nextStep.y);
                }
            }
            if (new Random().nextDouble() < 0.1 && enemies.size() < 10) {
                enemies.add(new Point(new Random().nextInt(GRID_SIZE), 0));
            }
        }

        checkCollisions();
        if (score >= 1500) gameWin = true;
        if (hp <= 0) { hp = 0; gameOver = true; }
    }

    private List<Node> findDiagonalPath(Point start, Point target) {
        Queue<Node> queue = new LinkedList<>();
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        queue.add(new Node(start.x, start.y, null));
        visited[start.y][start.x] = true;

        // 包含斜向的 8 方向
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0},{-1,-1},{-1,1},{1,-1},{1,1}};
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            if (curr.x == target.x && curr.y == target.y) {
                LinkedList<Node> path = new LinkedList<>();
                while (curr != null) { path.addFirst(curr); curr = curr.parent; }
                return path;
            }
            for (int[] d : dirs) {
                int nx = curr.x + d[0], ny = curr.y + d[1];
                if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE && !visited[ny][nx] && !isObstacleAt(nx, ny)) {
                    visited[ny][nx] = true;
                    queue.add(new Node(nx, ny, curr));
                }
            }
        }
        return null;
    }

    private boolean isObstacleAt(int x, int y) {
        for (Point p : obstacles) if (p.x == x && p.y == y) return true;
        return false;
    }

    private void checkCollisions() {
        for (Point ob : obstacles) {
            if (ob.equals(player)) { hp--; ob.y = 0; ob.x = new Random().nextInt(GRID_SIZE); }
        }
        for (Point b : bullets) {
            for (Point en : enemies) {
                if (b.equals(en)) { bullets.remove(b); enemies.remove(en); score += 100; break; }
            }
        }
        for (Point en : enemies) if (en.equals(player)) { hp--; enemies.remove(en); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // 隕石
        g2.setColor(new Color(139, 69, 19));
        for (Point p : obstacles) g2.fillOval(p.x*TILE_SIZE+5, p.y*TILE_SIZE+5, 30, 30);
        
        // 敵人
        g2.setColor(Color.RED);
        for (Point en : enemies) {
            int ex = en.x*TILE_SIZE, ey = en.y*TILE_SIZE;
            g2.fillPolygon(new int[]{ex+20, ex+5, ex+35}, new int[]{ey+35, ey+5, ey+5}, 3);
        }

        // 玩家
        g2.setColor(Color.GREEN);
        g2.fillPolygon(new int[]{player.x*TILE_SIZE+20, player.x*TILE_SIZE+5, player.x*TILE_SIZE+35}, 
                        new int[]{player.y*TILE_SIZE+5, player.y*TILE_SIZE+35, player.y*TILE_SIZE+35}, 3);

        // 子彈
        g2.setColor(Color.YELLOW);
        for (Point b : bullets) g2.fillRect(b.x*TILE_SIZE+18, b.y*TILE_SIZE, 4, 15);

        // UI
        g2.setColor(new Color(30, 30, 40));
        g2.fillRect(WIDTH, 0, 200, HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("SCORE: " + score, WIDTH+20, 50);
        g2.drawString("HP: " + hp, WIDTH+20, 90);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("[P] PAUSE", WIDTH+20, 150);
        g2.drawString("[R] RESTART", WIDTH+20, 180);

        if (isPaused) {
            g2.setColor(new Color(0,0,0,150));
            g2.fillRect(0,0,WIDTH,HEIGHT);
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("PAUSED", WIDTH/2-100, HEIGHT/2);
        }

        if (gameOver || gameWin) {
            g2.setColor(new Color(0,0,0,200));
            g2.fillRect(0,0,WIDTH,HEIGHT);
            g2.setColor(gameWin ? Color.CYAN : Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString(gameWin ? "YOU WIN!" : "GAME OVER", WIDTH/2-100, HEIGHT/2);
        }
    }

    private void setupKeyBindings() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { 
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) up = true;
                if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) down = true;
                if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) left = true;
                if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) right = true;
                if (k == KeyEvent.VK_SPACE) shooting = true;
                if (k == KeyEvent.VK_P) isPaused = !isPaused; // 暫停切換
                if (k == KeyEvent.VK_R) { score=0; hp=20; gameWin=false; gameOver=false; isPaused=false; initLevel(); }
            }
            public void keyReleased(KeyEvent e) { 
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) up = false;
                if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) down = false;
                if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) left = false;
                if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) right = false;
                if (k == KeyEvent.VK_SPACE) shooting = false;
            }
        });
    }
}
