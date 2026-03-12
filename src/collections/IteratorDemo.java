package collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/*
 * =============================================================================
 * IteratorDemo.java — Iterator and ListIterator
 * =============================================================================
 *
 * CONCEPT: Iterator
 * -----------------
 * Iterator is a design pattern that provides a uniform way to traverse
 * different types of collections without exposing their internal structure.
 *
 * WHY USE Iterator:
 *  - Safely REMOVE elements while iterating (enhanced for loop can't do this)
 *  - Works with any Collection that implements Iterable
 *  - Standard pattern used by the enhanced for loop internally
 *
 * Iterator METHODS:
 *   hasNext() → returns true if there are more elements
 *   next()    → returns the next element and advances the cursor
 *   remove()  → removes the last element returned by next() (SAFE removal)
 *
 * ListIterator (for Lists only) METHODS (extends Iterator):
 *   hasPrevious() → can iterate backwards
 *   previous()    → returns the previous element
 *   nextIndex()   → returns the index of the next element
 *   previousIndex()
 *   set(E e)      → replaces the last element returned
 *   add(E e)      → inserts an element
 *
 * HOW TO RUN:
 *  $ javac -d out src/collections/IteratorDemo.java
 *  $ java -cp out collections.IteratorDemo
 * =============================================================================
 */
public class IteratorDemo {

    public static void main(String[] args) {

        // ── BASIC ITERATOR ────────────────────────────────────────────────────
        System.out.println("=== Basic Iterator ===");

        List<String> fruits = new ArrayList<>(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"));

        Iterator<String> it = fruits.iterator();
        while (it.hasNext()) {
            String fruit = it.next();
            System.out.println("  " + fruit);
        }

        // ── REMOVE WITH ITERATOR (safe removal during iteration) ─────────────
        System.out.println("\n=== Safe Removal with Iterator ===");
        System.out.println("Before: " + fruits);

        Iterator<String> removeIt = fruits.iterator();
        while (removeIt.hasNext()) {
            String fruit = removeIt.next();
            if (fruit.startsWith("B") || fruit.startsWith("D")) {
                removeIt.remove();  // safe — won't throw ConcurrentModificationException
            }
        }
        System.out.println("After removing B* and D*: " + fruits);

        // ── WHY NOT REMOVE WITH FOR-EACH ─────────────────────────────────────
        System.out.println("\n=== Why not remove in enhanced for (demo) ===");
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        try {
            for (Integer n : numbers) {
                if (n % 2 == 0) {
                    numbers.remove(n);  // throws ConcurrentModificationException!
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException caught! Don't modify in for-each.");
        }

        // SAFE alternative: removeIf (Java 8+)
        numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        numbers.removeIf(n -> n % 2 == 0);   // removes all even numbers safely
        System.out.println("After removeIf(even): " + numbers);

        // ── LIST ITERATOR (bidirectional) ─────────────────────────────────────
        System.out.println("\n=== ListIterator ===");

        List<String> animals = new ArrayList<>(List.of("Cat", "Dog", "Elephant", "Fox"));
        ListIterator<String> listIt = animals.listIterator();

        System.out.print("Forward:  ");
        while (listIt.hasNext()) {
            System.out.print(listIt.next() + " ");
        }
        System.out.println();

        System.out.print("Backward: ");
        while (listIt.hasPrevious()) {
            System.out.print(listIt.previous() + " ");
        }
        System.out.println();

        // ── MODIFY WHILE ITERATING WITH LISTITERATOR ──────────────────────────
        System.out.println("\n=== Modify with ListIterator ===");
        ListIterator<String> modIt = animals.listIterator();
        while (modIt.hasNext()) {
            String animal = modIt.next();
            modIt.set(animal.toUpperCase());   // replace each element
        }
        System.out.println("Uppercased: " + animals);

        // ── ITERATOR OVER MAP ─────────────────────────────────────────────────
        System.out.println("\n=== Iterator over Map entries ===");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 80);
        scores.put("Charlie", 72);
        scores.put("Diana", 88);

        // Iterate and remove entries below threshold
        Iterator<Map.Entry<String, Integer>> mapIt = scores.entrySet().iterator();
        while (mapIt.hasNext()) {
            Map.Entry<String, Integer> entry = mapIt.next();
            if (entry.getValue() < 85) {
                System.out.println("Removing low score: " + entry.getKey() + "=" + entry.getValue());
                mapIt.remove();   // safe map entry removal
            }
        }
        System.out.println("High scores only: " + scores);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic Iterator ===
 *   Apple
 *   Banana
 *   Cherry
 *   Date
 *   Elderberry
 *
 * === Safe Removal with Iterator ===
 * Before: [Apple, Banana, Cherry, Date, Elderberry]
 * After removing B* and D*: [Apple, Cherry, Elderberry]
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Calling next() without checking hasNext() → NoSuchElementException
 *
 * 2. Calling remove() before next() → IllegalStateException
 *    Must call next() before each remove().
 *
 * 3. Calling remove() twice in a row → IllegalStateException
 *    Can only call remove() once per next() call.
 *
 * 4. Modifying the collection while iterating with enhanced for:
 *    for (T item : list) { list.remove(item); } ← ConcurrentModificationException
 *    Use Iterator.remove() or removeIf() instead.
 *
 * 5. Using Iterator on HashMap directly — you need to iterate keySet(), values(),
 *    or entrySet() (which returns a Set of Map.Entry objects).
 */
