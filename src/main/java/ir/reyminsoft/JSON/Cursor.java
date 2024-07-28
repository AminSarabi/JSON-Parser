package ir.reyminsoft.JSON;

class Cursor {
    int index;
    final String string;

    Cursor(String s) {
        this.string = s; //we reuse the string and avoid creating another char[] array. (memory gain)
    }

    Cursor increment() {
        index++;
        return this;
    }

    boolean incrementAndHasNext() {
        if (index + 1 >= size()) return false;
        index++;
        return true;
    }

    Cursor increment(int count) {
        index += count;
        return this;
    }

    Cursor decrement() {
        index--;
        return this;
    }

    char charAt(int x) {
        return string.charAt(x);
    }

    int size() {
        return string.length();
    }

    char currentCharacter() {
        return charAt(currentIndex());
    }

    char nextCharacter(int forward) {
        return charAt(currentIndex() + forward);
    }


    void setIndex(int value) {
        index = value;
    }

    int currentIndex() {
        return index;
    }

    void assertChar(char ch) {
        if (!charIs(ch)) {
            throw new JSONException("Expected " + ch + " at " + index + " got " + currentCharacter());
        }
    }

    boolean charIs(char character) {
        return currentCharacter() == character;
    }

    boolean indexIs(int other) {
        return index == other;
    }

    boolean indexIsNot(int other) {
        return index != other;
    }

    boolean indexIsLessThan(int other) {
        return index < other;
    }

    boolean hasNextChar() {
        return indexIsLessThan(size());
    }

    boolean hasNextChars(int count) {
        return indexIsLessThan(currentIndex() + count);
    }

    void assertNextChars(int count) {
        if (!hasNextChars(count)) {
            throw new JSONException("unexpected end of stream at " + size());
        }
    }

    boolean indexIsGreaterThan(int other) {
        return index > other;
    }

    String getRangeAsString(int begin, int end) {
        return string.substring(begin, end);
    }

    String getRangeAsString(int count) {
        return string.substring(currentIndex(), currentIndex() + 4);
    }

    void throwUnrecognizedCharacter() {
        throw new JSONException("unrecognized character at " + currentIndex() + " " + currentCharacter());
    }

    void throwUnrecognizedValue(String value) {
        throw new JSONException("unrecognized character near " + currentIndex() + " " + value);
    }
}
