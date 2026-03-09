package multithreading;

import java.util.concurrent.*;

/**
 * Concurrency utilities: CountDownLatch, CyclicBarrier, Semaphore, Phaser.
 */
public class ConcurrencyUtilitiesExamples {

    public static void demonstrateCountDownLatch() throws InterruptedException {
        System.out.println("=== CountDownLatch ===");

        CountDownLatch latch = new CountDownLatch(2);

        new Thread(() -> {
            sleep(50);
            System.out.println("Task 1 done");
            latch.countDown();
        }).start();

        new Thread(() -> {
            sleep(80);
            System.out.println("Task 2 done");
            latch.countDown();
        }).start();

        latch.await();
        System.out.println("All tasks done");
    }

    public static void demonstrateCyclicBarrier() throws InterruptedException, BrokenBarrierException {
        System.out.println("\n=== CyclicBarrier ===");

        CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("Barrier opened"));

        Runnable worker = () -> {
            sleep(50);
            System.out.println(Thread.currentThread().getName() + " reached barrier");
            try {
                barrier.await();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(worker, "worker-1");
        Thread t2 = new Thread(worker, "worker-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public static void demonstrateSemaphore() throws InterruptedException {
        System.out.println("\n=== Semaphore ===");

        Semaphore semaphore = new Semaphore(2);
        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " acquired");
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release();
                System.out.println(Thread.currentThread().getName() + " released");
            }
        };

        Thread t1 = new Thread(task, "sem-1");
        Thread t2 = new Thread(task, "sem-2");
        Thread t3 = new Thread(task, "sem-3");
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }

    public static void demonstratePhaser() throws InterruptedException {
        System.out.println("\n=== Phaser ===");

        Phaser phaser = new Phaser(2);

        Runnable phaseTask = () -> {
            System.out.println(Thread.currentThread().getName() + " phase 1");
            phaser.arriveAndAwaitAdvance();
            System.out.println(Thread.currentThread().getName() + " phase 2");
            phaser.arriveAndAwaitAdvance();
        };

        Thread t1 = new Thread(phaseTask, "phase-1");
        Thread t2 = new Thread(phaseTask, "phase-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        demonstrateCountDownLatch();
        demonstrateCyclicBarrier();
        demonstrateSemaphore();
        demonstratePhaser();
    }
}

