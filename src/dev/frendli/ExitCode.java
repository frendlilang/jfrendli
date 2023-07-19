package dev.frendli;

/**
 * Program exit code (UNIX).
 * See sysexits.h: https://www.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+13.1-RELEASE&arch=default&format=html
 */
public enum ExitCode {
    /**
     * The command was used incorrectly, e.g., with the wrong number
     * of arguments, a bad flag, a bad syntax in a parameter, etc.
     */
    USAGE_ERROR (64),

    /**
     * The input data was incorrect in some way. This should only
     * be used for user's data and not system files.
     */
    INPUT_DATA_ERROR (65),

    /**
     * An input file (not a system file) did not exist or was not
     * readable. This could also include errors like "No message"
     * to a mailer (if it cared to catch it).
     */
    INPUT_FILE_ERROR (66),

    /**
     * An internal software error has been detected. This should be
     * limited to non-operating system related errors as possible.
     */
    INTERNAL_SOFTWARE_ERROR (70);

    private int value;

    ExitCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
