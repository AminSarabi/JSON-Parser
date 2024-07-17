package ir.reyminsoft.JSON;

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

    public void addCharToEscape(char ch) {
        this.charsToEscape.add(ch);
    }

    public char[] escape(char[] chars) {
        Set<Integer> positions = new HashSet<>();
        for (int x = 0; x != chars.length; x++) {
            char ch = chars[x];
            if (charsToEscape.contains(ch)) {
                positions.add(x);
            }
        }
        char[] second = new char[positions.size() + chars.length];
        int y = 0;
        for (int x = 0; x != second.length; x++) {
            if (positions.contains(x + y)) {
                second[x] = escapingChar;
                y++;
            } else second[x] = chars[x - y];
        }
        return second;
    }

    public char[] unescape(char[] chars) {
        Hashtable<Integer, Integer> startEnds = new Hashtable<>();
        int start = 0;
        int toRemove = 0;
        for (int x = 0; x != chars.length; x++) {
            char ch = chars[x];
            if (ch == escapingChar) {
                boolean first = false;
                boolean last = false;
                if (x == 0) first = true;
                else if (chars[x - 1] != escapingChar) first = true;
                if (x + 1 == chars.length) last = true;
                else if (chars[x + 1] != escapingChar) last = true;
                if (first && last) throw new RuntimeException("char at" + x + " is a single escaping character.");
                if (first) {
                    start = x;
                }
                if (last) {
                    startEnds.put(start, x);
                    int count = x - start;
                    if (count % 2 == 0) {
                        toRemove += count / 2;
                    } else {
                        if (x + 1 == chars.length)
                            throw new RuntimeException("odd count of escape chars from " + first + " to " + x + " escape nothing. eof");
                        char escaped = chars[x];
                        if (!charsToEscape.contains(escaped)) {
                            throw new RuntimeException("odd count of escape chars from " + first + " to " + x + " escape " + escaped + " but it is not a special char");
                        }
                        toRemove += count / 2 + 1;
                    }
                }
            }
        }

        char[] second = new char[chars.length - toRemove];
        int removed = 0;
        for (int x = 0; x != second.length; x++) {
            if (startEnds.containsKey(x)) {
                int end = startEnds.get(x);
                int count = end - x;
                count = count % 2 == 0 ? count / 2 : count / 2 + 1;
                for (int k = x; k != count; k++) {
                    second[x] = escapingChar;
                }
                removed += count;
            } else {
                second[x] = chars[x+removed];
            }
        }
        return second;
    }

    public String escape(String str) {
        return new String(escape(str.toCharArray()));
    }

    public String unescape(String str) {
        return new String(unescape(str.toCharArray()));
    }

}
