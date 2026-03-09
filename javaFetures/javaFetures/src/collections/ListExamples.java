package collections;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Advanced List interface concepts for experienced developers
 * Topics: ArrayList, LinkedList, Vector, CopyOnWriteArrayList, Stack
 * Performance characteristics, thread-safety, use cases
 */
public class ListExamples {

    /**
     * Question 1: What's the time complexity difference between ArrayList and LinkedList
     * for add, get, and remove operations at different positions?
     */
    public static void demonstrateListPerformance() {
        System.out.println("=== List Performance Analysis ===");

        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();

        // ArrayList: O(1) amortized for add at end, O(n) for add at beginning
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayList.add(0, "Element-" + i); // Adding at beginning
        }
        long arrayListTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedList.add(0, "Element-" + i); // Adding at beginning
        }
        long linkedListTime = System.nanoTime() - start;

        System.out.println("ArrayList add at beginning: " + arrayListTime / 1_000_000 + " ms");
        System.out.println("LinkedList add at beginning: " + linkedListTime / 1_000_000 + " ms");

        // Random access comparison
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.get(i * 5);
        }
        arrayListTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.get(i * 5);
        }
        linkedListTime = System.nanoTime() - start;

        System.out.println("ArrayList random access: " + arrayListTime / 1_000 + " μs");
        System.out.println("LinkedList random access: " + linkedListTime / 1_000 + " μs");
    }

    /**
     * Question 2: How does ArrayList handle capacity and when does it resize?
     * What's the growth factor?
     */
    public static void demonstrateArrayListCapacity() {
        System.out.println("\n=== ArrayList Capacity Management ===");

        // Initial capacity of 10, grows by ~50% (oldCapacity + (oldCapacity >> 1))
        ArrayList<Integer> list = new ArrayList<>(10);
        System.out.println("Initial capacity: 10");

        for (int i = 0; i < 15; i++) {
            list.add(i);
            // Capacity grows when size exceeds current capacity
        }
        System.out.println("After adding 15 elements, ArrayList resized internally");
        System.out.println("Growth factor is approximately 1.5x");

        // Pre-sizing for performance
        ArrayList<Integer> preSized = new ArrayList<>(1000);
        System.out.println("Pre-sized ArrayList avoids multiple resizing operations");
    }

    /**
     * Question 3: What are the differences between Vector and ArrayList?
     * When would you use Vector?
     */
    public static void demonstrateVectorVsArrayList() {
        System.out.println("\n=== Vector vs ArrayList ===");

        // Vector is synchronized (thread-safe), ArrayList is not
        Vector<String> vector = new Vector<>();
        ArrayList<String> arrayList = new ArrayList<>();

        System.out.println("Vector: synchronized, thread-safe, legacy class");
        System.out.println("ArrayList: not synchronized, faster, modern choice");
        System.out.println("Vector doubles capacity when full, ArrayList grows by 50%");

        // For thread-safety, prefer Collections.synchronizedList() or CopyOnWriteArrayList
        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        System.out.println("Use Collections.synchronizedList() instead of Vector");
    }

    /**
     * Question 4: What is CopyOnWriteArrayList and when should it be used?
     * Explain its trade-offs.
     */
    public static void demonstrateCopyOnWriteArrayList() {
        System.out.println("\n=== CopyOnWriteArrayList ===");

        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();

        cowList.add("Item1");
        cowList.add("Item2");
        cowList.add("Item3");

        // Safe iteration without ConcurrentModificationException
        Iterator<String> iterator = cowList.iterator();

        // This modification won't affect the iterator (snapshot semantics)
        cowList.add("Item4");

        System.out.println("Iterating over snapshot:");
        while (iterator.hasNext()) {
            System.out.println(iterator.next()); // Won't see Item4
        }

        System.out.println("\nUse case: Many reads, few writes (observer patterns)");
        System.out.println("Trade-off: Expensive writes (full array copy), memory overhead");
    }

    /**
     * Question 5: Demonstrate fail-fast vs fail-safe iterators
     */
    public static void demonstrateFailFastIterator() {
        System.out.println("\n=== Fail-Fast vs Fail-Safe Iterators ===");

        // Fail-fast: ArrayList
        List<String> arrayList = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        try {
            for (String item : arrayList) {
                System.out.println(item);
                if (item.equals("B")) {
                    arrayList.remove(item); // ConcurrentModificationException
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught - Fail-fast behavior");
        }

        // Fail-safe: CopyOnWriteArrayList
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>(Arrays.asList("A", "B", "C", "D"));
        for (String item : cowList) {
            System.out.println(item);
            if (item.equals("B")) {
                cowList.remove(item); // No exception
            }
        }
        System.out.println("No exception with CopyOnWriteArrayList - Fail-safe behavior");
    }

    /**
     * Question 6: How to properly remove elements while iterating?
     */
    public static void demonstrateProperRemoval() {
        System.out.println("\n=== Proper Element Removal ===");

        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // Method 1: Using Iterator
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer num = iterator.next();
            if (num % 2 == 0) {
                iterator.remove(); // Safe removal
            }
        }
        System.out.println("After iterator removal: " + list);

        // Method 2: Using removeIf (Java 8+)
        list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        list.removeIf(num -> num % 2 == 0);
        System.out.println("After removeIf: " + list);

        // Method 3: Using streams (creates new list)
        list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> filtered = list.stream()
                                     .filter(num -> num % 2 != 0)
                                     .toList();
        System.out.println("After stream filter: " + filtered);
    }

    /**
     * Question 7: Explain ListIterator and its bidirectional capabilities
     */
    public static void demonstrateListIterator() {
        System.out.println("\n=== ListIterator Advanced Features ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));
        ListIterator<String> listIterator = list.listIterator();

        // Forward traversal
        System.out.println("Forward:");
        while (listIterator.hasNext()) {
            System.out.print(listIterator.next() + " ");
        }

        // Backward traversal
        System.out.println("\nBackward:");
        while (listIterator.hasPrevious()) {
            System.out.print(listIterator.previous() + " ");
        }

        // Modification during iteration
        listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            String item = listIterator.next();
            if (item.equals("C")) {
                listIterator.set("C_MODIFIED"); // Replace
                listIterator.add("C_NEW"); // Add after current
            }
        }
        System.out.println("\nAfter modification: " + list);
    }

    public static void main(String[] args) {
        demonstrateListPerformance();
        demonstrateArrayListCapacity();
        demonstrateVectorVsArrayList();
        demonstrateCopyOnWriteArrayList();
        demonstrateFailFastIterator();
        demonstrateProperRemoval();
        demonstrateListIterator();
    }
}

