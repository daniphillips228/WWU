//Gavin Harris, Lab8, WED lab 2pm

/*
My program starts in the main function and asks user to pick an option of what they want the program to do
if the user picks the first option, the program will ask the user which table they want, the high values or 
low values. To show this to the user, the high values are layer 0 of the 3D array we have built, and low 
values are layer 1. If they choose option 2 we will call the average_function, we will ask the user which
month they want and whether they want the low average, or high average. Option 3 will call the highest_temp
function. It will ask the user which month the user will want to find the maximum temp from. We use if else
statement to find out if the the next value in the array is bigger than previous. Option 4 will be the 
opposite and we will call the lowest_temp function and use if else statement to figure out if the next value
in the array is smaller than the previous.
 
 Command line instructions:
 - compile with g++ temperatureData.cpp -o temperatureData
 - run with ./temperatureData
 
*/

#include<iostream>
#include<iomanip>
using namespace std;

int user_input, month, total, table_input, user_choice, highest_month, lowest_month;
int average_function(int, int);
int highest_temp(int);
int lowest_temp(int);
int int_array[31][3][2] = 
{{{67, 50}, {71, 53}, {75, 55}}, //day 1
{{67, 50}, {72, 53}, {75, 55}},
{{67, 50}, {72, 53}, {75, 55}},
{{67, 50}, {72, 53}, {75, 55}},
{{67, 50}, {72, 53}, {75, 55}}, //day 5
{{68, 50}, {72, 53}, {75, 55}},
{{68, 51}, {72, 53}, {75, 55}},
{{68, 51}, {73, 53}, {74, 55}},
{{68, 51}, {73, 53}, {74, 55}},
{{68, 51}, {73, 53}, {74, 55}}, //day 10
{{68, 51}, {73, 54}, {74, 55}},
{{68, 51}, {73, 54}, {74, 55}},
{{69, 51}, {73, 54}, {74, 55}},
{{69, 51}, {73, 54}, {74, 55}},
{{69, 51}, {74, 54}, {74, 55}}, //day 15
{{69, 51}, {74, 54}, {74, 55}},
{{69, 51}, {74, 54}, {74, 55}},
{{69, 52}, {74, 54}, {74, 54}},
{{69, 52}, {74, 54}, {73, 54}},
{{70, 52}, {74, 54}, {73, 54}}, //day 20
{{70, 52}, {74, 54}, {73, 54}},
{{70, 52}, {74, 55}, {73, 54}},
{{70, 52}, {74, 55}, {73, 54}},
{{70, 52}, {74, 55}, {73, 54}},
{{70, 52}, {74, 55}, {73, 54}}, //day 25
{{71, 52}, {75, 55}, {72, 54}},
{{71, 52}, {75, 55}, {72, 53}},
{{71, 52}, {75, 55}, {72, 53}},
{{71, 52}, {75, 55}, {72, 53}},
{{71, 53}, {75, 55}, {72, 53}}, //day 30
{{0, 0}, {75, 55}, {72, 53}}};

int main()
{



	cout<< "\nWould you like to print a table of high or low values for the three months? (Enter 1)\nFind the average high and low temperatures for one of the months? (Enter 2)\nFind the highest temperature for one of the months? (Enter 3)\nFind the lowest temperature for one of the months? (Enter 4)" <<endl; 
	cin>> user_input;

if (user_input == 1)

{
	cout<< "\nWould you like a table of high or low values? (Enter 1 for high, 2 for low)"<<endl;
	cin>> table_input; 

	for (int layer = 1; layer < 2; layer++ )
		{
			if (table_input ==1) //user picks high
			{
			cout << "\n\nHigh Temperatures" << endl << endl;
			}
			else //user picks low
			{
			cout<< "\n\nLow Temperatures" << endl<< endl;
			}
		
			for(int rows = 0; rows < 31; rows++)
			{
				cout << endl; //ends the line for each row
				for(int cols = 0; cols < 3; cols++)
				{                                           //chooses which layer gets printed
					cout << setw(4) << int_array[rows][cols][table_input-1];
				}
			}
		}
	cout<< endl;
}

else if (user_input ==2)

{
	int option_2 = average_function(month, user_choice);
}

else if (user_input == 3)

{
	int option_3 = highest_temp(highest_month);
}

else

{
	int option_4 = lowest_temp(lowest_month);
}

return 0;
}

int average_function(int month, int user_choice)
{
	cout << "\nWhich month would you like to find the average High or low for? (1 for June, 2 for July, 3 for August)" <<endl;
	cin >> month;
	cout << "\nWould you like to find the high or low average? (1 for high, 2 for low)"<<endl;
	cin >> user_choice;

	for (int layer = 0; layer < 2;)
		{

			for(int rows = 0; rows < 31; rows++)
			{
				for(int cols = 0; cols < 1; cols++)
				{                                     //using data from user
					total = total + int_array[rows][month-1][user_choice-1];
				}
			}
			layer = layer +2;
		}

	if (user_choice ==1) //user picks high
	{	
		if (month == 1) //june only has 30 days instead of 31
		{
		cout<< "\nAverage high temp is: " << double(total)/30 << endl;
		}
		else
		{
		cout << "\nAverage high temp is: " << double(total)/31 << endl;
		}
	}
	
	else //user picks low
	{
		if (month ==1 ) //june only has 30 days instead of 31
		{
		cout<< "\nAverage low temp is: " <<double(total) / 30 <<endl;
		}
		else
		{
		cout<< "\nAverage low temp is: " << double(total) / 31 <<endl;
		}
	}
	
	cout<<endl;
	return total; //returns value, we never use it though
}

int highest_temp (int highest_month)
{
	cout<<"\nWhich month would you like to find the highest temperature for? (1 for June, 2 for July, 3 for August)"<<endl;
	cin>> highest_month;
	int max_temp = 0;
	
	for (int layer = 0; layer < 2;)
		{

			for(int rows = 0; rows < 31; rows++) //all 31 rows of data
			{
				for(int cols = 0; cols < 1; cols++) //only want 1 column of data
				{
					if (int_array[rows][highest_month-1][layer] > max_temp)
					{
						max_temp = int_array[rows][highest_month-1][layer];
					}   //reassigning max_temp if number from arrary is bigger
				}
			}
			layer = layer +2; //goes through original for loop once
		}
		
	cout<<"\nThe highest temperature is "<< max_temp<<endl;
	return max_temp;
}

int lowest_temp (int lowest_month)
{
	cout<<"\nWhich month would you like to find the lowest temperature for? (1 for June, 2 for July, 3 for August)"<<endl;
	cin>> lowest_month;
	int low_temp = 100;
	
	if (lowest_month == 1) //june only has 30 days so it must have an if statement
	{
		for (int layer = 1; layer < 2;)
				{
                                       //june only has 30 days
					for(int rows = 0; rows < 30; rows++)
					{
						for(int cols = 0; cols < 1; cols++)
						{
							if (int_array[rows][lowest_month-1][layer] < low_temp)
							{
								low_temp = int_array[rows][lowest_month-1][layer];
							}    //reassigning value if number from array is smaller
						}
					}
					layer = layer +1;
				}
	}
	
	else
	{
		for (int layer = 1; layer < 2;)
			{
                                       //other months have 31 days
				for(int rows = 0; rows < 31; rows++)
				{
					for(int cols = 0; cols < 1; cols++)
					{
						if (int_array[rows][lowest_month-1][layer] < low_temp)
						{
							low_temp = int_array[rows][lowest_month-1][layer];
						}
					}
				}
				layer = layer +2; // for loops only once
			}
	}
	cout<<"\nThe lowest temperature is "<< low_temp <<endl;
	return low_temp;
}
