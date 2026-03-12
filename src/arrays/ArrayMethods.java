package arrays;

import java.util.Arrays;    // Arrays utility class for sorting, searching, etc.

/*
 * =============================================================================
 * ArrayMethods.java — java.util.Arrays Utility Class and Common Operations
 * =============================================================================
 *
 * CONCEPT: Arrays Utility Class
 * --------------------------------
 * java.util.Arrays provides static methods to work with arrays:
 *
 *   Arrays.sort(arr)              — sort in ascending order (in-place)
 *   Arrays.sort(arr, from, to)    — sort a range only
 *   Arrays.binarySearch(arr, key) — find element (array must be sorted first!)
 *   Arrays.fill(arr, value)       — fill all elements with a value
 *   Arrays.copyOf(arr, newLen)    — copy with new length (truncates or pads)
 *   Arrays.copyOfRange(arr, from, to) — copy a subrange
 *   Arrays.equals(arr1, arr2)     — compare content of two arrays
 *   Arrays.toString(arr)          — convert array to readable string "[1, 2, 3]"
 *   Arrays.deepToString(arr2D)    — for multi-dimensional arrays
 *
 * HOW TO RUN:
 *  $ javac -d out src/arrays/ArrayMethods.java
 *  $ java -cp out arrays.ArrayMethods
 * =============================================================================
 */
public class ArrayMethods {

    public static void main(String[] args) {

        // ── ARRAYS.TOSTRING ───────────────────────────────────────────────────
        System.out.println("=== Arrays.toString() ===");
        int[] arr = {5, 2, 8, 1, 9, 3};
        System.out.println("Array: " + Arrays.toString(arr));  // [5, 2, 8, 1, 9, 3]

        // ── ARRAYS.SORT ───────────────────────────────────────────────────────
        System.out.println("\n=== Arrays.sort() ===");
        Arrays.sort(arr);    // sort in-place, ascending order
        System.out.println("Sorted: " + Arrays.toString(arr));  // [1, 2, 3, 5, 8, 9]

        // Sort strings (alphabetical order)
        String[] fruits = {"Banana", "Apple", "Cherry", "Date", "Mango"};
        Arrays.sort(fruits);
        System.out.println("Sorted fruits: " + Arrays.toString(fruits));

        // Sort a range [from, to) — from=inclusive, to=exclusive
        int[] partial = {5, 3, 8, 1, 9, 4};
        Arrays.sort(partial, 1, 4);   // sort indices 1, 2, 3 only
        System.out.println("Partial sort (indices 1-3): " + Arrays.toString(partial));

        // ── ARRAYS.BINARYSEARCH ───────────────────────────────────────────────
        System.out.println("\n=== Arrays.binarySearch() ===");
        // IMPORTANT: Array MUST be sorted before binary search!
        int[] sorted = {1, 3, 5, 7, 9, 11, 13};
        int idx = Arrays.binarySearch(sorted, 7);
        System.out.println("Index of 7: " + idx);         // 3

        int notFound = Arrays.binarySearch(sorted, 6);
        System.out.println("Index of 6 (not found): " + notFound);  // negative value

        // ── ARRAYS.FILL ───────────────────────────────────────────────────────
        System.out.println("\n=== Arrays.fill() ===");
        int[] zeros = new int[5];
        Arrays.fill(zeros, 7);            // fill all with 7
        System.out.println("Filled with 7: " + Arrays.toString(zeros));  // [7,7,7,7,7]

        Arrays.fill(zeros, 1, 3, 99);     // fill indices 1, 2 with 99
        System.out.println("Partial fill: " + Arrays.toString(zeros));   // [7,99,99,7,7]

        // ── ARRAYS.COPYOF ────────────────────────────────────────────────────
        System.out.println("\n=== Arrays.copyOf() ===");
        int[] original = {10, 20, 30, 40, 50};

        int[] shorter = Arrays.copyOf(original, 3);  // first 3 elements
        System.out.println("Shorter: " + Arrays.toString(shorter));   // [10, 20, 30]

        int[] longer = Arrays.copyOf(original, 7);   // extends with 0s
        System.out.println("Longer:  " + Arrays.toString(longer));    // [10,20,30,40,50,0,0]

        int[] range = Arrays.copyOfRange(original, 1, 4);  // indices 1 to 3
        System.out.println("Range [1,4): " + Arrays.toString(range)); // [20, 30, 40]

        // ── ARRAYS.EQUALS ────────────────────────────────────────────────────
        System.out.println("\n=== Arrays.equals() ===");
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        int[] c = {1, 2, 4};

        System.out.println("a equals b: " + Arrays.equals(a, b));  // true
        System.out.println("a equals c: " + Arrays.equals(a, c));  // false
        System.out.println("a == b:     " + (a == b));              // false (different objects!)

        // ── DEEPTOSTRING FOR 2D ───────────────────────────────────────────────
        System.out.println("\n=== Arrays.deepToString() for 2D array ===");
        int[][] matrix = {{1, 2}, {3, 4}, {5, 6}};
        System.out.println("2D: " + Arrays.deepToString(matrix));  // [[1, 2], [3, 4], [5, 6]]

        // ── BUBBLE SORT (manual implementation) ──────────────────────────────
        System.out.println("\n=== Bubble Sort (manual) ===");
        int[] bubbleArr = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("Before: " + Arrays.toString(bubbleArr));
        bubbleSort(bubbleArr);
        System.out.println("After:  " + Arrays.toString(bubbleArr));

        // ── LINEAR SEARCH (manual) ────────────────────────────────────────────
        System.out.println("\n=== Linear Search ===");
        int[] data = {15, 42, 7, 93, 26, 81};
        int target = 93;
        int foundAt = linearSearch(data, target);
        System.out.println("Linear search for " + target + ": index " + foundAt);
    }

    // Bubble sort: repeatedly swap adjacent elements if out of order
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // Linear search: check each element one by one
    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;   // not found
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Calling binarySearch on an UNSORTED array → wrong/unpredictable results
 * 2. Using == to compare arrays → compares references, not content!
 *    Use Arrays.equals() for content comparison.
 * 3. Using Arrays.toString() on 2D array → prints reference addresses!
 *    Use Arrays.deepToString() for multi-dimensional arrays.
 * 4. Arrays.sort() modifies the original array in-place; make a copy first
 *    if you need the original order.
 */
