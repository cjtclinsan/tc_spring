package com.tc;

import com.tc.spring.demo.action.MyAction;
import com.tc.spring.framework.context.TCApplicationContext;

/**
 * @author taosh
 * @create 2019-08-09 16:05
 */
public class Test {
    public static void main(String[] args) throws Exception {

        TCApplicationContext applicationContext = new TCApplicationContext("classpath:application.properties");
        Object object = applicationContext.getBean(MyAction.class);
    }
}
