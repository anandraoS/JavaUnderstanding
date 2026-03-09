# 🚀 Quick Reference - Java Collections Framework

## 📁 Project Files

### Source Code (src/)
```
Main.java                                    Interactive menu system
collections/
  ├── ListExamples.java                     List implementations (7 methods)
  ├── SetExamples.java                      Set implementations (8 methods)
  ├── MapExamples.java                      Map implementations (11 methods)
  ├── QueueExamples.java                    Queue implementations (10 methods)
  ├── CollectionUtilities.java              Utility methods (10 methods)
  └── AdvancedCollectionConcepts.java       Advanced topics (9 methods)
streams/
  └── StreamsRunner.java                     Stream concepts and examples
multithreading/
  └── ThreadingRunner.java                   Multithreading concepts and examples
```

### Documentation
```
README.md                                    Main documentation + hierarchy
GETTING_STARTED.md                           Quick start guide
CONCEPTS_SUMMARY.md                          All 225+ concepts listed
QUICK_REFERENCE.md                           This file
```

### Scripts
```
compile.bat                                  Compile all Java files
run.bat                                      Run interactive menu
```

---

## ⚡ Quick Commands

### Compile & Run
```batch
compile.bat          # Compile everything
run.bat             # Run interactive menu
```

### Manual Compilation
```bash
cd src
javac Main.java collections/*.java
java Main
```

### Run Individual Classes
```bash
cd src
java collections.ListExamples
java collections.SetExamples
java collections.MapExamples
java collections.QueueExamples
java collections.CollectionUtilities
java collections.AdvancedCollectionConcepts
java streams.StreamsRunner
java multithreading.ThreadingRunner
```

---

## 📊 Collection Comparison Matrix

| Collection | Ordered | Sorted | Duplicates | Null | Thread-Safe | Time Complexity |
|------------|---------|--------|------------|------|-------------|-----------------|
| ArrayList | ✅ | ❌ | ✅ | ✅ | ❌ | O(1) get, O(n) add/remove |
| LinkedList | ✅ | ❌ | ✅ | ✅ | ❌ | O(n) get, O(1) add/remove |
| Vector | ✅ | ❌ | ✅ | ✅ | ✅ | O(1) get, O(n) add/remove |
| CopyOnWriteArrayList | ✅ | ❌ | ✅ | ✅ | ✅ | O(n) write, O(1) read |
| HashSet | ❌ | ❌ | ❌ | ✅ | ❌ | O(1) all operations |
| LinkedHashSet | ✅ | ❌ | ❌ | ✅ | ❌ | O(1) all operations |
| TreeSet | ✅ | ✅ | ❌ | ❌ | ❌ | O(log n) all |
| EnumSet | ✅ | ✅ | ❌ | ❌ | ❌ | O(1) all operations |
| HashMap | ❌ | ❌ | No keys | ✅ | ❌ | O(1) all operations |
| LinkedHashMap | ✅ | ❌ | No keys | ✅ | ❌ | O(1) all operations |
| TreeMap | ✅ | ✅ | No keys | ❌ | ❌ | O(log n) all |
| ConcurrentHashMap | ❌ | ❌ | No keys | ❌ | ✅ | O(1) all operations |
| PriorityQueue | ✅ | ✅ | ✅ | ❌ | ❌ | O(log n) add/poll |
| ArrayDeque | ✅ | ❌ | ✅ | ❌ | ❌ | O(1) add/remove |

---

## 🎯 When to Use What

### List
- **ArrayList** - Default choice, random access needed
- **LinkedList** - Frequent insertions/deletions at beginning/middle
- **CopyOnWriteArrayList** - Thread-safe, read-heavy workload

### Set
- **HashSet** - Default choice, no duplicates needed
- **LinkedHashSet** - Maintain insertion order
- **TreeSet** - Need sorted elements, range queries
- **EnumSet** - Enum types only, best performance

### Map
- **HashMap** - Default choice, key-value pairs
- **LinkedHashMap** - Maintain insertion/access order (LRU cache)
- **TreeMap** - Sorted keys, range queries
- **ConcurrentHashMap** - Thread-safe, high concurrency
- **EnumMap** - Enum keys only, best performance
- **WeakHashMap** - Keys eligible for garbage collection

### Queue
- **ArrayDeque** - Stack or queue operations (best performance)
- **LinkedList** - Queue with nulls
- **PriorityQueue** - Priority-based processing
- **ArrayBlockingQueue** - Bounded, thread-safe queue
- **LinkedBlockingQueue** - Unbounded/bounded, thread-safe

---

## 💡 Common Operations Cheat Sheet

### List Operations
```java
list.add(element)              // Add to end
list.add(index, element)       // Add at position
list.get(index)                // Get by index
list.remove(index)             // Remove by index
list.set(index, element)       // Replace at index
list.contains(element)         // Check existence
list.size()                    // Get size
list.clear()                   // Remove all
list.subList(from, to)         // View of range
```

### Set Operations
```java
set.add(element)               // Add element
set.remove(element)            // Remove element
set.contains(element)          // Check existence
set.size()                     // Get size
set.clear()                    // Remove all
set.addAll(collection)         // Union
set.retainAll(collection)      // Intersection
set.removeAll(collection)      // Difference
```

### Map Operations
```java
map.put(key, value)                    // Add/update
map.get(key)                           // Get value
map.remove(key)                        // Remove entry
map.containsKey(key)                   // Check key
map.containsValue(value)               // Check value
map.getOrDefault(key, default)         // Safe get
map.putIfAbsent(key, value)            // Add if missing
map.computeIfAbsent(key, function)     // Compute if missing
map.merge(key, value, remappingFn)     // Merge values
```

### Queue Operations
```java
queue.offer(element)           // Add (safe)
queue.poll()                   // Remove head (safe)
queue.peek()                   // View head (safe)
queue.add(element)             // Add (throws exception)
queue.remove()                 // Remove head (throws)
queue.element()                // View head (throws)
```

---

## 🔧 Performance Tips

### Capacity Planning
```java
// Pre-size collections
List<Integer> list = new ArrayList<>(expectedSize);
Map<K, V> map = new HashMap<>((int)(expectedSize / 0.75) + 1);
Set<String> set = new HashSet<>((int)(expectedSize / 0.75) + 1);
```

### Bulk Operations
```java
// Use addAll instead of loop
list.addAll(Arrays.asList(1, 2, 3, 4, 5));

// Use removeIf instead of iterator
list.removeIf(x -> x % 2 == 0);

// Use streams for transformations
List<String> strings = list.stream()
    .map(String::valueOf)
    .collect(Collectors.toList());
```

### Immutable Collections
```java
// Use factory methods (Java 9+)
List<String> immutable = List.of("A", "B", "C");
Set<Integer> immutableSet = Set.of(1, 2, 3);
Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2);
```

---

## ⚠️ Common Pitfalls

### 1. ConcurrentModificationException
```java
// ❌ Wrong
for (String item : list) {
    if (condition) list.remove(item);  // Exception!
}

// ✅ Correct
list.removeIf(item -> condition);
// OR
Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    if (condition) iter.remove();
}
```

### 2. Arrays.asList() Fixed Size
```java
// ❌ Wrong
List<String> list = Arrays.asList("A", "B");
list.add("C");  // UnsupportedOperationException!

// ✅ Correct
List<String> list = new ArrayList<>(Arrays.asList("A", "B"));
list.add("C");  // Works!
```

### 3. HashMap with Mutable Keys
```java
// ❌ Wrong - modifying key after insertion
List<String> key = new ArrayList<>();
key.add("A");
map.put(key, "value");
key.add("B");  // Now can't find the entry!

// ✅ Correct - use immutable keys
String key = "A";
map.put(key, "value");
```

### 4. Not Overriding equals() and hashCode()
```java
// ❌ Wrong - only override equals()
class Person {
    String name;
    @Override
    public boolean equals(Object o) { /* ... */ }
    // Missing hashCode()!
}

// ✅ Correct - override both
class Person {
    String name;
    @Override
    public boolean equals(Object o) { /* ... */ }
    @Override
    public int hashCode() { return Objects.hash(name); }
}
```

---

## 📈 Learning Path

### Hour 1: Quick Overview
1. Run `run.bat`
2. Select option 7 (Run ALL Examples)
3. Review output

### Hours 2-4: Deep Dive
1. Study ListExamples
2. Study SetExamples
3. Study MapExamples

### Hours 5-6: Advanced
1. Study QueueExamples
2. Study CollectionUtilities
3. Study AdvancedCollectionConcepts

### Days 2-3: Mastery
1. Implement concepts from scratch
2. Modify examples
3. Create your own use cases

---

## 📚 Key Concepts by Class

### ListExamples (25 concepts)
- ArrayList vs LinkedList performance
- Capacity management
- Fail-fast vs fail-safe iterators
- CopyOnWriteArrayList

### SetExamples (30 concepts)
- HashSet internals
- Hash collision handling
- NavigableSet operations
- EnumSet efficiency

### MapExamples (40 concepts)
- HashMap treeification
- LRU Cache implementation
- Compute operations
- ConcurrentHashMap

### QueueExamples (35 concepts)
- PriorityQueue heap
- BlockingQueue implementations
- Producer-Consumer pattern
- DelayQueue

### CollectionUtilities (45 concepts)
- Immutable collections
- Collections utilities
- Arrays utilities
- Common pitfalls

### AdvancedCollectionConcepts (50 concepts)
- Capacity planning
- Collection views
- Custom implementations
- Performance optimization

### Streams (Complete module)
- StreamBasics, StreamCollectors, StreamAdvanced, StreamPitfalls
- Lazy evaluation, collectors, parallel streams, pitfalls

### Multithreading (Complete module)
- ThreadBasics, SynchronizationExamples, ExecutorExamples
- Concurrency utilities, concurrent collections, pitfalls

---

## 🎓 Interview Questions

### Easy (10 questions)
✅ Covered in ListExamples and SetExamples

### Medium (30 questions)
✅ Covered in MapExamples and QueueExamples

### Hard (40 questions)
✅ Covered in CollectionUtilities and AdvancedCollectionConcepts

### Expert (20 questions)
✅ Covered across all classes with deep dives

---

## 📞 Quick Help

### Problem: Java not found
**Solution**: Add Java to PATH or use full path
```
C:\Program Files\Java\jdk-XX\bin\javac ...
```

### Problem: Compilation errors
**Solution**: Ensure you're in src directory
```bash
cd src
javac collections/*.java
```

### Problem: Can't run examples
**Solution**: Compile first, run from src directory
```bash
javac Main.java collections/*.java
java Main
```

---

## ✅ Checklist for Mastery

- [ ] Run all examples successfully
- [ ] Understand ArrayList vs LinkedList trade-offs
- [ ] Explain HashMap internal implementation
- [ ] Implement LRU cache from scratch
- [ ] Use appropriate concurrent collections
- [ ] Avoid common pitfalls
- [ ] Override equals() and hashCode() correctly
- [ ] Choose optimal collection for each scenario
- [ ] Write thread-safe collection code
- [ ] Optimize collection performance

---

## 🎯 Quick Stats

- **6 Classes** with complete examples
- **60+ Methods** demonstrating concepts
- **225+ Concepts** covered in detail
- **100+ Interview Questions** answered
- **20+ Collection Types** explained

---

**Start Learning Now:**
```bash
compile.bat
run.bat
```

**Happy Coding! 🚀**
