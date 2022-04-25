#include <bits/stdc++.h>

using namespace std;
typedef long long ll;

class LengthsNotEqualException : public exception{
string errmsg;
public:
    LengthsNotEqualException(string msg){
        errmsg = msg;
    }
    string what(){
        return errmsg;
    }
};

int asz, bsz;

template <typename A, typename B>
ll DotProduct(A a, B b)
{
    if(asz != bsz) throw LengthsNotEqualException("The lengths of two containers are not equal\n");
    ll r = 0;
    int c1 = 0, c2;
    for(auto u : a){
        c1++;
        c2 = 0;
        for(auto v : b){
            c2++;
            if(c1 == c2){
                r += u*v;
                break;
            }
        }
    }
    return r;
}

int main()
{
    cin>>asz;
    vector<int>a;
    a.resize(asz);

    for(int i=0;i<asz;i++){
        cin>>a[i];
    }

    cin>>bsz;
    vector<int>b;
    b.resize(bsz);

    for(int i=0;i<bsz;i++){
        cin>>b[i];
    }

    try{
        cout << DotProduct(a, b) << endl;
    }
    catch (LengthsNotEqualException e){
        cout<<e.what();
    }
    catch(...){
        cout<<"This is not supposed to happen\n";
    }
}
