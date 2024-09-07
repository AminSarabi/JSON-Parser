package ir.reyminsoft.json;

import java.util.Objects;

public class ObjectTypeTwo {
    String value;
    ObjectTypeOne objectTypeOne;

    public ObjectTypeTwo() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTypeTwo that = (ObjectTypeTwo) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
