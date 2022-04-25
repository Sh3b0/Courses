    import java.util.*;
     
    // Ahmed Nouralla - Group 02 - a.shaaban@innopolis.university
     
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
            Queue q = new Queue();               // Output Queue of Strings
     
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
                        if (!os.empty() && os.top() == '(') os.pop();
                    }
                }
            }
            while (!os.empty()) {
                q.push(Character.toString(os.pop()));
            }
            q.finish();
        }
    }
     
    // Ordinary implementation of a Stack using a Linked-List
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
            T s = top.item;
            top = top.next;
            sz--;
            return s;
        }
     
        T top() {
            return top.item;
        }
    }
     
    // Some strange data structure I created
    /*
        Description:
        A queue of without a pop operation
        Added a prev node which points to it's previous node
        Added a finish function to evaluate the RPL and print the result
     */
     
    class Queue {
        Node front = new Node();
        Node back = new Node();
        private int sz = 0;
     
        public void finish() {
            if (this.sz == 0) System.out.println("0");
            else if (this.sz == 1) System.out.print(front.item);
            else {
                Node i = front;
                while (true) {
                    // Evaluate binary operations and replace them with their value
                    if ((i.item.equals("+") || i.item.equals("-") || i.item.equals("*") || i.item.equals("/"))) {
                        long x = 0;
                        if (i.item.equals("+")) x = Long.parseLong(i.prev.prev.item) + Long.parseLong(i.prev.item);
                        else if (i.item.equals("-")) x = Long.parseLong(i.prev.prev.item) - Long.parseLong(i.prev.item);
                        else if (i.item.equals("*")) x = Long.parseLong(i.prev.prev.item) * Long.parseLong(i.prev.item);
                        else x = Long.parseLong(i.prev.prev.item) / Long.parseLong(i.prev.item);
     
                        if (i.next == null) {
                            System.out.println(x);
                            break;
                        }
     
                        // Stupid Debugging :D
                    /*
                    System.out.println("HI, I am an operation " + i.item);
                    System.out.println("My result is " + x);
                    */
     
                        Node r = new Node();
                        r.item = Long.toString(x);
     
                        if (i.prev.prev.prev != null) {
                            r.prev = i.prev.prev.prev;
                            i.prev.prev.prev.next = r;
                        }
                        if (i.next != null) {
                            r.next = i.next;
                            i.next.prev = r;
                        }
                        i = r.next;
                    } else i = i.next;
                }
            }
        }
     
        class Node {
            String item;
            Node next;
            Node prev;
        }
     
        void push(String t) {
            Node cur = new Node();
            cur.item = t;
            if (sz == 0) {
                back = cur;
                front = back;
            } else {
                back.next = cur;
                cur.prev = back;
                back = cur;
            }
            sz++;
        }
    }