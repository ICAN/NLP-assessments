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

import static com.sun.xml.internal.ws.util.ServiceFinder.find;
import edu.stanford.nlp.util.StringUtils;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Neal
 */
public class Utility {

    //Uses the "header" to determine field names
    //If "header" is empty, then the first line of the input file is used
    //If a header is provided as an argument, it will be split on whitespace
    public static Document importTSVDocument(String fileName, String columnSeparator, String[] fields) {
        ArrayList<String> lines = Utility.readFileAsLines(fileName);
        int i = 0;
        ArrayList<Token> tokens = new ArrayList<>();

        //Use the first line as the header if none was provided
        if (fields.length == 0) {
            fields = lines.get(0).split(columnSeparator);
            i++;
        }
        Pattern colSepPattern = Pattern.compile(columnSeparator);
        //Parse each line, adding new tokens to the token list
        while (i < lines.size()) {
            if (!lines.get(i).matches("//.*") && !lines.get(i).isEmpty()) {
                String[] split = lines.get(i).split(columnSeparator);
                Token token = new Token();
                if (split.length == fields.length) {

                    for (int j = 0; j < fields.length; j++) {
                        token.set(fields[j], split[j]);
                    }

                } else if (fields.length == Utility.countInstancesOf(colSepPattern, lines.get(i)) + 1) {
                    //String.split() ignores trailing whitespace/empty strings, 
                    //causing the previous case to fail if the last field in row is empty
                    //This should handle that (and only that) unfortunate case
                    String[] replacementSplit = new String[fields.length];
                    System.arraycopy(split, 0, replacementSplit, 0, split.length);
                    replacementSplit[fields.length - 1] = "";
                    for (int j = 0; j < fields.length; j++) {
                        token.set(fields[j], replacementSplit[j]);
                    }

                } else {
                    System.out.println("Error: " + lines.get(i) + "     contains an incorrect number of fields");
                    System.out.println("Should contain " + fields.length + " but contains " + split.length);
                }
                tokens.add(token);
            }
            i++;
        }
        //Use file name, excluding path and extension, to get document name
        String docName = fileName.replaceFirst("\\.\\w+", "").replaceFirst(".*/", "");
        Document document = new Document(docName);

        System.out.println(
                "Imported " + docName);
        document.tokens = tokens;
        return document;
    }

//    //Includes trailing empty strings
//TODO: write this, account for empty strings
//    public static String[] split(String s, Pattern delimiter) {
//        int delimiters = Utility.countInstancesOf(delimiter, s);
//        String[] split = new String[delimiters+1];
//        Matcher matcher = delimiter.matcher(s);
//        int start = 0;
//        for(int i = 0; i < delimiters; i++) {
//            matcher.find();
//            split[i] = s.substring(start, matcher.start());
//            start = matcher.end();
//        }
//        split[delimiters] = s.substring()
//    }
//Counts instances of pattern in s
    public static int countInstancesOf(Pattern pattern, String s) {
        Matcher matcher = pattern.matcher(s);
        int i = 0;
        while (matcher.find()) {
            i++;
        }
        return i;
    }

    //Uses the "header" to determine field names
    public static Document importTSVDocument(String fileName, String columnSeparator) {
        return importTSVDocument(fileName, columnSeparator, new String[0]);
    }

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

//    public static ArrayList<Token> readFileAsStandardTokens(String fileName, String targetTag) {
//        return standardLinesToTokens(readFileAsLines(fileName), targetTag);
//    }
//
//    public static ArrayList<Token> readFileAsShortTokens(String fileName, int lineLength, String targetTag) {
//        return shortLinesToTokens(readFileAsLines(fileName), lineLength, targetTag);
//    }
    public static String listToString(ArrayList<String> lines, boolean insertLineBreaks) {
        return listToString(lines, "\n");
    }

    public static String listToString(ArrayList<String> lines, String spacer) {
        String condensed = "";
        for (String line : lines) {
            condensed += (spacer + line);
        }
        return condensed;
    }

    public static String arrayToString(String[] tokens, boolean spacesBetweenTokens) {
        String string = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            string += (tokens[i]);
            if (spacesBetweenTokens) {
                string += " ";
            }
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
    Ignores commented lines
     */
    public static ArrayList<String> readFileAsLines(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inFile = null;

        try {
            System.out.println(new File("").getAbsolutePath());
            System.out.println("Read: " + fileName);
            inFile = new Scanner(new FileReader(fileName));
        } catch (Exception e) {
            System.out.println("Failed to open input file. Exiting.");
            System.exit(-1);
        }

        while (inFile.hasNextLine()) {
            String line = inFile.nextLine();
            if (!line.matches("//.*")
                    && line.length() > 2) {
                lines.add(line);
            }
        }

        return lines;
    }

    /*
     Reads a file and returns its lines in an arraylist
    Ignores commented lines
     */
    public static ArrayList<String> readCommentsAsLines(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inFile = null;

        try {
            System.out.println(new File("").getAbsolutePath());
            System.out.println("Read: " + fileName);
            inFile = new Scanner(new FileReader(fileName));
        } catch (Exception e) {
            System.out.println("Failed to open input file. Exiting.");
            System.exit(-1);
        }

        while (inFile.hasNextLine()) {
            String line = inFile.nextLine();
            if (line.matches("//.*")
                    && line.length() > 2) {
                lines.add(line);
            }
        }

        return lines;
    }

    public static String readFileAsString(String fileName, boolean insertLineBreaks) {
        return listToString(readFileAsLines(fileName), insertLineBreaks);
    }

    public static void writeFile(String contents, String fileName) {
        try {
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write(contents);
            writer.close();

        } catch (Exception e) {
            System.out.println("Output file error: " + e.getMessage() + "        ...Exiting.");

            System.exit(-1);
        }

    }

    /*
     General file output method
     */
    public static void writeFile(ArrayList<String> lines, String fileName) {

        String contents = "";
        for (String line : lines) {
            contents += line + "\n";
        }

        writeFile(contents, fileName);

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
//    private static ArrayList<Token> getCommonTokenList(ArrayList<Token> input, ArrayList<Token> standard, int searchRange) {
//
//        ArrayList<Token> output = new ArrayList<>();
//
//        System.out.println("\n\nSTARTING PAIRWISE TOKEN REMOVAL");
//
//        //Results variables
//        int standardTokensIgnored = 0;
//        int inputTokensExcluded = 0;
//        int tokenCount = 0;
//
//        //Iterators
//        int stdIter = 0, inputIter = 0;
//        while (stdIter < standard.size()
//                && inputIter < input.size()) {
//
//            //DETECTING TOKEN MISMATCH
//            boolean tokenMatch = false;
//            if (!input.get(inputIter).get("token").equalsIgnoreCase(standard.get(stdIter).get("token"))) {
////                System.out.println("Token mismatch: std='" + standard.get(stdIter).i + "' input='" + input.get(inputIter).i + "'");
//            } else {
//                tokenMatch = true;
//            }
//
//            //HANDLING TOKEN MISMATCH
//            //Uses the "standard" iterator as an anchor, searches in increasingly wide regions
//            //around the "input" iterator, but only forward from the starting point 
//            //because all previous tokens are assumed to have been matched or discarded
//            //The further the "standard" iterator has had to increase, the further the "input" iterator is allowed to search from there
//            if (!tokenMatch) {
//                int startingInputIter = inputIter;
//
//                //corrections for ugly algorithm below
//                int deltaStdIter = -1;
//                stdIter--;
//
//                //ITERATING FORWARD IN GOLD STANDARD
//                while (!tokenMatch
//                        && stdIter < standard.size() - 2) {
//                    stdIter++;
//                    deltaStdIter++;
//
//                    //SEARCHING INCREASINGLY WIDE IN RESULTS TO FIND MATCHING TOKEN
//                    inputIter = startingInputIter + Math.max(0, deltaStdIter / 2 - searchRange);
//
//                    while (inputIter < startingInputIter + deltaStdIter * 1.5 + searchRange
//                            && inputIter < input.size()) {
//                        if (input.get(inputIter).get("token").equalsIgnoreCase(standard.get(stdIter).get("token"))) {
//                            tokenMatch = true;
//
//                            //Debug
////                            System.out.println("Match found: " + input.get(inputIter).i + "=" + standard.get(stdIter).i);
//                            break;
//                        } else {
//                            inputIter++;
//                        }
//                    }
//
//                }
//
//                //CALCULATING TRACKERS
//                int deltaInputIter = inputIter - startingInputIter;
////                System.out.println("Ignored " + deltaInputIter + " input tokens and " + deltaStdIter + " standard tokens this mismatch");
//                standardTokensIgnored += deltaStdIter;
//                inputTokensExcluded += deltaInputIter;
//
//                //Debug
////                if (stdIter < standard.size() && inputIter < input.size()) {
////                    System.out.println("Gold: " + stdIter + " " + standard.get(stdIter).i + " Res: " + inputIter + " " + input.get(inputIter).i);
////                }
//            }
//
//            //ADD INPUT TOKEN TO OUTPUT
//            if (tokenMatch) {
//                tokenCount++;
//                Token token = input.get(inputIter);
//                token.indexInText = tokenCount;
//                output.add(token);
//            } else {
//                //Fail fast if tokens are being rejected excessively
//                System.out.println("ERROR: MATCHING FAILED");
//            }
//
////            //Debug
////            if (stdIter >= standard.size()
////                    || inputIter >= input.size()) {
////                System.out.println("ITERATOR OUT OF BOUNDS");
////                System.out.println("Standard Iterator: " + stdIter + "/" + standard.size()
////                        + "\n" + "Target Iterator: " + inputIter + "/" + input.size());
////                break;
////            }
//            inputIter++;
//            stdIter++;
//        }
//
//        assert (output.size() + inputTokensExcluded == input.size());
//
//        System.out.println("\n" + standardTokensIgnored + "/" + standard.size() + " standard tokens unmatched "
//                + "\n" + inputTokensExcluded + "/" + input.size() + " input tokens excluded");
//
//        return output;
//
//    }
    //TODO: Test
    public static boolean almostEquals(String s, String t, double threshold) {
        double diff = StringUtils.levenshteinDistance(s, t) / ((double) (s.length() + t.length()));
        return diff < threshold;
    }

}
