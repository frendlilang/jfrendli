package dev.frendli;

import java.util.List;

// All native functions reside here as individual classes
// inheriting from the "NativeFunction" base class. Each
// subclass must implement the "arity" and "call" methods
// declared in the FrendliCallable interface.

public abstract class NativeFunction implements FrendliCallable {
    /**
     * Native function for getting the number of milliseconds since the epoch.
     */
    public static class Time extends NativeFunction {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            return (double)System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "<native function: time>";
        }
    }

    /**
     * Native function for outputting a string representation to the user.
     */
    public static class Display extends NativeFunction {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            print(stringify(arguments.get(0)));

            return null;
        }

        @Override
        public String toString() {
            return "<native function: display>";
        }

        /**
         * Convert a value to the Frendli representation.
         *
         * @param value The value to convert.
         * @return The Frendli value.
         */
        private String stringify(Object value) {
            if (value == null) {
                return "empty";
            }
            if (value instanceof Double) {
                // Even though all numbers are treated as doubles,
                // show integers without the decimal point.
                String text = value.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                return text;
            }

            return value.toString();
        }

        private void print(String value) {
            System.out.println(value);
        }
    }
}
