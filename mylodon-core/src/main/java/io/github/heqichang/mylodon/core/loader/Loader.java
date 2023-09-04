package io.github.heqichang.mylodon.core.loader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.heqichang.mylodon.core.loader.cache.LoadInfo;
import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Loader<T> {
    private List<T> dataList;

    private List<ParameterGroup> parameterGroupList;

    private List<String> loadFieldList;

    private List<String> skipFieldList;

    private int deepLevel = 0;

    private Loader() {}

    public static <T> Loader<T> init(T data) {
        Loader<T> loader = new Loader();
        loader.dataList = CollectionUtil.toList(data);
        return loader;
    }

    public static <T, V> Loader<T> init(V data, Class<T> tClass) {
        Loader<T> loader = new Loader();
        List<V> vList = CollectionUtil.toList(data);
        loader.dataList = BeanUtil.copyToList(vList, tClass);
        return loader;
    }

    public static <T> Loader<T> init(List<T> dataList) {
        Loader<T> loader = new Loader();
        loader.dataList = dataList;
        return loader;
    }

    public static <T> Loader<T> init(List<?> dataList, Class<T> tClass) {
        Loader<T> loader = new Loader();
        loader.dataList = BeanUtil.copyToList(dataList, tClass);
        return loader;
    }

    public Loader<T> addParameterGroup(ParameterGroup parameterGroup) {
        if (null == parameterGroupList) {
            parameterGroupList = new ArrayList<>();
        }

        parameterGroupList.add(parameterGroup);
        return this;
    }

    public Loader<T> loadField(String... fields) {
        if (null == loadFieldList) {
            loadFieldList = new ArrayList<>(fields.length);
        }

        skipFieldList = null;
        loadFieldList.addAll(Arrays.asList(fields));

        return this;
    }

    public Loader<T> skipField(String... fields) {
        if (null == skipFieldList) {
            skipFieldList = new ArrayList<>(fields.length);
        }

        loadFieldList = null;
        skipFieldList.addAll(Arrays.asList(fields));

        return this;
    }

    public Loader<T> load() {
        load(dataList);
        return this;
    }

    public T one() {
        if (ObjectUtil.isEmpty(dataList)) {
            return null;
        }

        return dataList.get(0);
    }

    public List<T> list() {
        return dataList;
    }

    private void load(List<?> loadList) {

        if (ObjectUtil.isEmpty(loadList) || ObjectUtil.isNull(dataList.get(0))) {
            return;
        }

        Class<?> tClass = loadList.get(0).getClass();

        List<LoadInfo> infoList = com.ruoyi.common.experiment.loader.cache.LoadInfoCache.get(tClass);

        if (ObjectUtil.isEmpty(infoList)) {
            return;
        }

        List<CompletableFuture<Void>> taskList = new ArrayList<>(infoList.size());
        for (LoadInfo info : infoList) {

            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {

                if ((ObjectUtil.isEmpty(loadFieldList) && ObjectUtil.isEmpty(skipFieldList)) ||
                        (ObjectUtil.isNotEmpty(loadFieldList) && matchFields(info, loadFieldList)) ||
                        (ObjectUtil.isNotEmpty(skipFieldList) && !matchFields(info, skipFieldList))) {

                    ParameterGroup parameterGroup = findParameterGroup(info);

                    // 加载
                    ILoader loader = info.createLoader(loadList);
                    loader.load(parameterGroup);
                }

                if (info.isDeepLoad()) {
                    List<?> deepLoadList = CollectionUtil.getFieldValues(loadList, info.getLoadFieldName(), true);
                    List<String> nextLoadFieldList = findNextFields(info, loadFieldList);
                    List<String> nextSkipFieldList = findNextFields(info, skipFieldList);
                    Loader nextLoader = Loader.init(deepLoadList);
                    nextLoader.deepLevel = deepLevel + 1;
                    nextLoader.parameterGroupList = parameterGroupList;
                    nextLoader.loadFieldList = nextLoadFieldList;
                    nextLoader.skipFieldList = nextSkipFieldList;
                    nextLoader.load();
                }
            });

            taskList.add(task);
        }

        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
    }

    private ParameterGroup findParameterGroup(LoadInfo info)  {
        if (ObjectUtil.isNull(info) || ObjectUtil.isEmpty(parameterGroupList)) {
            return null;
        }

        for (ParameterGroup parameters : parameterGroupList) {
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

    private List<String> findNextFields(LoadInfo info, List<String> fieldList) {

        if (ObjectUtil.isEmpty(fieldList)) {
            return null;
        }

        List<String> nextFields = new ArrayList<>();

        for (String loadField : fieldList) {

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

            nextFields.add(sj.toString());
        }

        return nextFields;
    }

    private boolean matchFields(LoadInfo info, List<String> fieldList) {

        for (String loadField : fieldList) {

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
