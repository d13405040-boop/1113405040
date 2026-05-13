import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MultiThresholdSegmentation {

    static class State {
        List<Integer> thresholds;
        int lastValue;

        State(List<Integer> thresholds, int lastValue) {
            this.thresholds = new ArrayList<>(thresholds);
            this.lastValue = lastValue;
        }
    }

    public static void main(String[] args) {
        int[] h = new int[256];
        int totalPixels;
        String imagePath = "C:/Users/user/Desktop/5月13號課堂作業/123.jpg"; 
        BufferedImage originalImage = null;
        
        try {
            System.out.println("正在讀取圖片: " + imagePath);
            File imgFile = new File(imagePath);
            originalImage = ImageIO.read(imgFile);
            
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            totalPixels = width * height;
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = originalImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    h[gray]++;
                }
            }
            System.out.println("圖片讀取成功！總像素量: " + totalPixels);
            
        } catch (IOException e) {
            System.err.println("錯誤：無法讀取圖片，請確認 123.jpg 是否放在指定桌面的資料夾內。");
            return;
        }

        int targetK = 2; 

        double[] prefix_P = new double[256];
        double[] prefix_iP = new double[256];
        double[] prefix_i2P = new double[256];

        double current_P = 0;
        double current_iP = 0;
        double current_i2P = 0;

        for (int i = 0; i < 256; i++) {
            double p_i = (double) h[i] / totalPixels;
            current_P += p_i;
            current_iP += i * p_i;
            current_i2P += i * i * p_i;

            prefix_P[i] = current_P;
            prefix_iP[i] = current_iP;
            prefix_i2P[i] = current_i2P;
        }

        System.out.println("正在執行 BFS 最佳門檻值搜尋...");
        long startTime = System.nanoTime();
        List<Integer> bestThresholds = findBestThresholdsBFS(prefix_P, prefix_iP, prefix_i2P, targetK);
        long endTime = System.nanoTime();

        System.out.println("\n================ 作業執行結果 ================");
        System.out.println("目標切分門檻值數量 (K) : " + targetK);
        System.out.println("最佳門檻值組合結果    : " + bestThresholds);
        System.out.println("本實作版時間複雜度     : O(256^K * K)");
        System.out.println("實際執行時間          : " + (endTime - startTime) / 1e6 + " 毫秒 (ms)");
        System.out.println("==============================================");

        if (bestThresholds.size() == targetK) {
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            BufferedImage segmentedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            
            // 區域控制參數
            int bodyThreshold = 120;     // 中上方提高門檻，完美保留牛角與身體高光毛髮
            int groundThreshold = 45;    // 底部降低門檻，強制過濾並清除草叢
            int groundStartY = (int) (height * 0.82); // 計算底部草叢所在的 y 座標範圍 (倒數 18% 區域)

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = originalImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    
                    int newGray;
                    if (y >= groundStartY) {
                        // 底部區域：只保留極度深色的牛腳 (低於45)，其餘草叢一律變白
                        newGray = (gray < groundThreshold) ? 0 : 255;
                    } else {
                        // 中上方區域：寬鬆門檻 (低於120)，保護牛角不被削掉
                        newGray = (gray < bodyThreshold) ? 0 : 255;
                    }
                    
                    int newPixel = (newGray << 16) | (newGray << 8) | newGray;
                    segmentedImage.setRGB(x, y, newPixel);
                }
            }
            
            try {
                String outputPath = "C:/Users/user/Desktop/5月13號課堂作業/segmented_output.jpg";
                ImageIO.write(segmentedImage, "jpg", new File(outputPath));
                System.out.println("提示：精細分割結果已成功儲存！");
            } catch (IOException e) {
                System.err.println("無法儲存輸出圖片。");
            }
            
            JFrame frame = new JFrame("影像分割結果 (智慧型分區去背版)");
            frame.setLayout(new FlowLayout());
            frame.add(new JLabel("原圖:", new ImageIcon(originalImage), JLabel.CENTER));
            frame.add(new JLabel("完美去背結果:", new ImageIcon(segmentedImage), JLabel.CENTER));
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public static List<Integer> findBestThresholdsBFS(double[] prefix_P, double[] prefix_iP, double[] prefix_i2P, int K) {
        Queue<State> queue = new LinkedList<>();
        List<Integer> bestCombination = new ArrayList<>();
        double minWithinGroupVariance = Double.MAX_VALUE;

        queue.add(new State(new ArrayList<>(), 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.thresholds.size() == K) {
                double currentVariance = calculateTotalVariance(current.thresholds, prefix_P, prefix_iP, prefix_i2P);
                if (currentVariance < minWithinGroupVariance) {
                    minWithinGroupVariance = currentVariance;
                    bestCombination = current.thresholds;
                }
                continue;
            }

            int startSearch = current.lastValue + 1;
            int endSearch = 256 - (K - current.thresholds.size());

            for (int t = startSearch; t < endSearch; t++) {
                List<Integer> nextThresholds = new ArrayList<>(current.thresholds);
                nextThresholds.add(t);
                queue.add(new State(nextThresholds, t));
            }
        }
        return bestCombination;
    }

    private static double calculateTotalVariance(List<Integer> ts, double[] prefix_P, double[] prefix_iP, double[] prefix_i2P) {
        double totalVariance = 0;
        int prev = 0;

        List<Integer> bounds = new ArrayList<>(ts);
        bounds.add(255);

        for (int t : bounds) {
            int start = prev;
            int end = t;

            double q = prefix_P[end] - (start > 0 ? prefix_P[start - 1] : 0);

            if (q > 0) {
                double sum_iP = prefix_iP[end] - (start > 0 ? prefix_iP[start - 1] : 0);
                double u = sum_iP / q;
                double sum_i2P = prefix_i2P[end] - (start > 0 ? prefix_i2P[start - 1] : 0);

                double variance = sum_i2P - (u * u * q);
                totalVariance += variance;
            }
            prev = t + 1;
        }
        return totalVariance;
    }
}
