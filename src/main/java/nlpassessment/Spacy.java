/*
 * The MIT License
 *
 * Copyright 2016 Neal.
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
public class Spacy {

    //Good
    public static void standardizePOS(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawPOS(raw);
        simplifyPOSTags(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //Looks good
    public static void standardizeSplits(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawSplits(raw);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Write this
    public static void standardizeNER(String inputFile, String outputFile) {

    }

    //TODO: Write this
    public static void standardizeLemmas(String inputFile, String outputFile) {

    }

    //PRIVATE METHODS
    //POS-TAGGING
    //Assumed to have only semantic tokens in text
    private static ArrayList<Token> tokenizeRawPOS(ArrayList<String> lines) {

        ArrayList<Token> taggedTokens = new ArrayList<Token>();

        int tokenCount = 1;
        for (String line : lines) {

            String[] split = line.split("\\s+");

            if (split.length == 2) {
                if (!split[0].trim().equalsIgnoreCase("")) {
                    Token token = new Token(split[0]);
                    token.tags.put("pos", split[1]);
                    token.indexInText = tokenCount;
                    tokenCount++;
                }
            } else {
                //Lots of "SPACE" tokens unfortunately
                System.out.println("Invalid line (incorrect split) " + line);
            }

        }

        return taggedTokens;

    }

    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("pos", simplifyPOSTag(token.tags.get("pos")));
        }
    }

    //TODO: Check
    private static String simplifyPOSTag(String tag) {

        if (tag.matches("NOUN")) {
            return "NN";
        } else if (tag.matches("ADJ")) {
            return "JJ";
        } else if (tag.matches("VERB")) {
            return "VB";
        } else if (tag.matches("ADV")) {
            return "RB";
        } else {
            return "Other";
        }
    }

    //NAMED ENTITY RECOGNITION - NER
    //SENTENCE SPLITTING
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
                output.add(token);
                tokenCount++;
            }
        }
        return output;
    }

}
