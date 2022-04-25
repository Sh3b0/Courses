using System;

namespace Differential_Equations
{
    public class Runge_Kutta : Numerical_Method
    {
        public new double[] x, y;
        public double[] e;
        public Runge_Kutta(int N, double x0, double y0, double X) : base(N, x0, y0, X)
        {
            x = new double[N + 1];
            y = new double[N + 1];
            e = new double[N + 1];
            x[0] = x0;
            y[0] = y0;
        }

        // Prepares the XY values for Runge-Kutta method graphs.
        public new double Calculate()
        {
            double mxe = 0;
            for (int i = 1; i < N; i++)
            {
                x[i] = x[i - 1] + h;
                double k1, k2, k3, k4;
                k1 = F(x[i - 1], y[i - 1]);
                k2 = F(x[i - 1] + h / 2.0, y[i - 1] + h / 2.0 * k1);
                k3 = F(x[i - 1] + h / 2.0, y[i - 1] + h / 2.0 * k2);
                k4 = F(x[i - 1] + h, y[i - 1] + h * k3);
                y[i] = y[i - 1] + h / 6.0 * (k1 + 2 * k2 + 2 * k3 + k4);

                e[i] = Math.Abs(y[i] - Exact(x[i]));
                mxe = Math.Max(mxe, e[i]);

                CheckOverflow(x[i], y[i], e[i]);

                //Debug.WriteLine("RKM: " + x[i] + " " + y[i]);
            }
            return mxe;
        }
    }
}