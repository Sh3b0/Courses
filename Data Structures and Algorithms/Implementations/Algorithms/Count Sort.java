// Counting Sort, O(n + mx), stable, out of place

public class Main {
    public static void main(String[] args) {

        int[] x = {2, 5, 3, 0, 2, 3, 0, 3};               // Array to be sorted, no negative/huge numbers.
        int mx = -1;                                      // To hold the max value in array.

        for (int i : x) mx = Math.max(mx, i);             // Find max value.
        
        int[] c = new int[mx + 1], y = new int[x.length]; // Init counting array, result array.
        
        for (int i : x) c[i]++;                           // Fill count array.
        for (int i = 1; i <= mx; i++) c[i] += c[i - 1];   // Do a prefix sum on counting array.
        
        for (int i : x) {                                 
            y[c[i] - 1] = i;                              // Put each array element in the correct index.
            c[i]--;                                       // Decrement the counter.
        }
        
        for (int i : y) System.out.print(i + " ");        // Output the result.
    }
}