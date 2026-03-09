package streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Advanced stream topics: parallelism, ordering, custom collectors, spliterator.
 */
public class StreamAdvanced {

    /**
     * Question: When to use parallel streams and what are the ordering implications?
     */
    public static void demonstrateParallelStreams() {
        System.out.println("=== Parallel Streams ===");

        List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        List<Integer> parallelResult = numbers.parallelStream()
                .map(n -> n * n)
                .collect(Collectors.toList());

        System.out.println("Parallel result (order not guaranteed): " + parallelResult);

        System.out.print("forEach (unordered): ");
        numbers.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("forEachOrdered: ");
        numbers.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
    }

    /**
     * Question: Explain Spliterator characteristics.
     */
    public static void demonstrateSpliterator() {
        System.out.println("\n=== Spliterator ===");

        List<String> items = Arrays.asList("A", "B", "C", "D");
        Spliterator<String> spliterator = items.spliterator();

        System.out.println("Estimated size: " + spliterator.estimateSize());
        System.out.println("Characteristics: " + spliterator.characteristics());

        Spliterator<String> split = spliterator.trySplit();
        System.out.println("Split created: " + (split != null));

        if (split != null) {
            System.out.print("Split part: ");
            split.forEachRemaining(s -> System.out.print(s + " "));
            System.out.println();
        }

        System.out.print("Remaining: ");
        spliterator.forEachRemaining(s -> System.out.print(s + " "));
        System.out.println();
    }

    /**
     * Question: How to build a custom collector?
     */
    public static void demonstrateCustomCollector() {
        System.out.println("\n=== Custom Collector ===");

        Collector<String, StringBuilder, String> upperJoined = Collector.of(
                StringBuilder::new,
                (sb, s) -> {
                    if (sb.length() > 0) sb.append("|");
                    sb.append(s.toUpperCase());
                },
                (left, right) -> {
                    if (left.length() > 0 && right.length() > 0) left.append("|");
                    left.append(right);
                    return left;
                },
                StringBuilder::toString
        );

        String result = Stream.of("alpha", "beta", "gamma")
                .collect(upperJoined);

        System.out.println("Custom collected string: " + result);
    }

    /**
     * Question: What are stateful vs stateless operations?
     */
    public static void demonstrateStatefulOperations() {
        System.out.println("\n=== Stateful vs Stateless ===");

        List<Integer> numbers = Arrays.asList(5, 1, 3, 2, 4);

        List<Integer> stateless = numbers.stream()
                .map(n -> n * 2) // stateless
                .collect(Collectors.toList());

        List<Integer> stateful = numbers.stream()
                .sorted() // stateful
                .collect(Collectors.toList());

        System.out.println("Stateless (map): " + stateless);
        System.out.println("Stateful (sorted): " + stateful);
    }

    /**
     * Question: Why prefer Collectors.toList() over mutable reduction with side effects?
     */
    public static void demonstrateSideEffects() {
        System.out.println("\n=== Side Effects in Streams ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> safe = numbers.stream()
                .map(n -> n * 2)
                .collect(Collectors.toList());

        List<Integer> unsafe = new ArrayList<>();
        numbers.stream().forEach(n -> unsafe.add(n * 2));

        System.out.println("Safe collect: " + safe);
        System.out.println("Side-effect list: " + unsafe);
    }

    public static void main(String[] args) {
        demonstrateParallelStreams();
        demonstrateSpliterator();
        demonstrateCustomCollector();
        demonstrateStatefulOperations();
        demonstrateSideEffects();
    }
}

