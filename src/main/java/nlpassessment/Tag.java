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

public class Tag {

    //General
    public static final String TAG_SEP = ","; //Intertag separator

    //Properties
    public static final String INDEX_IN_TEXT = "indexInText";
    public static final String INDEX_IN_SENT = "indexInSentence";
    public static final String SENT_NUMBER = "sentenceNumber";
    public static final String START_CHAR = "startingChar";
    public static final String END_CHAR = "endingChar"; 
    public static final String POS = "posTag";
    public static final String TOKEN = "token";
    public static final String NE = "nerTag";
    public static final String LEMMA = "lemma";
    public static final String SPLIT = "ssplit";

    //Standard POS tags (small set)
    public static final String POS_NOUN = "NN";
    public static final String POS_VERB = "VB";
    public static final String POS_ADV = "RB";
    public static final String POS_ADJ = "JJ";
    public static final String POS_OTHER = "OO";

    //Standard NE tags
    public static final String NE_PERSON = "PERS";
    public static final String NE_ORG = "ORG";
    public static final String NE_LOCATION = "LOC";
    public static final String NE_TIME = "TIME";
    public static final String NE_DATE = "DATE";
    public static final String NE_MONEY = "MONEY";
    public static final String NE_PERCENT = "PCT";
    public static final String NE_ORDINAL = "ORD#";
    public static final String NE_CARDINAL = "CARD#";
    public static final String NE_GPE = "GPE";
    
  
    
}
