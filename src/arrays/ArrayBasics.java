package arrays;

/*
 * =============================================================================
 * ArrayBasics.java — 1D Arrays in Java
 * =============================================================================
 *
 * CONCEPT: Arrays
 * ----------------
 * An array is a fixed-size, ordered collection of elements of the SAME type.
 * Arrays in Java are OBJECTS stored on the heap.
 *
 * KEY CHARACTERISTICS:
 *  - Fixed size: you must specify size at creation; it CANNOT change
 *  - Zero-indexed: first element is at index 0, last at index (length-1)
 *  - Homogeneous: all elements must be the same type
 *  - Default values: int[] → 0, double[] → 0.0, boolean[] → false, Object[] → null
 *
 * SYNTAX:
 *   // Declaration
 *   int[] numbers;           // preferred (type[] name)
 *   int numbers[];           // also valid, C-style (avoid)
 *
 *   // Creation (allocation)
 *   numbers = new int[5];    // creates array of 5 integers (all initialized to 0)
 *
 *   // Declaration + Creation
 *   int[] numbers = new int[5];
 *
 *   // Declaration + Creation + Initialization
 *   int[] numbers = {10, 20, 30, 40, 50};  // array literal
 *   int[] numbers = new int[]{10, 20, 30};  // also valid
 *
 * MEMORY:
 *   - The array VARIABLE stores a reference (address) to the array object
 *   - The array DATA is stored on the Heap
 *   - Assigning one array to another copies the REFERENCE, not the data
 *
 * HOW TO RUN:
 *  $ javac -d out src/arrays/ArrayBasics.java
 *  $ java -cp out arrays.ArrayBasics
 * =============================================================================
 */
public class ArrayBasics {

    public static void main(String[] args) {

        // ── CREATING ARRAYS ───────────────────────────────────────────────────
        System.out.println("=== Creating Arrays ===");

        // Method 1: new keyword (elements are set to default value — 0 for int)
        int[] scores = new int[5];
        System.out.println("New int[5]: " + scores[0] + ", " + scores[1] + "... (all zeros)");

        // Method 2: Array literal (specify values at creation)
        int[] primes = {2, 3, 5, 7, 11};
        System.out.println("Primes array length: " + primes.length);  // .length is NOT a method call!

        // Method 3: new with initializer list
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"};
        System.out.println("Days[0]: " + days[0]);

        // ── ACCESSING ELEMENTS ────────────────────────────────────────────────
        System.out.println("\n=== Accessing Elements ===");

        int[] nums = {10, 20, 30, 40, 50};
        System.out.println("First:  nums[0] = " + nums[0]);              // 10
        System.out.println("Last:   nums[nums.length-1] = " + nums[nums.length - 1]);  // 50
        System.out.println("Middle: nums[2] = " + nums[2]);              // 30

        // ── MODIFYING ELEMENTS ────────────────────────────────────────────────
        System.out.println("\n=== Modifying Elements ===");
        nums[2] = 999;   // change element at index 2
        System.out.println("After nums[2] = 999: nums[2] = " + nums[2]);

        // ── ITERATING OVER ARRAYS ─────────────────────────────────────────────
        System.out.println("\n=== Iterating (traditional for) ===");
        int[] temperatures = {22, 25, 19, 28, 17, 30, 24};

        for (int i = 0; i < temperatures.length; i++) {
            System.out.printf("  Day %d: %d°C%n", i + 1, temperatures[i]);
        }

        System.out.println("\n=== Iterating (enhanced for) ===");
        for (int temp : temperatures) {
            System.out.print(temp + " ");
        }
        System.out.println();

        // ── ARRAY OPERATIONS ─────────────────────────────────────────────────
        System.out.println("\n=== Array Operations ===");

        // Find max, min, sum
        int max = temperatures[0], min = temperatures[0], sum = 0;
        for (int temp : temperatures) {
            if (temp > max) max = temp;
            if (temp < min) min = temp;
            sum += temp;
        }
        double avg = (double) sum / temperatures.length;

        System.out.println("Max temperature: " + max + "°C");
        System.out.println("Min temperature: " + min + "°C");
        System.out.printf("Average:         %.1f°C%n", avg);

        // ── ARRAY COPY ────────────────────────────────────────────────────────
        System.out.println("\n=== Array Copy ===");
        int[] original = {1, 2, 3, 4, 5};

        // WRONG: this copies the reference, not the data
        int[] wrongCopy = original;
        wrongCopy[0] = 999;       // modifies BOTH original and wrongCopy!
        System.out.println("original[0] after wrongCopy[0]=999: " + original[0]);  // 999!

        // CORRECT: Manual copy
        int[] manualCopy = new int[original.length];
        for (int i = 0; i < original.length; i++) {
            manualCopy[i] = original[i];
        }

        // CORRECT: System.arraycopy (most efficient)
        int[] src = {1, 2, 3, 4, 5};
        int[] dest = new int[5];
        System.arraycopy(src, 0, dest, 0, src.length);
        dest[0] = 100;
        System.out.println("src[0] unchanged: " + src[0]);   // 1 (not affected)
        System.out.println("dest[0] changed:  " + dest[0]);  // 100

        // ── ARRAY BOUNDS CHECK ────────────────────────────────────────────────
        System.out.println("\n=== ArrayIndexOutOfBoundsException Demo ===");
        int[] arr = {10, 20, 30};
        try {
            System.out.println(arr[5]);  // Index 5 doesn't exist (max is 2)
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // ── PASSING ARRAYS TO METHODS ─────────────────────────────────────────
        System.out.println("\n=== Passing Arrays to Methods ===");
        int[] data = {5, 3, 8, 1, 9, 2};
        System.out.println("Sum of data: " + sumArray(data));
        System.out.println("Reversed:");
        reverseArray(data);
        for (int d : data) System.out.print(d + " ");
        System.out.println();
    }

    // Method that accepts an array and returns the sum
    static int sumArray(int[] arr) {
        int total = 0;
        for (int val : arr) total += val;
        return total;
    }

    // Method that reverses an array IN-PLACE (modifies the original)
    static void reverseArray(int[] arr) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Creating Arrays ===
 * New int[5]: 0, 0... (all zeros)
 * Primes array length: 5
 * Days[0]: Mon
 *
 * === Accessing Elements ===
 * First:  nums[0] = 10
 * Last:   nums[nums.length-1] = 50
 * Middle: nums[2] = 30
 *
 * ... (plus more output)
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. ArrayIndexOutOfBoundsException: accessing arr[arr.length] — valid range is 0 to length-1
 * 2. Shallow copy: int[] b = a; — both variables point to the same array!
 * 3. Using arr.length() with parentheses — .length is a FIELD, not a method!
 * 4. Trying to change array size after creation — use ArrayList for dynamic sizing
 * 5. Confusing arrays of objects (null default) with arrays of primitives (0 default)
 */
