import java.util.ArrayList;

// Priority Queue using Binary Heap

/*
    swim: exchange the node with its parent if it's bigger than it
    sink: exchange the node with the biggest of it's two children as long as it's smaller than them
    insert: add node at the end then swim it up
    delMax: swap the top with the last -> remove the last -> sink the top
*/

class PQ<T extends Comparable<T>> {

    private ArrayList<T> x;   // Binary heap
    private int size;           // Size

    PQ() {
        size = 0;
        x = new ArrayList<>();
        x.add(null);          // Just a placeholder to count from 1
    }

    void swim(int k) {

        // Iterative way
        /*
            while (k > 1 && x.get(k).compareTo(x.get(k / 2)) > 0) {
                swap(x, k, k / 2);
                k /= 2;
            }
        */

        // Recursive way
        if (k == 1) return;
        if (x.get(k).compareTo(x.get(k / 2)) > 0) swap(x, k, k / 2);
        swim(k / 2);

    }

    void sink(int k) {

        // Iterative way

        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && x.get(j).compareTo(x.get(j + 1)) < 0) j++;
            if (x.get(k).compareTo(x.get(j)) < 0) swap(x, k, j);
            else return;
            k = j;
        }

        // Recursive way
        /*
        int l = 2 * k, r = 2 * k + 1;                               // left and right child of k.
        if (l == size) {                                            // If k has only left child.
            if (x.get(k).compareTo(x.get(l)) < 0) swap(x, k, l);
        } else if (l < size) {                                      // If k has both children.
            int g; // Will hold index of the greater of two children.
            if (x.get(l).compareTo(x.get(r)) > 0) g = 2 * k;
            else g = 2 * k + 1;
            if (x.get(k).compareTo(x.get(g)) < 0) swap(x, k, g);
            else return;
            sink(g);
        }
         */
    }

    private void swap(ArrayList<T> x, int i, int j) {
        T tmp = x.get(i);
        x.set(i, x.get(j));
        x.set(j, tmp);
    }

    void push(T i) {
        x.add(i);
        swim(++size);
    }

    T pop() {
        if (size == 0) throw new NullPointerException("Queue is empty\n");
        T t = x.get(1);
        swap(x, 1, size);
        x.remove(size--);
        sink(1);
        return t;
    }

    void levelOrderTraversal() {
        for (int i = 1; i <= size; i++) {
            System.out.print(x.get(i) + " ");
        }
        System.out.println("");
    }

    public int size() {
        return size;
    }
}

public class Main {
    public static void main(String[] args) {
        PQ<Integer> pq = new PQ<>();
        pq.push(1);
        pq.push(5);
        pq.push(2);
        pq.push(3);
        pq.push(9);
        pq.push(2);
        pq.push(4);

        System.out.println(pq.pop());
        pq.levelOrderTraversal();
    }
}