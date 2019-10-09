package com.tc.spring.framework.webmvc.servlet;

import lombok.Data;

import java.util.Map;

/**
 * @author taosh
 * @create 2019-08-14 17:46
 */
@Data
public class TCModelAndView {
    private String viewName;

    private Map<String, ?> model;

    public TCModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public TCModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
