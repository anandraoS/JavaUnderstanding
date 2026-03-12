package basics;

/*
 * =============================================================================
 * Operators.java — All Java Operators with Examples
 * =============================================================================
 *
 * CONCEPT: Operators
 * ------------------
 * Operators are symbols that perform operations on variables and values.
 *
 * CATEGORIES OF OPERATORS IN JAVA:
 * 1. Arithmetic Operators      : +, -, *, /, %
 * 2. Assignment Operators      : =, +=, -=, *=, /=, %=, &=, |=, ^=, <<=, >>=
 * 3. Comparison (Relational)   : ==, !=, >, <, >=, <=
 * 4. Logical Operators         : &&, ||, !
 * 5. Bitwise Operators         : &, |, ^, ~, <<, >>, >>>
 * 6. Unary Operators           : +, -, ++, --, !
 * 7. Ternary Operator          : condition ? valueIfTrue : valueIfFalse
 * 8. instanceof Operator       : checks if object is an instance of a class
 *
 * OPERATOR PRECEDENCE (high to low, simplified):
 *   (), [], .    →  ++, --  →  !, ~, unary +/-  →  *, /, %  →  +, -
 *   →  <<, >>  →  <, >, <=, >=, instanceof  →  ==, !=
 *   →  &  →  ^  →  |  →  &&  →  ||  →  ?:  →  =, +=, -=, ...
 *
 * HOW TO RUN:
 *  $ javac -d out src/basics/Operators.java
 *  $ java -cp out basics.Operators
 * =============================================================================
 */
public class Operators {

    public static void main(String[] args) {

        // ── ARITHMETIC OPERATORS ─────────────────────────────────────────────
        System.out.println("=== Arithmetic Operators ===");

        int a = 15, b = 4;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b  = " + (a + b));   // 19 — addition
        System.out.println("a - b  = " + (a - b));   // 11 — subtraction
        System.out.println("a * b  = " + (a * b));   // 60 — multiplication
        System.out.println("a / b  = " + (a / b));   // 3  — integer division (truncates!)
        System.out.println("a % b  = " + (a % b));   // 3  — modulus (remainder)

        // Integer vs floating-point division
        double divFloat = (double) a / b;     // Cast one to double for decimal result
        System.out.println("(double)a / b = " + divFloat);  // 3.75

        // Common use of modulus: check even/odd
        System.out.println("15 % 2 = " + (15 % 2) + " (odd)");    // 1 = odd
        System.out.println("16 % 2 = " + (16 % 2) + " (even)");   // 0 = even

        // ── ASSIGNMENT OPERATORS ─────────────────────────────────────────────
        System.out.println("\n=== Assignment Operators ===");

        int x = 10;
        System.out.println("x = " + x);
        x += 5;   System.out.println("x += 5  → " + x);   // 15 (x = x + 5)
        x -= 3;   System.out.println("x -= 3  → " + x);   // 12
        x *= 2;   System.out.println("x *= 2  → " + x);   // 24
        x /= 4;   System.out.println("x /= 4  → " + x);   // 6
        x %= 4;   System.out.println("x %= 4  → " + x);   // 2

        // ── COMPARISON (RELATIONAL) OPERATORS ───────────────────────────────
        System.out.println("\n=== Comparison Operators ===");

        int p = 10, q = 20;
        System.out.println("p=" + p + ", q=" + q);
        System.out.println("p == q : " + (p == q));   // false
        System.out.println("p != q : " + (p != q));   // true
        System.out.println("p >  q : " + (p > q));    // false
        System.out.println("p <  q : " + (p < q));    // true
        System.out.println("p >= q : " + (p >= q));   // false
        System.out.println("p <= q : " + (p <= q));   // true

        // WARNING: Do NOT use == for String comparison!
        String s1 = new String("hello");   // creates a new object on the heap
        String s2 = new String("hello");   // creates another new object
        System.out.println("\nString comparison:");
        System.out.println("s1 == s2    : " + (s1 == s2));        // false — different objects!
        System.out.println("s1.equals(s2): " + s1.equals(s2));    // true  — same content

        // ── LOGICAL OPERATORS ────────────────────────────────────────────────
        System.out.println("\n=== Logical Operators ===");

        boolean sunny = true, warm = false;
        System.out.println("sunny=" + sunny + ", warm=" + warm);
        System.out.println("sunny && warm : " + (sunny && warm));  // false — both must be true
        System.out.println("sunny || warm : " + (sunny || warm));  // true  — at least one true
        System.out.println("!sunny        : " + !sunny);           // false — NOT operator

        // Short-circuit evaluation:
        // && stops evaluating if the left side is false (result can't be true)
        // || stops evaluating if the left side is true  (result can't be false)
        int counter = 0;
        boolean result = false && (++counter > 0);  // counter is NOT incremented!
        System.out.println("Short-circuit &&: counter=" + counter);  // 0

        counter = 0;
        result = true || (++counter > 0);    // counter is NOT incremented!
        System.out.println("Short-circuit ||: counter=" + counter);  // 0

        // ── UNARY OPERATORS ──────────────────────────────────────────────────
        System.out.println("\n=== Unary Operators ===");

        int n = 5;
        System.out.println("n = " + n);
        System.out.println("+n = " + (+n));    // 5  — unary plus (no-op for integers)
        System.out.println("-n = " + (-n));    // -5 — negation

        // Increment: ++ adds 1 to the variable
        // Pre-increment (++n): increment FIRST, then use the value
        // Post-increment (n++): use the value FIRST, then increment
        int pre = 5;
        System.out.println("pre-increment  ++pre: " + (++pre));  // 6 (incremented before use)
        System.out.println("pre after:           " + pre);       // 6

        int post = 5;
        System.out.println("post-increment post++: " + (post++));  // 5 (used before increment)
        System.out.println("post after:            " + post);       // 6

        // ── TERNARY OPERATOR ─────────────────────────────────────────────────
        System.out.println("\n=== Ternary Operator ===");

        // Syntax: condition ? valueIfTrue : valueIfFalse
        int age = 20;
        String status = (age >= 18) ? "adult" : "minor";  // one-line if-else!
        System.out.println("age=" + age + " → " + status);

        int score = 75;
        String grade = (score >= 90) ? "A" : (score >= 75) ? "B" : (score >= 60) ? "C" : "F";
        System.out.println("score=" + score + " → grade=" + grade);  // B

        // ── BITWISE OPERATORS ────────────────────────────────────────────────
        System.out.println("\n=== Bitwise Operators ===");

        int bA = 12;   // binary: 1100
        int bB = 10;   // binary: 1010

        System.out.println("a=12 (1100), b=10 (1010)");
        System.out.println("a & b  = " + (bA & bB));   // 8  = 1000 — AND: 1 only where BOTH are 1
        System.out.println("a | b  = " + (bA | bB));   // 14 = 1110 — OR:  1 where EITHER is 1
        System.out.println("a ^ b  = " + (bA ^ bB));   // 6  = 0110 — XOR: 1 where DIFFERENT
        System.out.println("~a     = " + (~bA));        // -13 (inverts all bits)
        System.out.println("a << 1 = " + (bA << 1));   // 24 = multiply by 2
        System.out.println("a >> 1 = " + (bA >> 1));   // 6  = divide by 2 (signed)
        System.out.println("a >>> 1= " + (bA >>> 1));  // 6  = divide by 2 (unsigned/zero-fill)

        // ── INSTANCEOF OPERATOR ──────────────────────────────────────────────
        System.out.println("\n=== instanceof Operator ===");

        Object obj = "Hello";         // String is also an Object
        System.out.println("obj instanceof String : " + (obj instanceof String));   // true
        System.out.println("obj instanceof Integer: " + (obj instanceof Integer));  // false

        // Java 16+ pattern matching instanceof:
        if (obj instanceof String str) {
            // 'str' is already cast to String — no explicit cast needed!
            System.out.println("String length: " + str.length());
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Arithmetic Operators ===
 * a = 15, b = 4
 * a + b  = 19
 * a - b  = 11
 * a * b  = 60
 * a / b  = 3
 * a % b  = 3
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Integer division: 5 / 2 = 2 (not 2.5!) — cast to double if needed
 * 2. Using = instead of == for comparison: if (x = 5) ← COMPILE ERROR in Java
 * 3. Using == for String comparison — use .equals() instead
 * 4. Confusing pre- and post-increment in complex expressions
 * 5. Modulo with negatives: -7 % 3 = -1 in Java (sign follows the dividend)
 */
