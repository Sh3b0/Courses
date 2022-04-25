// In-Place Quick Sort, a little bit dump way, but maybe easier to understand.

public class Main {

    static void swap(int x[], int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    static int partition(int[] x, int l, int r) {
        int p = r; // Choosing last element as pivot.

        boolean flag = false; // Used to avoid unnecessary iterations, can be omitted.

        for (int i = l; i < r; i++) {
            while (x[i] > x[p] && p > i) {
                swap(x, p, p - 1);
                if (i != p - 1) swap(x, i, p);
                else flag = true;
                p--;
            }
            if(flag) break;
        }

        // Debugging
        System.out.println("Array after partitioning with pivot = " + x[p]);
        for (int i : x) System.out.print(i + " ");
        System.out.println("");

        return p;
    }

    static void qsort(int[] x, int l, int r) {
        if (l >= r) return;
        int p = partition(x, l, r);
        qsort(x, l, p - 1);
        qsort(x, p + 1, r);
    }

    public static void main(String[] args) {
        int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
        // We can random shuffle the array here to avoid worst case.
        qsort(x, 0, x.length - 1);
        for (int i : x) System.out.print(i + " ");
    }
}