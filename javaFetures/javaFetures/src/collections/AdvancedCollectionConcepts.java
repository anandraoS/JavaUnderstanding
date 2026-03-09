package collections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced Collections Performance and Best Practices
 * Topics: Performance optimization, capacity planning, bulk operations,
 * Collection views, Comparator chains, Custom implementations
 */
public class AdvancedCollectionConcepts {

    /**
     * Question 1: How to optimize collection performance with capacity planning?
     */
    public static void demonstrateCapacityPlanning() {
        System.out.println("=== Capacity Planning ===");

        // Bad: Multiple resizing operations
        long start = System.nanoTime();
        List<Integer> badList = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            badList.add(i);
        }
        long badTime = System.nanoTime() - start;

        // Good: Pre-sized
        start = System.nanoTime();
        List<Integer> goodList = new ArrayList<>(100000);
        for (int i = 0; i < 100000; i++) {
            goodList.add(i);
        }
        long goodTime = System.nanoTime() - start;

        System.out.println("Without capacity: " + badTime / 1_000_000 + " ms");
        System.out.println("With capacity: " + goodTime / 1_000_000 + " ms");
        System.out.println("Speedup: " + (badTime / (double) goodTime) + "x");

        // HashMap capacity calculation
        int expectedSize = 1000;
        int capacity = (int) (expectedSize / 0.75) + 1;
        Map<Integer, String> map = new HashMap<>(capacity);
        System.out.println("\nHashMap capacity for 1000 entries: " + capacity);

        // HashSet with capacity
        Set<String> set = new HashSet<>(expectedSize * 4 / 3 + 1);
        System.out.println("HashSet capacity for 1000 entries: " + (expectedSize * 4 / 3 + 1));
    }

    /**
     * Question 2: Explain collection views - subList, keySet, values, entrySet
     * Are they backed by original collection?
     */
    public static void demonstrateCollectionViews() {
        System.out.println("\n=== Collection Views ===");

        // SubList view
        List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> subList = list.subList(2, 7); // [2, 7)

        System.out.println("Original list: " + list);
        System.out.println("SubList(2, 7): " + subList);

        // Modifications to subList affect original
        subList.set(0, 99);
        System.out.println("After subList.set(0, 99):");
        System.out.println("Original: " + list);
        System.out.println("SubList: " + subList);

        // Clear subList
        subList.clear();
        System.out.println("After subList.clear():");
        System.out.println("Original: " + list);

        // Map views
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        Set<String> keySet = map.keySet();
        Collection<Integer> values = map.values();
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

        System.out.println("\nMap views:");
        System.out.println("keySet: " + keySet);
        System.out.println("values: " + values);

        // Remove from keySet affects map
        keySet.remove("B");
        System.out.println("After keySet.remove('B'): " + map);

        // Iterate and remove via entrySet
        entrySet.removeIf(entry -> entry.getValue() == 1);
        System.out.println("After entrySet removeIf: " + map);

        System.out.println("\nViews are backed by original collection!");
    }

    /**
     * Question 3: Demonstrate advanced Comparator usage
     */
    public static void demonstrateAdvancedComparators() {
        System.out.println("\n=== Advanced Comparators ===");

        class Employee {
            String name;
            int age;
            double salary;

            Employee(String name, int age, double salary) {
                this.name = name;
                this.age = age;
                this.salary = salary;
            }

            @Override
            public String toString() {
                return String.format("%s(age=%d, salary=%.0f)", name, age, salary);
            }
        }

        List<Employee> employees = Arrays.asList(
            new Employee("Alice", 30, 70000),
            new Employee("Bob", 25, 60000),
            new Employee("Charlie", 30, 75000),
            new Employee("David", 25, 60000)
        );

        // Complex comparator chain
        Comparator<Employee> comparator = Comparator
            .comparing((Employee e) -> e.age) // Primary: age
            .thenComparing(Comparator.comparing((Employee e) -> e.salary).reversed()) // Secondary: salary desc
            .thenComparing(e -> e.name); // Tertiary: name

        employees.sort(comparator);
        System.out.println("Sorted (age ASC, salary DESC, name ASC):");
        employees.forEach(System.out::println);

        // Null-safe comparator
        List<String> withNulls = Arrays.asList("Zebra", null, "Apple", null, "Mango");
        withNulls.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("\nNull-safe sort: " + withNulls);

        // Custom comparator with nullsLast
        withNulls.sort(Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        System.out.println("Nulls last: " + withNulls);
    }

    /**
     * Question 4: Explain internal iteration vs external iteration
     */
    public static void demonstrateInternalVsExternalIteration() {
        System.out.println("\n=== Internal vs External Iteration ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // External iteration (traditional for loop)
        System.out.println("External iteration:");
        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) % 2 == 0) {
                System.out.print(numbers.get(i) + " ");
            }
        }

        // External iteration (enhanced for loop)
        System.out.println("\nExternal (enhanced for):");
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                System.out.print(num + " ");
            }
        }

        // Internal iteration (forEach)
        System.out.println("\nInternal (forEach):");
        numbers.forEach(num -> {
            if (num % 2 == 0) {
                System.out.print(num + " ");
            }
        });

        // Internal iteration (stream)
        System.out.println("\nInternal (stream):");
        numbers.stream()
               .filter(num -> num % 2 == 0)
               .forEach(num -> System.out.print(num + " "));

        System.out.println("\n\nInternal iteration advantages:");
        System.out.println("- Easier to parallelize");
        System.out.println("- More declarative");
        System.out.println("- Library controls iteration");
    }

    /**
     * Question 5: How to implement a custom collection class?
     */
    public static void demonstrateCustomCollection() {
        System.out.println("\n=== Custom Collection Implementation ===");

        // Ring buffer implementation
        class RingBuffer<E> extends AbstractList<E> {
            private final Object[] buffer;
            private int head = 0;
            private int size = 0;

            RingBuffer(int capacity) {
                buffer = new Object[capacity];
            }

            @Override
            public boolean add(E element) {
                int index = (head + size) % buffer.length;
                buffer[index] = element;

                if (size < buffer.length) {
                    size++;
                } else {
                    head = (head + 1) % buffer.length;
                }
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E get(int index) {
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException();
                }
                int actualIndex = (head + index) % buffer.length;
                return (E) buffer[actualIndex];
            }

            @Override
            public int size() {
                return size;
            }
        }

        RingBuffer<Integer> ring = new RingBuffer<>(5);

        for (int i = 1; i <= 7; i++) {
            ring.add(i);
            System.out.println("Added " + i + ", buffer: " + ring);
        }

        System.out.println("Final size: " + ring.size());
        System.out.println("Extends AbstractList - gets all Collection methods!");
    }

    /**
     * Question 6: Explain structural modification and ConcurrentModificationException
     */
    public static void demonstrateStructuralModification() {
        System.out.println("\n=== Structural Modification ===");

        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        // Structural modification: add, remove, clear
        // Non-structural: set (replace)

        System.out.println("Structural modifications: add, remove, clear");
        System.out.println("Non-structural: set");

        // modCount tracking
        try {
            Iterator<Integer> iter = list.iterator();
            list.add(6); // Changes modCount
            iter.next(); // Throws ConcurrentModificationException
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException: modCount changed");
        }

        // Safe modification
        list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Iterator<Integer> safeIter = list.iterator();
        while (safeIter.hasNext()) {
            Integer num = safeIter.next();
            if (num == 3) {
                safeIter.remove(); // Safe
            }
        }
        System.out.println("After safe removal: " + list);
    }

    /**
     * Question 7: What are the performance characteristics of common operations?
     */
    public static void demonstratePerformanceCharacteristics() {
        System.out.println("\n=== Performance Characteristics ===");

        System.out.println("ArrayList:");
        System.out.println("  get(i): O(1), add(e): O(1) amortized, add(i,e): O(n), remove(i): O(n)");

        System.out.println("\nLinkedList:");
        System.out.println("  get(i): O(n), add(e): O(1), add(i,e): O(n), remove(i): O(n)");

        System.out.println("\nHashSet/HashMap:");
        System.out.println("  add/remove/contains: O(1) average, O(n) worst case");

        System.out.println("\nTreeSet/TreeMap:");
        System.out.println("  add/remove/contains: O(log n)");

        System.out.println("\nPriorityQueue:");
        System.out.println("  add: O(log n), poll: O(log n), peek: O(1)");

        System.out.println("\nArrayDeque:");
        System.out.println("  addFirst/addLast: O(1), removeFirst/removeLast: O(1)");

        // Practical demonstration
        int n = 100000;

        // ArrayList get
        List<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < n; i++) arrayList.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayList.get(i);
        }
        long arrayGetTime = System.nanoTime() - start;

        // LinkedList get
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < n; i++) linkedList.add(i);

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedList.get(i);
        }
        long linkedGetTime = System.nanoTime() - start;

        System.out.println("\nPractical test (10000 random accesses):");
        System.out.println("ArrayList: " + arrayGetTime / 1000 + " μs");
        System.out.println("LinkedList: " + linkedGetTime / 1000 + " μs");
        System.out.println("Ratio: " + (linkedGetTime / (double) arrayGetTime) + "x slower");
    }

    /**
     * Question 8: Demonstrate collection conversion best practices
     */
    public static void demonstrateCollectionConversions() {
        System.out.println("\n=== Collection Conversions ===");

        // List to Set
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 3, 4);
        Set<Integer> set = new HashSet<>(list);
        System.out.println("List to Set: " + set);

        // Set to List
        List<Integer> listFromSet = new ArrayList<>(set);
        System.out.println("Set to List: " + listFromSet);

        // Array to List
        String[] array = {"A", "B", "C"};
        List<String> listFromArray = new ArrayList<>(Arrays.asList(array));
        System.out.println("Array to List: " + listFromArray);

        // List to Array
        String[] arrayFromList = listFromArray.toArray(new String[0]);
        System.out.println("List to Array: " + Arrays.toString(arrayFromList));

        // Note: Use new String[0] instead of new String[size] (JVM optimizes)

        // Map to List
        Map<String, Integer> map = Map.of("A", 1, "B", 2, "C", 3);
        List<String> keys = new ArrayList<>(map.keySet());
        List<Integer> values = new ArrayList<>(map.values());
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());

        System.out.println("Map keys: " + keys);
        System.out.println("Map values: " + values);

        // Stream conversions
        List<String> streamList = set.stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.toList());
        System.out.println("Stream conversion: " + streamList);

        // Collectors
        Set<Integer> streamSet = list.stream()
                                     .collect(Collectors.toSet());
        System.out.println("Stream to Set: " + streamSet);

        Map<Integer, String> streamMap = list.stream()
                                             .distinct()
                                             .collect(Collectors.toMap(i -> i, i -> "Value" + i));
        System.out.println("Stream to Map: " + streamMap);
    }

    /**
     * Question 9: What are the common pitfalls with collections?
     */
    public static void demonstrateCommonPitfalls() {
        System.out.println("\n=== Common Pitfalls ===");

        // Pitfall 1: Arrays.asList() is fixed size
        System.out.println("Pitfall 1: Arrays.asList() is fixed size");

        // Pitfall 2: Modifying collection while iterating
        System.out.println("Pitfall 2: ConcurrentModificationException");

        // Pitfall 3: Using == instead of equals()
        String s1 = new String("test");
        String s2 = new String("test");
        System.out.println("Pitfall 3: s1 == s2: " + (s1 == s2));
        System.out.println("Correct: s1.equals(s2): " + s1.equals(s2));

        // Pitfall 4: Not overriding equals() and hashCode()
        System.out.println("Pitfall 4: Must override both equals() and hashCode()");

        // Pitfall 5: Using collection as key without proper equals/hashCode
        List<String> list1 = new ArrayList<>(Arrays.asList("A", "B"));
        List<String> list2 = new ArrayList<>(Arrays.asList("A", "B"));

        Map<List<String>, String> map = new HashMap<>();
        map.put(list1, "Value");

        System.out.println("Pitfall 5: Mutable object as key");
        System.out.println("Before modification: " + map.get(list2)); // Works

        list1.add("C"); // Modifying key!
        System.out.println("After modification: " + map.get(list1)); // Broken!

        // Pitfall 6: Returning null instead of empty collection
        System.out.println("\nPitfall 6: Return Collections.emptyList() instead of null");

        // Pitfall 7: Using raw types
        System.out.println("Pitfall 7: Avoid raw types, use generics");
    }

    public static void main(String[] args) {
        demonstrateCapacityPlanning();
        demonstrateCollectionViews();
        demonstrateAdvancedComparators();
        demonstrateInternalVsExternalIteration();
        demonstrateCustomCollection();
        demonstrateStructuralModification();
        demonstratePerformanceCharacteristics();
        demonstrateCollectionConversions();
        demonstrateCommonPitfalls();
    }
}

