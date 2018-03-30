#Varman Joseph, Gavin Harris
#CSCI 141 - HW 3
#Due 11/28/2014

# How to run: python Sierpinski.py

import pygame, random, math

#######################
#Name: newImage()
#Input: Takes size, which is a 2-tuple (width,height) dictating the size of the image.
#Output: Creates a list of length size[0].  Each item of that list is a list of length size[1].  Each item of that list is a 3-tuple.
#   Thus, points of this data structure are denoted as image[x][y] where
#   each point has has three components: [0] for red, [1] for green
#   and [2] for blue
#Purpose: Creates the pygame drawing surface

def newImage(size):
    '''Create new 3d array (list) of pixels'''
    return pygame.surfarray.array3d(pygame.Surface(size))

#######################
#Name: showImage()
#Input: image is a list of lists of 3-tuples.  Each 3 tuple corresponds to
#   a (R,G,B) color.
#Output: the image is displayed in the window.
#Purpose: Takes an image created by newImage and displays it in open window

def showImage(image):
    width,height,depth = image.shape
    pygame.display.set_mode((width,height))
    surface = pygame.display.get_surface()
    pygame.surfarray.blit_array(surface,image)
    pygame.display.flip()

###########################
#Name: findMidpoint()
#input: The parameters are the random generated point and the random corner.
#Output: The tuple representing the dot that will be displayed on the screen.
#Purpose: Calculate the mid point between the random point and the random corner and return the point as a tuple. 

def findMidpoint(ranPoint, newCorner):
    cornerX = newCorner[0]
    cornerY = newCorner[1]
    randX = ranPoint[0]
    randY = ranPoint[1]
    midX = (cornerX + randX)//2 
    midY = (cornerY + randY)//2
    return (midX, midY)

    
#########################
#Name: main()
#Input: Does not take an input.
#Output: Displays the Sierpinski Triangle using py game.
#Purpose: Takes window dimensions from the user, calls previous functions, generates random points and corners in order to display the Sierpinski Triangle.

def main():
    pygame.init()
#Promt user for window dimensions.
    width = int(input("How large do you want your window? "))
    height = int(input("How tall do you want your window? "))
    window = newImage((width,height))
#Create and display window.        
    for x in range(width):
        for y in range(height):
            window[x][y] = (255,255,255) # Color the whole window white
    showImage(window)
#Find corners of the triangle depending on the user's input.
    points = [((width//2),0), (0,height), (width,height)]
#Generate random point.
    pointX = random.randint(0,width)
    pointY = random.randint(0,height)
    ranPoint = (pointX,pointY)
#Generate random corner.    
    corner = random.randint(0,2)
    newCorner = points[corner]
    
#Set while loop to iterate 100000 times.    
    z = 0
    r = 0
    g = 0
    b = 0
    while  z <= 100000:
        #Starting at ten.
        if z > 10:
            corner = random.randint(0,2)
            newCorner = points[corner]
            #Call findMidpoint function and split tuple that got returned.
            mid = findMidpoint(ranPoint, newCorner)
            a = mid[0]
            b = mid[1]
            #window[a][b] = (r,g,b)
            #showImage(window)
            #If statements determining the selected corner and assigning a color to it.
            if corner == 0:
                window[a][b] = (255,0,0) #makes top triangle red
            elif corner == 1:
                window[a][b] = (0,255,0) #makes bottom left corner green
            else:
                window[a][b] = (0,0,255) #makes bottom right corner blue
            #Reset ranPoint to equal the mid tuple found from the function above.
            ranPoint = mid
            z = z + 1
            #Iterate every 10 points to speed up the coloring of the triangle.
            if z%10 == 0:
                showImage(window)
        else:
            z = z + 1
    print('Done!')
    input("Enter to quit")
    pygame.quit()
#########################
    
main()
