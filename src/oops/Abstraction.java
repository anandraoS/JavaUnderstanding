package oops;

/*
 * =============================================================================
 * Abstraction.java — Abstract Classes and Abstract Methods
 * =============================================================================
 *
 * CONCEPT: Abstraction
 * ----------------------
 * Abstraction means hiding the implementation details and showing only the
 * essential features/functionality to the user.
 *
 * "What it does" is visible. "How it does it" is hidden.
 *
 * ABSTRACT CLASS:
 *  - Declared with 'abstract' keyword
 *  - Cannot be instantiated (can't do: new AbstractClass())
 *  - Can have BOTH abstract methods (no body) and concrete methods (with body)
 *  - Can have constructors, fields, static methods
 *  - Subclasses MUST implement all abstract methods, or be abstract themselves
 *
 * ABSTRACT METHOD:
 *  - Declared without a body: abstract void methodName();
 *  - Forces subclasses to provide their own implementation
 *  - Can only exist in abstract classes or interfaces
 *
 * ABSTRACT CLASS vs INTERFACE:
 *  - Abstract class: partial implementation, can have state (fields), single inheritance
 *  - Interface: pure contract, no state (constants only), multiple implementation
 *  - Rule of thumb: Abstract class for "is-a" with shared behavior
 *                   Interface for "can-do" capabilities
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Abstraction.java
 *  $ java -cp out oops.Abstraction
 * =============================================================================
 */

// ── ABSTRACT BASE CLASS ───────────────────────────────────────────────────────
abstract class Vehicle {
    // Fields (shared state for all vehicles)
    protected String brand;
    protected String model;
    protected int year;
    protected double fuelLevel;   // 0.0 to 1.0

    Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.fuelLevel = 1.0;   // full tank
    }

    // ── ABSTRACT METHODS — subclasses MUST implement these ───────────────────
    abstract void start();          // different vehicles start differently
    abstract void stop();
    abstract double fuelEfficiency(); // km per litre — different for each type
    abstract String getFuelType();    // petrol, diesel, electric, etc.

    // ── CONCRETE METHODS — shared implementation for all vehicles ─────────────
    void refuel(double amount) {
        fuelLevel = Math.min(1.0, fuelLevel + amount);
        System.out.printf("%s %s: Refuelled. Fuel level: %.0f%%%n", brand, model, fuelLevel * 100);
    }

    double calculateRange() {
        // Uses abstract fuelEfficiency() — resolved at runtime!
        return fuelLevel * 50 * fuelEfficiency();   // 50 litres × km/litre
    }

    void showDetails() {
        System.out.printf("Vehicle: %d %s %s | Fuel: %s | Level: %.0f%% | Range: %.0f km%n",
                year, brand, model, getFuelType(), fuelLevel * 100, calculateRange());
    }
}

// ── CONCRETE SUBCLASS 1: Car ──────────────────────────────────────────────────
class PetrolCar extends Vehicle {
    PetrolCar(String brand, String model, int year) {
        super(brand, model, year);
    }

    @Override
    void start() {
        System.out.println(brand + " " + model + ": Ignition... Vroom!");
    }

    @Override
    void stop() {
        System.out.println(brand + " " + model + ": Engine off.");
    }

    @Override
    double fuelEfficiency() { return 15.0; }   // 15 km/litre

    @Override
    String getFuelType() { return "Petrol"; }
}

// ── CONCRETE SUBCLASS 2: ElectricCar ─────────────────────────────────────────
class ElectricCar extends Vehicle {
    private int batteryCapacityKwh;

    ElectricCar(String brand, String model, int year, int batteryKwh) {
        super(brand, model, year);
        this.batteryCapacityKwh = batteryKwh;
    }

    @Override
    void start() {
        System.out.println(brand + " " + model + ": Silent start... Ready to go ⚡");
    }

    @Override
    void stop() {
        System.out.println(brand + " " + model + ": Powered down. Regenerative braking active.");
    }

    @Override
    double fuelEfficiency() { return 6.0; }   // 6 km/kWh (different unit)

    @Override
    String getFuelType() { return "Electric (" + batteryCapacityKwh + " kWh)"; }
}

// ── CONCRETE SUBCLASS 3: Motorcycle ──────────────────────────────────────────
class Motorcycle extends Vehicle {
    Motorcycle(String brand, String model, int year) {
        super(brand, model, year);
    }

    @Override
    void start() {
        System.out.println(brand + " " + model + ": BRAAAP! Engine roaring!");
    }

    @Override
    void stop() {
        System.out.println(brand + " " + model + ": Bike stopped.");
    }

    @Override
    double fuelEfficiency() { return 25.0; }   // 25 km/litre

    @Override
    String getFuelType() { return "Petrol"; }
}

public class Abstraction {

    public static void main(String[] args) {

        System.out.println("=== Cannot Instantiate Abstract Class ===");
        // Vehicle v = new Vehicle("Generic", "Model", 2024); ← COMPILE ERROR
        System.out.println("(Attempting 'new Vehicle()' would cause a compile error)");

        System.out.println("\n=== Creating Concrete Vehicles ===");
        Vehicle car = new PetrolCar("Toyota", "Corolla", 2022);
        Vehicle tesla = new ElectricCar("Tesla", "Model S", 2024, 100);
        Vehicle bike = new Motorcycle("Yamaha", "MT-07", 2023);

        System.out.println("\n=== Starting Vehicles (polymorphic) ===");
        // Same method call, different behavior based on actual type
        car.start();
        tesla.start();
        bike.start();

        System.out.println("\n=== Vehicle Details ===");
        car.showDetails();     // uses abstract fuelEfficiency() internally
        tesla.showDetails();
        bike.showDetails();

        System.out.println("\n=== Refuelling ===");
        car.fuelLevel = 0.2;   // simulate low fuel
        car.refuel(0.5);
        car.showDetails();

        System.out.println("\n=== Stopping Vehicles ===");
        car.stop();
        tesla.stop();
        bike.stop();

        System.out.println("\n=== Polymorphism with Abstract Class ===");
        Vehicle[] fleet = {car, tesla, bike, new PetrolCar("Ford", "Focus", 2021)};

        double totalRange = 0;
        for (Vehicle v : fleet) {
            totalRange += v.calculateRange();
        }
        System.out.printf("Total fleet range: %.1f km%n", totalRange);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Creating Concrete Vehicles ===
 * (no output from constructors)
 *
 * === Starting Vehicles ===
 * Toyota Corolla: Ignition... Vroom!
 * Tesla Model S: Silent start... Ready to go ⚡
 * Yamaha MT-07: BRAAAP! Engine roaring!
 *
 * === Vehicle Details ===
 * Vehicle: 2022 Toyota Corolla | Fuel: Petrol | Level: 100% | Range: 750 km
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Trying to instantiate abstract class: new Vehicle() → COMPILE ERROR
 *
 * 2. Not implementing ALL abstract methods in a concrete subclass → COMPILE ERROR
 *    All abstract methods must be overridden unless the subclass is also abstract.
 *
 * 3. Calling abstract method in constructor (before subclass initializes) → risky
 *
 * 4. Confusing abstract class with interface:
 *    Abstract class: use when subclasses share common STATE (fields) and behavior
 *    Interface: use for a pure CONTRACT that unrelated classes can implement
 *
 * 5. Making a class abstract without any abstract methods is valid (prevents instantiation),
 *    but unusual. Usually have at least one abstract method.
 */
