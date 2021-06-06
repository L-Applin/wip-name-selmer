package ca.applin.selmer.ast;

import ca.applin.selmer.interp.InterpResult;
import ca.applin.selmer.interp.Interpreter;
import java.io.Serializable;

public abstract class Ast implements Serializable {
    public static final Ast EMPTY = new Ast() { public String toString() { return "<empty>"; } };
    public Ast next;
    public static String DEFAULT_DEPTH_PER_LEVEL = "    ";
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + toString() + (next == null ? "" : next.toStringIndented(level));
    }

    public void add_next(Ast ast) {
        if (next == null) {
            next = ast;
            return;
        }
        next.add_next(ast);
    }

}

