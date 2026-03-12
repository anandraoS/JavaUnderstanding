package java8features;

import java.util.Optional;

/*
 * =============================================================================
 * OptionalClass.java — Optional to Handle Null Values Safely
 * =============================================================================
 *
 * CONCEPT: Optional<T>
 * ---------------------
 * Optional is a container object that may or may not contain a non-null value.
 * It was introduced in Java 8 to reduce NullPointerExceptions and make
 * the absence of a value explicit in the API.
 *
 * INSTEAD OF:
 *   String result = findUser(id);
 *   if (result != null) { ... }   ← easy to forget the null check!
 *
 * USE:
 *   Optional<String> result = findUser(id);
 *   result.ifPresent(user -> ...);   ← forces handling of absent value
 *
 * KEY METHODS:
 *   Optional.of(value)       — creates Optional with a non-null value (throws NPE if null!)
 *   Optional.ofNullable(val) — creates Optional, allowing null (becomes empty)
 *   Optional.empty()         — creates empty Optional
 *   isPresent()              — true if value is present
 *   isEmpty()                — true if no value (Java 11+)
 *   get()                    — returns value (throws NoSuchElementException if empty!)
 *   orElse(default)          — returns value or default if empty
 *   orElseGet(Supplier)      — returns value or computed default if empty
 *   orElseThrow(Supplier)    — returns value or throws exception if empty
 *   ifPresent(Consumer)      — runs consumer if value present
 *   ifPresentOrElse(C, R)    — Java 9+: runs consumer or runnable
 *   map(Function)            — transforms value if present
 *   flatMap(Function)        — transforms to Optional if present (avoids nested Optionals)
 *   filter(Predicate)        — returns empty if value doesn't match predicate
 *   stream()                 — Java 9+: converts to Stream (0 or 1 element)
 *
 * HOW TO RUN:
 *  $ javac -d out src/java8features/OptionalClass.java
 *  $ java -cp out java8features.OptionalClass
 * =============================================================================
 */
public class OptionalClass {

    // Simulated database of users
    static String[] users = {"Alice", "Bob", "Charlie", null, "Eve"};

    // Without Optional — returns null if not found (risky!)
    static String findUserUnsafe(int id) {
        if (id < 0 || id >= users.length) return null;
        return users[id];
    }

    // With Optional — explicitly handles absence
    static Optional<String> findUser(int id) {
        if (id < 0 || id >= users.length) return Optional.empty();
        return Optional.ofNullable(users[id]);   // ofNullable handles null entries
    }

    public static void main(String[] args) {

        // ── CREATING OPTIONAL ────────────────────────────────────────────────
        System.out.println("=== Creating Optional ===");

        Optional<String> withValue  = Optional.of("Hello");
        Optional<String> empty      = Optional.empty();
        Optional<String> nullable   = Optional.ofNullable(null);    // empty
        Optional<String> nullable2  = Optional.ofNullable("World"); // with value

        System.out.println("withValue:  " + withValue);
        System.out.println("empty:      " + empty);
        System.out.println("nullable:   " + nullable);
        System.out.println("nullable2:  " + nullable2);

        // ── CHECKING PRESENCE ─────────────────────────────────────────────────
        System.out.println("\n=== Checking Presence ===");

        System.out.println("withValue.isPresent(): " + withValue.isPresent());
        System.out.println("empty.isPresent():     " + empty.isPresent());
        System.out.println("empty.isEmpty():       " + empty.isEmpty());   // Java 11+

        // ── GETTING VALUES ────────────────────────────────────────────────────
        System.out.println("\n=== Getting Values ===");

        // get() - only use when you're sure it's present!
        System.out.println("withValue.get(): " + withValue.get());
        // empty.get(); ← throws NoSuchElementException!

        // orElse: return default if empty
        System.out.println("empty.orElse(\"default\"): " + empty.orElse("default"));
        System.out.println("withValue.orElse(\"X\"):   " + withValue.orElse("X"));

        // orElseGet: compute default lazily (only if empty)
        System.out.println("empty.orElseGet: " + empty.orElseGet(() -> "Computed default"));

        // orElseThrow: throw exception if empty
        try {
            empty.orElseThrow(() -> new RuntimeException("No value found!"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }

        // ── CONDITIONAL EXECUTION ─────────────────────────────────────────────
        System.out.println("\n=== Conditional Execution ===");

        withValue.ifPresent(v -> System.out.println("Present: " + v));
        empty.ifPresent(v -> System.out.println("Never prints"));

        // Java 9+: ifPresentOrElse
        withValue.ifPresentOrElse(
            v -> System.out.println("ifPresentOrElse: " + v),
            () -> System.out.println("Empty!")
        );
        empty.ifPresentOrElse(
            v -> System.out.println("Never"),
            () -> System.out.println("ifPresentOrElse: Empty!")
        );

        // ── TRANSFORMATION ────────────────────────────────────────────────────
        System.out.println("\n=== map() and filter() ===");

        Optional<String> upper = withValue.map(String::toUpperCase);
        System.out.println("map to upper: " + upper.orElse("empty"));

        Optional<Integer> length = withValue.map(String::length);
        System.out.println("map to length: " + length.orElse(0));

        Optional<String> longOnly = withValue.filter(s -> s.length() > 3);
        System.out.println("filter(>3):  " + longOnly.orElse("too short"));

        Optional<String> shortOnly = withValue.filter(s -> s.length() < 3);
        System.out.println("filter(<3):  " + shortOnly.orElse("too long"));

        // ── USING OPTIONAL IN REAL CODE ────────────────────────────────────────
        System.out.println("\n=== Real Usage: findUser() ===");

        int[] ids = {0, 2, 3, 99};
        for (int id : ids) {
            Optional<String> user = findUser(id);
            String result = user
                .map(name -> "Found: " + name.toUpperCase())
                .orElse("User with id=" + id + " not found");
            System.out.println(result);
        }

        // ── CHAINING OPTIONALS ─────────────────────────────────────────────────
        System.out.println("\n=== Chaining Optionals ===");

        Optional<String> result = findUser(0)
            .filter(name -> name.length() > 3)
            .map(name -> "Welcome, " + name + "!");

        result.ifPresentOrElse(
            System.out::println,
            () -> System.out.println("No greeting possible")
        );
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Creating Optional ===
 * withValue:  Optional[Hello]
 * empty:      Optional.empty
 *
 * === Real Usage: findUser() ===
 * Found: ALICE
 * Found: CHARLIE
 * User with id=3 not found (null entry in array)
 * User with id=99 not found
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Calling get() on an empty Optional → NoSuchElementException
 *    Always use orElse/orElseGet/ifPresent/isPresent before get().
 *
 * 2. Using Optional.of(null) → NullPointerException!
 *    Use Optional.ofNullable(null) for values that might be null.
 *
 * 3. Using Optional as a field in a class (it's not Serializable):
 *    class User { Optional<String> email; } ← anti-pattern!
 *    Use null for optional fields, Optional only for return types.
 *
 * 4. Calling isPresent() + get() instead of orElse() or ifPresent():
 *    if (opt.isPresent()) { opt.get(); } ← verbose, just use opt.ifPresent()
 *
 * 5. Using Optional for collections — return empty list/map instead:
 *    Optional<List<T>> ← anti-pattern, return List.empty() instead
 */
