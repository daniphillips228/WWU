Resource Pairing web Application
Created by:
Gavin Harris    Alex Rice-Reynolds
Zach Shaw       Jared Vigil
Advisor: Shameem Ahmed

Western Washington University
Spring 2017
CSCI 497G ICTD

How to use this software:
    Required technologies:
        Python
        Pip
        Flask

    Unpack the program files to a directory of your choice.

    Ensure you have python installed on your system. This can be downloaded at python.org
    or installed with your systems package manager (ex: sudo apt-get install python)

    Ensure you have pip installed. This can be downloaded by running python get-pip.py,
    sudo apt-get install python-pip, or if you are on windows: 
    python -m pip install -U pip setuptools

    Ensure that you have Flask installed on your system. It can be download from flask.pocoo.org
    or installed with pip: pip install Flask

    At this point you may navigate to the main directory of our application: /application/.

    This directory contains 6 files and one directory:
    config.py       :A file containing a flask configuration
    config.pyc      :The compiled version of config.py
    database.db     :The database - DO NOT MODIFY
    FlaskApp.py     :The main Flask application for our software
    license.txt     :A file containing our BSD3 clause license
    mkdb.py         :A file containing the setup code for our database
    templates/      :A directory containing our application's templates

    To run a local version of our software you may run: python FlaskApp.py
    This will start a server running on localhost:5000 (127.0.0.1:5000)

    While the server is running, navigate in a browser to either of the above urls and you
    will be greeted with our homepage.

    From here navigation through the website should be fairly straightforward. 

    For our professor Shameem Ahmed:  If you would like to see this application deployed 
    to a server you can navigate your browser to http://138.68.28.56:9191/ where we have a 
    server running the application with WSGI. We will keep this server up and running through
    June 16th 2017 after which it will be unavailable.
    
Should I aware of any issue of the code? If yes, please mention it explicitly.  

    Yes! Our code is certainly far from perfect. While our code runs and is reactive to both
    mobile and desktop formats, the formatting of the mobile version is in a very early state.
    As such running our application on a mobile browser may have unexpected or unintended
    results. We initially began this project as a tutorial for flask and as such there are some
    pieces that have confusing names; for example the page for adding a new item to the 
    database is called student.html because the database used to contain students rather
    than items. There are a few other instances of this as well.

    There are also a few files that are included but are not actually used while running
    the application. These are files that would be used in future implementation and would
    be important at that time.

Is there any part of the code incomplete? If yes, please mention it explicitly. 

    Our code is complete in its current functionality though there are some buttons and
    placeholders for things that are not yet implemented and as such appear not to work for
    instance images in item pages and the login button.

Is there any feature(s) you wanted to implement but couldn't do so? 

    We had high hopes when we began the design phase of this project. As we entered into 
    the implementation we realized that there were a few planned features that were 
    unreasonable to accomplish in the time allowed.

    Login Functionality: We really wanted to have some sort of login and signup working in
    our application but realized that doing so properly requires the addition of services 
    and apis. This was going to take more time than we had available and rather than 
    compromise the security of our current system we decided to leave the functionality 
    out and rely on the inherent security employed by our would be users.

    Item Images: This was the one feature that we really expected we would be able to 
    implement but were unable to do so. Saving images to a database in an efficient form 
    can be some what of an interesting task. The other option is to have an online hosting
    service that we could load the pictures from. Services like these cost money and none
    of us in the group wanted to put forth any sort of funds to implement this feature. 
    With more time and some funding we would easily be able to add this functionality to 
    our implementation.

    Chat Functionality: We wanted to have some way for users to converse within our application
    but we quickly realized that there are legal limitations as well as some complex code 
    required to implement a forum or text form of chat. This is another function that most 
    people prefer to have handled by a specialist in this area and as such would be code that 
    we would get from some other existing library or api. The time needed to research and 
    integrate this into our application placed it outside of our four week development scope.


