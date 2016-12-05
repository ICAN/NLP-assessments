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

import java.util.ArrayList;

/**
 *
 * @author neal
 */
public class Document {
    
    //Use primarily token-in-sentence field to determine sentence breaks
    //TODO: include "whitespace" tokens so that extra spaces,tabs, etc. can be preserved?
    //Tags go on tokens
    public ArrayList<Token> tokenList;
    
    
    public void print(String[] fields) {
        for(Token token : tokenList) {
            for(String field : fields) {
                System.out.print(token.get(field) + "\t");
            }
        }
    }
    
            
    public void printText(){
        int sentence = -1;
        for(int i = 0; i < tokenList.size(); i++) {
            if(this.get(i).sentenceNumber > sentence) {
                System.out.print("\n");
            }
            System.out.print(this.get(i).get("token") + " ");
        }
    }
        
    public Document() {
        tokenList = new ArrayList<>();
    }
        
    public int getTagCounts(String tagType, String tagValueRegex) {
        
        int count = 0;
        for (Token token : tokenList) {
            if(token.get(tagType).matches(tagValueRegex)) {
                count++;
            }
        }
        return count;
    }
    
    //Adds any tags in "other" to the tokens in this document
    //Overwrites any existing tags in this document
    //"other" is not modified
    public void mergeDocumentProperties(Document other) {
        //Validate first
        if(this.tokenList.size() != other.tokenList.size()) {
            System.out.println("Merging failed. Unequal sizes.");
            return;
        }
        
        for(int i = 0; i < this.tokenList.size(); i++) {
            if(!this.get(i).get("token").equals(other.get(i).get("token"))) {
                System.out.println("Merging failed. Token mismatch at index = " + i);
                return;
            }
        }
        
        for(int i = 0; i < this.tokenList.size(); i++) {
            for(String tag : other.get(i).getTagset()) {
                this.get(i).set(tag, other.get(i).get(tag));
            }
        }
    }
    
    public Token get(int index) {
        return this.tokenList.get(index);
    }
    
    public Document deepClone() {
        Document document = new Document();
        for(Token token : tokenList) {
            document.tokenList.add(token.deepClone());
        }
        return document;
    }
    
    
    public ArrayList<ArrayList<Token>> toSentences() {
        ArrayList<ArrayList<Token>> sentences = new ArrayList<>();
        int sentenceNumber = -1;
        ArrayList<Token> currentSentence = null;
        for(Token token : tokenList) {
            if(token.sentenceNumber > sentenceNumber) {
                if(sentences != null) {
                    sentences.add(currentSentence);
                }
                sentenceNumber = token.sentenceNumber;
                currentSentence = new ArrayList<>();
            }
            currentSentence.add(token);
        }
        return sentences;
    }
    

    public int getPositionOf(Token token) {
        for(int i = 0; i < tokenList.size(); i++) {
            if(token == tokenList.get(i)) {
                return i;
            }
        }
        //If not found:
        return -1;
    }
    
    public void tagSentenceSplits() {
        for(int i = 1; i < tokenList.size(); i++) {
            if(tokenList.get(i).indexInSentence <= tokenList.get(i-1).indexInSentence) {
                tokenList.get(i).set("split", "1");
            }
        }
        //Tag final char
        tokenList.get(tokenList.size()-1).set("split", "1");
    }
            
            
    
    
    
    
}
