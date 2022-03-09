#include<bits/stdc++.h>
using namespace std;

/// DFS, O( V + E )
/// The graph: https://www.geeksforgeeks.org/depth-first-search-or-dfs-for-a-graph/

map<int,vector<int> >g;
map<int,bool>seen;

void dfs(int v){

    seen[v]=1;

    cout<<v<<" ";

    for(auto u:g[v]){
        if(!seen[u])dfs(u);
    }
}

int main(){

    g[0].push_back(1);
    g[0].push_back(2);
    g[1].push_back(2);
    g[2].push_back(0);
    g[2].push_back(3);
    g[3].push_back(3);

    dfs(2);
}
