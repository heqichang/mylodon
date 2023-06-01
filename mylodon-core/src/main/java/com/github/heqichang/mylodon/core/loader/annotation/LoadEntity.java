package com.github.heqichang.mylodon.core.loader.annotation;

import com.github.heqichang.mylodon.core.loader.ILoadEntityProvider;
import java.lang.annotation.*;

/**
 * @author heqichang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoadEntity {

    String thisColumn() default "";

    String entityColumn();

    Class<? extends ILoadEntityProvider> provider() default ILoadEntityProvider.class;

    boolean deepLoad() default false;
}
