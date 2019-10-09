package com.tc.spring.framework.aop;

/**
 * @author taosh
 * @create 2019-09-14 11:08
 */
public interface TCAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
