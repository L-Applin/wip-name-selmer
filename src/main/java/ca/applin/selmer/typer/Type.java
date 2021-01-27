package ca.applin.selmer.typer;

import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.lexer.LexerToken;
import com.applin.selmer.util.ShouldNeverHappenedException;
import java.util.Objects;

public class Type extends Ast {
    public static final Type UNKNOWN = new Type("", false);
    public static final Type VOID = new Type("void", true);
    public static Type simple(String type_value) { return new Type(type_value, true, false); }
    public static Type ptr(String base_type) { return new Type(base_type, true, true); }
    public static Type simple(String type_value, boolean ptr) {
        if (!ptr) return simple(type_value);
        return new Type(type_value, true, ptr);
    }


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
    public static final Type CHAR = simple("char");
    public static final Type STRING = simple("String");
    public static final Type array(Type base) { return new ArrayType(base); }

    public static final Type PTR_S64  = ptr("s64");
    public static final Type PTR_S32  = ptr("s32");
    public static final Type PTR_S16  = ptr("s16");
    public static final Type PTR_S8   = ptr("s8");
    public static final Type PTR_U64  = ptr("u64");
    public static final Type PTR_U32  = ptr("u32");
    public static final Type PTR_U16  = ptr("u16");
    public static final Type PTR_U8   = ptr("u8");
    public static final Type PTR_F64  = ptr("f64");
    public static final Type PTR_F32  = ptr("f32");
    public static final Type PTR_CHAR = ptr("char");


    public static Type get_type_from_token(LexerToken tok) {
        if (!tok.is_litteral()) return UNKNOWN;
        return switch (tok.token_type) {
            case PRIMITIVE_U8     -> U8;
            case PRIMITIVE_U16    -> U16;
            case PRIMITIVE_U32    -> U32;
            case PRIMITIVE_U64    -> U64;
            case PRIMITIVE_S8     -> S8;
            case PRIMITIVE_S16    -> S16;
            case PRIMITIVE_S32    -> S32;
            case PRIMITIVE_F64    -> F64;
            case PRIMITIVE_F32    -> F32;
            case PRIMITIVE_CHAR   -> CHAR;
            case PRIMITIVE_STRING -> STRING;
            case PRIMITIVE_S64, PRIMITIVE_INT -> S64; // Int is an alias for s64
            default -> throw new ShouldNeverHappenedException(
                    "Type %s returns true to `is_litteral` but is not in the litteral switch case".formatted(tok.toString()));
        };
    }

    public static Type get_ptr_type_from_token(LexerToken tok) {
        if (!tok.is_litteral()) return UNKNOWN;
        return switch (tok.token_type) {
            case PRIMITIVE_U8     -> PTR_U8;
            case PRIMITIVE_U16    -> PTR_U16;
            case PRIMITIVE_U32    -> PTR_U32;
            case PRIMITIVE_U64    -> PTR_U64;
            case PRIMITIVE_S8     -> PTR_S8;
            case PRIMITIVE_S16    -> PTR_S16;
            case PRIMITIVE_S32    -> PTR_S32;
            case PRIMITIVE_F64    -> PTR_F64;
            case PRIMITIVE_F32    -> PTR_F32;
            case PRIMITIVE_CHAR   -> PTR_CHAR;
            case PRIMITIVE_S64, PRIMITIVE_INT -> PTR_S64; // Int is an alias for s64
            default -> throw new ShouldNeverHappenedException(
                    "Type %s returns true to `is_litteral` but is not in the litteral switch cas");
        };
    }

    public static Type get_type_from_litteral(LexerToken token) {
        assert token.is_litteral() : "Token %s is not litteral.".formatted(token);
        return switch (token.token_type) {
            case STRING_LITTERAL  -> STRING;
            case FLOAT_LITTERAL   -> F64;
            case INTEGER_LITTERAL -> S64;
            case HEX_LITTERAL     -> U64;
            case CHAR_LITTERAL    -> CHAR;
            default -> throw new ShouldNeverHappenedException(
                    "Type %s returns true to `is_litteral` but is not in the litteral switch case");
        };
    }

    public String type_value;
    public boolean is_know;
    public boolean is_pointer;

    public Type(String type_value) {
        this(type_value, true);
    }

    public Type(String type_value, boolean is_know) {
        this.type_value = type_value;
        this.is_know = is_know;
    }

    public Type(String type_value, boolean is_know, boolean is_pointer) {
        this.type_value = type_value;
        this.is_know = is_know;
        this.is_pointer = is_pointer;
    }


    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Type: " + toString();
    }

    @Override
    public String toString() {
        return type_value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Type type = (Type) o;
        return is_pointer == type.is_pointer &&
                type_value.equals(type.type_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type_value, is_pointer);
    }
}

