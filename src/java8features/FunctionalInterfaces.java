package java8features;

import java.util.function.*;

/*
 * =============================================================================
 * FunctionalInterfaces.java — Built-in Functional Interfaces in java.util.function
 * =============================================================================
 *
 * CONCEPT: Functional Interfaces
 * --------------------------------
 * Java 8 introduced the java.util.function package with commonly used
 * functional interfaces, so you don't have to create your own.
 *
 * CORE INTERFACES:
 *   Predicate<T>           : T -> boolean
 *   Function<T, R>         : T -> R
 *   Consumer<T>            : T -> void
 *   Supplier<T>            : () -> T
 *   UnaryOperator<T>       : T -> T (specialization of Function)
 *   BinaryOperator<T>      : (T, T) -> T
 *   BiPredicate<T, U>      : (T, U) -> boolean
 *   BiFunction<T, U, R>    : (T, U) -> R
 *   BiConsumer<T, U>       : (T, U) -> void
 *
 * PRIMITIVE SPECIALIZATIONS (avoid autoboxing overhead):
 *   IntPredicate, LongPredicate, DoublePredicate
 *   IntFunction<R>, ToIntFunction<T>, IntToLongFunction
 *   IntConsumer, LongConsumer, DoubleConsumer
 *   IntSupplier, LongSupplier, DoubleSupplier
 *   IntUnaryOperator, IntBinaryOperator, etc.
 *
 * HOW TO RUN:
 *  $ javac -d out src/java8features/FunctionalInterfaces.java
 *  $ java -cp out java8features.FunctionalInterfaces
 * =============================================================================
 */
public class FunctionalInterfaces {

    public static void main(String[] args) {

        // ── PREDICATE ─────────────────────────────────────────────────────────
        System.out.println("=== Predicate<T> ===");

        Predicate<String> notEmpty = s -> !s.isEmpty();
        Predicate<String> noSpaces = s -> !s.contains(" ");
        Predicate<String> isAlpha  = s -> s.matches("[a-zA-Z]+");

        // Combining predicates: and(), or(), negate()
        Predicate<String> validUsername = notEmpty.and(noSpaces).and(isAlpha);

        String[] testNames = {"Alice", "bob123", "hello world", "", "Java"};
        for (String name : testNames) {
            System.out.printf("%-15s valid: %b%n", "'" + name + "'", validUsername.test(name));
        }

        // ── FUNCTION ─────────────────────────────────────────────────────────
        System.out.println("\n=== Function<T, R> ===");

        Function<String, Integer> wordCount = s -> s.split("\\s+").length;
        Function<Integer, Boolean> isEven   = n -> n % 2 == 0;

        // andThen: f.andThen(g) = g(f(x)) — chain from left to right
        Function<String, Boolean> wordCountEven = wordCount.andThen(isEven);
        System.out.println("'Hello World' has even word count: " + wordCountEven.apply("Hello World"));  // false (2)
        System.out.println("'One Two Three Four' has even word count: " + wordCountEven.apply("One Two Three Four")); // true

        // compose: f.compose(g) = f(g(x)) — chain from right to left
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> addThree = x -> x + 3;
        Function<Integer, Integer> doubleThenAdd = addThree.compose(doubleIt);  // addThree(doubleIt(x))
        Function<Integer, Integer> addThenDouble = doubleIt.compose(addThree);  // doubleIt(addThree(x))

        System.out.println("doubleThenAdd(5): " + doubleThenAdd.apply(5));  // (5*2)+3 = 13
        System.out.println("addThenDouble(5): " + addThenDouble.apply(5));  // (5+3)*2 = 16

        // identity: returns input unchanged
        Function<String, String> identity = Function.identity();
        System.out.println("identity: " + identity.apply("unchanged"));

        // ── CONSUMER AND BICONSUMER ───────────────────────────────────────────
        System.out.println("\n=== Consumer<T> and BiConsumer<T, U> ===");

        Consumer<String>        print      = s  -> System.out.print(s + " ");
        Consumer<String>        printUpper = s  -> System.out.print(s.toUpperCase() + " ");
        BiConsumer<String, Integer> repeat  = (s, n) -> System.out.print(s.repeat(n) + " ");

        System.out.print("Chained consumers: ");
        print.andThen(printUpper).accept("hello");
        System.out.println();

        System.out.print("BiConsumer: ");
        repeat.accept("Ha", 3);
        System.out.println();

        // ── SUPPLIER ──────────────────────────────────────────────────────────
        System.out.println("\n=== Supplier<T> ===");

        Supplier<String> greeting  = () -> "Hello, World!";
        Supplier<Long>   timestamp = System::currentTimeMillis;  // returns a Long
        Supplier<int[]>  newArray  = () -> new int[]{1, 2, 3, 4, 5};

        System.out.println("greeting:  " + greeting.get());
        System.out.println("timestamp: " + timestamp.get());
        System.out.println("array:     " + java.util.Arrays.toString(newArray.get()));

        // ── UNARYOPERATOR ─────────────────────────────────────────────────────
        System.out.println("\n=== UnaryOperator<T> ===");

        UnaryOperator<String> trim      = String::trim;
        UnaryOperator<String> toUpper   = String::toUpperCase;
        UnaryOperator<String> exclaim   = s -> s + "!";

        // Compose operators
        UnaryOperator<String> pipeline  = s -> exclaim.apply(toUpper.apply(trim.apply(s)));

        System.out.println("pipeline('  hello  '): " + pipeline.apply("  hello  "));

        // ── BINARYOPERATOR ────────────────────────────────────────────────────
        System.out.println("\n=== BinaryOperator<T> ===");

        BinaryOperator<Integer> add = Integer::sum;
        BinaryOperator<String> concat = (a, b) -> a + b;
        BinaryOperator<Integer> max = BinaryOperator.maxBy(Integer::compareTo);

        System.out.println("add(3, 4):     " + add.apply(3, 4));
        System.out.println("concat:        " + concat.apply("Hello", " World"));
        System.out.println("max(7, 12):    " + max.apply(7, 12));

        // ── PRIMITIVE SPECIALIZATIONS ─────────────────────────────────────────
        System.out.println("\n=== Primitive Specializations ===");

        IntPredicate    isPositive    = n -> n > 0;
        IntUnaryOperator square       = n -> n * n;
        IntBinaryOperator sumInts     = Integer::sum;
        IntSupplier      randomInt    = () -> (int)(Math.random() * 100);

        System.out.println("isPositive(5):   " + isPositive.test(5));
        System.out.println("square(7):       " + square.applyAsInt(7));
        System.out.println("sumInts(3, 4):   " + sumInts.applyAsInt(3, 4));
        System.out.println("randomInt:       " + randomInt.getAsInt());

        // ── BUILDING A PROCESSING PIPELINE ───────────────────────────────────
        System.out.println("\n=== Processing Pipeline ===");

        // Validate → Transform → Consume
        Predicate<String>      validate  = s -> s != null && !s.isBlank();
        Function<String, String> transform = s -> s.trim().toUpperCase();
        Consumer<String>       consume   = s -> System.out.println("Processed: " + s);

        String[] inputs = {"  hello  ", null, "  world  ", "  ", "java8"};
        for (String input : inputs) {
            if (validate.test(input)) {
                consume.accept(transform.apply(input));
            } else {
                System.out.println("Invalid input: '" + input + "'");
            }
        }
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Confusing Predicate and Function:
 *    Predicate: returns boolean (for filtering/testing)
 *    Function:  returns any type (for transformation)
 *
 * 2. andThen vs compose order:
 *    f.andThen(g) = g(f(x))  — f first, then g
 *    f.compose(g) = f(g(x))  — g first, then f
 *
 * 3. Using boxed types with primitive streams — performance loss due to autoboxing.
 *    Use IntStream, LongStream, DoubleStream instead of Stream<Integer>, etc.
 *
 * 4. Creating functional interfaces for standard use cases that already exist in
 *    java.util.function — always check there first!
 *
 * 5. Consumer.andThen: runs both consumers; second one's return type must also be void.
 */
