package advanced;

import java.util.EnumMap;
import java.util.EnumSet;

/*
 * =============================================================================
 * EnumDemo.java — Enumerations (Enums) in Java
 * =============================================================================
 *
 * CONCEPT: Enum
 * --------------
 * An enum (enumeration) is a special class that represents a group of
 * NAMED CONSTANTS. Use enums when a variable should only have a fixed set
 * of possible values.
 *
 * ADVANTAGES OVER using String/int constants:
 *  - Type safety: compiler prevents invalid values
 *  - IDE autocomplete and refactoring support
 *  - Can have methods, fields, and implement interfaces
 *  - Built-in name() and ordinal() methods
 *  - Works well with switch statements
 *
 * KEY FEATURES:
 *  - Enum constants are public static final instances of the enum class
 *  - All enums implicitly extend java.lang.Enum (so can't extend anything else)
 *  - Can implement interfaces
 *  - Can have constructors, fields, abstract methods
 *  - Automatically provides: name(), ordinal(), values(), valueOf()
 *
 * HOW TO RUN:
 *  $ javac -d out src/advanced/EnumDemo.java
 *  $ java -cp out advanced.EnumDemo
 * =============================================================================
 */

// ── BASIC ENUM ────────────────────────────────────────────────────────────────
enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    // Enum methods
    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    public boolean isWeekday() {
        return !isWeekend();
    }
}

// ── ENUM WITH FIELDS AND CONSTRUCTOR ─────────────────────────────────────────
enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6),
    MARS   (6.421e+23, 3.3972e6);

    private final double mass;     // in kilograms
    private final double radius;   // in meters

    // Enum constructor is implicitly private
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    // Fields accessible via getters
    public double getMass()   { return mass; }
    public double getRadius() { return radius; }

    // Method using the fields
    static final double G = 6.67300E-11;
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

// ── ENUM IMPLEMENTING AN INTERFACE ───────────────────────────────────────────
interface Operation {
    double apply(double x, double y);
}

enum MathOp implements Operation {
    PLUS("+")  { public double apply(double x, double y) { return x + y; } },
    MINUS("-") { public double apply(double x, double y) { return x - y; } },
    TIMES("*") { public double apply(double x, double y) { return x * y; } },
    DIVIDE("/") {
        public double apply(double x, double y) {
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x / y;
        }
    };

    private final String symbol;
    MathOp(String symbol) { this.symbol = symbol; }

    @Override public String toString() { return symbol; }
}

// ── ENUM WITH ABSTRACT METHOD ─────────────────────────────────────────────────
enum Season {
    SPRING {
        @Override public String describe() { return "Flowers bloom and weather warms."; }
    },
    SUMMER {
        @Override public String describe() { return "Hot and sunny days."; }
    },
    AUTUMN {
        @Override public String describe() { return "Leaves fall and cool breeze."; }
    },
    WINTER {
        @Override public String describe() { return "Cold and possibly snowy."; }
    };

    public abstract String describe();
}

public class EnumDemo {

    public static void main(String[] args) {

        // ── BASIC ENUM USAGE ──────────────────────────────────────────────────
        System.out.println("=== Basic Enum (Day) ===");

        Day today = Day.WEDNESDAY;
        System.out.println("Today: " + today);                    // WEDNESDAY
        System.out.println("Is weekend: " + today.isWeekend());   // false
        System.out.println("Is weekday: " + today.isWeekday());   // true

        // Enum with switch
        String type = switch (today) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
            case SATURDAY, SUNDAY -> "Weekend";
        };
        System.out.println("Type: " + type);

        // ── ENUM METHODS ──────────────────────────────────────────────────────
        System.out.println("\n=== Enum Built-in Methods ===");

        System.out.println("name():    " + today.name());       // WEDNESDAY
        System.out.println("ordinal(): " + today.ordinal());    // 2 (0-indexed)

        // values(): returns all enum constants as an array
        System.out.println("\nAll days:");
        for (Day d : Day.values()) {
            System.out.printf("  %d. %-10s weekend=%b%n", d.ordinal(), d.name(), d.isWeekend());
        }

        // valueOf(): get enum constant by name
        Day friday = Day.valueOf("FRIDAY");
        System.out.println("\nvalueOf(\"FRIDAY\"): " + friday);

        // ── ENUM WITH FIELDS ──────────────────────────────────────────────────
        System.out.println("\n=== Enum with Fields (Planet) ===");

        double earthWeight = 75.0;   // kg
        double mass = earthWeight / Planet.EARTH.surfaceGravity();

        for (Planet p : Planet.values()) {
            System.out.printf("Weight on %-8s: %6.2f N%n", p, p.surfaceWeight(mass));
        }

        // ── ENUM IMPLEMENTING INTERFACE ───────────────────────────────────────
        System.out.println("\n=== Enum Implementing Interface (MathOp) ===");

        double x = 10.0, y = 3.0;
        for (MathOp op : MathOp.values()) {
            System.out.printf("%.1f %s %.1f = %.2f%n", x, op, y, op.apply(x, y));
        }

        // ── ENUM WITH ABSTRACT METHOD ─────────────────────────────────────────
        System.out.println("\n=== Enum with Abstract Method (Season) ===");
        for (Season season : Season.values()) {
            System.out.println(season + ": " + season.describe());
        }

        // ── ENUMSET AND ENUMMAP ────────────────────────────────────────────────
        System.out.println("\n=== EnumSet ===");

        EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        EnumSet<Day> weekend  = EnumSet.of(Day.SATURDAY, Day.SUNDAY);

        System.out.println("Weekdays: " + weekdays);
        System.out.println("Weekend:  " + weekend);
        System.out.println("WEDNESDAY in weekdays: " + weekdays.contains(Day.WEDNESDAY));

        System.out.println("\n=== EnumMap ===");

        EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
        schedule.put(Day.MONDAY,    "Team standup");
        schedule.put(Day.WEDNESDAY, "Code review");
        schedule.put(Day.FRIDAY,    "Sprint retrospective");
        schedule.put(Day.SATURDAY,  "Personal projects");

        schedule.forEach((day, event) ->
            System.out.printf("  %-10s: %s%n", day, event));
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Basic Enum (Day) ===
 * Today: WEDNESDAY
 * Is weekend: false
 * Type: Weekday
 *
 * === Enum Built-in Methods ===
 * name():    WEDNESDAY
 * ordinal(): 2
 *
 * === Enum with Fields (Planet) ===
 * Weight on MERCURY :  28.33 N
 * Weight on VENUS   :  67.89 N
 * Weight on EARTH   :  75.00 N
 * Weight on MARS    :  28.46 N
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using strings or ints instead of enums for fixed sets of values — lose type safety!
 *
 * 2. Enum can't be extended:
 *    class MyDay extends Day {} ← COMPILE ERROR (enums are final)
 *
 * 3. Comparing enums: use == (not .equals()), since enum constants are singletons:
 *    day == Day.MONDAY ← correct (reference equality, fast)
 *    day.equals(Day.MONDAY) ← also works, but unnecessary
 *
 * 4. Enum ordinal() is fragile for storage — if you add/reorder constants, ordinals change!
 *    Store name() (String) or a dedicated code/id field for database mapping.
 *
 * 5. valueOf() throws IllegalArgumentException if the name doesn't match any constant.
 *    Wrap in try-catch or use a utility method for safe parsing.
 */
