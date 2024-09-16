package ir.reyminsoft.json;

import java.util.Objects;

public class ObjectTypeOne {

    ObjectTypeTwo objectTypeTwo;

    long primitiveValue= 7091;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectTypeOne that = (ObjectTypeOne) o;
        return Objects.equals(objectTypeTwo, that.objectTypeTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectTypeTwo);
    }
}
