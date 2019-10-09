package com.tc.spring.framework.aop.aspect;

import com.tc.spring.framework.aop.intercept.TCMethodInterceptor;
import com.tc.spring.framework.aop.intercept.TCMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author taosh
 * @create 2019-09-14 15:06
 */
public class TCAfterReturningAdviceInterceptor extends TCAbstractAspectAdvice implements TCMethodInterceptor {

    private TCJoinPoint joinPoint;

    public TCAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectObject) {
        super(aspectMethod, aspectObject);
    }

    public Object invoke(TCMethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();

        this.joinPoint = invocation;

        this.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());

        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) {
        try {
            super.invokeAdviceMethod(this.joinPoint, retVal, null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
