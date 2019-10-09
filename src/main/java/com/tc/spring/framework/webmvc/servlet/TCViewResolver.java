package com.tc.spring.framework.webmvc.servlet;

import lombok.Data;

import java.io.File;
import java.util.Locale;

/**
 * @author taosh
 * @create 2019-08-14 21:03
 */
@Data
public class TCViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";
    private File templateRootDir;
//    private String viewName;

    public TCViewResolver(String template) {
        String templateRootPath = this.getClass().getClassLoader().getResource(template).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public TCView resolveViewName(String viewName, Locale locale) throws Exception{
        if( null == viewName || "".equals(viewName.trim())){
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);

        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));

        return new TCView(templateFile);
    }
}
