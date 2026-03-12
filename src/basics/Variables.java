package basics;

/*
 * =============================================================================
 * Variables.java — Variables, Constants, and Scope in Java
 * =============================================================================
 *
 * CONCEPT: Variables
 * ------------------
 * A variable is a named memory location that holds a value. In Java, every
 * variable must be declared with a type before it can be used.
 *
 * TYPES OF VARIABLES IN JAVA:
 *
 * 1. LOCAL VARIABLES
 *    - Declared inside a method, constructor, or block
 *    - Must be initialized before use (no default value)
 *    - Only accessible within the block where declared
 *    - Stored on the Stack
 *
 * 2. INSTANCE VARIABLES (Fields)
 *    - Declared inside a class but outside any method
 *    - Each object has its own copy
 *    - Gets a default value (int → 0, boolean → false, Object → null)
 *    - Stored on the Heap
 *
 * 3. STATIC (CLASS) VARIABLES
 *    - Declared with 'static' keyword inside a class
 *    - Shared by ALL instances of the class (only one copy)
 *    - Gets a default value
 *    - Stored in the Method Area
 *
 * 4. CONSTANTS (final variables)
 *    - Declared with 'final' keyword
 *    - Value cannot be changed after initialization
 *    - By convention, named in UPPER_SNAKE_CASE
 *
 * NAMING CONVENTIONS (follow these!):
 *    - Variables & methods: camelCase  (e.g., myVariable, calculateArea)
 *    - Classes:             PascalCase  (e.g., MyClass, DataTypes)
 *    - Constants:           UPPER_SNAKE_CASE (e.g., MAX_SIZE, PI)
 *    - Packages:            lowercase   (e.g., basics, controlflow)
 *
 * var KEYWORD (Java 10+):
 *    - Local variable type inference: let the compiler figure out the type
 *    - Only works for LOCAL variables with an initializer
 *
 * HOW TO RUN:
 *  $ javac -d out src/basics/Variables.java
 *  $ java -cp out basics.Variables
 * =============================================================================
 */
public class Variables {

    // ── INSTANCE VARIABLES (Fields) ──────────────────────────────────────────
    // Declared inside the class but outside methods.
    // Each object of this class will have its own copy.
    String instanceName = "Instance Variable";   // gets default "" if not set
    int instanceAge;                              // gets default value 0

    // ── STATIC (CLASS) VARIABLES ─────────────────────────────────────────────
    // Only ONE copy exists for the entire class, shared by all objects.
    static int objectCount = 0;
    static String className = "Variables";

    // ── CONSTANTS ────────────────────────────────────────────────────────────
    // 'final' means the value cannot be changed after assignment.
    // Convention: ALL_CAPS with underscores.
    static final double PI = 3.14159265358979;
    static final int MAX_STUDENTS = 50;

    public static void main(String[] args) {

        // ── LOCAL VARIABLES ──────────────────────────────────────────────────
        // Declared inside a method. MUST be initialized before use.

        int age = 25;               // Declaration + initialization in one line
        String city;                // Declaration only
        city = "Mumbai";            // Initialization (must happen before first use)
        double salary = 75000.50;
        boolean isEmployed = true;

        System.out.println("=== Local Variables ===");
        System.out.println("Age:        " + age);
        System.out.println("City:       " + city);
        System.out.println("Salary:     " + salary);
        System.out.println("Employed:   " + isEmployed);

        // ── MULTIPLE DECLARATIONS ────────────────────────────────────────────
        // You can declare multiple variables of the same type on one line.
        // However, one variable per line is better for readability.
        int x = 10, y = 20, z = 30;
        System.out.println("\n=== Multiple Declarations ===");
        System.out.println("x=" + x + ", y=" + y + ", z=" + z);

        // ── CONSTANTS ────────────────────────────────────────────────────────
        System.out.println("\n=== Constants (final) ===");
        System.out.println("PI = " + PI);
        System.out.println("MAX_STUDENTS = " + MAX_STUDENTS);

        // Attempting to reassign a constant causes a compile error:
        // PI = 3.14;  ← COMPILE ERROR: cannot assign a value to final variable PI

        // ── LOCAL CONSTANT ───────────────────────────────────────────────────
        final int MY_CONSTANT = 100;  // Local final variable
        System.out.println("Local constant: " + MY_CONSTANT);

        // ── VAR KEYWORD (Type Inference, Java 10+) ───────────────────────────
        // The compiler infers the type from the right-hand side.
        // The type is still fixed at compile time — this is NOT dynamic typing!
        var message = "Hello, var!";   // inferred as String
        var count = 42;                // inferred as int
        var price = 9.99;              // inferred as double
        var flag = true;               // inferred as boolean

        System.out.println("\n=== var Keyword (Type Inference) ===");
        System.out.println("message: " + message + " (type: " + ((Object)message).getClass().getSimpleName() + ")");
        System.out.println("count:   " + count);
        System.out.println("price:   " + price);
        System.out.println("flag:    " + flag);

        // ── VARIABLE SCOPE ───────────────────────────────────────────────────
        // Variables are only accessible within the block {} where they are declared.
        System.out.println("\n=== Variable Scope ===");

        int outerVar = 10;   // visible to everything below in this method

        {
            // This is an inner block (scope)
            int innerVar = 20;   // only visible within this block
            System.out.println("outerVar inside block: " + outerVar);   // OK
            System.out.println("innerVar inside block: " + innerVar);   // OK
        }

        // innerVar is NOT accessible here — it went out of scope when the block ended
        // System.out.println(innerVar); ← COMPILE ERROR: cannot find symbol

        System.out.println("outerVar outside block: " + outerVar);  // Still accessible

        // ── STATIC VARIABLES ─────────────────────────────────────────────────
        System.out.println("\n=== Static Variables ===");
        System.out.println("Class name: " + className);    // accessed via class (no object needed)
        System.out.println("Object count: " + objectCount);

        // ── SHADOWING ────────────────────────────────────────────────────────
        // A local variable can shadow (hide) an outer variable with the same name.
        // Avoid this — it leads to confusing code!
        int shadowMe = 100;
        System.out.println("\n=== Variable Shadowing (avoid this!) ===");
        System.out.println("outer shadowMe: " + shadowMe);   // 100

        {
            int shadowMe2 = 999;  // Different name to avoid re-declaration error in same scope
            System.out.println("inner shadowMe2: " + shadowMe2);   // 999
        }

        // ── SWAPPING VARIABLES ───────────────────────────────────────────────
        System.out.println("\n=== Swapping Variables ===");
        int a = 5, b = 10;
        System.out.println("Before: a=" + a + ", b=" + b);

        // Method 1: Using a temporary variable (most readable)
        int temp = a;
        a = b;
        b = temp;
        System.out.println("After swap (temp var): a=" + a + ", b=" + b);

        // Method 2: Using arithmetic (no temp variable — can overflow for large ints!)
        a = a + b;   // a = 15
        b = a - b;   // b = 5 (original a)
        a = a - b;   // a = 10 (original b)
        System.out.println("After swap (arithmetic): a=" + a + ", b=" + b);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Local Variables ===
 * Age:        25
 * City:       Mumbai
 * Salary:     75000.5
 * Employed:   true
 *
 * === Constants (final) ===
 * PI = 3.14159265358979
 * MAX_STUDENTS = 50
 *
 * === var Keyword ===
 * message: Hello, var! (type: String)
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using a local variable before initializing it:
 *    int x; System.out.println(x); ← COMPILE ERROR
 *
 * 2. Trying to reassign a final variable:
 *    final int X = 5; X = 10; ← COMPILE ERROR
 *
 * 3. Accessing a variable outside its scope:
 *    { int a = 1; } System.out.println(a); ← COMPILE ERROR
 *
 * 4. Using 'var' without an initializer:
 *    var x; ← COMPILE ERROR (can't infer type)
 *
 * 5. Naming conventions matter for code readability:
 *    int MyAge = 25; ← works but wrong (should be myAge)
 */
