import java.util.ArrayList;

/// Merge Sort O( n * log(n) ), Stable, Out of place

public class Main {
    private static int[] x = {5, 9, 1, 0, -561, 69, 98, -20, 6};
        
    private static ArrayList<Integer> merge(ArrayList<Integer> l, ArrayList<Integer> r) {
        ArrayList<Integer> al = new ArrayList<>();
        while(!l.isEmpty() && !r.isEmpty()) {
            if (l.get(0) < r.get(0)) {
                al.add(l.get(0));
                l.remove(0);
            } else {
                al.add(r.get(0));
                r.remove(0);
            }
        }
        while (!l.isEmpty()) {
            al.add(l.get(0));
            l.remove(0);
        }
        while (!r.isEmpty()) {
            al.add(r.get(0));
            r.remove(0);
        }
        return al;
    }

    private static ArrayList<Integer> sort(int l, int r) {
        if (l == r) {
            ArrayList<Integer> al = new ArrayList<>();
            al.add(x[l]);
            return al;
        }
        int m = (l + r) / 2;
        return merge(sort(l, m), sort(m + 1, r));
    }

    public static void main(String[] args) {
        System.out.println(sort(0, x.length - 1));
    }
}