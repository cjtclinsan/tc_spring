package com.tc.orm.test;

import com.tc.orm.demo.entity.AppVersion;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * @author taosh
 * @create 2019-10-07 14:52
 */
public class JdbcTest {
    public static void main(String[] args) {
        AppVersion version = new AppVersion();
        version.setAppVersionId(1);
        version.setVersionCode("1");
        List<?> result =  select(version);

        System.out.println(Arrays.toString(result.toArray()));
    }

    private static List<?> select(Object condition) {

        Class<?> entityClass = condition.getClass();

        List<Object> appList = new ArrayList<Object>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            //1，加载驱动类
            Class.forName("com.mysql.jdbc.Driver");

            //2，建立连接
            con = DriverManager.getConnection("jdbc:mysql://118.31.42.188:13307/ruida_dev_v2?useUnicode=true&serverTimezone=GMT%2B8&characterEncoding=UTF-8&useSSL=false",
                    "ruida", "Ruida@2019");

            Table table = entityClass.getAnnotation(Table.class);
            //3，创建语句集
            StringBuffer sql = new StringBuffer("select * from "+ table.name());

            Field[] fields = entityClass.getDeclaredFields();
            Map<String,String> mapper = new HashMap<String, String>(fields.length);
            Map<String, String> columnByFieldMapper = new HashMap<String, String>();

            for (Field field : fields) {
                String fieldName = field.getName();
                if( field.isAnnotationPresent(Column.class) ){
                    Column column = field.getAnnotation(Column.class);
                    mapper.put( column.name(), fieldName);
                    columnByFieldMapper.put( fieldName, column.name() );

                }else {
                    mapper.put(fieldName, fieldName);
                    columnByFieldMapper.put(fieldName, fieldName);
                }
            }

            StringBuffer where = new StringBuffer(" where 1 = 1");
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(condition);
                if( null != value ){
                    if( String.class == field.getType() ){
                        where.append(" and " + columnByFieldMapper.get(field.getName()) + " = '" + value + "'");
                    }else {
                        where.append(" and " + columnByFieldMapper.get(field.getName()) + " = " + value);
                    }
                }
            }

            sql.append(where);

            System.out.println(sql.toString());

            pstm = con.prepareStatement(sql.toString());
            //4，执行语句集
            rs = pstm.executeQuery();

            //元数据：保存了除了真正数值以外得所有附加信息
            int columnCounts = rs.getMetaData().getColumnCount();

            //5，获取结果集
            while ( rs.next() ){

                Object instance = entityClass.newInstance();
                for (int i = 1 ; i <= columnCounts; i++){
                    //从rs中取得当前游标下得类名
                    String columnName = rs.getMetaData().getColumnName(i);

                    //有可能是私有的
                    Field field = entityClass.getDeclaredField(mapper.get(columnName));

                    field.setAccessible(true);

                    field.set(instance, rs.getObject(columnName));
                }

                //通过反射机制拿到实体类得所有字段
                appList.add(instance);
            }

            //6，关闭结果集、语句集、连接
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstm.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return appList;
    }
}
