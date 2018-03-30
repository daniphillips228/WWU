/* 
	Gavin Harris
	CSCI347 11am

	Built off Aran Clausons micro-make program

	CSCI 347 micro-make
  
	09 AUG 2017, Aran Clauson
*/


#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include "arg_parse.h"
#include "target.h"

/* CONSTANTS */

/* PROTOTYPES */

/* Expand
 * orig    The input string that may contain variables to be expanded
 * new     An output buffer that will contain a copy of orig with all 
 *         variables expanded
 * newsize The size of the buffer pointed to by new.
 * returns 1 upon success or 0 upon failure. 
 *
 * Example: "Hello, ${PLACE}" will expand to "Hello, World" when the environment
 * variable PLACE="World". 
 */
int expand(char* orig, char* new, int newsize);

/* Process Line
 * line   The command line to execute.
 * This function interprets line as a command line. It creates a new child
 * process to execute the line and waits for that process to complete.
 * Calls defineline to determine which type of process to execute
 */
void processline(char* line);

/* Define Line
 * Reads the line given to it and determines what was in that line
 * and returns a char to signify what it was
 */
char defineLine(char* line);

/*  Process Lists
 *  Once the lists have been built this function is called
 *  We process each command line argument from argv, and call the 
 *  recursive function if necessary
 */
void processLists(list_t* dependList, list_t* commandList, int argc, const char* argv[]);

/*  Recursive Targets
 *  check the dependencies of the target, if they also have dependencies 
 *  make a recursive call, otherwise check if that target has been executed
 *  if not that execute the commands
 */
void recursiveTargets(list_t* dependList, list_t* commandList, const char* target);

/*  Newer Depend Check
 *  check the dependencies of the target to see if they are newer than the target
 */
bool newerDependCheck(list_t* dependList, list_t* commandList, const char* target);

/*  Execute Commands
 *  Execute all commands for the given target name
 */
void executeCommands(const char* target, list_t* commandList);

/* Main entry point.
 * argc    A count of command-line arguments 
 * argv    The command-line argument values
 *
 * Micro-make (umake) reads from the uMakefile in the current working
 * directory.  The file is read one line at a time.  Lines with a leading tab
 * character ('\t') are interpreted as a command and passed to processline minus
 * the leading tab.
 */
int main(int argc, const char* argv[]) {

  FILE* makefile = fopen("./uMakefile", "r");

  //if fopen fails it sets makefile to null
  if (makefile == NULL){
    fprintf(stderr,"%s","no makefile\n");
    exit(1);
  }

  argc--;

  size_t  bufsize = 0;
  char*   line    = NULL;
  char*   Command;
  char*   New;
  char*   Depend;
  char*   targetName;
  char**  depend;
  int     usedDepend = 0;
  ssize_t linelen = getline(&line, &bufsize, makefile);
  list_t* commandList = list_new();
  list_t* dependList = list_new();

  while(-1 != linelen) {

    if(line[linelen-1]=='\n') {
      linelen -= 1;
      line[linelen] = '\0';
    }

    if(isspace(line[0]) == 0 && strlen(line) != 0) {

      char d = defineLine(line);

      //target
      if (d == 't'){
        depend = processDepend(line);
        targetName = strdup(getTargetName(line));

        //has dependencies
        if (depend[0] != 0){
          int i=0;
          while(depend[i] != 0){
            Depend = strdup(depend[i]);
            usedDepend = 1;
            list_append(dependList, targetName, Depend);
            i++;
          }
        }
      }

      //variable assignment, 
      if (d == 'v'){
        int vPosition;
        int i=0;
        int size = strlen(line);

        while (i < size && line[i] != '#'){
          if(line[i] == '='){
            line[i]=0;
            vPosition = i+1;
          }
          i++;
        }
        line[i]=0;
        setenv(&line[0], &line[vPosition], 0);
      }
    }

    //found a rule
    if(line[0] == '\t'){
      int i=0;
      while(line[i+1] != 0 && line[i+1] != '#'){
        line[i] = line[i+1];
        i++;
      }
      line[i]= 0;
      Command = strdup(&line[0]);

      char new[100];
      //depending on result, append orig or new
      int result = expand(Command, new, 100); 
      New = strdup(new);
      if (result == 0){
        list_append(commandList, targetName, Command);
      }else{
        list_append(commandList, targetName, New);
      }
    }		
    linelen = getline(&line, &bufsize, makefile);
  }

  processLists(dependList, commandList, argc, argv);

  list_free(dependList);
  list_free(commandList);
  free(Command);
  if(usedDepend == 1){ //not guaranteed to be used
    free(Depend);
  }
  free(targetName);
  free(New);
  free(line);

  return EXIT_SUCCESS;
}

/*  Process Line
 *  Make sure line isn't empty, call arg_parse and then make the appropriate execvp
 *  based on what type of line we received
 */
void processline (char* line) {

  int argc;
  char** args;
  char type = defineLine(line);
  args = arg_parse(line, &argc);

  if(argc != 0){

    const pid_t cpid = fork();
    switch(cpid) {

      case -1: {
        perror("fork");
        break;
      }

      case 0: {
        //normal
        if (type == 'N'){
          execvp(args[0], args);
          perror("execvp");
          exit(EXIT_FAILURE);
          break;
        }
        //truncate >
        if (type == 'T'){
          close(1);
          open(args[argc-1], O_WRONLY|O_CREAT|O_TRUNC, 0777);
          args[argc-1]=0;
          args[argc-2]=0;
          execvp(args[0], args);
          perror("execvp");
          exit(EXIT_FAILURE);
          break;
        }

        //re-direct input <
        if (type == 'R'){
          close(0);
          open(args[argc-1], O_RDONLY);
          args[argc-1]=0;
          args[argc-2]=0;
          execvp(args[0], args);
          perror("execvp");
          exit(EXIT_FAILURE);
          break;
        }

        //append >>
        if(type == 'A'){
          close(1);
          open(args[argc-1], O_WRONLY|O_CREAT|O_APPEND, 0777);
          args[argc-1]=0;
          args[argc-2]=0;
          execvp(args[0], args);
          perror("execvp");
          exit(EXIT_FAILURE);
          break;
        }
      }

      default: {
        int   status;
        const pid_t pid = wait(&status);
        if(-1 == pid) {
          perror("wait");
        }
        else if (pid != cpid) {
          fprintf(stderr, "wait: expected process %d, but waited for process %d",
          cpid, pid);
        }
        free(args);
        break;
      }
    }
  }
}

//determine what type of a line this is, and return a char specifying it
char defineLine (char* line) {

  int foundr = 0;
  int foundT = 0;
  int founda = 0;

  //whole line is commented out
  if(line[0] == '#'){
    return 'c';
  }

  for( int i=0;i<strlen(line);i++){
    
    //target
    if (line[i] == ':'){
      return 't';
    }
    //variable assignment
    if(line[i] == '='){
      return 'v';
    }
    //redirect input
    if(line[i] == '<'){
      foundr=1;
    }
    if(line[i] == '>'){      
      //append
      if(line[i+1] == '>'){
        founda=1;
      //truncate
      }else{
        foundT=1;
      }
    }
  }

  //create process
  if((foundr == 1 && foundT == 1) || (foundr == 1 && founda == 1)){
    return 'P';
  }else{
    if (foundr == 1){
      return 'R';
    }
    if (founda == 1){
      return 'A';
    }
    if (foundT == 1){
      return 'T';
    }
  }
  return 'N';
}

//for each argv element, look at dependencies and decide what to do
void processLists(list_t* dependList, list_t* commandList, int argc, const char* argv[]){

  for(int i=1; i<=argc; i++){

    //if target has dependencies use recursion
    int depNum = numDepends(argv[i], dependList);
    if(depNum != 0){
      recursiveTargets(dependList, commandList, argv[i]);
    }else{
      //no dependencies
      executeCommands(argv[i], commandList);
    }
  }
}

//execute the commands for the target
void executeCommands(const char* target, list_t* commandList){

  list_iterator_t it;

  for (it=list_first(commandList); it; iterator_next(&it)) {
    if (strcmp(iterator_target(&it), target) == 0){
      char* newLine = strdup(iterator_data(&it));
      processline(newLine);
      free(newLine);
    }
  }
}

/*
 *  if the targets dependency has dependencies, keep calling recursion otherwise 
 *  check if you can execute. Once ready to execute check the dependencies to see
 *  if they are newer than the target. If not don't update
 */
void recursiveTargets(list_t* dependList, list_t* commandList, const char* target){

  list_iterator_t it;
  //iterate through dependList and find dependencies for the target
  for (it=list_first(dependList); it; iterator_next(&it)) {
    if (strcmp(iterator_target(&it), target) == 0){
      
      int depNum = numDepends(iterator_data(&it), dependList);
      
      if(depNum != 0){
        recursiveTargets(dependList, commandList, iterator_data(&it));
      }else{

        //check for a dependency that is a target without dependencies of its own
        if (isTarget(commandList, iterator_data(&it))){
            executeCommands(iterator_data(&it), commandList);
        }
      }
    }
  }
    bool update = newerDependCheck(dependList, commandList, target);
    if (update == true){
      executeCommands(target, commandList);
    }
}

/*
    return true if there is a dependency that is newer than the target, 
    or if target doesn't exist
 */
bool newerDependCheck (list_t* dependList, list_t* commandList, const char* target){

  struct stat buf;
  struct stat buf2;
  int ret = stat(target, &buf);

  if (ret == -1){
    return true;
  }else{
    int mtime=buf.st_mtime;

    //iterate through dependList and find dependencies for the target
    list_iterator_t it;
    for (it=list_first(dependList); it; iterator_next(&it)) {
      if (strcmp(iterator_target(&it), target) == 0){

        stat(iterator_data(&it), &buf2);
        int mtime2=buf2.st_mtime;

        if (mtime2 >= mtime){
          return true;
        }
      }
    }
  }

  return false;
}

/*
  iterate through the line and look for a bracket
  once we find the first ({) bracket we mark the position
  then once we find the second bracket we mark that position too, 
  then get the size of how big the variable we are replacing 
  and get the size of how big the new variable is
  we determine what the difference is and call that padding
  we use this to keep the spacing between the elements in the line 
  when we expand it
  returns 1 if new has been expanded, 0 if nothing got changed
*/
int expand(char* orig, char* new, int newsize){

  bool found = false;
  int retVal = 0;
  int p1, p2, size;
  int padding = 0;
  int x=0;
  int len = strlen(orig);
  char var[newsize];

  for( int i=0;i<len;i++){
    if(orig[i] == '{'){
      found = true;
      p1 = i-1;
      retVal = 1;
    }
    if (found == false){
      new[i+padding] = orig[i];
    }else{
      if(orig[i] != '{'){
        var[x]= orig[i];
        x++;
      }
    }
    if(orig[i] == '}'){
      p2 = i;
      size = (p2-p1)+1;
      var[x-1]=0;
      char* newVar = getenv(var);
      padding = strlen(newVar)-size;
      for(int y=0;y<strlen(newVar);y++){
        new[p1+y]=newVar[y];
      }
      found = false;
    }
  }
  //if found still equals true, report an error to user
  if (found == true){
    printf("Error: mismatched braces\n");
  }

  new[len+padding] = 0;

  return retVal;
}
