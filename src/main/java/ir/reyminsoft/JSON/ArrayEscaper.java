package ir.reyminsoft.json;

class ArrayEscaper extends Escaper {

    public ArrayEscaper() {
        super('\\');
    }

    @Override
    protected boolean shouldEscape(char ch) {
        switch (ch) {
            case '[':
            case ']':
            case '\"':
            case ',':
            case ':':
            case '\t':
            case '\b':
            case '\\':
            case '\f':
            case '\n':
            case '\r':
                return true;
            default:
                return false;
        }
    }

    @Override
    void addCharToEscape(char... chars) {
        throw new JSONException("special escaper can not be modified.");
    }

    @Override
    void addCharToEscape(char ch) {
        throw new JSONException("special escaper can not be modified.");
    }
}
