/*Gavin Harris, wed lab 2pm

 Command line instructions:
 - compile with g++ chipsGame.cpp -o chipsGame
 - run with ./chipsGame
 
*/

/* Game of Chips */
#include <iostream>
using namespace std;
int chips;
int init_game(); 
int ask_move(bool, int);
bool is_move_legal(int, int); 
void declare_winner(bool);
int main() 
{ 
    bool player1turn = false; // This variable keeps track of whose turn it is. // When true it is player 1’s turn and when // false it is player 2’s turn.
    bool game_over = false; // will be set to true once the chips are gone. int chips_in_pile; int chips_taken;
    
    int chips_in_pile = init_game();
    
    while (game_over == false) 
    { 
    
        int chips_taken = ask_move(player1turn, chips_in_pile);        
        chips_in_pile = chips_in_pile - chips_taken;
        
        if (chips_in_pile == 0) 
        { 
            game_over = true; 
            declare_winner(player1turn); 
        } 
        else 
        { 
            cout << "There are " << chips_in_pile << " chips left." << endl << endl;
            player1turn = !player1turn; 
        }
        
    } 
    
    return(0);
}

/*
This function gets called from the main function once the chips_in_pile is equal to zero.
Its parameter is player1turn and I used an if else statement to determine the winner, if
the bool of player1turn is true player2 is the winner and if the bool is false then player
1 is the winner.
*/
void declare_winner(bool player1turn)
{ 
    if (player1turn == true)
    {
        cout << "Congratulations player 2! You won the game of chips." << endl;
    }   
	else
    {
        cout<< "Congratulations player 1! You won the game of chips." << endl;
 	}
}

/* 
This function is called by the ask_move function to find out if the user entered a valid
integer, we will take the value entered chips_taken and since we are not allowed to take
more than half of the chips_in_pile we divide chips_in_pile by 2 and if the chips_taken
is bigger than that it will be an invalid answer and return false, the chips_taken must 
also be more than one so we set up our if statement to have an and statement so both must
be true. I added an or to the if statement for the situation when theres only 1 chip in pile.
If both are true, or the or is true it will return true, or else it will return false.
*/ 
bool is_move_legal(int chips_taken, int chips_in_pile) 
{ 
if(((chips_taken <= chips_in_pile/2) and (chips_taken >= 1)) or chips_in_pile == 1)
	{
 	return true;
	}
 
else
	{
 	return false;
	}
} 

/* 
This function is called by the main program after the init_game function starts.    
It starts by asking player 1 to select an amount of chips to take from the chips_in_pile
and stores it as chips_taken, then calls the is_move_legal function to determine if the 
move was legal. We assign this to the bool legal, once it has been determined its a legal move,
if (legal == true) we return chips_taken. If not legal the function while ask the user to input 
another answer and will be in while loop until legal answer is entered, then will 
return chips_taken.
*/
int ask_move(bool player1turn, int chips_in_pile)
{ 
 int chips_taken;
cout << "Player" << player1turn + 1 << " " << "- how many chips would you like to take? ";
    cin >> chips_taken; 
    
    bool legal = is_move_legal (chips_taken, chips_in_pile);
    
    if (legal == true)
    	return chips_taken;
    else 
    	while (is_move_legal (chips_taken, chips_in_pile) == false)
    		{
				cout<< "Sorry that was not a legal move. Try again."<<endl<<endl<<"Player"<<
				player1turn + 1 << " " << "- how many chips would you like to take? ";
				cin >> chips_taken; 
				is_move_legal (chips_taken, chips_in_pile);
     
  			}
    	return chips_taken;
    	
}

/*
This function will be the first called by the main function and will ask the user to enter
a number to the starting value of chips which must be between 2-50 inclusively. It will 
store the entered data as chips and if valid be returned, if not entered correctly it will
go through the while loop until a valid answer is entered by user.
*/
int init_game() 
{ 
    cout<< "How many chips do you want to start with> (2-50 inclusive)" << endl;
    cin>> chips;
    while (chips < 2 or chips > 50)
    {
        cout<< "Sorry you must enter a number between 2 and 50 inclusive. Try again." << endl<<
        endl<< "How many chips do you want to start with> (2-50 inclusive)"<< endl;
        cin>> chips;
    }
return chips;
}