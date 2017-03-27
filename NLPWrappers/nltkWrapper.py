#WARNING:

import nltk
# nltk.download()
from nltk.tree import ParentedTree
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet
import re
import utility


#Compiled patterns for Penn pos tag simplification
NN = re.compile("N.*")
VB = re.compile("V.*")
JJ = re.compile("A.*")
RB = re.compile("R.*")

def simplify_pos(tag):
    if re.match(NN, tag):
        return wordnet.NOUN
    elif re.match(VB, tag):
        return wordnet.VERB
    elif re.match(JJ, tag):
        return wordnet.ADJ
    elif re.match(RB, tag):
        return wordnet.ADV
    else:
        return ''


def write_tsv(doc, out_file):

    # TSV column headers
    out_file.write(
        "indexInText"
        + "\tindexInSentence"
        + "\tsentenceNumber"
        + "\ttoken"
        + "\tposTag"
        # + "\tnerTag"
        + "\tlemma"
        + "\n\r"
    )

    indexInText = 1  # 1-indexed
    sentenceNumber = 1  # 1-indexed

    for sent in doc:
        tokenInSentence = 1  # 1-indexed

        for token in sent:
            string = str(indexInText) + "\t" + str(tokenInSentence) + "\t" + str(sentenceNumber)
            string += "\t" + token[0] #token
            string += "\t" + token[1] #pos-tag
            string += "\t" + token[2] #lemma
            string += "\n\r"
            # string = string.encode("ascii", "ignore")
            out_file.write(string)
            indexInText += 1  # Count only tokens that are included in output
            tokenInSentence += 1
        sentenceNumber += 1



def run_annotator(in_file_name, file_read_method, out_file_name):

    # in_file = open(in_file_name, "r")
    out_file = open(out_file_name, "w")

    # input_string = "Between us there was, as I have already said somewhere, the bond of the sea. Only the gloom to the west, brooding over the upper reaches, became more sombre every minute, as if angered by the approach of the sun."
    input_string = file_read_method(in_file_name)

    #Split sentences
    sentences = nltk.sent_tokenize(input_string)
    #Tokenize
    tokenized_sentences = []
    for sent in sentences:
        tokenized_sent = nltk.word_tokenize(sent)
        # print(sent)
        # print(tokenized_sent)
        tokenized_sentences.append(tokenized_sent)

    #POS-tag
    tagged_sentences = []
    for sent in tokenized_sentences:
        tagged_sent = nltk.pos_tag(sent)
        tagged_sentences.append(tagged_sent)
        # print(sent)
        # print(tagged_sent)

    #Lemmatize using NLTK's api for the WordNet lemmatizer
    lemmatized_sentences = []
    wordnet_lemmatizer = WordNetLemmatizer()
    for sent in tagged_sentences:
        new_sentence = []
        lemmatized_sentences.append(new_sentence)
        for token in sent:
            new_token = []
            new_sentence.append(new_token)
            new_token.append(token[0]) #word
            new_token.append(token[1]) #pos

            #Appent lemma to token
            if(simplify_pos(token[1]) != ""):
                new_token.append(wordnet_lemmatizer.lemmatize(token[0], simplify_pos(token[1])))
            else:
                new_token.append(wordnet_lemmatizer.lemmatize(token[0]))

    # for sent in lemmatized_sentences:
    #     for token in sent:
    #         print(token)





    print("writing tsv for " + in_file_name)
    write_tsv(lemmatized_sentences, out_file)


if __name__ == '__main__':
    # command line args; sys.argv[0] is the script name
    # in_file_name = sys.argv[1] # First cmd-line arg is input file
    # out_file_name = sys.argv[2] # Second cmd-line arg is output file

    # in_file_name = 'corpora/' + "small_test" + ".txt"
    # out_file_name = 'annotator_outputs/' + "small_test" + "-mbsp.tsv"
    # run_annotator(in_file_name, out_file_name)


    texts = [
        # "Academic1",
        # "Academic3",
        # "Academic10",
        # "GenFict1",
        # "GenFict5",
        # "GenFict6",
        # "NewsArticle1",
        # "NewsArticle3",
        # "NewsArticle10",
        "cleanSplits",
        # "mixed"
        ]


    for text in texts:
        in_file_name = 'corpora/' + text + ".txt"
        out_file_name = 'annotator_outputs/' + text + "-nltk.tsv"
        run_annotator(in_file_name, utility.read_raw, out_file_name)
