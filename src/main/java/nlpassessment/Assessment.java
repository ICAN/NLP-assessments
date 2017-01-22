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
import java.util.HashMap;

public class Assessment {

    //Compares tags of two standardized token lists
    //NOTE: list lengths and all contained tokens MUST match
    public static String compareTags(ArrayList<Token> results, ArrayList<Token> goldStandard, String tagValue, String tagKey) {

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
        //TODO: Why not *always* detect token mismatches?
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).get("token").equalsIgnoreCase(goldStandard.get(i).get("token"))) {
                tokenMismatches++;
                System.out.println("Mismatch: " + results.get(i).toString() + " \t\t " + goldStandard.get(i).toString());
                System.out.println("Warning: Results invalid due to " + tokenMismatches + " token mismatches");
            }

            if (results.get(i).get(tagKey).equalsIgnoreCase(tagValue)
                    && goldStandard.get(i).get(tagKey).equalsIgnoreCase(tagValue)) {

                truePositives++;

            } else if (goldStandard.get(i).get(tagKey).equalsIgnoreCase(tagValue)
                    && !results.get(i).get(tagKey).equalsIgnoreCase(tagValue)) {

                falseNegatives++;

            } else if (results.get(i).get(tagKey).equalsIgnoreCase(tagValue)
                    && !goldStandard.get(i).get(tagKey).equalsIgnoreCase(tagValue)) {
                falsePositives++;

            } else {
                trueNegatives++;
            }

        }

        assert results.size() == (trueNegatives + truePositives + falseNegatives + falsePositives);

        double sensitivity = (double) truePositives / (double) (falseNegatives + truePositives);
        double specificity = (double) trueNegatives / (double) (falsePositives + trueNegatives);

        String report = tagValue;
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
    public static ArrayList<Token> getTagConsensus(ArrayList<ArrayList<Token>> inputs, double threshold, String tagKey) {

        int agreement = 0;
        int unanimousDecision = 0;
        int disagreement = 0;

        ArrayList<Token> consensus = new ArrayList<>();

        //For each token i
        for (int i = 0; i < inputs.get(0).size(); i++) {

            HashMap<String, Integer> tagCount = new HashMap<>();
            Token token = new Token(inputs.get(0).get(i).get("token"));
            token.indexInText = i;

            System.out.print("\nToken: " + token.get("token") + " ");

            //Count instances of each tagset for this token i
            for (ArrayList<Token> list : inputs) {

                //Tokens must match
                assert list.get(i).get("token").equalsIgnoreCase(list.get((i + 1) % inputs.size()).get("token"));

                String tag = list.get(i).get(tagKey);
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
                    token.set(tagKey, tag);
//                    System.out.print(" : " + tag);
                    agreement++;
                    if (tagCount.get(tag) == inputs.size()) {
                        unanimousDecision++;
                    }
                }
            }

            if (token.get(tagKey).equalsIgnoreCase("??")) {
                System.out.print(" : ????");
                disagreement++;
            }
            consensus.add(token);
        }

        assert (agreement + disagreement) == inputs.get(0).size();

        System.out.println("\nTarget tag type: " + tagKey
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
    
  /* 
    Returns a new token list based on the "input" token list, 
    and excluding those tokens which fail to find a match in "standard"
    "input" and "standard" lists are not modified

    searchRange variable:
    searchRange helps determine how far to look when dealing with token mismatches
    It is not literally the distance to be searched in characters or tokens
    Higher search range numbers search further
    3 seems to work fine for word-tokens
    8 seems to work for character-tokens
*/
    //TODO: rewrite/clarify
private static ArrayList<Token> getCommonTokenList(ArrayList<Token> input, ArrayList<Token> standard, int searchRange) {

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
            if (!input.get(inputIter).get("token").equalsIgnoreCase(standard.get(stdIter).get("token"))) {
//                System.out.println("Token mismatch: std='" + standard.get(stdIter).i + "' input='" + input.get(inputIter).i + "'");
            } else {
                tokenMatch = true;
            }

            //HANDLING TOKEN MISMATCH
            //Uses the "standard" iterator as an anchor, searches in increasingly wide regions
            //around the "input" iterator, but only forward from the starting point 
            //because all previous tokens are assumed to have been matched or discarded
            //The further the "standard" iterator has had to increase, the further the "input" iterator is allowed to search from there
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
                    inputIter = startingInputIter + Math.max(0, deltaStdIter / 2 - searchRange);

                    while (inputIter < startingInputIter + deltaStdIter * 1.5 + searchRange
                            && inputIter < input.size()) {
                        if (input.get(inputIter).get("token").equalsIgnoreCase(standard.get(stdIter).get("token"))) {
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
                //Fail fast if tokens are being rejected excessively
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
