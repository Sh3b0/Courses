// Author: Ahmed Nouralla - BS19-02 - a.shaaban@innopolis.university
// CodeForces Submission Link: https://codeforces.com/group/3ZU2JJw8vQ/contest/269072/submission/70952409
import java.util.Scanner; // To handle standard input/Output.

public class BalancedParenthesis {
    private static Character pair(char c) { // Given a(n) (opening/closing) bracket, this function returns the corresponding (closing/opening) bracket.
        if (c == '(') return ')';
        else if (c == '[') return ']';
        else if (c == '{') return '}';
        else if (c == ')') return '(';
        else if (c == ']') return '[';
        else if (c == '}') return '{';
        return null;
    }

    private static boolean isOpeningBracket(char c) { // Returns true if the given parameter is an opening bracket, false otherwise.
        return (c == '(' || c == '[' || c == '{');
    }

    private static boolean isClosingBracket(char c) { // Returns true if the given parameter is a closing bracket, false otherwise.
        return (c == ')' || c == ']' || c == '}');
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        int l = 1; // To keep track of the line number we are currently processing.
        String s = ""; // To hold the current line being processed.
        Stack<Character> st = new Stack<>(); // To hold the opening brackets in order they appear.
        while (n-- != 0) { // While we have more lines to process.
            s = sc.nextLine(); // Read the line an assign it to the string s.
            for (int i = 0; i < s.length(); i++) {
                if (st.isEmpty() && isClosingBracket(s.charAt(i))) { // If we got a closing bracket before any opening bracket.
                    System.out.println("Error in line " + l + ", column " + (i + 1) + ": unexpected closing '" + s.charAt(i) + "'.");
                    return;
                } else if (isClosingBracket(s.charAt(i))) {
                    if (st.top() == pair(s.charAt(i)))
                        st.pop(); // If we have a closing bracket preceded by the corresponding opening bracket.
                    else { // If we have a closing bracket NOT preceded by the corresponding opening bracket.
                        System.out.println("Error in line " + l + ", column " + (i + 1) + ": expected '" + pair(st.top()) + "', but got '" + s.charAt(i) + "'.");
                        return;
                    }
                } else if (isOpeningBracket(s.charAt(i))) { // If we have an opening bracket, we add it to the stack.
                    st.push(s.charAt(i));
                }
            }
            l++;
        }
        if (!st.isEmpty()) // If There are still unclosed brackets.
            System.out.println("Error in line " + (l - 1) + ", column " + (s.length()) + ": expected '" + pair(st.top()) + "', but got end of input.");
        else // If no Errors encountered so far, then the Input is properly balanced.
            System.out.println("Input is properly balanced.");
    }
}

// Implements a stack using a Singly-Linked List.
class Stack<T> {
    // Class properties
    private Node top = new Node();
    private int size = 0;

    // Stack node
    private class Node {
        T item;
        Node next;
    }

    // Adds elements to the top of the stack.
    void push(T t) { // O(1)
        Node cur = new Node(); // New node to be added.
        cur.item = t; // Assign the element the node will contain.
        cur.next = top; // Let the next of that node be the top.
        top = cur; // Newly added node is now the top.
        size++; // We added a new node, so size increased by one.
    }

    // Will return true if the size is zero (means the stack is empty), false otherwise.
    boolean isEmpty() { // O(1)
        return size == 0;
    }

    // Remove the element at the top of the stack.
    void pop() { // O(1)
        if (isEmpty()) throw new NullPointerException("Stack is empty");
        top = top.next; // Top of the stack becomes the element under the top.
        size--; // We removed an element, so size decreases by one.
    }

    // Returns the element at the top of the stack.
    T top() { // O(1)
        if (size == 0) throw new NullPointerException("Stack is empty");
        return top.item;
    }
}