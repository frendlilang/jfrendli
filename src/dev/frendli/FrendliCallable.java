package dev.frendli;

import java.util.List;

/**
 * Interface that should be implemented by any callable object.
 */
public interface FrendliCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
