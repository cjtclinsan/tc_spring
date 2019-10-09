package com.tc.spring.framework.beans.config;

import lombok.Data;

/**
 * @author taosh
 * @create 2019-08-06 19:28
 */
@Data
public class TCBeanDefinition {

    private String beanClassName;

    boolean isLazyInit = false;

    private String factoryBeanName;

    private boolean singelton = true;
}
