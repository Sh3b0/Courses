import java.util.ArrayList;

/// Quick Sort O( n * log(n) )
/// Not in place, for simplicity.

public class Main {
    private static int[] x = {5, 9, 7, 1, 6};

    private static ArrayList<Integer> sort(ArrayList<Integer> t) {
        if (t.size() <= 1) return t;

        ArrayList<Integer> L = new ArrayList<>();
        ArrayList<Integer> E = new ArrayList<>();
        ArrayList<Integer> G = new ArrayList<>();

        int p = t.get(t.size() - 1); // Choosing Last element as pivot.
        for (int u : t) {
            if (u < p) L.add(u);
            else if (u > p) G.add(u);
            else E.add(u);
        }

        ArrayList<Integer> r = new ArrayList<>();
        r.addAll(sort(L));
        r.addAll(E);
        r.addAll(sort(G));
        return r;
    }

    public static void main(String[] args) {
        ArrayList<Integer> al = new ArrayList<>();
        for (int u : x) al.add(u);
        System.out.println(sort(al));
    }
}