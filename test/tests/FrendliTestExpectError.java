package tests;

import dev.frendli.ExitCode;
import dev.frendli.Frendli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class FrendliTestExpectError extends FrendliTest {
    // TODO: `SecurityManager` is deprecated, see `SystemExitInterceptor` class.
    private SecurityManager originalSecurityManager;

    @BeforeEach
    void interceptExitProcess() {
        originalSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new SystemExitInterceptor(originalSecurityManager));
    }

    @AfterEach
    void resetExitProcess() {
        System.setSecurityManager(originalSecurityManager);
    }

    protected String runExpectComptimeError(String sourceFilePath) {
        return runExpectError(sourceFilePath, ExitCode.INPUT_DATA_ERROR);
    }

    protected String runExpectRuntimeError(String sourceFilePath) {
        return runExpectError(sourceFilePath, ExitCode.INTERNAL_SOFTWARE_ERROR);
    }

    private String runExpectError(String sourceFilePath, ExitCode exitCode) {
        assertExits(
            exitCode.getValue(),
            () -> Frendli.main(new String[]{ toAbsolutePath(sourceFilePath) })
        );

        return readOutput();
    }

    private void assertExits(int expectedStatus, Executable executable) {
        try {
            executable.execute();
            fail("Expected system to exit with status code " + expectedStatus + ", but the program ran successfully.");
        }
        catch (SystemExitInterceptor.SystemExitInterceptException e) {
            assertEquals(expectedStatus, e.status, "System exited with unexpected status.");
        }
        catch (Throwable e) {
            e.printStackTrace();
            fail("System exited with unexpected exception: " + e.getMessage());
        }
    }
}
