package com.tc.spring.demo.service.impl;

import com.tc.spring.demo.service.IQueryService;
import com.tc.spring.framework.annotation.TCService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author taosh
 * @create 2019-08-09 16:52
 */
@TCService
public class QueryService implements IQueryService {
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\""+name+"\",time:\""+time+"\"}";
        return json;
    }
}
