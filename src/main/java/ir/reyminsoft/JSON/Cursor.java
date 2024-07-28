package ir.reyminsoft.json;

class Cursor {
    int index;
    final String string;
    final int size;

    Cursor(final String s) {
        this.string = s; //we reuse the string and avoid creating another char[] array. (memory gain)
        size = s.length();
    }

    Cursor increment() {
        index++;
        return this;
    }

    void increment(final int count) {
        index += count;
    }

    char charAt(final int x) {
        return string.charAt(x);
    }

    int size() {
        return string.length();
    }

    char currentCharacter() {
        return charAt(currentIndex());
    }

    void setIndex(final int value) {
        index = value;
    }

    int currentIndex() {
        return index;
    }

    void assertChar(final char ch) {
        if (!charIs(ch)) {
            throw new JSONException("Expected " + ch + " at " + index + " got " + currentCharacter());
        }
    }

    boolean charIs(final char character) {
        return currentCharacter() == character;
    }


    boolean indexIsLessThan(final int other) {
        return index < other;
    }

    boolean hasNextChar() {
        return index < size;
    }


    String getRangeAsString(final int begin, final int end) {
        return string.substring(begin, end);
    }
}
