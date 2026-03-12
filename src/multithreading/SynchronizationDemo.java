package multithreading;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * =============================================================================
 * SynchronizationDemo.java — Synchronized Keyword, Locks, and Atomic Variables
 * =============================================================================
 *
 * CONCEPT: Synchronization
 * -------------------------
 * When multiple threads access SHARED MUTABLE DATA, synchronization prevents
 * RACE CONDITIONS — unpredictable results from concurrent modifications.
 *
 * RACE CONDITION EXAMPLE:
 *   Thread 1: reads count=5, adds 1, writes count=6
 *   Thread 2: reads count=5 (before Thread 1 writes!), adds 1, writes count=6
 *   Expected: count=7   Actual: count=6  ← RACE CONDITION!
 *
 * SOLUTIONS:
 *  1. synchronized keyword     — built-in Java mechanism
 *  2. java.util.concurrent.locks.Lock  — more flexible, explicit locking
 *  3. java.util.concurrent.atomic.*    — lock-free thread-safe operations
 *  4. volatile keyword         — ensures visibility of changes across threads
 *
 * HOW TO RUN:
 *  $ javac -d out src/multithreading/SynchronizationDemo.java
 *  $ java -cp out multithreading.SynchronizationDemo
 * =============================================================================
 */

// ── UNSAFE COUNTER (no synchronization) ──────────────────────────────────────
class UnsafeCounter {
    int count = 0;

    void increment() {
        count++;   // NOT atomic! read + increment + write = 3 separate ops
    }
}

// ── SAFE COUNTER with synchronized method ────────────────────────────────────
class SynchronizedCounter {
    private int count = 0;

    // 'synchronized' ensures only ONE thread executes this method at a time
    // Other threads trying to enter must WAIT (acquire the object's lock)
    synchronized void increment() {
        count++;   // now atomic with respect to this object
    }

    synchronized int getCount() {
        return count;   // reading shared state also needs synchronization
    }
}

// ── SAFE COUNTER with synchronized block ─────────────────────────────────────
class BlockSynchronizedCounter {
    private int count = 0;
    private final Object lock = new Object();   // explicit lock object

    void increment() {
        // synchronized block: only synchronize the critical section
        // (allows other methods to run concurrently if they don't need the lock)
        synchronized (lock) {
            count++;
        }
    }

    int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}

// ── SAFE COUNTER with ReentrantLock ──────────────────────────────────────────
class LockBasedCounter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();

    void increment() {
        lock.lock();          // acquire lock (blocks if another thread holds it)
        try {
            count++;
        } finally {
            lock.unlock();    // ALWAYS release in finally to prevent deadlocks!
        }
    }

    int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

// ── SAFE COUNTER with AtomicInteger ──────────────────────────────────────────
class AtomicCounter {
    // AtomicInteger provides lock-free, thread-safe integer operations
    private AtomicInteger count = new AtomicInteger(0);

    void increment() {
        count.incrementAndGet();   // atomic operation — no synchronized needed
    }

    int getCount() {
        return count.get();
    }
}

public class SynchronizationDemo {

    static final int THREAD_COUNT = 10;
    static final int INCREMENTS_PER_THREAD = 1000;
    static final int EXPECTED = THREAD_COUNT * INCREMENTS_PER_THREAD;   // 10000

    public static void main(String[] args) throws InterruptedException {

        // ── DEMONSTRATE RACE CONDITION ────────────────────────────────────────
        System.out.println("=== Race Condition (Unsafe Counter) ===");
        System.out.println("Expected count: " + EXPECTED);

        UnsafeCounter unsafe = new UnsafeCounter();
        Thread[] unsafeThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) unsafe.increment();
            });
            unsafeThreads[i].start();
        }
        for (Thread t : unsafeThreads) t.join();
        System.out.println("Unsafe count:   " + unsafe.count + " (likely < " + EXPECTED + " due to race!)");

        // ── SYNCHRONIZED METHOD ───────────────────────────────────────────────
        System.out.println("\n=== Synchronized Method Counter ===");

        SynchronizedCounter synced = new SynchronizedCounter();
        Thread[] syncThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            syncThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) synced.increment();
            });
            syncThreads[i].start();
        }
        for (Thread t : syncThreads) t.join();
        System.out.println("Synced count: " + synced.getCount() + " (always " + EXPECTED + ")");

        // ── LOCK-BASED COUNTER ────────────────────────────────────────────────
        System.out.println("\n=== Lock-Based Counter ===");

        LockBasedCounter locked = new LockBasedCounter();
        Thread[] lockThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            lockThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) locked.increment();
            });
            lockThreads[i].start();
        }
        for (Thread t : lockThreads) t.join();
        System.out.println("Lock count: " + locked.getCount());

        // ── ATOMIC COUNTER ────────────────────────────────────────────────────
        System.out.println("\n=== AtomicInteger Counter ===");

        AtomicCounter atomic = new AtomicCounter();
        Thread[] atomicThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) atomic.increment();
            });
            atomicThreads[i].start();
        }
        for (Thread t : atomicThreads) t.join();
        System.out.println("Atomic count: " + atomic.getCount());

        System.out.println("\n=== Summary ===");
        System.out.println("All safe counters should show: " + EXPECTED);
    }
}

/*
 * EXPECTED OUTPUT:
 * ─────────────────
 * === Race Condition (Unsafe Counter) ===
 * Expected count: 10000
 * Unsafe count:   9673  (or similar — varies, less than 10000!)
 *
 * === Synchronized Method Counter ===
 * Synced count: 10000 (always correct)
 *
 * === Lock-Based Counter ===
 * Lock count: 10000
 *
 * === AtomicInteger Counter ===
 * Atomic count: 10000
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Synchronizing on 'this' (implicit in synchronized methods) vs explicit lock objects:
 *    synchronized(this) — others with reference to this object can also lock it!
 *    Use a private final Object lock = new Object(); for a private lock.
 *
 * 2. Not releasing Lock in finally → DEADLOCK if exception occurs between lock and unlock
 *
 * 3. Over-synchronizing: making everything synchronized slows down performance.
 *    Synchronize only the critical section (minimal scope).
 *
 * 4. Using volatile without understanding: volatile ensures VISIBILITY but NOT ATOMICITY.
 *    volatile int count; count++; ← still a race condition!
 *    Use AtomicInteger for atomic operations.
 *
 * 5. Deadlock: Thread A holds Lock 1, waits for Lock 2.
 *              Thread B holds Lock 2, waits for Lock 1. → Both wait forever!
 *    Fix: always acquire locks in the same order across all threads.
 */
