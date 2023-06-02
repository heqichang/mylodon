package io.github.heqichang.mylodon.core.loader;


import io.github.heqichang.mylodon.core.loader.parameter.ParameterGroup;

import java.util.List;

/**
 * 自定义关联对象数据提供器
 * @author heqichang
 */
public interface ILoadEntityProvider<T, V> {

    /**
     * 自定义关联对象提供方法
     * @param data 数据源
     * @param parameters 外部入参
     * @return 关联数据列表
     */
    List<V> load(List<T> data, ParameterGroup parameters);

    /**
     * 自定义键值，可能原始数据上存在多个 key ，比如 task_ids 字段保存的是 1,2,3 这样的值
     * 如果为 null ，就用注解上的 entityColumn
     * @param data
     * @return
     */
    default List<String> matchKey(T data) {
        return null;
    }

}
