#include<bits/stdc++.h>

using namespace std;

map<int, vector<pair<int, int> > > adjl;
priority_queue<pair<int, pair<int, int> > > q;
map<int, bool> visited;
map<int, pair<int, int> > dist;

int main(){
    int n,m;
    cin>>n>>m;
    for(int i=0;i<m;i++){
        int x,y,w;
        cin>>x>>y>>w;
        adjl[x].push_back({y,w});
        adjl[y].push_back({x,w});
    }
    q.push({0,{1,0}});
    while(!q.empty()){
        int ind = q.top().second.first; /// index of node we are currently processing
        int dist1 = -q.top().first;     /// distance so far till we reached here
        int par = q.top().second.second;/// parent of current node.
        q.pop();
        if(visited[ind])continue;
        visited[ind] = 1;
        dist[ind] = {dist1, par};
        for(auto u : adjl[ind]){
            if(!visited[u.first]){
                q.push({-(u.second+dist1),{u.first,ind}});
            }
        }
    }
    for(int i=1;i<=n;i++) cout<<dist[i].first<<" "<<dist[i].second<<endl;
}
