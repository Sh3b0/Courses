#include<bits/stdc++.h>
using namespace std;

/// Implementing Kosaraju-Sharir Strongly connected components algorithm
/// Time complexity: O( V + E )

unordered_map<int, vector<int> >g;
unordered_map<int,bool>seen;
unordered_map<int,int>cmp;
stack<int>ts;

void dfs(int s, int c){
    seen[s] = 1;
    cmp[s] = c;
    for(auto u:g[s]){
        if(!seen[u])
            dfs(u, c);
    }
    if(c==-1) ts.push(s); /// Builds the topSort for the first dfs.
}

int main(){
    ios_base::sync_with_stdio(0);cin.tie(0);cout.tie(0);

    int n,m;
    cin>>n>>m;
    int a[m], b[m];

    for(int i=0;i<m;i++){
        cin>>a[i]>>b[i];
        g[b[i]].push_back(a[i]);
    }

    /// First DFS.
    for(int i=0;i<n;i++){
        if(!seen[i])
            dfs(i, -1);
    }

    g.clear();
    seen.clear();
    int cc = 0;

    /// Inverting the graph.
    for(int i=0;i<m;i++){
        g[a[i]].push_back(b[i]);
    }

    /// Second DFS, considering the order given by topSort.
    while(!ts.empty()){
        if(!seen[ts.top()])
            dfs(ts.top(), cc++);
        ts.pop();
    }

    for(int i=0;i<n;i++) cout<<cmp[i]<<" "; /// Strong component of node i.
}
