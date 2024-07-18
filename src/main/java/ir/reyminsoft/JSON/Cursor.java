package ir.reyminsoft.JSON;

public class Cursor {
    int index;
    String string;

    public Cursor(String s) {
        this.string = s; //we reuse the string and avoid creating another char[] array. (memory gain)
    }

    public Cursor increment() {
        index++;
        return this;
    }

    public Cursor decrement() {
        index--;
        return this;
    }

    private char charAt(int x) {
        return string.charAt(x);
    }

    private int size() {
        return string.length();
    }

    public char currentCharacter() {
        return charAt(currentIndex());
    }

    public char nextCharacter(int forward) {
        return charAt(currentIndex() + forward);
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
        return indexIsLessThan(size());
    }

    public boolean hasNextChars(int count) {
        return indexIsLessThan(currentIndex() + count);
    }

    public void assertNextChars(int count) {
        if (!hasNextChars(count)) {
            throw new JSONException("unexpected end of stream at " + size());
        }
    }

    public boolean indexIsGreaterThan(int other) {
        return index > other;
    }

    public String getRangeAsString(int begin, int end) {
        return string.substring(begin, end);
    }

    public String getRangeAsString(int count) {
        return string.substring(currentIndex(), currentIndex() + 4);
    }

    public void throwUnrecognizedCharacter() {
        throw new JSONException("unrecognized character at " + currentIndex() + " " + currentCharacter());
    }

    public void throwUnrecognizedValue(String value) {
        throw new JSONException("unrecognized character near " + currentIndex() + " " + value);
    }
}
