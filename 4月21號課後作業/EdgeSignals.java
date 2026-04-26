import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class EdgeSignals {

    public static void main(String[] args) {
        try {
            // 1. 讀取圖片 (請確認檔名與路徑正確，例如您資料夾內的 2026_04_21_094021.png)
// 請根據你電腦的實際路徑修改，範例如下：
            File input = new File("C:/Users/User/Desktop/java/zoo.png"); 
            BufferedImage image = ImageIO.read(input);
            int width = image.getWidth();
            int height = image.getHeight();

            // 2. 將圖片轉換為二維灰階陣列 f(x,y)
            int[][] grayScale = new int[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(image.getRGB(x, y));
                    int gray = (int) (c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
                    grayScale[y][x] = gray;
                }
            }

            // 3. 準備儲存結果的圖片
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

            // 4. 套用講義公式：有限差分 (Finite Difference)
            // 遍歷像素（避開邊界防止 index 溢出）
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    
                    // Ix = (f(x+1, y) - f(x-1, y)) / 2
                    double gradX = (grayScale[y][x + 1] - grayScale[y][x - 1]) / 2.0;
                    
                    // Iy = (f(x, y+1) - f(x, y-1)) / 2
                    double gradY = (grayScale[y + 1][x] - grayScale[y - 1][x]) / 2.0;
                    
                    // 計算梯度強度 Magnitude = sqrt(Ix^2 + Iy^2)
                    int magnitude = (int) Math.min(255, Math.sqrt(gradX * gradX + gradY * gradY));
                    
                    int displayValue = (int)(gradX + 128); 

                    // 設定結果像素顏色
                    Color newColor = new Color(displayValue, displayValue, displayValue);
                    outputImage.setRGB(x, y, newColor.getRGB());
                }
            }

            // 5. 匯出結果
            // 5. 匯出結果 (指定存到你的 java 資料夾)
            File outputFile = new File("C:/Users/User/Desktop/java/edge_result.png");
            ImageIO.write(outputImage, "png", outputFile);
            System.out.println("成功存檔到：C:/Users/User/Desktop/java/edge_result.png");


        } catch (Exception e) {
            System.out.println("發生錯誤: " + e.getMessage());
        }
    }
}
