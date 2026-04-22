import java.util.*;

// --- 作業 2: Huffman 節點類別 ---
class HuffmanNode implements Comparable<HuffmanNode> {
    char data;
    int frequency;
    HuffmanNode left, right;
    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }
    @Override
    public int compareTo(HuffmanNode o) { return this.frequency - o.frequency; }
}

// --- 作業 1: Job 類別 ---
class Job {
    char id;
    int start, finish, value;
    public Job(char id, int start, int finish, int value) {
        this.id = id; this.start = start; this.finish = finish; this.value = value;
    }
}

public class AlgorithmAssignment {

    public static void solveWeightedInterval() {
        System.out.println("### Assignment 1: Weighted Interval Scheduling ###");
        // 嚴格對照截圖第 31-34 行
        Job[] jobs = {
            new Job('A', 1, 4, 5), new Job('B', 3, 5, 1),
            new Job('C', 0, 6, 8), new Job('D', 4, 7, 4),
            new Job('E', 3, 8, 6), new Job('F', 5, 9, 3),
            new Job('G', 6, 10, 2), new Job('H', 8, 11, 4) 
        };

        // 1. 依結束時間排序
        Arrays.sort(jobs, Comparator.comparingInt(j -> j.finish));

        int n = jobs.length;
        int[] dp = new int[n + 1];
        int[] p = new int[n + 1];

        // 2. 計算 p(j)
        for (int j = 1; j <= n; j++) {
            for (int i = j - 1; i >= 1; i--) {
                if (jobs[i - 1].finish <= jobs[j - 1].start) {
                    p[j] = i;
                    break;
                }
            }
        }

        // 3. DP 填表
        for (int j = 1; j <= n; j++) {
            dp[j] = Math.max(jobs[j - 1].value + dp[p[j]], dp[j - 1]);
        }

        System.out.println("Maximum total value: " + dp[n]);
        System.out.println("Time Complexity: O(n log n)");
    }

    public static void solveHuffmanCoding() {
        System.out.println("\n### Assignment 2: Huffman Coding ###");
        // 作業要求的字元與頻率
        char[] chars = {'A', 'B', 'C', 'D', 'E', 'F'};
        int[] freq = {5, 9, 12, 13, 16, 45};

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (int i = 0; i < chars.length; i++) {
            pq.add(new HuffmanNode(chars[i], freq[freq.length - 1 - i])); // 逆序匹配頻率
        }

        // 修正邏輯：按照頻率排序加入 pq
        pq.clear();
        for (int i = 0; i < chars.length; i++) pq.add(new HuffmanNode(chars[i], freq[i]));

        while (pq.size() > 1) {
            HuffmanNode x = pq.poll(), y = pq.poll();
            HuffmanNode sum = new HuffmanNode('-', x.frequency + y.frequency);
            sum.left = x; sum.right = y;
            pq.add(sum);
        }
        System.out.println("Huffman Codes:");
        printCodes(pq.peek(), "");
    }

    private static void printCodes(HuffmanNode node, String code) {
        if (node.left == null && node.right == null) {
            System.out.println(node.data + ": " + code);
            return;
        }
        printCodes(node.left, code + "0");
        printCodes(node.right, code + "1");
    }

    public static void main(String[] args) {
        solveWeightedInterval();
        solveHuffmanCoding();
    }
}
