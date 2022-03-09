/*
    Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
    Task 2.2 - Components dictionary
    Submission Link on CodeForces: https://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/77580783

    Class FastReader is used for reading from standard input, because Scanner is not fast enough to pass time limits for online judge.
        Implementation of class FastReader is taken from: https://www.geeksforgeeks.org/fast-io-in-java-in-competitive-programming/
        IMPORTANT NOTE: FastReader class was not a part of the assignment that's why I copied the implementation from the link above.
        ALL other classes are my own implementation.
*/

import java.util.ArrayList;
import java.util.HashMap;

// Packages used by FastReader.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class FastReader {
    private BufferedReader br;
    private StringTokenizer st;

    FastReader() {
        br = new BufferedReader(new
                InputStreamReader(System.in));
    }

    private String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }
}

class UndirectedGraph<T> {
    // Graph AdjList, maps every vertex index to a list of indices of adjacent vertices.
    private HashMap<Integer, ArrayList<Integer>> G = new HashMap<>();

    // Maps every vertex index to an index of the connected component it belongs to, components can have an index from 1 up to N.
    private int[] component;

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
        component = new int[N + 1];
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

    // Adds a bidirectional edge between vertex number "from" and the vertex number "to".
    void addEdge(int from, int to) { // O(1)
        if (from < 1 || from > N || to < 1 || to > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);
        if (G.get(from) == null) G.put(from, new ArrayList<>());
        if (G.get(to) == null) G.put(to, new ArrayList<>());

        G.get(from).add(to);
        G.get(to).add(from);
    }

    // Runs a modified depth-first search starting from vertex number s, that assigns for each vertex index, the index of the component it belongs to.
    // The second parameter represents the index of the connected component we are currently processing.
    private void dfs(int s, int c) { // O(N + M)
        if (s < 1 || s > N)
            throw new IndexOutOfBoundsException("Nodes are numbered from 1 to " + N);
        seen[s] = true;
        component[s] = c;
        if (G.get(s) != null) {
            for (int u : G.get(s)) {
                if (!seen[u]) {
                    dfs(u, c);
                }
            }
        }
    }

    // Builds and returns the component dictionary, described above, using dfs.
    int[] vertexComponents() { // O( N + M ) because it does a simple DFS, and we will not visit a vertex twice.
        int cc = 0; // Number of Connected Components
        for (int i = 1; i <= N; i++) {
            if (!seen[i]) {
                cc++;
                dfs(i, cc);
            }
        }
        return component;
    }
}

public class Components_Dictionary {
    public static void main(String[] args) {
        // Testing
        FastReader sc = new FastReader();
        int N = sc.nextInt(), M = sc.nextInt();
        UndirectedGraph<Integer> graph = new UndirectedGraph<>(N); // Type of data stored in graph vertices doesn't matter in this problem.
        while (M-- != 0) {
            graph.addEdge(sc.nextInt(), sc.nextInt());
        }
        int[] dict = graph.vertexComponents();
        for (int i = 1; i <= N; i++) {
            System.out.print(dict[i] + " ");
        }
    }
}