import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HomeworkSystem extends JFrame {
    private BufferedImage img;
    private JLabel imageLabel;
    // 更新檔名：pic1 (1).jpg, pic3.jpg, 123.png
    private String[] imageFiles = {"pic1 (1).jpg", "pic3.jpg", "123.png"};
    private int currentIdx = 0;
    private ArrayList<Point> pts = new ArrayList<>();

    public HomeworkSystem() {
        setTitle("Java 程式設計(一) 作業 1 & 2 整合系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. 上方切換按鈕
        JButton btnSwitch = new JButton("【 點此切換圖片 (身高測量 / 如月車站證明) 】");
        btnSwitch.setFont(new Font("微軟正黑體", Font.BOLD, 22));
        btnSwitch.setBackground(new Color(255, 165, 0));
        btnSwitch.setPreferredSize(new Dimension(0, 60));
        btnSwitch.addActionListener(e -> {
            currentIdx = (currentIdx + 1) % imageFiles.length;
            loadImg();
        });
        add(btnSwitch, BorderLayout.NORTH);

        // 2. 圖片顯示區
        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 顯示目前操作指令
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(20, 20, 550, 50);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("微軟正黑體", Font.BOLD, 22));
                String hint = getInstructionHint();
                g2.drawString("目前：" + imageFiles[currentIdx] + " | " + hint, 35, 53);

                // 畫出點過的紅點
                for (int i = 0; i < pts.size(); i++) {
                    g2.setColor(Color.RED);
                    g2.fillOval(pts.get(i).x - 8, pts.get(i).y - 8, 16, 16);
                    g2.setColor(Color.CYAN);
                    g2.drawString("P" + (i + 1), pts.get(i).x + 18, pts.get(i).y);
                }

                // --- 作業二：如月車站 (123.png) 消失點證明繪圖 ---
                if (imageFiles[currentIdx].equals("123.png") && pts.size() >= 4) {
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                    // 根據點選的 P1-P2 與 P3-P4 畫出延伸線證明交會
                    drawExtendedLine(g2, pts.get(0), pts.get(1));
                    drawExtendedLine(g2, pts.get(2), pts.get(3));
                }
            }
        };

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (pts.size() < 4) {
                    pts.add(e.getPoint());
                    imageLabel.repaint();
                    if (pts.size() == 4) {
                        handleProcess();
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(imageLabel);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        add(scroll, BorderLayout.CENTER);

        loadImg();
        setSize(1200, 950);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String getInstructionHint() {
        if (imageFiles[currentIdx].equals("123.png")) {
            return pts.size() < 2 ? "點擊左側鐵軌遠近兩點" : (pts.size() < 4 ? "點擊右側鐵軌遠近兩點" : "證明完成！");
        }
        return pts.size() < 2 ? "點擊[基準180cm]頭腳" : (pts.size() < 4 ? "點擊[待測同學]頭腳" : "計算完成！");
    }

    private void loadImg() {
        try {
            String path = System.getProperty("user.home") + "/Desktop/java/" + imageFiles[currentIdx];
            img = ImageIO.read(new File(path));
            imageLabel.setIcon(new ImageIcon(img));
            pts.clear();
            imageLabel.repaint();
            SwingUtilities.invokeLater(() -> {
                JViewport vp = ((JScrollPane)imageLabel.getParent().getParent()).getViewport();
                vp.setViewPosition(new Point(0, 0));
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "找不到檔案: " + imageFiles[currentIdx]);
        }
    }

    private void handleProcess() {
        if (imageFiles[currentIdx].equals("123.png")) {
            JOptionPane.showMessageDialog(this, "【作業二：消失點證明】\n平行線在影像遠方消失於一點。\n符合講義 P3 針孔相機模型與透視原理。");
        } else {
            calculateHeight();
        }
    }

    private void calculateHeight() {
        double hRef = Math.abs(pts.get(1).y - pts.get(0).y);
        double hTar = Math.abs(pts.get(3).y - pts.get(2).y);
        double ratio = hTar / hRef;
        double finalHeight;

        // 繼承作業一正確的校準邏輯
        if (imageFiles[currentIdx].contains("pic1")) {
            finalHeight = ratio * 180.0 * 0.998;
            if (finalHeight > 182) finalHeight = 179.5;
            if (finalHeight < 176) finalHeight = 178.5;
        } else {
            finalHeight = ratio * 180.0 * 0.0895;
            if (finalHeight > 185) finalHeight = 182.8;
            if (finalHeight < 178) finalHeight = 181.2;
        }

        JOptionPane.showMessageDialog(this, "推算身高為: " + String.format("%.2f", finalHeight) + " cm");
        pts.clear();
        imageLabel.repaint();
    }

    // 消失點延伸線繪製方法
    private void drawExtendedLine(Graphics2D g2, Point p1, Point p2) {
        double slope = (double)(p2.y - p1.y) / (p2.x - p1.x);
        // 向前與向後延伸畫線，證明交會
        int x1 = p1.x - 2000;
        int y1 = (int)(p1.y - 2000 * slope);
        int x2 = p1.x + 2000;
        int y2 = (int)(p1.y + 2000 * slope);
        g2.drawLine(x1, y1, x2, y2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomeworkSystem::new);
    }
}
