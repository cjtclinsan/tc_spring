package com.tc.spring.demo.service.impl;

import com.tc.spring.demo.service.IModifyService;
import com.tc.spring.framework.annotation.TCService;

/**
 * @author taosh
 * @create 2019-08-09 16:50
 */
@TCService
public class ModifyService implements IModifyService {
    public String add(String name, String addr) throws Exception{
        throw new Exception("异常测试");
    }

    public String edit(Integer id, String name) {
        return "modifyService edit:id=" + id + ",name=" + name;
    }

    public String remove(Integer id) {
        return "modifyService remove:id=" + id;
    }
}
