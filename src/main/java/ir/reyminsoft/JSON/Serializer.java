package ir.reyminsoft.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

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
                if (field.getType().isPrimitive()) {
                    if (value != null) field.set(t, value);
                } else {
                    if (value instanceof JSONObject && field.getType() != JSONObject.class) {
                        if (recursionAvoidSet.containsKey(value)) {
                            value = recursionAvoidSet.get(value);
                        } else {
                            if (field.getType() != Object.class) {
                                value = deserialize((JSONObject) value, field.getType(), recursionAvoidSet);
                            }
                        }
                    }
                    if (value instanceof JSONArray && field.getType() == List.class) {
                        Class s = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        value = Serializer.deserializeAsList((JSONArray) value, s);
                    }
                    field.set(t, value);
                }
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
                T t;
                if (clazz.isPrimitive() || isWrapperType(clazz) || clazz == String.class) {
                    t = jsonArray.get(x);

                } else {
                    JSONObject jsonObject = jsonArray.get(x);
                    t = deserialize(jsonObject, clazz);
                }
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
                if (!Modifier.isPublic(field.getModifiers()))
                    throw new RuntimeException("serialized object " + o.getClass().getSimpleName() + "  has non public values: " + field.getName());
                Object value = field.get(o);
                if (value == null) value = JSONObject.NULL;
                if (JSONObject.validateType(value) != null) {
                    if (field.getType().isPrimitive()) {
                        jsonObject.put(field.getName(), value);
                    } else {
                        if (recursionAvoidSet.containsKey(value)) {
                            jsonObject.put(field.getName(), recursionAvoidSet.get(value));
                        } else {
                            if (field.getType() == List.class) {
                                Class s = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                if (s.isPrimitive() || isWrapperType(s) || s == String.class) {
                                    jsonObject.put(field.getName(), new JSONArray((List<Object>) value));
                                } else {
                                    List<Object> list = (List<Object>) value;
                                    JSONArray jsonArray = new JSONArray();
                                    for (int x = 0; x != list.size(); x++) {
                                        jsonArray.put(serialize(list.get(x)));
                                    }
                                    jsonObject.put(field.getName(), jsonArray);
                                }

                            } else {
                                JSONObject convertedInternalObject = serialize(value, recursionAvoidSet);
                                jsonObject.put(field.getName(), convertedInternalObject);
                            }
                        }
                    }

                } else jsonObject.put(field.getName(), value);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error");
        }
    }

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
}
