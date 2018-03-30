/*

 Command line instructions:
 - compile with g++ matrixSum3DAsync.cpp -o matrixSum3DAsync -std=c++0x
 - run with ./matrixSum3DAsync
 
*/

#include <iostream>
#include <future>
#include <sys/time.h>
#include <stdio.h>
#include <thread>

using namespace std;

//retrieve clock time (from lab1)
double get_wallTime(){
	struct timeval tp;
	gettimeofday(&tp, NULL);
	return (double) (tp.tv_sec + tp.tv_usec/1000000.0);
}
/*
	A function, myFunction, that receives two arguments, one of which is a pointer to a 
	3D array the other an int, and which returns a long double
*/

long double myFunction(double*** array3D, int dimLower, int dimUpper, int dim){

	// Iterate over the portion of the 3D cube array that this function receives, 
	// and sums the entries return the sum

	long double aValue = 0;
	
	//double startTime = get_wallTime();
	if ( dimLower == dim){
		for (int i=0; i < dim; i++){		
			for(int j=0; j<dim; j++){				
				for(int k=0; k<dim; k++){
					aValue = aValue + array3D[i][j][k];
				}
			}
		}
	}

	for (int i=dimLower; i < dimUpper; i++){		
		for(int j=0; j<dim; j++){				
			for(int k=0; k<dim; k++){
				aValue = aValue + array3D[i][j][k];
			}
		}
	}
	
	return aValue;

}

//main routine
int main(){

	//array dimension
	int dim = 1000;

	//create a 3d array up of pointers pointer pointers to doubles
	double ***my3DArray = new double**[dim];
	
	double startTime = get_wallTime();
	
	for (int i=0; i < dim; i++){		
		my3DArray[i] = new double*[dim];
		for(int j=0; j<dim; j++){	
			my3DArray[i][j] = new double[dim];
			for(int k=0; k<dim; k++){
				my3DArray[i][j][k] = 3.6;
			}
		}
	}
	double endTime = get_wallTime();
	double init_time = endTime-startTime;
	cout << "Init time: "<< init_time << endl;
	

	double startTime2 = get_wallTime();

	std::future<long double> thread = std::async (std::launch::async,myFunction, my3DArray, 0, dim/2, dim);
	std::future<long double> thread2 = std::async (std::launch::async,myFunction, my3DArray, dim/2, dim, dim);
	long double myOutput = thread.get();
	long double myOutput2 = thread2.get();

	double endTime2 = get_wallTime();
	
	
	cout << "SumThread time: "<< endTime2-startTime2 << endl;
	cout << "The sum of the matrix's entries, threaded calculation: "<< (myOutput + myOutput2) << endl;
	
	double startTime3 = get_wallTime();
	double NT_Result = myFunction(my3DArray, dim, dim, dim);
	double endTime3 = get_wallTime();

	cout << "SumNonThread time: "<< endTime3-startTime3 << endl;
	cout << "The sum of the matrix's entries, non-threaded calculation: "<< NT_Result << endl;

}

