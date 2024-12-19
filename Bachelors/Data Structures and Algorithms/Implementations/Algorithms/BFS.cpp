#include<bits/stdc++.h>
using namespace std;

/// BFS, O( V + E )
/// The graph: https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/

map<int,vector<int> >g;
map<int,bool>seen;
map<int,int>dist;
int n = 4;

void bfs(int x){

    for(int i=0;i<n;i++){
        dist[i]=-1;
    }

    queue<int>q;
    q.push(x);
    seen[x]=1;
    dist[x]=0;

    while(!q.empty()){
        int cur=q.front();

        cout<<cur<<" ";

        q.pop();
        for(auto u:g[cur]){
            if(!seen[u]){
                seen[u]=1;
                dist[u] = dist[cur] + 1;
                q.push(u);
            }
        }
    }
}

int main(){

    g[0].push_back(1);
    g[0].push_back(2);
    g[1].push_back(2);
    g[2].push_back(0);
    g[2].push_back(3);
    g[3].push_back(3);

    bfs(2);

    cout<<'\n';
    for(int i=0;i<n;i++)cout<<dist[i]<<" ";
}
