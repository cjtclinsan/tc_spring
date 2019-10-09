package com.tc.spring.framework.aop.support;

import com.sun.istack.internal.Nullable;
import com.tc.spring.framework.aop.aspect.TCAfterReturningAdviceInterceptor;
import com.tc.spring.framework.aop.aspect.TCAfterThrowingAdviceInterceptor;
import com.tc.spring.framework.aop.aspect.TCMethodBefreAdviceInterceptor;
import com.tc.spring.framework.aop.config.TCAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author taosh
 * @create 2019-09-14 11:33
 */
public class TCAdvisedSupport {

    private Class<?> targetClass;
    private Object target;
    private TCAopConfig config;
    private Pattern pointCutClassPattern;

    private Map<Method, List<Object>> methodCache;

    public TCAdvisedSupport(TCAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut().replaceAll("\\.", "\\\\.")
                                              .replaceAll("\\\\.\\*", ".*")
                                              .replaceAll("\\(", "\\\\(")
                                              .replaceAll("\\)", "\\\\)");

        String pointCutForClassRegx = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);

        pointCutClassPattern = Pattern.compile("class "+pointCutForClassRegx.substring(pointCutForClassRegx.lastIndexOf(" ") + 1));


        try {

            methodCache = new HashMap<Method, List<Object>>();

            Pattern pattern = Pattern.compile(pointCut);

            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();

            for (Method method : this.targetClass.getMethods()) {
                String methodStr = method.toString();
                if( methodStr.contains("throws") ){
                    methodStr = methodStr.substring(0, methodStr.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodStr);
                if( matcher.matches() ){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成MethodInterceptor

                    //before
                    if( ! (null == config.getAspectBefore() || "".equals(config.getAspectBefore())) ){
                        //创建一个advice对象
                        advices.add(new TCMethodBefreAdviceInterceptor(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    if( ! (null == config.getAspectAfter() || "".equals(config.getAspectAfter())) ){
                        //创建一个advice对象
                        advices.add(new TCAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    if( ! (null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow())) ){
                        //创建一个advice对象
                        TCAfterThrowingAdviceInterceptor throwingAdvice =
                                new TCAfterThrowingAdviceInterceptor(aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }

                    methodCache.put(method, advices);
                }
            }
        } catch ( Exception e ){

        }

    }

    public Object getTarget(){

        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) throws NoSuchMethodException {

        List<Object> cached = this.methodCache.get(method);

        if( cached== null ){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached.add(m);
            this.methodCache.put(method, cached);
        }

        return cached;
    }

    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
