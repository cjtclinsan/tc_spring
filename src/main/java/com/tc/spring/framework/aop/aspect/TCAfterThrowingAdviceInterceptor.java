package com.tc.spring.framework.aop.aspect;

import com.tc.spring.framework.aop.intercept.TCMethodInterceptor;
import com.tc.spring.framework.aop.intercept.TCMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author taosh
 * @create 2019-09-14 15:06
 */
public class TCAfterThrowingAdviceInterceptor extends TCAbstractAspectAdvice implements TCMethodInterceptor {

    private String throwName;

    public TCAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectObject) {
        super(aspectMethod, aspectObject);
    }

    public Object invoke(TCMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }catch (Exception e){
            invokeAdviceMethod(invocation, null, e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwName = throwName;
    }
}
