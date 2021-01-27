package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;

public class Ast_Function_Arg extends Ast_Declaration {
    public String        identifier;
    public Maybe<String> name;
    public Type          type;

    public Ast_Function_Arg(String identifier) {
        this(identifier, Maybe.nothing(), Type.UNKNOWN);
    }

    public Ast_Function_Arg(String identifier, Type type) {
        this(identifier, Maybe.nothing(), type);
    }

    public Ast_Function_Arg(String identifier, Maybe<String> name, Type type) {
        this.identifier = identifier;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "%s: %s%s".formatted(identifier, type.toString(), name.isJust() ? " = " + name.getOrElse("") : "");
    }

    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Function arg %s: %s".formatted(identifier, type.toStringIndented(0));
    }
}
