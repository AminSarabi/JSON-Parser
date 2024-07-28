package ir.reyminsoft.json;

class Escapable {

    final CharSequence escapedVersion;
    CharSequence unescapedVersion;

    public Escapable(final CharSequence escapedVersion) {
        this.escapedVersion = escapedVersion;
    }

    public String getContentEscaped() {
        return escapedVersion.toString();
    }

    public String getContentUnescaped(final Escaper escaper) {
        if (unescapedVersion==null) {
            unescapedVersion = escaper.unescape(escapedVersion);
        }
        return unescapedVersion.toString();
    }
}
