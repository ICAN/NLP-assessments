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
    
    
    public Document() {
        tokenList = new ArrayList<>();
    }
    
    //From word-tokens to character-tokens
    public Document toCharacterTokenDocument() {
        
        
        //TODO: write
        
        return this;
    }
        
    //Convert from character-tokens to word-tokens
    public Document toWordTokenDocument() {
        
        //TODO: write
        
        return this;
    }
    
    
    //Use token-in-sentence field to determine sentence breaks
    public ArrayList<String> toLines() {
        ArrayList<String> lines = new ArrayList<>();
        
        //TODO: write
        
        
        return lines;
    }

    
    
    
    
}
