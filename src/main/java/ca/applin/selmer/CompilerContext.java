package ca.applin.selmer;

import ca.applin.selmer.ast.Ast;
import ca.applin.selmer.lexer.LexerException;
import ca.applin.selmer.lexer.LexerToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CompilerContext {

    public static final boolean _DEBUG = true;
    public Ast ast;

    public  <T, E extends RuntimeException> T emit_error(LexerToken lexerToken, String msg) {
        return emit_error(lexerToken.formatted_filename, lexerToken.line, lexerToken.col, msg, LexerException.class);
    }

    public <T, E extends RuntimeException> T emit_error(String filename, int line, int col, String msg, Class<E> exClass) {
        System.err.printf("%s:%s:%s %s%n", filename, line, col, msg);
        try {
            throw exClass.getConstructor(String.class).newInstance(msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T TODO() {
        throw new NotYetImplementedException();
    }

    public void not_yet_implemented(String msg) {
        throw new NotYetImplementedException(msg);
    }
}
