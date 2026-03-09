/**
 * RECURSION AND DYNAMIC PROGRAMMING
 *
 * RECURSION:
 * - Function calls itself with smaller input
 * - Must have base case to stop recursion
 * - Can be slow due to repeated calculations
 *
 * DYNAMIC PROGRAMMING:
 * - Optimization technique using memoization or tabulation
 * - Stores results of subproblems to avoid recalculation
 * - Applicable when problem has overlapping subproblems
 *
 * Steps to identify DP:
 * 1. Can problem be broken into overlapping subproblems?
 * 2. Does optimal solution contain optimal solutions of subproblems?
 * 3. Can we store results to avoid recomputation?
 */

import java.util.*;

public class RecursionAndDP {

    // ============ FIBONACCI ============

    /**
     * FIBONACCI - Pure Recursion (SLOW!)
     * fib(n) = fib(n-1) + fib(n-2)
     *
     * DRY RUN: Calculate fib(5)
     *
     *                        fib(5)
     *                       /      \
     *                   fib(4)      fib(3)
     *                  /     \       /    \
     *             fib(3)   fib(2)  fib(2) fib(1)
     *            /   \     /  \    /  \
     *        fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
     *       /  \
     *   fib(1) fib(0)
     *
     * Notice fib(3) is calculated twice! This is inefficiency.
     *
     * Calculation order:
     * fib(0)=0, fib(1)=1
     * fib(2)=1+0=1
     * fib(3)=1+1=2
     * fib(4)=2+1=3
     * fib(5)=3+2=5
     *
     * Time: O(2^n) - EXPONENTIAL!
     * Space: O(n) recursion stack
     */
    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * FIBONACCI - Memoization (TOP-DOWN DP)
     * Store calculated values to avoid recalculation
     *
     * DRY RUN: Calculate fib(5) with memo
     *
     * fib(5): Check memo[5]? No
     *   fib(4): Check memo[4]? No
     *     fib(3): Check memo[3]? No
     *       fib(2): Check memo[2]? No
     *         fib(1): Check memo[1]? No, return 1, memo[1]=1
     *         fib(0): Check memo[0]? No, return 0, memo[0]=0
     *       memo[2] = 1+0 = 1
     *       fib(1): Check memo[1]? YES! return 1
     *     memo[3] = 1+1 = 2
     *     fib(2): Check memo[2]? YES! return 1
     *   memo[4] = 2+1 = 3
     *   fib(3): Check memo[3]? YES! return 2
     * memo[5] = 3+2 = 5
     *
     * No repeated calculations!
     *
     * Time: O(n) - Each subproblem solved once
     * Space: O(n) - Memo array + recursion stack
     */
    public static int fibonacciMemo(int n, Map<Integer, Integer> memo) {
        if (n <= 1) {
            return n;
        }

        if (memo.containsKey(n)) {
            return memo.get(n);
        }

        int result = fibonacciMemo(n - 1, memo) + fibonacciMemo(n - 2, memo);
        memo.put(n, result);
        return result;
    }

    /**
     * FIBONACCI - Tabulation (BOTTOM-UP DP)
     * Build solution iteratively from base cases
     *
     * DRY RUN: Calculate fib(5) with tabulation
     *
     * Initialize: dp[0]=0, dp[1]=1
     *
     * i=2: dp[2] = dp[1] + dp[0] = 1+0 = 1
     * i=3: dp[3] = dp[2] + dp[1] = 1+1 = 2
     * i=4: dp[4] = dp[3] + dp[2] = 2+1 = 3
     * i=5: dp[5] = dp[4] + dp[3] = 3+2 = 5
     *
     * Result: dp[5] = 5
     *
     * Time: O(n)
     * Space: O(n) for dp array
     * Better: Can optimize space to O(1) using two variables
     */
    public static int fibonacciTab(int n) {
        if (n <= 1) {
            return n;
        }

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    // ============ COIN CHANGE ============

    /**
     * COIN CHANGE PROBLEM
     * Given coins and target amount, find minimum coins needed
     *
     * Example: coins = [1, 2, 5], amount = 5
     * Possible: 5+0=1 coin, 2+2+1=3 coins
     * Minimum: 1 coin (just 5)
     *
     * DRY RUN with DP:
     *
     * dp[0]=0 (0 amount needs 0 coins)
     * dp[1]=inf, dp[2]=inf, ..., dp[5]=inf
     *
     * For amount=1:
     *   Try coin 1: dp[1] = min(inf, dp[1-1]+1) = min(inf, 0+1) = 1
     *
     * For amount=2:
     *   Try coin 1: dp[2] = min(inf, dp[2-1]+1) = min(inf, 1+1) = 2
     *   Try coin 2: dp[2] = min(2, dp[2-2]+1) = min(2, 0+1) = 1
     *
     * For amount=3:
     *   Try coin 1: dp[3] = min(inf, dp[3-1]+1) = min(inf, 1+1) = 2
     *   Try coin 2: dp[3] = min(2, dp[3-2]+1) = min(2, 1+1) = 2
     *
     * For amount=4:
     *   Try coin 1: dp[4] = min(inf, dp[4-1]+1) = min(inf, 2+1) = 3
     *   Try coin 2: dp[4] = min(3, dp[4-2]+1) = min(3, 1+1) = 2
     *
     * For amount=5:
     *   Try coin 1: dp[5] = min(inf, dp[5-1]+1) = min(inf, 2+1) = 3
     *   Try coin 2: dp[5] = min(3, dp[5-2]+1) = min(3, 2+1) = 3
     *   Try coin 5: dp[5] = min(3, dp[5-5]+1) = min(3, 0+1) = 1
     *
     * Result: dp[5] = 1 (need 1 coin of value 5)
     *
     * Time: O(amount * coins), Space: O(amount)
     */
    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);  // Initialize with impossible value
        dp[0] = 0;  // 0 coins needed for amount 0

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    // ============ LONGEST COMMON SUBSEQUENCE ============

    /**
     * LCS (Longest Common Subsequence)
     * Find longest sequence common to both strings (not necessarily contiguous)
     *
     * Example: "ABCDGH" and "AEDFHR"
     * LCS: "ADH" (length 3)
     *
     * DRY RUN:
     * str1 = "ABC"
     * str2 = "AC"
     *
     * Create 2D DP:
     *     ""  A  B  C
     * ""  0   0  0  0
     * A   0   1  1  1
     * C   0   1  1  2
     *
     * Filling logic:
     * dp[i][j] = longest common subsequence of str1[0..i-1] and str2[0..j-1]
     *
     * If str1[i-1] == str2[j-1]:
     *   dp[i][j] = dp[i-1][j-1] + 1
     * Else:
     *   dp[i][j] = max(dp[i-1][j], dp[i][j-1])
     *
     * Fill step by step:
     * dp[1][1]: str1[0]='A' == str2[0]='A'? YES
     *   dp[1][1] = dp[0][0] + 1 = 1
     * dp[1][2]: str1[0]='A' == str2[1]='C'? NO
     *   dp[1][2] = max(dp[0][2], dp[1][1]) = max(0, 1) = 1
     * dp[1][3]: str1[0]='A' == str2[2]='C'? NO
     *   dp[1][3] = max(dp[0][3], dp[1][2]) = max(0, 1) = 1
     *
     * dp[2][1]: str1[1]='B' == str2[0]='A'? NO
     *   dp[2][1] = max(dp[1][1], dp[2][0]) = max(1, 0) = 1
     * ... continue ...
     *
     * Result: dp[2][2] = 2 (LCS = "AC")
     *
     * Time: O(m * n), Space: O(m * n)
     */
    public static int longestCommonSubsequence(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    // ============ 0/1 KNAPSACK ============

    /**
     * 0/1 KNAPSACK PROBLEM
     * Given items with weights and values, find max value with weight limit
     * Each item can be used 0 or 1 time (not fractional)
     *
     * DRY RUN:
     * items: [(value=1, weight=1), (value=5, weight=4), (value=6, weight=5)]
     * capacity = 5
     *
     * DP table:
     *       0  1  2  3  4  5
     * 0     0  0  0  0  0  0
     * 1(v=1,w=1)
     * 2(v=5,w=4)
     * 3(v=6,w=5)
     *
     * For item 1 (value=1, weight=1):
     *   Capacity 1: Can take, dp[1][1] = max(dp[0][1], dp[0][1-1]+1) = max(0,1) = 1
     *   Capacity 2: Can take, dp[1][2] = max(0, dp[0][1]+1) = 1
     *   ... all capacities = 1
     *
     * For item 2 (value=5, weight=4):
     *   Capacity 1-3: Can't take (weight=4), dp[2][1-3] = dp[1][1-3]
     *   Capacity 4: Can take, dp[2][4] = max(dp[1][4], dp[1][0]+5) = max(1, 5) = 5
     *   Capacity 5: Can take, dp[2][5] = max(dp[1][5], dp[1][1]+5) = max(1, 6) = 6
     *
     * For item 3 (value=6, weight=5):
     *   Capacity 5: Can take, dp[3][5] = max(dp[2][5], dp[2][0]+6) = max(6, 6) = 6
     *
     * Result: dp[3][5] = 6
     *
     * Time: O(n * capacity), Space: O(n * capacity)
     */
    public static int knapsack(int[] values, int[] weights, int capacity) {
        int n = values.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(
                        dp[i - 1][w],  // Don't take item
                        dp[i - 1][w - weights[i - 1]] + values[i - 1]  // Take item
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];  // Can't take item
                }
            }
        }

        return dp[n][capacity];
    }

    public static void main(String[] args) {
        System.out.println("===== RECURSION AND DYNAMIC PROGRAMMING =====\n");

        // Fibonacci
        System.out.println("--- Fibonacci ---");
        int n = 10;
        System.out.println("fib(" + n + ") using pure recursion: " + fibonacci(n));

        Map<Integer, Integer> memo = new HashMap<>();
        System.out.println("fib(" + n + ") using memoization: " + fibonacciMemo(n, memo));

        System.out.println("fib(" + n + ") using tabulation: " + fibonacciTab(n));
        System.out.println();

        // Coin Change
        System.out.println("--- Coin Change ---");
        int[] coins = {1, 2, 5};
        int amount = 5;
        System.out.println("Coins: " + Arrays.toString(coins) + ", Amount: " + amount);
        System.out.println("Minimum coins needed: " + coinChange(coins, amount));

        amount = 11;
        System.out.println("Amount: " + amount);
        System.out.println("Minimum coins needed: " + coinChange(coins, amount));
        System.out.println();

        // LCS
        System.out.println("--- Longest Common Subsequence ---");
        String str1 = "ABCDGH";
        String str2 = "AEDFHR";
        System.out.println("String 1: " + str1);
        System.out.println("String 2: " + str2);
        System.out.println("LCS length: " + longestCommonSubsequence(str1, str2));
        System.out.println();

        // Knapsack
        System.out.println("--- 0/1 Knapsack ---");
        int[] values = {1, 5, 6};
        int[] weights = {1, 4, 5};
        int capacity = 5;
        System.out.println("Values: " + Arrays.toString(values));
        System.out.println("Weights: " + Arrays.toString(weights));
        System.out.println("Capacity: " + capacity);
        System.out.println("Maximum value: " + knapsack(values, weights, capacity));
    }
}

