#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Nov 13 16:49:23 2016

@author: neal
"""

import spacy;
from spacy.en import English;
import sys;

def value_in_span(value, span):
    if value >= span.start and value < span.end:
        yield True
    else:
        yield False
        

def spans_overlap(a, b):
    if value_in_span(a.start, b) or value_in_span(a.end, b):
        yield True
    else:
        yield False

#Yields false if outer and inner spans are the same
def span_contains(outer, inner):
    if inner.start < outer.start or inner.end > outer.end:
        yield False
    elif inner.start == outer.start and inner.end == outer.end:
        yield False
    else:
        yield True        
    
def sent_process(file_in):
	#_doc = nlp(unicode(file_in, encoding="utf-8"))
	doc = nlp(file_in)
	sents = doc.sents
	return sents
    
#Something about customizable pipelines
def custom_pipeline(nlp):
    return (nlp.tagger, nlp.entity)

#
def print_file(file):
    for line in file:
        print(line)
    

def write_tsv(doc, out_file):
    
    sents = doc.sents
    sent_starts = set()
    sent_ends = set()
    for sent in sents:
        sent_starts.add(sent.start)



        
    internal_token_number = 0
    tokenInText = 1 #1-indexed
    tokenInSentence = 1 #1-indexed
    sentenceNumber = 1 #1-indexed
    
    #TSV column headers
    out_file.write(
                  "tokenInText"
                  + "\ttokenInSentence"
                  + "\tsentenceNumber"           
                  + "\ttoken"                  
                  + "\tposTag"
                  + "\tlemma"
                  + "\tneTag"
                  + "\r"
                  )
        
    for token in doc:

        if internal_token_number in sent_starts:
            tokenInSentence = 1
            sentenceNumber += 1                    
        if token.tag_ != 'SP': #'SP' tokens excluded
            out_file.write(
                          str(tokenInText)
                          + "\t" + str(tokenInSentence)
                          + "\t" + str(sentenceNumber)    
                          + "\t" + str(token)      
                          + "\t" + token.tag_ 
                          + "\t" + token.lemma_
                          + "\t" + token.ent_type_
                          + "\r")
            tokenInText += 1 #Count only tokens that are included in output
            tokenInSentence += 1
            
        internal_token_number += 1 #Count all tokens regardless of inclusion

            
if __name__ == '__main__':           
      
    #command line args; sys.argv[0] is the script name
    #in_file_name = sys.argv[1] # First cmd-line arg is input file
    #out_file_name = sys.argv[2] # Second cmd-line arg is output file
      
    in_file_name = 'test.txt'
    out_file_name = 'spacyOut.tsv'
            
    in_file = open(in_file_name, "r", encoding="utf-8")
    out_file = open(out_file_name, "w", encoding ="utf-8")   


    #Loads the nlp pipeline
    nlp = spacy.load('en')

    #Create Doc object from input file text, which should contain annotations
    #Or should this be nlp.make_doc(text)? Instructions unclear
    doc = nlp(open(in_file_name).read())

    #i = 1;
    #for token in doc:
    #    print(str(i) + " " + str(token))
    #    i += 1    

    #not sure if these do anything
    nlp.tagger(doc)
    nlp.entity(doc)


#    write_tsv(doc, out_file)

    
    sents = doc.sents
    filtered_sents = set()
    sents_checked = 0
    for checked_sent in sents:
        is_contained = False
        sents_checked += 1
        for possible_containing_sent in sents:
            if span_contains(possible_containing_sent, checked_sent):
                is_contained = True;

                
        if is_contained != True:
            filtered_sents.add(checked_sent)

     
            
    print("sents discarded: " + str(sents_checked - len(filtered_sents)))
            
            
    sent_starts = set()
    sent_ends = set()
    for sent in sents:
        sent_starts.add(sent.start)


    
#        
#    internal_token_number = 0
#    tokenInText = 1; #1-indexed
#    tokenInSentence = 1; #1-indexed
#    sentenceNumber = 1; #1-indexed
#    for token in doc:
#
#        if internal_token_number in sent_starts:
#
#            tokenInSentence = 1
#            sentenceNumber += 1    
#            
#        if token.tag_ != 'SP':
#            print(
##                          str(internal_token_number) + "\t" +
#                          str(tokenInText)
#                          + "\t" + str(tokenInSentence)
#                          + "\t" + str(sentenceNumber)
#
#                          + "\t" + str(token) 
# 
#                          + "\t" + token.tag_ 
#                          + "\t" + token.lemma_
#                          + "\t" + token.ent_type_
#                          + "\r")
#            tokenInText += 1 #Count only tokens that are included
#            tokenInSentence += 1
#        
#            
#        internal_token_number += 1
#
#
#    


