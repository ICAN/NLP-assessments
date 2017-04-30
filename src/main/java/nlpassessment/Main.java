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

import java.util.regex.Pattern;

/**
 *
 * @author neal
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//


//        AssessmentPipeline.runJavaAnnotators();
//        AssessmentPipeline.runPreGoldPOSPipeline();
        AssessmentPipeline.runPostGoldPOSPipeline();

        //Lemma stuff
//        CoreNLP.runAnnotatorStackedLines("corpora/lemmas.txt", "annotator_outputs/lemmas-core.txt");
//        Utility.countLinesMatching("results_workspace/lemmas/" + "lemmas-spacy" + ".txt", "//\\+");     


        //Stemming stuff
//        Stemming.runStemmerTests();

        
        //Splitting stuff
//        AnnotatorScripts.convertSplits();
//        AssessmentPipeline.runJavaAnnotators();
//        AssessmentPipelineScripts.prettySplittingOutput();
//        AssessmentPipeline.countSplittingErrors();
    }

}
