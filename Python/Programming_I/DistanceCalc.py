#Gavin Harris, W01099190
#CS141: Monday Lab 2pm
#Due 11/1/14

#My code asks user to enter data for first point then calls getUserInput to get input from the user then stores them as variables,
#then my main program asks user to enter data for second point and calls getUserInput again to get input again and stores them as different variables
#then the main program calls on each function one after the other and using the variables we found earlier as parameters to solve distances and etc for us
#we print the returned values found from functions to user using print statements.
#additional comments below

# How to run: python DistanceCalc.py

x1 = 0
y1 = 0
distance = 0 #intializing
midpoint = 0
hamming = 0

def getUserInput ():
	x = int(input("x coordinate: ")) #asking for x coordinate
	y = int(input("y coordinate: ")) #asking for y coordinate

	return (x, y) #returning both coordinates and creates a tuple data type

#Function: getUserInput()
#Input: The input is the coordinates for the points, no parameters, integers
#Output: The output is the x and y coordinate, returns a tuple data type
#Purpose: Is to find the data points to give us values to plug into other functions

def getDistance (x1, y1, x2, y2):
	dist = (((x2-x1)**2) + ((y1 - y2)**2))**.5 #using parameters given we solve for distance and store as dist
	
	return dist #return our variable we solved for
    
#Function: getDistance(x1, y1, x2,y2)
#Input: The input is the data points for the two points user entered in previously.
#the parameters are x1, y1, x2, y2. The data types are integers.  
#Ouput: The output is the distance between the two points, and it gets converted to a float data type
#Purpose: The purpose is to return the distance and print out the distance to the user in main function

def findMidpoint (x1, x2, y1, y2):
	xMidpoint = (x1 + x2) / 2 #finding the xmidpoint given our parameters storing as variable
	yMidpoint = (y1 + y2) / 2 #finding the ymidpoint ...
	
	return (xMidpoint, yMidpoint) #returning the two midpoints and creatings a tuple data type

#Function: findMidpoint (x1, x2, y1, y2)
#Input: The input is the data points entered by the user in getUserInput function.
# the parameters are x1, x2, y1, y2. The data types are integers
#Ouput: The output is the x, y coordinate of the midpoint of the two points, it is a tuple data type.
#Purpose: Find the midpoint coordinates using the two points and print out the result to the user in the main function

def findHamming (x1, x2, y1, y2):
	hammingDistance = abs(x1 - x2) + abs(y1 - y2) #using parameters we solve for the hamming distance and store as variable
	
	return (hammingDistance) #returing variable we solved for

#Function: findHamming (x1, x2, y1, y2)
#Input: The input is the data points entered by the user ni getUserInput function
# the parameters are x1, x2, y1, y2. The data types are integers.
#Ouput: The output is the Hamming distance between the two points, and it is an integer data type.
#Purpose: Find the hamming distance and print out the result to the user from the main function.

def main ():
    print "Enter data for first point" #asking user for  data about first point
    first = getUserInput() #storing input from user via getUserInput function as variable
    x1 = first[0] #seperating the x value from the tuple, and storing it as first x coordinate
    y1 = first[1] #seperating the y value from the tuple, and storing it as first y coordinate

    print "\nEnter data for second point" #asking user for data about second point, used \n for better spacing
    second = getUserInput() #storing second point values as seperate variable
    x2 = second[0] #seperating x value from tuple, and storing it as second x coordinate
    y2 = second[1] #seperating y value from tuple and storing it as second y coordinate

    distance = getDistance (x1, y1, x2, y2) #turning returned value from function getDistance into a variable
    print "\nThe straight line distance between these points is" , distance #printing result to the user, used \n for better spacing

    midpoint = findMidpoint (x1, x2, y1, y2) #turning returned value from function findMidpoint into a variable
    print "The midpoint of these points is", midpoint #printing result to the user
   
    hamming = findHamming (x1, x2, y1, y2) #turning returned value from funtion findHamming into a variable
    print "The hamming distance between these two points is", hamming #print result to the user
main()