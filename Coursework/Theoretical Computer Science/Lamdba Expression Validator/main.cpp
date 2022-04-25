// Code by Ahmed Nouralla
#include <bits/stdc++.h>

using namespace std;

int Count(string s, string t) // Counts number of t's in s
{
    int n = s.size(), m = t.size(), r = 0;
    for (int i = 0; i <= n-m; i++)
    {
        int j;
        for (j = 0; j < n; j++){
            if (s[i+j] != t[j]) break;
        }
        if (j == m)
        {
           r++;
           j=0;
        }
    }
    return r;
}

int main()
{
	// Input and Output
    freopen("input.txt","r",stdin);
    freopen("output.txt","w",stdout);

    string s;
    cin>>s;
    int n = s.size(), c = Count(s,"(\\");
	
	// Check for illegeal characters
    for(int i = 0; i < n; i++){
        if(s[i]=='(' || s[i]==')' || s[i]=='.' || s[i]=='\\') continue;
        if(isalpha(s[i]) || isdigit(s[i])) // e \in v
                s[i] = 'e';
        else
                return cout<<"NO\n", 0;
    }
	
	// Applying production rules
    while(1){
        int f1 = s.find("ee");
        int f2 = s.find("(e)e");
        int f3 = s.find("\\e.e");

        if(f1 != -1){
            s.replace(f1, 2, "e");
        }
        else if(f2 != -1){
            s.replace(f2, 4, "e");
        }
        else if(f3 != -1){
            s.replace(f3, 4, "e");
        }
        else break;
    }

	// If the answer is an element of \Lambda then YES, otherwise NO.
    (s == "e") ? cout << "YES\n" << c << '\n' : cout << "NO\n";
}