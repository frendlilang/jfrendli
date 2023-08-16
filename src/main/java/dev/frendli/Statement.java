package dev.frendli;

import java.util.List;

// All statements reside here as individual classes inheriting
// from the "Statement" base class. Each subclass must implement
// the "accept" method and call the corresponding "visit" method
// on its visitor.

/**
 * The compile-time representation of a statement (the tree node).
 */
public abstract class Statement {
    public abstract <R> R accept(StatementVisitor<R> visitor);

    public static class Block extends Statement {
        public final List<Statement> statements;

        public Block(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
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
        public <R> R accept(StatementVisitor<R> visitor) {
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
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitChangeStatement(this);
        }
    }

    public static class Define extends Statement {
        public final Token name;
        public final List<Token> parameters;
        public final Statement.Block body;

        public Define(Token name, List<Token> parameters, Statement.Block body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitDefineStatement(this);
        }
    }

    // The name of this class also includes "Statement" in order
    // to not conflict with the "Expression" abstract class
    public static class ExpressionStatement extends Statement {
        public final Expression expression;

        public ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    public static class If extends Statement {
        public final Expression condition;
        public final Statement thenBranch;
        public final List<OtherwiseIf> otherwiseIfs;
        public final Statement otherwiseBranch;
        public final Token location;

        public If(Expression condition, Statement thenBranch, List<OtherwiseIf> otherwiseIfs, Statement otherwiseBranch, Token location) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.otherwiseIfs = otherwiseIfs;
            this.otherwiseBranch = otherwiseBranch;
            this.location = location;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }
    }

    // Helper class to If
    public static class OtherwiseIf {
        public final Expression condition;
        public final Statement thenBranch;
        public final Token location;

        public OtherwiseIf(Expression condition, Statement thenBranch, Token location) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.location = location;
        }
    }

    public static class RepeatTimes extends Statement {
        public final Expression times;
        public final Statement body;
        public final Token location;

        public RepeatTimes(Expression times, Statement body, Token location) {
            this.times = times;
            this.body = body;
            this.location = location;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitRepeatTimesStatement(this);
        }
    }

    public static class RepeatWhile extends Statement {
        public final Expression condition;
        public final Statement body;
        public final Token location;

        public RepeatWhile(Expression condition, Statement body, Token location) {
            this.condition = condition;
            this.body = body;
            this.location = location;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitRepeatWhileStatement(this);
        }
    }

    public static class Return extends Statement {
        public final Token location;

        public Return(Token location) {
            this.location = location;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitReturnStatement(this);
        }
    }

    public static class ReturnWith extends Statement {
        public final Expression value;
        public final Token location;

        public ReturnWith(Token location, Expression value) {
            this.value = value;
            this.location = location;
        }

        @Override
        public <R> R accept(StatementVisitor<R> visitor) {
            return visitor.visitReturnWithStatement(this);
        }
    }
}
