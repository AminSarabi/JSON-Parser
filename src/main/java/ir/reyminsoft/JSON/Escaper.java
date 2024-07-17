package ir.reyminsoft.JSON;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class Escaper {

    HashSet<Character> charsToEscape;
    char escapingChar;

    public Escaper(char escapingChar) {
        this.charsToEscape = new HashSet<>();
        this.escapingChar = escapingChar;
        this.charsToEscape.add(escapingChar);
    }

    protected boolean shouldEscape(char ch) {
        return charsToEscape.contains(ch);
    }

    public void addCharToEscape(char ch) {
        this.charsToEscape.add(ch);
    }

    public void addCharToEscape(char... chars) {
        for (char ch : chars) addCharToEscape(ch);
    }

    public char[] escape(char[] chars, int start, int end) {
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

    public char[] escape(char[] chars) {
        return escape(chars, 0, chars.length);
    }

    public char[] unescape(char[] chars, int start, int end) {
        Hashtable<Integer, Integer> startEnds = new Hashtable<>();
        int firstEscapeChar = 0;
        int toRemove = 0;
        for (int x = start; x != end; x++) {
            if (chars[x] == escapingChar) {
                boolean first = false;
                boolean last = false;
                if (x == 0) first = true;
                else if (chars[x - 1] != escapingChar) first = true;
                if (x + 1 == end) last = true;
                else if (chars[x + 1] != escapingChar) last = true;
                if (first && last) {
                    char next = chars[x + 1];
                    char replacement = replaceWithControl(next);
                    if (replacement == next && !shouldEscape(next))
                        throw new RuntimeException("char at " + x + " is a single escaping character." + " " + new String(Arrays.copyOfRange(chars, x - 5, x + 5)));
                }
                if (first) {
                    firstEscapeChar = x;
                }
                if (last) {
                    startEnds.put(firstEscapeChar, x);
                    int count = x - firstEscapeChar + 1;
                    if (count % 2 == 0) {
                        toRemove += count / 2;
                    } else {
                        if (x + 1 == chars.length)
                            throw new RuntimeException("odd count of escape chars from " + first + " to " + x + " escape nothing. eof");
                        char escaped = chars[x];
                        if (!shouldEscape(escaped)) {
                            throw new RuntimeException("odd count of escape chars from " + first + " to " + x + " escape " + escaped + " but it is not a special char");
                        }
                        toRemove += count / 2 + 1;
                    }

                }
            }
        }

        char[] second = new char[end - start - toRemove];
        int o = start;
        for (int s = 0; s < second.length; s++) {
            boolean wasEscaped = false;
            if (startEnds.containsKey(o)) {
                int lastEscapeChar = startEnds.get(o);
                int count = lastEscapeChar - o + 1;
                int countToKeep = count / 2;
                wasEscaped = count % 2 != 0;
                for (int k = 0; k != countToKeep; k++) {
                    second[s] = escapingChar;
                    s++;
                }
                o += count;
            }
            if (s < second.length && o < chars.length) {
                second[s] = wasEscaped ? replaceWithControl(chars[o]) : chars[o];
                o++;
            }
        }
        return second;
    }

    public char[] unescape(char[] chars) {
        return unescape(chars, 0, chars.length);
    }

    public String escape(String str) {
        return new String(escape(str.toCharArray()));
    }

    public String unescape(String str) {
        return new String(unescape(str.toCharArray()));
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
