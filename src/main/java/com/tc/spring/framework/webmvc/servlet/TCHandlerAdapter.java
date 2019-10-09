package com.tc.spring.framework.webmvc.servlet;

import com.tc.spring.framework.annotation.TCRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taosh
 * @create 2019-08-14 17:39
 */
public class TCHandlerAdapter {

    public boolean support(Object handler){
        return (handler instanceof TCHandlerMapping);
    }

    public TCModelAndView handler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        TCHandlerMapping handlerMapping = (TCHandlerMapping) handler;

        //把方法的形参列表和request的参数列表所在的顺序一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();

        //提取方法中的注解参数，得到一个二维数组，因为一个参数有多个注解，一个方法又有多个参数
        Annotation[] [] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            Annotation[] annotations = pa[i];
            for (Annotation annotation : annotations) {
                if( annotation instanceof TCRequestParam){
                    String paramName = ((TCRequestParam) annotation).value();
                    if( !"".equals(paramName.trim()) ){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //提取方法中的request和response参数
        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> type = paramsTypes[i];
            if( type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(), i);
            }
        }

        //获得方法的形参列表
        Map<String, String[]> params = request.getParameterMap();

        //实参列表
        Object [] paramValue = new Object[paramsTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {

            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if( !paramIndexMapping.containsKey(param.getKey())){
                continue;
            }

            int index = paramIndexMapping.get(param.getKey());
            paramValue[index] = caseStringValue(value, paramsTypes[index]);
        }


        if( paramIndexMapping.containsKey(HttpServletRequest.class.getName()) ){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValue[reqIndex] = request;
        }

        if( paramIndexMapping.containsKey(HttpServletResponse.class.getName()) ){
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValue[respIndex] = response;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValue);
        if( result == null || result instanceof Void ){
            return null;
        }

        boolean isModelAndView = (handlerMapping.getMethod().getReturnType() == TCModelAndView.class);
        if( isModelAndView ){
            return (TCModelAndView) result;
        }



        return null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if( String.class == paramsType ){
            return value;
        }else if( Integer.class == paramsType ){
            return Integer.valueOf(value);
        }else if( Double.class == paramsType ){
            return Double.valueOf(value);
        }else {
            if( value != null ){
                return value;
            }
            return null;
        }
    }


}
