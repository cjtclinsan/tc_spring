package com.tc.spring.framework.aop.intercept;

import com.sun.istack.internal.Nullable;
import com.tc.spring.framework.aop.aspect.TCJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taosh
 * @create 2019-09-14 11:44
 */
public class TCMethodInvocation implements TCJoinPoint {

    private Object proxy;
    private Method method;
    private Object target;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;
    private Class<?> targetClass;

    private Map<String, Object> userAttributes;

    //定义一个索引，从-1开始记录当前拦截器执行的位置
    private int currentInterceptorIndex = -1;

    public TCMethodInvocation(Object proxy, Object target, Method method, @Nullable Object[] arguments,
                                 @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers ) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;


    }

    public Object proceed() throws Throwable {

        //如果Interceptor执行完了，则执行joinpoint
        if( this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() -1 ){
            return this.method.invoke(this.target, this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        //如果要动态匹配joinPoint
        if( interceptorOrInterceptionAdvice instanceof TCMethodInterceptor ){
            TCMethodInterceptor tmi = (TCMethodInterceptor) interceptorOrInterceptionAdvice;

            return tmi.invoke(this);
        }else {
            return proceed();
        }
    }

    private Object invokeJoinpoint() {
        return null;
    }

    public Object getThis() {
        return this.target;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setUserAttribute(String key, Object value) {
        if( value != null ){
            if( this.userAttributes == null ){
                this.userAttributes = new HashMap<String, Object>();
            }
            this.userAttributes.put(key, value);
        }else {
            if( this.userAttributes != null ){
                this.userAttributes.remove(key);
            }
        }
    }

    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }

}
