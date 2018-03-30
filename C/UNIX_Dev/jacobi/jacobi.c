//*****************************************************************************
// jacobi.c  Author: Knabb, Aidan  Date: 12/6/2017
//
// This is the main file of the jacobi project it takes two arguments, the
// first is the input file and the second is the number of threads with which
// you would like to execute the program with.
//***************************************************************************** 

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <pthread.h>

#include "line_parse.h"

#define SIZE 1024
#define EPSILON 0.00001

typedef
struct arg_st
{
  double (*matrix)[SIZE];
  double (*matrix2)[SIZE];
  double* delta;
  int noth;
  int thd_num;
} arg_t;

//-----------------------------------------------------------------------------
// threadbody()
// args - args is the argument structure for the threads to use in the
//        threadbody()
//
// threadbody() is the lines of code that each thread executes.
//-----------------------------------------------------------------------------
void* threadbody(void* args);

//-----------------------------------------------------------------------------
// threadrun()
// matrix - matrix is one of the two matrixes used to use the jacobi method
// matrix2 - matrix2 is one of the two matrixes used to use the jacobi method
// NOTH - NOTH is number of threads to create
//
// run() uses the two input matrixes to do the jacobi method, then prints the
// most accurate matrix to the output file.
//-----------------------------------------------------------------------------
void threadrun(double (*matrix)[SIZE], double (*matrix2)[SIZE], int NOTH);

int main(int argc, const char* argv[])
{
  FILE* input;
  int current_line = 0;
  int NOTH = 0;
  int start_t = 0.0;
  int end_t = 0.0;
  double elapsed = 0.0;
  double (*matrix)[SIZE] = malloc(SIZE * SIZE * sizeof(double));
  double (*matrix2)[SIZE] = malloc(SIZE * SIZE * sizeof(double));

  start_t = get_wallTime();

  if(argc != 3)
  {
    printf("Number of args must be 2\n");
    free(matrix);
    free(matrix2);
    exit(1);
  }
  input = fopen(argv[1], "r");
  NOTH = atoi(argv[2]);

  if(input == NULL)
  {
    printf("No %s file found\n", argv[1]);
    free(matrix);
    free(matrix2);
    exit(1);
  }
  size_t bufsize = 0;
  char* line = NULL;
  ssize_t linelen = getline(&line, &bufsize, input);

  while(-1 != linelen)
  {
    if(line[linelen - 1] == '\n')
    {
      linelen -= 1;
      line[linelen] = '\0';
    }
    processline(matrix, matrix2, current_line, line);
    linelen = getline(&line, &bufsize, input);
    current_line++;
  }
  free(line);
  fclose(input);

  printf("Number of threads: %d\n", NOTH);
  printf("Running jacobi method...\n");
  threadrun(matrix, matrix2, NOTH);

  free(matrix);
  free(matrix2);

  end_t = get_wallTime();
  elapsed = (end_t - start_t);
  printf("Elapsed %f seconds\n", elapsed);

  return EXIT_SUCCESS;
}

//-----------------------------------------------------------------------------
// threadrun()
// matrix - matrix is one of the two matrixes used to use the jacobi method
// matrix2 - matrix2 is one of the two matrixes used to use the jacobi method
// NOTH - NOTH is the number of threads to create
//
// run() uses the two input matrixes to do the jacobi method by using the
// jacobi method to calculate the value of each cell. threadrun() creates the
// number of threads specified by NOTH and gives each thread the threadbody
// function and and arg_t struct. threadrun() then uses pthread_join() as a
// barrier to prevent the threads from moving past eachother. The highest
// difference change in values is found out of each thread and compared to the
// given epsilon value to decide if the process should be repeated.
//-----------------------------------------------------------------------------
void threadrun(double (*matrix)[SIZE], double (*matrix2)[SIZE], int NOTH)
{
  double (*tmp)[SIZE] = matrix;
  double (*tmp2)[SIZE] = matrix2;
  double (*solution)[SIZE] = matrix2;
  double delta[NOTH];
  double ldelta = 1.0;
  pthread_t thd[NOTH];
  arg_t args[NOTH];
  void* unused;
  int j;

  while(ldelta >= EPSILON)
  {
    ldelta = 0.0;
    for(j = 0; j < NOTH; j++)
    {
      delta[j] = 0.0;
    }
    for(j = 0; j < NOTH; j++)
    {
      args[j].matrix = tmp;
      args[j].matrix2 = tmp2;
      args[j].delta = delta;
      args[j].noth = NOTH;
      args[j].thd_num = j;
      if(pthread_create(&thd[j], NULL, &threadbody, (void*)&args[j]))
      {
        fprintf(stderr, "pthread_create");
        free(matrix);
        free(matrix2);
        exit(1);
      }
    }
    for(j = 0; j < NOTH; j++)
    {
      pthread_join(thd[j], &unused);
    }
    for(j = 0; j < NOTH; j++)
    {
      if(delta[j] > ldelta)
      {
        ldelta = delta[j];
      }
    }
    solution = tmp2;
    tmp2 = tmp;
    tmp = solution;
  }

  validate(solution);
}

//-----------------------------------------------------------------------------
// threadbody()
// args - args is the argument structure for the threads to use in the
//        threadbody()
//
// threadbody() uses interlacing to assign each thread it's set of rows to
// process. Each thread then uses Jacobi's method to calculate the new value of
// the matrix. threadbody() then records the difference in matrix values and
// records them in the delta array.
//-----------------------------------------------------------------------------  
void* threadbody(void* args)
{
  arg_t* arg = (arg_t*)(args);
  double old_num = 0.0;
  double new_num = 0.0;
  int row = 0;
  int column = 0;

  for(row = 1 + arg->thd_num; row < SIZE - 1; row += arg->noth)
  {
    for(column = 1; column < SIZE - 1; column++)
    {
      old_num  = arg->matrix[row][column];
      new_num = ((arg->matrix[row - 1][column] + arg->matrix[row][column - 1] + arg->matrix[row][column + 1] + arg->matrix[row + 1][column]) / 4.00);
      arg->matrix2[row][column] = new_num;
      if((new_num - old_num) > arg->delta[arg->thd_num])
      {
        arg->delta[arg->thd_num] = (new_num - old_num);
      }
    }
  }

  return NULL;
}
