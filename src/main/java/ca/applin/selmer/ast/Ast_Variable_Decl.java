package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;

public class Ast_Variable_Decl extends Ast_Declaration {
    public String                identifier;
    public Type                  type;
    public Maybe<Ast_Expression> initialization;
    public boolean               is_const;

    public Ast_Variable_Decl(String identifier, Type type, Maybe<Ast_Expression> initialization, boolean is_const) {
        this.identifier = identifier;
        this.type = type;
        this.initialization = initialization;
        this.is_const = is_const;
    }
}
