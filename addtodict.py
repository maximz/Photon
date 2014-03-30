import os
import time
import re

dict = open('dict.txt', 'r').read().split('\n');

basedir = './wikitext'

p = re.compile('[\s\W]')

localFile = open('dict.txt', 'a+')

for filename in os.listdir(basedir):
    print("Processing: " + filename)
    text = p.split(open(basedir + '/' + filename, 'r').read())
    for each in text:
        if each not in dict:
            print("Added: " + each)
            localFile.write(each)
            localFile.write('\n')

localFile.close()
