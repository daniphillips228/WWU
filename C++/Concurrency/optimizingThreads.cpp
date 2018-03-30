// Gavin Harris, CSCI322, lab5
// Original Code from Filip Jagodzinski's optimizingThreads program
/*

 Command line instructions:
 - compile with g++ optimizingThreads.cpp -o optimizingThreads -std=c++0x
 - run with ./optimizingThreads
 
*/

#include <iostream>
#include <sys/time.h>
#include <stdio.h>
#include <stdlib.h>
#include <random>
#include <ctime>
#include <thread>
#include <future>

using namespace std;

// get the wall time
double get_wallTime() {
	struct timeval tp;
	gettimeofday(&tp,NULL);
	return (double) (tp.tv_sec + tp.tv_usec/1000000.0);
}

// A vanilla random number generator
double genRandNum(double min, double max){
	return min + (rand() / (RAND_MAX / (max - min)));
}

// A ridiculous calculation optimized
double ridiculousCalcOptimized(double* array1, double* array2, double* array3, int dimension, double threadNum, double numThreads){

	double* resultArray1 = new double[dimension];
	double* resultArray2 = new double[dimension];
	double* resultArray3 = new double[dimension];
	double* resultArray4 = new double[dimension];
	double* resultArray5 = new double[dimension];
	double* outputArray = new double[dimension];
	double factor = 0.000000001;
	
	//if no threading
	if (threadNum == -1){
		for (int i=0; i<dimension; i++){
			//reducing redundant code
			double x = array2[i] / array1[i];
			double y = array2[i] * array3[i];

			resultArray1[i] = array1[i] / y * -1.456;
			resultArray2[i] = resultArray1[i] / array3[i] * array3[i];
			resultArray3[i] = y + array1[i] * array2[i]/(array2[i] * -0002.7897);
			resultArray4[i] = resultArray3[i] * x;
			resultArray5[i] = resultArray4[i] * x * resultArray4[i] * 0.0000000023;
		}

		//loop fission for speed up
		for (int i=0; i<dimension; i++){
			outputArray[i] = resultArray1[i]*factor + resultArray2[i]*factor + resultArray3[i]*factor + resultArray4[i]*factor + resultArray5[i]*factor;
		}

		double output = 0.0;
		for (int i=0; i<dimension; i++){
			output += outputArray[i];
		}  
		return output;	
	
	//else there will be threading used	
	}else{
		
		//temp variables used to chunk the dimensions
		int temp = (threadNum * ((1/numThreads)*dimension));
		int temp2 = (1/numThreads)*dimension;
		
		for (int i= temp; i<(temp+temp2); i++){
			
			//reducing redundant code
			double x = array2[i] / array1[i];
			double y = array2[i] * array3[i];

			resultArray1[i] = array1[i] / y * -1.456;
			resultArray2[i] = resultArray1[i] / array3[i] * array3[i];
			resultArray3[i] = y + array1[i] * array2[i]/(array2[i] * -0002.7897);
			resultArray4[i] = resultArray3[i] * x;
			resultArray5[i] = resultArray4[i] * x * resultArray4[i] * 0.0000000023;
			//no loop fission for threading
			outputArray[i] = resultArray1[i]*factor + resultArray2[i]*factor + resultArray3[i]*factor + resultArray4[i]*factor + resultArray5[i]*factor;
		}

		//temp variables used to chunk the dimensions
		int temp3 = (threadNum * ((1/numThreads)*dimension));
		int temp4 = (1/numThreads)*dimension;
		
		double output = 0.0;
		for (int i= temp3; i<(temp3+temp4); i++){
			output += outputArray[i];
		}  
		return output;
	}
}

// A ridiculous calculation
double ridiculousCalc(double* array1, double* array2, double* array3, int dimension, double threadNum, double numThreads){

	double* resultArray1 = new double[dimension];
	double* resultArray2 = new double[dimension];
	double* resultArray3 = new double[dimension];
	double* resultArray4 = new double[dimension];
	double* resultArray5 = new double[dimension];
	double* outputArray = new double[dimension];
	double factor = 0.000000001;

	if (threadNum == -1){
		for (int i=0; i<dimension; i++){
			resultArray1[i] = array1[i] / array2[i] * array3[i] * -1.456;
			resultArray2[i] = resultArray1[i] / array3[i] * array3[i];
			resultArray3[i] = array3[i] * array2[i] + array1[i] * array2[i]/(array2[i] * -0002.7897);
			resultArray4[i] = resultArray3[i] * array2[i] / array1[i];
			resultArray5[i] = resultArray4[i] * array2[i] / array1[i] * resultArray4[i] * 0.0000000023;
			outputArray[i] = resultArray1[i]*factor + resultArray2[i]*factor + resultArray3[i]*factor + resultArray4[i]*factor + resultArray5[i]*factor;
		}
		double output = 0.0;
		for (int i=0; i<dimension; i++){
			output += outputArray[i];
		}
		return output;

	}else{

		//temp variables used to chunk the dimensions
		int temp = (threadNum * ((1/numThreads)*dimension));
		int temp2 = (1/numThreads)*dimension;
		for (int i= temp; i<(temp+temp2); i++){

			resultArray1[i] = array1[i] / array2[i] * array3[i] * -1.456;
			resultArray2[i] = resultArray1[i] / array3[i] * array3[i];
			resultArray3[i] = array3[i] * array2[i] + array1[i] * array2[i]/(array2[i] * -0002.7897);
			resultArray4[i] = resultArray3[i] * array2[i] / array1[i];
			resultArray5[i] = resultArray4[i] * array2[i] / array1[i] * resultArray4[i] * 0.0000000023;
			outputArray[i] = resultArray1[i]*factor + resultArray2[i]*factor + resultArray3[i]*factor + resultArray4[i]*factor + resultArray5[i]*factor;
		}

		//temp variables used to chunk the dimensions
		int temp3 = (threadNum * ((1/numThreads)*dimension));
		int temp4 = (1/numThreads)*dimension;
		double output = 0.0;
		for (int i= temp3; i<(temp3+temp4); i++){
			output += outputArray[i];
		}
		return output;
	}
}

// main routine
int main(){

	// dimension of the array
	int dim = 10000000;

	// seed the random number generator
	srand( time(NULL));

	// create the arrays and populate them 
	// time the entire event
	double createStart = get_wallTime();
	static double *array1 = new double[dim];
	static double *array2 = new double[dim];
	static double *array3 = new double[dim];
	for (int i=0; i<dim; i++){
		array1[i] = genRandNum(0, 1000000);
		array2[i] = genRandNum(0, 1000000);
		array3[i] = genRandNum(0, 1000000);
	}
	double createEnd = get_wallTime();
	cout << "\n Time needed to create arrays                  : "<< createEnd - createStart << endl;
	cout << " ========================================================"<< endl;

	// perform non-optimized calculations
	double ridiculousStart = get_wallTime();
	double output = ridiculousCalc(array1, array2, array3, dim, -1, 0);
	double ridiculousEnd = get_wallTime();
	cout << " Time needed to complete ridiculous calculation: "<< ridiculousEnd - ridiculousStart << endl;
	cout << " Ridiculous calculation output                 : "<< output << endl;
	cout << " ========================================================"<< endl;

	// perform non-optimized calculations
	double ridiculousOptStart = get_wallTime();
	double output2 = ridiculousCalcOptimized(array1, array2, array3, dim, -1, 0);
	double ridiculousOptEnd = get_wallTime();
	cout << " Time needed to complete optimized calculation: "<< ridiculousOptEnd - ridiculousOptStart << endl;
	cout << " Ridiculous calculation output                 : "<< output2 << endl;
	cout << " ========================================================"<< endl;

	// find number of threads availible
	int numThreads = std::thread::hardware_concurrency();
	cout << " Number of threads availible "<< numThreads << endl;
	cout << " ========================================================"<< endl;

	// perform threaded non optimized calculations
	double threadedStart = get_wallTime();
	double Tcalc=0;
	std::vector<std::future<double>> nonOptimizedThreadedPool;  

	for (int x= 0; x< numThreads; x++){
		nonOptimizedThreadedPool.push_back( std::async(launch::async,ridiculousCalc,array1, array2,array3, dim, x, numThreads));
	}
	for (int y= 0; y< numThreads; y++){
		Tcalc = Tcalc + nonOptimizedThreadedPool[y].get();
	}
	
	double threadedEnd = get_wallTime();

	cout << " Time needed to complete threaded calculation: "<< threadedEnd - threadedStart << endl;
	cout << " Ridiculous calculation output                 : "<< Tcalc << endl;
	cout << " ========================================================"<< endl;

	//perform threaded optimized calculations
	double OthreadedStart = get_wallTime();
	double OTcalc=0;
	std::vector<std::future<double>> OptimizedThreadedPool;  

	for (int x= 0; x< numThreads; x++){
		OptimizedThreadedPool.push_back( std::async(launch::async,ridiculousCalcOptimized,array1, array2,array3, dim, x, numThreads));

	}
	for (int y= 0; y< numThreads; y++){
		OTcalc = OTcalc + OptimizedThreadedPool[y].get();

	}
	double OthreadedEnd = get_wallTime();

	cout << " Time needed to complete opt-thread calculation: "<< OthreadedEnd - OthreadedStart << endl;
	cout << " Ridiculous calculation output                 : "<< OTcalc << endl;
	cout << " ========================================================"<< endl;
}