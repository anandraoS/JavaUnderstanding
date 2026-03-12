package oops;

/*
 * =============================================================================
 * ClassesAndObjects.java — Classes, Objects, Fields, and Methods
 * =============================================================================
 *
 * CONCEPT: Classes and Objects
 * -----------------------------
 * A CLASS is a blueprint/template that defines the structure and behavior
 * of objects. It specifies what data (fields) an object will have and what
 * actions (methods) it can perform.
 *
 * An OBJECT is an instance of a class — a concrete entity created from the
 * blueprint. Each object has its own copy of the instance fields.
 *
 * ANATOMY OF A CLASS:
 *   class ClassName {
 *       // 1. Fields (instance variables) — store data
 *       dataType fieldName;
 *
 *       // 2. Constructor — called when creating an object with 'new'
 *       ClassName(params) { ... }
 *
 *       // 3. Methods — define behavior
 *       returnType methodName(params) { ... }
 *   }
 *
 * CREATING AN OBJECT:
 *   ClassName obj = new ClassName(args);
 *   // 'new' allocates heap memory and calls the constructor
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/ClassesAndObjects.java
 *  $ java -cp out oops.ClassesAndObjects
 * =============================================================================
 */

// A class representing a Car — this is the BLUEPRINT
class Car {
    // ── FIELDS (Instance Variables) ──────────────────────────────────────────
    // Each Car object will have its own copy of these fields
    String brand;       // car brand (default: null)
    String model;       // car model
    int year;           // manufacturing year
    double price;       // price in USD
    boolean isRunning;  // is the engine running?

    // ── STATIC FIELD ─────────────────────────────────────────────────────────
    // Shared by ALL Car objects — only ONE copy in memory
    static int totalCarsCreated = 0;

    // ── CONSTRUCTOR ───────────────────────────────────────────────────────────
    // Called automatically when you do: new Car(...)
    Car(String brand, String model, int year, double price) {
        this.brand = brand;   // 'this' refers to the current object
        this.model = model;
        this.year = year;
        this.price = price;
        this.isRunning = false;   // default: engine off
        totalCarsCreated++;        // increment the shared counter
    }

    // ── METHODS ───────────────────────────────────────────────────────────────
    void startEngine() {
        if (!isRunning) {
            isRunning = true;
            System.out.println(brand + " " + model + ": Vroom! Engine started.");
        } else {
            System.out.println(brand + " " + model + ": Engine is already running.");
        }
    }

    void stopEngine() {
        isRunning = false;
        System.out.println(brand + " " + model + ": Engine stopped.");
    }

    // Method that returns a value
    String getDescription() {
        return year + " " + brand + " " + model + " ($" + price + ")";
    }

    // Method that takes a parameter
    void applyDiscount(double discountPercent) {
        double discount = price * discountPercent / 100;
        price -= discount;
        System.out.printf("Discount applied: -$%.2f. New price: $%.2f%n", discount, price);
    }

    // toString: describes the object as a String (called by println, +, etc.)
    @Override
    public String toString() {
        return "Car{brand='" + brand + "', model='" + model + "', year=" + year + ", price=$" + price + "}";
    }
}

// Main class with the entry point
public class ClassesAndObjects {

    public static void main(String[] args) {

        // ── CREATING OBJECTS ─────────────────────────────────────────────────
        System.out.println("=== Creating Car Objects ===");

        // Each 'new Car(...)' creates a separate object in heap memory
        Car car1 = new Car("Toyota", "Camry", 2022, 25000.0);
        Car car2 = new Car("Honda", "Civic", 2023, 22000.0);
        Car car3 = new Car("Tesla", "Model 3", 2024, 40000.0);

        System.out.println("Cars created: " + Car.totalCarsCreated);  // 3 (static field)

        // ── ACCESSING FIELDS AND METHODS ─────────────────────────────────────
        System.out.println("\n=== Accessing Fields ===");
        System.out.println("car1.brand: " + car1.brand);
        System.out.println("car2.year:  " + car2.year);

        System.out.println("\n=== Calling Methods ===");
        System.out.println(car1.getDescription());
        car1.startEngine();
        car1.startEngine();    // try to start an already-running engine
        car1.stopEngine();

        // ── MODIFYING FIELDS ─────────────────────────────────────────────────
        System.out.println("\n=== Modifying Fields ===");
        System.out.println("Before discount: " + car2.price);
        car2.applyDiscount(10);   // 10% off
        System.out.println("After  discount: " + car2.price);

        // ── OBJECT REFERENCE ─────────────────────────────────────────────────
        System.out.println("\n=== Object References ===");

        Car car4 = car1;   // car4 and car1 now point to THE SAME object!
        car4.brand = "Modified";
        System.out.println("car1.brand: " + car1.brand);  // "Modified" — shared reference!
        System.out.println("car4.brand: " + car4.brand);  // "Modified" — same object

        // ── TOSTRING ─────────────────────────────────────────────────────────
        System.out.println("\n=== toString() ===");
        System.out.println(car3.toString());   // explicit call
        System.out.println(car3);              // println calls toString() automatically

        // ── NULL REFERENCE ────────────────────────────────────────────────────
        System.out.println("\n=== Null Reference ===");
        Car nullCar = null;   // no object, just a null reference
        System.out.println("nullCar is null: " + (nullCar == null));
        try {
            nullCar.startEngine();   // NullPointerException!
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: cannot call method on null object");
        }

        // ── ARRAY OF OBJECTS ─────────────────────────────────────────────────
        System.out.println("\n=== Array of Objects ===");
        Car[] fleet = new Car[3];
        fleet[0] = new Car("Ford", "F-150", 2021, 35000.0);
        fleet[1] = new Car("BMW", "3 Series", 2023, 45000.0);
        fleet[2] = new Car("Hyundai", "Elantra", 2022, 20000.0);

        for (Car car : fleet) {
            System.out.println("  " + car.getDescription());
        }

        System.out.println("\nTotal cars ever created: " + Car.totalCarsCreated);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Creating Car Objects ===
 * Cars created: 3
 *
 * === Calling Methods ===
 * 2022 Toyota Camry ($25000.0)
 * Toyota Camry: Vroom! Engine started.
 * Toyota Camry: Engine is already running.
 * Toyota Camry: Engine stopped.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting 'new' when creating objects:
 *    Car c = Car("Toyota", ...); ← COMPILE ERROR, must use 'new'
 *
 * 2. NullPointerException: calling methods on uninitialized object references
 *
 * 3. Confusing class (blueprint) with object (instance):
 *    Car.startEngine(); ← COMPILE ERROR (startEngine is an instance method)
 *    car1.startEngine(); ← correct
 *
 * 4. Shallow copy of objects: Car b = a; — both point to same object!
 *    Changes via b affect a too.
 *
 * 5. Accessing static fields/methods via object reference (legal but confusing):
 *    car1.totalCarsCreated ← works but misleading, use Car.totalCarsCreated
 */
