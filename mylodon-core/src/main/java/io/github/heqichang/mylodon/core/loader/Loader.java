package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.experiment.loader.cache.LoadEntityCache;
import io.github.heqichang.mylodon.core.loader.cache.LoadEntityInfo;
import io.github.heqichang.mylodon.core.loader.cache.LoadInfo;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Loader {
    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass) {
        List<V> vList = BeanUtil.copyToList(data, vClass);
        loadList(vList);
        return vList;
    }

    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass, String ...loadFields) {
        List<V> vList = BeanUtil.copyToList(data, vClass);
        loadList(vList, loadFields);
        return vList;
    }


    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass, List<ParameterGroup> groups) {
        List<V> vList = BeanUtil.copyToList(data, vClass);
        loadList(vList, groups);
        return vList;
    }

    public static  <T, V> List<V> convertLoad(List<T> data, Class<V> vClass, List<ParameterGroup> groups, String ...loadFields) {
        List<V> vList = BeanUtil.copyToList(data, vClass);
        loadList(vList, groups, loadFields);
        return vList;
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass, String ...loadFields) {
        V v = BeanUtil.copyProperties(data, vClass);
        load(v, loadFields);
        return v;
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass) {
        V v = BeanUtil.copyProperties(data, vClass);
        load(v);
        return v;
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass, List<ParameterGroup> groups) {
        V v = BeanUtil.copyProperties(data, vClass);
        load(v, groups);
        return v;
    }

    public static  <T, V> V convertLoad(T data, Class<V> vClass, List<ParameterGroup> groups, String ...loadFields) {
        V v = BeanUtil.copyProperties(data, vClass);
        load(v, groups, loadFields);
        return v;
    }

    public static <T> void load(T data) {
        loadList(Collections.singletonList(data));
    }

    public static <T> void load(T data, String ...loadFields) {
        loadList(Collections.singletonList(data), null, loadFields);
    }

    public static <T> void load(T data, List<ParameterGroup> groups) {
        loadList(Collections.singletonList(data), groups);
    }

    public static <T> void load(T data, List<ParameterGroup> groups, String ...loadFields) {
        loadList(Collections.singletonList(data), groups, 0, loadFields);
    }

    public static <T> void loadList(List<T> data) {
        loadList(data, null, 0);
    }

    public static <T> void loadList(List<T> data, String ...loadFields) {
        loadList(data, null, 0, loadFields);
    }

    public static <T> void loadList(List<T> data, List<ParameterGroup> groups, String ...loadFields) {
        loadList(data, groups, 0, loadFields);
    }

    public static <T> void loadList(List<T> data, List<ParameterGroup> groups) {
        loadList(data, groups, 0);
    }

    private static <T> void loadList(List<T> data, List<ParameterGroup> groups, int deepLevel, String... loadFields) {

        if (ObjectUtil.isEmpty(data) || null == data.get(0)) {
            return;
        }

        Class<?> tClass = data.get(0).getClass();

        List<LoadInfo> infoList = LoadEntityCache.get(tClass);

        if (ObjectUtil.isEmpty(infoList)) {
            return;
        }

        List<CompletableFuture<Void>> taskList = new ArrayList<>(infoList.size() * 2);
        for (LoadInfo info : infoList) {

            if (ObjectUtil.isNotEmpty(loadFields) && !matchLoadFields(info, loadFields)) {
                continue;
            }

            ParameterGroup parameterGroup = findParameterGroup(info, deepLevel, groups);

            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {

                ILoader loader = info.createLoader(data);
                loader.load(parameterGroup);

                if (info instanceof LoadEntityInfo) {
                    LoadEntityInfo entityInfo = (LoadEntityInfo) info;
                    if (entityInfo.isDeepLoad()) {
                        List<?> deepLoadList = CollectionUtil.getFieldValues(data, info.getLoadFieldName(), true);
                        String[] nextLoadFields = findNextLoadFields(info, loadFields);
                        loadList(deepLoadList, groups, deepLevel + 1, nextLoadFields);
                    }
                }

            });

            taskList.add(task);
        }

        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
    }

    private static ParameterGroup findParameterGroup(LoadInfo info, int deepLevel, List<ParameterGroup> groups)  {
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

    private static String[] findNextLoadFields(LoadInfo info, String[] loadFields) {

        if (ArrayUtil.isEmpty(loadFields)) {
            return ArrayUtil.newArray(String.class, 0);
        }

        List<String> nextLoadFields = new ArrayList<>();

        for (String loadField : loadFields) {

            if (!loadField.contains(".")) {
                continue;
            }

            String[] matchFields = loadField.split("\\.");
            if (!matchFields[0].equals(info.getLoadFieldName())) {
                continue;
            }

            StringJoiner sj = new StringJoiner(".");
            for (int i = 1; i < matchFields.length; i++) {
                sj.add(matchFields[i]);
            }

            nextLoadFields.add(sj.toString());
        }

        return nextLoadFields.toArray(new String[0]);
    }

    private static boolean matchLoadFields(LoadInfo info, String[] loadFields) {

        for (String loadField : loadFields) {

            if (loadField.contains(".")) {
                // 说明不是当前 level 需要加载的字段
                continue;
            }

            if (info.getLoadFieldName().equals(loadField)) {
                return true;
            }
        }

        return false;
    }

}
