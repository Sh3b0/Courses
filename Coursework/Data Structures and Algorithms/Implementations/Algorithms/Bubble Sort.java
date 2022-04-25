// Bubble Sort O(n^2), In-Place, stable.

public class Main {

    private static void swap(int[] x, int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void main(String[] args) {
        int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
        boolean flag;
        do {
            flag = false; // true if we made at least one swap.
            for (int i = 1; i < x.length; i++) {
                if (x[i] < x[i - 1]) {
                    swap(x, i, i - 1);
                    flag = true;
                }
            }
        } while (flag);
        for (int i : x) System.out.print(i + " ");
    }
}