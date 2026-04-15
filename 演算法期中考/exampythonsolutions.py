import math
from collections import deque

def bubble_sort(arr):
    n = len(arr)
    new_arr = arr[:]
    for i in range(n):
        for j in range(0, n - i - 1):
            if new_arr[j] > new_arr[j + 1]:
                new_arr[j], new_arr[j + 1] = new_arr[j + 1], new_arr[j]
    return new_arr

arr = [5,1,4,2,8]
print("1 排序:", bubble_sort(arr))

def factorial(n):
    if n == 0 or n == 1:
        return 1
    return n * factorial(n - 1)

print("2 Factorial(5):", factorial(5))

def min_coins(amount):
    coins = [25, 10, 5, 1]
    used = []
    for coin in coins:
        while amount >= coin:
            amount -= coin
            used.append(coin)
    return used

used_coins = min_coins(63)
print(f"3 最少錢幣數量: {len(used_coins)} coins, {used_coins}")

class Node:
    def __init__(self, val, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def dfs_search(root, target):
    if not root: return False
    if root.val == target: return True
    return dfs_search(root.left, target) or dfs_search(root.right, target)

root = Node(5, Node(3, Node(2), Node(4)), Node(8, Node(7)))
print("4 二元樹:")
print("      5 (Root)")
print("     / \\")
print("    3   8")
print("   / \\ /")
print("  2  4 7")
print("4 尋找7:", "Found" if dfs_search(root, 7) else "Not Found")


print("\n--- 5 堆疊 (LIFO) ---")
stack = []
stack.append(10); print(f"Push 10: {stack}")
stack.append(20); print(f"Push 20: {stack}")
popped_s = stack.pop(); print(f"Pop {popped_s}:  {stack}")
stack.append(30); print(f"Push 30: {stack}")
print("堆疊結果:", stack)

print("\n--- 5 佇列 (FIFO) ---")
queue = deque()
queue.append(10); print(f"Enqueue 10: {list(queue)}")
queue.append(20); print(f"Enqueue 20: {list(queue)}")
popped_q = queue.popleft(); print(f"Dequeue {popped_q}: {list(queue)}")
queue.append(30); print(f"Enqueue 30: {list(queue)}")
print("佇列結果:", list(queue), "\n")

class ListNode:
    def __init__(self, val):
        self.val = val
        self.next = None

head = ListNode(1); head.next = ListNode(2); head.next.next = ListNode(3); head.next.next.next = ListNode(4)
print("6 鏈結串列:", end=" ")
curr = head
while curr:
    print(curr.val, end=" ")
    curr = curr.next
print()

points = {"A": (1, 1), "B": (4, 4), "C": (6, 1)}
P = (3, 2)

def l1(p1, p2):
    return abs(p1[0] - p2[0]) + abs(p1[1] - p2[1])

def l2(p1, p2):
    return math.sqrt((p1[0] - p2[0])**2 + (p1[1] - p2[1])**2)

l1_results = {name: l1(P, pt) for name, pt in points.items()}
l2_results = {name: l2(P, pt) for name, pt in points.items()}

min_l1_name = min(l1_results, key=l1_results.get)
min_l2_name = min(l2_results, key=l2_results.get)

print("7 L1 distances:", l1_results)
print(f"Nearest neighbor under L1: {min_l1_name}")

print("7 L2 distances:", {k: round(v, 3) for k, v in l2_results.items()})
print(f"Nearest neighbor under L2: {min_l2_name}")
