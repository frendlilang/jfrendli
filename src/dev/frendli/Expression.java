package dev.frendli;

import java.util.List;

// All expressions reside here as individual classes inheriting
// from the "Expression" base class. Each subclass must implement
// the "accept" method and call the corresponding "visit" method
// on its visitor.

/**
 * The compile-time representation of an expression (the tree node).
 */
public abstract class Expression {
    public abstract <R> R accept(ExpressionVisitor<R> visitor);

    public static class Binary extends Expression {
        public final Expression left;
        public final Token operator;
        public final Expression right;

        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    public static class Call extends Expression {
        public final Expression callee;
        public final List<Expression> arguments;
        public final Token endToken;

        public Call(Expression callee, List<Expression> arguments, Token endToken) {
            this.callee = callee;
            this.arguments = arguments;
            this.endToken = endToken;
        }

        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitCallExpression(this);
        }
    }

    public static class Grouping extends Expression {
        public final Expression expression;

        public Grouping(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }

    public static class Literal extends Expression {
        public final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    public static class Logical extends Expression {
        public final Expression left;
        public final Token operator;
        public final Expression right;

        public Logical(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }
    }

    public static class Unary extends Expression {
        public final Token operator;
        public final Expression right;

        public Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }

    public static class Variable extends Expression {
        public final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(ExpressionVisitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }
    }
}
