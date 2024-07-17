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

    public void addCharToEscape(char ch) {
        this.charsToEscape.add(ch);
    }

    public void addCharToEscape(char... chars) {
        for (char ch : chars) addCharToEscape(ch);
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
            if (positions.contains(y)) {
                second[x] = escapingChar;
                x++;
            }
            second[x] = chars[y];
            y++;
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
                if (first && last) {
                    if (!charsToEscape.contains(chars[x + 1]))
                        throw new RuntimeException("char at " + x + " is a single escaping character." + " " + new String(Arrays.copyOfRange(chars,x-5,x+5)));
                }
                if (first) {
                    start = x;
                }
                if (last) {
                    startEnds.put(start, x);
                    int count = x - start + 1;
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
        int o = 0;
        for (int s = 0; s < second.length; s++) {
            if (startEnds.containsKey(o)) {
                int end = startEnds.get(o);
                int count = end - o + 1;
                int countToKeep = count / 2;

                for (int k = 0; k != countToKeep; k++) {
                    second[s] = escapingChar;
                    s++;
                }
                o += count;
            }
            if (s < second.length && o < chars.length) {
                second[s] = chars[o];
                o++;
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
