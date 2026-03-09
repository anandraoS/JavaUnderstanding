/**
 * SEARCHING AND SORTING - Core Algorithms
 *
 * SEARCHING:
 * - Linear Search: O(n)
 * - Binary Search: O(log n) - requires sorted array
 *
 * SORTING:
 * - Bubble Sort: O(n²) - simple but slow
 * - Selection Sort: O(n²) - minimizes writes
 * - Insertion Sort: O(n²) - good for small/partially sorted data
 * - Merge Sort: O(n log n) - stable, requires extra space
 * - Quick Sort: O(n log n) average, O(n²) worst - in-place
 * - Heap Sort: O(n log n) - in-place
 */

public class SearchingAndSorting {

    // ============ BINARY SEARCH ============
    /**
     * CONCEPT: Divide and conquer on sorted array
     *
     * DRY RUN: Search for 5 in [1, 3, 5, 7, 9, 11]
     *
     * Initial: left=0, right=5
     * Step 1: mid = (0+5)/2 = 2, arr[2]=5
     *         Target=5, found! Return 2
     *
     * Example 2: Search for 6 in [1, 3, 5, 7, 9, 11]
     * Initial: left=0, right=5
     * Step 1: mid=2, arr[2]=5 < 6, so left=3
     * Step 2: mid=(3+5)/2=4, arr[4]=9 > 6, so right=3
     * Step 3: mid=(3+3)/2=3, arr[3]=7 > 6, so right=2
     * Step 4: left=3, right=2, left>right, stop
     * Result: -1 (not found)
     *
     * Time: O(log n), Space: O(1)
     */
    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;  // Avoid overflow

            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    // ============ BUBBLE SORT ============
    /**
     * CONCEPT: Repeatedly swap adjacent elements if wrong order
     * Works by "bubbling" larger elements to the end
     *
     * DRY RUN: Sort [5, 2, 8, 1, 9]
     *
     * Pass 1 (i=0):
     *   Compare 5,2 -> swap -> [2, 5, 8, 1, 9]
     *   Compare 5,8 -> no swap -> [2, 5, 8, 1, 9]
     *   Compare 8,1 -> swap -> [2, 5, 1, 8, 9]
     *   Compare 8,9 -> no swap -> [2, 5, 1, 8, 9]
     *
     * Pass 2 (i=1):
     *   Compare 2,5 -> no swap -> [2, 5, 1, 8, 9]
     *   Compare 5,1 -> swap -> [2, 1, 5, 8, 9]
     *   Compare 5,8 -> no swap -> [2, 1, 5, 8, 9]
     *
     * Pass 3 (i=2):
     *   Compare 2,1 -> swap -> [1, 2, 5, 8, 9]
     *   Compare 2,5 -> no swap -> [1, 2, 5, 8, 9]
     *
     * Pass 4 (i=3):
     *   Compare 1,2 -> no swap -> [1, 2, 5, 8, 9]
     *
     * Result: [1, 2, 5, 8, 9] ✓
     *
     * Time: O(n²), Space: O(1)
     * Best case: O(n) with optimization (no swaps = sorted)
     */
    public static void bubbleSort(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            // Each pass, largest element "bubbles" to end
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Swap
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }

            // Optimization: if no swap, array is sorted
            if (!swapped) {
                break;
            }
        }
    }

    // ============ SELECTION SORT ============
    /**
     * CONCEPT: Find minimum element and place it at beginning
     * Repeat for remaining unsorted portion
     *
     * DRY RUN: Sort [5, 2, 8, 1, 9]
     *
     * Pass 1 (i=0):
     *   Find min in [5, 2, 8, 1, 9] -> 1 at index 3
     *   Swap arr[0] and arr[3] -> [1, 2, 8, 5, 9]
     *
     * Pass 2 (i=1):
     *   Find min in [2, 8, 5, 9] -> 2 at index 1 (already in place)
     *   No swap -> [1, 2, 8, 5, 9]
     *
     * Pass 3 (i=2):
     *   Find min in [8, 5, 9] -> 5 at index 3
     *   Swap arr[2] and arr[3] -> [1, 2, 5, 8, 9]
     *
     * Pass 4 (i=3):
     *   Find min in [8, 9] -> 8 at index 3 (already in place)
     *   No swap -> [1, 2, 5, 8, 9]
     *
     * Result: [1, 2, 5, 8, 9] ✓
     *
     * Time: O(n²), Space: O(1)
     * Good for: Minimizing writes (only n swaps)
     */
    public static void selectionSort(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            // Find minimum in remaining array
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }

            // Swap minimum with current position
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
    }

    // ============ INSERTION SORT ============
    /**
     * CONCEPT: Build sorted array one element at a time
     * Like sorting playing cards in hand
     *
     * DRY RUN: Sort [5, 2, 8, 1, 9]
     *
     * Initial: sorted=[5], unsorted=[2, 8, 1, 9]
     *
     * Step 1: Insert 2
     *   Compare with 5: 2 < 5, shift 5 right -> [2, 5], unsorted=[8, 1, 9]
     *
     * Step 2: Insert 8
     *   Compare with 5: 8 > 5, done -> [2, 5, 8], unsorted=[1, 9]
     *
     * Step 3: Insert 1
     *   Compare with 8: 1 < 8, shift -> [2, 5, 1, 8]
     *   Compare with 5: 1 < 5, shift -> [2, 1, 5, 8]
     *   Compare with 2: 1 < 2, shift -> [1, 2, 5, 8]
     *   unsorted=[9]
     *
     * Step 4: Insert 9
     *   Compare with 8: 9 > 8, done -> [1, 2, 5, 8, 9]
     *
     * Result: [1, 2, 5, 8, 9] ✓
     *
     * Time: O(n²) average, O(n) best case (nearly sorted)
     * Space: O(1)
     * Good for: Small arrays, nearly sorted data
     */
    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            // Shift elements greater than key one position right
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            // Insert key at correct position
            arr[j + 1] = key;
        }
    }

    // ============ MERGE SORT ============
    /**
     * CONCEPT: Divide array in half, sort recursively, merge
     * Stable, guaranteed O(n log n), but needs extra space
     *
     * DRY RUN: Sort [5, 2, 8, 1]
     *
     * Divide:
     *   [5, 2, 8, 1]
     *   /          \
     * [5, 2]       [8, 1]
     * /    \       /    \
     * [5]   [2]   [8]   [1]
     *
     * Merge (conquer):
     * [5] + [2] -> compare 5 vs 2 -> [2, 5]
     * [8] + [1] -> compare 8 vs 1 -> [1, 8]
     * [2,5] + [1,8] -> compare 2 vs 1 -> [1]
     *               -> compare 2 vs 8 -> [1, 2]
     *               -> compare 5 vs 8 -> [1, 2, 5]
     *               -> only 8 left -> [1, 2, 5, 8]
     *
     * Result: [1, 2, 5, 8] ✓
     *
     * Time: O(n log n), Space: O(n)
     * Good for: Large datasets, when stability needed
     */
    public static void mergeSort(int[] arr) {
        if (arr.length < 2) return;

        int mid = arr.length / 2;
        int[] left = new int[mid];
        int[] right = new int[arr.length - mid];

        System.arraycopy(arr, 0, left, 0, mid);
        System.arraycopy(arr, mid, right, 0, arr.length - mid);

        mergeSort(left);
        mergeSort(right);
        merge(arr, left, right);
    }

    private static void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }

        while (i < left.length) {
            arr[k++] = left[i++];
        }

        while (j < right.length) {
            arr[k++] = right[j++];
        }
    }

    // ============ QUICK SORT ============
    /**
     * CONCEPT: Pick pivot, partition around it, recursively sort
     * In-place, average O(n log n), worst O(n²)
     *
     * DRY RUN: Sort [5, 2, 8, 1, 9] with pivot=last element(9)
     *
     * Partition:
     *   left=0, right=4, pivot=9
     *   2 < 9 -> keep, 1 < 9 -> keep, 8 < 9 -> keep, 5 < 9 -> keep
     *   All < 9, so pivot position = 4
     *   Array: [5, 2, 8, 1, 9]
     *
     * Recursively sort [5, 2, 8, 1] with pivot=1:
     *   Swap 5 and 1 -> [1, 2, 8, 5, 9]
     *   Partition around 1: [1, 2, 8, 5, 9]
     *
     * Recursively sort [2, 8, 5] with pivot=5:
     *   2 < 5, keep; 8 > 5, partition
     *   Swap 8 and 2: [2, 5, 8]
     *
     * Continue until fully sorted: [1, 2, 5, 8, 9]
     *
     * Time: O(n log n) average, O(n²) worst, Space: O(log n) recursion
     * Good for: Large datasets, in-place sorting needed
     */
    public static void quickSort(int[] arr) {
        if (arr.length == 0) return;
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int partitionIdx = partition(arr, low, high);
            quickSort(arr, low, partitionIdx - 1);
            quickSort(arr, partitionIdx + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) {
        System.out.println("===== SEARCHING AND SORTING =====\n");

        // Binary Search
        System.out.println("--- Binary Search ---");
        int[] sortedArr = {1, 3, 5, 7, 9, 11};
        System.out.println("Array: " + java.util.Arrays.toString(sortedArr));
        System.out.println("Search 5: " + binarySearch(sortedArr, 5));
        System.out.println("Search 6: " + binarySearch(sortedArr, 6));
        System.out.println();

        // Test all sorting algorithms
        int[][] testArrays = {
            {5, 2, 8, 1, 9},
            {5, 2, 8, 1, 9},
            {5, 2, 8, 1, 9},
            {5, 2, 8, 1, 9},
            {5, 2, 8, 1, 9}
        };

        System.out.println("--- Bubble Sort ---");
        System.out.println("Before: " + java.util.Arrays.toString(testArrays[0]));
        bubbleSort(testArrays[0]);
        System.out.println("After:  " + java.util.Arrays.toString(testArrays[0]));
        System.out.println();

        System.out.println("--- Selection Sort ---");
        System.out.println("Before: " + java.util.Arrays.toString(testArrays[1]));
        selectionSort(testArrays[1]);
        System.out.println("After:  " + java.util.Arrays.toString(testArrays[1]));
        System.out.println();

        System.out.println("--- Insertion Sort ---");
        System.out.println("Before: " + java.util.Arrays.toString(testArrays[2]));
        insertionSort(testArrays[2]);
        System.out.println("After:  " + java.util.Arrays.toString(testArrays[2]));
        System.out.println();

        System.out.println("--- Merge Sort ---");
        System.out.println("Before: " + java.util.Arrays.toString(testArrays[3]));
        mergeSort(testArrays[3]);
        System.out.println("After:  " + java.util.Arrays.toString(testArrays[3]));
        System.out.println();

        System.out.println("--- Quick Sort ---");
        System.out.println("Before: " + java.util.Arrays.toString(testArrays[4]));
        quickSort(testArrays[4]);
        System.out.println("After:  " + java.util.Arrays.toString(testArrays[4]));
    }
}

