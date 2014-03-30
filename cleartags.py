import BeautifulSoup
import os
import time

basedir = './wikicontent'
if not os.path.exists(basedir):
	os.makedirs(basedir)

i = 0
for filename in os.listdir('./wikipedia/1396144344'):
	doc = open('./wikipedia/1396144344/' + filename, 'r').read()
	soup = BeautifulSoup.BeautifulSoup(doc)
	localFile = open(basedir + '/' + str(int(time.time())) + '.txt', 'a+') # a+ means read and write (append)
	for tag in soup.recursiveChildGenerator():
		if isinstance(tag, BeautifulSoup.Tag):
			tagString = str("" if tag.string is None else tag.string)
			localFile.write(tagString)
	i += 1
	print("HTML File detagged")
	print(i)
	localFile.close()

i = 0

basedir = './wikiprocessed'
if not os.path.exists(basedir):
	os.makedirs(basedir)

for filename in os.listdir('./wikicontent'):
	doc = open('./wikicontent/' + filename, 'r').read()
	localFile = open(basedir + '/' + str(int(time.time())) + '.txt', 'a+')
	for i in range(0, len(doc)):
		if (doc[i].isalpha()):
			localFile.write(doc[i])
		else:
			localFile.write(' ')

	i += 1
	print("Text File processed")
	print(i)
	localFile.close()
		
