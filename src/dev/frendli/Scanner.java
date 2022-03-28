package dev.frendli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Map<String, TokenType> keywords = new HashMap<>();
    private int start = 0;              // first character of current lexeme being scanned
    private int current = 0;            // current character of current lexeme being scanned
    private int line = 0;               // current line in source file (for error reporting)
    private boolean isAtStartOfLine = true;
    private boolean isBlankLine = false;

    public Scanner(String source) {
        this.source = source;
        fillKeywords();
    }

    private void fillKeywords() {
        keywords.put("accept", TokenType.ACCEPT);
        keywords.put("and", TokenType.AND);
        keywords.put("change", TokenType.CHANGE);
        keywords.put("create", TokenType.CREATE);
        keywords.put("define", TokenType.DEFINE);
        keywords.put("describe", TokenType.DESCRIBE);
        keywords.put("empty", TokenType.EMPTY);
        keywords.put("equals", TokenType.EQUALS_WORD);
        keywords.put("false", TokenType.FALSE);
        keywords.put("has", TokenType.HAS);
        keywords.put("if", TokenType.IF);
        keywords.put("inherit", TokenType.INHERIT);
        keywords.put("me", TokenType.ME);
        keywords.put("new", TokenType.NEW);
        keywords.put("not", TokenType.NOT);
        keywords.put("or", TokenType.OR);
        keywords.put("otherwise", TokenType.OTHERWISE);
        keywords.put("parent", TokenType.PARENT);
        keywords.put("repeat", TokenType.REPEAT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("send", TokenType.SEND);
        keywords.put("times", TokenType.TIMES);
        keywords.put("true", TokenType.TRUE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("with", TokenType.WITH);
    }

    /**
     * Scan the source code for tokens.
     *
     * @return List of tokens.
     */
    public List<Token> scanTokens() {
        // As long as the end of the file has not been reached, keep
        // scanning token by token, resetting the start position of the
        // lexeme to the first character in the next one to be scanned.
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));

        return tokens;
    }

    /**
     * Scan the current lexeme for a token.
     */
    private void scanToken() {
        char currentCharacter = advance();
        switch (currentCharacter) {
            case '\n':
                if (!isBlankLine) {
                    System.out.println("NEWLINE");      // TEMP: DEBUGGING
                    addToken(TokenType.NEWLINE);
                }
                line++;
                isAtStartOfLine = true;
                break;
            case '(':
                addToken(TokenType.OPEN_PAREN);
                break;
            case ')':
                addToken(TokenType.CLOSE_PAREN);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '=':
                addToken(TokenType.EQUALS_SIGN);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '>':
                addToken(match('=')
                    ? TokenType.GREATER_THAN_EQUALS
                    : TokenType.GREATER_THAN);
                break;
            case '<':
                addToken(match('=')
                    ? TokenType.LESS_THAN_EQUALS
                    : TokenType.LESS_THAN);
                break;
            case '/':
                if (match('/')) {
                    skipUntilEndOfLine();
                }
                else {
                    addToken(TokenType.SLASH);
                }
                break;
            // Ignore whitespace and tabs that are
            // not at the beginning of the lines
            case ' ':
            case '\t':
                break;
            case '"':
                text();
                break;
            default:
                // Check digits and alphas here instead
                // of in individual cases
                if (isDigit(currentCharacter)) {
                    number();
                }
                else if (isAlpha(currentCharacter)) {
                    identifier();
                }
                else {
                    Frendli.error(line, "Found unexpected character " + currentCharacter);
                }
                break;
        }
    }

    /**
     * Consume the current number.
     */
    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        // Only consume the dot if there are succeeding digits.
        // (since it may otherwise be a method call)
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the dot (.) then all following digits
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }

        double literal = Double.parseDouble(source.substring(start, current));
        addToken(TokenType.NUMBER, literal);
    }

    /**
     * Consume the current identifier.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // If the literal matches one of the reserved keywords, the token
        // type will be that of the keyword, otherwise a regular identifier
        String literal = source.substring(start, current);
        TokenType type = keywords.getOrDefault(literal, TokenType.IDENTIFIER);
        addToken(type);
    }

    /**
     * Consume the current text literal.
     */
    private void text() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                Frendli.error(line++, "Found a newline in text. Text cannot contain newline characters.");
            }
            advance();
        }

        if (isAtEnd()) {
            Frendli.error(line, "Text is not terminated. Text must be terminated by a \"");
            return;
        }

        // Consume the terminating double quote (")
        advance();

        // Remove double quotes from text literal
        String literal = source.substring(start + 1, current - 1);
        addToken(TokenType.TEXT, literal);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Create a token from the current lexeme and add it to
     * the list of tokens.
     *
     * @param type The type of the token.
     * @param literal The literal of the token type (e.g. "Hello World").
     */
    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    /**
     * Consume the current character (advance the pointer).
     *
     * @return The consumed character.
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Check if the current unconsumed character matches an expected
     * character and consume if it does.
     *
     * @param expected The expected character.
     * @return Whether they match or not.
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (getCurrentUnconsumed() != expected) {
            return false;
        }

        current++;

        return true;
    }

    /**
     * Look ahead at the current unconsumed character without consuming it.
     *
     * @return The current unconsumed character.
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';    // null
        }

        return getCurrentUnconsumed();
    }

    /**
     * Look ahead at the next unconsumed character without consuming it.
     *
     * @return The next unconsumed character.
     */
    private char peekNext() {
        if (current + 1 > source.length()) {
            return '\0';    // null
        }

        return source.charAt(current + 1);
    }

    private boolean isAlpha(char character) {
        return (character >= 'a' && character <= 'z')
                || (character >= 'A' && character <= 'Z')
                || (character == '_');
    }

    private boolean isAlphaNumeric(char character) {
        return isAlpha(character) || isDigit(character);
    }

    private boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void skipUntilEndOfLine() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }

    private char getCurrentUnconsumed() {
        return source.charAt(current);
    }
}
