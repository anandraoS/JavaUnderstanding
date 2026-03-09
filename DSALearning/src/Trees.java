/**
 * TREES - Hierarchical Data Structure
 *
 * Basic Definitions:
 * - Root: Top node with no parent
 * - Leaf: Node with no children
 * - Height: Maximum distance from root to leaf
 * - Depth: Distance from root to a node
 *
 * Types:
 * - Binary Tree: Each node has at most 2 children
 * - Binary Search Tree (BST): Left < Parent < Right
 * - Balanced Trees: AVL, Red-Black (height difference ≤ 1)
 * - Trie: For string storage/search
 */

import java.util.*;

public class Trees {

    // ============ BINARY TREE NODE ============
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    // ============ TREE TRAVERSALS ============

    /**
     * INORDER TRAVERSAL (Left, Node, Right)
     * For BST, gives elements in sorted order!
     *
     * DRY RUN on tree:    2
     *                     / \
     *                    1   3
     *
     * inorder(2):
     *   inorder(1):
     *     inorder(null): return
     *     process 1
     *     inorder(null): return
     *   process 2
     *   inorder(3):
     *     inorder(null): return
     *     process 3
     *     inorder(null): return
     *
     * Result: [1, 2, 3] ✓ (Sorted!)
     *
     * Time: O(n), Space: O(h) where h = height
     */
    public static void inorder(TreeNode root, List<Integer> result) {
        if (root == null) return;

        inorder(root.left, result);
        result.add(root.val);
        inorder(root.right, result);
    }

    /**
     * PREORDER TRAVERSAL (Node, Left, Right)
     * Used for copying tree
     *
     * DRY RUN on same tree:
     *
     * preorder(2):
     *   process 2
     *   preorder(1):
     *     process 1
     *     preorder(null), preorder(null)
     *   preorder(3):
     *     process 3
     *     preorder(null), preorder(null)
     *
     * Result: [2, 1, 3]
     *
     * Time: O(n), Space: O(h)
     */
    public static void preorder(TreeNode root, List<Integer> result) {
        if (root == null) return;

        result.add(root.val);
        preorder(root.left, result);
        preorder(root.right, result);
    }

    /**
     * POSTORDER TRAVERSAL (Left, Right, Node)
     * Used for deleting tree
     *
     * DRY RUN:
     *
     * postorder(2):
     *   postorder(1):
     *     process 1
     *   postorder(3):
     *     process 3
     *   process 2
     *
     * Result: [1, 3, 2]
     *
     * Time: O(n), Space: O(h)
     */
    public static void postorder(TreeNode root, List<Integer> result) {
        if (root == null) return;

        postorder(root.left, result);
        postorder(root.right, result);
        result.add(root.val);
    }

    /**
     * LEVEL ORDER TRAVERSAL (BFS)
     * Using Queue
     *
     * DRY RUN on tree:    1
     *                     / \
     *                    2   3
     *                   / \
     *                  4   5
     *
     * queue = [1]
     *
     * Dequeue 1: result=[1], enqueue 2,3 -> queue=[2,3]
     * Dequeue 2: result=[1,2], enqueue 4,5 -> queue=[3,4,5]
     * Dequeue 3: result=[1,2,3], no children -> queue=[4,5]
     * Dequeue 4: result=[1,2,3,4], no children -> queue=[5]
     * Dequeue 5: result=[1,2,3,4,5], no children -> queue=[]
     *
     * Result: [1, 2, 3, 4, 5]
     *
     * Time: O(n), Space: O(max_width)
     */
    public static List<Integer> levelOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            result.add(node.val);

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }

        return result;
    }

    // ============ BINARY SEARCH TREE OPERATIONS ============

    /**
     * BST INSERTION
     * Property: Left < Node < Right
     *
     * DRY RUN: Insert 4 into BST with root 3
     *              3
     *             / \
     *            1   5
     *
     * insert(3, 4):
     *   4 > 3, go right (3.right = 5)
     *   insert(5, 4):
     *     4 < 5, go left (5.left = null)
     *     Create new node, 5.left = 4
     *
     * Result:
     *        3
     *       / \
     *      1   5
     *         /
     *        4
     *
     * Time: O(log n) average, O(n) worst (skewed tree)
     */
    public static TreeNode insert(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insert(root.left, val);
        } else if (val > root.val) {
            root.right = insert(root.right, val);
        }

        return root;
    }

    /**
     * BST SEARCH
     *
     * DRY RUN: Search for 4 in BST
     *        3
     *       / \
     *      1   5
     *         /
     *        4
     *
     * search(3, 4):
     *   4 > 3, go right (search 5)
     *   search(5, 4):
     *     4 < 5, go left (search 4)
     *     search(4, 4):
     *       4 == 4, FOUND! return true
     *
     * Time: O(log n) average, O(n) worst
     */
    public static boolean search(TreeNode root, int val) {
        if (root == null) {
            return false;
        }

        if (val == root.val) {
            return true;
        } else if (val < root.val) {
            return search(root.left, val);
        } else {
            return search(root.right, val);
        }
    }

    /**
     * HEIGHT OF TREE
     *
     * DRY RUN:
     *        1          <- height = 2
     *       / \
     *      2   3        <- height = 1
     *     /
     *    4              <- height = 0 (leaf)
     *
     * height(4): 1 + max(-1, -1) = 0
     * height(2): 1 + max(0, -1) = 1
     * height(3): 1 + max(-1, -1) = 0
     * height(1): 1 + max(1, 0) = 2
     *
     * Result: 2
     *
     * Time: O(n), Space: O(h)
     */
    public static int height(TreeNode root) {
        if (root == null) {
            return -1;
        }

        return 1 + Math.max(height(root.left), height(root.right));
    }

    /**
     * IS BALANCED TREE?
     * A tree is balanced if height difference of left and right ≤ 1
     *
     * DRY RUN:
     * Balanced:
     *      1
     *     / \
     *    2   3
     *   /
     *  4
     *
     * height(4) = 0
     * height(2) = 1, balanced (|0 - (-1)| = 1)
     * height(3) = 0
     * height(1) = 2, balanced (|1 - 0| = 1)
     * Result: BALANCED ✓
     *
     * Not balanced:
     *      1
     *     /
     *    2
     *   /
     *  3
     * height(3) = 0
     * height(2) = 1, balanced
     * height(1) = 2, NOT balanced (|1 - (-1)| = 2 > 1)
     * Result: NOT BALANCED ✗
     *
     * Time: O(n), Space: O(h)
     */
    public static boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }

        int leftHeight = height(root.left);
        int rightHeight = height(root.right);

        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        }

        return isBalanced(root.left) && isBalanced(root.right);
    }

    /**
     * LOWEST COMMON ANCESTOR (LCA)
     * Find the deepest node that has both p and q as descendants
     *
     * DRY RUN:
     *        3
     *       / \
     *      5   1
     *     / \  / \
     *    6  2 0  8
     *      / \
     *     7   4
     *
     * Find LCA of 5 and 4:
     *
     * lca(3, 5, 4):
     *   5 < 3? No. 4 < 3? No.
     *   Both are in different subtrees? 5 in left, 4 in right? YES!
     *   Return 3 (common ancestor found)
     *
     * Find LCA of 2 and 4:
     *
     * lca(3, 2, 4):
     *   2 < 3? Yes, search in left
     *   lca(5, 2, 4):
     *     2 < 5? Yes, search in left
     *     lca(6, 2, 4): Both not found
     *     Back to searching in right of 5
     *     lca(2, 2, 4):
     *       2 == 2? YES! Return 2
     *
     * Result: 2
     *
     * Time: O(log n) in balanced BST, O(n) worst
     */
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }

        // Both p and q are smaller, LCA is in left
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }

        // Both p and q are larger, LCA is in right
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }

        // One on each side, or one is root itself
        return root;
    }

    // ============ TREE CONSTRUCTION ============

    public static void main(String[] args) {
        System.out.println("===== TREES =====\n");

        // Create example tree:    2
        //                         / \
        //                        1   3
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);

        System.out.println("--- Tree Traversals ---");
        List<Integer> inorderResult = new ArrayList<>();
        inorder(root, inorderResult);
        System.out.println("Inorder (Left-Node-Right): " + inorderResult);

        List<Integer> preorderResult = new ArrayList<>();
        preorder(root, preorderResult);
        System.out.println("Preorder (Node-Left-Right): " + preorderResult);

        List<Integer> postorderResult = new ArrayList<>();
        postorder(root, postorderResult);
        System.out.println("Postorder (Left-Right-Node): " + postorderResult);

        List<Integer> levelOrderResult = levelOrder(root);
        System.out.println("Level Order (BFS): " + levelOrderResult);
        System.out.println();

        // BST Operations
        System.out.println("--- BST Operations ---");
        TreeNode bst = new TreeNode(3);
        bst = insert(bst, 1);
        bst = insert(bst, 5);
        bst = insert(bst, 0);
        bst = insert(bst, 2);
        bst = insert(bst, 4);
        bst = insert(bst, 6);

        System.out.println("BST (Inorder): ");
        List<Integer> bstInorder = new ArrayList<>();
        inorder(bst, bstInorder);
        System.out.println(bstInorder);

        System.out.println("Search 2: " + search(bst, 2));
        System.out.println("Search 10: " + search(bst, 10));
        System.out.println();

        // Tree Height and Balance
        System.out.println("--- Tree Properties ---");
        System.out.println("Height of BST: " + height(bst));
        System.out.println("Is BST balanced: " + isBalanced(bst));
        System.out.println();

        // LCA
        System.out.println("--- Lowest Common Ancestor ---");
        TreeNode p = bst.left;  // Node 1
        TreeNode q = bst.right; // Node 5
        TreeNode lca = lowestCommonAncestor(bst, p, q);
        System.out.println("LCA of " + p.val + " and " + q.val + ": " + lca.val);

        p = bst.left;       // Node 1
        q = bst.left.right; // Node 2
        lca = lowestCommonAncestor(bst, p, q);
        System.out.println("LCA of " + p.val + " and " + q.val + ": " + lca.val);
    }
}

