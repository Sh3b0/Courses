#include<bits/stdc++.h>

using namespace std;

/**
	Input:
	12000000
	385
	30
	50
	Output should be ~20000
*/

int main()
{
    long long n;
    int x1,t1,d;
    double a,r;

    cout<<"Forecast of the amount of the infected people in Wuhan\n";

    cout<<"Input city population N: ";
    cin>>n;
    cout<<'\n';

    cout<<"Input the official statistics : \n";
    cout<<"Input the amount of infected people : ";
    cin>>x1;
    cout<<'\n';
    cout<<"Input the number of the day : ";
    cin>>t1;
    cout<<'\n';

    a = -log((n+1.0-x1)/(x1*n))/(t1*(n+1.0));

    cout<<"Input the day of the forecast : ";
    cin>>d;
    cout<<'\n';

    r = 1.0 /( (n/(n+1.0)) * exp(-a*d*(n+1.0)) + 1.0/(n+1.0) );

    cout<<"Result (amount of infected people on day "<< d <<") = " << (long long)r << endl;
}
