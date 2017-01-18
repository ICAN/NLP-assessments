#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Dec  6 18:02:19 2016

@author: neal
"""

import os
import sys

import nltk
from nltk.tree import ParentedTree
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet


def lemma_process(file_in):
	_lemmas = []
	_sents = nltk.sent_tokenize(file_in)
	wordnet_lemmatizer = WordNetLemmatizer()
	for sent in _sents:
		_pos = nltk.pos_tag(nltk.word_tokenize(sent))
		for pos in _pos:
			tag = get_wordnet_pos(pos[1])
			lem = ''
			if tag != '':
				lem = '{}/{}/{}'.format(pos[0].encode("ascii", "ignore"),
						pos[1],
						wordnet_lemmatizer.lemmatize(pos[0], tag).encode("ascii", "ignore"))
			else:
				lem = '{}/{}/{}'.format(pos[0].encode("ascii", "ignore"),
						pos[1],
						wordnet_lemmatizer.lemmatize(pos[0]).encode("ascii", "ignore"))
			_lemmas.append(lem)
	return _lemmas


if __name__ == '__main__':  
    doc = []
    sents = []
    pos_tagged = []
    lemmas = []
    ner_tagged = []
   


    in_file_name = 'test.txt'
    out_file_name = 'nltk-out.tsv'
            
    in_file = open(in_file_name, "r", encoding="utf-8")
    out_file = open(out_file_name, "w", encoding ="utf-8")   
    
    
    
    #Splitting "sentence tokenization"
    #use sent_tokenize() on entire text to get array of sentences
        


    for sent in sents:
        print sent.encode("ascii", "ignore")
    
        
    #Tokenization "word tokenization"
    #use word_tokenize() on sentences to get array of words
    
    for 
    
        
    
    #POS-tagging
    #use pos_tag() on words
    


     #NE Recognition
     #Use ne_chunk() on pos-tagged sentences
     
    _sents = nltk.sent_tokenize(uni_str)
    _tokens = [nltk.word_tokenize(sent) for sent in _sents]
    _pos = [nltk.pos_tag(sent) for sent in _tokens]
    _chunked = nltk.ne_chunk_sents(_pos, binary=False)
    ner_process(_chunked)
    sys.stdout.write(" ".join(_ners))



    #Lemmatizing
    lemmas = lemma_process(uni_str)
    #for lemma in lemmas:
        #print lemma
        #sys.stdout.write(lemma)
    sys.stdout.write(" ".join(lemmas))
    
      
   

  