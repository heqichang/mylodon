package io.github.heqichang.mylodon.core.loader.cache;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.heqichang.mylodon.core.loader.ILoader;

import java.util.List;

/**
 * @author heqichang
 */
public abstract class LoadInfo<T> {



    /**
     * 需要进行加载的模型字段名
     */
    private String loadFieldName;


    /**
     * 当前主模型提取的数据库字段
     */
    private String thisFieldColumnName;

    /**
     * 对应需要加载关联模型的关联数据库字段
     */
    private String entityFieldColumnName;

    /**
     * 持有的 plus 的 service
     */
    private IService<T> service;

    public abstract ILoader createLoader(List<?> data);

    public String getLoadFieldName() {
        return loadFieldName;
    }

    public String getThisFieldColumnName() {
        return thisFieldColumnName;
    }

    public String getEntityFieldColumnName() {
        return entityFieldColumnName;
    }

    public IService<T> getService() {
        return service;
    }

    public void setLoadFieldName(String loadFieldName) {
        this.loadFieldName = loadFieldName;
    }

    public void setThisFieldColumnName(String thisFieldColumnName) {
        this.thisFieldColumnName = thisFieldColumnName;
    }

    public void setEntityFieldColumnName(String entityFieldColumnName) {
        this.entityFieldColumnName = entityFieldColumnName;
    }

    public void setService(IService<T> service) {
        this.service = service;
    }

}
