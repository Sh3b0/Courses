#include <bits/stdc++.h>
using namespace std;

/** IMPLEMENTING THIS PSEUDOCODE
    SINGLE-SOURCE-DIJKSTRA(Graph G, vertex s)
    foreach vertex u \in G.V
            u.distance = ∞
            u.marked = false
        s.distance = 0
        s.marked = true

    MIN-PQ<Edge> Q = \emptyset
        foreach vertex v \in G.Adj[s]
            v.distnace = weight(s, v)
            Q.push(Edge(s, v))

        while Q ≠ \emptyset
            vertex u = Q.EXTRACT-MIN().to
            u.marked = true
            foreach vertex v \in G.Adj[u]
                v.distance = min(v.distance, u.distance + weight(u, v))
                if v.marked = false
                    Q.push(Edge(u, v))

*/

class Graph{
public:
    unordered_map<int,int> dist, par;
    map<int, vector<pair<int, int> > > Adj;
    int n;
    Graph(int n){
        this->n = n;
    }
    int size(){
        return n;
    }
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

void SINGLE_SOURCE_DIJKSTRA(Graph& G, int s){

    unordered_map<int, bool> marked;

    for(int i=1;i<=G.size();i++){
		G.dist[i] = INT_MAX;
        marked[i] = 0;
	}

	G.dist[s] = 0;
	marked[s] = 1;
	G.par[s] = 0;
    priority_queue<Edge> Q;
	for(auto v : G.Adj[s]){
		Q.push(*new Edge(s, v.first, v.second));
		G.dist[v.first] = v.second;
		G.par[v.first] = s;
	}
	while (!Q.empty()){
		Edge u = Q.top(); Q.pop();
		marked[u.to] = 1;
		for(auto v : G.Adj[u.to]){
            if(G.dist[u.to] + v.second < G.dist[v.first]){
                G.dist[v.first] = G.dist[u.to] + v.second;
                G.par[v.first] = u.to;
            }
            if(!marked[v.first]){
            	Q.push(*new Edge(u.to, v.first, v.second));
			}
		}
	}

}
int main()
{
    // Constructing this Graph https://photos.app.goo.gl/89Gc6My4vyZcFVTQ9
    Graph G(5);
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

    SINGLE_SOURCE_DIJKSTRA(G, 1);

    for(int i=1; i<= G.size();i++){
        cout<<G.dist[i]<<'\n';
    }
}
