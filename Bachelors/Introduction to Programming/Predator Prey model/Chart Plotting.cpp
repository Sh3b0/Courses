/**
    Author: Ahmed Nouralla - BS19-02 - a.shaaban@innopolis university
    This code is tested on
        - A windows machine
        - With gnuplot installed in the directory C:\gnuplot
        - With GNU GCC Compiler following the 1999 ISO C language standard [-std=c99].
    And is not guaranteed to work on other machines having different properties.
*/
#include <bits/stdc++.h>
using namespace std;
double v0, k0, a1, a2, b1, b2;
int T, N, c = 0;

double v(double t)
{
    if(!t) return v0+a2/b2;
    return v0*cos(sqrt(a1*a2)*t) - k0*((sqrt(a2)*b1)/(b2*sqrt(a1)))*sin(sqrt(a1*a2)*t) + a2/b2;
}

double k(double t)
{
    if(!t) return k0+a1/b1;
    return v0*((sqrt(a1)*b2)/(b1*sqrt(a2)))*sin(sqrt(a1*a2)*t) + k0*cos(sqrt(a1*a2)*t) + a1/b1;
}

void plot()
{
    FILE* pipe = _popen("C:\\gnuplot\\bin\\gnuplot -persist", "w");
    if(pipe != NULL)
    {
        fprintf(pipe, "%s\n" ,"set terminal qt 0");
        fprintf(pipe, "%s\n", "plot '-' title 'victims' with lines, '-' title 'killers' with lines");

        for(double t = 0.0; t <= T; t += double(T)/N)
        {
            fprintf(pipe, "%f\t%f\n", t, v(t));
        }
        fprintf(pipe, "%s\n", "e");
        for(double t = 0.0; t <= T; t += double(T)/N)
        {
            fprintf(pipe, "%f\t%f\n", t, k(t));
        }
        fprintf(pipe, "%s\n", "e");
        fprintf(pipe, "%s\n" ,"set terminal qt 1");
        fprintf(pipe, "%s\n", "plot '-' title 'relation' with lines");
		
        // Please wait for some seconds for the second window to appear.
        for(double t = 0.0; t <= T; t += double(T)/N)
        {
            fprintf(pipe, "%f\t%f\n", v(t), k(t));
        }
        fflush(pipe);
        _pclose(pipe);
    }
    else
        cout<<"Error\n";
}

int main()
{
    cin>>v0>>k0>>a1>>b1>>a2>>b2>>T>>N;
    v0 -= a2/b2;
    k0 -= a1/b1;
    ostringstream V, K;
    cout<<"t:\n";
    for(double t = 0.0; t <= T; t += double(T)/N)
    {
        cout<<fixed<<setprecision(2)<<t;
        V<<fixed<<setprecision(2)<<v(t);
        K<<fixed<<setprecision(2)<<k(t);
        if(c != N)
        {
            cout<<" ";
            V<<" ";
            K<<" ";
        }
        else
        {
            cout<<"\nv:\n";
            V<<"\nk:\n";
            K<<"\n";
        }
        c++;
    }
    cout<<V.str()<<K.str();
    plot();
}