package dev.frendli;

import java.util.HashMap;
import java.util.Map;

/**
 * A character in the ASCII table.
 */
public enum Ascii {
    CLOSE_PAREN (')'),
    COMMA (','),
    DOT ('.'),
    DOUBLE_QUOTE ('"'),
    EQUALS ('='),
    GREATER_THAN ('>'),
    LESS_THAN ('<'),
    MINUS ('-'),
    NEWLINE ('\n'),
    NULL ('\0'),
    OPEN_PAREN ('('),
    PLUS ('+'),
    SPACE (' '),
    STAR ('*'),
    SLASH ('/'),
    TAB ('\t'),
    UNDERSCORE ('_'),

    _0 ('0'),
    _1 ('1'),
    _2 ('2'),
    _3 ('3'),
    _4 ('4'),
    _5 ('5'),
    _6 ('6'),
    _7 ('7'),
    _8 ('8'),
    _9 ('9'),

    a ('a'),
    b ('b'),
    c ('c'),
    d ('d'),
    e ('e'),
    f ('f'),
    g ('g'),
    h ('h'),
    i ('i'),
    j ('j'),
    k ('k'),
    l ('l'),
    m ('m'),
    n ('n'),
    o ('o'),
    p ('p'),
    q ('q'),
    r ('r'),
    s ('s'),
    t ('t'),
    u ('u'),
    v ('v'),
    w ('w'),
    x ('x'),
    y ('y'),
    z ('z'),

    A ('A'),
    B ('B'),
    C ('C'),
    D ('D'),
    E ('E'),
    F ('F'),
    G ('G'),
    H ('H'),
    I ('I'),
    J ('J'),
    K ('K'),
    L ('L'),
    M ('M'),
    N ('N'),
    O ('O'),
    P ('P'),
    Q ('Q'),
    R ('R'),
    S ('S'),
    T ('T'),
    U ('U'),
    V ('V'),
    W ('W'),
    X ('X'),
    Y ('Y'),
    Z ('Z');

    private static final Map<Character, Ascii> characters = new HashMap<>();    // All the characters of the enum
    private final char character;                                               // The character of the current enum type

    static {
        // Add the character and its wrapped Ascii to
        // the map of characters one time (static).
        for (Ascii ascii : Ascii.values()) {
            characters.put(ascii.get(), ascii);
        }
    }

    Ascii(char character) {
        this.character = character;
    }

    /**
     * Get the character.
     *
     * @return The character.
     */
    public char get() {
        return this.character;
    }

    /**
     * Get the Ascii wrapped character if valid, otherwise null.
     *
     * @param character The character to find.
     * @return The Ascii wrapped character.
     */
    public static Ascii find(char character) {
        return characters.get(character);
    }
}
