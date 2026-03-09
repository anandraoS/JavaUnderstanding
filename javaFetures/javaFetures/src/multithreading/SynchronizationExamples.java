package multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronization, locks, visibility, and atomic operations.
 */
public class SynchronizationExamples {

    private static class Counter {
        private int value = 0;
        private final AtomicInteger atomic = new AtomicInteger(0);
        private final ReentrantLock lock = new ReentrantLock();

        public void incrementSynchronized() {
            synchronized (this) {
                value++;
            }
        }

        public void incrementWithLock() {
            lock.lock();
            try {
                value++;
            } finally {
                lock.unlock();
            }
        }

        public void incrementAtomic() {
            atomic.incrementAndGet();
        }

        public int getValue() {
            return value;
        }

        public int getAtomicValue() {
            return atomic.get();
        }
    }

    /**
     * Question: Race condition and synchronization.
     */
    public static void demonstrateRaceCondition() throws InterruptedException {
        System.out.println("=== Race Condition and Synchronization ===");

        Counter counter = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementSynchronized();
                counter.incrementAtomic();
            }
        };

        Thread t1 = new Thread(task, "t1");
        Thread t2 = new Thread(task, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Synchronized value: " + counter.getValue());
        System.out.println("Atomic value: " + counter.getAtomicValue());
    }

    /**
     * Question: How to avoid deadlocks with lock ordering?
     */
    public static void demonstrateLockOrdering() throws InterruptedException {
        System.out.println("\n=== Lock Ordering to Avoid Deadlocks ===");

        Object lockA = new Object();
        Object lockB = new Object();

        Thread t1 = new Thread(() -> lockInOrder(lockA, lockB), "ordered-1");
        Thread t2 = new Thread(() -> lockInOrder(lockA, lockB), "ordered-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Both threads completed without deadlock.");
    }

    private static void lockInOrder(Object first, Object second) {
        synchronized (first) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (second) {
                // Safe work
            }
        }
    }

    /**
     * Question: Visibility with volatile.
     */
    public static void demonstrateVolatileVisibility() throws InterruptedException {
        System.out.println("\n=== Volatile Visibility ===");

        class Flag {
            volatile boolean stop = false;
        }

        Flag flag = new Flag();
        Thread worker = new Thread(() -> {
            while (!flag.stop) {
                // busy wait
            }
            System.out.println("Worker observed stop flag.");
        }, "volatile-worker");

        worker.start();
        TimeUnit.MILLISECONDS.sleep(50);
        flag.stop = true;
        worker.join();
    }

    public static void main(String[] args) throws InterruptedException {
        demonstrateRaceCondition();
        demonstrateLockOrdering();
        demonstrateVolatileVisibility();
    }
}

