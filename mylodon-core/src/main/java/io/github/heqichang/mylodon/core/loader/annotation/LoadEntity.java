package io.github.heqichang.mylodon.core.loader.annotation;

import io.github.heqichang.mylodon.core.loader.ILoadEntityProvider;

import java.lang.annotation.*;

/**
 * 绑定关联对象
 * @author heqichang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoadEntity {

    /**
     * 当前提取对象的数据列名（如果提供自定义实现且实现了 matchKey，这个字段可以忽略）
     * @return
     */
    String[] thisColumns() default {};

    String[] entityColumns() default {};

    /**
     * 自定义关联模型数据提供器
     * @return 自定义类类型
     */
    Class<? extends ILoadEntityProvider> provider() default ILoadEntityProvider.class;

    /**
     * 是否需要嵌套调用
     * @return 是否需要
     */
    boolean deepLoad() default false;
}
