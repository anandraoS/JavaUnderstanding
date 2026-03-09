# Java Collections Framework - Comprehensive Guide for Experienced Developers

This project contains comprehensive examples covering all aspects of the Java Collections Framework, designed for developers with 10+ years of experience.

## 📚 Table of Contents

### 1. **ListExamples.java** - List Interface Deep Dive
- ✅ ArrayList vs LinkedList performance characteristics (O(1) vs O(n) operations)
- ✅ ArrayList internal capacity management and growth factor (1.5x)
- ✅ Vector vs ArrayList - synchronization and legacy considerations
- ✅ CopyOnWriteArrayList - fail-safe iterators and use cases
- ✅ Fail-fast vs Fail-safe iterators with ConcurrentModificationException
- ✅ Proper element removal techniques (Iterator, removeIf, streams)
- ✅ ListIterator - bidirectional traversal and modification during iteration

**Key Concepts:**
- Time complexity analysis
- Memory efficiency
- Thread-safety considerations
- Iterator patterns

---

### 2. **SetExamples.java** - Set Interface Mastery
- ✅ HashSet internals - hash collision handling (chaining → trees in Java 8)
- ✅ HashSet vs LinkedHashSet vs TreeSet comparison
- ✅ TreeSet and NavigableSet operations (floor, ceiling, subSet, etc.)
- ✅ Custom Comparators for TreeSet with multiple sorting criteria
- ✅ EnumSet - bit vector implementation and efficiency
- ✅ Concurrent Set implementations (CopyOnWriteArraySet, ConcurrentSkipListSet)
- ✅ Proper equals() and hashCode() contracts
- ✅ Set bulk operations (union, intersection, difference)

**Key Concepts:**
- Hash functions and collision resolution
- Sorted vs unsorted collections
- Thread-safe alternatives
- Set theory operations

---

### 3. **MapExamples.java** - Map Interface Expert Level
- ✅ HashMap internals - Node array, treeification threshold (TREEIFY_THRESHOLD = 8)
- ✅ HashMap vs LinkedHashMap vs TreeMap characteristics
- ✅ LinkedHashMap with access-order mode (LRU Cache implementation)
- ✅ Java 8 compute operations (compute, computeIfAbsent, computeIfPresent, merge)
- ✅ Map operations (getOrDefault, putIfAbsent, replace)
- ✅ WeakHashMap - automatic garbage collection of keys
- ✅ IdentityHashMap - reference equality (==) vs value equality (equals)
- ✅ EnumMap - compact array representation
- ✅ ConcurrentHashMap - lock striping, CAS operations, segment-level locking
- ✅ TreeMap and NavigableMap operations (lowerEntry, floorEntry, subMap)
- ✅ ConcurrentSkipListMap - concurrent sorted map

**Key Concepts:**
- Hash table implementation details
- LRU caching strategies
- Concurrent programming patterns
- Memory management with weak references

---

### 4. **QueueExamples.java** - Queue and Deque Interfaces
- ✅ Queue methods - add/offer, remove/poll, element/peek differences
- ✅ PriorityQueue - binary heap implementation, min-heap and max-heap
- ✅ Custom Comparator with PriorityQueue for priority scheduling
- ✅ Deque interface - double-ended queue operations
- ✅ ArrayDeque vs LinkedList performance comparison
- ✅ ArrayDeque vs Stack class - why ArrayDeque is better
- ✅ BlockingQueue implementations (ArrayBlockingQueue, LinkedBlockingQueue, etc.)
- ✅ Producer-Consumer pattern with BlockingQueue
- ✅ DelayQueue - scheduled task execution
- ✅ LinkedTransferQueue - transfer() and direct handoff
- ✅ Queue implementation comparison matrix

**Key Concepts:**
- Heap data structures
- FIFO and LIFO operations
- Thread coordination patterns
- Blocking vs non-blocking operations

---

### 5. **CollectionUtilities.java** - Collections Framework Utilities
- ✅ Immutable collections (List.of(), Set.of(), Map.of() vs unmodifiableList)
- ✅ Collections utility methods (sort, shuffle, rotate, reverse, binarySearch)
- ✅ Synchronized collections and their limitations
- ✅ Empty collections and singleton collections
- ✅ Checked collections for type-safety
- ✅ Collections.nCopies() efficient repetition
- ✅ Bulk operations (disjoint, addAll, copy)
- ✅ Arrays utility class (sort, binarySearch, fill, equals, deepEquals, stream)
- ✅ Arrays.asList() pitfalls (fixed-size, primitive arrays)
- ✅ Modern collection factories vs traditional methods

**Key Concepts:**
- Immutability patterns
- Utility method performance
- Type safety at runtime
- Modern Java idioms

---

### 6. **AdvancedCollectionConcepts.java** - Advanced Topics
- ✅ Capacity planning and performance optimization
- ✅ Collection views (subList, keySet, values, entrySet) - backed views
- ✅ Advanced Comparator chains (nullsFirst, nullsLast, multiple criteria)
- ✅ Internal vs External iteration
- ✅ Custom collection implementation (RingBuffer example)
- ✅ Structural modification and modCount tracking
- ✅ Performance characteristics comparison (O(1), O(log n), O(n))
- ✅ Collection conversion best practices
- ✅ Common pitfalls and anti-patterns

**Key Concepts:**
- Performance tuning
- Custom implementations
- Iterator internals
- Best practices and pitfalls

---

### 7. **Streams Package** - Streams End-to-End (Interview Level)
- ✅ Stream creation, laziness, intermediate vs terminal
- ✅ map vs flatMap, reduce, primitive streams
- ✅ Collectors: groupingBy, partitioningBy, toMap, joining
- ✅ Parallel streams and ordering
- ✅ Spliterator basics and custom collectors
- ✅ Common pitfalls (reuse, side effects, ordering)

**Key Concepts:**
- Stream pipeline design
- Correct reduction in parallel
- Collector selection and downstreams
- Avoiding side effects

---

### 8. **Multithreading Package** - Concurrency End-to-End (Interview Level)
- ✅ Thread lifecycle, interrupt, join, naming
- ✅ Synchronization, locks, visibility, atomics
- ✅ ExecutorService, Future, CompletableFuture, scheduling
- ✅ Concurrency utilities: CountDownLatch, CyclicBarrier, Semaphore, Phaser
- ✅ Concurrent collections and BlockingQueue
- ✅ Deadlock avoidance and ThreadLocal usage

**Key Concepts:**
- Visibility vs atomicity
- Race conditions and fixes
- Executor sizing and shutdown
- Safe concurrency patterns

---

## 🎯 Key Interview Questions Covered

### Beginner to Intermediate
1. What's the difference between List, Set, and Map?
2. When to use ArrayList vs LinkedList?
3. How does HashMap work internally?
4. What is the contract between equals() and hashCode()?
5. What is ConcurrentModificationException?

### Advanced
6. Explain HashMap's treeification in Java 8
7. How to implement a thread-safe LRU cache?
8. What are the different concurrent collection alternatives?
9. When to use WeakHashMap vs HashMap?
10. Explain the fail-fast vs fail-safe iterator mechanism

### Expert Level
11. Explain HashMap's hash function: (key.hashCode() ^ (h >>> 16)) & (n-1)
12. How does ConcurrentHashMap achieve better concurrency than Hashtable?
13. What is the difference between CopyOnWriteArrayList and Collections.synchronizedList()?
14. How to implement a custom collection extending AbstractList?
15. Explain the performance implications of various collection operations

---

## 🚀 Running the Examples

### Run All Examples
```bash
javac src/collections/*.java
javac src/streams/*.java
javac src/multithreading/*.java
java -cp src collections.ListExamples
java -cp src collections.SetExamples
java -cp src collections.MapExamples
java -cp src collections.QueueExamples
java -cp src collections.CollectionUtilities
java -cp src collections.AdvancedCollectionConcepts
java -cp src streams.StreamsRunner
java -cp src multithreading.ThreadingRunner
```

### Run from Main Menu
```bash
javac src/Main.java src/collections/*.java src/streams/*.java src/multithreading/*.java
java -cp src Main
```

---

## 📊 Collections Framework Hierarchy

```
Collection
├── List (ordered, allows duplicates)
│   ├── ArrayList (resizable array)
│   ├── LinkedList (doubly-linked list)
│   ├── Vector (synchronized, legacy)
│   └── CopyOnWriteArrayList (thread-safe, snapshot iterators)
│
├── Set (no duplicates)
│   ├── HashSet (hash table, no order)
│   ├── LinkedHashSet (hash table + linked list, insertion order)
│   ├── TreeSet (red-black tree, sorted)
│   ├── EnumSet (bit vector, enum types only)
│   └── CopyOnWriteArraySet (thread-safe)
│
└── Queue (processing order)
    ├── PriorityQueue (binary heap, priority order)
    ├── ArrayDeque (resizable array, FIFO/LIFO)
    ├── LinkedList (doubly-linked list, FIFO/LIFO)
    └── BlockingQueue (thread-safe, blocking operations)
        ├── ArrayBlockingQueue (bounded, array)
        ├── LinkedBlockingQueue (optionally bounded)
        ├── PriorityBlockingQueue (unbounded, priority)
        ├── DelayQueue (delayed elements)
        ├── SynchronousQueue (zero capacity)
        └── LinkedTransferQueue (transfer operations)

Map (key-value pairs)
├── HashMap (hash table, no order)
├── LinkedHashMap (hash table + linked list, insertion/access order)
├── TreeMap (red-black tree, sorted by keys)
├── WeakHashMap (weak references, GC eligible)
├── IdentityHashMap (reference equality)
├── EnumMap (array, enum keys only)
├── Hashtable (synchronized, legacy)
└── ConcurrentHashMap (thread-safe, high concurrency)
```

---

## ⚡ Performance Cheat Sheet

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| add(e) | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| get(i) | O(1) | O(n) | N/A | N/A | O(1) | O(log n) |
| remove(i) | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| contains | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |

*O(1) amortized due to occasional resizing

---

## 🔧 Best Practices

1. **Prefer interfaces over implementations** - `List<String> list = new ArrayList<>();`
2. **Specify initial capacity** when size is known - `new ArrayList<>(1000)`
3. **Use diamond operator** - `new HashMap<>()` instead of `new HashMap<String, Integer>()`
4. **Favor immutable collections** - Use `List.of()`, `Set.of()`, `Map.of()`
5. **Return empty collections, not null** - Use `Collections.emptyList()`
6. **Use EnumSet and EnumMap** for enum types
7. **Prefer ConcurrentHashMap** over Hashtable or synchronized collections
8. **Use proper generic types** - avoid raw types
9. **Override equals() and hashCode() together**
10. **Use try-with-resources** for AutoCloseable collections

---

## 📖 Additional Resources

- Java Collections Framework Documentation: https://docs.oracle.com/en/java/javase/
- Effective Java by Joshua Bloch (Chapter 9: General Programming)
- Java Concurrency in Practice (Chapter 5: Building Blocks)

---

## 🎓 Learning Path

1. Start with **ListExamples** - understand basic collection concepts
2. Move to **SetExamples** - learn about uniqueness and ordering
3. Study **MapExamples** - master key-value associations
4. Explore **QueueExamples** - understand processing patterns
5. Review **CollectionUtilities** - learn framework utilities
6. Master **AdvancedCollectionConcepts** - expert-level optimization

---

## ✅ Topics Covered Checklist

- [x] All List implementations
- [x] All Set implementations
- [x] All Map implementations
- [x] All Queue/Deque implementations
- [x] Concurrent collections
- [x] Immutable collections
- [x] Collection utilities
- [x] Performance optimization
- [x] Thread-safety patterns
- [x] Custom implementations
- [x] Best practices and pitfalls

**Total Examples: 60+ methods covering 100+ concepts**

---

## 💡 Pro Tips

- Always consider **thread-safety** requirements before choosing a collection
- Use **capacity planning** to avoid expensive resizing operations
- Understand **fail-fast vs fail-safe** semantics for iterators
- Know when to use **synchronized wrappers** vs **concurrent collections**
- Master **Comparator** chains for complex sorting requirements
- Learn the **internal implementation** details for performance tuning

---

Happy Learning! 🚀

