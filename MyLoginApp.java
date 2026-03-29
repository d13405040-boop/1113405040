import java.util.ArrayList;
import java.util.Scanner;

// 自定義例外類別 (符合講義第 13 頁要求)
class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }
}

// 父類別 (符合講義第 5 頁：繼承概念)
class Person {
    protected String name; 

    public Person(String name) {
        this.name = name;
    }
}

// 使用者類別 (符合講義第 4 頁：封裝概念)
class User extends Person {
    private String username; 
    private String password;

    public User(String name, String username, String password) {
        super(name); 
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }

    public boolean checkPassword(String pw) {
        return this.password.equals(pw);
    }

    // 多型方法 (符合講義第 7 頁)
    public void showRoleInfo() {
        System.out.println("[一般管理員] 姓名: " + name);
    }
}

// 學生使用者 (符合講義第 7 頁：覆寫 Override)
class StudentUser extends User {
    public StudentUser(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    public void showRoleInfo() {
        System.out.println("[學生帳戶] 姓名: " + name + " (具備課程存取權限)");
    }
}

// 主程式類別：檔名請存為 Assignment1.java
public class MyLoginApp {
    private static ArrayList<User> userDatabase = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 初始化資料庫：已改為您的姓名與學號
        userDatabase.add(new User("羅浚嘉", "1113405040", "1234"));
        userDatabase.add(new StudentUser("小明", "student01", "password"));

        System.out.println("==== 澎科大資工系 登入系統 (Assignment 1) ====");
        System.out.println("學生姓名: 羅浚嘉 | 學號: 1113405040");
        System.out.println("--------------------------------------------");

        try {
            System.out.print("請輸入帳號 (學號): ");
            String inputId = sc.nextLine();
            System.out.print("請輸入密碼: ");
            String inputPw = sc.nextLine();

            // 執行登入驗證
            User loggedInUser = login(inputId, inputPw);
            
            System.out.println("\n>>> 登入成功！ <<<");
            loggedInUser.showRoleInfo();

        } catch (LoginFailedException e) {
            // 捕捉自定義例外 (符合講義第 11 頁)
            System.err.println("登入失敗: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("系統發生未知錯誤: " + e.getMessage());
        } finally {
            // 確保資源關閉 (符合講義第 12 頁)
            sc.close();
            System.out.println("\n[系統提示] 程式已安全關閉。");
        }
    }

    // 登入邏輯：拋出例外 (符合講義第 13 頁要求)
    public static User login(String u, String p) throws LoginFailedException {
        for (User user : userDatabase) {
            if (user.getUsername().equals(u)) {
                if (user.checkPassword(p)) {
                    return user;
                } else {
                    throw new LoginFailedException("密碼輸入錯誤，請重新再試。");
                }
            }
        }
        throw new LoginFailedException("找不到此學號，請確認帳號是否正確。");
    }
}
