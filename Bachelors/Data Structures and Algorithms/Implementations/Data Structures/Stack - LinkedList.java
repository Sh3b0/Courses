// Implementing a stack in java using LinkedList

class Stack<T> {
    Node top = new Node();
    private int sz = 0;

    class Node {
        T item;
        Node next;
    }

    void push(T t) {
        Node cur = new Node();
        cur.item = t;
        cur.next = top;
        top = cur;
        sz++;
    }

    boolean empty() {
        return sz == 0;
    }

    T pop(){
        if (sz == 0) throw new NullPointerException("Stack is empty");
        T s = top.item;
        top = top.next;
        sz--;
        return s;
    }

    int size() {
        return sz;
    }

    T top() {
        return top.item;
    }
}

public class Main {
    public static void main(String[] args){
        Stack<Integer> s = new Stack<>();
    }
}
