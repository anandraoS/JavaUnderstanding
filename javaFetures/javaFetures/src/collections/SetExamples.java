package collections;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Advanced Set interface concepts for experienced developers
 * Topics: HashSet, LinkedHashSet, TreeSet, EnumSet, ConcurrentSkipListSet
 * Hash collision handling, ordering guarantees, performance characteristics
 */
public class SetExamples {

    /**
     * Question 1: How does HashSet internally work? Explain hash collision handling.
     */
    public static void demonstrateHashSetInternals() {
        System.out.println("=== HashSet Internals ===");

        // HashSet internally uses HashMap (keys are set elements, values are PRESENT object)
        Set<String> hashSet = new HashSet<>();

        System.out.println("HashSet uses HashMap internally");
        System.out.println("Default initial capacity: 16, load factor: 0.75");
        System.out.println("Hash collision handling: Chaining (linked list, trees for Java 8+)");

        // Custom object with poor hashCode
        class BadHash {
            int value;
            BadHash(int value) { this.value = value; }
            @Override
            public int hashCode() { return 1; } // All objects hash to same bucket!
            @Override
            public boolean equals(Object obj) {
                return obj instanceof BadHash && ((BadHash) obj).value == this.value;
            }
        }

        Set<BadHash> badSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            badSet.add(new BadHash(i));
        }
        System.out.println("Poor hashCode causes all elements in one bucket - O(n) lookup!");
    }

    /**
     * Question 2: What are the differences between HashSet, LinkedHashSet, and TreeSet?
     */
    public static void demonstrateSetTypes() {
        System.out.println("\n=== HashSet vs LinkedHashSet vs TreeSet ===");

        // HashSet: No ordering guarantee
        Set<String> hashSet = new HashSet<>();
        hashSet.addAll(Arrays.asList("Gamma", "Alpha", "Beta", "Delta"));
        System.out.println("HashSet (no order): " + hashSet);

        // LinkedHashSet: Maintains insertion order
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.addAll(Arrays.asList("Gamma", "Alpha", "Beta", "Delta"));
        System.out.println("LinkedHashSet (insertion order): " + linkedHashSet);

        // TreeSet: Sorted order (natural or comparator)
        Set<String> treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList("Gamma", "Alpha", "Beta", "Delta"));
        System.out.println("TreeSet (sorted order): " + treeSet);

        System.out.println("\nPerformance:");
        System.out.println("HashSet: O(1) add/remove/contains");
        System.out.println("LinkedHashSet: O(1) add/remove/contains + maintains order");
        System.out.println("TreeSet: O(log n) add/remove/contains + sorted");
    }

    /**
     * Question 3: Explain TreeSet implementation and NavigableSet operations
     */
    public static void demonstrateTreeSetAdvanced() {
        System.out.println("\n=== TreeSet and NavigableSet ===");

        // TreeSet implements NavigableSet (extends SortedSet)
        NavigableSet<Integer> treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6));

        System.out.println("TreeSet: " + treeSet);

        // NavigableSet operations
        System.out.println("first(): " + treeSet.first()); // 1
        System.out.println("last(): " + treeSet.last()); // 9
        System.out.println("lower(5): " + treeSet.lower(5)); // 4 (< 5)
        System.out.println("floor(5): " + treeSet.floor(5)); // 5 (<= 5)
        System.out.println("ceiling(5): " + treeSet.ceiling(5)); // 5 (>= 5)
        System.out.println("higher(5): " + treeSet.higher(5)); // 6 (> 5)

        // SubSet operations
        System.out.println("headSet(5): " + treeSet.headSet(5)); // [1, 2, 3, 4]
        System.out.println("tailSet(5): " + treeSet.tailSet(5)); // [5, 6, 7, 8, 9]
        System.out.println("subSet(3, 7): " + treeSet.subSet(3, 7)); // [3, 4, 5, 6]

        // Descending operations
        System.out.println("descendingSet(): " + treeSet.descendingSet());

        // pollFirst/pollLast (remove and return)
        System.out.println("pollFirst(): " + treeSet.pollFirst()); // 1
        System.out.println("pollLast(): " + treeSet.pollLast()); // 9
        System.out.println("After polls: " + treeSet);
    }

    /**
     * Question 4: How to create a custom Comparator for TreeSet?
     */
    public static void demonstrateCustomComparator() {
        System.out.println("\n=== Custom Comparator in TreeSet ===");

        // Reverse order comparator
        TreeSet<Integer> reverseSet = new TreeSet<>(Comparator.reverseOrder());
        reverseSet.addAll(Arrays.asList(5, 2, 8, 1, 9, 3));
        System.out.println("Reverse order: " + reverseSet);

        // Custom object with Comparator
        class Person {
            String name;
            int age;

            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }

            @Override
            public String toString() {
                return name + "(" + age + ")";
            }
        }

        // Sort by age, then by name
        TreeSet<Person> people = new TreeSet<>(
            Comparator.comparingInt((Person p) -> p.age)
                      .thenComparing(p -> p.name)
        );

        people.add(new Person("Alice", 30));
        people.add(new Person("Bob", 25));
        people.add(new Person("Charlie", 25));
        people.add(new Person("David", 35));

        System.out.println("Sorted people: " + people);
    }

    /**
     * Question 5: What is EnumSet and why is it efficient?
     */
    public static void demonstrateEnumSet() {
        System.out.println("\n=== EnumSet Advantages ===");

        enum Day {
            MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
        }

        // EnumSet uses bit vector internally - extremely efficient
        EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
        EnumSet<Day> allDays = EnumSet.allOf(Day.class);
        EnumSet<Day> noDays = EnumSet.noneOf(Day.class);

        System.out.println("Weekdays: " + weekdays);
        System.out.println("Weekend: " + weekend);

        // Set operations
        EnumSet<Day> workWeek = EnumSet.complementOf(weekend);
        System.out.println("Work week: " + workWeek);

        System.out.println("\nEnumSet advantages:");
        System.out.println("- Uses bit vector (long/long[]) - very compact");
        System.out.println("- All operations are O(1)");
        System.out.println("- Much faster than HashSet for enums");
        System.out.println("- Type-safe");
    }

    /**
     * Question 6: Explain concurrent Set implementations
     */
    public static void demonstrateConcurrentSets() {
        System.out.println("\n=== Concurrent Set Implementations ===");

        // CopyOnWriteArraySet - backed by CopyOnWriteArrayList
        Set<String> cowSet = new CopyOnWriteArraySet<>();
        cowSet.addAll(Arrays.asList("A", "B", "C"));
        System.out.println("CopyOnWriteArraySet: " + cowSet);
        System.out.println("Use case: Many reads, few writes, small sets");

        // ConcurrentSkipListSet - concurrent TreeSet alternative
        NavigableSet<Integer> skipListSet = new ConcurrentSkipListSet<>();
        skipListSet.addAll(Arrays.asList(5, 2, 8, 1, 9));
        System.out.println("ConcurrentSkipListSet: " + skipListSet);
        System.out.println("Use case: Concurrent sorted set, O(log n) operations");

        // ConcurrentHashMap.newKeySet() - concurrent HashSet alternative
        Set<String> concurrentHashSet = ConcurrentHashMap.newKeySet();
        concurrentHashSet.addAll(Arrays.asList("X", "Y", "Z"));
        System.out.println("ConcurrentHashMap.newKeySet(): " + concurrentHashSet);
        System.out.println("Use case: Concurrent unsorted set, O(1) operations");
    }

    /**
     * Question 7: How to implement equals() and hashCode() correctly for Set usage?
     */
    public static void demonstrateEqualsHashCode() {
        System.out.println("\n=== Proper equals() and hashCode() ===");

        class Employee {
            private final int id;
            private final String name;

            Employee(int id, String name) {
                this.id = id;
                this.name = name;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Employee employee = (Employee) o;
                return id == employee.id && Objects.equals(name, employee.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name);
            }

            @Override
            public String toString() {
                return "Emp{" + id + ", " + name + "}";
            }
        }

        Set<Employee> employees = new HashSet<>();
        Employee emp1 = new Employee(1, "John");
        Employee emp2 = new Employee(1, "John"); // Same as emp1

        employees.add(emp1);
        employees.add(emp2); // Won't be added (duplicate)

        System.out.println("Employees: " + employees);
        System.out.println("Size: " + employees.size()); // 1

        System.out.println("\nContracts:");
        System.out.println("1. If a.equals(b), then a.hashCode() == b.hashCode()");
        System.out.println("2. Consistent: multiple calls return same value");
        System.out.println("3. If hashCodes differ, objects are definitely different");
    }

    /**
     * Question 8: Demonstrate Set bulk operations
     */
    public static void demonstrateSetOperations() {
        System.out.println("\n=== Set Bulk Operations ===");

        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));

        // Union
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("Union: " + union);

        // Intersection
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("Intersection: " + intersection);

        // Difference
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("Difference (set1 - set2): " + difference);

        // Symmetric Difference
        Set<Integer> symDiff = new HashSet<>(set1);
        symDiff.addAll(set2);
        Set<Integer> temp = new HashSet<>(set1);
        temp.retainAll(set2);
        symDiff.removeAll(temp);
        System.out.println("Symmetric Difference: " + symDiff);

        // Subset check
        Set<Integer> subset = new HashSet<>(Arrays.asList(2, 3));
        System.out.println("Is {2,3} subset of set1? " + set1.containsAll(subset));
    }

    public static void main(String[] args) {
        demonstrateHashSetInternals();
        demonstrateSetTypes();
        demonstrateTreeSetAdvanced();
        demonstrateCustomComparator();
        demonstrateEnumSet();
        demonstrateConcurrentSets();
        demonstrateEqualsHashCode();
        demonstrateSetOperations();
    }
}

