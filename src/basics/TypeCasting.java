package basics;

/*
 * =============================================================================
 * TypeCasting.java — Implicit and Explicit Type Casting in Java
 * =============================================================================
 *
 * CONCEPT: Type Casting
 * ----------------------
 * Type casting is converting a value from one data type to another.
 *
 * TWO TYPES:
 *
 * 1. WIDENING (Implicit) Casting — Automatic, no data loss
 *    Converting a smaller type to a larger type.
 *    Direction: byte → short → int → long → float → double
 *    Java does this automatically (implicitly).
 *
 * 2. NARROWING (Explicit) Casting — Manual, possible data loss
 *    Converting a larger type to a smaller type.
 *    Direction: double → float → long → int → short → byte
 *    Must be done manually with a cast operator: (targetType)
 *
 * TYPE PROMOTION IN EXPRESSIONS:
 *    When doing arithmetic, Java promotes smaller types:
 *    - byte + byte → int (not byte!)
 *    - int + long  → long
 *    - int + float → float
 *    - int + double → double
 *
 * OBJECT CASTING (Reference Types):
 *    - Upcasting: child class → parent class (implicit, safe)
 *    - Downcasting: parent class → child class (explicit, can throw ClassCastException)
 *
 * HOW TO RUN:
 *  $ javac -d out src/basics/TypeCasting.java
 *  $ java -cp out basics.TypeCasting
 * =============================================================================
 */
public class TypeCasting {

    public static void main(String[] args) {

        // ── WIDENING (IMPLICIT) CASTING ──────────────────────────────────────
        System.out.println("=== Widening (Implicit) Casting ===");
        // No data is lost — going from smaller to larger type
        // Java does this automatically, no cast syntax needed

        byte  byteVal  = 42;
        short shortVal = byteVal;    // byte → short (automatic)
        int   intVal   = shortVal;   // short → int (automatic)
        long  longVal  = intVal;     // int → long (automatic)
        float floatVal = longVal;    // long → float (automatic)
        double dblVal  = floatVal;   // float → double (automatic)

        System.out.println("byte   → " + byteVal);
        System.out.println("short  → " + shortVal);
        System.out.println("int    → " + intVal);
        System.out.println("long   → " + longVal);
        System.out.println("float  → " + floatVal);
        System.out.println("double → " + dblVal);

        // ── NARROWING (EXPLICIT) CASTING ─────────────────────────────────────
        System.out.println("\n=== Narrowing (Explicit) Casting ===");
        // MUST explicitly cast — risk of data loss!
        // Syntax: (targetType) value

        double pi = 3.99;
        int truncated = (int) pi;    // Decimal part TRUNCATED (not rounded!) → 3
        System.out.println("(int) 3.99 = " + truncated);  // 3, NOT 4

        long bigNumber = 1_234_567_890_123L;
        int smallNumber = (int) bigNumber;    // data LOST — overflow behavior
        System.out.println("long → int (possible overflow): " + smallNumber);

        double dbl = 9.99;
        float flt = (float) dbl;    // slight precision loss
        System.out.println("(float) 9.99 = " + flt);

        // char to int and back
        char ch = 'A';
        int ascii = (int) ch;       // char → int: gets Unicode/ASCII code
        System.out.println("(int) 'A' = " + ascii);   // 65
        char back = (char) 66;      // int → char: gets character with that code
        System.out.println("(char) 66 = " + back);    // 'B'

        // ── TYPE PROMOTION IN EXPRESSIONS ────────────────────────────────────
        System.out.println("\n=== Type Promotion in Expressions ===");

        byte b1 = 10, b2 = 20;
        // byte + byte is promoted to int automatically!
        // byte sum = b1 + b2;  ← COMPILE ERROR: int cannot be assigned to byte
        int sumBytes = b1 + b2;     // Must store in int or cast back
        System.out.println("byte + byte = int: " + sumBytes);

        int i = 100;
        long l = 200L;
        long mixed = i + l;          // int + long = long
        System.out.println("int + long = long: " + mixed);

        int num = 7;
        double result = num + 2.5;   // int + double = double
        System.out.println("int + double = double: " + result);

        // ── PARSING STRINGS TO NUMBERS ───────────────────────────────────────
        System.out.println("\n=== String → Number (Parsing) ===");

        String numStr = "42";
        int parsed = Integer.parseInt(numStr);        // String → int
        double parsedDouble = Double.parseDouble("3.14");  // String → double
        long parsedLong = Long.parseLong("9000000000");

        System.out.println("parseInt(\"42\")         = " + parsed);
        System.out.println("parseDouble(\"3.14\")    = " + parsedDouble);
        System.out.println("parseLong(\"9000000000\")= " + parsedLong);

        // ── NUMBER TO STRING ─────────────────────────────────────────────────
        System.out.println("\n=== Number → String ===");

        int number = 123;
        String s1 = String.valueOf(number);   // preferred method
        String s2 = Integer.toString(number); // also works
        String s3 = "" + number;              // simple but less clear

        System.out.println("String.valueOf(123)  = \"" + s1 + "\"");
        System.out.println("Integer.toString(123)= \"" + s2 + "\"");
        System.out.println("\"\" + 123            = \"" + s3 + "\"");

        // ── OBJECT CASTING (Reference Types) ─────────────────────────────────
        System.out.println("\n=== Object (Reference) Casting ===");

        // Upcasting: more specific → more general (automatic, always safe)
        Object obj = "Hello World";   // String is-a Object
        System.out.println("Upcasted: " + obj);

        // Downcasting: more general → more specific (manual, may fail)
        String str = (String) obj;    // safe here because obj IS actually a String
        System.out.println("Downcasted: " + str.toUpperCase());

        // ClassCastException example — WRONG downcasting
        Object numObj = Integer.valueOf(42);
        // String wrongCast = (String) numObj; ← Throws ClassCastException at runtime!

        // Always check with instanceof before downcasting:
        if (numObj instanceof Integer integer) {
            System.out.println("Safe cast: " + (integer * 2));  // 84
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Widening (Implicit) Casting ===
 * byte   → 42
 * short  → 42
 * int    → 42
 * ...
 * === Narrowing (Explicit) Casting ===
 * (int) 3.99 = 3
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Expecting rounding in narrowing cast:
 *    (int) 3.99 = 3, NOT 4  — truncation, not rounding!
 *    Use Math.round() if you want rounding.
 *
 * 2. byte/short arithmetic without awareness of promotion:
 *    byte a = 10, b = 20;
 *    byte c = a + b; ← COMPILE ERROR — result is promoted to int!
 *    byte c = (byte)(a + b); ← correct
 *
 * 3. Unchecked downcast leading to ClassCastException:
 *    Object obj = 42;
 *    String s = (String) obj; ← ClassCastException at runtime!
 *    Always use instanceof before downcasting.
 *
 * 4. Integer.parseInt() vs Integer.valueOf():
 *    parseInt() returns primitive int
 *    valueOf()  returns Integer object (autoboxed)
 *    Either works in most cases due to autoboxing.
 *
 * 5. NumberFormatException when parsing invalid strings:
 *    Integer.parseInt("abc"); ← NumberFormatException!
 *    Always validate or use try-catch.
 */
