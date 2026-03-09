package collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;

/**
 * Advanced Map interface concepts for experienced developers
 * Topics: HashMap, LinkedHashMap, TreeMap, WeakHashMap, IdentityHashMap, EnumMap
 * Concurrent maps, compute operations, hash collision handling
 */
public class MapExamples {

    /**
     * Question 1: How does HashMap work internally? Explain the structure.
     * What changed in Java 8 regarding collision handling?
     */
    public static void demonstrateHashMapInternals() {
        System.out.println("=== HashMap Internals ===");

        System.out.println("HashMap structure:");
        System.out.println("- Array of Node<K,V>[] (buckets)");
        System.out.println("- Default capacity: 16, load factor: 0.75");
        System.out.println("- Hash function: (key.hashCode() ^ (h >>> 16)) & (n-1)");
        System.out.println("\nJava 8 improvement:");
        System.out.println("- Buckets use LinkedList initially");
        System.out.println("- When bucket size > TREEIFY_THRESHOLD (8), converts to Red-Black Tree");
        System.out.println("- Improves worst-case from O(n) to O(log n)");

        // Demonstrate hash collision
        class BadHash {
            String value;
            BadHash(String value) { this.value = value; }
            @Override
            public int hashCode() { return 42; } // All collide!
            @Override
            public boolean equals(Object obj) {
                return obj instanceof BadHash && ((BadHash) obj).value.equals(this.value);
            }
            @Override
            public String toString() { return value; }
        }

        Map<BadHash, Integer> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(new BadHash("Key" + i), i);
        }
        System.out.println("\nWith poor hashCode, all entries in one bucket");
        System.out.println("Size: " + map.size());
    }

    /**
     * Question 2: Compare HashMap, LinkedHashMap, and TreeMap
     */
    public static void demonstrateMapTypes() {
        System.out.println("\n=== HashMap vs LinkedHashMap vs TreeMap ===");

        // HashMap: No ordering
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Gamma", 3);
        hashMap.put("Alpha", 1);
        hashMap.put("Beta", 2);
        hashMap.put("Delta", 4);
        System.out.println("HashMap (no order): " + hashMap);

        // LinkedHashMap: Insertion order (or access order)
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Gamma", 3);
        linkedHashMap.put("Alpha", 1);
        linkedHashMap.put("Beta", 2);
        linkedHashMap.put("Delta", 4);
        System.out.println("LinkedHashMap (insertion): " + linkedHashMap);

        // TreeMap: Sorted by keys
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Gamma", 3);
        treeMap.put("Alpha", 1);
        treeMap.put("Beta", 2);
        treeMap.put("Delta", 4);
        System.out.println("TreeMap (sorted): " + treeMap);

        System.out.println("\nPerformance:");
        System.out.println("HashMap: O(1) get/put/remove");
        System.out.println("LinkedHashMap: O(1) get/put/remove + order");
        System.out.println("TreeMap: O(log n) get/put/remove + sorted");
    }

    /**
     * Question 3: Explain LinkedHashMap with access-order mode (LRU Cache)
     */
    public static void demonstrateLRUCache() {
        System.out.println("\n=== LinkedHashMap Access-Order (LRU Cache) ===");

        // LRU Cache implementation using LinkedHashMap
        class LRUCache<K, V> extends LinkedHashMap<K, V> {
            private final int capacity;

            LRUCache(int capacity) {
                super(capacity, 0.75f, true); // accessOrder = true
                this.capacity = capacity;
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        }

        LRUCache<String, Integer> cache = new LRUCache<>(3);

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("After adding A, B, C: " + cache);

        cache.get("A"); // Access A (moves to end)
        System.out.println("After accessing A: " + cache);

        cache.put("D", 4); // Evicts B (least recently used)
        System.out.println("After adding D: " + cache);

        System.out.println("\nUse case: Caching, maintaining recent items");
    }

    /**
     * Question 4: What are Java 8 compute operations in Map?
     */
    public static void demonstrateComputeOperations() {
        System.out.println("\n=== Java 8 Map Compute Operations ===");

        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 5);
        map.put("Banana", 3);

        // compute: always compute new value
        map.compute("Apple", (k, v) -> v == null ? 1 : v + 1);
        System.out.println("After compute (Apple): " + map);

        // computeIfAbsent: compute only if absent
        map.computeIfAbsent("Cherry", k -> 10);
        map.computeIfAbsent("Apple", k -> 100); // Won't execute
        System.out.println("After computeIfAbsent: " + map);

        // computeIfPresent: compute only if present
        map.computeIfPresent("Banana", (k, v) -> v * 2);
        map.computeIfPresent("Mango", (k, v) -> 50); // Won't execute
        System.out.println("After computeIfPresent: " + map);

        // merge: combine old and new values
        map.merge("Apple", 3, Integer::sum); // 6 + 3 = 9
        map.merge("Grape", 7, Integer::sum); // New entry
        System.out.println("After merge: " + map);

        // Practical example: Word frequency counter
        String text = "apple banana apple cherry banana apple";
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : text.split(" ")) {
            wordCount.merge(word, 1, Integer::sum);
        }
        System.out.println("Word count: " + wordCount);
    }

    /**
     * Question 5: Explain getOrDefault, putIfAbsent, replace operations
     */
    public static void demonstrateMapOperations() {
        System.out.println("\n=== Map Operations ===");

        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        // getOrDefault: avoid null checks
        String value = map.getOrDefault("key2", "default");
        System.out.println("getOrDefault for missing key: " + value);

        // putIfAbsent: thread-safe way to add if not exists
        map.putIfAbsent("key1", "newValue"); // Won't replace
        map.putIfAbsent("key2", "value2"); // Will add
        System.out.println("After putIfAbsent: " + map);

        // replace: atomic replacement
        map.replace("key1", "replaced");
        System.out.println("After replace: " + map);

        // replace with check: only if current value matches
        boolean replaced = map.replace("key1", "replaced", "conditional");
        System.out.println("Conditional replace success: " + replaced);
        System.out.println("After conditional replace: " + map);

        // remove with check
        boolean removed = map.remove("key2", "wrongValue"); // Won't remove
        System.out.println("Conditional remove (wrong value): " + removed);
        removed = map.remove("key2", "value2"); // Will remove
        System.out.println("Conditional remove (correct value): " + removed);
    }

    /**
     * Question 6: What is WeakHashMap and when to use it?
     */
    public static void demonstrateWeakHashMap() {
        System.out.println("\n=== WeakHashMap ===");

        Map<Object, String> weakMap = new WeakHashMap<>();
        Map<Object, String> normalMap = new HashMap<>();

        Object key1 = new String("Key1"); // Not interned, eligible for GC
        Object key2 = new String("Key2");

        weakMap.put(key1, "Value1");
        weakMap.put(key2, "Value2");
        normalMap.put(key1, "Value1");
        normalMap.put(key2, "Value2");

        System.out.println("Before GC - WeakHashMap: " + weakMap.size());
        System.out.println("Before GC - HashMap: " + normalMap.size());

        key1 = null; // Remove strong reference
        System.gc(); // Suggest GC

        try {
            Thread.sleep(100); // Give GC time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("After GC - WeakHashMap: " + weakMap.size());
        System.out.println("After GC - HashMap: " + normalMap.size());

        System.out.println("\nUse case: Cache implementations, preventing memory leaks");
        System.out.println("Keys can be garbage collected when no strong references exist");
    }

    /**
     * Question 7: What is IdentityHashMap and how is it different?
     */
    public static void demonstrateIdentityHashMap() {
        System.out.println("\n=== IdentityHashMap ===");

        Map<String, Integer> identityMap = new IdentityHashMap<>();
        Map<String, Integer> hashMap = new HashMap<>();

        String s1 = new String("Key");
        String s2 = new String("Key");
        String s3 = "Key"; // Interned

        // IdentityHashMap uses == instead of equals()
        identityMap.put(s1, 1);
        identityMap.put(s2, 2);
        identityMap.put(s3, 3);
        System.out.println("IdentityHashMap size: " + identityMap.size()); // 3

        // HashMap uses equals()
        hashMap.put(s1, 1);
        hashMap.put(s2, 2);
        hashMap.put(s3, 3);
        System.out.println("HashMap size: " + hashMap.size()); // 1

        System.out.println("\nIdentityHashMap:");
        System.out.println("- Uses System.identityHashCode() instead of hashCode()");
        System.out.println("- Uses == instead of equals()");
        System.out.println("- Use case: Object topology, serialization, debugging");
    }

    /**
     * Question 8: Explain EnumMap benefits
     */
    public static void demonstrateEnumMap() {
        System.out.println("\n=== EnumMap ===");

        enum Status {
            NEW, IN_PROGRESS, COMPLETED, FAILED
        }

        // EnumMap uses array internally - very efficient
        EnumMap<Status, String> statusMap = new EnumMap<>(Status.class);
        statusMap.put(Status.NEW, "Just created");
        statusMap.put(Status.IN_PROGRESS, "Being processed");
        statusMap.put(Status.COMPLETED, "Successfully completed");

        System.out.println("Status map: " + statusMap);
        System.out.println("Order: " + statusMap.keySet()); // Natural enum order

        System.out.println("\nEnumMap advantages:");
        System.out.println("- Compact array representation");
        System.out.println("- Very fast O(1) operations");
        System.out.println("- Maintains natural enum order");
        System.out.println("- Type-safe, null keys not allowed");
    }

    /**
     * Question 9: Explain ConcurrentHashMap and its benefits over Hashtable
     */
    public static void demonstrateConcurrentHashMap() {
        System.out.println("\n=== ConcurrentHashMap ===");

        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();

        System.out.println("ConcurrentHashMap advantages over Hashtable:");
        System.out.println("- Lock striping (segment-level locking in Java 7)");
        System.out.println("- CAS operations (Java 8+)");
        System.out.println("- Better concurrency, higher throughput");
        System.out.println("- No locking for reads");
        System.out.println("- Null keys/values not allowed");

        // Atomic operations
        concurrentMap.put("counter", 0);

        // Thread-safe increment
        concurrentMap.compute("counter", (k, v) -> v == null ? 1 : v + 1);
        System.out.println("After atomic increment: " + concurrentMap);

        // forEach with parallelismThreshold
        concurrentMap.put("A", 1);
        concurrentMap.put("B", 2);
        concurrentMap.put("C", 3);

        concurrentMap.forEach(1, (k, v) ->
            System.out.println(Thread.currentThread().getName() + ": " + k + "=" + v)
        );
    }

    /**
     * Question 10: Demonstrate TreeMap and NavigableMap operations
     */
    public static void demonstrateNavigableMap() {
        System.out.println("\n=== TreeMap and NavigableMap ===");

        NavigableMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(5, "Five");
        treeMap.put(2, "Two");
        treeMap.put(8, "Eight");
        treeMap.put(1, "One");
        treeMap.put(9, "Nine");

        System.out.println("TreeMap: " + treeMap);

        // NavigableMap operations
        System.out.println("firstEntry(): " + treeMap.firstEntry());
        System.out.println("lastEntry(): " + treeMap.lastEntry());
        System.out.println("lowerEntry(5): " + treeMap.lowerEntry(5));
        System.out.println("floorEntry(5): " + treeMap.floorEntry(5));
        System.out.println("ceilingEntry(5): " + treeMap.ceilingEntry(5));
        System.out.println("higherEntry(5): " + treeMap.higherEntry(5));

        // SubMap operations
        System.out.println("headMap(5): " + treeMap.headMap(5));
        System.out.println("tailMap(5): " + treeMap.tailMap(5));
        System.out.println("subMap(2, 8): " + treeMap.subMap(2, 8));

        // Descending
        System.out.println("descendingMap(): " + treeMap.descendingMap());

        // pollFirst/pollLast
        System.out.println("pollFirstEntry(): " + treeMap.pollFirstEntry());
        System.out.println("pollLastEntry(): " + treeMap.pollLastEntry());
    }

    /**
     * Question 11: Demonstrate ConcurrentSkipListMap
     */
    public static void demonstrateConcurrentSkipListMap() {
        System.out.println("\n=== ConcurrentSkipListMap ===");

        ConcurrentSkipListMap<Integer, String> skipListMap = new ConcurrentSkipListMap<>();
        skipListMap.put(5, "Five");
        skipListMap.put(2, "Two");
        skipListMap.put(8, "Eight");

        System.out.println("ConcurrentSkipListMap: " + skipListMap);
        System.out.println("Concurrent alternative to TreeMap");
        System.out.println("O(log n) operations with thread-safety");
        System.out.println("No locking for reads");
    }

    public static void main(String[] args) {
        demonstrateHashMapInternals();
        demonstrateMapTypes();
        demonstrateLRUCache();
        demonstrateComputeOperations();
        demonstrateMapOperations();
        demonstrateWeakHashMap();
        demonstrateIdentityHashMap();
        demonstrateEnumMap();
        demonstrateConcurrentHashMap();
        demonstrateNavigableMap();
        demonstrateConcurrentSkipListMap();
    }
}

