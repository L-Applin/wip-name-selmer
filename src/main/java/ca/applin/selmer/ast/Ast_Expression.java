package ca.applin.selmer.ast;

import ca.applin.selmer.interp.AstInterpreter;
import ca.applin.selmer.interp.InterpResult;
import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.typer.Type;
import java.util.List;

public abstract class Ast_Expression extends Ast {
    public Type type_info;
    public List<LexerToken> reversePolishNotation;

    public Ast_Expression(Type type_info) {
        this.type_info = type_info;
    }

    public Ast_Expression() {
        this.type_info = Type.UNKNOWN;
    }

    public void setReversePolishNotation(List<LexerToken> reversePolishNotation) {
        this.reversePolishNotation = reversePolishNotation;
    }

    public abstract InterpResult interp(AstInterpreter interpreter);
}
