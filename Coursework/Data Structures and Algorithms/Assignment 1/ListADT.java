// Author: Ahmed Nouralla - BS19-02 - a.shaaban@innopolis.university

interface List<T> { // List ADT interface that contains signatures of all methods required.

    // Checks if list is empty.
    boolean isEmpty();

    // Adds an element to a specific index.
    void add(int i, T e);

    // Adds an element to the beginning.
    void addFirst(T e);

    // Adds an element to the end.
    void addLast(T e);

    // Deletes an element at a specific index
    void delete(int i);

    // Deletes the first occurrence of a specific element.
    void delete(T e);

    // Deletes the first element in the list.
    void deleteFirst();

    // Deletes the last element in the list.
    void deleteLast();

    // Update an element at a specific index by a specific value.
    void set(int i, T e);

    // Get the contents of a specific index.
    T get(int i);

}

class DynamicArrayList<T> implements List<T> { // Implementation of an Array-Based List

    // Class properties
    private int size, cap; // To hold number of elements in the list, and the list capacity, respectively.
    private Object[] data; // Array of objects to hold the data, because java doesn't support generic array creation.

    DynamicArrayList() { // Default constructor, Initializes size, capacity and data array.
        size = 0;
        cap = 1;
        data = new Object[cap];
    }

    DynamicArrayList(int initCap) { // Constructor that initializes capacity with a specific argument value.
        size = 0;
        cap = initCap;
        data = new Object[cap];
    }

    @Override
    public boolean isEmpty() { // O(1)
        return size == 0; // Returns true if the list is empty, false otherwise.
    }

    @Override
    public void add(int i, T e) { // O(n)

        // If the client called add(0, e) when the list is empty, it is technically an invalid call.
        // But we can accept it by adding this condition.
        if (i == 0 && isEmpty()) {
            addFirst(e);
            return;
        }

        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        checkOverflow(); // To check if we ran out of space. If so, fix the problem using doubling strategy.
        size++; // We are adding a new element so size increases by one.
        for (int j = size - 1; j > i; j--) { // Shifts all elements to the right to free place for the element to be added.
            data[j] = data[j - 1];
        }
        data[i] = e; // Add the new element to the list.
    }

    @Override
    public void addFirst(T e) { // O(n)
        checkOverflow(); // To check if we ran out of space. If so, fix the problem using doubling strategy.
        size++; // We are adding a new element so size increases by one.
        for (int j = size - 1; j > 0; j--) { // Shifts all elements to the right to free place for the element to be added.
            data[j] = data[j - 1];
        }
        data[0] = e; // Add the new element to the list.
    }

    @Override
    public void addLast(T e) { // O(1) amortized
        checkOverflow(); // To check if we ran out of space. If so, fix the problem using doubling strategy.
        data[size++] = e; // Adding the new element to the end of the list then increases size
    }

    @Override
    public void delete(int i) { // O(n)
        if (isEmpty()) throw new NullPointerException("List is empty");
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        for (int j = i; j < size - 1; j++) { // Shifts elements to the left, overwriting the deleted element.
            data[j] = data[j + 1];
        }
        size--; // We removed an element so size decreases by one.
    }

    @Override
    public void delete(T e) { // O(n)
        for (int i = 0; i < size; i++) { // Sequentially searching for the first occurrence of the element.
            if (data[i] == e) { // If we found the element
                for (int j = i; j < size - 1; j++) { // Shift elements to the left, overwriting the deleted element.
                    data[j] = data[j + 1];
                }
                size--; // We removed an element so size decreases by one.
                return; // To stop the search and remove only one occurrence.
            }
        }
    }

    @Override
    public void deleteFirst() { // O(n)
        delete(0); // Calls the same delete function with 0 as index to delete the first element.
    }

    @Override
    public void deleteLast() { // O(1)
        if (isEmpty()) throw new NullPointerException("List is empty");
        size--; // We just decrease the size by one, the next element to be added will overwrite the deleted element.
    }

    @Override
    public void set(int i, T e) { // O(1)
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        data[i] = e; // Simply set the value to the given index.
    }

    @Override
    public T get(int i) { // O(1)
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        return (T) data[i]; // Simply return the value at the given index.
    }

    private void checkOverflow() { // O(n) when the condition is true.
        if (size == cap) { // If we ran out of space...
            cap *= 2; // Double the capacity.
            Object[] temp = new Object[cap]; // Create a new list with doubled capacity.
            for (int i = 0; i < size; i++) { // Copy all elements to the newly created list.
                temp[i] = data[i];
            }
            data = (T[]) temp; // Use that list for upcoming queries.
        }
    }

    // Method to print all elements in the list in order, created for debugging purposes.
    void iterate() { // O(n)
        for (int i = 0; i < size; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println("");
    }
}

class DoublyLinkedList<T> implements List<T> { // Implementation of a List ADT using a Doubly-Linked List.

    // Class properties
    private int size;
    private Node head, last;

    // Default constructor
    DoublyLinkedList() {
        size = 0;
        head = new Node();
        last = new Node();
    }

    // Doubly-Linked List Node
    private class Node {
        T item;
        Node prev;
        Node next;
    }

    @Override
    public boolean isEmpty() { // O(1)
        return size == 0; // Returns true if the size is zero, false otherwise.
    }

    private void check() { // O(1)
        // Used to validate list state before we try to set, get or remove.
        if (isEmpty()) throw new NullPointerException("List is empty");
    }

    @Override
    public void add(int i, T e) { // O(n)
        // Task didn't specify whether to shift elements to the left or to the right when adding to an index
        // This code shifts elements to the RIGHT.

        // If the client called add(0, e) when the list is empty, it is technically an invalid call.
        // But we can accept it by adding this condition.
        if (i == 0 && isEmpty()) {
            addFirst(e);
            return;
        }

        if (i < 0 || i >= size) {
            // If the client provided an invalid index
            throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        } else if (i == 0) addFirst(e); // If we are adding to the beginning of the list
        else {
            // Going from the head to index i in Linear time, because Linked-List doesn't support random access.
            Node n = head; // Temporary node which I use to iterate until reaching index i.
            while (i-- != 0) {
                n = n.next;
            }

            // Shifting elements to the right
            Node temp = new Node(); // New node to be added to the list.
            temp.item = e;
            temp.next = n;
            temp.prev = n.prev;
            n.prev.next = temp;
            n.prev = temp;
            size++;
        }
    }

    @Override
    public void addFirst(T e) { // O(1)
        Node n = new Node(); // New node to be added to the list.
        n.item = e;
        if (isEmpty()) { // If the list is empty, we have head = last = newly added node
            head = n;
            last = n;
        } else { // Otherwise we add the node and update the linking pointers.
            n.next = head;
            head.prev = n;
            head = n;
        }
        size++; // We added a new element, so the size of the list increased by one.
    }

    @Override
    public void addLast(T e) { // O(1)
        // New node to be added to the list.
        Node n = new Node();
        n.item = e;
        if (isEmpty()) { // If the list is empty, we have head = last = newly added node
            head = n;
            last = n;
        } else { // Otherwise we add the node and update the linking pointers.
            n.prev = last;
            last.next = n;
            last = n;
        }
        size++; // We added a new element, so the size of the list increased by one.
    }

    @Override
    public void delete(int i) { // O(n)
        check();
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        else if (i == 0) deleteFirst(); // If we are deleting from the beginning.
        else if (i == size - 1) deleteLast(); // If we are deleting from the end.
        else { // Otherwise we delete the element and update linking pointers.
            Node n = head; // Temporary node which I use to iterate until reaching index i.
            while (i-- != 0) {
                n = n.next;
            }
            // Delete the element and update the linking pointers.
            n.prev.next = n.next;
            n.next.prev = n.prev;
            n = null; // Destroying the temporary node to be collected by garbage collector and free the heap.
            size--; // We deleted an item, so size decreases by one.
        }
    }

    @Override
    public void delete(T e) { // O(n)
        check();
        if (head.item == e) deleteFirst(); // If the item is at the beginning.
        else {
            for (Node i = head; i != null; i = i.next) {
                if (i.item == e) { // If we found the first occurrence of the item.
                    if (i.next != null) { // If the item is not at the end, we update linking pointers.
                        i.prev.next = i.next;
                        i.next.prev = i.prev;
                        i = null;
                    } else { // If the item is at the end
                        deleteLast();
                    }
                    size--; // We deleted an item, so size decreases by one.
                }
            }
        }
        // If element isn't found, we don't do anything.
    }

    @Override
    public void deleteFirst() { // O(1)
        check();
        // Updating the linking pointers.
        head = head.next;
        head.prev = null; // Destroying the node to be collected by garbage collector and free the heap.
        size--; // We deleted an item, so size decreases by one.
    }

    @Override
    public void deleteLast() { // O(1)
        check();
        // Updating the linking pointers.
        last = last.prev;
        last.next = null; // Destroying the temporary node to be collected by garbage collector and free the heap.
        size--; // We deleted an item, so size decreases by one.
    }

    @Override
    public void set(int i, T e) { // O(n)
        check();
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        Node n = head;
        while (i-- != 0) {
            n = n.next;
        }
        n.item = e;
    }

    @Override
    public T get(int i) { // O(n)
        check();
        if (i < 0 || i >= size) throw new NullPointerException("Index is out of range: [0," + (size - 1) + "]");
        Node n = head; // Temporary node which I use to iterate until reaching index i.
        while (i-- != 0) {
            n = n.next;
        }
        return n.item; // Return the item at index i.
    }

    // Method to print all elements in the list in order, created for debugging purposes.
    void iterate() { // O(n)
        for (Node i = head; i != null; i = i.next) {
            System.out.print(i.item + " ");
        }
        System.out.println("");
    }
}

public class ListADT {
    public static void main(String[] args) {
        // Testing
        DynamicArrayList<String> d1 = new DynamicArrayList<>();
        DoublyLinkedList<String> d2 = new DoublyLinkedList<>();

        d1.addFirst("1");
        d1.addFirst("0");
        d1.addLast("3");
        d1.addLast("4");
        d1.add(2, "2");
        d1.iterate();
        d1.delete(0);
        d1.delete("4");
        d1.iterate();

        d2.addFirst("1");
        d2.addFirst("0");
        d2.addLast("3");
        d2.addLast("4");
        d2.add(2, "2");
        d2.iterate();
        d2.delete(0);
        d2.delete("4");
        d2.iterate();

    }
}