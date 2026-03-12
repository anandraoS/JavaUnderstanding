package oops;

/*
 * =============================================================================
 * Polymorphism.java — Compile-Time and Runtime Polymorphism
 * =============================================================================
 *
 * CONCEPT: Polymorphism
 * ----------------------
 * Polymorphism means "many forms". In Java, the same method name can behave
 * differently depending on context or the actual object type.
 *
 * TWO TYPES:
 *
 * 1. COMPILE-TIME (Static) Polymorphism — METHOD OVERLOADING
 *    - Multiple methods with the SAME NAME but DIFFERENT PARAMETERS
 *    - Resolved at compile time
 *    - Also called "static dispatch" or "early binding"
 *
 * 2. RUNTIME (Dynamic) Polymorphism — METHOD OVERRIDING
 *    - Child class provides a SPECIFIC IMPLEMENTATION of a parent method
 *    - Resolved at RUNTIME based on the actual object type
 *    - Also called "dynamic dispatch" or "late binding"
 *    - Requires inheritance (extends or implements)
 *
 * HOW TO RUN:
 *  $ javac -d out src/oops/Polymorphism.java
 *  $ java -cp out oops.Polymorphism
 * =============================================================================
 */

// ── COMPILE-TIME POLYMORPHISM: Calculator with overloaded methods ─────────────
class Calculator {

    // Same method name 'add', different parameter types/counts — overloading
    int add(int a, int b) {
        System.out.println("add(int, int) called");
        return a + b;
    }

    int add(int a, int b, int c) {         // different number of parameters
        System.out.println("add(int, int, int) called");
        return a + b + c;
    }

    double add(double a, double b) {       // different parameter types
        System.out.println("add(double, double) called");
        return a + b;
    }

    String add(String a, String b) {       // different parameter types
        System.out.println("add(String, String) called");
        return a + b;
    }

    // NOTE: return type alone does NOT differentiate overloads
    // double add(int a, int b) {} ← COMPILE ERROR: same params as add(int, int)
}

// ── RUNTIME POLYMORPHISM: Shape hierarchy ────────────────────────────────────
class Shape {
    String color;

    Shape(String color) {
        this.color = color;
    }

    double area() {
        return 0;   // base implementation — subclasses will override
    }

    double perimeter() {
        return 0;
    }

    void describe() {
        System.out.printf("Shape: %s | Area: %.2f | Perimeter: %.2f%n",
                getClass().getSimpleName(), area(), perimeter());
    }
}

class Circle extends Shape {
    double radius;

    Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;   // π × r²
    }

    @Override
    double perimeter() {
        return 2 * Math.PI * radius;   // 2πr
    }
}

class RectangleShape extends Shape {
    double width, height;

    RectangleShape(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }

    @Override
    double perimeter() {
        return 2 * (width + height);
    }
}

class Triangle extends Shape {
    double a, b, c;   // three sides

    Triangle(String color, double a, double b, double c) {
        super(color);
        this.a = a; this.b = b; this.c = c;
    }

    @Override
    double area() {
        double s = (a + b + c) / 2;   // semi-perimeter (Heron's formula)
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    double perimeter() {
        return a + b + c;
    }
}

public class Polymorphism {

    public static void main(String[] args) {

        // ── COMPILE-TIME POLYMORPHISM ─────────────────────────────────────────
        System.out.println("=== Compile-Time Polymorphism (Method Overloading) ===");
        Calculator calc = new Calculator();

        System.out.println("add(2, 3)        = " + calc.add(2, 3));           // int version
        System.out.println("add(1, 2, 3)     = " + calc.add(1, 2, 3));        // 3-arg version
        System.out.println("add(1.5, 2.5)    = " + calc.add(1.5, 2.5));       // double version
        System.out.println("add(\"Hi\", \"!\") = " + calc.add("Hi", "!"));    // String version

        // Java automatically calls the best-matching overload based on argument types

        // ── RUNTIME POLYMORPHISM ──────────────────────────────────────────────
        System.out.println("\n=== Runtime Polymorphism (Method Overriding) ===");

        // Array of Shape references — each holds different actual objects
        Shape[] shapes = {
            new Circle("Red", 5.0),
            new RectangleShape("Blue", 4.0, 6.0),
            new Triangle("Green", 3.0, 4.0, 5.0),
            new Circle("Yellow", 2.5)
        };

        // Same method call (describe) → different behavior for each shape type
        // The JVM decides WHICH version to run at RUNTIME based on actual type
        for (Shape s : shapes) {
            s.describe();   // polymorphic call
        }

        // ── CALCULATING TOTAL AREA ────────────────────────────────────────────
        System.out.println("\n=== Total Area of All Shapes ===");
        double totalArea = 0;
        for (Shape s : shapes) {
            totalArea += s.area();   // each calls its own area() implementation
        }
        System.out.printf("Total Area: %.2f%n", totalArea);

        // ── POLYMORPHIC VARIABLE ──────────────────────────────────────────────
        System.out.println("\n=== Polymorphic Reference ===");

        Shape s1 = new Circle("Purple", 3.0);    // Shape reference, Circle object
        System.out.println("Declared type: Shape");
        System.out.println("Actual type:   " + s1.getClass().getSimpleName());
        System.out.printf("Area: %.2f%n", s1.area());   // Circle's area() is called!

        // Downcasting to access Circle-specific methods
        if (s1 instanceof Circle circle) {
            System.out.println("Radius: " + circle.radius);   // Circle-specific field
        }

        // ── UPCASTING AND DOWNCASTING ─────────────────────────────────────────
        System.out.println("\n=== Upcasting and Downcasting ===");

        Circle c = new Circle("Orange", 7.0);
        Shape s = c;              // UPCASTING (implicit): Circle → Shape (always safe)
        System.out.println("Upcast: " + s.area());

        Circle c2 = (Circle) s;   // DOWNCASTING (explicit): Shape → Circle (safe here)
        System.out.println("Downcast radius: " + c2.radius);

        // Unsafe downcast example:
        Shape rect = new RectangleShape("Black", 3, 3);
        try {
            Circle wrongCast = (Circle) rect;   // ClassCastException!
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Compile-Time Polymorphism (Method Overloading) ===
 * add(int, int) called
 * add(2, 3)        = 5
 * ...
 * === Runtime Polymorphism (Method Overriding) ===
 * Shape: Circle | Area: 78.54 | Perimeter: 31.42
 * Shape: RectangleShape | Area: 24.00 | Perimeter: 20.00
 * ...
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Overloading vs Overriding confusion:
 *    Overloading: same class, same name, different params (compile-time)
 *    Overriding:  child class, same name, same params (runtime)
 *
 * 2. Only return type differs → NOT an overload (compile error):
 *    int add(int a, int b) { return a+b; }
 *    double add(int a, int b) { return a+b; } ← COMPILE ERROR
 *
 * 3. Trying to call child-specific methods via parent reference without casting:
 *    Shape s = new Circle(...);
 *    s.radius ← COMPILE ERROR (Shape doesn't have radius field)
 *
 * 4. Unsafe downcast without instanceof check → ClassCastException at runtime
 *
 * 5. static methods are NOT polymorphic — they use the declared type (compile-time)!
 *    Called "method hiding", not overriding.
 */
