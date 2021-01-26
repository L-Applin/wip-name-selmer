package ca.applin.selmer.ast;

import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.typer.Type;

public class Ast_Literral_Expr extends Ast_Expression {

    public static Ast_Literral_Expr from_lexer_token(LexerToken token) {
        Type type = Type.UNKNOWN;
        if (token.is_litteral()) {
            type = Type.get_type_from_litteral(token);
        }
        return new Ast_Literral_Expr(token.value, type, token.filename, token.line, token.col);
    }

    public String litteral_value;
    public String filename;
    public int line;
    public int col;

    public Ast_Literral_Expr(String litteral_value, String filename, int line, int col) {
        this.litteral_value = litteral_value;
        this.filename = filename;
        this.line = line;
        this.col = col;
    }

    public Ast_Literral_Expr(String litteral_value, Type type, String filename, int line, int col) {
        super(type);
        this.litteral_value = litteral_value;
        this.filename = filename;
        this.line = line;
        this.col = col;
    }


    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Litteral: [" + litteral_value + "]\n";
    }

    @Override
    public String toString() {
        return "(Litteral " + litteral_value + ")";
    }
}
