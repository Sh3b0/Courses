#include <bits/stdc++.h>
using namespace std;

unordered_map<int, vector<int> > g;
unordered_map<int,bool> vis;
stack<int>s;

void dfs(int i){
    vis[i]=1;
    for(auto u:g[i]){
        if(!vis[u])dfs(u);
    }
    s.push(i);
}

int main()
{
    /** TopSoring this graph.

       (5) -> (0) <- (4)
        |             |
        V             V
       (2) -> (3) -> (1)

    */

    g[2].push_back(3);
    g[3].push_back(1);
    g[4].push_back(0);
    g[4].push_back(1);
    g[5].push_back(0);
    g[5].push_back(2);

    for(int i=0;i<=5;i++){
        if(!vis[i]){
            dfs(i);
        }
    }

    while(!s.empty()){
        cout<<s.top()<<" ";
        s.pop();
    }
}
