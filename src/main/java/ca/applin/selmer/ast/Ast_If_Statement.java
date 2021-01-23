package ca.applin.selmer.ast;

public class Ast_If_Statement extends Ast_Statement {
    public Ast_Expression  condition;
    public Ast             if_branch;
    public Ast             else_branch;

}
