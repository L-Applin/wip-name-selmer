package ca.applin.selmer.parser;

import static ca.applin.selmer.parser.Parser.cout_argument;
import static ca.applin.selmer.parser.Parser.is_function_call;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type;
import ca.applin.selmer.lexer.LexerToken;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ShuntingYardAlgorithm {

    public static final int UNKNOW_ARGUMENT_COUNT = -1;
    public CompilerContext compilerContext;

    public ShuntingYardAlgorithm(CompilerContext compilerContext) {
        this.compilerContext = compilerContext;
    }

    /*
    while there are tokens to be read:
        read a token.
        if the token is a number, then:
            push it to the output queue.
        else if the token is a function then:
            push it onto the operator stack
        else if the token is an operator then:
            while ((there is an operator at the top of the operator stack)
                  and ((the operator at the top of the operator stack has greater precedence)
                      or (the operator at the top of the operator stack has equal precedence and the token is left associative))
                  and (the operator at the top of the operator stack is not a left parenthesis)):
                pop operators from the operator stack onto the output queue.
            push it onto the operator stack.
        else if the token is a left parenthesis (i.e. "("), then:
            push it onto the operator stack.
        else if the token is a right parenthesis (i.e. ")"), then:
            while the operator at the top of the operator stack is not a left parenthesis:
                pop the operator from the operator stack onto the output queue.
            // If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
            if there is a left parenthesis at the top of the operator stack, then:
                pop the operator from the operator stack and discard it
            if there is a function token at the top of the operator stack, then:
                pop the function from the operator stack onto the output queue.
    // After while loop, if operator stack not null, pop everything to output queue
    if there are no more tokens to read then:
        while there are still operator tokens on the stack:
        // If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
        pop the operator from the operator stack onto the output queue.
    exit.
     */
    public List<LexerToken> reverse_polish_notation(Deque<LexerToken> input) {
        List<LexerToken> output = new ArrayList<>();
        Deque<LexerToken> operator_stack = new ArrayDeque<>();
        while (!input.isEmpty() && input.peek().token_type != Lexer_Token_Type.SEMI_COLON) {
            LexerToken it  = input.pop();
            LexerToken top = input.peek();
            boolean is_func_type = is_function_call(it, top);

            if (it.is_numeric() || it.token_type == Lexer_Token_Type.IDENTIFIER && !is_func_type) {
                output.add(it);

            }else if (it.is_litteral()) {
                output.add(it);
            } else if (is_func_type) {
                it.is_function_call = true;
                if (it.function_arg_count == UNKNOW_ARGUMENT_COUNT) {
                    it.function_arg_count = cout_argument(it, new ArrayList<>(input).subList(1, input.size()), compilerContext);
                }
                operator_stack.push(it);


            } else if (it.is_operator()) {
                while (!operator_stack.isEmpty() && ((operator_stack.peek().is_operator())
                        && (operator_stack.peek().precedence > it.precedence || (operator_stack.peek().precedence == it.precedence && it.is_left_associative()))
                        && operator_stack.peek().token_type != Lexer_Token_Type.OPEN_PAREN
                || operator_stack.peek().is_function_call)) {
                    output.add(operator_stack.pop());
                }
                operator_stack.push(it);

            } else if (it.token_type == Lexer_Token_Type.OPEN_PAREN) {
                operator_stack.push(it);

            } else if (it.token_type == Lexer_Token_Type.CLOSE_PAREN) {
                while (true) {
                    if (operator_stack.isEmpty()) {
                        compilerContext.emit_error(it, "Cannot parse expression %s, left parenthesis does not correspond to a right parenthesis.".formatted(it.getFormatted()));
                        break;
                    }
                    if (operator_stack.peek().token_type == Lexer_Token_Type.OPEN_PAREN) {
                        break;
                    }
                    output.add(operator_stack.pop()); // Error here should means mismatched parentheses
                }
                if (!operator_stack.isEmpty() && operator_stack.peek().token_type == Lexer_Token_Type.OPEN_PAREN) {
                    operator_stack.pop(); // discard it
                }
                if (!operator_stack.isEmpty() && operator_stack.peek().token_type == Lexer_Token_Type.IDENTIFIER) {
                    LexerToken maybe_func = operator_stack.pop();
                    if (!operator_stack.isEmpty() && operator_stack.peek().token_type == Lexer_Token_Type.OPEN_PAREN) {
                        // function
                        output.add(maybe_func);
                    } else {
                        operator_stack.push(maybe_func);
                    }
                }
            }
        }

        if (input.isEmpty()) {
            while (!operator_stack.isEmpty()) {
                output.add(operator_stack.pop());
            }
        }
        return output;
    }


}
