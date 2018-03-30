//*****************************************************************************
// line_parse.c  Author: Knabb, Aidan  Date: 12/6/2017
//
// line_parse.c contains the implementations of utility function that are used
// in jacobi.c
//*****************************************************************************
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <math.h>

#include "line_parse.h"

#define SIZE 1024

//-----------------------------------------------------------------------------
// line_parse()
// line - line is the line to parse
//
// line_parse() takes the given line and returns an array of the tokens that
// were in the line by iterating through the line and finding were the start of
// each token is and adding a pointer to that location. line_parse() then adds
// a NULL pointer in the last element of the array.
//-----------------------------------------------------------------------------
char** line_parse(char* line)
{
  char** args = malloc((SIZE + 1) * sizeof(char*));
  int position = 0;
  int count = 0;
  int isPreviousSpace = 1;

  while(line[position] != '\0') {
    if (line[position] != ' ' && isPreviousSpace) {
      args[count] = &line[position];
      count++;
      isPreviousSpace = 0;
    }
    if (line[position] == ' ' && !isPreviousSpace) {
      line[position] = '\0';
      isPreviousSpace = 1;
    }
    if (line[position] == ' ') {
      isPreviousSpace = 1;
    }

    position++;
  }

  args[count] = NULL;

  return args;
}

//-----------------------------------------------------------------------------
// processline()
// matrix - matrix is one of the two matrixes used to use the jacobi method
// matrix2 - matrix2 is one of the two matrixes used to use the jacobi method
// current_line - current_line is the current row of the matrixes
// line - line is the current row of values with spaces seperating the values
//
// processline() calls line_parse() of the line to break up line into an array
// of values. processline() then iterates through that array to initialize the
// current row of both matrixes.
//-----------------------------------------------------------------------------
void processline(double (*matrix)[SIZE], double (*matrix2)[SIZE], int current_line, char* line)
{
  char** args = NULL;
  int position = 0;
  double temp = 0;
  args = line_parse(line);

  while(args[position] != NULL)
  {
    temp = atof(args[position]);
    matrix[current_line][position] = temp;
    matrix2[current_line][position] = temp;
    position++;
  }
  free(args);
}

//-----------------------------------------------------------------------------
// print_mtx()
// matrix - The matrix to save to file
//
// print_mtx() saves the given matrix to file by writing each row as its own
// line and columns seprated by spaces
//-----------------------------------------------------------------------------
void print_mtx(double (*matrix)[SIZE])
{
  int column = 0;
  int row = 0;
  FILE* out = fopen("test_out.mtx", "w");

  while(row < SIZE)
  {
    column = 0;
    while(column < SIZE)
    {
      fprintf(out, "%.10f ", matrix[row][column]);
      column++;
    }
    fprintf(out, "\n");
    row++;
  }
  fclose(out);
}

//-----------------------------------------------------------------------------
// get_wallTime()
//
// get_wallTime() retrieves the current system time and returns it in seconds
//-----------------------------------------------------------------------------
double get_wallTime()
{
  struct timeval tp;
  gettimeofday(&tp, NULL);
  return (double) (tp.tv_sec + tp.tv_usec / 1000000.0);
}

static
int validate_helper(double (*matrix)[SIZE], char** args, int current_line)
{
  int position = 0;
  int is_valid = 1;

  for(position = 0; position < SIZE && is_valid; position++)
  {
    if(fabs(matrix[current_line][position] - atof(args[position])) > 0.00000001)
    {
      is_valid = 0;
    }
  }

  return is_valid;
}

//-----------------------------------------------------------------------------
// validate()
// matrix - The matrix of which to check the answers of
//
// validate() compares the results of the program with the known solution to
// check the correctness of the program by comparing the output file with the
// solution file
//-----------------------------------------------------------------------------
void validate(double (*matrix)[SIZE])
{
  FILE* solution = fopen("output.mtx", "r");

  if(solution == NULL)
  {
    fprintf(stderr, "No output.mtx file to check against\n");
    return;
  }

  int is_valid = 1;
  int current_line = 0;
  size_t bufsize = 0;
  char* line = NULL;
  char** args = NULL;
  ssize_t linelen = getline(&line, &bufsize, solution);

  while(-1 != linelen && is_valid)
  {
    if(line[linelen - 1] == '\n')
    {
      linelen -= 1;
      line[linelen] = '\0';
    }
    args = line_parse(line);
    is_valid = validate_helper(matrix, args, current_line);
    linelen = getline(&line, &bufsize, solution);
    current_line++;
  }
  free(line);
  fclose(solution);

  if(is_valid)
  {
    printf("Valid\n");
  }
  else
  {
    printf("Not valid\n");
  }
}
