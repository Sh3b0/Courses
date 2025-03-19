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
    /*
        Bugs:
         - Loop condition is incorrect (it checks for null pointer). Should instead check for *c != '\0'
         - No checks are done on the length of the key (it shouldn't exceed MAP_MAX=128)
         - No checks for 0-length strings or ones that are not null-terminated
        Impact: Confidentiality, Availability.
        Relevant: CWE-125 (Out-of-bounds Read), if the loop reads beyond reserved memory, leading to
         unexpected behavior (crash), or potential information leakage.
    */
    for (char* c = key; c; c++){
        /*
            CWE-190: Integer Overflow or Wraparound. Impact: Integrity, Availability
            Explanation: unsigned type (sum) is limited to 32-bit
                If the limit is exceeded, wraparound to zero is possible, impacting integrity.
                Depending on how the return value is used, this may crash the program as well.
        */
        sum += *c;
    }
    return sum;
}

HashMap* HashInit() {
    /*
        CWE-252: Unchecked Return Value. Impact: Availability
        Explanation: Malloc may return NULL on failure to reserve memory, no checks are done for that

        CWE-457 [Potentially]: Use of uninitialized variable. Impact: Integrity, Availability
        Explanation: HashMap contains an array (data) of pointers, such pointers need to be initialized before usage.
    */
    return malloc(sizeof(HashMap));
}

void HashAdd(HashMap *map, PairValue *value) {
    unsigned idx = HashIndex(value->KeyName); // Unchecked return value

    /*
        CWE-476: Null Pointer Dereference. Impact: Availability.
        Explanation: If an argument is NULL, this would lead to a runtime error
    */
    if (map->data[idx]) 
        value->Next = map->data[idx]->Next;

    map->data[idx] = value;	
}

PairValue* HashFind(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key); // Unchecked return value
    
    for( PairValue* val = map->data[idx]; val != NULL; val = val->Next ) {
        /*
            CWE-676: Use of Potentially Dangerous Function. Impact: varies by context
            Explanation: `strcpy` does not do length checks before copying, and is considered unsafe.
               If key is large, this may lead to CWE-120 (Classic Buffer Overflow).
               Based on program logic, it seems that the author intends to compare strings rather than copy
               `key` to `val->KeyName`. In that case, `strcmp` should be used instead of `strcpy`
        */
        if (strcpy(val->KeyName, key))
            return val;
    }
    
    return NULL; 
}

void HashDelete(HashMap *map, const char* key) {
    unsigned idx = HashIndex(key);

    for( PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next ) {
        /*
            Same issue explained above in `HashFind`.
        */
        if (strcpy(val->KeyName, key)) {
            if (prev)
                prev->Next = val->Next;
            else
                map->data[idx] = val->Next;
            /*
                Missing return statement after successful deletion of a HashMap entry
                Missing best practice: after deleting the PairValue, one should set val = NULL to
                    avoid having a dangling pointer and eliminate any possiblity of CWE-416 (Use After Free)
            */
        }
    }
}

void HashDump(HashMap *map) {
    for( unsigned i = 0; i < MAP_MAX; i++ ) {
        for( PairValue* val = map->data[i]; val != NULL; val = val->Next ) {
            /*
                CWE-134: Use of Externally-Contolloed Format String. Impact: Mainly confidentiality
                Explanation: printf expects format string as the first argument. Instead, a user-controlled key-name is used.
                   A crafted format string enables multiple exploits, including information disclosure, DoS, buffer overflow, or even ACE.
            */
            printf(val->KeyName);
        }
    }
}
