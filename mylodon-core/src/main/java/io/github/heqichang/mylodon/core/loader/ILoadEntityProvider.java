package io.github.heqichang.mylodon.core.loader;


import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.List;

/**
 * @author heqichang
 */
public interface ILoadEntityProvider<T, V> {

    List<V> load(List<T> data, ParameterGroup parameters);

    // 自定义键值，可能原始数据上存在多个 key ，比如 task_ids 字段保存的是 1,2,3 这样的值
    default List<String> matchKey(T data) {
        return null;
    }

}
