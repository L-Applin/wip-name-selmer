package ca.applin.selmer.parser;

import static ca.applin.selmer.CompilerContext.TODO;
import static ca.applin.selmer.CompilerContext._DEBUG;
import static ca.applin.selmer.ast.Ast_Literral_Expr.from_lexer_token;
import static ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type.*;
import static com.applin.selmer.util.Maybe.just;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.NotYetImplementedException;
import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Compiler_Instruction;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Funtion_Call;
import ca.applin.selmer.ast.Ast_Operator.Operator;
import ca.applin.selmer.ast.Ast_Struct_Decl;
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
        // @Lang : do we really want to end each parsing unit to end with a semi colon ?
        if (is_compiler_instruction()) {
            Ast_Compiler_Instruction ast = parse_compiler_istructions();
            compilerContext.ast.add_next(ast);
        } else if (tokens.current().token_type == IDENTIFIER) {
            if (is_variable_declaration()) {
                Ast_Variable_Decl ast = parse_variable_decleration();
                compilerContext.ast.add_next(ast);
            } else if (is_struct_declaration()) {
                Ast_Struct_Decl ast = parse_struct_declaration();
                compilerContext.ast.add_next(ast);
            } else if (is_type_declaration()) {
                Ast_Type_Declaration ast = parse_type_declaration();
            }
        } else {
            switch (tokens.current().token_type) {

            }
        }
        return parse();
    }

    private Ast_Type_Declaration parse_type_declaration() {
        return TODO();
    }

    private boolean is_type_declaration() {
        return TODO();
    }

    private Ast_Struct_Decl parse_struct_declaration() {
        return TODO();
    }

    private boolean is_struct_declaration() {
        return TODO();
    }

    private Ast_Variable_Decl parse_variable_decleration() {
        return TODO();
    }

    private boolean is_variable_declaration() {
        LexerToken current = tokens.peek();
        LexerToken next = tokens.peek_next();
        LexerToken next_next = tokens.peek(2);
        return current.token_type == IDENTIFIER
                && (next.token_type == COLON || next.token_type == COLON_EQUALS || next.token_type == DOUBLE_COLON)
                && (next_next.token_type != KEYWORD_TYPE && next_next.token_type != KEYWORD_FUN);
    }

    private Ast_Compiler_Instruction parse_compiler_istructions() {
        return null;
    }

    private boolean is_compiler_instruction() {
        return tokens.tokens.get(0).token_type == HASH;
    }

    private Predicate<LexerToken> is_not(Lexer_Token_Type tok_type) {
        return it -> it.token_type != tok_type;
    }

    public Ast_Variable_Decl parse_var_decl() {
        assert tokens.current().token_type == IDENTIFIER :
                "variable parse must start with an identifier but started with " + tokens.current().toString();
        tokens.advance();

        LexerToken current_token = tokens.current();
        String identifier = current_token.value;
        return switch (current_token.token_type) {

            case COLON -> {
                tokens.advance();
                List<LexerToken> toks = tokens.take_while(t -> t.token_type != SEMI_COLON);
                Type type_info = parse_type(toks);
                LexerToken seperator = tokens.current();
                boolean is_const = switch (seperator.token_type) {
                    case COLON  -> true;
                    case EQUALS -> false;
                    default -> compilerContext.emit_error(filename, current_token.line, current_token.col,
                        "cannot parse variable declaration for variable '%s'".formatted(current_token.value), ParserException.class);
                };
                yield TODO();

            }

            case COLON_EQUALS -> {
                tokens.advance();
                Ast_Expression init_expr = parse_expr();
                yield new Ast_Variable_Decl(identifier, init_expr.type_info, just(init_expr), false);
            }

            case DOUBLE_COLON -> {
                tokens.advance();
                Ast_Expression init_expr= parse_expr();
                yield new Ast_Variable_Decl(identifier, init_expr.type_info, just(init_expr), true);
            }

            default -> compilerContext.emit_error(filename, current_token.line, current_token.col,
                    "Cannot parse variable declaration for variable '%s'".formatted(current_token.value), ParserException.class);
        };
    }

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
        if (toks.size() == 1) return Type.simple(toks.get(0).value);

        while (toks.get(0).token_type == NEW_LINE) {
            toks = toks.subList(1, toks.size());
        }
        LexerToken current = toks.get(0);

        // Array types
        if (current.token_type == OPEN_SQUARE_BRACKET) {
            assert toks.get(toks.size() - 1).token_type == CLOSE_SQUARE_BRACKET : "Array type missing closing bracket: " + toks.stream().map(t -> t.value).collect(Collectors.joining(" "));
            List<LexerToken> within = toks.subList(1, toks.size() - 1);
            Type base_type = parse_type(within);
            return new ArrayType(base_type);
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
        throw new NotYetImplementedException();
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
        return compilerContext.emit_error(func_name, "Cannot find closing parenthsis.");
    }


    public static void main(String[] args) throws Exception {
        Lexer lexer = new Lexer("/Users/Applin/Documents/develop/selmer/examples/types.sel", new CompilerContext());
        LexerTokenStream tokens = lexer.lex();
        Parser parser = new Parser(tokens, lexer.compilerContext, lexer.filename);
        parser.tokens.split().forEach( str -> {
            Type type = parser.parse_type(str.tokens);
            System.out.println(type);
        });
    }

}
