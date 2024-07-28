package ir.reyminsoft.JSON;

class Escapable {

    CharSequence escapedVersion;
    CharSequence unescapedVersion;

    public Escapable(CharSequence escapedVersion) {
        this.escapedVersion = escapedVersion;
    }

    public String getContentEscaped() {
        return escapedVersion.toString();
    }

    public String getContentUnescaped(Escaper escaper) {
        if (unescapedVersion==null) {
            unescapedVersion = escaper.unescape(escapedVersion);
        }
        return unescapedVersion.toString();
    }
}
