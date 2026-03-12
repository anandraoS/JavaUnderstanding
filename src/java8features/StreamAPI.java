package java8features;

import java.util.*;
import java.util.stream.*;

/*
 * =============================================================================
 * StreamAPI.java — Java 8 Stream API for Data Processing
 * =============================================================================
 *
 * CONCEPT: Stream API
 * --------------------
 * Streams provide a functional, declarative way to process collections of data.
 * Think of a stream as a pipeline: data flows in, gets transformed, flows out.
 *
 * STREAM PIPELINE:
 *   Source → Intermediate Operations (lazy) → Terminal Operation (eager)
 *
 *   source.intermediateOp1().intermediateOp2().terminalOp()
 *
 * KEY POINTS:
 *  - Streams do NOT store data (they process it)
 *  - Operations are LAZY: intermediate ops run only when terminal op is called
 *  - Streams can only be consumed ONCE
 *  - Streams don't modify the original collection
 *
 * SOURCES: Collection.stream(), Arrays.stream(), Stream.of(), Stream.generate()
 *
 * INTERMEDIATE OPERATIONS (return a Stream — lazy):
 *   filter(Predicate)    — keep elements matching condition
 *   map(Function)        — transform each element
 *   flatMap(Function)    — flatten nested collections
 *   sorted()             — sort elements
 *   distinct()           — remove duplicates
 *   limit(n)             — take first n elements
 *   skip(n)              — skip first n elements
 *   peek(Consumer)       — inspect elements without consuming (debug)
 *
 * TERMINAL OPERATIONS (consume Stream — eager):
 *   forEach(Consumer)    — iterate and consume
 *   collect(Collector)   — gather into collection
 *   count()              — count elements
 *   findFirst()          — return first element (Optional)
 *   findAny()            — return any element (Optional)
 *   reduce(...)          — combine elements into one value
 *   min(Comparator)      — find minimum
 *   max(Comparator)      — find maximum
 *   anyMatch(Predicate)  — true if ANY element matches
 *   allMatch(Predicate)  — true if ALL elements match
 *   noneMatch(Predicate) — true if NO element matches
 *
 * HOW TO RUN:
 *  $ javac -d out src/java8features/StreamAPI.java
 *  $ java -cp out java8features.StreamAPI
 * =============================================================================
 */
public class StreamAPI {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank");

        // ── FILTER ───────────────────────────────────────────────────────────
        System.out.println("=== filter() ===");

        List<Integer> evens = numbers.stream()
            .filter(n -> n % 2 == 0)   // keep only even numbers
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evens);

        List<String> longNames = names.stream()
            .filter(s -> s.length() > 3)
            .collect(Collectors.toList());
        System.out.println("Names > 3 chars: " + longNames);

        // ── MAP ───────────────────────────────────────────────────────────────
        System.out.println("\n=== map() ===");

        List<Integer> squares = numbers.stream()
            .map(n -> n * n)            // transform: square each number
            .collect(Collectors.toList());
        System.out.println("Squares: " + squares);

        List<String> upperNames = names.stream()
            .map(String::toUpperCase)   // method reference
            .collect(Collectors.toList());
        System.out.println("Upper names: " + upperNames);

        List<Integer> nameLengths = names.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("Name lengths: " + nameLengths);

        // ── SORTED AND DISTINCT ───────────────────────────────────────────────
        System.out.println("\n=== sorted() and distinct() ===");

        List<Integer> nums = Arrays.asList(5, 2, 8, 2, 1, 9, 5, 3);
        List<Integer> sortedDistinct = nums.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Sorted distinct: " + sortedDistinct);

        // Sorted with comparator (reverse order)
        List<String> sortedDesc = names.stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        System.out.println("Names desc: " + sortedDesc);

        // ── LIMIT AND SKIP ────────────────────────────────────────────────────
        System.out.println("\n=== limit() and skip() ===");

        List<Integer> first5 = numbers.stream().limit(5).collect(Collectors.toList());
        List<Integer> skip3  = numbers.stream().skip(3).collect(Collectors.toList());
        List<Integer> middle = numbers.stream().skip(2).limit(5).collect(Collectors.toList());

        System.out.println("First 5: " + first5);
        System.out.println("Skip 3:  " + skip3);
        System.out.println("Middle (skip2, limit5): " + middle);

        // ── REDUCE ────────────────────────────────────────────────────────────
        System.out.println("\n=== reduce() ===");

        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);   // 0 is identity, (a,b)->a+b is accumulator
        System.out.println("Sum: " + sum);

        int product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);

        Optional<Integer> max = numbers.stream()
            .reduce(Integer::max);   // method reference
        System.out.println("Max: " + max.orElse(-1));

        // ── COLLECTORS ────────────────────────────────────────────────────────
        System.out.println("\n=== collect() and Collectors ===");

        // Collect to different types
        Set<Integer> evenSet = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toSet());
        System.out.println("Even set: " + evenSet);

        // Join strings
        String joined = names.stream()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Joined: " + joined);

        // Count, sum, avg, min, max
        IntSummaryStatistics stats = numbers.stream()
            .collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("Stats: count=" + stats.getCount()
            + ", sum=" + stats.getSum()
            + ", avg=" + stats.getAverage()
            + ", min=" + stats.getMin()
            + ", max=" + stats.getMax());

        // Group by
        Map<Integer, List<String>> groupedByLength = names.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("Grouped by length: " + groupedByLength);

        // ── MATCH OPERATIONS ──────────────────────────────────────────────────
        System.out.println("\n=== Match Operations ===");
        System.out.println("anyMatch > 5:   " + numbers.stream().anyMatch(n -> n > 5));
        System.out.println("allMatch > 0:   " + numbers.stream().allMatch(n -> n > 0));
        System.out.println("noneMatch < 0:  " + numbers.stream().noneMatch(n -> n < 0));

        // ── FLATMAP ───────────────────────────────────────────────────────────
        System.out.println("\n=== flatMap() ===");

        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8, 9)
        );
        List<Integer> flattened = nested.stream()
            .flatMap(List::stream)   // flatten List<List<Integer>> to List<Integer>
            .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);

        // ── STREAM.GENERATE AND ITERATE ───────────────────────────────────────
        System.out.println("\n=== Stream.generate() and iterate() ===");

        List<Double> randoms = Stream.generate(Math::random)
            .limit(5)
            .collect(Collectors.toList());
        System.out.printf("5 randoms: %s%n", randoms.stream()
            .map(d -> String.format("%.3f", d))
            .collect(Collectors.joining(", ")));

        List<Integer> fibonacci = Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
            .limit(8)
            .map(f -> f[0])
            .collect(Collectors.toList());
        System.out.println("Fibonacci: " + fibonacci);
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Reusing a consumed stream → IllegalStateException
 *    Stream<T> s = list.stream(); s.forEach(...); s.filter(...) ← ILLEGAL!
 *    Create a new stream each time.
 *
 * 2. Not calling a terminal operation — intermediate ops are lazy!
 *    list.stream().filter(x -> x > 5); ← nothing happens without terminal op!
 *
 * 3. Modifying the source collection inside a stream operation → unpredictable behavior
 *
 * 4. Using forEach for transformation instead of map:
 *    stream.forEach(x -> result.add(x.toUpperCase())); ← anti-pattern
 *    Use map() and collect() instead.
 *
 * 5. Overusing parallel streams: parallelStream() is not always faster!
 *    It has overhead for splitting and merging. Use only for large data sets.
 */
