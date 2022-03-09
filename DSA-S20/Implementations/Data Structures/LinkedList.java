// DEBUG THIS
class LL<T> {

    private int size = 0;
    private Node head, last;

    class Node {
        Node prev;
        Node next;
        T item;
    }

    void addFirst(T e) {
        Node n = new Node();
        n.item = e;
        if (size == 0) {
            head = n;
            last = n;
        } else {
            n.next = head;
            head.prev = n;
            head = n;
        }
        size++;
    }

    void addLast(T e) {
        Node n = new Node();
        n.item = e;
        if (size == 0) {
            head = n;
            last = n;
        } else {
            n.prev = last;
            last.next = n;
            last = n;
        }
        size++;
    }

    void removeFirst() {
        if(head == last){
            size = 0;
            head = null;
            last = null;
        }
        else if (head != null) {
            head = head.next;
            head.prev = null;
            size--;
        }
    }

    void removeLast() {
        if(head == last){
            size = 0;
            head = null;
            last = null;
        }
        else if(last != null) {
            last = last.prev;
            last.next = null;
            size--;
        }
    }

    boolean isEmpty() {
        return size == 0;
    }

    int size() {
        return size;
    }

    void iterate() {
        Node n = head;
        while (n != null) {
            System.out.print(n.item + " ");
            n = n.next;
        }
    }
}

class Main {
    public static void main(String[] args) {
        LL<Integer> x = new LL<>();

        x.addLast(2);
        x.addLast(3);
        x.addFirst(1);

        x.addFirst(-1);
        x.addLast(-1);
        x.removeFirst();
        x.removeLast();

        x.iterate();
    }
}