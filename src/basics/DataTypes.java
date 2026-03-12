package basics;

/*
 * =============================================================================
 * DataTypes.java — Primitive and Reference Data Types in Java
 * =============================================================================
 *
 * CONCEPT: Data Types
 * -------------------
 * Java is a STATICALLY TYPED language — you must declare the type of every
 * variable before using it. This means the compiler knows the type at compile
 * time, catching many bugs early.
 *
 * Java has two categories of data types:
 *
 * 1. PRIMITIVE TYPES (8 total) — stored directly in memory (stack)
 *    ┌──────────┬──────────┬───────────────────────────────────────┐
 *    │ Type     │ Size     │ Range / Notes                          │
 *    ├──────────┼──────────┼───────────────────────────────────────┤
 *    │ byte     │ 1 byte   │ -128 to 127                            │
 *    │ short    │ 2 bytes  │ -32,768 to 32,767                      │
 *    │ int      │ 4 bytes  │ -2,147,483,648 to 2,147,483,647        │
 *    │ long     │ 8 bytes  │ -9.2 × 10^18 to 9.2 × 10^18           │
 *    │ float    │ 4 bytes  │ ~±3.4 × 10^38, 7 decimal digits        │
 *    │ double   │ 8 bytes  │ ~±1.7 × 10^308, 15 decimal digits      │
 *    │ char     │ 2 bytes  │ Unicode character '\u0000' to '\uFFFF' │
 *    │ boolean  │ 1 bit    │ true or false only                     │
 *    └──────────┴──────────┴───────────────────────────────────────┘
 *
 * 2. REFERENCE TYPES — store a reference (memory address) to an object (heap)
 *    - String, arrays, classes, interfaces
 *    - Default value is null (not pointing to any object)
 *
 * WRAPPER CLASSES:
 *    Each primitive has a corresponding Wrapper class for use in Collections:
 *    int → Integer, double → Double, char → Character, boolean → Boolean, etc.
 *
 * HOW TO RUN:
 *  $ javac -d out src/basics/DataTypes.java
 *  $ java -cp out basics.DataTypes
 * =============================================================================
 */
public class DataTypes {

    public static void main(String[] args) {

        // ── INTEGER TYPES ────────────────────────────────────────────────────

        byte myByte = 100;           // 1 byte: smallest integer type (-128 to 127)
        short myShort = 30000;       // 2 bytes: small integers (-32,768 to 32,767)
        int myInt = 2_000_000;       // 4 bytes: most common integer type
                                     // Underscores in numeric literals (Java 7+) improve readability
        long myLong = 9_000_000_000L; // 8 bytes: note the 'L' suffix — required for long literals
                                      // Without 'L', Java treats the number as int and may overflow

        System.out.println("=== Integer Types ===");
        System.out.println("byte:  " + myByte);
        System.out.println("short: " + myShort);
        System.out.println("int:   " + myInt);
        System.out.println("long:  " + myLong);

        // ── FLOATING POINT TYPES ─────────────────────────────────────────────

        float myFloat = 3.14f;       // 4 bytes: note the 'f' suffix — REQUIRED for float literals
                                     // Without 'f', Java treats decimal literals as double
        double myDouble = 3.14159265358979; // 8 bytes: default for decimal numbers, more precise

        System.out.println("\n=== Floating Point Types ===");
        System.out.printf("float:  %.2f%n", myFloat);    // %.2f = 2 decimal places
        System.out.printf("double: %.15f%n", myDouble);  // %.15f = 15 decimal places

        // ── CHARACTER TYPE ───────────────────────────────────────────────────

        char myChar = 'A';           // Single character — uses SINGLE QUOTES (not double!)
        char unicodeChar = '\u0041'; // Unicode escape — \u0041 is also 'A'
        char numericChar = 65;       // Characters are stored as numbers (65 = 'A' in ASCII/Unicode)

        System.out.println("\n=== Character Type ===");
        System.out.println("char:         " + myChar);
        System.out.println("unicode char: " + unicodeChar);
        System.out.println("numeric char: " + numericChar);
        System.out.println("char + 1:     " + (char)(myChar + 1)); // 'B' — arithmetic on chars

        // ── BOOLEAN TYPE ─────────────────────────────────────────────────────

        boolean isJavaFun = true;    // Only two values: true or false
        boolean isHard = false;
        boolean result = isJavaFun && !isHard;  // && = AND, ! = NOT

        System.out.println("\n=== Boolean Type ===");
        System.out.println("isJavaFun: " + isJavaFun);
        System.out.println("isHard:    " + isHard);
        System.out.println("result:    " + result);

        // ── REFERENCE TYPES ──────────────────────────────────────────────────

        String name = "Java";        // String is a reference type (not primitive!)
                                     // String literals are stored in the String Pool (heap)
        String nullRef = null;       // Reference types can be null (pointing to nothing)

        System.out.println("\n=== Reference Types ===");
        System.out.println("String: " + name);
        System.out.println("null reference: " + nullRef);

        // ── DEFAULT VALUES ───────────────────────────────────────────────────
        // Local variables must be explicitly initialized before use.
        // Class-level fields (instance/static) get default values:
        //   int → 0, double → 0.0, boolean → false, char → '\u0000', Object → null
        System.out.println("\n=== Note on Default Values ===");
        System.out.println("Class fields get defaults. Local variables must be initialized.");

        // ── OVERFLOW DEMONSTRATION ───────────────────────────────────────────

        int maxInt = Integer.MAX_VALUE;    // 2,147,483,647
        int overflow = maxInt + 1;         // Wraps around to Integer.MIN_VALUE!

        System.out.println("\n=== Integer Overflow ===");
        System.out.println("MAX int: " + maxInt);
        System.out.println("MAX + 1: " + overflow);  // -2147483648 — dangerous bug!
        System.out.println("Use long if values may exceed int range.");

        // ── WRAPPER CLASSES ──────────────────────────────────────────────────

        Integer wrappedInt = 42;           // Autoboxing: int → Integer automatically
        int unwrapped = wrappedInt;        // Unboxing: Integer → int automatically

        System.out.println("\n=== Wrapper Classes ===");
        System.out.println("Integer.MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Double.MAX_VALUE:  " + Double.MAX_VALUE);
        System.out.println("Integer.toBinaryString(255): " + Integer.toBinaryString(255)); // "11111111"
        System.out.println("Integer.toHexString(255):    " + Integer.toHexString(255));   // "ff"
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Integer Types ===
 * byte:  100
 * short: 30000
 * int:   2000000
 * long:  9000000000
 *
 * === Floating Point Types ===
 * float:  3.14
 * double: 3.141592653589790
 *
 * === Character Type ===
 * char:         A
 * unicode char: A
 * numeric char: A
 * char + 1:     B
 *
 * ... etc.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting 'L' suffix for long literals: long x = 9000000000; ← ERROR
 * 2. Forgetting 'f' suffix for float literals: float x = 3.14; ← ERROR (it's a double)
 * 3. Using double quotes for char: char c = "A"; ← ERROR (use single quotes)
 * 4. Integer overflow: not checking if value exceeds MAX_VALUE
 * 5. NullPointerException: calling methods on a null reference
 */
