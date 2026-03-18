#include <iostream>
#include <vector>

using namespace std;

// --- 堆疊 (Stack) 類別 ---
class InteractiveStack {
private:
    vector<int> data;
public:
    void push(int val) {
        data.push_back(val);
        cout << "\n[操作: Push " << val << "]" << endl;
        cout << " -> 運作過程: 將數值放入頂端 (Index: " << data.size()-1 << ")" << endl;
        cout << " -> 時間複雜度: O(1) (僅操作頂端指標)" << endl;
        display();
    }

    void pop() {
        if (data.empty()) {
            cout << "\n[錯誤] 堆疊已空，無法 Pop！" << endl;
            return;
        }
        int val = data.back();
        data.pop_back();
        cout << "\n[操作: Pop " << val << "]" << endl;
        cout << " -> 運作過程: 直接移除頂端元素，其餘資料不動" << endl;
        cout << " -> 時間複雜度: O(1) (常數時間)" << endl;
        display();
    }

    void display() {
        cout << " 目前 Stack (底->頂): [ ";
        for (int i = 0; i < (int)data.size(); ++i) cout << data[i] << " ";
        cout << "]" << endl;
    }
};

// --- 佇列 (Queue) 類別 ---
class InteractiveQueue {
private:
    vector<int> data;
    int frontIdx;
public:
    InteractiveQueue() : frontIdx(0) {}

    void enqueue(int val) {
        data.push_back(val);
        cout << "\n[操作: Enqueue " << val << "]" << endl;
        cout << " -> 運作過程: 在尾端 (Index: " << data.size()-1 << ") 新增資料" << endl;
        cout << " -> 時間複雜度: O(1)" << endl;
        display();
    }

    void dequeue() {
        if (frontIdx >= (int)data.size()) {
            cout << "\n[錯誤] 佇列已空，無法 Dequeue！" << endl;
            return;
        }
        int val = data[frontIdx];
        frontIdx++; // 移動指標即完成刪除
        cout << "\n[操作: Dequeue " << val << "]" << endl;
        cout << " -> 運作過程: 僅移動 Front 指標 (目前指向: " << frontIdx << ")，不挪動後面資料" << endl;
        cout << " -> 時間複雜度: O(1) (這就是為什麼 Queue 很快)" << endl;
        display();
    }

    void display() {
        cout << " 目前 Queue (頭->尾): [ ";
        for (int i = frontIdx; i < (int)data.size(); ++i) cout << data[i] << " ";
        cout << "]" << endl;
    }
};

int main() {
    int choice, subChoice, val;
    InteractiveStack s;
    InteractiveQueue q;

    while (true) {
        cout << "\n======= 資料結構 $O(1)$ 演示工具 =======" << endl;
        cout << "1. 測試 Stack (堆疊)" << endl;
        cout << "2. 測試 Queue (佇列)" << endl;
        cout << "3. 退出程式" << endl;
        cout << "請選擇: ";
        cin >> choice;

        if (choice == 3) break;

        if (choice == 1) { // Stack 模式
            while (true) {
                cout << "\n[Stack 模式] 1.Push 2.Pop 3.返回主選單: ";
                cin >> subChoice;
                if (subChoice == 3) break;
                if (subChoice == 1) {
                    cout << "請輸入要放入的整數: ";
                    cin >> val;
                    s.push(val);
                } else if (subChoice == 2) {
                    s.pop();
                }
            }
        } else if (choice == 2) { // Queue 模式
            while (true) {
                cout << "\n[Queue 模式] 1.Enqueue 2.Dequeue 3.返回主選單: ";
                cin >> subChoice;
                if (subChoice == 3) break;
                if (subChoice == 1) {
                    cout << "請輸入要放入的整數: ";
                    cin >> val;
                    q.enqueue(val);
                } else if (subChoice == 2) {
                    q.dequeue();
                }
            }
        }
    }

    cout << "演示結束！" << endl;
    return 0;
}

