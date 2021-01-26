package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;

public class Ast_Struct_Member extends Ast_Declaration {
    public String                name;
    public Type                  type;
    public boolean               is_with;
    public Maybe<Ast_Expression> init_expr;

    public Ast_Struct_Member(String name, Type type, boolean is_with, Maybe<Ast_Expression> init_expr) {
        this.name = name;
        this.type = type;
        this.is_with = is_with;
        this.init_expr = init_expr;
    }

    @Override
    public String toStringIndented(int level) {
        return "%sMember: %s, %s%s".formatted(
                DEFAULT_DEPTH_PER_LEVEL.repeat(level),
                name,
                "Type: " + type.toString(),
                init_expr.map(expr -> expr.toStringIndented(level))
                        .getOrElse(""));
    }

    @Override
    public String toString() {
        return "(%s (%s)%s)"
            .formatted(name, type,
                    init_expr
                        .map(expr -> " (init %s)".formatted(init_expr.toString())).getOrElse(""));
    }
}
