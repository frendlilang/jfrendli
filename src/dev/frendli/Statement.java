package dev.frendli;

// All statements reside here as individual classes inheriting
// from the "Statement" base class. Each subclass must implement
// the "accept" method and call the corresponding "visit" method
// on its visitor.

public abstract class Statement {
    public interface Visitor<R> {
        R visitExpressionStatement(Expression statement);
        R visitDisplayStatement(Display statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Expression extends Statement {
        public final Expression expression;

        public Expression(Expression expression) {
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
