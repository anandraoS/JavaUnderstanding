package controlflow;

/*
 * =============================================================================
 * IfElse.java — if, else if, else, and Ternary Operator
 * =============================================================================
 *
 * CONCEPT: Conditional Statements
 * --------------------------------
 * Conditional statements allow a program to make decisions and execute
 * different code based on whether conditions are true or false.
 *
 * SYNTAX FORMS:
 *   1. if (condition) { ... }
 *   2. if (condition) { ... } else { ... }
 *   3. if (c1) { ... } else if (c2) { ... } else { ... }
 *   4. ternary: condition ? valueIfTrue : valueIfFalse
 *
 * RULES:
 *   - Condition MUST be a boolean expression (true/false)
 *   - Java does NOT allow: if (x = 5) — use == for comparison
 *   - Curly braces {} are optional for single-statement blocks, but ALWAYS
 *     use them to avoid bugs!
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/IfElse.java
 *  $ java -cp out controlflow.IfElse
 * =============================================================================
 */
public class IfElse {

    public static void main(String[] args) {

        // ── BASIC IF ─────────────────────────────────────────────────────────
        System.out.println("=== Basic if ===");
        int temperature = 30;

        if (temperature > 25) {   // condition: is it hotter than 25°C?
            System.out.println("It's hot outside!");
        }
        // If temperature <= 25, nothing prints (the block is skipped)

        // ── IF-ELSE ──────────────────────────────────────────────────────────
        System.out.println("\n=== if-else ===");
        int number = -7;

        if (number >= 0) {
            System.out.println(number + " is positive (or zero)");
        } else {
            System.out.println(number + " is negative");
        }

        // ── IF-ELSE IF-ELSE ───────────────────────────────────────────────────
        System.out.println("\n=== if-else if-else (Grade Calculator) ===");
        int marks = 72;

        if (marks >= 90) {
            System.out.println("Grade: A (Excellent)");
        } else if (marks >= 80) {
            System.out.println("Grade: B (Very Good)");
        } else if (marks >= 70) {
            System.out.println("Grade: C (Good)");      // This runs: 72 is >= 70
        } else if (marks >= 60) {
            System.out.println("Grade: D (Pass)");
        } else {
            System.out.println("Grade: F (Fail)");
        }

        // ── NESTED IF ────────────────────────────────────────────────────────
        System.out.println("\n=== Nested if ===");
        int age = 22;
        boolean hasLicense = true;

        if (age >= 18) {
            System.out.println("Age is 18+");
            if (hasLicense) {
                System.out.println("Can drive!");   // Both conditions true
            } else {
                System.out.println("Needs a license to drive");
            }
        } else {
            System.out.println("Too young to drive");
        }

        // ── TERNARY OPERATOR ─────────────────────────────────────────────────
        System.out.println("\n=== Ternary Operator ===");

        int a = 10, b = 20;
        int max = (a > b) ? a : b;   // inline if-else: pick the larger value
        System.out.println("Max of " + a + " and " + b + " = " + max);

        String result = (marks >= 60) ? "Pass" : "Fail";
        System.out.println("Marks " + marks + " → " + result);

        // ── COMPOUND CONDITIONS ───────────────────────────────────────────────
        System.out.println("\n=== Compound Conditions ===");
        int x = 15;

        // && (AND): both conditions must be true
        if (x > 10 && x < 20) {
            System.out.println(x + " is between 10 and 20");
        }

        // || (OR): at least one condition must be true
        if (x == 5 || x == 15) {
            System.out.println(x + " is either 5 or 15");
        }

        // ! (NOT): inverts the boolean
        boolean isEmpty = false;
        if (!isEmpty) {
            System.out.println("The container is not empty");
        }

        // ── NULL CHECK PATTERN ────────────────────────────────────────────────
        System.out.println("\n=== Null Check Pattern ===");
        String name = null;

        // Check for null BEFORE calling methods to avoid NullPointerException
        if (name != null && name.length() > 0) {
            System.out.println("Name: " + name);
        } else {
            System.out.println("Name is null or empty");
        }

        // ── AVOID DANGLING ELSE ISSUE ─────────────────────────────────────────
        // Always use curly braces {} to make intent clear!
        System.out.println("\n=== Always Use Braces ===");
        int val = 5;

        // RISKY (no braces — else matches the nearest if)
        // if (val > 0)
        //     if (val > 10)
        //         System.out.println("Greater than 10");
        //     else
        //         System.out.println("This else belongs to inner if!");

        // SAFE (with braces)
        if (val > 0) {
            if (val > 10) {
                System.out.println("Greater than 10");
            }
        } else {
            System.out.println("Non-positive (outer else — correctly matched)");
        }

        System.out.println("val=" + val + " — reached end of if-else demo");
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Basic if ===
 * It's hot outside!
 *
 * === if-else ===
 * -7 is negative
 *
 * === if-else if-else (Grade Calculator) ===
 * Grade: C (Good)
 *
 * === Nested if ===
 * Age is 18+
 * Can drive!
 *
 * === Ternary Operator ===
 * Max of 10 and 20 = 20
 * Marks 72 → Pass
 *
 * === Compound Conditions ===
 * 15 is between 10 and 20
 * 15 is either 5 or 15
 * The container is not empty
 *
 * === Null Check Pattern ===
 * Name is null or empty
 *
 * === Always Use Braces ===
 * val=5 — reached end of if-else demo
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using = instead of == for comparison (Java catches this as a compile error)
 * 2. Missing else clause for important cases
 * 3. Dangling else — always use {} to be clear
 * 4. Not checking null before calling methods → NullPointerException
 * 5. Over-nesting if statements — consider using switch or early returns instead
 */
