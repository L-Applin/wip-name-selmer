package ca.applin.selmer.ast;

import ca.applin.selmer.interp.AstInterpreter;
import ca.applin.selmer.interp.InterpResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Ast_Tuple_Litteral extends Ast_Expression {

    public List<Ast_Expression> items;

    public Ast_Tuple_Litteral() {
        this.items = new ArrayList<>();
    }

    public Ast_Tuple_Litteral(List<Ast_Expression> items) {
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
                "Tuple %d:\n".formatted(items.size()) + items.stream()
                .map(item -> item.toStringIndented(level + 1))
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ast_Tuple_Litteral that = (Ast_Tuple_Litteral) o;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }
}
