package multithreading;

import java.util.*;
import java.util.concurrent.*;

/**
 * ExecutorService, Future, CompletableFuture, ScheduledExecutorService.
 */
public class ExecutorExamples {

    /**
     * Question: Why prefer executors over raw threads?
     */
    public static void demonstrateExecutorService() throws Exception {
        System.out.println("=== ExecutorService ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Callable<Integer> task = () -> {
            TimeUnit.MILLISECONDS.sleep(50);
            return 42;
        };

        Future<Integer> future = pool.submit(task);
        System.out.println("Future result: " + future.get());

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

    /**
     * Question: How to use CompletableFuture for async pipelines?
     */
    public static void demonstrateCompletableFuture() throws Exception {
        System.out.println("\n=== CompletableFuture ===");

        CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> "hello")
                .thenApply(String::toUpperCase)
                .thenCombine(CompletableFuture.completedFuture(" WORLD"), String::concat)
                .exceptionally(ex -> "ERROR");

        System.out.println("CompletableFuture result: " + cf.get());
    }

    /**
     * Question: How does ScheduledExecutorService work?
     */
    public static void demonstrateScheduledExecutor() throws Exception {
        System.out.println("\n=== ScheduledExecutorService ===");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        scheduler.schedule(() -> {
            System.out.println("Scheduled task executed.");
            latch.countDown();
        }, 100, TimeUnit.MILLISECONDS);

        latch.await(1, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    public static void main(String[] args) throws Exception {
        demonstrateExecutorService();
        demonstrateCompletableFuture();
        demonstrateScheduledExecutor();
    }
}

