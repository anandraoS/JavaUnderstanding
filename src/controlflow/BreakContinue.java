package controlflow;

/*
 * =============================================================================
 * BreakContinue.java — break, continue, and Labeled Statements
 * =============================================================================
 *
 * CONCEPT: Loop Control Statements
 * ----------------------------------
 * break    → immediately exits the nearest enclosing loop or switch
 * continue → skips the rest of the current iteration, jumps to next
 * return   → exits the current method entirely
 *
 * LABELED STATEMENTS:
 *   You can label a loop and use break/continue with the label to exit/skip
 *   outer loops from inside nested loops.
 *
 *   Syntax:  LABEL_NAME: for (...) { ... }
 *            break LABEL_NAME;
 *            continue LABEL_NAME;
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/BreakContinue.java
 *  $ java -cp out controlflow.BreakContinue
 * =============================================================================
 */
public class BreakContinue {

    public static void main(String[] args) {

        // ── BREAK IN FOR LOOP ────────────────────────────────────────────────
        System.out.println("=== break in for loop ===");

        for (int i = 1; i <= 10; i++) {
            if (i == 6) {
                System.out.println("Breaking at i=" + i);
                break;   // immediately exit the loop
            }
            System.out.println("i = " + i);
        }
        System.out.println("Loop ended (via break)");

        // ── BREAK IN WHILE LOOP ───────────────────────────────────────────────
        System.out.println("\n=== break in while loop ===");

        int n = 1;
        while (true) {    // "infinite" loop, controlled by break
            System.out.print(n + " ");
            if (n >= 5) break;
            n++;
        }
        System.out.println("\nDone");

        // ── CONTINUE IN FOR LOOP ──────────────────────────────────────────────
        System.out.println("\n=== continue: skip even numbers ===");

        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;   // skip the rest of this iteration for even numbers
            }
            System.out.print(i + " ");   // only odd numbers reach here
        }
        System.out.println();

        // ── CONTINUE: SKIP SPECIFIC VALUE ────────────────────────────────────
        System.out.println("\n=== continue: skip 'null' entries ===");

        String[] names = {"Alice", null, "Bob", null, "Charlie"};
        for (String name : names) {
            if (name == null) {
                continue;   // skip null entries
            }
            System.out.println("Hello, " + name + "!");
        }

        // ── BREAK IN NESTED LOOPS ────────────────────────────────────────────
        System.out.println("\n=== break in nested loops (only exits inner) ===");

        for (int i = 1; i <= 3; i++) {           // outer loop
            System.out.print("Row " + i + ": ");
            for (int j = 1; j <= 5; j++) {        // inner loop
                if (j == 3) {
                    break;   // only exits the INNER loop, outer continues!
                }
                System.out.print(j + " ");
            }
            System.out.println();
        }

        // ── LABELED BREAK (exit outer loop) ──────────────────────────────────
        System.out.println("\n=== labeled break (exit outer loop) ===");

        outerLoop:                              // label for the outer loop
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i == 2 && j == 2) {
                    System.out.println("Breaking out of both loops at i=" + i + ", j=" + j);
                    break outerLoop;            // exits the OUTER loop entirely
                }
                System.out.println("  i=" + i + ", j=" + j);
            }
        }
        System.out.println("Exited outer loop via labeled break");

        // ── LABELED CONTINUE ─────────────────────────────────────────────────
        System.out.println("\n=== labeled continue (skip outer iteration) ===");

        outer:
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (j == 2) {
                    continue outer;  // skip to NEXT iteration of OUTER loop
                }
                System.out.println("  i=" + i + ", j=" + j);
            }
        }

        // ── PRACTICAL: FIND FIRST PRIME > 20 ─────────────────────────────────
        System.out.println("\n=== Find first prime > 20 ===");

        int candidate = 21;
        int firstPrime = -1;

        searchLoop:
        while (candidate < 100) {
            boolean isPrime = true;
            for (int divisor = 2; divisor <= Math.sqrt(candidate); divisor++) {
                if (candidate % divisor == 0) {
                    isPrime = false;
                    break;    // no need to check more divisors
                }
            }
            if (isPrime) {
                firstPrime = candidate;
                break searchLoop;   // found it — exit the while loop
            }
            candidate++;
        }

        System.out.println("First prime > 20 is: " + firstPrime);   // 23
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === break in for loop ===
 * i = 1
 * i = 2
 * i = 3
 * i = 4
 * i = 5
 * Breaking at i=6
 * Loop ended (via break)
 *
 * === break in while loop ===
 * 1 2 3 4 5
 * Done
 *
 * === continue: skip even numbers ===
 * 1 3 5 7 9
 *
 * === continue: skip 'null' entries ===
 * Hello, Alice!
 * Hello, Bob!
 * Hello, Charlie!
 *
 * === break in nested loops (only exits inner) ===
 * Row 1: 1 2
 * Row 2: 1 2
 * Row 3: 1 2
 *
 * === labeled break (exit outer loop) ===
 *   i=1, j=1
 *   i=1, j=2
 *   i=1, j=3
 *   i=2, j=1
 * Breaking out of both loops at i=2, j=2
 * Exited outer loop via labeled break
 *
 * === labeled continue (skip outer iteration) ===
 *   i=1, j=1
 *   i=2, j=1
 *   i=3, j=1
 *
 * === Find first prime > 20 ===
 * First prime > 20 is: 23
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using break in nested loops expecting it to exit ALL loops
 *    → break only exits the innermost loop! Use labeled break for outer loops.
 *
 * 2. Putting continue/break outside a loop → COMPILE ERROR
 *
 * 3. Forgetting that break in switch only exits the switch,
 *    not any surrounding loop.
 *
 * 4. Using goto-style labeled breaks excessively → hard to read.
 *    Consider refactoring into a separate method and using return instead.
 *
 * 5. continue in a for loop still executes the update (i++):
 *    for (int i=0; i<5; i++) {
 *        if (i==2) continue;  // i++ still runs → skips body for i=2 only
 *    }
 */
