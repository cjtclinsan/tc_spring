package com.tc.spring.demo.action;

import com.tc.spring.demo.service.IModifyService;
import com.tc.spring.demo.service.IQueryService;
import com.tc.spring.framework.annotation.TCAutowired;
import com.tc.spring.framework.annotation.TCController;
import com.tc.spring.framework.annotation.TCRequestMapping;
import com.tc.spring.framework.annotation.TCRequestParam;
import com.tc.spring.framework.webmvc.servlet.TCModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taosh
 * @create 2019-08-09 16:45
 */
@TCController
@TCRequestMapping("/web")
public class MyAction {
    @TCAutowired
    IQueryService queryService;
    @TCAutowired
    IModifyService modifyService;

    @TCRequestMapping("/query.json")
    public TCModelAndView query(HttpServletRequest request, HttpServletResponse response, @TCRequestParam("name") String name){
        String result = queryService.query(name);
        return out(response, result);
    }

    @TCRequestMapping("/add*.json")
    public TCModelAndView add(HttpServletRequest request, HttpServletResponse response,
                    @TCRequestParam("name") String name, @TCRequestParam("addr") String addr){
        String result = null;
        try {
            result = modifyService.add(name, addr);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("detail", e.getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new TCModelAndView("500", model);
        }
        return out(response, result);
    }

    @TCRequestMapping("/remove.json")
    public TCModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                    @TCRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @TCRequestMapping("/edit.json")
    public TCModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                       @TCRequestParam("id") Integer id, @TCRequestParam("name") String name){
        String result = modifyService.edit(id, name);
        return out(response, result);
    }

    private TCModelAndView out(HttpServletResponse response, String result) {
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
