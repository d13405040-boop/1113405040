import java.util.*;

interface LecturePage { void display(); }

// [Page 2] 固定輸入：發生個數 10，全部可能 50
class Page02_Definition implements LecturePage {
    private final int count = 10, total = 50;
    public void display() {
        System.out.printf("[Page 2] 機率定義：P(A) = %d/%d = %.4f\n", count, total, (double)count/total);
    }
}

class Page03_SampleSpace implements LecturePage {
    int nS; public Page03_SampleSpace(int nS) { this.nS = nS; }
    public void display() { System.out.println("[Page 3] 樣本空間 (S)：總數 n(S) = " + nS); }
}

class Page04_Event implements LecturePage {
    int nA; public Page04_Event(int nA) { this.nA = nA; }
    public void display() { System.out.println("[Page 4] 事件 (A)：個數 n(A) = " + nA); }
}

class Page05_BasicFormula implements LecturePage {
    int nA, nS; public Page05_BasicFormula(int nA, int nS) { this.nA = nA; this.nS = nS; }
    public void display() {
        System.out.printf("[Page 5] 基本公式：P(A) = n(A)/n(S) = %d/%d = %.4f\n", nA, nS, (double)nA/nS);
    }
}

class Page06_Complement implements LecturePage {
    double pA; public Page06_Complement(double pA) { this.pA = pA; }
    public void display() {
        System.out.printf("[Page 6] 補事件：P(A^c) = 1 - P(A) = 1 - %.2f = %.2f\n", pA, 1 - pA);
    }
}

class Page07_Union implements LecturePage {
    double pA, pB, pInter;
    public Page07_Union(double a, double b, double i) { this.pA = a; this.pB = b; this.pInter = i; }
    public void display() {
        System.out.printf("[Page 7] 聯集：P(A∪B) = P(A)+P(B)-P(A∩B) = %.2f+%.2f-%.2f = %.2f\n", pA, pB, pInter, pA + pB - pInter);
    }
}

class Page08_Intersection implements LecturePage {
    double pA, pBgivenA;
    public Page08_Intersection(double a, double bga) { this.pA = a; this.pBgivenA = bga; }
    public void display() {
        System.out.printf("[Page 8] 交集：P(A∩B) = P(A)P(B|A) = %.2f * %.2f = %.4f\n", pA, pBgivenA, pA * pBgivenA);
    }
}

class Page09_Conditional implements LecturePage {
    double pInter, pB;
    public Page09_Conditional(double pi, double pb) { this.pInter = pi; this.pB = pb; }
    public void display() {
        System.out.printf("[Page 9] 條件機率：P(A|B) = P(A∩B)/P(B) = %.2f / %.2f = %.4f\n", pInter, pB, pInter / pB);
    }
}

class Page10_Independent implements LecturePage {
    double pA, pB;
    public Page10_Independent(double a, double b) { this.pA = a; this.pB = b; }
    public void display() {
        System.out.printf("[Page 10] 獨立性：P(A)P(B) = %.2f * %.2f = %.4f\n", pA, pB, pA * pB);
    }
}

class Page11_Bayes implements LecturePage {
    double pBA, pA, pB;
    public Page11_Bayes(double pba, double pa, double pb) { this.pBA = pba; this.pA = pa; this.pB = pb; }
    public void display() {
        System.out.printf("[Page 11] 貝氏定理：P(A|B) = (P(B|A)*P(A))/P(B) = (%.2f*%.2f)/%.2f = %.4f\n", pBA, pA, pB, (pBA * pA) / pB);
    }
}

// [Page 12] 全機率公式：展開完整運算式
class Page12_TotalProb implements LecturePage {
    double pA_B1, pB1, pA_B2, pB2;
    public Page12_TotalProb(double a1, double b1, double a2, double b2) {
        this.pA_B1 = a1; this.pB1 = b1; this.pA_B2 = a2; this.pB2 = b2;
    }
    public void display() {
        double term1 = pA_B1 * pB1;
        double term2 = pA_B2 * pB2;
        double total = term1 + term2;
        System.out.println("[Page 12] 全機率公式：P(A) = Σ P(A|Bi)P(Bi)");
        System.out.printf("          = P(A|B1)P(B1) + P(A|B2)P(B2)\n");
        System.out.printf("          = (%.2f * %.2f) + (%.2f * %.2f)\n", pA_B1, pB1, pA_B2, pB2);
        System.out.printf("          = %.3f + %.3f = %.4f\n", term1, term2, total);
    }
}

class Page13_SchoolExample implements LecturePage {
    int J, B, N;
    public Page13_SchoolExample(int j, int b, int n) { this.J = j; this.B = b; this.N = n; }
    public void display() {
        System.out.printf("[Page 13] 學校例子：P(建中)=%d/%d=%.3f, P(北一女)=%d/%d=%.3f\n", J, N, (double)J/N, B, N, (double)B/N);
    }
}

class Page14_UnionExclusive implements LecturePage {
    int J, B, N;
    public Page14_UnionExclusive(int j, int b, int n) { this.J = j; this.B = b; this.N = n; }
    public void display() {
        System.out.printf("[Page 14] 互斥聯集：P(J∪B) = (%d+%d)/%d = %.4f\n", J, B, N, (double)(J+B)/N);
    }
}

public class ProbabilityAssignment {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(">>> 請輸入參數 (J B N pA pB pInter pBgivenA pBA_B pA_B pB_B) <<<");
        // 建議輸入: 150 100 500 0.3 0.2 0.05 0.1 0.8 0.1 0.2
        int J = sc.nextInt(), B = sc.nextInt(), N = sc.nextInt();
        double pA = sc.nextDouble(), pB = sc.nextDouble(), pInter = sc.nextDouble();
        double pBgivenA = sc.nextDouble();
        double pBA_B = sc.nextDouble(), pA_B = sc.nextDouble(), pB_B = sc.nextDouble();

        List<LecturePage> pages = new ArrayList<>();
        pages.add(new Page02_Definition());
        pages.add(new Page03_SampleSpace(N));
        pages.add(new Page04_Event(J));
        pages.add(new Page05_BasicFormula(J, N));
        pages.add(new Page06_Complement(pA));
        pages.add(new Page07_Union(pA, pB, pInter));
        pages.add(new Page08_Intersection(pA, pBgivenA));
        pages.add(new Page09_Conditional(pInter, pB));
        pages.add(new Page10_Independent(pA, pB));
        pages.add(new Page11_Bayes(pBA_B, pA_B, pB_B));
        
        // Page 12: 假設兩個分組 Bi (例如男生/女生) 的數據進行展開
        pages.add(new Page12_TotalProb(0.8, 0.6, 0.5, 0.4)); 
        
        pages.add(new Page13_SchoolExample(J, B, N));
        pages.add(new Page14_UnionExclusive(J, B, N));

        System.out.println("\n" + "=".repeat(70));
        System.out.println("              Assignment 1: Probability Calculation Details");
        System.out.println("=".repeat(70));
        for (LecturePage p : pages) { p.display(); System.out.println("-".repeat(70)); }
        sc.close();
    }
}
