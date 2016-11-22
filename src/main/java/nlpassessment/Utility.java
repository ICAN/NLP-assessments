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

import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Neal
 */
public class Utility {

    //Waits for the process to end & returns the result
    //TODO: build variant which returns Process
    public static int runCommand(String cmd) {

        ProcessBuilder pb = new ProcessBuilder(cmd);

        try {

            Process process = pb.start();
            process.waitFor();
            return process.exitValue();

        } catch (Exception e) {
            System.out.println("Error running command: " + cmd + "\nMessage: " + e.getMessage());
            return -1;
        }
    }

    public static int countNonemptyLines(String fileName) {
        ArrayList<String> lines = readFileAsLines(fileName);
        int lineCount = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                lineCount++;
            }
        }
        System.out.println("Non-empty lines in " + fileName + ": " + lineCount);

        return lineCount;
    }

    public static int countLinesMatching(String fileName, String regex) {
        ArrayList<String> lines = readFileAsLines(fileName);
        int lineCount = 0;
        for (String line : lines) {
            if (!line.trim().matches(regex)) {
                lineCount++;
            }
        }
        System.out.println("Non-empty lines in " + fileName + ": " + lineCount);

        return lineCount;
    }

    public static ArrayList<Token> readFileAsStandardTokens(String fileName, String targetTag) {
        return standardLinesToTokens(readFileAsLines(fileName), targetTag);
    }

    public static ArrayList<Token> readFileAsShortTokens(String fileName, int lineLength, String targetTag) {
        return shortLinesToTokens(readFileAsLines(fileName), lineLength, targetTag);
    }

    public static String listToString(ArrayList<String> lines, boolean insertLineBreaks) {
        String condensed = "";
        if (insertLineBreaks) {
            for (String line : lines) {
                condensed += ("\n" + line);
            }
        } else {
            for (String line : lines) {
                condensed += (line);
            }
        }
        return condensed;
    }

    public static String arrayToString(String[] tokens, boolean spacesBetweenTokens) {
        String string = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            string += (" " + tokens[i]);
        }
        return string;
    }

    public static Integer[] countTokensPerLine(ArrayList<String> lines) {
        Integer[] tokenCounts = new Integer[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            tokenCounts[i] = lines.get(i).split("\\s+").length;
        }
        return tokenCounts;
    }

    /*
     Reads a file and returns its lines in an arraylist
     */
    public static ArrayList<String> readFileAsLines(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inFile = null;

        try {
            System.out.println(new File("").getAbsolutePath());
            System.out.println(fileName);
            inFile = new Scanner(new FileReader(fileName));
        } catch (Exception e) {
            System.out.println("Failed to open input file. Exiting.");
            System.exit(-1);
        }

        while (inFile.hasNextLine()) {
            lines.add(inFile.nextLine());
        }
        return lines;
    }

    public static String readFileAsString(String fileName, boolean insertLineBreaks) {
        return listToString(readFileAsLines(fileName), insertLineBreaks);
    }

    
    //TODO: 
    public static ArrayList<Token> tsvToTokens(ArrayList<String> lines, ArrayList<String> fields) {
        ArrayList<Token> tokens = new ArrayList<>();
        
        //TODO: Write. Generalized.
        
        
        return tokens;
    }
    
    //Input: Any standardized set of lines (one token per line, 4 fields per token)
    //Returns a list of tokens corresponding to the tokens on those lines
    //Assumes all tokens are semantic
    //TODO: Check/test
    public static ArrayList<Token> standardLinesToTokens(ArrayList<String> lines, String targetTag) {

        ArrayList<Token> tokens = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split("\\s+");
            if (split.length == 4) {
                Token token = new Token(split[2]);
                token.indexInText = Integer.parseInt(split[0]);
                token.indexInSentence = Integer.parseInt(split[1]);
                token.tags.put(targetTag, split[3]);
                tokens.add(token);
            } else {
                System.out.println("Invalid line: " + line + " has " + split.length + " tokens.");
            }
        }
        return tokens;
    }

    //Input: Lines in token-whitepace-tag format
    //Output: Token list with indexInText set, and "semantic" defaulted to true
    //TODO: FIX
    public static ArrayList<Token> shortLinesToTokens(ArrayList<String> lines, int lineLength, String targetTag) {
        ArrayList<Token> tokens = new ArrayList<>();
        int tokenCount = 0;
        for (String line : lines) {
            String[] split = line.split("\\s+");
            if (split.length == 3
                    && lineLength == 3) {
                tokenCount++;
                //TODO: THIS IS WRONG
                Token token = new Token(split[0]);
                token.indexInText = tokenCount;

                tokens.add(token);
            } else if (split.length == 2
                    && lineLength == 2) {
                tokenCount++;
                Token token = new Token(split[0]);
                token.indexInText = tokenCount;
                token.tags.put(targetTag, split[1]);
                tokens.add(token);
            } else {
                System.out.println("Invalid line: " + line + " has " + split.length + " tokens.");
            }
        }
        return tokens;
    }

    //Uses Token.toString()
    public static ArrayList<String> tokensToStandardLines(ArrayList<Token> tokens) {
        ArrayList<String> lines = new ArrayList<>();
        for (Token token : tokens) {
            lines.add(token.toString());
        }
        return lines;
    }

    //Includes only Token.token and Token.tagset in each line
    public static ArrayList<String> tokensToShortLines(ArrayList<Token> tokens, String targetTag) {
        ArrayList<String> lines = new ArrayList<>();
        for (Token token : tokens) {
            lines.add(token.indexInText + "\t" + token.token + "\t" + token.tags.get(targetTag));
        }
        return lines;
    }

    public static void writeFile(String contents, String fileName) {
        try {
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write(contents);
            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }
    }

    /*
     General file output method
     */
    public static void writeFile(ArrayList<String> lines, String fileName) {

        try {
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);

            for (String line : lines) {
                writer.write(line + "\n");
            }

            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }

    }

    public static HashMap<String, Integer> getTagCounts(ArrayList<Token> tokens, String targetTag) {

        HashMap<String, Integer> tagCounts = new HashMap<>();

        for (Token token : tokens) {
            if (tagCounts.keySet().contains(token.tags.get(targetTag))) {
                tagCounts.put(token.tags.get(targetTag), tagCounts.get(token.tags.get(targetTag)) + 1);
            } else {
                tagCounts.put(token.tags.get(targetTag), 1);
            }
        }
        return tagCounts;
    }

    public static int getSemanticTokenCount(ArrayList<Token> tokens) {
        int count = 0;
        for (Token token : tokens) {
            if (token.semantic) {
                count++;
            }
        }
        return count;
    }

    public static void countTags(ArrayList<Token> tokens) {
        int adj = 0, noun = 0, adv = 0, verb = 0, other = 0;

        for (Token token : tokens) {
            if (token.tags.get("pos").equalsIgnoreCase("NN")) {
                noun++;
            } else if (token.tags.get("pos").equalsIgnoreCase("JJ")) {
                adj++;
            } else if (token.tags.get("pos").equalsIgnoreCase("RB")) {
                adv++;
            } else if (token.tags.get("pos").equalsIgnoreCase("VB")) {
                verb++;
            } else {
                other++;
            }
        }

    }

    //Compares tags of two standardized token lists
    //NOTE: list lengths and all contained tokens MUST match
    public static String compareTags(ArrayList<Token> results, ArrayList<Token> goldStandard, String key, String tagType) {

        if (results.size() != goldStandard.size()) {
            System.out.println("Tokens list lengths differ");
        }

        System.out.println("\n\n\nSTARTING COMPARISON"
                + "\n");

        int trueNegatives = 0;
        int falseNegatives = 0;
        int truePositives = 0;
        int falsePositives = 0;
        int tokenMismatches = 0;

        //Detect mismatches on tokens which differ between results and standard
        //Unless both are tagged "other"
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).token.equalsIgnoreCase(goldStandard.get(i).token) //                    && !results.get(goldIter).tagset.equalsIgnoreCase("Other")
                    //                    && !goldStandard.get(goldIter).tagset.equalsIgnoreCase("Other")
                    ) {
                tokenMismatches++;
                System.out.println("Mismatch: " + results.get(i).toString() + " \t\t " + goldStandard.get(i).toString());
                System.out.println("Warning: Results invalid due to " + tokenMismatches + " token mismatches");
            }

            if (results.get(i).tags.get(tagType).equalsIgnoreCase(key)
                    && goldStandard.get(i).tags.get(tagType).equalsIgnoreCase(key)) {

                truePositives++;

            } else if (goldStandard.get(i).tags.get(tagType).equalsIgnoreCase(key)
                    && !results.get(i).tags.get(tagType).equalsIgnoreCase(key)) {

                falseNegatives++;

            } else if (results.get(i).tags.get(tagType).equalsIgnoreCase(key)
                    && !goldStandard.get(i).tags.get(tagType).equalsIgnoreCase(key)) {
                falsePositives++;

            } else {
                trueNegatives++;
            }

        }

        assert results.size() == (trueNegatives + truePositives + falseNegatives + falsePositives);

        double sensitivity = (double) truePositives / (double) (falseNegatives + truePositives);
        double specificity = (double) trueNegatives / (double) (falsePositives + trueNegatives);

        String report = key;
        report += "\nSampleSize = " + results.size();
        report += "\nTruePos = " + truePositives;
        report += "\nFalseNeg = " + falseNegatives;
        report += "\nFalsePos = " + falsePositives;
        report += "\nSensitivity = " + sensitivity;
        report += "\nSpecificity = " + specificity;

        return report;

    }

    //Returns the consensus of the input token lists
    //Inputs must already be standardized and restricted to common tokens 
    //so that input list lengths are identical and all tokens match
    public static ArrayList<Token> getTagConsensus(ArrayList<ArrayList<Token>> inputs, double threshold, String target) {

        int agreement = 0;
        int unanimousDecision = 0;
        int disagreement = 0;

        ArrayList<Token> consensus = new ArrayList<>();

        //For each token i
        for (int i = 0; i < inputs.get(0).size(); i++) {

            HashMap<String, Integer> tagCount = new HashMap<>();
            Token token = new Token(inputs.get(0).get(i).token);
            token.indexInText = i;

            System.out.print("\nToken: " + token.token + " ");

            //Count instances of each tagset for this token i
            for (ArrayList<Token> list : inputs) {

                //Tokens must match
                assert list.get(i).token.equalsIgnoreCase(list.get((i + 1) % inputs.size()).token);

                String tag = list.get(i).tags.get(target);
                System.out.print(tag + " ");
                if (tagCount.containsKey(tag)) {
                    tagCount.put(tag, tagCount.get(tag) + 1);
                } else {
                    tagCount.put(tag, 1);
                }
            }

            //Find & set consensus tagset
            for (String tag : tagCount.keySet()) {
                if (tagCount.get(tag) > inputs.size() * threshold) {
                    token.tags.put(target, tag);
//                    System.out.print(" : " + tag);
                    agreement++;
                    if (tagCount.get(tag) == inputs.size()) {
                        unanimousDecision++;
                    }
                }
            }

            if (token.tags.get(target).equalsIgnoreCase("??")) {
                System.out.print(" : ????");
                disagreement++;
            }
            consensus.add(token);
        }

        assert (agreement + disagreement) == inputs.get(0).size();

        System.out.println("\nTarget tag type: " + target
                + "\nTokens: " + (agreement + disagreement)
                + "\nDisagreement: " + disagreement
                + "\nAgreements: " + agreement
                + "\nUnanimous: " + unanimousDecision);

        System.out.print("\nSizes: ");
        for (int i = 0; i < inputs.size(); i++) {
            System.out.print(inputs.get(i).size() + " ");
        }
        return consensus;
    }
    
    //Returns a new token list based on the "input" token list, 
    //and excluding those tokens which fail to find a match in "standard"
    //"input" and "standard" lists are not modified
    //TODO: rewrite/clarify
    //skipCatchupRange helps determine how far to look when dealing with token mismatches
    //Higher skipCatchupRange numbers search further; 3 seems to work well for word-tokens
    //8 seems to work for character-tokens
    public static ArrayList<Token> getCommonTokenList(ArrayList<Token> input, ArrayList<Token> standard, int skipCatchupRange) {

        ArrayList<Token> output = new ArrayList<>();

        System.out.println("\n\nSTARTING PAIRWISE TOKEN REMOVAL");

        //Results variables
        int standardTokensIgnored = 0;
        int inputTokensExcluded = 0;
        int tokenCount = 0;

        //Iterators
        int stdIter = 0, inputIter = 0;
        while (stdIter < standard.size()
                && inputIter < input.size()) {

            //DETECTING TOKEN MISMATCH
            boolean tokenMatch = false;
            if (!input.get(inputIter).token.equalsIgnoreCase(standard.get(stdIter).token)) {
//                System.out.println("Token mismatch: std='" + standard.get(stdIter).i + "' input='" + input.get(inputIter).i + "'");
            } else {
                tokenMatch = true;
            }

            //HANDLING TOKEN MISMATCH
            //Uses the standard iterator as an anchor, searches in increasingly wide regions
            //around the output iterator, but only forward of the starting point
            //The further the standard iterator has to increase, the further the input iterator is allowed to range
            //Neither is permitted to search backwards from their starting position at mismatch, 
            //since it can be assumed that immediately preceding tokens are properly matched
            if (!tokenMatch) {
                int startingInputIter = inputIter;

                //corrections for ugly algorithm below
                int deltaStdIter = -1;
                stdIter--;

                //ITERATING FORWARD IN GOLD STANDARD
                while (!tokenMatch
                        && stdIter < standard.size() - 2) {
                    stdIter++;
                    deltaStdIter++;

                    //SEARCHING INCREASINGLY WIDE IN RESULTS TO FIND MATCHING TOKEN
                    inputIter = startingInputIter + Math.max(0, deltaStdIter / 2 - skipCatchupRange);

                    while (inputIter < startingInputIter + deltaStdIter * 1.5 + skipCatchupRange
                            && inputIter < input.size()) {
                        if (input.get(inputIter).token.equalsIgnoreCase(standard.get(stdIter).token)) {
                            tokenMatch = true;

                            //Debug
//                            System.out.println("Match found: " + input.get(inputIter).i + "=" + standard.get(stdIter).i);
                            break;
                        } else {
                            inputIter++;
                        }
                    }

                }

                //CALCULATING TRACKERS
                int deltaInputIter = inputIter - startingInputIter;
//                System.out.println("Ignored " + deltaInputIter + " input tokens and " + deltaStdIter + " standard tokens this mismatch");
                standardTokensIgnored += deltaStdIter;
                inputTokensExcluded += deltaInputIter;

                //Debug
//                if (stdIter < standard.size() && inputIter < input.size()) {
//                    System.out.println("Gold: " + stdIter + " " + standard.get(stdIter).i + " Res: " + inputIter + " " + input.get(inputIter).i);
//                }
            }

            //ADD INPUT TOKEN TO OUTPUT
            if (tokenMatch) {
                tokenCount++;
                Token token = input.get(inputIter);
                token.indexInText = tokenCount;
                output.add(token);
            } else {
                System.out.println("ERROR: MATCHING FAILED");
            }

//            //Debug
//            if (stdIter >= standard.size()
//                    || inputIter >= input.size()) {
//                System.out.println("ITERATOR OUT OF BOUNDS");
//                System.out.println("Standard Iterator: " + stdIter + "/" + standard.size()
//                        + "\n" + "Target Iterator: " + inputIter + "/" + input.size());
//                break;
//            }
            inputIter++;
            stdIter++;
        }

        assert (output.size() + inputTokensExcluded == input.size());

        System.out.println("\n" + standardTokensIgnored + "/" + standard.size() + " standard tokens unmatched "
                + "\n" + inputTokensExcluded + "/" + input.size() + " input tokens excluded");

        return output;

    }

    //Returns a list of tokens, excluding any tokens which don't appear in *all* input lists
    //Uses the list at "base" as the starting list (so its tags and other data are used)
    //and restricts from there
    public static ArrayList<Token> getCommonlTokenList(ArrayList<ArrayList<Token>> inputs, int base, int skipCatchupRange) {

        //Start with arbitrary base token list
        ArrayList<Token> output = inputs.get(base);

        //Iterate through other lists, restricting base list to tokens for which
        //matches are found in the other lists
        for (int i = 0; i < inputs.size(); i++) {
            if (i != base) {
                output = getCommonTokenList(output, inputs.get(i), skipCatchupRange);
            }
        }

        System.out.print("\nInput lengths: ");
        for (ArrayList<Token> list : inputs) {
            System.out.print(list.size() + ", ");
        }

        System.out.print("\nOutput length: " + output.size());
        return output;
    }
    
    

}
