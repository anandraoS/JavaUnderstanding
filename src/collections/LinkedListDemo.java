package collections;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Deque;

/*
 * =============================================================================
 * LinkedListDemo.java — LinkedList as List, Queue, and Deque
 * =============================================================================
 *
 * CONCEPT: LinkedList
 * --------------------
 * LinkedList is a doubly-linked list implementation.
 * It implements both List AND Deque interfaces, making it versatile.
 *
 * STRUCTURE: Each node holds data + reference to next and previous nodes
 *   null ← [prev|data|next] ↔ [prev|data|next] ↔ [prev|data|next] → null
 *
 * KEY CHARACTERISTICS:
 *  - Efficient add/remove at BOTH ENDS: O(1)
 *  - Slow random access by index: O(n)  ← must traverse from head or tail
 *  - More memory per element than ArrayList (stores two references per node)
 *  - Can be used as: List, Stack, Queue, Deque
 *
 * USE CASES:
 *  - Frequent add/remove at beginning or end
 *  - Implementing a Queue (FIFO) or Stack (LIFO)
 *  - When you need a Deque (double-ended queue)
 *
 * WHEN TO USE ArrayList vs LinkedList:
 *  ArrayList:   Better for random access and when most operations are reads
 *  LinkedList:  Better for frequent insertions/deletions at head or tail
 *
 * HOW TO RUN:
 *  $ javac -d out src/collections/LinkedListDemo.java
 *  $ java -cp out collections.LinkedListDemo
 * =============================================================================
 */
public class LinkedListDemo {

    public static void main(String[] args) {

        // ── LINKEDLIST AS LIST ────────────────────────────────────────────────
        System.out.println("=== LinkedList as List ===");

        LinkedList<String> list = new LinkedList<>();
        list.add("Banana");         // add to end
        list.add("Cherry");
        list.add("Date");
        list.addFirst("Apple");     // add to BEGINNING — O(1)
        list.addLast("Elderberry"); // add to END — O(1)

        System.out.println("List: " + list);
        System.out.println("First: " + list.getFirst());
        System.out.println("Last:  " + list.getLast());
        System.out.println("Size:  " + list.size());

        // Remove from ends
        String removedFirst = list.removeFirst();
        String removedLast  = list.removeLast();
        System.out.println("Removed first: " + removedFirst);
        System.out.println("Removed last:  " + removedLast);
        System.out.println("After removes: " + list);

        // ── LINKEDLIST AS QUEUE (FIFO) ────────────────────────────────────────
        System.out.println("\n=== LinkedList as Queue (FIFO) ===");

        Queue<String> queue = new LinkedList<>();  // use Queue interface

        // Enqueue (add to end)
        queue.offer("Task 1");
        queue.offer("Task 2");
        queue.offer("Task 3");
        queue.offer("Task 4");

        System.out.println("Queue: " + queue);
        System.out.println("Peek (head): " + queue.peek());   // see front without removing

        // Dequeue (remove from front)
        while (!queue.isEmpty()) {
            String task = queue.poll();   // removes and returns the head
            System.out.println("Processing: " + task);
        }
        System.out.println("Queue empty: " + queue.isEmpty());

        // ── LINKEDLIST AS DEQUE (double-ended queue) ──────────────────────────
        System.out.println("\n=== LinkedList as Deque ===");

        Deque<Integer> deque = new LinkedList<>();
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(3);
        deque.addLast(4);

        System.out.println("Deque: " + deque);

        System.out.println("peekFirst(): " + deque.peekFirst());   // 1
        System.out.println("peekLast():  " + deque.peekLast());    // 4
        deque.pollFirst();   // removes 1
        deque.pollLast();    // removes 4
        System.out.println("After pollFirst/Last: " + deque);

        // ── LINKEDLIST AS STACK (LIFO) ────────────────────────────────────────
        System.out.println("\n=== LinkedList as Stack (LIFO) ===");

        // Using Deque as a stack (push/pop from the front)
        Deque<String> stack = new LinkedList<>();
        stack.push("Page 1");
        stack.push("Page 2");
        stack.push("Page 3");

        System.out.println("Stack (top is first): " + stack);

        while (!stack.isEmpty()) {
            System.out.println("Pop: " + stack.pop());
        }

        // ── PRACTICAL EXAMPLE: BROWSER HISTORY ───────────────────────────────
        System.out.println("\n=== Browser History Simulation ===");
        Deque<String> history = new LinkedList<>();
        String[] pages = {"google.com", "github.com", "stackoverflow.com", "java.com"};

        System.out.println("Browsing pages:");
        for (String page : pages) {
            history.push(page);   // push each page (most recent is at top)
            System.out.println("  Visited: " + page);
        }

        System.out.println("\nBack-navigating:");
        for (int i = 0; i < 3; i++) {
            System.out.println("  Back to: " + history.pop());
        }
        System.out.println("Current page: " + history.peek());
    }
}

/*
 * EXPECTED OUTPUT (abbreviated):
 * ──────────────────────────────
 * === LinkedList as List ===
 * List: [Apple, Banana, Cherry, Date, Elderberry]
 * First: Apple
 * Last:  Elderberry
 *
 * === LinkedList as Queue (FIFO) ===
 * Queue: [Task 1, Task 2, Task 3, Task 4]
 * Peek (head): Task 1
 * Processing: Task 1
 * Processing: Task 2
 * Processing: Task 3
 * Processing: Task 4
 * Queue empty: true
 *
 * COMMON MISTAKES:
 * ─────────────────
 * 1. Using get(index) on LinkedList for large lists — O(n) traversal!
 *    Use ArrayList if you need random access by index.
 *
 * 2. Confusing Queue methods:
 *    offer() vs add(): offer() returns false on failure, add() throws exception
 *    poll() vs remove(): poll() returns null if empty, remove() throws exception
 *    peek() vs element(): peek() returns null if empty, element() throws exception
 *
 * 3. LinkedList vs ArrayDeque:
 *    For Queue/Stack/Deque usage, ArrayDeque is FASTER than LinkedList.
 *    LinkedList allocates node objects on heap; ArrayDeque uses an array internally.
 *
 * 4. Thread safety: LinkedList is not thread-safe.
 *    Use Collections.synchronizedList() or ConcurrentLinkedQueue for thread safety.
 */
