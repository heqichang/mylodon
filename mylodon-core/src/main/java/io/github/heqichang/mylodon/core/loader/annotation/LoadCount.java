package io.github.heqichang.mylodon.core.loader.annotation;

import io.github.heqichang.mylodon.core.loader.ILoadCountProvider;

import java.lang.annotation.*;

/**
 * @author heqichang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoadCount {

    /**
     * 需要加载的类
     * @return
     */
    Class<?> entity();

    /**
     * 当前类需要聚合字段的数据库列名
     * @return
     */
    String[] thisColumns() default {};

    /**
     * 加载对象数据需要 groupBy 的数据库字段，如果提供自定义实现，这个字段可以忽略
     * @return
     */
    String[] entityColumns() default {};

    Class<? extends ILoadCountProvider> provider() default ILoadCountProvider.class;
}
