package tests;

import java.security.Permission;

/**
 * Manager for intercepting calls to `System.exit()` made by
 * the Frendli program to allow tests to continue running.
 *
 * TODO: `SecurityManager` is deprecated, implement an alternative.
 *       (see https://openjdk.org/jeps/411)
 */
public class SystemExitInterceptor extends SecurityManager {
    public class SystemExitInterceptException extends SecurityException {
        public final int status;

        private SystemExitInterceptException(final int status) {
            this.status = status;
        }
    }

    private final SecurityManager originalSecurityManager;

    public SystemExitInterceptor(final SecurityManager originalSecurityManager) {
        this.originalSecurityManager = originalSecurityManager;
    }

    @Override
    public void checkPermission(final Permission permission) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(permission);
        }
    }

    @Override
    public void checkPermission(final Permission permission, final Object context) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(permission, context);
        }
    }

    @Override
    public void checkExit(final int status) {
        super.checkExit(status);
        throw new SystemExitInterceptException(status);
    }
}
