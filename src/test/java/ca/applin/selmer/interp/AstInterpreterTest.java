package ca.applin.selmer.interp;

import static org.junit.jupiter.api.Assertions.*;

import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Funtion_Call;
import ca.applin.selmer.parser.Parser;
import org.junit.jupiter.api.Test;

class AstInterpreterTest {

    AstInterpreter interp = new AstInterpreter();

    @Test
    public void testInterpretSimpleExpression() {

        String expr = "3 + 5;";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(8, (int) interp.interp(ast).value);

        expr = "3 * 5;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(15, (int) interp.interp(ast).value);

        expr = "2 * (3 + 4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(14, (int) interp.interp(ast).value);

        expr = "2 * 3 + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(10, (int) interp.interp(ast).value);

        expr = "(1 + 2) * (3 + 4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(21, (int) interp.interp(ast).value);

        expr = "1 + 2 * 3 + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(11, (int) interp.interp(ast).value);
    }

    @Test
    public void testUnopInterpr() {
        String expr = "++3;";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(4, (int) interp.interp(ast).value);

        expr = "++11;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(12, (int) interp.interp(ast).value);

        expr = "20++;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(21, (int) interp.interp(ast).value);

        expr = "--3;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(2, (int) interp.interp(ast).value);

        expr = "--10;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(9, (int) interp.interp(ast).value);

        expr = "20--;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(19, (int) interp.interp(ast).value);

        expr = "3 + ++5;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(9, (int) interp.interp(ast).value);

        expr = "--(2 + 2);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(3, (int) interp.interp(ast).value);

        expr = "(10 + 10)--;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(19, (int) interp.interp(ast).value);

        expr = "++(10 + 10)--;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(20, (int) interp.interp(ast).value);

        expr = "++ ++(10 + 10);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(22, (int) interp.interp(ast).value);

        expr = "++(10 + ++10);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(22, (int) interp.interp(ast).value);

    }

    @Test
    public void testDivision() {
        String expr = "15/3;";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(5, (int) interp.interp(ast).value);

        expr = "12/5;"; // integer division
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(2, (int) interp.interp(ast).value);

    }

    @Test
    public void testBitSHifts() {
        String toFormat = "1<<%s;";
        String expr;
        Ast_Expression ast;
        for (int i = 0; i < 16; i++) {
            expr = toFormat.formatted(i);
            ast = (Ast_Expression) Parser.parseString(expr);
            assertEquals(1<<i, (int) interp.interp(ast).value);
        }

    }

    @Test
    public void testFunctionCall() {
        String expr = "2 + f(3);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(5, (int) interp.interp(ast).value);


        expr = "2 + f();";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(2, (int) interp.interp(ast).value);

    }

    @Test
    public void testFunctionCallAdd() {
        interp.setFunctionCallInterpreter(this::addTwoFunctionArgs);

        String expr = "f(2, 3);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(5, (int) interp.interp(ast).value);

        expr = "2 + f(3, 4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(9, (int) interp.interp(ast).value);

        expr = "2 + f(3 + 4, 5 - 6);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(8, (int) interp.interp(ast).value);

        expr = "f(2, f(3, 4));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(9, (int) interp.interp(ast).value);

        expr = "f(f(1, 2), f(3, 4));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals(10, (int) interp.interp(ast).value);

    }

    private InterpResult addTwoFunctionArgs(Ast_Funtion_Call ast) {
        return interp.interp(ast.args.get(0))
                .accumulate(this.interp.interp(ast.args.get(1)), Integer.class, Integer::sum);
    }


}