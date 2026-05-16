import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class KurumiProcessor {

    public static void main(String[] args) {
        try {
            // 1. 設定檔案路徑 (依照你的截圖，檔案在桌面 java 資料夾)
            String path = "C:/Users/user/Desktop/java/";
            File file = new File(path + "123.jpg"); // 或是 123.png

            if (!file.exists()) {
                System.out.println("找不到 123.jpg，請確認檔名與副檔名。");
                return;
            }

            BufferedImage img = ImageIO.read(file);
            int w = img.getWidth();
            int h = img.getHeight();
            int[][] gray = new int[w][h];

            // 2. 轉灰階並增強對比 (狂三的細節很多，需要更好的灰階轉換)
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = new Color(img.getRGB(x, y));
                    // 使用權重灰階公式，對紅色（衣服）更敏感
                    int g = (int)(c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                    gray[x][y] = g;
                }
            }

            // --- 部分一：X/Y 梯度偵測 (yu 的邏輯) ---
            BufferedImage xGrad = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            BufferedImage yGrad = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < h; y++) {
                for (int x = 1; x < w - 1; x++) {
                    // 計算左右亮度差，並放大差異讓輪廓更明顯
                    int diffX = Math.min(255, Math.abs(gray[x + 1][y] - gray[x - 1][y]) * 2);
                    xGrad.setRGB(x, y, new Color(diffX, diffX, diffX).getRGB());
                }
            }

            for (int y = 1; y < h - 1; y++) {
                for (int x = 0; x < w; x++) {
                    // 計算上下亮度差
                    int diffY = Math.min(255, Math.abs(gray[x][y + 1] - gray[x][y - 1]) * 2);
                    yGrad.setRGB(x, y, new Color(diffY, diffY, diffY).getRGB());
                }
            }
            ImageIO.write(xGrad, "jpg", new File(path + "kurumi_X_scan.jpg"));
            ImageIO.write(yGrad, "jpg", new File(path + "kurumi_Y_scan.jpg"));

            // --- 部分二：卡方距離偵測 (ChiSquareScan 的邏輯) ---
            BufferedImage chiImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            double[][] chiResult = new double[w][h];
            double maxChi = 0;
            int win = 2; // 縮小視窗讓狂三的細節（頭髮）更銳利
            int bins = 8; // 減少分箱數增加穩定度

            for (int y = win; y < h - win; y++) {
                for (int x = win; x < w - win; x++) {
                    int[] h1 = new int[bins];
                    int[] h2 = new int[bins];
                    for (int yy = y - win; yy <= y + win; yy++) {
                        for (int xx = x - win; xx < x; xx++) h1[gray[xx][yy] * bins / 256]++;
                        for (int xx = x + 1; xx <= x + win; xx++) h2[gray[xx][yy] * bins / 256]++;
                    }
                    double chi = 0;
                    for (int k = 0; k < bins; k++) {
                        if (h1[k] + h2[k] > 0) chi += Math.pow(h1[k] - h2[k], 2) / (h1[k] + h2[k]);
                    }
                    chiResult[x][y] = chi;
                    if (chi > maxChi) maxChi = chi;
                }
            }

            // 將卡方結果繪製出來
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int val = (maxChi == 0) ? 0 : (int)((chiResult[x][y] / maxChi) * 255);
                    val = Math.min(255, val * 3); // 增強亮度
                    chiImg.setRGB(x, y, new Color(val, val, val).getRGB());
                }
            }
            ImageIO.write(chiImg, "jpg", new File(path + "kurumi_chi_square.jpg"));

            System.out.println("偵測完成！請檢查桌面 java 資料夾中的圖片。");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
