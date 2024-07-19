package ir.reyminsoft.JSON;

import java.util.HashSet;
import java.util.Set;

class Escaper {

    HashSet<Character> charsToEscape;
    char escapingChar;
    StringBuilder stringBuilder;

    Escaper(char escapingChar) {
        this.charsToEscape = new HashSet<>();
        this.escapingChar = escapingChar;
        this.charsToEscape.add(escapingChar);
        //initiate with a big capacity.
        this.stringBuilder = new StringBuilder(8192);
        // this does not have that much of memory overhead but shows benefit on big objects.
    }


    Escaper(char escapingChar, int initialCapacity) {
        this.charsToEscape = new HashSet<>();
        this.escapingChar = escapingChar;
        this.charsToEscape.add(escapingChar);
        this.stringBuilder = new StringBuilder(initialCapacity);
    }

    protected boolean shouldEscape(char ch) {
        return charsToEscape.contains(ch);
    }

    void addCharToEscape(char ch) {
        this.charsToEscape.add(ch);
    }

    void addCharToEscape(char... chars) {
        for (char ch : chars) addCharToEscape(ch);
    }

    char[] escape(char[] chars, int start, int end) {
        Set<Integer> positions = new HashSet<>();
        for (int x = start; x != end; x++) {
            char ch = chars[x];
            if (shouldEscape(ch)) {
                positions.add(x);
            }
        }
        char[] second = new char[positions.size() + end - start];
        int y = 0;
        for (int x = 0; x != second.length; x++) {
            if (positions.contains(y)) {
                second[x] = escapingChar;
                x++;
                second[x] = handleControlChar(chars[y]);
            } else {
                second[x] = chars[y];
            }
            y++;
        }
        return second;
    }

    char[] escape(char[] chars) {
        return escape(chars, 0, chars.length);
    }

    String unescapeHunting(Cursor cursor, char prey) {
        stringBuilder.setLength(0);
        boolean wasEscaped = false;
        while (cursor.hasNextChar()) {
            if (wasEscaped) {
                stringBuilder.append(replaceWithControl(cursor.currentCharacter()));
                wasEscaped = false;
            } else if (cursor.currentCharacter() == prey) {
                break;
            } else if (cursor.currentCharacter() == escapingChar) {
                wasEscaped = true;
            } else {
                stringBuilder.append(cursor.currentCharacter());
            }
            cursor.increment();
        }
        return stringBuilder.toString();
    }

    String unescape(char[] chars, int start, int end) {
        char[] second = new char[end - start];
        boolean wasEscaped = false;
        int secondIndex = 0;
        for (int x = start; x != end; x++) {
            if (wasEscaped) {
                second[secondIndex] = replaceWithControl(chars[x]);
                secondIndex++;
                if (secondIndex >= second.length) break;
                wasEscaped = false;
                continue;
            }
            if (chars[x] == escapingChar) {
                wasEscaped = true;
            } else {
                if (secondIndex >= second.length) break;
                second[secondIndex] = chars[x];
                secondIndex++;
            }
        }
        return new String(second, 0, secondIndex);
    }

    String unescape(char[] chars) {
        return unescape(chars, 0, chars.length);
    }

    String escape(String str) {
        return new String(escape(str.toCharArray()));
    }

    String unescape(String str) {
        return unescape(str.toCharArray());
    }


    private char handleControlChar(char ch) {
        return switch (ch) {
            case '\n' -> 'n';
            case '\t' -> 't';
            case '\b' -> 'b';
            case '\r' -> 'r';
            case '\f' -> 'f';
            case '\0' -> '0';
            default -> ch;
        };
    }

    private char replaceWithControl(char ch) {
        return switch (ch) {
            case 'n' -> '\n';
            case 't' -> '\t';
            case 'b' -> '\b';
            case 'r' -> '\r';
            case 'f' -> '\f';
            case '0' -> '\0';
            default -> ch;
        };
    }

}
