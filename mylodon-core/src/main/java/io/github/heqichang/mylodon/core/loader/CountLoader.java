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

        String thisField = StringUtils.underlineToCamel(info.getThisFieldColumnName());
        // 获取加载对象的字段名称
        String loadFiled = info.getLoadFieldName();

        List<?> thisFieldDataList = CollectionUtil.getFieldValues(data, thisField, true);

        // 没有待提取的数据
        if (ObjectUtil.isEmpty(thisFieldDataList)) {
            return;
        }

        Map<String, Long> countMap;

        // 有自定义实现，优先用自定义实现
        if (ObjectUtil.isNotNull(info.getProvider())) {

            countMap = info.getProvider().load(data, parameterGroup);

        } else {
            wrapper.select(String.format("%s AS id", info.getEntityFieldColumnName()), "COUNT(*) AS count");
            wrapper.in(info.getEntityFieldColumnName(), thisFieldDataList);
            buildWrapper(wrapper, parameterGroup);
            wrapper.groupBy(info.getEntityFieldColumnName());

            List<Map<String, Object>> mapList = info.getService().listMaps(wrapper);

            if (ObjectUtil.isEmpty(mapList)) {
                return;
            }

            countMap = buildCountMap(mapList);
        }

        for (Object o : data) {
            String matchKey = BeanUtil.getProperty(o, thisField).toString();
            if (countMap.containsKey(matchKey)) {
                Long count = countMap.get(matchKey);
                BeanUtil.setFieldValue(o, loadFiled, count);
            }
        }
    }

    private Map<String, Long> buildCountMap(List<Map<String, Object>> mapList) {

        Map<String, Long> countMap = new HashMap<>(mapList.size() * 2);

        // 理论来说应该 list 每一项就是一个 key-value 对应
        for (Map<String, Object> map : mapList) {
            countMap.put(map.get("id").toString(), Long.parseLong(map.get("count").toString()));
        }

        return countMap;
    }
}
