package ir.reyminsoft.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class JSONArray {
    private static final Escaper escaper;


    static {
        escaper = new ArrayEscaper();
    }

    private final List<Object> objectList;

    public JSONArray() {
        this.objectList = new ArrayList<>();
    }

    JSONArray(final List<Object> objectList) {
        this.objectList = objectList;
    }

    public static JSONArray from(List<?> list) {
        JSONArray jsonArray = new JSONArray();
        for (Object o : list) {
            if (isUnknownType(o)) {
                jsonArray.put(Serializer.serialize(o));
            } else jsonArray.put(o);
        }
        return jsonArray;
    }

    public static JSONArray from(Object... objects) {
        JSONArray jsonArray = new JSONArray();
        for (Object o : objects) {
            if (isUnknownType(o)) {
                jsonArray.put(Serializer.serialize(o));
            } else jsonArray.put(o);
        }
        return jsonArray;
    }


    int cachedStringLength = 128;

    public JSONArray(final String string) {
        cachedStringLength = string.length();
        final Cursor cursor = new Cursor(string);
        cursor.assertChar('[');
        this.objectList = readArray(cursor.increment());
    }

    static List<Object> readArray(final Cursor cursor) {
        final List<Object> list = new ArrayList<>();
        while (cursor.hasNextChar()) {
            char ch = cursor.currentCharacter();
            cursor.increment();
            switch (ch) {
                case ',':
                    break;
                case '[':
                    list.add(new JSONArray(readArray(cursor)));
                    break;
                case ']':
                    return list;
                case '{':
                    list.add(new JSONObject(JSONObject.readObject(cursor, 1)));
                    break;
                case '"':
                    list.add(JSONObject.readStringValue(cursor));
                    break;

                case 't':
                case 'T':
                    list.add(true);
                    cursor.increment(3);
                    break;
                case 'f':
                case 'F':
                    list.add(false);
                    cursor.increment(4);
                    break;
                case 'n':
                case 'N':
                    list.add(null);
                    cursor.increment(3);
                    break;
                case '+':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    list.add(JSONObject.readNumeric(cursor, ch, ']'));
                    if (cursor.isMarked()) {
                        cursor.clearMark();
                        return list;
                    }
                    break;
            }
        }


        return list;
    }


    @Override
    public String toString() { //todo if the content is not modified, use a cached string (weak reference)
        final StringBuilder stringBuilder = new StringBuilder(cachedStringLength);
        toString(stringBuilder);
        return stringBuilder.toString();
    }

    void toString(final StringBuilder stringBuilder) {
        stringBuilder.append("[");
        boolean first = true;
        for (final Object o : objectList) {
            if (!first) {
                stringBuilder.append(',');
            } else {
                first = false;
            }
            if (o == null) {
                stringBuilder.append("null");
            } else if (o instanceof Escapable) {
                final Escapable escapable = (Escapable) o;
                stringBuilder.append('"').append(escapable.getContentEscaped()).append('"');
            } else if (o instanceof String) {
                stringBuilder.append('"').append(escaper.escape((String) o)).append('"');
            } else if (o instanceof JSONObject) {
                ((JSONObject) o).toString(stringBuilder);
            } else if (o instanceof JSONArray) {
                ((JSONArray) o).toString(stringBuilder);
            } else {
                stringBuilder.append(o.toString());
            }
        }
        stringBuilder.append("]");
    }

    public void put(Object o) {
        if (isUnknownType(o)) {
            try {
                put(Serializer.serialize(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new JSONException("unknown type to put in json-array: " + o.getClass());
            }

        }
        this.objectList.add(o);
    }

    public void remove(int x) {
        this.objectList.remove(x);
    }

    public boolean remove(Object o) {
        return this.objectList.remove(o);
    }

    private static boolean isUnknownType(Object o) {
        if (o == null) return false;
        return !(o instanceof String || o instanceof Integer || o instanceof Long || o instanceof Double ||
                o instanceof Boolean || o instanceof JSONArray
                || o instanceof JSONObject);
    }

    public JSONArray getJSONArray(final int i) {
        return get(i);
    }

    public JSONObject getJSONObject(final int i) {
        return get(i);
    }

    public String getString(final int i) {
        final Object o = objectList.get(i);
        if (o instanceof String) return (String) o;
        final Escapable escapable = (Escapable) o;
        return escapable.getContentUnescaped(escaper);
    }

    public boolean getBoolean(final int i) {
        return get(i);
    }

    public int getInteger(final int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return ((Number) objectList.get(i)).intValue();
    }

    public double getDouble(final int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return ((Number) objectList.get(i)).doubleValue();
    }

    public long getLong(final int i) {
        if (i >= objectList.size() || i < 0) return 0;
        return ((Number) objectList.get(i)).longValue();
    }

    public BigDecimal getBigDecimal(final int i) {
        if (i >= objectList.size() || i < 0) return BigDecimal.ZERO;
        return (BigDecimal) objectList.get(i);
    }


    public <T> T get(final int i) {
        if (i >= objectList.size() || i < 0) return null;
        Object o = objectList.get(i);
        if (o == null) return null;
        if (o instanceof Escapable) {
            o = ((Escapable) o).getContentUnescaped(escaper);
        }
        @SuppressWarnings("unchecked") final T casted = (T) o;
        return casted;
    }


    public int size() {
        return objectList.size();
    }

    public int length() {
        return size();
    }


    public <T> void forEach(JSONArrayForEachConsumer<T> consumer) {
        for (int x = 0; x != length(); x++) {
            consumer.consume(get(x));
        }
    }


    public interface JSONArrayForEachConsumer<T> {
        void consume(T t);

    }
}
