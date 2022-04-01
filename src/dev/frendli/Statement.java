package dev.frendli;

// All statements reside here as individual classes inheriting
// from the "Statement" base class. Each subclass must implement
// the "accept" method and call the corresponding "visit" method
// on its visitor.

import java.util.List;

public abstract class Statement {
    public interface Visitor<R> {
        R visitBlockStatement(Block statement);
        R visitCreateStatement(Create statement);
        R visitChangeStatement(Change statement);
        R visitDisplayStatement(Display statement);
        R visitExpressionStatement(ExpressionStatement statement);
        R visitIfStatement(If statement);
        R visitRepeatWhileStatement(RepeatWhile statement);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Block extends Statement {
        public final List<Statement> statements;

        public Block(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }
    }

    public static class Create extends Statement {
        public final Token name;
        public final Expression initializer;

        public Create(Token name, Expression initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCreateStatement(this);
        }
    }

    public static class Change extends Statement {
        public final Token name;
        public final Expression assignment;

        public Change(Token name, Expression assignment) {
            this.name = name;
            this.assignment = assignment;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitChangeStatement(this);
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

    public static class If extends Statement {
        public final Token start;   // For reporting location of error if condition is not a boolean
        public final Expression condition;
        public final Block thenBranch;
        public final Block otherwiseBranch;

        public If(Token start, Expression condition, Block thenBranch, Block otherwiseBranch) {
            this.start = start;
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.otherwiseBranch = otherwiseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }
    }

    public static class RepeatWhile extends Statement {
        Expression condition;
        Block body;

        public RepeatWhile(Expression condition, Block body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeatWhileStatement(this);
        }
    }
}
