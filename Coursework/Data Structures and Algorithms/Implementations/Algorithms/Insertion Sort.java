// Insertion Sort O(n^2), In-Place, stable.

public class Main {

    private static void swap(int[] x, int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void main(String[] args) {
        int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
        for (int i = 0; i < x.length; i++) {
            for (int j = i; j > 0; j--) {
                if (x[j] < x[j - 1]) swap(x, j, j - 1);
                else break;
            }
        }
        for (int i : x) System.out.print(i + " ");
    }
}