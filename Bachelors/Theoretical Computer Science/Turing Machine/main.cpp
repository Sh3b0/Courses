// Code by Ahmed Nouralla

#include <iostream>
#include <map>
#include <regex>

using namespace std;

string inp;
map<int, char> mem;

/// Get input at a specific index.
char get_inp(int idx){
    if(idx < 0 || idx >= (int)inp.size()) return '_';
    return inp[idx];
}

/// Get memory at a specific index.
char get_mem(int idx){
    if(!mem[idx]) return '_';
    return mem[idx];
}

/// Replace the character at memory location number idx with n
void set_mem(int idx, char n){
    if(n=='_') return;
    mem[idx] = n;
}

int main()
{
    /// Reading input.
    freopen("input.txt","r",stdin);
    freopen("output.txt","w",stdout);

    cin >> inp;
    regex r("^(([0][#])|([1][0-1]*[#]))(([0])|([1][0-1]*))$");
    if(!regex_match(inp, r)) return cout<<"Invalid input", 0;

    /// Hard-Coding the TM from the task.
    
    map<pair<string,string>, pair<string,string> > t;

    t[{"q0", "0,Z"}] = {"q0", "Z,(S,R)"};
    t[{"q0", "1,Z"}] = {"q0", "Z,(S,R)"};
    t[{"q0", "0,_"}] = {"q0", "0,(R,R)"};
    t[{"q0", "1,_"}] = {"q0", "1,(R,R)"};
    t[{"q0", "#,_"}] = {"q1", "_,(R,L)"};

    t[{"q1", "1,1"}] = {"q1", "1,(R,L)"};
    t[{"q1", "0,0"}] = {"q1", "0,(R,L)"};
    t[{"q1", "1,0"}] = {"q1", "0,(R,L)"};
    t[{"q1", "0,1"}] = {"q1", "1,(R,L)"};
    t[{"q1", "_,1"}] = {"q4", "1,(S,S)"};
    t[{"q1", "_,0"}] = {"q4", "0,(S,S)"};
    t[{"q1", "_,Z"}] = {"q2", "Z,(L,S)"};

    t[{"q2", "0,Z"}] = {"q2", "Z,(L,S)"};
    t[{"q2", "1,Z"}] = {"q2", "Z,(L,S)"};
    t[{"q2", "#,Z"}] = {"q3", "Z,(R,R)"};

    t[{"q3", "1,1"}] = {"q3", "1,(R,R)"};
    t[{"q3", "0,0"}] = {"q3", "0,(R,R)"};
    t[{"q3", "0,1"}] = {"q4", "1,(S,S)"};

    /// Setting the initial state and symbol.
	
    string init_q = "q0";
    string cur_state = "q0";
    mem[0] = 'Z';

    int inp_head = 0, mem_head = 0;
    bool flag = 0;

    /// Simulating the TM.
    while(1){
        string action = "";
        action += get_inp(inp_head);
        action += ",";
        action += get_mem(mem_head);

        /// Outputting Configuration.
        // -------------------------
        start:
        cout<<cur_state<<", ";
        for(int i=0;i<(int)inp.size();i++){
            if(inp_head==i)cout<<"^";
            cout<<inp[i];
        }
        if(inp_head==(int)inp.size())cout<<"^";

        cout<<", ";
        for(int i=0;i<(int)mem.size();i++){
            if(mem_head==i)cout<<"^";
            if(mem[i]=='0'||mem[i]=='1'||mem[i]=='Z')cout<<mem[i];
        }
        cout<<" \n";
        // -------------------------

        /// If we reached the final state, TM accepts.
        if(cur_state == "q4") return cout<<"YES", 0;

        /// Prepare the next move.
        pair<string, string> move = t[{cur_state, action}];

        /// If we can't move, TM rejects.
        if(move.first == "" && move.second == ""){
            if(!flag){
                flag = 1;
                goto start;
            }
            return cout<<"NO", 0;
        }

        /// Change state and move heads.
        cur_state = move.first;

        set_mem(mem_head, move.second[0]);

        if(move.second[3]=='L') inp_head--;
        else if(move.second[3]=='R') inp_head++;

        if(move.second[5]=='L') mem_head--;
        else if(move.second[5]=='R') mem_head++;
    }
}
