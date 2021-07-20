package ca.applin.selmer.lexer;

import static ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;
import static ca.applin.selmer.parser.ShuntingYardAlgorithm.UNKNOW_ARGUMENT_COUNT;

import java.io.File;

public class LexerToken {
    public static final String NUM_BASE    = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+/";
    public static final String NUM_BASE_16 = "0123456789ABCDEF";

    public static final int FUNCTION_CALL_FLAG        = 0x00000001;
    public static final int OPERATOR_FLAG             = 0x00000002;
    public static final int UNARY_FLAG                = 0x00000004;
    public static final int BINARY_OP_FLAG            = 0x00000004;
    public static final int NUMERIC_FLAG              = 0x00000010;
    public static final int LEFT_ASSOC_FLAG           = 0x00000020;
    public static final int LITTERAL_FLAG             = 0x00000040;
    public static final int UNARY_POST_INCR_FLAG      = 0x00000080;
    public static final int BOOLEAN_OPERATOR_FLAG     = 0x00000100;
    public static final int ASSIGNEMENT_OPERATOR_FLAG = 0x00000200;
    public static final int TYPE_FLAG                 = 0x00000400;
    public static final int PRIMITIVE_TYPE_FLAG       = 0x00000800;

    public enum Lexer_Token_Type {
        NEW_LINE,
        IDENTIFIER,
        KEYWORD_FUN,
        KEYWORD_TYPE,
        KEYWORD_STRUCT,
        KEYWORD_DEFER,
        KEYWORD_IF,
        KEYWORD_ELSE,
        KEYWORD_WHILE,
        KEYWORD_FOR,
        KEYWORD_NEW,
        KEYWORD_VOID,
        KEYWORD_INDEX,
        KEYWORD_ASSERT,
        KEYWORD_DELETE,
        KEYWORD_RETURN,
        KEYWORD_WITH,
        KEYWORD_UNIT,
        OPEN_PAREN,
        CLOSE_PAREN,
        OPEN_SQUARE_BRACKET,
        CLOSE_SQUARE_BRACKET,
        OPEN_CURLY_BRACKET,
        CLOSE_CURLY_BRACKET,
        DOT(10, OPERATOR_FLAG | LEFT_ASSOC_FLAG),
        COMMA(10, OPERATOR_FLAG),
        HASH,
        QUESTION_MARK,
        GT(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        DOUBLE_GT(OPERATOR_FLAG),
        TRIPLE_GT(OPERATOR_FLAG),
        GT_EQ(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        LT(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        DOUBLE_LT(OPERATOR_FLAG),
        TRIPLE_LT(OPERATOR_FLAG),
        LT_EQ(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        MINUS(4, OPERATOR_FLAG),
        MINUS_EQUALS,
        DOUBLE_MINUS(7, OPERATOR_FLAG | UNARY_FLAG),
        PLUS(4, OPERATOR_FLAG),
        DOUBLE_PLUS(7, OPERATOR_FLAG | UNARY_FLAG),
        PLUS_EQUALS(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        EQUALS,
        DOUBLE_EQUALS(3, OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        NOT_EQUALS(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        TIMES(5, OPERATOR_FLAG),
        DOUBLE_TIMES(6, OPERATOR_FLAG | LEFT_ASSOC_FLAG), // exponentiation
        TIMES_EQUALS,
        MOD(OPERATOR_FLAG),
        BACKSLASH,
        FORWARD_SLASH(5, OPERATOR_FLAG), // division
        FORWARD_SLASH_EQ,
        COLON,
        DOUBLE_COLON,
        COLON_EQUALS,
        SEMI_COLON,
        PIPE(OPERATOR_FLAG),
        DOUBLE_PIPE(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        AMPERSAND(OPERATOR_FLAG),
        DOUBLE_AMPERSAND(OPERATOR_FLAG | BOOLEAN_OPERATOR_FLAG),
        AT_SIGN,
        INTEGER_LITTERAL(LITTERAL_FLAG | NUMERIC_FLAG),
        FLOAT_LITTERAL(LITTERAL_FLAG | NUMERIC_FLAG),
        HEX_LITTERAL(LITTERAL_FLAG | NUMERIC_FLAG),
        CHAR_LITTERAL(LITTERAL_FLAG),
        STRING_LITTERAL(LITTERAL_FLAG),
        LAMBDA_ARROW,
        PRIMITIVE_U8(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_U16(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_U32(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_U64(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_S8(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_S16(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_S32(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_S64(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_F32(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_F64(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_INT(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_CHAR(PRIMITIVE_TYPE_FLAG),
        PRIMITIVE_STRING;

        public final int precedence;
        public final int flag;

        Lexer_Token_Type(int precedence, int flag) {
            this.precedence = precedence;
            this.flag = flag;
        }

        Lexer_Token_Type(int flag) {
            this(-1, flag);
        }

        Lexer_Token_Type() {
            this(-1, 0x0);
        }

    }

    public Lexer_Token_Type token_type;
    public String value;
    public String formatted_filename;
    public String filename;
    public int line;
    public int col;
    public int precedence;
    public boolean is_function_call;
    public int function_arg_count = UNKNOW_ARGUMENT_COUNT ;

    public LexerToken(Lexer_Token_Type tokenType, String value, String filename, int line, int col) {
        this.token_type = tokenType;
        this.filename = filename;
        File f = new File(filename);//@Improvement just parse and keep everything after last dash
        this.formatted_filename = f.getName();
        this.value = value;
        this.line = line;
        this.col = col;
        this.precedence = tokenType.precedence;
    }

    public LexerToken(Lexer_Token_Type tokenType, char[] value, String filename, int line, int col) {
        this(tokenType, String.valueOf(value), filename, line, col);
    }

    @Override
    public String toString() {
        String info = "%s:%d:%d".formatted(formatted_filename, line, col);
        if (info.length() < 15) {
            int pad = 15 - info.length();
            info += " ".repeat(pad);
        }
        return info + " " + getFormatted() + (is_function_call ? " fun: " + function_arg_count + " args" : "");
    }

    public String getFormatted() {
        return "%s:[%s]".formatted(token_type.name(), value.equals("\n") ? "\\n" : value);
    }

    public boolean is_litteral() {
        return check_flag(LITTERAL_FLAG);
    }

    public boolean is_numeric() {
        return token_type == INTEGER_LITTERAL
                || token_type == FLOAT_LITTERAL
                || token_type == HEX_LITTERAL;
    }

    public boolean is_unary() {
        return check_flag(UNARY_FLAG);
       // return token_type == DOUBLE_PLUS || token_type == DOUBLE_MINUS;
    }

    public boolean is_operator() {
        return token_type == GT
                || token_type == DOUBLE_GT
                || token_type == TRIPLE_GT
                || token_type == GT_EQ
                || token_type == LT
                || token_type == DOUBLE_LT
                || token_type == TRIPLE_LT
                || token_type == LT_EQ
                || token_type == MINUS
                || token_type == MINUS_EQUALS
                || token_type == DOUBLE_MINUS
                || token_type == PLUS
                || token_type == DOUBLE_PLUS
                || token_type == PLUS_EQUALS
                || token_type == DOUBLE_EQUALS
                || token_type == NOT_EQUALS
                || token_type == TIMES
                || token_type == DOUBLE_TIMES
                || token_type == TIMES_EQUALS
                || token_type == FORWARD_SLASH
                || token_type == FORWARD_SLASH_EQ
                || token_type == MOD
                || token_type == AMPERSAND
                || token_type == DOUBLE_AMPERSAND
                || token_type == DOT
                ;
    }

    public boolean is_left_associative() {
        return check_flag(LEFT_ASSOC_FLAG);
    }

    public boolean check_flag(int flag_to_check) {
        return (flag_to_check & token_type.flag) != 0;
    }

    public boolean is_expresion_start() {
        return is_litteral() || token_type == OPEN_PAREN
                            ||token_type == DOUBLE_PLUS
                            || token_type == DOUBLE_MINUS
                            || token_type == OPEN_SQUARE_BRACKET
                ;

    }

}
