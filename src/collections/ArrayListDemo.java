package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * =============================================================================
 * ArrayListDemo.java — ArrayList: Dynamic Array in Java
 * =============================================================================
 *
 * CONCEPT: ArrayList
 * -------------------
 * ArrayList is a resizable array implementation of the List interface.
 * Unlike regular arrays, it can grow and shrink dynamically.
 *
 * KEY CHARACTERISTICS:
 *  - Ordered: elements maintain insertion order
 *  - Allows duplicates
 *  - Allows null values
 *  - NOT synchronized (not thread-safe by default)
 *  - Random access: O(1) — fast to get by index
 *  - Insert/delete in middle: O(n) — slow (shifts elements)
 *
 * IMPORT: import java.util.ArrayList;
 *
 * GENERICS: ArrayList<Type> ensures type safety
 *   ArrayList<String> list = new ArrayList<>();
 *   (The diamond <> lets Java infer the type from the variable declaration)
 *
 * HOW TO RUN:
 *  $ javac -d out src/collections/ArrayListDemo.java
 *  $ java -cp out collections.ArrayListDemo
 * =============================================================================
 */
public class ArrayListDemo {

    public static void main(String[] args) {

        // ── CREATING ARRAYLIST ────────────────────────────────────────────────
        System.out.println("=== Creating ArrayList ===");

        ArrayList<String> fruits = new ArrayList<>();    // empty list
        ArrayList<Integer> numbers = new ArrayList<>(10);  // initial capacity 10 (for performance)

        // Creating from existing collection
        List<String> fromList = new ArrayList<>(List.of("A", "B", "C"));  // Java 9+

        System.out.println("Empty list: " + fruits);
        System.out.println("From list:  " + fromList);

        // ── ADD ELEMENTS ──────────────────────────────────────────────────────
        System.out.println("\n=== Adding Elements ===");

        fruits.add("Apple");           // add to end
        fruits.add("Banana");
        fruits.add("Cherry");
        fruits.add("Date");
        fruits.add(1, "Apricot");      // add at specific index (shifts right)

        System.out.println("Fruits: " + fruits);
        System.out.println("Size: " + fruits.size());

        // ── ACCESS ELEMENTS ───────────────────────────────────────────────────
        System.out.println("\n=== Accessing Elements ===");

        System.out.println("get(0): " + fruits.get(0));                      // first
        System.out.println("get(last): " + fruits.get(fruits.size() - 1));   // last
        System.out.println("indexOf(\"Cherry\"): " + fruits.indexOf("Cherry"));

        // ── REMOVE ELEMENTS ───────────────────────────────────────────────────
        System.out.println("\n=== Removing Elements ===");

        fruits.remove("Date");           // remove by VALUE
        fruits.remove(0);                // remove by INDEX (removes "Apple")
        System.out.println("After removes: " + fruits);

        // ── MODIFY ELEMENTS ───────────────────────────────────────────────────
        System.out.println("\n=== Modifying Elements ===");

        fruits.set(0, "Avocado");        // replace element at index 0
        System.out.println("After set: " + fruits);

        // ── SEARCH ───────────────────────────────────────────────────────────
        System.out.println("\n=== Searching ===");

        System.out.println("contains(\"Banana\"): " + fruits.contains("Banana"));
        System.out.println("contains(\"Grape\"):  " + fruits.contains("Grape"));
        System.out.println("isEmpty():           " + fruits.isEmpty());

        // ── ITERATION ────────────────────────────────────────────────────────
        System.out.println("\n=== Iteration Methods ===");

        // Method 1: Enhanced for loop
        System.out.print("for-each:    ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        // Method 2: Traditional for with index
        System.out.print("for index:   ");
        for (int i = 0; i < fruits.size(); i++) {
            System.out.print(i + ":" + fruits.get(i) + " ");
        }
        System.out.println();

        // Method 3: forEach with lambda (Java 8+)
        System.out.print("forEach λ:   ");
        fruits.forEach(f -> System.out.print(f.toLowerCase() + " "));
        System.out.println();

        // ── SORTING ──────────────────────────────────────────────────────────
        System.out.println("\n=== Sorting ===");

        ArrayList<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Diana"));
        System.out.println("Before sort: " + names);
        Collections.sort(names);                             // natural order (alphabetical)
        System.out.println("Sorted asc:  " + names);
        Collections.sort(names, Collections.reverseOrder()); // descending
        System.out.println("Sorted desc: " + names);

        // Sort by custom criteria (lambda)
        names.sort((a, b) -> a.length() - b.length());   // sort by string length
        System.out.println("By length:   " + names);

        // ── NUMBERS OPERATIONS ────────────────────────────────────────────────
        System.out.println("\n=== Number Operations ===");

        ArrayList<Integer> nums = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3, 7, 4, 6));
        System.out.println("Numbers: " + nums);
        System.out.println("Max: " + Collections.max(nums));
        System.out.println("Min: " + Collections.min(nums));
        Collections.shuffle(nums);
        System.out.println("Shuffled: " + nums);
        Collections.sort(nums);
        System.out.println("Sorted:   " + nums);

        // ── SUBLIST ───────────────────────────────────────────────────────────
        System.out.println("\n=== subList ===");
        List<Integer> sub = nums.subList(2, 5);   // indices 2 to 4 (exclusive end)
        System.out.println("subList(2,5): " + sub);

        // ── CLEAR AND SIZE ────────────────────────────────────────────────────
        System.out.println("\n=== Clear ===");
        System.out.println("Before clear: size=" + fruits.size());
        fruits.clear();
        System.out.println("After clear:  size=" + fruits.size() + ", isEmpty=" + fruits.isEmpty());
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Adding Elements ===
 * Fruits: [Apple, Apricot, Banana, Cherry, Date]
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using raw type: ArrayList list = new ArrayList(); ← no type safety!
 *    Always use generics: ArrayList<String> list = new ArrayList<>();
 *
 * 2. remove(int) vs remove(Object) confusion:
 *    list.remove(1)    → removes element at INDEX 1
 *    list.remove("1")  → removes the String "1" (by value)
 *    For Integer lists: list.remove(Integer.valueOf(1)) to remove by value
 *
 * 3. ConcurrentModificationException: modifying list during enhanced for loop:
 *    for (String s : list) { list.remove(s); } ← throws ConcurrentModificationException
 *    Use Iterator.remove() or removeIf() instead.
 *
 * 4. Index out of bounds: list.get(list.size()) → IndexOutOfBoundsException
 *    Valid indices: 0 to size()-1
 *
 * 5. ArrayList vs Array: ArrayLists can't hold primitives directly:
 *    ArrayList<int> → COMPILE ERROR, use ArrayList<Integer> (autoboxing handles it)
 */
