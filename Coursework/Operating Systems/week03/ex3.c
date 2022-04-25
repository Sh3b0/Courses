#include <stdio.h>
#include <stdlib.h>

struct node
{
    int data;
    struct node* next;
};

struct node *head;

void print_list()
{
    for(struct node* i = head; i != NULL; i = i->next)
    {
        printf("%d ", i->data);
    }
    printf("\n");
}

void insert_node(struct node *cur_node, struct node *new_node)
{
    if (cur_node == NULL)
    {
        head = new_node;
        return;
    }
    struct node *tmp = cur_node -> next;
    cur_node -> next = new_node;
    new_node -> next = tmp;
}

void delete_node(struct node *bad_node)
{
    for(struct node *i = head; i != NULL; i = i->next)
    {
        if(i -> next == bad_node)
        {
            i -> next = bad_node -> next;
            break;
        }
    }
}

int main()
{
    struct node *elem1, *elem2, *elem3;

    elem1 = malloc(sizeof(struct node));
    elem2 = malloc(sizeof(struct node));
    elem3 = malloc(sizeof(struct node));

    elem1 -> data = 1;
    elem2 -> data = 2;
    elem3 -> data = 3;

    insert_node(NULL, elem1);
    insert_node(elem1, elem2);
    insert_node(elem2, elem3);

    print_list();

    delete_node(elem2);

    print_list();
}
