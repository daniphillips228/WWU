/*
	Gavin Harris, CSCI 322, 10am

	Current non-concurrent & concurrent implementation of mergeSort for Final Project

	When run: 
	- Enter the dimension of the array that will be sorted,
	- If you want to view the actual array values to check the correctness of merge sort, enter y or Y, else press anything besides those

	Used random number generator and get wall time functions from Filip Jagodzinski

	Command line instructions: 
		- compile with g++ mergeSort.cpp -o mergeSort -std=c++0x -pthread
		- run with ./mergeSort
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
#include <mutex>

using namespace std;

// get the wall time
double get_wallTime() {
	struct timeval tp;
	gettimeofday(&tp,NULL);
	return (double) (tp.tv_sec + tp.tv_usec/1000000.0);
}

// a vanilla random num generator
int genRandNum(int min, int max){
	return min + (rand() / (RAND_MAX / (max - min))); //% 10;
}

//helper function to print out array contents
void displayArray(int sortArray[], int dim){
	cout << endl;
	for (int i = 0; i < dim; i++){
		cout << sortArray[i] << " "; 	
	}
	cout << endl;
}

//merge the array
void merge(int sortArray[], int leftIndex, int mid, int rightIndex, int dim) {

	//create temp array
	int* temp = new int[rightIndex-leftIndex+1];

	int left = leftIndex;
	int right = mid+1;
	int index = 0;

	//Compares the left and right values of sortArray and puts smaller value into our temp array
	while(left <= mid && right <= rightIndex) {

		//if left element is smaller that the right, put left value into temp and increment left
		if(sortArray[left] <= sortArray[right]) {
			temp[index] = sortArray[left];
			left++;
		}else { //else the right element is smaller that the left, put the right value into temp and increment right
			temp[index] = sortArray[right];  
			right++;
		}
		index++;
	}

	//Set our temp array to the right side of sortArray
	if(left > mid) { 
		for(int i=right; i <= rightIndex;i++) {
			temp[index] = sortArray[i];
			index++;
		}
	}else {   
		//Else set our temp array to the left side of sortArray
		for(int i=left; i <= mid; i++) {
			temp[index] = sortArray[i];
			index++;
		}
	}

	//Update sortArray with the sorted values from temp
	for(int i=0; i <= (rightIndex-leftIndex); i++) {
		sortArray[i+leftIndex] = temp[i];
	}
}

//use recurrsion for mergeSort
void mergeSort(int sortArray[], int leftIndex, int rightIndex, int dim){
	if(leftIndex >= rightIndex){
		return;
	}else{
		int mid = (leftIndex+rightIndex)/2;
		mergeSort(sortArray, leftIndex, mid, dim);  //left half
		mergeSort(sortArray, mid+1, rightIndex, dim);  //right half
		merge(sortArray, leftIndex, mid, rightIndex, dim);  //merge the two halfs
	}
}

void ThreadedmergeSort(int sortArray[], int leftIndex, int rightIndex, int dim){
	if(leftIndex >= rightIndex){
		return;
	}else{
		int mid = (leftIndex+rightIndex)/2;
	
		//use threads on first call, then call the nonconcurrent version of mergesort so we don't call a million threads we use recursion

		thread t1(mergeSort, sortArray, leftIndex, mid, dim);  //left half
		thread t2(mergeSort, sortArray, mid+1, rightIndex, dim);  //right half

		//make sure we join them before we merge
		t1.join();
		t2.join();

		merge(sortArray, leftIndex, mid, rightIndex, dim);
	}
}
 
int main(){        
	/* 
		Apprx times

		Dim: nonconcurrent time, concurrent time

		20 million: 3.89s NC, 2.07s C
		10 million: 1.88s NC, 1.01s C
		1 million: .17 NC, .09 C
		500k: .087 NC, 0.049 C
		100K: .019 NC, 0.012 C
		10K: 0.0037 NC, 0.0022 C
		under 5K concurrent begins to be sometimes slower than nonconcurrent
		1K: 0.0005 NC, 0.0008 C

	*/

	//dimension of arrays to be sorted specified by user
	int randomNum;
	int dim;
	//user specifies whether they want to view the array
	char display;
	cout << "Enter the dimension of array to sort (integers only please)"<<endl;
	cin >> dim;
	cout << "If you would like to display the array being sorted press y, else press anything besides y"<<endl;
	cin >> display;


	// seed the random number generator
	srand( time(NULL));

	// create two arrays, populate them with same values to be consistent when timing speedup
	int* nonConcurrentArray = new int[dim];
	int* concurrentArray = new int[dim];
	for (int i=0; i<dim; i++){
		randomNum = genRandNum(0,100);
		nonConcurrentArray[i] = randomNum;
		concurrentArray[i] = randomNum;
	}
	if ((display == 'y') or (display == 'Y')){

		cout << endl;
		cout<<"Non concurrent array before: ";
		displayArray(nonConcurrentArray, dim);

		double nonConStart = get_wallTime();
		mergeSort(nonConcurrentArray, 0, (dim-1), dim);
		double nonConEnd = get_wallTime();

		cout << endl;
		cout<<"Non concurrent array after: ";
		displayArray(nonConcurrentArray, dim);

		cout << endl;
		cout << "* Time needed to complete non concurrent sorting: "<< nonConEnd - nonConStart << " *" <<endl;
		cout << endl;
	
	}else{

		double nonConStart = get_wallTime();
		mergeSort(nonConcurrentArray, 0, (dim-1), dim);
		double nonConEnd = get_wallTime();

		cout << endl;
		cout << "* Time needed to complete non concurrent sorting: "<< nonConEnd - nonConStart << " *" <<endl;
		cout << endl;
	}

	if ((display == 'y') or (display == 'Y')){

		cout<<"Concurrent array before: "<<endl;
		displayArray(concurrentArray, dim);

		double ConStart = get_wallTime();
		ThreadedmergeSort(concurrentArray, 0, (dim-1), dim);
		double ConEnd = get_wallTime();

		cout<<"Concurrent array after: "<<endl;
		displayArray(concurrentArray, dim);

	
	}else{

		double ConStart = get_wallTime();
		ThreadedmergeSort(concurrentArray, 0, (dim-1), dim);
		double ConEnd = get_wallTime();

		cout << endl;
		cout << "* Time needed to complete concurrent sorting: "<< ConEnd - ConStart <<" *" <<endl;
		cout << endl;
	}
}
