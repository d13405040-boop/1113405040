#include <iostream>
#include <vector>
#include <stack>
#include <queue>
#include <algorithm>

using namespace std;

// --- 樹節點結構 (作業 2 核心) ---
struct Node {
    int data;
    Node *left, *right;
    Node(int val) : data(val), left(NULL), right(NULL) {}
};

// 1. 動態建樹邏輯 (依據手繪圖：完全二元樹結構)
// 索引 i 的左子節點在 2i+1, 右子節點在 2i+2
Node* buildTree(const vector<int>& nums, int i) {
    if (i >= nums.size()) return NULL;
    Node* root = new Node(nums[i]);
    root->left = buildTree(nums, 2 * i + 1);
    root->right = buildTree(nums, 2 * i + 2);
    return root;
}

// 2. 樹遍歷邏輯 (作業 3 核心)
void preOrder(Node* node) {
    if (!node) return;
    cout << node->data << " ";
    preOrder(node->left);
    preOrder(node->right);
}

void inOrder(Node* node) {
    if (!node) return;
    inOrder(node->left);
    cout << node->data << " ";
    inOrder(node->right);
}

void postOrder(Node* node) {
    if (!node) return;
    postOrder(node->left);
    postOrder(node->right);
    cout << node->data << " ";
}

// ==========================================
// 作業 4: 分治法 (格式完全保留)
// ==========================================
void merge(vector<int>& arr, int l, int m, int r) {
    int n1 = m - l + 1, n2 = r - m;
    vector<int> L(n1), R(n2);
    for (int i = 0; i < n1; i++) L[i] = arr[l + i];
    for (int j = 0; j < n2; j++) R[j] = arr[m + 1 + j];
    int i = 0, j = 0, k = l;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) arr[k++] = L[i++];
        else arr[k++] = R[j++];
    }
    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];
}

void mergeSort(vector<int>& arr, int l, int r) {
    if (l < r) {
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }
}

int binarySearch(const vector<int>& arr, int x) {
    int l = 0, r = arr.size() - 1;
    while (l <= r) {
        int m = l + (r - l) / 2;
        if (arr[m] == x) return m;
        if (arr[m] < x) l = m + 1;
        else r = m - 1;
    }
    return -1;
}

// ==========================================
// 主程式
// ==========================================
int main() {
    cout << "=== 演算法整合程式 (動態樹遍歷版本) ===" << endl;

    // --- 作業 1: Stack & Queue (自行輸入) ---
    vector<int> inputNums;
    int val;
    cout << "\n[作業 1: Stack & Queue]" << endl;
    cout << "請輸入多個數字 (以空格分隔，輸入 0 結束): ";
    while (cin >> val && val != 0) {
        inputNums.push_back(val);
    }

    if (inputNums.empty()) {
        cout << "未輸入數據，程式結束。" << endl;
        return 0;
    }

    // 展示 Stack
    cout << "Stack (LIFO): ";
    stack<int> s;
    for(int i = 0; i < inputNums.size(); i++) s.push(inputNums[i]);
    while(!s.empty()){ cout << s.top() << " "; s.pop(); }
    
    // 展示 Queue
    cout << "\nQueue (FIFO): ";
    queue<int> q;
    for(int i = 0; i < inputNums.size(); i++) q.push(inputNums[i]);
    while(!q.empty()){ cout << q.front() << " "; q.pop(); }
    cout << endl;

    // --- 作業 2 & 3: 根據輸入生成的樹及其遍歷 ---
    // 不論輸入多少數字，都會依照手繪圖結構(完全二元樹)自動生成
    cout << "\n[作業 2 & 3: 動態生成樹與遍歷結果]" << endl;
    Node* root = buildTree(inputNums, 0);

    cout << "前序 (Preorder):  "; preOrder(root); cout << endl;
    cout << "中序 (Inorder):   "; inOrder(root);  cout << endl;
    cout << "後序 (Postorder): "; postOrder(root); cout << endl;

    // --- 作業 4: 分治法 (格式與數據完全保留) ---
    cout << "\n[作業 4: 分治法展示 (格式保留)]" << endl;
    int dataArr[] = {88, 12, 5, 33, 1, 9, 21};
    vector<int> data(dataArr, dataArr + 7);
    
    cout << "排序前: ";
    for(int i = 0; i < data.size(); i++) cout << data[i] << " "; 
    cout << endl;

    mergeSort(data, 0, data.size() - 1);
    cout << "合併排序後 (O(n log n)): ";
    for(int i = 0; i < data.size(); i++) cout << data[i] << " "; 
    cout << endl;

    int target = 33;
    int pos = binarySearch(data, target);
    cout << "二元搜尋數字 " << target << ": 找到在索引 " << pos << endl;

    cout << "\n=== 程式結束 ===" << endl;
    system("pause");
    return 0;
}

