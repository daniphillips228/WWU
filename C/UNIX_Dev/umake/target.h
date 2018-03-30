#include <stddef.h>
#include <stdbool.h>

//list structs
struct list_st;
typedef struct list_st list_t;
typedef char* list_iterator_t;

//return targetname
char* getTargetName(char* line);
//return pointers to the dependencies
char** processDepend(char* line);
//return the number of dependencies
int numDepends(const char* target, list_t* dependList);
//check if the char* is a target
bool isTarget (list_t* commandList, const char* target);

//list functions
list_t* list_new();
void list_free(list_t* list);

void list_append(list_t* list, char* target, char* data);
int list_length(list_t* list);

list_iterator_t list_first(list_t* list);
list_iterator_t list_last(list_t* list);

char* iterator_target(list_iterator_t* iterator);
char* iterator_data(list_iterator_t* iterator);
void iterator_next(list_iterator_t* iterator);
