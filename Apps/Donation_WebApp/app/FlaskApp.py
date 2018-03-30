# FlaskApp.py
# This file is the main driver of our Flask application.
# It handles all of the definitions for routing and rendering templates
# for Flask.
from flask import Flask, render_template, request, flash, url_for, redirect
import sqlite3 as sql

# Definition of the Flask Application and configuration file.
app = Flask(__name__)
app.config.from_object('config')

# Routing and Definition for the main page of our application
@app.route("/")
def main():
    return render_template('index.html')

@app.route('/index')
def home():
	return render_template('index.html')

# Routing and Definition for the Donate page of our application
@app.route('/enternew')
def new_student():
	return render_template('student.html')

# Routing and Definition for the contact page of our application
@app.route('/contact')
def Contact():
    return render_template('contact.html')

# Routing and Definition for the request page of our application
@app.route('/enternewrequest')
def new_request():
	return render_template('request.html')

# Routing and Definition for database insertion in our application
@app.route('/addrec',methods = ['POST', 'GET'])
def addrec():
	message = "Before record insertion"
	if request.method == 'POST':
		try:
			nm = request.form['nm']
			addr = request.form['add']
			city = request.form['city']
			descrp = request.form['descrp']
			Type = request.form['Type']
			aid = request.form['aid']
			with sql.connect("database.db") as con:
				cur = con.cursor()
				cur.execute("INSERT INTO allItems (name,addr,city,descrp,Type,aid) VALUES (?,?,?,?,?,?)",(nm,addr,city,descrp,Type,aid)
)
				con.commit()
				message = "Record successfully added"
			with sql.connect("database.db") as con:
				cur = con.cursor()
				cur.execute("INSERT INTO "+Type+" (name,addr,city,descrp,Type,aid) VALUES (?,?,?,?,?,?)",(nm,addr,city,descrp,Type,aid))
				con.commit()
				message = "Record successfully added into "+Type
		except:
			con.rollback()
			message = "Error in insert operation"

		finally:
			return render_template("result.html", msg = message)
			con.close()

# Routing and Definition for listing the donations in our application
@app.route('/listdonations')
def listdonations():
        con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from food  WHERE aid = 'donation'")
	foodRows = cur.fetchall()
	cur.execute("select * from hygiene  WHERE aid = 'donation'")
	hygieneRows = cur.fetchall()
	cur.execute("select * from jobs  WHERE aid = 'donation'")
	jobsRow = cur.fetchall()
	cur.execute("select * from clothes  WHERE aid = 'donation'")
	clothesRows = cur.fetchall()
	cur.execute("select * from shelter  WHERE aid = 'donation'")
	shelterRows = cur.fetchall()
	cur.execute("select * from transportation  WHERE aid = 'donation'")
	transportationRows = cur.fetchall()
        donDict ={};
        donDict['foodRows'] = foodRows;
        donDict['foodRowsSize'] = len(foodRows);
        donDict['hygieneRows'] = hygieneRows;
        donDict['hygieneRowsSize'] = len(hygieneRows);
        donDict['jobsRow'] = jobsRow;
        donDict['jobsRowSize'] = len(jobsRow);
        donDict['clothesRows'] = clothesRows;
        donDict['clothesRowsSize'] = len(clothesRows);
        donDict['shelterRows'] = shelterRows;
        donDict['shelterRowsSize'] = len(shelterRows);
        donDict['transportationRows'] = transportationRows;
        donDict['transportationRowsSize'] = len(transportationRows);
        title = "All the Donations"


	rows = cur.fetchall()
	return render_template('listAll.html', donDict = donDict,title=title)

# Routing and Definition for listing all requests in our application
@app.route('/listrequests')
def listrequests():
        con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from food  WHERE aid = 'request'")
	foodRows = cur.fetchall()
	cur.execute("select * from hygiene  WHERE aid = 'request'")
	hygieneRows = cur.fetchall()
	cur.execute("select * from jobs  WHERE aid = 'request'")
	jobsRow = cur.fetchall()
	cur.execute("select * from clothes  WHERE aid = 'request'")
	clothesRows = cur.fetchall()
	cur.execute("select * from shelter  WHERE aid = 'request'")
	shelterRows = cur.fetchall()
	cur.execute("select * from transportation  WHERE aid = 'request'")
	transportationRows = cur.fetchall()
        donDict ={};
        donDict['foodRows'] = foodRows;
        donDict['foodRowsSize'] = len(foodRows);
        donDict['hygieneRows'] = hygieneRows;
        donDict['hygieneRowsSize'] = len(hygieneRows);
        donDict['jobsRow'] = jobsRow;
        donDict['jobsRowSize'] = len(jobsRow);
        donDict['clothesRows'] = clothesRows;
        donDict['clothesRowsSize'] = len(clothesRows);
        donDict['shelterRows'] = shelterRows;
        donDict['shelterRowsSize'] = len(shelterRows);
        donDict['transportationRows'] = transportationRows;
        donDict['transportationRowsSize'] = len(transportationRows);
        title = "All the Requests"

	return render_template('listAll.html', donDict = donDict,title=title)

# Routing and Definition for the donation page of our application
@app.route('/Donate')
def Donate():
    return render_template('donate.html')

# Routing and Definition for the request page of our application
@app.route('/Receive')
def Receive():
    return render_template('receive.html')

# Routing and Definition for the Tutorial page of our application
@app.route('/Tutorial')
def Tutorial():
    return render_template('tutorial.html')

# Routing and Definition for the login page of our application
# TODO - This page is currently not in use but is here for further
# Development
@app.route('/Signup')
def Signup():
    return render_template('signup.html')

# Routing and Definition for listing food donations and requests
# in our application
@app.route('/showFood')
def showFood():

	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from food WHERE aid = '"+aid+"'")
        title = ""
	rows = cur.fetchall()
        if(aid == "donation"):
            title = "Donations for All Food"
        else:
            title = "Request for All Food"
	return render_template('list.html',  rows = rows,aid=aid, title=title)

# Routing and Definition for the dynamically created item pages
# for each donation and request in our application
@app.route('/item')
def item():
    fileid = request.args.get('fileid', None)
    tableType = request.args.get('type', None)

    con = sql.connect("database.db")
    con.row_factory = sql.Row
    cur = con.cursor()
    cur.execute("SELECT * FROM "+tableType+" WHERE  fileid=?",[fileid])
    info = cur.fetchall()
    idTable = info[0]
    return render_template('item.html',info = idTable)

# Routing and Definition for the delete functionality of items in
# our application
@app.route('/delete')
def delete():
    fileid = request.args.get('fileid', None)
    tableType = request.args.get('type', None)

    con = sql.connect("database.db")
    con.row_factory = sql.Row
    cur = con.cursor()
    cur.execute("DELETE FROM "+tableType+" WHERE  fileid=?",[fileid])
    con.commit()
    message = "you have deleted the item from the database"
    return render_template("result.html", msg = message)
    con.close()

# Routing and Definition for listing all hygiene donations and
# requests page in our application
@app.route('/showHygiene')
def showHygiene():

	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from hygiene WHERE aid = '"+aid+"'")

	rows = cur.fetchall()
	if(aid == "donation"):
            title = "Donations for All Hygiene"
        else:
            title = "Request for All Food"
	return render_template('list.html',  rows = rows,aid=aid,title = title)

# Routing and Definition for listing all job donations and
# requests page in our application
@app.route('/showOddjobs')
def showOddjobs():
	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from jobs WHERE aid = '"+aid+"'")

	rows = cur.fetchall()
	if(aid == "donation"):
            title = "Donations for All Odd Jobs"
        else:
            title = "Request for All Odd Jobs"
	return render_template('list.html',  rows = rows,aid=aid,title = title)

# Routing and Definition for listing all clothes donations and
# requests page in our application
@app.route('/showClothes')
def showClothes():
	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from clothes WHERE aid = '"+aid+"'")

	rows = cur.fetchall()
	if(aid == "donation"):
            title = "Donations for All Clothes"
        else:
            title = "Request for All Clothes"
	return render_template('list.html',  rows = rows,aid=aid,title = title)

# Routing and Definition for listing all shelter donations and
# requests page in our application
@app.route('/showShelter')
def showShelter():
	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from shelter WHERE aid = '"+aid+"'")

	rows = cur.fetchall()
	if(aid == "donation"):
            title = "Donations for All Shelter"
        else:
            title = "Request for All Shelter"
	return render_template('list.html',  rows = rows,aid=aid,title = title)

# Routing and Definition for listing all transportation donations and
# requests page in our application
@app.route('/showTransportation')
def showTransportation():
	aid = request.args.get('aid', None)
	con = sql.connect("database.db")
	con.row_factory = sql.Row

	cur = con.cursor()
	cur.execute("select * from transportation WHERE aid = '"+aid+"'")

	rows = cur.fetchall()
	if(aid == "donation"):
            title = "Donations for All Transportation"
        else:
            title = "Request for All Transportation"
	return render_template('list.html',  rows = rows,aid=aid,title = title)

# Run the application when called by Flask
if __name__ == "__main__":
    app.run(debug = True)
