/**
 * HASHING, BIT MANIPULATION, AND GREEDY ALGORITHMS
 *
 * HASHING:
 * - Hash Table: Maps keys to values using hash function
 * - Time: O(1) average, O(n) worst case
 * - Collision handling: Chaining, Open addressing
 *
 * BIT MANIPULATION:
 * - Bitwise operations for optimization
 * - Common tricks: AND, OR, XOR, bit shifting
 *
 * GREEDY ALGORITHMS:
 * - Make locally optimal choice at each step
 * - Assumes local optimal leads to global optimal
 * - Examples: Activity selection, fractional knapsack, Huffman coding
 */

import java.util.*;

public class HashingAndAdvanced {

    // ============ HASHING PROBLEMS ============

    /**
     * PROBLEM 1: Two Sum using HashMap
     * Given array and target, find two indices that sum to target
     *
     * DRY RUN: arr=[2,7,11,15], target=9
     *
     * Approach 1: Brute force O(n²)
     *   Check all pairs: 2+7=9? YES! Return indices
     *
     * Approach 2: HashMap O(n)
     *
     * i=0, num=2:
     *   complement = 9-2 = 7
     *   Is 7 in map? No
     *   Add 2 to map: map={2:0}
     *
     * i=1, num=7:
     *   complement = 9-7 = 2
     *   Is 2 in map? YES! map[2]=0
     *   Return [0, 1]
     *
     * Time: O(n), Space: O(n)
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }

            map.put(nums[i], i);
        }

        return null;
    }

    /**
     * PROBLEM 2: First Unique Character using HashMap
     * Find first character that appears only once
     *
     * DRY RUN: s = "leetcode"
     *
     * Count frequency:
     * l:1, e:3, t:1, c:1, o:1, d:1
     *
     * Find first with count=1:
     * Position 0: 'l', count=1? YES! Return 0
     *
     * Alternative: s = "loveleetcode"
     * l:2, o:2, v:1, e:4, t:1, c:1, d:1
     *
     * Position 2: 'v', count=1? YES! Return 2
     *
     * Time: O(n), Space: O(1) for ASCII (26 letters)
     */
    public static int firstUniqChar(String s) {
        Map<Character, Integer> charCount = new HashMap<>();

        // Count frequencies
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // Find first with count 1
        for (int i = 0; i < s.length(); i++) {
            if (charCount.get(s.charAt(i)) == 1) {
                return i;
            }
        }

        return -1;
    }

    /**
     * PROBLEM 3: Valid Anagram
     * Check if two strings are anagrams (same chars, different order)
     *
     * DRY RUN: s1="anagram", s2="nagaram"
     *
     * Approach: Count characters
     * s1: a:3, n:1, g:1, r:1, m:1
     * s2: n:1, a:3, g:1, r:1, m:1
     *
     * Same counts? YES! Return true
     *
     * Time: O(n), Space: O(1) for fixed charset
     */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] counts = new int[26];

        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }

        for (int count : counts) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

    // ============ BIT MANIPULATION ============

    /**
     * BIT MANIPULATION - Common Operations
     *
     * & (AND):     Both bits 1 -> result 1
     * | (OR):      Either bit 1 -> result 1
     * ^ (XOR):     Different bits -> result 1
     * ~ (NOT):     Flip all bits
     * << (Left shift):  Multiply by 2
     * >> (Right shift): Divide by 2
     *
     * DRY RUN:
     * 5 in binary: 0101
     * 3 in binary: 0011
     *
     * 5 & 3:
     *   0101 & 0011 = 0001 = 1
     *
     * 5 | 3:
     *   0101 | 0011 = 0111 = 7
     *
     * 5 ^ 3:
     *   0101 ^ 0011 = 0110 = 6
     */

    /**
     * TRICK 1: Check if power of 2
     * Number is power of 2 if it has only one bit set
     *
     * DRY RUN:
     * 8 in binary: 1000
     * 8-1 = 7 in binary: 0111
     * 8 & 7 = 1000 & 0111 = 0000 = 0? YES! 8 is power of 2
     *
     * 6 in binary: 0110
     * 6-1 = 5 in binary: 0101
     * 6 & 5 = 0110 & 0101 = 0100 = 4? NO! 6 is not power of 2
     *
     * Time: O(1)
     */
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * TRICK 2: Count number of 1 bits
     * Brian Kernighan's Algorithm
     *
     * DRY RUN: n = 11 (binary: 1011)
     *
     * Step 1: 1011 & 1010 = 1010, count=1
     * Step 2: 1010 & 1001 = 1000, count=2
     * Step 3: 1000 & 0111 = 0000, count=3
     *
     * Result: 3 ones
     *
     * Time: O(k) where k = number of 1 bits
     */
    public static int countOnes(int n) {
        int count = 0;
        while (n > 0) {
            n = n & (n - 1);  // Remove rightmost 1 bit
            count++;
        }
        return count;
    }

    /**
     * TRICK 3: Single Number - Find element appearing once
     * All others appear twice
     *
     * DRY RUN: [2, 2, 1]
     *
     * XOR property: a ^ a = 0, a ^ 0 = a
     * 2 ^ 2 ^ 1 = 0 ^ 1 = 1
     *
     * Why: Pairs cancel out (XOR), single number remains
     *
     * Time: O(n), Space: O(1)
     */
    public static int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    // ============ GREEDY ALGORITHMS ============

    /**
     * GREEDY PROBLEM 1: Activity Selection
     * Select maximum activities that don't overlap
     * Each activity has start and end time
     *
     * DRY RUN:
     * Activities (start, end):
     * (1,4), (3,5), (0,6), (5,7), (3,9), (5,9), (6,10), (8,11), (8,12), (2,13), (12,16)
     *
     * Greedy: Always pick activity ending earliest
     *
     * Sort by end time:
     * (1,4), (3,5), (0,6), (5,7), (3,9), (6,10), (5,9), (8,11), (2,13), (8,12), (12,16)
     *
     * Select (1,4): end=4
     * Try (3,5): start=3 < 4? Overlaps! Skip
     * Try (0,6): start=0 < 4? Overlaps! Skip
     * Try (5,7): start=5 >= 4? OK! Select, end=7
     * Try (3,9): start=3 < 7? Overlaps! Skip
     * Try (6,10): start=6 < 7? Overlaps! Skip
     * Try (5,9): start=5 < 7? Overlaps! Skip
     * Try (8,11): start=8 >= 7? OK! Select, end=11
     * Try (2,13): start=2 < 11? Overlaps! Skip
     * Try (8,12): start=8 < 11? Overlaps! Skip
     * Try (12,16): start=12 >= 11? OK! Select, end=16
     *
     * Result: [(1,4), (5,7), (8,11), (12,16)] = 4 activities
     *
     * Time: O(n log n) for sorting, Space: O(1)
     */
    public static int selectActivities(int[][] activities) {
        Arrays.sort(activities, (a, b) -> a[1] - b[1]);

        int count = 1;
        int lastEnd = activities[0][1];

        for (int i = 1; i < activities.length; i++) {
            if (activities[i][0] >= lastEnd) {
                count++;
                lastEnd = activities[i][1];
            }
        }

        return count;
    }

    /**
     * GREEDY PROBLEM 2: Coin Change (Greedy approach)
     * Note: Greedy doesn't always work! But works for standard coin systems
     *
     * DRY RUN: Coins [25, 10, 5, 1], amount = 41
     *
     * Take 25 cent coins: 41 / 25 = 1 coin, remainder = 16
     * Take 10 cent coins: 16 / 10 = 1 coin, remainder = 6
     * Take 5 cent coins:  6 / 5 = 1 coin, remainder = 1
     * Take 1 cent coins:  1 / 1 = 1 coin, remainder = 0
     *
     * Total: 1 + 1 + 1 + 1 = 4 coins
     *
     * Time: O(c) where c = number of coin types
     */
    public static int greedyCoinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        int count = 0;

        for (int i = coins.length - 1; i >= 0; i--) {
            count += amount / coins[i];
            amount %= coins[i];
        }

        return count;
    }

    public static void main(String[] args) {
        System.out.println("===== HASHING AND ADVANCED CONCEPTS =====\n");

        // Two Sum
        System.out.println("--- Two Sum ---");
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);
        System.out.println("Array: " + Arrays.toString(nums) + ", Target: " + target);
        System.out.println("Result indices: " + Arrays.toString(result));
        System.out.println();

        // First Unique Character
        System.out.println("--- First Unique Character ---");
        String s = "leetcode";
        System.out.println("String: " + s);
        System.out.println("First unique char index: " + firstUniqChar(s));
        System.out.println();

        // Valid Anagram
        System.out.println("--- Valid Anagram ---");
        String s1 = "anagram";
        String s2 = "nagaram";
        System.out.println("s1: " + s1 + ", s2: " + s2);
        System.out.println("Is anagram: " + isAnagram(s1, s2));
        System.out.println();

        // Bit Manipulation
        System.out.println("--- Bit Manipulation ---");
        System.out.println("Is 8 power of 2: " + isPowerOfTwo(8));
        System.out.println("Is 6 power of 2: " + isPowerOfTwo(6));
        System.out.println("Count ones in 11: " + countOnes(11));
        System.out.println("Single number in [2,2,1]: " + singleNumber(new int[]{2, 2, 1}));
        System.out.println();

        // Greedy: Activity Selection
        System.out.println("--- Activity Selection ---");
        int[][] activities = {{1, 4}, {3, 5}, {0, 6}, {5, 7}, {3, 9}, {5, 9}};
        System.out.println("Max activities: " + selectActivities(activities));
        System.out.println();

        // Greedy: Coin Change
        System.out.println("--- Greedy Coin Change ---");
        int[] coins = {25, 10, 5, 1};
        int amount = 41;
        System.out.println("Coins: " + Arrays.toString(coins) + ", Amount: " + amount);
        System.out.println("Min coins (greedy): " + greedyCoinChange(coins, amount));
    }
}

