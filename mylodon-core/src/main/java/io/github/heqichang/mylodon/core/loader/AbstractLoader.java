package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.heqichang.mylodon.core.loader.parameter.Parameter;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

/**
 * @author heqichang
 */
public abstract class AbstractLoader implements ILoader{

    protected <T> void buildWrapper(QueryWrapper<T> wrapper, ParameterGroup parameters) {

        if (ObjectUtil.isNull(parameters)) {
            return;
        }

        for (Parameter parameter : parameters) {

            switch (parameter.getOperation()) {
                case EQ:
                    wrapper.eq(parameter.getColumn(), parameter.getValue());
                    break;
                case GE:
                    wrapper.ge(parameter.getColumn(), parameter.getValue());
                    break;
                case GT:
                    wrapper.gt(parameter.getColumn(), parameter.getValue());
                    break;
                case LE:
                    wrapper.le(parameter.getColumn(), parameter.getValue());
                    break;
                case LT:
                    wrapper.lt(parameter.getColumn(), parameter.getValue());
                    break;
            }
        }
    }
}
