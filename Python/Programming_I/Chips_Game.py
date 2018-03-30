#Gavin Harris, W01099190
#CSCI 141, Monday lab 2pm
#Due 11/7/14

# How to run: python Chips_Game.py

#My program starts the game by calling the initGame function, this will get the amount
#of chips the user will want to start the game with in the two piles. Then we make a while
#loop to run until both piles equal 0, then it will pop out of the loop and declare a winner
#Next we call the displayPiles function to start the game, then call updatePile function
#to update the new values in the piles. After updating we will use displayPiles again to 
#show user the piles again, then we will call the computerMove function to take chips out
#of the other pile. We give the pile1 and pile2 variables the updated value from the cmove
#variable. Then we loop until both the piles are 0. Then we print out the winning statement.

import time

def initGame():
    print("Welcome to the game of chips, lets get started\n")
    p1 = int(input("How many chips would you like to start with? "))
    p2 = p1
    return (p1, p2)
    
#Function: initGame()
#Input: we ask for input to get the number of chips to start the game, no parameters.
#Output: We will return the amount of chips in both pile1, and pile2.
#Purpose: The purpose is to start the game and get the initial value of the piles,
#there will be the same amount in both piles so we set p2 to be the same value as p1
#so the returned value of pile 1 and pile2 will be the same. Will be called only once.

def displayPiles(pile1, pile2):
    out1 = ""
    out2 = ""
    print("\nHere are the piles:")
    for i in range(1, pile1+1):
        out1 = out1 + "o"
        if i %5 ==0:
            out1 = out1 + " " #creates space after 5 o's
    print("Pile 1: ", out1)
 
    for i in range(1, pile2+1):
        out2 = out2 + "o"
        if i %5 ==0:
            out2 = out2 + " " #creates space after 5 o's
    print("Pile 2: ", out2)
 
    return "" #we don't return any value
    
#Function: displayPiles(pile1, pile2)
#Input: the input is the value in pile1 and pile2, parameters are pile1, pile2. They are 
#integer data types.
#Output: It will print the amount of chips in o's in both groups of 5's, doesn't return
#any values.
#Purpose: This will keep track of the piles and show the user how many chips are in the
#piles with the o's in groups of 5. 

 
def getHumanMove(pile1, pile2):
    loop = False
    while (loop == False):
        take = int(input("\nWhich pile would you like to take from? (1 or 2) "))
        if take == 1:
            legal = int(input("How many would you like to take from pile 1? "))
            if legal > pile1:
                print("\nPile 1 does not have that many chips. Try again")
 
            elif legal < 1:
                print("\nYou must take at least one chip. Try again")
 
            else:
                print("\nThat was a legal move. Thank you.")
                loop = True
 
                return (pile1+100, legal) #we added 100 to help organize which pile is 1
                						  #and which is pile 2
        
        else:
            legal = int(input("How many would you like to take from pile 2? "))
            if legal > pile2:
                print("\nPile 2 does not have that many chips. Try again")
 
            elif legal < 1:
                print("\nYou must take at least one chip. Try again")
 
            else:
                print("\nThat was a legal move. Thank you.")
                loop = True
 
                return (legal, pile2)
        
#Function: getHumanMove(pile1, pile2)
#Input: the input is which pile the user would like to take from, and then how many chips
#they want to take out of the pile they chose. Parameters are pile1 and pile2.
#Output: Will return the amount we are taking out of the pile, and the pile we are taking
#the chips out of
#Purpose: To find the move the human wishes to make, ask which pile and the amount 
#to take, using a while loop to repeat until user enters correct information

 
def updatePiles (move, pile1, pile2):
    if move[0] > move[1]:
        pile1 = pile1- move[1]
    else:
        pile2 = pile2 - move[0]
 
    return(pile1, pile2)

#Function: updatePiles (move, pile1, pile2)
#Input: the parameters are move, pile1, and pile2.
#Output: We return the values of the new piles
#Purpose: The purpose is to return the new values of piles so the piles are updated.
#

 
def computerMove (pile1, pile2, comPile):
    print("\nNow it is the computers turn")
    if pile1> pile2:
        subtract= pile1- pile2
        pile1 = pile2
    else:
        subtract = pile2 - pile1
        pile2 = pile1
    time.sleep(5)
    print("The computer has taken", subtract, "chips from pile", comPile )
 
    return (pile1, pile2)

#Function: computerMove (pile1, pile2, comPile)
#Input: The 3 parameters are pile1, pile2, and comPile.
#Output: It will return the new values of pile1, and pile2
#Purpose: The purpose for this function is to take the same amount of chips as the user did
#but for the other pile.
 
def main():
    chips = initGame()
    pile1 = chips[0]
    pile2 = chips[1]
    while (pile1 > 0 and pile2 > 0):
        print("\nIt is your move human.")
        piles = displayPiles(pile1, pile2)
        move = getHumanMove(pile1, pile2) #create tuple with info on which pile human took from and how much
        if move [0] > 100: #from earlier when we added 100 to pile one
            comPile = 2 #if user did pile 1, computer will take from pile 2
        else:
            comPile = 1
        update = updatePiles(move, pile1, pile2)
        pile1 = update[0] #updating pile one to equal position 0 of the tuple update
        pile2= update[1]  #updating pile two to equal position 1 of the tuple update
        piles = displayPiles(pile1, pile2)
        cmove = computerMove(pile1, pile2, comPile)
        pile1 = cmove[0] #pile1 after the computer move, 
        pile2 = cmove[1]
    if cmove == (0, 0): #once after the computer moves and the cmove tuple is 0,0
        print("\nThe game is over, computer wins")

    else:
        print("\ncongrats, user wins the game")      
main()
