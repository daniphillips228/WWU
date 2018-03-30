from math import radians, sin, cos, atan, atan2, sqrt
from simplemapplot import make_us_state_map
from time import sleep

# How to run: python3 HW4.py, then enter sandwich.txt and search for word sandwich
# then creates an output_state_map.svg file showing the sentiments score for each state

#######################
# Name:readStateCenterFile(stateCenterDict)
# Input:
# Output:
# Purpose: reads in the file of state centers as described in the handout
#
def readStateCenterFile(stateCenterDict):
    stateFile = open("stateCenters.txt")
    for line in stateFile:
        value = line.split(',')
        stateCenterDict[value[0]] = (float(value[1]), float(value[2]))

    stateFile.close()
########################

########################
# Name:readTweetFile(tweetList)
# Input:
# Output:
# Purpose: reads the tweets from the file into a list.  The file is specified in
# the "open" command.  Change the "open" call when you want the big file. 
#
def readTweetFile(tweetList):
    tweetFile = open("sandwich.txt", encoding="utf-8")
    
    for line in tweetFile:
        try:
            value = line.split("\t")
            lat,long = value[0].split(",")
            lat = float(lat[1:])
            long = float(long[:-1])
            tweetList.append(((lat,long),str(value[3])))

        except:
            None

    tweetFile.close()

#########################

#########################    
# Name:readSentimentFile(sentimentDict)
# Input:
# Output:
# Purpose:this function will read in the sentiment file and create a 
# dictionary with key: word/phrase and a value: float in range -1..1
#
def readSentimentFile(sentimentDict):
    sentimentFile = open("sentimentsFull.csv")
    for line in sentimentFile:
            
            value = line.split(",")
            
            sentimentDict[value[0]] = (float(value[len(value)-1]))
    
    sentimentFile.close()  

##########################

###########################
# Name:distance (lat1, lon1, lat2, lon2)
# Input:
# Output:
# Purpose:takes a latitude and longitude for two given points and returns
# the great circle distance between them in miles
#
def distance (lat1, lon1, lat2, lon2):
    earth_radius = 3963.2  # miles
    lat1 = radians(float(lat1))
    lat2 = radians(float(lat2))
    lon1 = radians(float(lon1))
    lon2 = radians(float(lon2))
    dlat, dlon = lat2-lat1, lon2-lon1
    a = sin(dlat/2) ** 2  + sin(dlon/2) ** 2 * cos(lat1) * cos(lat2)
    c = 2 * atan2(sqrt(a), sqrt(1-a));
    return earth_radius * c;

#####################################################
#Name: display_stateDict (stateCenterDict, found_Tweets, tweetList)
#Input:
#Output:
#Purpose: Returns a 3 tuple of...
# a list of every state used
# a dictionary where position of tweet is key, and state is value
# an updated list of all the positions of tweets we found, excludes tweets to far
#away from the US

def display_stateDict(stateCenterDict, found_Tweets, tweetList):
    counting_state_list = []
    tweet_state_dict = {}
    updated_found_Tweets = []
    
    for y in found_Tweets:
        
        dist= 100000
        lat1 = float(tweetList[y][0][0])
        lon1 = float(tweetList[y][0][1])
        new_dist = 0
        for i in stateCenterDict.keys():
            lat2 = float(stateCenterDict[i][0])
            lon2 = float(stateCenterDict[i][1])
            new_dist = distance(lat1,lon1, lat2, lon2)
            
            if new_dist <= 400:
                
                if new_dist < dist:
                    dist = new_dist
                    state = i

        if dist <= 400:
            if lat1 > 55:
                    state = str("AK")
            elif lat1 < 26:
                    state = str("HI")
            counting_state_list.append(state)
            tweet_state_dict[y] = state
            updated_found_Tweets.append(y)
                
            for x in tweet_state_dict:
                tweet_state_dict[y] = state
        else:
            if lat1 > 55:
                    state = str("AK")
            elif lat1 < 26:
                    state = str("HI")

            counting_state_list.append(state)
            tweet_state_dict[y] = state
            updated_found_Tweets.append(y)
                
            for x in tweet_state_dict:
                tweet_state_dict[y] = state
    
    return counting_state_list, tweet_state_dict, updated_found_Tweets

#######################################################
#Name: counting_states
#Input:
#Output:
#Purpose: builds stateCountDict

def counting_states (states_list, stateCenterDict, stateCountDict):
    counting_dict = {}
    for i in stateCenterDict.keys():
        stateCountDict[i] = 0
    
    for y in states_list:
        stateCountDict[y] = stateCountDict[y] + 1
    sorted_count = list(stateCountDict.keys())
    sorted_count.sort()
    for k in sorted_count:
        a = 1
    
#########################################################3
#Name: assign_sentiment_state
#Input:
#Output:
#Purpose: Provides the stateSentimentScore dictionary with the sentiment scores
#as the values for the correct state which is the key.
        
def assign_sentiment_state (found_Tweets, tweet_list_sandwich, sentimentDict, stateSentimentScore, find_state, stateCountDict):

    for i in found_Tweets:

        for y in sentimentDict.keys():

            if y in tweet_list_sandwich[i].lower():

                before = stateSentimentScore[find_state[i]]
                value = sentimentDict[y]
                count = stateCountDict[find_state[i]]
                
                stateSentimentScore[find_state[i]] = before + float(value / count)

############################
#Name: display_tweetlist
#Input:
#Output:
#Purpose: Creates a list that contains the text of the tweet. 
                
def display_tweetlist(tweetList):
    x= 0
    s_tweetList = []
    for i in tweetList:
        s_tweetList.append(tweetList[x][1])
        x= x+1
    return s_tweetList

#################################
#Name: find_tweet(wordOfInterest, tweet_list_sandwich)
#Input:
#Output:
#Purpose: Seartches for the word of interest entered by the user in the tweet.
#We use the list we created and search the word in each position of the list.
#If found we append to a new list found_tweets, and return that value.

def find_tweet(wordOfInterest, tweet_list_sandwich):
    x = 0
    found_tweets = []
    for tweet in tweet_list_sandwich:

        if wordOfInterest in tweet.lower():
            found_tweets.append(x)

            x = x+1
        else:

            x = x+1
    return found_tweets

#############################
#Name: assignSentimentColors
#Input:
#Output:
#Purpose: We loop through stateSentimentScore to find the maximum and minimum
#sentiments from all tweets found. We loop a second time to assign each state
#an index for color based on how close it is to the max and min sentiment
#We reassign the value in the stateSentimentScore dictionary to the index and
#we return that new dictionary

def assignSentimentColors (stateSentimentScore):
    min = 1000
    max = 0

    for i in stateSentimentScore: #i is the state abreviation

        temp = stateSentimentScore[i] #temp is the sentiment score pulled from key i

        if temp > max:  #searching for max sentiment score
            max = temp
        elif temp < min: #searching for min sentiment score
            min = temp

    for x in stateSentimentScore: #x is state abreviation

        test = stateSentimentScore[x] #test is sentiment score pulled from key x


        if test > 0: #if its a positive sentiment
            if test >= (.6 *max):
                stateSentimentScore[x] = 8
               
            elif test < (.6 * max) and test >= (.3 * max):
                stateSentimentScore[x] = 7
                
            elif test < (.3 * max) and test >= (.15 * max):
                stateSentimentScore[x] = 6
                
            else:
                stateSentimentScore[x] = 5
                

        elif test == 0: #no sentiment, neutral
            stateSentimentScore[x] = 4
            

        else: #negative sentiment
            if test <= (.6 * min):
                stateSentimentScore[x] = 0
                
            elif test > (.6 * min) and test <= (.3 * min):
                stateSentimentScore[x] = 1
                
            elif test > (.3 * min) and test <= (.15 * min):
                stateSentimentScore[x] = 2
                
            else:
                stateSentimentScore[x] = 3
     
    return stateSentimentScore

############################
#MAIN
############################
def main():
    i = 0
    stateCenterDict = {}        #Key: state abbrev  Value: 2-tuple of (lat,long) of state center
    tweetList = []              #list of two items, first is 2-tuple (lat,long), second is tweet
    sentimentDict = {}          #Key: words/phrases Value: sentiment score from -1 .. 1 for each word

    stateSentimentScore = {}    #Key: state abbrev  Value: sentiment score for word
    stateCountDict = {}         #Key: state abbrev  Value: count for word
    
    readStateCenterFile(stateCenterDict)
    readTweetFile(tweetList)

    for key in stateCenterDict:
        stateCountDict[key] = 0
        stateSentimentScore[key] = 0

    wordOfInterest = input("what word are you looking for? ")
    
    wordOfInterest = wordOfInterest.lower()
    
    tweet_list_sandwich = display_tweetlist(tweetList) #list of text from all tweets
    
    found_Tweets = find_tweet(wordOfInterest, tweet_list_sandwich) #list of positions of tweets that had the wordOfInterest
    
    states_tuple = display_stateDict(stateCenterDict, found_Tweets, tweetList)
    states_list = states_tuple[0]# list of every state used
    find_state = states_tuple[1] # a dictionary where key is position of tweet, and a value of state
    updated_Found_Tweets = states_tuple[2] #updated list of positions of tweets that have the word of interest and were assigned states

    counting_states(states_list, stateCenterDict, stateCountDict) # builds stateCountDict
  
    readSentimentFile(sentimentDict) #builds sentimentDict

    assign_sentiment_state(updated_Found_Tweets, tweet_list_sandwich, sentimentDict, stateSentimentScore, find_state, stateCountDict)

    state_sentiment_scores = stateSentimentScore #creating new variable
    
    SORTED = sorted(stateSentimentScore.keys()) #organizing by keys aka state abbreviations
    for organize in SORTED:
        print(organize, stateCountDict[organize], state_sentiment_scores[organize])
    
    Colors = assignSentimentColors(stateSentimentScore) #dictionary how states and their index for color of map

    ###########################################################################
 
    # FILL IN YOUR MAIN CODE HERE - Add 3-5 functions outside of main to help.  

    ###########################################################################

    ##################
    #I used color brewer to help me pick the colors.
    #Red represents positive sentiment, shading to orange and yellow for less positive senitment.
    #Light gray means no data.
    #Dark blue represents negative sentiment, shading to light blue for less negative sentiment.
    ####INDEX#########[     0          1         2          3          4          5          6          7           8   ]  
    SENTIMENTCOLORS = ["#4575b4", "#74add1", "#abd9e9", "#e0f3f8", "#bababa", "#fee090", "#fdae61", "#f46d43", "#d73027"]
    ####COLOR#########["darkblue", "blue", "lightblue", "lightcyan", "lightgray", "yellow", "orange", "darkorange", "red"] 

    make_us_state_map(stateSentimentScore, SENTIMENTCOLORS)

#
#############################   
main()
    
