package ca.applin.selmer.typer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionType extends Type {

    public List<Type> arguments;
    public Type       return_type;

    public FunctionType(List<Type> arguments, Type return_type) {
        super("function");
        this.arguments = arguments;
        this.return_type = return_type;
    }

    @Override
    public String toString() {
        return "Type: Function ("
                + arguments.stream().map(Objects::toString).collect(Collectors.joining(", "))
                + ") -> "
                + return_type.toString();
    }

    @Override
    public String toStringIndented(int level) {
        return "Function ("
                + arguments.stream().map(Objects::toString).collect(Collectors.joining(", "))
                + ") -> "
                + return_type.toString();

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
        FunctionType that = (FunctionType) o;
        return arguments.equals(that.arguments) &&
                return_type.equals(that.return_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), arguments, return_type);
    }
}
