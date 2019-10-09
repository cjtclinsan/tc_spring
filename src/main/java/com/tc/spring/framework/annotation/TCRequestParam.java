package com.tc.spring.framework.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TCRequestParam {
    String value() default "";

    boolean required() default false;
}
