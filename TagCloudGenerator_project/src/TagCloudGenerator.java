import java.util.Comparator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;

/**
 * Write a Java program that generates a tag cloud from a given input text.
 *
 * @author Yoora Choi
 */
public final class TagCloudGenerator {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudGenerator() {
        // no code needed here
    }

    /**
     * Compare key of {Pair<String, Integer>} in alphabetical order. Regardless
     * of case.
     */
    private static final class MapPairKeyLT implements Comparator<Pair<String, Integer>> {
        /**
         * @param o1
         *            the first pair
         * @param o2
         *            the second pair
         * @requires String to be a single word
         *
         * @return a negative integer if o1.key() should appear before o2.key(),
         *         a positive integer if o1.key() should appear after o2.key(),
         *         or zero if the keys are equal ignoring case
         */
        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {

            return o1.key().compareToIgnoreCase(o2.key());
        }
    }

    /**
     * Compare value of {Pair<String, Integer>} in decreasing order.
     *
     */
    private static final class MapPairValueLT
            implements Comparator<Pair<String, Integer>> {

        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {

            return o2.value().compareTo(o1.value());
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
     * Processing through the input file ({@code SimpleReader}) and assigns the
     * word and its occurrences in a {@code Map}.
     *
     * @param fileIn
     *            the input text file ({@code SimpleReader})
     * @param words
     *            the {@code Map} containing all the words and its occurrences
     * @updates {@code words}
     * @ensures <pre>
     * fileIn's words = {@code Map}'s Key(words) and Value(occurrences)
     * </pre>
     */
    private static void wordCount(SimpleReader fileIn, Map<String, Integer> words) {
        assert fileIn.isOpen() : "Violation of : fileIn is open";
        assert words != null : "Violation of : words is not null";

        // Clear Map
        words.clear();

        // Declare sepSet and store separator elements into sepSet
        // using 'defineSeparators' method
        Set<Character> sepSet = new Set1L<Character>();
        defineSeparators(sepSet);

        int index;

        // Using while loop until lines from input has been read
        while (!fileIn.atEOS()) {

            String line = fileIn.nextLine();
            line = line.toLowerCase();
            index = 0;

            while (index < line.length()) {

                // Extract a word or a separator from fileIn
                //using Method 'nextWordOrSeparator'.
                String wordORsep = nextWordOrSeparator(line, index, sepSet);

                if (!sepSet.contains(wordORsep.charAt(0))) {

                    // If a word is extracted and is eligible for a key ,
                    // then store the word as a key and 1 as its occurrences in Map
                    if (!words.hasKey(wordORsep)) {
                        words.add(wordORsep, 1);
                    } else { //Otherwise, just increase value and store it in the map
                        int value = words.value(wordORsep);
                        value++;
                        words.replaceValue(wordORsep, value);
                    }

                }

                index += wordORsep.length();

            }

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
     *            maxCoun
     * @return a SortingMachine containing up to N pairs, which will be ordered
     *         alphabetically by key when changed to extraction mode
     */
    public static SortingMachine<Pair<String, Integer>> wordProcess(
            Map<String, Integer> words, int wordNum, int[] minMax) {

        assert words != null : "Violation of: words is not null";
        assert minMax != null && minMax.length == 2 : "Violation of: minMax has length 2";

        Comparator<Map.Pair<String, Integer>> key = new MapPairKeyLT();
        Comparator<Map.Pair<String, Integer>> value = new MapPairValueLT();

        SortingMachine<Map.Pair<String, Integer>> sortWords = new SortingMachine1L<Map.Pair<String, Integer>>(
                key);

        SortingMachine<Map.Pair<String, Integer>> sortCount = new SortingMachine1L<Map.Pair<String, Integer>>(
                value);

        // Move all pairs from the map into a SortingMachine
        // that sorts them by decreasing count
        while (words.size() != 0) {
            Pair<String, Integer> temp = words.removeAny();
            sortCount.add(temp);
        }
        // Switch sortCount to extraction mode (largest counts come out first)
        sortCount.changeToExtractionMode();

        // Select the top 'wordNum' words while tracking min/max frequencies
        int size = sortCount.size();
        int limit = Math.min(wordNum, size);

        // Initialize min and max using the first selected word
        boolean first = true;
        int min = 0;
        int max = 0;

        for (int i = 0; i < limit; i++) {
            Pair<String, Integer> temp = sortCount.removeFirst();
            int c = temp.value();

            // Update min and max
            if (first) {
                min = c;
                max = c;
                first = false;
            } else {
                if (c < min) {
                    min = c;
                }
                if (c > max) {
                    max = c;
                }
            }
            // Add the selected pair to the alphabetically sorted machine
            sortWords.add(temp);
        }

        if (first) {
            // If no words were selected
            minMax[0] = 0;
            minMax[1] = 0;
        } else {
            minMax[0] = min;
            minMax[1] = max;
        }

        return sortWords;

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
    private static void HtmlHeader(SimpleWriter html, String path, int n) {

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
    private static void writeTagCloud(SimpleWriter html,
            SortingMachine<Pair<String, Integer>> words, int minCount, int maxCount) {

        while (words.size() > 0) {
            Pair<String, Integer> p = words.removeFirst();
            String word = p.key();
            int count = p.value();

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
    private static void HtmlFooter(SimpleWriter html) {
        assert html != null : "Violation of : html is not null";
        assert html.isOpen() : "Violation of : html is open";

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

        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        // Ask the user for the name of an input file
        out.print("Enter the input file name: ");
        String inputName = in.nextLine();
        SimpleReader fileIn = new SimpleReader1L(inputName);

        // Ask the user for the name of output file.
        out.print("Enter the output file name: ");
        String outputName = in.nextLine();
        SimpleWriter fileOut = new SimpleWriter1L(outputName);

        // Ask the user for the number of words to be included in the generated tag cloud
        out.print("Enter the positive number of words to be included in "
                + "the generated tag cloud: ");
        int wordNum = in.nextInteger();

        // Count the number of words in input file
        Map<String, Integer> words = new Map1L<String, Integer>();
        wordCount(fileIn, words);

        // Select the top N words and compute min/max frequencies among them
        int[] minMax = new int[2]; // [0] = minCount, [1] = maxCount
        SortingMachine<Pair<String, Integer>> topWords = wordProcess(words, wordNum,
                minMax);
        int minCount = minMax[0];
        int maxCount = minMax[1];

        // Change to extraction mode to remove pairs in alphabetical order
        topWords.changeToExtractionMode();

        // generate HTML
        HtmlHeader(fileOut, inputName, wordNum);
        writeTagCloud(fileOut, topWords, minCount, maxCount);
        HtmlFooter(fileOut);

        // Close input and output streams
        in.close();
        out.close();
        fileIn.close();
        fileOut.close();
    }

}
