package collections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Advanced Collections Utilities and Best Practices
 * Topics: Collections class, Arrays class, Immutable collections,
 * Unmodifiable views, Synchronized wrappers, Empty collections
 */
public class CollectionUtilities {

    /**
     * Question 1: What are the different ways to create immutable collections?
     */
    public static void demonstrateImmutableCollections() {
        System.out.println("=== Immutable Collections ===");

        // Java 9+ - Immutable collections (truly immutable)
        List<String> immutableList = List.of("A", "B", "C");
        Set<String> immutableSet = Set.of("X", "Y", "Z");
        Map<String, Integer> immutableMap = Map.of("One", 1, "Two", 2);

        System.out.println("List.of(): " + immutableList);
        System.out.println("Set.of(): " + immutableSet);
        System.out.println("Map.of(): " + immutableMap);

        // Java 10+ - copyOf (creates immutable copy)
        List<String> mutable = new ArrayList<>(Arrays.asList("P", "Q", "R"));
        List<String> copy = List.copyOf(mutable);
        System.out.println("List.copyOf(): " + copy);

        // Unmodifiable view (wraps existing collection)
        List<String> unmodifiable = Collections.unmodifiableList(mutable);
        mutable.add("S"); // Affects unmodifiable view!
        System.out.println("After modifying source: " + unmodifiable);

        System.out.println("\nKey differences:");
        System.out.println("List.of() - Truly immutable, no nulls, compact");
        System.out.println("Collections.unmodifiableList() - View, reflects changes");

        try {
            immutableList.add("D");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify List.of() collections");
        }
    }

    /**
     * Question 2: Explain Collections utility methods
     */
    public static void demonstrateCollectionsUtilities() {
        System.out.println("\n=== Collections Utility Methods ===");

        List<Integer> list = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));

        // Sorting
        Collections.sort(list);
        System.out.println("After sort: " + list);

        Collections.sort(list, Collections.reverseOrder());
        System.out.println("Reverse sort: " + list);

        // Shuffling
        Collections.shuffle(list);
        System.out.println("After shuffle: " + list);

        // Rotating
        Collections.rotate(list, 2);
        System.out.println("After rotate(2): " + list);

        // Reversing
        Collections.reverse(list);
        System.out.println("After reverse: " + list);

        // Binary search (requires sorted list)
        Collections.sort(list);
        int index = Collections.binarySearch(list, 5);
        System.out.println("Binary search for 5: index " + index);

        // Min/Max
        System.out.println("min(): " + Collections.min(list));
        System.out.println("max(): " + Collections.max(list));

        // Frequency
        list.add(2);
        list.add(2);
        System.out.println("Frequency of 2: " + Collections.frequency(list, 2));

        // Fill
        Collections.fill(list, 0);
        System.out.println("After fill(0): " + list);

        // Replace all
        list = new ArrayList<>(Arrays.asList(1, 2, 3, 2, 4, 2));
        Collections.replaceAll(list, 2, 99);
        System.out.println("After replaceAll(2, 99): " + list);
    }

    /**
     * Question 3: When to use synchronized wrappers? Are they truly thread-safe?
     */
    public static void demonstrateSynchronizedCollections() {
        System.out.println("\n=== Synchronized Collections ===");

        List<String> list = new ArrayList<>();
        Set<String> set = new HashSet<>();
        Map<String, Integer> map = new HashMap<>();

        // Synchronized wrappers
        List<String> syncList = Collections.synchronizedList(list);
        Set<String> syncSet = Collections.synchronizedSet(set);
        Map<String, Integer> syncMap = Collections.synchronizedMap(map);

        System.out.println("Synchronized collections wrap each method with synchronized block");

        // IMPORTANT: Iteration still needs manual synchronization!
        syncList.add("A");
        syncList.add("B");
        syncList.add("C");

        System.out.println("\nManual synchronization needed for iteration:");
        synchronized (syncList) {
            for (String item : syncList) {
                System.out.println(item);
            }
        }

        System.out.println("\nLimitations:");
        System.out.println("- Coarse-grained locking (entire collection)");
        System.out.println("- Poor performance under contention");
        System.out.println("- Compound operations not atomic");
        System.out.println("\nPrefer: ConcurrentHashMap, CopyOnWriteArrayList, etc.");
    }

    /**
     * Question 4: Explain empty collections and singleton collections
     */
    public static void demonstrateEmptyAndSingletons() {
        System.out.println("\n=== Empty and Singleton Collections ===");

        // Empty collections (immutable, shared instances)
        List<String> emptyList = Collections.emptyList();
        Set<String> emptySet = Collections.emptySet();
        Map<String, Integer> emptyMap = Collections.emptyMap();

        System.out.println("Empty collections are immutable and reuse same instance");
        System.out.println("emptyList() == emptyList(): " + (Collections.emptyList() == Collections.emptyList()));

        // Singleton collections (immutable)
        List<String> singletonList = Collections.singletonList("OnlyOne");
        Set<String> singletonSet = Collections.singleton("OnlyOne");
        Map<String, Integer> singletonMap = Collections.singletonMap("Key", 1);

        System.out.println("Singleton: " + singletonList);

        // Use cases
        System.out.println("\nUse cases:");
        System.out.println("- Return empty collection instead of null");
        System.out.println("- Default values, avoiding NullPointerException");
        System.out.println("- Memory efficient (shared instance)");

        // Example: Safe method return
        List<String> result = getItems(false);
        System.out.println("Safe iteration: " + result.size() + " items");
    }

    private static List<String> getItems(boolean hasData) {
        return hasData ? Arrays.asList("Item1", "Item2") : Collections.emptyList();
    }

    /**
     * Question 5: Demonstrate checked collections (type-safety at runtime)
     */
    public static void demonstrateCheckedCollections() {
        System.out.println("\n=== Checked Collections ===");

        List<String> list = new ArrayList<>();
        List<String> checkedList = Collections.checkedList(list, String.class);

        checkedList.add("Valid String");

        // Without checked wrapper, this can cause ClassCastException later
        @SuppressWarnings("unchecked")
        List rawList = list;
        // rawList.add(123); // Would compile but fail at runtime later

        System.out.println("Checked collections throw ClassCastException immediately");
        System.out.println("Useful when interfacing with legacy code");

        try {
            @SuppressWarnings("unchecked")
            List rawChecked = checkedList;
            rawChecked.add(123); // Fails immediately
        } catch (ClassCastException e) {
            System.out.println("ClassCastException caught: " + e.getMessage());
        }
    }

    /**
     * Question 6: What is Collections.nCopies() and when to use it?
     */
    public static void demonstrateNCopies() {
        System.out.println("\n=== Collections.nCopies() ===");

        List<String> copies = Collections.nCopies(5, "Repeat");
        System.out.println("nCopies(5, 'Repeat'): " + copies);

        // Efficient - doesn't actually create 5 objects
        System.out.println("Memory efficient - single object reference repeated");

        // Use case: Initialize with default values
        List<Integer> list = new ArrayList<>(Collections.nCopies(10, 0));
        System.out.println("Initialized list: " + list);

        // Use case: Padding
        List<String> data = new ArrayList<>(Arrays.asList("A", "B"));
        data.addAll(Collections.nCopies(3, "-"));
        System.out.println("Padded: " + data);
    }

    /**
     * Question 7: Explain Collections.disjoint(), addAll(), and other bulk operations
     */
    public static void demonstrateBulkOperations() {
        System.out.println("\n=== Collections Bulk Operations ===");

        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(4, 5, 6, 7, 8);
        List<Integer> list3 = Arrays.asList(10, 11, 12);

        // disjoint - check if no common elements
        boolean disjoint = Collections.disjoint(list1, list2);
        System.out.println("list1 and list2 disjoint? " + disjoint);

        disjoint = Collections.disjoint(list1, list3);
        System.out.println("list1 and list3 disjoint? " + disjoint);

        // addAll - add multiple elements
        List<String> list = new ArrayList<>();
        Collections.addAll(list, "A", "B", "C", "D");
        System.out.println("After addAll: " + list);

        // copy - copy elements (destination must be large enough)
        List<String> dest = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
        List<String> src = Arrays.asList("X", "Y");
        Collections.copy(dest, src);
        System.out.println("After copy: " + dest);
    }

    /**
     * Question 8: Demonstrate Arrays utility class
     */
    public static void demonstrateArraysUtilities() {
        System.out.println("\n=== Arrays Utility Class ===");

        int[] arr = {5, 2, 8, 1, 9};

        // Sort
        Arrays.sort(arr);
        System.out.println("After sort: " + Arrays.toString(arr));

        // Binary search
        int index = Arrays.binarySearch(arr, 8);
        System.out.println("Binary search for 8: " + index);

        // Fill
        int[] filled = new int[5];
        Arrays.fill(filled, 7);
        System.out.println("Filled array: " + Arrays.toString(filled));

        // Equals
        int[] arr2 = {1, 2, 5, 8, 9};
        System.out.println("Arrays equal? " + Arrays.equals(arr, arr2));

        // Deep equals (for multi-dimensional arrays)
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{1, 2}, {3, 4}};
        System.out.println("Deep equals? " + Arrays.deepEquals(matrix1, matrix2));

        // asList - bridge to collections
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("Arrays.asList(): " + list);
        System.out.println("Note: Fixed-size, backed by array");

        // copyOf - create new array
        int[] copy = Arrays.copyOf(arr, arr.length);
        int[] extended = Arrays.copyOf(arr, arr.length + 3);
        System.out.println("Copy: " + Arrays.toString(copy));
        System.out.println("Extended: " + Arrays.toString(extended));

        // Stream API
        int sum = Arrays.stream(arr).sum();
        System.out.println("Sum using stream: " + sum);
    }

    /**
     * Question 9: What are the pitfalls of Arrays.asList()?
     */
    public static void demonstrateAsListPitfalls() {
        System.out.println("\n=== Arrays.asList() Pitfalls ===");

        // Pitfall 1: Fixed-size
        List<String> list = Arrays.asList("A", "B", "C");
        try {
            list.add("D"); // UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("Pitfall 1: Cannot add/remove (fixed-size)");
        }

        // But can modify existing elements
        list.set(0, "Z");
        System.out.println("Can modify: " + list);

        // Pitfall 2: Primitive arrays
        int[] primitives = {1, 2, 3};
        List<int[]> wrongList = Arrays.asList(primitives); // List of array, not integers!
        System.out.println("Pitfall 2: Primitive array size: " + wrongList.size()); // 1, not 3

        // Correct way for primitives
        List<Integer> correctList = Arrays.stream(primitives).boxed().collect(Collectors.toList());
        System.out.println("Correct primitive list: " + correctList);

        // Solution: Create mutable copy
        List<String> mutableList = new ArrayList<>(Arrays.asList("A", "B", "C"));
        mutableList.add("D");
        System.out.println("Mutable copy: " + mutableList);
    }

    /**
     * Question 10: Demonstrate modern collection factories vs traditional methods
     */
    public static void demonstrateCollectionFactories() {
        System.out.println("\n=== Collection Factories Comparison ===");

        // Traditional way
        List<String> traditional = new ArrayList<>();
        traditional.add("A");
        traditional.add("B");
        traditional.add("C");

        // Double brace initialization (DON'T use - creates anonymous class)
        @SuppressWarnings("unused")
        List<String> doubleBrace = new ArrayList<String>() {{
            add("A");
            add("B");
            add("C");
        }};

        // Arrays.asList (fixed-size)
        List<String> arraysList = Arrays.asList("A", "B", "C");

        // Stream collectors
        List<String> stream = Stream.of("A", "B", "C").collect(Collectors.toList());

        // Java 9+ factory methods (immutable, preferred)
        List<String> listOf = List.of("A", "B", "C");
        Set<String> setOf = Set.of("A", "B", "C");
        Map<String, Integer> mapOf = Map.of("A", 1, "B", 2, "C", 3);

        // For larger maps
        Map<String, Integer> mapOfEntries = Map.ofEntries(
            Map.entry("A", 1),
            Map.entry("B", 2),
            Map.entry("C", 3),
            Map.entry("D", 4)
        );

        System.out.println("Recommendation:");
        System.out.println("- Immutable: Use List.of(), Set.of(), Map.of()");
        System.out.println("- Mutable: Use new ArrayList<>(List.of(...))");
        System.out.println("- Avoid double brace initialization");
        System.out.println("- Arrays.asList() for varargs to fixed-size list");
    }

    public static void main(String[] args) {
        demonstrateImmutableCollections();
        demonstrateCollectionsUtilities();
        demonstrateSynchronizedCollections();
        demonstrateEmptyAndSingletons();
        demonstrateCheckedCollections();
        demonstrateNCopies();
        demonstrateBulkOperations();
        demonstrateArraysUtilities();
        demonstrateAsListPitfalls();
        demonstrateCollectionFactories();
    }
}

