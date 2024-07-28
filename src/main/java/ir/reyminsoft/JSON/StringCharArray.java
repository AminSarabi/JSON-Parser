package ir.reyminsoft.JSON;

class StringCharArray implements CharSequence {

    char[] chars;
    int start;
    int end;

    public StringCharArray(char[] chars, int start, int end) {
        this.chars = chars;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return chars[start+index];
    }

    @Override
    public boolean isEmpty() {
        return end - start == 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new StringCharArray(chars, start, end);
    }

    @Override
    public String toString() {
        return new String(chars, start, end);
    }
}
