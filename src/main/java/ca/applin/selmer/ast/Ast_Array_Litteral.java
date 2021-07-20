package ca.applin.selmer.ast;

import ca.applin.selmer.interp.AstInterpreter;
import ca.applin.selmer.interp.InterpResult;
import java.util.List;
import java.util.stream.Collectors;

public class Ast_Array_Litteral extends Ast_Expression {

    public List<Ast_Expression> items;

    public Ast_Array_Litteral(List<Ast_Expression> items) {
        this.items = items;
    }

    @Override
    public InterpResult interp(AstInterpreter interpreter) {
        List<Object> res = items.stream().map(interpreter::interp).collect(Collectors.toList());
        return new InterpResult(res);
    }

    @Override
    public String toStringIndented(int level) {
        return DEFAULT_DEPTH_PER_LEVEL.repeat(level) +
                "Array %d:\n".formatted(items.size()) + items.stream()
                .map(item -> item.toStringIndented(level + 1))
                .collect(Collectors.joining());
    }
}
