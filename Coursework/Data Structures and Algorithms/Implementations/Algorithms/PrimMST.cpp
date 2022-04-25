#include <bits/stdc++.h>
using namespace std;

/// PRIM - MINIMUM SPANNING TREE, IMPLEMENTING THIS PSEUDO-CODE (Which I wrote) :D
/**

PRIM-MST(Graph G, vertex s)
	List<Edge> MST = \emptyset
    MIN-PQ<Edge> Q = \emptyset
	foreach vertex v \in G.Adj[s]
		Q.push(Edge(s, v))

	while Q \neq \emptyset
		Edge e = Q.EXTRACT-MIN()
		if e not in mst
            MST.add(e)
      vertex u = e.to()
		foreach vertex v \in G.Adj[u]
			if v \notin MST
				Q.push(Edge(u, v))
	return MST
*/



class Graph{
public:
    map<int, vector<pair<int, int> > > Adj;
};

class Edge{
public:
    int from, to, weight;
    Edge(int from, int to, int weight){
        this->from = from;
        this->to = to;
        this->weight = weight;
    }

    friend bool operator < (const Edge& a, const Edge& b){
        return a.weight > b.weight; /// Now it's a MIN_PQ
    }
};

vector<Edge> PRIM_MST (Graph G, int s) {
        vector<Edge> MST;
        priority_queue< Edge > Q;

        for(auto v : G.Adj[s])
            Q.push(*new Edge(s, v.first, v.second));

        unordered_map<int, bool> in_MST;

        while (!Q.empty()){
            Edge e = Q.top(); Q.pop();

            if(!in_MST[e.to])
                MST.push_back(e);
            in_MST[e.from] = 1;
            in_MST[e.to] = 1;

            int u = e.to;
            for(auto v : G.Adj[u])
                if(!in_MST[v.first])
                    Q.push(*new Edge(u, v.first, v.second));
        }
        return MST;
}

int main()
{
    // Constructing this Graph https://photos.app.goo.gl/89Gc6My4vyZcFVTQ9
    Graph G;
    G.Adj[1].push_back({2,3});
    G.Adj[1].push_back({3,1});
    G.Adj[2].push_back({1,3});
    G.Adj[2].push_back({3,1});
    G.Adj[2].push_back({4,3});
    G.Adj[2].push_back({5,1});
    G.Adj[3].push_back({1,1});
    G.Adj[3].push_back({2,1});
    G.Adj[3].push_back({5,3});
    G.Adj[4].push_back({2,3});
    G.Adj[4].push_back({5,1});
    G.Adj[5].push_back({2,1});
    G.Adj[5].push_back({3,3});
    G.Adj[5].push_back({4,1});

    for(auto u: PRIM_MST(G, 1)){
        cout<<u.from<<" "<<u.to<<'\n';
    }
}
