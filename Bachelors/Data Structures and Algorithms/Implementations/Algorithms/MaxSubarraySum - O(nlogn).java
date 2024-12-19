// Solving MSS using Divide-and-Conquer method - O(n * log(n))

import java.util.Scanner;

class Main {
    static int cross(int[] x, int l, int r, int m) {
        int t = 0, lmx = Integer.MIN_VALUE, rmx = Integer.MIN_VALUE;
        for (int i = m; i >= l; i--) {
            t += x[i];
            lmx = Math.max(lmx, t);
        }
        t = 0;
        for (int i = m + 1; i <= r; i++) {
            t += x[i];
            rmx = Math.max(rmx, t);
        }
        return lmx + rmx;
    }

    static int solve(int[] x, int l, int r) {
        if (l == r) return x[l];
        int m = (l + r) / 2;
        return max(solve(x, l, m), solve(x, m + 1, r), cross(x, l, r, m));
    }

    static int max(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int x[] = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = sc.nextInt();
        }
        System.out.println(solve(x, 0, n - 1));
    }
}