/*
    Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
    Task 2.3 - Minimum spanning forest.
    Submission link on CodeForces: https://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/77601150
 */

import java.util.*;

// Edge class, works like a triplet, implements comparable to allow sorting edges for Kruskal MST.
class Edge implements Comparable<Edge> {
    int from, to, weight;

    Edge(int from, int to, int weight) { // Constructor
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge that) { // Edge with minimum weight comes first.
        return this.weight - that.weight;
    }
}

// DisjointSet data structure, used for Kruskal MST to allow detecting and ignoring cycles.
class UnionFind {
    private int[] par, sz; // par[a] = b means b is a parent of a, sz[a] = the size of the component that a belongs to.
    private int cc;        // Number of connected components

    UnionFind(int n) {     // Constructor, initialize objects and variables.
        par = new int[n];
        sz = new int[n];
        cc = n;            // In the beginning we have n connected components, each with only one element.

        for (int i = 0; i < n; i++) {
            par[i] = i;    // Each node is a parent of itself.
            sz[i] = 1;     // Each component has size 1 by default.
        }
    }

    // Connects the two components storing p and q.
    void union(int p, int q) {
        int rp = root(p), rq = root(q);
        if (rp == rq) return;

        // Append the tree with min size, to the one with max size, to avoid long trees, update size.
        if (sz[rp] < sz[rq]) {
            par[rp] = rq;
            sz[rq] += sz[rp];
        } else {
            par[rq] = rp;
            sz[rp] += sz[rq];
        }

        // We joined two components, number of components decreases by one.
        cc--;
    }

    // Returns true if p and q are in the same component, false otherwise.
    boolean connected(int p, int q) {
        return root(p) == root(q);
    }

    int root(int i) {
        while (i != par[i]) {
            par[i] = par[par[i]]; // path compression
            i = par[i];           // going upwards in the tree
        }
        return i;
    }

    int count() { // returns the number of connected components
        return cc;
    }
}

public class Minimum_Spanning_Forest {

    private static int n;
    private static ArrayList<Edge> edges = new ArrayList<>();

    // Calculates the Minimum Spanning Forest, outputs the number of trees it has.
    static HashMap<Integer, ArrayList<Edge>> minimumSpanningForest() { // O( M log(M) )
        // Firstly, sort all edges by minimum weight.
        Collections.sort(edges);

        // Create a UnionFind DS of size n + 1, to allow connecting MST vertices and detecting cycles.
        UnionFind uf = new UnionFind(n + 1);

        // Will hold a list of all edges in the Minimum Spanning Forest
        ArrayList<Edge> mst = new ArrayList<>();

        // To mark seen vertices, unseen vertices are trees of size 1.
        boolean[] seen = new boolean[n + 1];

        // Passing on all edges in order.
        for (Edge e : edges) {

            // if the edge can be added without creating a cycle, we add it, Mark both of its ends as seen, add it to the list.
            if (!uf.connected(e.from, e.to)) {
                uf.union(e.from, e.to);
                seen[e.from] = true;
                seen[e.to] = true;
                mst.add(e);
            }
        }

        // Maps the root of a MST to a list of all edges in the tree.
        HashMap<Integer, ArrayList<Edge>> tree = new HashMap<>();
        for (Edge e : mst) {
            int root = uf.root(e.from); // or e.to, both have the same root;
            if (tree.get(root) == null) tree.put(root, new ArrayList<>());
            tree.get(root).add(e);
        }

        System.out.println(uf.count() - 1); // Number of MSTs in the forest. 1 is subtracted because uf is 0-indexed.

        // Check for single vertex components.
        for (int i = 1; i <= n; i++) {
            if (!seen[i]) System.out.println("1 " + i);
        }

        return tree;
    }

    public static void main(String[] args) {
        // Testing
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        int m = sc.nextInt();

        for (int i = 0; i < m; i++)
            edges.add(new Edge(sc.nextInt(), sc.nextInt(), sc.nextInt()));

        for (HashMap.Entry<Integer, ArrayList<Edge>> entry : minimumSpanningForest().entrySet()) {
            System.out.println(entry.getValue().size() + 1 + " " + entry.getKey());
            for (Edge e : entry.getValue()) System.out.println(e.from + " " + e.to + " " + e.weight);
        }
    }
}