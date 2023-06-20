package io.github.heqichang.mylodon.core.loader.cache;

import io.github.heqichang.mylodon.core.loader.CountLoader;
import io.github.heqichang.mylodon.core.loader.ILoadCountProvider;
import io.github.heqichang.mylodon.core.loader.ILoader;

import java.util.List;

/**
 * @author heqichang
 */
@SuppressWarnings({"rawtypes"})
public class LoadCountInfo<T> extends LoadInfo<T> {


    private ILoadCountProvider provider;

    @Override
    public ILoader createLoader(List<?> data) {
        return new CountLoader<>(this, data);
    }

    public ILoadCountProvider getProvider() {
        return provider;
    }

    public void setProvider(ILoadCountProvider provider) {
        this.provider = provider;
    }
}
