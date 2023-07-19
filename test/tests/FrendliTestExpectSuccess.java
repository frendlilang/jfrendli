package tests;

import dev.frendli.Frendli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public abstract class FrendliTestExpectSuccess extends FrendliTest {
    protected String run(String sourceFilePath) {
        assertDoesNotThrow(() -> Frendli.main(new String[]{ toAbsolutePath(sourceFilePath) }));

        return readOutput();
    }
}
