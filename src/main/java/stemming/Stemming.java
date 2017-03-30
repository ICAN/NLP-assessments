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
package stemming;

import java.util.ArrayList;
import java.util.HashSet;
import nlpassessment.Utility;
import static nlpassessment.Utility.countTokensPerLine;

/**
 *
 * @author Neal
 *
 * A family of methods for running stemmers and assessing results
 *
 */
public class Stemming {

    //Runs all stemming, from beginning to data output
    //Done!
    //TODO: Check
    public static void runStemmerTests() {

        
        String testFileName = "stems/stems.txt";

        Stemming.cleanStemTest(testFileName, "stems/stems-clean.txt");
        
        Stemming.stemDocument("stems/stems-clean.txt", "stems/stems-out-porter1.txt", new stemming.Porter1());
        Stemming.stemDocument("stems/stems-clean.txt", "stems/stems-out-porter2.txt", new stemming.Porter2());
        Stemming.stemDocument("stems/stems-clean.txt", "stems/stems-out-lancaster.txt", new stemming.Lancaster());
        
        collapseStemmingResults("stems/stems-out-porter1.txt", "stems/stems-unique-porter1.txt");
        collapseStemmingResults("stems/stems-out-porter2.txt", "stems/stems-unique-porter2.txt");
        collapseStemmingResults("stems/stems-out-lancaster.txt", "stems/stems-unique-lancaster.txt");

        assessStemmingResults("stems/stems-clean.txt", "stems/stems-unique-porter1.txt", "stems/stems-data-porter1.tsv");
        assessStemmingResults("stems/stems-clean.txt", "stems/stems-unique-porter2.txt", "stems/stems-data-porter2.tsv");
        assessStemmingResults("stems/stems-clean.txt", "stems/stems-unique-lancaster.txt", "stems/stems-data-lancaster.tsv");
        
    }
        
    //Takes an array of String tokens
    //Returns a set including all unique tokens in the array
    //Since the return type is a set, not a list, the order of the tokens is not preserved
    //TODO: Test
    public static HashSet<String> getUnique(String[] input) {
        HashSet<String> unique = new HashSet<>();

        for (String string : input) {
            unique.add(string.trim());
        }
        return unique;

    }

    //Removes unique splitLines; ignores the first token in each unstemmedLine
    private static void cleanStemTest(String inputFile, String outputFile) {

        ArrayList<String> inputLines = Utility.readFileAsLines(inputFile, true);
        ArrayList<String> outputLines = new ArrayList<>();

        for (int i = 0; i < inputLines.size(); i++) {
            String outputLine = "";
            //Get a set of unique tokens from the current input line
            HashSet<String> unique = getUnique(inputLines.get(i).split("\\s+"));

            //Add all unique words to current output line
            for (String word : unique) {
                //Exclude hyphenated tokens
                if (!word.matches(".*-.*")) {
                    outputLine += (word.trim() + " ");
                }
            }
            //Add current output line to output
            outputLines.add(outputLine);
        }

        Utility.writeFile(outputLines, outputFile);
    }

    //Takes a text document as input; this file is not modified
    //Each token in the document is stemmed individually using the specified stemmer
    //The stemmed results are written to outputFile
    //TODO: Test
    public static void stemDocument(String inputFile, String outputFile, stemming.Stemmer stemmer) {

        //Get the stemming test case
        ArrayList<String> inputLines = Utility.readFileAsLines(inputFile, true);
        ArrayList<String> stemmedLines = new ArrayList<>();

        //For each inputLine
        for (String inputLine : inputLines) {

            String[] splitLine = inputLine.split("\\s+");
            String stemmedLine = "";

            //Keep the initial unstemmedLine-marking token <stem>
//            stemmedLine[0] = "" + unstemmedLine[0];
            //Stem each word, add to the stemmed line
            for (String token : splitLine) {

//                System.out.print(token + "-");
                token = token.trim().replaceAll("\\W+", "");
//                System.out.print(token + "-");   
                token = stemmer.stem(token);
//                System.out.print(token + "-");   
//                token = token.trim();
//                System.out.print(token + "-\n"); 
                stemmedLine += token + " ";
            }

            //Add current stemmed line to stemmed lines
            stemmedLines.add(stemmedLine);

        }

        Utility.writeFile(stemmedLines, outputFile);

    }

    //Takes a text file (inputFile) which is not modified
    //For each line in inputFile, a line including only unique tokens is added to outputFile
    //The order of the tokens in each line is not preserved
    //TODO: Test (looks okay)
    private static void collapseStemmingResults(String inputFile, String outputFile) {

        ArrayList<String> lines = Utility.readFileAsLines(inputFile, true);

        ArrayList<String> collapsedLines = new ArrayList<>();

        for (String line : lines) {

            String[] splitLine = line.split("\\s+");

            HashSet<String> uniqueStems = getUnique(splitLine);

            String collapsedLine = "";

            for (String uniqueStem : uniqueStems) {
                if (!uniqueStem.isEmpty()) {
                    collapsedLine += (uniqueStem.trim() + " ");
                }
            }

            collapsedLines.add(collapsedLine);

        }

        Utility.writeFile(collapsedLines, outputFile);
    }

    //Takes a cleaned test file (all tokens in line must be unique and must be part of the test)
    //and a collapsed stemmer output file (must already be collapsed so that each line consists only of unique tokens)
    public static void assessStemmingResults(String testFile, String stemmerOutputFile, String dataOutputFile) {

        ArrayList<String> testLines = Utility.readFileAsLines(testFile, true);
        ArrayList<String> stemmerOutputLines = Utility.readFileAsLines(stemmerOutputFile, true);

        //Validity checks
        if (testLines.size() != stemmerOutputLines.size()) {
            System.out.println("Test file and stemmer output file contain different numbers of lines");
        } else if (Utility.countNonemptyLines(testFile) != Utility.countNonemptyLines(stemmerOutputFile)) {
            System.out.println("Test file and stemmer output file contain different numbers of non-empty lines");
        }

        int totalTestTokens = 0;
        int totalStemmedTokens = 0;

        //Intraline results
        Integer[] testTokensPerLine = countTokensPerLine(testLines);
        Integer[] stemmedTokensPerLine = countTokensPerLine(stemmerOutputLines);
        Double[] proportionOfUniqueTokensRemaining = new Double[testTokensPerLine.length];

        for (int i = 0; i < testTokensPerLine.length; i++) {
            proportionOfUniqueTokensRemaining[i] = (double) stemmedTokensPerLine[i] / (double) testTokensPerLine[i];
//            System.out.println(relativeStemmedTokens[i] + " ");
        }

        //TODO: Add more interline assessment metrics
        //Interline results

        Integer[] numberOfLinesContainingMatches = new Integer[testLines.size()];
        
        //For each line, 
        //Determine how many other lines contain tokens matching a token the current line
        for (int currentLineIndex = 0; currentLineIndex < testTokensPerLine.length; currentLineIndex++) {
            int otherLinesContainingMatches = 0;        
            
            for (int otherLineIndex= 0; otherLineIndex < testTokensPerLine.length; otherLineIndex++) {
                boolean matchFoundInThisLine = false;
                //Don't compare lines to themselves                
                if(currentLineIndex!=otherLineIndex) {
                    
                    for(String tokenInCurrentLine : stemmerOutputLines.get(currentLineIndex).split("\\s+")) {
                        for(String tokenInComparedLine : stemmerOutputLines.get(otherLineIndex).split("\\s+")) {
                            if(tokenInCurrentLine.trim().equalsIgnoreCase(tokenInComparedLine.trim())) {
                                matchFoundInThisLine = true;
                            }
                        }
                    }                    
                }
                if(matchFoundInThisLine) {
                    otherLinesContainingMatches += 1;
                }
            }
            numberOfLinesContainingMatches[currentLineIndex] = otherLinesContainingMatches;            
        }

        //Prepare TSV lines & field headers
        ArrayList<String> assessmentLines = new ArrayList<>();
        assessmentLines.add(""
                + "line number\t"
                + "sample stem\t"
                + "unstemmed tokens\t"
                + "stemmed tokens\t"
                + "proportion of tokens remaining\t"
                + "number of lines containing matching tokens\t");
        
        //Add data fields
        for(int i = 0; i < testTokensPerLine.length; i++) {
            //Line number
            String line = (i+1) + "\t";
            //Stem
            line += stemmerOutputLines.get(i).split("\\s+")[0] + "\t";
            //Unstemmed tokens in line
            line += testTokensPerLine[i] + "\t";
            //Stemmed tokens in line
            line += stemmedTokensPerLine[i] + "\t";
            //Proportion of tokens remaining
            line += proportionOfUniqueTokensRemaining[i] + "\t";
            //Number of other lines which contain at least one token
            //matching at least one token in this line
            line += numberOfLinesContainingMatches[i] + "\t";
            
            assessmentLines.add(line);    
        }
        
        Utility.writeFile(assessmentLines, dataOutputFile);
        
        
    }

}
