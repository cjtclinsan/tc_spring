package com.tc.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author taosh
 * @create 2019-09-14 15:42
 */
public abstract class TCAbstractAspectAdvice implements TCAdvice{

    private Method aspectMethod;

    private Object aspectTarget;

    public TCAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(TCJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
        if( null == paramTypes || paramTypes.length == 0 ){
            return this.aspectMethod.invoke(aspectTarget);
        }else {
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++ ) {
                if( paramTypes[i] == TCJoinPoint.class ){
                    args[i] = joinPoint;
                }else if( paramTypes[i] == Throwable.class ){
                    args[i] = tx;
                }else if( paramTypes[i] == Object.class ){
                    args[i] = returnValue;
                }
            }


            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
