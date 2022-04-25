using System;
using System.Windows.Forms;

namespace Differential_Equations
{
    // The main class that contains common fields and methods needed by all descendents
    public class Numerical_Method
    {
        public double[] x, y;
        public int N;
        protected double c, x0, y0, X, h;

        public Numerical_Method(int N, double x0, double y0, double X)
        {
            this.N = N + 1;
            this.X = X;
            this.x0 = x0;
            this.y0 = y0;
            if (x0 == 0 || x0 == 1.0 / y0)
            {
                MessageBox.Show("IVP has no solution");
                Application.Exit();
            }
            c = Math.Pow(x0, 4 / 3.0) * (x0 * y0 - 2) / (x0 * x0 * y0 - x0);
            h = (X - x0) / N;
        }

        // Prepares the XY values for the exact graph with step 0.01
        public void Calculate()
        {
            int n = (int)((X - x0) / 0.01);
            x = new double[n + 1];
            y = new double[n + 1];
            int co = 0;
            for (double i = x0; i <= X; i += 0.01)
            {
                x[co] = i;
                y[co] = Exact(i);
                co++;
            }
        }

        // The function that the application operates on: f(x, y) = y'
        protected double F(double x, double y)
        {
            if (x == 0.0)
            {
                MessageBox.Show("Interval [x0, X] has a point of discontinuity");
                Application.Exit();
            }
            return -y * y / 3 - 2 / (3 * x * x);
        }

        // Returns the exact solution calculated at some point x. 
        protected double Exact(double x)
        {
            if (x == 0.0)
            {
                MessageBox.Show("Interval [x0, X] has a point of discontinuity");
                Application.Exit();
            }

            return c / (Math.Pow(x0, 4 / 3.0) - c * x) + 2.0 / x;
        }

        // Checks 3 values for Overflow
        protected void CheckOverflow(double a, double b, double c)
        {
            if(double.IsInfinity(a) || double.IsInfinity(b) || double.IsInfinity(c))
            {
                MessageBox.Show("Overflow: Calculations exceeded the range for Double data type");
                Application.Exit();
            }
        }
    }
}
