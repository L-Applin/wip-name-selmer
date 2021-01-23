package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;

public class Ast_Function_Arg extends Ast_Declaration {
    public String        identifier;
    public Maybe<String> name;
    public Type          type;
}
