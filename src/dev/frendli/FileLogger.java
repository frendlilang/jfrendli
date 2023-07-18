package dev.frendli;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The file logger - logs messages to a file.
 * The content of the output file will be cleared on instantiation.
 */
public class FileLogger implements Logger {
    private String outputPath = "frendli-default.log";

    public FileLogger() {
        clearFile();
    }

    public FileLogger(String outputPath) {
        this.outputPath = outputPath;
        clearFile();
    }

    @Override
    public void log(String message) {
        try (var fileWriter = new FileWriter(outputPath, true)) {
            fileWriter.write(message + "\n");
        }
        catch (IOException error) {
            System.err.println("There was an error writing to the log file:");
            error.printStackTrace();
        }
    }

    private void clearFile() {
        try (var fileWriter = new FileWriter(outputPath)) {
            fileWriter.write("");
        }
        catch (IOException error) {
            System.err.println("There was an error clearing the log file:");
            error.printStackTrace();
        }
    }
}
