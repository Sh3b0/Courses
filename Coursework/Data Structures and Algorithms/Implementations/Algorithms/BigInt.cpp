string add(string a, string b) // Sum Strings
{
    if (a == "0")return b;
    else if (b == "0")return a;
    while (b.size() < a.size()) b.insert(0, "0");
    while (a.size() < b.size()) a.insert(0, "0");
    int p = int(a.size()) - 1, q = 0;
    string r;
    while (p >= 0) {
        int c = (a[p] - '0') + (b[p] - '0') + q;
        q = c / 10;
        r += char(c % 10 + 48);
        if (!p)r += char(c / 10 + 48);
        p--;
    }
    reverse(r.begin(), r.end());
    while (r[0] == '0')r.erase(0, 1);
    return r;
}

string mul(string a, string b) // Multiply Strings
{
    if (a == "0" || b == "0")return "0";
    else if (a == "1")return b;
    else if (b == "1")return a;
    if (b.size() > a.size())swap(a, b);
    int pa, pb = int(b.size()) - 1, q, z = 0;
    string r, sum = "0";
    while (pb >= 0) {
        pa = int(a.size()) - 1;
        q = 0;
        r.assign(z, '0');
        while (pa >= 0) {
            int c = (a[pa] - '0') * (b[pb] - '0') + q;
            q = c / 10;
            r += char(c % 10 + 48);
            if (!pa)r += char(c / 10 + 48);
            pa--;
        }
        pb--;
        reverse(r.begin(), r.end());
        sum = add(sum, r);
        z++;
    }
    return sum;
}