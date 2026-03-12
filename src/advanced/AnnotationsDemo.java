package advanced;

import java.lang.annotation.*;
import java.lang.reflect.*;

/*
 * =============================================================================
 * AnnotationsDemo.java — Built-in and Custom Annotations
 * =============================================================================
 *
 * CONCEPT: Annotations
 * ----------------------
 * Annotations provide METADATA about code. They don't directly affect
 * program logic, but are used by:
 *  - The compiler (for checks and warnings)
 *  - Build tools (Maven, Gradle)
 *  - Frameworks (Spring, JUnit, Hibernate)
 *  - Runtime reflection
 *
 * BUILT-IN ANNOTATIONS:
 *   @Override         — marks method as overriding a parent method (compile-time check)
 *   @Deprecated       — marks element as deprecated (warns at usage)
 *   @SuppressWarnings — suppresses specified compiler warnings
 *   @FunctionalInterface — marks interface as functional (single abstract method)
 *   @SafeVarargs      — suppresses unchecked varargs warnings
 *
 * META-ANNOTATIONS (annotations that annotate other annotations):
 *   @Retention(policy) — how long annotation is kept:
 *       SOURCE:  discarded by compiler
 *       CLASS:   in .class file, not accessible at runtime (default)
 *       RUNTIME: accessible at runtime via reflection
 *   @Target(element) — where annotation can be used:
 *       TYPE, METHOD, FIELD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, etc.
 *   @Documented      — include annotation in Javadoc
 *   @Inherited       — annotation is inherited by subclasses
 *   @Repeatable      — annotation can be applied multiple times
 *
 * HOW TO RUN:
 *  $ javac -d out src/advanced/AnnotationsDemo.java
 *  $ java -cp out advanced.AnnotationsDemo
 * =============================================================================
 */

// ── CUSTOM ANNOTATION DEFINITIONS ────────────────────────────────────────────

// Simple marker annotation (no elements)
@Retention(RetentionPolicy.RUNTIME)   // accessible at runtime
@Target(ElementType.METHOD)           // can only be applied to methods
@interface TestCase {
    // no elements — this is a marker annotation
}

// Annotation with elements
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@interface Info {
    String author();                  // required element
    String version() default "1.0";   // optional element with default
    String description() default "";
    String[] tags() default {};       // array element
}

// Annotation with a single element named 'value' (can omit "value=" when using)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();   // single element named 'value' — can use @MaxLength(50) instead of @MaxLength(value=50)
}

// Repeatable annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Schedules.class)   // requires container annotation
@interface Schedule {
    String dayOfWeek();
    String time() default "09:00";
}

// Container annotation for @Repeatable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Schedules {
    Schedule[] value();
}

// ── ANNOTATED CLASSES ─────────────────────────────────────────────────────────

@Info(
    author = "Java Learner",
    version = "2.0",
    description = "A demo class for annotation processing",
    tags = {"demo", "annotations", "java"}
)
class TaskManager {

    @MaxLength(50)
    private String taskName;

    @MaxLength(200)
    private String description;

    // Built-in @Deprecated — marks this method as obsolete
    @Deprecated
    public void oldMethod() {
        System.out.println("This method is deprecated. Use newMethod() instead.");
    }

    // Built-in @Override — verifies this actually overrides a parent method
    @Override
    public String toString() {
        return "TaskManager{taskName='" + taskName + "'}";
    }

    @TestCase
    public void testSomething() {
        System.out.println("Running test: testSomething");
    }

    @TestCase
    public void testAnotherThing() {
        System.out.println("Running test: testAnotherThing");
    }

    @Schedule(dayOfWeek = "Monday", time = "09:00")
    @Schedule(dayOfWeek = "Thursday", time = "14:00")
    public void weeklyMeeting() {
        System.out.println("Weekly meeting!");
    }

    @SuppressWarnings("unchecked")  // suppress specific warning
    public void suppressedWarning() {
        // This cast would normally generate an "unchecked" warning
        java.util.List list = new java.util.ArrayList();  // raw type
        list.add("item");
    }
}

public class AnnotationsDemo {

    public static void main(String[] args) throws Exception {

        // ── BUILT-IN ANNOTATIONS ──────────────────────────────────────────────
        System.out.println("=== Built-in Annotations ===");
        System.out.println("@Override:         compile-time check for method overriding");
        System.out.println("@Deprecated:       marks element as outdated");
        System.out.println("@SuppressWarnings: suppresses compiler warnings");
        System.out.println("@FunctionalInterface: marks single-abstract-method interface");

        // ── READING CLASS ANNOTATION ──────────────────────────────────────────
        System.out.println("\n=== Reading @Info Annotation ===");

        Class<TaskManager> clazz = TaskManager.class;
        if (clazz.isAnnotationPresent(Info.class)) {
            Info info = clazz.getAnnotation(Info.class);
            System.out.println("Author:      " + info.author());
            System.out.println("Version:     " + info.version());
            System.out.println("Description: " + info.description());
            System.out.print("Tags:        ");
            for (String tag : info.tags()) System.out.print(tag + " ");
            System.out.println();
        }

        // ── READING FIELD ANNOTATION ──────────────────────────────────────────
        System.out.println("\n=== Reading @MaxLength on Fields ===");

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(MaxLength.class)) {
                MaxLength ml = field.getAnnotation(MaxLength.class);
                System.out.println("Field '" + field.getName() + "' max length: " + ml.value());
            }
        }

        // ── FINDING AND RUNNING @TESTCASE METHODS ─────────────────────────────
        System.out.println("\n=== Auto-discovering @TestCase Methods (mini test runner) ===");

        TaskManager manager = new TaskManager();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TestCase.class)) {
                System.out.println("Found @TestCase: " + method.getName());
                method.invoke(manager);   // run the test method via reflection
            }
        }

        // ── REPEATABLE ANNOTATIONS ────────────────────────────────────────────
        System.out.println("\n=== Repeatable @Schedule Annotations ===");

        Method meetingMethod = clazz.getMethod("weeklyMeeting");
        Schedule[] schedules = meetingMethod.getAnnotationsByType(Schedule.class);
        for (Schedule s : schedules) {
            System.out.println("  Meeting on " + s.dayOfWeek() + " at " + s.time());
        }

        // ── LISTING ALL ANNOTATIONS ON A METHOD ───────────────────────────────
        System.out.println("\n=== All Annotations on weeklyMeeting() ===");
        for (Annotation ann : meetingMethod.getAnnotations()) {
            System.out.println("  " + ann.annotationType().getSimpleName());
        }
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === Reading @Info Annotation ===
 * Author:      Java Learner
 * Version:     2.0
 * Description: A demo class for annotation processing
 * Tags:        demo annotations java
 *
 * === Auto-discovering @TestCase Methods ===
 * Found @TestCase: testSomething
 * Running test: testSomething
 * Found @TestCase: testAnotherThing
 * Running test: testAnotherThing
 *
 * === Repeatable @Schedule Annotations ===
 *   Meeting on Monday at 09:00
 *   Meeting on Thursday at 14:00
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting @Retention(RUNTIME) when you need to read annotations at runtime:
 *    Without it, annotations are discarded by default (RetentionPolicy.CLASS).
 *
 * 2. @Target restrictions: applying annotation where not allowed → COMPILE ERROR
 *
 * 3. Annotation elements can only be: primitive types, String, Class, enum, annotation,
 *    or arrays of the above. No List, Object, etc.!
 *
 * 4. Forgetting the container annotation for @Repeatable → COMPILE ERROR
 *
 * 5. Overusing annotations for logic — keep annotations as metadata only.
 *    Business logic should be in regular code.
 */
