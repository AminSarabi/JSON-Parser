package ir.reyminsoft.JSON;

public class ObjectEscaper extends Escaper {

    public ObjectEscaper() {
        super('\\');
    }

    @Override
    protected boolean shouldEscape(char ch) {
        return switch (ch) {
            case '{', '}', '\"', ',', '\\', ':', '\t', '\b', '\f', '\n', '\r' -> true;
            default -> false;
        };
    }
}
