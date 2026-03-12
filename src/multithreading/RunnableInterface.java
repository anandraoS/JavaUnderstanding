package multithreading;

/*
 * =============================================================================
 * RunnableInterface.java — Runnable, Callable, and Thread Pools
 * =============================================================================
 *
 * CONCEPT: Runnable Interface
 * ----------------------------
 * Runnable is a functional interface with a single method: void run()
 * It's the preferred way to define thread tasks because:
 *  - It separates the task from the thread (cleaner design)
 *  - A class can implement Runnable AND extend another class (unlike Thread)
 *  - Can be used with lambda expressions (Java 8+)
 *  - Works with thread pools (ExecutorService)
 *
 * CALLABLE vs RUNNABLE:
 *  Runnable: void run()         → no return value, can't throw checked exceptions
 *  Callable: V call() throws E → returns a value, can throw exceptions
 *
 * EXECUTORSERVICE (Thread Pool):
 *  Instead of creating threads manually, use a thread pool:
 *  - Reuses threads (creating threads is expensive)
 *  - Controls the number of concurrent threads
 *  - Manages thread lifecycle automatically
 *
 * HOW TO RUN:
 *  $ javac -d out src/multithreading/RunnableInterface.java
 *  $ java -cp out multithreading.RunnableInterface
 * =============================================================================
 */

import java.util.concurrent.*;

// ── IMPLEMENTING RUNNABLE ─────────────────────────────────────────────────────
class PrintTask implements Runnable {
    private String taskName;
    private int count;

    PrintTask(String taskName, int count) {
        this.taskName = taskName;
        this.count = count;
    }

    @Override
    public void run() {      // return type is void — no return value
        for (int i = 1; i <= count; i++) {
            System.out.println(taskName + " step " + i + " [Thread: " + Thread.currentThread().getName() + "]");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

// ── IMPLEMENTING CALLABLE (returns a value) ───────────────────────────────────
class SumCallable implements Callable<Long> {
    private int from;
    private int to;

    SumCallable(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Long call() throws Exception {   // CAN return a value and throw exceptions
        long sum = 0;
        for (int i = from; i <= to; i++) {
            sum += i;
        }
        System.out.println("Computed sum(" + from + " to " + to + ") = " + sum
                + " [Thread: " + Thread.currentThread().getName() + "]");
        return sum;
    }
}

public class RunnableInterface {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // ── RUNNABLE WITH Thread ──────────────────────────────────────────────
        System.out.println("=== Runnable with Thread ===");

        Runnable task1 = new PrintTask("Alpha", 3);
        Runnable task2 = new PrintTask("Beta", 3);

        Thread t1 = new Thread(task1, "Worker-1");
        Thread t2 = new Thread(task2, "Worker-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // ── RUNNABLE WITH LAMBDA ──────────────────────────────────────────────
        System.out.println("\n=== Runnable with Lambda (Java 8+) ===");

        Runnable lambdaTask = () -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Lambda task: " + i + " [" + Thread.currentThread().getName() + "]");
            }
        };

        Thread lambdaThread = new Thread(lambdaTask, "LambdaThread");
        lambdaThread.start();
        lambdaThread.join();

        // ── EXECUTORSERVICE (Fixed Thread Pool) ───────────────────────────────
        System.out.println("\n=== ExecutorService: Fixed Thread Pool ===");

        // Creates a pool of 3 threads to handle tasks
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit multiple tasks — pool reuses threads
        for (int i = 1; i <= 5; i++) {
            final int taskNum = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNum + " running on [" + Thread.currentThread().getName() + "]");
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        // Must shutdown after submitting all tasks
        executor.shutdown();                         // no new tasks accepted
        executor.awaitTermination(5, TimeUnit.SECONDS);  // wait for completion
        System.out.println("All pool tasks completed.");

        // ── CALLABLE AND FUTURE ───────────────────────────────────────────────
        System.out.println("\n=== Callable and Future ===");

        ExecutorService callablePool = Executors.newFixedThreadPool(2);

        // Submit Callable tasks — returns a Future to retrieve results
        Future<Long> future1 = callablePool.submit(new SumCallable(1, 50));
        Future<Long> future2 = callablePool.submit(new SumCallable(51, 100));

        // future.get() BLOCKS until the result is ready
        long sum1 = future1.get();   // waits for task 1 to complete
        long sum2 = future2.get();   // waits for task 2 to complete

        System.out.println("Sum(1-50)  = " + sum1);
        System.out.println("Sum(51-100)= " + sum2);
        System.out.println("Total sum  = " + (sum1 + sum2));  // should be 5050

        callablePool.shutdown();

        // ── SINGLE THREAD EXECUTOR ────────────────────────────────────────────
        System.out.println("\n=== Single Thread Executor ===");

        ExecutorService singleExec = Executors.newSingleThreadExecutor();
        for (int i = 1; i <= 4; i++) {
            final int n = i;
            singleExec.submit(() -> System.out.println("Sequential task: " + n));
        }
        singleExec.shutdown();
        singleExec.awaitTermination(3, TimeUnit.SECONDS);
    }
}

/*
 * EXPECTED OUTPUT (abbreviated, order varies):
 * ─────────────────────────────────────────────
 * === Runnable with Thread ===
 * Alpha step 1 [Thread: Worker-1]
 * Beta step 1 [Thread: Worker-2]
 * ...
 *
 * === ExecutorService: Fixed Thread Pool ===
 * Task 1 running on [pool-1-thread-1]
 * Task 2 running on [pool-1-thread-2]
 * Task 3 running on [pool-1-thread-3]
 * Task 4 running on [pool-1-thread-1]
 * Task 5 running on [pool-1-thread-2]
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Forgetting to call executor.shutdown() → JVM won't exit! Thread pool keeps running.
 *
 * 2. Calling future.get() without handling InterruptedException and ExecutionException
 *
 * 3. Catching InterruptedException without restoring interrupted status:
 *    Thread.currentThread().interrupt(); // restore the flag!
 *
 * 4. Creating too many threads manually — use thread pools (ExecutorService) instead.
 *
 * 5. Blocking the main thread with future.get() before submitting all tasks:
 *    Submit all tasks first, then call get() on all futures for parallel execution.
 */
