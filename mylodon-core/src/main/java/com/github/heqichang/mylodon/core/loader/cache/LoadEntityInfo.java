package com.github.heqichang.mylodon.core.loader.cache;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.heqichang.mylodon.core.loader.ILoadEntityProvider;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes"})
public class LoadEntityInfo<T> {


    private String loadFieldName;

    private String thisFieldColumnName;

    private String entityFieldColumnName;

    private boolean oneToMany;

    private Class<? extends T> loadClass;

    private Class<T> entityRawClass;

    private ILoadEntityProvider provider;

    private IService<T> service;

    private boolean deepLoad;


    public String getLoadFieldName() {
        return loadFieldName;
    }

    public void setLoadFieldName(String loadFieldName) {
        this.loadFieldName = loadFieldName;
    }

    public String getThisFieldColumnName() {
        return thisFieldColumnName;
    }

    public void setThisFieldColumnName(String thisFieldColumnName) {
        this.thisFieldColumnName = thisFieldColumnName;
    }

    public String getEntityFieldColumnName() {
        return entityFieldColumnName;
    }

    public void setEntityFieldColumnName(String entityFieldColumnName) {
        this.entityFieldColumnName = entityFieldColumnName;
    }

    public boolean isOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(boolean oneToMany) {
        this.oneToMany = oneToMany;
    }

    public Class<? extends T> getLoadClass() {
        return loadClass;
    }

    public void setLoadClass(Class<? extends T> loadClass) {
        this.loadClass = loadClass;
    }

    public Class<T> getEntityRawClass() {
        return entityRawClass;
    }

    public void setEntityRawClass(Class<T> entityRawClass) {
        this.entityRawClass = entityRawClass;
    }

    public ILoadEntityProvider getProvider() {
        return provider;
    }

    public void setProvider(ILoadEntityProvider provider) {
        this.provider = provider;
    }

    public IService<T> getService() {
        return service;
    }

    public void setService(IService<T> service) {
        this.service = service;
    }

    public boolean isDeepLoad() {
        return deepLoad;
    }

    public void setDeepLoad(boolean deepLoad) {
        this.deepLoad = deepLoad;
    }

}
