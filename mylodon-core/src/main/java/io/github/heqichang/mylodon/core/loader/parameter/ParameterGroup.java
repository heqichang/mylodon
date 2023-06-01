package io.github.heqichang.mylodon.core.loader.parameter;

import java.util.ArrayList;

/**
 * @author heqichang
 */
public class ParameterGroup extends ArrayList<Parameter> {

    private String field;

    public static ParameterGroup newGroup() {
        return new ParameterGroup();
    }

    public ParameterGroup add(ParameterOperation operation, String column, Object value) {
        add(new Parameter(operation, column, value));
        return this;
    }

    public ParameterGroup match(String field) {
        this.field = field;
        return this;
    }

    public String getField() {
        return field;
    }
}
