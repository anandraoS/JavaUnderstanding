/**
 * ARRAYS AND STRINGS - Fundamental Data Structures
 *
 * Arrays: Fixed-size collection of elements
 * Strings: Immutable sequence of characters
 *
 * Common Operations:
 * - Access: O(1)
 * - Search: O(n)
 * - Insert: O(n)
 * - Delete: O(n)
 */

public class ArraysAndStrings {

    // ============ TWO-POINTER TECHNIQUE ============
    /**
     * CONCEPT: Use two pointers to solve problems efficiently
     *
     * Example 1: Two Sum (Target Sum)
     * Given sorted array, find two numbers that add up to target
     *
     * DRY RUN:
     * Input: arr = [1, 2, 3, 6, 9], target = 15
     *
     * Step 1: left=0(1), right=4(9)  -> 1+9=10 < 15, move left++
     * Step 2: left=1(2), right=4(9)  -> 2+9=11 < 15, move left++
     * Step 3: left=2(3), right=4(9)  -> 3+9=12 < 15, move left++
     * Step 4: left=3(6), right=4(9)  -> 6+9=15 = 15, FOUND! Return [6,9]
     *
     * Time: O(n), Space: O(1)
     */
    public static int[] twoSum(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            int sum = arr[left] + arr[right];

            if (sum == target) {
                return new int[]{arr[left], arr[right]};
            } else if (sum < target) {
                left++;  // Need larger sum, move left pointer right
            } else {
                right--;  // Need smaller sum, move right pointer left
            }
        }
        return null;
    }

    /**
     * CONCEPT: SLIDING WINDOW Technique
     *
     * Example: Find longest substring without repeating characters
     *
     * DRY RUN:
     * Input: s = "abcabcbb"
     *
     * Position: 0 1 2 3 4 5 6 7
     * String:   a b c a b c b b
     *
     * Step 1: left=0, right=0, char='a'   -> window="a",   maxLen=1
     * Step 2: left=0, right=1, char='b'   -> window="ab",  maxLen=2
     * Step 3: left=0, right=2, char='c'   -> window="abc", maxLen=3
     * Step 4: left=0, right=3, char='a'   -> 'a' already at index 0!
     *         Move left to 1 (skip duplicate) -> window="bca", maxLen=3
     * Step 5: left=1, right=4, char='b'   -> 'b' already at index 1!
     *         Move left to 2 -> window="cab", maxLen=3
     * Step 6: left=2, right=5, char='c'   -> 'c' already at index 2!
     *         Move left to 3 -> window="abc", maxLen=3
     * Step 7: left=3, right=6, char='b'   -> 'b' already in window
     *         Move left to 4 -> window="cb", maxLen=3
     * Step 8: left=4, right=7, char='b'   -> 'b' already at index 4!
     *         Move left to 5 -> window="b", maxLen=3
     *
     * Answer: 3 (substring "abc")
     * Time: O(n), Space: O(min(m, n)) where m = charset size
     */
    public static int lengthOfLongestSubstring(String s) {
        java.util.Map<Character, Integer> charIndex = new java.util.HashMap<>();
        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // If character exists in current window, move left pointer
            if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
                left = charIndex.get(c) + 1;
            }

            charIndex.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Example: Reverse an array
     * Input: [1, 2, 3, 4, 5]
     *
     * DRY RUN:
     * Step 1: left=0, right=4  -> swap arr[0] and arr[4] -> [5,2,3,4,1]
     * Step 2: left=1, right=3  -> swap arr[1] and arr[3] -> [5,4,3,2,1]
     * Step 3: left=2, right=2  -> left >= right, stop
     *
     * Result: [5, 4, 3, 2, 1]
     */
    public static void reverseArray(int[] arr) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            // Swap
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;

            left++;
            right--;
        }
    }

    /**
     * ============ STRING MANIPULATION ============
     *
     * Example: Check if string is palindrome
     * Input: "racecar"
     *
     * DRY RUN:
     * Position: 0 1 2 3 4 5 6
     * String:   r a c e c a r
     *
     * Step 1: Compare 'r'(0) and 'r'(6) -> match ✓
     * Step 2: Compare 'a'(1) and 'a'(5) -> match ✓
     * Step 3: Compare 'c'(2) and 'c'(4) -> match ✓
     * Step 4: left=3, right=3 -> stop
     *
     * Result: true (palindrome)
     */
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("===== ARRAYS AND STRINGS =====\n");

        // Two-Pointer Example
        System.out.println("--- Two-Pointer: Two Sum ---");
        int[] arr1 = {1, 2, 3, 6, 9};
        int target = 15;
        int[] result = twoSum(arr1, target);
        System.out.println("Array: " + java.util.Arrays.toString(arr1));
        System.out.println("Target: " + target);
        System.out.println("Result: " + java.util.Arrays.toString(result));
        System.out.println();

        // Sliding Window Example
        System.out.println("--- Sliding Window: Longest Substring ---");
        String s1 = "abcabcbb";
        int len = lengthOfLongestSubstring(s1);
        System.out.println("String: " + s1);
        System.out.println("Longest substring length without repeating: " + len);
        System.out.println();

        // Reverse Array
        System.out.println("--- Two-Pointer: Reverse Array ---");
        int[] arr2 = {1, 2, 3, 4, 5};
        System.out.println("Original: " + java.util.Arrays.toString(arr2));
        reverseArray(arr2);
        System.out.println("Reversed: " + java.util.Arrays.toString(arr2));
        System.out.println();

        // Palindrome Check
        System.out.println("--- String: Palindrome Check ---");
        String[] testStrings = {"racecar", "hello", "a", "ab"};
        for (String test : testStrings) {
            System.out.println(test + " -> " + isPalindrome(test));
        }
    }
}

