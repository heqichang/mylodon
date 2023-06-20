package io.github.heqichang.mylodon.core.loader.parameter;

/**
 * @author heqichang
 */
public class Parameter {

    private final ParameterOperation operation;

    private final String column;

    private final Object value;


    Parameter(ParameterOperation operation, String column, Object value) {
        this.operation = operation;
        this.column = column;
        this.value = value;
    }

    public ParameterOperation getOperation() {
        return operation;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }
}
