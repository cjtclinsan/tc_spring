package com.tc.spring.framework.core;

/**
 * 单利工厂的顶层设计
 * @author taosh
 * @create 2019-08-05 21:37
 */
public interface TCBeanFactory {
    /**
     * 根据beanName从ioc容器中获取一个实例bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
