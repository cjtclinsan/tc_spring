package com.tc.spring.framework.aop.aspect;

import com.tc.spring.framework.aop.intercept.TCMethodInterceptor;
import com.tc.spring.framework.aop.intercept.TCMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author taosh
 * @create 2019-09-14 15:04
 */
public class TCMethodBefreAdviceInterceptor extends TCAbstractAspectAdvice implements TCMethodInterceptor {

    private TCJoinPoint joinPoint;

    public TCMethodBefreAdviceInterceptor(Method aspectMethod, Object aspectObject) {
        super(aspectMethod, aspectObject);
    }

    private void befor(Method method, Object[] args, Object target) throws Throwable {
        //传送给织入的参数
//        method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    public Object invoke(TCMethodInvocation invocation) throws Throwable {
        //从被织入的代码中拿到，JoinPoint
        this.joinPoint = invocation;
        befor(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }
}
