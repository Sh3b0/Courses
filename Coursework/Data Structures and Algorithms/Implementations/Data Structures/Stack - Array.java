interface Stack<T> {
    void push(T e);

    T pop();

    T peek();

    int size();

    boolean isEmpty();
}

class ArrayStack<T> implements Stack<T> {
    private T[] storage;
    private int sz, cap;

    ArrayStack(int t) {
        storage = (T[]) new Object[t];
        this.cap = t;
    }

    @Override
    public T pop() {
        if (sz == 0) throw new NullPointerException("Stack is empty");
        return storage[--sz];
    }

    @Override
    public T peek() {
        if (sz == 0) throw new NullPointerException("Stack is empty");
        return storage[sz-1];
    }

    @Override
    public int size() {
        return sz;
    }

    @Override
    public void push(T e) {
        if (sz == cap) {
            cap *= 2;
            Object[] temp = (T[]) new Object[cap * 2];
            for (int i = 0; i < sz; i++) {
                temp[i] = storage[i];
            }
            storage = (T[]) temp;
        }
        storage[sz++] = e;
    }

    @Override
    public boolean isEmpty() {
        return sz == 0;
    }
}

class Practice {
    public static void main(String[] args) {
        ArrayStack<Integer> s = new ArrayStack<>(2);
        s.push(1);
        s.push(2);
        s.push(3);
        System.out.println(s.pop());
        System.out.println(s.pop());
        System.out.println(s.pop());
        System.out.println(s.size());
    }
}