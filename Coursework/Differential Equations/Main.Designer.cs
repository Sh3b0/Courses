namespace Differential_Equations
{
    partial class Main
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea1 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
            System.Windows.Forms.DataVisualization.Charting.Legend legend1 = new System.Windows.Forms.DataVisualization.Charting.Legend();
            System.Windows.Forms.DataVisualization.Charting.Series series1 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series2 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series3 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series4 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea2 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
            System.Windows.Forms.DataVisualization.Charting.Legend legend2 = new System.Windows.Forms.DataVisualization.Charting.Legend();
            System.Windows.Forms.DataVisualization.Charting.Series series5 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series6 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series7 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea3 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
            System.Windows.Forms.DataVisualization.Charting.Legend legend3 = new System.Windows.Forms.DataVisualization.Charting.Legend();
            System.Windows.Forms.DataVisualization.Charting.Series series8 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series9 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series10 = new System.Windows.Forms.DataVisualization.Charting.Series();
            this.chart_methods = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.textBox_y0 = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.textBox_N = new System.Windows.Forms.TextBox();
            this.textBox_X = new System.Windows.Forms.TextBox();
            this.textBox_x0 = new System.Windows.Forms.TextBox();
            this.label_N = new System.Windows.Forms.Label();
            this.label_X = new System.Windows.Forms.Label();
            this.label_x0 = new System.Windows.Forms.Label();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.checkBox3 = new System.Windows.Forms.CheckBox();
            this.button_plot = new System.Windows.Forms.Button();
            this.checkBox2 = new System.Windows.Forms.CheckBox();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.chart_lte = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.chart_gte = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.panel1 = new System.Windows.Forms.Panel();
            this.textBox_n0 = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.chart_methods)).BeginInit();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chart_lte)).BeginInit();
            this.tabPage3.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chart_gte)).BeginInit();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // chart_methods
            // 
            chartArea1.Name = "ChartArea1";
            this.chart_methods.ChartAreas.Add(chartArea1);
            legend1.Name = "Legend1";
            this.chart_methods.Legends.Add(legend1);
            this.chart_methods.Location = new System.Drawing.Point(2, 6);
            this.chart_methods.Name = "chart_methods";
            series1.BorderWidth = 2;
            series1.ChartArea = "ChartArea1";
            series1.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series1.Legend = "Legend1";
            series1.LegendText = "Euler";
            series1.Name = "Series1";
            series2.BorderWidth = 2;
            series2.ChartArea = "ChartArea1";
            series2.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series2.Legend = "Legend1";
            series2.LegendText = "Improved Euler";
            series2.Name = "Series2";
            series3.BorderWidth = 2;
            series3.ChartArea = "ChartArea1";
            series3.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series3.Legend = "Legend1";
            series3.LegendText = "Runge Kutta";
            series3.Name = "Series3";
            series4.BorderDashStyle = System.Windows.Forms.DataVisualization.Charting.ChartDashStyle.Dot;
            series4.BorderWidth = 2;
            series4.ChartArea = "ChartArea1";
            series4.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series4.Color = System.Drawing.Color.Lime;
            series4.Legend = "Legend1";
            series4.LegendText = "Exact";
            series4.Name = "Series4";
            this.chart_methods.Series.Add(series1);
            this.chart_methods.Series.Add(series2);
            this.chart_methods.Series.Add(series3);
            this.chart_methods.Series.Add(series4);
            this.chart_methods.Size = new System.Drawing.Size(557, 377);
            this.chart_methods.TabIndex = 0;
            this.chart_methods.Text = "chart1";
            // 
            // textBox_y0
            // 
            this.textBox_y0.Location = new System.Drawing.Point(45, 71);
            this.textBox_y0.Name = "textBox_y0";
            this.textBox_y0.Size = new System.Drawing.Size(100, 20);
            this.textBox_y0.TabIndex = 16;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Times New Roman", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(19, 67);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(25, 23);
            this.label1.TabIndex = 15;
            this.label1.Text = "y₀";
            // 
            // textBox_N
            // 
            this.textBox_N.Location = new System.Drawing.Point(45, 208);
            this.textBox_N.Name = "textBox_N";
            this.textBox_N.Size = new System.Drawing.Size(100, 20);
            this.textBox_N.TabIndex = 14;
            // 
            // textBox_X
            // 
            this.textBox_X.Location = new System.Drawing.Point(45, 117);
            this.textBox_X.Name = "textBox_X";
            this.textBox_X.Size = new System.Drawing.Size(100, 20);
            this.textBox_X.TabIndex = 13;
            // 
            // textBox_x0
            // 
            this.textBox_x0.Location = new System.Drawing.Point(45, 29);
            this.textBox_x0.Name = "textBox_x0";
            this.textBox_x0.Size = new System.Drawing.Size(100, 20);
            this.textBox_x0.TabIndex = 12;
            // 
            // label_N
            // 
            this.label_N.AutoSize = true;
            this.label_N.Font = new System.Drawing.Font("Times New Roman", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_N.Location = new System.Drawing.Point(19, 207);
            this.label_N.Name = "label_N";
            this.label_N.Size = new System.Drawing.Size(21, 19);
            this.label_N.TabIndex = 11;
            this.label_N.Text = "N";
            // 
            // label_X
            // 
            this.label_X.AutoSize = true;
            this.label_X.Font = new System.Drawing.Font("Times New Roman", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_X.Location = new System.Drawing.Point(19, 118);
            this.label_X.Name = "label_X";
            this.label_X.Size = new System.Drawing.Size(20, 19);
            this.label_X.TabIndex = 10;
            this.label_X.Text = "X";
            // 
            // label_x0
            // 
            this.label_x0.AutoSize = true;
            this.label_x0.Font = new System.Drawing.Font("Times New Roman", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_x0.Location = new System.Drawing.Point(19, 26);
            this.label_x0.Name = "label_x0";
            this.label_x0.Size = new System.Drawing.Size(26, 23);
            this.label_x0.TabIndex = 9;
            this.label_x0.Text = "x₀";
            // 
            // checkBox1
            // 
            this.checkBox1.AutoSize = true;
            this.checkBox1.Checked = true;
            this.checkBox1.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBox1.Location = new System.Drawing.Point(35, 259);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(50, 17);
            this.checkBox1.TabIndex = 6;
            this.checkBox1.Text = "Euler";
            this.checkBox1.UseVisualStyleBackColor = true;
            this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
            // 
            // checkBox3
            // 
            this.checkBox3.AutoSize = true;
            this.checkBox3.Checked = true;
            this.checkBox3.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBox3.Location = new System.Drawing.Point(35, 305);
            this.checkBox3.Name = "checkBox3";
            this.checkBox3.Size = new System.Drawing.Size(86, 17);
            this.checkBox3.TabIndex = 8;
            this.checkBox3.Text = "Runge Kutta";
            this.checkBox3.UseVisualStyleBackColor = true;
            this.checkBox3.CheckedChanged += new System.EventHandler(this.checkBox3_CheckedChanged);
            // 
            // button_plot
            // 
            this.button_plot.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.button_plot.Location = new System.Drawing.Point(45, 338);
            this.button_plot.Name = "button_plot";
            this.button_plot.Size = new System.Drawing.Size(100, 34);
            this.button_plot.TabIndex = 5;
            this.button_plot.Text = "Plot";
            this.button_plot.UseVisualStyleBackColor = true;
            this.button_plot.Click += new System.EventHandler(this.Plot_click);
            // 
            // checkBox2
            // 
            this.checkBox2.AutoSize = true;
            this.checkBox2.Checked = true;
            this.checkBox2.CheckState = System.Windows.Forms.CheckState.Checked;
            this.checkBox2.Location = new System.Drawing.Point(35, 282);
            this.checkBox2.Name = "checkBox2";
            this.checkBox2.Size = new System.Drawing.Size(97, 17);
            this.checkBox2.TabIndex = 7;
            this.checkBox2.Text = "Improved Euler";
            this.checkBox2.UseVisualStyleBackColor = true;
            this.checkBox2.CheckedChanged += new System.EventHandler(this.checkBox2_CheckedChanged);
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Location = new System.Drawing.Point(12, 12);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(572, 415);
            this.tabControl1.TabIndex = 15;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.chart_methods);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(564, 389);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Methods";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.chart_lte);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(564, 389);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Local Errors";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // chart_lte
            // 
            chartArea2.Name = "ChartArea1";
            this.chart_lte.ChartAreas.Add(chartArea2);
            legend2.Name = "Legend1";
            this.chart_lte.Legends.Add(legend2);
            this.chart_lte.Location = new System.Drawing.Point(6, 6);
            this.chart_lte.Name = "chart_lte";
            series5.BorderWidth = 2;
            series5.ChartArea = "ChartArea1";
            series5.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series5.Legend = "Legend1";
            series5.LegendText = "Euler";
            series5.Name = "Series1";
            series6.BorderWidth = 2;
            series6.ChartArea = "ChartArea1";
            series6.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series6.Legend = "Legend1";
            series6.LegendText = "Improved Euler";
            series6.Name = "Series2";
            series7.BorderWidth = 2;
            series7.ChartArea = "ChartArea1";
            series7.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series7.Legend = "Legend1";
            series7.LegendText = "Runge Kutta";
            series7.Name = "Series3";
            this.chart_lte.Series.Add(series5);
            this.chart_lte.Series.Add(series6);
            this.chart_lte.Series.Add(series7);
            this.chart_lte.Size = new System.Drawing.Size(552, 380);
            this.chart_lte.TabIndex = 24;
            this.chart_lte.Text = "chart_lte";
            // 
            // tabPage3
            // 
            this.tabPage3.Controls.Add(this.chart_gte);
            this.tabPage3.Location = new System.Drawing.Point(4, 22);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage3.Size = new System.Drawing.Size(564, 389);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "Global Errors";
            this.tabPage3.UseVisualStyleBackColor = true;
            // 
            // chart_gte
            // 
            chartArea3.Name = "ChartArea1";
            this.chart_gte.ChartAreas.Add(chartArea3);
            legend3.Name = "Legend1";
            this.chart_gte.Legends.Add(legend3);
            this.chart_gte.Location = new System.Drawing.Point(3, 3);
            this.chart_gte.Name = "chart_gte";
            series8.BorderWidth = 2;
            series8.ChartArea = "ChartArea1";
            series8.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series8.Legend = "Legend1";
            series8.LegendText = "Euler";
            series8.Name = "Series1";
            series9.BorderWidth = 2;
            series9.ChartArea = "ChartArea1";
            series9.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series9.Legend = "Legend1";
            series9.LegendText = "Improved Euler";
            series9.Name = "Series2";
            series10.BorderWidth = 2;
            series10.ChartArea = "ChartArea1";
            series10.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series10.Legend = "Legend1";
            series10.LegendText = "Runge Kutta";
            series10.Name = "Series3";
            this.chart_gte.Series.Add(series8);
            this.chart_gte.Series.Add(series9);
            this.chart_gte.Series.Add(series10);
            this.chart_gte.Size = new System.Drawing.Size(555, 380);
            this.chart_gte.TabIndex = 25;
            this.chart_gte.Text = "chart_gte";
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.Color.Silver;
            this.panel1.Controls.Add(this.textBox_n0);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.textBox_x0);
            this.panel1.Controls.Add(this.label_x0);
            this.panel1.Controls.Add(this.button_plot);
            this.panel1.Controls.Add(this.textBox_y0);
            this.panel1.Controls.Add(this.checkBox3);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.checkBox1);
            this.panel1.Controls.Add(this.checkBox2);
            this.panel1.Controls.Add(this.textBox_X);
            this.panel1.Controls.Add(this.label_X);
            this.panel1.Controls.Add(this.textBox_N);
            this.panel1.Controls.Add(this.label_N);
            this.panel1.Location = new System.Drawing.Point(602, 34);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(181, 389);
            this.panel1.TabIndex = 17;
            // 
            // textBox_n0
            // 
            this.textBox_n0.Location = new System.Drawing.Point(45, 162);
            this.textBox_n0.Name = "textBox_n0";
            this.textBox_n0.Size = new System.Drawing.Size(100, 20);
            this.textBox_n0.TabIndex = 18;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Times New Roman", 15.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label2.Location = new System.Drawing.Point(19, 158);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(26, 23);
            this.label2.TabIndex = 17;
            this.label2.Text = "n₀";
            // 
            // Main
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.ClientSize = new System.Drawing.Size(795, 440);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.tabControl1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Name = "Main";
            this.Text = "Differential Equations";
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.chart_methods)).EndInit();
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.chart_lte)).EndInit();
            this.tabPage3.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.chart_gte)).EndInit();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion
        private System.Windows.Forms.DataVisualization.Charting.Chart chart_methods;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.CheckBox checkBox3;
        private System.Windows.Forms.Button button_plot;
        private System.Windows.Forms.CheckBox checkBox2;
        private System.Windows.Forms.TextBox textBox_N;
        private System.Windows.Forms.TextBox textBox_X;
        private System.Windows.Forms.TextBox textBox_x0;
        private System.Windows.Forms.Label label_N;
        private System.Windows.Forms.Label label_X;
        private System.Windows.Forms.Label label_x0;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.TextBox textBox_y0;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.DataVisualization.Charting.Chart chart_lte;
        private System.Windows.Forms.TabPage tabPage3;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.TextBox textBox_n0;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.DataVisualization.Charting.Chart chart_gte;
    }
}

