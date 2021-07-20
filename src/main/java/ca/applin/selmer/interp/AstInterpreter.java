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
import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Funtion_Call;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Unop;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class AstInterpreter implements Interpreter {

    private Function<Ast_Funtion_Call, InterpResult> dummyFunctionCallInterpreter =
            ast -> ast.args.isEmpty() ? new InterpResult(0) : interp(ast.args.get(0));

    public void setFunctionCallInterpreter(Function<Ast_Funtion_Call, InterpResult> functionCallInterpreter) {
        this.dummyFunctionCallInterpreter = functionCallInterpreter;
    }

    @Override
    public InterpResult interp(Ast_Binop ast) {

        // todo: floats, other datatypes
        BinaryOperator<Integer> op = switch (ast.operator) {
            case PLUS -> Integer::sum;
            case TIMES -> (i, j) -> i * j;
            case MINUS -> (i, j) -> i - j;
            case DIV -> (i, j) -> i / j;
            case BIT_SHIFT_RIGHT -> (i, j) -> i >> j;
            case BIT_SHIFT_LEFT, BIT_SHIFT_LEFT_SIGNED -> (i, j) -> i << j;
            case BIT_SHIFT_RIGHT_SIGNED -> (i, j) -> i >>> j;
            default -> throw new NotYetImplementedException(
                    "%s not yet interpretation implementd".formatted(ast.operator.toString()));
        };
        return binopInterp(ast, op);
    }

    @SuppressWarnings("unchecked")
    private <T> InterpResult binopInterp(Ast_Binop binop, BinaryOperator<T> f) {
        return new InterpResult(f.apply((T)interp(binop.left).value, (T)interp(binop.right).value));
    }

    private InterpResult unopInterp(Ast_Expression expression, UnaryOperator<Object> f) {
        return new InterpResult(f.apply(interp(expression).value));
    }

    @Override
    public InterpResult interp(Ast_Unop ast) {
        return switch (ast.operator) {
            case PRE_INCRE, POST_INCR -> unopInterp(ast.expr, i -> (int) i + 1);
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
    public InterpResult interp(Ast_Funtion_Call ast) {
        // todo real implementation of function call
        return dummyFunctionCallInterpreter.apply(ast);
    }

    @Override
    public InterpResult interp(Ast_Expression ast) {
        return ast.interp(this);
    }
}
