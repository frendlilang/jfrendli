package dev.frendli;

/**
 * The error reporter - reports any errors found. All
 * error reporters must extend the ErrorReporter base class.
 */
public abstract class ErrorReporter {
    private boolean compileTimeErrorReported = false;
    private boolean runtimeErrorReported = false;

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
        else if (token.type == TokenType.INDENT || token.type == TokenType.DEDENT) {
            location = "at the indentation";
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

    protected abstract void report(int line, String location, String message);

    /**
     * The console error reporter - reports errors to the console.
     */
    public static class Console extends ErrorReporter {
        @Override
        protected void report(int line, String location, String message) {
            System.err.println("Error " + location + "\n" +
                    "   Line " + line + " |\t" + message
            );
            System.out.println();
        }
    }
}
