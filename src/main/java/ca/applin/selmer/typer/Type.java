package ca.applin.selmer.typer;

public class Type {
    public static Type UNKNOWN = new Type("", false);
    public static Type simple(String type_value) { return new Type(type_value); }

    public static final Type S64 = simple("s64");
    public static final Type S32 = simple("s32");
    public static final Type S16 = simple("s16");
    public static final Type S8  = simple("s8");
    public static final Type U64 = simple("u64");
    public static final Type U32 = simple("u32");
    public static final Type U16 = simple("u16");
    public static final Type U8  = simple("u8");
    public static final Type F64 = simple("f64");
    public static final Type F32 = simple("f32");
    public static final Type STRING = simple("String");
    public static final Type array(Type base) { return new ArrayType(base); }

    public String type_value;
    public boolean is_know;

    public Type(String type_value) {
        this(type_value, true);
    }

    public Type(String type_value, boolean is_know) {
        this.type_value = type_value;
        this.is_know = is_know;
    }


    @Override
    public String toString() {
        return "Type: " + type_value;
    }
}

