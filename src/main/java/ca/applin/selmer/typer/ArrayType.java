package ca.applin.selmer.typer;

import java.util.Objects;

public class ArrayType extends Type {

    public ArrayType(Type baseType) {
        super("array", true);
        this.baseType = baseType;
    }

    public Type baseType;

    @Override
    public String toString() {
        return "Type: Array [" + baseType.toString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(baseType, arrayType.baseType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseType);
    }
}
