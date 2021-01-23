package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;

public class Ast_Expression extends Ast {
    public Type type_info;

    public Ast_Expression(Type type_info) {
        this.type_info = type_info;
    }

    public Ast_Expression() {
        this.type_info = Type.UNKNOWN;
    }

}
