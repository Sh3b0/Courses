#include <bits/stdc++.h>

using namespace std;

map<int, vector< pair<int, int> > > g;
map<int, int> dist, par;
int n;

/// Dijkstra's Single-Source Shortest Path for DAG.
void DSSSP(int src)
{
    for(int i=1; i<=n; i++)
    {
        dist[i] = INT_MAX;
    }
    dist[1] = 0;

    priority_queue< pair<int,int> > q;

    for(auto u : g[src])
    {
        q.push(u);
        dist[u.second] = u.first;
    }

    while(!q.empty())
    {
        int cur = q.top().second;
        q.pop();
        for(auto u : g[cur])
        {
            /// relaxing the edge u.
            if(dist[u.second] > dist[cur] + u.first)
            {
                dist[u.second] = dist[cur] + u.first;
                par[u.second] = cur;
            }
            q.push(u);
        }
    }
}

int main()
{
    int m, a, b, w;

    cin>>n>>m;

    while(m--)
    {
        cin>>a>>b>>w;
        g[a].push_back({w, b});
    }

    DSSSP(1);

    /// Printing the path, if doesn't exist, prints -1.
    if(dist[n]==INT_MAX) cout<<-1;
    else
    {
        stack<int> path;
        while(n)
        {
            path.push(n);
            n = par[n];
        }
        path.push(1);
        while(!path.empty())
        {
            cout<<path.top()<<" ";
            path.pop();
        }
    }
}
