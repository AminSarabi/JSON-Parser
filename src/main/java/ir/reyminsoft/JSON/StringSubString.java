package ir.reyminsoft.json;

class StringSubString implements CharSequence {

    final String string;
    final int start;
    final int end;

    public StringSubString(final String string, final int start, final int end) {
        this.string = string;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(final int index) {
        return string.charAt(start + index);
    }


    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new StringSubString(string, start, end);
    }

    @Override
    public String toString() {
        return string.substring(start, end);
    }
}
