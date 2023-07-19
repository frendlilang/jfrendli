package tests;

import dev.frendli.Frendli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public abstract class FrendliTest {
    private final String outputPath = "frendli-test.log";

    protected FrendliTest() {
        Frendli._logToFile(outputPath);
    }

    protected final String readOutput() {
        try (
            var fileReader = new FileReader(new File(outputPath).getAbsolutePath());
            var bufferedReader = new BufferedReader(fileReader)
        ) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        catch (FileNotFoundException e) {
            System.err.println("The file does not exist:");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("There was an error reading the output:");
            e.printStackTrace();
        }

        return "";
    }

    protected final String toAbsolutePath(String sourceFilePath) {
        return new File("test/tests/" + sourceFilePath).getAbsolutePath();
    }
}
