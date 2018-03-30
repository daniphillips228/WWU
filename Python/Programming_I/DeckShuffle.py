#Gavin Harris, W01099190
#CSCI 141, Monday Lab 2pm
#Due 11/24/14

#My program starts by asking the user to enter in how many cards will be in the
#deck, the ask them how many times they want to shuffle. Then I use the integer
#entered in for cards in the deck and call the initDeck function to put the cards
#in a list, then printing out the list to the user saying "Initial deck".
#We return the list from the function, then call the cutDeck function. We use
#the list we got from initDeck and if the list has an even amount of numbers
#we will the split the deck in half if not we make sure the bottom pile will have
#the extra card. We return the piles from the function and this creates a tuple.
#We take the first pile and seperate it from the second one and this assign this
#to the bottom_deck variable, then assign the other top_deck. Now I a create a
#while loop which will run until we loop as many times as user stated earlier.
#While in the while loop we call the shuffle function and this will combine the
#two decks and shuffle them riffle style according to rubric. In the function
#we will put the first bottom_deck card in first and will put a bottom_deck card
#in every two cards and in between put the first top_deck cards in. Then after
#we print out the shuffled hand, we re-assign the variable new_deck to deck and
#put call the cutDeck function again to get ready to re-shuffle deck again in
#case we need to shuffle again. 

# How to run: python DeckShuffle.py


def initDeck (starting_cards):
    numberList = [ ]
    for i in range(starting_cards):
        numberList.append (i + 1)
    print("Initial deck:    ",numberList)
    return (numberList)

#Function: initDeck (starting_cards)
#Input: Our parameter is the starting_cards entered in by user
#Output: We will print a list of the deck of cards to user and return the list
#Purpose: To create a list of the cards to use for other functions.

def cutDeck (deck):
    if len(deck) % 2 ==0:
        deck1 = deck[ 0: len(deck)//2 ]
        deck2 = deck[ len(deck)//2 : len(deck) ]

    else:
        deck1 = deck[ 0 : ((len(deck)//2)+1)] #makes deck1 have the extra card
        deck2 = deck[ ((len(deck)//2)+1) : len(deck) ]   
    return (deck1, deck2)

#Function: cutDeck (deck)
#Input: Our parameter is deck, which is the list from initDeck of cards
#Output: We return deck1, deck2. Which will return a tuple of the two piles
#we split from the original deck of cards.
#Purpose: We create a tuple so we can access each pile seperately from the other
#without losing information. 

def shuffle(top_deck, bottom_deck):
    shuffled_deck = bottom_deck + top_deck
    shuffled_deck[1::2] = top_deck #skips an index, starts after bottom_deck
    shuffled_deck[::2] = bottom_deck #skips an index, starts at first position
    return (shuffled_deck)

#Function: shuffle (top_deck, bottom_deck)
#Input: Our parameters are the variables top_deck, bottom_deck
#Output: We return shuffled_deck the combination of the shuffling as one whole
#list.
#Purpose: We combined the two decks of cards to shuffle them, turning them both
#into lists and then combining them to make the shuffled new deck. 

def main():
    count = 1
    starting_cards = int(input("How many cards are in the deck? "))
    number_shuffles = int(input("How many times do you want to shuffle? "))
    deck = initDeck (starting_cards)
    split = cutDeck(deck)
    bottom_deck = split[0]
    top_deck = split[1]
    while number_shuffles >= count:
        new_deck = shuffle(top_deck, bottom_deck)
        print("After", count, "shuffles:", new_deck)
        count = count +1
        deck = new_deck
        split = cutDeck(deck)
        bottom_deck = split[0]
        top_deck = split[1]
        

main()
