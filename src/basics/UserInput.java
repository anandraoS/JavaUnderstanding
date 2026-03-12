package basics;

import java.util.Scanner;  // Import Scanner class for reading user input

/*
 * =============================================================================
 * UserInput.java — Reading User Input with Scanner in Java
 * =============================================================================
 *
 * CONCEPT: User Input
 * -------------------
 * Java uses the Scanner class to read input from the user (or other sources).
 * Scanner is in the java.util package, so it must be imported.
 *
 * HOW SCANNER WORKS:
 *    Scanner reads input from a stream (System.in = keyboard by default).
 *    It reads tokens separated by whitespace (spaces, newlines, tabs).
 *
 * IMPORTANT SCANNER METHODS:
 *    nextLine()  → reads an entire line (including spaces) until Enter
 *    next()      → reads one token (word) up to the first whitespace
 *    nextInt()   → reads an integer
 *    nextDouble()→ reads a double
 *    nextLong()  → reads a long
 *    nextBoolean()→ reads a boolean (true/false)
 *    hasNextInt()→ checks if next token is an integer (useful for validation)
 *
 * COMMON PITFALL: The Scanner "newline" issue
 *    After nextInt()/nextDouble()/etc., the newline (\n) from pressing Enter
 *    is LEFT in the buffer. The NEXT nextLine() call will read that empty line!
 *    Fix: Call scanner.nextLine() to consume the leftover newline.
 *
 * NOTE FOR THIS DEMO:
 *    Since this file is run programmatically, we simulate some inputs to
 *    demonstrate the concepts without requiring live keyboard input.
 *    The interactive parts are wrapped in comments to show the pattern.
 *
 * HOW TO RUN:
 *  $ javac -d out src/basics/UserInput.java
 *  $ java -cp out basics.UserInput
 * =============================================================================
 */
public class UserInput {

    public static void main(String[] args) {

        // ── BASIC SCANNER SETUP ──────────────────────────────────────────────
        // System.in is the standard input stream (keyboard by default)
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Java User Input with Scanner ===");
        System.out.println("(This demo uses pre-supplied input for automation)");
        System.out.println();

        // ── READING DIFFERENT TYPES ───────────────────────────────────────────
        // In a real interactive program, you'd do:
        //
        //   System.out.print("Enter your name: ");
        //   String name = scanner.nextLine();
        //
        //   System.out.print("Enter your age: ");
        //   int age = scanner.nextInt();
        //
        //   System.out.print("Enter your GPA: ");
        //   double gpa = scanner.nextDouble();

        // Simulating via a string-based scanner for demo purposes:
        Scanner simInput = new Scanner("Alice\n25\n3.85\ntrue\nJava Programming\n");

        // Read a line (name including spaces)
        System.out.print("Enter your name: ");
        String name = simInput.nextLine();   // reads "Alice"
        System.out.println(name);

        // Read an integer (age)
        System.out.print("Enter your age: ");
        int age = simInput.nextInt();         // reads 25
        System.out.println(age);

        // Read a double (GPA)
        System.out.print("Enter your GPA: ");
        double gpa = simInput.nextDouble();   // reads 3.85
        System.out.println(gpa);

        // Read a boolean
        System.out.print("Is Java fun? (true/false): ");
        boolean isFun = simInput.nextBoolean();  // reads true
        System.out.println(isFun);

        // PITFALL: After nextBoolean(), there's a leftover newline in the buffer.
        simInput.nextLine();   // ← CONSUME the leftover newline!

        // Now nextLine() will correctly read the next full line
        System.out.print("Enter your favourite subject: ");
        String subject = simInput.nextLine();  // reads "Java Programming"
        System.out.println(subject);

        System.out.println();
        System.out.println("--- Summary ---");
        System.out.println("Name:     " + name);
        System.out.println("Age:      " + age);
        System.out.printf("GPA:      %.2f%n", gpa);
        System.out.println("Fun:      " + isFun);
        System.out.println("Subject:  " + subject);

        // ── NEXT VS NEXTLINE ─────────────────────────────────────────────────
        System.out.println("\n=== next() vs nextLine() ===");

        Scanner wordScan = new Scanner("Hello World\nFoo Bar Baz");

        String word1 = wordScan.next();       // reads only "Hello" (stops at space)
        System.out.println("next():     " + word1);       // Hello

        String rest = wordScan.nextLine();    // reads " World" (rest of first line)
        System.out.println("nextLine(): [" + rest + "]"); // [ World]

        String fullLine = wordScan.nextLine();  // reads full second line
        System.out.println("nextLine(): " + fullLine);    // Foo Bar Baz

        // ── INPUT VALIDATION ─────────────────────────────────────────────────
        System.out.println("\n=== Input Validation ===");

        Scanner validateScan = new Scanner("abc\n42\n");

        System.out.println("Validating integer input:");
        while (!validateScan.hasNextInt()) {
            // If not an integer, skip the invalid token and warn the user
            String invalid = validateScan.next();
            System.out.println("  '" + invalid + "' is not a valid integer. Try again.");
        }
        int validInt = validateScan.nextInt();
        System.out.println("  Valid integer received: " + validInt);

        // ── ALWAYS CLOSE THE SCANNER ─────────────────────────────────────────
        // Closing the scanner frees the underlying resource.
        // NOTE: Closing scanner on System.in also closes System.in, so only
        //       close it at the very end of your program.
        scanner.close();
        simInput.close();
        wordScan.close();
        validateScan.close();

        System.out.println("\n=== Scanner Demo Complete ===");

        // ── INTERACTIVE PATTERN (reference, not executed) ────────────────────
        System.out.println("\n--- Interactive Pattern (how to use in your own program) ---");
        System.out.println("Scanner sc = new Scanner(System.in);");
        System.out.println("System.out.print(\"Enter name: \");");
        System.out.println("String name = sc.nextLine();");
        System.out.println("System.out.print(\"Enter age: \");");
        System.out.println("int age = sc.nextInt();");
        System.out.println("sc.nextLine(); // consume newline after nextInt()");
        System.out.println("sc.close();");
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Java User Input with Scanner ===
 * (This demo uses pre-supplied input for automation)
 *
 * Enter your name: Alice
 * Enter your age: 25
 * Enter your GPA: 3.85
 * Is Java fun? (true/false): true
 * Enter your favourite subject: Java Programming
 *
 * --- Summary ---
 * Name:     Alice
 * Age:      25
 * GPA:      3.85
 * Fun:      true
 * Subject:  Java Programming
 *
 * === next() vs nextLine() ===
 * next():     Hello
 * nextLine(): [ World]
 * nextLine(): Foo Bar Baz
 *
 * === Input Validation ===
 * Validating integer input:
 *   'abc' is not a valid integer. Try again.
 *   Valid integer received: 42
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting to consume the leftover newline after nextInt()/nextDouble():
 *    int x = sc.nextInt();
 *    String line = sc.nextLine(); ← reads empty string (the leftover \n)!
 *    Fix: add sc.nextLine(); before reading the string.
 *
 * 2. InputMismatchException: entering wrong type for nextInt() etc.
 *    Fix: Use hasNextInt() first, or wrap in try-catch.
 *
 * 3. Not importing Scanner:
 *    import java.util.Scanner; ← required at top of file
 *
 * 4. Not closing Scanner: minor resource leak (OK for small programs).
 *
 * 5. NoSuchElementException: calling next() when no more input available.
 *    Fix: Use hasNext() / hasNextLine() to check before reading.
 */
