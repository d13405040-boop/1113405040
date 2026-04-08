import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// 1. 居民類別
class Resident {
    double x, y;
    int choice; // 0:A(藍), 1:B(黃), 2:C(紅)
    int targetX; 

    public Resident(double x, double y, int choice) {
        this.x = x; this.y = y; this.choice = choice;
        this.targetX = (int)x;
    }

    public double dist(double tx, double ty) {
        return Math.sqrt(Math.pow(x - tx, 2) + Math.pow(y - ty, 2));
    }

    // 根據鄰居決定，設定水平移動目標
    public void setTarget(int nextChoice) {
        if (nextChoice == 0) targetX = 155; // A區中心
        else if (nextChoice == 1) targetX = 340; // B區中心
        else targetX = 525; // C區中心
    }

    // 左右大幅度移動
    public void moveStep() {
        double diff = targetX - x;
        if (Math.abs(diff) > 2) {
            this.x += diff * 0.5; // 大步移動 (移動差值的 50%)
            this.x += (Math.random() - 0.5) * 8; // 微量隨機防止完全重疊
        }
        updateColorByLocation();
    }

    // 核心邏輯：位址改變，顏色(決策)就改變
    public void updateColorByLocation() {
        if (x < 240) choice = 0; // 進入北區範圍變藍色
        else if (x >= 240 && x < 430) choice = 1; // 進入中區範圍變黃色
        else choice = 2; // 進入南區範圍變紅色
    }
}

public class FogCityChallenge extends JPanel {
    private List<Resident> citizens = new ArrayList<>();
    private Point investigator = new Point(325, 650); // 其始點 (下方中間)
    private int K = 1; // 預設 K=1
    private int day = 1;

    public FogCityChallenge() {
        initCityData();
        // 滑鼠互動
        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { move(e); }
            public void mouseDragged(MouseEvent e) { move(e); }
            private void move(MouseEvent e) { investigator.setLocation(e.getPoint()); repaint(); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void initCityData() {
        Random r = new Random();
        citizens.clear();
        // 初始化北、中、南三區居民，暫不設定誤導區
        for(int i=0; i<30; i++) citizens.add(new Resident(r.nextInt(120)+80, r.nextInt(400)+100, 0));
        for(int i=0; i<30; i++) citizens.add(new Resident(r.nextInt(120)+265, r.nextInt(400)+100, 1));
        for(int i=0; i<30; i++) citizens.add(new Resident(r.nextInt(120)+450, r.nextInt(400)+100, 2));
    }

    // 演化邏輯
    private void nextStep() {
        // 1. 決定目標
        for (Resident me : citizens) {
            List<Resident> neighbors = citizens.stream()
                .filter(other -> other != me)
                .sorted(Comparator.comparingDouble(other -> me.dist(other.x, other.y)))
                .limit(K).collect(Collectors.toList());

            if (!neighbors.isEmpty()) {
                int next = neighbors.stream()
                    .collect(Collectors.groupingBy(n -> n.choice, Collectors.counting()))
                    .entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
                me.setTarget(next);
            }
        }
        // 2. 執行移動與變色
        for (Resident me : citizens) me.moveStep();
        day++;
        repaint();
    }

    private int predict(double tx, double ty) {
        if (citizens.isEmpty()) return -1;
        List<Resident> sorted = citizens.stream()
            .sorted(Comparator.comparingDouble(c -> c.dist(tx, ty)))
            .limit(K).collect(Collectors.toList());
        return sorted.stream().collect(Collectors.groupingBy(n -> n.choice, Collectors.counting()))
                     .entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(15, 15, 25)); g.fillRect(0, 0, getWidth(), getHeight());

        // 背景格線
        g2.setColor(new Color(255, 255, 255, 15));
        for(int i=0; i<getWidth(); i+=45) g2.drawLine(i, 0, i, getHeight());
        for(int i=0; i<getHeight(); i+=45) g2.drawLine(0, i, getWidth(), i);

        // 區域框佈局 (對齊截圖)
        drawZone(g2, 75, 100, 160, 480, new Color(0, 180, 255), "北區 出口 A");
        drawZone(g2, 260, 100, 160, 480, new Color(255, 200, 0), "中區 出口 B");
        drawZone(g2, 445, 100, 160, 480, new Color(255, 50, 100), "南區 出口 C");

        // 畫居民點
        for (Resident c : citizens) {
            g.setColor(c.choice == 0 ? new Color(0, 180, 255) : (c.choice == 1 ? new Color(255, 200, 0) : new Color(255, 50, 100)));
            g.fillOval((int)c.x-4, (int)c.y-4, 8, 8);
        }

        // 左上角 UI
        int res = predict(investigator.x, investigator.y);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("當前 K = " + K + " | 第 " + day + " 天", 35, 45);
        g.setColor(res == 2 ? Color.RED : Color.GREEN);
        String out = (res == 0 ? "出口 A" : (res == 1 ? "出口 B" : (res == 2 ? "出口 C" : "未決定")));
        g.drawString("調查員決策：" + out, 35, 75);

        // 連線與調查員 (起始點在最下方)
        g2.setColor(new Color(255, 255, 255, 60));
        citizens.stream().sorted(Comparator.comparingDouble(c -> c.dist(investigator.x, investigator.y)))
                 .limit(K).forEach(n -> g2.drawLine(investigator.x, investigator.y, (int)n.x, (int)n.y));
        g.setColor(Color.WHITE);
        g.fillOval(investigator.x-15, investigator.y-15, 30, 30);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("操作：[ENTER] 演化 (左右移動+自動變色) | [1/3/5] 切換 K | [滑鼠] 拖曳", 35, 710);
    }

    private void drawZone(Graphics2D g2, int x, int y, int w, int h, Color c, String t) {
        g2.setColor(c); g2.setStroke(new BasicStroke(2)); g2.drawRect(x, y, w, h);
        g2.drawString(t, x, y - 10);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("KNN 城市演化：純淨三區版");
        FogCityChallenge sim = new FogCityChallenge();
        f.add(sim); f.setSize(700, 780);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null); f.setVisible(true);

        f.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) sim.nextStep();
                if(e.getKeyCode() == KeyEvent.VK_1) sim.K = 1;
                if(e.getKeyCode() == KeyEvent.VK_3) sim.K = 3;
                if(e.getKeyCode() == KeyEvent.VK_5) sim.K = 5;
                sim.repaint();
            }
        });
    }
}
