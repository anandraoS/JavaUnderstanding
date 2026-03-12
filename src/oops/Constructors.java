package oops;

/*
 * =============================================================================
 * Constructors.java — Default, Parameterized, Copy Constructors, and 'this'
 * =============================================================================
 *
 * CONCEPT: Constructors
 * ----------------------
 * A constructor is a special method that is automatically called when an object
 * is created with 'new'. It initializes the object's state.
 *
 * RULES:
 *  - Constructor name MUST match the class name exactly
 *  - Constructors have NO return type (not even void)
 *  - A class can have MULTIPLE constructors (constructor overloading)
 *  - If no constructor is defined, Java provides a default no-args constructor
 *  - Once you define ANY constructor, Java does NOT provide a default one
 *
 * TYPES OF CONSTRUCTORS:
 *  1. Default (No-Args) Constructor — takes no parameters
 *  2. Parameterized Constructor     — takes parameters
 *  3. Copy Constructor              — creates a copy of another object
 *
 * 'this' KEYWORD:
 *  - Refers to the current object instance
 *  - Used to distinguish between field names and parameter names
 *  - this(...) can be used to call another constructor in the same class
 *    (constructor chaining) — must be the FIRST statement!
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Constructors.java
 *  $ java -cp out oops.Constructors
 * =============================================================================
 */

class Student {
    String name;
    int age;
    double gpa;
    String major;

    // ── 1. DEFAULT (NO-ARGS) CONSTRUCTOR ─────────────────────────────────────
    // Called when no arguments are provided: new Student()
    Student() {
        // Assign default values
        this.name = "Unknown";
        this.age = 18;
        this.gpa = 0.0;
        this.major = "Undecided";
        System.out.println("Default constructor called");
    }

    // ── 2. PARAMETERIZED CONSTRUCTOR ─────────────────────────────────────────
    // Called with arguments: new Student("Alice", 20, 3.8, "CS")
    Student(String name, int age, double gpa, String major) {
        this.name = name;      // 'this.name' = field, 'name' = parameter
        this.age = age;
        this.gpa = gpa;
        this.major = major;
        System.out.println("Parameterized constructor called for: " + this.name);
    }

    // ── 3. PARTIAL CONSTRUCTOR (using this() for constructor chaining) ────────
    // Calls another constructor to avoid code duplication
    Student(String name) {
        this(name, 18, 0.0, "Undecided");   // calls the 4-arg constructor!
        // this() MUST be the first statement in the constructor
        System.out.println("Name-only constructor called (chained)");
    }

    // ── 4. COPY CONSTRUCTOR ───────────────────────────────────────────────────
    // Creates a new object with the same values as another object (deep copy)
    Student(Student other) {
        this.name  = other.name;    // copy each field
        this.age   = other.age;
        this.gpa   = other.gpa;
        this.major = other.major;
        System.out.println("Copy constructor called for: " + this.name);
    }

    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, gpa=%.2f, major='%s'}",
                name, age, gpa, major);
    }
}

public class Constructors {

    public static void main(String[] args) {

        System.out.println("=== 1. Default Constructor ===");
        Student s1 = new Student();          // calls Student()
        System.out.println(s1);

        System.out.println("\n=== 2. Parameterized Constructor ===");
        Student s2 = new Student("Alice", 20, 3.85, "Computer Science");
        System.out.println(s2);

        System.out.println("\n=== 3. Constructor Chaining (this()) ===");
        Student s3 = new Student("Bob");     // calls Student(String), which calls Student(String,int,double,String)
        System.out.println(s3);

        System.out.println("\n=== 4. Copy Constructor ===");
        Student s4 = new Student(s2);        // creates a new object with s2's values
        System.out.println("Original: " + s2);
        System.out.println("Copy:     " + s4);

        // Modifying the copy should NOT affect the original
        s4.name = "Modified Copy";
        System.out.println("\nAfter modifying copy:");
        System.out.println("Original: " + s2.name);   // Alice — unchanged!
        System.out.println("Copy:     " + s4.name);   // Modified Copy

        // ── DEMONSTRATING NO DEFAULT AFTER CUSTOM ────────────────────────────
        System.out.println("\n=== Constructor Rules Demo ===");
        System.out.println("Student class has explicit constructors.");
        System.out.println("Java does NOT auto-generate default constructor when any is defined.");

        // ── OVERLOADING EXAMPLE ───────────────────────────────────────────────
        System.out.println("\n=== Constructor Overloading ===");
        Student[] students = {
            new Student(),
            new Student("Charlie"),
            new Student("Diana", 22, 3.9, "Physics")
        };
        System.out.println("\nAll students:");
        for (Student s : students) {
            System.out.println("  " + s);
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === 1. Default Constructor ===
 * Default constructor called
 * Student{name='Unknown', age=18, gpa=0.00, major='Undecided'}
 *
 * === 2. Parameterized Constructor ===
 * Parameterized constructor called for: Alice
 * Student{name='Alice', age=20, gpa=3.85, major='Computer Science'}
 *
 * === 3. Constructor Chaining (this()) ===
 * Parameterized constructor called for: Bob
 * Name-only constructor called (chained)
 * Student{name='Bob', age=18, gpa=0.00, major='Undecided'}
 *
 * === 4. Copy Constructor ===
 * Copy constructor called for: Alice
 * Original: Student{name='Alice', ...}
 * Copy:     Student{name='Alice', ...}
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Adding return type to constructor: void Student() {...} ← becomes a method, not constructor!
 * 2. this() not being the first statement in a constructor → COMPILE ERROR
 * 3. Infinite constructor chain: A() { this(); } → causes StackOverflowError
 * 4. Forgetting to re-define default constructor after adding parameterized one
 *    (if you want both, you must write both explicitly)
 * 5. Shallow copy: constructor sets this.name = other.name ← for mutable objects
 *    (like arrays, ArrayList), this shares the reference! Use deep copy.
 */
