package multithreading;

import java.util.concurrent.*;

/**
 * Common multithreading pitfalls and safe patterns.
 */
public class ThreadingPitfalls {

    /**
     * Question: Show a race condition and a fix.
     */
    public static void demonstrateRaceCondition() throws InterruptedException {
        System.out.println("=== Race Condition Pitfall ===");

        class Counter {
            int value = 0;
            synchronized void increment() { value++; }
        }

        Counter counter = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task, "rc-1");
        Thread t2 = new Thread(task, "rc-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Counter value (expected 2000): " + counter.value);
    }

    /**
     * Question: How to avoid deadlocks with tryLock and timeouts?
     */
    public static void demonstrateDeadlockAvoidance() throws InterruptedException {
        System.out.println("\n=== Deadlock Avoidance ===");

        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();

        Runnable task = () -> {
            try {
                if (lockA.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        if (lockB.tryLock(50, TimeUnit.MILLISECONDS)) {
                            try {
                                System.out.println(Thread.currentThread().getName() + " acquired both");
                            } finally {
                                lockB.unlock();
                            }
                        } else {
                            System.out.println(Thread.currentThread().getName() + " could not get lockB");
                        }
                    } finally {
                        lockA.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task, "dl-1");
        Thread t2 = new Thread(task, "dl-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    /**
     * Question: Why is ThreadLocal both useful and dangerous?
     */
    public static void demonstrateThreadLocal() throws InterruptedException {
        System.out.println("\n=== ThreadLocal ===");

        ThreadLocal<String> context = new ThreadLocal<>();

        Runnable task = () -> {
            context.set(Thread.currentThread().getName());
            System.out.println("Context: " + context.get());
            context.remove();
        };

        Thread t1 = new Thread(task, "tl-1");
        Thread t2 = new Thread(task, "tl-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public static void main(String[] args) throws Exception {
        demonstrateRaceCondition();
        demonstrateDeadlockAvoidance();
        demonstrateThreadLocal();
    }
}

