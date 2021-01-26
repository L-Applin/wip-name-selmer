package ca.applin.selmer.ast;

import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;
import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.parser.ParserException;

public class Ast_Binop extends Ast_Operator {
    public Ast_Expression   left;
    public Ast_Expression   right;

    public Ast_Binop(Operator operator) {
        super(operator);
    }

    public Ast_Binop(Operator operator, Ast_Expression left, Ast_Expression right) {
        super(operator);
        this.left = left;
        this.right = right;
    }

    public static Ast_Binop from_lexer_token(LexerToken op) {
        Operator operator = Operator.from(op);
        if (operator == null) {
            throw new ParserException("Trying to create Operator from token %s".formatted(op.toString()));
        }
        return new Ast_Binop(operator);
    }

    public static Ast_Binop from_lexer_token(LexerToken op, Ast_Expression left, Ast_Expression right) {
        Operator operator = Operator.from(op);
        if (operator == null) {
            throw new ParserException("Trying to create Operator from token %s".formatted(op.toString()));
        }
        return new Ast_Binop(operator, left, right);
    }

    public boolean is_left_associative() {
        return operator == Operator.MINUS || operator == Operator.DIV; // @Cleanup check for other left associative operator
    }

    public int precedence() {
        return operator.precedence;
    }

    @Override
    public String toStringIndented(int level) {
        String left_str = left.toStringIndented(level + 1);
        String right_str = right.toStringIndented(level + 1);
        if (right_str.endsWith("\n")) {
            right_str = right_str.substring(0, right_str.length() - 1);
        }
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Binop: " + operator.name() + '\n' +
                left_str + right_str + '\n';
    }

    @Override
    public String toString() {
        return "(Binop (%s %s %s))".formatted(operator.toString(), left.toString(), right.toString());
    }
}
