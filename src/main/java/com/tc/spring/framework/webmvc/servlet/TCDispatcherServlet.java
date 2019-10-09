package com.tc.spring.framework.webmvc.servlet;

import com.tc.spring.framework.annotation.TCController;
import com.tc.spring.framework.annotation.TCRequestMapping;
import com.tc.spring.framework.context.TCApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author taosh
 * @create 2019-08-13 20:17
 */

public class TCDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private TCApplicationContext context;

    private List<TCHandlerMapping> handleMappings = new ArrayList<TCHandlerMapping>();

    private Map<TCHandlerMapping, TCHandlerAdapter> handlerAdapters = new HashMap<TCHandlerMapping, TCHandlerAdapter>();

    private List<TCViewResolver>  viewResolvers = new ArrayList<TCViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n"+ Arrays.toString(e.getStackTrace())
                    .replaceAll("\\[|\\]", "").replaceAll(",\\s","\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //通过从request中拿到URL，去匹配一个handlerMapping
        TCHandlerMapping handler = getHandler(req);

        System.out.println(handler);
        if ( handler == null ){
            processDispatchResult(req, resp, new TCModelAndView("404"));
            return;
        }

        //准备调用前的参数
        TCHandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        //真正的调用方法,返回ModelAndView存储了页面的值和模板名称
        TCModelAndView mv = handlerAdapter.handler(req, resp, handler);

        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, TCModelAndView mv) throws Exception {
        //把modelandview变成一个html/outputStream/json/freemark等
        if( null == mv ){
            return;
        }

        //如果modelandview不为null
        if( this.viewResolvers.isEmpty() ){
            return;
        }

        for (TCViewResolver viewResolver : this.viewResolvers) {
            TCView view = viewResolver.resolveViewName(mv.getViewName(),null);

            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private TCHandlerAdapter getHandlerAdapter(TCHandlerMapping handler) {
        if( this.handlerAdapters.isEmpty() ){
            return null;
        }

        TCHandlerAdapter ha = this.handlerAdapters.get(handler);
        if (ha.support(handler)){
            return ha;
        }

        return null;
    }

    private TCHandlerMapping getHandler(HttpServletRequest req) {
        if( handleMappings.isEmpty() ){
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (TCHandlerMapping handler : handleMappings) {

            Matcher matcher = handler.getPattern().matcher(url);

            //如果没有匹配，继续下一个匹配
            if( !matcher.matches() ){
                continue;
            }

            return handler;
        }

        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化Application
        context = new TCApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //初始化spring mvc的九大组件
        initStrategies(context);
    }

    protected void initStrategies(TCApplicationContext context) {
        //多文件上传组建
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理
        initThemeResolver(context);
        //
        initHandlerMappings(context);
        //初始化参数适配器
        initHandlerAdapters(context);
        //初始化异常拦截
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    private void initFlashMapManager(TCApplicationContext context) {
    }

    private void initViewResolvers(TCApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new TCViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(TCApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(TCApplicationContext context) {
    }

    private void initHandlerAdapters(TCApplicationContext context) {
        //把一个request请求变成一个handler，参数都是字符串的，自动匹配到handler中的形参
        //拿到handlerMapper才能干活
        //意味着有几个handlerMapping就有几个handlerAdapter
        for (TCHandlerMapping handleMapping : this.handleMappings) {
            this.handlerAdapters.put(handleMapping, new TCHandlerAdapter());
        }
    }

    private void initHandlerMappings(TCApplicationContext context) {
        String [] beanNames = context.getBeanDefinitionNames();

        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();
                if( !clazz.isAnnotationPresent(TCController.class) ){
                    continue;
                }

                String baseUrl = "";

                //获取Controller的url配置
                if( clazz.isAnnotationPresent(TCRequestMapping.class) ){
                    TCRequestMapping requestMapping = clazz.getAnnotation(TCRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加TCRequestMapping注解的忽略
                    if( !method.isAnnotationPresent(TCRequestMapping.class) ){
                        continue;
                    }

                    //映射url
                    TCRequestMapping requestMapping = method.getAnnotation(TCRequestMapping.class);

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*"))
                                                                                .replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handleMappings.add(new TCHandlerMapping(controller, method, pattern));

                }
            }
        }catch (Exception e){

        }


    }

    private void initThemeResolver(TCApplicationContext context) {
    }

    private void initLocaleResolver(TCApplicationContext context) {
    }

    private void initMultipartResolver(TCApplicationContext context) {
    }
}
