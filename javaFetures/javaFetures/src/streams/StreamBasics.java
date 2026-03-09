package streams;

import java.util.*;
import java.util.stream.*;

/**
 * Stream fundamentals for senior interviews.
 * Focus: creation, pipeline stages, lazy evaluation, primitives, reduce.
 */
public class StreamBasics {

    /**
     * Question: How do you create streams, and when are they consumed?
     */
    public static void demonstrateCreationAndLaziness() {
        System.out.println("=== Stream Creation and Laziness ===");

        Stream<String> fromValues = Stream.of("A", "B", "C");
        Stream<String> fromCollection = Arrays.asList("X", "Y").stream();
        Stream<Integer> generated = Stream.generate(() -> 42).limit(3);
        Stream<Integer> iterated = Stream.iterate(1, n -> n + 1).limit(5);
        Stream<String> nullable = Stream.ofNullable(null);
        IntStream range = IntStream.range(1, 4); // 1,2,3

        System.out.println("From values: " + fromValues.collect(Collectors.toList()));
        System.out.println("From collection: " + fromCollection.collect(Collectors.toList()));
        System.out.println("Generated: " + generated.collect(Collectors.toList()));
        System.out.println("Iterated: " + iterated.collect(Collectors.toList()));
        System.out.println("ofNullable(null) count: " + nullable.count());
        System.out.println("IntStream range: " + range.boxed().collect(Collectors.toList()));

        List<String> names = Arrays.asList("Ann", "Ben", "Cara");
        Stream<String> lazy = names.stream().filter(s -> {
            System.out.println("filter: " + s);
            return s.startsWith("C");
        });
        System.out.println("No output above because the stream is lazy until terminal operation.");
        System.out.println("Terminal result: " + lazy.collect(Collectors.toList()));
    }

    /**
     * Question: Explain intermediate vs terminal operations.
     */
    public static void demonstratePipelineStages() {
        System.out.println("\n=== Intermediate vs Terminal Operations ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> result = numbers.stream()
                .filter(n -> n % 2 == 0)      // intermediate
                .map(n -> n * n)              // intermediate
                .sorted()                     // intermediate
                .collect(Collectors.toList()); // terminal

        System.out.println("Pipeline result: " + result);
        System.out.println("Rule: stream can be consumed only once.");
    }

    /**
     * Question: When to use map vs flatMap?
     */
    public static void demonstrateMapAndFlatMap() {
        System.out.println("\n=== map vs flatMap ===");

        List<String> words = Arrays.asList("java", "stream");
        List<String[]> mapped = words.stream()
                .map(w -> w.split(""))
                .collect(Collectors.toList());

        List<String> flatMapped = words.stream()
                .flatMap(w -> Arrays.stream(w.split("")))
                .collect(Collectors.toList());

        System.out.println("map produces: " + mapped.size() + " arrays");
        System.out.println("flatMap produces: " + flatMapped);
    }

    /**
     * Question: Explain reduce and why it can be tricky in parallel.
     */
    public static void demonstrateReduce() {
        System.out.println("\n=== reduce ===");

        List<Integer> values = Arrays.asList(1, 2, 3, 4);

        int sum = values.stream().reduce(0, Integer::sum);
        Optional<Integer> product = values.stream().reduce((a, b) -> a * b);

        System.out.println("Sum (identity + accumulator): " + sum);
        System.out.println("Product (no identity): " + product.orElse(-1));

        int parallelSum = values.parallelStream().reduce(0, Integer::sum, Integer::sum);
        System.out.println("Parallel sum with combiner: " + parallelSum);
    }

    /**
     * Question: Why use primitive streams?
     */
    public static void demonstratePrimitiveStreams() {
        System.out.println("\n=== Primitive Streams ===");

        IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
        IntSummaryStatistics stats = intStream.summaryStatistics();

        System.out.println("Count: " + stats.getCount());
        System.out.println("Sum: " + stats.getSum());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
        System.out.println("Average: " + stats.getAverage());

        List<Integer> boxed = IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList());
        System.out.println("Boxed: " + boxed);
    }

    /**
     * Question: Short-circuiting operations and their impact.
     */
    public static void demonstrateShortCircuiting() {
        System.out.println("\n=== Short-Circuiting Operations ===");

        List<String> items = Arrays.asList("alpha", "beta", "gamma");

        boolean anyStartsWithB = items.stream().anyMatch(s -> s.startsWith("b"));
        Optional<String> firstLong = items.stream().filter(s -> s.length() > 4).findFirst();
        long limitedCount = items.stream().limit(2).count();

        System.out.println("anyMatch startsWith b: " + anyStartsWithB);
        System.out.println("findFirst length > 4: " + firstLong.orElse("<none>"));
        System.out.println("limit(2) count: " + limitedCount);
    }

    public static void main(String[] args) {
        demonstrateCreationAndLaziness();
        demonstratePipelineStages();
        demonstrateMapAndFlatMap();
        demonstrateReduce();
        demonstratePrimitiveStreams();
        demonstrateShortCircuiting();
    }
}

