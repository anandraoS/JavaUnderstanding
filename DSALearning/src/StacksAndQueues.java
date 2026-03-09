/**
 * STACKS AND QUEUES - Linear Data Structures
 *
 * STACK (LIFO - Last In First Out):
 * - Operations: push, pop, peek
 * - Real-world: Undo/Redo, browser back button, call stack
 * - Time: O(1) for all operations
 *
 * QUEUE (FIFO - First In First Out):
 * - Operations: enqueue, dequeue, peek
 * - Real-world: Printer queue, ticket counter, BFS traversal
 * - Time: O(1) for all operations
 *
 * PRIORITY QUEUE:
 * - Elements served by priority, not insertion order
 * - Time: O(log n) for insertion/deletion
 */

import java.util.*;

public class StacksAndQueues {

    // ============ STACK IMPLEMENTATION ============
    public static class Stack<T> {
        private Node<T> top;

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
            }
        }

        public void push(T data) {
            Node<T> newNode = new Node<>(data);
            newNode.next = top;
            top = newNode;
        }

        public T pop() {
            if (isEmpty()) return null;
            T data = top.data;
            top = top.next;
            return data;
        }

        public T peek() {
            return isEmpty() ? null : top.data;
        }

        public boolean isEmpty() {
            return top == null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            Node<T> current = top;
            while (current != null) {
                sb.append(current.data).append(", ");
                current = current.next;
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // ============ QUEUE IMPLEMENTATION ============
    public static class Queue<T> {
        private Node<T> front;
        private Node<T> rear;

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
            }
        }

        public void enqueue(T data) {
            Node<T> newNode = new Node<>(data);
            if (isEmpty()) {
                front = newNode;
            } else {
                rear.next = newNode;
            }
            rear = newNode;
        }

        public T dequeue() {
            if (isEmpty()) return null;
            T data = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            return data;
        }

        public T peek() {
            return isEmpty() ? null : front.data;
        }

        public boolean isEmpty() {
            return front == null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            Node<T> current = front;
            while (current != null) {
                sb.append(current.data).append(", ");
                current = current.next;
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // ============ PROBLEMS USING STACKS ============

    /**
     * PROBLEM 1: Check if Parentheses are Balanced
     *
     * DRY RUN: Check "({[]})"
     *
     * char='(': stack is empty, push '('        -> stack=['(']
     * char='{': push '{'                        -> stack=['(', '{']
     * char='[': push '['                        -> stack=['(', '{', '[']
     * char=']': top='[', matches, pop           -> stack=['(', '{']
     * char='}': top='{', matches, pop           -> stack=['(']
     * char=')': top='(', matches, pop           -> stack=[]
     *
     * Stack is empty at end -> Balanced ✓
     *
     * Counter-example: "({[}])"
     * ... skip to char='}'
     * char='}': top='[', does NOT match '}' -> NOT Balanced ✗
     *
     * Time: O(n), Space: O(n)
     */
    public static boolean isBalanced(String s) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = new HashMap<>();
        pairs.put(')', '(');
        pairs.put('}', '{');
        pairs.put(']', '[');

        for (char c : s.toCharArray()) {
            if (pairs.containsKey(c)) {
                // Closing bracket
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            } else {
                // Opening bracket
                stack.push(c);
            }
        }

        return stack.isEmpty();
    }

    /**
     * PROBLEM 2: Reverse a String using Stack
     *
     * DRY RUN: Reverse "hello"
     *
     * Push phase:
     *   'h' -> stack=['h']
     *   'e' -> stack=['h', 'e']
     *   'l' -> stack=['h', 'e', 'l']
     *   'l' -> stack=['h', 'e', 'l', 'l']
     *   'o' -> stack=['h', 'e', 'l', 'l', 'o']
     *
     * Pop phase:
     *   pop 'o' -> result="o"
     *   pop 'l' -> result="ol"
     *   pop 'l' -> result="oll"
     *   pop 'e' -> result="olle"
     *   pop 'h' -> result="olleh"
     *
     * Time: O(n), Space: O(n)
     */
    public static String reverseString(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            stack.push(c);
        }

        StringBuilder reversed = new StringBuilder();
        while (!stack.isEmpty()) {
            reversed.append(stack.pop());
        }

        return reversed.toString();
    }

    /**
     * PROBLEM 3: Evaluate Postfix Expression
     * (Also called Reverse Polish Notation)
     *
     * In postfix, operators come AFTER operands
     * Example: "3 4 +" means 3 + 4 = 7
     *
     * DRY RUN: Evaluate "3 4 + 5 *"
     * This means: (3 + 4) * 5 = 35
     *
     * Token='3': push 3           -> stack=[3]
     * Token='4': push 4           -> stack=[3, 4]
     * Token='+': pop 4 and 3, push 3+4=7 -> stack=[7]
     * Token='5': push 5           -> stack=[7, 5]
     * Token='*': pop 5 and 7, push 7*5=35 -> stack=[35]
     *
     * Result: 35 ✓
     *
     * Time: O(n), Space: O(n)
     */
    public static int evaluatePostfix(String expression) {
        Stack<Integer> stack = new Stack<>();
        String[] tokens = expression.split(" ");

        for (String token : tokens) {
            if (token.matches("-?\\d+")) {  // Is a number
                stack.push(Integer.parseInt(token));
            } else {  // Is an operator
                int b = stack.pop();
                int a = stack.pop();
                int result = 0;

                switch (token) {
                    case "+":
                        result = a + b;
                        break;
                    case "-":
                        result = a - b;
                        break;
                    case "*":
                        result = a * b;
                        break;
                    case "/":
                        result = a / b;
                        break;
                }

                stack.push(result);
            }
        }

        return stack.peek();
    }

    // ============ PROBLEMS USING QUEUES ============

    /**
     * PROBLEM 1: Implement Queue using Stacks
     *
     * Approach: Use two stacks
     * - Stack1: for push operations
     * - Stack2: for pop operations
     * When Stack2 is empty, move all from Stack1 to Stack2
     *
     * DRY RUN: Enqueue 1,2,3 then Dequeue
     *
     * Enqueue 1: stack1=[1]
     * Enqueue 2: stack1=[1, 2]
     * Enqueue 3: stack1=[1, 2, 3]
     *
     * Dequeue:
     *   stack2 is empty, move stack1 to stack2
     *   stack1=[]
     *   stack2=[3, 2, 1] (reversed)
     *   pop from stack2 = 1, stack2=[3, 2]
     *
     * Dequeue:
     *   stack2 is not empty
     *   pop from stack2 = 2, stack2=[3]
     *
     * Time: O(1) amortized
     */

    /**
     * PROBLEM 2: Print Level Order (BFS using Queue)
     *
     * For tree:     1
     *              / \
     *             2   3
     *            / \
     *           4   5
     *
     * DRY RUN:
     *
     * queue = [1]
     *
     * Step 1: dequeue 1, print 1, enqueue children 2,3
     *   Output: 1
     *   queue = [2, 3]
     *
     * Step 2: dequeue 2, print 2, enqueue children 4,5
     *   Output: 1 2
     *   queue = [3, 4, 5]
     *
     * Step 3: dequeue 3, print 3, no children
     *   Output: 1 2 3
     *   queue = [4, 5]
     *
     * Step 4: dequeue 4, print 4, no children
     *   Output: 1 2 3 4
     *   queue = [5]
     *
     * Step 5: dequeue 5, print 5, no children
     *   Output: 1 2 3 4 5
     *   queue = []
     *
     * Result: Level order = [1, 2, 3, 4, 5]
     * Time: O(n), Space: O(max_level_width)
     */

    public static void main(String[] args) {
        System.out.println("===== STACKS AND QUEUES =====\n");

        // Stack demonstration
        System.out.println("--- Stack Operations ---");
        Stack<Integer> stack = new Stack<>();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        System.out.println("Stack after pushing 10, 20, 30: " + stack);
        System.out.println("Peek: " + stack.peek());
        System.out.println("Pop: " + stack.pop());
        System.out.println("Stack after pop: " + stack);
        System.out.println();

        // Queue demonstration
        System.out.println("--- Queue Operations ---");
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println("Queue after enqueueing 10, 20, 30: " + queue);
        System.out.println("Peek: " + queue.peek());
        System.out.println("Dequeue: " + queue.dequeue());
        System.out.println("Queue after dequeue: " + queue);
        System.out.println();

        // Balanced parentheses
        System.out.println("--- Balanced Parentheses ---");
        String[] tests = {"({[]})", "({[}])", "([{}])", "([)]"};
        for (String test : tests) {
            System.out.println(test + " -> " + (isBalanced(test) ? "Balanced" : "Not Balanced"));
        }
        System.out.println();

        // Reverse string
        System.out.println("--- Reverse String using Stack ---");
        String str = "hello";
        System.out.println("Original: " + str);
        System.out.println("Reversed: " + reverseString(str));
        System.out.println();

        // Evaluate postfix
        System.out.println("--- Evaluate Postfix Expression ---");
        String postfix = "3 4 + 5 *";
        System.out.println("Expression: " + postfix);
        System.out.println("Evaluation: " + postfix + " = " + evaluatePostfix(postfix));
        System.out.println();

        String postfix2 = "10 5 /";
        System.out.println("Expression: " + postfix2);
        System.out.println("Evaluation: " + postfix2 + " = " + evaluatePostfix(postfix2));
    }
}

