package basics;

/*
 * =============================================================================
 * HelloWorld.java — Your First Java Program
 * =============================================================================
 *
 * CONCEPT: Hello World
 * --------------------
 * Every Java learning journey starts with "Hello World". This program
 * introduces the basic structure of a Java program.
 *
 * KEY TERMS:
 *  - class       : The fundamental building block of Java. Everything lives inside a class.
 *  - public      : An access modifier. 'public' means this class/method is accessible
 *                  from anywhere.
 *  - static      : The method belongs to the class itself, not to any object instance.
 *                  'main' must be static so Java can call it without creating an object.
 *  - void        : The return type. 'void' means the method returns nothing.
 *  - main        : The entry point of every Java application. The JVM looks for
 *                  'public static void main(String[] args)' to start execution.
 *  - String[]    : An array of String objects. 'args' holds command-line arguments.
 *  - System.out  : The standard output stream (your terminal/console).
 *  - println()   : Prints text followed by a newline character.
 *  - print()     : Prints text WITHOUT a newline character.
 *  - printf()    : Prints formatted text (like C's printf).
 *
 * FILE NAMING RULE (IMPORTANT on Mac):
 *  - The filename MUST match the public class name exactly, including case.
 *  - This file is named HelloWorld.java because the class is named HelloWorld.
 *  - Mac's filesystem is case-insensitive by default, but Java is case-sensitive!
 *    Always use exact matching to avoid cross-platform issues.
 *
 * HOW TO COMPILE AND RUN (Mac Terminal):
 *  $ javac -d out src/basics/HelloWorld.java
 *  $ java -cp out basics.HelloWorld
 *
 * OR use the run script:
 *  $ ./run.sh basics.HelloWorld
 * =============================================================================
 */
public class HelloWorld {

    public static void main(String[] args) {

        // ── Basic Output ─────────────────────────────────────────────────────

        // System.out.println() prints the text and moves to the next line
        System.out.println("Hello, World!");

        // System.out.print() prints text WITHOUT moving to the next line
        System.out.print("Hello, ");
        System.out.print("Java! ");
        System.out.println("(same line)");

        // System.out.printf() formats output — %s = string, %d = integer, %n = newline
        System.out.printf("Welcome to %s version %d!%n", "Java", 17);

        // ── String Concatenation ─────────────────────────────────────────────

        String name = "Learner";                      // Declare and assign a String variable
        System.out.println("Hello, " + name + "!");   // '+' concatenates (joins) strings

        // ── Escaped Characters ───────────────────────────────────────────────

        // Special characters need a backslash prefix:
        System.out.println("Tab:\tafter tab");          // \t = horizontal tab
        System.out.println("Newline:\nSecond line");    // \n = newline
        System.out.println("Quote: \"Hello\"");         // \" = double quote
        System.out.println("Backslash: \\");            // \\ = single backslash

        // ── Comments ─────────────────────────────────────────────────────────

        // This is a single-line comment — ignored by the compiler

        /*
         * This is a multi-line comment.
         * It can span multiple lines.
         * Useful for longer explanations.
         */

        /**
         * This is a Javadoc comment — used to generate documentation.
         * Tools like IntelliJ and javadoc utility read these.
         */

        System.out.println("Done! Java basics.HelloWorld complete.");
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * Hello, World!
 * Hello, Java! (same line)
 * Welcome to Java version 17!
 * Hello, Learner!
 * Tab:    after tab
 * Newline:
 * Second line
 * Quote: "Hello"
 * Backslash: \
 * Done! Java basics.HelloWorld complete.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Missing semicolons (;) at the end of statements — Java requires them.
 * 2. Case sensitivity: 'string' is wrong, 'String' is correct.
 * 3. Forgetting to make main method 'public static void' exactly.
 * 4. File name not matching class name: HelloWorld.java must contain class HelloWorld.
 */
