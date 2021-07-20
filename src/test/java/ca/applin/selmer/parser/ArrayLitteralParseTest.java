package ca.applin.selmer.parser;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.lexer.Lexer;
import ca.applin.selmer.lexer.LexerTokenStream;
import org.junit.jupiter.api.Test;

public class ArrayLitteralParseTest {

    @Test
    public void arrayLitteralParse() {
        String expr = "[1, 2, 3, 4];";
        LexerTokenStream tokens = Lexer.lexString(expr);
        Parser p = new Parser(tokens, new CompilerContext(), "");
        System.out.println(p.parse().toStringIndented(0));
    }

}
