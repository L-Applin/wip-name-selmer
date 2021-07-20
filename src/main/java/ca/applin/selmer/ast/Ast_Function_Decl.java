package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Scope;
import ca.applin.selmer.typer.Type;
import java.util.List;
import java.util.stream.Collectors;

public class Ast_Function_Decl extends Ast_Declaration {
    public String                   identifier;
    public Type                     type;
    public List<Ast_Function_Arg>   parameters;
    public Ast                      body;
    public Scope scope;

    public Ast_Function_Decl(String identifier, Type type,
            List<Ast_Function_Arg> parameters, Ast body, Scope scope) {
        this.identifier = identifier;
        this.type = type;
        this.parameters = parameters;
        this.body = body;
        this.scope = scope;
    }

    @Override
    public String toStringIndented(int level) {
        String params = parameters.stream().map(ast ->  ast.toStringIndented(level + 1)).collect(Collectors.joining("\n"));
        String body = DEFAULT_DEPTH_PER_LEVEL.repeat(level + 1) + "Function body:\n" + this.body.toStringIndented(level + 2);
        return "Function declaration: `%s` %s\n%s\n%s".formatted(identifier, type.toString(), params, body);
    }
}
