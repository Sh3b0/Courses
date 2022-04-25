#include <bits/stdc++.h>

using namespace std;

/// Merge Sort O( n * log(n) )

deque<int>c;
int x[] = {51, 6851,-561,841,58, 64, 6},n;

deque<int> merge(deque<int>a, deque<int>b)  /// Merges two sorted lists
{

	// Debug
    /**
        cout<<"Now I'll merge 2 arrays:\n";
        for(auto u:a)cout<<u<<" ";cout<<endl;
        for(auto u:b)cout<<u<<" ";cout<<endl;
    */

    c.clear();

    while(!a.empty()&&!b.empty())
    {
        if(a.front()<b.front())
        {
            c.push_back(a.front());
            a.pop_front();
        }
        else
        {
            c.push_back(b.front());
            b.pop_front();
        }
    }

    if(a.empty())       c.insert(c.end(),b.begin(),b.end());
    else if(b.empty())  c.insert(c.end(),a.begin(),a.end());

    return c;
}

deque<int> sort(int l,int r)
{
    if(l==r)
    {
        deque<int>d;
        d.push_back(x[l]);
        return d;
    }
    int m = (l+r)/2;
    return merge(sort(l,m),sort(m+1,r));
}

int main()
{
    n = sizeof(x)/sizeof(x[0]);
    for(int u:sort(0,n-1))cout<<u<<" ";
}
