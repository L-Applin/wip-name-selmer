package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;

public class Ast_Unop extends Ast_Operator {
    public Ast_Expression expr;

    public Ast_Unop(Operator operator, Ast_Expression expr) {
        super(operator);
        this.expr = expr;
    }

    public Ast_Unop(Operator operator, Ast_Expression expr, Type type) {
        super(type, operator);
        this.expr = expr;
    }


    @Override
    public String toStringIndented(int level) {
        String oper = expr.toStringIndented(level + 1);
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Unary op: " + operator.name() + '\n' + oper;
    }
}
