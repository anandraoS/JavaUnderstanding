package multithreading;

import java.util.*;
import java.util.concurrent.*;

/**
 * Concurrent collections and their trade-offs.
 */
public class ConcurrentCollectionsExamples {

    public static void demonstrateConcurrentHashMap() {
        System.out.println("=== ConcurrentHashMap ===");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("A", 1);
        map.compute("A", (k, v) -> v == null ? 1 : v + 1);

        System.out.println("ConcurrentHashMap: " + map);
    }

    public static void demonstrateCopyOnWrite() {
        System.out.println("\n=== CopyOnWriteArrayList ===");

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");

        for (String item : list) {
            if ("A".equals(item)) {
                list.add("C");
            }
        }

        System.out.println("List after iteration: " + list);
    }

    public static void demonstrateBlockingQueue() throws InterruptedException {
        System.out.println("\n=== BlockingQueue ===");

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        queue.put(1);
        queue.put(2);

        System.out.println("Take: " + queue.take());
        System.out.println("Poll: " + queue.poll(50, TimeUnit.MILLISECONDS));
    }

    public static void main(String[] args) throws Exception {
        demonstrateConcurrentHashMap();
        demonstrateCopyOnWrite();
        demonstrateBlockingQueue();
    }
}

