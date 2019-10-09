package com.tc.spring.framework.beans;

/**
 * @author taosh
 * @create 2019-08-08 17:41
 */
public class TCBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public TCBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
