package multithreading;

/*
 * =============================================================================
 * ThreadBasics.java — Creating and Managing Threads
 * =============================================================================
 *
 * CONCEPT: Threads
 * -----------------
 * A thread is the smallest unit of execution within a process.
 * Multithreading allows multiple threads to run concurrently, enabling:
 *  - Parallel computation
 *  - Responsive UIs (background work while UI remains active)
 *  - Efficient use of multi-core processors
 *
 * THREAD LIFECYCLE:
 *   NEW → RUNNABLE → RUNNING → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED
 *
 * TWO WAYS TO CREATE THREADS:
 *  1. Extend the Thread class (less flexible — single inheritance)
 *  2. Implement Runnable interface (preferred — more flexible)
 *
 * THREAD STATES:
 *  NEW:          Thread created but not started
 *  RUNNABLE:     Thread started, waiting for CPU time
 *  RUNNING:      Thread currently executing
 *  BLOCKED:      Waiting for a synchronized lock
 *  WAITING:      Waiting indefinitely for another thread
 *  TIMED_WAITING: Waiting for a specific time (sleep, wait with timeout)
 *  TERMINATED:   Thread has finished executing
 *
 * HOW TO RUN:
 *  $ javac -d out src/multithreading/ThreadBasics.java
 *  $ java -cp out multithreading.ThreadBasics
 * =============================================================================
 */

// ── METHOD 1: Extend Thread class ────────────────────────────────────────────
class CounterThread extends Thread {
    private String threadName;
    private int count;

    CounterThread(String name, int count) {
        super(name);           // set the thread's name
        this.threadName = name;
        this.count = count;
    }

    @Override
    public void run() {      // run() contains the code that the thread executes
        System.out.println(threadName + " started (id=" + Thread.currentThread().getId() + ")");
        for (int i = 1; i <= count; i++) {
            System.out.println(threadName + ": " + i);
            try {
                Thread.sleep(100);   // pause for 100ms (allows other threads to run)
            } catch (InterruptedException e) {
                System.out.println(threadName + " interrupted!");
                Thread.currentThread().interrupt();   // restore interrupted status
                return;
            }
        }
        System.out.println(threadName + " finished.");
    }
}

public class ThreadBasics {

    public static void main(String[] args) throws InterruptedException {

        // ── MAIN THREAD INFO ──────────────────────────────────────────────────
        System.out.println("=== Main Thread Info ===");
        Thread mainThread = Thread.currentThread();
        System.out.println("Thread name: " + mainThread.getName());
        System.out.println("Thread id:   " + mainThread.getId());
        System.out.println("Priority:    " + mainThread.getPriority());  // 1-10, default 5
        System.out.println("Is daemon:   " + mainThread.isDaemon());     // main is not a daemon

        // ── CREATING AND STARTING THREADS ─────────────────────────────────────
        System.out.println("\n=== Creating and Starting Threads ===");

        CounterThread t1 = new CounterThread("Thread-A", 3);
        CounterThread t2 = new CounterThread("Thread-B", 3);

        // start() creates a new thread and calls run() in it
        // DO NOT call run() directly — that runs in the current thread, not a new one!
        t1.start();
        t2.start();

        // join() makes the main thread wait for t1 and t2 to finish
        t1.join();
        t2.join();

        System.out.println("Both threads finished. Main thread continues.");

        // ── THREAD SLEEP ──────────────────────────────────────────────────────
        System.out.println("\n=== Thread Sleep ===");
        System.out.println("Sleeping for 500ms...");
        Thread.sleep(500);
        System.out.println("Awake!");

        // ── THREAD PRIORITY ───────────────────────────────────────────────────
        System.out.println("\n=== Thread Priority ===");

        Thread highPriority = new Thread(() -> {
            System.out.println("High priority thread running");
        });
        Thread lowPriority = new Thread(() -> {
            System.out.println("Low priority thread running");
        });

        highPriority.setPriority(Thread.MAX_PRIORITY);  // 10
        lowPriority.setPriority(Thread.MIN_PRIORITY);   // 1
        // Note: priority is a hint to the OS scheduler, not a guarantee!

        lowPriority.start();
        highPriority.start();
        lowPriority.join();
        highPriority.join();

        // ── DAEMON THREADS ────────────────────────────────────────────────────
        System.out.println("\n=== Daemon Thread ===");

        Thread daemonThread = new Thread(() -> {
            int i = 0;
            while (true) {
                System.out.println("Daemon thread running: " + i++);
                try { Thread.sleep(200); } catch (InterruptedException e) { break; }
                if (i >= 3) break;   // limit for demo
            }
        });

        daemonThread.setDaemon(true);   // mark as daemon BEFORE starting
        // Daemon threads are background threads that die when all non-daemon threads finish
        // JVM exits when only daemon threads remain
        daemonThread.start();
        daemonThread.join();

        System.out.println("Daemon thread demo done.");

        // ── THREAD STATE ──────────────────────────────────────────────────────
        System.out.println("\n=== Thread States ===");

        Thread stateThread = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        });

        System.out.println("Before start: " + stateThread.getState());   // NEW
        stateThread.start();
        System.out.println("After start:  " + stateThread.getState());   // RUNNABLE or TIMED_WAITING
        stateThread.join();
        System.out.println("After join:   " + stateThread.getState());   // TERMINATED
    }
}

/*
 * EXPECTED OUTPUT (order may vary — threads run concurrently):
 * ─────────────────────────────────────────────────────────────
 * === Main Thread Info ===
 * Thread name: main
 * Thread id:   1
 * Priority:    5
 * Is daemon:   false
 *
 * === Creating and Starting Threads ===
 * Thread-A started (id=...)
 * Thread-B started (id=...)
 * Thread-A: 1
 * Thread-B: 1
 * Thread-A: 2
 * Thread-B: 2
 * Thread-A: 3
 * Thread-B: 3
 * Thread-A finished.
 * Thread-B finished.
 * Both threads finished. Main thread continues.
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Calling run() instead of start():
 *    thread.run() → executes in the CURRENT thread (no new thread created!)
 *    thread.start() → creates a NEW thread and calls run() in it
 *
 * 2. Starting a thread more than once → IllegalThreadStateException
 *
 * 3. Not handling InterruptedException properly:
 *    Catch it and either re-interrupt: Thread.currentThread().interrupt();
 *    or propagate it up.
 *
 * 4. Not joining threads before accessing their results:
 *    Accessing shared data before join() may give incomplete results.
 *
 * 5. Race conditions: multiple threads modifying shared data without synchronization
 *    → unpredictable results. See SynchronizationDemo.java.
 */
