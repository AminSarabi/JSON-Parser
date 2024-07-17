package ir.reyminsoft.JSON;

public class JSONEscaper extends Escaper {

    public JSONEscaper() {
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
