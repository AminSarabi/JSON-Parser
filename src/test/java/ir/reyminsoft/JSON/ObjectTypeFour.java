package ir.reyminsoft.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ObjectTypeFour {
    public String value;
    public ObjectTypeOne objectTypeOne;

    public ObjectTypeFour() {

    }

    public List<String> strings;
    public List<Integer> integers = Arrays.asList(0, 1, 1);
    public List<ObjectTypeOne> objectTypeOnes = Arrays.asList(new ObjectTypeOne(),new ObjectTypeOne());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTypeFour that = (ObjectTypeFour) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
