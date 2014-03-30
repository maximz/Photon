

# Crawls and scrapes the top 5000 articles on wikipedia

import re
import os
import time
import urllib2

# 5000 most popular wikipedia pages in the last 10 days
starturl = "http://en.wikipedia.org/wiki/User:West.andrew.g/Popular_pages"

page = urllib2.urlopen(starturl)
html = page.read()

# get the names of the urls

p = re.compile('<a href="/wiki/([^":]+)"')
pages = p.findall(html)
print pages

# make a directory based on the day the popular list was accessed

basedir = './wikipedia'
if not os.path.exists(basedir):
	os.makedirs(basedir)
basedir += '/' + str(int(time.time()))
if not os.path.exists(basedir):
	os.makedirs(basedir)

# save the pages locally

for page in pages:
	try:
		pageurl = 'http://en.wikipedia.org/wiki/' + page
		pageDoc = urllib2.urlopen(pageurl)

		localFile = open(basedir + '/' + page + '.html', 'a+') # a+ means read and write (append)
		localFile.write(pageDoc.read())
		localFile.close()
		print(pageurl + ' saved to ' + basedir + '/' + page)
	except IOError as err:
		print(err)
		continue