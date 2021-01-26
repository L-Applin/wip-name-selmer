package ca.applin.selmer.parser;

import static ca.applin.selmer.CompilerContext.TODO;
import static ca.applin.selmer.CompilerContext._DEBUG;
import static ca.applin.selmer.ast.Ast_Literral_Expr.from_lexer_token;
import static ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;
import static com.applin.selmer.util.Maybe.just;
import static com.applin.selmer.util.Maybe.nothing;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.NotYetImplementedException;
import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Compiler_Instruction;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Funtion_Call;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Operator.Operator;
import ca.applin.selmer.ast.Ast_Struct_Decl;
import ca.applin.selmer.ast.Ast_Struct_Member;
import ca.applin.selmer.ast.Ast_Type_Declaration;
import ca.applin.selmer.ast.Ast_Unop;
import ca.applin.selmer.ast.Ast_Variable_Decl;
import ca.applin.selmer.ast.Ast_Variable_Reference;
import ca.applin.selmer.lexer.Lexer;
import ca.applin.selmer.lexer.LexerToken;
import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type;
import ca.applin.selmer.lexer.LexerTokenStream;
import ca.applin.selmer.typer.ArrayType;
import ca.applin.selmer.typer.FunctionType;
import ca.applin.selmer.typer.TupleType;
import ca.applin.selmer.typer.Type;
import com.applin.selmer.util.Maybe;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Parser {
    public LexerTokenStream tokens;
    public CompilerContext compilerContext;
    public String filename;

    public Parser(LexerTokenStream tokens, CompilerContext compilerContext, String filename) {
        this.tokens = tokens;
        this.filename = filename;
        this.compilerContext = compilerContext;
    }

    public Ast parse() {
        // todo parse top level declaration: imports, etc
        if (tokens.end()) {
            return compilerContext.ast;
        }

        while (tokens.current().token_type == NEW_LINE) {
            tokens.advance();
        }

        // @Lang : do we really want to end each parsing unit to end with a semi colon ?
        if (is_compiler_instruction()) {
            Ast_Compiler_Instruction ast = parse_compiler_istructions();
            compilerContext.ast.add_next(ast);
        } else if (tokens.current().token_type == IDENTIFIER) {
            if (is_variable_declaration()) {
                Ast_Variable_Decl ast = parse_variable_decleration();
                compilerContext.add_to_ast(ast);
            } else if (is_struct_declaration()) {
                Ast_Struct_Decl ast = parse_struct_declaration();
                compilerContext.add_to_ast(ast);
                if (tokens.current().token_type == SEMI_COLON) tokens.advance();
            } else if (is_type_declaration()) {
                Ast_Type_Declaration ast = parse_type_declaration();
                compilerContext.add_to_ast(ast);
                if (tokens.current().token_type == SEMI_COLON) tokens.advance();
            }
        } else {
            switch (tokens.current().token_type) {

            }
        }
        return parse();
    }


    // <editor-fold desc="type-decl">
    // for type aliasing, ie `Predicate :: Type = Any -> Bool`
    private boolean is_type_declaration() {
        assert tokens.current().token_type == IDENTIFIER;
        LexerToken next = tokens.peek_next();
        LexerToken type_decl = tokens.peek(2);
        LexerToken equals_or_open_curly = tokens.peek(3);
        return (next.token_type == DOUBLE_COLON || next.token_type == COLON) // for error reporting
                && type_decl.token_type == KEYWORD_TYPE
                && equals_or_open_curly.token_type == EQUALS;
    }

    private Ast_Type_Declaration parse_type_declaration() {
        LexerToken identifier = tokens.current();
        tokens.assert_current(IDENTIFIER);
        tokens.advance();
        if (tokens.current().token_type == COLON) {
            // @Improvement parse the type for reporting
            compilerContext.emit_error(identifier, "Type declaration must be constant");
        }
        tokens.assert_current(DOUBLE_COLON, "Type `%s` has no double colon in its declaration.");
        tokens.advance();
        tokens.assert_current(KEYWORD_TYPE);
        tokens.advance();
        tokens.assert_current(OPEN_CURLY_BRACKET);
        tokens.advance();
        List<LexerToken> type_toks = tokens.take_while(is_not(SEMI_COLON));
        Type the_type = parse_type(type_toks);
        return new Ast_Type_Declaration(identifier.value, the_type);
    }
    // </editor-fold>

    // <editor-fold desc="struct-decl">
    //@Todo type parameters
    private boolean is_struct_declaration() {
        assert tokens.current().token_type == IDENTIFIER;
        LexerToken next = tokens.peek_next();
        LexerToken type_decl = tokens.peek(2);
        LexerToken equals_or_curly_open = tokens.peek(3);
        return (next.token_type == COLON || next.token_type == DOUBLE_COLON) // accept double colon for error reporting
                && type_decl.token_type == KEYWORD_TYPE
                && ((equals_or_curly_open.token_type == EQUALS && tokens.peek(4).token_type == OPEN_CURLY_BRACKET)
                || equals_or_curly_open.token_type == OPEN_CURLY_BRACKET);
    }

    private Ast_Struct_Decl parse_struct_declaration() {
        LexerToken identifier = tokens.current();
        tokens.advance();
        LexerToken double_colon = tokens.current();
        if (double_colon.token_type != DOUBLE_COLON) {
            compilerContext.emit_error(double_colon,
                "new type / struct `%s` declaration must be constant. Use `::` instead.".formatted(identifier.value));
        }
        tokens.advance();
        LexerToken type_token = tokens.current();
        assert type_token.token_type == KEYWORD_TYPE;
        tokens.advance();
        if (tokens.current().token_type == EQUALS) {
            tokens.advance(2);
        } else if (tokens.current().token_type == OPEN_CURLY_BRACKET) {
            tokens.advance();
        } else  {
            compilerContext.emit_error(identifier, "Error while parsing struct declaration, it must have am opening curly bracket.");
        }
        List<Ast_Struct_Member> members = new ArrayList<>();
        while (tokens.current().token_type != CLOSE_CURLY_BRACKET) {
            Ast_Struct_Member member = parse_struct_member_decl(identifier.value);
            members.add(member);
            while(tokens.current().token_type == NEW_LINE) {
                tokens.advance();
            }
        }
        tokens.advance();
        return new Ast_Struct_Decl(identifier.value, members);
    }

    private Ast_Struct_Member parse_struct_member_decl(String struct_name) {
        if (tokens.current().token_type == NEW_LINE) tokens.advance();
        boolean is_with = tokens.current().token_type == KEYWORD_WITH;
        if (is_with) tokens.advance();
        LexerToken identifier = tokens.current();
        tokens.advance();
        if (tokens.current().token_type != COLON) {
            compilerContext.emit_error(tokens.current(), "Struct member declaration name and type must be seperated by colons. Type: %s, member: %s"
                    .formatted(struct_name, identifier));
        }
        tokens.advance();
        List<LexerToken> type_tokens = tokens.take_while(t -> t.token_type != EQUALS && t.token_type != SEMI_COLON);
        Type type = parse_type(type_tokens);
        if (tokens.current().token_type == SEMI_COLON) {
            // no init
            tokens.advance();
            return new Ast_Struct_Member(identifier.value, type, is_with, nothing());
        }
        if (tokens.current().token_type != EQUALS) {
            compilerContext.emit_error(tokens.current(), "Struct member declaration type and initialisation expression must be seperated by an equal sign. Type: %s, member: %s"
                .formatted(struct_name, identifier));
        }

        tokens.advance();
        if (tokens.current().token_type == NEW_LINE) tokens.advance();
        Ast_Expression init_expr;
        if (tokens.current().is_litteral() && tokens.peek_next().token_type == SEMI_COLON) {
            init_expr = Ast_Literral_Expr.from_lexer_token(tokens.current());
            tokens.advance(2);  // 2: litteral and semi colon;
        } else {
            init_expr = parse_expr();
        }
        return new Ast_Struct_Member(identifier.value, type, is_with, just(init_expr));
    }
    // </editor-fold >

    // <editor-fold desc="var-decl">"
    private boolean is_variable_declaration() {
        LexerToken current = tokens.peek();
        LexerToken next = tokens.peek_next();
        LexerToken next_next = tokens.peek(2);
        return current.token_type == IDENTIFIER
                && (next.token_type == COLON || next.token_type == COLON_EQUALS || next.token_type == DOUBLE_COLON)
                && (next_next.token_type != KEYWORD_TYPE && next_next.token_type != KEYWORD_FUN);
    }

    private Ast_Variable_Decl parse_variable_decleration() {
        assert tokens.current().token_type == IDENTIFIER;
        LexerToken identifier = tokens.current();

        Ast_Variable_Decl variable_decl = switch (tokens.peek_next().token_type) {
            case COLON -> {
                tokens.advance(2);
                List<LexerToken> type_tokens = tokens.take_while(is_not(EQUALS));
                Type type = parse_type(type_tokens);
                while (tokens.current().token_type != SEMI_COLON && tokens.current().token_type != EQUALS) {
                    tokens.advance();
                }
                Maybe<Ast_Expression> init_expr = nothing();
                if (tokens.current().token_type == EQUALS) {
                    tokens.advance();
                    init_expr = just(parse_expr());
                }
                yield new Ast_Variable_Decl(identifier.value, type, init_expr, false);
            }

            default -> compilerContext.emit_error(identifier, "Error in variable declaration.");
        };
        return variable_decl;
    }
    // </editor-fold>


    private boolean is_compiler_instruction() {
        return tokens.tokens.get(0).token_type == HASH;
    }

    private Ast_Compiler_Instruction parse_compiler_istructions() {
        return null;
    }

    private Predicate<LexerToken> is_not(Lexer_Token_Type tok_type) {
        return it -> it.token_type != tok_type;
    }

    // <editor-fold desc="type-decl">
    // @Improvement, do we support Tuples types?
    /**
     * Assumes that current is at the beginning of the type, token after colon
     *  --> x: Int;             simple type, primitive
     *  --> p: Person           simple type, struct
     *  --> x: [Int];           Array type
     *  --> f: fun Int -> Int;  Functional type
     *  --> t: (Int, String)    Tuple type ???
     *
     *
     * @return the type that has been parsed
     *
     */
    private Type parse_type(List<LexerToken> toks) {
        if (_DEBUG) System.out.println("Parsing " + toks.stream().map(t -> t.value).collect(Collectors.joining(" ")));
        // size 0 ???
        // simple types

        while (toks.get(0).token_type == NEW_LINE) {
            toks = toks.subList(1, toks.size());
        }
        LexerToken current = toks.get(0);
        boolean is_pointer_type = current.token_type == AT_SIGN;
        if (toks.size() == 1) {
            if (current.is_litteral()) {
                return is_pointer_type ? Type.get_ptr_type_from_token(current) : Type.get_type_from_token(current);
            }
            return Type.simple(toks.get(0).value, is_pointer_type);
        }

        // Array types
        if (current.token_type == OPEN_SQUARE_BRACKET) {
            assert toks.get(toks.size() - 1).token_type == CLOSE_SQUARE_BRACKET : "Array type missing closing bracket: " + toks.stream().map(t -> t.value).collect(Collectors.joining(" "));
            List<LexerToken> within = toks.subList(1, toks.size() - 1);
            Type base_type = parse_type(within);
            return Type.array(base_type);
        }

        // function types
        if (current.token_type == KEYWORD_FUN) {
            if (toks.get(1).token_type == OPEN_PAREN) {
                // parse every type in between commas as arguments
                // find arrow index
                int open_paren_count = 0;
                int close_paren_count = 0;
                for (int i = 1; i < toks.size(); i++) {
                    LexerToken tok = toks.get(i);
                    if (tok.token_type == OPEN_PAREN) open_paren_count++;
                    if (tok.token_type == CLOSE_PAREN) close_paren_count++;
                    if (tok.token_type == LAMBDA_ARROW) {
                        if (open_paren_count == close_paren_count) {
                            List<Type> args_type = parse_tuple_type(toks.subList(1, i));
                            Type retur_type = parse_type(toks.subList(i + 1, toks.size()));
                            return new FunctionType(args_type, retur_type);
                        }
                    }
                }
            } else {
                // single argument, no parenthesis
                LexerToken arrow = toks.get(2);
                assert arrow.token_type == LAMBDA_ARROW : "Should be a lambda arrow, " + arrow.toString();
                Type arg_type = Type.simple(toks.get(1).value);
                Type return_type = parse_type(toks.subList(3, toks.size()));
                return new FunctionType(Collections.singletonList(arg_type), return_type);
            }
        }

        // Tuple type
        if (current.token_type == OPEN_PAREN) {
            List<Type> types = parse_tuple_type(toks);
            return new TupleType(types);
        }
        String info = toks.stream().map(t -> t.value).collect(Collectors.joining(" "));
        return compilerContext.emit_error(toks.get(0), "Cannot parse type %s.".formatted(info));
    }

    private List<Type> parse_tuple_type(List<LexerToken> tokens) {
        assert tokens.get(0).token_type == OPEN_PAREN : "Parsing tuple type must start with open paren: " + tokens.get(0).toString();
        assert tokens.get(tokens.size()-1).token_type == CLOSE_PAREN: "Parsing tuple type must end with open paren: " + tokens.get(tokens.size() - 1).toString();
        List<Type> types = new ArrayList<>();
        int current_index = 0;
        int open_parent = 0;
        int close_paren = 0;
        for (int i = 0; i < tokens.size(); i++) {
            LexerToken tok = tokens.get(i);
            if (tok.token_type == OPEN_PAREN) open_parent++;
            if (tok.token_type == CLOSE_PAREN) {
                close_paren++;
                if (open_parent == close_paren) {
                    Type right_type = parse_type(tokens.subList(current_index + 1, i));
                    types.add(right_type);
                    current_index = i;
                }
            }
            if (tok.token_type == COMMA && open_parent == close_paren + 1) {
                Type type = parse_type(tokens.subList(current_index + 1, i));
                types.add(type);
                current_index = i;
            }
        }
        return types;
    }
    // </editor-fold>


    // @Todo unary operators
    public Ast_Expression parse_expr() {
        ShuntingYardAlgorithm shuntingYardAlgorithmParser = new ShuntingYardAlgorithm(compilerContext);
        // if there is a function call, surround arguments with parenthesis in token stream
        if (_DEBUG) System.out.print("Tokens read: ");
        tokens.tokens.forEach(t -> System.out.print(t.value + " "));
        if (_DEBUG) System.out.println();
        tokens.tokens = surround_function_call_argument_with_parenthesis(tokens.tokens);
        List<LexerToken> reversePolishNotation = shuntingYardAlgorithmParser.reverse_polish_notation(new ArrayDeque<>(tokens.take_while(t -> t.token_type != SEMI_COLON)));
        if (_DEBUG) System.out.print("Reverse polish notation: ");
        reversePolishNotation.forEach(t -> System.out.print(t.value + " "));
        if (_DEBUG) System.out.println();
        Deque<Ast_Expression> expr_stack = new ArrayDeque<>();
        for (LexerToken token : reversePolishNotation) {
            if (token.is_litteral()) {
                expr_stack.push(from_lexer_token(token));
            } else if (token.is_operator()) {
                if (token.is_unary()) {
                    Ast_Expression expr = expr_stack.pop();
                    Ast_Unop unop = new Ast_Unop(Operator.from(token), expr);
                    expr_stack.push(unop);
                } else {
                     Ast_Expression left = expr_stack.pop();
                     Ast_Expression right = expr_stack.pop();
                     Ast_Binop binop = Ast_Binop.from_lexer_token(token, left, right);
                     expr_stack.push(binop);
                }
            } else if (token.is_function_call) {
                List<Ast_Expression> args = new ArrayList<>();
                for (int i = 0; i < token.function_arg_count; i++) {
                    args.add(expr_stack.pop());
                }
                Ast_Funtion_Call funtion_call = new Ast_Funtion_Call(token.value, args);
                expr_stack.push(funtion_call);
            } else if (token.token_type == IDENTIFIER) {
                Ast_Variable_Reference variable_reference = new Ast_Variable_Reference(token.value);
                expr_stack.push(variable_reference);
            }
        }
        Ast_Expression exrp = expr_stack.pop();
        assert tokens.current().token_type == SEMI_COLON : "End of expression parsing is not a semi colon. %s:%s:%s".formatted(filename, reversePolishNotation.get(0).line, reversePolishNotation.get(0).col);
        tokens.advance();
        return exrp;
    }

    // @Bug: some expression have a trailing close parenthesis, for exemple :
    private List<LexerToken> surround_function_call_argument_with_parenthesis(List<LexerToken> input) {
        int open_parenthesis_count = 0;
        int close_parenthesis_count = 0;
        int required_close_paren_to_add = 0;
        LinkedList<LexerToken> list_with_added_dummy_parenthesis = new LinkedList<>();
        for (int i = 0; i < input.size(); i++) {
            LexerToken it = input.get(i);
            list_with_added_dummy_parenthesis.add(it);
            if (i < input.size() - 1 && is_function_call(it, input.get(i + 1))) {
                it.is_function_call = true;
                int arg_amount = cout_argument(it, input.subList(i + 1, input.size()), compilerContext);
                it.function_arg_count = arg_amount;
                if (arg_amount > 1) {
                    list_with_added_dummy_parenthesis.add(new LexerToken(OPEN_PAREN, "(", it.filename, it.line, it.col));
                    required_close_paren_to_add++;
                }
            } else {
                switch (it.token_type) {
                    case COMMA -> {
                        list_with_added_dummy_parenthesis.add(list_with_added_dummy_parenthesis.size() - 1, new LexerToken(Lexer_Token_Type.CLOSE_PAREN, ")", it.filename, it.line, it.col));
                        list_with_added_dummy_parenthesis.add(new LexerToken(Lexer_Token_Type.OPEN_PAREN, "(", it.filename, it.line, it.col));
                    }

                    case OPEN_PAREN -> open_parenthesis_count++;

                    case CLOSE_PAREN -> {
                        close_parenthesis_count++;
                        if (close_parenthesis_count == open_parenthesis_count && required_close_paren_to_add > 0) {
                            list_with_added_dummy_parenthesis.add(
                                    new LexerToken(Lexer_Token_Type.CLOSE_PAREN, ")",
                                            it.filename, it.line, it.col));
                            close_parenthesis_count++;
                            required_close_paren_to_add--;
                        }
                    }
                }
            }
        }
        return list_with_added_dummy_parenthesis;
    }

    public static boolean is_function_call(LexerToken it, LexerToken top) {
        return it.token_type == Lexer_Token_Type.IDENTIFIER && top.token_type == Lexer_Token_Type.OPEN_PAREN;
    }

    public static int cout_argument(LexerToken func_name, List<LexerToken> input, CompilerContext compilerContext) {
        // edge case no args
        if (input.get(1).token_type == Lexer_Token_Type.CLOSE_PAREN) return 0;

        int open_parenthesis_count  = 0;
        int close_parenthesis_count = 0;
        int comma_seen = 0;
        for (LexerToken token : input) {
            if (token.token_type == Lexer_Token_Type.OPEN_PAREN)
                open_parenthesis_count++;
            else if (token.token_type == Lexer_Token_Type.CLOSE_PAREN) {
                // if we are done,
                close_parenthesis_count++;
                if (open_parenthesis_count == close_parenthesis_count) {
                    return comma_seen + 1;
                }
            } else if (token.token_type == Lexer_Token_Type.COMMA
                    && open_parenthesis_count == close_parenthesis_count + 1) {
                ++comma_seen;
            }
        }
        return compilerContext.emit_error(func_name, "Cannot find closing parenthesis.");
    }


    public static void main(String[] args) throws Exception {
        long t1 = System.currentTimeMillis();
        CompilerContext compilerContext = new CompilerContext();
        Lexer lexer = new Lexer("/Users/Applin/Documents/develop/selmer/examples/types.sel", compilerContext);
        LexerTokenStream tokens = lexer.lex();
        Parser parser = new Parser(tokens, compilerContext, lexer.filename);
        Ast ast = parser.parse();
        compilerContext.printy_ast(System.out);
        long t2 = System.currentTimeMillis();
        System.out.println("Time elapsed = " + (t2 - t1) + " ms");
    }

}
