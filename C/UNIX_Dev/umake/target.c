#include "target.h"
#include "arg_parse.h"
#include <ctype.h>
#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>

/*
 *	look until you get the semi-colon, then place a null pointer in that position 
 *	and return the pointer
 */
char* getTargetName(char* line){

  int lineSize = strlen(line);
  for (int i=0; i < lineSize; i++){
    if (line[i] == ':'){
      line[i]=0;
    }
  }

  char* name = &line[0];
  return name;
}

/*
 *  call arg parse, but replace the first entry because that is the target name and we
 *	only want dependencies
 */
char** processDepend(char* line){
  char** depend;
  int argc;
  depend = arg_parse(line, &argc);

  int i=0;
  while(depend[i+1] != 0){
    depend[i] = depend[i+1];
    i++;
  }
  depend[i]= 0;
  return depend;
}

//return how many dependencies the target name has that are in the textfile
int numDepends(const char* target, list_t* dependList){
  int count = 0;
  list_iterator_t it;
  for (it=list_first(dependList); it; iterator_next(&it)) {
    if (strcmp(iterator_target(&it), target) == 0){
	    count++;
    }
  }
  return count;
}

/*
    return true is char* is a target
 */
bool isTarget (list_t* commandList, const char* target){


  //iterate through commandList and find if it is a target
  list_iterator_t it;
  for (it=list_first(commandList); it; iterator_next(&it)) {
    if (strcmp(iterator_target(&it), target) == 0){
        return true;
    }
  }

  return false;
}

//Linked list stuff, built of Aran Clauson's linked list in header files video

/*  Example of how a target would be split into nodes and put into seperate lists
 *
 *	umake: umake.o arg_parse.o
 *		rm umake
 *		gcc -o umake umake.o arg_parse.o
 *
 *	These would be put into the dependList
 *	(target-> umake, data-> umake.o)
 *	(target-> umake, data-> arg_parse.o)
 *
 *	These would be put into the commandList
 *	(target-> umake, data-> rm umake)
 *	(target-> umake, data-> gcc -o umake umake.o arg_parse.o)
 *
 */

typedef 
struct node_st {
  struct node_st* next;
  char* target;
  char* data;
} node_t;

struct list_st {
  node_t* first;
  node_t* last;
};

static
void list_init(list_t* list) {
  assert(list);
  list->first = NULL;
  list->last = NULL;
}

list_t* list_new() {
  list_t* list = (list_t*)malloc(sizeof(list_t));

  if(list != NULL) 
    list_init(list);

  return list;
}

static
void list_fini(list_t* list) {
  assert(list != NULL);
  while(list->first != NULL) {
    node_t* node = list->first;
    list->first = node->next;
    free(node);
  }
  list->first = NULL;
  list->last = NULL;
}

void list_free(list_t* list) {
  if(list != NULL) {
    list_fini(list);
    free(list);
  }
}

static
node_t* node_new(char* target, char* data) {
  node_t* node = (node_t*)malloc(sizeof(node_t));
  if(node != NULL) {
    node->target = target;
    node->data = data;
    node->next = NULL;
  }
  return node;
}

void list_append(list_t* list, char* target, char* data) {
  assert(list != NULL);
  node_t* node = node_new(target, data);
  assert(node != NULL);

  if(list->first == NULL) {
    assert(list->last == NULL);
    list->first = node;
    list->last = node;
  }else{
    list->last->next = node;
    list->last = node;
  }
}

int list_length(list_t* list) {
  assert(list != NULL);
  int length = 0;
  node_t* node = list->first;
  while(node != NULL) {
    length ++;
    node = node->next;
  }
  return length;
}

list_iterator_t list_first(list_t* list) {
  assert (list != NULL);
  return (list_iterator_t)list->first;
}

list_iterator_t list_last(list_t* list) {
  assert (list != NULL);
  return (list_iterator_t)NULL;
}

char* iterator_target(list_iterator_t* iterator) {
  assert(iterator != NULL);
  node_t* node = (node_t*)(*iterator);
  assert(node != NULL);
  return node->target;
}

char* iterator_data(list_iterator_t* iterator) {
  assert(iterator != NULL);
  node_t* node = (node_t*)(*iterator);
  assert(node != NULL);
  return node->data;
}

void iterator_next(list_iterator_t* iterator) {
  assert(iterator != NULL);
  node_t* node = (node_t*)(*iterator);
  assert(node != NULL);
  *iterator = (list_iterator_t)(node->next);
}
