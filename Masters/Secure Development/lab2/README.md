# Lab 2 - Secure Coding

> Ahmed Nouralla - a.shaaban@innopolis.universty

[TOC]

This report includes a summary of the discovered weaknesses in the provided `hash.c` and `hash.h` files with recommended mitigations (according to the CWE listing).

## 1. CWE-120: Buffer Copy without Checking Size of Input ('Classic Buffer Overflow')

- **Issue**: The `HashIndex` function does not check the length of the input key, which could lead to a buffer overflow if the key is longer than expected.

- **Fix**: Ensure the key length is within bounds before processing.

  ```c
  unsigned HashIndex(const char* key) {
      unsigned sum = 0;
      for (const char* c = key; *c != '\0' && (c - key) < KEY_STRING_MAX; c++) {
          sum += *c;
      }
      return sum;
  }
  ```

## 2. CWE-125: Out-of-bounds Read

- **Issue**: The `HashIndex` function could read beyond the intended memory if the key is not null-terminated.

- **Fix**: Ensure the key is null-terminated and within bounds.

  ```c
  unsigned HashIndex(const char* key) {
      unsigned sum = 0;
      for (const char* c = key; *c != '\0' && (c - key) < KEY_STRING_MAX; c++) {
          sum += *c;
      }
      return sum;
  }
  ```

## 3. CWE-476: NULL Pointer Dereference

- **Issue**: The `HashAdd` and `HashFind` functions do not check if `map` is `NULL`, which could lead to a NULL pointer dereference.

- **Fix**: Add NULL checks for `map`.

  ```c
  void HashAdd(HashMap *map, PairValue *value) {
      if (map == NULL || value == NULL) return;
      unsigned idx = HashIndex(value->KeyName);
      if (map->data[idx]) 
          value->Next = map->data[idx]->Next;
      map->data[idx] = value;	
  }
  
  PairValue* HashFind(HashMap *map, const char* key) {
      if (map == NULL || key == NULL) return NULL;
      unsigned idx = HashIndex(key);
      for (PairValue* val = map->data[idx]; val != NULL; val = val->Next) {
          if (strcmp(val->KeyName, key) == 0)
              return val;
      }
      return NULL; 
  }
  ```

## 4. CWE-134: Use of Externally-Controlled Format String

- **Issue**: The `HashDump` function uses `printf` with a user-controlled format string (`val->KeyName`), which could lead to format string vulnerabilities.

- **Fix**: Use a format specifier to safely print the string.

  ```c
  void HashDump(HashMap *map) {
      if (map == NULL) return;
      for (unsigned i = 0; i < MAP_MAX; i++) {
          for (PairValue* val = map->data[i]; val != NULL; val = val->Next) {
              printf("%s\n", val->KeyName);
          }
      }
  }
  ```

## 5. CWE-190: Integer Overflow or Wraparound

- **Issue**: The `HashIndex` function sums the characters of the key without checking for integer overflow.

- **Fix**: Add a check to prevent integer overflow.

  ```c
  unsigned HashIndex(const char* key) {
      unsigned sum = 0;
      for (const char* c = key; *c != '\0' && (c - key) < KEY_STRING_MAX; c++) {
          if (sum + *c < sum) { // Check for overflow
              sum = UINT_MAX;
              break;
          }
          sum += *c;
      }
      return sum;
  }
  ```

## 6. CWE-119: Improper Restriction of Operations within the Bounds of a Memory Buffer

- **Issue**: The `HashDelete` function uses `strcpy` instead of `strcmp` to compare strings, which is incorrect and could lead to memory corruption.

- **Fix**: Use `strcmp` for string comparison.

  ```c
  void HashDelete(HashMap *map, const char* key) {
      if (map == NULL || key == NULL) return;
      unsigned idx = HashIndex(key);
      for (PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next) {
          if (strcmp(val->KeyName, key) == 0) {
              if (prev)
                  prev->Next = val->Next;
              else
                  map->data[idx] = val->Next;
              free(val); // Free the deleted node
              break;
          }
      }
  }
  ```

## 7. CWE-415: Double Free

- **Issue**: The `HashDelete` function does not free the memory of the deleted node, which could lead to memory leaks.

- **Fix**: Free the memory of the deleted node.

  ```c
  void HashDelete(HashMap *map, const char* key) {
      if (map == NULL || key == NULL) return;
      unsigned idx = HashIndex(key);
      for (PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next) {
          if (strcmp(val->KeyName, key) == 0) {
              if (prev)
                  prev->Next = val->Next;
              else
                  map->data[idx] = val->Next;
              free(val); // Free the deleted node
              break;
          }
      }
  }
  ```

## 8. CWE-416: Use After Free

- **Issue**: The `HashDelete` function does not set the pointer to `NULL` after freeing, which could lead to use-after-free vulnerabilities.

- **Fix**: Set the pointer to `NULL` after freeing.

  ```c
  void HashDelete(HashMap *map, const char* key) {
      if (map == NULL || key == NULL) return;
      unsigned idx = HashIndex(key);
      for (PairValue* val = map->data[idx], *prev = NULL; val != NULL; prev = val, val = val->Next) {
          if (strcmp(val->KeyName, key) == 0) {
              if (prev)
                  prev->Next = val->Next;
              else
                  map->data[idx] = val->Next;
              free(val); // Free the deleted node
              val = NULL; // Set to NULL to prevent use-after-free
              break;
          }
      }
  }
  ```

## 9. CWE-131: Incorrect Calculation of Buffer Size

- **Issue**: The `HashInit` function does not initialize the `data` array in the `HashMap` structure, which could lead to undefined behavior.

- **Fix**: Initialize the `data` array to `NULL`.

  ```c
  HashMap* HashInit() {
      HashMap* map = malloc(sizeof(HashMap));
      if (map) {
          for (int i = 0; i < MAP_MAX; i++) {
              map->data[i] = NULL;
          }
      }
      return map;
  }
  ```

## 10. CWE-252: Unchecked Return Value

- **Issue**: The `malloc` call in `HashInit` does not check if the allocation was successful.

- **Fix**: Check the return value of `malloc`.

  ```c
  HashMap* HashInit() {
      HashMap* map = malloc(sizeof(HashMap));
      if (map == NULL) {
          return NULL; // Handle allocation failure
      }
      for (int i = 0; i < MAP_MAX; i++) {
          map->data[i] = NULL;
      }
      return map;
  }
  ```

By addressing these issues, the code will be more secure and less prone to common vulnerabilities.
