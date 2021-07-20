package ca.applin.selmer.interp;

import java.util.function.BinaryOperator;

public class InterpResult {
    public Object value;

    public InterpResult(Object value) {
        this.value = value;
    }

    public InterpResult accumulate(InterpResult other, BinaryOperator<InterpResult> op) {
        return op.apply(this, other);
    }

    public <T> InterpResult accumulate(InterpResult other, Class<T> clz, BinaryOperator<T> op) {
        return new InterpResult(op.apply((T)this.value, (T)other.value));
    }

}
