// Implementing a HashMap with a fixed capacity using a Singly-Linked-List
// Strategy used for handling collisions: Separate chaining

class Map<K, V> {
    private int sz = 0;
    private final int cap = 10007; // Just a random prime fixed capacity

    static class Node {
        Object key;
        Object val;
        Node next;
    }

    private Node[] map = new Node[cap];

    private int h(K k) {
        return (((K) k).hashCode() & 0x7FFFFFFF) % cap; // Used java built-in hashing function for objects
        // AND-ing with 2^31 to get only positive values, then taking mod capacity to get a number b/w 0 and cap - 1
    }

    void put(K k, V v) {
        int hash = h(k);
        // System.out.println("hash for " + k + " = " + hash);
        if (map[hash] == null) { // If there are no collisions yet (More likely to happen)
            sz++;
            map[hash] = new Node();
            map[hash].key = k;
            map[hash].val = v;
            return;
        }
		
        Node n = map[hash], last = map[hash];
        while (n != null) {
            if (n.key.equals(k)) { // If key is already in the hash map, update the value
                n.val = v;
                return;
            }
            last = n;
            n = n.next;
        }
        sz++;
        n = new Node();
        last.next = n;
        n.key = k;
        n.val = v;
    }

    V get(K k) {
        Node n = map[h(k)];
        while (n != null) {
            if (n.key.equals(k)) {
                return (V) n.val;
            }
            n = n.next;
        }
        return null; // Not Found
    }

    void remove(K k) {
        Node n = map[h(k)], prev = null;
        while (n != null) {
            if (n.key.equals(k)) {
                if (prev != null && n.next != null) {
                    prev.next = n.next;
                }
                n = null;
                return;
            }
            prev = n;
            n = n.next;
        }
    }

    int size() {
        return sz;
    }
}

public class Practice{
	public static void main(String[] args){
		// Test
	}
}