/**
 * LINKED LISTS - Dynamic Data Structure
 *
 * Singly Linked List: Each node points to next
 * Doubly Linked List: Each node points to next and previous
 *
 * Advantages over arrays:
 * - No fixed size, grows dynamically
 * - Fast insertion/deletion at beginning or with pointer
 *
 * Disadvantages:
 * - No random access, O(n) to reach element
 * - Extra memory for pointers
 */

public class LinkedLists {

    // ============ NODE DEFINITION ============
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // ============ BASIC OPERATIONS ============

    /**
     * Insert at beginning
     * DRY RUN: Insert 0 at beginning of 1->2->3
     *
     * Step 1: Create new node with data=0
     *         newNode.next = head (points to 1)
     * Step 2: head = newNode
     * Result: 0->1->2->3
     *
     * Time: O(1), Space: O(1)
     */
    public static Node insertAtBeginning(Node head, int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        return newNode;
    }

    /**
     * Insert at end
     * DRY RUN: Insert 4 at end of 1->2->3
     *
     * Step 1: Traverse to last node (where next is null)
     *         1 -> 2 -> 3(last, next=null)
     * Step 2: Create new node with data=4
     * Step 3: last.next = newNode
     * Result: 1->2->3->4
     *
     * Time: O(n), Space: O(1)
     */
    public static Node insertAtEnd(Node head, int data) {
        Node newNode = new Node(data);

        if (head == null) {
            return newNode;
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;

        return head;
    }

    /**
     * Delete a node with given value
     * DRY RUN: Delete 2 from 1->2->3->2->4
     *
     * Step 1: Start at head (1)
     * Step 2: Move to next (2), check if data=2, YES!
     *         Set head.next = current.next (skip 2)
     *         Result: 1->3->2->4
     * Step 3: Continue from current = 3
     * Step 4: Move to next (2), check if data=2, YES!
     *         Set 3.next = 2.next (skip 2)
     *         Result: 1->3->4
     * Step 5: Move to next (4), data != 2
     * Step 6: Reach end, stop
     * Result: 1->3->4
     *
     * Time: O(n), Space: O(1)
     */
    public static Node deleteNode(Node head, int value) {
        if (head == null) return null;

        // Delete from beginning
        while (head != null && head.data == value) {
            head = head.next;
        }

        if (head == null) return null;

        // Delete from middle/end
        Node current = head;
        while (current.next != null) {
            if (current.next.data == value) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return head;
    }

    /**
     * REVERSE A LINKED LIST
     * One of the most important problems!
     *
     * Approach: Three pointers - prev, current, next
     *
     * DRY RUN: Reverse 1->2->3->4
     *
     * Initial: prev=null, curr=1, next=null
     *          1->2->3->4
     *
     * Iteration 1:
     *   next = curr.next = 2 (save next before changing curr.next)
     *   curr.next = prev = null (reverse the link)
     *   prev = curr = 1
     *   curr = next = 2
     *   List so far: 1(reversed)
     *
     * Iteration 2:
     *   next = curr.next = 3
     *   curr.next = prev = 1 (reverse the link)
     *   prev = curr = 2
     *   curr = next = 3
     *   List so far: 2->1(reversed)
     *
     * Iteration 3:
     *   next = curr.next = 4
     *   curr.next = prev = 2 (reverse the link)
     *   prev = curr = 3
     *   curr = next = 4
     *   List so far: 3->2->1(reversed)
     *
     * Iteration 4:
     *   next = curr.next = null
     *   curr.next = prev = 3 (reverse the link)
     *   prev = curr = 4
     *   curr = next = null
     *   Loop ends
     *
     * Result: 4->3->2->1
     *
     * Time: O(n), Space: O(1)
     */
    public static Node reverseList(Node head) {
        Node prev = null;
        Node current = head;

        while (current != null) {
            Node next = current.next;  // Save next node
            current.next = prev;        // Reverse the link
            prev = current;             // Move prev forward
            current = next;             // Move current forward
        }

        return prev;  // New head is prev
    }

    /**
     * DETECT CYCLE IN LINKED LIST (Floyd's Cycle Detection)
     * Problem: List may have a cycle, find if one exists
     *
     * Approach: Two pointers - slow (1 step), fast (2 steps)
     * If they meet, there's a cycle
     *
     * DRY RUN: Detect cycle in 1->2->3->4->2 (3 points to 2)
     *
     * Initial: slow=1, fast=1, hasCycle=false
     *
     * Step 1:
     *   slow = slow.next = 2
     *   fast = fast.next.next = 3
     *   2 != 3, continue
     *
     * Step 2:
     *   slow = slow.next = 3
     *   fast = fast.next.next = 2
     *   3 != 2, continue
     *
     * Step 3:
     *   slow = slow.next = 4
     *   fast = fast.next.next = 3
     *   4 != 3, continue
     *
     * Step 4:
     *   slow = slow.next = 2
     *   fast = fast.next.next = 2
     *   2 == 2, CYCLE DETECTED!
     *
     * Why it works: In a cycle, fast pointer moves 2 steps while slow moves 1 step.
     * They will eventually meet at the same node.
     *
     * Time: O(n), Space: O(1)
     */
    public static boolean hasCycle(Node head) {
        if (head == null || head.next == null) {
            return false;
        }

        Node slow = head;
        Node fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;  // Cycle detected
            }
        }

        return false;  // No cycle
    }

    /**
     * MERGE TWO SORTED LINKED LISTS
     *
     * DRY RUN: Merge 1->3->5 and 2->4->6
     *
     * Create dummy node: dummy(0)
     * pointer = dummy
     *
     * Iteration 1:
     *   Compare 1 vs 2: 1 < 2
     *   pointer.next = 1, pointer = 1, list1 = 3
     *
     * Iteration 2:
     *   Compare 3 vs 2: 3 > 2
     *   pointer.next = 2, pointer = 2, list2 = 4
     *
     * Iteration 3:
     *   Compare 3 vs 4: 3 < 4
     *   pointer.next = 3, pointer = 3, list1 = 5
     *
     * Iteration 4:
     *   Compare 5 vs 4: 5 > 4
     *   pointer.next = 4, pointer = 4, list2 = 6
     *
     * Iteration 5:
     *   Compare 5 vs 6: 5 < 6
     *   pointer.next = 5, pointer = 5, list1 = null
     *
     * list1 is null, attach remaining list2: pointer.next = 6
     *
     * Result: 0->1->2->3->4->5->6 (dummy.next)
     * Final: 1->2->3->4->5->6
     *
     * Time: O(m + n), Space: O(1)
     */
    public static Node mergeSortedLists(Node list1, Node list2) {
        Node dummy = new Node(0);
        Node pointer = dummy;

        while (list1 != null && list2 != null) {
            if (list1.data <= list2.data) {
                pointer.next = list1;
                list1 = list1.next;
            } else {
                pointer.next = list2;
                list2 = list2.next;
            }
            pointer = pointer.next;
        }

        // Attach remaining nodes
        if (list1 != null) {
            pointer.next = list1;
        } else {
            pointer.next = list2;
        }

        return dummy.next;
    }

    // ============ UTILITY FUNCTIONS ============

    public static void printList(Node head) {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public static Node createList(int... values) {
        if (values.length == 0) return null;
        Node head = new Node(values[0]);
        Node current = head;
        for (int i = 1; i < values.length; i++) {
            current.next = new Node(values[i]);
            current = current.next;
        }
        return head;
    }

    public static void main(String[] args) {
        System.out.println("===== LINKED LISTS =====\n");

        // Insert at beginning
        System.out.println("--- Insert at Beginning ---");
        Node head = createList(2, 3, 4);
        System.out.println("Original: ");
        printList(head);
        head = insertAtBeginning(head, 1);
        System.out.println("After inserting 1 at beginning: ");
        printList(head);
        System.out.println();

        // Insert at end
        System.out.println("--- Insert at End ---");
        head = createList(1, 2, 3);
        System.out.println("Original: ");
        printList(head);
        head = insertAtEnd(head, 4);
        System.out.println("After inserting 4 at end: ");
        printList(head);
        System.out.println();

        // Delete node
        System.out.println("--- Delete Node ---");
        head = createList(1, 2, 3, 2, 4);
        System.out.println("Original: ");
        printList(head);
        head = deleteNode(head, 2);
        System.out.println("After deleting 2: ");
        printList(head);
        System.out.println();

        // Reverse list
        System.out.println("--- Reverse List ---");
        head = createList(1, 2, 3, 4);
        System.out.println("Original: ");
        printList(head);
        head = reverseList(head);
        System.out.println("Reversed: ");
        printList(head);
        System.out.println();

        // Merge sorted lists
        System.out.println("--- Merge Sorted Lists ---");
        Node list1 = createList(1, 3, 5);
        Node list2 = createList(2, 4, 6);
        System.out.println("List 1: ");
        printList(list1);
        System.out.println("List 2: ");
        printList(list2);
        head = mergeSortedLists(list1, list2);
        System.out.println("Merged: ");
        printList(head);
        System.out.println();

        // Detect cycle
        System.out.println("--- Detect Cycle ---");
        head = createList(1, 2, 3, 4);
        System.out.println("List without cycle: ");
        printList(head);
        System.out.println("Has cycle: " + hasCycle(head));

        // Create a list with cycle for testing
        System.out.println("List with cycle (1->2->3->2): " + "Has cycle: " + hasCycle(head));
    }
}

