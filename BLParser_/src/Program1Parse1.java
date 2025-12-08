import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Put your name here
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to the block string that is the body of
     *          the instruction string at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens, Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION")
                : "" + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";

        // Consume "INSTRUCTION"
        tokens.dequeue();

        // Get instruction name
        String instrName = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(instrName), "Error: \""
                + instrName + "\" is not a valid BL identifier for an instruction name.");

        // Check not a primitive instruction name
        final String[] primitiveNames = { "move", "turnleft", "turnright", "infect",
                "skip" };
        for (String prim : primitiveNames) {
            Reporter.assertElseFatalError(!prim.equals(instrName),
                    "Error: instruction name \"" + instrName
                            + "\" must not be a primitive instruction name.");
        }

        // Expect "IS"
        Reporter.assertElseFatalError(tokens.front().equals("IS"),
                "Syntax error: expected \"IS\" after instruction name \"" + instrName
                        + "\".");
        tokens.dequeue(); // IS

        // Parse body block into body using Statement1.parseBlock
        body.parseBlock(tokens);

        // Expect "END"
        Reporter.assertElseFatalError(tokens.front().equals("END"),
                "Syntax error: expected \"END\" to terminate instruction \"" + instrName
                        + "\".");
        tokens.dequeue(); // END

        // Ending name must match instruction name
        String endName = tokens.dequeue();
        Reporter.assertElseFatalError(endName.equals(instrName),
                "Error: instruction ending name \"" + endName
                        + "\" must match beginning name \"" + instrName + "\".");

        return instrName;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0
                : "" + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";

        // PROGRAM
        Reporter.assertElseFatalError(tokens.front().equals("PROGRAM"),
                "Syntax error: expected \"PROGRAM\" at start of program.");
        tokens.dequeue(); // PROGRAM

        // Program name
        String programName = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(programName), "Error: \""
                + programName + "\" is not a valid BL identifier for program name.");

        // IS
        Reporter.assertElseFatalError(tokens.front().equals("IS"),
                "Syntax error: expected \"IS\" after program name \"" + programName
                        + "\".");
        tokens.dequeue(); // IS

        // Create empty context
        Map<String, Statement> ctxt = this.newContext();

        // Zero or more INSTRUCTION definitions
        while (tokens.front().equals("INSTRUCTION")) {
            Statement instrBody = this.newBody();
            String instrName = parseInstruction(tokens, instrBody);

            // Each user-defined instruction name must be unique
            Reporter.assertElseFatalError(!ctxt.hasKey(instrName),
                    "Error: duplicate instruction name \"" + instrName
                            + "\". Each user-defined instruction must be unique.");

            ctxt.add(instrName, instrBody);
        }

        // BEGIN
        Reporter.assertElseFatalError(tokens.front().equals("BEGIN"),
                "Syntax error: expected \"BEGIN\" before program body.");
        tokens.dequeue(); // BEGIN

        // Parse program body block
        Statement body = this.newBody();
        body.parseBlock(tokens);

        // END
        Reporter.assertElseFatalError(tokens.front().equals("END"),
                "Syntax error: expected \"END\" at end of program body.");
        tokens.dequeue(); // END

        // Ending program name must match beginning program name
        String endProgramName = tokens.dequeue();
        Reporter.assertElseFatalError(endProgramName.equals(programName),
                "Error: program ending name \"" + endProgramName
                        + "\" must match beginning program name \"" + programName
                        + "\".");

        // Must be at END_OF_INPUT now
        Reporter.assertElseFatalError(tokens.front().equals(Tokenizer.END_OF_INPUT),
                "Syntax error: extra tokens after end of program; expected END-OF-"
                        + "INPUT but found \"" + tokens.front() + "\".");

        // Replace distinguished parameter this
        this.setName(programName);
        this.swapContext(ctxt);
        this.swapBody(body);

    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
