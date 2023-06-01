package com.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.heqichang.mylodon.core.loader.cache.LoadEntityInfo;
import com.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.*;

/**
 * @author heqichang
 */
@SuppressWarnings({"unchecked"})
class EntityLoader<T> {

    private final QueryWrapper<T> wrapper;

    private final LoadEntityInfo<T> info;

    private final List<?> data;

    EntityLoader(LoadEntityInfo<T> info, List<?> data) {

        this.wrapper = new QueryWrapper<>();
        this.info = info;
        this.data = data;
    }

    void load(ParameterGroup parameterGroup) {

        String thisField = StringUtils.underlineToCamel(info.getThisFieldColumnName());
        // 获取加载对象的字段名称
        String loadFiled = info.getLoadFieldName();

        List<T> list;
        // 优先使用自定义数据源
        if (ObjectUtil.isNotNull(info.getProvider())) {
            list = info.getProvider().load(data, parameterGroup);
        } else {
            List<?> thisFieldDataList = CollectionUtil.getFieldValues(data, thisField);
            wrapper.in(info.getEntityFieldColumnName(), thisFieldDataList);
            list = info.getService().list(wrapper);
        }

        if (ObjectUtil.isEmpty(list)) {
            return;
        }

        Map<String, Object> map = buildEntityMap(list);
        for (Object o : data) {
            List<String> matchKeys = null;

            // 自定义键值，可能原始数据上存在多个 key ，比如 task_ids 字段保存的是 1,2,3 这样的值
            if (ObjectUtil.isNotNull(info.getProvider())) {
                matchKeys = info.getProvider().matchKey(o);
            }

            if (ObjectUtil.isEmpty(matchKeys)) {
                String matchKey = BeanUtil.getProperty(o, thisField).toString();
                matchKeys = Collections.singletonList(matchKey);
            }

            List<Object> newList = new ArrayList<>();

            for (String matchKey : matchKeys) {
                if (map.containsKey(matchKey)) {
                    // 考虑是否转型
                    if (!info.getLoadClass().equals(info.getEntityRawClass())) {
                        if (info.isOneToMany()) {
                            List<?> entityList = BeanUtil.copyToList((Collection<?>) map.get(matchKey), info.getLoadClass());
                            newList.addAll(entityList);
                        } else {
                            Object newValue = BeanUtil.copyProperties(map.get(matchKey), info.getLoadClass());
                            newList.add(newValue);
                        }
                    } else {
                        if (info.isOneToMany()) {
                            newList.addAll((Collection<?>) map.get(matchKey));
                        } else {
                            newList.add(map.get(matchKey));
                        }
                    }
                }
            }

            if (ObjectUtil.isNotEmpty(newList)) {
                if (info.isOneToMany()) {
                    BeanUtil.setFieldValue(o, loadFiled, newList);
                } else {
                    BeanUtil.setFieldValue(o, loadFiled, newList.get(0));
                }
            }
        }
    }

    private Map<String, Object> buildEntityMap(List<T> list) {

        Map<String, Object> map = new HashMap<>(list.size());

        // 获取加载对象的字段名称
        String entityField = StringUtils.underlineToCamel(info.getEntityFieldColumnName());

        for (T t : list) {

            String keyValue = BeanUtil.getProperty(t, entityField).toString();

            if (ObjectUtil.isNull(keyValue)) {
                continue;
            }

            if (info.isOneToMany()) {

                if (!map.containsKey(keyValue)) {
                    List<T> vList = new ArrayList<>();
                    map.put(keyValue, vList);
                }

                List<T> vList = (List<T>)  map.get(keyValue);
                vList.add(t);

            } else {
                map.put(keyValue, t);
            }
        }

        return map;

    }

}
