package java8features;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/*
 * =============================================================================
 * LambdaExpressions.java — Lambda Expressions in Java 8+
 * =============================================================================
 *
 * CONCEPT: Lambda Expressions
 * ----------------------------
 * A lambda expression is an anonymous function (a function without a name).
 * It provides a concise way to implement functional interfaces.
 *
 * SYNTAX:
 *   (parameters) -> expression
 *   (parameters) -> { statements; }
 *
 * EXAMPLES:
 *   () -> System.out.println("Hello")           — no params, no return
 *   (x) -> x * x                                — one param, expression return
 *   x -> x * x                                  — parens optional for single param
 *   (x, y) -> x + y                             — two params
 *   (String s) -> s.length() > 5               — with explicit type
 *   (x, y) -> { int z = x + y; return z; }     — multi-statement, explicit return
 *
 * FUNCTIONAL INTERFACE:
 *   An interface with exactly ONE abstract method.
 *   Lambda expressions are used to implement functional interfaces.
 *
 * BUILT-IN FUNCTIONAL INTERFACES (java.util.function):
 *   Predicate<T>      : T -> boolean       (test/filter)
 *   Function<T, R>    : T -> R             (transform/map)
 *   Consumer<T>       : T -> void          (consume/print)
 *   Supplier<T>       : () -> T            (create/supply)
 *   BiFunction<T,U,R> : (T, U) -> R        (two inputs, one output)
 *   UnaryOperator<T>  : T -> T             (transform same type)
 *   BinaryOperator<T> : (T, T) -> T        (combine same type)
 *
 * METHOD REFERENCES:
 *   ClassName::staticMethod     — static method reference
 *   object::instanceMethod      — instance method reference
 *   ClassName::instanceMethod   — unbound instance method reference
 *   ClassName::new              — constructor reference
 *
 * HOW TO RUN:
 *  $ javac -d out src/java8features/LambdaExpressions.java
 *  $ java -cp out java8features.LambdaExpressions
 * =============================================================================
 */
public class LambdaExpressions {

    // A custom functional interface
    @FunctionalInterface
    interface MathOp {
        int apply(int a, int b);
    }

    @FunctionalInterface
    interface StringTransformer {
        String transform(String s);
    }

    public static void main(String[] args) {

        // ── BASIC LAMBDA SYNTAX ───────────────────────────────────────────────
        System.out.println("=== Basic Lambda Syntax ===");

        // Before Java 8: Anonymous inner class
        Runnable oldStyle = new Runnable() {
            @Override
            public void run() {
                System.out.println("Old style: anonymous class");
            }
        };

        // Java 8+ Lambda (equivalent, much cleaner)
        Runnable lambda = () -> System.out.println("Lambda style: concise");

        oldStyle.run();
        lambda.run();

        // ── CUSTOM FUNCTIONAL INTERFACE ───────────────────────────────────────
        System.out.println("\n=== Custom Functional Interface ===");

        MathOp add      = (a, b) -> a + b;
        MathOp subtract = (a, b) -> a - b;
        MathOp multiply = (a, b) -> a * b;

        System.out.println("add(5, 3)      = " + add.apply(5, 3));
        System.out.println("subtract(10,4) = " + subtract.apply(10, 4));
        System.out.println("multiply(4, 5) = " + multiply.apply(4, 5));

        // ── PREDICATE<T> — takes T, returns boolean ───────────────────────────
        System.out.println("\n=== Predicate<T> ===");

        Predicate<Integer> isEven   = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<String>  isLong   = s -> s.length() > 5;

        System.out.println("isEven(4):     " + isEven.test(4));
        System.out.println("isEven(7):     " + isEven.test(7));
        System.out.println("isLong(\"Hi\"): " + isLong.test("Hi"));
        System.out.println("isLong(\"Hello World\"): " + isLong.test("Hello World"));

        // Combining predicates
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        Predicate<Integer> isEvenOrPositive  = isEven.or(isPositive);
        Predicate<Integer> isOdd             = isEven.negate();

        System.out.println("isEvenAndPositive(4):  " + isEvenAndPositive.test(4));
        System.out.println("isEvenAndPositive(-4): " + isEvenAndPositive.test(-4));
        System.out.println("isOdd(7):              " + isOdd.test(7));

        // ── FUNCTION<T, R> — takes T, returns R ──────────────────────────────
        System.out.println("\n=== Function<T, R> ===");

        Function<String, Integer> strLength = s -> s.length();
        Function<Integer, String> numToStr  = n -> "Number: " + n;
        Function<String, String>  upperCase = String::toUpperCase;   // method reference

        System.out.println("strLength(\"Hello\"): " + strLength.apply("Hello"));
        System.out.println("numToStr(42):        " + numToStr.apply(42));
        System.out.println("upperCase(\"hello\"): " + upperCase.apply("hello"));

        // Function composition: f.andThen(g) = g(f(x))
        Function<String, String> trim     = String::trim;
        Function<String, String> trimAndUpper = trim.andThen(String::toUpperCase);
        System.out.println("trimAndUpper(\"  hello  \"): " + trimAndUpper.apply("  hello  "));

        // ── CONSUMER<T> — takes T, returns void ──────────────────────────────
        System.out.println("\n=== Consumer<T> ===");

        Consumer<String> print    = s -> System.out.println("Consuming: " + s);
        Consumer<String> printLen = s -> System.out.println("Length: " + s.length());

        print.accept("Hello");
        // Chain consumers: andThen
        Consumer<String> printBoth = print.andThen(printLen);
        printBoth.accept("World");

        // ── SUPPLIER<T> — no input, returns T ────────────────────────────────
        System.out.println("\n=== Supplier<T> ===");

        Supplier<String>  greet  = () -> "Hello, World!";
        Supplier<Double>  random = () -> Math.random();
        Supplier<List<String>> emptyList = () -> new java.util.ArrayList<>();

        System.out.println("greet:  " + greet.get());
        System.out.printf("random: %.4f%n", random.get());
        System.out.println("list:   " + emptyList.get());

        // ── BIFUNCTION<T, U, R> ───────────────────────────────────────────────
        System.out.println("\n=== BiFunction<T, U, R> ===");

        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("repeat(\"ha\", 3): " + repeat.apply("ha", 3));

        // ── METHOD REFERENCES ─────────────────────────────────────────────────
        System.out.println("\n=== Method References ===");

        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "Diana");

        // Static method reference: ClassName::staticMethod
        names.stream().map(String::toUpperCase).forEach(System.out::println);

        // Sort using method reference
        names.sort(String::compareTo);   // equivalent to (a, b) -> a.compareTo(b)
        System.out.println("Sorted: " + names);

        // Constructor reference: ClassName::new
        Supplier<StringBuilder> sbSupplier = StringBuilder::new;
        StringBuilder sb = sbSupplier.get();
        sb.append("Built with constructor reference!");
        System.out.println(sb);

        // ── LAMBDA CAPTURES VARIABLES ─────────────────────────────────────────
        System.out.println("\n=== Lambda Variable Capture ===");

        String prefix = "Dear";   // effectively final (not modified after assignment)
        Function<String, String> greetPerson = name -> prefix + " " + name;
        System.out.println(greetPerson.apply("Alice"));
        System.out.println(greetPerson.apply("Bob"));
        // prefix = "Hello"; ← would cause compile error if uncommented
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Lambda captures require effectively final variables:
 *    int x = 5;
 *    x = 10; ← makes x not effectively final
 *    Runnable r = () -> System.out.println(x); ← COMPILE ERROR
 *
 * 2. Forgetting that Runnable's run() has no return type:
 *    Runnable r = () -> return 5; ← COMPILE ERROR
 *
 * 3. Returning from lambda vs returning from the enclosing method:
 *    () -> { return 5; } ← returns from the lambda function
 *    Not the enclosing method
 *
 * 4. Complex lambdas: if your lambda is too complex, extract it to a method.
 *    Lambdas should be short and readable.
 *
 * 5. Method references work only when the signature matches the functional interface.
 *    String::length → can be used as Function<String, Integer>
 *    System.out::println → can be used as Consumer<String>
 */
