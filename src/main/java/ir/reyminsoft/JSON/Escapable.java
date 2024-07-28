package ir.reyminsoft.JSON;

class Escapable {

    String content;
    boolean isUnescaped = false;

    public Escapable(String content) {
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
