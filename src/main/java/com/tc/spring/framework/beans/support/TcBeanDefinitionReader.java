package com.tc.spring.framework.beans.support;

import com.tc.spring.framework.beans.config.TCBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author taosh
 * @create 2019-08-06 21:28
 */
public class TcBeanDefinitionReader {

    private List<String> registerBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    //固定配置文件中的key，相当于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public TcBeanDefinitionReader(String... locations) {
        //通过url定位找到其对应的文件，转换为文件流，读取
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if( null != is ){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/
        URL url = this.getClass().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classpath = new File(url.getFile());

        for (File file : classpath.listFiles()) {
            if( file.isDirectory() ){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if( !file.getName().endsWith(".class") ){
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registerBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig(){
        return this.config;
    }

    /**把每一个配置信息解析成beanDefinition*/
    public List<TCBeanDefinition> loadBeanDefinitions(){
        List<TCBeanDefinition> result = new ArrayList<TCBeanDefinition>();

        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                //有可能是一个接口，用他的实现类作为beanClassName
                if( beanClass.isInterface() ){
                    continue;
                }

                //beanName三种情况：
                //1,默认是类名首字母小写
                //2,自定义名字
                //3,接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果有多个实现类，只能覆盖
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**把每一个配置信息解析成一个BeanDefinition*/
    private TCBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName){
        TCBeanDefinition beanDefinition = new TCBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**大写转小写  Ascii码*/
    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
