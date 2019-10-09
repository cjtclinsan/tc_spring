package com.tc.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author taosh
 * @create 2019-08-14 21:07
 */
public class TCView {

    public final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public TCView(File viewFile) {
        this.viewFile = viewFile;
    }

    public void render(Map<String, ?> model,
                       HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        String line = null;

        while ( null != ra.readLine() ){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while ( matcher.find() ){
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}", "");
                Object paramValue = model.get(paramName);
                if( null == paramValue ){
                    continue;
                }
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType(DEFAULT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }

    //特殊字符处理
    public static String makeStringForRegExp(String str){
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
