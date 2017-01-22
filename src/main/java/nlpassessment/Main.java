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


public class Main {

    public static final String[] RAW_TEXTS = {
        "Academic1.txt",
        "Academic3.txt",
        "Academic10.txt",
        "GenFict1.txt",
        "GenFict5.txt",
        "GenFict6.txt",
        "NewsArticle1.txt",
        "NewsArticle3.txt",
        "NewsArticle10.txt"
    };
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        Assessment.runStemmerTests();
        
        for(String input : RAW_TEXTS) {
            CoreNLP.runAnnotator("corpora/" + input, "output/" + input.replaceAll(".txt", "-core.tsv"));
            OpenNLP.runPOSAnnotator("corpora/" + input, "output/" + input.replaceAll(".txt", "-open.tsv"));
        }
        
    }
    
    
}
