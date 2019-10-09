package com.tc.orm.test;

/**
 * @author taosh
 * @create 2019-10-08 14:17
 */
public class OrmTest {
    //ORM  对象关系映射  Object Relation Mapping    Hibernate/Spring JDBC/Mybatis/JPA

    //Hibernate  全自动   不需要写sql  效率低
    //Mybatis  半自动  支持简单映射  复杂的需要自己写sql
    //JDBC  手动   自己写sql  规定了一套标准
    //JPA

    //约定优于配置
    //1，先制定顶层接口，参数返回值全部统一
    //List<?> Page<?> select(QueryRule queryRule)
    //Boolean delete(T entity) entity中得ID不能为空，若为空，其他条件不能为空
    //ReturnID insert(T entity)  只要entity不为空
    //Int update(T entity) entity中得ID不能为空，若为空，其他条件不能为空

    //基于jdbc封装了一套
    //基于redis封装了一套
    //基于Mongdb封装了一套
    //基于ElasticSearch封装了一套
    //基于HBase，Hive

    //QueryRule
}
