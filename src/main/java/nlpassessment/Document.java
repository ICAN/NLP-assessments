/*
 * The MIT License
 *
 * Copyright 2016 Neal Logan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nlpassessment;

import edu.stanford.nlp.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author neal
 */
public class Document {

    //Use primarily token-in-sentence field in Token objects to determine sentence breaks
    //TODO: include "whitespace" tokens so that extra spaces,tabs, etc. can be preserved?
    public String name = "";
    public ArrayList<Token> tokens = new ArrayList<>();
    
    
    //////////////////////////CONSTRUCTORS////////////////////////////////
    public Document(){}
    
    public Document(String name) {
        this.name = name;
    }
    
    public Document(ArrayList<Token> tokens, String name) {
        this.tokens = tokens;
        this.name = name;
    }
    
    
    //////////////////////////////ACCESSORS/MUTATORS///////////////////////////
    public Token get(int index) {
        return this.tokens.get(index);
    }
    
    public int getPositionOf(Token token) {
        for (int i = 0; i < tokens.size(); i++) {
            if (token == tokens.get(i)) {
                return i;
            }
        }
        //If not found:
        return -1;
    }
    
    public Document deepClone() {
        Document document = new Document(this.name);
        for (Token token : tokens) {
            document.tokens.add(token.deepClone());
        }
        return document;
    }
  
    
    public void tagSentenceSplits() {
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (tokens.get(i).indexInSentence >= tokens.get(i + 1).indexInSentence) {
                tokens.get(i).set(Tag.SPLITTING, Tag.SS_END_OF_SENT);
            } else {
                tokens.get(i).set(Tag.SPLITTING, Tag.SS_NONSPLIT);
            }
        }
        //Tag final char
        tokens.get(tokens.size() - 1).set(Tag.SPLITTING, Tag.SS_END_OF_SENT);
    }
    
    //For each token, removes all instances of the specified pattern from the specified tag
    public void replaceAll(String replacedPattern, String replacementPattern, String tag) {
        for (Token token : tokens) {
            token.set(tag, token.get(tag).replaceAll(replacedPattern, replacementPattern));
        }
    }

    //Doesn't affect annotations, just the tokens themselves
    public void removeNonwordChars() {
        this.replaceAll("\\W+", "", Tag.TOKEN);
    }

    public void removeEmptyTokens() {
        ArrayList<Token> replacement = new ArrayList<>();
        for (Token token : this.tokens) {
            if (!token.get(Tag.TOKEN).isEmpty()) {
                replacement.add(token);
            }
        }
        this.tokens = replacement;
    }
    
    /*
        Converts standard POS tags to an extremely simplified set
        
     */
    //TODO: Replace with explicit mapping from each Penn POS tag to 
    //its corresponding simplified tag
    public void PennToSimplifiedPOSTags() {
        for (Token token : tokens) {
            String tag = token.get(Tag.POS);
            String replacementTag;

            if (tag.matches("NN.*")
                    || tag.equals("PRP")
                    || tag.equals("WP")) {
                replacementTag = Tag.POS_NOUN;
            } else if (tag.matches("JJ.*")
                    || tag.equals("WP$")
                    || tag.equals("PRP$")) {
                replacementTag = Tag.POS_ADJ;
            } else if (tag.matches("V.*")
                    || tag.equals("MD")) {
                replacementTag = Tag.POS_VERB;
            } else if (tag.matches("RB.*")
                    || tag.equals("WRB")) {
                replacementTag = Tag.POS_ADV;
            } else {
                replacementTag = Tag.POS_OTHER;
            }

            token.set(Tag.POS, replacementTag);
        }

    }
    
    //Adds any tags in "other" document to the tokens in this document
    //Overwrites any existing tags in this document
    //"other" is not modified
    //TODO: make flexible
//    public void integrateDocumentProperties(Document other) {
//        //Validate first
//        if (this.tokens.size() != other.tokens.size()) {
//            System.out.println("Merging failed. Document sizes differ.");
//            return;
//        }
//        for (int i = 0; i < this.tokens.size(); i++) {
//            if (!this.get(i).get("token").equals(other.get(i).get("token"))) {
//                System.out.println("Merging failed. Token mismatch at index = " + i);
//                return;
//            }
//        }
//
//        //Actual merge
//        for (int i = 0; i < this.tokens.size(); i++) {
//            for (String tag : other.get(i).getTagset()) {
//                this.get(i).set(tag, other.get(i).get(tag));
//            }
//        }
//    }

    /*
    baseSearchRange determines how far ahead the method will start looking for matching 
    tokens before escalating. Will escalate to 3*baseSearchRange before giving up
    
    stringMatchThreshold determines which strings will be considered matches
    If LevensteinDistance/(sum of string lengths) is less than this value,
    they will be condsidered a match. A value <=0 requires exact match.
    
    WARNING: This method is not perfectly symmetrical. If each document in a set
    is used to restrict the others, the order in which the restrictions are applied
    could change the results. 
    
    Also, if used on a set of document, it is not guaranteed to produce documents
    of the same length in a single pass. However, they should be close to the same
    length with a single pass, and very few passes should be required to 
    reach stability.
     */
    public void restrictToTokensInOtherDocs(ArrayList<Document> otherDocs, int baseSearchRange, double matchThreshold) {
        
        System.out.println("\n\nInitial " + this.name + " doc length: " + this.tokens.size());
        if(otherDocs.contains(this)) {
            System.out.println("Removed this document (" + this.name + ") from restricters");
            otherDocs.remove(this);
        }  
        System.out.print("Input lengths: ");
        for (Document document : otherDocs) {
            System.out.print(document.name + ": " + document.tokens.size() + ", ");
        }
        System.out.println();
        
        
        Document original = this.deepClone();
        int searchRange = baseSearchRange;
        //Iterate through other documents, restricting results to tokens for which
        //matches are found in the other lists
        for (Document other : otherDocs) {
            
            int thisBaseIter = 0, otherBaseIter = 0;
            //Start with arbitrary base token list
            Document intermediateResult = new Document();
            int discarded = 0;
            while (thisBaseIter < this.tokens.size() 
                    && otherBaseIter < other.tokens.size()) {
                
                
                String thisToken = this.tokens.get(thisBaseIter).get(Tag.TOKEN);
                String otherToken = other.tokens.get(otherBaseIter).get(Tag.TOKEN);

                if (Utility.almostEquals(thisToken, otherToken, matchThreshold)) {
                    //Matched! Add token!
                    intermediateResult.tokens.add(original.tokens.get(thisBaseIter));

                    //Continue forward!
                    thisBaseIter++;
                    otherBaseIter++;
                    
                } else { //Token mismatch: search!
                    boolean match = false;
                    int thisOffset = 0;
                    while(!match 
                            && thisOffset <= searchRange
                            && thisBaseIter + thisOffset < original.tokens.size()){
                        int otherOffset = Math.max(otherBaseIter + thisOffset * -1 + 1, otherBaseIter) - otherBaseIter;
                        while(!match 
                                && otherOffset <= searchRange
                                && otherBaseIter + otherOffset < other.tokens.size()) {
                            //If a match is found at this offset...                                                       
                            if(Utility.almostEquals(
                                    original.tokens.get(thisBaseIter+thisOffset).get(Tag.TOKEN),
                                    other.tokens.get(otherBaseIter+otherOffset).get(Tag.TOKEN),
                                    matchThreshold)) {
                                //Move base iterators to this position and continue loop
                                thisBaseIter += thisOffset;
                                otherBaseIter += otherOffset;
                                match = true;
                            }        
                            otherOffset++;
                        }
                        thisOffset++;
                    }
                    //Escalate search range to double the base search range if 
                    if (!match && searchRange <= 3*baseSearchRange) {
                        searchRange++;                        
                    } else if(!match && searchRange > 3*baseSearchRange) {
                        System.out.println("Error: match not found in range! Discarded " + 
                                original.tokens.get(thisBaseIter).get(Tag.TOKEN) + " and "
                                +other.tokens.get(otherBaseIter).get(Tag.TOKEN) + " at "
                                + "this: " + thisBaseIter + " and " + otherBaseIter);
                        discarded++;
                        thisBaseIter++;
                        otherBaseIter++;
                    } else if (match) {
                        //Match, so reset searchRange
                        searchRange = baseSearchRange;
                    }
                }
            }
            original.tokens = intermediateResult.tokens;
            System.out.println("Discarded " + discarded + " pairs of tokens due to matching failure.");
        }
        this.tokens = original.tokens;
        System.out.print("\nOutput length: " + this.tokens.size());
    }
    
    
    ////////////////////////DOCUMENT STATISTICS/////////////////////////////////
    
    public int getTagCounts(String tagType, String tagValueRegex) {

        int count = 0;
        for (Token token : tokens) {
            if (token.get(tagType).matches(tagValueRegex)) {
                count++;
            }
        }
        return count;
    }

    //TODO: Check, document
    public HashMap<String, Integer> getTagCounts(String field) {

        HashMap<String, Integer> tagCounts = new HashMap<>();

        for (Token token : tokens) {
            if (tagCounts.keySet().contains(token.get(field))) {
                tagCounts.put(token.get(field), tagCounts.get(token.get(field)) + 1);
            } else {
                tagCounts.put(token.get(field), 1);
            }
        }
        return tagCounts;
    }
    
    public static boolean sameLength(ArrayList<Document> docs) {
        int length = docs.get(0).tokens.size();
        for(Document doc : docs) {
            if(doc.tokens.size() != length) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean tokensMatchExactly(ArrayList<Document> docs) {
        if(!sameLength(docs)) {
            return false;
        }
        
        for(int i = 0; i < docs.get(0).tokens.size(); i++) {
            String token = docs.get(0).get(i).get(Tag.TOKEN);
            for(Document doc : docs) {
                if (!doc.get(i).get(Tag.TOKEN).equals(token)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean tokensMatchAlmost(ArrayList<Document> docs, double threshold) {
        if(!sameLength(docs)) {
            return false;
        }
        
        for(int i = 0; i < docs.get(0).tokens.size(); i++) {
            String token = docs.get(0).get(i).get(Tag.TOKEN);
            for(Document doc : docs) {
                if (!Utility.almostEquals(doc.get(i).get(Tag.TOKEN), token, threshold)) {
                    return false;
                }
            }
        }
        return true;
    }
    
   ///////////////////////////////CONVERSIONS//////////////////////////////////
   public ArrayList<ArrayList<Token>> toSentences() {
        ArrayList<ArrayList<Token>> sentences = new ArrayList<>();
        int sentenceNumber = -1;
        ArrayList<Token> currentSentence = null;
        for (Token token : tokens) {
            if (token.sentenceNumber > sentenceNumber) {
                if (sentences != null) {
                    sentences.add(currentSentence);
                }
                sentenceNumber = token.sentenceNumber;
                currentSentence = new ArrayList<>();
            }
            currentSentence.add(token);
        }
        return sentences;
    }

    //Returns the entire document as a TSV, one token per line,
    //including only the specified fields, in the specified order (left to right)
    public ArrayList<String> toTSV(String[] fields) {
        ArrayList<String> tsv = new ArrayList<>();
        String header = "indexInDoc\t";
        for (String field : fields) {
            header += field + "\t";
        }
        tsv.add(header.trim());
        for (int i = 0; i < tokens.size(); i++) {
            tsv.add((i+1) + "\t" + tokens.get(i).getAsTSV(fields));
        }
        return tsv;
    }

    
    
    /////////////////////////////DISPLAY/DEBUG///////////////////////////////
    //Print entire document, specific fields only
    public void print(String[] fields) {
        for (Token token : tokens) {
            for (String field : fields) {
                System.out.println(token.get(field) + "\t");
            }
        }
    }

    //Print entire document
    public void printText() {
        int sentence = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (this.get(i).sentenceNumber > sentence) {
                System.out.print("\n");
            }
            System.out.print(this.get(i).get("token") + " ");
        }
    }
    






    
    

    


}
