package com.tc.spring.framework.aop.intercept;

/**
 * @author taosh
 * @create 2019-09-14 12:00
 */
public interface TCMethodInterceptor {
    Object invoke(TCMethodInvocation invocation) throws Throwable;
}
