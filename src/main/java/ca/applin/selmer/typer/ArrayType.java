package ca.applin.selmer.typer;

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
}
