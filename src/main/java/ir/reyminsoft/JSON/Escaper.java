package ir.reyminsoft.json;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Escaper {

    HashSet<Character> charsToEscape;
    final char escapingChar;

    Escaper(final char escapingChar) {
        this.escapingChar = escapingChar;
    }

    protected boolean shouldEscape(final char ch) {
        return ch == escapingChar || (charsToEscape != null && charsToEscape.contains(ch));
    }

    void addCharToEscape(final char ch) {
        if (this.charsToEscape == null) charsToEscape = new HashSet<>();
        this.charsToEscape.add(ch);
    }

    void addCharToEscape(final char... chars) {
        for (final char ch : chars) addCharToEscape(ch);
    }

    char[] escape(final char[] chars, final int start, final int end) {
        final Set<Integer> positions = new HashSet<>();
        for (int x = start; x != end; x++) {
            final char ch = chars[x];
            if (shouldEscape(ch)) {
                positions.add(x);
            }
        }
        final char[] second = new char[positions.size() + end - start];
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

    char[] escape(final char[] chars) {
        return escape(chars, 0, chars.length);
    }

    String unescapeHunting(final Cursor cursor, final char prey) {
        char[] buffer = new char[32];
        boolean wasEscaped = false;
        char ch;
        int i = cursor.currentIndex();
        int bufferIndex = 0;
        final int size = cursor.size();
        for (; i != size; i++) {
            ch = cursor.charAt(i);
            if (wasEscaped) {
                if (ch == 'u') {
                    //this can be a unicode character. needs to be converted to the actual one.
                    int hex = 0;
                    boolean hexSuccess = true;
                    for (int ii = 0; ii != 4; ii++) {
                        i++;
                        ch = cursor.charAt(i);
                        switch (ch) {
                            case '0':
                                hex *= 16;
                                break;
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                hex += (int) (Math.pow(16, 3 - ii) * (ch - '0'));
                                break;
                            case 'A':
                            case 'a':
                            case 'B':
                            case 'b':
                            case 'C':
                            case 'c':
                            case 'D':
                            case 'd':
                            case 'E':
                            case 'e':
                            case 'F':
                            case 'f':
                                hex += (int) (Math.pow(16, 3 - ii) * ((Character.toUpperCase(ch)) - 'A' + 10));
                                break;
                            default:
                                hexSuccess = false;
                                break;
                        }
                    }
                    if (hexSuccess) {
                        ch = (char) hex;
                        buffer[bufferIndex] = ch;
                    } else {
                        i -= 4;
                        buffer[bufferIndex++] = '\\';
                        buffer[bufferIndex++] = 'u';
                    }
                } else {
                    buffer[bufferIndex++] = replaceWithControl(ch);
                }
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

    String unescape(final char[] chars, final int start, final int end) {
        final char[] buffer = new char[end - start];
        boolean wasEscaped = false;
        int bufferIndex = 0;
        for (int x = start; x != end; x++) {
            if (wasEscaped) {
                if (chars[x] == 'u') {
                    //this can be a unicode character. needs to be converted to the actual one.
                    int hex = 0;
                    boolean hexSuccess = true;
                    for (int ii = 0; ii != 4; ii++) {
                        x++;
                        char ch = chars[x];
                        switch (ch) {
                            case '0':
                                break;
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                hex += (int) (Math.pow(16, 3 - ii) * (ch - '0'));
                                break;
                            case 'A':
                            case 'a':
                            case 'B':
                            case 'b':
                            case 'C':
                            case 'c':
                            case 'D':
                            case 'd':
                            case 'E':
                            case 'e':
                            case 'F':
                            case 'f':
                                hex += (int) (Math.pow(16, 3 - ii) * ((Character.toUpperCase(ch)) - 'A' + 10));
                                break;
                            default:
                                hexSuccess = false;
                                break;
                        }
                    }
                    if (hexSuccess) {
                        char ch = (char) hex;
                        buffer[bufferIndex] = ch;
                    } else {
                        x -= 4;
                        buffer[bufferIndex++] = '\\';
                        buffer[bufferIndex] = 'u';
                    }
                } else {
                    buffer[bufferIndex] = replaceWithControl(chars[x]);
                }
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

    String unescape(final char[] chars) {
        return unescape(chars, 0, chars.length);
    }

    String escape(final String str) {
        return new String(escape(str.toCharArray()));
    }

    String unescape(final String str) {
        return unescape(str.toCharArray());
    }

    CharSequence escape(final CharSequence charSequence) {
        final Set<Integer> positions = new HashSet<>();
        final int size = charSequence.length();
        for (int x = 0; x != size; x++) {
            final char ch = charSequence.charAt(x);
            if (shouldEscape(ch)) {
                positions.add(x);
            }
        }
        final char[] second = new char[positions.size() + size];
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

    CharSequence unescape(final CharSequence charSequence) {
        final int size = charSequence.length();
        final char[] buffer = new char[size];
        boolean wasEscaped = false;
        int bufferIndex = 0;
        for (int x = 0; x != size; x++) {
            if (wasEscaped) {
                if (charSequence.charAt(x) == 'u') {
                    //this can be a unicode character. needs to be converted to the actual one.
                    int hex = 0;
                    boolean hexSuccess = true;
                    for (int ii = 0; ii != 4; ii++) {
                        x++;
                        char ch = charSequence.charAt(x);
                        switch (ch) {
                            case '0':
                                break;
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                hex += (int) (Math.pow(16, 3 - ii) * (ch - '0'));
                                break;
                            case 'A':
                            case 'a':
                            case 'B':
                            case 'b':
                            case 'C':
                            case 'c':
                            case 'D':
                            case 'd':
                            case 'E':
                            case 'e':
                            case 'F':
                            case 'f':
                                hex += (int) (Math.pow(16, 3 - ii) * ((Character.toUpperCase(ch)) - 'A' + 10));
                                break;
                            default:
                                hexSuccess = false;
                                break;
                        }
                    }
                    if (hexSuccess) {
                        char ch = (char) hex;
                        buffer[bufferIndex] = ch;
                    } else {
                        x -= 4;
                        buffer[bufferIndex++] = '\\';
                        buffer[bufferIndex++] = 'u';
                    }
                } else {
                    buffer[bufferIndex] = replaceWithControl(charSequence.charAt(x));
                }
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

    private char handleControlChar(final char ch) {
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

    private char replaceWithControl(final char ch) {
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
