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

import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Neal
 */
public class Utility {

    //Uses the "header" to determine field names
    //If "header" is the empty string, then the first line of the input file is used
    //If a header is provided as an argument, it will be split on whitespace
    public static Document importTSVDocument(String fileName, String columnSeparator, String header) {

        ArrayList<String> lines = Utility.readFileAsLines(fileName);
        int i = 0;
        ArrayList<Token> tokens = new ArrayList<>();

        String[] fields;

        //Use the first line as the header if none was provided
        if (header.equalsIgnoreCase("")) {
            fields = lines.get(0).split(columnSeparator);
            i++;
        } else {
            //If a header was provided, split it on whitespace
            fields = header.split("\\s+");
        }

        //Parse each line, adding new tokens to the token list
        while (i < lines.size()) {
            String[] split = lines.get(i).split(columnSeparator);
            Token token = new Token();
            for (int j = 0; j < fields.length; j++) {
                if (fields[j].equalsIgnoreCase("indexInText")) {
                    token.indexInText = Integer.parseInt(split[j]);
                } else if (fields[j].equalsIgnoreCase("indexInSentence")) {
                    token.indexInSentence = Integer.parseInt(split[j]);
                } else {
                    token.set(fields[j], split[j]);
                }
            }
            tokens.add(token);
            i++;
        }

        Document document = new Document();
        document.tokenList = tokens;
        return document;
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
        String condensed = "";
        if (insertLineBreaks) {
            for (String line : lines) {
                condensed += ("\n" + line);
            }
        } else {
            for (String line : lines) {
                condensed += (line);
            }
        }
        return condensed;
    }

    public static String arrayToString(String[] tokens, boolean spacesBetweenTokens) {
        String string = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            string += (" " + tokens[i]);
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
            lines.add(inFile.nextLine());
        }
        return lines;
    }

    public static String readFileAsString(String fileName, boolean insertLineBreaks) {
        return listToString(readFileAsLines(fileName), insertLineBreaks);
    }

    //TODO: 
    public static ArrayList<Token> tsvToTokens(ArrayList<String> lines, ArrayList<String> fields) {
        ArrayList<Token> tokens = new ArrayList<>();

        //TODO: Write. Generalized.
        return tokens;
    }


    public static void writeFile(String contents, String fileName) {
        try {
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write(contents);
            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }
    }

    /*
     General file output method
     */
    public static void writeFile(ArrayList<String> lines, String fileName) {

        try {
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);

            for (String line : lines) {
                writer.write(line + "\n");
            }

            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }

    }

    public static HashMap<String, Integer> getTagCounts(ArrayList<Token> tokens, String key) {

        HashMap<String, Integer> tagCounts = new HashMap<>();

        for (Token token : tokens) {
            if (tagCounts.keySet().contains(token.get(key))) {
                tagCounts.put(token.get(key), tagCounts.get(token.get(key)) + 1);
            } else {
                tagCounts.put(token.get(key), 1);
            }
        }
        return tagCounts;
    }

    public static void countTags(ArrayList<Token> tokens) {
        int adj = 0, noun = 0, adv = 0, verb = 0, other = 0;

        for (Token token : tokens) {
            String tag = token.get("posTag");
            if (tag.equalsIgnoreCase("NN")) {
                noun++;
            } else if (tag.equalsIgnoreCase("JJ")) {
                adj++;
            } else if (tag.equalsIgnoreCase("RB")) {
                adv++;
            } else if (tag.equalsIgnoreCase("VB")) {
                verb++;
            } else {
                other++;
            }
        }

    }

}
