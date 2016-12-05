/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlpassessment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author neal
 */
public class Assessment {

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
            if (!results.get(i).get("token").equalsIgnoreCase(goldStandard.get(i).get("token")) //                    && !results.get(goldIter).tagset.equalsIgnoreCase("Other")
                    //                    && !goldStandard.get(goldIter).tagset.equalsIgnoreCase("Other")
                    ) {
                tokenMismatches++;
                System.out.println("Mismatch: " + results.get(i).toString() + " \t\t " + goldStandard.get(i).toString());
                System.out.println("Warning: Results invalid due to " + tokenMismatches + " token mismatches");
            }

            if (results.get(i).get(tagType).equalsIgnoreCase(key)
                    && goldStandard.get(i).get(tagType).equalsIgnoreCase(key)) {

                truePositives++;

            } else if (goldStandard.get(i).get(tagType).equalsIgnoreCase(key)
                    && !results.get(i).get(tagType).equalsIgnoreCase(key)) {

                falseNegatives++;

            } else if (results.get(i).get(tagType).equalsIgnoreCase(key)
                    && !goldStandard.get(i).get(tagType).equalsIgnoreCase(key)) {
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
    public static ArrayList<Token> getTagConsensus(ArrayList<ArrayList<Token>> inputs, double threshold, String key) {

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

                String tag = list.get(i).get(key);
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
                    token.set(key, tag);
//                    System.out.print(" : " + tag);
                    agreement++;
                    if (tagCount.get(tag) == inputs.size()) {
                        unanimousDecision++;
                    }
                }
            }

            if (token.get(key).equalsIgnoreCase("??")) {
                System.out.print(" : ????");
                disagreement++;
            }
            consensus.add(token);
        }

        assert (agreement + disagreement) == inputs.get(0).size();

        System.out.println("\nTarget tag type: " + key
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
    private static ArrayList<Token> getCommonTokenList(ArrayList<Token> input, ArrayList<Token> standard, int skipCatchupRange) {

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
