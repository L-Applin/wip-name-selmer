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
}
