#include <stdio.h>

int main()
{
    char foo[] = "Hello World";  // foo is the pointer to the beginning of the character array (or a string for simplicity)
    char *ss = foo;              // ss is a pointer to foo
    char **s = &ss;              // s is a pointer to ss
    printf("s is %s\n", *s);     // dereferencing s yields the string.
    s[0] = foo;                  // it was the same before
    printf("s[0] is %c\n", **s); // yields the first character of the string.
    return 0;
}
