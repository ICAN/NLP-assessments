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

/**
 *
 * @author neal
 */
public class FormatChangeScripts {

    
    //Turns a document in format used to make document the splitting test cases 
    //into a raw text format on a single line for use by machine annotators
    public static void testCaseToRaw() {
        ArrayList<String> inputLines = Utility.readFileAsLines("corpora/splits.txt", true);
        System.out.println("input done");
        int i = 0;
        String output = "";
        for (String line : inputLines) {
            String[] split = line.trim().replaceAll("\\n", "").split("\\t");
            if (split.length == 2 && !split[0].matches("//.*")) {
                System.out.println(line);
                output += split[1] + " ";
                i++;
            }
        }
        System.out.println("Wrote " + i + " lines");
        Utility.writeFile(output, "corpora/cleanSplits.txt");
    }
    
    
    
}
