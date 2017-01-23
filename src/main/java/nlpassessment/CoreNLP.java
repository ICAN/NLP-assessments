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

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class CoreNLP {

    public static final String[] FIELDS = {
        Tag.INDEX_IN_TEXT,
        Tag.INDEX_IN_SENT,
        Tag.SENT_NUMBER,
        //        Tag.START_CHAR,
        //        Tag.END_CHAR,
        Tag.TOKEN,
        Tag.POS,
//        Tag.NE
    };

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
    public static void runAnnotator(String inputFileName, String outputFileName) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        //Create empty Annotation
        Annotation annotation = new Annotation(Utility.readFileAsString(inputFileName, true));

        //Run annotators
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

        Document document = new Document();

        int sentenceNumber = 1;
        int indexInText = 1;
        for (CoreMap sentence : sentences) {
            int indexInSentence = 1;
            for (CoreLabel coreToken : sentence.get(TokensAnnotation.class)) {
                String word = coreToken.get(TextAnnotation.class);
                String pos = coreToken.get(PartOfSpeechAnnotation.class);
                String ne = coreToken.get(NamedEntityTagAnnotation.class);

                Token token = new Token();
                token.set(Tag.INDEX_IN_SENT, indexInSentence);
                token.set(Tag.INDEX_IN_TEXT, indexInText);
                token.set(Tag.SENT_NUMBER, sentenceNumber);
                //TODO: Add start char & end char if feasible             
                token.set(Tag.TOKEN, word);
                token.set(Tag.POS, pos);
                token.set(Tag.NE, ne);
                document.tokens.add(token);

                indexInSentence++;
                indexInText++;
            }

            sentenceNumber++;
        }

        document.PennToSimplifiedPOSTags();
        
        renormalizeBrackets(document.tokens);
        Utility.writeFile(document.toTSV(FIELDS), outputFileName);

    }

    public static void runAnnotator(String[] inputFileNames, String[] outputFileNames) {
        if (inputFileNames.length != outputFileNames.length) {
            System.out.println("Error: number of input files and number of output files differ");
            System.exit(-1);
        }

        for (int i = 0; i < inputFileNames.length; i++) {
            runAnnotator(inputFileNames[i], outputFileNames[i]);
        }
    }

    /*
        Converts Penn-standardized brackets to their correct single-character forms
     */
    private static void renormalizeBrackets(ArrayList<Token> tokens) {
        HashMap<String, String> map = new HashMap<>();
        map.put("-LRB-", "(");
        map.put("-RRB-", ")");
        map.put("-LCB-", "{");
        map.put("-RCB-", "}");
        map.put("-LSB-", "[");
        map.put("-RSB-", "]");

        for (Token token : tokens) {
            if (map.containsKey(token.get(Tag.TOKEN))) {
                token.set(Tag.TOKEN, map.get(token.get(Tag.TOKEN)));
            }
        }
    }

    
}
