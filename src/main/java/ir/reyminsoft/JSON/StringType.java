package ir.reyminsoft.JSON;

public class StringType {

    String content;
    boolean isUnescaped = false;

    public StringType(String content) {
        this.content = content;
    }

    public String getContentEscaped(Escaper escaper) {
        if (isUnescaped) {
            return escaper.escape(content);
        }
        return content;
    }
    public String getContentUnescaped(Escaper escaper) {
        if (!isUnescaped) {
            content = escaper.unescape(content);
            isUnescaped = true;
        }
        return content;
    }
}
