package ir.reyminsoft.json;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class Serializer {

    public static <T> T deserialize(String text, Class<T> clazz) throws Exception {
        return deserialize(new JSONObject(text), clazz);
    }

    public static <T> T deserialize(JSONObject jsonObject, Class<T> clazz) throws Exception {
        T t = clazz.getConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.set(t, jsonObject.get(field.getName()));
        }
        return t;
    }

    public static JSONObject serialize(Object o) throws IllegalAccessException {
        JSONObject jsonObject = new JSONObject();
        for (Field field : o.getClass().getDeclaredFields()) {
            jsonObject.put(field.getName(), field.get(o));
        }
        return jsonObject;
    }
}
