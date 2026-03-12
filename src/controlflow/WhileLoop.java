package controlflow;

/*
 * =============================================================================
 * WhileLoop.java — while Loop
 * =============================================================================
 *
 * CONCEPT: while Loop
 * --------------------
 * Use a while loop when you DON'T know the number of iterations in advance,
 * and want to keep looping as long as a condition remains true.
 *
 * SYNTAX:
 *   while (condition) {
 *       // body
 *   }
 *
 * - Condition is checked BEFORE each iteration (pre-test loop)
 * - If condition is false from the start, the body NEVER executes
 * - You must update the condition inside the loop to avoid infinite loops
 *
 * WHEN TO USE while vs for:
 *   - for:   when you know the number of iterations
 *   - while: when the number of iterations depends on runtime conditions
 *            (e.g., user input, reading a file, waiting for an event)
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/WhileLoop.java
 *  $ java -cp out controlflow.WhileLoop
 * =============================================================================
 */
public class WhileLoop {

    public static void main(String[] args) {

        // ── BASIC WHILE LOOP ─────────────────────────────────────────────────
        System.out.println("=== Basic while Loop (1 to 5) ===");
        int i = 1;              // initialization (before the loop)

        while (i <= 5) {        // condition checked each iteration
            System.out.println("i = " + i);
            i++;                // update (inside the loop body)
        }
        // i is now 6 (still accessible — declared outside the loop)
        System.out.println("After loop, i = " + i);

        // ── WHILE WITH SENTINEL VALUE ────────────────────────────────────────
        System.out.println("\n=== Sum until negative value ===");
        int[] data = {5, 10, 3, -1, 8, 2};  // -1 is the sentinel (stop signal)
        int idx = 0;
        int total = 0;

        while (idx < data.length && data[idx] >= 0) {  // stop at negative value
            total += data[idx];
            idx++;
        }
        System.out.println("Sum = " + total + " (stopped at index " + idx + ")");

        // ── WHILE SIMULATING USER INPUT ───────────────────────────────────────
        System.out.println("\n=== Simulated: keep doubling until > 1000 ===");
        int value = 1;

        while (value <= 1000) {
            System.out.print(value + " ");
            value *= 2;   // keep doubling
        }
        System.out.println("\nFinal value: " + value);

        // ── WHILE WITH STRING PROCESSING ─────────────────────────────────────
        System.out.println("\n=== Count digits in a number ===");
        int num = 123456789;
        int count = 0;

        int temp = Math.abs(num);  // handle negative numbers
        if (temp == 0) {
            count = 1;  // 0 has one digit
        } else {
            while (temp > 0) {
                count++;
                temp /= 10;  // remove last digit
            }
        }
        System.out.println("Digits in " + num + " = " + count);

        // ── WHILE FOR COLLECTING DIGITS ───────────────────────────────────────
        System.out.println("\n=== Extract and reverse digits ===");
        int original = 12345;
        int reversed = 0;
        int n = original;

        while (n != 0) {
            int digit = n % 10;    // get last digit
            reversed = reversed * 10 + digit;  // build reversed number
            n /= 10;               // remove last digit
        }
        System.out.println("Original: " + original + ", Reversed: " + reversed);

        // ── CHECKING CONDITION ONCE (condition false from start) ──────────────
        System.out.println("\n=== While that never executes ===");
        int x = 10;
        while (x > 100) {   // false from the start
            System.out.println("This will never print!");
            x++;
        }
        System.out.println("x is " + x + " — loop body was skipped entirely");

        // ── WHILE WITH MULTIPLE CONDITIONS ────────────────────────────────────
        System.out.println("\n=== Binary search (while loop) ===");
        int[] sorted = {2, 5, 8, 12, 16, 23, 38, 56};
        int target = 23;
        int low = 0, high = sorted.length - 1, mid;
        boolean found = false;

        while (low <= high) {
            mid = (low + high) / 2;   // find the middle index
            if (sorted[mid] == target) {
                System.out.println("Found " + target + " at index " + mid);
                found = true;
                break;
            } else if (sorted[mid] < target) {
                low = mid + 1;    // search right half
            } else {
                high = mid - 1;   // search left half
            }
        }
        if (!found) {
            System.out.println(target + " not found");
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Basic while Loop (1 to 5) ===
 * i = 1
 * i = 2
 * i = 3
 * i = 4
 * i = 5
 * After loop, i = 6
 *
 * === Sum until negative value ===
 * Sum = 18 (stopped at index 3)
 *
 * === Simulated: keep doubling until > 1000 ===
 * 1 2 4 8 16 32 64 128 256 512
 * Final value: 1024
 *
 * === Count digits in a number ===
 * Digits in 123456789 = 9
 *
 * === Extract and reverse digits ===
 * Original: 12345, Reversed: 54321
 *
 * === While that never executes ===
 * x is 10 — loop body was skipped entirely
 *
 * === Binary search (while loop) ===
 * Found 23 at index 5
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Infinite loop — forgetting to update the condition variable:
 *    int i = 0;
 *    while (i < 5) {
 *        System.out.println(i); // forgot i++!
 *    } → runs forever!
 *
 * 2. Off-by-one: using < vs <=
 *
 * 3. Using = instead of == in the condition:
 *    while (x = 5) ← COMPILE ERROR in Java (but a common mistake in C/C++)
 *
 * 4. Not handling the case where the loop never executes
 *    (e.g., assuming at least one iteration happened)
 *
 * 5. Unintended exit: breaking out of the loop due to side effects
 */
