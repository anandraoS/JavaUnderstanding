package advanced;

import java.util.ArrayList;
import java.util.List;

/*
 * =============================================================================
 * Generics.java — Generic Classes, Methods, and Wildcards
 * =============================================================================
 *
 * CONCEPT: Generics
 * ------------------
 * Generics allow you to write classes and methods that work with any type,
 * while maintaining TYPE SAFETY at compile time.
 *
 * WITHOUT GENERICS (pre-Java 5):
 *   List list = new ArrayList();
 *   list.add("Hello");
 *   String s = (String) list.get(0);   // need explicit cast, risk of ClassCastException
 *
 * WITH GENERICS:
 *   List<String> list = new ArrayList<>();
 *   list.add("Hello");
 *   String s = list.get(0);  // no cast needed, type-safe!
 *
 * TYPE PARAMETERS (by convention):
 *   T — Type (general)
 *   E — Element (used in collections)
 *   K — Key
 *   V — Value
 *   N — Number
 *   R — Return type
 *   S, U, V — 2nd, 3rd, 4th types
 *
 * BOUNDED TYPE PARAMETERS:
 *   <T extends Number>    — T must be Number or a subclass (upper bound)
 *   <T super Integer>     — T must be Integer or a superclass (lower bound)
 *   <? extends Number>    — wildcard: any type that is Number or subclass
 *   <? super Integer>     — wildcard: any type that is Integer or superclass
 *   <?>                   — unbounded wildcard: any type
 *
 * TYPE ERASURE:
 *   At runtime, all generic type info is ERASED.
 *   List<String> and List<Integer> are both just List at runtime.
 *   This is why you can't do: new T(), new T[10], or instanceof T
 *
 * HOW TO RUN:
 *  $ javac -d out src/advanced/Generics.java
 *  $ java -cp out advanced.Generics
 * =============================================================================
 */

// ── GENERIC CLASS ─────────────────────────────────────────────────────────────
class Box<T> {
    private T content;   // T is replaced with actual type at usage

    Box(T content) {
        this.content = content;
    }

    T getContent() { return content; }
    void setContent(T content) { this.content = content; }

    @Override
    public String toString() {
        return "Box<" + content.getClass().getSimpleName() + ">[" + content + "]";
    }
}

// ── GENERIC CLASS WITH MULTIPLE TYPE PARAMETERS ───────────────────────────────
class GenericPair<K, V> {
    private K first;
    private V second;

    GenericPair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    K getFirst()  { return first; }
    V getSecond() { return second; }

    @Override
    public String toString() { return "(" + first + ", " + second + ")"; }
}

// ── GENERIC CLASS WITH BOUNDED TYPE PARAMETER ─────────────────────────────────
class NumberBox<T extends Number> {  // T must be a Number subtype (Integer, Double, etc.)
    private T value;

    NumberBox(T value) { this.value = value; }

    // Can call Number methods on T (because T extends Number)
    double doubleValue() { return value.doubleValue(); }
    boolean isPositive()  { return value.doubleValue() > 0; }

    @Override
    public String toString() { return "NumberBox[" + value + "]"; }
}

// ── GENERIC STACK ─────────────────────────────────────────────────────────────
class GenericStack<E> {
    private List<E> elements = new ArrayList<>();

    void push(E item) {
        elements.add(item);
    }

    E pop() {
        if (isEmpty()) throw new RuntimeException("Stack is empty!");
        return elements.remove(elements.size() - 1);
    }

    E peek() {
        if (isEmpty()) throw new RuntimeException("Stack is empty!");
        return elements.get(elements.size() - 1);
    }

    boolean isEmpty() { return elements.isEmpty(); }
    int size()        { return elements.size(); }

    @Override
    public String toString() { return elements.toString(); }
}

public class Generics {

    // ── GENERIC METHOD ────────────────────────────────────────────────────────
    // Type parameter <T> declared before the return type
    static <T> void printArray(T[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // Generic method with bounded type (T must implement Comparable)
    static <T extends Comparable<T>> T findMax(T[] arr) {
        T max = arr[0];
        for (T item : arr) {
            if (item.compareTo(max) > 0) max = item;
        }
        return max;
    }

    // Generic swap method
    static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ── WILDCARD METHODS ─────────────────────────────────────────────────────
    // <? extends Number>: accepts List<Integer>, List<Double>, List<Number>, etc.
    static double sumList(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) sum += n.doubleValue();
        return sum;
    }

    // <? super Integer>: accepts List<Integer>, List<Number>, List<Object>
    static void addIntegers(List<? super Integer> list, int count) {
        for (int i = 1; i <= count; i++) list.add(i);
    }

    // Unbounded wildcard: works with any type, but can only call Object methods
    static void printList(List<?> list) {
        for (Object obj : list) System.out.print(obj + " ");
        System.out.println();
    }

    public static void main(String[] args) {

        // ── USING GENERIC BOX ─────────────────────────────────────────────────
        System.out.println("=== Generic Box ===");

        Box<String>  strBox = new Box<>("Hello, Generics!");
        Box<Integer> intBox = new Box<>(42);
        Box<Double>  dblBox = new Box<>(3.14);

        System.out.println(strBox);
        System.out.println(intBox);
        System.out.println(dblBox);

        // Type-safe: no casts needed
        String value = strBox.getContent();   // no cast!
        System.out.println("String value: " + value.toUpperCase());

        // ── PAIR ─────────────────────────────────────────────────────────────
        System.out.println("\n=== Generic Pair ===");

        GenericPair<String, Integer> nameAge  = new GenericPair<>("Alice", 25);
        GenericPair<String, String>  cityPair = new GenericPair<>("New York", "USA");

        System.out.println("Name-Age: " + nameAge);
        System.out.println("City:     " + cityPair);

        // ── BOUNDED TYPE PARAMETER ────────────────────────────────────────────
        System.out.println("\n=== Bounded NumberBox ===");

        NumberBox<Integer> intNumBox = new NumberBox<>(42);
        NumberBox<Double>  dblNumBox = new NumberBox<>(-3.14);

        System.out.println(intNumBox + " → doubleValue: " + intNumBox.doubleValue() + ", positive: " + intNumBox.isPositive());
        System.out.println(dblNumBox + " → doubleValue: " + dblNumBox.doubleValue() + ", positive: " + dblNumBox.isPositive());

        // ── GENERIC STACK ─────────────────────────────────────────────────────
        System.out.println("\n=== Generic Stack ===");

        GenericStack<String> stack = new GenericStack<>();
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        System.out.println("Stack: " + stack);
        System.out.println("Pop: " + stack.pop());
        System.out.println("Peek: " + stack.peek());
        System.out.println("Stack after: " + stack);

        // ── GENERIC METHODS ───────────────────────────────────────────────────
        System.out.println("\n=== Generic Methods ===");

        Integer[] intArr = {3, 1, 4, 1, 5, 9, 2, 6};
        String[]  strArr = {"banana", "apple", "cherry", "date"};
        Double[]  dblArr = {1.1, 2.2, 3.3};

        System.out.print("Int array:    "); printArray(intArr);
        System.out.print("String array: "); printArray(strArr);

        System.out.println("Max int:    " + findMax(intArr));
        System.out.println("Max String: " + findMax(strArr));

        swap(intArr, 0, intArr.length - 1);
        System.out.print("After swap: "); printArray(intArr);

        // ── WILDCARDS ─────────────────────────────────────────────────────────
        System.out.println("\n=== Wildcards ===");

        List<Integer> ints    = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Double>  doubles = new ArrayList<>(List.of(1.1, 2.2, 3.3));
        List<Number>  numbers = new ArrayList<>(List.of(10, 20.5, 30));

        System.out.println("Sum of ints:    " + sumList(ints));
        System.out.println("Sum of doubles: " + sumList(doubles));
        System.out.println("Sum of numbers: " + sumList(numbers));

        System.out.print("All lists: "); printList(ints);
    }
}

/*
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Generic type at runtime is erased:
 *    if (list instanceof List<String>) ← COMPILE ERROR (can't check generic type at runtime)
 *    Use: if (list instanceof List<?>) ← correct
 *
 * 2. Creating generic arrays:
 *    T[] arr = new T[10]; ← COMPILE ERROR due to type erasure
 *    Workaround: T[] arr = (T[]) new Object[10]; ← unchecked cast warning
 *
 * 3. Raw types bypass type safety:
 *    Box box = new Box("Hello");  // raw type — no type checking!
 *    Always specify type parameters.
 *
 * 4. PECS: Producer Extends, Consumer Super
 *    Use <? extends T> when you only READ from the collection (producer)
 *    Use <? super T>   when you only WRITE to the collection (consumer)
 *
 * 5. Static members can't reference class-level type parameters:
 *    class Box<T> { static T item; } ← COMPILE ERROR
 *    Static members are shared by all instances regardless of type.
 */
