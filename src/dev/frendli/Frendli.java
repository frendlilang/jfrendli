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
            System.exit(64);    // UNIX sysexits.h
        }
        else if (args.length == 1) {
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        final String REGEX_ALL_NEWLINES = "(\\r\\n)|(\\r)/g";
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()).replaceAll(REGEX_ALL_NEWLINES, "\n"));

        if (reporter.hadSyntaxError()) {
            System.exit(65);    // UNIX sysexits.h
        }
        if (reporter.hadRuntimeError()) {
            System.exit(70);    // UNIX sysexits.h
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

            // Do not kill user's process in interactive mode
            reporter.resetError();
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source, reporter);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, reporter);
        Expression expression = parser.parse();

        // If any syntax errors were found, do not continue interpreting.
        if (reporter.hadSyntaxError()) {
            return;
        }

        interpreter.interpret(expression);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("\tRun a file:");
        System.out.println("\t   java dev.frendli.Frendli <file>");
        System.out.println("\tOr run the interactive prompt:");
        System.out.println("\t   java dev.frendli.Frendli");
    }
}
