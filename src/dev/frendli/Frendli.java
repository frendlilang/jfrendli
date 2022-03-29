package dev.frendli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Frendli {
    private static ErrorReporter reporter = new ErrorReporter.Console();

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

        if (reporter.hadError()) {
            System.exit(65);    // UNIX sysexits.h
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
        parser.parse();

        // If any syntax errors were found, do not continue interpreting.
        if (reporter.hadError()) {
            return;
        }

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    private static void printUsage() {
        System.out.println("Run the compiled program from the src directory:");
        System.out.println("\tjava dev.frendli.Frendli <file>");
    }
}
