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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author Neal
 */
public class OpenNLP {

    //Returns an annotated Document object
    //Including sentence-splits, tokenization, POS-tagging
    //TODO: Add NE recognition for person, org, time, ...?
    public static Document runAnnotator(String inputFileName, String outputFileName) {

        
        String modelPath = System.getProperty("user.dir") + "/src/main/OpenNLPModels/";
        String input = Utility.readFileAsString(inputFileName, true);

        //Initialization of Tokenization API        
        InputStream sentenceModelIn = null;
        SentenceModel sentenceModel = null;

        InputStream tokenModelIn = null;
        TokenizerModel tokenizerModel = null;

        InputStream nerPersonModelIn = null;
        TokenNameFinderModel nerPersonModel = null;

        InputStream posMaxEntModelIn = null;
        POSModel posMaxEntModel = null;
        InputStream posPerceptronModelIn = null;
        POSModel posPerceptronModel = null;

        try {
            sentenceModelIn = new FileInputStream(modelPath + "en-sent.bin");
            sentenceModel = new SentenceModel(sentenceModelIn);

            tokenModelIn = new FileInputStream(modelPath + "en-token.bin");           
            tokenizerModel = new TokenizerModel(tokenModelIn);
            
            nerPersonModelIn = new FileInputStream(modelPath + "en-ner-person.bin");
            nerPersonModel = new TokenNameFinderModel(nerPersonModelIn);
            
            
            posMaxEntModelIn = new FileInputStream(modelPath + "en-pos-maxent.bin");
            posMaxEntModel = new POSModel(posMaxEntModelIn);
            posPerceptronModelIn = new FileInputStream(modelPath + "en-pos-perceptron.bin");
            posPerceptronModel = new POSModel(posPerceptronModelIn);

        } catch (IOException e) {
            e.printStackTrace();
        }

        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);

        //Can also get span[], but 
        String[] sentences = sentenceDetector.sentDetect(input);

//        for(String sent : sentences) {
//            System.out.println(sent);
//        }
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);
        String[][] tokens = new String[sentences.length][]; //[sentence#][word# in sentence]

        for (int i = 0; i < sentences.length; i++) {
            tokens[i] = tokenizer.tokenize(sentences[i]);
        }

//        for (String[] sent : tokens) {
//            for(String token : sent) {
//                System.out.print(token + " ");
//            }
//            System.out.print("\n");
//        }
//        POSTaggerME posTagger = new POSTaggerME(posMaxEntModel);
        POSTaggerME posTagger = new POSTaggerME(posPerceptronModel);
        String[][] posTags = new String[sentences.length][];

        for (int i = 0; i < sentences.length; i++) {
            posTags[i] = posTagger.tag(tokens[i]);
        }

        for (int i = 0; i < sentences.length; i++) {
            String[] currentTokens = tokens[i];
            String[] currentTags = posTags[i];
            for (int j = 0; j < currentTokens.length; j++) {
                System.out.print(currentTokens[j] + "/" + currentTags[j] + "  ");
            }
            System.out.print("\n");
        }

//      Produce Document
        Document document = new Document();
        int tokenCount = 1;
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < tokens[i].length; j++) {
                Token token = new Token(tokens[i][j]);
                //All 1-indexed according to Token standard
                token.indexInSentence = j+1; 
                token.indexInText = tokenCount;
                token.sentenceNumber = i+1;
                token.set(Tag.POS, posTags[i][j]);
                document.tokenList.add(token);
                tokenCount++;
            }
        }
        
        //Output TSV
//        document.writeToTSV(outputFileName);       
        
        
        return new Document();
    }
    
    
    


    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.set("posTag", simplifyPOSTag(token.get("posTag")));
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
                token.set("neTag", split[1]);
            } else {
            }

        }
        return taggedTokens;
    }

    private static void simplifyNETags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            //TODO: Write
        }
    }

    //TODO: Check
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
                token.set("split", "_");
                tokenCount++;

            }
        }
        return output;
    }

}
