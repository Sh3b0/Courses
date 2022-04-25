// Selection Sort O(n^2), In-Place, not stable.

public class Main {

    private static void swap(int[] x, int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void main(String[] args) {
        int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
        for (int i = 0; i < x.length; i++) {
            int mn = x[i], mni = i; // Min value, Min value index.
            for (int j = i + 1; j < x.length; j++) {
                if (x[j] < mn) {
                    mn = x[j];
                    mni = j;
                }
            }
            swap(x, mni, i);
        }
        for (int i : x) System.out.print(i + " ");
    }
}