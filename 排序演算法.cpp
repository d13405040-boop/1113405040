#include <iostream>
#include <vector>
#include <ctime>     // 傳統計時方式，Dev-C++ 預設支援
#include <cstdlib>   // 用於產生隨機數
#include <iomanip>   // 用於表格排版

using namespace std;

// ==========================================
// 作業 1 & 2：基礎演算法實作
// ==========================================

// 1. 線性掃描 (Linear Scan / Array Sum) - 複雜度 O(n)
long long linearScanSum(const vector<int>& arr) {
    long long sum = 0;
    // 使用傳統迴圈，避免 Dev-C++ 報錯
    for (size_t i = 0; i < arr.size(); i++) {
        sum += arr[i];
    }
    return sum;
}

// 2. 泡沫排序 (Bubble Sort) - 複雜度 O(n^2)
void bubbleSort(vector<int>& a) {
    int n = a.size();
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (a[j] > a[j + 1]) {
                int temp = a[j];
                a[j] = a[j + 1];
                a[j + 1] = temp;
            }
        }
    }
}

// 3. 插入排序 (Insertion Sort) - 複雜度 O(n^2)
void insertionSort(vector<int>& a, int low, int high) {
    for (int i = low + 1; i <= high; i++) {
        int key = a[i];
        int j = i - 1;
        while (j >= low && a[j] > key) {
            a[j + 1] = a[j];
            j--;
        }
        a[j + 1] = key;
    }
}

// 4. 快速排序 (Quick Sort) - 複雜度 O(n log n)
int partition(vector<int>& a, int low, int high) {
    int pivot = a[high];
    int i = (low - 1);
    for (int j = low; j < high; j++) {
        if (a[j] < pivot) {
            i++;
            int temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
    }
    int temp = a[i + 1];
    a[i + 1] = a[high];
    a[high] = temp;
    return (i + 1);
}

void quickSort(vector<int>& a, int low, int high) {
    if (low < high) {
        int pi = partition(a, low, high);
        quickSort(a, low, pi - 1);
        quickSort(a, pi + 1, high);
    }
}

// ==========================================
// 作業 3：自創/改進演算法 (Hybrid Sort)
// 理念：結合 Quick Sort 的速度與 Insertion Sort 在小數據量的低開銷
// ==========================================
void hybridSort(vector<int>& a, int low, int high) {
    while (low < high) {
        // 當子陣列長度小於 10 時，切換成插入排序以提高常數項效率
        if (high - low < 10) {
            insertionSort(a, low, high);
            break;
        } else {
            int pi = partition(a, low, high);
            // 尾端遞迴優化
            if (pi - low < high - pi) {
                hybridSort(a, low, pi - 1);
                low = pi + 1;
            } else {
                hybridSort(a, pi + 1, high);
                high = pi - 1;
            }
        }
    }
}

// ==========================================
// 實驗觀測與輸出
// ==========================================
void runExperiment(int n) {
    vector<int> originalData(n);
    for (int i = 0; i < n; i++) originalData[i] = rand() % 10000;

    cout << "\n>>> 測試數據量 (n): " << n << endl;
    cout << "------------------------------------------" << endl;
    cout << left << setw(20) << "Algorithm" << "Time (seconds)" << endl;
    cout << "------------------------------------------" << endl;

    // 測試 Bubble Sort (數據太大會跑很久)
    if (n <= 10000) {
        vector<int> data = originalData;
        clock_t start = clock();
        bubbleSort(data);
        cout << left << setw(20) << "Bubble Sort" << (double)(clock() - start) / CLOCKS_PER_SEC << " s" << endl;
    }

    // 測試 Insertion Sort
    if (n <= 10000) {
        vector<int> data = originalData;
        clock_t start = clock();
        insertionSort(data, 0, n - 1);
        cout << left << setw(20) << "Insertion Sort" << (double)(clock() - start) / CLOCKS_PER_SEC << " s" << endl;
    }

    // 測試 Quick Sort
    vector<int> qData = originalData;
    clock_t startQ = clock();
    quickSort(qData, 0, n - 1);
    cout << left << setw(20) << "Quick Sort" << (double)(clock() - startQ) / CLOCKS_PER_SEC << " s" << endl;

    // 測試作業 3 的 Hybrid Sort
    vector<int> hData = originalData;
    clock_t startH = clock();
    hybridSort(hData, 0, n - 1);
    cout << left << setw(20) << "Hybrid Sort (HW3)" << (double)(clock() - startH) / CLOCKS_PER_SEC << " s" << endl;
    
    cout << "------------------------------------------" << endl;
}

int main() {
    srand(time(NULL)); // 隨機數種子

    // 分別測試小數據與大數據量，觀察時間複雜度的變化
    int testSizes[] = {100, 1000, 5000, 10000}; 
    
    for (int i = 0; i < 4; i++) {
        runExperiment(testSizes[i]);
    }

    cout << "\n測試完成，請將上述數據填入 IEEE 格式報告中。" << endl;
    system("pause"); // 讓 Dev-C++ 視窗不要跑完立刻關閉
    return 0;
}

