import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class ContrastiveLearningAssignment {

    public static void main(String[] args) {
        // C 為相似度上限常數
        double C = 100.0; 
        String folderPath = "C:/Users/user/Desktop/5月6號課堂作業/";

        try {
            // 提取特徵 D(x) = [重心X, 重心Y, 寬度分布, 高度分布, 點數比例]
            double[] feat123 = getEnhancedFeatures(new File(folderPath + "123.jpg"));
            double[] feat234 = getEnhancedFeatures(new File(folderPath + "234.jpg"));
            double[] feat345 = getEnhancedFeatures(new File(folderPath + "345.jpg"));

            System.out.println("--- 最終強化版：形狀與體積比對 ---");

            // 計算 L = max(0, C - ||D(x1) - D(x2)||_2)
            double L_234_123 = calculateL(feat234, feat123, C); // 貓 vs 貓
            double L_234_345 = calculateL(feat234, feat345, C); // 貓 vs 猩猩

            System.out.printf("234(貓) 與 123(貓) 的相似度 L = %.4f\n", L_234_123);
            System.out.printf("234(貓) 與 345(猩猩) 的相似度 L = %.4f\n", L_234_345);

        } catch (Exception e) {
            System.out.println("發生錯誤：" + e.getMessage());
        }
    }

    /**
     * 提取增強特徵：包含位置、體型胖瘦以及「體積感(點數比例)」
     */
    public static double[] getEnhancedFeatures(File file) throws Exception {
        if (!file.exists()) throw new Exception("找不到檔案: " + file.getName());
        BufferedImage img = ImageIO.read(file);
        int w = img.getWidth();
        int h = img.getHeight();
        
        List<Integer> px = new ArrayList<>();
        List<Integer> py = new ArrayList<>();
        int threshold = 220; 

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = img.getRGB(x, y);
                if (((rgb >> 16) & 0xFF) < threshold) {
                    px.add(x);
                    py.add(y);
                }
            }
        }

        if (px.isEmpty()) return new double[]{0, 0, 0, 0, 0};

        // 1. 重心 (位置)
        double mx = 0, my = 0;
        for (int i = 0; i < px.size(); i++) { mx += px.get(i); my += py.get(i); }
        mx /= px.size(); my /= py.size();

        // 2. 標準差 (胖瘦高矮)
        double varX = 0, varY = 0;
        for (int i = 0; i < px.size(); i++) {
            varX += Math.pow(px.get(i) - mx, 2);
            varY += Math.pow(py.get(i) - my, 2);
        }
        double sdX = Math.sqrt(varX / px.size());
        double sdY = Math.sqrt(varY / px.size());

        // 3. 點數比例 (體積感) - 猩猩會明顯大於貓
        double density = (double) px.size() / (w * h) * 100; 

        return new double[]{mx, my, sdX, sdY, density};
    }

    public static double calculateL(double[] f1, double[] f2, double C) {
        double distSq = 0;
        for (int i = 0; i < f1.length; i++) {
            // 為了讓物種差異更明顯，可以對體型和體積特徵(索引2,3,4)增加權重
            double weight = (i >= 2) ? 2.0 : 1.0; 
            distSq += Math.pow((f1[i] - f2[i]) * weight, 2);
        }
        return Math.max(0, C - Math.sqrt(distSq));
    }
}
