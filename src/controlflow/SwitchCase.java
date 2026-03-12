package controlflow;

/*
 * =============================================================================
 * SwitchCase.java — switch Statement and switch Expression
 * =============================================================================
 *
 * CONCEPT: switch
 * ---------------
 * switch is used when you want to execute different code based on the VALUE
 * of a single variable. It's cleaner than a long if-else if-else chain when
 * checking many possible exact values.
 *
 * TYPES:
 *  1. Traditional switch STATEMENT (Java 1+) — uses break, has fall-through
 *  2. Switch EXPRESSION (Java 14+)           — uses ->, no fall-through, yields a value
 *
 * SUPPORTED TYPES for switch:
 *    int, Integer, byte, Byte, short, Short, char, Character, String, enum
 *    (NOT: long, float, double, boolean)
 *
 * FALL-THROUGH:
 *    In traditional switch, if you forget 'break', execution "falls through"
 *    to the next case. This is sometimes intentional but usually a bug!
 *
 * HOW TO RUN:
 *  $ javac -d out src/controlflow/SwitchCase.java
 *  $ java -cp out controlflow.SwitchCase
 * =============================================================================
 */
public class SwitchCase {

    public static void main(String[] args) {

        // ── TRADITIONAL SWITCH (int) ─────────────────────────────────────────
        System.out.println("=== Traditional Switch (day of week) ===");
        int day = 3;  // 1=Mon, 2=Tue, 3=Wed, ...

        switch (day) {         // evaluate 'day'
            case 1:
                System.out.println("Monday");
                break;         // IMPORTANT: break exits the switch
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");  // This runs
                break;
            case 4:
                System.out.println("Thursday");
                break;
            case 5:
                System.out.println("Friday");
                break;
            case 6:
            case 7:            // Fall-through intentionally: both 6 and 7 → weekend
                System.out.println("Weekend!");
                break;
            default:           // runs if no case matches — like 'else'
                System.out.println("Invalid day number");
        }

        // ── FALL-THROUGH DEMONSTRATION ───────────────────────────────────────
        System.out.println("\n=== Fall-through (no break) ===");
        int x = 2;

        switch (x) {
            case 1:
                System.out.println("One");
                // no break → falls through to case 2!
            case 2:
                System.out.println("Two");   // runs (x==2)
                // no break → falls through to case 3!
            case 3:
                System.out.println("Three"); // also runs due to fall-through!
                break;
            case 4:
                System.out.println("Four");
        }
        // Output: Two, Three (both print because of fall-through)

        // ── SWITCH WITH STRING ────────────────────────────────────────────────
        System.out.println("\n=== Switch with String ===");
        String season = "Winter";

        switch (season) {
            case "Spring":
                System.out.println("Flowers bloom 🌸");
                break;
            case "Summer":
                System.out.println("Hot and sunny ☀️");
                break;
            case "Autumn":
            case "Fall":
                System.out.println("Leaves fall 🍂");
                break;
            case "Winter":
                System.out.println("Cold and snowy ❄️");  // runs
                break;
            default:
                System.out.println("Unknown season");
        }

        // ── SWITCH EXPRESSION (Java 14+) ─────────────────────────────────────
        // Modern syntax: uses -> (arrow), no break needed, no fall-through,
        // returns a value directly
        System.out.println("\n=== Switch Expression (Java 14+) ===");

        int dayNum = 6;
        String dayName = switch (dayNum) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";   // selected
            case 7 -> "Sunday";
            default -> "Invalid";
        };
        System.out.println("Day " + dayNum + " = " + dayName);

        // ── SWITCH EXPRESSION WITH BLOCK (yield) ─────────────────────────────
        System.out.println("\n=== Switch Expression with Block (yield) ===");
        int month = 2;
        int year = 2024;  // leap year

        int daysInMonth = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;   // multiple labels with comma
            case 4, 6, 9, 11            -> 30;
            case 2 -> {
                // Multi-line block: use 'yield' to return a value
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    yield 29;   // leap year
                } else {
                    yield 28;
                }
            }
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
        System.out.println("Days in month " + month + "/" + year + " = " + daysInMonth);

        // ── SWITCH WITH ENUM ─────────────────────────────────────────────────
        System.out.println("\n=== Switch with Enum ===");
        Direction dir = Direction.NORTH;

        String action = switch (dir) {
            case NORTH -> "Moving north ↑";
            case SOUTH -> "Moving south ↓";
            case EAST  -> "Moving east →";
            case WEST  -> "Moving west ←";
        };   // no default needed when all enum values are covered!
        System.out.println(action);
    }

    // Helper enum for the switch-with-enum demo
    enum Direction { NORTH, SOUTH, EAST, WEST }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Traditional Switch (day of week) ===
 * Wednesday
 *
 * === Fall-through (no break) ===
 * Two
 * Three
 *
 * === Switch with String ===
 * Cold and snowy ❄️
 *
 * === Switch Expression (Java 14+) ===
 * Day 6 = Saturday
 *
 * === Switch Expression with Block (yield) ===
 * Days in month 2/2024 = 29
 *
 * === Switch with Enum ===
 * Moving north ↑
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting 'break' in traditional switch → unintended fall-through
 * 2. Using float/double/long as switch expression → COMPILE ERROR
 * 3. Using == for String in switch → use switch(string) directly, Java handles it
 * 4. Case labels must be constants (not variables):
 *    int x = 5; case x: ← COMPILE ERROR
 * 5. Duplicate case labels → COMPILE ERROR
 * 6. Missing default in switch expression when not all cases are covered → COMPILE ERROR
 */
