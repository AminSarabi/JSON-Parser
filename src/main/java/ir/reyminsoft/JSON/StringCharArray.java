package ir.reyminsoft.json;

class StringCharArray implements CharSequence {

    final char[] chars;
    final int start;
    final int end;

    public StringCharArray(final char[] chars, final int start, final int end) {
        this.chars = chars;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(final int index) {
        return chars[start+index];
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new StringCharArray(chars, start, end);
    }

    @Override
    public String toString() {
        return new String(chars, start, end);
    }
}
