package strings;

/*
 * =============================================================================
 * StringBuilderDemo.java — StringBuilder and StringBuffer
 * =============================================================================
 *
 * CONCEPT: StringBuilder and StringBuffer
 * -----------------------------------------
 * String is IMMUTABLE. Every concatenation with + creates a new String object.
 * For performance in loops, use StringBuilder or StringBuffer instead.
 *
 * STRINGBUILDER vs STRINGBUFFER:
 *   StringBuilder  — NOT thread-safe, faster (use in single-threaded code)
 *   StringBuffer   — thread-safe (synchronized), slightly slower
 *
 * KEY METHODS:
 *   append(...)   — add content at the end
 *   insert(i, s)  — insert content at position i
 *   delete(i, j)  — remove characters from i to j-1
 *   replace(i,j,s)— replace characters from i to j-1 with s
 *   reverse()     — reverse the content
 *   charAt(i)     — get character at index
 *   setCharAt(i,c)— change character at index
 *   indexOf(s)    — find first occurrence
 *   length()      — current length
 *   capacity()    — current allocated capacity (auto-grows as needed)
 *   toString()    — convert back to String (use this at the end!)
 *
 * PERFORMANCE:
 *   String:        "a" + "b" + "c" creates 2 new String objects
 *   StringBuilder: append("a").append("b").append("c") — ONE object
 *
 *   In a loop of 10,000 iterations:
 *     String concatenation: ~O(n²) time — very slow!
 *     StringBuilder:        ~O(n) time  — much faster
 *
 * HOW TO RUN:
 *  $ javac -d out src/strings/StringBuilderDemo.java
 *  $ java -cp out strings.StringBuilderDemo
 * =============================================================================
 */
public class StringBuilderDemo {

    public static void main(String[] args) {

        // ── BASIC STRINGBUILDER ───────────────────────────────────────────────
        System.out.println("=== Basic StringBuilder ===");

        StringBuilder sb = new StringBuilder();        // default capacity 16
        sb.append("Hello");         // add "Hello"
        sb.append(", ");            // add ", "
        sb.append("Java");          // add "Java"
        sb.append("!");             // add "!"

        System.out.println("Content: " + sb.toString());   // Convert to String when done
        System.out.println("Length:  " + sb.length());     // 12
        System.out.println("Capacity:" + sb.capacity());   // 16 or more

        // ── APPEND DIFFERENT TYPES ────────────────────────────────────────────
        System.out.println("\n=== Appending Different Types ===");

        StringBuilder sb2 = new StringBuilder("Data: ");
        sb2.append(42);              // int
        sb2.append(", ");
        sb2.append(3.14);            // double
        sb2.append(", ");
        sb2.append(true);            // boolean
        sb2.append(", ");
        sb2.append('Z');             // char

        System.out.println(sb2.toString());   // Data: 42, 3.14, true, Z

        // ── METHOD CHAINING ───────────────────────────────────────────────────
        System.out.println("\n=== Method Chaining ===");

        // All append() methods return 'this' (the same StringBuilder), so you can chain them
        String result = new StringBuilder()
                .append("Hello")
                .append(" ")
                .append("World")
                .append("!")
                .toString();
        System.out.println("Chained: " + result);

        // ── INSERT ───────────────────────────────────────────────────────────
        System.out.println("\n=== insert() ===");
        StringBuilder sb3 = new StringBuilder("Hello World");
        System.out.println("Before insert: " + sb3);
        sb3.insert(5, ",");          // insert comma at index 5
        System.out.println("After insert:  " + sb3);   // "Hello, World"

        // ── DELETE ───────────────────────────────────────────────────────────
        System.out.println("\n=== delete() / deleteCharAt() ===");
        StringBuilder sb4 = new StringBuilder("Hello, World!");
        System.out.println("Before delete: " + sb4);
        sb4.delete(5, 7);            // delete chars at indices 5 and 6 (", ")
        System.out.println("After delete:  " + sb4);   // "HelloWorld!"

        sb4.deleteCharAt(sb4.length() - 1);  // delete last character
        System.out.println("After deleteCharAt: " + sb4);  // "HelloWorld"

        // ── REPLACE ──────────────────────────────────────────────────────────
        System.out.println("\n=== replace() ===");
        StringBuilder sb5 = new StringBuilder("I love cats!");
        sb5.replace(7, 11, "dogs");  // replace "cats" with "dogs"
        System.out.println("After replace: " + sb5);   // "I love dogs!"

        // ── REVERSE ──────────────────────────────────────────────────────────
        System.out.println("\n=== reverse() ===");
        StringBuilder sb6 = new StringBuilder("Hello");
        System.out.println("Original: " + sb6);
        sb6.reverse();
        System.out.println("Reversed: " + sb6);  // "olleH"

        // ── SETCHARAT / CHARAT ────────────────────────────────────────────────
        System.out.println("\n=== setCharAt() / charAt() ===");
        StringBuilder sb7 = new StringBuilder("Hello");
        System.out.println("charAt(1): " + sb7.charAt(1));    // 'e'
        sb7.setCharAt(0, 'h');   // change 'H' to 'h'
        System.out.println("After setCharAt: " + sb7);        // "hello"

        // ── PERFORMANCE COMPARISON ────────────────────────────────────────────
        System.out.println("\n=== Performance Comparison ===");

        int iterations = 10_000;

        // String concatenation in loop (SLOW — creates many objects)
        long start = System.currentTimeMillis();
        String slowResult = "";
        for (int i = 0; i < iterations; i++) {
            slowResult += "a";   // creates a new String each time!
        }
        long slowTime = System.currentTimeMillis() - start;

        // StringBuilder (FAST — modifies one object)
        start = System.currentTimeMillis();
        StringBuilder fastBuilder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            fastBuilder.append("a");   // modifies in-place, no new objects
        }
        String fastResult = fastBuilder.toString();
        long fastTime = System.currentTimeMillis() - start;

        System.out.printf("String + loop (%d iterations): %d ms%n", iterations, slowTime);
        System.out.printf("StringBuilder (%d iterations): %d ms%n", iterations, fastTime);
        System.out.println("Both results equal: " + slowResult.equals(fastResult));

        // ── STRINGBUFFER (thread-safe version) ───────────────────────────────
        System.out.println("\n=== StringBuffer (thread-safe) ===");
        StringBuffer buffer = new StringBuffer("Thread-safe: ");
        buffer.append("Hello ").append("World");
        System.out.println(buffer.toString());
        System.out.println("Use StringBuffer only when multiple threads modify the same object.");
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic StringBuilder ===
 * Content: Hello, Java!
 * Length:  12
 *
 * === Performance Comparison ===
 * String + loop (10000 iterations): [some ms — typically much higher]
 * StringBuilder (10000 iterations): [~0ms]
 * Both results equal: true
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using StringBuilder for every string operation — overkill for simple concatenations.
 *    For 1-2 concatenations, + is fine. Use StringBuilder in LOOPS.
 *
 * 2. Forgetting to call toString() when you need a String:
 *    StringBuilder sb = new StringBuilder("Hello");
 *    System.out.println(sb);        // OK (println calls toString automatically)
 *    String s = sb;                 // COMPILE ERROR — must call sb.toString()
 *
 * 3. Confusing StringBuilder.replace(start, end, str) with String.replace(old, new):
 *    StringBuilder.replace uses INDEX positions
 *    String.replace uses the VALUE to replace
 *
 * 4. Using StringBuffer when thread-safety isn't needed — unnecessary overhead.
 *    Use StringBuilder in single-threaded contexts.
 *
 * 5. delete(start, end): end is EXCLUSIVE (same as substring).
 *    delete(5, 7) deletes chars at positions 5 and 6 (not 7).
 */
