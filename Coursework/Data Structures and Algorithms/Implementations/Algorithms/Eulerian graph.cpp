#include<bits/stdc++.h>
using namespace std;

/// Determine if a given undirected graph is Eulerian, semi-Eulerian, or not Eulerian at all.
unordered_map<int, vector<int> >g;

int main()
{
    int n;
    cin>>n;
    /* // Reading the graph as adj matrix
    bool x;
    for(int i=0;i<n;i++){
        for(int j=0;j<n;j++){
            cin>>x;
            if(x) g[i].push_back(j);
        }
    }
    */
    // Reading number of edges, then the graph as an adj list.
    int m, a, b;
    cin>>m;
    while(m--)
    {
        cin>>a>>b;
        g[a].push_back(b);
        g[b].push_back(a);
    }
    int o=0;
    for(int i=0; i<n; i++)
    {
        if(g[i].size()%2)o++;
    }
    /// o can't be 1 in undirected graph.
    if(o==0)cout<<"Graph has Eulerian cycle\n";
    else if(o==2)cout<<"Graph has Eulerian path\n";
    else if(o>2)cout<<"Graph is not Eulerian\n";
}
