package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Scope;
import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;

public class Ast_Variable_Decl extends Ast_Declaration {
    public String                identifier;
    public Type                  type;
    public Maybe<Ast_Expression> initialization;
    public boolean               is_const;
    public Scope                 scope;

    public Ast_Variable_Decl(String identifier, Type type, Maybe<Ast_Expression> initialization, Scope scope,
            boolean is_const) {
        this.identifier = identifier;
        this.type = type;
        this.initialization = initialization;
        this.is_const = is_const;
        this.scope = scope;
    }

    @Override
    public String toStringIndented(int level) {
        String self = DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Variable declaration: " + identifier
                + "\n" + DEFAULT_DEPTH_PER_LEVEL.repeat(level + 1) + type.toString()
                + "\n" + initialization
                             .map(ast -> ast.toStringIndented(level + 1))
                            .getOrElse(DEFAULT_DEPTH_PER_LEVEL.repeat(level + 1) + "<no init>");
        if (this.next != null) {
            String next_str = this.next.toStringIndented(level);
            return self + next_str;
        }
        return self ;
    }


    @Override
    public String toString() {
        return "Ast_Variable_Decleration %s%s: %s, init: %s".formatted(
                is_const ? "(const) " : "", identifier, type.toString(), initialization.isJust() ? "true" : "false");
    }
}
