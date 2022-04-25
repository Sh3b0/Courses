/*
Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
   - Implementing Sweep Line Algorithm for solving the problem of Intersecting Segments:
        Given a list of segments, determine if any two of them intersects and output any intersecting pair.
   - Code uses:
        1- Class MergeSorter for sorting points.
        2- Class Queue (a modified queue that allows iterating) for storing elements while sorting.
        3- Class AVL (A Self-Balancing BST) for storing the Segments and allow fast insertion and removals.
        4- Class Point for manipulating points in 2D Cartesian coordinate system.
        5- Class Segment for manipulating segments and calculate intersections.
        6- Class FastReader for reading from standard input, because Scanner is not fast enough to pass time limits.
            - Implementation of class FastReader is taken from: https://www.geeksforgeeks.org/fast-io-in-java-in-competitive-programming/
        IMPORTANT NOTE: FastReader class was not a part of the assignment that's why I copied the implementation from the link above.
              ALL other classes are my own implementation.
	- Link to Accepted submission on CodeForces: https://codeforces.com/group/3ZU2JJw8vQ/contest/272963/submission/74142834
*/

// Packages used by FastReader.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class SweepLine { // Main class
    public static void main(String[] args) {

        // Initializing
        FastReader sc = new FastReader();
        int n = sc.nextInt(), x1, y1, x2, y2, c = 0;
        Point[] p = new Point[n * 2];
        Segment[] s = new Segment[n];

        // Reading and storing input.
        for (int i = 0; i < n * 2; i += 2) {
            x1 = sc.nextInt();
            y1 = sc.nextInt();
            x2 = sc.nextInt();
            y2 = sc.nextInt();

            s[i / 2] = new Segment(x1, y1, x2, y2);
            p[i] = new Point(x1, y1);
            p[i + 1] = new Point(x2, y2);

            // Link each point to its corresponding segment.
            p[i].seg = s[i / 2];
            p[i + 1].seg = s[i / 2];

            // To mark left/right points.
            if (x1 < x2) {
                p[i].flag = true;
            } else if (x1 > x2) {
                p[i].flag = false;
            } else {
                p[i].flag = y1 < y2;
            }

            p[i + 1].flag = !p[i].flag; // if one point is left, the other one is right and vice-versa.
        }

        // Sorting points using MergeSort algorithm.
        MergeSorter<Point> ms = new MergeSorter<>();
        for (Queue<Point>.Node i = ms.sort(p).front; i != null; i = i.next) p[c++] = i.item;

        // Testing
        // for (Point i : p) System.out.println(i.x + " " + i.y);

        // Creating an AVL tree of Segments.
        AVL<Segment> t = new AVL<>();

        for (int i = 0; i < 2 * n; i++) { // Looping over all points.

            Segment cur = p[i].seg; // To hold the segment we are 'cur'rently processing.
            t.pred_succ(cur);       // Calculates segment predecessor and successor.
            Segment pre = t.pre;
            Segment suc = t.suc;

            // System.out.println("Now processing segment " + cur + ", pre = " + pre + ", suc = " + suc);

            if (p[i].flag) { // If the point is flagged left, we insert it in the AVL

                // System.out.println("Inserting");
                t.insert(cur);

                // Check if the current segment intersects with its predecessor
                if (pre != null && pre != cur && cur.intersect(pre)) {
                    System.out.println("INTERSECTION");
                    System.out.println(cur.Xa + " " + cur.Ya + " " + cur.Xb + " " + cur.Yb);
                    System.out.println(pre.Xa + " " + pre.Ya + " " + pre.Xb + " " + pre.Yb);
                    return;
                }

                // Check if the current segment intersects with its successor
                if (suc != null && suc != cur && cur.intersect(suc)) {
                    System.out.println("INTERSECTION");
                    System.out.println(cur.Xa + " " + cur.Ya + " " + cur.Xb + " " + cur.Yb);
                    System.out.println(suc.Xa + " " + suc.Ya + " " + suc.Xb + " " + suc.Yb);
                    return;
                }
            } else {

                // Check if the current segment predecessor intersects with its predecessor
                if (pre != null && suc != null && pre != suc && pre.intersect(suc)) {
                    System.out.println("INTERSECTION");
                    System.out.println(pre.Xa + " " + pre.Ya + " " + pre.Xb + " " + pre.Yb);
                    System.out.println(suc.Xa + " " + suc.Ya + " " + suc.Xb + " " + suc.Yb);
                    return;
                }

                // System.out.println("Removing");
                t.remove(cur);
            }
        }
        // If all points are processed and the program didn't terminate, then no intersections detected.
        System.out.println("NO INTERSECTIONS");
    }
}

// A replacement of Scanner, to allow faster I/O for codeforces testing.
class FastReader {
    BufferedReader br;
    StringTokenizer st;

    public FastReader() {
        br = new BufferedReader(new
                InputStreamReader(System.in));
    }

    String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }
}

class Point implements Comparable<Point> {

    int x, y;
    boolean flag; // true if point is left in its segment, false if it's right.
    Segment seg;  // Holds a reference to the segment which this point belong to.

    Point(int x, int y) { // Constructor
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Point that) { // To enable sorting points according to their x axis.
        if (this.x < that.x) return -1;
        else if (this.x > that.x) return 1;
        else {                         // Priority is for LEFT points.
            if (this.flag) return -1;
            else if (that.flag) return 1;
            else return 0;
        }
    }
}

class Segment implements Comparable<Segment> { // To manipulate line segments.
    long Xa, Ya, Xb, Yb; // Calculations can lead to long numbers.

    Segment(int Xa, int Ya, int Xb, int Yb) { // Constructor
        this.Xa = Xa;
        this.Ya = Ya;
        this.Xb = Xb;
        this.Yb = Yb;
    }

    @Override
    public int compareTo(Segment that) { // To allow sorting elements by their y coordinate
        if (this.Ya < that.Ya) return -1;
        else if (this.Ya > that.Ya) return 1;
        else {                           // Priority is for lower beginning segments.
            if (this.Xa < that.Xa) return -1;
            else if (this.Xa > that.Xa) return 1;
            else return 0;
        }
    }

    // Checks if a point (x, y) lies on the segment between (x1,y1), (x2,y2)
    // The function assumes that all points lies on the same line.
    private static boolean between(double x, double y, double x1, double y1, double x2, double y2) {
        return (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2));
    }

    // Returns true if 'this' segment intersects with the parameter segment 'that'.
    boolean intersect(Segment that) {
        // Applying the formula from task 1 description.
        long Xc = that.Xa, Xd = that.Xb, Yc = that.Ya, Yd = that.Yb;
        final double den = (Xa - Xb) * (Yc - Yd) - (Ya - Yb) * (Xc - Xd);
        final double nom1 = ((Xa * Yb - Ya * Xb) * (Xc - Xd) - (Xa - Xb) * (Xc * Yd - Yc * Xd));
        final double nom2 = ((Xa * Yb - Ya * Xb) * (Yc - Yd) - (Ya - Yb) * (Xc * Yd - Yc * Xd));
        double Xp, Yp;

        // Segments have the same slope, They are either parallel or coincident or they share an end.
        if (den == 0.0) {
            if (nom1 == 0 || nom2 == 0) {
                if ((Xa == Xc && Ya == Yc) || (Xa == Xd && Ya == Yd))
                    return true; // Segments share an end
                else if ((Xb == Xc && Yb == Yc) || (Xb == Xd && Yb == Yd))
                    return true; // Segments share an end
                else if (between(Xa, Ya, Xc, Yc, Xd, Yd) || between(Xb, Yb, Xc, Yc, Xd, Yd) || between(Xc, Yc, Xa, Ya, Xb, Yb) || between(Xd, Yd, Xa, Ya, Xb, Yb))
                    return true; // Segments are coincident
                else
                    return false; // Segments are parallel

            } else {
                return false; // Segments are parallel
            }
        } else { // Segments have different slope, Either they intersect or not, but they can't be parallel or coincident.
            Xp = nom1 / den;
            Yp = nom2 / den;

            // To ignore small errors
            if (Math.abs(Xp) < 0.001) Xp = 0.0;
            if (Math.abs(Yp) < 0.001) Yp = 0.0;

            // Check if the point of intersection lies on both of the segments.
            if (between(Xp, Yp, Xa, Ya, Xb, Yb) && between(Xp, Yp, Xc, Yc, Xd, Yd))
                return true; // Segments intersect at (Xp, Yp)
            else
                return false; // Lines intersect, but segments don't.
        }
    }
}

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
       Pred -> go left then find max, if you can't go right, set successor to be the item and recurse.
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

class MergeSorter<T extends Comparable<T>> {

    // The only method available for clients to use, It calls the internal sorting method of MergeSorter.
    Queue<T> sort(T[] t) {
        return sort(t, 0, t.length - 1);
    }

    // Merges two sorted queues l, r into a new queue mrg, then returns it.
    private Queue<T> merge(Queue<T> l, Queue<T> r) {
        Queue<T> mrg = new Queue<>();
        while (!l.isEmpty() && !r.isEmpty()) {        // While both of the queues are not empty.
            if (l.front().compareTo(r.front()) < 0) { // Push the smaller value first in the merged queue.
                mrg.push(l.front());
                l.pop();
            } else {
                mrg.push(r.front());
                r.pop();
            }
        }

        // If one of the queues is empty, we concatenate the other one into the resulting merged queue.
        while (!l.isEmpty()) {
            mrg.push(l.front());
            l.pop();
        }
        while (!r.isEmpty()) {
            mrg.push(r.front());
            r.pop();
        }

        // Finally we return the merged result.
        return mrg;
    }

    // Given an array of a generic type, left index, right index this function does the divide and conquer.
    private Queue<T> sort(T[] t, int l, int r) {

        // Base case when the array has only one element, we create a list containing only that element and returns it.
        // I used a queue for the list implementation because we don't need random access on elements.
        // Algorithm only needs appending at the end and removing from the beginning, The queue does the job perfectly.

        if (l == r) {
            Queue<T> tmp = new Queue<>();
            tmp.push(t[l]);
            return tmp;
        }

        int m = (l + r) / 2; // Divide the problem into sub-problems

        // Recursively solve the sub-problems, combining all solutions using the merge method then returning the final result.
        return merge(sort(t, l, m), sort(t, m + 1, r));
    }
}

class Queue<T> { // Implementing a Queue using a LinkedList that supports basic operations.

    Node front; // Not private to allow easy iteration.
    private Node back;
    private int size = 0;

    class Node { // Queue Node
        T item;
        Node next;
    }

    void push(T t) { // Add a new element to the end of the queue. O(1)
        Node cur = new Node();
        cur.item = t;
        if (size == 0) {
            front = new Node();
            back = new Node();
        }
        back.next = cur;
        back = cur;
        if (size == 0) {
            front = back;
        }
        size++;
    }

    boolean isEmpty() { // Checks if the queue is empty. O(1)
        return size == 0;
    }

    void pop() { // Deletes an element from the front of the queue. O(1)
        if (size == 0) throw new NullPointerException("Queue is empty");
        T t = front.item;
        front = front.next;
        size--;
        if (size == 0) {
            front = null;
            back = null;
        }
    }

    T front() { // Gets the value stored at the front of the queue O(1)
        return front.item;
    }

    T back() { // Gets the value stored at the back of the queue. O(1)
        return back.item;
    }
}