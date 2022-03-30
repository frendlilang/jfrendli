package dev.frendli;

import java.util.ArrayList;
import java.util.List;

// ========
// GRAMMAR: (incrementally added to and modified)
// ========
// file:                declaration* EOF ;
// declarationStmt:     variableDecl
//                      | statement ;
// variableDecl:        "create" IDENTIFIER "=" expression NEWLINE ;
// statement:           displayStmt
//                      | expressionStmt ;
// displayStmt:         "display" expression NEWLINE ;
// expressionStmt:      expression NEWLINE ;
// expression:          equality ;
// equality:            comparison ( ( "not"? "equals" ) comparison )* ;
// comparison:          term ( ( "<" | "<=" | ">" | ">=" ) term )* ;
// term:                factor ( ( "+" | "-" ) factor )* ;
// factor:              unary ( ( "*" | "/" ) unary )* ;
// unary:               ( "not" | "-" ) unary | primary ;
// primary:             IDENTIFIER | NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;

/**
 * The parser - traverses the tokens produced by the scanner and
 * maps them to rules in the grammar to form an abstract syntax tree,
 * as well as detecting and reporting parse errors.
 * (It performs recursive descent/top-down parsing, starting with
 * the lowest-precedence.)
 */
public class Parser {
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
     * @return The statement syntax trees.
     */
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    // declarationStmt: variableDecl
    //                  | statement ;
    private Statement declaration() {
        try {
            if (match(TokenType.CREATE)) {
                return variableDeclaration();
            }

            return statement();
        }
        // Catch parse errors here as the point being synchronized
        // to in the Java call stack when needed. This method is
        // suitable to synchronize to since it always gets called
        // for each statement in the code.
        catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    // variableDecl: "create" IDENTIFIER "=" expression NEWLINE ;
    private Statement variableDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "A name for what is created must be provided.");
        consume(TokenType.EQUALS_SIGN, "'" + name.lexeme + "' must be initialized using '='. You may set it to 'empty' if needed.");
        Expression initializer = expression();
        consumeNewline();

        return new Statement.Create(name, initializer);
    }

    // statement: displayStmt
    //            | expressionStmt ;
    private Statement statement() {
        if (match(TokenType.DISPLAY)) {
            return displayStatement();
        }

        return expressionStatement();
    }

    // displayStmt: "display" expression NEWLINE ;
    private Statement displayStatement() {
        Expression value = expression();
        consumeNewline();

        return new Statement.Display(value);
    }

    // expressionStmt: expression NEWLINE ;
    private Statement expressionStatement() {
        Expression expression = expression();
        consumeNewline();

        return new Statement.ExpressionStatement(expression);
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

    // primary: IDENTIFIER | NUMBER | TEXT | "true" | "false" | "empty" | "(" expression ")" ;
    private Expression primary() {
        if (match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(getJustConsumed());
        }
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
     * Consume the current token if it is a newline, otherwise
     * report an error.
     *
     * @return The consumed newline token.
     */
    private Token consumeNewline() {
        return consume(TokenType.NEWLINE, "Expected a new line.");
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
        reporter.syntaxError(token, message);

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
