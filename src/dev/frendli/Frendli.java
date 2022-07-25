package dev.frendli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * The main class of jfrendli.
 */
public class Frendli {
    private static final ErrorReporter reporter = new ErrorReporter.Console();
    // The interpreter is static to allow the user's session in the interactive
    // prompt to keep using the same interpreter without creating a new one.
    private static final Interpreter interpreter = new Interpreter(reporter);

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            printUsage();
            System.exit(ExitCode.USAGE_ERROR.getValue());
        }
        else if (args.length == 1) {
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        verifyExtension(path);

        final String REGEX_ALL_NEWLINES = "(\\r\\n)|(\\r)/g";
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, Charset.defaultCharset());
        run(source.replaceAll(REGEX_ALL_NEWLINES, "\n"));

        if (reporter.hadCompileTimeError()) {
            System.exit(ExitCode.INPUT_DATA_ERROR.getValue());
        }
        if (reporter.hadRuntimeError()) {
            System.exit(ExitCode.INTERNAL_SOFTWARE_ERROR.getValue());
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("\nHowdy! Welcome to the Frendli interactive prompt!\n");
        System.out.println("Go ahead and enter one line of code to be executed.");

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            boolean hasExitedPrompt = (line == null);   // Caused when killed by Ctrl + D
            if (hasExitedPrompt) {
                return;
            }
            run(line);

            // Do not kill user's process in interactive mode.
            reporter.reset();
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source, reporter);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, reporter);
        List<Statement> statements = parser.parse();

        // If any syntax errors were found, do not continue resolving.
        if (reporter.hadCompileTimeError()) {
            return;
        }

        Resolver resolver = new Resolver(interpreter, reporter);
        resolver.resolve(statements);

        // If any resolution errors were found, do not continue interpreting.
        if (reporter.hadCompileTimeError()) {
            return;
        }

        interpreter.interpret(statements);
    }

    private static void verifyExtension(String path) {
        if (!path.toLowerCase().endsWith(".frendli")) {
            System.out.println("Frendli only understands files with extension '.frendli'");
            System.exit(ExitCode.INPUT_FILE_ERROR.getValue());
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("\tRun a file:");
        System.out.println("\t   java dev.frendli.Frendli <file>");
        System.out.println("\tOr run the interactive prompt:");
        System.out.println("\t   java dev.frendli.Frendli");
    }
}
