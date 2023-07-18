package dev.frendli;

/**
 * The console logger - logs messages to the console.
 */
public class ConsoleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println(message);
    }

    public void logSameLine(String message) {
        System.out.print(message);
    }

    public void logError(String message) {
        System.err.println(message);
    }
}
