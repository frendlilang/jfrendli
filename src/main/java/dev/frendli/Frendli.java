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
    private static final ConsoleLogger usageLogger = new ConsoleLogger();
    private static ErrorReporter reporter = new ErrorReporter(new ConsoleLogger());
    // The interpreter is static to allow the user's session in the interactive
    // prompt to keep using the same interpreter without creating a new one.
    private static Interpreter interpreter = new Interpreter(reporter, new ConsoleLogger());

    public static void main(String[] args) throws IOException, SecurityException {
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

    private static void runFile(String path) throws IOException, SecurityException {
        verifyExtension(path);

        final String REGEX_ALL_NEWLINES = "(\\r\\n)|(\\r)/g";
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, Charset.defaultCharset());
        run(source.replaceAll(REGEX_ALL_NEWLINES, "\n"));

        if (reporter.hadCompileTimeError()) {
            usageLogger.log("Exiting");
            System.exit(ExitCode.INPUT_DATA_ERROR.getValue());
        }
        if (reporter.hadRuntimeError()) {
            System.exit(ExitCode.INTERNAL_SOFTWARE_ERROR.getValue());
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        usageLogger.log("\nHowdy! Welcome to the Frendli interactive prompt!\n");
        usageLogger.log("Go ahead and enter one line of code to be executed.");

        while (true) {
            usageLogger.logSameLine("> ");
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
        List<Token> tokens = scanner.scan();
        Parser parser = new Parser(tokens, reporter);
        List<Statement> statements = parser.parse();

        // If any syntax errors were found, do not continue resolving.
        if (reporter.hadCompileTimeError()) {
            return;
        }

        // The resolver inserts the resolved data directly into the interpreter.
        Resolver resolver = new Resolver(interpreter, reporter);
        resolver.resolve(statements);

        // If any resolution errors were found, do not continue interpreting.
        if (reporter.hadCompileTimeError()) {
            return;
        }

        interpreter.interpret(statements);
    }

    private static void verifyExtension(String path) throws SecurityException {
        if (!path.toLowerCase().endsWith(".frendli")) {
            usageLogger.logError("Frendli only understands files with extension '.frendli'");
            System.exit(ExitCode.INPUT_FILE_ERROR.getValue());
        }
    }

    private static void printUsage() {
        usageLogger.log("""
                Usage: java dev.frendli.Frendli [path]
                
                    The REPL (interactive prompt) starts if no [path] is provided
                """);
    }

    /**
     * Log output and runtime/comptime Frendli errors to a default
     * file (frendli-default.log). The file will be overwritten for
     * each program run. (Used primarily for the tests.)
     */
    public static void _logToFile() {
        setLogger(new FileLogger());
    }

    /**
     * Log output and runtime/comptime Frendli errors to a file.
     * The file will be overwritten for each program run.
     * (Used primarily for the tests.)
     *
     * @param outputPath The path to the file to log to.
     */
    public static void _logToFile(String outputPath) {
        setLogger(new FileLogger(outputPath));
    }

    /**
     * Log output and runtime/comptime Frendli errors to the console.
     * This is the default behavior.
     */
    public static void _logToConsole() {
        setLogger(new ConsoleLogger());
    }

    private static void setLogger(Logger logger) {
        reporter = new ErrorReporter(logger);
        interpreter = new Interpreter(reporter, logger);
    }
}
