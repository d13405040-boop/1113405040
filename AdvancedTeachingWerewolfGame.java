import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Player {
    private int id;
    private String role;
    private String camp; 
    private boolean alive;

    public Player(int id, String role, String camp) {
        this.id = id;
        this.role = role;
        this.camp = camp;
        this.alive = true;
    }

    public int getId() { return id; }
    public String getRole() { return role; }
    public String getCamp() { return camp; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}

public class AdvancedTeachingWerewolfGame {
    static Scanner scanner = new Scanner(System.in);
    static List<Player> players = new ArrayList<>();
    static boolean hasHeal = true;
    static boolean hasPoison = true;

    public static void main(String[] args) {
        System.out.println("==== 進階教學版狼人殺遊戲 (Assignment 2) ====");
        
        System.out.print("請輸入狼人(Bad)數量: ");
        int w = scanner.nextInt();
        System.out.print("請輸入預言家(Good)數量: ");
        int s = scanner.nextInt();
        System.out.print("請輸入女巫(Good)數量: ");
        int wi = scanner.nextInt();
        System.out.print("請輸入獵人(Good)數量: ");
        int h = scanner.nextInt();
        System.out.print("請輸入村民(Good)數量: ");
        int v = scanner.nextInt();

        setupGame(w, s, wi, h, v);
        gameLoop();
    }

    static void setupGame(int w, int s, int wi, int h, int v) {
        List<String[]> rolesPool = new ArrayList<>();
        for (int i = 0; i < w; i++) rolesPool.add(new String[]{"Werewolf", "Bad"});
        for (int i = 0; i < s; i++) rolesPool.add(new String[]{"Seer", "Good"});
        for (int i = 0; i < wi; i++) rolesPool.add(new String[]{"Witch", "Good"});
        for (int i = 0; i < h; i++) rolesPool.add(new String[]{"Hunter", "Good"});
        for (int i = 0; i < v; i++) rolesPool.add(new String[]{"Villager", "Good"});
        
        Collections.shuffle(rolesPool);
        for (int i = 0; i < rolesPool.size(); i++) {
            players.add(new Player(i + 1, rolesPool.get(i)[0], rolesPool.get(i)[1]));
        }
        
        System.out.println("\n角色分配完成！總人數：" + players.size());
        
        // --- 新增：狼人互認環節 ---
        System.out.println("\n【系統提示】請狼人玩家確認隊友：");
        for (Player p : players) {
            if (p.getRole().equals("Werewolf")) {
                System.out.print("狼人玩家 " + p.getId() + " 號，你的隊友是：");
                for (Player teammate : players) {
                    if (teammate.getRole().equals("Werewolf") && teammate.getId() != p.getId()) {
                        System.out.print("[" + teammate.getId() + "號] ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("確認完畢後，請按任意鍵並 Enter 開始遊戲...");
        scanner.next(); 
    }

    static void gameLoop() {
        int day = 1;
        while (true) {
            System.out.println("\n--- 第 " + day + " 天夜晚 ---");
            nightPhase();
            if (checkWin()) break;

            System.out.println("\n--- 第 " + day + " 天白天 ---");
            dayPhase();
            if (checkWin()) break;
            day++;
        }
    }

    static void nightPhase() {
        // 1. 狼人行動 (提示隊友)
        System.out.println("\n[狼人行動]");
        System.out.print("狼人陣營存活成員為：");
        for (Player p : players) {
            if (p.getRole().equals("Werewolf") && p.isAlive()) System.out.print("[" + p.getId() + "號] ");
        }
        System.out.println("\n請商議後選擇擊殺目標 ID (輸入 0 代表空刀):");
        showAlive();
        int wolfTargetId = scanner.nextInt();
        Player victim = getPlayerById(wolfTargetId);
        
        // 2. 女巫行動
        Player witch = getAliveRolePlayer("Witch");
        if (witch != null) {
            System.out.println("\n[女巫行動] 你是女巫(" + witch.getId() + "號)，藥水狀態：解藥(" + (hasHeal?"有":"無") + ") 毒藥(" + (hasPoison?"有":"無") + ")");
            if (hasHeal && wolfTargetId != 0) {
                System.out.println("今晚玩家 " + wolfTargetId + " 被殺了，要使用解藥嗎？(1:是 / 0:否)");
                if (scanner.nextInt() == 1) {
                    victim = null;
                    hasHeal = false;
                }
            }
            if (hasPoison) {
                System.out.println("要使用毒藥嗎？(1:是 / 0:否)");
                if (scanner.nextInt() == 1) {
                    System.out.println("請選擇毒殺目標 ID:");
                    showAlive();
                    int pId = scanner.nextInt();
                    Player pVictim = getPlayerById(pId);
                    if (pVictim != null && pVictim.isAlive()) {
                        pVictim.setAlive(false);
                        System.out.println("你毒殺了玩家 " + pId);
                        checkHunter(pVictim);
                    }
                    hasPoison = false;
                }
            }
        }

        if (victim != null && victim.isAlive()) {
            victim.setAlive(false);
            System.out.println("\n夜晚結束... 玩家 " + wolfTargetId + " 慘遭殺害。");
            checkHunter(victim);
        } else {
            System.out.println("\n夜晚結束... 昨晚是個平安夜。");
        }

        // 3. 預言家行動
        Player seer = getAliveRolePlayer("Seer");
        if (seer != null) {
            System.out.println("\n[預言家行動] 你是預言家(" + seer.getId() + "號)，請選擇查驗目標 ID:");
            showAlive();
            int checkId = scanner.nextInt();
            Player targetP = getPlayerById(checkId);
            if (targetP != null) {
                System.out.println("查驗結果：玩家 " + checkId + " 屬於 " + targetP.getCamp() + " 陣營。");
            }
        }
    }

    static void dayPhase() {
        System.out.println("\n[白天投票] 請輸入投票 ID (輸入 0 代表棄票):");
        showAlive();
        
        int[] votes = new int[players.size() + 1];
        int voteCount = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                System.out.print("玩家 " + p.getId() + " 投票: ");
                int v = scanner.nextInt();
                if (v >= 0 && v <= players.size()) {
                    votes[v]++;
                    voteCount++;
                }
            }
        }

        int max = 0, targetId = -1;
        boolean tie = false;
        for (int i = 1; i < votes.length; i++) {
            if (votes[i] > max) { max = votes[i]; targetId = i; tie = false; }
            else if (votes[i] == max && max > 0) tie = true;
        }

        if (tie || targetId == -1 || votes[targetId] <= votes[0]) {
            System.out.println("投票結果：平票或棄票(" + votes[0] + "票)佔多數，無人出局。");
        } else {
            Player voted = getPlayerById(targetId);
            if (voted != null) {
                voted.setAlive(false);
                System.out.println("結果：玩家 " + targetId + " 被投票出局，身分為：" + voted.getRole());
                checkHunter(voted);
            }
        }
    }

    static void checkHunter(Player p) {
        if (p != null && p.getRole().equals("Hunter")) {
            System.out.println("【獵人技能觸發】玩家 " + p.getId() + " 出局！請選擇帶走一名存活玩家 (ID) 或輸入 0 放棄:");
            showAlive();
            int shootId = scanner.nextInt();
            Player target = getPlayerById(shootId);
            if (target != null && target.isAlive()) {
                target.setAlive(false);
                System.out.println("獵人開槍帶走了玩家 " + shootId);
            }
        }
    }

    static boolean checkWin() {
        int bad = 0, good = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                if (p.getCamp().equals("Bad")) bad++;
                else good++;
            }
        }
        if (bad == 0) { System.out.println("\n🎉 好人陣營 (Good Camp) 獲勝！"); return true; }
        if (bad >= good) { System.out.println("\n🐺 狼人陣營 (Bad Camp) 獲勝！"); return true; }
        return false;
    }

    static void showAlive() {
        System.out.print(">> 當前存活玩家: ");
        for (Player p : players) {
            if (p.isAlive()) System.out.print("[" + p.getId() + "號] ");
        }
        System.out.println();
    }

    static Player getPlayerById(int id) {
        for (Player p : players) if (p.getId() == id) return p;
        return null;
    }

    static Player getAliveRolePlayer(String role) {
        for (Player p : players) if (p.getRole().equals(role) && p.isAlive()) return p;
        return null;
    }
}
