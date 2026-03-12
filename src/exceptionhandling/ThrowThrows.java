package exceptionhandling;

/*
 * =============================================================================
 * ThrowThrows.java — throw vs throws Keywords
 * =============================================================================
 *
 * CONCEPT: throw and throws
 * --------------------------
 * throw:   used to MANUALLY throw an exception object
 *          Syntax: throw new ExceptionType("message");
 *
 * throws:  declares that a method MAY throw checked exceptions
 *          Callers must handle these exceptions
 *          Syntax: void methodName() throws ExceptionType1, ExceptionType2
 *
 * WHEN TO USE:
 *   throw  → inside a method when you detect an error condition
 *   throws → on the method signature when you want to propagate a checked exception
 *
 * HOW TO RUN:
 *  $ javac -d out src/exceptionhandling/ThrowThrows.java
 *  $ java -cp out exceptionhandling.ThrowThrows
 * =============================================================================
 */
public class ThrowThrows {

    // ── THROWS: method declares it can throw a checked exception ─────────────
    // Callers MUST handle this with try-catch or propagate with throws
    static int divide(int a, int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");   // throw!
        }
        return a / b;
    }

    // ── THROWS with multiple exceptions ──────────────────────────────────────
    static String processInput(String input) throws IllegalArgumentException, NullPointerException {
        if (input == null) {
            throw new NullPointerException("Input cannot be null");
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        return input.toUpperCase();
    }

    // ── THROWING and RE-THROWING ──────────────────────────────────────────────
    static void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age: " + age + ". Must be 0-150.");
        }
        System.out.println("Valid age: " + age);
    }

    // ── CHECKED EXCEPTION with throws ────────────────────────────────────────
    // java.io.IOException is checked — must be declared or caught
    static void simulateFileRead(String filename) throws java.io.IOException {
        if (filename == null || filename.isEmpty()) {
            throw new java.io.IOException("Filename cannot be empty");
        }
        if (!filename.endsWith(".txt")) {
            throw new java.io.IOException("Only .txt files supported: " + filename);
        }
        System.out.println("File '" + filename + "' read successfully (simulated).");
    }

    public static void main(String[] args) {

        // ── USING A METHOD THAT throws ────────────────────────────────────────
        System.out.println("=== throw and throws ===");

        try {
            int result = divide(10, 2);
            System.out.println("10 / 2 = " + result);

            divide(10, 0);   // this will throw
        } catch (ArithmeticException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // ── MULTIPLE throws ───────────────────────────────────────────────────
        System.out.println("\n=== Multiple throws ===");

        String[] inputs = {"Hello", "", null};
        for (String input : inputs) {
            try {
                String result = processInput(input);
                System.out.println("Processed: " + result);
            } catch (NullPointerException e) {
                System.out.println("NullPointerException: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("IllegalArgumentException: " + e.getMessage());
            }
        }

        // ── THROW IN VALIDATION ───────────────────────────────────────────────
        System.out.println("\n=== Throw in Validation ===");

        int[] ages = {25, -5, 200, 0};
        for (int age : ages) {
            try {
                validateAge(age);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // ── CHECKED EXCEPTION (throws IOException) ────────────────────────────
        System.out.println("\n=== Checked Exception (throws IOException) ===");

        String[] filenames = {"data.txt", "image.png", ""};
        for (String filename : filenames) {
            try {
                simulateFileRead(filename);
            } catch (java.io.IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }

        // ── RETHROWING EXCEPTIONS ─────────────────────────────────────────────
        System.out.println("\n=== Rethrowing Exceptions ===");

        try {
            rethrowExample();
        } catch (Exception e) {
            System.out.println("Final catch: " + e.getMessage());
        }
    }

    static void rethrowExample() {
        try {
            throw new RuntimeException("Original exception");
        } catch (RuntimeException e) {
            System.out.println("Caught in inner method, rethrowing...");
            throw e;   // rethrow the same exception to the caller
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === throw and throws ===
 * 10 / 2 = 5
 * Caught: Cannot divide by zero
 *
 * === Multiple throws ===
 * Processed: HELLO
 * IllegalArgumentException: Input cannot be empty
 * NullPointerException: Input cannot be null
 *
 * === Throw in Validation ===
 * Valid age: 25
 * Error: Invalid age: -5. Must be 0-150.
 * Error: Invalid age: 200. Must be 0-150.
 * Valid age: 0
 *
 * === Checked Exception ===
 * File 'data.txt' read successfully (simulated).
 * IOException: Only .txt files supported: image.png
 * IOException: Filename cannot be empty
 *
 * === Rethrowing Exceptions ===
 * Caught in inner method, rethrowing...
 * Final catch: Original exception
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Confusing throw and throws:
 *    throw  = action (throw this specific exception object)
 *    throws = declaration (this method may throw this type of exception)
 *
 * 2. throw without 'new': throw RuntimeException("msg"); ← COMPILE ERROR
 *    Must use: throw new RuntimeException("msg");
 *
 * 3. Method declares throws but never actually throws → compiles but misleading
 *
 * 4. Catching and re-throwing with a different exception type can lose the original
 *    stack trace. Use: throw new NewException("msg", originalException);
 *    to preserve the cause chain.
 *
 * 5. throws only applies to CHECKED exceptions. Adding unchecked exceptions
 *    to throws is optional (but can serve as documentation).
 */
