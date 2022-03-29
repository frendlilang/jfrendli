package dev.frendli;

/**
 * The error reporter - reports any errors found. All
 * error reporters must extend the ErrorReporter base class.
 */
public abstract class ErrorReporter {
    private boolean errorReported = false;

    public boolean hadError() {
        return errorReported;
    }

    public void report(int line, String message) {
        report(line, "", message);
        errorReported = true;
    }

    public void report(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, "at the end of the file", message);
        }
        else {
            report(token.line, "at '" + token.lexeme + "'", message);
        }
        errorReported = true;
    }

    public void resetError() {
        errorReported = false;
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
