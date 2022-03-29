package dev.frendli;

/**
 * The error reporter - reports any errors found. All
 * error reporters must extend the ErrorReporter base class.
 */
public abstract class ErrorReporter {
    private boolean syntaxErrorReported = false;
    private boolean runtimeErrorReported = false;

    public boolean hadSyntaxError() {
        return syntaxErrorReported;
    }

    public boolean hadRuntimeError() {
        return runtimeErrorReported;
    }

    public void syntaxError(int line, String message) {
        report(line, "", message);
        syntaxErrorReported = true;
    }

    public void syntaxError(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, "at the end of the file", message);
        }
        else {
            report(token.line, "at '" + token.lexeme + "'", message);
        }
        syntaxErrorReported = true;
    }

    public void runtimeError(RuntimeError error) {
        report(error.token.line, "", error.getMessage());
        runtimeErrorReported = true;
    }

    public void resetError() {
        syntaxErrorReported = false;
    }

    public abstract void report(int line, String location, String message);

    /**
     * The console error reporter - reports errors to the console.
     */
    public static class Console extends ErrorReporter {
        @Override
        public void report(int line, String location, String message) {
            System.err.println("Error " + location + "\n" +
                    "   Line " + line + " |\t" + message
            );
        }
    }
}
