#include<bits/stdc++.h>

using namespace std;

unordered_map<int, vector<int> >g;
unordered_map<int,bool>vis, co;
bool bi = 1;

void dfs(int v, bool c)
{
    if(vis[v])
    {
        if(c!=co[v]) bi=0;
        return;
    }
    vis[v]=1;
    co[v] = c;
    for(auto u:g[v])
    {
        dfs(u,!c);
    }
}

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
    dfs(0,0);
    (bi)?cout<<"YES\n":cout<<"NO\n";
}
