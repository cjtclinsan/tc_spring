package com.tc.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author taosh
 * @create 2019-09-14 16:58
 */
public interface TCJoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
