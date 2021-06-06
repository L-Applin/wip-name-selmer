package ca.applin.selmer.interp;

import static org.junit.jupiter.api.Assertions.*;

import ca.applin.selmer.ast.Ast_Expression;
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

}