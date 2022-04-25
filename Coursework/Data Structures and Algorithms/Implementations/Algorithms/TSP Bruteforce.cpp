#include <iostream>
#include <algorithm>

using namespace std;

int main() {
    int n, a, b, d;
    cin >> n;
    int x[n], m[n][n];
    for (int i = 0; i < n; i++) x[i] = i;
    for (int i = 0; i < n * (n - 1) / 2; i++) {
        cin >> a >> b >> d;
        m[a][b] = d;
        m[b][a] = d;
    }
    int mn = INT_MAX;
    do {
        if (x[0] != 0)continue;
        int cur = 0;
        for (int i = 0; i < n - 1; i++) {
            cur += m[x[i]][x[i + 1]];
        }
        cur += m[x[n - 1]][0];
        mn = min(mn, cur);
    } while (next_permutation(x, x + n));
    cout << mn;
}
