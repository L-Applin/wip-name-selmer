package ca.applin.selmer.parser;

import static ca.applin.selmer.CompilerContext._DEBUG;
import static ca.applin.selmer.typer.Type.simple;
import static org.junit.jupiter.api.Assertions.*;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.lexer.Lexer;
import ca.applin.selmer.lexer.LexerTokenStream;
import ca.applin.selmer.typer.ArrayType;
import ca.applin.selmer.typer.FunctionType;
import ca.applin.selmer.typer.TupleType;
import ca.applin.selmer.typer.Type;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    public void testParser_3_plus_5() {
        String expr = "3 + 5;";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("35+", getRpnAsString(ast));
        System.out.println(ast.toStringIndented(0));
    }

    @Test
    public void testParserWithParenthesis() {
        String expr = "2 * (3 + 4);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("234+*", getRpnAsString(ast));
        System.out.println(ast.toStringIndented(0));

        expr = "2 * 3 + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("23*4+", getRpnAsString(ast));

        expr = "(1 + 2) * (3 + 4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+*", getRpnAsString(ast));

        expr = "1 + 2 * 3 + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("123*4++", getRpnAsString(ast));

    }

    @Test
    public void testFunctionSingleArg() {
        String expr = "2 + f(3);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("23f+", getRpnAsString(ast));

        expr = "f(2) + 3;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3+", getRpnAsString(ast));

        expr = "f(2) + f(3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3f+", getRpnAsString(ast));

        expr = "f(2) + f(3) + g(4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3f4g++", getRpnAsString(ast));

        expr = "f(2) * f(3) + g(4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3f*4g+", getRpnAsString(ast));

        expr = "f(2) + f(3) * g(4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3f4g*+", getRpnAsString(ast));

        expr = "f(2) * (f(3) + g(4));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2f3f4g+*", getRpnAsString(ast));

        expr = "(fun1(2) + fun2(3)) * gun1(4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("2fun13fun2+4gun1*", getRpnAsString(ast));
    }

    @Test
    public void testFunctionMultipleArgs() {
        String expr = "f(1, 2);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12f", getRpnAsString(ast));
        System.out.println(ast.toStringIndented(0));

        expr = "f(1, 2, 3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("123f", getRpnAsString(ast));

        expr = "func(1, 2, 3, 4, 5, 6, 7);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("1234567func", getRpnAsString(ast));

        expr = "f(1+2, 3+4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+f", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+f", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4, 5 + 6);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+56+f", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4, 5 + 6) * 2;";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+56+f2*", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4, 5 + 6) * g(2);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+56+f2g*", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4, 5 + 6) * g(1 + 2);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+56+f12+g*", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4, 5 + 6) * g(2, 3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+56+f23g*", getRpnAsString(ast));

        expr = "f(1 + 2, 3 + 4) * g(4 + 5, 6 + 7) + h(8 + 9, 10 + 11);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+34+f45+67+g*89+1011+h+", getRpnAsString(ast));
    }

    @Test
    public void testFunctionCallWhithinFunctionCall() {
        String expr = "f(g(1));";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("1gf", getRpnAsString(ast));

        expr = "f(g(1 + 2));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+gf", getRpnAsString(ast));

        expr = "f(g(1 , 2));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12gf", getRpnAsString(ast));

        expr = "f(g(1 + 2), 3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+g3f", getRpnAsString(ast));

        expr = "f(g(1 + 2, 3));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+3gf", getRpnAsString(ast));

        expr = "f(g(1 + 2, 3)) + h(1);";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12+3gf1h+", getRpnAsString(ast));

        expr = "f(g(1) + h(2));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("1g2h+f", getRpnAsString(ast));

        expr = "f(g(1 + 2) + h(3));";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("12+g3h+f", getRpnAsString(ast));

        // f ( ( g ( ( 1 ) , ( 2 ) ) , ( h ( 3 ) ) )
        expr = "f(g(1, 2), h(3));";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("12g3hf", getRpnAsString(ast));

        expr = "f(g(1, 2), h(3, 4));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12g34hf", getRpnAsString(ast));

        expr = "f(g(1, 2), h(3, 4, s(5)));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12g345shf", getRpnAsString(ast));

        expr = "f(g(1, 2), h(3, 4), s(5));";
        ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("12g34h5sf", getRpnAsString(ast));

    }

    @Test
    public void testUnop() {
        String expr = "++1;";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        assertEquals("1++", getRpnAsString(ast));
    }

    @Test
    public void testDotNotation() {
        String expr = "str.len(2);";
        Ast_Expression ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("str 2 len .", getRpnAsString(ast, " "));

        expr = "str.len(2, 3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("str 2 3 len .", getRpnAsString(ast, " "));

        expr = "1 + str.len(2, 3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("1 str 2 3 len . +", getRpnAsString(ast, " "));

        expr = "str.len(2, 3) + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("str 2 3 len . 4 +", getRpnAsString(ast, " "));

        expr = "1 + str.len(2, 3) + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("1 str 2 3 len . 4 + +", getRpnAsString(ast, " "));

        expr = "(1 + str.len(2, 3)) + 4;";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("1 str 2 3 len . + 4 +", getRpnAsString(ast, " "));

        expr = "str.len(0, 1).plus(2).plus(3);";
        ast = (Ast_Expression) Parser.parseString(expr);
        System.out.println(ast.toStringIndented(0));
        assertEquals("str 0 1 len . 2 plus . 3 plus .", getRpnAsString(ast, " "));
    }


    private String getRpnAsString(Ast_Expression ast) {
        return ast.reversePolishNotation.stream()
                .map(t -> t.value)
                .collect(Collectors.joining());
    }

    private String getRpnAsString(Ast_Expression ast, String sep) {
        return ast.reversePolishNotation.stream()
                .map(t -> t.value)
                .collect(Collectors.joining(sep));
    }

}