package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.heqichang.mylodon.core.loader.parameter.Parameter;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author heqichang
 */
public abstract class AbstractLoader implements ILoader{

    protected <T> void wrapParameter(QueryWrapper<T> wrapper, ParameterGroup parameters) {

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

    protected <T> void wrapFields(QueryWrapper<T> wrapper, List<String> dataFields, List<String> loadFields, List<?> data) {

        if (dataFields.size() == 0 || loadFields.size() == 0 || dataFields.size() != loadFields.size()) {
            throw new RuntimeException("字段数量不对应");
        }

        if (dataFields.size() == 1) {
            List<?> thisFieldDataList = CollectionUtil.getFieldValues(data,
                    StringUtils.underlineToCamel(dataFields.get(0)));
            wrapper.in(loadFields.get(0), thisFieldDataList);
        } else {
            wrapper.nested( q1 -> {
                for (int i = 0; i < data.size(); i++) {
                    Object o = data.get(i);
                    if (i == 0) {
                        q1.nested(q2 -> {
                            wrapFields(q2, dataFields, loadFields, o);
                        });
                    } else {
                        q1.or(q2 -> {
                            wrapFields(q2, dataFields, loadFields, o);
                        });
                    }
                }
            });
        }
    }

    protected <T> void wrapFields(QueryWrapper<T> wrapper, List<String> dataFields, List<String> loadFields, Object o) {

        for (int j = 0; j < dataFields.size(); j++) {
            // 常数处理
            if (loadFields.get(j).contains("'")) {
                // do nothing
            } else if (dataFields.get(j).contains("'")) {
                wrapper.eq(loadFields.get(j), dataFields.get(j));
            } else {
                wrapper.eq(loadFields.get(j), ReflectUtil.getFieldValue(o,
                        StringUtils.underlineToCamel(dataFields.get(j))));
            }
        }
    }

    protected List<String> convertToCamelFields(List<String> fields) {

        List<String> result = new ArrayList<>();
        for (String field : fields) {
            if (field.contains("'")) {
                result.add(field);
            } else {
                result.add(StringUtils.underlineToCamel(field));
            }
        }
        return result;
    }

    protected String buildFieldsKey(List<String> fields, Object o) {
        return buildFieldsKey(fields, o, false);
    }

    protected String buildFieldsKey(List<String> fields, Object o, boolean enumHandle) {
        StringJoiner keyJoiner = new StringJoiner("-");
        for (String field : fields) {

            // 常数处理
            if (field.contains("'")) {
                String strValue = field.substring(1, field.length() - 1);
                keyJoiner.add(strValue);
                continue;
            }

            Object value = BeanUtil.getProperty(o, field);

            if (enumHandle) {
                Class<?> valueClass = ClassUtil.getClass(value);
                if (valueClass.isEnum()) {
                    String name = "value";
                    if (!IEnum.class.isAssignableFrom(valueClass)) {
                        name = MybatisEnumTypeHandler.findEnumValueFieldName(valueClass)
                                .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", valueClass.getName())));
                    }
                    String method = StringUtils.underlineToCamel("get_" + name);
                    value = ReflectUtil.invoke(value, method);
                }
            }

            if (ObjectUtil.isNull(value)) {
                keyJoiner.add("");
            } else {
                keyJoiner.add(value.toString());
            }

        }

        return keyJoiner.toString();
    }
}
