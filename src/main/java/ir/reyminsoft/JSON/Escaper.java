package ir.reyminsoft.json;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Escaper {

    HashSet<Character> charsToEscape;
    final char escapingChar;

    Escaper(char escapingChar) {
        this.escapingChar = escapingChar;
    }

    protected boolean shouldEscape(char ch) {
        return ch == escapingChar || (charsToEscape != null && charsToEscape.contains(ch));
    }

    void addCharToEscape(char ch) {
        if (this.charsToEscape == null) charsToEscape = new HashSet<>();
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
        char[] buffer = new char[32];
        boolean wasEscaped = false;
        char ch;
        int i = cursor.currentIndex();
        int bufferIndex = 0;
        final int size = cursor.size();
        for (; i != size; i++) {
            ch = cursor.charAt(i);
            if (wasEscaped) {
                buffer[bufferIndex++] = replaceWithControl(ch);
                wasEscaped = false;
            } else if (ch == prey) {
                i++;
                break;
            } else if (ch == escapingChar) {
                wasEscaped = true;
            } else {
                buffer[bufferIndex++] = ch;
            }
            if (bufferIndex >= buffer.length) {
                buffer = Arrays.copyOf(buffer, (int) (buffer.length * 1.5));
            }
        }
        cursor.setIndex(i);
        return new String(buffer, 0, bufferIndex);
    }

    String unescape(char[] chars, int start, int end) {
        char[] buffer = new char[end - start];
        boolean wasEscaped = false;
        int bufferIndex = 0;
        for (int x = start; x != end; x++) {
            if (wasEscaped) {
                buffer[bufferIndex] = replaceWithControl(chars[x]);
                bufferIndex++;
                if (bufferIndex >= buffer.length) break;
                wasEscaped = false;
                continue;
            }
            if (chars[x] == escapingChar) {
                wasEscaped = true;
            } else {
                if (bufferIndex >= buffer.length) break;
                buffer[bufferIndex] = chars[x];
                bufferIndex++;
            }
        }
        return new String(buffer, 0, bufferIndex);
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

    CharSequence escape(CharSequence charSequence) {
        Set<Integer> positions = new HashSet<>();
        int size = charSequence.length();
        for (int x = 0; x != size; x++) {
            char ch = charSequence.charAt(x);
            if (shouldEscape(ch)) {
                positions.add(x);
            }
        }
        char[] second = new char[positions.size() + size];
        int y = 0;
        for (int x = 0; x != second.length; x++) {
            if (positions.contains(y)) {
                second[x] = escapingChar;
                x++;
                second[x] = handleControlChar(charSequence.charAt(y));
            } else {
                second[x] = charSequence.charAt(y);
            }
            y++;
        }
        return new StringCharArray(second, 0, second.length);
    }

    CharSequence unescape(CharSequence charSequence){
        int size = charSequence.length();
        char[] buffer = new char[size];
        boolean wasEscaped = false;
        int bufferIndex = 0;
        for (int x = 0; x != size; x++) {
            if (wasEscaped) {
                buffer[bufferIndex] = replaceWithControl(charSequence.charAt(x));
                bufferIndex++;
                if (bufferIndex >= buffer.length) break;
                wasEscaped = false;
                continue;
            }
            if (charSequence.charAt(x) == escapingChar) {
                wasEscaped = true;
            } else {
                if (bufferIndex >= buffer.length) break;
                buffer[bufferIndex] = charSequence.charAt(x);
                bufferIndex++;
            }
        }
        return new StringCharArray(buffer, 0, bufferIndex);
    }

    private char handleControlChar(char ch) {
        switch (ch) {
            case '\n':
                return 'n';
            case '\t':
                return 't';
            case '\b':
                return 'b';
            case '\r':
                return 'r';
            case '\f':
                return 'f';
            case '\0':
                return '0';
            default:
                return ch;
        }
    }

    private char replaceWithControl(char ch) {
        switch (ch) {
            case 'n':
                return '\n';
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case '0':
                return '\0';
            default:
                return ch;
        }
    }

}
