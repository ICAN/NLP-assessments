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
 * @author Neal
 */
public class MBSP {

    //PUBLIC METHODS

    public static void standardizePOS(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawPOS(raw);
        simplifyPOSTags(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //FUNCTION NOT SUPPORTED
    //TODO: Double-check
    public static void standardizeNER(String inputFile, String outputFile) {

    }

    //TODO: Write this
    public static void standardizeSplits(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawSplits(raw);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Write this
    public static void standardizeLemmas(String inputFile, String outputFile) {

    }

    //PRIVATE METHODS
    //POS-TAGGING
    private static ArrayList<Token> tokenizeRawPOS(ArrayList<String> lines) {

        ArrayList<Token> taggedTokens = new ArrayList<Token>();

        //Tokenize
        int textTokenCount = 0;
        for (String line : lines) {
            String[] split = line.split("\\s+");

            if (split.length == 2) {

                //Validate line and add
                if ((split[0] + " " + split[1]).matches(".+\\s+[A-Z\\p{Punct}]+.*")) {
                    textTokenCount++;
                    Token token = new Token(split[0]);
                    token.indexInText = textTokenCount;
                    token.tags.put("pos", split[1]);
                    taggedTokens.add(token);
                } else {
                    System.out.println("Failed to validate line " + " " + split[0] + " " + split[1]);
                }

            } else {
                System.out.println("Failed to convert " + line);
            }
        }
        return taggedTokens;
    }

    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("pos", simplifyPOSTag(token.tags.get("pos")));
        }
    }

    private static String simplifyPOSTag(String tag) {

        if (tag.matches("NN.*")
                || tag.equals("PRP")
                || tag.equals("WP")) {
            return "NN";
        } else if (tag.matches("JJ.*")
                || tag.equals("WP$")
                || tag.equals("PRP$")) {
            return "JJ";
        } else if (tag.matches("V.*")
                || tag.equals("MD")) {
            return "VB";
        } else if (tag.matches("RB.*")
                || tag.equals("WRB")) {
            return "RB";
        } else {
            return "Other";
        }
    }

    
    //SENTENCE SPLITTING
    //Tokenizes by character, excluding all whitespace, numbering the characters
    //in each sentence
    //TODO: Write this
    private static ArrayList<Token> tokenizeRawSplits(ArrayList<String> lines) {
        ArrayList<Token> output = new ArrayList<>();

        int tokenCount = 1;
        for (String line : lines) {
            String[] split = line.split("\\s+");
            String combined = "";
            
            for (int i = 0; i < split.length; i++) {
                combined+=split[i];
            }
            
            for(int i = 0; i < combined.length(); i++) {
             
                
                    tokenCount++;
            
            }    
        }
        return output;
    }

}
