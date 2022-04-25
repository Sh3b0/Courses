class BST<T extends Comparable<T>> {
// TODO: Implement methods: floor, ceil, rank, SubTreeSize(x) [of the subtree rooted at x].
// TODO: Complete the implementation of remove method.

    private Object[] tree;
    private int cap;
    int size = 0;

    BST() {
        size = 0;
        cap = 1;
        tree = new Object[cap];
    }

    void insert(T e) { insert(e, 1); }
    void remove(T e) { remove(e, 1); }
    boolean search(T e) { return search(e, 1); }
    T min() { return min(1); }
    T max() { return max(1); }
    void preOrder(){ preOrder(1); }
    void postOrder(){ postOrder(1); }
    void inOrder(){ inOrder(1); }

    private void insert(T e, int i) { // Element, Index
        if (i >= cap) extend();
        if (tree[i] == null) {
            tree[i] = e;
            size++;
        } else {
            if (e.compareTo((T) tree[i]) < 0) insert(e, 2 * i);
            else insert(e, 2 * i + 1);
        }
    }

    private void extend() {
        Object[] tmp = new Object[cap * 2];
        for (int i = 0; i < cap; i++)
            tmp[i] = tree[i];
        tree = tmp;
        cap *= 2;
    }

    private void remove(T e, int i) { // TODO: Complete the implementation.
        if (i >= cap || tree[i] == null) return;
        if (tree[i] == e) { // found
            // set tree[i] = suc(i) or pred(i) from inorder traversal
            // delete suc(i) or pred(i)
            size--;
        }
        if (e.compareTo((T) tree[i]) < 0) remove(e, i * 2);
        else remove(e, i * 2 + 1);
    }

    private boolean search(T e, int i) {
        if (i >= cap || tree[i] == null) return false;
        if (tree[i] == e) return true;
        if (e.compareTo((T) tree[i]) < 0) return search(e, i * 2);
        else return search(e, i * 2 + 1);
    }

    private T min(int i) {
        if (i * 2 >= cap || tree[i * 2] == null) return (T) tree[i];
        return min(2 * i);
    }

    private T max(int i) {
        if (i * 2 >= cap || tree[i * 2] == null) return (T) tree[i];
        return max(2 * i + 1);
    }

	// Remove comments to prevent null nodes printing
    
	private void preOrder(int i) {
        if (i >= cap /*|| tree[i] == null*/) return;
        System.out.print(tree[i] + " ");
        preOrder(i * 2);
        preOrder(i * 2 + 1);
    }

    private void postOrder(int i) { 
        if (i >= cap /*|| tree[i] == null*/) return;
        postOrder(i * 2);
        postOrder(i * 2 + 1);
        System.out.print(tree[i] + " ");
    }

    private void inOrder(int i) {
        if (i >= cap /*|| tree[i] == null*/) return;
        inOrder(i * 2);
        System.out.print(tree[i] + " ");
        inOrder(i * 2 + 1);
    }
}

public class Main {
    public static void main(String[] args) {
        BST<Integer> bst = new BST<>();
        /*   Builds this tree. n represents null.
                        5
                     /     \
                    2       8
                   / \     / \
                  1   3   7   9
                 / \ / \ / \ / \
                n  n n 4 6 n n  n
        */

        bst.insert(5);
        bst.insert(8);
        bst.insert(9);
        bst.insert(2);
        bst.insert(1);
        bst.insert(7);
        bst.insert(3);
        bst.insert(6);
        bst.insert(4);

        System.out.println("\nPre-order traversal:");
        bst.preOrder();
        System.out.println("\nPost-order traversal:");
        bst.postOrder();
        System.out.println("\nIn-order traversal:");
        bst.inOrder();
        System.out.println("\nTree has " + bst.size + " elements");

        System.out.println("Searching for 5: " + bst.search(5));
        System.out.println("Searching for 11: " + bst.search(11));

        System.out.println("Tree min is " + bst.min());
        System.out.println("Tree max is " + bst.max());
    }
}