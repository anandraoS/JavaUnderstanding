package multithreading;

/**
 * Simple runner to execute all multithreading examples in order.
 */
public class ThreadingRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("MULTITHREADING MODULE - COMPLETE WALKTHROUGH\n");

        ThreadBasics.main(new String[]{});
        SynchronizationExamples.main(new String[]{});
        ExecutorExamples.main(new String[]{});
        ConcurrencyUtilitiesExamples.main(new String[]{});
        ConcurrentCollectionsExamples.main(new String[]{});
        ThreadingPitfalls.main(new String[]{});

        System.out.println("\nMULTITHREADING MODULE COMPLETED");
    }
}

