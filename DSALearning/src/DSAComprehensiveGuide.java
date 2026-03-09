/**
 * ============================================================================
 * COMPREHENSIVE DSA INTERVIEW PREPARATION GUIDE
 * ============================================================================
 *
 * This workspace contains detailed implementations and explanations of
 * all essential Data Structures and Algorithms concepts needed to crack
 * coding interviews.
 *
 * ============================================================================
 * 1. ARRAYS AND STRINGS
 * ============================================================================
 * File: ArraysAndStrings.java
 *
 * KEY CONCEPTS:
 * • Two-Pointer Technique: Use two pointers to solve problems efficiently
 * • Sliding Window: Maintain a window that slides through the array
 * • String Manipulation: Reversal, palindrome checking, etc.
 *
 * PROBLEMS COVERED:
 * - Two Sum (find pair with given sum)
 * - Longest Substring Without Repeating Characters
 * - Array Reversal
 * - Palindrome Checking
 *
 * TIME COMPLEXITIES:
 * - Access: O(1)
 * - Search: O(n)
 * - Insert: O(n)
 * - Delete: O(n)
 *
 * TIPS:
 * • Two pointers are ideal for sorted arrays
 * • Sliding window is great for substring/subarray problems
 * • Always consider space vs time tradeoffs
 *
 * ============================================================================
 * 2. SEARCHING AND SORTING
 * ============================================================================
 * File: SearchingAndSorting.java
 *
 * SEARCHING ALGORITHMS:
 * • Linear Search: O(n) - simple, works on unsorted arrays
 * • Binary Search: O(log n) - requires sorted array, efficient
 *
 * SORTING ALGORITHMS:
 *
 * Bubble Sort:
 * - Time: O(n²), Space: O(1)
 * - How: Repeatedly swap adjacent elements if wrong order
 * - Best for: Small datasets, educational purposes
 *
 * Selection Sort:
 * - Time: O(n²), Space: O(1)
 * - How: Find minimum and place at beginning, repeat for rest
 * - Good for: Minimizing writes (only n swaps)
 *
 * Insertion Sort:
 * - Time: O(n²) average, O(n) best case
 * - How: Build sorted array by inserting elements one by one
 * - Best for: Small arrays, nearly sorted data
 *
 * Merge Sort:
 * - Time: O(n log n), Space: O(n)
 * - How: Divide, sort recursively, then merge
 * - Best for: Large datasets, when stability required
 * - Stable: YES (maintains relative order of equal elements)
 *
 * Quick Sort:
 * - Time: O(n log n) avg, O(n²) worst, Space: O(log n)
 * - How: Pick pivot, partition, recursively sort
 * - Best for: General purpose, in-place sorting
 * - Stable: NO
 *
 * INTERVIEW TIPS:
 * • Know which sorting algorithm uses which approach
 * • Understand stability and space complexity tradeoffs
 * • Binary search is crucial for many problems
 *
 * ============================================================================
 * 3. LINKED LISTS
 * ============================================================================
 * File: LinkedLists.java
 *
 * KEY OPERATIONS:
 *
 * Insert at Beginning: O(1)
 * Insert at End: O(n) [without tail pointer, O(1) with tail]
 * Delete: O(n)
 * Search: O(n)
 *
 * CRITICAL PROBLEMS:
 *
 * Reverse Linked List:
 * - Time: O(n), Space: O(1)
 * - Technique: Three pointers (prev, current, next)
 * - DRY RUN: Keep reversing links while traversing
 *
 * Detect Cycle:
 * - Algorithm: Floyd's Cycle Detection (tortoise and hare)
 * - Time: O(n), Space: O(1)
 * - Why: Slow pointer moves 1 step, fast moves 2 steps
 *        If they meet, cycle exists
 *
 * Merge Sorted Lists:
 * - Time: O(m + n), Space: O(1)
 * - Technique: Use dummy node and compare nodes
 *
 * INTERVIEW TIPS:
 * • Always be careful with null pointers
 * • Use dummy node for easier insertion/deletion
 * • Tortoise-hare technique is very common
 *
 * ============================================================================
 * 4. STACKS AND QUEUES
 * ============================================================================
 * File: StacksAndQueues.java
 *
 * STACK (LIFO - Last In First Out):
 * - Operations: push O(1), pop O(1), peek O(1)
 * - Use cases: Undo/Redo, browser back button, function call stack
 *
 * QUEUE (FIFO - First In First Out):
 * - Operations: enqueue O(1), dequeue O(1), peek O(1)
 * - Use cases: Printer queue, BFS traversal, scheduling
 *
 * IMPORTANT PROBLEMS:
 *
 * Balanced Parentheses:
 * - Algorithm: Push opening, pop when closing
 * - Time: O(n), Space: O(n)
 * - Variants: (), {}, []
 *
 * Evaluate Postfix Expression:
 * - Algorithm: Push numbers, apply operators to top 2
 * - Time: O(n), Space: O(n)
 * - Example: "3 4 + 5 *" = (3+4)*5 = 35
 *
 * Queue using Stacks:
 * - Two stacks: one for push, one for pop
 * - Amortized Time: O(1)
 *
 * INTERVIEW TIPS:
 * • Practice converting between problems (stack to queue, etc.)
 * • Postfix expression problems are common
 * • Know when to use which data structure
 *
 * ============================================================================
 * 5. TREES
 * ============================================================================
 * File: Trees.java
 *
 * TREE TRAVERSALS:
 *
 * Inorder (Left, Node, Right):
 * - For BST: Gives sorted order!
 * - Time: O(n), Space: O(h)
 *
 * Preorder (Node, Left, Right):
 * - Use: Tree copying, expression trees
 * - Time: O(n), Space: O(h)
 *
 * Postorder (Left, Right, Node):
 * - Use: Deleting tree, postfix expressions
 * - Time: O(n), Space: O(h)
 *
 * Level Order (BFS):
 * - Use: Queue-based traversal
 * - Time: O(n), Space: O(max_width)
 *
 * BINARY SEARCH TREE (BST):
 * Property: Left < Parent < Right
 *
 * Insert: O(log n) avg, O(n) worst (skewed)
 * Search: O(log n) avg, O(n) worst
 * Delete: O(log n) avg
 *
 * BALANCED TREES:
 * AVL Trees, Red-Black Trees maintain height ≤ log n
 * Guarantee O(log n) operations
 *
 * KEY PROBLEMS:
 * - Height: O(n) time, O(h) space
 * - Is Balanced: O(n) time, check height difference ≤ 1
 * - Lowest Common Ancestor: O(h) time for BST
 *
 * INTERVIEW TIPS:
 * • Understand difference between each traversal
 * • Know when to use which traversal
 * • LCA is a frequently asked problem
 * • Practice both recursive and iterative approaches
 *
 * ============================================================================
 * 6. GRAPHS
 * ============================================================================
 * File: Graphs.java
 *
 * REPRESENTATIONS:
 * 1. Adjacency List: Better for sparse graphs
 *    Space: O(V + E)
 *
 * 2. Adjacency Matrix: Better for dense graphs
 *    Space: O(V²)
 *
 * TRAVERSALS:
 *
 * DFS (Depth-First Search):
 * - Explores as far as possible before backtracking
 * - Time: O(V + E), Space: O(V)
 * - Implementation: Recursion or stack
 * - Use: Cycle detection, topological sort, connected components
 *
 * BFS (Breadth-First Search):
 * - Explores all neighbors before going deeper
 * - Time: O(V + E), Space: O(V)
 * - Implementation: Queue
 * - Use: Shortest path in unweighted, level order
 *
 * SHORTEST PATH ALGORITHMS:
 *
 * Dijkstra (Non-negative weights):
 * - Time: O((V + E) log V) with min-heap
 * - Greedy algorithm using priority queue
 * - Cannot handle negative edges
 *
 * Floyd-Warshall (All pairs):
 * - Time: O(V³)
 * - Works with negative edges (no negative cycles)
 * - Dynamic programming approach
 *
 * TOPOLOGICAL SORT:
 * - For Directed Acyclic Graphs (DAGs)
 * - Time: O(V + E)
 * - DFS-based: Use stack to store post-order
 * - Use: Task scheduling, build systems
 *
 * INTERVIEW TIPS:
 * • Know both DFS and BFS implementations
 * • Dijkstra is frequently asked
 * • Always clarify if graph is weighted/directed
 *
 * ============================================================================
 * 7. RECURSION AND DYNAMIC PROGRAMMING
 * ============================================================================
 * File: RecursionAndDP.java
 *
 * RECURSION:
 * - Function calls itself with smaller input
 * - MUST have base case to stop
 * - Time: Often exponential O(2^n)
 *
 * DYNAMIC PROGRAMMING:
 * Three approaches:
 *
 * 1. Memoization (Top-Down):
 *    - Start from problem, store results
 *    - Recursive with caching
 *    - Time: O(n) with memoization vs O(2^n) without
 *
 * 2. Tabulation (Bottom-Up):
 *    - Start from base cases, build up
 *    - Iterative with DP table
 *    - Space-optimizable
 *
 * 3. Space Optimization:
 *    - Use only necessary previous values
 *    - Instead of full DP array
 *
 * CLASSIC DP PROBLEMS:
 *
 * Fibonacci:
 * - Naive: O(2^n)
 * - Memoization: O(n)
 * - Tabulation: O(n)
 *
 * Coin Change:
 * - Time: O(amount * coins), Space: O(amount)
 * - DP[i] = min coins for amount i
 *
 * Longest Common Subsequence:
 * - Time: O(m * n), Space: O(m * n)
 * - dp[i][j] = LCS of first i and j characters
 *
 * 0/1 Knapsack:
 * - Time: O(n * capacity)
 * - dp[i][w] = max value using first i items, capacity w
 * - Each item used 0 or 1 time
 *
 * INTERVIEW TIPS:
 * • Identify overlapping subproblems
 * • Start with brute force, then optimize
 * • Practice both memoization and tabulation
 * • Know how to optimize space
 *
 * ============================================================================
 * 8. HASHING, BIT MANIPULATION, AND GREEDY
 * ============================================================================
 * File: HashingAndAdvanced.java
 *
 * HASHING:
 * - Hash Table: O(1) average, O(n) worst
 * - Use cases: Two sum, first unique character, duplicates
 *
 * COMMON HASH PROBLEMS:
 * - Two Sum: O(n) with map
 * - First Unique: O(n) with frequency map
 * - Valid Anagram: O(n) with character count
 * - Group Anagrams: O(n * k log k)
 *
 * BIT MANIPULATION:
 * Operations: &, |, ^, ~, <<, >>
 *
 * Important Tricks:
 * - a & (a-1): Remove rightmost 1 bit
 * - a ^ a = 0: XOR cancels equal numbers
 * - n << 1 = n * 2
 * - n >> 1 = n / 2
 *
 * Classic Problems:
 * - Power of 2: n > 0 && (n & (n-1)) == 0
 * - Count Ones: Use (n & (n-1)) to remove 1 bit each time
 * - Single Number: XOR all, duplicates cancel out
 *
 * GREEDY ALGORITHMS:
 * - Make locally optimal choice at each step
 * - Works when local optimal = global optimal
 *
 * Examples:
 * - Activity Selection: Always pick ending earliest
 * - Coin Change: Use largest denominations first (for standard coins)
 * - Huffman Coding: Always merge smallest frequencies
 *
 * INTERVIEW TIPS:
 * • Hashing is straightforward, practice implementation
 * • Bit manipulation can be tricky, focus on common patterns
 * • Greedy: Prove why greedy choice is safe
 *
 * ============================================================================
 * 9. COMPLEXITY ANALYSIS (BIG O NOTATION)
 * ============================================================================
 *
 * O(1) - Constant: Direct access, hash lookup
 * O(log n) - Logarithmic: Binary search, balanced BST
 * O(n) - Linear: Simple loop, linear search
 * O(n log n) - Linearithmic: Merge sort, quick sort avg
 * O(n²) - Quadratic: Nested loops, bubble sort
 * O(n³) - Cubic: Triple nested loops, Floyd-Warshall
 * O(2^n) - Exponential: Subsets, combinations
 * O(n!) - Factorial: Permutations
 *
 * For competitive programming:
 * • 10^6 operations: O(n) or O(n log n)
 * • 10^4 operations: O(n²)
 * • 10^3 operations: O(n³)
 *
 * ============================================================================
 * 10. INTERVIEW PREPARATION CHECKLIST
 * ============================================================================
 *
 * PRACTICE ROUTINE:
 * 1. Read the problem carefully, ask clarifying questions
 * 2. Think of multiple approaches (brute force to optimal)
 * 3. Dry run with examples to verify correctness
 * 4. Write clean, readable code
 * 5. Test with edge cases
 * 6. Optimize if needed
 * 7. Explain time and space complexity
 *
 * EDGE CASES TO ALWAYS CONSIDER:
 * • Empty input (array, list, string)
 * • Single element
 * • Duplicates
 * • Negative numbers
 * • Very large numbers (overflow)
 * • Null pointers
 *
 * THINGS TO MEMORIZE:
 * • Time complexities of common algorithms
 * • Common patterns (two-pointer, sliding window, etc.)
 * • When to use which data structure
 * • Space-time tradeoffs
 *
 * ============================================================================
 * 11. HOW TO USE THIS WORKSPACE
 * ============================================================================
 *
 * STEP 1: Run Each File
 * - Each file has a main() method with examples
 * - Run to see output and understand concepts
 *
 * STEP 2: Read Comments and Dry Runs
 * - Each function has detailed explanation
 * - DRY RUN shows step-by-step execution
 *
 * STEP 3: Modify and Experiment
 * - Change input values to test different cases
 * - Add more examples
 * - Try to implement variations
 *
 * STEP 4: Practice Coding
 * - Try writing algorithms from scratch
 * - Don't copy-paste, type it yourself
 * - Test your implementations
 *
 * STEP 5: Time Yourself
 * - Practice solving under time pressure
 * - Start with 60 minutes per problem
 * - Gradually reduce time as you improve
 *
 * ============================================================================
 * 12. RECOMMENDED PROBLEM SOURCES
 * ============================================================================
 *
 * LeetCode:
 * - Start with Easy problems
 * - Then Medium
 * - Finally Hard (once confident)
 *
 * Topics to Master in Order:
 * 1. Arrays (Easy: 30 problems)
 * 2. Linked Lists (Easy: 20 problems)
 * 3. Stacks/Queues (Easy: 20 problems)
 * 4. Hashing (Easy: 15 problems)
 * 5. Trees (Easy: 25 problems)
 * 6. Sorting (Easy: 10 problems)
 * 7. DP (Medium: 50+ problems)
 * 8. Graphs (Medium: 30+ problems)
 * 9. Bit Manipulation (Easy: 10 problems)
 *
 * ============================================================================
 * 13. FINAL TIPS FOR SUCCESS
 * ============================================================================
 *
 * 1. CONSISTENCY: Study 1-2 hours daily, every day
 * 2. UNDERSTAND: Don't memorize, understand concepts
 * 3. PRACTICE: Solve many problems, multiple approaches
 * 4. CODE: Write code, don't just think
 * 5. ANALYZE: Always analyze complexity
 * 6. OPTIMIZE: Try to improve solutions
 * 7. COMMUNICATE: Explain your approach clearly
 * 8. TEST: Test with multiple examples
 * 9. LEARN: Learn from mistakes, review wrong solutions
 * 10. BELIEVE: You can crack it with consistent effort!
 *
 * ============================================================================
 * GOOD LUCK WITH YOUR INTERVIEW PREPARATION!
 * ============================================================================
 */

public class DSAComprehensiveGuide {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("         COMPREHENSIVE DSA INTERVIEW PREPARATION GUIDE");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Welcome to the complete DSA learning resource!");
        System.out.println();
        System.out.println("📁 WORKSPACE STRUCTURE:");
        System.out.println("─────────────────────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("1. ArraysAndStrings.java");
        System.out.println("   ├─ Two-Pointer Technique");
        System.out.println("   ├─ Two Sum Problem");
        System.out.println("   ├─ Longest Substring Without Repeating");
        System.out.println("   ├─ Array Reversal");
        System.out.println("   └─ Palindrome Checking");
        System.out.println();

        System.out.println("2. SearchingAndSorting.java");
        System.out.println("   ├─ Binary Search");
        System.out.println("   ├─ Bubble Sort");
        System.out.println("   ├─ Selection Sort");
        System.out.println("   ├─ Insertion Sort");
        System.out.println("   ├─ Merge Sort");
        System.out.println("   └─ Quick Sort");
        System.out.println();

        System.out.println("3. LinkedLists.java");
        System.out.println("   ├─ Insert at Beginning/End");
        System.out.println("   ├─ Delete Node");
        System.out.println("   ├─ Reverse List");
        System.out.println("   ├─ Detect Cycle");
        System.out.println("   └─ Merge Sorted Lists");
        System.out.println();

        System.out.println("4. StacksAndQueues.java");
        System.out.println("   ├─ Stack Implementation");
        System.out.println("   ├─ Queue Implementation");
        System.out.println("   ├─ Balanced Parentheses");
        System.out.println("   ├─ Reverse String");
        System.out.println("   └─ Evaluate Postfix Expression");
        System.out.println();

        System.out.println("5. Trees.java");
        System.out.println("   ├─ Tree Traversals (Inorder, Preorder, Postorder, Level Order)");
        System.out.println("   ├─ BST Operations (Insert, Search)");
        System.out.println("   ├─ Tree Height");
        System.out.println("   ├─ Is Balanced Tree");
        System.out.println("   └─ Lowest Common Ancestor");
        System.out.println();

        System.out.println("6. Graphs.java");
        System.out.println("   ├─ DFS (Depth-First Search)");
        System.out.println("   ├─ BFS (Breadth-First Search)");
        System.out.println("   ├─ Dijkstra's Algorithm");
        System.out.println("   └─ Topological Sort");
        System.out.println();

        System.out.println("7. RecursionAndDP.java");
        System.out.println("   ├─ Fibonacci (Recursive, Memoization, Tabulation)");
        System.out.println("   ├─ Coin Change");
        System.out.println("   ├─ Longest Common Subsequence");
        System.out.println("   └─ 0/1 Knapsack");
        System.out.println();

        System.out.println("8. HashingAndAdvanced.java");
        System.out.println("   ├─ Two Sum");
        System.out.println("   ├─ First Unique Character");
        System.out.println("   ├─ Valid Anagram");
        System.out.println("   ├─ Bit Manipulation Tricks");
        System.out.println("   ├─ Greedy Algorithms");
        System.out.println("   └─ Activity Selection");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("🎯 HOW TO USE THIS WORKSPACE:");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("STEP 1: Run each Java file individually");
        System.out.println("        → See working examples and expected outputs");
        System.out.println();
        System.out.println("STEP 2: Read all comments and dry runs");
        System.out.println("        → Understand step-by-step execution");
        System.out.println();
        System.out.println("STEP 3: Analyze time and space complexity");
        System.out.println("        → Understand why each approach is used");
        System.out.println();
        System.out.println("STEP 4: Modify inputs and test edge cases");
        System.out.println("        → Verify correctness with different scenarios");
        System.out.println();
        System.out.println("STEP 5: Practice implementing from scratch");
        System.out.println("        → Don't copy-paste, code it yourself");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("⏱️ RECOMMENDED LEARNING TIMELINE:");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Week 1: Arrays, Strings, Searching & Sorting");
        System.out.println("Week 2: Linked Lists");
        System.out.println("Week 3: Stacks & Queues");
        System.out.println("Week 4: Trees");
        System.out.println("Week 5: Graphs");
        System.out.println("Week 6: Recursion & Dynamic Programming");
        System.out.println("Week 7: Hashing & Advanced Concepts");
        System.out.println("Week 8+: Practice problems on LeetCode");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("🎉 YOU'VE GOT THIS! GOOD LUCK WITH YOUR INTERVIEW PREPARATION!");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
    }
}

