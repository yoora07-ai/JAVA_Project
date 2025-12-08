import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Project name: Word Counter
 *
 * Write a Java program that counts word occurrences in a given input file and
 * outputs an HTML document with a table of the words and counts listed in
 * alphabetical order.
 *
 * @author Yoora Choi
 *
 */
public final class WordCounter {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private WordCounter() {
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
     * For internal purpose: Compare {@code String}s in lexicographic order.
     * Regardless of case.
     */
    private static class StringLT implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {

            return s1.compareToIgnoreCase(s2);
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
     * Store keys in {@code words} in the {@code queue}.
     *
     * @param words
     *            store words(key) and its occurrences(value) in {@code Map}
     * @return keyQueue store words(key) in {@code queue}
     */
    private static Queue<String> keysInQueue(Map<String, Integer> words) {
        Map<String, Integer> tempMap = new Map1L<>();
        Queue<String> keyQueue = new Queue1L<>();
        tempMap.transferFrom(words);

        while (tempMap.size() != 0) {
            Map.Pair<String, Integer> pair = tempMap.removeAny();
            String key = pair.key();
            int value = pair.value();
            keyQueue.enqueue(key);
            words.add(key, value);
        }

        return keyQueue;
    }

    /**
     * Create HTML page from words and its occurrences in {@code Map}.
     *
     * @param outFile
     *            the input text file read in by {@code SimpleWriter}
     * @param words
     *            store words and its occurrences using {@code Map}
     * @param keyQueue
     *            store keys of{@code Queue}
     * @param inFile
     *            the input text file read in by {@code SimpleReader}
     */
    public static void htmlCreator(SimpleWriter outFile, Map<String, Integer> words,
            Queue<String> keyQueue, SimpleReader inFile) {

        Map<String, Integer> temp = words.newInstance();
        temp.transferFrom(words);

        /*
         * Sorting words in the ascending order
         */
        Comparator<String> sortingAsc = new StringLT();
        keyQueue.sort(sortingAsc);

        // Head of HTML web site
        outFile.println("<html>");
        outFile.println("<head>");
        outFile.println("<title>Words Counted in " + inFile.name() + "</title>");
        outFile.println("</head>");

        // Table
        outFile.println("<body>");
        outFile.println("<h1>Words Counted in " + inFile.name() + "</h1>");
        outFile.println("<table border =\"1\">");

        outFile.println("<tr>");
        outFile.println("<th>Words</th>");
        outFile.println("<th>Counts</th>");
        outFile.println("</tr>");

        // Adding rows in the table
        while (temp.size() > 0) {
            Map.Pair<String, Integer> tempPair = temp.remove(keyQueue.dequeue());
            outFile.println("<tr>");
            outFile.println("<td>" + tempPair.key() + "</td>");
            outFile.println("<td>" + tempPair.value() + "</td>");
            outFile.println("</tr>");
        }

        // close
        outFile.println("</table>");
        outFile.println("</body>");
        outFile.println("</html>");

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        Map<String, Integer> words = new Map1L<String, Integer>();

        // Ask the user for the name of an input file
        out.print("Enter the input file name: ");
        SimpleReader fileIn = new SimpleReader1L(in.nextLine());

        // Ask the user for the name of output file.
        out.print("Enter the output file name: ");
        SimpleWriter fileOut = new SimpleWriter1L(in.nextLine());

        // Count the number of words
        wordCount(fileIn, words);

        // Declare keys
        Queue<String> keys = keysInQueue(words);

        // Export HTML
        htmlCreator(fileOut, words, keys, fileIn);

        // Close input and output streams
        in.close();
        out.close();
        fileIn.close();
        fileOut.close();

    }

}
