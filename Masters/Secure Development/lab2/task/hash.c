/**
*
* @Name : hash.c
*
**/
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "hash.h"

unsigned HashIndex(const char* key) {
    unsigned sum = 0;
    for (char* c = key; c; c++){
        sum += *c;
    }
    
    return sum;
}

HashMap* HashInit() {
	return malloc(sizeof(HashMap));
}

void HashAdd(HashMap *map,PairValue *value) {
    unsigned idx = HashIndex(value->KeyName);
    
    if (map->data[idx]) 
        value->Next = map->data[idx]->Next;
    map->data[idx] = value;	
}

PairValue* HashFind(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key);
    
    for( PairValue* val = map->data[idx]; val != NULL; val = val->Next ) {
        if (strcpy(val->KeyName, key))
            return val;
    }
    
    return NULL; 
}

void HashDelete(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key);
    
    for( PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next ) {
        if (strcpy(val->KeyName, key)) {
            if (prev)
                prev->Next = val->Next;
            else
                map->data[idx] = val->Next;
        }
    }
}

void HashDump(HashMap *map) {
    for( unsigned i = 0; i < MAP_MAX; i++ ) {
        for( PairValue* val = map->data[i]; val != NULL; val = val->Next ) {
            printf(val->KeyName);
        }
    }
}
