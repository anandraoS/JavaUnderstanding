package oops;

/*
 * =============================================================================
 * Inheritance.java — extends, super, Method Overriding
 * =============================================================================
 *
 * CONCEPT: Inheritance
 * ----------------------
 * Inheritance allows a class (child/subclass) to inherit fields and methods
 * from another class (parent/superclass). This promotes code reuse.
 *
 *   class Child extends Parent { ... }
 *
 * KEY TERMS:
 *  - Superclass (Parent): the class being inherited FROM
 *  - Subclass (Child): the class that INHERITS
 *  - extends: keyword to inherit from a class
 *  - super: refers to the parent class (methods and constructor)
 *  - @Override: annotation to mark overridden methods
 *
 * WHAT IS INHERITED:
 *  - Public and protected fields and methods
 *  - (NOT private members, NOT constructors)
 *
 * METHOD OVERRIDING:
 *  - Subclass can redefine a method from the parent
 *  - Same name, same parameters, same (or covariant) return type
 *  - Use @Override annotation (best practice — compiler checks it)
 *  - super.methodName() calls the PARENT's version of the method
 *
 * JAVA SINGLE INHERITANCE:
 *  - A class can extend only ONE class (no multiple inheritance for classes)
 *  - Use interfaces for multiple inheritance of behavior
 *
 * IS-A RELATIONSHIP:
 *  - Dog IS-A Animal → valid inheritance
 *  - Car IS-A Vehicle → valid inheritance
 *  - Car IS-A Engine → NOT valid (a car HAS an engine, use composition)
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Inheritance.java
 *  $ java -cp out oops.Inheritance
 * =============================================================================
 */

// ── PARENT CLASS ─────────────────────────────────────────────────────────────
class Animal {
    protected String name;      // protected = accessible in subclasses
    protected String sound;
    protected int age;

    Animal(String name, int age) {
        this.name = name;
        this.age = age;
        this.sound = "...";
        System.out.println("Animal constructor called for: " + name);
    }

    void makeSound() {
        System.out.println(name + " says: " + sound);
    }

    void eat() {
        System.out.println(name + " is eating.");
    }

    void sleep() {
        System.out.println(name + " is sleeping.");
    }

    @Override
    public String toString() {
        return "Animal{name='" + name + "', age=" + age + "}";
    }
}

// ── CHILD CLASS 1: Dog ────────────────────────────────────────────────────────
class Dog extends Animal {   // Dog inherits from Animal
    private String breed;

    Dog(String name, int age, String breed) {
        super(name, age);    // MUST call parent constructor first! super() must be first statement
        this.breed = breed;
        this.sound = "Woof";  // can access protected field from parent
        System.out.println("Dog constructor called for: " + name);
    }

    // METHOD OVERRIDING — redefine the parent's method
    @Override
    void makeSound() {
        System.out.println(name + " the " + breed + " barks: " + sound + " " + sound + "!");
    }

    // NEW method specific to Dog (doesn't exist in Animal)
    void fetch() {
        System.out.println(name + " fetches the ball!");
    }

    // Call parent method using super
    void makeOriginalSound() {
        super.makeSound();   // calls Animal.makeSound()
    }

    @Override
    public String toString() {
        return "Dog{name='" + name + "', age=" + age + ", breed='" + breed + "'}";
    }
}

// ── CHILD CLASS 2: Cat ────────────────────────────────────────────────────────
class Cat extends Animal {
    private boolean isIndoor;

    Cat(String name, int age, boolean isIndoor) {
        super(name, age);
        this.sound = "Meow";
        this.isIndoor = isIndoor;
        System.out.println("Cat constructor called for: " + name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says: " + sound + (isIndoor ? " (indoor cat)" : " (outdoor cat)"));
    }

    void purr() {
        System.out.println(name + " purrs...");
    }

    @Override
    public String toString() {
        return "Cat{name='" + name + "', indoor=" + isIndoor + "}";
    }
}

// ── MULTI-LEVEL INHERITANCE: GoldenRetriever extends Dog extends Animal ───────
class GoldenRetriever extends Dog {
    GoldenRetriever(String name, int age) {
        super(name, age, "Golden Retriever");   // calls Dog's constructor
        System.out.println("GoldenRetriever constructor called");
    }

    @Override
    void makeSound() {
        System.out.println(name + " the Golden Retriever says: Woof! (friendly)");
    }

    void guidePerson() {
        System.out.println(name + " guides person safely across the street.");
    }
}

public class Inheritance {

    public static void main(String[] args) {

        System.out.println("=== Creating Objects ===");
        Animal animal = new Animal("Generic Animal", 3);
        Dog dog = new Dog("Rex", 4, "German Shepherd");
        Cat cat = new Cat("Whiskers", 2, true);

        System.out.println("\n=== Method Overriding ===");
        animal.makeSound();   // Animal's version
        dog.makeSound();      // Dog's OVERRIDDEN version
        cat.makeSound();      // Cat's OVERRIDDEN version

        System.out.println("\n=== Inherited Methods ===");
        dog.eat();     // inherited from Animal — NOT overridden
        dog.sleep();   // inherited from Animal — NOT overridden

        System.out.println("\n=== Child-Specific Methods ===");
        dog.fetch();   // Dog-specific — not in Animal
        cat.purr();    // Cat-specific — not in Animal

        System.out.println("\n=== Calling Parent Method with super ===");
        dog.makeOriginalSound();   // calls Animal.makeSound() via super

        System.out.println("\n=== Multi-Level Inheritance ===");
        GoldenRetriever buddy = new GoldenRetriever("Buddy", 3);
        buddy.makeSound();     // GoldenRetriever's overridden version
        buddy.fetch();         // inherited from Dog
        buddy.eat();           // inherited from Animal (via Dog)
        buddy.guidePerson();   // GoldenRetriever-specific

        System.out.println("\n=== instanceof (IS-A check) ===");
        System.out.println("buddy instanceof GoldenRetriever: " + (buddy instanceof GoldenRetriever)); // true
        System.out.println("buddy instanceof Dog:             " + (buddy instanceof Dog));             // true!
        System.out.println("buddy instanceof Animal:          " + (buddy instanceof Animal));          // true!
        System.out.println("buddy instanceof Cat:             " + (((Animal) buddy) instanceof Cat));     // false

        System.out.println("\n=== Polymorphic Reference ===");
        // Parent type variable can hold child type object
        Animal polyAnimal = new Dog("Fido", 2, "Poodle");   // Animal ref, Dog object
        polyAnimal.makeSound();   // Dog's version runs (runtime polymorphism!)
        // polyAnimal.fetch();   ← COMPILE ERROR — Animal type doesn't have fetch()

        System.out.println("\n=== toString ===");
        System.out.println(dog.toString());
        System.out.println(cat.toString());
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Creating Objects ===
 * Animal constructor called for: Generic Animal
 * Animal constructor called for: Rex
 * Dog constructor called for: Rex
 * Animal constructor called for: Whiskers
 * Cat constructor called for: Whiskers
 *
 * === Method Overriding ===
 * Generic Animal says: ...
 * Rex the German Shepherd barks: Woof Woof!
 * Whiskers says: Meow (indoor cat)
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Calling super() not as the first statement in child constructor → COMPILE ERROR
 * 2. Forgetting @Override — misspelling method name creates new method, not override:
 *    void makesound() {} ← NOT an override! use @Override to catch this mistake
 * 3. Trying to access private parent fields in child class:
 *    class Child extends Parent { void m() { this.privateField = 1; } } ← COMPILE ERROR
 *    Use protected or provide getters/setters.
 * 4. Multiple inheritance with classes: class C extends A, B {} ← NOT ALLOWED in Java
 *    Use interfaces for multiple behavior inheritance.
 * 5. Confusion between overriding (same signature) and overloading (different params)
 */
