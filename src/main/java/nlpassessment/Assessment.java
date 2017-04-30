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
import org.apache.commons.lang3.StringUtils;

public class Assessment {

    //Compares tags of two standardized token lists
    //NOTE: list lengths and all contained tokens MUST match
    //TODO: Distinguish between major and minor token mismatches 
    //using StringUtils.getLevenshteinDistance(String s, String t) 
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
        int tokenMinorMismatches = 0;
        int tokenMajorMismatches = 0;

        //Detect mismatches on tokens which differ between results and standard
        //Unless both are tagged "other"
        //TODO: Why not *always* detect token mismatches?
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).get("token").equalsIgnoreCase(goldStandard.get(i).get("token"))) {
                tokenMajorMismatches++;
                System.out.println("Mismatch: " + results.get(i).toString() + " \t\t " + goldStandard.get(i).toString());
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

        double sensitivity = truePositives / ((double) (falseNegatives + truePositives));
        double specificity = trueNegatives / ((double) (falsePositives + trueNegatives));

        String report = tagValue;
        report += "\tSampleSize=" + results.size();
        report += "\tTagTruePos=" + truePositives;
        report += "\tTagFalseNeg=" + falseNegatives;
        report += "\tTagFalsePos=" + falsePositives;
        report += "\tTagSensitivity=" + sensitivity;
        report += "\tTagSpecificity=" + specificity;
        report += "\tMinorTokenMismatches=" + tokenMinorMismatches;
        report += "\tMajorTokenMistmatches" + tokenMajorMismatches;

        return report;

    }

    //Returns the consensus of the input token lists
    //Inputs must already be standardized and restricted to common tokens 
    //so that input list lengths are identical and tokens are at least analogous
    //Threshold must be greater than 0.5
    //TODO: support plurality (highest vote < 0.5) decision
    public static Document getTagConsensus(ArrayList<Document> inputs, double threshold, String[] fields) {

//        int agreement = 0;
//        int unanimousDecision = 0;
//        int disagreement = 0;       
        
        Document consensus = new Document();

        //For each token
        for (int i = 0; i < inputs.get(0).tokens.size(); i++) {

            //Make a new consensus token & add to consensus document
            Token token = new Token();
            consensus.tokens.add(token);

            //For each field
            for (String field : fields) {

                //Count instances of each tagset for this token i
                HashMap<String, Integer> tagCount = new HashMap<>();
                for (Document document : inputs) {
                    String fieldValue = document.get(i).get(field);
                    if (tagCount.containsKey(fieldValue)) {
                        tagCount.put(fieldValue, tagCount.get(fieldValue) + 1);
                    } else {
                        tagCount.put(fieldValue, 1);
                    }
                }
                
                //Determine & set majority tag
                String majorityTag = Tag.NO_CONSENSUS;
                for(String tag : tagCount.keySet()) {
                    if (tagCount.get(tag) > threshold * tagCount.keySet().size()) {
                        majorityTag = tag;
                        
                    }
                }
                                
                token.set(field, majorityTag);
            }
        }

//        //
//        for(String field : fields) {
//            System.out.println("\nField: " + field
//                + "\nTokens: " + (agreement + disagreement)
//                + "\nDisagreement: " + disagreement
//                + "\nAgreements: " + agreement
//                + "\nUnanimous: " + unanimousDecision);
//        }
        
        return consensus;
    }

}
