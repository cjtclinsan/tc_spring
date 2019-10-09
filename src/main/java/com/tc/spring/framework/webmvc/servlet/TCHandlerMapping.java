package com.tc.spring.framework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author taosh
 * @create 2019-08-14 17:10
 */
@Data
public class TCHandlerMapping {

    private Object controller;      //保存方法对应的实例

    private Method method;          //保存映射的方法

    protected Pattern pattern;      //URL的正则匹配

    public TCHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
