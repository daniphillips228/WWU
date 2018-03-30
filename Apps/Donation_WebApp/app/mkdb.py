# mkdb.py
# This file contains the code required to initialize our empty database
# and create the necessary tables to store all of the donations and
# requests.
import sqlite3

conn = sqlite3.connect('database.db')
print "Database opened successfully"

conn.execute('CREATE TABLE allItems (name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
print "Table created Successfully"

conn.execute('CREATE TABLE food (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT )')
conn.execute('CREATE TABLE hygiene (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
conn.execute('CREATE TABLE jobs (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
conn.execute('CREATE TABLE clothes (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
conn.execute('CREATE TABLE shelter (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
conn.execute('CREATE TABLE transportation (fileid INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, addr TEXT, city TEXT, descrp TEXT, Type TEXT, aid TEXT)')
conn.close()
