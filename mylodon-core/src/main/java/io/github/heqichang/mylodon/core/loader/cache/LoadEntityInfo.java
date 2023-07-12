package io.github.heqichang.mylodon.core.loader.cache;

import io.github.heqichang.mylodon.core.loader.EntityLoader;
import io.github.heqichang.mylodon.core.loader.ILoadEntityProvider;
import io.github.heqichang.mylodon.core.loader.ILoader;

import java.util.List;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes"})
public class LoadEntityInfo<T> extends LoadInfo<T> {


    private boolean oneToMany;

    private Class<? extends T> loadClass;

    private Class<T> entityRawClass;

    private ILoadEntityProvider provider;


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

    @Override
    public ILoader createLoader(List<?> data) {
        return new EntityLoader<>(this, data);
    }
}
