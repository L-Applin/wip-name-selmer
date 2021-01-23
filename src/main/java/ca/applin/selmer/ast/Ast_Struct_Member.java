package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;

public class Ast_Struct_Member extends Ast_Declaration {
    public String  name;
    public Type    type;
    public boolean is_with;
}
