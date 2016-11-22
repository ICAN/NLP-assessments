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

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Neal
 */
public class OpenNLP {

    public static void runAnnotator(String inputFileName) {

        
        
        
        
    }
    
    
    
    //PUBLIC METHODS
    //Works as of V5
    public static void standardizePOS(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawPOS(raw);
        simplifyPOSTags(tokens);
        correctSpecialTokens(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Finish components
    public static void standardizeNER(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawNER(raw);
        simplifyNETags(tokens);
        correctSpecialTokens(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //Looks good
    public static void standardizeSplits(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawSplits(raw);
        correctSpecialTokens(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Write this
    public static void standardizeLemmas(String inputFile, String outputFile) {

    }

    //GENERAL INPUT STUFF
    private static void correctSpecialTokens(ArrayList<Token> tokens) {

        HashMap<String, String> mapping = new HashMap<>();

        mapping.put("-LRB-", "(");
        mapping.put("-RRB-", ")");
        mapping.put("-LSB-", "[");
        mapping.put("-RSB-", "]");

        int replacedTokens = 0;
        for (Token token : tokens) {
            if (mapping.containsKey(token.token)) {
                token.token = mapping.get(token.token);
                replacedTokens++;
            }
        }
    }

    //PARTS OF SPEECH TAGGER - POS
    private static ArrayList<Token> tokenizeRawPOS(ArrayList<String> lines) {

        ArrayList<Token> taggedTokens = new ArrayList<>();
        int tokenCount = 0;
        for (String line : lines) {
            //Tokens and tags are separated by an underscore
            String[] split = line.split("_+");

            if (split.length == 2) {
                tokenCount++;
                Token token = new Token(split[0].trim());
                token.indexInText = tokenCount;
                taggedTokens.add(token);
            } else {
//                System.out.println("Failed to tokenize '" + line + "'\nTokens: " + split.length + "\n");
            }

        }
        return taggedTokens;
    }

    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("pos", simplifyPOSTag(token.tags.get("pos")));
        }
    }

    private static String simplifyPOSTag(String posTag) {

        if (posTag.matches("NN.*")
                || posTag.equals("PRP")
                || posTag.equals("WP")) {
            return "NN";
        } else if (posTag.matches("JJ.*")
                || posTag.equals("WP$")
                || posTag.equals("PRP$")) {
            return "JJ";
        } else if (posTag.matches("V.*")
                || posTag.equals("MD")) {
            return "VB";
        } else if (posTag.matches("RB.*")
                || posTag.equals("WRB")) {
            return "RB";
        } else {
            return "Other";
        }
    }

    //NAMED-ENTITY RECOGNITION - NER
    //TODO: Finish
    private static ArrayList<Token> tokenizeRawNER(ArrayList<String> lines) {

        ArrayList<Token> taggedTokens = new ArrayList<>();
        int tokenCount = 0;
        for (String line : lines) {
            //Tokens and tags are separated by an underscore
            String[] split = line.split("_+");

            if (split.length == 2) {
                tokenCount++;
                //TODO: Check following
                Token token = new Token(split[0].trim());
                token.tags.put("ne", split[1].trim());
            } else {
            }

        }
        return taggedTokens;
    }

    private static void simplifyNETags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("ne", simplifyPOSTag(token.tags.get("ne")));
        }
    }

    //TODO: Write
    private static String simplifyNETag(String tag) {

        if (tag.matches("")) {
            return "None";
        } else {
            return "NE";
        }

    }

    //SENTENCE-SPLITTING
//Tokenizes by character, excluding all whitespace, numbering the characters
    //in each sentence
    private static ArrayList<Token> tokenizeRawSplits(ArrayList<String> lines) {
        ArrayList<Token> output = new ArrayList<>();

        int tokenCount = 1;
        for (String line : lines) {
            String[] split = line.split("\\s+");
            String combined = "";

            for (int i = 0; i < split.length; i++) {
                combined += split[i];
            }

            for (int i = 0; i < combined.length(); i++) {
                Token token = new Token("" + combined.charAt(i));
                token.indexInText = tokenCount;
                token.indexInSentence = i + 1;
                token.tags.put("split", "_");
                tokenCount++;

            }
        }
        return output;
    }

}
