package ca.applin.selmer.ast;

public class Ast_Return_Statement extends Ast_Statement {
    public Ast_Expression return_value;

    public Ast_Return_Statement(Ast_Expression return_value) {
        this.return_value = return_value;
    }

    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Return statement: \n" +
                return_value.toStringIndented(level + 1);
    }
}
