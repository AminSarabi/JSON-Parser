package ir.reyminsoft.JSON;

import java.util.Arrays;

public class Cursor {
    int index;
    char[] chars;

    public Cursor(char[] chars) {
        this.chars = chars;
    }

    public Cursor increment() {
        index++;
        return this;
    }

    public Cursor decrement() {
        index--;
        return this;
    }

    public char currentCharacter() {
        return chars[index];
    }

    public char nextCharacter(int forward) {
        return chars[index + forward];
    }


    public void setIndex(int value) {
        index = value;
    }

    public int currentIndex() {
        return index;
    }

    public void assertChar(char ch) {
        if (!charIs(ch)) {
            throw new JSONException("Expected " + ch + " at " + index);
        }
    }

    public boolean charIs(char character) {
        return currentCharacter() == character;
    }

    public boolean indexIs(int other) {
        return index == other;
    }

    public boolean indexIsNot(int other) {
        return index != other;
    }

    public boolean indexIsLessThan(int other) {
        return index < other;
    }

    public boolean hasNextChar() {
        return indexIsLessThan(chars.length);
    }

    public boolean hasNextChars(int count) {
        return indexIsLessThan(currentIndex() + count);
    }

    public void assertNextChars(int count) {
        if (!hasNextChars(count)) {
            throw new JSONException("unexpected end of stream at " + chars.length);
        }
    }

    public boolean indexIsGreaterThan(int other) {
        return index > other;
    }

    public String getRangeAsString(int begin, int end) {
        return new String(Arrays.copyOfRange(chars, begin, end));
    }

    public String getRangeAsString(int count) {
        return new String(Arrays.copyOfRange(chars, currentIndex(), currentIndex() + 4));
    }

    public void throwUnrecognizedCharacter() {
        throw new JSONException("unrecognized character at " + currentIndex() + " " + currentCharacter());
    }

    public void throwUnrecognizedValue(String value) {
        throw new JSONException("unrecognized character near " + currentIndex() + " " + value);
    }
}
