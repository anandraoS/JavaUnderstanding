package strings;

/*
 * =============================================================================
 * StringMethods.java — Common String Methods
 * =============================================================================
 *
 * CONCEPT: String Methods
 * ------------------------
 * The String class provides many built-in methods to work with text.
 * Since Strings are immutable, all methods return NEW Strings.
 *
 * COMMON METHODS COVERED:
 *   length(), charAt(), indexOf(), lastIndexOf()
 *   substring(), contains(), startsWith(), endsWith()
 *   toLowerCase(), toUpperCase(), trim(), strip()
 *   replace(), replaceAll(), split(), join()
 *   toCharArray(), intern(), formatted() (Java 15+)
 *
 * HOW TO RUN:
 *  $ javac -d out src/strings/StringMethods.java
 *  $ java -cp out strings.StringMethods
 * =============================================================================
 */
public class StringMethods {

    public static void main(String[] args) {

        String str = "  Hello, Java World!  ";

        // ── LENGTH AND CHARACTER ACCESS ───────────────────────────────────────
        System.out.println("=== Length & Character Access ===");
        System.out.println("Original: \"" + str + "\"");
        System.out.println("length(): " + str.length());          // includes spaces
        System.out.println("charAt(7): " + str.charAt(7));        // 'J' (0-indexed)

        // ── SEARCHING ────────────────────────────────────────────────────────
        System.out.println("\n=== Searching ===");
        String text = "Hello, Java! Hello, World!";
        System.out.println("text: " + text);
        System.out.println("indexOf('J'):        " + text.indexOf('J'));            // 7
        System.out.println("indexOf(\"Hello\"):    " + text.indexOf("Hello"));      // 0 (first occurrence)
        System.out.println("lastIndexOf(\"Hello\"): " + text.lastIndexOf("Hello")); // 14 (last occurrence)
        System.out.println("indexOf(\"Python\"):   " + text.indexOf("Python"));     // -1 (not found)
        System.out.println("contains(\"Java\"):    " + text.contains("Java"));      // true

        // ── SUBSTRINGS ───────────────────────────────────────────────────────
        System.out.println("\n=== Substrings ===");
        String s = "JavaProgramming";
        System.out.println("s: " + s);
        System.out.println("substring(4):    " + s.substring(4));      // "Programming" (from index 4 to end)
        System.out.println("substring(0,4):  " + s.substring(0, 4));   // "Java" (from 0, up to but NOT including 4)
        System.out.println("substring(4,11): " + s.substring(4, 11));  // "Program"

        // ── STARTS/ENDS WITH ─────────────────────────────────────────────────
        System.out.println("\n=== startsWith / endsWith ===");
        String filename = "Report_2024.pdf";
        System.out.println("startsWith(\"Report\"): " + filename.startsWith("Report"));  // true
        System.out.println("endsWith(\".pdf\"):      " + filename.endsWith(".pdf"));      // true
        System.out.println("endsWith(\".txt\"):      " + filename.endsWith(".txt"));      // false

        // ── CASE CONVERSION ──────────────────────────────────────────────────
        System.out.println("\n=== Case Conversion ===");
        String mixed = "Hello World";
        System.out.println("toLowerCase(): " + mixed.toLowerCase());  // hello world
        System.out.println("toUpperCase(): " + mixed.toUpperCase());  // HELLO WORLD

        // ── TRIMMING WHITESPACE ───────────────────────────────────────────────
        System.out.println("\n=== Trimming Whitespace ===");
        String padded = "   Hello Java   ";
        System.out.println("Original:         \"" + padded + "\"");
        System.out.println("trim():           \"" + padded.trim() + "\"");   // removes leading/trailing spaces
        System.out.println("strip():          \"" + padded.strip() + "\"");  // Java 11+, handles Unicode whitespace
        System.out.println("stripLeading():   \"" + padded.stripLeading() + "\"");
        System.out.println("stripTrailing():  \"" + padded.stripTrailing() + "\"");

        // ── REPLACE ──────────────────────────────────────────────────────────
        System.out.println("\n=== Replace ===");
        String sentence = "I like cats. Cats are cute.";
        System.out.println("Original: " + sentence);
        System.out.println("replace (char): " + sentence.replace('a', '*'));           // replaces ALL occurrences of char
        System.out.println("replace (str):  " + sentence.replace("cats", "dogs"));    // case-sensitive
        System.out.println("replaceAll:     " + sentence.replaceAll("[Cc]ats", "dogs")); // regex

        // replaceFirst: replaces only the FIRST occurrence
        System.out.println("replaceFirst:   " + sentence.replaceFirst("cats", "dogs"));

        // ── SPLIT ────────────────────────────────────────────────────────────
        System.out.println("\n=== Split ===");
        String csv = "Alice,Bob,Charlie,Dave,Eve";
        String[] names = csv.split(",");       // split by comma
        System.out.println("Names count: " + names.length);
        for (String name : names) {
            System.out.println("  " + name);
        }

        // Split with limit
        String[] firstTwo = csv.split(",", 2);  // split into max 2 parts
        System.out.println("split limit=2: [" + firstTwo[0] + "] and [" + firstTwo[1] + "]");

        // Split on whitespace
        String words = "Hello   World   Java";
        String[] wordArr = words.split("\\s+");  // \\s+ = one or more whitespace chars
        System.out.println("Words: " + java.util.Arrays.toString(wordArr));

        // ── JOIN ─────────────────────────────────────────────────────────────
        System.out.println("\n=== Join ===");
        String joined = String.join(", ", "Alice", "Bob", "Charlie");  // join with delimiter
        System.out.println("Joined: " + joined);

        String[] fruits = {"Apple", "Banana", "Cherry"};
        System.out.println("Joined array: " + String.join(" | ", fruits));

        // ── TOCHARARRAY ───────────────────────────────────────────────────────
        System.out.println("\n=== toCharArray ===");
        char[] charArr = "Hello".toCharArray();
        for (char c : charArr) {
            System.out.print(c + " ");
        }
        System.out.println();

        // ── FORMATTED (Java 15+, replaces String.format) ──────────────────────
        System.out.println("\n=== String.format / formatted ===");
        String formatted = String.format("Name: %s, Age: %d, GPA: %.2f", "Alice", 21, 3.85);
        System.out.println(formatted);

        // Java 15+ instance method:
        String formatted2 = "Name: %s, Age: %d".formatted("Bob", 25);
        System.out.println(formatted2);

        // ── USEFUL PATTERNS ───────────────────────────────────────────────────
        System.out.println("\n=== Useful Patterns ===");

        // Check if string is a number
        String numStr = "12345";
        boolean isNumeric = numStr.matches("\\d+");   // \\d+ = one or more digits
        System.out.println("\"" + numStr + "\" is numeric: " + isNumeric);

        // Reverse a string
        String original = "Hello";
        String reversed = new StringBuilder(original).reverse().toString();
        System.out.println("Reversed \"" + original + "\": " + reversed);

        // Count occurrences of a character
        String longStr = "banana";
        long countA = longStr.chars().filter(c -> c == 'a').count();
        System.out.println("Count of 'a' in \"" + longStr + "\": " + countA);
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. IndexOutOfBoundsException: substring(start, end) where end > length()
 * 2. Forgetting that substring end index is EXCLUSIVE: "Java".substring(0,4) = "Java" (not "Java\0")
 * 3. Not capturing the result of string methods (they return new strings):
 *    str.toUpperCase();   ← result discarded!
 *    str = str.toUpperCase(); ← correct
 * 4. split() with special regex characters needs escaping:
 *    "1.2.3".split(".")  → splits on EVERY character (. in regex = any char)!
 *    "1.2.3".split("\\.") → correct (escaped dot)
 * 5. trim() doesn't remove Unicode whitespace — use strip() (Java 11+) instead
 */
