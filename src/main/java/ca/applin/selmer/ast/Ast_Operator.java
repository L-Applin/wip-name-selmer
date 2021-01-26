package ca.applin.selmer.ast;


import static ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;

import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type;
import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.typer.Type;

public class Ast_Operator extends Ast_Expression {

    public static Ast_Operator from_lexer_token(LexerToken token) {
        return new Ast_Operator(Operator.from(token));
    }

    public enum Op_Type {
        UNARY, BINARY, OPEN_PAREN, CLOSE_PAREN, ASSIGN
    }

    public enum Operator {
        LEFT_PAREN(2, Op_Type.OPEN_PAREN),
        POST_INCR(2, Op_Type.CLOSE_PAREN),
        POST_DECR(2, Op_Type.UNARY),
        PRE_INCRE(3, Op_Type.UNARY),
        PRE_DECR(3),
        EXP(4, Op_Type.UNARY),
        MOD(5),
        DIV(5),
        TIMES(5),
        PLUS(6),
        MINUS(6),
        BIT_SHIFT_LEFT(7),
        BIT_SHIFT_LEFT_SIGNED(7),
        BIT_SHIFT_RIGHT(7),
        BIT_SHIFT_RIGHT_SIGNED(7),
        LT(9),
        LT_EQ(9),
        GT(9),
        GT_EQ(9),
        EQ(10),
        NOT_EQ(10),
        BIT_AND(11),
        BIT_XOR(12),
        BIT_OR(13),
        LOGICAL_AND(14),
        LOGICAL_OR(15),
        MINUS_EQ(16, Op_Type.ASSIGN),
        PLUS_EQ(16, Op_Type.ASSIGN),
        TIMES_EQ(16, Op_Type.ASSIGN),
        DIV_EQ(16, Op_Type.ASSIGN);
        final int precedence;
        final Op_Type op_type;
        Operator(int precedence) {
            this.precedence = precedence;
            this.op_type = Op_Type.BINARY;
        }

        Operator(int precedence, Op_Type op_type) {
            this.precedence = precedence;
            this.op_type = op_type;
        }

        public static Operator from(LexerToken lexerToken) {
            return switch (lexerToken.token_type) {
                     case GT -> Operator.GT;
                     case GT_EQ -> Operator.GT_EQ;
                     case DOUBLE_GT -> Operator.BIT_SHIFT_RIGHT;
                     case TRIPLE_GT -> Operator.BIT_SHIFT_RIGHT_SIGNED;
                     case LT -> Operator.LT;
                     case LT_EQ -> Operator.LT_EQ;
                     case DOUBLE_LT -> Operator.BIT_SHIFT_LEFT;
                     case TRIPLE_LT -> Operator.BIT_SHIFT_LEFT_SIGNED;
                     case MINUS -> Operator.MINUS;
                     case MINUS_EQUALS -> Operator.MINUS_EQ;
                     case DOUBLE_MINUS -> Operator.PRE_DECR;
                     case PLUS -> Operator.PLUS;
                     case PLUS_EQUALS -> Operator.PLUS_EQ;
                     case DOUBLE_PLUS -> Operator.PRE_INCRE;
                     case EQUALS -> Operator.EQ;
                     case NOT_EQUALS -> Operator.NOT_EQ;
                     case TIMES -> Operator.TIMES;
                     case DOUBLE_TIMES -> Operator.EXP;
                     case TIMES_EQUALS -> Operator.TIMES_EQ;
                     case FORWARD_SLASH -> Operator.DIV;
                     case FORWARD_SLASH_EQ -> Operator.DIV_EQ;
                     case MOD -> Operator.MOD;
                     case AMPERSAND -> Operator.BIT_AND;
                     case DOUBLE_AMPERSAND -> Operator.LOGICAL_AND;
                     case PIPE -> Operator.BIT_OR;
                     case DOUBLE_PIPE -> Operator.LOGICAL_OR;
                     default -> null; //@Cleanup
            };
        }
    }

    public Operator operator;

    public Ast_Operator(Operator operator) {
        this.operator = operator;
    }

    public Ast_Operator(Type type_info, Operator operator) {
        super(type_info);
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(Op " + operator.toString() + ")";
    }
}
