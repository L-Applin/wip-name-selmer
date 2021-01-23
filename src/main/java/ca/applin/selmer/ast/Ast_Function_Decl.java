package ca.applin.selmer.ast;

import java.util.List;

public class Ast_Function_Decl extends Ast_Declaration {
    public String                   identifier;
    public List<Ast_Function_Arg>   parameters;
}
