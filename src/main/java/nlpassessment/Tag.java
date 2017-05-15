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

    //Grading
    public static final String CORRECTNESS = "correct?";
    public static final String WAS_CORRECT = "YES";
    public static final String WAS_INCORRECT = "NO";

    //Properties
    public static final String INDEX_IN_TEXT = "indexInText";
    public static final String INDEX_IN_SENT = "indexInSentence";
    public static final String SENT_NUMBER = "sentenceNumber";
    public static final String START_CHAR = "startingChar";
    public static final String END_CHAR = "endingChar";
    public static final String POS = "posTag";
    public static final String APOS = "taggerPOS";
    public static final String GPOS = "goldPOS";
    public static final String TOKEN = "token";
    public static final String NE = "nerTag";
    public static final String LEMMA = "lemma";
    public static final String SPLITTING = "ssplit";

    //Simplified POS tags (small set)
    public static final String POS_NOUN = "NN";
    public static final String POS_VERB = "VB";
    public static final String POS_ADV = "RB";
    public static final String POS_ADJ = "JJ";
    public static final String POS_OTHER = "OO";

    
    
    //Penn POS tags
    //TODO: Finish adding
    public static final String PPOS_CO_CONJ = "CC";
    public static final String PPOS_CARD = "CD";
    public static final String PPOS_DET = "DT";
    public static final String PPOS_EX = "EX";
    public static final String PPOS_FOREIGN = "FW";
    public static final String PPOS_PREP = "IN";
    public static final String PPOS_ADJ = "JJ";
    public static final String PPOS_COMP_ADJ = "JJR";
    public static final String PPOS_SUPER_ADJ = "JJS";
    public static final String PPOS_ITEM = "LS";
    public static final String PPOS_MODAL = "MD";
    public static final String PPOS_NOUN_SING = "NN";
    public static final String PPOS_NN_PLURAL = "NNS";
    public static final String PPOS_NN_PROP_SING = "NNP";
    public static final String PPOS_NN_PROP_PLUR = "NNPS";
    public static final String PPOS_PREDET = "PDT";
    public static final String PPOS_POSS_END = "POS";
    public static final String PPOS_PERS_PRON = "PRP";
    public static final String PPOS_POSS_PRON = "PRP$";
    public static final String PPOS_ADV = "RB";
    public static final String PPOS_COMP_ADV = "RBR";
    public static final String PPOS_SUPER_ADV = "RBS";
    public static final String PPOS_PARTICLE = "RP";
    public static final String PPOS_SYMBOL = "SYM";
    public static final String PPOS_TO = "TO";
    public static final String PPOS_INTERJECT = "UH";
    public static final String PPOS_VB_BASE = "VB";
    public static final String PPOS_VB_PAST = "VBD";
    public static final String PPOS_VB_GER_PRESPAR = "VBG";
    public static final String PPOS_VB_PASPAR = "VBN";
    public static final String PPOS_VB_NOT3_SING_PRES = "VBP";
    public static final String PPOS_VB_3PER_SING_PRES = "VBZ";
    public static final String PPOS_WH_DET = "WDT";
    public static final String PPOS_WH_PRON = "WP";
    public static final String PPOS_WH_POSS_PRON = "WP$";
    public static final String PPOS_WH_ADV = "WRB";

    //TODO: Come up with actual standard? Is this possible?
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

    //Machine voting tags
    public static final String NO_CONSENSUS = "??";

    //Sentence splitting tags
    public static final String SS_END_OF_SENT = "SPLIT";
    public static final String SS_NONSPLIT = "--";
}
