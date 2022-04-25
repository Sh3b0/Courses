using System;

namespace Differential_Equations
{
    public class Euler : Numerical_Method
    {
        public new double[] x, y;
        public double[] e;
        public Euler(int N, double x0, double y0, double X) : base(N, x0, y0, X)
        {
            x = new double[N + 1];
            y = new double[N + 1];
            e = new double[N + 1];
            x[0] = x0;
            y[0] = y0;
        }

        // Prepares the XY values for Euler method graphs.
        public new double Calculate()
        {
            double mxe = 0;
            for (int i = 1; i < N; i++)
            {
                x[i] = x[i - 1] + h;
                y[i] = y[i - 1] + h * F(x[i - 1], y[i - 1]);

                e[i] = Math.Abs(y[i] - Exact(x[i]));
                mxe = Math.Max(mxe, e[i]);

                CheckOverflow(x[i], y[i], e[i]);

                // Debug.WriteLine("EM: " + x[i] + " " + y[i]);
            }
            return mxe;
        }
    }
}