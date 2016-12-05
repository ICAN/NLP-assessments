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
//

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class CoreNLP {

    //PUBLIC METHODS
    //A script for running the CoreNLP pipeline
    //Temporary setup for Windows
//    private static void runCoreNLPTerminal(String inputFileName) {
//        
//        Path currentRelativePath = Paths.get("");
//        String s = currentRelativePath.toAbsolutePath().toString();
//        System.out.println("Current relative path is: " + s);
//
//        //Order of command-line arguments changed because it doesn't work 
//        //if you put them in the order specified in the CoreNLP instructions
//        String cmd = "java -cp \"c:\\NLP\\CoreNLP\\3.6.0\\*\" -Xmx2g "
//                + "edu.stanford.nlp.pipeline.StanfordCoreNLP -outputFormat conll "
//                + "-file " + inputFileName + " -annotators tokenize,ssplit,pos,lemma,ner";
//        
//        ProcessBuilder pb = new ProcessBuilder(cmd.split("\\s"));
////        System.out.println("Process created");
////        pb.directory(new File("c:\\NLP\\CoreNLP\\3.6.0\\"));
//
//        try {
//            
//            File log = new File("0_CoreNLP-log.txt");
////            System.out.println("Created test log");
//            pb.redirectErrorStream(true);
//            pb.redirectOutput(Redirect.appendTo(log));
//            
//            Process process = pb.start();
////            Process process = Runtime.getRuntime().exec(cmd.split("\\s"));
////            System.out.println("Process started");
////            System.out.println("CoreNLP path is " + Paths.get("").toAbsolutePath().toString());
//            System.out.println("CoreNLP finished, returned: " + process.waitFor());
//            
//        } catch (Exception e) {
//            System.out.println("Error running CoreNLP\nMessage: " + e.getMessage());
//            
//        }
//        
//    }
    //TODO: Test. 
    //Not sure about working directories etc.
    public static void runAnnotator(String inputFile, String outputFile) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(Utility.readFileAsString(inputFile, true));

        // run all Annotators on this text
        pipeline.annotate(document);

        //TODO: Output document?
        
        
    }

    

    
    /*
     Converts Penn-standardized brackets to their correct single-character forms
     */
    private static void renormalizeAllBrackets(ArrayList<Token> tokens) {

        for (Token token : tokens) {
            token.set("token", renormalizeBracket(token.get("token")));
        }
    }

    private static String renormalizeBracket(String token) {
        HashMap<String, String> map = new HashMap<>();
        map.put("-LRB-", "(");
        map.put("-RRB-", ")");
        map.put("-LCB-", "{");
        map.put("-RCB-", "}");
        map.put("-LSB-", "[");
        map.put("-RSB-", "]");

        if (map.containsKey(token)) {
            token = map.get(token);
        }
        return token;
    }
    

    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.set(Tag.POS, simplifyPOSTag(token.get(Tag.POS)));
        }
    }

    private static String simplifyPOSTag(String tag) {

        if (tag.matches("NN.*")
                || tag.equals("PRP")
                || tag.equals("WP")) {
            return "NN";
        } else if (tag.matches("JJ.*")
                || tag.equals("WP$")
                || tag.equals("PRP$")) {
            return "JJ";
        } else if (tag.matches("V.*")
                || tag.equals("MD")) {
            return "VB";
        } else if (tag.matches("RB.*")
                || tag.equals("WRB")) {
            return "RB";
        } else {
            return "Other";
        }
    }
}
