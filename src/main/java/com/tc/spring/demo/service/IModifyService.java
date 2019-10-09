package com.tc.spring.demo.service;

/**
 * @author taosh
 * @create 2019-08-09 16:47
 */
public interface IModifyService {
    /**
     * 增加
     */
    public String add(String name, String addr) throws Exception;

    /**
     * 修改
     */
    public String edit(Integer id, String name);

    /**
     * 删除
     */
    public String remove(Integer id);
}
