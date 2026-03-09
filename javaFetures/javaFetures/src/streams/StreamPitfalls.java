package streams;

import java.util.*;
import java.util.stream.*;

/**
 * Stream pitfalls that show up in senior interviews.
 */
public class StreamPitfalls {

    /**
     * Question: Why can't you reuse a stream?
     */
    public static void demonstrateReusePitfall() {
        System.out.println("=== Stream Reuse Pitfall ===");

        Stream<String> stream = Stream.of("A", "B", "C");
        long count = stream.count();
        System.out.println("Count: " + count);

        try {
            stream.forEach(System.out::println);
        } catch (IllegalStateException e) {
            System.out.println("Stream reuse error: " + e.getMessage());
        }
    }

    /**
     * Question: Why are side effects risky in parallel streams?
     */
    public static void demonstrateSideEffectsInParallel() {
        System.out.println("\n=== Side Effects in Parallel Streams ===");

        List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        List<Integer> shared = Collections.synchronizedList(new ArrayList<>());

        numbers.parallelStream().forEach(n -> shared.add(n));
        System.out.println("Size with side effects: " + shared.size());

        List<Integer> safe = numbers.parallelStream().collect(Collectors.toList());
        System.out.println("Size with collect: " + safe.size());
    }

    /**
     * Question: Explain ordering pitfalls with parallel streams.
     */
    public static void demonstrateOrderingPitfall() {
        System.out.println("\n=== Ordering Pitfall ===");

        List<Integer> numbers = IntStream.rangeClosed(1, 8).boxed().collect(Collectors.toList());

        System.out.print("parallel forEach: ");
        numbers.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("parallel forEachOrdered: ");
        numbers.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
    }

    /**
     * Question: Why are stateful lambdas problematic?
     */
    public static void demonstrateStatefulLambda() {
        System.out.println("\n=== Stateful Lambda Pitfall ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> accumulator = new ArrayList<>();

        numbers.stream().map(n -> {
            accumulator.add(n); // side effect
            return n * 2;
        }).collect(Collectors.toList());

        System.out.println("Accumulator mutated by map: " + accumulator);
    }

    public static void main(String[] args) {
        demonstrateReusePitfall();
        demonstrateSideEffectsInParallel();
        demonstrateOrderingPitfall();
        demonstrateStatefulLambda();
    }
}

