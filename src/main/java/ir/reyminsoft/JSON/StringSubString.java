package ir.reyminsoft.JSON;

class StringSubString implements CharSequence {

    String string;
    int start;
    int end;

    public StringSubString(String string, int start, int end) {
        this.string = string;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return string.charAt(start + index);
    }

    @Override
    public boolean isEmpty() {
        return end - start == 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new StringSubString(string, start, end);
    }

    @Override
    public String toString() {
        return string.substring(start, end);
    }
}
