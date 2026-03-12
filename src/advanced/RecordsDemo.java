package advanced;

import java.util.*;
import java.util.stream.*;

/*
 * =============================================================================
 * RecordsDemo.java — Java Records (Java 16+)
 * =============================================================================
 *
 * CONCEPT: Records
 * -----------------
 * Records (introduced in Java 16) are a special kind of class for modeling
 * IMMUTABLE DATA. They automatically provide:
 *  - Private final fields
 *  - Public accessor methods (getters named like the field, NOT getXxx())
 *  - Constructor
 *  - equals() and hashCode() based on all components
 *  - toString() showing all components
 *
 * BEFORE RECORDS (verbose):
 *   class Point {
 *       private final int x;
 *       private final int y;
 *       public Point(int x, int y) { this.x = x; this.y = y; }
 *       public int x() { return x; }
 *       public int y() { return y; }
 *       @Override public boolean equals(Object o) { ... }
 *       @Override public int hashCode() { ... }
 *       @Override public String toString() { ... }
 *   }
 *
 * WITH RECORDS (concise):
 *   record Point(int x, int y) {}
 *
 * KEY POINTS:
 *  - Records are implicitly FINAL (can't be extended)
 *  - Components are PRIVATE FINAL (immutable)
 *  - Accessor methods match the component names (not getX(), but x())
 *  - Can implement interfaces
 *  - Can have COMPACT CONSTRUCTOR for validation/normalization
 *  - Can have additional methods and static members
 *  - Can NOT extend classes (already extends java.lang.Record)
 *
 * HOW TO RUN:
 *  $ javac -d out src/advanced/RecordsDemo.java
 *  $ java -cp out advanced.RecordsDemo
 * =============================================================================
 */

// ── BASIC RECORDS ─────────────────────────────────────────────────────────────

record Point(int x, int y) {
    // Optional: add methods
    double distanceTo(Point other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Static factory method
    static Point origin() { return new Point(0, 0); }
}

record Person(String name, int age, String email) {
    // COMPACT CONSTRUCTOR: validates/normalizes inputs
    // Parameters are implicitly declared; just write the validation body
    Person {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (age < 0 || age > 150)          throw new IllegalArgumentException("Invalid age: " + age);
        // Normalization: trim whitespace
        name = name.trim();
        email = email == null ? "" : email.toLowerCase().trim();
    }

    // Additional method
    boolean isAdult() { return age >= 18; }

    String greeting() { return "Hello, I'm " + name + " (" + age + ")"; }
}

record Range(int min, int max) {
    // Compact constructor
    Range {
        if (min > max) throw new IllegalArgumentException("min (" + min + ") > max (" + max + ")");
    }

    int size() { return max - min; }
    boolean contains(int value) { return value >= min && value <= max; }
    boolean overlaps(Range other) { return this.min <= other.max && other.min <= this.max; }
}

// ── GENERIC RECORD ────────────────────────────────────────────────────────────
record Pair<A, B>(A first, B second) {
    // Swap the pair
    Pair<B, A> swapped() { return new Pair<>(second, first); }
}

// ── RECORD IMPLEMENTING INTERFACE ─────────────────────────────────────────────
interface Describable {
    String describe();
}

record Product(String name, double price, String category) implements Describable {
    @Override
    public String describe() {
        return String.format("%-20s $%6.2f [%s]", name, price, category);
    }
}

public class RecordsDemo {

    public static void main(String[] args) {

        // ── BASIC RECORD USAGE ────────────────────────────────────────────────
        System.out.println("=== Basic Record: Point ===");

        Point p1 = new Point(3, 4);
        Point p2 = new Point(0, 0);
        Point origin = Point.origin();

        System.out.println("p1:     " + p1);           // toString() auto-generated
        System.out.println("p2:     " + p2);
        System.out.println("origin: " + origin);

        // Accessor methods (NOT getX() — just x())
        System.out.println("p1.x(): " + p1.x());
        System.out.println("p1.y(): " + p1.y());

        System.out.printf("Distance from p1 to p2: %.2f%n", p1.distanceTo(p2));

        // equals() is auto-generated (compares all components)
        Point p3 = new Point(3, 4);
        System.out.println("p1.equals(p3): " + p1.equals(p3));  // true — same values!
        System.out.println("p1 == p3:      " + (p1 == p3));     // false — different objects

        // ── RECORD WITH VALIDATION ────────────────────────────────────────────
        System.out.println("\n=== Record with Compact Constructor: Person ===");

        Person alice = new Person("  Alice  ", 25, "ALICE@EXAMPLE.COM");
        System.out.println(alice);
        System.out.println("Name (trimmed): '" + alice.name() + "'");
        System.out.println("Email (lower):  '" + alice.email() + "'");
        System.out.println("Is adult: " + alice.isAdult());
        System.out.println(alice.greeting());

        // Validation works
        try {
            Person invalid = new Person("", 25, "test@test.com");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation: " + e.getMessage());
        }

        // ── RANGE RECORD ──────────────────────────────────────────────────────
        System.out.println("\n=== Range Record ===");

        Range r1 = new Range(1, 10);
        Range r2 = new Range(5, 15);
        Range r3 = new Range(20, 30);

        System.out.println("r1: " + r1 + " size=" + r1.size());
        System.out.println("r1 contains 5: " + r1.contains(5));
        System.out.println("r1 contains 15: " + r1.contains(15));
        System.out.println("r1 overlaps r2: " + r1.overlaps(r2));
        System.out.println("r1 overlaps r3: " + r1.overlaps(r3));

        // ── GENERIC RECORD ────────────────────────────────────────────────────
        System.out.println("\n=== Generic Record: Pair<A, B> ===");

        Pair<String, Integer> nameAge = new Pair<>("Alice", 25);
        System.out.println("Pair: " + nameAge);
        System.out.println("First:   " + nameAge.first());
        System.out.println("Second:  " + nameAge.second());
        System.out.println("Swapped: " + nameAge.swapped());

        // ── RECORD IN COLLECTIONS ─────────────────────────────────────────────
        System.out.println("\n=== Records in Collections ===");

        List<Product> products = List.of(
            new Product("Java Book",   49.99, "Books"),
            new Product("Keyboard",    89.99, "Electronics"),
            new Product("Coffee Mug",  12.99, "Kitchen"),
            new Product("IDE License", 199.0, "Software")
        );

        System.out.println("Products:");
        products.forEach(p -> System.out.println("  " + p.describe()));

        // Use records with Stream API
        double avgPrice = products.stream()
            .mapToDouble(Product::price)
            .average()
            .orElse(0);
        System.out.printf("Average price: $%.2f%n", avgPrice);

        List<Product> expensive = products.stream()
            .filter(p -> p.price() > 50)
            .sorted(Comparator.comparingDouble(Product::price).reversed())
            .collect(Collectors.toList());
        System.out.println("Expensive products:");
        expensive.forEach(p -> System.out.println("  " + p.describe()));

        // ── HASHMAP WITH RECORD KEYS ───────────────────────────────────────────
        System.out.println("\n=== Record as Map Key ===");

        Map<Point, String> grid = new HashMap<>();
        grid.put(new Point(0, 0), "Origin");
        grid.put(new Point(1, 0), "Right");
        grid.put(new Point(0, 1), "Up");

        // Records work perfectly as map keys because equals() and hashCode() are correct!
        System.out.println("At (0,0): " + grid.get(new Point(0, 0)));  // "Origin" — works!
        System.out.println("At (1,0): " + grid.get(new Point(1, 0)));
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic Record: Point ===
 * p1:     Point[x=3, y=4]
 * p2:     Point[x=0, y=0]
 * p1.x(): 3
 * p1.equals(p3): true
 * p1 == p3:      false
 *
 * === Record with Compact Constructor: Person ===
 * Person[name=Alice, age=25, email=alice@example.com]
 * Name (trimmed): 'Alice'
 * Email (lower):  'alice@example.com'
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using getX() instead of x() for accessor methods:
 *    record Point(int x, int y) {}
 *    point.getX() ← COMPILE ERROR, must use point.x()
 *
 * 2. Trying to modify a record's fields:
 *    record Point(int x, int y) {}
 *    point.x = 5; ← COMPILE ERROR, fields are final
 *    Records are IMMUTABLE by design.
 *
 * 3. Trying to extend a record:
 *    class ColorPoint extends Point {} ← COMPILE ERROR, records are final
 *    Use composition instead.
 *
 * 4. Records can't have instance fields outside the record header:
 *    record MyRecord(int x) { int y; } ← COMPILE ERROR
 *    All instance fields must be in the record header.
 *
 * 5. Confusing compact constructor with the regular constructor:
 *    In compact constructor: DON'T assign to fields (done automatically)
 *    Just validate and normalize the implicit parameters.
 *    Avoid: record Point(int x) { Point { this.x = x; } } ← error! 'this.x' not assignable in compact constructor
 */
