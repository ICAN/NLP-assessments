#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Nov 13 16:49:23 2016

@author: neal
"""

import spacy;
#from spacy.en import English;
import sys;
import os;

#command line args; sys.argv[0] is the script name
annotator = sys.argv[1] # First command-line argument is which annotator to use
inFileName = sys.argv[2] # Second is input file
outFileName = sys.argv[3] # Third is output file



#Apparently this is possible
def custom_pipeline(nlp):
        return (nlp.tagger, nlp.entity)

        
#Create instance of spacy.language.Language object for English-language nlp
#using our custom pipeline
nlp = spacy.load('en', create_pipeline=custom_pipeline)
        
#Create Doc object from input file text, which should contain annotations
#Or should this be nlp.make_doc(text)? Instructions unclear
doc = nlp(open(inFileName).read())

#Delete any old output file
os.remove(outFileName)

#set up pipeline, specifying which annotators to use
nlp.pipeline = [nlp.tagger, nlp.entity]

for proc in nlp.pipeline:




#Open new output file in 'a'ppend mode
out = open(outFileName, 'a')

    


