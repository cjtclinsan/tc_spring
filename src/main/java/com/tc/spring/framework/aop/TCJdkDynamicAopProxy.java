package com.tc.spring.framework.aop;

import com.tc.spring.framework.aop.intercept.TCMethodInvocation;
import com.tc.spring.framework.aop.support.TCAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author taosh
 * @create 2019-09-14 11:09
 */
public class TCJdkDynamicAopProxy implements TCAopProxy, InvocationHandler {

    private TCAdvisedSupport advised;

    public TCJdkDynamicAopProxy(TCAdvisedSupport config) {
        this.advised = config;
    }

    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {

        Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);

        return null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers =
                this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());

        TCMethodInvocation invocation = new TCMethodInvocation(proxy,null , method, args,
                this.advised.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
