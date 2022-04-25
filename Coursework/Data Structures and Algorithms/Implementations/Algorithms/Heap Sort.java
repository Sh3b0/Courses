public class Main {
    private static int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
    private static int size = x.length - 1;

    private static void swap(int[] x, int i, int j) {
        int tmp = x[i];
        x[i] = x[j];
        x[j] = tmp;
    }

    private static void swim(int k) {
        if (k == 0) return;
        if (x[k] > x[k / 2]) swap(x, k, k / 2);
        swim(k / 2);
    }

    private static void sink(int k) {
        int l = 2 * k, r = 2 * k + 1;
        if (l == size) {
            if (x[k] < x[l]) swap(x, k, l);
        } else if (l < size) {
            int g;
            if (x[l] > x[r]) g = 2 * k;
            else g = 2 * k + 1;
            if (x[k] < x[g]) swap(x, k, g);
            else return;
            sink(g);
        }
    }

    public static void main(String[] args) {
        for (int i = x.length - 1; i >= 0; i--) swim(i);
        while (size > 0) {
            swap(x, 0, size--);
            sink(0);
        }
        for (int i : x) System.out.print(i + " ");
    }
}