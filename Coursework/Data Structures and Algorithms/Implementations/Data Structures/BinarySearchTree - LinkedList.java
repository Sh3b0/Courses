class BST<T extends Comparable<T>> {
    private Node root, pre, suc;

    class Node {
        T item;
        Node left;
        Node right;
        int occ; // Multiplicity of the same item in the tree.
        int sss; // SubTreeSize (rooted at 'this')

        Node(T e) {
            this.item = e;
            occ = 1;
            sss = 1;
        }
    }

    void insert(T e) {
        if (root == null) root = new Node(e);
        else insert(root, e);
    }

    int search(T e) {
        return search(root, e);
    }

    void remove(T e) {
        remove(root, e);
    }

    T min_element() {
        return SubTreeMin(root).item;
    }

    T max_element() {
        return SubTreeMax(root).item;
    }

    void preOrder() {
        preOrder(root);
    }

    void postOrder() {
        postOrder(root);
    }

    void inOrder() {
        inOrder(root);
    }

    int size() {
        return SubTreeSize(root);
    }

    T floor(T e) {
        return floor(root, e);
    }

    T ceil(T e) {
        return ceil(root, e);
    }

    int rank(T e) {
        return rank(root, e);
    }

    // Number of Nodes having elements less that e.
    int rank(Node i, T e) {
        if (i == null) return 0;
        int cmp = e.compareTo(i.item);

        // Element is less than the current one, we go left.
        if (cmp < 0) return rank(i.left, e);

        // Element is greater than the current one and all its left subtree, then we try to find more by going right.
        else if (cmp > 0) return 1 + SubTreeSize(i.left) + rank(i.right, e);

        // Element is same as current, rank is size of its left subtree.
        else return SubTreeSize(i.left);
    }

    private T floor(Node i, T e) {     // Returns largest key <= e
        if (i == null) return null;
        int cmp = e.compareTo(i.item);
        if (cmp < 0) {                 // Floor cannot be this one, it can be present in left subtree.
            return floor(i.left, e);
        } else if (cmp > 0) {          // Floor could be this one, or it could be to the right of it.
            T f = floor(i.right, e);
            if (f != null) return f;
            else return i.item;
        } else                         // Element is a floor of itself.
            return e;

    }

    private T ceil(Node i, T e) {       // Returns smallest key >= e
        if (i == null) return null;
        int cmp = e.compareTo(i.item);
        if (cmp < 0) {                  // Ceil could be this one, it could be on the left.
            T c = ceil(i.left, e);
            if (c != null) return c;
            else return i.item;
        } else if (cmp > 0) {           // Ceil cannot be this one, it can be present in left subtree.
            return ceil(i.right, e);
        } else
            return e;                   // Element is a ceil of itself.
    }

    private void preOrder(Node i) {
        if (i == null) return;
        System.out.print(i.item + "(" + i.occ + ") ");
        preOrder(i.left);
        preOrder(i.right);
    }

    private void postOrder(Node i) {
        if (i == null) return;
        postOrder(i.left);
        postOrder(i.right);
        System.out.print(i.item + "(" + i.occ + ") ");
    }

    private void inOrder(Node i) {
        if (i == null) return;
        inOrder(i.left);
        System.out.print(i.item + "(" + i.occ + ") ");
        inOrder(i.right);
    }

    // Returns the In-Order predecessor for a given key in the BST.
    Node pred(T e) {
        pred_succ(e, root);
        return pre;
    }

    // Returns the In-Order successor for a given key in the BST.
    Node succ(T e) {
        pred_succ(e, root);
        return suc;
    }

    // Computes the predecessor and successor for a given key and stores them as class properties pre and suc.
    private void pred_succ(T e, Node i) {
        if (i == null) return;
        if (i.item == e) {
            if (i.left != null) pre = SubTreeMax(i.left);
            if (i.right != null) suc = SubTreeMin(i.right);
            return;
        }
        int cmp = e.compareTo(i.item);
        if (cmp > 0) {
            pre = i;
            pred_succ(e, i.right);
        } else if (cmp < 0) {
            suc = i;
            pred_succ(e, i.left);
        }
    }

    // Returns the rightmost element in the subtree rooted at i.
    private Node SubTreeMax(Node i) {
        Node r = i;
        while (r.right != null) r = r.right;
        return r;
    }

    // Returns the leftmost element in the subtree rooted at i.
    private Node SubTreeMin(Node i) {
        Node l = i;
        while (l.left != null) l = l.left;
        return l;
    }

    private int SubTreeSize(Node i) {
        return i.sss;
    }

    private Node insert(Node i, T e) {
        if (i == null) return new Node(e);
        int cmp = e.compareTo(i.item);
        if (cmp < 0) i.left = insert(i.left, e);
        else if (cmp > 0) i.right = insert(i.right, e);
        else {
            i.occ++;
            i.sss--; // Adding more occurrence won't change SubTreeSize.
        }
        i.sss++;
        return i;
    }

    private int search(Node i, T e) {
        if (i == null) return 0;
        int cmp = e.compareTo(i.item);
        if (cmp < 0) return search(i.left, e);
        else if (cmp > 0) return search(i.right, e);
        else return i.occ;
    }

    private Node remove(Node i, T e) {
        if (i == null) return null; // Not found
        int cmp = e.compareTo(i.item);
        if (cmp < 0) i.left = remove(i.left, e);
        else if (cmp > 0) i.right = remove(i.right, e);
        else { // Found
            if (i.left == null && i.right == null) { // Has no children, remove safely.
                i.occ--;
                if (i.occ == 0) {
                    i.sss--;
                    return null;
                }
            } else if (i.left == null) { // Has only right child, remove and connect parent to child.
                i.occ--;
                if (i.occ == 0) {
                    i.sss--;
                    return i.right;
                }
            } else if (i.right == null) { // Has only left child, remove and connect parent to child.
                i.occ--;
                if (i.occ == 0) {
                    i.sss--;
                    return i.left;
                }
            } else { // Has both, replace node with its pred(or succ), then remove that pred(or succ).
                i.occ--;
                if (i.occ == 0) {
                    i.sss--;
                    Node pre = pred(i.item);
                    i.item = pre.item;
                    i.occ = pre.occ;
                    remove(i.left, pre.item);
                }
            }
        }
        return i;
    }

}

public class Main {
    public static void main(String[] args) {
        BST<Integer> bst = new BST<>();
        /*   Builds this tree. n represents null.
                       5(2)
                     /     \
                    2       8
                   / \     / \
                  1   3   7   9
                 / \ / \ / \ / \
                n  n n 4 6 n n  n
        */
        bst.insert(5);
        bst.insert(5); // Added multiple times.
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
        System.out.println("\nTree size = " + bst.size()); // Number of nodes in tree.

        System.out.println("Searching for 5: " + bst.search(5));
        System.out.println("Searching for 11: " + bst.search(11));

        System.out.println("Tree min is " + bst.min_element());
        System.out.println("Tree max is " + bst.max_element());

        System.out.println("pred(5) = " + bst.pred(5).item);
        System.out.println("succ(5) = " + bst.succ(5).item);
        System.out.println("pred(6) = " + bst.pred(6).item);
        System.out.println("succ(4) = " + bst.succ(4).item);
        System.out.println("floor(10) = " + bst.floor(10));
        System.out.println("ceil(0) = " + bst.ceil(0));
        System.out.println("rank(7) = " + bst.rank(7));

        bst.remove(5); // Will remove only one instance
        bst.remove(5); // Will remove the second instance

        System.out.println("PreOrder after removing the root :");
        bst.preOrder();
    }
}