package dev.frendli;

import java.util.List;

// ========
// GRAMMAR: (incrementally added to and modified)
// ========
// expression:          equality ;
// equality:            comparison ( ( "not"? "equals" ) comparison )* ;
// comparison:          term ( ( "<" | "<=" | ">" | ">=" ) term )* ;
// term:                factor ( ( "+" | "-" ) factor )* ;
// factor:              unary ( ( "*" | "/" ) unary )* ;
// unary:               ( "not" | "-" ) unary | primary ;
// primary:             NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final ErrorReporter reporter;
    private final List<Token> tokens;
    private int current = 0;

    public Parser (List<Token> tokens, ErrorReporter reporter) {
        this.tokens = tokens;
        this.reporter = reporter;
    }

    /**
     * Parse the tokens.
     *
     * @return The resulting expression.
     */
    public Expression parse() {
        // Catch parse errors here as the point being
        // synchronized to in the Java call stack.
        try {
            return expression();
        }
        catch (ParseError error) {
            return null;
        }
    }

    // expression: equality ;
    private Expression expression() {
        return equality();
    }

    // equality: comparison ( ( "not"? "equals" ) comparison )* ;
    private Expression equality() {
        Expression left = comparison();

        while (match(TokenType.NOT, TokenType.EQUALS_WORD)) {
            Token operator = getJustConsumed();
            if (operator.type == TokenType.NOT) {
                consume(TokenType.EQUALS_WORD, "'not' must be followed by 'equals' when comparing equality.");
                operator = new Token(TokenType.NOT_EQUALS, "not equals", null, operator.line);
            }
            Expression right = comparison();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // comparison: term ( ( "<" | "<=" | ">" | ">=" ) term )* ;
    private Expression comparison() {
        Expression left = term();

        while (match(TokenType.LESS_THAN, TokenType.LESS_THAN_EQUALS,
                TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUALS)) {
            Token operator = getJustConsumed();
            Expression right = term();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // term: factor ( ( "+" | "-" ) factor )* ;
    private Expression term() {
        Expression left = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = getJustConsumed();
            Expression right = factor();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // factor: unary ( ( "*" | "/" ) unary )* ;
    private Expression factor() {
        Expression left = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = getJustConsumed();
            Expression right = unary();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    // unary: ( "not" | "-" ) unary | primary ;
    private Expression unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = getJustConsumed();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    // primary: NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;
    private Expression primary() {
        if (match(TokenType.NUMBER, TokenType.TEXT)) {
            return new Expression.Literal(getJustConsumed().literal);
        }
        if (match(TokenType.TRUE)) {
            return new Expression.Literal(true);
        }
        if (match(TokenType.FALSE)) {
            return new Expression.Literal(false);
        }
        if (match(TokenType.EMPTY)) {
            return new Expression.Literal(null);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression expression = expression();
            consume(TokenType.CLOSE_PAREN, "A closing parenthesis ')' is missing.");
            return new Expression.Grouping(expression);
        }

        // If this is reached, the current token is not the start
        // of an expression. Hence, an error is thrown to synchronize
        // the parser's state using Java's call stack, catching the
        // exception where it's being synchronized to.
        throw error(peek(), "Cannot find a valid expression.");
    }

    /**
     * Advance to the next token.
     *
     * @return The consumed token.
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return getJustConsumed();
    }

    /**
     * Consume the current token if it is of the expected type,
     * otherwise report an error.
     *
     * @param type The expected type.
     * @param errorMessage The error message if unexpected type.
     * @return The consumed token.
     */
    private Token consume(TokenType type, String errorMessage) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), errorMessage);
    }

    /**
     * Check if the current unconsumed token is of any of the types
     * provided and consume it if it is.
     *
     * @param types The types to check for.
     * @return Whether it matches any of the types.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the current unconsumed token is of a certain type
     * without consuming it.
     *
     * @param type The type to check for.
     * @return Whether it matches the type.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }

        return type == peek().type;
    }

    /**
     * Look ahead at the current unconsumed token without consuming it.
     *
     * @return The current unconsumed token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Look at the most recently consumed token.
     *
     * @return The previous token.
     */
    private Token getJustConsumed() {
        return tokens.get(current - 1);
    }

    /**
     * Check if there are no more tokens to be parsed.
     *
     * @return Whether all tokens have been parsed.
     */
    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    /**
     * Report a parse error.
     *
     * @param token The token that caused the error.
     * @param message The error message.
     * @return The ParseError.
     */
    private ParseError error(Token token, String message) {
        reporter.report(token, message);

        // Let the caller decide what to do with the error,
        // as synchronization may not be needed for all errors.
        // Thus don't throw the error here.
        return new ParseError();
    }

    /**
     * Synchronize the tokens to the next statement.
     * (Prevents cascaded errors deriving from an original
     * error to be falsely reported.)
     */
    private void synchronize() {
        advance();

        // Advance to the next token until the start of the
        // next statement likely has been reached.
        while (!isAtEnd() && !isAtStartOfStatement(peek())) {
            advance();
        }
    }

    /**
     * Check if a given token is the start of a statement.
     *
     * @param token The token.
     * @return Whether it is a start token.
     */
    private boolean isAtStartOfStatement(Token token) {
        switch (token.type) {
            case CHANGE:
            case CREATE:
            case DEFINE:
            case DESCRIBE:
            case DISPLAY:     // TEMPORARY (until built-in function)
            case HAS:
            case IF:
            case REPEAT:
            case RETURN:
                return true;
            default:
                return false;
        }
    }
}
