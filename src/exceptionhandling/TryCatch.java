package exceptionhandling;

/*
 * =============================================================================
 * TryCatch.java — Exception Handling Basics with try-catch-finally
 * =============================================================================
 *
 * CONCEPT: Exceptions
 * --------------------
 * An exception is an event that disrupts the normal flow of a program.
 * Java uses exception handling to gracefully deal with runtime errors.
 *
 * EXCEPTION HIERARCHY:
 *   Throwable
 *   ├── Error          (serious JVM problems — don't catch these usually)
 *   │   ├── OutOfMemoryError
 *   │   └── StackOverflowError
 *   └── Exception
 *       ├── IOException         (checked — must handle or declare)
 *       ├── SQLException        (checked)
 *       └── RuntimeException    (unchecked — optional to handle)
 *           ├── NullPointerException
 *           ├── ArrayIndexOutOfBoundsException
 *           ├── NumberFormatException
 *           ├── ClassCastException
 *           └── ArithmeticException
 *
 * CHECKED vs UNCHECKED EXCEPTIONS:
 *   Checked:   must be handled with try-catch OR declared with throws
 *              (compile-time enforcement — e.g., IOException, SQLException)
 *   Unchecked: RuntimeException subclasses — optional to handle
 *              (e.g., NullPointerException, ArrayIndexOutOfBoundsException)
 *
 * SYNTAX:
 *   try {
 *       // risky code
 *   } catch (ExceptionType1 e) {
 *       // handle ExceptionType1
 *   } catch (ExceptionType2 e) {
 *       // handle ExceptionType2
 *   } finally {
 *       // ALWAYS runs (cleanup code)
 *   }
 *
 * HOW TO RUN:
 *  $ javac -d out src/exceptionhandling/TryCatch.java
 *  $ java -cp out exceptionhandling.TryCatch
 * =============================================================================
 */
public class TryCatch {

    public static void main(String[] args) {

        // ── BASIC TRY-CATCH ───────────────────────────────────────────────────
        System.out.println("=== Basic try-catch ===");

        try {
            int result = 10 / 0;   // ArithmeticException: division by zero
            System.out.println("Result: " + result);  // never reached
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }
        System.out.println("Program continues after exception handling.");

        // ── MULTIPLE CATCH BLOCKS ─────────────────────────────────────────────
        System.out.println("\n=== Multiple catch blocks ===");

        String[] scenarios = {"10", "abc", null};
        for (String s : scenarios) {
            try {
                int value = Integer.parseInt(s);   // may throw NumberFormatException
                System.out.println("Parsed: " + value);

                if (s.length() > 2) {   // NullPointerException if s is null
                    System.out.println("Long string!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Cannot parse '" + s + "': " + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("Null string — cannot parse");
            }
        }

        // ── MULTI-CATCH (Java 7+) ─────────────────────────────────────────────
        System.out.println("\n=== Multi-catch (Java 7+) ===");

        try {
            String text = null;
            int[] arr = new int[3];
            arr[5] = 10;           // ArrayIndexOutOfBoundsException
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ── CATCHING PARENT EXCEPTION ─────────────────────────────────────────
        System.out.println("\n=== Catching parent Exception class ===");
        try {
            String s = null;
            s.length();   // NullPointerException
        } catch (RuntimeException e) {
            // Catches any RuntimeException subclass (NullPointerException, etc.)
            System.out.println("RuntimeException caught: " + e.getClass().getSimpleName());
        }

        // ── EXCEPTION INFORMATION ─────────────────────────────────────────────
        System.out.println("\n=== Exception Information ===");
        try {
            int[] arr = {1, 2, 3};
            arr[10] = 99;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("getMessage():   " + e.getMessage());
            System.out.println("getClass():     " + e.getClass().getName());
            System.out.println("toString():     " + e.toString());
            // e.printStackTrace(); // prints full stack trace — very useful for debugging
        }

        // ── FINALLY BLOCK ─────────────────────────────────────────────────────
        System.out.println("\n=== Finally block ===");

        for (int divisor : new int[]{2, 0}) {
            System.out.println("--- Trying to divide by " + divisor + " ---");
            try {
                int result = 10 / divisor;
                System.out.println("Result: " + result);
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                System.out.println("Finally always runs!");  // runs whether or not exception occurred
            }
        }

        // ── TRY-WITH-RESOURCES (Java 7+) ─────────────────────────────────────
        System.out.println("\n=== Try-with-resources ===");
        // Resources that implement AutoCloseable are automatically closed
        // Great for file I/O, database connections, etc.
        try (AutoCloseableDemo resource = new AutoCloseableDemo("DemoResource")) {
            System.out.println("Using resource: " + resource.getData());
            // resource.close() is called AUTOMATICALLY when block exits
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println("Resource is automatically closed.");

        // ── NESTED TRY-CATCH ──────────────────────────────────────────────────
        System.out.println("\n=== Nested try-catch ===");
        try {
            System.out.println("Outer try");
            try {
                System.out.println("Inner try");
                throw new RuntimeException("Inner exception");
            } catch (RuntimeException e) {
                System.out.println("Inner catch: " + e.getMessage());
                throw new RuntimeException("Re-thrown exception");  // rethrow!
            }
        } catch (RuntimeException e) {
            System.out.println("Outer catch: " + e.getMessage());
        } finally {
            System.out.println("Outer finally");
        }
    }
}

// Demo class implementing AutoCloseable for try-with-resources
class AutoCloseableDemo implements AutoCloseable {
    private String name;

    AutoCloseableDemo(String name) {
        this.name = name;
        System.out.println("Resource opened: " + name);
    }

    String getData() { return "data from " + name; }

    @Override
    public void close() throws Exception {
        System.out.println("Resource automatically closed: " + name);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic try-catch ===
 * Caught ArithmeticException: / by zero
 * Program continues after exception handling.
 *
 * === Finally block ===
 * --- Trying to divide by 2 ---
 * Result: 5
 * Finally always runs!
 * --- Trying to divide by 0 ---
 * Error: / by zero
 * Finally always runs!
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Catching Exception/Throwable too broadly — hides real bugs:
 *    catch (Exception e) { } // swallowed! <- bad practice
 *    Catch the most specific exception possible.
 *
 * 2. Empty catch blocks — silently ignoring exceptions:
 *    catch (IOException e) {} ← very bad! At least log it.
 *
 * 3. Catch blocks in wrong order — more specific must come BEFORE more general:
 *    catch (Exception e) {} first, then catch (IOException e) {} ← COMPILE ERROR
 *    Put specific exceptions first, general ones last.
 *
 * 4. Forgetting that finally always runs — even with return in try/catch:
 *    try { return 1; } finally { return 2; } ← returns 2! (overrides try return)
 *
 * 5. Using exceptions for normal flow control — expensive! Exceptions are for errors.
 */
