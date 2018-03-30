#Gavin Harris, Lab 3a, 
#My program asks the user for an input of how many coins they have
#Using a while loop it asks what each coin is up to whatever number inputed

# How to run: python ChangeCounter.py

coins = int(input("How many coins do you have?"))


count = 0
quarters = 0
dimes = 0
nickles = 0
pennies = 0
total = 0
dollars = 0
cents = 0

while count < coins:
    count = count +1
    coin = input("What is coin #" + str(count)+ "? (enter q for quarter, d for dime, n for nickel, p for penny):")

#here im giving the input of either q d n p values based on the coin they entered
    if coin == "q":
        quarters = quarters + 25
    elif coin == "d" :
        dimes = dimes + 10
    elif coin == "n" :
        nickles = nickles + 5
    elif coin == "p" :
        pennies = pennies +1
    else:
        print("Unkown input")

#here im adding the totals of the coins entered and making them the variable total

total = quarters + dimes + nickles + pennies

#this is where I split the final output determining how many dollars and how many cents are in the total value

if total < 100:
    cents = total

else:
    dollars = int(total/100)
    cents = total - (dollars * 100)

#in the final print statement I put in the variable of the total dollars and cents to output the total to the user

print("You have",dollars, "dollars and",cents,"cents. Wow, you are rich!")
