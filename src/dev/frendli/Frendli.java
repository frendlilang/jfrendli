package dev.frendli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Frendli {
    static boolean errorFound = false;

    public static void main(String[] args) {
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

        if (errorFound) {
            System.exit(65);    // UNIX sysexits.h
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.println("> ");
            String line = reader.readLine();
            boolean hasExitedPrompt = (line == null);   // Caused when killed by Ctrl + D
            if (hasExitedPrompt) {
                return;
            }
            run(line);

            // Do not kill user's process in interactive mode
            errorFound = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String location, String message) {
        System.err.println("Error " + location + "\n" +
                line + " |" + "\t" + message
        );
        errorFound = true;
    }

    private static void printUsage() {
        System.out.println("Usage: jfrendli <script>");
    }
}
