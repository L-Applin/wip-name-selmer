package ca.applin.selmer.ast;

public class Ast_If_Statement extends Ast_Statement {
    public Ast_Expression  condition;
    public Ast             if_branch;
    public Ast             else_branch;

    @Override
    public String toString() {
        return "(If (%s) then (%s) else(%s))"
                .formatted(condition.toString(), if_branch.toString(), else_branch.toString());

    }
}
