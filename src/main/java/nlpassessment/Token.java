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
    public int startingChar = -1; //inclusive, 1-indexed
    public int endingChar = -1; //exclusive, 1-indexed //TODO: Check
    public HashMap<String, String> properties = new HashMap<>();

    public Token() {
        properties.put(Tag.TOKEN, "");
    }

    public Token(String token) {
        properties.put(Tag.TOKEN, token);
    }
    
    //Both keys and values are case sensitive
    public void set(String key, String value) {
        if (isCoreNumericField(key)) {
            System.out.println("ERROR: " + key + " is a core numeric field");
            return;
        } else {
            properties.put(key, value);
        }
    }

    public void set(String key, int value) {
        
        if (!isCoreNumericField(key)) {
            System.out.print("Error: attempting to set non-existent numeric field");
        } else if (key.equalsIgnoreCase(Tag.INDEX_IN_TEXT)) {
            indexInText = value;
        } else if (key.equalsIgnoreCase(Tag.INDEX_IN_SENT)) {
            indexInSentence = value;
        } else if (key.equalsIgnoreCase(Tag.SENT_NUMBER)) {
            sentenceNumber = value;
        } else if (key.equalsIgnoreCase(Tag.START_CHAR)) {
            startingChar = value;
        } else if (key.equalsIgnoreCase(Tag.END_CHAR)) {
            endingChar = value;
        } else {
            System.out.print("Error: attempting to set non-existent numeric field");
        }
    }

    //Case sensitive
    //Returns the empty string if property is not set
    //Returns core numeric fields as strings
    public String get(String key) {
        if (!isCoreNumericField(key)) {
            return properties.getOrDefault(key, "");
        } else if (key.equalsIgnoreCase("indexInText")) {
            return "" + indexInText;
        } else if (key.equalsIgnoreCase("indexInSentence")) {
            return "" + indexInSentence;
        } else if (key.equalsIgnoreCase("sentenceNumber")) {
            return "" + sentenceNumber;
        } else if (key.equalsIgnoreCase("startingChar")) {
            return "" + startingChar;
        } else if (key.equalsIgnoreCase("endingChar")) {
            return "" + endingChar;
        } else {
            System.out.println("ERROR: Property " + key + " not found in token.");
            return "";
        }
    }

    //Returns this token as a single TSV line with the specified fields
    public String getAsTSV(String[] fields) {
        String tsv = "";
        for (String field : fields) {
            tsv += this.get(field) + "\t";
        }
        return tsv.trim();
    }

    private boolean isCoreNumericField(String key) {
        if (key.equalsIgnoreCase("indexInText")
                || key.equalsIgnoreCase("indexInSentence")
                || key.equalsIgnoreCase("sentenceNumber")
                || key.equalsIgnoreCase("startingChar")
                || key.equalsIgnoreCase("endingChar")) {
            return true;
        } else {
            return false;
        }
    }

    public Set<String> getTagset() {
        return properties.keySet();
    }

    public Token deepClone() {
        Token clone = new Token(this.get("token"));
        clone.indexInText = this.indexInText;
        clone.indexInSentence = this.indexInSentence;
        clone.sentenceNumber = this.sentenceNumber;
        clone.startingChar = this.startingChar;
        clone.endingChar = this.endingChar;
        for (String tag : this.getTagset()) {
            clone.set(tag, this.get(tag));
        }
        return clone;
    }

    //TODO: fix
    //Columns are: wordIndex (in entire text),wordIndex(in sentence), token, lemma, POS, NER
    public String toString() {
        return indexInText + "\t " + indexInSentence + "\t" + properties.get("token");
    }

}
