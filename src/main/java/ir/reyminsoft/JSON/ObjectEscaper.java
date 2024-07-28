package ir.reyminsoft.json;

class ObjectEscaper extends Escaper {

    public ObjectEscaper() {
        super('\\');
    }

    @Override
    protected boolean shouldEscape(final char ch) {
        return switch (ch) {
            case '{', '}', '\"', ',', '\\', ':', '\t', '\b', '\f', '\n', '\r' -> true;
            default -> false;
        };
    }

    @Override
    void addCharToEscape(final char... chars) {
        throw new JSONException("special escaper can not be modified.");
    }

    @Override
    void addCharToEscape(final char ch) {
        throw new JSONException("special escaper can not be modified.");
    }
}
