package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.heqichang.mylodon.core.loader.cache.LoadEntityCache;
import io.github.heqichang.mylodon.core.loader.cache.LoadEntityInfo;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Loader {

    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass) {
        return convertLoad(data, vClass, null);
    }

    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass, List<ParameterGroup> groups) {
        List<V> vList = BeanUtil.copyToList(data, vClass);
        loadList(vList, groups);
        return vList;
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass) {
        return convertLoad(data, vClass, null);
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass, List<ParameterGroup> groups) {
        V v = BeanUtil.copyProperties(data, vClass);
        load(v, groups);
        return v;
    }

    public static <T> void load(T data) {
        load(data, null);
    }

    public static <T> void load(T data, List<ParameterGroup> groups) {
        loadList(Collections.singletonList(data), groups);
    }

    public static <T> void loadList(List<T> data) {
        loadList(data, null);
    }

    public static <T> void loadList(List<T> data, List<ParameterGroup> groups) {
        loadList(data, groups, 0);
    }

    private static <T> void loadList(List<T> data, List<ParameterGroup> groups, int deepLevel) {

        if (ObjectUtil.isEmpty(data)) {
            return;
        }

        Class<?> tClass = data.get(0).getClass();

        List<LoadEntityInfo> infoList = LoadEntityCache.get(tClass);

        if (ObjectUtil.isEmpty(infoList)) {
            return;
        }

        List<CompletableFuture<Void>> taskList = new ArrayList<>(infoList.size() * 2);
        for (LoadEntityInfo info : infoList) {

            ParameterGroup parameterGroup = findParameterGroup(info, deepLevel, groups);

            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                EntityLoader loader = new EntityLoader(info, data);
                loader.load(parameterGroup);
                if (info.isDeepLoad()) {
                    List<?> deepLoadList = CollectionUtil.getFieldValues(data, info.getLoadFieldName());
                    loadList(deepLoadList, groups, deepLevel + 1);
                }
            });

            taskList.add(task);
        }

        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
    }

    private static ParameterGroup findParameterGroup(LoadEntityInfo info, int deepLevel, List<ParameterGroup> groups)  {
        if (ObjectUtil.isNull(info) || ObjectUtil.isEmpty(groups)) {
            return null;
        }

        for (ParameterGroup parameters : groups) {
            String matchField = parameters.getField();
            if (null == matchField) {
                continue;
            }
            String[] matchFields = matchField.split("\\.");
            if (deepLevel >= matchFields.length) {
                continue;
            }
            if (matchFields[deepLevel].equals(info.getLoadFieldName())) {
                return parameters;
            }
        }

        return null;
    }

}
