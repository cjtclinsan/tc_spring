package com.tc.spring.framework.beans.config;

/**
 * @author taosh
 * @create 2019-08-13 20:00
 */
public class TCBeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }
}
