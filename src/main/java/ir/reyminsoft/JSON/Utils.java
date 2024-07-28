package ir.reyminsoft.json;

import java.util.Arrays;

class Utils {

    static void print(final Object... args) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (final Object o : args) {
            if (isFirst) isFirst = false;
            else stringBuilder.append(" , ");
            stringBuilder.append(stringify(o));
        }
        System.out.println(stringBuilder);
    }

    static boolean equals(final Object o, final Object o2) {
        if (o == o2) return true;
        if (o2 == null) return false;
        if (o == null) return false;
        if (o.getClass().isArray() && o2.getClass().isArray()) {
            if (o instanceof int[] && o2 instanceof int[]) return Arrays.equals((int[]) o, (int[]) o2);
            if (o instanceof long[] && o2 instanceof long[]) return Arrays.equals((long[]) o, (long[]) o2);
            if (o instanceof double[] && o2 instanceof double[]) return Arrays.equals((double[]) o, (double[]) o2);
            if (o instanceof float[] && o2 instanceof float[]) return Arrays.equals((float[]) o, (float[]) o2);
            if (o instanceof boolean[] && o2 instanceof boolean[]) return Arrays.equals((boolean[]) o, (boolean[]) o2);
            if (o instanceof byte[] && o2 instanceof byte[]) return Arrays.equals((byte[]) o, (byte[]) o2);
            if (o instanceof short[] && o2 instanceof short[]) return Arrays.equals((short[]) o, (short[]) o2);
            if (o instanceof Object[] && o2 instanceof Object[]) return Arrays.equals((Object[]) o, (Object[]) o2);
        }
        if (o instanceof Number && o2 instanceof Number) {
            return o.toString().equals(o2.toString()); //if both are written the same, they are the same.
        }
        if (o2.getClass().isArray()) return false;
        return o.equals(o2);
    }

    static String stringify(final Object o) {
        if (o == null) return null;
        if (o.getClass().isArray()) {
            if (o instanceof int[]) return Arrays.toString((int[]) o);
            if (o instanceof long[]) return Arrays.toString((long[]) o);
            if (o instanceof double[]) return Arrays.toString((double[]) o);
            if (o instanceof float[]) return Arrays.toString((float[]) o);
            if (o instanceof boolean[]) return Arrays.toString((boolean[]) o);
            if (o instanceof byte[]) return Arrays.toString((byte[]) o);
            if (o instanceof short[]) return Arrays.toString((short[]) o);
            if (o instanceof Object[]) return Arrays.toString((Object[]) o);
        }
        return o.toString();
    }

    static String twoDigitAtLeast(final int value) {
        if (value < 10) return "0" + value;
        return String.valueOf(value);
    }

    static String threeDigitAtLeast(final int value) {
        if (value < 10) return "00" + value;
        if (value < 100) return "0" + value;
        return String.valueOf(value);
    }

    static String fourDigitAtLeast(final int value) {
        if (value < 10) return "0" + value;
        if (value < 100) return "00" + value;
        if (value < 1000) return "000" + value;
        return String.valueOf(value);
    }
}
