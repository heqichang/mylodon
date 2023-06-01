package com.github.heqichang.mylodon.core.loader.parameter;

/**
 * @author heqichang
 */
class Parameter {

    private final ParameterOperation operation;

    private final String column;

    private final Object value;


    Parameter(ParameterOperation operation, String column, Object value) {
        this.operation = operation;
        this.column = column;
        this.value = value;
    }

    ParameterOperation getOperation() {
        return operation;
    }

    String getColumn() {
        return column;
    }

    Object getValue() {
        return value;
    }
}
