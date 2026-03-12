package collections;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/*
 * =============================================================================
 * HashMapDemo.java — HashMap: Key-Value Storage
 * =============================================================================
 *
 * CONCEPT: HashMap
 * -----------------
 * HashMap stores data as KEY-VALUE pairs (entries).
 * Keys must be UNIQUE; values can be duplicated.
 *
 * KEY CHARACTERISTICS:
 *  - No guaranteed order (for ordered: use LinkedHashMap or TreeMap)
 *  - O(1) average time for get, put, remove
 *  - Allows one null key and multiple null values
 *  - NOT synchronized (not thread-safe — use ConcurrentHashMap for threads)
 *
 * IMPORT: import java.util.HashMap;
 *
 * HOW TO RUN:
 *  $ javac -d out src/collections/HashMapDemo.java
 *  $ java -cp out collections.HashMapDemo
 * =============================================================================
 */
public class HashMapDemo {

    public static void main(String[] args) {

        // ── CREATING HASHMAP ──────────────────────────────────────────────────
        System.out.println("=== Creating HashMap ===");

        HashMap<String, Integer> scores = new HashMap<>();

        // ── PUT (add / update) ────────────────────────────────────────────────
        System.out.println("\n=== put() ===");
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        scores.put("Diana", 88);

        // put() with existing key — OVERWRITES the old value
        scores.put("Bob", 90);    // updates Bob's score from 87 to 90

        System.out.println("Scores: " + scores);
        System.out.println("Size: " + scores.size());

        // ── GET ───────────────────────────────────────────────────────────────
        System.out.println("\n=== get() ===");
        System.out.println("Alice's score: " + scores.get("Alice"));
        System.out.println("Eve's score:   " + scores.get("Eve"));      // null (key not found)
        System.out.println("getOrDefault:  " + scores.getOrDefault("Eve", 0));   // 0 if not found

        // ── CONTAINS ──────────────────────────────────────────────────────────
        System.out.println("\n=== Contains ===");
        System.out.println("containsKey(\"Alice\"):  " + scores.containsKey("Alice"));   // true
        System.out.println("containsKey(\"Eve\"):    " + scores.containsKey("Eve"));     // false
        System.out.println("containsValue(95):      " + scores.containsValue(95));       // true

        // ── REMOVE ───────────────────────────────────────────────────────────
        System.out.println("\n=== remove() ===");
        Integer removed = scores.remove("Diana");
        System.out.println("Removed Diana's score: " + removed);
        System.out.println("After remove: " + scores);

        // ── ITERATION ────────────────────────────────────────────────────────
        System.out.println("\n=== Iteration Methods ===");

        // Method 1: entrySet() — iterate key-value pairs
        System.out.println("entrySet():");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println("  " + entry.getKey() + " → " + entry.getValue());
        }

        // Method 2: keySet() — iterate keys only
        System.out.print("keySet():    ");
        for (String key : scores.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();

        // Method 3: values() — iterate values only
        System.out.print("values():    ");
        for (int val : scores.values()) {
            System.out.print(val + " ");
        }
        System.out.println();

        // Method 4: forEach with lambda (Java 8+)
        System.out.println("forEach λ:");
        scores.forEach((name, score) ->
            System.out.println("  " + name + " scored " + score));

        // ── ADVANCED OPERATIONS (Java 8+) ─────────────────────────────────────
        System.out.println("\n=== Advanced Operations ===");

        // putIfAbsent: only adds if key doesn't exist
        scores.putIfAbsent("Eve", 75);
        scores.putIfAbsent("Alice", 999);  // Alice already exists, not overwritten
        System.out.println("After putIfAbsent: " + scores);

        // compute: update value based on current value
        scores.compute("Alice", (key, val) -> val + 5);  // add 5 to Alice's score
        System.out.println("After compute (Alice +5): Alice=" + scores.get("Alice"));

        // merge: combine with existing value
        scores.merge("Bob", 10, Integer::sum);  // Bob's score = 90 + 10 = 100
        System.out.println("After merge (Bob +10): Bob=" + scores.get("Bob"));

        // ── WORD COUNT EXAMPLE ────────────────────────────────────────────────
        System.out.println("\n=== Word Count Example ===");
        String text = "the quick brown fox jumps over the lazy dog the fox";
        String[] words = text.split(" ");
        HashMap<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        System.out.println("Word counts: " + wordCount);

        // ── TREEMAP (sorted keys) ─────────────────────────────────────────────
        System.out.println("\n=== TreeMap (sorted order) ===");
        TreeMap<String, Integer> sorted = new TreeMap<>(scores);  // TreeMap sorts by key
        System.out.println("Sorted by name: " + sorted);
        System.out.println("firstKey(): " + sorted.firstKey());
        System.out.println("lastKey():  " + sorted.lastKey());
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === put() ===
 * Scores: {Bob=90, Alice=95, Charlie=92, Diana=88} (order not guaranteed)
 *
 * === get() ===
 * Alice's score: 95
 * Eve's score:   null
 * getOrDefault:  0
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Expecting HashMap to maintain insertion order — it doesn't!
 *    Use LinkedHashMap for insertion-order, TreeMap for sorted order.
 *
 * 2. NullPointerException when unboxing: Integer val = map.get(key); int x = val;
 *    If key not found, get() returns null; unboxing null throws NPE!
 *    Use getOrDefault() or check for null first.
 *
 * 3. Using mutable objects as keys: if the key changes after insertion,
 *    the entry may become unreachable! Keys should be immutable (String, Integer, etc.)
 *
 * 4. Iterating over map while modifying it → ConcurrentModificationException
 *    Use entrySet() with Iterator.remove() or compute/merge instead.
 *
 * 5. equals() and hashCode(): custom key objects must override both!
 *    HashMap uses hashCode() to find the bucket, then equals() to find the key.
 */
