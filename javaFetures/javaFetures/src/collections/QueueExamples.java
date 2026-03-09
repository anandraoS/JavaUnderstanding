package collections;

import java.util.*;
import java.util.concurrent.*;

/**
 * Advanced Queue and Deque interface concepts
 * Topics: PriorityQueue, ArrayDeque, LinkedList, BlockingQueue implementations
 * Producer-Consumer patterns, Priority scheduling, Thread-safe queues
 */
public class QueueExamples {

    /**
     * Question 1: Explain Queue interface methods: add/offer, remove/poll, element/peek
     * What are the differences?
     */
    public static void demonstrateQueueMethods() {
        System.out.println("=== Queue Methods ===");

        Queue<String> queue = new LinkedList<>();

        // add vs offer
        queue.add("A"); // Throws exception if capacity restricted
        queue.offer("B"); // Returns false if capacity restricted

        System.out.println("Queue: " + queue);

        // element vs peek
        System.out.println("element(): " + queue.element()); // Throws if empty
        System.out.println("peek(): " + queue.peek()); // Returns null if empty

        // remove vs poll
        System.out.println("remove(): " + queue.remove()); // Throws if empty
        System.out.println("poll(): " + queue.poll()); // Returns null if empty

        System.out.println("\nPrefer 'offer', 'poll', 'peek' for graceful handling");
        System.out.println("Use 'add', 'remove', 'element' when failure is exceptional");
    }

    /**
     * Question 2: How does PriorityQueue work? What's the underlying data structure?
     */
    public static void demonstratePriorityQueue() {
        System.out.println("\n=== PriorityQueue ===");

        // Natural ordering (min-heap by default)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.addAll(Arrays.asList(5, 2, 8, 1, 9, 3));

        System.out.println("Min-heap poll order:");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " "); // 1, 2, 3, 5, 8, 9
        }

        // Max-heap using reverse order
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.addAll(Arrays.asList(5, 2, 8, 1, 9, 3));

        System.out.println("\nMax-heap poll order:");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " "); // 9, 8, 5, 3, 2, 1
        }

        System.out.println("\n\nPriorityQueue characteristics:");
        System.out.println("- Backed by binary heap (array-based)");
        System.out.println("- O(log n) for add/poll");
        System.out.println("- O(1) for peek");
        System.out.println("- NOT thread-safe");
        System.out.println("- Iterator doesn't guarantee order");
    }

    /**
     * Question 3: Demonstrate custom Comparator with PriorityQueue for complex objects
     */
    public static void demonstrateCustomPriority() {
        System.out.println("\n=== Custom Priority Queue ===");

        class Task {
            String name;
            int priority;

            Task(String name, int priority) {
                this.name = name;
                this.priority = priority;
            }

            @Override
            public String toString() {
                return name + "(P" + priority + ")";
            }
        }

        // Higher priority value = higher priority
        PriorityQueue<Task> taskQueue = new PriorityQueue<>(
            Comparator.comparingInt((Task t) -> -t.priority) // Negate for descending
                      .thenComparing(t -> t.name)
        );

        taskQueue.offer(new Task("Email", 2));
        taskQueue.offer(new Task("Meeting", 5));
        taskQueue.offer(new Task("Coffee", 1));
        taskQueue.offer(new Task("Report", 5));
        taskQueue.offer(new Task("Call", 3));

        System.out.println("Tasks by priority:");
        while (!taskQueue.isEmpty()) {
            System.out.println(taskQueue.poll());
        }
    }

    /**
     * Question 4: What is Deque interface? Demonstrate ArrayDeque vs LinkedList
     */
    public static void demonstrateDeque() {
        System.out.println("\n=== Deque Interface ===");

        Deque<String> deque = new ArrayDeque<>();

        // Deque as Queue (FIFO)
        deque.offer("A");
        deque.offer("B");
        System.out.println("As Queue (FIFO): " + deque.poll()); // A

        // Deque as Stack (LIFO)
        deque.push("X");
        deque.push("Y");
        System.out.println("As Stack (LIFO): " + deque.pop()); // Y

        // Double-ended operations
        deque.clear();
        deque.offerFirst("First");
        deque.offerLast("Last");
        deque.offerFirst("New First");
        deque.offerLast("New Last");

        System.out.println("Deque: " + deque);
        System.out.println("pollFirst(): " + deque.pollFirst());
        System.out.println("pollLast(): " + deque.pollLast());

        System.out.println("\nArrayDeque vs LinkedList:");
        System.out.println("ArrayDeque: Circular array, faster, no null elements");
        System.out.println("LinkedList: Doubly-linked, allows nulls, more memory");
        System.out.println("Prefer ArrayDeque for stack/queue operations");
    }

    /**
     * Question 5: Why ArrayDeque is better than Stack class?
     */
    public static void demonstrateStackAlternative() {
        System.out.println("\n=== ArrayDeque as Stack ===");

        // DON'T use legacy Stack class (extends Vector, synchronized)
        Deque<Integer> stack = new ArrayDeque<>();

        // Stack operations
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("Stack: " + stack);
        System.out.println("peek(): " + stack.peek());
        System.out.println("pop(): " + stack.pop());
        System.out.println("After pop: " + stack);

        System.out.println("\nWhy ArrayDeque > Stack:");
        System.out.println("- Stack extends Vector (legacy, synchronized overhead)");
        System.out.println("- ArrayDeque is faster, modern implementation");
        System.out.println("- ArrayDeque is consistent with Deque interface");
    }

    /**
     * Question 6: Explain BlockingQueue and its implementations
     */
    public static void demonstrateBlockingQueue() throws InterruptedException {
        System.out.println("\n=== BlockingQueue ===");

        // ArrayBlockingQueue: Bounded, array-backed
        BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(3);

        // LinkedBlockingQueue: Optionally bounded, linked nodes
        BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(3);

        // PriorityBlockingQueue: Unbounded, priority-based
        BlockingQueue<Integer> priorityQueue = new PriorityBlockingQueue<>();

        // SynchronousQueue: No capacity, direct handoff
        BlockingQueue<String> synchronousQueue = new SynchronousQueue<>();

        System.out.println("BlockingQueue implementations:");
        System.out.println("1. ArrayBlockingQueue - Bounded, fairness optional");
        System.out.println("2. LinkedBlockingQueue - Optionally bounded");
        System.out.println("3. PriorityBlockingQueue - Unbounded, ordered");
        System.out.println("4. SynchronousQueue - Zero capacity, direct handoff");
        System.out.println("5. DelayQueue - Elements available after delay");
        System.out.println("6. LinkedTransferQueue - Transfer and wait");

        // Blocking operations
        arrayQueue.put("Item1"); // Blocks if full
        arrayQueue.offer("Item2", 100, TimeUnit.MILLISECONDS); // Timeout

        String item = arrayQueue.take(); // Blocks if empty
        System.out.println("Took: " + item);

        String polled = arrayQueue.poll(100, TimeUnit.MILLISECONDS); // Timeout
        System.out.println("Polled: " + polled);
    }

    /**
     * Question 7: Demonstrate Producer-Consumer pattern with BlockingQueue
     */
    public static void demonstrateProducerConsumer() throws InterruptedException {
        System.out.println("\n=== Producer-Consumer Pattern ===");

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    Integer item = queue.take();
                    System.out.println("  Consumed: " + item);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Producer-Consumer completed");
    }

    /**
     * Question 8: What is DelayQueue and when to use it?
     */
    public static void demonstrateDelayQueue() throws InterruptedException {
        System.out.println("\n=== DelayQueue ===");

        class DelayedTask implements Delayed {
            private final String name;
            private final long startTime;

            DelayedTask(String name, long delayMs) {
                this.name = name;
                this.startTime = System.currentTimeMillis() + delayMs;
            }

            @Override
            public long getDelay(TimeUnit unit) {
                long diff = startTime - System.currentTimeMillis();
                return unit.convert(diff, TimeUnit.MILLISECONDS);
            }

            @Override
            public int compareTo(Delayed o) {
                return Long.compare(this.startTime, ((DelayedTask) o).startTime);
            }

            @Override
            public String toString() {
                return name;
            }
        }

        DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();

        delayQueue.put(new DelayedTask("Task1", 300));
        delayQueue.put(new DelayedTask("Task2", 100));
        delayQueue.put(new DelayedTask("Task3", 200));

        System.out.println("Tasks will be available after their delay:");
        while (!delayQueue.isEmpty()) {
            DelayedTask task = delayQueue.take(); // Blocks until delay expires
            System.out.println("Executed: " + task);
        }

        System.out.println("\nUse cases: Scheduled tasks, cache expiration, rate limiting");
    }

    /**
     * Question 9: Explain LinkedTransferQueue
     */
    public static void demonstrateLinkedTransferQueue() throws InterruptedException {
        System.out.println("\n=== LinkedTransferQueue ===");

        LinkedTransferQueue<String> transferQueue = new LinkedTransferQueue<>();

        System.out.println("LinkedTransferQueue features:");
        System.out.println("- transfer(): Blocks until element is consumed");
        System.out.println("- tryTransfer(): Non-blocking attempt");
        System.out.println("- Useful for direct producer-consumer handoff");

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(500); // Delay
                String item = transferQueue.take();
                System.out.println("Consumer received: " + item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();

        System.out.println("Producer calling transfer()...");
        long start = System.currentTimeMillis();
        transferQueue.transfer("Message"); // Blocks until consumed
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Transfer completed after " + elapsed + "ms");

        consumer.join();
    }

    /**
     * Question 10: Compare Queue implementations - when to use which?
     */
    public static void demonstrateQueueComparison() {
        System.out.println("\n=== Queue Implementation Comparison ===");

        System.out.println("\n1. LinkedList (Deque)");
        System.out.println("   - Unbounded, allows nulls");
        System.out.println("   - Not thread-safe");
        System.out.println("   - Use: Simple queue/deque needs");

        System.out.println("\n2. ArrayDeque");
        System.out.println("   - Unbounded, no nulls");
        System.out.println("   - Faster than LinkedList");
        System.out.println("   - Use: Stack or deque operations");

        System.out.println("\n3. PriorityQueue");
        System.out.println("   - Unbounded, natural ordering");
        System.out.println("   - O(log n) operations");
        System.out.println("   - Use: Priority-based processing");

        System.out.println("\n4. ArrayBlockingQueue");
        System.out.println("   - Bounded, thread-safe");
        System.out.println("   - Blocking operations");
        System.out.println("   - Use: Producer-consumer with back-pressure");

        System.out.println("\n5. LinkedBlockingQueue");
        System.out.println("   - Optionally bounded, thread-safe");
        System.out.println("   - Higher throughput than Array version");
        System.out.println("   - Use: High-throughput scenarios");

        System.out.println("\n6. PriorityBlockingQueue");
        System.out.println("   - Unbounded, thread-safe, ordered");
        System.out.println("   - Use: Priority-based concurrent processing");

        System.out.println("\n7. SynchronousQueue");
        System.out.println("   - Zero capacity, direct handoff");
        System.out.println("   - Use: Thread pools, direct exchange");

        System.out.println("\n8. DelayQueue");
        System.out.println("   - Unbounded, delays");
        System.out.println("   - Use: Scheduled tasks, timeouts");
    }

    public static void main(String[] args) throws InterruptedException {
        demonstrateQueueMethods();
        demonstratePriorityQueue();
        demonstrateCustomPriority();
        demonstrateDeque();
        demonstrateStackAlternative();
        demonstrateBlockingQueue();
        demonstrateProducerConsumer();
        demonstrateDelayQueue();
        demonstrateLinkedTransferQueue();
        demonstrateQueueComparison();
    }
}

