// Gavin Harris, CSCI 322, 10am
// Lab 7
// Original program created by: Filip Jagodzinski

// mpi headers and needed libs
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>

// get the computer's wall time
double get_wallTime(){
  struct timeval tp;
  gettimeofday(&tp, NULL);
  return (double) (tp.tv_sec + tp.tv_usec/1000000.0);
}

// main routine
int main(int argc, char** argv) {

  // specify the number of messages each process sends
  const int NUM_MSSGS = 3;

  // initialize the MPI environment
  MPI_Init(NULL, NULL);

  // 1. find out "this" ID among the processes in the world. Save
  // the process ID into the variable myID
  // Get the rank of the process; save into the variable myID
  int myID;
  MPI_Comm_rank(MPI_COMM_WORLD, &myID);

  // 2. find out how many processes there exist in the world
  // Get the number of processes; save into variable totalPopulation
  int numProcesses;
  MPI_Comm_size(MPI_COMM_WORLD, &numProcesses);
  
  // this program is intended for exactly 2 processes
  if (numProcesses != 2) {
    fprintf(stderr, "World size must be two for %s\n", argv[0]);
    MPI_Abort(MPI_COMM_WORLD, 1);
  }

  // process (local heap, stack) variables
  int secretNum = 0;
  int numMessagesSent = 0;
  double start_t, end_t;
  int other_process;

  // 3. determine the ID of the "other" process
  if (myID == 1){
  	other_process = 0;
  }else{
	other_process = 1;
  }


  // have both processes start their timers
  if (secretNum == 0){
    start_t = get_wallTime();
  }
  
  // keep on running until the correct number of messages have been sent
  while (numMessagesSent < NUM_MSSGS) {
    if (myID == (secretNum % 2)) {

      // increment the secret number
      secretNum++;

      // print a "sending" message
      printf("Process %d sending to Process %d the secretNum %d\n", myID, other_process, secretNum);

      // 4. send the secret number to the "other" process
	  MPI_Send(&secretNum, 1, MPI_INT, other_process, 0, MPI_COMM_WORLD);
      
      // increment the number of messages sent
      numMessagesSent++;

    } else {

      // 5. receive a message from the "other" process
	  MPI_Recv(&secretNum, 1, MPI_INT, other_process, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

      // print a "received" message
      printf("Process %d received from Process %d the secretNum %d\n", myID, other_process, secretNum);

      // 6. sleep num of seconds equivalent to the received secretNum
	  sleep(secretNum);


    }
  }

  // have process 0 output how much time it spend "running"
  if (myID == 1){
    end_t = get_wallTime();
    printf("The total elapsed time was %f \n", (end_t - start_t));
  }
  MPI_Finalize();
}
