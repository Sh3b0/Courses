// Author: Ahmed Nouralla - BS19-02 - a.shaaban@innopolis.university
// CodeForces Submission Link: https://codeforces.com/group/3ZU2JJw8vQ/contest/269072/submission/70978136

import java.util.ArrayList;
import java.util.Scanner;

public class PhoneBook {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // To read from standard input.
        Map<String, ArrayList<String>> m = new Map<>(); // Links a person with a list of his/her phone numbers
        int n = sc.nextInt(); // To hold number of queries.
        sc.nextLine(); // To catch the empty line after getting n.
        while (n-- != 0) {
            String[] in = sc.nextLine().split(" ", 2); // Read a query line and split it to: type and description
            String query = in[0], key, val;
            StringBuilder tmp = new StringBuilder(); // To help extracting key, value from description string.
            int i; // To keep track of indices.
            for (i = 0; i < in[1].length(); i++) { // Read and append at temp until you encounter a comma.
                if (in[1].charAt(i) == ',') {
                    i++; // to skip the comma character itself.
                    break;
                }
                tmp.append(in[1].charAt(i));
            }
            key = tmp.toString(); // Key is captured.
            tmp = new StringBuilder(); // Reuse temp to get the rest of the description string as value.
            for (; i < in[1].length(); i++) {
                tmp.append(in[1].charAt(i));
            }
            val = tmp.toString(); // Value is captured, it can be empty.

            // For testing
            // System.out.println(query + "|" + key + "|" + val + "|");

            // Check the type of the query.
            if (query.equalsIgnoreCase("ADD")) {
                ArrayList<String> al;
                if (m.get(key) == null) { // Create a list if it's first time to see the contact.
                    m.put(key, new ArrayList<String>());
                }
                if (!m.get(key).contains(val))
                    m.get(key).add(val); // Add the number to the list associated with the contact, if doesn't exist.
            } else if (query.equalsIgnoreCase("DELETE")) {
                if (val.isEmpty()) { // If no specific value to delete, delete the whole contact.
                    m.put(key, null);
                } else { // Otherwise delete that specific value.
                    if (m.get(key) != null) {
                        m.get(key).remove(val);
                        // If there are no more numbers for that contact, delete it.
                        if (m.get(key).size() == 0) m.put(key, null);
                    }
                }
            } else if (query.equalsIgnoreCase("FIND")) {
                if (m.get(key) == null) { // If contact not found.
                    System.out.println("No contact info found for " + key);
                } else { // Otherwise, we output the number of phone numbers associated with the contact.
                    System.out.print("Found " + m.get(key).size() + " phone numbers for " + key + ": ");
                    for (String u : m.get(key)) { // Output the numbers one by one, separated with a space.
                        System.out.print(u + " ");
                    }
                    System.out.println(""); // Outputs a new line.
                }
            } else { // If the query entered is not "ADD" or "FIND" or "DELETE".
                System.out.println("Query is not valid");
            }
        }
    }
}

// Implementing a HashMap with a fixed capacity using a Singly-Linked-List
// Strategy used for handling collisions: Separate chaining

class Map<K, V> {
    // Class properties
    private int cap; // To hold maximum capacity of the map
    private Node[] map; // Our map is an array of nodes, each one is a head of a linked-list that contain other collided nodes.

    Map() { // Default constructor
        cap = 10007; // Just a random prime fixed capacity
        map = new Node[cap]; // initializing the map with the fixed capacity
    }

    Map(int initCap) { // Constructor with one integer parameter for initializing capacity.
        cap = initCap;
        map = new Node[cap]; // initializing the map with the fixed capacity
    }

    static class Node { // Map node
        Object key;
        Object val;
        Node next;
    }

    private int h(K k) { // Hashing function
        return (k.hashCode() & 0x7FFFFFFF) % cap; // Used java built-in hashing function.
        // AND-ing with 2^31 to get only positive values, then taking mod capacity to get a number b/w 0 and cap - 1
    }

    // To create key value pairs or update existing key with a new value.
    void put(K k, V v) { // O(1) Amortized.
        int hash = h(k); // To hold the hashing index in order not to compute it multiple times.
        // System.out.println("hash for " + k + " = " + hash);
        if (map[hash] == null) { // If there are no collisions yet (More likely to happen), then we add the new element.
            map[hash] = new Node();
            map[hash].key = k;
            map[hash].val = v;
        } else { // If there is a collision, then we add a new node next to the last one in the chain.
            Node n = map[hash], prev = map[hash]; // Nodes to iterate through the chain. n: currently processed, prev: n predecessor.
            while (n != null) {
                if (n.key.equals(k)) { // If key is already in the hash map, update the value
                    n.val = v;
                    return;
                }
                prev = n;
                n = n.next;
            }
            // If key is not present, we create a node for it and add it to the end of the chain.
            n = new Node();
            n.key = k;
            n.val = v;
            prev.next = n;
        }
    }

    // To get value associated with key.
    V get(K k) { // O(1) Amortized.
        Node n = map[h(k)];
        while (n != null) { // Search in the chain.
            if (n.key.equals(k)) { // If key found, return the corresponding value.
                return (V) n.val;
            }
            n = n.next;
        }
        return null; // Not Found
    }
}