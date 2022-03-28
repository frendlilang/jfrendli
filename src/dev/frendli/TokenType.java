package dev.frendli;

public enum TokenType {
    // Single characters
    OPEN_PAREN,
    CLOSE_PAREN,
    COMMA,
    DOT,
    EQUALS_SIGN,
    GREATER_THAN,
    GREATER_THAN_EQUALS,
    LESS_THAN,
    LESS_THAN_EQUALS,
    MINUS,
    PLUS,
    STAR,
    SLASH,

    // Keywords
    ACCEPT,
    AND,
    CHANGE,
    CREATE,
    DEFINE,
    DESCRIBE,
    DISPLAY,    // TEMPORARY (until built-in function)
    EMPTY,
    EQUALS_WORD,
    FALSE,
    HAS,
    IF,
    INHERIT,
    ME,
    NEW,
    NOT,
    OR,
    OTHERWISE,
    PARENT,
    REPEAT,
    RETURN,
    SEND,
    TIMES,
    TRUE,
    WHILE,
    WITH,

    // Literals
    IDENTIFIER,
    NUMBER,
    TEXT,

    // Formatting
    DEDENT,
    INDENT,
    NEWLINE,

    // End of file
    EOF
}
