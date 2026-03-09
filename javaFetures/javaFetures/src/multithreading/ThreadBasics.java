package multithreading;

import java.util.concurrent.TimeUnit;

/**
 * Thread basics for senior interviews.
 * Focus: lifecycle, interrupt, join, naming, priorities.
 */
public class ThreadBasics {

    /**
     * Question: What are the thread lifecycle states and how do you observe them?
     */
    public static void demonstrateThreadLifecycle() throws InterruptedException {
        System.out.println("=== Thread Lifecycle ===");

        Thread worker = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "worker-1");

        System.out.println("State before start: " + worker.getState());
        worker.start();
        System.out.println("State after start: " + worker.getState());
        worker.join();
        System.out.println("State after join: " + worker.getState());
    }

    /**
     * Question: How does interrupt work and how to handle it correctly?
     */
    public static void demonstrateInterrupt() throws InterruptedException {
        System.out.println("\n=== Interrupt ===");

        Thread sleeper = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while sleeping, restoring flag.");
                Thread.currentThread().interrupt();
            }
        }, "sleeper");

        sleeper.start();
        TimeUnit.MILLISECONDS.sleep(50);
        sleeper.interrupt();
        sleeper.join();

        System.out.println("Interrupted flag after join: " + sleeper.isInterrupted());
    }

    /**
     * Question: Runnable vs Thread - what is preferred?
     */
    public static void demonstrateRunnable() throws InterruptedException {
        System.out.println("\n=== Runnable vs Thread ===");

        Runnable task = () -> System.out.println("Runnable executed by: " + Thread.currentThread().getName());
        Thread thread = new Thread(task, "runnable-thread");
        thread.start();
        thread.join();
    }

    public static void main(String[] args) throws InterruptedException {
        demonstrateThreadLifecycle();
        demonstrateInterrupt();
        demonstrateRunnable();
    }
}

