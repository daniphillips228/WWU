/* Gavin Harris, WED 2pm lab
 
 Command line instructions:
 - compile with g++ WeatherStats.cpp -o WeatherStats
 - run with ./WeatherStats
 
 enter project4.txt when prompted for input file
*/

/*
My code works by first asking a user to enter a file to get data from, it will determine
if it can find the file and tell the user. Once it has the file, my code will start building 
two 1D arrays, snowfall will go into the first array and precipiation will go into the second.
Then I call my 3 functions to use the data from the arrays and will display the mean, median,
and standard deviation. Mean function will add up totals from the arrays and divide them by the
count and will cout both the means. Median function will organize the arrays from lowest to highest
value, then will cout the median position 585. The standard deviation function will for each value
subract the value by the mean and then square it and it will sum this number with each other number.
Then divide the total sum by the number of data values, and take the square root of that. It will do
this for both arrays and cout the standard deviation for both. I have more comments below on the 
specifics of how this is being done.
*/

#include <iostream>
#include <cmath>
#include<fstream>
#include<cstdlib>
#include<iomanip>
#include<string>
using namespace std;

	int com = 2342;
	int snow_array[1171];
	int prec_array[1171];
	double num, snow_num, prec_num, squared_num, s_standard, standard_snow_deviation;
	int total = 0;
	int snow_total = 0;
	double mean_function (int[], int[], int, int, int, int, int, int, int, int);
	int median_function(int[], int[], int, int, int, int);
	double standard_deviation (int[], int[], int, int, int, int, double, double, int, int, int, int, int, int, int, int, int, int, int);
	int call_mean, call_median, call_standard_deviation;
	int snow_count = 0;
	int count1 = 0;
	int count_s = 0;
	int count_p = 0;
	double result = 0;
	int s, p, x, a, b, c, d, l, temp, snow_temp;

int main()
{ 

	//Now begin reading values from the file.
	ifstream inFile;

	string filename;
	
	cout << "Which file would you like to open? Include the file's name and extension: "
		 << endl;
	cin >> filename;
	
	inFile.open(filename.c_str()); //attempt to open file for input
	
	if(!inFile.fail()) //if it doesn't fail, the file exists 
	{
		cout << "A file by the name " << filename << " exists." << endl;
		//telling user if it found a file
	}
	
	else
	{
		cout << "A file by the name" << filename << " does not exist." << endl;
		//telling user if it couldn't find a file
	}
	
	for (int i = 0; i < 2342; i++) //loops through entire txt document
	{
		if (i % 2 == 0) //picks the value in the first column of the txt document
		{
			inFile >> snow_array[i/2]; //storing in the snow_array, divide i by two to put in correct spot in array

		}
		
		else
		{
			inFile >> prec_array[i/2]; //storing in the prec_array, divide i by two to put in correct spot in array

		}
	}
	cout<<endl;
	
	inFile.close(); //closes the file
	
	call_mean = mean_function (snow_array , prec_array, s, p, snow_total, snow_num, num, total, snow_count, count1);
	cout<<endl;
	call_median = median_function(prec_array, snow_array, l, x, temp, snow_temp);
	cout<<endl;
	call_standard_deviation = standard_deviation (snow_array, prec_array, result, count_s, count_p, squared_num, s_standard, standard_snow_deviation, a, b, c, d, snow_total, snow_num, prec_num, num, total, snow_count, count1);
	cout<<endl;
return 0;
}


double mean_function(int snow_array[], int prec_array[], int s, int p, int snow_total, int snow_num, int num, int total, int snow_count, int count1)
{
	for (int s = 0; s < 1171; s++)
	{
		snow_num = snow_array[s]; //getting values from snow_array
		snow_total = snow_total + snow_num; //totaling up the values from array
		snow_count = snow_count + 1; //counting the amount of values
	}				
	
	for (int p = 0; p <1171; p++)
	{
		num = prec_array[p];
		total = total + num; //same as above but for prec_array
		count1 = count1 + 1;
	}

double s_mean = double(snow_total) / snow_count; //dividing total by count to get the mean
double p_mean = double(total) / count1;

cout<< "The mean for the snowfall is " <<  s_mean << " cm"<<endl;
cout<< "The mean for precipitation mean is " << p_mean << " mm"<<endl;

return 0; //dont return anything, we cout from function
}

int median_function(int prec_array[], int snow_array[], int l, int x, int temp, int snow_temp)
{
	l = 0;
	while (l< 1171) //makes the for loop go 1171 times just to be sure it organizes completely
	{
		for (int x = 0; x < 1170; x++) //goes through loop, while loop runs this again 1171 times
		{
			if (prec_array[x] > prec_array[x+1]) //this organizes by arranging the max value to the end
			{
				temp = prec_array[x+1];
				prec_array[x+1] = prec_array[x]; //switches the integers places, moving max farther in array
				prec_array[x] = temp;
			}
			
			if (snow_array[x] > snow_array[x+1]) // same as above but for snow_array
			{
				snow_temp = snow_array[x+1];
				snow_array[x+1] = snow_array[x];
				snow_array[x] = snow_temp;
			}
		
		}
		l = l+1;
	}

	cout << "The median for snowfall is " << snow_array[585] << " cm" << endl; //585 is the medians place
	cout << "The median for precipitation is " << prec_array[585] << " mm" << endl; //585 is the medians place
return 0;
}

double standard_deviation(int snow_arrary[], int prec_array[], int result, int count_s, int count_p, int squared_num, double s_standard, double standard_snow_deviation, int a, int b, int c, int d, int snow_total, int snow_num, int prec_num, int num, int total, int snow_count, int count1)
{
	
	for (int a = 0; a < 1171; a++)
	{
		snow_num = snow_array[a];
		snow_total = snow_total + snow_num; //calculating the mean for snow
		snow_count = snow_count + 1;
	}				
	
	for (int b = 0; b <1171; b++)
	{
		num = prec_array[b];
		total = total + num; // calculating the mean for precipitation
		count1 = count1 + 1;
	}

double s_mean = double(snow_total) / snow_count; //mean of the snowfall
double p_mean = double(total) / count1; //mean of the precipitation
result = 0;
	
	for (int c = 0; c <1171; c++)
	{
		snow_num = snow_array[c]; //getting value from array
		squared_num = pow(s_mean - snow_num,2); //subracting value from mean, then squaring
		result = result + squared_num; //totaling up the values of the value we got from above
		count_s = count_s + 1;
	}
	
s_standard = result / count_s; //dividing the total of values calculated and dividing by the amount of numbers
standard_snow_deviation = sqrt(s_standard); // getting the square root of the number we just calculated, this is the standard deviation
cout<< "The standard deviation for the snowfall is "<< standard_snow_deviation<< " cm"<<endl;

	for (int d = 0; d <1171; d++)
	{
		prec_num = prec_array[d];
		squared_num = pow(p_mean - prec_num,2); //same as above but for prec_array
		result = result + squared_num;
		count_p = count_p + 1;
	}
	
s_standard = result / count_p; //same as above
standard_snow_deviation = sqrt(s_standard);
cout<< "The standard deviation for the precipitation is "<< standard_snow_deviation<< " mm"<<endl;
		
return 0;
}