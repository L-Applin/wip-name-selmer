package ca.applin.selmer.interp;

import static ca.applin.selmer.typer.Type.F32;
import static ca.applin.selmer.typer.Type.F64;
import static ca.applin.selmer.typer.Type.S16;
import static ca.applin.selmer.typer.Type.S32;
import static ca.applin.selmer.typer.Type.S64;
import static ca.applin.selmer.typer.Type.S8;
import static ca.applin.selmer.typer.Type.U16;
import static ca.applin.selmer.typer.Type.U32;
import static ca.applin.selmer.typer.Type.U64;
import static ca.applin.selmer.typer.Type.U8;

import ca.applin.selmer.NotYetImplementedException;
import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Unop;
import ca.applin.selmer.typer.Type;

public class AstInterpreter implements Interpreter {

    @Override
    public InterpResult interp(Ast_Binop ast) {
        return switch (ast.operator) {
            case PLUS -> new InterpResult((Integer) interp(ast.left).value + (Integer) interp(ast.right).value);
            case TIMES -> new InterpResult((Integer) interp(ast.left).value * (Integer) interp(ast.right).value);
            case MINUS -> new InterpResult((Integer) interp(ast.left).value - (Integer) interp(ast.right).value);
            default -> throw new NotYetImplementedException(
                    "%s not yet interpretation implementd".formatted(ast.operator.toString()));
        };
    }

    @Override
    public InterpResult interp(Ast_Unop ast) {
        return switch (ast.operator) {
            case PRE_INCRE, POST_INCR -> new InterpResult((Integer) interp(ast.expr).value + 1);
            case PRE_DECR, POST_DECR -> new InterpResult((Integer) interp(ast.expr).value - 1);
            default -> throw new NotYetImplementedException(
                    "%s not yet interpretation implementd".formatted(ast.operator.toString()));
        };
    }

    @Override
    public InterpResult interp(Ast_Literral_Expr ast) {
        // dont care about data types for now
        if (ast.type_info.equals(S64) ||
            ast.type_info.equals(S32) ||
            ast.type_info.equals(S16) ||
            ast.type_info.equals(S8)  ||
            ast.type_info.equals(U64) ||
            ast.type_info.equals(U32) ||
            ast.type_info.equals(U16) ||
            ast.type_info.equals(U8 ) ||
            ast.type_info.equals(F64) ||
            ast.type_info.equals(F32)) {
                return new InterpResult(Integer.parseInt(ast.litteral_value));
        }
        throw new NotYetImplementedException("Currently only support integer type, but was given %s:%s"
            .formatted(ast.type_info, ast.litteral_value));
    }

    @Override
    public InterpResult interp(Ast_Expression ast) {
        return ast.interp(this);
    }
}
