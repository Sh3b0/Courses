    import java.util.Scanner;
     
    // Ahmed Nouralla - Group 02 - a.shaaban@innopolis.university
     
    // Implementation of a Stack using a Linked-List
     
    public class Practice {
        // Function to test whether the character is a digit
        public static boolean isNum(char t) {
            return (t == '0' || t == '1' || t == '2' || t == '3' || t == '4' || t == '5' || t == '6' || t == '7' || t == '8' || t == '9');
        }
     
        // Function to test whether the character is an operator
        public static boolean isOp(char t) {
            return (t == '+' || t == '-' || t == '*' || t == '/');
        }
     
        // Function that returns the priority of operations
        public static int pr(char t) {
            if (t == '+' || t == '-') return 1;
            else if (t == '*' || t == '/') return 2;
            return 3;
        }
     
        public static void main(String[] args) {
     
            Scanner sc = new Scanner(System.in);
            String exp = sc.nextLine();          // Expression to parse
            Stack<Character> os = new Stack<>(); // Operators Stack
            Queue<String> q = new Queue<>();  // Output Queue
     
            // For testing
            // System.out.println(exp);
            String tmp = "";
            exp += " ";
            /// Implementing the PseudoCode on https://en.wikipedia.org/wiki/Shunting-yard_algorithm
            for (int i = 0; i < exp.length(); i++) {
                if (isNum(exp.charAt(i))) {
                    tmp += exp.charAt(i);
                } else {
                    if (tmp != "") {
                        q.push(tmp);
                        tmp = "";
                    }
                    if (isOp(exp.charAt(i))) {
                        while (!os.empty() && pr(os.top()) >= pr(exp.charAt(i)) && os.top() != '(') {
                            q.push(Character.toString(os.pop()));
                        }
                        os.push(exp.charAt(i));
                    } else if (exp.charAt(i) == '(') {
                        os.push(exp.charAt(i));
                    } else if (exp.charAt(i) == ')') {
                        while (!os.empty() && os.top() != '(') {
                            q.push(Character.toString(os.pop()));
                        }
                        if (os.top() == '(') os.pop();
                    }
                }
            }
            while (!os.empty()) {
                q.push(Character.toString(os.pop()));
            }
            q.print();
        }
    }
     
    class Stack<T> {
        Node top = new Node();
        private int sz = 0;
     
        class Node {
            T item;
            Node next;
        }
     
        void push(T t) {
            Node cur = new Node();
            cur.item = t;
            cur.next = top;
            top = cur;
            sz++;
        }
     
        boolean empty() {
            return sz == 0;
        }
     
        T pop() {
            if (sz == 0) throw new NullPointerException("Stack is empty\n");
            T s = top.item;
            top = top.next;
            sz--;
            return s;
        }
     
        T top() {
            if (sz == 0) throw new NullPointerException("Stack is empty\n");
            return top.item;
        }
    }
     
    // Implementation of a Queue using a Linked-List.
    // We won't need to pop from the queue. I'm too lazy to implement it.
     
    class Queue<T> {
        Node front = new Node();
        Node back = new Node();
        private int sz = 0;
     
        // A function to print contents of the queue in order
        public void print() {
            Node i = front;
            while (i != null) {
                System.out.print(i.item + " ");
                i = i.next;
            }
        }
     
        class Node {
            T item;
            Node next;
        }
     
        void push(T t) {
            Node cur = new Node();
            cur.item = t;
            back.next = cur;
            back = cur;
            if (sz == 0) {
                front = back;
            }
            sz++;
        }
    }