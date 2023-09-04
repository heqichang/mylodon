package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.heqichang.mylodon.core.loader.cache.LoadCountInfo;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author heqichang
 */
@SuppressWarnings({"unchecked"})
public class CountLoader<T> extends AbstractLoader {

    private final QueryWrapper<T> wrapper;

    private final LoadCountInfo<T> info;

    private final List<?> data;

    public CountLoader(LoadCountInfo<T> info, List<?> data) {
        this.wrapper = new QueryWrapper<>();
        this.info = info;
        this.data = data;
    }

    @Override
    public void load(ParameterGroup parameterGroup) {

        // 获取加载对象的字段名称
        String loadFiled = info.getLoadFieldName();
        Map<String, Long> countMap;

        // 有自定义实现，优先用自定义实现
        if (ObjectUtil.isNotNull(info.getProvider())) {
            countMap = info.getProvider().load(data, parameterGroup);
        } else {
            // 放 map 里 key 也保持 camel 的命名方式
            StringJoiner selectJoiner = new StringJoiner(",");
            for (String entityFieldColumnName : info.getEntityFieldColumnNames()) {
                selectJoiner.add(String.format("%s AS %s",
                        entityFieldColumnName, StringUtils.underlineToCamel(entityFieldColumnName)));
            }
            wrapper.select(selectJoiner.toString(), "COUNT(*) AS count");
            wrapFields(wrapper, info.getThisFieldColumnNames(), info.getEntityFieldColumnNames(), data);
            wrapParameter(wrapper, parameterGroup);
            wrapper.groupBy(info.getEntityFieldColumnNames());

            List<Map<String, Object>> mapList = info.getService().listMaps(wrapper);

            if (ObjectUtil.isEmpty(mapList)) {
                return;
            }

            countMap = buildCountMap(mapList);
        }

        if (ObjectUtil.isEmpty(countMap)) {
            return;
        }

        List<String> dataFields = convertToCamelFields(info.getThisFieldColumnNames());
        for (Object o : data) {
            String matchKey = buildFieldsKey(dataFields, o);
            if (countMap.containsKey(matchKey)) {
                Long count = countMap.get(matchKey);
                BeanUtil.setFieldValue(o, loadFiled, count);
            }
        }
    }

    private Map<String, Long> buildCountMap(List<Map<String, Object>> mapList) {

        Map<String, Long> countMap = new HashMap<>(mapList.size());

        // 理论来说应该 list 每一项就是一个 key-value 对应
        List<String> entityFields = convertToCamelFields(info.getEntityFieldColumnNames());
        for (Map<String, Object> map : mapList) {
            // 需要处理 enum 的值为原始值
            String key = buildFieldsKey(entityFields, map, true);
            countMap.put(key, Long.parseLong(map.get("count").toString()));
        }

        return countMap;
    }
}
