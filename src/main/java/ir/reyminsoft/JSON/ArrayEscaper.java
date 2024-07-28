package ir.reyminsoft.JSON;

class ArrayEscaper extends Escaper {

    public ArrayEscaper() {
        super('\\');
    }

    @Override
    protected boolean shouldEscape(char ch) {
        return switch (ch) {
            case '[', ']', '\"', ',', ':', '\t', '\b', '\\', '\f', '\n', '\r' -> true;
            default -> false;
        };
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
