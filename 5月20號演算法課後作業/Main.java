import java.util.*;

// ==========================================
// 1. 定義圖形結構與物品類別
// ==========================================

// 定義圖形的邊
class Edge {
    int source, dest, weight;
    public Edge(int source, int dest, int weight) {
        this.source = source;
        this.dest = dest;
        this.weight = weight;
    }
}

// 用於 Dijkstra 優先佇列（PriorityQueue）的節點
class Node implements Comparable<Node> {
    int id, distance;
    public Node(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }
    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
}

// 物品類別（用於第四個結果：背包問題）
class Item {
    String name;
    int weight, value;
    public Item(String name, int weight, int value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }
}

// ==========================================
// 2. 主程式核心（包含 4 個全部結果與複雜度分析）
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("        課堂演算法最佳化系統（四大完整結果與時間複雜度分析）          ");
        System.out.println("==================================================================\n");

        // --------------------------------------------------------
        // 👉【結果一 & 二】Dijkstra 演算法（對應投影片 P4~5 的 6點正權重範例）
        // --------------------------------------------------------
        int dijNodes = 7; // 使用 1~6 號節點，索引 0 不使用
        List<List<Edge>> dijGraph = new ArrayList<>();
        for (int i = 0; i < dijNodes; i++) dijGraph.add(new ArrayList<>());
        
        // 精準建立投影片 P4 的正權重雙向邊
        dijGraph.get(1).add(new Edge(1, 2, 4));  dijGraph.get(1).add(new Edge(1, 3, 9));  dijGraph.get(1).add(new Edge(1, 5, 5));
        dijGraph.get(2).add(new Edge(2, 1, 4));  dijGraph.get(2).add(new Edge(2, 3, 2));  dijGraph.get(2).add(new Edge(2, 4, 7)); dijGraph.get(2).add(new Edge(2, 5, 4));
        dijGraph.get(3).add(new Edge(3, 1, 9));  dijGraph.get(3).add(new Edge(3, 2, 2));  dijGraph.get(3).add(new Edge(3, 4, 10));
        dijGraph.get(4).add(new Edge(4, 2, 7));  dijGraph.get(4).add(new Edge(4, 3, 10)); dijGraph.get(4).add(new Edge(4, 5, 4));  dijGraph.get(4).add(new Edge(4, 6, 8));
        dijGraph.get(5).add(new Edge(5, 1, 5));  dijGraph.get(5).add(new Edge(5, 2, 4));  dijGraph.get(5).add(new Edge(5, 4, 4));  dijGraph.get(5).add(new Edge(5, 6, 6));
        dijGraph.get(6).add(new Edge(6, 4, 8));  dijGraph.get(6).add(new Edge(6, 5, 6));

        int[] dijDist = new int[dijNodes];
        Arrays.fill(dijDist, Integer.MAX_VALUE);
        dijDist[1] = 0; // 起點為 1
        boolean[] done = new boolean[dijNodes];
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(1, 0));

        System.out.println("======【結果一】Dijkstra 演算法：節點鎖定推進過程 ======");
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;
            if (done[u]) continue;
            done[u] = true;
            System.out.printf("   [鎖定] 確定集合加入節點 [%d] -> 目前最短距離為: %d\n", u, dijDist[u]);

            for (Edge edge : dijGraph.get(u)) {
                int v = edge.dest;
                if (!done[v] && dijDist[u] + edge.weight < dijDist[v]) {
                    dijDist[v] = dijDist[u] + edge.weight;
                    pq.add(new Node(v, dijDist[v]));
                }
            }
        }

        System.out.println("\n======【結果二】Dijkstra 演算法：最終最短路徑陣列（標籤值） ======");
        for (int i = 1; i <= 6; i++) {
            System.out.printf("   起點 1 到節點 %d 的最短路徑長度 = %d\n", i, dijDist[i]);
        }
        
        // 💡 Dijkstra 複雜度分析
        System.out.println("\n[⏱️ 複雜度分析 - Dijkstra]");
        System.out.println("   • 時間複雜度: O((V + E) log V) -> 本範例頂點 V=6, 邊 E=16。");
        System.out.println("     - 原因: 透過 PriorityQueue 每次取出當前最短距離點需 O(log V)，並對每條邊進行鬆弛嘗試。");
        System.out.println("   • 空間複雜度: O(V + E) -> 用於儲存鄰接清單與距離陣列。");
        System.out.println("\n" + "=".repeat(66) + "\n");


        // --------------------------------------------------------
        // 👉【結果三】Bellman-Ford 演算法（精準對應第一張截圖 P8~9 的負值範例）
        // --------------------------------------------------------
        int bfNodes = 5; // 1~4 號節點，0不用
        List<Edge> bfEdges = new ArrayList<>();
        // 精準填入截圖一中的結構與負權重邊（負值在此演算法中必須保留）
        bfEdges.add(new Edge(1, 2, 6));
        bfEdges.add(new Edge(1, 3, 5));
        bfEdges.add(new Edge(1, 4, 5));
        bfEdges.add(new Edge(2, 3, -2)); // 負值
        bfEdges.add(new Edge(3, 2, -2)); // 負值
        bfEdges.add(new Edge(4, 3, -2)); // 負值

        int[] bfDist = new int[bfNodes];
        Arrays.fill(bfDist, 99999); // 用 99999 代表無限大 (∞)
        bfDist[1] = 0; // 起點為 1

        System.out.println("======【結果三】Bellman-Ford 演算法：逐輪全局無差別鬆弛（對應截圖一） ======");
        // 進行最多 V-1 輪全局掃描（4個點就是跑 3 輪）
        for (int i = 1; i <= 3; i++) {
            System.out.printf("   [第 %d 輪 邊的無差別疊加]\n", i);
            boolean anyUpdate = false;
            for (Edge edge : bfEdges) {
                if (bfDist[edge.source] != 99999 && bfDist[edge.source] + edge.weight < bfDist[edge.dest]) {
                    int oldVal = bfDist[edge.dest];
                    bfDist[edge.dest] = bfDist[edge.source] + edge.weight;
                    anyUpdate = true;
                    System.out.printf("      發現更短路徑！ 節點 %d -> %d：原距離 %s 改為 %d\n", 
                        edge.source, edge.dest, (oldVal == 99999 ? "∞" : oldVal), bfDist[edge.dest]);
                }
            }
            if (!anyUpdate) {
                System.out.println("      (本輪無任何更新，觸發智慧提早結束機制)");
                break;
            }
        }
        System.out.println("\n   --- Bellman-Ford 最終收斂陣列 ---");
        for (int i = 1; i <= 4; i++) {
            System.out.printf("   起點 1 到節點 %d 的最短距離 = %d\n", i, bfDist[i]);
        }
        
        // 💡 Bellman-Ford 複雜度分析
        System.out.println("\n[⏱️ 複雜度分析 - Bellman-Ford]");
        System.out.println("   • 時間複雜度: O(V × E) -> 本範例頂點 V=4, 邊 E=6。");
        System.out.println("     - 原因: 外層迴圈固定掃描 V-1 輪，內層迴圈每輪皆無差別掃描全部的 E 條邊。正因為這種全局視野，才能正確處理【負值】路徑。");
        System.out.println("   • 空間複雜度: O(V + E) -> 用於儲存邊的集合與距離陣列。");
        System.out.println("\n" + "=".repeat(66) + "\n");


        // --------------------------------------------------------
        // 👉【結果四】0/1 背包問題 動態規劃（精準對應第二張截圖 P10 的 DP 表格）
        // --------------------------------------------------------
        System.out.println("======【結果四】0/1 背包問題：動態規劃決策矩陣（對應截圖二） ======");
        
        // 建立截圖表格中的三個物品資料
        Item[] items = {
            new Item("物品1", 2, 3),
            new Item("物品2", 3, 4),
            new Item("物品3", 1, 2)
        };
        int maxCapacity = 4; // 限重為 4 (橫軸 0, 1, 2, 3, 4)
        int n = items.length;
        int[][] dp = new int[n + 1][maxCapacity + 1];

        // 填表核心決策邏輯
        for (int i = 1; i <= n; i++) {
            int w = items[i - 1].weight;
            int v = items[i - 1].value;
            for (int cap = 0; cap <= maxCapacity; cap++) {
                if (w <= cap) {
                    dp[i][cap] = Math.max(dp[i - 1][cap], dp[i - 1][cap - w] + v);
                } else {
                    dp[i][cap] = dp[i - 1][cap];
                }
            }
        }

        // 印出如同第二張截圖一模一樣的 DP 狀態矩陣表格
        System.out.println("   [DP 二維矩陣填表狀態輸出]");
        System.out.println("   物品 \\ 背包限重  0    1    2    3    4");
        System.out.println("   ---------------------------------------");
        System.out.printf("   初始狀態 (0)  ");
        for (int cap = 0; cap <= maxCapacity; cap++) System.out.printf("%-5d", dp[0][cap]);
        System.out.println();
        
        for (int i = 1; i <= n; i++) {
            System.out.printf("   %s (重%d,值%d) ", items[i-1].name, items[i-1].weight, items[i-1].value);
            for (int cap = 0; cap <= maxCapacity; cap++) {
                System.out.printf("%-5d", dp[i][cap]);
            }
            System.out.println();
        }
        System.out.println("\n   👑 最佳化決策產出：背包能裝載的最大價值為 = " + dp[n][maxCapacity]);
        
        // 💡 0/1 背包問題複雜度分析
        System.out.println("\n[⏱️ 複雜度分析 - 0/1 背包問題動態規劃]");
        System.out.println("   • 時間複雜度: O(N × W) -> 本範例物品數 N=3, 背包限重 W=4。");
        System.out.println("     - 原因: 透過雙層嵌套迴圈，精準填滿一個大小為 (N+1) × (W+1) 的二維決策矩陣，每格計算時間為 O(1)。");
        System.out.println("   • 空間複雜度: O(N × W) -> 用於配置儲存子問題最佳解的二維陣列表格空間。");
        System.out.println("==================================================================");
    }
}