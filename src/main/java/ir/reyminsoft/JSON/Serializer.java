package ir.reyminsoft.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings("unused")
public class Serializer {

    public static <T> T deserialize(String text, Class<T> clazz) {
        return deserialize(new JSONObject(text), clazz);
    }

    public static <T> T deserialize(JSONObject jsonObject, Class<T> clazz) {
        return deserialize(jsonObject, clazz, new Hashtable<>());
    }

    private static <T> T deserialize(JSONObject jsonObject, Class<T> clazz, Hashtable<Object, Object> recursionAvoidSet) {
        try {
            T t = clazz.getConstructor().newInstance();
            recursionAvoidSet.put(jsonObject, t);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                Object value = jsonObject.get(field.getName());
                //JSONObject.NULL conversion is not needed as jsonObject.get already handles it.
                if (value instanceof JSONObject && field.getType() != JSONObject.class) {
                    if (recursionAvoidSet.containsKey(value)) {
                        value = recursionAvoidSet.get(value);
                    } else value = deserialize((JSONObject) value, field.getType(), recursionAvoidSet);
                }
                field.set(t, value);
            }
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> deserializeAsList(JSONArray jsonArray, Class<T> clazz) {
        try {
            List<T> list = new ArrayList<>();
            for (int x = 0; x != jsonArray.size(); x++) {
                JSONObject jsonObject = jsonArray.get(x);
                T t = deserialize(jsonObject, clazz);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject serialize(Object o) {
        return serialize(o, new Hashtable<>());
    }

    private static JSONObject serialize(Object o, Hashtable<Object, Object> recursionAvoidSet) {
        try {
            JSONObject jsonObject = new JSONObject();
            recursionAvoidSet.put(o, jsonObject);
            for (Field field : o.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                Object value = field.get(o);
                if (value == null) value = JSONObject.NULL;
                if (JSONObject.validateType(value) != null) {
                    if (recursionAvoidSet.containsKey(value)) {
                        jsonObject.put(field.getName(), recursionAvoidSet.get(value));
                    } else {
                        JSONObject convertedInternalObject = serialize(value, recursionAvoidSet);
                        jsonObject.put(field.getName(), convertedInternalObject);
                    }

                } else jsonObject.put(field.getName(), value);
            }
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
