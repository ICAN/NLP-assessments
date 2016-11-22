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

import stemming.Stemming;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Neal
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Stemming.runStemmerTests();

    }

    //Prints the number of tags of type tagType in the specified file
    public static void printTagCounts(String fileName, String tagType) {
        HashMap<String, Integer> tagCounts = Utility.getTagCounts(
                Utility.readFileAsStandardTokens(fileName, tagType), tagType);

        System.out.println("\nTags in " + fileName);
        for (String tag : tagCounts.keySet()) {
            System.out.println(tag + "\t" + tagCounts.get(tag));
        }

    }

    //First step in producing/comparing POS texts
    //Inptus: raw sentence splitter output "-out"
    //Outputs: standardized format for sentence splitting "-std"
    public static void standardizeAllSplits(String inputPath, String outputPath) {
        Spacy.standardizeSplits("split-spacy-out.txt", "split-spacy-std.txt");
        OpenNLP.standardizeSplits("split-opennlp-out.txt", "split-opennlp-std.txt");
        CoreNLP.standardizeSplits("split-corenlp-out.txt", "split-corenlp-std.txt");
        NLTK.standardizeSplits("split-nltk-out.txt", "split-nltk-std.txt");
        MBSP.standardizeSplits("split-mbsp-out.txt", "split-mbsp-std.txt");
    }

    //First step in producing/comparing POS texts
    //Inptus: raw POS-tagger output "-out"
    //Outputs: standardized format POS-tagger "-std"
    public static void standardizeAllPOS(String inputPath, String outputPath) {
        MBSP.standardizePOS(inputPath + "pos-mbsp-out.txt", outputPath + "pos-mbsp-std.txt");
        CoreNLP.standardizePOS(inputPath + "pos-corenlp-out.txt", outputPath + "pos-corenlp-std.txt");
        OpenNLP.standardizePOS(inputPath + "pos-opennlp-out.txt", outputPath + "pos-opennlp-std.txt");
        NLTK.standardizePOS(inputPath + "pos-nltk-out.txt", outputPath + "pos-nltk-std.txt");
        Spacy.standardizePOS(inputPath + "pos-spacy-out.txt", outputPath + "pos-spacy-std.txt");
    }

    //Second step in producing/comparing most texts
    //Inputs: A set of POS-tagger outputs in standarized format "-std"
    //Outputs: A corresponding set of POS-tagger outputs "-restricted", 
    //These outputs include only tokens which were reflected in all input documents
    public static void restrictDocumentsToCommonTokens(int range, String tagType) {

        HashMap<String, ArrayList<Token>> tokenLists = new HashMap<>();

        //Add lists
        tokenLists.put("mbsp", Utility.readFileAsStandardTokens(tagType + "-mbsp-std.txt", tagType));
        tokenLists.put("core", Utility.readFileAsStandardTokens(tagType + "-corenlp-std.txt", tagType));
        tokenLists.put("open", Utility.readFileAsStandardTokens(tagType + "-opennlp-std.txt", tagType));
        tokenLists.put("nltk", Utility.readFileAsStandardTokens(tagType + "-nltk-std.txt", tagType));
        tokenLists.put("spacy", Utility.readFileAsStandardTokens(tagType + "-spacy-std.txt", tagType));

        //Restrict lists iteratively
        //TODO: Make less ugly
        int iterations = 0;
        while (true) {
            iterations++;
            int deltaSize = 0;
            HashMap<String, ArrayList<Token>> restrictedLists = new HashMap<>();

            for (String baseListName : tokenLists.keySet()) {
                ArrayList<ArrayList<Token>> currentLists = new ArrayList<>();
                currentLists.add(tokenLists.get(baseListName)); //Put the baseList baseListName at index 0

                //Add the rest of the lists, order immaterial
                for (String otherList : tokenLists.keySet()) {
                    if (!otherList.equalsIgnoreCase(baseListName)) {
                        currentLists.add(tokenLists.get(otherList));
                    }
                }

                //Minimize the current base list
                ArrayList<Token> baseList = Utility.getCommonlTokenList(currentLists, 0, range);
                restrictedLists.put(baseListName, baseList);

            }

            for (String listName : tokenLists.keySet()) {
                deltaSize += (tokenLists.get(listName).size() - restrictedLists.get(listName).size());
            }

            tokenLists = restrictedLists;

            if (deltaSize == 0) {
                System.out.println("Completed producing all minimized POS results after " + iterations + " iterations.");
                break;
            } else {
                System.out.println("Iteration " + iterations + ": eliminated " + deltaSize + " tokens.");
            }

        }

        //Write all output files
        for (String listName : tokenLists.keySet()) {
            Utility.writeFile(Utility.tokensToStandardLines(tokenLists.get(listName)), tagType + "-" + listName + "-restricted.txt");
        }
    }

    //Takes a set of -restricted files and a -gold file
    //Produces sensitivity and specificity measurements for each specified tag
    //for each standardized, restricted output file as compared to the gold standard
    //and outputs these results to a a set of text files or something
    public static void testAllPOS(String goldStd) {

        //set which tests to run
        String[] tests = {"core", "spacy", "nltk", "mbsp", "open"};

        HashMap<String, ArrayList<Token>> outputs = new HashMap<>();

        for (String test : tests) {
            outputs.put(test, Utility.readFileAsStandardTokens(test + "-pos-restricted.txt", "pos"));
        }

        //read in gold std
        ArrayList<Token> gold = Utility.readFileAsStandardTokens(goldStd, "pos");

        String[] keys = {"VB", "RB", "NN", "JJ", "Other"};

        //For each tool
        for (String test : outputs.keySet()) {
            //Start a report
            String report = test.toUpperCase() + " REPORT\n\n";
            //For each key, 
            for (String key : keys) {
                //add the tool- and key-specific report
                report += Utility.compareTags(outputs.get(test), gold, key, "pos");
                report += "\n\n";
            }
            //When the tool report is finished for all keys, output the report

            Utility.writeFile(report, test + "-pos-report.txt");
        }

    }

    //Takes standardized, restricted text and produces a machine consensus
    public static void producePOSConsensus(String inputPath, String outputPath, double threshold) {

        ArrayList<ArrayList<Token>> tokenLists = new ArrayList<>();

        tokenLists.add(Utility.readFileAsStandardTokens("mbsp-pos-restricted.txt", "pos"));
        tokenLists.add(Utility.readFileAsStandardTokens("core-pos-restricted.txt", "pos"));
        tokenLists.add(Utility.readFileAsStandardTokens("open-pos-restricted.txt", "pos"));
        tokenLists.add(Utility.readFileAsStandardTokens("nltk-pos-restricted.txt", "pos"));
        tokenLists.add(Utility.readFileAsStandardTokens("spacy-pos-restricted.txt", "pos"));

        Utility.writeFile(
                Utility.tokensToStandardLines(Utility.getTagConsensus(tokenLists, threshold, "pos")),
                "all-pos-consensus.txt");

    }

    //Takes split, restricted? output and produces a machine consensus    
    public static void produceSplitConsensus(String inputPath, String outputPath, double threshold) {
        ArrayList<ArrayList<Token>> tokenLists = new ArrayList<>();

        tokenLists.add(Utility.readFileAsStandardTokens("mbsp-split-tagged.txt", "split"));
        tokenLists.add(Utility.readFileAsStandardTokens("core-split-tagged.txt", "split"));
        tokenLists.add(Utility.readFileAsStandardTokens("open-split-tagged.txt", "split"));
        tokenLists.add(Utility.readFileAsStandardTokens("nltk-split-tagged.txt", "split"));
        tokenLists.add(Utility.readFileAsStandardTokens("spacy-split-tagged.txt", "split"));

        Utility.writeFile(
                Utility.tokensToStandardLines(Utility.getTagConsensus(tokenLists, threshold, "split")),
                "all-split-consensus.txt");
    }

    //Takes all tagged-stage sentence splitter outputs and condenses them
    public static void produceSplitCondensed() {
        Splitting.condenseSentences("core-split-tagged.txt", "core-split-condensed.txt");
        Splitting.condenseSentences("nltk-split-tagged.txt", "nltk-split-condensed.txt");
        Splitting.condenseSentences("open-split-tagged.txt", "open-split-condensed.txt");
        Splitting.condenseSentences("mbsp-split-tagged.txt", "mbsp-split-condensed.txt");
        Splitting.condenseSentences("spacy-split-tagged.txt", "spacy-split-condensed.txt");
        Splitting.condenseSentences("all-split-consensus.txt", "all-split-consensus-condensed.txt");
    }

    public static void tagAllSplits(String inputPath, String outputPath) {
        Splitting.tagFinalCharacters("mbsp-split-restricted.txt", "mbsp-split-tagged.txt");
        Splitting.tagFinalCharacters("open-split-restricted.txt", "open-split-tagged.txt");
        Splitting.tagFinalCharacters("core-split-restricted.txt", "core-split-tagged.txt");
        Splitting.tagFinalCharacters("nltk-split-restricted.txt", "nltk-split-tagged.txt");
        Splitting.tagFinalCharacters("spacy-split-restricted.txt", "spacy-split-tagged.txt");
    }

}
