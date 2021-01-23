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
}
