using System;
using System.Windows.Forms;

namespace Differential_Equations
{
    public partial class Main : Form
    {
        public Main()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            MaximizeBox = false;

            // Initial textboxes data.
            textBox_x0.Text = "1";
            textBox_y0.Text = "2";
            textBox_X.Text = "5";
            textBox_n0.Text = "3";
            textBox_N.Text = "10";
            Plot_click(sender, e);
        }

        // Plot button clicked
        private void Plot_click(object sender, EventArgs e)
        {
            double x0 = 0, y0 = 0, X = 0;
            int n0 = 0, N = 0;
            try
            {
                x0 = double.Parse(textBox_x0.Text);
                y0 = double.Parse(textBox_y0.Text);
                X = double.Parse(textBox_X.Text);
                n0 = int.Parse(textBox_n0.Text);
                N = int.Parse(textBox_N.Text);
                if (n0 <= 0 || N <= 0 || n0 >= N || x0 >= X) throw new Exception(); 
            }
            catch
            {
                MessageBox.Show("Invalid input\n");
                Application.Exit();
            }

            
            int[] cnt = new int[N - n0 + 1];
            double[] EE = new double[N - n0 + 1], IEE = new double[N - n0 + 1], RKE = new double[N - n0 + 1];
            for (int i = n0; i <= N; i++)
            {
                cnt[i - n0] = i;
                Euler tmp1 = new Euler(i, x0, y0, X);
                Improved_Euler tmp2 = new Improved_Euler(i, x0, y0, X);
                Runge_Kutta tmp3 = new Runge_Kutta(i, x0, y0, X);
                
                EE[i - n0] = tmp1.Calculate();
                IEE[i - n0] = tmp2.Calculate();
                RKE[i - n0] = tmp3.Calculate();
            }


            Numerical_Method exact = new Numerical_Method(N, x0, y0, X);
            exact.Calculate();
            chart_methods.Series[3].Points.DataBindXY(exact.x, exact.y);

            if (checkBox1.Checked)
            {
                Euler m1 = new Euler(N, x0, y0, X);
                m1.Calculate();
                chart_methods.Series[0].Points.DataBindXY(m1.x, m1.y);
                chart_lte.Series[0].Points.DataBindXY(m1.x, m1.e);
                chart_gte.Series[0].Points.DataBindXY(cnt, EE);
            }
            if (checkBox2.Checked)
            {
                Improved_Euler m2 = new Improved_Euler(N, x0, y0, X);
                m2.Calculate();
                chart_methods.Series[1].Points.DataBindXY(m2.x, m2.y);
                chart_lte.Series[1].Points.DataBindXY(m2.x, m2.e);
                chart_gte.Series[1].Points.DataBindXY(cnt, IEE);
            }
            if (checkBox3.Checked)
            {
                Runge_Kutta m3 = new Runge_Kutta(N, x0, y0, X);
                m3.Calculate();
                chart_methods.Series[2].Points.DataBindXY(m3.x, m3.y);
                chart_lte.Series[2].Points.DataBindXY(m3.x, m3.e);
                chart_gte.Series[2].Points.DataBindXY(cnt, RKE);
            }

            chart_methods.ChartAreas[0].AxisX.Minimum = x0;
            chart_methods.ChartAreas[0].AxisX.Maximum = X;
            chart_lte.ChartAreas[0].AxisX.Minimum = x0;
            chart_lte.ChartAreas[0].AxisX.Maximum = X;
            chart_gte.ChartAreas[0].AxisX.Minimum = n0;
            chart_gte.ChartAreas[0].AxisX.Maximum = N;
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            chart_methods.Series[0].Enabled = checkBox1.Checked;
            chart_lte.Series[0].Enabled = checkBox1.Checked;
            chart_gte.Series[0].Enabled = checkBox1.Checked;
            Plot_click(sender, e);
        }

        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            chart_methods.Series[1].Enabled = checkBox2.Checked;
            chart_lte.Series[1].Enabled = checkBox2.Checked;
            chart_gte.Series[1].Enabled = checkBox2.Checked;
            Plot_click(sender, e);
        }

        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            chart_methods.Series[2].Enabled = checkBox3.Checked;
            chart_lte.Series[2].Enabled = checkBox3.Checked;
            chart_gte.Series[2].Enabled = checkBox3.Checked;
            Plot_click(sender, e);
        }
        
    }
}
