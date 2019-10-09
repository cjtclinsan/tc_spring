package com.tc.spring.framework.beans.support;

import com.tc.spring.framework.beans.config.TCBeanDefinition;
import com.tc.spring.framework.context.support.TcAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author taosh
 * @create 2019-08-06 19:22
 */
public class TcDefaultListableBeanFactory extends TcAbstractApplicationContext {

    /**存储注册信息的BeanDefinition*/
    protected final Map<String, TCBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, TCBeanDefinition>();
}
