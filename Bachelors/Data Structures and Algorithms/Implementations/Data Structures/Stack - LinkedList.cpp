#include <bits/stdc++.h>

using namespace std;

template<typename T>
class Stack {

private:
    int sz = 0;

public:

    class Node {
    public:
        T item;
        Node *next{};
    };

    Node *top{};

    void push(T t) {
        Node *cur = new Node();
        cur->item = t;
        cur->next = top;
        top = cur;
        sz++;
    }

    T pop() {
        if (sz == 0) {
            cout << "ERROR: Stack is empty\n";
            exit(0);
        }
        T t = top->item;
        top = top->next;
        sz--;
        return t;
    }

    int size() {
        return sz;
    }

    bool empty() {
        return sz == 0;
    }
};

int main() {
    Stack<string> s;
    s.push("1");
    s.push("2");
    s.push("3");

    cout << s.pop() << '\n';
    cout << s.pop() << '\n';
    cout << s.pop() << '\n';
}
