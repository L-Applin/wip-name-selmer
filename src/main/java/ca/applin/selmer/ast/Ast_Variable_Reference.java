package ca.applin.selmer.ast;

import ca.applin.selmer.interp.AstInterpreter;
import ca.applin.selmer.interp.InterpResult;
import ca.applin.selmer.typer.Type;

public class Ast_Variable_Reference extends Ast_Expression {
    public String var_name;

    public Ast_Variable_Reference(Type type_info, String var_name) {
        super(type_info);
        this.var_name = var_name;
    }

    public Ast_Variable_Reference(String var_name) {
        this.var_name = var_name;
    }

    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Variable: " + var_name + '\n';
    }

    @Override
    public InterpResult interp(AstInterpreter interpreter) {
        return interpreter.interp(this);
    }
}
