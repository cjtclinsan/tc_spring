package com.tc.spring.demo.aspect;

/**
 * @author taosh
 * @create 2019-08-29 11:11
 */
public class LogAspect {
    public void before(){
        //往对象里记录一下调用的开始时间
    }

    public void after(){
        //往对象里记录一下调用的结束时间-开始时间
    }

    public void afterThrowing(){

    }
}
