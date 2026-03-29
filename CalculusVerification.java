public class CalculusVerification {

    // (1) Finite Difference derivative
    public static double f(double x) {
        // Function f(x) = sin(x) if x > 0; else f(x) = 0
        if (x > 0) {
            return Math.sin(x);
        }
        return 0;
    }

    // (2) f'(x)
    public static double fprime(double x) {
        return Math.cos(x);
    }

    // (3) Forward Difference: (f(x+h) - f(x)) / h
    public static double forwardDiff(double x, double h) {
        return (f(x + h) - f(x)) / h;
    }

    // (4) Central Difference: (f(x+h) - f(x-h)) / (2 * h)
    public static double centralDiff(double x, double h) {
        return (f(x + h) - f(x - h)) / (2 * h);
    }

    // Riemann Sum 計算方法 (對應 Verification 2)
    public static double riemannSum(double a, double b, int n) {
        double dx = (b - a) / n;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += f(a + (i + 0.5) * dx) * dx;
        }
        return sum;
    }

    public static void main(String[] args) {
        // --- 終端機第一部分：Math Explanation (完全還原) ---
        System.out.println("--- Math Explanation (paste into Slide) ---");
        System.out.println();
        System.out.println("(1) Finite Difference Derivative");
        System.out.println("Derivative definition:");
        System.out.println("f'(x) = lim_{h->0} [ f(x+h) - f(x) ] / h");
        System.out.println("Finite difference approx. instead of taking the limit:");
        System.out.println("  forward difference:  f'(x) ≈ ( f(x+h) - f(x) ) / h");
        System.out.println("  central difference:  f'(x) ≈ ( f(x+h) - f(x-h) ) / (2*h)");
        System.out.println("Typical error orders:");
        System.out.println("  forward: O(h)");
        System.out.println("  central: O(h^2)  (more accurate as h decreases)");
        System.out.println();

        // --- 終端機第二部分：Riemann Sum 數學說明 (完全還原) ---
        System.out.println("(5) Riemann Sum (Integral Approximation)");
        System.out.println("Definite integral as limit of sums:");
        System.out.println("  I = ∫_a^b f(x) dx = lim_{n→∞} ∑_{i=1}^n f(x_i*) Δx");
        System.out.println("  where Δx = (b-a)/n and x_i* is a sample point in each sub-interval.");
        System.out.println("Common choices:");
        System.out.println("  Left sum:     x_i* = a + (i-1)Δx");
        System.out.println("  Right sum:    x_i* = a + iΔx");
        System.out.println("  Midpoint sum: x_i* = a + (i-0.5)Δx");
        System.out.println("As N increases (dx decreases), the sum converges to the true integral.");
        System.out.println();

        // --- 終端機第三部分：Verification 1 表格 (完全還原) ---
        System.out.println("--- Verification 1: Finite difference derivative ---");
        System.out.println("Function: f(x) = sin(x), true derivative f'(x) = cos(x)");
        
        double x0 = 1.2589;
        double trueDeriv = fprime(x0);
        System.out.println("Point x0 = " + x0);
        System.out.println("True derivative cos(x0) = " + trueDeriv);
        System.out.println();

        // 表格標題
        System.out.printf("%-10s %-20s %-20s %-20s %-20s\n", 
                          "h", "forwardDiff", "distToTrue", "centralDiff", "distToTrue");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        // 迭代計算
        for (int i = 1; i <= 8; i++) {
            double h = Math.pow(10, -i);
            double fd = forwardDiff(x0, h);
            double cd = centralDiff(x0, h);
            System.out.printf("%-10.1e %-20.10f %-20.5e %-20.10f %-20.5e\n", 
                              h, fd, Math.abs(fd - trueDeriv), cd, Math.abs(cd - trueDeriv));
        }
        System.out.println();

        // --- 終端機第四部分：Verification 2 積分結果 (完全還原) ---
        double a = 0, b = Math.PI, trueInt = 2.0;
        int n = 1000000;
        double result = riemannSum(a, b, n);
        System.out.printf("計算結果: %.6f, 真實值: %.1f, 誤差: %.7e\n", 
                          result, trueInt, Math.abs(result - trueInt));
    }
}
