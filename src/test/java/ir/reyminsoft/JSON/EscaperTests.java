package ir.reyminsoft.JSON;

public class EscaperTests implements TestClass {

    public static void main(String[] args) {
        TestClassRunner.run(EscaperTests.class);
    }


    public static void simple_escaping() {
        Escaper escaper = new Escaper('*');
        escaper.addCharToEscape('s');
        String[] strings = new String[]{
                "s",
                "*si",
                "*s s i",
                "**hi",
                "***si",
                "****hi",
                "* *  *",
                "*",
                "********************************",
                "/\\dafskmjml;ksjfieroiqurpoi4eupioru2349u2390**"
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

    }

}
