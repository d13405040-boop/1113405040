import java.util.Scanner;

class Student {
    String name;
    int score;

    Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }
}

public class StudentScoreSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("請輸入分數的個數：");
        int n = scanner.nextInt();
        Student[] students = new Student[n];

        for (int i = 0; i < n; i++) {
            System.out.print("請輸入第 " + (i + 1) + " 個分數：");
            int s = scanner.nextInt();
            students[i] = new Student("學生 " + (char)('A' + i), s);
        }

        System.out.println("\n[第 4 題] 請輸入一名學生的姓名：");
        String customName = scanner.next();
        System.out.print("請輸入該生分數：");
        int customScore = scanner.nextInt();
        Student specialStudent = new Student(customName, customScore);

        System.out.println("\n========= 執行結果 (1-10) =========");

        double sumOriginal = 0;
        for (Student s : students) sumOriginal += s.score;
        System.out.println("1. 平均: " + (sumOriginal / n));

        int[] currentScores = new int[n];
        for(int i=0; i<n; i++) currentScores[i] = students[i].score;
        int max = findMax(currentScores);
        System.out.println("2. 最高分: " + max);

        addBonus(currentScores);
        for(int i=0; i<n; i++) students[i].score = currentScores[i];
        
        System.out.print("3. 加 5 分後的分數陣列: {");
        for(int i=0; i<n; i++) System.out.print(currentScores[i] + (i==n-1?"":", "));
        System.out.println("}");

        System.out.println("4. 學生名與分:\n" + specialStudent);

        System.out.println("\n5. 不及格者加10：");
        for (Student s : students) {
            int oldScore = s.score;
            curve(s);
            if (s.score > oldScore) {
                System.out.println("加分成功 -> " + s);
            } else {
                System.out.println("維持原分 -> " + s);
            }
        }

        int pass = 0;
        for (Student s : students) if (s.score >= 60) pass++;
        System.out.println("\n6. 高於60分人數: " + pass);

        int[] finalScores = new int[n];
        for(int i=0; i<n; i++) finalScores[i] = students[i].score;
        System.out.println("7. 分總和: " + sum(finalScores));

        System.out.println("\n[第 8 題] 請輸入 3 名學生的資料：");
        Student[] studentArray8 = new Student[3];
        for (int i = 0; i < 3; i++) {
            System.out.print("請輸入第 " + (i + 1) + " 位學生姓名: ");
            String n8 = scanner.next();
            System.out.print("請輸入第 " + (i + 1) + " 位學生分數: ");
            int s8 = scanner.nextInt();
            studentArray8[i] = new Student(n8, s8);
        }
        System.out.println("8. 學生的名與分:");
        for (Student s : studentArray8) System.out.println(s);

        System.out.println("\n[第 9 題] 修改三位學生的分數：");
        for (int i = 0; i < 3; i++) {
            System.out.print("請輸入 " + studentArray8[i].name + " 的新分數: ");
            int newS = scanner.nextInt();
            updateScore(studentArray8[i], newS);
        }
        System.out.println("9. 修改後分數:");
        for (Student s : studentArray8) System.out.println(s);

        System.out.println("\n[第 10 題] 輸入整數陣列以找出最小數字：");
        System.out.print("請輸入數字的量：");
        int n10 = scanner.nextInt();
        int[] scores10 = new int[n10];
        for (int i = 0; i < n10; i++) {
            System.out.print("請輸入第 " + (i + 1) + " 個分數：");
            scores10[i] = scanner.nextInt();
        }
        int min = scores10[0];
        for (int i = 1; i < n10; i++) {
            if (scores10[i] < min) min = scores10[i];
        }
        System.out.println("10. 最小數字: " + min);

        scanner.close();
    }


    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    public static void addBonus(int[] arr) {
        for (int i = 0; i < arr.length; i++) arr[i] += 5;
    }

    public static void curve(Student s) {
        if (s.score < 60) s.score += 10;
    }

    public static int sum(int[] arr) {
        int t = 0;
        for (int n : arr) t += n;
        return t;
    }

    public static void updateScore(Student s, int ns) {
        s.score = ns;
    }
}
