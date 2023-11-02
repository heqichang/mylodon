package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.heqichang.mylodon.core.loader.parameter.Parameter;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.*;

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
            List<Map<String, Object>> eqMapList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                Object o = data.get(i);
                Map<String, Object> eqMap = filterCondition(dataFields, loadFields, o);
                if (eqMap.size() > 0) {
                    eqMapList.add(eqMap);
                }
            }

            if (eqMapList.size() > 0) {
                wrapper.nested( q1 -> {
                    for (int i = 0; i < eqMapList.size(); i++) {
                        Map<String, Object> eqMap = eqMapList.get(i);
                        if (i == 0) {
                            q1.nested(q2 -> wrapFields(q2, eqMap));
                        } else {
                            q1.or(q2 -> wrapFields(q2, eqMap));
                        }
                    }
                });
            } else {
                // trick 说明没有匹配的条件，让它返回假条件
                wrapper.eq("1", 0);
            }
        }
    }

    protected <T> void wrapFields(QueryWrapper<T> wrapper, Map<String, Object> eqMap) {
        for (Map.Entry<String, Object> entry : eqMap.entrySet()) {
            wrapper.eq(entry.getKey(), entry.getValue());
        }
    }

    protected Map<String, Object> filterCondition(List<String> dataFields, List<String> loadFields, Object o) {

        Map<String, Object> eqMap = new HashMap<>();

        for (int j = 0; j < dataFields.size(); j++) {
            // 常数处理
            if (loadFields.get(j).contains("'")) {

                String loadValue = loadFields.get(j).substring(1, loadFields.get(j).length() - 1);
                Object dataValue = ReflectUtil.getFieldValue(o, StringUtils.underlineToCamel(dataFields.get(j)));

                // 筛选条件不对
                if (ObjectUtil.isNull(dataValue) || !StrUtil.equals(dataValue.toString(), loadValue)) {
                    return MapUtil.empty();
                }

            } else if (dataFields.get(j).contains("'")) {
                // 去掉前后 ' 符号
                String value = dataFields.get(j).substring(1, dataFields.get(j).length() - 1);
                eqMap.put(loadFields.get(j), value);

            } else {
                eqMap.put(loadFields.get(j), ReflectUtil.getFieldValue(o,
                        StringUtils.underlineToCamel(dataFields.get(j))));

            }
        }

        return eqMap;
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
                if (MybatisEnumTypeHandler.isMpEnums(valueClass)) {
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
