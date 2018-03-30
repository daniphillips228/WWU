//Gavin Harris, WED lab 2pm

/*My code works by starting in a while loop and asking the user for input in the form of a string.
As soon as we get input we put it into the is_move_legal function which will return a 
boolean statement about whether it is legal (true) or invalid (false). Inside the 
function we state the variable of all the counts for the roman numerals so we reset
the values in case of an invalid answer, then do a for loop through the string
that the user entered. Each time we iterate through the string we set the char 
equal to the char variable check, then do a switch statement on check to count how
many of each character was entered by user. Once we are done iterating through the
string we have a series of if and if else statements to determine if the user entered
to many of a certain roman numeral, and if so we return false and the while loop 
will start all over again. If the enteries were all valid then we return true and 
pop out of the while loop in the main function. Once out of the main function we call
the convert_digit function which will return us the decimal conversion of the roman
numerals. The parameter of the convert_digit function is the count variables of all
the possible roman numerals, and then we just do math inside the function to add 
them up.

 Command line instructions:
 - compile with g++ Roman_Numerals.cpp -o Roman_Numerals
 - run with ./Roman_Numerals
*/

#include<iostream>
#include<string>
using namespace std;

bool is_move_legal(string);
int convert_digit(int, int, int, int, int, int, int);
bool if_legal;
int countM = 0;
int countD = 0;
int countC = 0;
int countL = 0;
int countX = 0;
int countV = 0;
int countI = 0;
char check;
int total = 0;
int rm_total;
string roman_number;

int main()
{


while (if_legal == false) //loops until valid input by user
{
	cout<< "Please enter a roman numeral (M, D, C, L, X, V, I) ex. DVII"<< endl;
	cin>> roman_number;
	if_legal = is_move_legal (roman_number); // calls is_move_legal function
	
	if (if_legal == false)
		cout<< "Roman numeral is invalid. ";

}
//calling convert_digit function below
rm_total = convert_digit (countM, countD, countC, countL, countX, countV, countI);
cout<< "decimal equivalent is " << rm_total << endl;

return 0;
}

bool is_move_legal(string roman_number)
{
	countM = 0;
	countD = 0;
	countC = 0;
	countL = 0; //reseting the count variables 
	countX = 0;
	countV = 0;
	countI = 0;
	
	for (int i = 0; i < int(roman_number.length()); i++) //iterates as many times as there is characters in the string
	{
		check = roman_number[i]; //created char variable for switch statement to check it
	
		
		switch(check)
		{
			case 'M': countM = countM+1; break; 
			case 'D': countD = countD+1; break;
			case 'C': countC = countC+1; break;
			case 'L': countL = countL+1; break; //adding up the amount of characters
			case 'X': countX = countX+1; break;
			case 'V': countV = countV+1; break;
			case 'I': countI = countI+1; break;
			default: return false; break; //if not a roman numeral it will return false
		}
		
	}
  		
  	if (countM > 4)
		return false;
  	
  	else if (countD > 1)
    	return false;
  	
  	else if (countC > 4)
    	return false;
  	
  	else if (countL > 1) //determining if input was legal
  		return false;
  	
  	else if (countX > 4)
    	return false;
  	
  	else if (countV > 1)
 		return false;
 	
 	else if (countI > 4)
    	return false;
  	
  	else
    	return true; //if legal will return true
    
}
//parameters for below function are the counts of all possible roman numeralls
int convert_digit(int countM, int countD, int countC, int countL, int countX, int countV, int countI)
{
	total = countM * 1000; //M worth 1000
	total = total + (countD * 500); //adding new variable amount to running total
	total = total + (countC * 100);
	total = total + (countL * 50);
	total = total + (countX * 10);
	total = total + (countV * 5);
	total = total + countI;
	
return total; //returns the total, which is integer
}
	