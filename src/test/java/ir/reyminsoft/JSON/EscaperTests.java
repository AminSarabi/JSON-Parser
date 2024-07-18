package ir.reyminsoft.JSON;

import java.util.Hashtable;

import static ir.reyminsoft.JSON.ObjectTests.randomString;

public class EscaperTests implements TestClass {

    public static void main(String[] args) {
        TestClassRunner.run(EscaperTests.class);
    }


    public static void simple_escaping() {
        Escaper escaper = new Escaper('*');
        escaper.addCharToEscape('s');
        String[] strings = new String[]{
                "s",
                " s",
                " s ",
                "s ",
                " * ",
                " ?* ",
                " ?s ",
                "*si",
                "*s s i",
                "**hi",
                "***si",
                "****hi",
                "* *  *",
                "*",
                "********s****************s********",
                "/\\dafskmjml;k s\njfieroiqurpoi4eupissosru2s349u2390**"
        };
        for (String original : strings) {
            Utils.print(original);
            String escaped = escaper.escape(original);
            Utils.print(escaped);
            String unescaped = escaper.unescape(escaped);
            Utils.print(unescaped);
            Utils.print("==================================");
            TestClassRunner.assertEquals(unescaped, original);
        }

    }

    public static void extreme_case(){
        Escaper escaper = new Escaper('\\');
        escaper.addCharToEscape('{', '}', '\"', ',', ':', '\t', '\b', '\t', '\f', '\n', '\r'
                , '[', ']');
        Hashtable<String, String> hashtable = new Hashtable<>();
        for (int x = 0; x != 10; x++) {
            String original = randomString();
            Utils.print(original);
            String escaped = escaper.escape(original);
            Utils.print(escaped);
            String unescaped = escaper.unescape(escaped);
            Utils.print(unescaped);
            Utils.print("==================================");
            TestClassRunner.assertEquals(unescaped, original);
        }
    }


    public static void control_characters(){
        Escaper escaper = new Escaper('\\');
        char[] chars = new char[]{'\t', '\b', '\t', '\f', '\n', '\r'};
        escaper.addCharToEscape(chars);
        String[] strings = new String[]{
            "\\n\n\\n\r\n",
                "\b \\b hi there i am \n testing"
        };
        for (String original : strings) {
            Utils.print(original);
            String escaped = escaper.escape(original);
            for (char ch :chars){
                TestClassRunner.assertEquals(escaped.contains(Character.toString(ch)),false);
            }
        }
    }

}
