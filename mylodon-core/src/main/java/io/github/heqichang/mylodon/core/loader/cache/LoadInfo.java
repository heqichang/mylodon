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


    public List<String> getThisFieldColumnNames() {
        return thisFieldColumnNames;
    }

    public void setThisFieldColumnNames(List<String> thisFieldColumnNames) {
        this.thisFieldColumnNames = thisFieldColumnNames;
    }

    public List<String> getEntityFieldColumnNames() {
        return entityFieldColumnNames;
    }

    public void setEntityFieldColumnNames(List<String> entityFieldColumnNames) {
        this.entityFieldColumnNames = entityFieldColumnNames;
    }

    /**
     * 当前主模型提取的数据库字段
     */
    private List<String> thisFieldColumnNames;


    /**
     * 对应需要加载关联模型的关联数据库字段
     */
    private List<String> entityFieldColumnNames;



    /**
     * 是否需要加载下一层
     */
    private boolean deepLoad;

    /**
     * 持有的 plus 的 service
     */
    private IService<T> service;

    public abstract ILoader createLoader(List<?> data);

    public String getLoadFieldName() {
        return loadFieldName;
    }



    public boolean isDeepLoad() {
        return deepLoad;
    }

    public IService<T> getService() {
        return service;
    }

    public void setLoadFieldName(String loadFieldName) {
        this.loadFieldName = loadFieldName;
    }



    public void setDeepLoad(boolean deepLoad) {
        this.deepLoad = deepLoad;
    }

    public void setService(IService<T> service) {
        this.service = service;
    }

}
