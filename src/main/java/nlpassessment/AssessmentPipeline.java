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
import java.util.regex.Pattern;

public class AssessmentPipeline {

    public static final String[] TEXTS = {
        //        "Academic1",
        //        "Academic3",
        //        "Academic10",
        //        "GenFict1",
        //        "GenFict5",
        //        "GenFict6",
        //        "NewsArticle1",
        //        "NewsArticle3",
        //        "NewsArticle10",
        "cleanSplits",
//        "mixed",
    };

    public static final String[] SPLITTING = {};

    public static final String[] PIPELINE_ANNOTATORS = {
        "core",
        "open",
        "nltk",
        "spacy",
    };

    public static final String[] JAVA_ANNOTATORS = {
        "core",
        "open"
    };

//    public static final String[] FIELDS_TO_USE = {
//        Tag.INDEX_IN_SENT,
//        Tag.TOKEN,};
    public static final String[] BASIC_FIELDS = {
        Tag.INDEX_IN_TEXT,
        Tag.INDEX_IN_SENT,
        Tag.SENT_NUMBER,
        Tag.TOKEN,
        Tag.POS
    };
    
    
    //Phase 1: Start a new assessment
    public static void runJavaAnnotators() {
        for (String text : TEXTS) {
            CoreNLP.runAnnotator("corpora/" + text + ".txt",
                    "annotator_outputs/" + text + "-core.tsv");
            OpenNLP.runPOSAnnotator("corpora/" + text + ".txt",
                    "annotator_outputs/" + text + "-open.tsv");
        }
    }

    //Splitting Branch
    public static void prettySplittingOutput() {
        
        for(String annotator : PIPELINE_ANNOTATORS) {
            Document doc = Utility.importTSVDocument("annotator_outputs/cleanSplits-" + annotator + ".tsv", "\t");
            for(Token token : doc.tokens) {
                System.out.print(token.get(Tag.TOKEN));
            }
            System.out.println();
            Utility.writeFile(doc.toSentences(), "results_workspace/splitting/prettySplits-"+annotator+".txt");
        }   
    }
    
    public static void countSplittingErrors() {
        
        Pattern missing = Pattern.compile("//-//");
        Pattern extra = Pattern.compile("//\\+//");
        for(String annotator : PIPELINE_ANNOTATORS) {
            String doc = Utility.readFileAsString("results_workspace/splitting/splitsAssessment-" + annotator + ".txt", false, false);
            doc = doc.replaceAll("\n", " ");
            System.out.println(doc);
            System.out.println("Missing: " + Utility.countInstancesOf(missing, doc));
            System.out.println("Extra: " + Utility.countInstancesOf(extra, doc));
            
        } 
    }
    
    
    //Phase 2: Assumes annotators have been run, include python annotators covered elsewhere
    public static void runPreGoldAssessmentPipeline() {
        double threshold = 0.1; //for fuzzy string matching

        //STEP 1: Import annotated docs
        ArrayList<Document> docs = new ArrayList<>();
        for (String text : TEXTS) {
            for (String annotator : PIPELINE_ANNOTATORS) {
                docs.add(Utility.importTSVDocument(
                        "annotator_outputs/" + text + "-" + annotator + ".tsv", "\t"));
            }
        }

        //STEP 2: Cleanup, conversions
        for (Document doc : docs) {
            doc.removeNonwordChars();
            doc.removeEmptyTokens();
            doc.PennToSimplifiedPOSTags();
            Utility.writeFile(doc.toTSV(BASIC_FIELDS),
                    "clean_outputs/" + doc.name + ".tsv");
        }

        //STEP 3: Produce common token annotations
        for (String text : TEXTS) {
            //Get all annotators' versions of this document
            ArrayList<Document> currentSet = new ArrayList<>();
            for (Document doc : docs) {
                if (doc.name.matches(text + "-.+")) {
                    currentSet.add(doc);
                }
            }

            //Keep working on this text until done
            while (true) {
                for (Document doc : currentSet) {
                    doc.restrictToTokensInOtherDocs((ArrayList<Document>) currentSet.clone(), 2, threshold);
                }

                if (!Document.sameLength(currentSet)) {
                    System.out.println("\n\nSome documents not the same length--beginning another pass");
                } else if (!Document.tokensMatchAlmost(currentSet, threshold)) {
                    System.out.println("\n\nTokens don't match sufficiently--beginning another pass");
                } else {
                    //Docs in set are same length and match sufficiently
                    //Write results and stop working on text
                    for (Document doc : currentSet) {
                        Utility.writeFile(doc.toTSV(BASIC_FIELDS),
                                "common_tokens/" + doc.name + ".tsv");
                    }
                    break;
                }
            }
        }

        //STEP 4: Produce machine consensus for each document on all specified fields
        //Add sentence splits
        for (Document doc : docs) {
            doc.tagSentenceSplits();
        }
        //Specify fields for consensus-building
        String[] consensusFields = {
            Tag.TOKEN,
            Tag.SPLITTING,
            Tag.POS, //            Tag.NE,
        };

        for (String text : TEXTS) {

            //Get all annotators' versions of this document
            ArrayList<Document> currentSet = new ArrayList<>();
            for (Document doc : docs) {
                if (doc.name.matches(text + "-.+")) {
                    currentSet.add(doc);
                }
            }

            Document consensus = Assessment.getTagConsensus(currentSet, 0.6, consensusFields);
            Utility.writeFile(consensus.toTSV(consensusFields), "consensus/" + text + ".tsv");
        }

    }
    
    //Phase 3: Assumes pre-gold std assessment pipeline has been run and that gold standards have been assembled
    public static void runPostGoldAssessmentPipeline() {

        //STEP 1: Import docs, gold standards, etc. from "common_tokens" and "gold_std"
        
       
        //STEP 2: Run assessments
        //TODO:
        //STEP 3: Format and output results
    }
    
    //TODO: move this?
    public static void convertCsvToSentences() {

        for (String annotator : PIPELINE_ANNOTATORS) {

            Document doc = Utility.importTSVDocument("annotator_outputs/splits-" + annotator + ".tsv", "\t");
            ArrayList<ArrayList<Token>> sentences = doc.toSentenceListsOfTokens();
            if (sentences != null) {
                System.out.println("Got doc as sentences");
            } else {
                System.out.println("Failed to get doc as sentences");
            }

            ArrayList<String> collapsedSents = new ArrayList<>();
            for (ArrayList<Token> sent : sentences) {
                String s = "";
                for (Token token : sent) {
                    s += token.get(Tag.TOKEN) + " ";
                }
                collapsedSents.add(s);
            }
            String output = "";
            int i = 0;
            for (String sent : collapsedSents) {
                i++;
                output += i + ">\t " + sent + "\n";
            }
            Utility.writeFile(output, "annotator_outputs/splitsReadable-" + annotator + ".txt");
        }

    }

}
