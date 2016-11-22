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
    
    public static void standardizePOS(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawPOS(raw);
        simplifyPOSTags(tokens);
        renormalizeAllBrackets(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Double-check
    public static void standardizeNER(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawNER(raw);
        simplifyNERTags(tokens);
        renormalizeAllBrackets(tokens);
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }

    //TODO: Test
    public static void standardizeSplits(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<Token> tokens = tokenizeRawSplits(raw);
        
        Utility.writeFile(Utility.tokensToStandardLines(tokens), outputFile);
    }
    
    public static void cleanSplits(String inputFile, String outputFile) {
        ArrayList<String> raw = Utility.readFileAsLines(inputFile);
        ArrayList<String> clean = cleanRawSplits(raw);
        ArrayList<String> spaced = new ArrayList<>();
        int sentenceNumber = 1;
        for (String string : clean) {
            spaced.add("<SENTENCE " + sentenceNumber + ">\t" + string);
            spaced.add("<>");
            sentenceNumber++;
        }
        Utility.writeFile(spaced, outputFile);
    }

    //TODO: Write this
    public static void standardizeLemmas(String inputFile, String outputFile) {
        
    }

    //PRIVATE METHODS
    /*
     Confirms that the string is a valid line
     */
    private static boolean validatePOSLine(String string) {
        String[] split = string.split("\\s+");
        if (split.length != 7) {
            return false;
        } else if (!string.matches("[\\S]+" //Token number in sentence
                + "[\\s]+[\\S]+" //Token
                + "[\\s]+_"
                + "[\\s]+[\\S]+" //Tag
                + "[\\s]+_.*")) {
            return false;
        }
        
        return true;
    }

    /*
     Converts Penn-standardized brackets to their correct single-character forms
     */
    private static void renormalizeAllBrackets(ArrayList<Token> tokens) {
        
        HashMap<String, String> map = getBracketMap();
        
        for (Token token : tokens) {
            token.token = renormalizeBracket(token.token, map);
        }
    }
    
    private static String renormalizeBracket(String token, HashMap<String, String> map) {
        if (map.containsKey(token)) {
            token = map.get(token);
        }
        return token;
    }
    
    private static HashMap<String, String> getBracketMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("-LRB-", "(");
        map.put("-RRB-", ")");
        map.put("-LCB-", "{");
        map.put("-RCB-", "}");
        map.put("-LSB-", "[");
        map.put("-RSB-", "]");
        return map;
    }

    //PARTS OF SPEECH TAGGING - POS
    private static ArrayList<Token> tokenizeRawPOS(ArrayList<String> lines) {
        
        ArrayList<Token> taggedTokens = new ArrayList<Token>();
        int tokenCount = 0;
        
        for (String line : lines) {
            if (validatePOSLine(line)) {
                tokenCount++;
                String[] split = line.split("\\s+");
                Token token = new Token(split[1]);
                token.tags.put("pos", split[3]);
                token.indexInText = tokenCount;
                taggedTokens.add(token);
//                System.out.println("Tokenized as: " + token + "\t" + tagset);
            }
        }
        
        return taggedTokens;
        
    }
    
    private static void simplifyPOSTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("pos", simplifyPOSTag(token.tags.get("pos")));
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

    //NAMED ENTITY RECOGNITION - NER
    //TODO: Test/Confirm
    private static ArrayList<Token> tokenizeRawNER(ArrayList<String> lines) {
        
        ArrayList<Token> taggedTokens = new ArrayList<Token>();
        int tokenCount = 0;
        
        for (String line : lines) {
            if (validatePOSLine(line)) {
                tokenCount++;
                String[] split = line.split("\\s+");
                Token token = new Token(split[2]);
                token.tags.put("ner", split[4]);
                token.indexInText = tokenCount;
                taggedTokens.add(token);
            }
        }
        
        return taggedTokens;
    }

    
    private static void simplifyNERTags(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            token.tags.put("ner", simplifyNERTag(token.tags.get("ner")));
        }
    }

    //TODO: Test
    private static String simplifyNERTag(String tag) {
        
        if (tag.equalsIgnoreCase("O")) {
            return "_";
        } else if (tag.equalsIgnoreCase("LOCATION")) {
            return "LOC";
        } else if (tag.equalsIgnoreCase("")) {
            
        } else {
            System.out.println("Error simplifying CoreNLP NER tags");
            return "--ERROR--";
        }
        
        return "";
    }

    //SENTENCE SPLITTING
    private static ArrayList<String> cleanRawSplits(ArrayList<String> lines) {
        ArrayList<String> intermediate = new ArrayList<>();

        //Combining multi-line sentences 
        ArrayList<String> output = new ArrayList<>();
        String combined = "";
        for (String line : intermediate) {
            if (line.matches("Sentence #[0-9]+.*")) {
                if (!combined.equals("")) {
                    output.add(combined);
                    combined = "";
                }
            } else {
                combined += line;
            }
        }
        
        if (!combined.equals("")) {
            output.add(combined);
        }
        
        return output;
    }

    //Tokenizes by whitespace; number tokens according to place in sentence
    //TODO: Finish/test
    private static ArrayList<Token> tokenizeRawSplits(ArrayList<String> lines) {
        HashMap<String, String> map = getBracketMap();
        ArrayList<Token> output = new ArrayList<>();
        int characterInText = 0;
        int characterInSentence = 0;
        for (int i = 1; i < lines.size(); i++) {
            
            String line = lines.get(i);
            if (line.matches("Sentence #[0-9]+.*")) {
                characterInSentence = 0;
            } else if (line.matches("\\[Text=.+ CharacterOffsetEnd=[0-9]+\\]")) {
                //Do nothing, we don't want these
//                System.out.println("ERROR: CLEAN FIRST sent:" + line);
            } else {
                
                String[] split = line.split("\\s+");

                //Renormalize brackets and build combined renormalized string
                for (int j = 0; j < split.length; j++) {
                    split[j] = renormalizeBracket(split[j], map);
                }
                String combined = "";
                for (int j = 0; j < split.length; j++) {
                    combined += split[j];
                }

                //Tokenize by character
                for (int j = 0; j < combined.length(); j++) {
                    characterInText++;
                    characterInSentence++;
                    Token token = new Token("" + combined.charAt(j));
                    token.indexInText = characterInText;
                    token.indexInSentence = characterInSentence;
                    token.tags.put("split", "_");
                    output.add(token);
                    
                }
            }
        }
        
        return output;
    }
    
}
