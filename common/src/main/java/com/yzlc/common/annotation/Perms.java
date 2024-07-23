package com.yzlc.common.annotation;


import com.yzlc.common.enums.Logical;

import java.lang.annotation.*;

/**
 * 权限注解，格式：模块:接口:方法
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Perms {
    String[] value();

    Logical logical() default Logical.AND;
}
