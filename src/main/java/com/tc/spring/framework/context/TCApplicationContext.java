package com.tc.spring.framework.context;

import com.tc.spring.framework.annotation.TCAutowired;
import com.tc.spring.framework.annotation.TCController;
import com.tc.spring.framework.annotation.TCService;
import com.tc.spring.framework.aop.TCAopProxy;
import com.tc.spring.framework.aop.TCCglibAopProxy;
import com.tc.spring.framework.aop.TCJdkDynamicAopProxy;
import com.tc.spring.framework.aop.config.TCAopConfig;
import com.tc.spring.framework.aop.support.TCAdvisedSupport;
import com.tc.spring.framework.beans.config.TCBeanPostProcessor;
import com.tc.spring.framework.core.TCBeanFactory;
import com.tc.spring.framework.beans.TCBeanWrapper;
import com.tc.spring.framework.beans.config.TCBeanDefinition;
import com.tc.spring.framework.beans.support.TcBeanDefinitionReader;
import com.tc.spring.framework.beans.support.TcDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC、DI
 * @author taosh
 * @create 2019-08-05 21:42
 */
public class TCApplicationContext extends TcDefaultListableBeanFactory implements TCBeanFactory {

    private String[] configLocations;

    private TcBeanDefinitionReader reader;

    /**例的IOC容器*/
    private Map<String, Object> singletonObjectCache = new ConcurrentHashMap<String, Object>();
    /**通用的IOC容器*/
    private Map<String, TCBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, TCBeanWrapper>();

    public TCApplicationContext(String... configLocations){
        this.configLocations = configLocations;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.定位配置文件
        reader = new TcBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<TCBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3.注册、把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类提前初始化
        doAutowired();
    }

    /**处理非延时加载的情况*/
    private void doAutowired() {
        for (Map.Entry<String, TCBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if( !beanDefinitionEntry.getValue().isLazyInit() ){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<TCBeanDefinition> beanDefinitions) throws Exception {
        for (TCBeanDefinition beanDefinition : beanDefinitions) {
            if( super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName()) ){
                throw new Exception("the "+beanDefinition.getFactoryBeanName()+" is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //到这里，容器初始化完毕
    }

    public Object getBean(Class<?> beanClass) throws Exception {
       return getBean(toLowerFirstCase(beanClass.getSimpleName()));
    }

    /**
     * 依赖注入 通过读取beanDefinition中的信息，通过反射机制创建一个实例并返回
     * Spring的做法是，不会把最原始的对象放出去，会用一个beanWrapper来进行一次包装
     * 装饰器模式：保留原来的OOP关系，我们需要进行扩展、增强（为AOP做好基础）
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) throws Exception {

        TCBeanDefinition tcBeanDefinition = this.beanDefinitionMap.get(beanName);

        Object instance = null;
        instance = instantiateBean(beanName, tcBeanDefinition);

        TCBeanPostProcessor postProcessor = new TCBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);
        //1,初始化

        //3，把这个对象封装到beanWrapper中
        //factoryBeanInstanceCache
        TCBeanWrapper beanWrapper = new TCBeanWrapper(instance);

        //4，把beanWrapper存到IOC容器中

        //2，拿到beanWrapper之后，保存到IOC容器中
//        if( this.factoryBeanInstanceCache.containsKey(beanName) ){
//            throw new Exception("The "+beanName+" is exists!");
//        }
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        postProcessor.postProcessAfterInitialization(instance, beanName);

        //3，注入
        populateBean(beanName, new TCBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, TCBeanDefinition tcBeanDefinition, TCBeanWrapper tcBeanWrapper) {
        Object instance = tcBeanWrapper.getWrappedInstance();

        Class<?> clazz = tcBeanWrapper.getWrappedClass();
        //只有加了注解的类，才执行注入
        if( !(clazz.isAnnotationPresent(TCController.class) || clazz.isAnnotationPresent(TCService.class)) ){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if( !field.isAnnotationPresent(TCAutowired.class) ){
                continue;
            }

            TCAutowired autowired = field.getAnnotation(TCAutowired.class);

            String autowiredBeanName = autowired.value().trim();
            if( "".equals(autowiredBeanName) ){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                if( this.factoryBeanInstanceCache.get(autowiredBeanName) == null ){
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, TCBeanDefinition tcBeanDefinition) {
        //1,拿到要实例化的对象的类名
        String className = tcBeanDefinition.getBeanClassName();

        Object instance = null;

        //2，反射实例化，得到一个对象,放到map中
        try {
            if( this.singletonObjectCache.containsKey(className) ){
                instance = this.singletonObjectCache.get(className);
            }else {

                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                TCAdvisedSupport config = instationAopConfig(tcBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //如果符合pointcut规则，就创建代理对象
                if( config.pointCutMatch() ){
                    instance = createProxy(config).getProxy();
                }

                this.singletonObjectCache.put(className, instance);
                this.singletonObjectCache.put(tcBeanDefinition.getFactoryBeanName(), instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    private TCAopProxy createProxy(TCAdvisedSupport config) {
        Class targetClass = config.getTargetClass();

        if( targetClass.getInterfaces().length > 0 ){
            return new TCJdkDynamicAopProxy(config);
        }

        return new TCCglibAopProxy(config);
    }

    private TCAdvisedSupport instationAopConfig(TCBeanDefinition tcBeanDefinition) {
        TCAopConfig config = new TCAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new TCAdvisedSupport(config);
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

    /**大写转小写  Ascii码*/
    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
