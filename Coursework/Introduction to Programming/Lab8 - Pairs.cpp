#include <iostream>

using namespace std;

template<typename T>
class Pair1
{
    T a,b;
public:
    Pair1(T t1, T t2)
    {
        a=t1;
        b=t2;
    }
    T first()
    {
        return a;
    }
    T second()
    {
        return b;
    }
};

template<typename T1, typename T2>
class Pair2
{
    T1 a;
    T2 b;
public:
    Pair2(T1 t1, T2 t2)
    {
        a=t1;
        b=t2;
    }
    T1 first()
    {
        return a;
    }
    T2 second()
    {
        return b;
    }
};

template<typename T>
class StringValuePair : public Pair2<string,T>
{
public:
    StringValuePair(string t1, T t2) : Pair2<string,T>(t1,t2) {};
};


int main()
{
    Pair1<int>p1(1, 2);
    cout<<p1.first()<<" "<<p1.second()<<'\n';

    Pair2<int,string> p2(1, "Ahmed");
    cout<<p2.first()<<" "<<p2.second()<<'\n';

    StringValuePair<int> p3("Ahmed", 5);
    cout<<p3.first()<<" "<<p3.second()<<'\n';
}
