import java.util.Scanner;

public class TimeComplexity {
    static class Node {
        int data; Node next;
        Node(int d) { this.data = d; this.next = null; }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Node head = null;
        
        System.out.println("========================================");
        System.out.println("   Linked List O(n) 效能分析系統");
        System.out.println("   (指令: 數字=新增/刪除, 0=結束)");
        System.out.println("========================================");

        while (true) {
            System.out.print("\n請輸入數字: ");
            if (!sc.hasNextInt()) break;
            int val = sc.nextInt();
            if (val == 0) break;

            int steps = 0;
            int totalN = countNodes(head); // 取得目前的資料量 n

            // --- 搜尋邏輯 ---
            if (head != null && head.data == val) {
                steps = 1;
                head = head.next;
                System.out.println(">> 在第 1 個位置找到重複，執行【刪除】");
            } else {
                Node curr = head;
                Node prev = null;
                boolean found = false;

                if (head == null) {
                    steps = 1;
                    head = new Node(val);
                    System.out.println(">> 串列為空，直接【新增】第一個節點");
                } else {
                    System.out.print(">> 開始走訪: ");
                    while (curr != null) {
                        steps++;
                        System.out.print("[" + curr.data + "] -> ");
                        if (curr.data == val) {
                            found = true;
                            break;
                        }
                        prev = curr;
                        curr = curr.next;
                    }

                    if (found) {
                        prev.next = curr.next;
                        System.out.println("\n>> 找到重複！執行【刪除】");
                    } else {
                        prev.next = new Node(val);
                        System.out.println("\n>> 走到底了沒發現重複，執行【新增】");
                    }
                }
            }

            // --- 強調 O(n) 分析 ---
            System.out.println("----------------------------------------");
            System.out.println("【實測數據】");
            System.out.println("資料總數 (n) : " + totalN);
            System.out.println("走訪步數 (Step): " + steps);
            System.out.println("分析結果: 步數隨 n 增加而增加，符合 O(n) 特徵");
            System.out.println("----------------------------------------");

            printList(head);
        }
        sc.close();
    }

    public static int countNodes(Node head) {
        int count = 0;
        Node t = head;
        while (t != null) { count++; t = t.next; }
        return count;
    }

    public static void printList(Node head) {
        System.out.print("目前串列清單: ");
        Node t = head;
        while (t != null) {
            System.out.print(t.data + " -> ");
            t = t.next;
        }
        System.out.println("null");
    }
}
