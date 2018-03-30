// Gavin Harris, Original Code from Filip Jagodzinski
// CSCI322

/*
 Implementing barriers/locks
	
 Command line instructions:
 - compile with g++ summingArray.cpp -o summingArray -std=c++0x
 - run with ./summingArray
 
*/

#include <iostream>
#include <sys/time.h>
#include <stdio.h>
#include <stdlib.h>
#include <random>
#include <ctime>
#include <future>
#include <thread>
#include <unistd.h>

using namespace std;

// get the wall time
double get_wallTime() {
  struct timeval tp;
  gettimeofday(&tp,NULL);
  return (double) (tp.tv_sec + tp.tv_usec/1000000.0);
}

// a vanilla random num generator mod 10
int genRandNum(int min, int max){
  return min + (rand() / (RAND_MAX / (max - min))) % 10;
}

// a global variable
int sumGlobal = 0;

// calcualte the sum of the entries of a 2D array
// using a locallly scoped variable, sumLocal
int sumArrayContentsLocalSum(int** array, int dimension){

  int sumLocal = 0;
  for (int i=0; i<dimension; i++){
    for (int j=0; j<dimension; j++){
      sumLocal += array[i][j];
    }
  }
  return sumLocal;
}

// calcualte the sum of the entries of a 2D array
// using a globally scoped variable, sumGlobal
int sumArrayContentsGlobalSum(int** array, int dimension){

  int output = 0;
  for (int i=0; i<dimension; i++){
    for (int j=0; j<dimension; j++){
      sumGlobal += array[i][j];
    }
  }
  return sumGlobal;
}


// main routine
int main(){

  // dimension of the array
  int dim = 6000;

  // seed the random number generator
  srand( time(NULL));

  // create the 2D array, populate it,
  // and time the entire event
  double createStart = get_wallTime();
  int** a2DArray = new int*[dim];
  for (int i=0; i<dim; i++){
    a2DArray[i] = new int[dim];
    for (int j=0; j<dim; j++){
      a2DArray[i][j] = genRandNum(0,100);
    }    
  }
  double createEnd = get_wallTime();

  ///////////////////////////////////////
  // Sequential
  ///////////////////////////////////////
  
  // manually sum the entires of array;
  // perform the summation 6 times
  int sumSequential = 0;
  double sumStart = get_wallTime();
  for (int m=0; m<6; m++){
    for (int i=0; i<dim; i++){
      for (int j=0; j<dim; j++){
	sumSequential += a2DArray[i][j];
      }    
    }
  }
  double sumEnd = get_wallTime();


  ///////////////////////////////////////////////
  // Concurrent, using a locally summed variable
  ///////////////////////////////////////////////

  double concurrentLocalStart = get_wallTime();
  std::vector<std::future<int>> thread_poolLocal;
  for (int i = 0; i < 6; ++i) {
    thread_poolLocal.push_back( std::async(launch::async,
					   sumArrayContentsLocalSum,
					   a2DArray, dim));
  }
  
  int sumLocal = 0;
  for (auto &threadLocalOutput : thread_poolLocal){
    sumLocal += threadLocalOutput.get();
  }
  double concurrentLocalEnd = get_wallTime();
  
  ///////////////////////////////////////////////
  // Concurrent, using a global scoped variable
  ///////////////////////////////////////////////
  
  double concurrentGlobalStart = get_wallTime();
  std::vector<std::future<int>> thread_poolGlobal;
  for (int i = 0; i < 6; ++i) {
    thread_poolGlobal.push_back( std::async(launch::async,
					    sumArrayContentsGlobalSum,
					    a2DArray, dim));
  }
  
  for (auto &threadGlobalOutput : thread_poolGlobal){
    sumGlobal += threadGlobalOutput.get();
  }
  double concurrentGlobalEnd = get_wallTime();

  
  // outputs
  cout << "Time needed to create array                 : " << createEnd - createStart << endl;
  cout << "Time needed to sum * 6 array's contents     : " << sumEnd - sumStart << endl;
  cout << "Time needed for concurrent local sum        : " << concurrentLocalEnd - concurrentLocalStart << endl;
  cout << "Time needed for concurrent global sum       : " << concurrentGlobalEnd - concurrentGlobalStart << endl;  

  // output the sum
  cout << "The sum of the 2D array                     : " << sumSequential << endl;
  cout << "The sum of the 2D array conc, local sum     : " << sumLocal << endl;
  cout << "The sum of the 2D array conc, global sum    : " << sumGlobal << endl;
    
 }