package ca.applin.selmer.interp;

import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Unop;

public interface Interpreter {
    InterpResult interp(Ast_Binop ast);
    InterpResult interp(Ast_Unop ast);
    InterpResult interp(Ast_Literral_Expr ast);
    InterpResult interp(Ast_Expression ast);


}
