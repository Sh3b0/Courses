class UnionFind {
    private int[] par, sz; // par[a] = b means b is a parent of a, sz[a] = the size of the component that a belongs to.
    private int cc; // number of connected components

    UnionFind(int n) {
        par = new int[n];
        sz = new int[n];
        cc = n;
        for (int i = 0; i < n; i++) {
            par[i] = i; sz[i] = i;}
    }

    void union(int p, int q) { // connects the two components storing p and q
        int rp = root(p), rq = root(q);
        if(rp == rq) return;
		if (sz[p] < sz[q]) par[rp] = rq;
        else par[rq] = rp;
        cc--;
    }
	
    boolean connected(int p, int q) { // checks if p and q are in the same component
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

class Main {
    public static void main(String[] args) {
		// Testing
        UnionFind uf = new UnionFind(10);
        uf.union(4,3);
        uf.union(4,8);
        uf.union(5,6);
        uf.union(9,4);
        uf.union(2,1);
        uf.union(5,0);
        uf.union(7,2);
        uf.union(6,1);
        System.out.println(uf.connected(9,1));
        uf.union(7,3);
        System.out.println(uf.connected(9,1));
        System.out.println(uf.count());
    }
}