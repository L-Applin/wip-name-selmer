package ca.applin.selmer.typer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TupleType extends Type {
    public List<Type> types;

    public TupleType(List<Type> types) {
        super("tuple");
        this.types = types;
    }

    @Override
    public String toString() {
        return "Type: Tuple ("
                + types.stream().map(Objects::toString).collect(Collectors.joining(", "))
                + ")";

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
        TupleType tupleType = (TupleType) o;
        return Objects.equals(types, tupleType.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), types);
    }
}
