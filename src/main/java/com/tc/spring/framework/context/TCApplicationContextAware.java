package com.tc.spring.framework.context;

/**
 * 通过解耦的方式获得ioc容器的顶层设计，后面将通过一个监听器去扫描所有的类
 * 只要实现了此接口，将自动调用setApplicationContext方法，从而将ioc容器注入到目标类中
 * @author taosh
 * @create 2019-08-06 19:35
 */
public interface TCApplicationContextAware {
    void setApplicationContext(TCApplicationContext applicationContext);
}
