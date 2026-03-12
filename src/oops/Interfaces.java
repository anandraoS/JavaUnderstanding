package oops;

/*
 * =============================================================================
 * Interfaces.java — Interfaces, Default Methods, Static Methods, Functional Interfaces
 * =============================================================================
 *
 * CONCEPT: Interface
 * ------------------
 * An interface is a PURE CONTRACT — it specifies WHAT a class can do,
 * without specifying HOW it does it (mostly).
 *
 *   interface InterfaceName {
 *       void abstractMethod();   // implicitly public abstract
 *       default void defaultMethod() { ... }  // Java 8+ concrete implementation
 *       static void staticMethod() { ... }    // Java 8+ class-level method
 *   }
 *
 * KEY POINTS:
 *  - All fields in an interface are implicitly: public static final (constants)
 *  - All methods are implicitly: public abstract (unless default/static/private)
 *  - A class IMPLEMENTS an interface: class Dog implements Animal
 *  - A class can implement MULTIPLE interfaces (unlike extends which allows only 1)
 *  - An interface can EXTEND multiple other interfaces
 *  - Cannot be instantiated directly
 *
 * JAVA 8+ ADDITIONS:
 *  - default methods: provide a default implementation (can be overridden)
 *  - static methods:  utility methods on the interface
 *  - Java 9+: private methods (helper for default methods)
 *
 * FUNCTIONAL INTERFACE:
 *  - An interface with exactly ONE abstract method
 *  - Can be used with lambda expressions
 *  - @FunctionalInterface annotation (optional but recommended)
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Interfaces.java
 *  $ java -cp out oops.Interfaces
 * =============================================================================
 */

// ── INTERFACE DEFINITIONS ─────────────────────────────────────────────────────

interface Flyable {
    double MAX_ALTITUDE = 15000;   // implicitly public static final

    void fly();                    // abstract method — subclass must implement
    void land();

    default void describeFlying() {   // default implementation (Java 8+)
        System.out.println(getClass().getSimpleName() + " can fly up to " + MAX_ALTITUDE + " meters");
    }

    static boolean isHighAltitude(double altitude) {   // static utility method
        return altitude > 10000;
    }
}

interface Swimmable {
    void swim();

    default void describeSwimming() {
        System.out.println(getClass().getSimpleName() + " can swim.");
    }
}

interface Runnable2 {   // Named Runnable2 to avoid conflict with java.lang.Runnable
    void run();
}

// ── IMPLEMENTING ONE INTERFACE ────────────────────────────────────────────────
class Eagle implements Flyable {
    private String name;

    Eagle(String name) { this.name = name; }

    @Override
    public void fly() {
        System.out.println(name + " spreads its wings and soars high!");
    }

    @Override
    public void land() {
        System.out.println(name + " gracefully lands on the cliff.");
    }
}

// ── IMPLEMENTING MULTIPLE INTERFACES ─────────────────────────────────────────
class Duck implements Flyable, Swimmable, Runnable2 {
    private String name;

    Duck(String name) { this.name = name; }

    @Override public void fly() { System.out.println(name + " flaps wings and flies low."); }
    @Override public void land() { System.out.println(name + " splashes down."); }
    @Override public void swim() { System.out.println(name + " paddles across the pond."); }
    @Override public void run() { System.out.println(name + " waddles fast!"); }
}

// ── INTERFACE EXTENDING INTERFACE ────────────────────────────────────────────
interface AdvancedFlyable extends Flyable {
    void performAcrobatics();   // adds a new abstract method
}

class AirplaneMock implements AdvancedFlyable {
    @Override public void fly()  { System.out.println("Airplane takes off."); }
    @Override public void land() { System.out.println("Airplane lands on runway."); }
    @Override public void performAcrobatics() { System.out.println("Airplane does a barrel roll!"); }
}

// ── FUNCTIONAL INTERFACE ──────────────────────────────────────────────────────
@FunctionalInterface   // annotation ensures only one abstract method
interface MathOperation {
    int operate(int a, int b);   // single abstract method

    // Can still have default and static methods
    default String describe() { return "MathOperation functional interface"; }
}

public class Interfaces {

    public static void main(String[] args) {

        // ── USING INTERFACES ──────────────────────────────────────────────────
        System.out.println("=== Eagle (implements Flyable) ===");
        Eagle eagle = new Eagle("Eagle");
        eagle.fly();
        eagle.land();
        eagle.describeFlying();   // default method

        System.out.println("\n=== Duck (implements Flyable + Swimmable + Runnable2) ===");
        Duck duck = new Duck("Donald");
        duck.fly();
        duck.swim();
        duck.run();
        duck.describeFlying();    // inherited default
        duck.describeSwimming();  // inherited default

        // ── INTERFACE AS TYPE ─────────────────────────────────────────────────
        System.out.println("\n=== Interface as Type (Polymorphism) ===");

        Flyable[] flyers = { new Eagle("Bald Eagle"), new Duck("Daffy"), new AirplaneMock() };
        for (Flyable f : flyers) {
            f.fly();   // each object's own fly() is called
        }

        // ── STATIC METHOD ON INTERFACE ────────────────────────────────────────
        System.out.println("\n=== Static Method on Interface ===");
        System.out.println("Is 12000m high altitude? " + Flyable.isHighAltitude(12000));
        System.out.println("Is 5000m high altitude?  " + Flyable.isHighAltitude(5000));

        // ── FUNCTIONAL INTERFACE WITH LAMBDA ──────────────────────────────────
        System.out.println("\n=== Functional Interface with Lambda ===");

        // Lambda expression provides the implementation of the single abstract method
        MathOperation add      = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;
        MathOperation max      = (a, b) -> a > b ? a : b;

        System.out.println("add(5, 3)      = " + add.operate(5, 3));
        System.out.println("multiply(5, 3) = " + multiply.operate(5, 3));
        System.out.println("max(5, 3)      = " + max.operate(5, 3));

        // ── CONSTANTS IN INTERFACES ───────────────────────────────────────────
        System.out.println("\n=== Interface Constants ===");
        System.out.println("MAX_ALTITUDE = " + Flyable.MAX_ALTITUDE);   // accessed via interface name
        System.out.println("(also accessible via implementing class: " + Eagle.MAX_ALTITUDE + ")");
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Eagle (implements Flyable) ===
 * Eagle spreads its wings and soars high!
 * Eagle gracefully lands on the cliff.
 * Eagle can fly up to 15000.0 meters
 *
 * === Duck (implements Flyable + Swimmable + Runnable2) ===
 * Donald flaps wings and flies low.
 * Donald paddles across the pond.
 * Donald waddles fast!
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Trying to instantiate an interface: new Flyable() → COMPILE ERROR
 *    (Can create anonymous classes or use lambdas for functional interfaces)
 *
 * 2. Implementing an interface but not providing all abstract methods:
 *    Either implement all, or make the class abstract.
 *
 * 3. Diamond problem with default methods: if two interfaces both have
 *    the same default method and a class implements both, you must override it.
 *
 * 4. Interface constants: all interface fields are public static final.
 *    You cannot have instance fields in interfaces.
 *
 * 5. Confusing abstract class and interface:
 *    - Use interface when unrelated classes should share the same capability
 *    - Use abstract class when classes ARE of the same family and share state
 *
 * 6. @FunctionalInterface annotation: if you add a second abstract method,
 *    the annotation causes a compile error (protects you from accidental changes)
 */
