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

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Neal Logan
 *
 */
public class Token {    
    
    //Properties
    public int indexInText = -1; //1-indexed
    public int indexInSentence = -1; //1-indexed
    public int sentenceNumber = -1; //1-indexed
    public int startingChar = -1; //inclusive
    public int endingChar = -1; //exclusive //TODO: Check
    public HashMap<String, String> props = new HashMap<>();
    
    public Token() {}
    
    public Token(String token) {
        props.put("token", token);
    }
    
    //Both keys and values are case sensitive
    public void set(String key, String value) {
        props.put(key, value);
    }

    //Case sensitive
    //Returns the empty string if property is not set
    public String get(String key) {
        return props.getOrDefault(key, "");
    }
    
    public Set<String> getTagset() {
        return props.keySet();
    }
    
    
    public Token deepClone() {
        Token clone = new Token(this.get("token"));
        clone.indexInText = this.indexInText;
        clone.indexInSentence = this.indexInSentence;
        clone.sentenceNumber = this.sentenceNumber;
        clone.startingChar = this.startingChar;
        clone.endingChar = this.endingChar;
        for(String tag : this.getTagset()) {
            clone.set(tag, this.get(tag));
        }
        return clone;
    }
    
    //TODO: fix
    //Columns are: wordIndex (in entire text),wordIndex(in sentence), token, lemma, POS, NER
    public String toString() {
        return indexInText + "\t " + indexInSentence + "\t" + props.get("token");
    }

}
