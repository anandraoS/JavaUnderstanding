package controlflow;

/*
 * =============================================================================
 * DoWhileLoop.java — do-while Loop
 * =============================================================================
 *
 * CONCEPT: do-while Loop
 * -----------------------
 * The do-while loop is like the while loop, but the condition is checked
 * AFTER the loop body executes — so the body ALWAYS runs at least once.
 *
 * SYNTAX:
 *   do {
 *       // body
 *   } while (condition);   ← semicolon is required!
 *
 * KEY DIFFERENCE from while:
 *   - while:    condition checked BEFORE body (may execute 0 times)
 *   - do-while: condition checked AFTER body  (executes AT LEAST 1 time)
 *
 * WHEN TO USE:
 *   - Menu-driven programs (always show the menu at least once)
 *   - Input validation (always ask at least once)
 *   - Any situation where the action must happen before you check to continue
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/DoWhileLoop.java
 *  $ java -cp out controlflow.DoWhileLoop
 * =============================================================================
 */
public class DoWhileLoop {

    public static void main(String[] args) {

        // ── BASIC DO-WHILE ───────────────────────────────────────────────────
        System.out.println("=== Basic do-while (1 to 5) ===");
        int i = 1;

        do {
            System.out.println("i = " + i);
            i++;
        } while (i <= 5);    // condition checked AFTER each iteration

        // ── DO-WHILE VS WHILE (key difference) ───────────────────────────────
        System.out.println("\n=== do-while: body always runs at least once ===");

        int x = 100;    // condition is already false

        // while: body skipped entirely
        System.out.println("while loop:");
        while (x < 10) {   // 100 < 10 is false → never enters
            System.out.println("  Inside while (never printed)");
        }
        System.out.println("  While loop done. x = " + x);

        // do-while: body runs once even though condition is false
        System.out.println("do-while loop:");
        do {
            System.out.println("  Inside do-while (runs ONCE even though x=" + x + ")");
        } while (x < 10);   // 100 < 10 is false → exits after first iteration
        System.out.println("  Do-while done.");

        // ── SIMULATED MENU-DRIVEN PROGRAM ─────────────────────────────────────
        System.out.println("\n=== Simulated Menu Program ===");
        // In real code: int choice = scanner.nextInt();
        // Here we simulate a sequence of choices:
        int[] menuChoices = {1, 2, 3, 0};  // 0 = exit
        int choiceIndex = 0;

        do {
            System.out.println("  --- MENU ---");
            System.out.println("  1. Say Hello");
            System.out.println("  2. Print Date");
            System.out.println("  3. Random Number");
            System.out.println("  0. Exit");

            int choice = menuChoices[choiceIndex++];
            System.out.println("  Your choice: " + choice);

            switch (choice) {
                case 1 -> System.out.println("  Hello!");
                case 2 -> System.out.println("  Today is a learning day!");
                case 3 -> System.out.println("  Random: " + (int)(Math.random() * 100));
                case 0 -> System.out.println("  Goodbye!");
                default -> System.out.println("  Invalid choice.");
            }
            System.out.println();

        } while (menuChoices[choiceIndex - 1] != 0);  // repeat until user chooses 0

        // ── INPUT VALIDATION PATTERN ──────────────────────────────────────────
        System.out.println("=== Input Validation with do-while ===");
        // Simulate: ask user for a number between 1 and 10
        int[] inputs = {-5, 0, 15, 7};  // -5, 0, 15 are invalid; 7 is valid
        int inputIdx = 0;
        int validInput;

        do {
            validInput = inputs[inputIdx++];
            System.out.println("  User entered: " + validInput);
            if (validInput < 1 || validInput > 10) {
                System.out.println("  Invalid! Must be between 1 and 10. Please try again.");
            }
        } while (validInput < 1 || validInput > 10);

        System.out.println("  Valid input accepted: " + validInput);

        // ── SUM OF DIGITS USING DO-WHILE ──────────────────────────────────────
        System.out.println("\n=== Sum of Digits ===");
        int num = 4567;
        int digitSum = 0;
        int temp = Math.abs(num);

        do {
            digitSum += temp % 10;  // add last digit
            temp /= 10;             // remove last digit
        } while (temp > 0);

        System.out.println("Sum of digits in " + num + " = " + digitSum);
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Basic do-while (1 to 5) ===
 * i = 1
 * i = 2
 * i = 3
 * i = 4
 * i = 5
 *
 * === do-while: body always runs at least once ===
 * while loop:
 *   While loop done. x = 100
 * do-while loop:
 *   Inside do-while (runs ONCE even though x=100)
 *   Do-while done.
 *
 * === Simulated Menu Program ===
 * (menu shown, choices 1, 2, 3 processed, then 0 exits)
 *
 * === Input Validation with do-while ===
 *   User entered: -5
 *   Invalid! ...
 *   User entered: 0
 *   Invalid! ...
 *   User entered: 15
 *   Invalid! ...
 *   User entered: 7
 *   Valid input accepted: 7
 *
 * === Sum of Digits ===
 * Sum of digits in 4567 = 22
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Missing semicolon after while condition:
 *    do { ... } while (condition)  ← COMPILE ERROR, need semicolon!
 *    do { ... } while (condition); ← correct
 *
 * 2. Thinking do-while checks condition first (it doesn't!)
 *
 * 3. Infinite loop — if condition never becomes false:
 *    do { System.out.println("x"); } while (true); ← runs forever
 *
 * 4. Using do-while when the body should sometimes not execute
 *    — use while instead.
 */
