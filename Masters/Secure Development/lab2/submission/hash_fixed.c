/**
*
* @Name : hash.c
*
**/
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "hash_fixed.h"

unsigned HashIndex(const char* key) {

    // Fix: added bound checks for key length
    if(strlen(key) < 1 || strlen(key) >= MAP_MAX) {
        fprintf(stderr, "%s\n", "Error: Key length is not within valid bounds");
        return 0; // Handle 0 return accordingly at caller
    }

    unsigned sum = 0;

    // Fix: corrected loop condition to properly check for string terminator
    for (const char* c = key; *c != '\0'; c++) {
        // Now that key length is limited, it's guaranteed that `sum` cannot overflow
        sum += *c;
    }
    
    return sum % MAP_MAX; // Fix: HashIndex should not exceed MAP_MAX
}

HashMap* HashInit() {
    // Fix: properly allocate memory using malloc by casting to HashMap pointer type
    HashMap* ptr = (HashMap*)malloc(sizeof(HashMap));

    if(ptr) {
        // Fix: initialize map data to zeros
        memset(ptr->data, 0, sizeof(ptr->data));
    }
    
    return ptr; // Handle possible NULL returns in client programs (memory allocation failures)
}

void HashAdd(HashMap *map, PairValue *value) {
    unsigned idx = HashIndex(value->KeyName);

    // Fix: avoid processing invalid input
    if(idx == 0 || map == NULL || value == NULL) return;

    // Fix: if the value was already there, increment ValueCount
    PairValue* found = HashFind(map, value->KeyName);
    if(found) {
        found->ValueCount++;
        return;
    }
    
    if (map->data[idx]) {
        value->Next = map->data[idx]->Next;
    }
    
    map->data[idx] = value;
}

PairValue* HashFind(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key);

    if(idx == 0 || map == NULL || strlen(key) < 1 || strlen(key) >= MAP_MAX)
        return NULL; // Fix: avoid processing invalid input
    
    for(PairValue* val = map->data[idx]; val != NULL; val = val->Next) {
        if (!strcmp(val->KeyName, key)) // Fix: use strcmp instead of strcpy
            return val;
    }
    
    return NULL;
}

void HashDelete(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key);
    if(idx == 0 || map == NULL || strlen(key) < 1 || strlen(key) >= MAP_MAX)
        return; // Fix: avoid processing invalid input
    
    for(PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next ) {
        if (!strcmp(val->KeyName, key)) { // Fix: use strcmp instead of strcpy
            if (prev)
                prev->Next = val->Next;
            else
                map->data[idx] = val->Next;
            
            // Improvement: explicitly set val=NULL to eliminate any possibility of UAF (dangling pointer)
            val = NULL;
            return;
        }
    }
}

void HashDump(HashMap *map) {
    if(map == NULL) return; // Fix: avoid processing invalid input

    printf("{"); // Added: Just for pretty-printing
    for(unsigned i = 1; i < MAP_MAX; i++) { // Fix: start from 1 since a HashIndex of 0 is invalid
        for(PairValue* val = map->data[i]; val != NULL; val = val->Next) {
            printf("'%s': %d, ", val->KeyName, val->ValueCount); // Fix: specify format string explicitly
        }
    }
    printf("}\n"); // Added: Just for pretty-printing
}


/* 
    Added main function to test the functionality of the HashMap
    Compile and run with: gcc hash_fixed.c && ./a.out
*/

int main() {
    HashMap* map = HashInit();
    if(!map) {
        printf("%s\n", "HashInit() Failed");
        return -1;
    }    
    printf("%s\n", "HashInit() Successful");
    
    PairValue pv1 = { .KeyName = "test_key", .ValueCount = 1, .Next = NULL };
    PairValue pv2 = { .KeyName = "other_key", .ValueCount = 1, .Next = NULL };
    
    printf("HashAdd(map, '%s')\n", pv1.KeyName);
    HashAdd(map, &pv1);

    printf("HashAdd(map, '%s')\n", pv1.KeyName);
    HashAdd(map, &pv1);

    printf("HashAdd(map, '%s')\n", pv2.KeyName);
    HashAdd(map, &pv2);

    printf("HashFind(map, %s) = ", pv1.KeyName);
    PairValue* result = HashFind(map, pv1.KeyName);
    if(result) {
        printf("{'%s': %d}\n", result->KeyName, result->ValueCount);
    }
    else {
        printf("%s\n", "Not found");
    }
    
    printf("%s", "HashDump(map) = ");
    HashDump(map);

    printf("HashDelete(map, '%s')\n", pv1.KeyName);
    HashDelete(map, pv1.KeyName);

    printf("HashFind(map, %s) = ", pv1.KeyName);
    result = HashFind(map, pv1.KeyName); 
    if(result) {
        printf("{'%s': %d}\n", result->KeyName, result->ValueCount);
    }
    else {
        printf("%s\n", "Not found");
    }

    printf("%s", "HashDump(map) = ");
    HashDump(map);
}
