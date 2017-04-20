#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Nov 13 16:49:23 2016
@author: neal

NOTE: Works in Python 3.5.2; does not appear to work in Python 2.7 due to a string type issue


"""

import spacy;
from spacy.en import English;
import sys;
import utility
import re

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


# Yields false if outer and inner spans are the same
def span_contains(outer, inner):
    if inner.start < outer.start or inner.end > outer.end:
        yield False
    elif inner.start == outer.start and inner.end == outer.end:
        yield False
    else:
        yield True


def sent_process(file_in):
    # _doc = nlp(unicode(file_in, encoding="utf-8"))
    doc = nlp(file_in)
    sents = doc.sents
    return sents


# Something about customizable pipelines
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
        sent_ends.add(sent.end)

    internal_token_number = 0
    tokenInText = 1  # 1-indexed
    tokenInSentence = 1  # 1-indexed
    sentenceNumber = 1  # 1-indexed

    # TSV column headers
    out_file.write(
        "indexInText"
        + "\tindexInSentence"
        + "\tsentenceNumber"
        + "\ttoken"
        + "\tposTag"
        + "\tlemma"
        + "\tneTag"
        + "\n\r"
    )

    for token in doc:

        # Sentence-splitting debugging
        #        is_start = False
        #        is_end = False
        #        starting_sentence_ending_tokens = "        "
        #        ending_sentence_starting_tokens = "        "
        #
        if internal_token_number in sent_starts:
            tokenInSentence = 1
            sentenceNumber += 1
        # is_start = True
        #            starting_sentence_ending_tokens = "e:"
        #
        #        if internal_token_number in sent_ends:
        #            is_end = True
        #            ending_sentence_starting_tokens = "s:"
        #
        #        for sent in doc.sents:
        #            sent_start = sent.start
        #            sent_end = sent.end
        #            if internal_token_number == sent_end:
        #                ending_sentence_starting_tokens += (str(sent.label) + " " + str(sent_start) + " ")
        #            if internal_token_number == sent_start:
        #                starting_sentence_ending_tokens += (str(sent.label) + " " + str(sent_end) + " ")


        if token.tag_ != 'SP':  # 'SP' tokens excluded
            #            print(
            out_file.write(
                str(tokenInText)
                #                          "\t" + str(internal_token_number)
                + "\t" + str(tokenInSentence)
                + "\t" + str(sentenceNumber)
                #                          + "\t" + starting_sentence_ending_tokens
                #                          + "\t" + ending_sentence_starting_tokens
                + "\t" + str(token)
                + "\t" + token.tag_
                + "\t" + token.lemma_
                + "\t" + token.ent_type_
                + "\n\r")
            tokenInText += 1  # Count only tokens that are included in output
            tokenInSentence += 1

        internal_token_number += 1  # Count all tokens regardless of inclusion


def run_annotator(in_file_name, file_read_method, out_file_name):

    input_string = file_read_method(in_file_name)
    out_file = open(out_file_name, "w")

    # Loads the nlp pipeline
    nlp = spacy.load('en')

    # Create Doc object from input file text, which should contain annotations
    # Or should this be nlp.make_doc(text)? Instructions unclear
    print(input_string)
    doc = nlp(input_string)

    # i = 1;
    # for token in doc:
    #    print(str(i) + " " + str(token))
    #    i += 1

    # not sure if these do anything
    nlp.tagger(doc)
    nlp.entity(doc)

    write_tsv(doc, out_file)


def run_annotator_stacked_sentences(in_file_name, out_file_name):

    input_lines = utility.read_raw_as_lines(in_file_name)
    annotations = utility.read_annotations(in_file_name)
    out_file = open(out_file_name, "w")

    # Loads the nlp pipeline
    nlp = spacy.load('en')

    # Create Doc object from input file text, which should contain annotations
    # Or should this be nlp.make_doc(text)? Instructions unclear

    processed_lines = []
    for i in range(len(input_lines)):
        processed_line = nlp(input_lines[i])
        nlp.tagger(processed_line)
        nlp.entity(processed_line)
        processed_lines.append(processed_line)

    print("Processed lines: " + str(len(processed_lines)))

    lemmas_only_sentences = []
    for i in range(len(processed_lines)):
        lemmas_only_sent = ""
        for token in processed_lines[i]:
            if(token.tag_ != "SP"):
                if(token.lemma_ is not None):
                    lemmas_only_sent += token.lemma_ + " "
                else:
                    lemmas_only_sent += "\t"
        lemmas_only_sentences.append(lemmas_only_sent)


    for i in range(len(input_lines)):
        # print(annotations[i])
        # print(lemmas_only_sentences[i])
        # print(input_lines[i])
        # print()
        out_file.write(annotations[i] + "\n")
        out_file.write(lemmas_only_sentences[i] + "\n")
        out_file.write(input_lines[i] + "\n")
        out_file.write("\n")


if __name__ == '__main__':
    # command line args; sys.argv[0] is the script name
    # in_file_name = sys.argv[1] # First cmd-line arg is input file
    # out_file_name = sys.argv[2] # Second cmd-line arg is output file
    texts = [
        ]

    # Run lemmas
    in_file_name = 'corpora/' + "lemmas" + ".txt"
    out_file_name = 'annotator_outputs/' + "lemmas" + "-spacy.tsv"
    run_annotator_stacked_sentences(in_file_name, out_file_name)


    # #Run splits
    # in_file_name = 'corpora/' + "splits" + ".txt"
    # out_file_name = 'annotator_outputs/' + "splits" + "-spacy.tsv"
    # run_annotator(in_file_name, utility.read_raw, out_file_name)



