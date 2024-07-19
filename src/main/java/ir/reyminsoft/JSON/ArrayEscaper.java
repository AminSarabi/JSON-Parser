package ir.reyminsoft.JSON;

public class ArrayEscaper extends Escaper {

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
}
