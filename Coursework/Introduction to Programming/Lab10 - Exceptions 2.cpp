#include <bits/stdc++.h>

using namespace std;

typedef long long ll;

class LengthsNotEqualException : public exception
{
    string errmsg;
public:
    LengthsNotEqualException(string msg)
    {
        errmsg = msg;
    }
    string what()
    {
        return errmsg;
    }
};

int asz, bsz;

double get_distance(vector<double> a, vector<double> b)
{
    if(asz!=bsz) throw invalid_argument("invalid_argument exception\n");
    vector<double> c;
    bool diff=0;
    for(auto i1 = a.begin(), i2 = b.begin(); i1!=a.end(),i2!=b.end(); i1++, i2++)
    {
        if(*i1!=*i2) diff = 1;
        c.push_back(*i2-*i1);
    }
    if(!diff) throw domain_error("domain_error exception\n");
    double magn = 0;
    for(auto u:c)
    {
        magn+=u*u;
    }
    magn = sqrt(magn);
    if(magn > 100.0) throw length_error("length_error exception\n");
    return magn;
}

int main()
{
    cin>>asz;
    vector<double>a;
    a.resize(asz);

    for(int i=0; i<asz; i++)
    {
        cin>>a[i];
    }

    cin>>bsz;
    vector<double>b;
    b.resize(bsz);

    for(int i=0; i<bsz; i++)
    {
        cin>>b[i];
    }

    try
    {
        cout << get_distance(a, b) << endl;
    }
    catch (invalid_argument e)
    {
        cout<<e.what();
    }
    catch (length_error e)
    {
        cout<<e.what();
    }
    catch (domain_error e)
    {
        cout<<e.what();
    }
    catch(...)
    {
        cout<<"This is not supposed to happen\n";
    }
}
