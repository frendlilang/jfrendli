package dev.frendli;

/**
 * The error reporter - reports any errors found.
 */
public class ErrorReporter {
    private final Logger logger;
    private boolean compileTimeErrorReported = false;
    private boolean runtimeErrorReported = false;

    public ErrorReporter(Logger logger) {
        this.logger = logger;
    }

    public boolean hadCompileTimeError() {
        return compileTimeErrorReported;
    }

    public boolean hadRuntimeError() {
        return runtimeErrorReported;
    }

    public void compileTimeError(int line, String message) {
        report(line, "", message);
        compileTimeErrorReported = true;
    }

    public void compileTimeError(Token token, String message) {
        String location = "at '" + token.lexeme + "'";
        if (token.type == TokenType.EOF) {
            location = "at the end of the file";
        }
        else if (token.type == TokenType.NEWLINE) {
            location = "at the end of the line";
        }
        else if (token.type == TokenType.INDENT) {
            location = "at the indentation";
        }
        else if (token.type == TokenType.DEDENT) {
            location = "at the decrease of indentation";
        }
        report(token.line, location, message);
        compileTimeErrorReported = true;
    }

    public void runtimeError(RuntimeError error) {
        report(error.token.line, "at '" + error.token.lexeme + "'", error.getMessage());
        runtimeErrorReported = true;
    }

    public void reset() {
        compileTimeErrorReported = false;
        runtimeErrorReported = false;
    }

    private void report(int line, String location, String message) {
        logger.log(format(line, location, message));
    }

    private String format(int line, String location, String message) {
        return "Error\n" +
                "  > Where: \n" +
                "     > Line " + line + " " + location + "\n" +
                "  > Message: \n" +
                "     > " + message + "\n\n";
    }
}
