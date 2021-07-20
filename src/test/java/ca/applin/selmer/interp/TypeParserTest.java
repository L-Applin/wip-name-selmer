package ca.applin.selmer.interp;

import static ca.applin.selmer.typer.Type.simple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.lexer.Lexer;
import ca.applin.selmer.lexer.LexerTokenStream;
import ca.applin.selmer.parser.Parser;
import ca.applin.selmer.typer.ArrayType;
import ca.applin.selmer.typer.FunctionType;
import ca.applin.selmer.typer.TupleType;
import ca.applin.selmer.typer.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TypeParserTest {

    @Test
    public void testTypeParser() {
        String expr = "String";
        LexerTokenStream toks = Lexer.lexString(expr);
        Parser parser = new Parser(toks, new CompilerContext(), "");
        Type typeParsed = parser.parse_type(toks.tokens);
        Type expected = simple("String");
        assertEquals(expected, typeParsed);

        expr = "(String, Int)";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new TupleType(List.of(simple("String"), simple("Int")));
        assertEquals(expected, typeParsed);

        expr = "fun String -> Int";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new FunctionType(simple("String"), simple("Int"));
        assertEquals(expected, typeParsed);

        expr = "fun (String, String) -> Int";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new FunctionType(
                List.of(simple("String"), simple("String")),
                simple("Int"));
        assertEquals(expected, typeParsed);

        expr = "fun Unit -> fun String -> Int";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new FunctionType(
                simple("Unit"),
                new FunctionType(simple("String"), simple("Int")));
        assertEquals(expected, typeParsed);

        expr = "[String]";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new ArrayType(simple("String"));
        assertEquals(expected, typeParsed);

        expr = "[fun String -> Int]";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new ArrayType(new FunctionType(simple("String"), simple("Int")));
        assertEquals(expected, typeParsed);

        expr = "(fun String -> Int, ())";
        toks = Lexer.lexString(expr);
        parser = new Parser(toks, new CompilerContext(), "");
        typeParsed = parser.parse_type(toks.tokens);
        expected = new TupleType(List.of(new FunctionType(simple("String"), simple("Int")), Type.UNIT));
        assertEquals(expected, typeParsed);

    }
}
