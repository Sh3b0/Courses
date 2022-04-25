#include<bits/stdc++.h>
#define MT(x,y,z) make_tuple(x,y,z)

using namespace std;

map<string,bool> vis;
map<pair<string,string>,vector<string> > m; /// {state, input} --> another state
map<pair<string,string>,vector<string> > p; /// {state, input} --> another state
vector<string>stat, alpha, init, fin, trans;

vector<string> split(string s)
{
    stringstream i(s);
    vector<string>r;
    string token;

    while(getline(i,token,','))
        r.push_back(token);

    return r;
}

void dfs(pair<string,string>s, map<pair<string,string>,vector<string> > t){
    vis[s.first]=1;
    for(auto u:t[s]){
        if(!vis[u]){
            for(auto a:alpha){
                dfs({u,a},t);
            }
        }
    }
}

void mal(){
    cout<<"Error:\nE5: Input file is malformed\n";
    exit(0);
}

void e1(string s){
    cout<<"Error:\nE1: A state '" + s + "' is not in the set of states\n";
    exit(0);
}

int main()
{
    freopen("fsa.txt","r",stdin);
    freopen("result.txt","w",stdout);

    bool w=0,w1=0,w3=0;

    string inp[5];
    for(int i=0; i<5; i++){
        cin>>inp[i];
    }

    int er[] = {8,7,9,8,7};

    if(inp[0].substr(0,er[0])!="states=[")mal();
    if(inp[1].substr(0,er[1])!="alpha=[")mal();
    if(inp[2].substr(0,er[2])!="init.st=[")mal();
    if(inp[3].substr(0,er[3])!="fin.st=[")mal();
    if(inp[4].substr(0,er[4])!="trans=[")mal();

    for(int i=0;i<5;i++){
        inp[i].erase(0,er[i]);
        inp[i].erase(inp[i].size()-1);
        if(inp[0].find(",,")!=-1)mal();
        for(int j=0;j<inp[i].size();j++){
            if(inp[i][j]==',')continue;
            if((i==1||i==4)&&inp[i][j]=='_')continue;
            if(i==4&&inp[i][j]=='>')continue;
            if(!isalpha(inp[i][j])&&!isdigit(inp[i][j]))mal();
        }
    }

    if(inp[0]=="")mal();
    if(inp[3]=="")w1=1;

    stat = split(inp[0]);
    alpha = split(inp[1]);
    init = split(inp[2]);
    fin = split(inp[3]);
    trans = split(inp[4]);

    if(init.empty()) return cout<<"Error:\nE4: Initial state is not defined\n", 0;

    map<string,bool>S_seen, A_seen; /// To mark previously seen states and alphabet.
    for(auto u:stat) S_seen[u]=1;
    for(auto u:alpha) A_seen[u]=1;

    /// First we check errors in order, if any of them occur, we output it and terminate.
    /// If no errors, we output report, then ALL warnings in order.

    ///----------------------CHECKING ERROR E1----------------------

    for(auto u:init)/// To check if any initial state is not one of the states.
        if(!S_seen[u])e1(u);

    for(auto u:fin) /// To check if any final state is not one of the states.
        if(!S_seen[u])e1(u);

    string a,b,c; /// Extract the 3 strings out of the transition string.
    int co=0; /// Counter
    for(auto i:trans){
        a="";b="";c="";co=0;
        //cout<<i<<endl;
        for(auto u:i){
            if(u=='>') co++;
            else{
                if(co==0)a+=u;
                else if(co==1)b+=u;
                else c+=u;
            }
        }
        //cout<<a<<" "<<b<<" "<<c<<" "<<endl;
        /// Check if any transition state is not one of the states.
        if(!S_seen[a])e1(a);
        if(!S_seen[c])e1(c);
        if(!A_seen[b])return cout<<"Error:\nE3: A transition '" + b + "' is not represented in the alphabet\n", 0;
        if(!m[{a,b}].empty()){
            for(auto u:m[{a,b}]){
                if(u!=c){w3=1;break;}
            }
        }
        /// Pre-checking Warning W3
        m[{a,b}].push_back(c); /// The REAL graph
        p[{a,b}].push_back(c); /// The pseudo undirected one.
        p[{c,b}].push_back(a);
        //cout<<"map of "<<a<<" , "<<b<<" = ";for(auto u:m[{a,b}])cout<<u<<" ";cout<<endl;

    }

    ///----------------------CHECKING ERROR E2----------------------
    int cc=0; /// Connected Components
    for(auto s:stat){
        if(!vis[s]){
            vis[s]=1;
            for(auto a:alpha){
                dfs({s,a},p);
            }
            cc++;
        }
    }
    if(cc>1)
        return cout<<"Error:\nE2: Some states are disjoint\n", 0;

    if(w3)
        return cout<<"Error:\nE6: FSA is nondeterministic\n", 0;

    /*
    cout<<"I survived\n";
    for(auto u:stat)cout<<u<<" ";cout<<endl;
    for(auto u:alpha)cout<<u<<" ";cout<<endl;
    for(auto u:trans)cout<<u<<" ";cout<<endl;
    */

    map<tuple<int,int,int>,string>R; /// R_(i,j)^-1

    for(int i=0;i<stat.size();i++){
        for(int j=0;j<stat.size();j++){
            for(auto u:alpha){
                if(!m[{stat[i], u}].empty()&&m[{stat[i], u}][0] == stat[j]) R[MT(i,j,-1)] += u + "|";
            }
            if(i==j){
                R[MT(i,j,-1)]+="eps";
            }
            else {
                if(R[MT(i,j,-1)]=="") R[MT(i,j,-1)]="{}";
                else R[MT(i,j,-1)].erase(R[MT(i,j,-1)].size()-1);
            }
            // printf("R[%d,%d,-1]=%s\n",i,j,R[MT(i,j,-1)].c_str());
        }
    }
    for(int k=0;k<stat.size();k++){
        for(int i=0;i<stat.size();i++){
            for(int j=0;j<stat.size();j++){
                    R[MT(i,j,k)]="("+R[MT(i,k,k-1)]+")("+R[MT(k,k,k-1)]+")*("+R[MT(k,j,k-1)]+")|("+R[MT(i,j,k-1)]+")";
                     // printf("R[%d,%d,%d]=%s\n",i,j,k,R[MT(i,j,k)].c_str());
                }
        }
    }
    map<string, int>idx; /// idx of state with a given name
    for(int i=0;i<stat.size();i++)idx[stat[i]]=i;

    string ans;
    for(auto u:fin){
        ans += R[MT(idx[init[0]],idx[u],stat.size()-1)]+"|";
    }
    if(!ans.empty()){
        ans.erase(ans.size()-1);
        cout<<ans<<'\n';
    }
    else cout<<"{}\n";
}
