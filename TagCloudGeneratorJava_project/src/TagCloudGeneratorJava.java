import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Write a Java program that generates a tag cloud from a given input text.
 *
 * @author Yoora Choi
 */
public final class TagCloudGeneratorJava {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudGeneratorJava() {
        // no code needed here
    }

    /**
     * Compare key of {Pair<String, Integer>} in alphabetical order. Regardless
     * of case.
     */
    private static final class MapPairKeyLT
            implements Comparator<Map.Entry<String, Integer>> {
        /**
         * @param o1
         *            the first pair
         * @param o2
         *            the second pair
         * @requires String to be a single word
         *
         * @return a negative integer if o1.getKey() should appear before
         *         o2.key(), a positive integer if o1.getKey() should appear
         *         after o2.key(), or zero if the keys are equal ignoring case
         */
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {

            return o1.getKey().compareToIgnoreCase(o2.getKey());

        }
    }

    /**
     * Compare value of {Map.Entry<String, Integer>} in decreasing order.
     *
     */
    private static final class MapPairValueLT
            implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {

            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        strSet.clear();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!strSet.contains(c)) {
                strSet.add(c);
            }
        }
    }

    /**
     * Define candidate separators and add them to a set by using the method
     * 'generateElements'.
     *
     * @param s
     * @updates s
     */
    private static void defineSeparators(Set<Character> s) {

        final String sepChars = " \t\r\n,.;:!?()[]{}<>\"'/-_`~@#$%^&*+=|\\";
        generateElements(sepChars, s);
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int i = position;

        //If a char in 'position' index doesn't contain separators,
        //then, increase i before 'i'-th char contains a separator.

        if (!separators.contains(text.charAt(position))) {

            while (i < text.length() && !separators.contains(text.charAt(i))) {
                i++;
            }
        }

        //Otherwise just increase i
        else {
            while (i < text.length() && separators.contains(text.charAt(i))) {
                i++;
            }

        }

        return text.substring(position, i);

    }

    /**
     * Processing through the input file ({@code BufferedReader}) and assigns
     * the word and its occurrences in a {@code Map}.
     *
     * @param fileIn
     *            the input text file ({@code BufferedReader})
     * @param words
     *            the {@code Map} containing all the words and its occurrences
     * @updates {@code words}
     * @ensures <pre>
     * fileIn's words = {@code Map}'s Key(words) and Value(occurrences)
     * </pre>
     */
    private static void wordCount(BufferedReader fileIn, Map<String, Integer> words)
            throws IOException {
        // Clear Map
        words.clear();

        // Declare sepSet and store separator elements into sepSet
        // using 'defineSeparators' method
        Set<Character> sepSet = new HashSet<Character>();
        defineSeparators(sepSet);

        String line = fileIn.readLine();

        while (line != null) {
            line = line.toLowerCase();
            int index = 0;

            while (index < line.length()) {
                // Extract a word or a separator from fileIn
                // using Method 'nextWordOrSeparator'.
                String wordORsep = nextWordOrSeparator(line, index, sepSet);

                // If sepSet starts with a non-separator, it's a word
                // and store the word as a key and 1 as its occurrences in Map
                if (!sepSet.contains(wordORsep.charAt(0))) {
                    if (words.containsKey(wordORsep)) {
                        int count = words.get(wordORsep);
                        count = count + 1;
                        words.put(wordORsep, count);
                    } else { //Otherwise, just increase count and store it in the map
                        words.put(wordORsep, 1);
                    }
                }

                index = index + wordORsep.length();

            }

            line = fileIn.readLine();

        }

    }

    /**
     * Selects up to {@code wordNum} most frequent words from {@code words} and
     * returns them in a SortingMachine ordered by key (in insertion mode).
     *
     * @param words
     *            map of words to their counts
     * @param wordNum
     *            number of most frequent words
     * @param minMax
     *            int array of length 2; minMax[0] = minCount, minMax[1] =
     *            maxCount
     * @return a list containing up to N pairs, which will be ordered
     *         alphabetically by key
     */
    public static List<Map.Entry<String, Integer>> wordProcess(Map<String, Integer> words,
            int wordNum, int[] minMax) {

        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(
                words.entrySet());

        // Sort in decreasing order.
        entries.sort(new MapPairValueLT());

        // Decide how many to take
        int size = entries.size();
        int limit = wordNum;
        if (limit > size) {
            limit = size;
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<Map.Entry<String, Integer>>();

        if (limit == 0) {
            // when there is no word chosen
            minMax[0] = 0;
            minMax[1] = 0;
            return result;
        }

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // Take top 'limit' entries while updating min/max
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> e = entries.get(i);
            result.add(e);

            int c = e.getValue();
            if (c < min) {
                min = c;
            }
            if (c > max) {
                max = c;
            }
        }

        minMax[0] = min;
        minMax[1] = max;

        // Sort result in alphabetical order
        result.sort(new MapPairKeyLT());

        // Return result
        return result;

    }

    /**
     *
     * @param count
     *            the word count
     * @param minCount
     *            minimum count among selected words
     * @param maxCount
     *            maximum count among selected words
     * @return font size between 11 and 48
     */
    private static int getWordSize(int count, int minCount, int maxCount) {

        // css styles font-size starts from f11 to f48.
        final int minFont = 11;
        final int maxFont = 48;

        // if frequency of all words is same
        if (minCount == maxCount) {
            return (minFont + maxFont) / 2;
        }

        // otherwise, calculate font size using the hint in Carmen.
        int diff = maxCount - minCount;
        int x = count - minCount;
        int fontsize = minFont + (x * (maxFont - minFont)) / diff;

        return fontsize;
    }

    /**
     * Writes the HTML header and opening tags.
     *
     * @param html
     *            output stream for html file
     * @param path
     *            path of the input file
     * @param n
     *            number of words in the tag cloud
     */
    private static void htmlHeader(PrintWriter html, String path, int n) {

        String[] header = { "<!DOCTYPE html>", "<html>", "<head>",
                "<title>Top " + n + " words in " + path + "</title>",
                "<link rel=\"stylesheet\" "
                        + "href=\"https://cse22x1.engineering.osu.edu/2231/"
                        + "web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\"",
                "type=\"text/css\">", "<link rel=\"stylesheet\" href=\"tagcloud.css\"",
                "type=\"text/css\">", "</head>", "<body>",
                "<h2>Top " + n + " words in " + path + "</h2>", "<hr>",
                "<div class=\"cdiv\">", "<p class=\"cbox\">" };
        for (int i = 0; i < header.length; i++) {
            html.println(header[i]);
        }
    }

    /**
     * Writes the tag cloud words as span elements.
     *
     * @param html
     *            output stream for html file
     * @param words
     *            SortingMachine of pairs in extraction mode, ordered by key
     * @param minCount
     *            minimum count among selected words
     * @param maxCount
     *            maximum count among selected words
     */
    private static void writeTagCloud(PrintWriter html,
            List<Map.Entry<String, Integer>> words, int minCount, int maxCount) {

        while (words.size() > 0) {
            Map.Entry<String, Integer> p = words.remove(0);
            String word = p.getKey();
            int count = p.getValue();
            int fontSize = getWordSize(count, minCount, maxCount);

            html.println("<span style=\"cursor:default\" class=\"f" + fontSize
                    + "\" title=\"count: " + count + "\">" + word + "</span>");
        }
    }

    /**
     * Writes the closing HTML tags.
     *
     * @param html
     *            output stream for html file
     */
    private static void htmlFooter(PrintWriter html) {
        assert html != null : "Violation of : html is not null";

        html.println("</p>");
        html.println("</div>");
        html.println("</body>");
        html.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {

        // Create scanner
        Scanner in = new Scanner(System.in);

        // Ask the user for the name of an input file
        System.out.print("Enter the input file name: ");
        String inputName = in.nextLine();

        // Ask the user for the name of output file.
        System.out.print("Enter the output file name: ");
        String outputName = in.nextLine();

        // Ask the user for the number of words to be included in the generated tag cloud
        int wordNum = 0;

        // check whether the number is valid or not
        while (wordNum <= 0) {
            System.out.print("Enter the positive number of words to be included in "
                    + "the generated tag cloud: ");
            String line = in.nextLine();
            try {
                wordNum = Integer.parseInt(line);
                if (wordNum <= 0) {
                    System.err.println(
                            "Error: the number must be postive greater than zero");
                }
            } catch (NumberFormatException e) {
                System.err.println(
                        "Error: Please enter the valid integer greater than zero");
            }
        }

        // Count the number of words in input file
        Map<String, Integer> words = new HashMap<>();

        // try-catch statement for input file
        try (BufferedReader fileIn = new BufferedReader(new FileReader(inputName))) {
            wordCount(fileIn, words);
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            in.close();
            return;
        }

        // Select the top N words and compute min/max frequencies among them
        int[] minMax = new int[2];
        List<Map.Entry<String, Integer>> topWords = wordProcess(words, wordNum, minMax);
        int minCount = minMax[0];
        int maxCount = minMax[1];

        // generate HTML
        try (PrintWriter fileOut = new PrintWriter(outputName)) {
            htmlHeader(fileOut, inputName, wordNum);
            writeTagCloud(fileOut, topWords, minCount, maxCount);
            htmlFooter(fileOut);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }

        // Close scanner
        in.close();

    }

}
