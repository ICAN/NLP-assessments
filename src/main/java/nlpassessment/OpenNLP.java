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
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 *
 * @author Neal
 */
public class OpenNLP {
    
    //modelPath must point to the OpenNLP models, which are large and packaged seperately
    public static final String modelPath = "/home/neal/OpenNLP/";
    public static final String[] FIELDS = {
        Tag.INDEX_IN_TEXT,
        Tag.INDEX_IN_SENT,
        Tag.SENT_NUMBER,
        Tag.TOKEN,
        Tag.POS
        };

    //Returns an annotated Document object
    //Including sentence-splits, tokenization, POS-tagging
    public static void runPOSAnnotator(String inputFileName, String outputFileName) {

        String input = Utility.readFileAsString(inputFileName, true);

        //Sentence splitting
        InputStream sentenceModelIn = null;
        SentenceModel sentenceModel = null;
        //Tokenization
        InputStream tokenModelIn = null;
        TokenizerModel tokenizerModel = null;
        //POS Maximum Entropy
        InputStream posMaxEntModelIn = null;
        POSModel posMaxEntModel = null;
        //POS Perceptron
        InputStream posPerceptronModelIn = null;
        POSModel posPerceptronModel = null;

        try {
            //Sentence splitting
            sentenceModelIn = new FileInputStream(modelPath + "en-sent.bin");
            sentenceModel = new SentenceModel(sentenceModelIn);
            //Tokenization
            tokenModelIn = new FileInputStream(modelPath + "en-token.bin");
            tokenizerModel = new TokenizerModel(tokenModelIn);
            //POS Maximum Entropy
            posMaxEntModelIn = new FileInputStream(modelPath + "en-pos-maxent.bin");
            posMaxEntModel = new POSModel(posMaxEntModelIn);
            //POS Perceptron model
            posPerceptronModelIn = new FileInputStream(modelPath + "en-pos-perceptron.bin");
            posPerceptronModel = new POSModel(posPerceptronModelIn);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Sentence splitting
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
        String[] sentences = sentenceDetector.sentDetect(input); //Could also get spans rather than strings

        //Tokenization
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);
        String[][] tokens = new String[sentences.length][]; //[sentence#][word# in sentence]        
        Span[][] tokenSpans = new Span[sentences.length][];

        for (int i = 0; i < sentences.length; i++) {
            tokens[i] = tokenizer.tokenize(sentences[i]);
            tokenSpans[i] = tokenizer.tokenizePos(sentences[i]);
        }

        //POS Tagging
        POSTaggerME posTagger = new POSTaggerME(posMaxEntModel); //Alternative POS tagger
//        POSTaggerME posTagger = new POSTaggerME(posPerceptronModel);
        String[][] posTags = new String[sentences.length][];

        //Do tagging
        for (int i = 0; i < sentences.length; i++) {
            posTags[i] = posTagger.tag(tokens[i]);
        }


        //Produce Document
        Document document = new Document();
        int tokenCount = 1;
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < tokens[i].length; j++) {
                Token token = new Token(tokens[i][j]);
                //All 1-indexed according to Token standard
                token.indexInSentence = j + 1;
                token.indexInText = tokenCount;
                token.sentenceNumber = i + 1;
                token.startingChar = tokenSpans[i][j].getStart() + 1; 
                token.endingChar = tokenSpans[i][j].getEnd() + 1; 
                token.set(Tag.POS, posTags[i][j]);
                document.tokens.add(token);
                tokenCount++;
            }
        }
        
        document.PennToSimplifiedPOSTags();
        
        ArrayList<String> output = document.toTSV(FIELDS);

        Utility.writeFile(output, outputFileName);
    }

    //Returns an annotated Document object
    //Includes sentence-splitting, tokenization, NE-tagging
    //TODO: Confirm NER-tagger functionality (looks broken)
    //TODO: Once NER working, make sure the output also works
    public static Document runNERAnnotator(String inputFileName) {

        String input = Utility.readFileAsString(inputFileName, true);

        //Sentence splitting
        InputStream sentenceModelIn = null;
        SentenceModel sentenceModel = null;
        //Tokenization
        InputStream tokenModelIn = null;
        TokenizerModel tokenizerModel = null;
        //NER Date
        InputStream nerDateModelIn = null;
        TokenNameFinderModel nerDateModel = null;
        //NER Location
        InputStream nerLocationModelIn = null;
        TokenNameFinderModel nerLocationModel = null;
        //NER Money
        InputStream nerMoneyModelIn = null;
        TokenNameFinderModel nerMoneyModel = null;
        //NER Organization
        InputStream nerOrganizationModelIn = null;
        TokenNameFinderModel nerOrganizationModel = null;
        //NER Percentage
        InputStream nerPercentageModelIn = null;
        TokenNameFinderModel nerPercentageModel = null;
        //NER Person
        InputStream nerPersonModelIn = null;
        TokenNameFinderModel nerPersonModel = null;
        //NER Time
        InputStream nerTimeModelIn = null;
        TokenNameFinderModel nerTimeModel = null;

        try {
            //Sentence splitting
            sentenceModelIn = new FileInputStream(modelPath + "en-sent.bin");
            sentenceModel = new SentenceModel(sentenceModelIn);
            //Tokenization
            tokenModelIn = new FileInputStream(modelPath + "en-token.bin");
            tokenizerModel = new TokenizerModel(tokenModelIn);
            //NER Date
            nerDateModelIn = new FileInputStream(modelPath + "en-ner-date.bin");
            nerDateModel = new TokenNameFinderModel(nerDateModelIn);
            //NER Location
            nerLocationModelIn = new FileInputStream(modelPath + "en-ner-location.bin");
            nerLocationModel = new TokenNameFinderModel(nerLocationModelIn);
            //NER Money
            nerMoneyModelIn = new FileInputStream(modelPath + "en-ner-money.bin");
            nerMoneyModel = new TokenNameFinderModel(nerMoneyModelIn);
            //NER Organization
            nerOrganizationModelIn = new FileInputStream(modelPath + "en-ner-organization.bin");
            nerOrganizationModel = new TokenNameFinderModel(nerOrganizationModelIn);
            //NER Percentage
            nerPercentageModelIn = new FileInputStream(modelPath + "en-ner-percentage.bin");
            nerPercentageModel = new TokenNameFinderModel(nerPercentageModelIn);
            //NER Person
            nerPersonModelIn = new FileInputStream(modelPath + "en-ner-person.bin");
            nerPersonModel = new TokenNameFinderModel(nerPersonModelIn);
            //NER Time
            nerTimeModelIn = new FileInputStream(modelPath + "en-ner-time.bin");
            nerTimeModel = new TokenNameFinderModel(nerTimeModelIn);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Sentence splitting
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
        String[] sentences = sentenceDetector.sentDetect(input); //Could also get spans rather than strings

        //Tokenization
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);
        String[][] tokens = new String[sentences.length][]; //[sentence#][word# in sentence]
        Span[][] tokenSpans = new Span[sentences.length][];

        for (int i = 0; i < sentences.length; i++) {
            tokens[i] = tokenizer.tokenize(sentences[i]);
            tokenSpans[i] = tokenizer.tokenizePos(sentences[i]);
        }

        //NE Tagging
        NameFinderME nerDateFinder = new NameFinderME(nerDateModel);
        NameFinderME nerLocationFinder = new NameFinderME(nerLocationModel);
        NameFinderME nerMoneyFinder = new NameFinderME(nerMoneyModel);
        NameFinderME nerOrganizationFinder = new NameFinderME(nerOrganizationModel);
        NameFinderME nerPercentageFinder = new NameFinderME(nerPercentageModel);
        NameFinderME nerPersonFinder = new NameFinderME(nerPersonModel);
        NameFinderME nerTimeFinder = new NameFinderME(nerTimeModel);

        Span[][] nerDateTags = new Span[tokens.length][];
        Span[][] nerLocationTags = new Span[tokens.length][];
        Span[][] nerMoneyTags = new Span[tokens.length][];
        Span[][] nerOrganizationTags = new Span[tokens.length][];
        Span[][] nerPercentageTags = new Span[tokens.length][];
        Span[][] nerPersonTags = new Span[tokens.length][];
        Span[][] nerTimeTags = new Span[tokens.length][];

        //Get named entity spans
        for (int i = 0; i < tokens.length; i++) {
            nerDateTags[i] = nerDateFinder.find(tokens[i]);
            System.out.print(Utility.arrayToString(tokens[i], true) + "\n");
            for (Span span : nerDateTags[i]) {
                System.out.print(Tag.NE_DATE + span.getStart() + "-" + span.getEnd() + "...");
            }
            System.out.print("\n");
            nerLocationTags[i] = nerLocationFinder.find(tokens[i]);
            nerMoneyTags[i] = nerMoneyFinder.find(tokens[i]);
            nerOrganizationTags[i] = nerOrganizationFinder.find(tokens[i]);
            nerPercentageTags[i] = nerPercentageFinder.find(tokens[i]);
            nerPersonTags[i] = nerPersonFinder.find(tokens[i]);
            nerTimeTags[i] = nerTimeFinder.find(tokens[i]);

            //Must call clearAdaptiveData after every document 
            //According to OpenNLP documentation
            nerDateFinder.clearAdaptiveData();
            nerLocationFinder.clearAdaptiveData();
            nerMoneyFinder.clearAdaptiveData();
            nerOrganizationFinder.clearAdaptiveData();
            nerPercentageFinder.clearAdaptiveData();
            nerPersonFinder.clearAdaptiveData();
            nerTimeFinder.clearAdaptiveData();
        }

        //Get NER tagsets
        String[][] nerTagsets = new String[tokens.length][];

        //For each sentence
        for (int i = 0; i < tokens.length; i++) {
            //One NER tagset per token
            nerTagsets[i] = new String[tokens[i].length];

            //For each token
            for (int j = 0; j < tokens[i].length; j++) {
                nerTagsets[i][j] = "";
                //For each span, add appropriate tags
                for (Span span : nerDateTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_DATE + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerLocationTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_LOCATION + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerMoneyTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_MONEY + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerOrganizationTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_ORG + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerPercentageTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_PERCENT + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerPersonTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_PERSON + Tag.TAG_SEP;
                    }
                }
                for (Span span : nerTimeTags[i]) {
                    if (span.intersects(tokenSpans[i][j])) {
                        nerTagsets[i][j] += Tag.NE_TIME + Tag.TAG_SEP;
                    }
                }
            }

        }

        //Produce Document
        Document document = new Document();
        int tokenCount = 1;
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < tokens[i].length; j++) {
                Token token = new Token(tokens[i][j]);
                //All 1-indexed according to Token standard
                token.indexInSentence = j + 1;
                token.indexInText = tokenCount;
                token.sentenceNumber = i + 1;
                token.startingChar = tokenSpans[i][j].getStart();
                token.endingChar = tokenSpans[i][j].getEnd();
                token.set(Tag.NE, nerTagsets[i][j]);
                document.tokens.add(token);
                tokenCount++;
            }
        }

        return new Document();
    }

    
    

}
