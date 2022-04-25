// Singly-Linked-List implementation of a Map
// set, get and remove all take linear time

class Map<K, V> {
    Node head = new Node();
    Node last = new Node();
    private int sz = 0;

    class Node {
        K key;
        V val;
        Node next;
    }

    void set(K k, V v) {
        if (sz == 0) {
            head.key = k;
            head.val = v;
            last = head;
            sz++;
            return;
        }

        Node n = head;
        while (n != null) {
            if (n.key == k) {   // If key exists, update it's value.
                n.val = v;
                return;
            }
            n = n.next;
        }

        // If key doesn't exist, add it to the map.
        n = new Node();
        n.key = k;
        n.val = v;
        last.next = n;
        last = n;
        sz++;
    }

    V get(K k) {
        Node n = head;
        while (n != null) {
            if (n.key == k) return n.val;
            n = n.next;
        }
        throw new NullPointerException(); // Not found
    }

    void remove(K k) {
        Node n = head;
        Node prev = null;
        while (n != null) {
            if (n.key == k) {
                if (prev != null) {
                    prev.next = n.next;
                }
                n = null;
                sz--;
                return;
            }
            prev = n;
            n = n.next;
        }
        throw new NullPointerException(); // Not found
    }

    int size() {
        return sz;
    }

    boolean isEmpty() {
        return sz == 0;
    }

}

class Practice {
    public static void main(String[] args) {
        Map<String, Integer> m = new Map<>();
        m.set("a", 5);
        m.set("b", 3);
        System.out.println(m.get("b"));
        m.set("b", 4);
        System.out.println(m.get("b"));
        System.out.println(m.size());
        m.remove("b");
        // System.out.println(m.get("b")); // Should throw an exception
        System.out.println(m.size());
    }
}