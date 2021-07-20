package ca.applin.selmer.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Tuple_Litteral;
import ca.applin.selmer.lexer.Lexer;
import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type;
import ca.applin.selmer.lexer.LexerTokenStream;
import ca.applin.selmer.typer.Scope;
import ca.applin.selmer.typer.ScopeTest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TupleLitteralParseTest {

    @Test
    public void test_is_2tuple_true() {
        String expr = "(1, 2);";
        LexerTokenStream tokens = Lexer.lexString(expr);
        Parser p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(1 + 2, 3);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(1 + 2, 3 + 4);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1), 2);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1 + 2), 3);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1 + 2), 3 + 4);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1 + 2), g(3));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1, 2), g(3 + 4));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1 + 2), g(3, 4));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(f(1, 2), g(3, 4));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());

        expr = "(var1, var2);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertTrue(p.is_tuple_expr());
    }

    @Test
    public void test_is_tuple_false() {
        String expr = "(1 + 2);";
        LexerTokenStream tokens = Lexer.lexString(expr);
        Parser p = new Parser(tokens, new CompilerContext(), "");
        assertFalse(p.is_tuple_expr());

        expr = "(f(1, 2));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertFalse(p.is_tuple_expr());

        expr = "(f(1, 2) + 3);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertFalse(p.is_tuple_expr());

        expr = "(f(1, 2) + g(3, 4));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        assertFalse(p.is_tuple_expr());

    }

    @Test
    public void test_split_on_significant_comma() {
        String expr = "(1 + 2);";
        LexerTokenStream tokens = Lexer.lexString(expr);
        Parser p = new Parser(tokens, new CompilerContext(), "");
        List<List<LexerToken>> toks = p.seperate_on_significant_comma(tokens.tokens, Lexer_Token_Type.OPEN_PAREN, Lexer_Token_Type.CLOSE_PAREN);
        System.out.println(toks);

        expr = "(f(1 + 2), 3 + 4);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        toks = p.seperate_on_significant_comma(tokens.tokens, Lexer_Token_Type.OPEN_PAREN, Lexer_Token_Type.CLOSE_PAREN);
        System.out.println(toks);

    }

    @Test
    public void test_2tuple_parse() {
        String expr = "(1, 2);";
        LexerTokenStream tokens = Lexer.lexString(expr);
        Parser p = new Parser(tokens, new CompilerContext(), "");
        Ast_Expression parsed = p.parse_tuple_litteral(ScopeTest.TEST_SCOPE);

        expr = "(f(1 + 2, 3), 4 + 5);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        parsed = p.parse_tuple_litteral(ScopeTest.TEST_SCOPE);
        System.out.println(parsed.toStringIndented(0));

        expr = "(f(1 + 2), 3 + 4, 5);";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        parsed = p.parse_tuple_litteral(ScopeTest.TEST_SCOPE);
        System.out.println(parsed.toStringIndented(0));

        expr = "((1, 2), (3, 4));";
        tokens = Lexer.lexString(expr);
        p = new Parser(tokens, new CompilerContext(), "");
        parsed = p.parse_tuple_litteral(ScopeTest.TEST_SCOPE);
        System.out.println(parsed.toStringIndented(0));

    }

}
