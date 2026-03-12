package collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/*
 * =============================================================================
 * HashSetDemo.java — HashSet: Unique Elements Collection
 * =============================================================================
 *
 * CONCEPT: HashSet
 * -----------------
 * HashSet stores UNIQUE elements — no duplicates allowed.
 * Based on a hash table (uses HashMap internally).
 *
 * KEY CHARACTERISTICS:
 *  - No duplicates (adding a duplicate is silently ignored)
 *  - No guaranteed order (use LinkedHashSet for insertion order, TreeSet for sorted)
 *  - O(1) average for add, remove, contains
 *  - Allows one null element
 *  - NOT synchronized (not thread-safe)
 *
 * SET IMPLEMENTATIONS:
 *  HashSet:       Fast, no order guarantee
 *  LinkedHashSet: Maintains insertion order
 *  TreeSet:       Sorted (natural or custom order), O(log n) operations
 *
 * SET OPERATIONS (mathematical):
 *  Union:        set1 + set2 (all unique elements from both)
 *  Intersection: elements in BOTH sets
 *  Difference:   elements in set1 but NOT in set2
 *
 * HOW TO RUN:
 *  $ javac -d out src/collections/HashSetDemo.java
 *  $ java -cp out collections.HashSetDemo
 * =============================================================================
 */
public class HashSetDemo {

    public static void main(String[] args) {

        // ── CREATING HASHSET ──────────────────────────────────────────────────
        System.out.println("=== HashSet Basics ===");

        HashSet<String> cities = new HashSet<>();
        cities.add("Mumbai");
        cities.add("Delhi");
        cities.add("Bangalore");
        cities.add("Chennai");
        cities.add("Mumbai");     // duplicate — silently ignored
        cities.add("Delhi");      // duplicate — silently ignored

        System.out.println("Cities: " + cities);   // order not guaranteed!
        System.out.println("Size: " + cities.size());  // 4 (not 6!)

        // ── CONTAINS AND REMOVE ───────────────────────────────────────────────
        System.out.println("\n=== Contains and Remove ===");
        System.out.println("contains(\"Mumbai\"):    " + cities.contains("Mumbai"));   // true
        System.out.println("contains(\"Kolkata\"):   " + cities.contains("Kolkata"));  // false

        boolean removed = cities.remove("Chennai");
        System.out.println("Removed Chennai: " + removed);
        System.out.println("After remove: " + cities);

        // ── ITERATION ────────────────────────────────────────────────────────
        System.out.println("\n=== Iteration ===");
        for (String city : cities) {
            System.out.println("  City: " + city);
        }

        // forEach with lambda
        System.out.print("forEach: ");
        cities.forEach(c -> System.out.print(c + " "));
        System.out.println();

        // ── SET OPERATIONS ────────────────────────────────────────────────────
        System.out.println("\n=== Set Operations ===");

        Set<Integer> setA = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Set.of(4, 5, 6, 7, 8));
        System.out.println("Set A: " + setA);
        System.out.println("Set B: " + setB);

        // UNION: all elements from both sets
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        System.out.println("Union:        " + union);

        // INTERSECTION: only common elements
        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        System.out.println("Intersection: " + intersection);

        // DIFFERENCE: in A but not in B
        Set<Integer> difference = new HashSet<>(setA);
        difference.removeAll(setB);
        System.out.println("Difference:   " + difference);

        // ── LINKEDHASHSET (insertion order) ───────────────────────────────────
        System.out.println("\n=== LinkedHashSet (insertion order) ===");

        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        ordered.add("Zebra");
        ordered.add("Apple");
        ordered.add("Mango");
        ordered.add("Apple");   // duplicate ignored, order maintained
        System.out.println("LinkedHashSet: " + ordered);  // [Zebra, Apple, Mango]

        // ── TREESET (sorted order) ────────────────────────────────────────────
        System.out.println("\n=== TreeSet (sorted order) ===");

        TreeSet<String> sorted = new TreeSet<>(cities);
        sorted.add("Hyderabad");
        sorted.add("Ahmedabad");
        System.out.println("TreeSet: " + sorted);   // alphabetically sorted!
        System.out.println("First: " + sorted.first());
        System.out.println("Last:  " + sorted.last());

        // ── PRACTICAL: REMOVE DUPLICATES FROM ARRAY ───────────────────────────
        System.out.println("\n=== Remove Duplicates ===");

        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        HashSet<Integer> unique = new HashSet<>();
        for (int n : arr) unique.add(n);
        System.out.println("Original: [3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5]");
        System.out.println("Unique:   " + unique);
        System.out.println("Count:    " + unique.size());
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === HashSet Basics ===
 * Cities: [Bangalore, Mumbai, Delhi, Chennai] (order may vary!)
 * Size: 4
 *
 * === Set Operations ===
 * Set A: [1, 2, 3, 4, 5]
 * Set B: [4, 5, 6, 7, 8]
 * Union:        [1, 2, 3, 4, 5, 6, 7, 8]
 * Intersection: [4, 5]
 * Difference:   [1, 2, 3]
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Expecting HashSet to maintain insertion order — it doesn't!
 *    Use LinkedHashSet for insertion order, TreeSet for sorted order.
 *
 * 2. Custom objects in HashSet: must override equals() AND hashCode():
 *    class Person { String name; }
 *    set.add(new Person("Alice")); set.add(new Person("Alice")); → 2 entries!
 *    Because without overriding, equals() uses reference equality.
 *
 * 3. Modifying elements inside a HashSet can "lose" them:
 *    If you change a field used in hashCode(), the element can't be found anymore.
 *    Store immutable objects in HashSets.
 *
 * 4. TreeSet requires Comparable elements or a Comparator:
 *    TreeSet<Object> set = new TreeSet<>(); set.add(new Object()); ← ClassCastException!
 */
