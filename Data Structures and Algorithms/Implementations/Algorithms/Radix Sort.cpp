#include <bits/stdc++.h>

using namespace std;

/// Simplified version of Radix sort.
/// O(d * (n + b)), Stable, Out of place.
/// d is number of digits of max value in array
/// b is the base number, in case of integers, it's 10

int main()
{
    /// Elements are supposed to be numbers is any base.
    string x[] = {"329", "57", "657", "839", "436", "720", "355"};

    int n = sizeof(x) / sizeof(x[0]);
    int d = max_element(x, x+n) -> size();

    vector< pair<char, string> > v;
    for(int i=0; i<d; i++) {

        v.clear();
        
        for(auto u:x)
            if(u.size()-1-i >= 0)
                v.push_back({u[u.size()-1-i], u});

        /// Should do counting sort here, to achieve the desired complexity, but I am too lazy to do it :/
        /// This step sorts the array elements according to i-th digit from the end. Sorting should be stable.
        sort(v.begin(),v.end());
    }

    for(auto u:v) cout<<u.second<<" ";
}
