package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;

public class Ast_Type_Declaration extends Ast_Declaration {
    public String identifier;
    public Type type;

    public Ast_Type_Declaration(String identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String toStringIndented(int level) {
        String info = "Type alias: " + identifier + '\n' + type.toStringIndented(level + 1);
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) + info;
    }

    @Override
    public String toString() {
        return "(Type: %s (%s))"
                .formatted(identifier, type.toString());
    }
}
