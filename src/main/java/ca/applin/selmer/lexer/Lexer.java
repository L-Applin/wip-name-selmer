package ca.applin.selmer.lexer;

import static ca.applin.selmer.CompilerContext._DEBUG;
import static ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;
import static ca.applin.selmer.lexer.Lexer.RESERVED_KEYWORD.*;
import static com.applin.selmer.util.StringUtils.str_dup_escape;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.StringUtils;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Stack;

/*
TODO
  * nested comments
  * number litterals (float, integers)
*/
public class Lexer {

    static String EMPTY_STRING = "";

    enum RESERVED_KEYWORD {
        IF("if"), U8("u8"), S8("s8"),
        FUN("fun"), NEW("new"), FOR("for"), U16("u16"), U32("u32"), U64("u64"), S16("s16"), S32("s32"), S64("s64"), F32("f32"), F64("f64"), INT("Int"),
        TYPE("type"), ELSE("else"), VOID("void"), CHAR("char"),
        WHILE("while"), DEFER("defer"), INDEX("index"),
        STRUCT("struct"), ASSERT("assert"), DELETE("delete"), RETURN("return"), STRING("String");
        final String value;
        RESERVED_KEYWORD(final String value) {
            this.value = value;
}
    }


    public static final char[] TOKEN_SEPERATOR = {
        ' ', '(', ')', '[', ']', '{', '}', '.', ',', '>', '<', '?', ':', ';', '\'', '\"',
            '!', '@', '#', '$', '%', '^', '&', '*', '+', '-', '=', '+', '`', '~', '|',
            '/',
            ' ', '\n'
    };

    LexerTokenStream tokens = new LexerTokenStream(new ArrayList<>());
    public CompilerContext compilerContext;

    public String filename;
    char[] file;
    char current;
    int pos, check, col = 0;
    int line = 1;

    private Lexer(CompilerContext compilerContext, String filename, char[] file) {
        this.compilerContext = compilerContext;
        this.filename = filename;
        this.file = file;
        this.current = file[0];
    }

    public static Lexer newInstanceFromFile(String filename) throws Exception {
        return newInstanceFromFile(filename, new CompilerContext());
    }

    public static Lexer newInstanceFromFile(String filename, CompilerContext compilerContext) throws Exception {
        // todo file not found
        // todo charset UTF-8 etc
        String fileContent = new String(new FileInputStream(filename).readAllBytes(), StandardCharsets.UTF_8);
        if (_DEBUG) {
            System.out.println("======== Lexing: " + filename + " ========");
        }
        return new Lexer(compilerContext, filename, fileContent.toCharArray());
    }

    public static Lexer newInstance(String toParse, CompilerContext compilerContext) {
        return new Lexer(compilerContext, "__from_string__", toParse.toCharArray());
    }

    public LexerTokenStream lex() {
        while (pos < file.length) {
            check = pos;

            if (isSeperator(current)) {
                switch (current) {
                    case ' ' : {
                        advance(1);
                        continue;
                    }
                    case '\n': {
                    tokens.put(new LexerToken(NEW_LINE, EMPTY_STRING, filename, line, col));
                        new_line();
                        continue;
                    }

                case '\'' : {
                        int len = make_char_litteral();
                        advance(len + 2); // for the quotes
                        assert file[pos - 1] == '\'';
                        continue;
                    }

                    case '\"' : {
                        int str_len = make_string_litteral();
                        advance(str_len + 2); // for the quotes
                        assert file[pos - 1] == '"';
                        continue;
                    }

                    case '/' : {
                        if (peek_next() == '/') {
                            int comment_length = 1;
                            while (check < file.length - 1 && file[++check] != '\n') {
                                comment_length++;
                            }
                            advance(comment_length);
                            continue;
                        }

                        if (peek_next() == '*') {
                            skip_over_multiline_comment();
                            continue;
                        }
                    }

                }
                int len = make_single_char_token();
                advance(len);
                continue;
            }

            if (Character.isDigit(current)) {
                int len = make_num_littral();
                advance(len);
                continue;
            }

            // check for number litteral


            do {
                check++;
            } while (check < file.length && !isSeperator(file[check]));

            int len = check - pos;

            LexerToken token = switch (len) {

                case 1 -> new LexerToken(IDENTIFIER, String.valueOf(current), filename, line, col);

                case 2 -> {
                    String str_value = String.copyValueOf(file, pos, 2);
                    yield switch (str_value) {
                        case "s8" -> new LexerToken(PRIMITIVE_S8, S8.value, filename, line, col);
                        case "u8" -> new LexerToken(PRIMITIVE_U8, INT.value, filename, line, col);
                        case "if" -> new LexerToken(KEYWORD_IF, IF.value, filename, line, col);
                        default   -> new LexerToken(IDENTIFIER, String.valueOf(file, pos, 2), filename, line, col);
                    };
                }

                case 3 -> {
                    String str_value = String.valueOf(file, pos, 3);
                    yield switch (str_value) {
                        case "for" -> new LexerToken(KEYWORD_FOR, FOR.value, filename, line, col);
                        case "new" -> new LexerToken(KEYWORD_NEW, NEW.value, filename, line, col);
                        case "fun" -> new LexerToken(KEYWORD_FUN, FUN.value, filename, line, col);
                        case "s16" -> new LexerToken(PRIMITIVE_S16, S16.value, filename, line, col);
                        case "s32" -> new LexerToken(PRIMITIVE_S32, S32.value, filename, line, col);
                        case "s64" -> new LexerToken(PRIMITIVE_S64, S64.value, filename, line, col);
                        case "u16" -> new LexerToken(PRIMITIVE_U16, U16.value, filename, line, col);
                        case "u32" -> new LexerToken(PRIMITIVE_U32, U32.value, filename, line, col );
                        case "u64" -> new LexerToken(PRIMITIVE_U64, U64.value, filename, line, col);
                        case "f32" -> new LexerToken(PRIMITIVE_F32, F32.value, filename, line, col);
                        case "f64" -> new LexerToken(PRIMITIVE_F64, F64.value, filename, line, col);
                        case "int" -> new LexerToken(PRIMITIVE_INT, INT.value, filename, line, col);
                        default -> new LexerToken(IDENTIFIER, str_value, filename, line, col);
                    };
                }

                case 4 -> {
                    String str_value = String.valueOf(file, pos, 4);
                    yield switch (str_value) {
                        case "type" -> new LexerToken(KEYWORD_TYPE, TYPE.value, filename, line, col);
                        case "else" -> new LexerToken(KEYWORD_ELSE, ELSE.value, filename, line, col);
                        case "void" -> new LexerToken(KEYWORD_VOID, VOID.value, filename, line, col);
                        case "char" -> new LexerToken(PRIMITIVE_CHAR, CHAR.value, filename, line, col);
                        default -> new LexerToken(IDENTIFIER, str_value, filename, line, col);
                    };
                }

                case 5 -> {
                    String str_value = String.valueOf(file, pos, 5);
                    yield switch (str_value) {
                        case "while" -> new LexerToken(KEYWORD_WHILE, WHILE.value, filename, line, col);
                        case "defer" -> new LexerToken(KEYWORD_DEFER, DEFER.value, filename, line, col);
                        case "index" -> new LexerToken(KEYWORD_INDEX, INDEX.value, filename, line, col);
                        default ->      new LexerToken(IDENTIFIER, str_value, filename, line, col);
                    };
                }

                case 6 -> {
                    String str_value = String.valueOf(file, pos, 6);
                    yield switch (str_value) {
                        case "struct" -> new LexerToken(KEYWORD_STRUCT, STRUCT.value, filename, line, col);
                        case "assert" -> new LexerToken(KEYWORD_ASSERT, ASSERT.value, filename, line, col);
                        case "delete" -> new LexerToken(KEYWORD_DELETE, DELETE.value, filename, line, col);
                        case "String" -> new LexerToken(PRIMITIVE_STRING, STRING.value, filename, line, col);
                        case "return" -> new LexerToken(KEYWORD_RETURN, RETURN.value, filename, line, col);
                        default ->      new LexerToken(IDENTIFIER, str_value, filename, line, col);
                    };
                }

                default ->  {
                    String str_value = String.valueOf(file, pos, len);
                    yield new LexerToken(IDENTIFIER, str_value, filename, line, col);
                }

            };

            tokens.put(token);
            advance(len);
        }
        return tokens;
    }

    public char peek_next() {
        // @check make sure \0 is OK
        if (pos + 1 >= file.length) return '\0';
        return file[pos + 1];
    }

    public char peek_next(int forward) {
        if (forward == 0) return current;
        if (forward == 1) return peek_next();
        return file[pos + forward];
    }


    private void new_line() {
        advance(1);
        line++;
        col = 0;
    }

    // @Improvement nested comments
    private void skip_over_multiline_comment() {
        // we are at the forward slahs starting a comment
        assert current == '/'    : "%s: %s:%s Beginning of comment parsing is not a forward slash".formatted(filename, line, col);
        assert peek_next() == '*': "%s:%s:%s Char after '/' in multiline comment is not a '*' ".formatted(filename, line, col);
        advance(2);
        int comment_size = 0;
        while (file[check] != '*' || file[check+1] != '/') {
            if (file[check] == '\n') line++;
            comment_size++;
            check++;
        }
        advance(comment_size + 2);
    }

    // @Improvement parse other bases
    private int make_num_littral() {
        assert Character.isDigit(current) : "Cannot 'parse_num_litteral' if current (%s) is not a digit".formatted(current+"");
        // try to find a dot or a seperator
        if (current == '0' && peek_next() == 'x') return parse_hex_litteral();

        // find next non digit
        check = pos;
        boolean is_floating_point = false;
        while (check < file.length - 1 && Character.isDigit(file[++check])) { }
        if (file[check] == '.') {
            is_floating_point = true;
            while (check < file.length - 1 && Character.isDigit(file[++check])) { }
        }

        int len = check - pos;
        char[] value = StringUtils.str_dup(file, pos, len);
        String str_value = new String(value);
        // @Hack, fix logic instead
        if (!Character.isDigit(str_value.charAt(len - 1))) {
            str_value = str_value.substring(0, str_value.length() - 1);
        }
        tokens.put(new LexerToken(is_floating_point ? FLOAT_LITTERAL : INTEGER_LITTERAL, str_value, filename, line, col));
        return len;

    }

    private int parse_hex_litteral() {
        assert current == '0' && peek_next() == 'x' : "Hex litteral mus start with with '0x---' but starts with " + current + peek_next();
        advance(2);
        while (check < file.length - 1 && LexerToken.NUM_BASE_16.indexOf(file[++check]) != -1) { }
        int len = check - pos;
        char[] value = StringUtils.str_dup(file, pos, check == file.length -1 ? ++len: len);
        tokens.put(new LexerToken(HEX_LITTERAL, new String(value), filename, line, col));
        return len;
    }

    private int make_char_litteral() {
        // for now, only char with a single value after backslash
        if (peek_next() == '\\') {
            char c = file[pos + 2];
            c = switch (c) {
                case 'n' -> '\n';
                case 't' -> '\t';
                case '0' -> '\0';
                case 'r' -> '\r';
                case 'f' -> '\f';
                case '\\' -> '\\';
                case '\'' -> '\'';
                case '\"' -> '\"';
                default -> compilerContext.emit_error(filename,line, col,  "not a valid char " + "\\" + c, LexerException.class);
            };
            tokens.put(new LexerToken(CHAR_LITTERAL, c + "", filename, line, col));
            return 2;
        } else {
            tokens.put(new LexerToken(CHAR_LITTERAL, peek_next() + "", filename, line, col));
            return 1;
        }
    }


    // @TODO deal with backslashes
    private int make_string_litteral() {
        // empty string edge case
        if (peek_next() == '"') {
            return 0;
        }
        int tmp = pos;
        char c = file[pos];
        while (tmp <= file.length && c != '\n') {
            c = file[++tmp];
            if (c == '"') {
                int tmp_2 = tmp-1;
                if (file[tmp_2] != '\\') {
                    int str_len = tmp - pos - 1;
                    tokens.put(new LexerToken(STRING_LITTERAL, str_dup_escape(file, pos + 1, str_len, '\''), filename, line, col));
                    return str_len;
                } else {
                    // there is an escape potential
                    char is_backslash = file[tmp_2];
                    int backslash_amount = 0;
                    while (is_backslash == '\\') {
                        is_backslash = file[--tmp_2];
                        backslash_amount++;
                    }
                    if (backslash_amount % 2 != 1) {
                        int str_len = tmp - pos - 1;
                        tokens.put(new LexerToken(STRING_LITTERAL, str_dup_escape(file, pos + 1, str_len, '\''), filename, line, col));
                        return str_len;
                    }
                }
            }
        }

        // TODO: throw Exception end of file reached without finding double quote
        String msg = "Missing end of String Litteral quote";
        return compilerContext.emit_error(filename, line, col, msg, LexerException.class);
    }

    private int make_single_char_token() {
        LexerToken token = switch (current) {
            case '+' -> switch (peek_next()) {
                case '+' -> new LexerToken(DOUBLE_PLUS, "++", filename, line, col);
                case '=' -> new LexerToken(PLUS_EQUALS, "+=", filename, line, col);
                default  -> new LexerToken(PLUS, "+", filename, line, col);
            };
            case '-' -> switch (peek_next()) {
                case '-' -> new LexerToken(DOUBLE_MINUS, "--", filename, line, col);
                case '=' -> new LexerToken(MINUS_EQUALS, "-=", filename, line, col);
                case '>' -> new LexerToken(LAMBDA_ARROW, "->", filename, line, col);
                default  -> new LexerToken(MINUS, "-", filename, line, col);
            };
            case '<' -> switch (peek_next()) {
                case '<' -> peek_next(2) == '<'
                            ? new LexerToken(TRIPLE_LT, "<<<", filename, line, col)
                            : new LexerToken(DOUBLE_LT, "<<", filename, line, col);
                case '=' -> new LexerToken(LT_EQ, "<=", filename, line, col);
                default  -> new LexerToken(LT, "<", filename, line, col);
            };
            case '>' -> switch (peek_next()) {
                case '>' -> peek_next(2) == '>'
                        ? new LexerToken(TRIPLE_GT, ">>>", filename, line, col)
                        : new LexerToken(DOUBLE_GT, ">>", filename, line, col);
                case '=' -> new LexerToken(GT_EQ, ">=", filename, line, col);
                default  -> new LexerToken(GT, ">", filename, line, col);
            };
            case '=' -> peek_next() == '='
                    ? new LexerToken(DOUBLE_EQUALS, "==", filename, line, col)
                    : new LexerToken(EQUALS, "=", filename, line, col);
            case '*' -> switch (peek_next()) {
                case '*' -> new LexerToken(DOUBLE_TIMES, "**", filename, line, col);
                case '=' -> new LexerToken(TIMES_EQUALS, "*=", filename, line, col);
                default  -> new LexerToken(TIMES, "*", filename, line, col);
            };
            case '(' -> new LexerToken(OPEN_PAREN, "(", filename, line, col);
            case ')' -> new LexerToken(CLOSE_PAREN, ")", filename, line, col);
            case '[' -> new LexerToken(OPEN_SQUARE_BRACKET, "[", filename, line, col);
            case ']' -> new LexerToken(CLOSE_SQUARE_BRACKET, "]", filename, line, col);
            case '{' -> new LexerToken(OPEN_CURLY_BRACKET, "{", filename, line, col);
            case '}' -> new LexerToken(CLOSE_CURLY_BRACKET, "}", filename, line, col);
            case '.' -> new LexerToken(DOT, ".", filename, line, col);
            case ',' -> new LexerToken(COMMA, ",", filename, line, col);
            case '?' -> new LexerToken(QUESTION_MARK, "?", filename, line, col);
            case ':' -> switch (peek_next()) {
                case ':' -> new LexerToken(DOUBLE_COLON, "::", filename, line, col);
                case '=' -> new LexerToken(COLON_EQUALS, ":=", filename, line, col);
                default  ->  new LexerToken(COLON, ":", filename, line, col);
            };
            case ';' -> new LexerToken(SEMI_COLON, ";", filename, line, col);
            case '%' -> new LexerToken(MOD, "%", filename, line, col);
            case '\\'-> new LexerToken(BACKSLASH, "\\", filename, line, col);
            case '/' -> switch (peek_next()) {
                case '=' -> new LexerToken(FORWARD_SLASH_EQ, "/=", filename, line, col);
                default -> new LexerToken(FORWARD_SLASH, "/", filename, line, col);
            };
            case '|' ->  switch (peek_next()) {
                case '|' -> new LexerToken(DOUBLE_PIPE, "||", filename, line, col);
                default  -> new LexerToken(PIPE, "|", filename, line, col);
            };
            case '&' -> switch (peek_next()) {
                case '&' -> new LexerToken(DOUBLE_AMPERSAND, "&&", filename, line, col);
                default  -> new LexerToken(AMPERSAND, "&", filename, line, col);
            };
            case '@' -> new LexerToken(AT_SIGN, "@", filename, line, col);
            case '!' -> peek_next() == '='
                    ? new LexerToken(NOT_EQUALS, "!=", filename, line, col)
                    : compilerContext.emit_error(filename, line, col, "cannot parse token after '!'", LexerException.class);

            // @Cleanup
            default -> {
                System.out.println(current);
                yield null;
            }
        };
        tokens.put(token);
        return token == null ? 1 : token.value.length();
    }

    private boolean isSeperator(char current) {
        for (char c : TOKEN_SEPERATOR) {
            if (c == current) return true;
        }
        return false;
    }

    private void advance(int i) {
        check = pos+=i;
        col+=i;
        if (pos < file.length) {
            current = file[pos];
        }
    }

    public static void main(String[] args) throws Exception {
        Lexer lexer = Lexer.newInstanceFromFile("/Users/Applin/Documents/develop/selmer/examples/str.sel", new CompilerContext());
        LexerTokenStream tokens = lexer.lex();
        tokens.log_all_file(System.out);
    }
}
