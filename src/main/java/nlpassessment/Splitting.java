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
import nlpassessment.Utility;
import nlpassessment.Token;

/**
 *
 * @author Neal
 */
public class Splitting {
    
    public static final String SPLIT_PATTERN = "<>";
    public static final String SPLIT_TAG = "SPLIT";
    
    
    
    
    
    
    //Produces a raw test case from a gold standard using <> as sentence-end markers
    public static void goldToRaw(String inputFile, String outputFile) {
        
        //Include newline characters
        String gold = Utility.readFileAsString(inputFile, true);
        
        String raw = gold.replaceAll(SPLIT_PATTERN, "");
        
        Utility.writeFile(raw, outputFile);
        
    }
    

    public static ArrayList<Token> goldStringFileToCharacterTokens (String inputFile) {
        
        //Exclude newline characters, whitespace
        String gold = Utility.readFileAsString(inputFile, false).replaceAll("\\s", "");
        
        ArrayList<Token> goldTokens = new ArrayList<>();
        Token last = new Token(""); //Dummy token, shouldn't ever get used
        int tokenInText = 0;
        int tokenInSentence = 0;
        int sentenceCount = 0;
        for(int i = 0; i < gold.length(); i++) {
            if(gold.substring(i,i+2).equalsIgnoreCase(SPLIT_PATTERN)) {
                tokenInSentence = 0;
                i+=2;
                last.tags.put("split", SPLIT_TAG);
                sentenceCount++;
            } else {
                tokenInText++;
                tokenInSentence++;
                last = new Token("" + gold.charAt(i));
                last.indexInText = tokenInText;
                last.indexInSentence = tokenInSentence;
                last.tags.put("split", "_");
                goldTokens.add(last);
                
            }
        }
        //Shouldn't need this
//        goldTokens.get(goldTokens.size() - 1).tagset = "<SPLIT>";
        System.out.println("Sentences: " + sentenceCount + "\nCharacters: " + tokenInText);
        
                
        return goldTokens;
    }
    
    
    
    //Takes standardized, common-token-restricted outputs 
    //Tags the last character in each sentence "SPLIT"
    public static void tagFinalCharacters(String inputFile, String outputFile) {
        ArrayList<Token> tokens = Utility.standardLinesToTokens(Utility.readFileAsLines(inputFile), "split");

        int splits = 0;
        for(int i = 0; i < tokens.size()-1; i++) {
            //If the next token is earlier in its sentence than the current token:
            if(tokens.get(i+1).indexInSentence <= tokens.get(i).indexInSentence) {
                splits++;
                //Tag the current token
                tokens.get(i).tags.put("split", SPLIT_TAG);
                
            }
        }
        //Assume a sentence split at the end
        splits++;
        tokens.get(tokens.size()-1).tags.put("split", SPLIT_TAG);
        
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }
    
    public static void condenseSentences(String inputFile, String outputFile) {
        ArrayList<Token> input = Utility.standardLinesToTokens(Utility.readFileAsLines(inputFile), "split");
        
        ArrayList<String> output = new ArrayList<>();
        
        String sentence = "";
        for(Token token : input) {
            sentence += token.token;
            
            if (token.tags.get("split").equalsIgnoreCase(SPLIT_TAG)) {
                sentence += "<>";
                output.add(sentence + "");
                sentence = "";
            }
        }
        Utility.writeFile(output, outputFile);
    }
    
        
    
    
}
