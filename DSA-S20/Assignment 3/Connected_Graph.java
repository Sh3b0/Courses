/*
    Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
    Task 2.1 - Connected Graph.
    Submission Link on CodeForces: https://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/77579675
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class UndirectedGraph<T> {
    // Graph AdjList, maps every vertex index to a list of indices of adjacent vertices.
    private HashMap<Integer, ArrayList<Integer>> G = new HashMap<>();

    // Indicates if we visited this vertex before or not, for DFS.
    private boolean[] seen;

    // Holds the number of vertices, vertices are numbered from 1 to N.
    private int N;

    // To hold the data stored inside graph vertices, will not be used in this problem.
    private T[] data;

    // Class constructor, initializing objects.
    UndirectedGraph(int N) {
        this.N = N;
        data = (T[]) new Object[N + 1];
        seen = new boolean[N + 1];
    }

    // To get the data stored inside graph vertices, will not be used in this problem.
    public T getData(int idx) { // O(1)
        if (idx < 1 || idx > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);
        return data[idx];
    }

    // To set the data stored in vertex number idx
    public void setData(int idx, T val){ // O(1)
        if (idx < 1 || idx > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);
        data[idx] = val;
    }

    // Adds a bidirectional edge between the vertex number "from" and the vertex number "to".
    void addEdge(int from, int to) { // O(1)
        if (from < 1 || from > N || to < 1 || to > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);

        if (G.get(from) == null) G.put(from, new ArrayList<>());
        if (G.get(to) == null) G.put(to, new ArrayList<>());

        G.get(from).add(to);
        G.get(to).add(from);
    }

    // Runs a depth-first search starting from vertex number s.
    private void dfs(int s) { // O(N + M)
        if (s < 1 || s > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);
        seen[s] = true;
        if (G.get(s) != null) {
            for (int u : G.get(s)) {
                if (!seen[u]) {
                    dfs(u);
                }
            }
        }
    }

    // Checks if the graph is connected, and outputs a counter-example if it's not, using dfs.
    void analyzeConnectivity() { // O ( N + M ) because it does a simple DFS, and we will not visit a vertex twice.
        int cc = 0; // Number of Connected Components
        int[] elementFromComponent = new int[N + 1];
        for (int i = 1; i <= N; i++) {
            if (!seen[i]) {
                cc++;
                elementFromComponent[cc] = i;
                dfs(i);
            }
        }
        if (cc == 1)
            System.out.println("GRAPH IS CONNECTED");
        else // Graph has at least two connected components, output any one from each of them.
            System.out.println("VERTICES " + elementFromComponent[1] + " AND " + elementFromComponent[2] + " ARE NOT CONNECTED BY A PATH");
    }
}

public class Connected_Graph {
    public static void main(String[] args) {
        // Testing
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(), M = sc.nextInt(); // Number of vertices and edges, respectively.
        UndirectedGraph<Integer> graph = new UndirectedGraph<>(N); // Type of data stored in graph vertices doesn't matter in this problem.
        while (M-- != 0) {
            graph.addEdge(sc.nextInt(), sc.nextInt());
        }
        graph.analyzeConnectivity();
    }
}