#Gavin Harris, Lab 3b

# How to run: python ChangeMaker.py

#ask user for input on how much change for program
change = int(input("How much change are you trying to give (in cents)? "))

quarters = 0
dimes = 0
nickels = 0
pennies = 0

#it will loop as long as the change is above 0 cents
while change > 0 :
#if the change is at or above 25 cents we will use quarters and subtract 25 cents from total and add one quarters to quarter variable
    if change >= 25:
        change = change - 25
        quarters = quarters + 1
#if the change is at or above 10 but less than 25 we will use dimes, subtract 10 from total and add one dime to dimes variable
    elif change >= 10:
        change = change - 10
        dimes = dimes + 1
#if the change is at or above 5 but less than 10 we will use nickels, subtract 5 from total and add one nickel to nickels variable
    elif change >= 5:
        change = change - 5
        nickels = nickels + 1
#if the change is at or above 1 but less than 5 we will use pennies, subtract 1 from total and add one penny to pennies variable
    elif change >= 1:
        change = change -1
        pennies = pennies + 1
#print unknown input if not valid input
    else :
        Print("Unknown input")
#this is the start of the print statement to tell the user what coins to use, we put end=" " to make it all go on one line of code
print("You should give the customer:", end= " ")

#I use if statements to figure out which coins will be over zero and if they are over zero it will do the print statement saying how many and has end="" to put them all on the same line of code. If at 0 it will be ignored
if quarters > 0:
    print( quarters, "quarters", end=" ")
else: ""

if dimes > 0:
    print(dimes, "dimes", end= " ")
else: ""

if nickels > 0:
    print (nickels, "nickels", end= " ")
else : ""

if pennies > 0:
    print( pennies, "pennies", end = " ")
else : ""