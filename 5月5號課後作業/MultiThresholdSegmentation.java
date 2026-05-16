import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MultiThresholdSegmentation {

    public static void main(String[] args) {
        try {
            // 1. 讀取影像 (請確保路徑正確)
            File input = new File("123.jpg");
            BufferedImage image = ImageIO.read(input);
            int width = image.getWidth();
            int height = image.getHeight();

            // 2. 轉為灰階並計算直方圖
            int[] histogram = new int[256];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(image.getRGB(x, y));
                    int gray = (int) (c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                    histogram[gray]++;
                }
            }

            // 3. 執行 Otsu 多門檻演算法 (尋找兩個門檻 T1, T2)
            int[] thresholds = findTwoThresholds(histogram, width * height);
            int T1 = thresholds[0];
            int T2 = thresholds[1];
            System.out.println("最佳門檻值: T1 = " + T1 + ", T2 = " + T2);

            // 4. 根據門檻產生結果圖 (分為三種亮度層次)
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(image.getRGB(x, y));
                    int gray = (int) (c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                    
                    if (gray < T1) {
                        outputImage.setRGB(x, y, Color.BLACK.getRGB()); // 背景
                    } else if (gray < T2) {
                        outputImage.setRGB(x, y, Color.GRAY.getRGB());  // 中間層
                    } else {
                        outputImage.setRGB(x, y, Color.WHITE.getRGB()); // 前景
                    }
                }
            }

            // 5. 儲存結果
            ImageIO.write(outputImage, "jpg", new File("output_segmented.jpg"));
            System.out.println("處理完成，結果已儲存。");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 多門檻 Otsu 演算法實作
    private static int[] findTwoThresholds(int[] histogram, int totalPixels) {
        double maxVariance = -1;
        int[] bestT = new int[2];

        // 窮舉所有可能的雙門檻組合 (T1 < T2)
        for (int t1 = 0; t1 < 254; t1++) {
            for (int t2 = t1 + 1; t2 < 255; t2++) {
                
                // 計算三部分的機率權重與平均值
                double w0 = 0, w1 = 0, w2 = 0;
                double sum0 = 0, sum1 = 0, sum2 = 0;

                for (int i = 0; i <= t1; i++) {
                    w0 += histogram[i];
                    sum0 += (double) i * histogram[i];
                }
                for (int i = t1 + 1; i <= t2; i++) {
                    w1 += histogram[i];
                    sum1 += (double) i * histogram[i];
                }
                for (int i = t2 + 1; i < 256; i++) {
                    w2 += histogram[i];
                    sum2 += (double) i * histogram[i];
                }

                if (w0 == 0 || w1 == 0 || w2 == 0) continue;

                double m0 = sum0 / w0;
                double m1 = sum1 / w1;
                double m2 = sum2 / w2;
                double mGlobal = (sum0 + sum1 + sum2) / totalPixels;

                // 計算組間變異量 (Between-group variance)
                double variance = (w0 / totalPixels) * Math.pow(m0 - mGlobal, 2) +
                                  (w1 / totalPixels) * Math.pow(m1 - mGlobal, 2) +
                                  (w2 / totalPixels) * Math.pow(m2 - mGlobal, 2);

                if (variance > maxVariance) {
                    maxVariance = variance;
                    bestT[0] = t1;
                    bestT[1] = t2;
                }
            }
        }
        return bestT;
    }
}