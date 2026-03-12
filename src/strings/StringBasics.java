package strings;

/*
 * =============================================================================
 * StringBasics.java — String Creation, Immutability, and Comparison
 * =============================================================================
 *
 * CONCEPT: Strings in Java
 * -------------------------
 * String is a CLASS in Java (java.lang.String), NOT a primitive type.
 * String objects are IMMUTABLE — once created, they cannot be changed.
 * Any "modification" creates a NEW String object.
 *
 * STRING POOL (String Interning):
 *   Java maintains a special area in heap memory called the "String Pool" (or
 *   String Constant Pool). When you create a string literal like "Hello",
 *   Java first checks if "Hello" already exists in the pool. If yes, it reuses
 *   that object. If no, it creates a new one.
 *
 *   This is why:
 *     String a = "Hello";  // points to pool object
 *     String b = "Hello";  // points to SAME pool object
 *     a == b               // TRUE (same reference!)
 *
 *   But:
 *     String c = new String("Hello");  // forces a NEW object on heap
 *     c == a               // FALSE (different object)
 *     c.equals(a)          // TRUE (same content)
 *
 * ALWAYS USE .equals() for String comparison, not ==!
 *
 * HOW TO RUN:
 *  $ javac -d out src/strings/StringBasics.java
 *  $ java -cp out strings.StringBasics
 * =============================================================================
 */
public class StringBasics {

    public static void main(String[] args) {

        // ── CREATING STRINGS ─────────────────────────────────────────────────
        System.out.println("=== Creating Strings ===");

        String s1 = "Hello";                        // String literal (goes in String Pool)
        String s2 = "Hello";                        // same pool object as s1
        String s3 = new String("Hello");            // new object on heap (avoid this)
        String s4 = new String("Hello");            // another new object on heap
        String s5 = String.valueOf(42);             // from another type: "42"
        char[] chars = {'J', 'a', 'v', 'a'};
        String s6 = new String(chars);              // from char array: "Java"

        System.out.println("s1 = " + s1);
        System.out.println("s6 = " + s6);
        System.out.println("s5 = " + s5);

        // ── STRING COMPARISON ────────────────────────────────────────────────
        System.out.println("\n=== String Comparison ===");

        // == compares REFERENCES (memory addresses)
        System.out.println("s1 == s2:        " + (s1 == s2));   // true  (same pool object)
        System.out.println("s1 == s3:        " + (s1 == s3));   // false (different heap object)
        System.out.println("s3 == s4:        " + (s3 == s4));   // false (two different heap objects)

        // .equals() compares CONTENT — ALWAYS use this!
        System.out.println("s1.equals(s2):   " + s1.equals(s2));  // true
        System.out.println("s1.equals(s3):   " + s1.equals(s3));  // true
        System.out.println("s3.equals(s4):   " + s3.equals(s4));  // true

        // equalsIgnoreCase(): compare content, ignoring case
        String upper = "JAVA";
        String lower = "java";
        System.out.println("equalsIgnoreCase: " + upper.equalsIgnoreCase(lower));  // true

        // compareTo(): lexicographic (dictionary) comparison
        // Returns < 0 if s1 < s2, 0 if equal, > 0 if s1 > s2
        System.out.println("\"apple\".compareTo(\"banana\"): " + "apple".compareTo("banana"));  // negative

        // ── NULL-SAFE COMPARISON ─────────────────────────────────────────────
        System.out.println("\n=== Null-Safe Comparison ===");
        String nullStr = null;

        // DANGEROUS: nullStr.equals("hello") → NullPointerException!
        // SAFE: put the known non-null string first:
        System.out.println("\"hello\".equals(nullStr): " + "hello".equals(nullStr));  // false (safe)

        // Or use Objects.equals():
        System.out.println("Objects.equals(nullStr, \"hello\"): " + java.util.Objects.equals(nullStr, "hello"));

        // ── IMMUTABILITY DEMONSTRATION ────────────────────────────────────────
        System.out.println("\n=== String Immutability ===");
        String original = "Hello";
        System.out.println("original address: " + System.identityHashCode(original));

        String modified = original.toUpperCase();   // creates a NEW string, doesn't modify original
        System.out.println("modified address: " + System.identityHashCode(modified));
        System.out.println("original is still: " + original);   // still "Hello"
        System.out.println("modified:          " + modified);   // "HELLO" (new object)

        // Concatenation also creates new strings
        String result = original + " World";
        System.out.println("concatenated: " + result);
        System.out.println("original unchanged: " + original);

        // ── STRING LENGTH AND CHARACTERS ─────────────────────────────────────
        System.out.println("\n=== Length and Characters ===");
        String word = "JavaProgramming";
        System.out.println("Length: " + word.length());        // 15
        System.out.println("charAt(0): " + word.charAt(0));    // J
        System.out.println("charAt(4): " + word.charAt(4));    // P

        // Iterate over characters
        System.out.print("Characters: ");
        for (int i = 0; i < word.length(); i++) {
            System.out.print(word.charAt(i) + " ");
        }
        System.out.println();

        // ── EMPTY AND BLANK STRINGS ───────────────────────────────────────────
        System.out.println("\n=== Empty and Blank Strings ===");
        String empty = "";
        String blank = "   ";        // only whitespace

        System.out.println("empty.isEmpty():  " + empty.isEmpty());    // true
        System.out.println("blank.isEmpty():  " + blank.isEmpty());    // false (has spaces)
        System.out.println("blank.isBlank():  " + blank.isBlank());    // true (Java 11+)
        System.out.println("empty.isBlank():  " + empty.isBlank());    // true

        // ── STRING CONCATENATION PERFORMANCE ─────────────────────────────────
        System.out.println("\n=== Concatenation Performance Note ===");
        System.out.println("Using + in a loop creates many temporary String objects.");
        System.out.println("Use StringBuilder for multiple concatenations in loops.");
        System.out.println("See StringBuilderDemo.java for details.");
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Creating Strings ===
 * s1 = Hello
 * s6 = Java
 * s5 = 42
 *
 * === String Comparison ===
 * s1 == s2:        true
 * s1 == s3:        false
 * s1.equals(s2):   true
 * s1.equals(s3):   true
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using == to compare strings → compares references, not content!
 *    Always use .equals() or .equalsIgnoreCase()
 *
 * 2. NullPointerException: calling methods on null String
 *    Fix: check for null first, or put string literal on left side of equals
 *
 * 3. Thinking string methods modify the original:
 *    s.toUpperCase() doesn't change s — it returns a NEW string!
 *    Must capture the result: s = s.toUpperCase();
 *
 * 4. Using new String("Hello") unnecessarily — wastes memory, bypasses pool.
 *    Just use "Hello" directly.
 */
