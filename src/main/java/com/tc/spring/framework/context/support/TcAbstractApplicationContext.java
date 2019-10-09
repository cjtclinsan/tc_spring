package com.tc.spring.framework.context.support;

/**
 * @author taosh
 * @create 2019-08-05 21:45
 */
public abstract class TcAbstractApplicationContext {

    /**受保护的，只提供给子类去重写*/
    protected void refresh() throws Exception {}
}
