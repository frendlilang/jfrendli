package dev.frendli;

// All statements reside here as individual classes inheriting
// from the "Statement" base class. Each subclass must implement
// the "accept" method and call the corresponding "visit" method
// on its visitor.

public abstract class Statement {
    public interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitDisplayStatement(Display statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    // This name of this class also includes "Statement" in order
    // to not conflict with the "Expression" abstract class
    public static class ExpressionStatement extends Statement {
        public final Expression expression;

        public ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    public static class Display extends Statement {
        public final Expression expression;

        public Display(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDisplayStatement(this);
        }
    }
}
