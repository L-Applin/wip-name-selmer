package ca.applin.selmer.ast;

import ca.applin.selmer.typer.Type;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Ast_Funtion_Call extends Ast_Expression {
    public String                       function_name;
    public List<Ast_Expression>         args;

    public Ast_Funtion_Call(Type type_info, String function_name,
            List<Ast_Expression> args) {
        super(type_info);
        this.function_name = function_name;
        this.args = args;
    }

    public Ast_Funtion_Call(String function_name,
            List<Ast_Expression> args) {
        this.function_name = function_name;
        this.args = args;
    }

    @Override
    public String toString() {
        return "(Function_call %s (%s))"
                .formatted(function_name, args.stream().map(Ast::toString).collect(
                        Collectors.joining(", ")));
    }

    @Override
    public String toStringIndented(int level) {
        String func_name = DEFAULT_DEPTH_PER_LEVEL.repeat(level) + "Function: " + function_name + '\n';
        StringBuilder sb = new StringBuilder();
        for (Ast_Expression arg : args) {
            sb.append(arg.toStringIndented(level + 1));
        }
        return func_name + sb.toString();
    }
}
