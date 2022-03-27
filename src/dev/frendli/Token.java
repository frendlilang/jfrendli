package dev.frendli;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;  // Used when reporting errors

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
