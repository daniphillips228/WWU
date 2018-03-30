#include "arg_parse.h"
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>

/*
 * Arg Parse
 * First parse line to find the amount of arguments so we can allocate the 
 * correct amount of space for malloc Next we parse line creating pointers 
 * at the first character of arguments found and adding them to an array of 
 * pointers and adding null characters after the arguments found
 */
char** arg_parse(char* line, int* argcp){

  arg_count(line, argcp);
  char** args = malloc ((*argcp+1) * sizeof(char*));
  bool foundArg = false;
  int count = 0;
  int lineSize = strlen(line);
  int i=0;

  while (i < lineSize && line[i] != '#'){
    if (line[i] == ' ' || line[i] == '\t'){
      if (foundArg == true){
        line[i] = 0;
        count++;
        foundArg = false;
      }
    }else{
      if(foundArg == false){
        args[count] = &line[i];
        foundArg = true;	
      }
    }
    i++;
  }
  line[i]=0;
  args[count+1] = 0;

  return args;
}

//helper function to find amount of args specified by the line and return that int
void arg_count(char* line, int* argcp){

  int count = 0;
  bool foundArg = false;
  int lineSize = strlen(line);
  int i=0;

  while (i < lineSize && line[i] != '#'){

    if (line[i] == ' ' || line[i] == '\t'){
      if (foundArg == true){
        //count++;
        foundArg = false;
      }
    }else{
      if(foundArg == false){
        foundArg = true;
        count++;	
      }
    }
  i++;
  }
  line[i]=0;
  *argcp = count;
}
