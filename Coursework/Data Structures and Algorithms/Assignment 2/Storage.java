/*
Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
   - Implementing AVL Tree: Self-balancing BST data structure.
   - Basic rule: for any node, The absolute difference between the height of its left and right subtrees can't exceed one.
*/

class AVL<T extends Comparable<T>> { // Generic, will work with any comparable type.

    private Node root;    // Holds the root node that we need to begin recursion.
    T pre, suc;

    private class Node {  // AVL Tree Node
        T item;           // The data stored in the node
        Node left, right; // Links to it's left and right child
        int h = 1;        // height, holds the size of the subtree rooted at this node, its 1 by default.
    }

    // The purpose of the following overloaded methods is to keep the root private, so that the client can't modify it by mistake.

    // The overloaded method available for the client, it calls the private recursive method for insertion.
    void insert(T e) { // O( height(root) ) = O( 2 * log(n) ) = O( log(n) )
        root = insert(e, root);
    }

    // Calls the remove method
    void remove(T e) { // O( log(n) )
        root = remove(e, root);
    }

    // Just for testing, this method does a preOrder traversal on the tree and outputs the data stored in its nodes.
    void preOrder() { // O(n)
        preOrder(root); // Calls the overloaded recursive method that will traverse the tree and does the output.
        System.out.println("");
    }

    // Calls the pred_succ private method, which will determine the inorder predecessor and successor of a given element.
    void pred_succ(T e) { // O( log(n) )
        pred_succ(e, root);
    }

    // Calculates the inorder predecessor & successor for a given element and assign the results to pre and suc class properties.
    /* Basic idea:
       Successor -> go right then find min, if you can't go right, set successor to be the item and recurse.
       Predecessor -> go left then find max, if you can't go left, set predecessor to be the item and recurse.
    */
    private void pred_succ(T e, Node i) { // O( log(n) )
        if (i == null) return;
        if (i.item == e) {
            if (i.left != null) pre = SubTreeMax(i.left).item;
            if (i.right != null) suc = SubTreeMin(i.right).item;
            return;
        }
        if (e.compareTo(i.item) > 0) {
            pre = i.item;
            pred_succ(e, i.right);
        } else if (e.compareTo(i.item) < 0) {
            suc = i.item;
            pred_succ(e, i.left);
        }
    }

    // Returns the right-most element in the subtree rooted at i.
    private Node SubTreeMax(Node i) { // O( log(n) )
        Node t = i;
        while (t.right != null) t = t.right;
        return t;
    }

    // Returns the left-most element in the subtree rooted at i.
    private Node SubTreeMin(Node i) { // O( log(n) )
        Node t = i;
        while (t.left != null) t = t.left;
        return t;
    }

    // The basic search functionality in the tree, Not needed because search is not the goal here, but it's a subtask.

    Node search(T e) {
        return search(e, root);
    }
    private Node search(T e, Node i) {
        if (i == null) return null;
        if (e.compareTo(i.item) < 0) {
            return search(e, i.left);
        } else if (e.compareTo(i.item) > 0) {
            return search(e, i.right);
        } else return i;
    }

    // Internal recursive insert method for adding new elements to the tree.
    // At first we search for the element, when we reach the null link, we insert the element there.
    // Then we check that the tree is still an AVL tree, if not, we balance it by rotations.

    private Node insert(T e, Node i) { // O( log(n) )

        if (i == null) { // Base Case, When we reach a null link, we insert the normal way.
            i = new Node();
            i.item = e;
            return i;
        }

        // Either the element we are inserting is less than the element stored in the current node, so we go left.
        if (e.compareTo(i.item) < 0) {
            i.left = insert(e, i.left);
        }
        // Or the element we are inserting is greater than the element stored in the current node, so we go right.
        else if (e.compareTo(i.item) > 0) {
            i.right = insert(e, i.right);
        }

        // This is not supposed to happen. Because tree doesn't allow duplicates.
        else {
            // If we get a duplicated element, we return the node containing it and do nothing.
            // We can also throw an error or activate a flag, or increase a counter.
            // Depending on the requirements of implementation.
            return i;
        }

        // Updating the height of the node i after insertion of e.
        i.h = Math.max(height(i.left), height(i.right)) + 1;

        // Calculating the balance of the current node, difference between its left and right subtree.
        // If they differ by more than one, a tree is not AVL and needs to be rotated.
        int b = height(i.left) - height(i.right);

        // If the left subtree is heavier that the right.
        if (b > 1) {
            if (e.compareTo(i.left.item) < 0)        // Left-Left Case
                return rightR(i);                    // We right-rotate to achieve balance.
            else if (e.compareTo(i.left.item) > 0) { // Left-Right Case
                i.left = leftR(i.left);              // We left-rotate to convert to left-left case.
                return rightR(i);                    // Then we right-rotate
            }
        } else if (b < -1) {
            if (e.compareTo(i.right.item) < 0) {     // Right-Left Case
                i.right = rightR(i.right);           // We right-rotate to convert to right-right case
                return leftR(i);                     // Then we left-rotate.
            }
            if (e.compareTo(i.right.item) > 0) {     // Right-Right Case
                return leftR(i);                     // We left-rotate to achieve balance.
            }
        }
        return i; // If the tree didn't need a balance, we'll return the node without modification.
    }

    // Internal recursive remove method for deleting an element from the tree.
    // At first we search for the element, if we didn't find it, we return null.
    // If we found the element, we have three cases (you can say 4), we handle each one separately.
    // Then we check that the tree is still an AVL tree, if not, we balance it by rotations.

    private Node remove(T e, Node i) { // O( log(n) )
        if (i == null) return null;
        // Either the element we are searching for is less than the element stored in the current node, so we go left.
        if (e.compareTo(i.item) < 0) {
            i.left = remove(e, i.left);
        }
        // Or the element we are searching for is greater than the element stored in the current node, so we go right.
        else if (e.compareTo(i.item) > 0) {
            i.right = remove(e, i.right);
        }
        // Otherwise, we found the element we need to delete.
        else {
            if (i.left == null && i.right == null) { // Node has no children, we can safely assign it to null.
                return null;
            } else if (i.right == null) { // Node has no right children, to delete it we replace it with its left child.
                return i.left;
            } else if (i.left == null) {  // Node has no left children, to delete it we replace it with its right child.
                return i.right;
            } else {                                // Node has both, left and right children
                T pre = SubTreeMax(i.left).item;    // We find its predecessor from in-order traversal (Successor is also ok).
                i.item = pre;                       // We replace the node with its predecessor
                i.left = remove(pre, i.left);       // We search for that predecessor and delete it.
                // predecessor can only be in the left subtree, that's why we don't need to do a full search from the root.
            }
        }

        // Updating the height of the node i after removing e.
        i.h = Math.max(height(i.left), height(i.right)) + 1;
		
        // Now we need to re-balance if AVL property is changed.
		
		// Calculating the balance of the current node, difference between its left and right subtree.
        // If they differ by more that one, a tree is not AVL and needs to be rotated.
        int b = height(i.left) - height(i.right);

        // If the left subtree is heavier that the right.
        if (b > 1) {
            if (height(i.left.left) >= height(i.left.right))     // Left-Left Case
                return rightR(i);                                // We right-rotate to achieve balance.
            else {                                               // Left-Right Case
                i.left = leftR(i.left);                          // We left-rotate to convert to left-left case.
                return rightR(i);                                // Then we right-rotate
            }
        } else if (b < -1) {
            if (height(i.right.left) >= height(i.right.right)) { // Right-Left Case
                i.right = rightR(i.right);                       // We right-rotate to convert to right-right case
                return leftR(i);                                 // Then we left-rotate.
            } else {                                             // Right-Right Case
                return leftR(i);                                 // We left-rotate to achieve balance.
            }
        }
        return i; // If the tree didn't need a balance, we'll return the node without modification.
    }

    // A method to left-rotate a node, changing some links.
    private Node leftR(Node i) { // O(1)
        // System.out.println("L " + i.item); // For testing
        /*
               i                     r
              / \     leftR(i)      / \
             a   r    -------->    i   b
                / \               / \
               l   b             a   l
         */

        // The above diagram explains the next 4 lines.
        Node r = i.right;
        Node l = r.left;
        r.left = i;
        i.right = l;

        // Updating the heights.
        i.h = Math.max(height(i.left), height(i.right)) + 1;
        r.h = Math.max(height(r.left), height(r.right)) + 1;
        return r; // returning the new subtree root after rotation.
    }

    private Node rightR(Node i) {
        // System.out.println("R " + i.item); // For testing

        /*
            i                      l
           / \     rightR(i)      / \
          l   a   ---------->    b   i
         / \                    / \
        b   r                  r   a

       */

        // The above diagram explains the next 4 lines.
        Node l = i.left;
        Node r = l.right;
        l.right = i;
        i.left = r;

        // Updating the heights.
        i.h = Math.max(height(i.left), height(i.right)) + 1;
        l.h = Math.max(height(l.left), height(l.right)) + 1;

        return l; // returning the new subtree root after rotation.
    }

    // To return the height of a given node, if null passed, returns zero.
    private int height(Node n) { // O(1)
        if (n == null) return 0;
        return n.h;
    }

    // For testing, this method does a preOrder traversal on the AVL.
    private void preOrder(Node i) { // O(n)
        if (i == null) return;
        System.out.print(i.item + " ");
        preOrder(i.left);
        preOrder(i.right);
    }
}

public class Main {
    public static void main(String[] args) {

        AVL<Integer> t = new AVL<>();

        /*
        Testing: inserting (6, 5, 4, 3, 2, 1) will result in the following trees.

                    6                   5                    5                    3
                   /    rightR(6)      / \    rightR(4)     / \    rightR(5)     / \
                  5    ---------->    4   6   -------->    3   6   -------->    2   5
                 /                   /                    / \                  /   / \
                4                   3                    2   4                1   4   6
                                   /                    /
                                  2                    1
              balance(6) = 2     balance(4) = 2       balance(5) = 2        AVL-Balanced

         */

        t.insert(6);
        t.insert(5);
        t.insert(4);
        t.insert(3);
        t.insert(2);
        t.insert(1);
        t.preOrder();

        // Testing predecessor and successor method.
        t.pred_succ(3);
        System.out.println("pre(3) = " + t.pre);
        System.out.println("suc(3) = " + t.suc);


        /*
        Testing: Removing random elements from the tree

                3                       3                      3                     4
               / \    remove(2)        / \     remove(6)      / \     remove(3)     / \
              2   5  ---------->      1   5   ---------->    1   5   ---------->   1   5
             /   / \                     / \                    /
            1   4   6                   4   6                  4

         */

        t.remove(2);
        t.preOrder();
        t.remove(6);
        t.preOrder();
        t.remove(3);
        t.preOrder();
    }
}