/*

Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
   - Implementing Merge Sort & QuickSort Algorithms
   - MergeSort
       - Best/Average/Worst case time complexity is O( n * log(n) )
       - Implementation type: out-of-place
       - Stable: Yes.
   - QuickSort
       - Best/Average case time complexity is O( n * log(n) )
       - Worst case time complexity is O( n ^ 2 )
       - Implementation type: out-of-place
       - Stable: No.
*/

public class Sorting {
    public static void main(String[] args) {

        // Testing arrays.
        Integer[] x = {5, 9, 1, 6, -561, 69, 98, -20, 0}; // Will be MergeSorted
        Integer[] y = {5, 9, 1, 6, -561, 69, 98, -20, 0}; // Will be QuickSorted
        int c1 = 0, c2 = 0; // Counters

        // MergeSorter and Quick Sorter will work with any comparable type.
        // Consider creating different MergeSorters/QuickSorters for different comparable types.

        System.out.println("Testing MergeSorter");
        MergeSorter<Integer> ms = new MergeSorter<>();
        for (Queue<Integer>.Node i = ms.sort(x).front; i != null; i = i.next) x[c1++] = i.item;
        for (int i : x) System.out.print(i + " ");

        System.out.println("\nTesting QuickSorter");
        QuickSorter<Integer> qs = new QuickSorter<>();
        for (Queue<Integer>.Node i = qs.sort(y).front; i != null; i = i.next) y[c2++] = i.item;
        for (int i : y) System.out.print(i + " ");

    }
}

class QuickSorter<T extends Comparable<T>> {

    // The only method available for clients to use, It calls the internal sorting method of QuickSorter.
    Queue<T> sort(T[] x) {
        Queue<T> q = new Queue<>();
        for (T t : x) q.push(t);
        return sort(q);
    }

    private Queue<T> sort(Queue<T> t) {
        if (t.size <= 1) return t; // Arrays of size 1 are sorted by definition

        // Three queues to hold element 'L'ess than, 'E'qual to, 'G'reater that 'p'ivot.
        Queue<T> L = new Queue<>(), E = new Queue<>(), G = new Queue<>();
        T p = t.back(); // Choosing last element as pivot.

        // Compare elements with pivot to store them in appropriate position.
        for (Queue<T>.Node i = t.front; i != null; i = i.next) {
            T cur = i.item;
            if (cur.compareTo(p) < 0) L.push(cur);
            else if (cur.compareTo(p) > 0) G.push(cur);
            else E.push(cur);
        }

        Queue<T> r = new Queue<>(); // Merging all solutions.

        for (Queue<T>.Node i = sort(L).front; i != null; i = i.next)
            r.push(i.item);

        for (Queue<T>.Node i = E.front; i != null; i = i.next)
            r.push(i.item);

        for (Queue<T>.Node i = sort(G).front; i != null; i = i.next)
            r.push(i.item);

        return r; // Returns the sorted queue.
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

	// Nodes are public to allow iteration on element of the queue.
    Node front;
    Node back;
    int size = 0;

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