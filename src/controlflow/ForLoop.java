package controlflow;

/*
 * =============================================================================
 * ForLoop.java — for Loop and Enhanced for Loop
 * =============================================================================
 *
 * CONCEPT: for Loop
 * -----------------
 * Use a for loop when you know (or can calculate) the number of iterations
 * in advance.
 *
 * SYNTAX:
 *   for (initialization; condition; update) {
 *       // body
 *   }
 *
 *   1. initialization: runs ONCE before the loop starts (e.g., int i = 0)
 *   2. condition:      checked BEFORE each iteration; loop stops when false
 *   3. update:         runs AFTER each iteration (e.g., i++)
 *
 * ENHANCED FOR LOOP (for-each):
 *   for (Type element : collection) {
 *       // use element
 *   }
 *   - Simpler syntax for iterating over arrays and collections
 *   - Cannot modify the index or loop variable to access index
 *   - Read-only iteration
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/ForLoop.java
 *  $ java -cp out controlflow.ForLoop
 * =============================================================================
 */
public class ForLoop {

    public static void main(String[] args) {

        // ── BASIC FOR LOOP ───────────────────────────────────────────────────
        System.out.println("=== Basic for Loop (1 to 5) ===");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
        // After the loop, 'i' is no longer accessible (it was declared inside the for)

        // ── LOOP COUNTING DOWN ────────────────────────────────────────────────
        System.out.println("\n=== Countdown (5 to 1) ===");
        for (int i = 5; i >= 1; i--) {   // decrement
            System.out.print(i + " ");
        }
        System.out.println("Go!");

        // ── LOOP WITH STEP ────────────────────────────────────────────────────
        System.out.println("\n=== Even Numbers (0 to 10) ===");
        for (int i = 0; i <= 10; i += 2) {   // step by 2
            System.out.print(i + " ");
        }
        System.out.println();

        // ── SUM USING A LOOP ─────────────────────────────────────────────────
        System.out.println("\n=== Sum of 1 to 100 ===");
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += i;   // accumulate sum
        }
        System.out.println("Sum = " + sum);  // 5050

        // ── MULTIPLICATION TABLE ─────────────────────────────────────────────
        System.out.println("\n=== Multiplication Table for 5 ===");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("5 x %2d = %2d%n", i, 5 * i);
        }

        // ── NESTED FOR LOOPS ─────────────────────────────────────────────────
        System.out.println("\n=== Nested Loops (Pattern) ===");

        // Outer loop controls rows, inner loop controls columns
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print("* ");
            }
            System.out.println();  // new line after each row
        }

        // ── LOOP WITH ARRAY ───────────────────────────────────────────────────
        System.out.println("\n=== Loop over Array ===");
        int[] numbers = {10, 20, 30, 40, 50};

        // Traditional for loop (gives access to index)
        System.out.println("Traditional for:");
        for (int i = 0; i < numbers.length; i++) {
            System.out.println("  numbers[" + i + "] = " + numbers[i]);
        }

        // ── ENHANCED FOR LOOP (for-each) ─────────────────────────────────────
        System.out.println("\n=== Enhanced for Loop (for-each) ===");
        String[] fruits = {"Apple", "Banana", "Cherry", "Mango"};

        for (String fruit : fruits) {
            System.out.println("  Fruit: " + fruit);
        }

        // Enhanced for with array of primitives
        int[] primes = {2, 3, 5, 7, 11, 13};
        System.out.print("Primes: ");
        for (int prime : primes) {
            System.out.print(prime + " ");
        }
        System.out.println();

        // ── INFINITE LOOP WITH BREAK ─────────────────────────────────────────
        System.out.println("\n=== Controlled Infinite Loop ===");
        int counter = 0;
        for (;;) {      // empty for(;;) = infinite loop
            counter++;
            if (counter >= 5) {
                break;  // exit the loop
            }
        }
        System.out.println("Counter stopped at: " + counter);

        // ── MULTIPLE VARIABLES IN FOR ─────────────────────────────────────────
        System.out.println("\n=== Multiple Variables in For ===");
        for (int i = 0, j = 10; i < j; i++, j--) {
            System.out.println("i=" + i + ", j=" + j);
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic for Loop (1 to 5) ===
 * i = 1
 * i = 2
 * i = 3
 * i = 4
 * i = 5
 *
 * === Countdown (5 to 1) ===
 * 5 4 3 2 1 Go!
 *
 * === Sum of 1 to 100 ===
 * Sum = 5050
 *
 * === Nested Loops (Pattern) ===
 * *
 * * *
 * * * *
 * * * * *
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Off-by-one errors: i < n vs i <= n (very common!)
 *    for (int i = 1; i < 5; i++) → runs 4 times (1,2,3,4)
 *    for (int i = 1; i <= 5; i++) → runs 5 times (1,2,3,4,5)
 *
 * 2. Modifying loop variable inside loop body (confusing):
 *    for (int i=0; i<10; i++) { i += 2; } ← i advances by 3 each time!
 *
 * 3. Trying to modify collection while using enhanced for:
 *    for (String s : list) { list.remove(s); } ← ConcurrentModificationException!
 *    Use Iterator.remove() instead.
 *
 * 4. Infinite loop: wrong condition or missing update:
 *    for (int i=0; i>=0; i++) { ... } ← never ends!
 *
 * 5. Using enhanced for when you need the index → use traditional for instead.
 */
